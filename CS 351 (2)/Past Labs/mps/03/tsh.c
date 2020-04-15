/* 
 * tsh - A tiny shell program with job control
 */
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <errno.h>

/* Misc manifest constants */
#define MAXLINE    1024   /* max line size */
#define MAXARGS     128   /* max args on a command line */
#define MAXJOBS      16   /* max jobs at any point in time */
#define MAXJID    1<<16   /* max job ID */

#define FORK_CHILD    0   /* executing in child process from fork() */

/* Built-in commands */
#define CMD_QUIT "quit"
#define CMD_FG "fg"
#define CMD_BG "bg"
#define CMD_JOBS "jobs"

/*
 * Jobs states: FG (foreground), BG (background), ST (stopped)
 * Job state transitions and enabling actions:
 *     FG -> ST  : ctrl-z
 *     ST -> FG  : fg command
 *     ST -> BG  : bg command
 *     BG -> FG  : fg command
 * At most 1 job can be in the FG state.
 */

enum exec_mode_t {
  EXEC_MODE_SKIP, /* nothing to execute (blank line) */
  EXEC_MODE_FG,   /* execute in foreground */
  EXEC_MODE_BG    /* execute in background */
};

enum job_state_t {
  JOB_STATE_UNDEF, /* undefined */
  JOB_STATE_FG,    /* running in foreground */
  JOB_STATE_BG,    /* running in background */
  JOB_STATE_ST     /* stopped */
};

/* parseline() return values */
enum parseresult_t {
  PARSELINE_BLANK,
  PARSELINE_FG,
  PARSELINE_BG,
};

/* Global variables */
extern char **environ;      /* defined in libc */
char prompt[] = "tsh> ";    /* command line prompt (DO NOT CHANGE) */
int verbose = 0;            /* if true, print additional output */
int nextjid = 1;            /* next job ID to allocate */
char sbuf[MAXLINE];         /* for composing sprintf messages */

struct job_t {            /* The job struct */
  pid_t pid;              /* job PID */
  int jid;                /* job ID [1, 2, ...] */
  enum job_state_t state; /* job state */
  char cmdline[MAXLINE];  /* command line */
} job_t;

struct job_t jobs[MAXJOBS]; /* The job list */
/* End global variables */


/* Function prototypes */

/* Here are the functions that you will implement */
void eval(char *cmdline);
bool try_eval_internal(char **argv, int argc);

pid_t do_exec(char *path, char **argv, char *cmdline);
void job_bg(struct job_t *job);
void job_fg(struct job_t *job);
void waitfg(pid_t pid);
void send_fg_sig(int sig);

void sigchld_handler(int sig);
void sigtstp_handler(int sig);
void sigint_handler(int sig);

/* Here are helper routines that we've provided for you */
enum exec_mode_t parseline(const char *cmdline, char **argv, int *argc_out);
void sigquit_handler(int sig);

void job_clear(struct job_t *job);
void jobs_init(struct job_t *jobs);
int jobs_highest_jid(struct job_t *jobs);
struct job_t * jobs_add(struct job_t *jobs, pid_t pid, enum job_state_t state,
                        char *cmdline);
int jobs_remove(struct job_t *jobs, pid_t pid);
pid_t jobs_fgpid(struct job_t *jobs);
bool job_exists_by_pid(struct job_t *jobs, pid_t pid);
bool job_exists_by_jid(struct job_t *jobs, int jid);
struct job_t *jobs_get_by_pid(struct job_t *jobs, pid_t pid);
struct job_t *jobs_get_by_jid(struct job_t *jobs, int jid);
int pid_to_jid(pid_t pid);
int job_print(struct job_t *job, bool show_state);
void jobs_list(struct job_t *jobs);

void usage(void);
void exec_error(char *msg);
void unix_error(char *msg);
void app_error(char *msg);
typedef void handler_t(int);
handler_t *register_signal(int signum, handler_t *handler);

/*
 * main - The shell's main routine 
 */
int main(int argc, char **argv)
{
    char c;
    char cmdline[MAXLINE];
    int emit_prompt = 1; /* emit prompt (default) */

    /* Redirect stderr to stdout (so that driver will get all output
     * on the pipe connected to stdout) */
    dup2(1, 2);

    /* Parse the command line */
    while ((c = (char) getopt(argc, argv, "hvp")) != EOF) {
        switch (c) {
        case 'h':             /* print help message */
            usage();
            break;
        case 'v':             /* emit additional diagnostic info */
            verbose = 1;
            break;
        case 'p':             /* don't print a prompt */
            emit_prompt = 0;  /* handy for automatic testing */
            break;
        default:
            usage();
        }
    }

    /* Install the signal handlers */

    /* These are the ones you will need to implement */
    register_signal(SIGINT, sigint_handler);   /* ctrl-c */
    register_signal(SIGTSTP, sigtstp_handler);  /* ctrl-z */
    register_signal(SIGCHLD,
                    sigchld_handler);  /* Terminated or stopped child */

    /* This one provides a clean way to kill the shell */
    register_signal(SIGQUIT, sigquit_handler);

    /* Initialize the job list */
    jobs_init(jobs);

    if (verbose) {
        printf("debug: tsh (%d:%d)\n", getpid(), getpgrp());
    }

    /* Execute the shell's read/eval loop */
    while (1) {
        /* Read command line */
        if (emit_prompt) {
            printf("%s", prompt);
            fflush(stdout);
        }
        if ((fgets(cmdline, MAXLINE, stdin) == NULL) && ferror(stdin)) {
            app_error("fgets error");
        }
        if (feof(stdin)) { /* End of file (ctrl-d) */
            fflush(stdout);
            exit(0);
        }

        /* Evaluate the command line */
        eval(cmdline);
        fflush(stdout);
        fflush(stdout);
    }

    exit(0); /* control never reaches here */
}

/* 
 * eval - Evaluate the command line that the user has just typed in
 * 
 * If the user has requested a built-in command (quit, jobs, bg or fg)
 * then execute it immediately. Otherwise, fork a child process and
 * run the job in the context of the child. If the job is running in
 * the foreground, wait for it to terminate and then return.  Note:
 * each child process must have a unique process group ID so that our
 * background children don't receive SIGINT (SIGTSTP) from the kernel
 * when we type ctrl-c (ctrl-z) at the keyboard.  
 */
void eval(char *cmdline)
{
    int i;
    enum exec_mode_t result;
    char *argv[MAXARGS];
    int argc;
    enum job_state_t job_state;
    pid_t pid;
    struct job_t *job;

    result = parseline(cmdline, argv, &argc);
    if (result == EXEC_MODE_SKIP) {
        return;
    }

    job_state =
        (result == EXEC_MODE_FG)
        ? JOB_STATE_FG
        : JOB_STATE_BG;

    if (verbose) {
        for (i = 0; argv[i] != NULL; i++) {
            printf("argv[%d]=%s%s", i, argv[i],
                   (argv[i + 1] == NULL) ? "\n" : ", ");
        }
    }

    if (try_eval_internal(argv, argc)) {
        return;
    }

    char *path = argv[0];
    pid = do_exec(path, argv, cmdline);
    job = jobs_add(jobs, pid, job_state, cmdline);

    if (job_state == JOB_STATE_FG) {
        waitfg(pid);
    } else {
        job_print(job, false);
    }

    return;
}

/*
 * do_exec - Executes the `path` executable with `argv` in a child process.
 */
pid_t do_exec(char *path, char **argv, char *cmdline)
{
    pid_t pid;
    if ((pid = fork()) == FORK_CHILD) {
        setpgrp(); /* set new process group for child */

        if (verbose) {
            printf("debug: starting (%d:%d) '%s'\n",
                   getpid(), getpgrp(), cmdline);
        }

        /* execs by searching for `path` in `environ` */
        if (execvp(path, argv) == -1) {
            exec_error(path);
        }
    }
    return pid;
}

/* 
 * parseline - Parse the command line and build the argv array.
 * 
 * Characters enclosed in single quotes are treated as a single
 * argument.  Return true if the user has requested a BG job, false if
 * the user has requested a FG job.  
 */
enum exec_mode_t parseline(const char *cmdline, char **argv, int *argc_out)
{
    static char array[MAXLINE]; /* holds local copy of command line */
    char *buf = array;          /* ptr that traverses command line */
    char *delim;                /* points to first space delimiter */
    int argc;                   /* number of args */
    int bg;                     /* background job? */

    strcpy(buf, cmdline);
    buf[strlen(buf) - 1] = ' ';  /* replace trailing '\n' with space */
    while (*buf && (*buf == ' ')) { /* ignore leading spaces */
        buf++;
    }

    /* Build the argv list */
    argc = 0;
    if (*buf == '\'') {
        buf++;
        delim = strchr(buf, '\'');
    }
    else {
        delim = strchr(buf, ' ');
    }

    while (delim) {
        argv[argc++] = buf;
        *delim = '\0';
        buf = delim + 1;
        while (*buf && (*buf == ' ')) { /* ignore spaces */
            buf++;
        }

        if (*buf == '\'') {
            buf++;
            delim = strchr(buf, '\'');
        }
        else {
            delim = strchr(buf, ' ');
        }
    }
    argv[argc] = NULL;

    if (argc == 0) {  /* ignore blank line */
        *argc_out = argc;
        return EXEC_MODE_SKIP;
    }

    /* should the job run in the background? */
    if ((bg = (*argv[argc - 1] == '&')) != 0) {
        argv[--argc] = NULL;
    }

    *argc_out = argc;
    return (bg) ? EXEC_MODE_BG : EXEC_MODE_FG;
}

/* 
 * try_eval_internal - If the user has typed a built-in command then execute
 *    it immediately.  
 */
bool try_eval_internal(char *argv[], int argc)
{
    int fg, bg = -1;

    if (argc <= 0) {
        return false;
    }

    char *cmd = argv[0];
    if (strcmp(cmd, CMD_QUIT) == 0) {
        exit(0);
        return true; /* never reached */
    }
    else if ((fg = strcmp(cmd, CMD_FG)) == 0 || (bg = strcmp(cmd, CMD_BG)) == 0) {
        struct job_t *job = NULL;
        if (argc == 2) {
            char *arg = argv[1];
            char *id_str = arg;
            bool is_jid = false;
            int id;

            if (arg[0] == '%') {
                id_str = arg + 1;
                is_jid = true;
            }

            id = atoi(id_str);
            if (id == 0) {
                printf("%s: argument must be a PID or %%jobid\n", cmd);
                return true;
            }

            if (is_jid) {
                job = jobs_get_by_jid(jobs, id);
            } else {
                job = jobs_get_by_pid(jobs, id);
            }

            if (job == NULL) {
                if (is_jid) {
                    printf("%s: No such job\n", arg);
                } else {
                    printf("(%s): No such process\n", arg);
                }
                return true;
            }
        }
        else {
            printf("%s command requires PID or %%jobid argument\n", cmd);
            return true;
        }

        if (fg == 0) {
            job_fg(job);
        }
        else if (bg == 0) {
            job_bg(job);
        }
        else {
            app_error("try_eval_internal(): invalid state");
        }

        return true;
    }
    else if (strcmp(cmd, CMD_JOBS) == 0) {
        jobs_list(jobs);
        return true;
    }

    return false; /* not a builtin command */
}

/*
 * job_fg - Transition job into foreground and then block.
 */
void job_fg(struct job_t *job)
{
    if (job->state == JOB_STATE_FG) {
        return;
    }

    job->state = JOB_STATE_FG;
    kill(-(job->pid), SIGCONT);
    waitfg(job->pid);
}

/*
 * job_bg - Transition job into background and continue execution.
 */
void job_bg(struct job_t *job)
{
    if (job->state == JOB_STATE_BG) {
        return;
    }

    kill(-(job->pid), SIGCONT);
    job->state = JOB_STATE_BG;
    job_print(job, false);
}

/* 
 * waitfg - Block until process pid is no longer the foreground process
 */
void waitfg(pid_t pid)
{
    struct job_t *job;
    while ((job = jobs_get_by_pid(jobs, pid)) != NULL) {
        if (job->state != JOB_STATE_FG) { return; }
        pause();
    }
}

/*****************
 * Signal handlers
 *****************/

/* 
 * sigchld_handler - The kernel sends a SIGCHLD to the shell whenever
 *     a child job terminates (becomes a zombie), or stops because it
 *     received a SIGSTOP or SIGTSTP signal. The handler reaps all
 *     available zombie children, but doesn't wait for any other
 *     currently running children to terminate.  
 */
void sigchld_handler(int sig)
{
    pid_t pid;
    int wstatus;
    int exit_status;
    int wsignal;
    struct job_t *job;

    if (verbose) {
        printf("debug: handle SIGCHLD\n");
    }

    pid = waitpid(-1, &wstatus, WNOHANG | WUNTRACED);
    if (pid == 0) {
        return; /* no children to reap */
    }
    if (pid == -1) {
        unix_error("waitpid()");
        return;
    }

    job = jobs_get_by_pid(jobs, pid);

    // TODO: Replace with process name
    if (WIFEXITED(wstatus)) {
        if (verbose && (exit_status = WEXITSTATUS(wstatus)) != 0) {
            printf("Exit status: %d\n", exit_status);
        }
        jobs_remove(jobs, pid);
    }
    else if (WIFSIGNALED(wstatus)) {
        wsignal = WTERMSIG(wstatus);
        printf("Job [%d] (%d) terminated by signal %d\n",
            job->jid, job->pid, wsignal);
        jobs_remove(jobs, pid);
    }
    else if (WIFSTOPPED(wstatus)) {
        if (job == NULL) {
            app_error("sigchld_handler(): failed to get job by pid");
            return;
        }
        wsignal = WSTOPSIG(wstatus);
        job->state = JOB_STATE_ST;
        printf("Job [%d] (%d) stopped by signal %d\n",
               job->jid, job->pid, wsignal);
    }
}

/*
 * send_fg_sig - Send the `sig` signal to the current foreground job, if any.
 */
void send_fg_sig(int sig)
{
    int pid;

    if ((pid = jobs_fgpid(jobs)) < 0) {
        return; /* nothing currently in fg */
    }

    kill(-pid, sig);
}

/* 
 * sigint_handler - The kernel sends a SIGINT to the shell whenver the
 *    user types ctrl-c at the keyboard.  Catch it and send it along
 *    to the foreground job.  
 */
void sigint_handler(int sig)
{
    if (verbose) {
        printf("debug: handle SIGINT\n");
    }

    send_fg_sig(sig);
}

/*
 * sigtstp_handler - The kernel sends a SIGTSTP to the shell whenever
 *     the user types ctrl-z at the keyboard. Catch it and suspend the
 *     foreground job by sending it a SIGTSTP.  
 */
void sigtstp_handler(int sig)
{
    if (verbose) {
        printf("debug: handle SIGTSTP\n");
    }

    send_fg_sig(sig);
}

/*********************
 * End signal handlers
 *********************/

/***********************************************
 * Helper routines that manipulate the job list
 **********************************************/

/* job_clear - Clear the entries in a job struct */
void job_clear(struct job_t *job)
{
    job->pid = 0;
    job->jid = 0;
    job->state = JOB_STATE_UNDEF;
    job->cmdline[0] = '\0';
}

/* jobs_init - Initialize the job list */
void jobs_init(struct job_t *jobs)
{
    int i;

    for (i = 0; i < MAXJOBS; i++) {
        job_clear(&jobs[i]);
    }
}

/* jobs_highest_jid - Returns largest allocated job ID */
int jobs_highest_jid(struct job_t *jobs)
{
    int i, max = 0;

    for (i = 0; i < MAXJOBS; i++) {
        if (jobs[i].jid > max) {
            max = jobs[i].jid;
        }
    }

    return max;
}

/* jobs_add - Add a job to the job list */
struct job_t *
jobs_add(
    struct job_t *jobs,
    pid_t pid,
    enum job_state_t state,
    char *cmdline)
{
    int i;

    if (pid < 1) {
        return NULL;
    }

    for (i = 0; i < MAXJOBS; i++) {
        struct job_t *job = &jobs[i];
        if (job->pid == 0) {
            job->pid = pid;
            job->state = state;
            job->jid = nextjid++;
            if (nextjid > MAXJOBS) {
                nextjid = 1;
            }
            strcpy(job->cmdline, cmdline);
            if (verbose) {
                printf("Added job [%d] %d %s\n", job->jid, job->pid,
                       job->cmdline);
            }
            return job;
        }
    }
    printf("Tried to create too many jobs\n");
    return NULL;
}

/* jobs_remove - Delete a job whose PID=pid from the job list */
int jobs_remove(struct job_t *jobs, pid_t pid)
{
    int i;

    if (pid < 1) {
        return 0;
    }

    for (i = 0; i < MAXJOBS; i++) {
        if (jobs[i].pid == pid) {
            job_clear(&jobs[i]);
            nextjid = jobs_highest_jid(jobs) + 1;
            return 1;
        }
    }

    return 0;
}

/* jobs_fgpid - Return PID of current foreground job, -1 if no such job */
pid_t jobs_fgpid(struct job_t *jobs)
{
    int i;

    for (i = 0; i < MAXJOBS; i++) {
        if (jobs[i].state == JOB_STATE_FG) {
            return jobs[i].pid;
        }
    }

    return -1;
}

/* job_exists_by_pid - Return `true` if job exists (by PID), `false` otherwise */
bool job_exists_by_pid(struct job_t *jobs, pid_t pid)
{
    return jobs_get_by_pid(jobs, pid) != NULL;
}

/* jobs_get_by_pid  - Find a job (by PID) on the job list */
struct job_t *jobs_get_by_pid(struct job_t *jobs, pid_t pid)
{
    int i;

    if (pid < 1) {
        return NULL;
    }

    for (i = 0; i < MAXJOBS; i++) {
        struct job_t *job = &jobs[i];
        if (job->pid == pid) {
            return job;
        }
    }

    return NULL;
}

/* job_exists_by_jid - Return `true` if job exists (by JID), `false` otherwise */
bool job_exists_by_jid(struct job_t *jobs, int jid)
{
    return jobs_get_by_jid(jobs, jid) != NULL;
}

/* jobs_get_by_jid  - Find a job (by JID) on the job list */
struct job_t *jobs_get_by_jid(struct job_t *jobs, int jid)
{
    int i;

    if (jid < 1) {
        return NULL;
    }
    for (i = 0; i < MAXJOBS; i++) {
        if (jobs[i].jid == jid) {
            return &jobs[i];
        }
    }

    return NULL;
}

/* pid_to_jid - Map process ID to job ID */
int pid_to_jid(pid_t pid)
{
    int i;

    if (pid < 1) {
        return 0;
    }
    for (i = 0; i < MAXJOBS; i++) {
        if (jobs[i].pid == pid) {
            return jobs[i].jid;
        }
    }

    return 0;
}

/* jobs_list - Print the job list */
void jobs_list(struct job_t *jobs)
{
    int i;
    struct job_t *job;

    for (i = 0; i < MAXJOBS; i++) {
        job = &jobs[i];
        if (!job_print(job, true)) {
            printf("jobs_list: Internal error: job[%d].state=%d ",
                   i, job->state);
        }
    }
}

/* job_print - Prints the job, optionally showing the job's state */
int job_print(struct job_t *job, bool show_state)
{
    if (job->pid == 0) { return true; }

    printf("[%d] (%d) ", job->jid, job->pid);
    if (show_state) {
        switch (job->state) {
        case JOB_STATE_BG:
            printf("Running ");
            break;
        case JOB_STATE_FG:
            printf("Foreground ");
            break;
        case JOB_STATE_ST:
            printf("Stopped ");
            break;
        default:
            return false;
        }
    }
    printf("%s", job->cmdline);

    return true;
}


/******************************
 * end job list helper routines
 ******************************/


/***********************
 * Other helper routines
 ***********************/

/*
 * usage - print a help message
 */
void usage(void)
{
    printf("Usage: shell [-hvp]\n");
    printf("   -h   print this message\n");
    printf("   -v   print additional diagnostic information\n");
    printf("   -p   do not emit a command prompt\n");
    exit(1);
}

/*
 * unix_error - unix-style error routine
 */
void unix_error(char *msg)
{
    fprintf(stdout, "%s: %s\n", msg, strerror(errno));
    exit(1);
}

/*
 * exec_error - prints error message when exec fails
 */
void exec_error(char *msg)
{
    char *error_msg;
    if (errno == ENOENT) {
        /* make the tests happy */
        error_msg = "Command not found";
    } else {
        error_msg = strerror(errno);
    }

    fprintf(stdout, "%s: %s\n", msg, error_msg);
    exit(1);
}

/*
 * app_error - application-style error routine
 */
void app_error(char *msg)
{
    fprintf(stdout, "%s\n", msg);
    exit(1);
}

/*
 * register_signal - wrapper for the sigaction function
 */
handler_t *register_signal(int signum, handler_t *handler)
{
    struct sigaction action, old_action;

    action.sa_handler = handler;
    sigemptyset(&action.sa_mask); /* block sigs of type being handled */
    action.sa_flags = SA_RESTART; /* restart syscalls if possible */

    if (sigaction(signum, &action, &old_action) < 0) {
        unix_error("Signal error");
    }

    return (old_action.sa_handler);
}

/*
 * sigquit_handler - The driver program can gracefully terminate the
 *    child shell by sending it a SIGQUIT signal.
 */
void sigquit_handler(int sig)
{
    printf("Terminating after receipt of SIGQUIT signal\n");
    exit(1);
}



