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

/* it is easier to make a function for each matrix type rather than make a single big function */
void transpose_32x32_Matrix(int M, int N, int A[N][M], int B[M][N]);
void transpose_64x64_Matrix(int M, int N, int A[N][M], int B[M][N]);
void transpose_Matrix(int M, int N, int A[N][M], int B[M][N]);

/* 
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded. 
 */
char transpose_submit_desc[] = "Transpose submission";
void transpose_submit(int M, int N, int A[N][M], int B[M][N]){
  if(N == 32){
    int t0; //temp register
    int r, c; //index in row or column
    int bSize, bR, bC; //block size, block row, block column
    
    bSize = 8; //because 32/4 = 8 and 8 cannot be mapped in a square matrix
    
    for(bR = 0; bR < N; bR += bSize){ //iterate parent matrix columns
      for(bC = 0; bC < N; bC += bSize){ //iterate parent matrix rows	

	for(r = bR; r < bR + bSize; r++){ //iterate submatrix rows

	  for(c = bC; c < bC + bSize; c++){ //iterate submatrix 
	    if(r != c) { 
	      B[c][r] = A[r][c]; 
	    }
	  }
	  if(bR == bC){ //if a diagnol in matrix 
	    t0 = A[r][r]; 
	    B[r][r] = t0; 
	  }
	}
      }
    }
  }
  else if(N == 64){
    int bR, bC, r, t0; //only transpose in rows and use temp register for smaller submatrices 
    int s0, s1, s2, s3, s4, s5, s6, s7; // 8 registers for a block size of 8
    
    //This can take O(NlogN) time and spatial complexity for this algorithm
    //Hence, we take our (similar) and better case of the 32x32 matrix which takes 287 misses
    //This operation LINEARLY grows (with respect to N
    //Therefore the optimal case for this is O(287*log(64)) = O(1722)
    //Due to the nature of the structure and using EIGHT registers (to store a full block in cache)
    //we can then base the algorithm of the growth rate of its smallest substructure which is O(logN)
    //our smallest substructure has a size of 8 so it can perform with O(log(8)) misses per block
    //this is much less of a miss rate if we use a "magic" block size of 8 rather than 4.
    //We can achieve a minimum of o(1148) misses. Our bound is 1300 misses. this is possible

    //Note: we store two submatrices into cache when we transpose
    //      It is a very good idea to store positions A(x) and A(y) such that A(x) transposes at location B(y) and A(y) to B(x) where in B y=x+c and c is a constant

    for(bR=0; bR<64; bR+=8){ //row wise transpose 
      for(bC=0; bC<64; bC+=8){
	for(r=bR; r<bR+4; r++){ //also a row wise transpose in submatrices

	  //storing all vals in row R into a submatrix (lets call this K)
	  s5 = A[r][bC];
	  s7 = A[r][bC+1];
	  s6 = A[r][bC+2];
	  s0 = A[r][bC+3];
	  s1 = A[r][bC+4];
	  s2 = A[r][bC+5];
	  s3 = A[r][bC+6];
	  s4 = A[r][bC+7];
	  //assigning row R values in B (transposing K)
	  B[bC][r] = s5;
	  B[bC][r+4] = s2;
	  B[bC+1][r] = s7;
	  B[bC+1][r+4] = s3;
	  B[bC+2][r] = s6;
	  B[bC+2][r+4] = s4;
	  B[bC+3][r] = s0;
	  B[bC+3][r+4] = s1;
	}

        //storing values in A at location of K' (K prime) into a submatrix (lets call this L)
	s5 = A[bR+4][bC+4];
	s7 = A[bR+5][bC+4];
	s6 = A[bR+6][bC+4];
	s0 = A[bR+7][bC+4];
	s1 = A[bR+4][bC+3];
	s2 = A[bR+5][bC+3];
	s3 = A[bR+6][bC+3];
	s4 = A[bR+7][bC+3];
	//assigning values of L into B at location K' in B (keeping diagnols constant without use of registers)
	B[bC+4][bR] = B[bC+3][bR+4];
	B[bC+4][bR+4] = s5;
	B[bC+3][bR+4] = s1;
	B[bC+4][bR+1] = B[bC+3][bR+5];
	B[bC+4][bR+5] = s7;
	B[bC+3][bR+5] = s2;
	B[bC+4][bR+2] = B[bC+3][bR+6];
	B[bC+4][bR+6] = s6;
	B[bC+3][bR+6] = s3;
	B[bC+4][bR+3] = B[bC+3][bR+7];
	B[bC+4][bR+7] = s0;
	B[bC+3][bR+7] = s4;
	
	//while the addresses of submatrix L and L' are stored, transpose L' into B 
	for(t0=0;t0<3;t0++){
	  s5 = A[bR+4][bC+5+t0];
	  s7 = A[bR+5][bC+5+t0];
	  s6 = A[bR+6][bC+5+t0];
	  s0 = A[bR+7][bC+5+t0];
	  s1 = A[bR+4][bC+t0];
	  s2 = A[bR+5][bC+t0];
	  s3 = A[bR+6][bC+t0];
	  s4 = A[bR+7][bC+t0];

	  B[bC+5+t0][bR] = B[bC+t0][bR+4];
	  B[bC+5+t0][bR+4] = s5;
	  B[bC+t0][bR+4] = s1;
	  B[bC+5+t0][bR+1] = B[bC+t0][bR+5];
	  B[bC+5+t0][bR+5] = s7;
	  B[bC+t0][bR+5] = s2;
	  B[bC+5+t0][bR+2] = B[bC+t0][bR+6];
	  B[bC+5+t0][bR+6] = s6;
	  B[bC+t0][bR+6] = s3;
	  B[bC+5+t0][bR+3] = B[bC+t0][bR+7];
	  B[bC+5+t0][bR+7] = s0;
	  B[bC+t0][bR+7] = s4;
	}
      }
    }

  }
  else{
    int t0, t1; //temp register
    int r, c; //index in row or column
    int bSize, bR, bC; //block size, block row, block column
    
    bSize = 16;
    
    for(bC = 0; bC < M; bC += bSize){ //iterate parent matrix columns
      for(bR = 0; bR < N; bR += bSize){ //iterate parent matrix rows	
	
	for(r = bR; (r < N) && (r < bR + bSize); r++){ //iterate submatrix rows
	  for(c = bC; (c < M) && (c < bC + bSize); c++){ //iterate submatrix 
	    
	    if(r != c) { B[c][r] = A[r][c]; }
	    else { t0 = A[r][c]; t1 = r; }

	  }
	  if(bR == bC){ B[t1][t1] = t0; }
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
}

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

