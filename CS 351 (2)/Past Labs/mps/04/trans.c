/*
Cache:
	offset - lg(block size)
	index - lg(no. lines)
	tag - remaining bits

	|========tag========|====index====|=offset=|

	write hit:
		write through: update memory & cache
		write back: update cache only then update memory when block changes
	Write miss:
		write around: update memory only
		write allocate: allocate space in cache then write hit

	Logical scenarios:
		 write through + write around (becuase both depend on memory access)
		 write back + write allocate (reduce misses in data with WB policy)
*/
/* 
 * trans.c - Matrix transpose B = A^T
 *
 * Each transpose function must have a prototype of the form:
 * void trans(int M, int N, int A[N][M], int B[M][N]);
 *
 * A transpose function is evaluated by counting the number of misses
 * on a 1KB direct mapped cache with a block size of 32 bytes.
 */ 
#include <stdio.h>
#include "cachelab.h"

int is_transpose(int M, int N, int A[N][M], int B[M][N]);

/* 
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded. 
 */
char transpose_submit_desc[] = "Transpose submission";
void transpose_submit(int M, int N, int A[N][M], int B[M][N]) {
  /*
	32 × 32: 8 points if m < 300, 0 points if m > 600
	64 × 64: 8 points if m < 1,300, 0 points if m > 2,000
	61 × 67: 10 points if m < 2,000, 0 points if m > 3,000
  */

	int bSize; //variable for size of block, used in each of the iterations, N ==32, N ==63 and the else 
	int bR, bC; //to iterate over blocks, user in outer loops
	int r, c; //to iterate through each block, used in inner loops
	int temp = 0, d = 0; //d stands for diagonal, temp is just a temporary variable
	int v0,v1,v2,v3,v4; //Variables to be used in the N==64 case for various assignments within it
	/*
		Using bSize = 8 in this case. Only N == 32 is used in the condition since matrix transpose can
		occur for any a*b and c*a where only a needs to be same and b and c can vary.
		Blocking is used here. 
		4 levels of loops are used here. 2 outer loops iterate accross blocks (in column major iteration) while the 2 inner loops iterate through each block. 
	*/
	if (N == 32) {
		bSize = 8;
		for(bC = 0; bC < N; bC += 8) {
			for(bR = 0; bR < N; bR += 8) {
				for(r = bR; r < bR + 8; r++) {
					for(c = bC; c < bC + 8; c++) {
						//Row and column are not equal
						if(r != c){ B[c][r] = A[r][c]; }
						else { //Store in temp instead of missing in B[j][i] to decrease misses
							temp = A[r][c];
							d = r;
						}
					}
					//We don't move elements on diagonals since we are transposing a square matrix
					if (bR == bC)	{ B[d][d] = temp; }
				}
			}
		}
	}

	/* Using bSize = 4 here. 
	2 levels of loops are used 
	We assign elements in each row individually. Causes reduced missess. */
	else if (N == 64) {	
 		bSize = 4;
		for(r = 0; r < N; r += bSize) {
			for(c = 0; c < M; c += bSize) {
				/*Elements in A[r][], A[r+1][], A[r+2][] are assigned to the variables for use throughout this loop
				This is becuase we are only allowed to modify the second matrix B but not the matrix A */
				v0 = A[r][c];
				v1 = A[r+1][c];
				v2 = A[r+2][c];
				v3 = A[r+2][c+1];
				v4 = A[r+2][c+2];
				//Elements in B[c+3][] are assigned
				B[c+3][r] = A[r][c+3];
				B[c+3][r+1] = A[r+1][c+3];
				B[c+3][r+2] = A[r+2][c+3];
				//Elements in B[c+2][] are assigned 
				B[c+2][r] = A[r][c+2];
				B[c+2][r+1] = A[r+1][c+2];
				B[c+2][r+2] = v4;
				v4 = A[r+1][c+1];
				//Elements in B[c+1][] are assigned
				B[c+1][r] = A[r][c+1];
				B[c+1][r+1] = v4;
				B[c+1][r+2] = v3;
				//Elements in B[c][] are assigned
				B[c][r] = v0;
				B[c][r+1] = v1;
				B[c][r+2] = v2;
				//Elements in row A[r+3][] are assigned to the left out elements in B (where B has r+3)
				B[c][r+3] = A[r+3][c];
				B[c+1][r+3] = A[r+3][c+1];
				B[c+2][r+3] = A[r+3][c+2];
				v0 = A[r+3][c+3];
				//Finally, elements in row B[c+3][] are assigned
				B[c+3][r+3] = v0;
			}
		}
	}

	/* This is the case for a random matrix size. We use bSize = 16 
	2 levels of loops are used to iterate over blocks in column major iteration and 2 levels are used to go through the blocks	*/
	else  {
		bSize = 16;
		for (bC = 0; bC < M; bC += bSize) {
			for (bR = 0; bR < N; bR += bSize) {	
				/*Since our sizes can be odd, not all blocks will be square. Special case: if (bR + 16 > N), we get an invalid access. 
				We also do regular check for i<N and j<M */
				for(r = bR; (r < N) && (r < bR + bSize); r++) {
					for(c = bC; (c < M) && (c < bC + bSize); c++) {
						//row and column are not same
						if (r != c) { B[c][r] = A[r][c]; }
						
						//row and column same 
						else {
							temp = A[r][c];
							d = r;
						}
					}
					
					//Row and column number are same in the blocks, diagonal element assigned
					if(bR == bC) { B[d][d] = temp; }
				}
			}
		}
	}
}


/*
 * registerFunctions - This function registers your transpose
 *     functions with the driver.  At runtime, the driver will
 *     evaluate each of the registered functions and summarize their
 *     performance. This is a handy way to experiment with different
 *     transpose strategies.
 */
void registerFunctions()
{
    /* Register your solution function */
    registerTransFunction(transpose_submit, transpose_submit_desc); 
	//Used only 1 function for all cases

    /* Register any additional transpose functions */
    // registerTransFunction(trans, trans_desc); 

}

/* 
 * is_transpose - This helper function checks if B is the transpose of
 *     A. You can check the correctness of your transpose by calling
 *     it before returning from the transpose function.
 */
int is_transpose(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; ++j) {
            if (A[i][j] != B[j][i]) {
                return 0;
            }
        }
    }
    return 1;
}

