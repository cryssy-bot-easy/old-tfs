package com.ucpb.tfs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ucpb.tfs.utils.LuhnUtil;

public class LuhnUtilTest {

	
	@Test
	public void testIntegerConversion(){
		int digit = Character.getNumericValue("7992739871".charAt(9));
		assertEquals(1,digit);
		assertEquals(7,Character.getNumericValue("7992739871".charAt(8)));
		assertEquals(8,Character.getNumericValue("7992739871".charAt(7)));
		assertEquals(9,Character.getNumericValue("7992739871".charAt(6)));
		assertEquals(3,Character.getNumericValue("7992739871".charAt(5)));

	}
	
	@Test
	public void correctlyComputeTheCheckSum(){
		assertEquals(67,LuhnUtil.getCheckSum("7992739871"));
	}
	
	@Test
	public void correctlyComputeTheCheckDigit(){
		assertEquals(3,LuhnUtil.getCheckDigit("7992739871"));
	}

    @Test
    public void correctlyComputeTheCheckDigit2(){
        assertEquals(8,LuhnUtil.getCheckDigit("1412516136131231"));
    }

    @Test
    public void correctlyComputeTheCheckDigit3(){
        assertEquals(6,LuhnUtil.getCheckDigit("496728470"));
    }

    @Test
    public void correctlyComputeTheCheckDigit4(){
        assertEquals(5,LuhnUtil.getCheckDigit("0941840851"));
    }

    @Test
    public void correctlyComputeTheCheckDigit5(){
        assertEquals(8,LuhnUtil.getCheckDigit("015"));
    }

    @Test
    public void correctlyComputeTheCheckDigit6(){
        assertEquals(3,LuhnUtil.getCheckDigit("17829"));
    }

    @Test
    public void correctlyComputeTheCheckDigitFromFormattedInput(){
        assertEquals(3,LuhnUtil.getCheckDigit("79-9273-987-1","-"));
    }
	
	@Test
	public void successfullyVerifyNumberWithCheckDigit(){
		assertTrue(LuhnUtil.isNumberValid("79927398713"));
	}
	
	@Test
	public void failInvalidNumber(){
		assertFalse(LuhnUtil.isNumberValid("718371831615128"));
		assertFalse(LuhnUtil.isNumberValid("79927398710"));

	}
}
