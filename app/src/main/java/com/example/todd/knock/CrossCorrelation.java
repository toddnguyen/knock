package com.example.todd.knock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Todd on 11/27/16.
 */

public class CrossCorrelation {
    private int offset;
    private boolean enable_offset;

    public CrossCorrelation(){
        offset = 0;
        enable_offset = false;
    }

    public CrossCorrelation(int dist){
        offset = dist;
        enable_offset = true;
    }

    public long[] crossCorrelate(ChannelList A, ChannelList B){
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
            start = aLength - 200;
            end = aLength + 200;
        }

        for(int i = start; i < end; i++){
            if(i == aLength-1) {
                for(int j = 0; j <= i; j++) {
                    corr[i] += A.get(j) * B.get(j);
                }
            } else if(i < aLength){
                for(int j = 0; j <= i; j++) {
                    corr[i] += A.get(aLength - i - 1 + j) * B.get(j);
                }
            } else{
                for(int j = 0; j <= xCorrLength - 1 - i; j++) {
                    corr[i] += A.get(j) * B.get(bLength - xCorrLength + i + j);
                }
            }
        }

        return corr;
    }

}
