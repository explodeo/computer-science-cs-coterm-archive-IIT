#include "types.h"
#include "user.h"
#include "syscall.h"

/**************************************************
* v2ptest - will check a range of addresses from
* VADDR_BASE to VADDR_MAX. Change these values to
* check new address ranges. Increment address check
* by using VADDR_INCREMENT This will only print out 
* valid address translations. 
*
* This will not  catch pagefaults on translate call
***************************************************/

//#define VADDR_BASE 0x8CFF0000 //we will scan for Physical addresses from this base
//#define VADDR_MAX 0x8DFFFFFF  //max address to scan
//#define	VADDR_INCREMENT	1   //how much to change virtual address by on each check

int main(int argc, char *argv[]){
  uint VADDR_BASE, VADDR_MAX, VADDR_INCREMENT;
  if (argc > 1){
	if(argc == 4){
		VADDR_BASE = hextoint(argv[1]);
		VADDR_MAX = hextoint(argv[2]);
		VADDR_INCREMENT = atoi(argv[3]);
		if(VADDR_BASE == -1 || VADDR_MAX == -1 || VADDR_INCREMENT == -1){
			printf(1,"%x\n",hextoint(argv[1]));
			printf(1,"%x\n",hextoint(argv[2]));
			printf(1,"%x\n",hextoint(argv[3]));
			printf(1, "Incorrect args. USAGE: v2ptest 0x<addr_start> 0x<addr_end> <increment>\n");
			exit();
		}
	}
	else {
		printf(1, "Too few args. USAGE: v2ptest 0x<addr_start> 0x<addr_end> <increment>\n");
		exit();
	}
  }
  else{
  	VADDR_BASE = 0;
	VADDR_MAX = 0x80000000;
	VADDR_INCREMENT = 1;
  }
  uint vaddr, invalid=1, paddr;
  vaddr=VADDR_BASE;
  while(vaddr < VADDR_MAX){
  	paddr = v2paddr(vaddr);
	if (paddr != -1){ //if syscall return statement is valid
		printf(1,"Virtual Address: 0x%x -> Physical Address: 0x%x\n", vaddr, paddr);
		invalid = 0;
  	}
  	//if(vaddr%0x00001000 == 0){ printf(1, "\nVA: 0x%x\n", vaddr);}
  	vaddr+=VADDR_INCREMENT;
  }
  if(invalid){ printf(1, "TEST PROGRAM FAILED\n"); }
  exit();
}