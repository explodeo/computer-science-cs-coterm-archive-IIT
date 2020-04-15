#include "types.h"
#include "user.h"
#include "syscall.h"

/****************** THREADING & MUTEX TEST ************************* 
 threads should execute concurrently and will start/end sequentially
 without a mutex jobs will start/end in an entirely random order
********************************************************************/  

//SYSTEM WILL CRASH IF MAXTHREADS LARGER THAN 61
#define THREAD_MEMORY_LIMIT 61 
#define MAXTHREADS 43 //this is the number of threads we are testing (keep small enough for stack mem)



int tid=0;    //tid ID (global)
int mutex;    //mutex for threads
uint *stack;  //thread stack
uint x,glob;  //global var

void thread_job(void *arg){
  mtx_lock(mutex);
  tid++;
  //printf(1, "  Job %d started",tid);
  for(x=0;x<1000000;x++){
    glob++;
  }
  printf(1, ", glob = %d\n",glob);
  mtx_unlock(mutex);
  exit();
}

int main(int argc, char *argv[]){
  mutex = mtx_create(1);                    //no I do not want to lock initially
  printf(1, "\nMAIN THREAD: %d\n", tid);
  stack = (uint*) malloc(32*sizeof(uint));
  while(tid<MAXTHREADS){
    mtx_lock(mutex);
    printf(1, "Creating Thread %d", (tid+1));
    thread_create(thread_job, (void*)stack, (void*)0);
    mtx_unlock(mutex);
    sleep(3); //disallow simultaneous mutex access & printing interrupts
  }
  while(thread_join((void**)&stack)!=-1);
  printf(1, "freeing stack\n");
  free(stack);
  printf(1, "EXIT MAIN THREAD\n");
  exit();
} 