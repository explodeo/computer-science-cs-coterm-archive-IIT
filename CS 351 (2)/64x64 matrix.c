if (N == 64 && M == 64){  // 64x64 square              
    for(bC = 0; N > bC; bC += 4){ // Traverses horizontally through blocks via N by size of block
        for(bR = 0; N > bR; bR += 4){ // Traverse vertically through blocks via M by size of block
            for(r = bR; bR+4 > r; r++){ // Progress by row via r
                for(c = bC; bC+4 > c; c++){ // Progress by column via c
                    if(r != c){ // If the index are not set equal, than the current matrix elements are not diagonal
                        B[c][r] = A[r][c]; // Swap
                    }
                    else{ // If the two index are equal, it is diagonal so...
                        buff = A[r][c]; // Set buff equal to position within A this iteration
                        tmp = r; // Hold index of current diagonal
                    }            
                }
                if(bC == bR){
                    B[tmp][tmp] = buff; // Set B[r][r] equal to A[r][c] for diagonal transposition
                }
            } 
        }
    }  
}



void transpose_64_64(int M, int N, int A[N][M], int B[M][N]) {
    int rowIndex, colIndex;
    int reg1, reg2, reg3, reg4, reg5, reg6, reg7, reg8;
    int ct; // Since only 12 local variables are allowed, 8 is used as a magic #
    for (rowIndex = 0; rowIndex < N; rowIndex += 8) {
        for (colIndex = 0; colIndex < M; colIndex += 8) {
            // Now operating block-wise
            for (ct = 0; ct < 8; ct++){
                // Utilize the local variables as "registers" to avoid
                // future misses
                // Store the 4 numbers in the current row in A 
                reg1 = A[colIndex + ct][rowIndex];
                reg2 = A[colIndex + ct][rowIndex + 1];
                reg3 = A[colIndex + ct][rowIndex + 2];
                reg4 = A[colIndex + ct][rowIndex + 3];
                if (0 == ct) {
                    // If on the first iteration within a block,
                    // Store the start of each row
                    reg5 = A[colIndex + ct][rowIndex + 4];
                    reg6 = A[colIndex + ct][rowIndex + 5];
                    reg7 = A[colIndex + ct][rowIndex + 6];
                    reg8 = A[colIndex + ct][rowIndex + 7];
                }
                // Now consider the block in B to be transposed into
                // Advancing in 64s (jump across an entire row), swap
                B[rowIndex][colIndex + ct] = reg1;
                B[rowIndex][colIndex + ct + 64] = reg2;
                B[rowIndex][colIndex + ct + 128] = reg3;
                B[rowIndex][colIndex + ct + 192] = reg4;
            }
            // Now go down (note how there's only 7 iterations since there is a
            // corner block shared in row-wise and column-wise iterations
            for (ct = 7; ct > 0; ct--) {
                reg1 = A[colIndex + ct][rowIndex + 4];
                reg2 = A[colIndex + ct][rowIndex + 5];
                reg3 = A[colIndex + ct][rowIndex + 6];
                reg4 = A[colIndex + ct][rowIndex + 7];
                B[rowIndex + 4][colIndex + ct] = reg1;
                B[rowIndex + 4][colIndex + ct + 64] = reg2;
                B[rowIndex + 4][colIndex + ct + 128] = reg3;
                B[rowIndex + 4][colIndex + ct + 192] = reg4;
            }
            // Consider the block in B to be transposed into
            // Advancing in 64s (jump across an entire row), swap
            B[rowIndex + 4][colIndex] = reg5;
            B[rowIndex + 4][colIndex + 64] = reg6;
            B[rowIndex + 4][colIndex + 128] = reg7;
            B[rowIndex + 4][colIndex + 192] = reg8;
        }
    } 
}








if (N == 64) { 
    for(bR=0; bR<64; bR+=8){
        for(bC=0; bC<64; bC+=8){
            for(r=bR; r<bR+4; r++){ 
                bSize = A[r][bC];
                t0 = A[r][bC+1];
                bD = A[r][bC+2];
                s0 = A[r][bC+3];
                s1 = A[r][bC+4];
                s2 = A[r][bC+5];
                s3 = A[r][bC+6];
                s4 = A[r][bC+7];
                B[bC][r] = bSize;
                B[bC][r+4] = s2;
                B[bC+1][r] = t0;
                B[bC+1][r+4] = s3;
                B[bC+2][r] = bD;
                B[bC+2][r+4] = s4;
                B[bC+3][r] = s0;
                B[bC+3][r+4] = s1;
            }
            
            bSize = A[bR+4][bC+4];
            t0 = A[bR+5][bC+4];
            bD = A[bR+6][bC+4];
            s0 = A[bR+7][bC+4];
            s1 = A[bR+4][bC+3];
            s2 = A[bR+5][bC+3];
            s3 = A[bR+6][bC+3];
            s4 = A[bR+7][bC+3];
            B[bC+4][bR] = B[bC+3][bR+4];
            B[bC+4][bR+4] = bSize;
            B[bC+3][bR+4] = s1;
            B[bC+4][bR+1] = B[bC+3][bR+5];
            B[bC+4][bR+5] = t0;
            B[bC+3][bR+5] = s2;
            B[bC+4][bR+2] = B[bC+3][bR+6];
            B[bC+4][bR+6] = bD;
            B[bC+3][bR+6] = s3;
            B[bC+4][bR+3] = B[bC+3][bR+7];
            B[bC+4][bR+7] = s0;
            B[bC+3][bR+7] = s4;

            for(r=0;r<3;r++){
                bSize = A[bR+4][bC+5+r];
                t0 = A[bR+5][bC+5+r];
                bD = A[bR+6][bC+5+r];
                s0 = A[bR+7][bC+5+r];
                s1 = A[bR+4][bC+r];
                s2 = A[bR+5][bC+r];
                s3 = A[bR+6][bC+r];
                s4 = A[bR+7][bC+r];

                B[bC+5+r][bR] = B[bC+r][bR+4];
                B[bC+5+r][bR+4] = bSize;
                B[bC+r][bR+4] = s1;
                B[bC+5+r][bR+1] = B[bC+r][bR+5];
                B[bC+5+r][bR+5] = t0;
                B[bC+r][bR+5] = s2;
                B[bC+5+r][bR+2] = B[bC+r][bR+6];
                B[bC+5+r][bR+6] = bD;
                B[bC+r][bR+6] = s3;
                B[bC+5+r][bR+3] = B[bC+r][bR+7];
                B[bC+5+r][bR+7] = s0;
                B[bC+r][bR+7] = s4;
            }
        }
    }
}