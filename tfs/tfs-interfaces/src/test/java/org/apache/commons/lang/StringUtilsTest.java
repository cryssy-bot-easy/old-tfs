package org.apache.commons.lang;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Learning tests for the apache StringUtils class
 */
public class StringUtilsTest {

    @Test
    public void getRightmostOfStringOfExceedingLength(){
        assertEquals("rue",StringUtils.right("true",3));
    }

    @Test
    public void getRightmostOfStringOfLessLength(){
        assertEquals("true",StringUtils.right("true",100));
    }

    @Test
    public void returnNullWhenInvokingRightOnNullInput(){
        assertNull(StringUtils.right(null,100));
    }

}
