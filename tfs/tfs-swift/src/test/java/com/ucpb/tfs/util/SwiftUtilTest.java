package com.ucpb.tfs.util;

import org.junit.Test;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 */
public class SwiftUtilTest {

	private final static String EMPTY= "";
	
	@Test
	public void formatSwiftLocationWithPartyIdentifierAndLocation() {
		String result = SwiftUtil.formatSwiftLocation("IDENTIFIER", "location");
		assertEquals("/IDENTIFIER\r\nlocation", result);
	}

	@Test
	public void formatCrLfSpacedLocation() {
		String result = SwiftUtil
				.formatSwiftLocation("7989456123", "Redstar Presents",
						"B 5-8 Plaza Mont Kiara\r\n\r\nMont Kiara 50480\r\nKuala Lumpur, Malaysia");
		assertEquals("/7989456123\r\n" + "Redstar Presents\r\n"
				+ "B 5-8 Plaza Mont Kiara\r\n" + "Mont Kiara 50480\r\n"
				+ "Kuala Lumpur, Malaysia", result);
	}

	@Test
	public void removeStrayCrLfCharacters() {
		String result = SwiftUtil
				.formatToLimit(35, "TRADE GALORE",
						"SUITE 206,\\r\\nTHE EXCELSIOR,\\r\\n161 ROXAS BLVD., PARANAQUE M.M.");
		assertEquals("TRADE GALORE\r\n" + "SUITE 206, THE EXCELSIOR, 161 \r\n"
				+ "ROXAS BLVD., PARANAQUE M.M.", result);
	}

	@Test
	public void returnEmptyStringForNullClearingCodeAndNullIdentifierAndNullCode() {
		assertEquals(EMPTY, SwiftUtil.formatPartyIdentifier(null, null, null));
	}

	@Test
	public void returnEmptyStringForEmptyClearingCodeAndEmptyIdentifierAndEmptyCode() {
		assertEquals(EMPTY, SwiftUtil.formatPartyIdentifier(EMPTY,EMPTY,EMPTY));
	}

	@Test
	public void appendSlashesForNonEmptyClearingCodeAndEmptyCode() {
		assertEquals("//AT", SwiftUtil.formatPartyIdentifier("AT", EMPTY, EMPTY));
	}

	@Test
	public void formatCompletePartyIdentifier() {
		assertEquals("//ATONE\r\nTWO",
				SwiftUtil.formatPartyIdentifier("AT", "ONE", "TWO"));
	}

	@Test
	public void doNotAppendNewLineIfClearingCodeAndIdentifierIsNotPresent() {
		assertEquals("TWO", SwiftUtil.formatPartyIdentifier(null, null, "TWO"));
	}

	@Test
	public void successfullyFormatCompletePartyIdentifierWithAddress() {
		assertEquals("//ATONE\r\n" + "NAME AND ADDRESS ADDRESS",
				SwiftUtil.formatPartyIdentifierAddress("AT", "ONE",
						"NAME AND ADDRESS ADDRESS"));
	}

	@Test
	public void doNotAppendNewLineIfClearingCodeAndIdentifierIsEmpty() {
		assertEquals("NAME AND ADDRESS ADDRESS",
				SwiftUtil.formatPartyIdentifierAddress(EMPTY, EMPTY,
						"NAME AND ADDRESS ADDRESS"));
	}

	@Test
	public void returnEmptyStringForAllWhitespaceInput() {
		assertEquals(EMPTY, SwiftUtil.formatToLimit(35, "       ", "   ",
				"      ", "           "));
	}

	@Test
	public void formatSwiftLocationWithNullPartyIdentifier() {
		assertEquals("location",
				SwiftUtil.formatSwiftLocation(null, "location"));
	}

	@Test
	public void successfullyFormatInstructionCodeWithNullAdditionalInformation() {
		assertEquals("CODE", SwiftUtil.formatInstructionCode("CODE", EMPTY));
	}

	@Test
	public void successfullyFormatInstructionCodeWithPresentAdditionalInformation() {
		assertEquals("CODE/ADDITIONAL INFORMATION",
				SwiftUtil.formatInstructionCode("CODE",
						"ADDITIONAL INFORMATION"));

	}

	@Test
    public void successfullyFormatField77C(){
    	String detailsOfGuarantee="909-02-929-13-00001-9<BR/><BR/>PLEASE RELAY TO BENEFICIARY " +
    			"WITHOUT<BR/>ADDING YOUR CONFIRMATION<BR/>.<BR/>LETTER OF CREDIT NO. " +
    			"909-02-929-13-00001-9<BR/>.<BR/>JULY 9, 2013<BR/>.<BR/>GENTLEMEN:<BR/>.<BR/>WE, " +
    			"UNITED COCONUT PLANTERS BANK<BR/>MAKATI CITY, PHILIPPINES HEREBY ESTABLISH<BR/>OUR " +
    			"IRREVOCABLE<BR/>LETTER OF CREDIT NO. 909-02-929-13-00001-9 IN<BR/>FAVOR OF ME THE<BR/>" +
    			"BENEFICIARY WITH OFFICE ADDRESS AT<BR/>HERE FOR THE ACCOUNT<BR/>OF TWINPACK CONTAINER C," +
    			" THE ACCOUNTEE OF 648 BO. LLANO, CALOOCAN, CITY. FOR AN AMOUNT NOT " +
    			"EXCEEDING<BR/>THE TOTAL SUM OF USD: US DOLLARS: FIVE THOUSAND " +
    			"ONLY<BR/>(USD 5,000.00).<BR/>.<BR/>THIS CREDIT GUARANTEES PAYMENT FOR " +
    			"THE<BR/>WALA LANG.<BR/>.<BR/>THIS CREDIT IS AVAILABLE FOR PAYMENT<BR/>AGAINST " +
    			"PRESENTATION OF THE FOLLOWING<BR/>DOCUMENTS;<BR/>.<BR/>1. BENEFICIARY'S " +
    			"SIGNED DRAFT AT<BR/> SIGHT DRAWN OURSELVES.<BR/>.<BR/>2. BENEFICIARY'S C" +
    			"ERTIFICATE THAT THE<BR/> APPLICANT HAS FAILED TO COMPLY WITH<BR/> THE TERMS AND " +
    			"CONDITIONS OF THE ABOVE<BR/> CONTRACT.<BR/>.<BR/>ALL DRAFTS DRAWN UNDER THIS CREDIT " +
    			"MUST<BR/>BEAR THE CLAUSE DRAWN UNDER UNITED COCONUT<BR/>PLANTERS BANK, MAKATI CITY, " +
    			"PHILIPPINES.<BR/>IRREVOCABLE LETTER OF CREDIT <BR/>NO. 909-02-929-13-00001-9 DATED " +
    			"JULY 5, 2013.<BR/>.<BR/>PAYMENT SHALL BE MADE IN THE CURRENCY<BR/>AS STIPULATED " +
    			"IN THE LC.<BR/>.<BR/>PARTIAL DRAWINGS ALLOWED.<BR/>.<BR/>ALL BANK CHARGES OUTSIDE " +
    			"THE<BR/>PHILIPPINES ARE FOR THE ACCOUNT OF THE<BR/>BENEFICIARY.<BR/>.<BR/>WE HEREBY " +
    			"ENGAGE THAT ALL DRAFTS<BR/>DRAWN UNDER AND IN COMPLIANCE WITH <BR/>THE TERMS ON THIS " +
    			"CREDIT WILL BE DULY<BR/>HONORED BEFORE THE EXPIRATION OF THIS<BR/>STANDBY LETTER OF " +
    			"CREDIT.<BR/>.<BR/>THIS CREDIT WILL EXPIRE ON OCTOBER 1, 2013<BR/>IN ARUBA .<BR/>.<BR/>" +
    			"UNLESS OTHERWISE EXPRESSLY STATED THIS CREDIT IS SUBJECT TO ISPR. LATEST";
    	String result=SwiftUtil.limitInputString(65,detailsOfGuarantee);
    	System.out.println(result);
    	assertNotNull(result);
    }
	
	@Test
	public void formatAndRestrictLocationExceeding35Chars() {
		String result = SwiftUtil.formatSwiftLocation("IDENTIFIER",
				"The quick brown fox jumps over the lazy dog.");
		assertEquals(
				"/IDENTIFIER\r\nThe quick brown fox jumps over the \r\nlazy dog.",
				result);
	}

	@Test
	public void getBigDecimalRemainder() {
		BigDecimal number = new BigDecimal("121.1415");
		BigDecimal remainder = number.remainder(BigDecimal.ONE);
		assertTrue(BigDecimal.ZERO.compareTo(remainder) < 0);

		BigDecimal number2 = new BigDecimal("121313");
		BigDecimal remainder2 = number2.remainder(BigDecimal.ONE);
		assertTrue(BigDecimal.ZERO.compareTo(remainder2) == 0);
	}

	@Test
	public void successfullyFormatRateString() {
		String formattedNumber = SwiftUtil.formatRate("12.14141414");
		assertEquals("12,14141414", formattedNumber);
	}

	@Test
	public void formatTimeIndicationCodeWithNullTimeField() {
		assertEquals("/12345/", SwiftUtil.formatTimeIndication("12345", null));
	}

	@Test
	public void formatTimeIndicationCodeWithNullTimeCode() {
		assertEquals("123131414",
				SwiftUtil.formatTimeIndication(null, "123131414"));
	}

	@Test
	public void formatCompleteTimeIndication() {
		assertEquals("/CODE/TIMEFIELD",
				SwiftUtil.formatTimeIndication("CODE", "TIMEFIELD"));
	}

	@Test
	public void successfullyFormatRateStringWithComa() {
		String formattedNumber = SwiftUtil.formatRate("123,186.2121");
		assertEquals("123186,2121", formattedNumber);
	}

	@Test
	public void successfullyConvertValidDateStringToAnotherFormat()
			throws ParseException {
		String formatted = SwiftUtil.formatDateString("yyMMdd", "MM/dd/yyyy",
				"01/08/2012");
		assertEquals("120108", formatted);
	}

	@Test
	public void failConvertDateStringToAnotherFormat() throws ParseException {
		String formatted = SwiftUtil.formatDateString("yyMMdd", "MM/dd/yyyy",
				EMPTY);
		assertEquals(EMPTY, formatted);
	}

	@Test
	public void successfullyConvertSwiftValidAmountToSwiftFormat() {
		assertEquals("PHP100,56", SwiftUtil.formatAmount("PHP", "100.56"));
		assertEquals("PHP100,14", SwiftUtil.formatAmount("PHP", "100.14141"));
		assertEquals("PHP100,", SwiftUtil.formatAmount("PHP", "100.0000000000"));
		System.out.println(SwiftUtil.formatAmount("PHP","100.464"));
	}

	@Test
	public void successfullyConvertSwiftValidAmountToSwiftFormatForJPYAndCHF(){
		assertEquals("JPY356,", SwiftUtil.formatAmount("JPY", "356.56"));
		assertEquals("CHF671,", SwiftUtil.formatAmount("CHF", "671.92"));
	}

	@Test
	public void getAmountIncreaseReturnsEmptyOnDecreasedAmount() {
		String result = SwiftUtil.getAmountIncrease("PHP", "5", "4");
		assertEquals(EMPTY,result);
	}

	@Test
	public void getAmountIncreaseReturnsDifferenceOnIncreasedAmount() {
		assertEquals("PHP100,",
				SwiftUtil.getAmountIncrease("PHP", "250", "350"));
	}

	@Test
	public void getAmountDecreaseReturnsEmptyOnIncreasedAmount() {
		assertEquals(EMPTY,SwiftUtil.getAmountDecrease("PHP", "250", "3000"));
	}

	@Test
	public void getAmountDecreaseReturnsDifferenceOnDecreasedAmount() {
		assertEquals("PHP250,",
				SwiftUtil.getAmountDecrease("PHP", "500", "250"));
	}

	@Test
	public void removesCommaNumberDivider() {
		assertEquals("PHP100345324,43",
				SwiftUtil.formatAmount("PHP", "100,345,324.43"));
	}

	@Test
	public void successfullyFormatRemittanceInformation() {
		assertEquals("/RAWR/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver(null, "/RAWR/ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("/RAWR/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver("RAWR", "/RAWR///ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("/RAWR/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver(null, "//RAWR///ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("/RAWR/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver(null, "///RAWR/ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("/RAWR/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver(null, "///RAWR///ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("/RAWR/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver("RAWR", "///RAWR///ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("/RAWR/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver(null, "/RAWR///ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("/RAWDDS/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver("RAWDDS", "////RAWR///ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("//ERERERER\r\n//RASSDS\r\n//ASSAAA\r\n//ASDFSDFSD SDSDF DDDDFD DDFDF \r\n//GGHGHDFG FDDD DDFDFDF",SwiftUtil.formatSenderToReceiver(null, "//ERERERER\n//RASSDS\n//ASSAAA\nASDFSDFSD SDSDF DDDDFD DDFDF GGHGHDFG FDDD DDFDFDF"));
		assertEquals("/RAWR/ERERERER\r\n//RASSDS\r\n//ASSAAA",SwiftUtil.formatSenderToReceiver("RAWR", "/RAWR/ERERERER\n//RASSDS\n//ASSAAA"));
		assertEquals("/ASD/KGFSJH",
				SwiftUtil.formatSenderToReceiver("ASD", "KGFSJH"));
		assertEquals("//KGFSJH", SwiftUtil.formatSenderToReceiver(null, "KGFSJH"));
		assertEquals("//KGFSJHKGFSJHKGFSJHKG \r\n//FSJHKGFSJHKGFS JH AAAA", SwiftUtil.formatSenderToReceiver(null, "KGFSJHKGFSJHKGFSJHKG FSJHKGFSJHKGFS JH AAAA"));
		assertEquals(EMPTY, SwiftUtil.formatSenderToReceiver(null, null));
		assertEquals(EMPTY, SwiftUtil.formatSenderToReceiver("RAWR", null));
	}

	@Test
	public void successfullyFormatMultipleLineRemittanceInformation() {
		String result = SwiftUtil.formatRemittanceInfo("ACC",
				"THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG.");
		assertEquals("/ACC/THE QUICK BROWN FOX JUMPS \r\n//OVER THE LAZY DOG.",
				result);
		assertEquals("/RFB/INV. NO. ESM/5933",SwiftUtil.formatRemittanceInfo("RFB","INV. NO. ESM/5933"));
	}

	@Test
	public void returnEmptyStringOnNullInputAmount() {
		assertEquals(EMPTY, SwiftUtil.formatAmount(null, null));
	}

	@Test
	public void successfullyAppendAllValues() {
		String out = SwiftUtil.concatWithNewLine("1212", "33333", "111111");
		assertEquals("1212\r\n" + "33333\r\n" + "111111", out);
	}

	@Test
	public void ignoreNullValues() {
		String out = SwiftUtil.concatWithNewLine("1212", null, "3333");
		assertEquals("1212\r\n" + "3333", out);
	}

	@Test
	public void splitOnWhitespace() {
		String[] result = SwiftUtil.split("this is", 6);
		assertEquals("this", result[0]);
		assertEquals("is", result[1]);
	}

	@Test
	public void splitOnPreviousWhitespace() {
		String[] result = SwiftUtil.split("this is", 7);
		assertEquals("this", result[0]);
		assertEquals("is", result[1]);
	}

	@Test
	public void splitIncludingDelimeter() {
		String delimeter = "(?=\\+)";
		String sourceString = "+this+that+andThat";
		// String[] result = sourceString.split(delimeter);
		String[] result = StringUtils.tokenizeToStringArray(sourceString,
				delimeter);
		// assertEquals(3,result.length);
		for (String row : result) {
			System.out.println("*" + row);
		}

	}

	@Test
	public void formatMultiLineSwiftField() {
//		String field= "+SIGNED COMMERCIAL INVOICE (TRIPLICATE)" +
//				"+PACKING LIST" +
//				"+AIRWAY BILL ADDRESSED TO UNITED COCONUT PLANTERS BANK, MANILA, MARKED FREIGHT PREPAID NOTIFY APPLICANT" +
//				"+AIR INSURANCE POLICY OR CERTIFICATE, WAR RISK INSURANCE POLICY OR CERTIFICATE INCLUDING STRIKES, RIOTS, CIVIL COMMOTION AND MARINE EXRENSION CLAUSES IN DUPLICATE FOR 110PCT OF FULL INVOICE VALUE FROM POINT OF ORIGIN TO WAREHOUSE AT DESTINATION" +
//				"+BENEFICIARY'S CERTIFICATE THAT COPY OF COMMERCIAL INVOICE PACKING LIST AND ONE FULL SET OF NON-NEGOTIABLE SHIPPING DOCUMENTS HAVE BEEN AIRMAILED DIRECTLY TO BUYER." +
//				"+BENEFICIARY'S CERTIFICATE THAT THE FOLLOWING HAVE BEEN SENT IN ADVANCED TO NATRAPHARM, INC. THRU FAX (632-821773830)\n"+
//				"A. COMMERCIAL INVOICE\n"+
//				"B. PACKING LIST\n"+
//				"C. CERTIFICATE OF ANALYSIS\n"+
//				"D. AIRWAY BILL\n"+
//				"E. SHIPPING DETAILS: FLIGHT NUMBER, MASTER AIRWAYBILL (MAWB) NUMBER, HOUSE AIRWAYBILL (HAWB) NUMBER AND DATE OF ARRIVALS.";
//		System.out.println(SwiftUtil.formatMultiLine(field,"+", 65));
		
		String field = "+OCEAN BILL OF LADING MUST BE DATED WITHIN THE  VALIDITY PERIOD OF THIS CREDIT+ALL DOCUMENTS MUST INDICATE COMMODITY CLASSIFICATION CODE AND LC NUMBER AS INDICATED ABOVE.+BL       DATED PRIOR TO ISSUANCE OF THIS CREDIT NOT ALLOWED.+A FEE OF USD  20.00 (OR EQUIVALENT) WILL BE CHARGED TO THE BENEFICIARY IF DOCUMENTS CONTAINING DISCREPANCIES ARE PRESENTED FOR PAYMENT/REIMBURSEMENT UNDER THIS LC.  THIS FEE WILL BE CHARGED FOR EACH SET OF DISCREPANT DOCUMENTS PRESENTED WHICH REQUIRE OUR OBTAINING ACCEPTANCE FROM OUR CUSTOMER.+NEGOTIATING BANK MUST PRESENT ALL DOCS AND REIMB CLAIMS  UNDER THIS CREDIT TO THE CONF. BANK.  BANK OF CHINA WHICH HOLDS SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS.";

		String result = SwiftUtil.formatMultiLine(field, 65);

		assertEquals(
				"+OCEAN BILL OF LADING MUST BE DATED WITHIN THE  VALIDITY PERIOD \r\n"
						+ "OF THIS CREDIT\r\n"
						+ "+ALL DOCUMENTS MUST INDICATE COMMODITY CLASSIFICATION CODE AND \r\n"
						+ "LC NUMBER AS INDICATED ABOVE.\r\n"
						+ "+BL       DATED PRIOR TO ISSUANCE OF THIS CREDIT NOT ALLOWED.\r\n"
						+ "+A FEE OF USD  20.00 (OR EQUIVALENT) WILL BE CHARGED TO THE \r\n"
						+ "BENEFICIARY IF DOCUMENTS CONTAINING DISCREPANCIES ARE PRESENTED \r\n"
						+ "FOR PAYMENT/REIMBURSEMENT UNDER THIS LC.  THIS FEE WILL BE \r\n"
						+ "CHARGED FOR EACH SET OF DISCREPANT DOCUMENTS PRESENTED WHICH \r\n"
						+ "REQUIRE OUR OBTAINING ACCEPTANCE FROM OUR CUSTOMER.\r\n"
						+ "+NEGOTIATING BANK MUST PRESENT ALL DOCS AND REIMB CLAIMS  UNDER \r\n"
						+ "THIS CREDIT TO THE CONF. BANK.  BANK OF CHINA WHICH HOLDS \r\n"
						+ "SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS.",
				result);

	}

	@Test
	public void splitUsingDelimeter() {
		String field = "+OCEAN BILL OF LADING MUST BE DATED WITHIN THE  VALIDITY PERIOD OF THIS CREDIT+ALL DOCUMENTS MUST INDICATE COMMODITY CLASSIFICATION CODE AND LC NUMBER AS INDICATED ABOVE.+BL       DATED PRIOR TO ISSUANCE OF THIS CREDIT NOT ALLOWED.+A FEE OF USD  20.00 (OR EQUIVALENT) WILL BE CHARGED TO THE BENEFICIARY IF DOCUMENTS CONTAINING DISCREPANCIES ARE PRESENTED FOR PAYMENT/REIMBURSEMENT UNDER THIS LC.  THIS FEE WILL BE CHARGED FOR EACH SET OF DISCREPANT DOCUMENTS PRESENTED WHICH REQUIRE OUR OBTAINING ACCEPTANCE FROM OUR CUSTOMER.+NEGOTIATING BANK MUST PRESENT ALL DOCS AND REIMB CLAIMS  UNDER THIS CREDIT TO THE CONF. BANK.  BANK OF CHINA WHICH HOLDS SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS.";
		String[] split = field.split("(?=\\+)");
		assertEquals(6, split.length);
		for (String message : split) {
			System.out.println(message);
		}
	}

	@Test
	public void restrictFieldLength() {
		String sourceString = "+OCEAN BILL OF LADING MUST BE DATED WITHIN THE  VALIDITY PERIOD OF THIS CREDIT";
		List<String> result = SwiftUtil.restrict(sourceString, 65);
		for (String row : result) {
			System.out.println(row);
		}
		assertEquals(
				"+OCEAN BILL OF LADING MUST BE DATED WITHIN THE  VALIDITY PERIOD ",
				result.get(0));
		assertEquals("OF THIS CREDIT", result.get(1));
	}

	@Test
	public void formatToLimitMultipleLines() {
		String sourceString1 = "+OCEAN BILL OF LADING MUST BE DATED WITHIN THE  VALIDITY PERIOD OF THIS CREDIT";
		String sourceString2 = "SOURCE STRING TWO";
		String sourceString3 = "123456789012345678901234567890 123456789012345678901234567890 1234567890";
		String result = SwiftUtil.formatToLimit(65, sourceString1,
				sourceString2, sourceString3);
		assertEquals(
				"+OCEAN BILL OF LADING MUST BE DATED WITHIN THE VALIDITY PERIOD "
						+ "\r\n"
						+ "OF THIS CREDIT"
						+ "\r\n"
						+ "SOURCE STRING TWO"
						+ "\r\n"
						+ "123456789012345678901234567890 123456789012345678901234567890 "
						+ "\r\n" + "1234567890", result);
	}

	@Test
	public void successfullyFormatDetailsOfAmount() {
		String result = SwiftUtil
				.formatDetailsOfAmount(
						"INTEREST",
						"PHP",
						"1000.00",
						"THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG. THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG. THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG.");
		assertEquals("/INTEREST/PHP1000,\r\n"
				+ "//THE QUICK BROWN FOX JUMPS OVER \r\n"
				+ "//THE LAZY DOG. THE QUICK BROWN \r\n"
				+ "//FOX JUMPS OVER THE LAZY DOG. THE \r\n"
				+ "//QUICK BROWN FOX JUMPS OVER THE \r\n" + "//LAZY DOG.",
				result);
	}

	@Test
	public void formatDetailsOfAmountOutputsEmptyStringForNullInputs() {
		assertEquals(EMPTY,
				SwiftUtil.formatDetailsOfAmount(null, null, null, null));
	}

	@Test
	public void formatDetailsOfAmountOutputsEmptyStringForEmptyStringInputs() {
		assertEquals(EMPTY, SwiftUtil.formatDetailsOfAmount(EMPTY, EMPTY, EMPTY, EMPTY));
	}

	@Test
	public void surroundSwiftCodeWithForwardSlash() {
		assertEquals("/CODE/",
				SwiftUtil.formatDetailsOfAmount("CODE", null, null, null));
	}

	@Test
	public void successfullySplitToLimit() {
		String result = SwiftUtil.formatToLimit(35, "TRADE GALORE",
				"SUITE 206, THE EXCELSIOR, 161 ROXAS BLVD., PARANAQUE M.M.");
		assertEquals("TRADE GALORE\r\n" + "SUITE 206, THE EXCELSIOR, 161 \r\n"
				+ "ROXAS BLVD., PARANAQUE M.M.", result);
	}

	@Test
	public void successfullyRestrictInputLength() {
		List<String> result = SwiftUtil
				.restrict(
						"SUITE 206, THE EXCELSIOR, 161 ROXAS BLVD., PARANAQUE M.M.",
						35);
		assertEquals("SUITE 206, THE EXCELSIOR, 161 ", result.get(0));
		assertEquals("ROXAS BLVD., PARANAQUE M.M.", result.get(1));
	}

	@Test
	public void successfullyFormatDetailsOfAmount2() {
		// "detailsOfAmountDescription", "some description",
		// "detailsOfAmountCurrency","PHP",
		// "detailsOfAmountTextField","12,000,000.43",
		// "detailsOfAmountTextArea","narrative goes here" ,
		// "detailsOfCharges","SOME DETAILS"

		String result = SwiftUtil.formatDetailsOfAmount("some description",
				"PHP", "12,000,000.43", "narrative goes here");
		assertEquals("/some description/PHP12000000,43\r\n"
				+ "//narrative goes here", result);
	}

	@Test
	public void formatToLimit() {
		String result = SwiftUtil.formatToLimit(35,
				"1st Avenue Manalac Compd\n" + "Sta. Maria Ind'l. Estate\r\n"
						+ "Bagumbayan Taguig M.M.");
		System.out.println(result);
	}

	@Test
	public void successfullyLimitInputContainingCommas() {
		String result = SwiftUtil
				.formatToLimit(35,
						"DYAN LANG SA AMIN_-0912309130(){}.,:/?,,,,,,46,,,98dfg,,,,,,,,");
		assertEquals("DYAN LANG SA \r\n"
				+ "AMIN_-0912309130(){}.,:/?,,,,,,46,,,98dfg,,,,,,,,", result);
	}

	@Test
	public void successfullyLimitSingleInputString() {
		String result = SwiftUtil
				.limitInputString(35,
						"DYAN LANG SA AMIN_-0912309130(){}.,:/?,,,,,,46,,,98dfg,,,,,,,,");
		assertEquals("DYAN LANG SA \r\n"
				+ "AMIN_-0912309130(){}.,:/?,,,,,,46,,,98dfg,,,,,,,,", result);
	}

	@Test
	public void successfullyReplaceAllWhitespaces() {
		StringBuilder sb = new StringBuilder(
				"THE QUICK BROWN FOX\r\nJUMPS OVER THE LAZY\r\n");
		SwiftUtil.replaceAllWhitespaces(sb);
		assertEquals("THE QUICK BROWN FOX JUMPS OVER THE LAZY ", sb.toString());
	}

	@Test
	public void failGetAmountTolerance() {
		assertEquals(EMPTY,SwiftUtil.getAmountTolerance("50",EMPTY));
	}
	
	@Test
	public void successfullyFormatSwiftLocationWithEmptyIdentifier(){
		String result=SwiftUtil.formatSwiftLocation(EMPTY, "LEPANTO CONSOLIDATED MINING COMPANY", "21ST FLOOR LEPANTO BLDG"+
		"\n8747 PASEO DE ROXAS\nMAKATI CITY");
		System.out.println(result);
		assertEquals("LEPANTO CONSOLIDATED MINING COMPANY\r\n"+
				"21ST FLOOR LEPANTO BLDG\r\n8747 PASEO DE ROXAS\r\n"+ 
				"MAKATI CITY",result);
	}
	
	@Test
	public void successfullFormatSwiftLocationWithLongWord(){
		String result = SwiftUtil.formatSwiftLocation(EMPTY, "LG IDENTIFIER", "LG GWANGHWAMUN BLDG., 92, SINMUNNO"+
		"5-GA, JONGNO(),.......(),.......(),"+
		".......(),.......(),.......(),....."+
		"..(),.......(),.......(),.......(");
		
		System.out.println(result);
	}

	@Test
	public void failReturnEmptyOnNullInputString(){
		String result=SwiftUtil.limitInputString(35, null);
		assertEquals(EMPTY,result);
	}
}
