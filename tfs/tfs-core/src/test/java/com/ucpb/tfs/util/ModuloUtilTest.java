package com.ucpb.tfs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Ignore;
import org.junit.Test;

import com.ucpb.tfs.utils.ModuloUtil;

public class ModuloUtilTest {

	
	@Test
	public void getTheCorrectWeightedSum(){
		//(2 * 2) + (3 * 3) + (5 * 4) + (6 * 5) + (3 * 6) + (7 * 0) = 81
		assertEquals(81,ModuloUtil.getWeightedSum(36532, 2));
	}
	
	@Test
	public void getTheCorrectWeightedSumOfSevenDigitNumber(){
		// (2 * 2) + (8 * 3) + (7 * 4) + (3 * 5) + (4 * 6) + (6 * 7) + (5 * 2) 
		assertEquals(147,ModuloUtil.getWeightedSum(5643782, 2));
	}
	
	@Test
	public void getTheCorrectCheckDigit(){
		assertEquals(7,ModuloUtil.getCheckDigit(36532));
	}
	
	@Test
	public void getTheCorrectCheckDigitForAccountNumber(){
		//"33-02-3333-12-00002"
		//(2 * 2) + (0 * 3) + (0 * 4) + (0 * 5) + (0 * 6) + (2*7) 
		//(1 * 2) + (3 * 3) + (3 * 4) + (3 * 5) + (3 * 6) + (2 * 7) 
		//(0 * 2) + (3 * 3) + (3 * 4)
		System.out.println((109 % 11));
		System.out.println(11 - (109 % 11));
		assertEquals(1,ModuloUtil.getCheckDigit("33-02-3333-12-00002","-"));
	}
	
	@Test
	public void getTheCorrectCheckDigitForAccountNumber2(){
		//"33-01-3333-12-00001"
		//(1 * 2) + (0 * 3) + (0 * 4) + (0 * 5) + (0 * 6) + (2*7) 
		//(1 * 2) + (3 * 3) + (3 * 4) + (3 * 5) + (3 * 6) + (2 * 7) 
		//(0 * 2) + (3 * 3) + (3 * 4)
		System.out.println((107 % 11));
		System.out.println(11 - (107 % 11));
		assertEquals(0,ModuloUtil.getCheckDigit("33-01-3333-12-00001","-"));
	}
	
	
	@Test
	public void successfullyVerifyCheckDigit(){
		assertTrue(ModuloUtil.isCheckDigitValid(36532, 7));
	}
	
	//TODO: fix logic of check digit valdiation for check digits of value 10
	@Ignore ("TODO: // fix logic of check digit valdiation for check digits of value 10")
	@Test
	public void successfullyVerifyCheckDigit2(){
		//33-01-3333-12-00001-0
		assertTrue(ModuloUtil.isCheckDigitValid(330133331200001l, 0));
	}
	
	@Test
	public void getTheCorrectCheckDigitOfStringInput(){
		assertEquals(7,ModuloUtil.getCheckDigit("036532"));
	}
	
	@Test(expected = NumberFormatException.class)
	public void numberFormatExceptionOnNonNumericInput(){
		ModuloUtil.getCheckDigit("ABC");
	}
	
	@Test
	public void removeSpecifiedSeparatorFromInput(){
		assertEquals(7,ModuloUtil.getCheckDigit("3--6-5---3-2","-"));

	}
	
}
