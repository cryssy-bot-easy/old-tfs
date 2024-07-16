package com.ucpb.tfs.interfaces.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class SequenceTest {


    @Test
    public void incrementSequence(){
        Sequence sequence = new Sequence();
        assertEquals(0,sequence.getSequenceNumber());
        sequence.increment();
        assertEquals(1,sequence.getSequenceNumber());
    }

}
