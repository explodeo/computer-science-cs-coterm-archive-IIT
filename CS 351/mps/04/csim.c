#include "cachelab.h"
#include <unistd.h>
#include <stdlib.h>
#include <getopt.h>
#include <string.h>
#include <stdio.h>

#define USER_ERROR 351 //file not found error or user messed up

/* make a boolean type and 64bit-int_t */
typedef unsigned long long int u64int_t;
typedef int bool;

FILE *tracefile; // pointer to file used to read in

/*needed for getopt*/
extern char *optarg; 
extern int optind, opterr, optopt, errno;

/*most basic cache structure*/
typedef struct cacheline {
  int age;
  bool valid;
  u64int_t tag;
} cacheline;

/*init global vars*/
bool VERBOSE = 0;
int hitcount = 0;
int misscount = 0;
int evictcount = 0;
int lines, b, s;

cacheline **cache;

void simulate_cache(char inst, u64int_t address, int size){
    
  /* cache will default evict unless a bad tag is found */
  bool evictflag = 1; 
  /* need these for evicting a line */
  int lruline = 0, mruline = 0;

  u64int_t curTag = (address >> s) >> b;
  u64int_t curSet = ((address << (64 - s - b)) >> (64 - s));

  if(VERBOSE){ printf("%c %llx,%d ", inst, address, size); }

  /* this will break and return on a hit. otherwise, it will write through and evict or miss */ 
  int x;
  for(x=0; x<lines; x++){
    /*no eviction needed if data is bad. just miss then hit */
    if(!cache[curSet][x].valid){ 
      evictflag = 0; 
    }
        
    /* if valid and tags match, data hit */
    if(cache[curSet][x].valid && cache[curSet][x].tag == curTag){
      cache[curSet][x].age++;
      hitcount++; 
      if(VERBOSE){ printf("hit "); }

      return; //return on hit
    }

    /*finds the lru line in cache*/
    if(cache[curSet][lruline].age > cache[curSet][x].age){ 
      lruline = x; 
    }

    /* finds the mru line in cache 
     * mru line is the line that evicts lru line */
    else if(cache[curSet][mruline].age < cache[curSet][x].age){ 
      mruline = x; 
    }
  }

  /* if data miss or evict */
  cacheline newLine = (cacheline){ .tag = curTag, .valid = 1, .age = cache[curSet][mruline].age + 1};

  if(evictflag){
    cache[curSet][lruline] = newLine;
    evictcount++;
  } 
  else{ //if the eviction flag isn't triggered, search for the first bad line and assign
    for(x=0; x<lines; x++){
      if(!cache[curSet][x].valid){
	cache[curSet][x] = newLine;
	break;
      }
    }
  }
  /* increment misscount if the function had to miss or evict */
  misscount++; 
  if(VERBOSE){ printf("miss "); }
  if(evictflag && VERBOSE){ printf("evict "); }
}

int main(int argc, char **argv){
  int opt;
  u64int_t address;
  /*scan the shell args */
  while ((opt = getopt(argc, argv, "hvs:E:b:t:")) != -1){
    switch (opt){
    case 'v': VERBOSE = 1; break;
    case 's': s = atoi(optarg); break;
    case 'E': lines = atoi(optarg); break;
    case 'b': b = atoi(optarg); break;
    case 't': tracefile = fopen(optarg , "r"); break;
    case 'h': break; //printUsage(); exit(0);
    default: exit(USER_ERROR);
    }
  }

  char inst, buf[32];
  int size;

  // compute the number of sets as 2^s
  u64int_t numsets = 2 << s;

  cache = malloc(numsets*sizeof(struct cacheline*));

  // allocate space for E lines in each set
  int x;
  for(x = 0; x < numsets; x++){
    cache[x] = malloc(lines*sizeof(struct cacheline));
  }
  
  /* parse the file */
  while (fgets(buf, 32, tracefile)){ //scan nextline
    if(VERBOSE){ printf("\n"); }//put next iteration on a new line
    if(sscanf(buf, " %s %llx,%d", &inst, &address, &size) == 3){ //sscanf is like print but reads
      switch(inst){
      case 'L': //these cases require only one call
      case 'S': //these cases require only one call
	simulate_cache(inst, address, size); break;
      case 'M': 
	simulate_cache(inst, address, size);
	simulate_cache(inst, address, size);
	break;
      default:
	break;
      }
    }
  }
    
  fclose(tracefile);
  //free_cache(cacheArray, numsets, lines);
  printSummary(hitcount, misscount, evictcount);
  return 0;
}
