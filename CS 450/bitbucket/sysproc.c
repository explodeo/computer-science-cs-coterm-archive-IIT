#include "types.h"
#include "x86.h"
#include "defs.h"
#include "date.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "proc.h"
#include "spinlock.h"

extern int join_thread(void **stack);
extern int create_thread(void (*tmain)(void*), void *stack, void *arg);

int
sys_fork(void)
{
  return fork();
}

int
sys_exit(void)
{
  exit();
  return 0;  // not reached
}

int
sys_wait(void)
{
  return wait();
}

int
sys_kill(void)
{
  int pid;

  if(argint(0, &pid) < 0)
    return -1;
  return kill(pid);
}

int
sys_getpid(void)
{
  return myproc()->pid;
}

int
sys_sbrk(void)
{
  int addr;
  int n;

  if(argint(0, &n) < 0)
    return -1;
  addr = myproc()->sz;
  if(growproc(n) < 0)
    return -1;
  return addr;
}

int
sys_sleep(void)
{
  int n;
  uint ticks0;

  if(argint(0, &n) < 0)
    return -1;
  acquire(&tickslock);
  ticks0 = ticks;
  while(ticks - ticks0 < n){
    if(myproc()->killed){
      release(&tickslock);
      return -1;
    }
    sleep(&ticks, &tickslock);
  }
  release(&tickslock);
  return 0;
}

// return how many clock tick interrupts have occurred
// since start.
int
sys_uptime(void)
{
  uint xticks;

  acquire(&tickslock);
  xticks = ticks;
  release(&tickslock);
  return xticks;
}

int
sys_getcount(void){
  int call;
  argint(0,&call);
  if(call<1 || call>29){ return -1; } //invalid syscall number
  int x = (myproc()->syscounts[call]);
  return x;
}


int sys_v2paddr(void){
  int va, pa;
  argint(0,&va);

 //get PGDIR BASE
  struct spinlock lock;
  initlock(&lock, "cpulock");
  acquire(&lock);
  struct cpu *c = mycpu();
  uint *procCR3 = (uint*)c->ts.cr3; //BASE OF PGDIRECTORY
  release(&lock);
  //cprintf("\nPGDIR BASE: 0x%x\n",PTE_FLAGS(&procCR3)); 
  pde_t *pgdir_base, *pde;
  pgdir_base = (pde_t*)procCR3;
  pde = &pgdir_base[PDX(va)]; //BASE OF PGTABLE


  if(*pde & 0x7){ //(PTE_P|PTE_U)
    pde_t *pgtab;
    pgtab = (pde_t*)PTE_ADDR((uint)pde); //REMOVE PDE FLAGS
    //cprintf("\n  PGTAB BASE: 0x%x\n",*pgtab);  

    uint pgframe;
    pgframe = (uint)pgtab[PTX(va)];
    //cprintf("  -->entry: 0x%x\n", pgframe);
    if(pgframe & 0x7){
      pa = PTE_ADDR(pgframe) | PGOFFSET(va);
      return pa;
    }
  }
  return -1;
}

/*********************  THREAD HANDLING ********************

  NOTE: user stack is filled by exec indexed in form stk[ bottom | top -1 |0 ]:
  stk[...|arg x ||| arg 0 | 0 | argptr x ||| argptr 0 | argv (ptr to str args) | argc (no args) | -1]
  note that stk[0] should be -1. 

***********************************************************/

int 
sys_thread_create(void){
  char *tmain, *stack, *arg; //thread starts exec at func tmain w/ args (arg)
  if (argptr(0, &tmain, 1) == -1){ return -1; } //function must be in calling proc's space
  if (argptr(1, &stack, 0)){ return -1; } //stack must be empty (new user stack)
  if (argptr(2, &arg, 0)){ return -1; } //must call with args
  return create_thread((void*) tmain, (void*) stack, (void*) arg);
}

int 
sys_thread_join(void){
  char *stack;
  if((stack = (void*)argptr(0, &stack, 1)) < 0){ return -1; }
  return join_thread((void**)stack); //need to pass to proc.c for the locking structure
}
 
/******************** MUTEX HANDLING *******************
    MAX THREADS = total virtual memory/(stack size*1024*1024) <--- this is for linux
      note:(stack/proc is 1 page table or 4MB)
          so MT = 4GB/(4KB*1024*1024)
                MAXTHREADS = 250. 
      We will keep this at 32 for the time being  
********************************************************/

struct { //32 mutexes max since these operations are costly
  int midx;
  struct spinlock locks[MAXMUTEXES];
} mutexes;

int 
sys_mtx_create(void){
  int locked = argint(0, &locked);
  if(mutexes.midx >= MAXMUTEXES || mutexes.midx < 0){ return -1; } //error if more than 32 mutexes on 32-bit system
  mutexes.midx++;
  struct spinlock *lk = &mutexes.locks[mutexes.midx-1];
  initlock(lk, "mutexlock"); //create mutex
  if(locked){
    acquire(lk); 
  } 
  return mutexes.midx;
}

int 
sys_mtx_lock(void){
  int lockID = argint(0, &lockID);
  if(lockID > mutexes.midx || lockID <= 0){ return -1; } //invalid lock_id
  struct spinlock *lk = &mutexes.locks[lockID-1];
  while (xchg(&lk->locked, 1) != 0);//wait while waiting for lock/sched
  acquire(lk);
  return 0;
}

int 
sys_mtx_unlock(void){
  int lockID = argint(0, &lockID);
  if(lockID > mutexes.midx || lockID <= 0){ return -1; } //invalid lock_id
  struct spinlock *lk = &mutexes.locks[lockID-1];
  //while(holding(lk)); //wait while waiting for lock/sched
  release(lk);
  return 0;
}