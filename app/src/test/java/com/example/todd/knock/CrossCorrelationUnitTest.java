package com.example.todd.knock;

/**
 * Created by Todd on 12/10/16.
 */

import org.junit.Test;

import java.lang.reflect.Array;

import static org.junit.Assert.*;

public class CrossCorrelationUnitTest {
    static CrossCorrelation regular = new CrossCorrelation();
    CrossCorrelation limited = new CrossCorrelation(5);

    static short [] data = {1, 1, 1, 1, 1, 1};
    static ChannelList leftdata = new ChannelList(data, 0);
    static ChannelList rightdata = new ChannelList(data, 1);

    static long [] answer = {1, 2, 3, 2, 1};

}
