package com.ucpb.tfs.swift.validator.xml;

import com.ucpb.tfs.util.XmlFormatter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class Mt4SeriesSchematronValidationIntegrationTest {

    private static final String SWIFT_SCHEMATRON_LOCATION = "/swift/schematron/compiled/swift-master.xsl";
    private XmlFormatter xmlFormatter = new XmlFormatter(SWIFT_SCHEMATRON_LOCATION);

    /*
     *		MT400
     */
    @Test
    public void validMt400(){
    	String validationResult = validate("/swift/messages/mt400/valid-mt400.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    
    @Test
    public void validfield57AWithField54AAndField53A(){
    	String validationResult = validate("/swift/messages/mt400/field57A-with-field53A-field54A.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField32AEqualsField33A(){
    	String validationResult = validate("/swift/messages/mt400/field32A-equals-field33A.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void invalidfield57AWithoutField54A(){
    	String validationResult = validate("/swift/messages/mt400/field57A-without-field54A.xml");
    	assertTrue(validationResult.contains("Field 57a may only be present when fields 53a and 54a are both present (Error C11)|"));
    }
    
    @Test
    public void invalidfield57AWithoutField53A(){
    	String validationResult = validate("/swift/messages/mt400/field57A-without-field53A.xml");
    	assertTrue(validationResult.contains("Field 57a may only be present when fields 53a and 54a are both present (Error C11)"));
    }

    @Test
    public void invalidfield57DWithoutField53A(){
        String validationResult = validate("/swift/messages/mt400/field57D-without-field53a.xml");
        assertTrue(validationResult.contains("Field 57a may only be present when fields 53a and 54a are both present (Error C11)"));
    }

    @Test
    public void valid57DField53DAndField54B(){
        String validationResult = validate("/swift/messages/mt400/field57D-53D-field54B.xml");
        assertFalse(validationResult.contains("Field 57a may only be present when fields 53a and 54a are both present (Error C11)"));
    }

    @Test
    public void invalidfield32ANotEqualField33A(){
    	String validationResult = validate("/swift/messages/mt400/field32A-not-equal-field33A.xml");
    	assertTrue(validationResult.contains("The currency code for Fields 32a and 33A must be the same (Error C02)|"));
    }

    @Test
    public void sameCurrency32BAnd33A(){
        String validationResult = validate("/swift/messages/mt400/field32B-33A-same-currency.xml");
        assertFalse(validationResult.contains("The currency code for Fields 32a and 33A must be the same (Error C02)|"));
    }

    @Test
    public void differentCurrency32BAnd33A(){
        String validationResult = validate("/swift/messages/mt400/field32B-33A-different-currency.xml");
        assertTrue(validationResult.contains("The currency code for Fields 32a and 33A must be the same (Error C02)|"));
    }

    @Test
    public void sameCurrency32KAnd33A(){
        String validationResult = validate("/swift/messages/mt400/field32K-and-33A-same-currency.xml");
        assertFalse(validationResult.contains("The currency code for Fields 32a and 33A must be the same (Error C02)|"));
    }

    @Test
    public void differentCurrency32KAnd33A(){
        String validationResult = validate("/swift/messages/mt400/field32K-and33A-different-currency.xml");
        assertTrue(validationResult.contains("The currency code for Fields 32a and 33A must be the same (Error C02)|"));
    }


    /*
     * 		MT410/MT412
     */
    @Test
    public void validMt410And412(){
    	String validationResult = validate("/swift/messages/mt410_mt412/valid-mt410-mt412.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField20Less10Times(){
    	String validationResult = validate("/swift/messages/mt410_mt412/field20-less-10times.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField21Less10Times(){
    	String validationResult = validate("/swift/messages/mt410_mt412/field21-less-10times.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField32ALess10Times(){
    	String validationResult = validate("/swift/messages/mt410_mt412/field32A-less-10times.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Ignore("Not Working Properly")
    public void validField32AAllEqual(){
    	String validationResult = validate("/swift/messages/mt410_mt412/field32A-all-equal.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void invalidField20Great10Times(){
    	String validationResult = validate("/swift/messages/mt410_mt412/field20-great-10times.xml");
    	assertTrue(validationResult.contains("Error Code T10:Field 20 may not appear more than ten times.|"));
    }
    
    @Test
    public void invalidField21Great10Times(){
    	String validationResult = validate("/swift/messages/mt410_mt412/field21-great-10times.xml");
    	assertTrue(validationResult.contains("Error Code T10:Field 21 may not appear more than ten times.|"));
    }
    
    @Test
    public void invalidField32AGreat10Times(){
    	String validationResult = validate("/swift/messages/mt410_mt412/field32A-great-10times.xml");
    	assertTrue(validationResult.contains("Error Code T10:Field 32A may not appear more than ten times.|"));
    }

    @Ignore("Not Working Properly")
    public void invalidField32AAllNotEqual(){
    	String validationResult = validate("/swift/messages/mt410_mt412/field32A-all-not-equal.xml");
    	assertTrue(validationResult.contains("Error Code C02:The currency code in the amount field 32a must be the same for all occurrences of this field in the message.|"));
    }
    
    private String validate(String sourceXml){
        String validationResult = xmlFormatter.formatFile(sourceXml);
        System.out.println(validationResult);
        return validationResult;
    }
}