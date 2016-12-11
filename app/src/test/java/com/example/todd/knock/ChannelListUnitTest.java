package com.example.todd.knock;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChannelListUnitTest {
    private short[] sampleData = {0, 1, 2, 3, 4, 5, 6, 7};

    @Test
    public void leftChannelGet0() throws Exception {
        ChannelList leftdata = new ChannelList(sampleData, 0);
        assertEquals(leftdata.get(0), 0);
    }

    @Test
    public void leftChannelGet1() throws Exception {
        ChannelList leftdata = new ChannelList(sampleData, 0);
        assertEquals(leftdata.get(1), 2);
    }

    @Test
    public void leftChannelGetLast() throws Exception {
        ChannelList leftdata = new ChannelList(sampleData, 0);
        assertEquals(leftdata.get(leftdata.length()-1), 6);
    }

    @Test
    public void leftChannelLength() throws Exception {
        ChannelList leftdata = new ChannelList(sampleData, 0);
        assertEquals(leftdata.length(), 4);
    }

    @Test
    public void rightChannelGet0() throws Exception {
        ChannelList rightdata = new ChannelList(sampleData, 1);
        assertEquals(rightdata.get(0), 1);
    }

    @Test
    public void rightChannelGet1() throws Exception {
        ChannelList rightdata = new ChannelList(sampleData, 1);
        assertEquals(rightdata.get(1), 3);
    }

    @Test
    public void rightChannelGetLast() throws Exception {
        ChannelList rightdata = new ChannelList(sampleData, 1);
        assertEquals(rightdata.get(rightdata.length()-1), 7);
    }

    @Test
    public void rightChannelLength() throws Exception {
        ChannelList rightdata = new ChannelList(sampleData, 1);
        assertEquals(rightdata.length(), 4);
    }
}
