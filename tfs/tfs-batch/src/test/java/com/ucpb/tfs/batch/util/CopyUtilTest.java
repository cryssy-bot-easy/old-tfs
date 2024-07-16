package com.ucpb.tfs.batch.util;

import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 */
public class CopyUtilTest {


    @Test
    public void successfullyCloneObject() throws CopyFailedException {
        MockObject obj1 = new MockObject();
        obj1.setAmount(new BigDecimal("121"));
        obj1.setFirstValue("first");

        MockObject obj2 = CopyUtil.copy(obj1);
        assertEquals("first",obj2.getFirstValue());
        assertEquals(new BigDecimal("121"),obj2.getAmount());
        assertFalse(obj2.getFirstValue() == obj1.getFirstValue());
        assertFalse(obj2.getAmount() == obj1.getAmount());
    }

    @Test(expected = CopyFailedException.class)
    public void failOnUnserializableObject() throws CopyFailedException {
        CopyUtil.copy(new Unserializable());
    }

}
