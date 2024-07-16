package com.ucpb.tfs.swift;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class SwiftMessageUtilTest {

    @Test
    public void convertCurrencyWithDecimal(){
        assertEquals("PHP1001.54",SwiftMessageUtil.convertToSwiftAmount("PHP","1001.54"));
    }

    @Test
    public void convertCurrencyWithoutDecimal(){
        assertEquals("USD1000,",SwiftMessageUtil.convertToSwiftAmount("USD","1000"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorOnMultipleDecimalPoints(){
        SwiftMessageUtil.convertToSwiftAmount("USD","121.00.121");
    }

    @Test(expected = NullPointerException.class)
    public void errorOnNullAmount(){
        SwiftMessageUtil.convertToSwiftAmount("USD",null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorOnThreeDecimalValues(){
        SwiftMessageUtil.convertToSwiftAmount("USD","121748.091");
    }

    public void convertWithSingleDecimal(){
        assertEquals("PHP231,1",SwiftMessageUtil.convertToSwiftAmount("PHP","231.1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorOnDecimalValuesWithNoIntegerComponent(){
        SwiftMessageUtil.convertToSwiftAmount("PHP",".12");
    }


}
