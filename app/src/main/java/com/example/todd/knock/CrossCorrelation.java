package com.example.todd.knock;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Todd on 11/27/16.
 */

public class CrossCorrelation {
    private Thread thread1 = null;
    private Thread thread2 = null;
    private Thread thread3 = null;
    private Thread thread4 = null;

    private final ReentrantLock lock = new ReentrantLock();

    private short[] A;
    private short[] B;
    private int maxLags;

    private long[] xcorr;
    int flag;


    public CrossCorrelation(short[] A, short[] B, int maxLags){
        this.A = A;
        this.B = B;
        this.maxLags = maxLags;
    }

    public long[] crossCorrelate(short[] A, short[] B){
        return crossCorrelate(A, B, false, 0);
    }

    public long[] crossCorrelate(short[] A, short[] B, int maxLags){
        return crossCorrelate(A, B, true, maxLags);
    }

    public long[] run(){
        if(A.length != B.length){
            return null;
        }

        int aLength = A.length;
        int bLength = B.length;

        int xCorrLength = aLength + bLength-1;
        int start = 0;
        int end = xCorrLength;

        xcorr = new long[xCorrLength];

        if(maxLags != 0){
            start = aLength - maxLags;
            end = aLength + maxLags;
        }
        int flag = 0;

        xcorr = new long[xCorrLength];
        Log.d("XCORR", "Starting threads");

        thread1 = new Thread(new Runnable() {
            public void run() {
                partialXcorr(0);
            }
        }, "XCorr Thread 1");

        thread2 = new Thread(new Runnable() {
            public void run() {
                partialXcorr(1);
            }
        }, "XCorr Thread 2");
        thread1.run();
        thread2.run();
        Log.d("XCORR", "Blocking");

        try{
            thread1.join(0);
            thread2.join(0);
        } catch (Exception e){
            Log.e("XCORR", e.toString());
        }
        Log.d("XCORR", "Done with cross correlation");


        thread1 = null;
        thread2 = null;

        return xcorr;
    }

    private long[] crossCorrelate(short[] A, short[] B, boolean enable_offset, int maxLags){
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

    private void partialXcorr(int section){
        int aLength = A.length;
        int bLength = B.length;

        int xCorrLength = aLength + bLength-1;
        int start = 0;
        int end = xCorrLength;

        if(maxLags != 0){
            start = aLength - maxLags;
            end = aLength + maxLags;
        }

        if(section == 0){
            end = aLength;
        } else {
            start = aLength;
        }

        for(int i = start; i < end; i++){
            if(i == aLength-1) {
                for(int j = 0; j <= i; j++) {
                    xcorr[i] += this.A[j] * this.B[j];
                }
            } else if(i < aLength){
                for(int j = 0; j <= i; j++) {
                    xcorr[i] += this.A[aLength - i - 1 + j] * this.B[j];
                }
            } else{
                for(int j = 0; j <= xCorrLength - 1 - i; j++) {
                    xcorr[i] += this.A[j] * this.B[bLength - xCorrLength + i + j];
                }
            }
        }

        Log.d("XCORR", "Thread " + Integer.toString(section) + " done");
        Log.d("XCORR", Integer.toString(flag));
    }

}
