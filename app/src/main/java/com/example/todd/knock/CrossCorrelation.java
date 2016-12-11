package com.example.todd.knock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Todd on 11/27/16.
 */

public class CrossCorrelation {
    public CrossCorrelation(){
    }

    public long[] crossCorrelate(short[] A, short[] B){
        return crossCorrelate(A, B, false, 0);
    }

    public long[] crossCorrelate(short[] A, short[] B, int maxLags){
        return crossCorrelate(A, B, true, maxLags);
    }

    public long[] crossCorrelate(short[] A, short[] B, boolean enable_offset, int maxLags){
        if(A.length != B.length){
            return null;
        }

        int aLength = A.length;
        int bLength = B.length;

        int xCorrLength = aLength + bLength-1;
        int start = 0;
        int end = xCorrLength;

        long[] corr = new long[xCorrLength];

        if(enable_offset){
            start = aLength - maxLags;
            end = aLength + maxLags;
        }

        for(int i = start; i < end; i++){
            if(i == aLength-1) {
                for(int j = 0; j <= i; j++) {
                    corr[i] += A[j] * B[j];
                }
            } else if(i < aLength){
                for(int j = 0; j <= i; j++) {
                    corr[i] += A[aLength - i - 1 + j] * B[j];
                }
            } else{
                for(int j = 0; j <= xCorrLength - 1 - i; j++) {
                    corr[i] += A[j] * B[bLength - xCorrLength + i + j];
                }
            }
        }

        return corr;
    }

}
