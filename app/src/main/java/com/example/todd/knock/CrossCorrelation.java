package com.example.todd.knock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Todd on 11/27/16.
 */

public class CrossCorrelation {
    public CrossCorrelation(){
    }

    public static long[] crossCorrelate(short[] A, short[] B){
        int length = A.length + B.length-1;

        long[] corr = new long[length];

        for(int i = A.length-200; i < A.length+200; i++){
            if(i == A.length-1) {
                for(int j = 0; j <= i; j++) {
                    corr[i] += A[j] * B[j];
                }
            } else if(i < A.length){
                for(int j = 0; j <= i; j++) {
                    corr[i] += A[A.length - i - 1 + j] * B[j];
                }
            } else{
                for(int j = 0; j <= length - 1 - i; j++) {
                    corr[i] += A[j] * B[B.length - length + i + j];
                }
            }
        }

        return corr;
    }

}
