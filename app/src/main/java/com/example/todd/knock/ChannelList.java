package com.example.todd.knock;

/**
 * Used for indexing to prevent the need for splitting the two arrays with loops
 */

public class ChannelList {
    private short[] array;
    private int channel;
    private boolean reverse;
    public int length;

    public ChannelList(short[] data, int channel_number){
        array = data;
        channel = channel_number;
        reverse = false;
        length = data.length/2;
    }

    public ChannelList(short[] data, int channel_number, boolean reversed){
        array = data;
        channel = channel_number;
        reverse = reversed;
        length = data.length/2;
    }

    public short get(int index){
        if(reverse){
            return array[array.length - 2 + channel - (2 * index)];
        }else {
            return array[2 * index + channel];
        }
    }
}
