package com.ucpb.tfs.interfaces.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class PackedDecimalConverterTest {


    @Test
    public void successfullyShiftRight(){
        assertEquals(4,(0x4F >> 4));
        assertEquals(5,(0x5F >> 4));
    }

    @Test
    public void successfullyConvertToInt(){
        assertEquals(21544d, PackedDecimalConverter.convertToDecimal(0x21544F));
    }

    @Test
    public void successfullyConvertNegativeNumber(){
        assertEquals(-146134d, PackedDecimalConverter.convertToDecimal(0x146134D));
    }





}
