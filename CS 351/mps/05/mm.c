#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "mm.h"
#include "memlib.h"
#define ALIGN(p) (((size_t)(p) + (ALIGNMENT-1)) & ~0x7)
#define SIZE_T_SIZE (ALIGN(sizeof(size_t)))
#define ALIGNMENT 8
#define WSIZE 4 
#define DSIZE 8 
#define CHUNKSIZE 16  
#define MINIMUM 24
#define MAX(x, y) ((x) > (y) ? (x) : (y))
#define GET(p) (*(int *)(p))
#define PUT(p, val) (*(int *)(p) = (val))
#define GET_SIZE(p) (GET(p) & ~0x7)
#define GET_ALLOC(p) (GET(p) & 0x1)
#define HDRP(ptr) ((void *)(ptr) - WSIZE)
#define FTRP(ptr) ((void *)(ptr) + GET_SIZE(HDRP(ptr)) - DSIZE)
#define NEXT_BLKP(ptr)  ((void *)(ptr) + GET_SIZE(HDRP(ptr)))
#define PREV_BLKP(ptr)  ((void *)(ptr) - GET_SIZE(HDRP(ptr) - WSIZE))
#define NEXT_FREEP(ptr)(*(void **)(ptr + DSIZE))
#define PREV_FREEP(ptr)(*(void **)(ptr))
#define LOAD(size, alloc) ((size)|(alloc))
static char *heap_listp = 0; 
static char *free_listp = 0; 
static void *extendHeap(size_t words);
static void place(void *ptr, size_t asize);
static void *findFit(size_t asize);
static void *coalesce(void *ptr);
static void removeBlock(void *ptr); 
int mm_init(void){
  if ((heap_listp = mem_sbrk(2*MINIMUM)) == NULL){ return -1; }
  PUT(heap_listp, 0); 
  PUT(heap_listp + WSIZE, LOAD(MINIMUM, 1)); 
  PUT(heap_listp + DSIZE, 0); 
  PUT(heap_listp + DSIZE+WSIZE, 0); 
  PUT(heap_listp + MINIMUM, LOAD(MINIMUM, 1)); 
  PUT(heap_listp+WSIZE + MINIMUM, LOAD(0, 1)); 
  free_listp = heap_listp + DSIZE; 
  if (extendHeap(CHUNKSIZE/WSIZE) == NULL){ return -1; }
  return 0;
}
void *mm_malloc(size_t size){
  size_t asize;      
  size_t extendsize; 
  char *ptr; 
  if (size <= 0) { return NULL; }  
  asize = MAX(ALIGN(size) + DSIZE, MINIMUM);
  if ((ptr = findFit(asize))){
    place(ptr, asize);
    return ptr;
  }
  extendsize = MAX(asize, CHUNKSIZE);
  if ((ptr = extendHeap(extendsize/WSIZE)) == NULL) { return NULL; }
  place(ptr, asize);
  return ptr;
} 
void mm_free(void *ptr){
  size_t size = GET_SIZE(HDRP(ptr));
  PUT(HDRP(ptr), LOAD(size, 0)); 
  PUT(FTRP(ptr), LOAD(size, 0));
  coalesce(ptr); 
}
void *mm_realloc(void *ptr, size_t size){
  size_t oldSize;
  void* newptr;
  newptr = mm_malloc(size);
  if (!newptr && size != 0) return NULL;
  if (ptr == NULL) return newptr;
  if (size != 0) { 
    oldSize = GET_SIZE(HDRP(ptr));
    if (size < oldSize){ oldSize = size; }
    memcpy(newptr, ptr, oldSize);
  } 
  else { newptr = 0; }
  mm_free(ptr);
  return newptr;
}

static void place(void *ptr, size_t asize){
  size_t csize = GET_SIZE(HDRP(ptr));
  if ((csize - asize) >= MINIMUM) {
    PUT(HDRP(ptr), LOAD(asize, 1));
    PUT(FTRP(ptr), LOAD(asize, 1));
    removeBlock(ptr);
    ptr = NEXT_BLKP(ptr);
    PUT(HDRP(ptr), LOAD(csize-asize, 0));
    PUT(FTRP(ptr), LOAD(csize-asize, 0));
    coalesce(ptr);
  } else {
    PUT(HDRP(ptr), LOAD(csize, 1));
    PUT(FTRP(ptr), LOAD(csize, 1));
    removeBlock(ptr);
  }
}
static void *findFit(size_t asize){
  void *ptr = NULL;
  void *minp = NULL;
  size_t psize;
  size_t minsize = 0;
  int x=0;
  for(ptr = free_listp; GET_ALLOC(HDRP(ptr)) == 0; ptr = NEXT_FREEP(ptr)){
    x++;
    if (x > 29){ break; }
    psize = (size_t)GET_SIZE(HDRP(ptr));
    if (asize <= psize){
      if (!minsize || psize < minsize){
        minsize = psize;
        minp = ptr;
      }
    }
  }
  if (minp){ return minp; }
  else { return NULL; }
}
static void *extendHeap(size_t words){
  char *ptr;
  size_t size;
  size = (words % 2) ? (words+1) * WSIZE : words * WSIZE;
  if (size < MINIMUM){ size = MINIMUM; }
  if ((long)(ptr = mem_sbrk(size)) == -1) { return NULL; }
  PUT(HDRP(ptr), LOAD(size, 0));         
  PUT(FTRP(ptr), LOAD(size, 0));         
  PUT(HDRP(NEXT_BLKP(ptr)), LOAD(0, 1)); 
  return coalesce(ptr);
}
static void *coalesce(void *ptr){
  size_t prev_alloc;
  prev_alloc = GET_ALLOC(FTRP(PREV_BLKP(ptr))) || PREV_BLKP(ptr) == ptr;
  size_t next_alloc = GET_ALLOC(HDRP(NEXT_BLKP(ptr)));
  size_t size = GET_SIZE(HDRP(ptr));
  if (prev_alloc && !next_alloc){     
    size += GET_SIZE(HDRP(NEXT_BLKP(ptr)));
    removeBlock(NEXT_BLKP(ptr));
    PUT(HDRP(ptr), LOAD(size, 0));
    PUT(FTRP(ptr), LOAD(size, 0));
  }
  else if (!prev_alloc && next_alloc){   
    size += GET_SIZE(HDRP(PREV_BLKP(ptr)));
    ptr = PREV_BLKP(ptr);
    removeBlock(ptr);
    PUT(HDRP(ptr), LOAD(size, 0));
    PUT(FTRP(ptr), LOAD(size, 0));
  }
  else if (!prev_alloc && !next_alloc){   
    size += GET_SIZE(HDRP(PREV_BLKP(ptr))) + GET_SIZE(HDRP(NEXT_BLKP(ptr)));
    removeBlock(PREV_BLKP(ptr));
    removeBlock(NEXT_BLKP(ptr));
    ptr = PREV_BLKP(ptr);
    PUT(HDRP(ptr), LOAD(size, 0));
    PUT(FTRP(ptr), LOAD(size, 0));
  }
  NEXT_FREEP(ptr) = free_listp; 
  PREV_FREEP(free_listp) = ptr; 
  PREV_FREEP(ptr) = NULL; 
  free_listp = ptr; 
  return ptr;
}
static void removeBlock(void *ptr){
  if (PREV_FREEP(ptr)) { NEXT_FREEP(PREV_FREEP(ptr)) = NEXT_FREEP(ptr); }
  else { free_listp = NEXT_FREEP(ptr); }
  PREV_FREEP(NEXT_FREEP(ptr)) = PREV_FREEP(ptr);
}
