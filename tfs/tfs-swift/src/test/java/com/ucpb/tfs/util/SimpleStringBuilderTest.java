package com.ucpb.tfs.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 */
public class SimpleStringBuilderTest {

    private SimpleStringBuilder simpleStringBuilder;

    @Before
    public void setup(){
        simpleStringBuilder = new SimpleStringBuilder();
    }

    @Test
    public void successfullyAppendAndRestrictInput(){
        simpleStringBuilder.appendAndRestrict(35,"TRADE GALORE","SUITE 206, THE EXCELSIOR, 161 ROXAS BLVD., PARANAQUE M.M.");
        assertEquals("TRADE GALORE\r\n" +
                "SUITE 206, THE EXCELSIOR, 161 \r\n" +
                "ROXAS BLVD., PARANAQUE M.M.",simpleStringBuilder.toString());
    }

    @Test
    public void dontAppendNullArray(){
        simpleStringBuilder.appendAndRestrict(35);
        assertEquals("",simpleStringBuilder.toString());
    }

    @Test
    public void noTextForEmptyStringBuilder(){
        assertFalse(simpleStringBuilder.hasText());
    }

    @Test
    public void noTextForStringBuilderWithEmptyStringContent(){
        simpleStringBuilder.append("");
        assertFalse(simpleStringBuilder.hasText());
    }

    @Test
    public void hasTextPassesForWhitespace(){
        simpleStringBuilder.append(" ");
        assertTrue(simpleStringBuilder.hasText());
    }

    @Test
    public void hasTextPassesForNewLineContent(){
        simpleStringBuilder.append('\n');
        assertTrue(simpleStringBuilder.hasText());
    }

    @Test
    public void dontAppendNullString(){
        simpleStringBuilder.append(null);
        assertEquals("",simpleStringBuilder.toString());
    }

    @Test
    public void appendCharacter(){
        simpleStringBuilder.append('c');
        assertEquals("c",simpleStringBuilder.toString());
    }
    
    @Test
    public void successfullyCallAppendAndRestrictWithoutInitialize(){
    	String[] s = {"FLKJJIOUOIJ1234546456","+SIGNED COMMERCIAL INVOICE (TRIPLICATE)" +
				"+PACKING LIST" +
				"+AIRWAY BILL ADDRESSED TO UNITED COCONUT PLANTERS BANK, MANILA, MARKED FREIGHT PREPAID NOTIFY APPLICANT" +
				"+AIR INSURANCE POLICY OR CERTIFICATE, WAR RISK INSURANCE POLICY OR CERTIFICATE INCLUDING STRIKES, RIOTS, CIVIL COMMOTION AND MARINE EXRENSION CLAUSES IN DUPLICATE FOR 110PCT OF FULL INVOICE VALUE FROM POINT OF ORIGIN TO WAREHOUSE AT DESTINATION" +
				"+BENEFICIARY'S CERTIFICATE THAT COPY OF COMMERCIAL INVOICE PACKING LIST AND ONE FULL SET OF NON-NEGOTIABLE SHIPPING DOCUMENTS HAVE BEEN AIRMAILED DIRECTLY TO BUYER." +
				"+BENEFICIARY'S CERTIFICATE THAT THE FOLLOWING HAVE BEEN SENT IN ADVANCED TO NATRAPHARM, INC. THRU FAX (632-821773830)\n"+
				"A. COMMERCIAL INVOICE\n"+
				"B. PACKING LIST\n"+
				"C. CERTIFICATE OF ANALYSIS\n"+
				"D. AIRWAY BILL\n"+
				"E. SHIPPING DETAILS: FLIGHT NUMBER, MASTER AIRWAYBILL (MAWB) NUMBER, HOUSE AIRWAYBILL (HAWB) NUMBER AND DATE OF ARRIVALS."};
    	SimpleStringBuilder result =simpleStringBuilder.appendAndRestrictWithoutInitialize(65, s); 
    	System.out.println(result.toString());
    	assertEquals("FLKJJIOUOIJ1234546456\r\n"+
    			"+SIGNED COMMERCIAL INVOICE (TRIPLICATE)+PACKING LIST+AIRWAY BILL \r\n"+ 
    			"ADDRESSED TO UNITED COCONUT PLANTERS BANK, MANILA, MARKED \r\n"+ 
    			"FREIGHT PREPAID NOTIFY APPLICANT+AIR INSURANCE POLICY OR \r\n"+ 
    			"CERTIFICATE, WAR RISK INSURANCE POLICY OR CERTIFICATE INCLUDING \r\n"+ 
    			"STRIKES, RIOTS, CIVIL COMMOTION AND MARINE EXRENSION CLAUSES IN \r\n"+ 
    			"DUPLICATE FOR 110PCT OF FULL INVOICE VALUE FROM POINT OF ORIGIN \r\n"+ 
    			"TO WAREHOUSE AT DESTINATION+BENEFICIARY'S CERTIFICATE THAT COPY \r\n"+ 
    			"OF COMMERCIAL INVOICE PACKING LIST AND ONE FULL SET OF \r\n"+ 
    			"NON-NEGOTIABLE SHIPPING DOCUMENTS HAVE BEEN AIRMAILED DIRECTLY \r\n"+ 
    			"TO BUYER.+BENEFICIARY'S CERTIFICATE THAT THE FOLLOWING HAVE BEEN \r\n"+
    			"SENT IN ADVANCED TO NATRAPHARM, INC. THRU FAX (632-821773830)\r\n"+
    			"A. COMMERCIAL INVOICE\r\n"+
    			"B. PACKING LIST\r\n"+
    			"C. CERTIFICATE OF ANALYSIS\r\n"+
    			"D. AIRWAY BILL\r\n"+
    			"E. SHIPPING DETAILS: FLIGHT NUMBER, MASTER AIRWAYBILL (MAWB) \r\n"+ 
    			"NUMBER, HOUSE AIRWAYBILL (HAWB) NUMBER AND DATE OF ARRIVALS.",result.toString());
    }
    
//    @Test
//    public void successfullySplitEachNewLine(){
//    	System.out.println("METRO BANK MANILA\n\nMETRO MANILA PHILIPPINES");
//    	System.out.println("==============================================================");
//    	List<String> temp=simpleStringBuilder.splitEachNewLine("METRO BANK MANILA\n\nMETRO MANILA PHILIPPINES");
//    	for(String s:temp){
//    		System.out.println("String: "+s);    		
//    	}
//    	assertNotNull(temp);
//    }
}
