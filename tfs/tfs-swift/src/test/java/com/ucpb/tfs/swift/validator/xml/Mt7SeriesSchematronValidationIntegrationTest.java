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
public class Mt7SeriesSchematronValidationIntegrationTest {

    private static final String SWIFT_SCHEMATRON_LOCATION = "/swift/schematron/compiled/swift-master.xsl";
    private XmlFormatter xmlFormatter = new XmlFormatter(SWIFT_SCHEMATRON_LOCATION);

    @Test
    public void validMt700(){
    	String validationResult = validate("/swift/messages/mt700/valid-mt700.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validMt707(){
    	String validationResult = validate("/swift/messages/mt707/valid-mt707.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validMt730(){
    	String validationResult = validate("/swift/messages/mt730/valid-mt730.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validMt740(){
    	String validationResult = validate("/swift/messages/mt740/valid-mt740.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validMt747(){
    	String validationResult = validate("/swift/messages/mt747/valid-mt747.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validMt750(){
    	String validationResult = validate("/swift/messages/mt750/valid-mt750.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validMt752(){
    	String validationResult = validate("/swift/messages/mt752/valid-mt752.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validMt760(){
    	String validationResult = validate("/swift/messages/mt760/valid-mt760.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField39APresent700(){
    	String validationResult = validate("/swift/messages/mt700/field39A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField39BPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field39B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField42AField42CPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field42A-field42C-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField42MPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field42M-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField42PPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field42P-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField44CPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field44C-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField44DPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field44D-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void invalidField42APresent700(){
    	String validationResult = validate("/swift/messages/mt700/field42A-present.xml");
    	assertTrue(validationResult.contains("When used, fields 42C and 42a must both be present (Error C90)|"));
    }

    @Test
    public void invalidField42AField42CField42MPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field42A-42C-42M-present.xml");
    	assertTrue(validationResult.contains("Either fields 42C and 42a together, or field 42M alone,"+
					" or field 42P alone may be present. No other combination of these fields is allowed (Error C90)|"));
    }

    @Test
    public void invalidField42MField42PPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field42M-field42P-present.xml");
    	assertTrue(validationResult.contains("Either fields 42C and 42a together, or field 42M alone,"+
    			" or field 42P alone may be present. No other combination of these fields is allowed (Error C90)|"));
    }

    @Test
    public void invalidField42CPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field42C-present.xml");
    	assertTrue(validationResult.contains("When used, fields 42C and 42a must both be present (Error C90)|"));
    }
    
    @Test
    public void invalidField39AField39BPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field39A-field39B-present.xml");
    	assertTrue(validationResult.contains("Either field 39A or 39B, but not both, may be present (Error D05)|"));
    }

    @Test
    public void invalidField44CField44DPresent700(){
    	String validationResult = validate("/swift/messages/mt700/field44C-field44D-present.xml");
    	assertTrue(validationResult.contains("Either field 44C or 44D, but not both, may be present (Error D06)|"));
    }
    
    @Test
    public void validField33BField34BPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field33B-34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    
    @Test
    public void validField32BField34BPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field32B-34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField32BField33BField34BPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field32B-33B-34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField23Field52APresent707(){
    	String validationResult = validate("/swift/messages/mt707/field23-52A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField23Field52DPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field23-52D-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField52APresent707(){
    	String validationResult = validate("/swift/messages/mt707/field52A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField39APresent707(){
    	String validationResult = validate("/swift/messages/mt707/field39A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField39BPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field39B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField44CPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field44C-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField44DPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field44D-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField32BField33BField34BEqual707(){
    	String validationResult = validate("/swift/messages/mt707/field32B-33B-34B-equal.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void invalidField23Present707(){
    	String validationResult = validate("/swift/messages/mt707/field23-present.xml");
    	assertTrue(validationResult.contains("If field 23 is present, field 52a must also be present (Error C16)|"));
    }
    
    @Test
    public void invalidField34BPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field34B-present.xml");
    	assertTrue(validationResult.contains("If field 34B is present, either field 32B or 33B must also be present (Error C12)"));    }
    
    @Test
    public void invalidField33BPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field33B-present.xml");
    	assertTrue(validationResult.contains("If either field 32B or 33B is present, field 34B must also be present (Error C12)|"));    }
    
    @Test
    public void invalidField32BPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field32B-present.xml");
    	assertTrue(validationResult.contains("If either field 32B or 33B is present, field 34B must also be present (Error C12)|"));    }

    @Test
    public void invalidField39AField39BPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field39A-39B-present.xml");
    	assertTrue(validationResult.contains("Either field 39A or 39B, but not both, may be present (Error D05)|"));
    }

    @Test
    public void invalidField44CField44DPresent707(){
    	String validationResult = validate("/swift/messages/mt707/field44C-44D-present.xml");
    	assertTrue(validationResult.contains("Either field 44C or 44D, but not both, may be present (Error D06)|"));
    }

    @Test
    public void invalidField32BField33BField34BUnequal707(){
    	String validationResult = validate("/swift/messages/mt707/field32B-33B-34B-unequal.xml");
    	assertTrue(validationResult.contains("The currency code in the amount fields 32B, 33B, and 34B must be the same. (Error C02)|"));
    }

    @Test
    public void invalidDoesNotContainRequiredField707(){
    	String validationResult = validate("/swift/messages/mt707/invalid-mt707.xml");
    	assertTrue(validationResult.contains("At least one of the fields 31E, 32B, 33B, 34B, 39A, 39B, 39C,"+
				" 44A, 44E, 44F, 44B, 44C, 44D, 79 or 72 must be present (Error C30)|"));
    }
        
    @Test
    public void validField25Present730(){
    	String validationResult = validate("/swift/messages/mt730/field25-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField32DPresent730(){
    	String validationResult = validate("/swift/messages/mt730/field32D-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField57APresent730(){
    	String validationResult = validate("/swift/messages/mt730/field57A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void invalidField25Field57APresent730(){
    	String validationResult = validate("/swift/messages/mt730/field25-57A-present.xml");
    	assertTrue(validationResult.contains("Either field 25 or 57a, but not both, may be present (Error C77)|"));
    }

    @Test
    public void invalidField32DField57APresent730(){
    	String validationResult = validate("/swift/messages/mt730/field32D-57A-present.xml");
    	assertTrue(validationResult.contains("If field 32D is present, field 57a must not be present (Error C78)|"));
    }
    
    @Test
    public void validField39APresent740(){
    	String validationResult = validate("/swift/messages/mt740/field39A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField39BPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field39B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField42AField42CPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42A-field42C-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField42CField42DPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42C-field42D-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField42MPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42M-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField42PPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42P-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField58APresent740(){
    	String validationResult = validate("/swift/messages/mt740/field58A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField59Present740(){
    	String validationResult = validate("/swift/messages/mt740/field59-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void invalidField42APresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42A-present.xml");
    	assertTrue(validationResult.contains("When used, fields 42C and 42a must both be present (Error C90)|"));
    }

    @Test
    public void invalidField42CPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42C-present.xml");
    	assertTrue(validationResult.contains("When used, fields 42C and 42a must both be present (Error C90)|"));
    }

    @Test
    public void invalidField42DPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42D-present.xml");
    	assertTrue(validationResult.contains("When used, fields 42C and 42a must both be present (Error C90)|"));
    }
    
    @Test
    public void invalidField42AField42CField42MPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42A-42C-42M-present.xml");
    	assertTrue(validationResult.contains("Either fields 42C and 42a together, or field 42M alone,"+
					" or field 42P alone may be present. No other combination of these fields is allowed (Error C90)|"));
    }

    @Test
    public void invalidField42MField42PPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field42M-field42P-present.xml");
    	assertTrue(validationResult.contains("Either fields 42C and 42a together, or field 42M alone,"+
    			" or field 42P alone may be present. No other combination of these fields is allowed (Error C90)|"));
    }

    
    @Test
    public void invalidField39AField39BPresent740(){
    	String validationResult = validate("/swift/messages/mt740/field39A-field39B-present.xml");
    	assertTrue(validationResult.contains("Either field 39A or 39B, but not both, may be present (Error D05)|"));
    }

    @Test
    public void invalidField58AField59Present740(){
    	String validationResult = validate("/swift/messages/mt740/field58A-field59-present.xml");
    	assertTrue(validationResult.contains("Either field 58a or 59, but not both, may be present (Error D84)|"));
    }
    
    @Test
    public void validField33BField34BPresent747(){
    	String validationResult = validate("/swift/messages/mt747/field33B-34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    
    @Test
    public void validField32BField34BPresent747(){
    	String validationResult = validate("/swift/messages/mt747/field32B-34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField32BField33BField34BPresent747(){
    	String validationResult = validate("/swift/messages/mt747/field32B-33B-34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField39APresent747(){
    	String validationResult = validate("/swift/messages/mt747/field39A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField39BPresent747(){
    	String validationResult = validate("/swift/messages/mt747/field39B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    

    @Test
    public void validField32BField33BField34BEqual747(){
    	String validationResult = validate("/swift/messages/mt747/field32B-33B-34B-equal.xml");
    	assertTrue(validationResult.isEmpty());
    }
        
    @Test
    public void invalidField34BPresent747(){
    	String validationResult = validate("/swift/messages/mt747/field34B-present.xml");
    	assertTrue(validationResult.contains("If field 34B is present, either field 32B or 33B must also be present (Error C12)|"));
    }
    
    @Test
    public void invalidField33BPresent747(){
    	String validationResult = validate("/swift/messages/mt747/field33B-present.xml");
    	assertTrue(validationResult.contains("If either field 32B or 33B is present, field 34B must also be present (Error C12)|"));
    }
    
    @Test
    public void invalidField32BPresent747(){
    	String validationResult = validate("/swift/messages/mt747/field32B-present.xml");
    	assertTrue(validationResult.contains("If either field 32B or 33B is present, field 34B must also be present (Error C12)|"));
    }

    @Test
    public void invalidField39AField39BPresent747(){
    	String validationResult = validate("/swift/messages/mt747/field39A-39B-present.xml");
    	assertTrue(validationResult.contains("Either field 39A or 39B, but not both, may be present (Error D05)|"));
    }

    @Test
    public void invalidField32BField33BField34BUnequal747(){
    	String validationResult = validate("/swift/messages/mt747/field32B-33B-34B-unequal.xml");
    	assertTrue(validationResult.contains("The currency code in the amount fields 32B, 33B, and 34B must be the same (Error C02)|"));
    }

    @Test
    public void invalidDoesNotContainRequiredField747(){
    	String validationResult = validate("/swift/messages/mt747/invalid-mt747.xml");
    	assertTrue(validationResult.contains("At least one of the fields 31E, 32B, 33B, 34B, 39A, 39B,39C, 72 or 77A must be present (Error C15)|"));
    }
    
    @Test
    public void validField32BField34BEqual750(){
    	String validationResult = validate("/swift/messages/mt750/field32B-34B-equal.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField32BPresent750(){
    	String validationResult = validate("/swift/messages/mt750/field32B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField33BField71BField34BPresent750(){
    	String validationResult = validate("/swift/messages/mt750/field33B-71B-34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField71BField73Field34BPresent750(){
    	String validationResult = validate("/swift/messages/mt750/field71B-73-34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField34BPresent750(){
    	String validationResult = validate("/swift/messages/mt750/field34B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void invalidField71BPresent750(){
    	String validationResult = validate("/swift/messages/mt750/field71B-present.xml");
    	assertTrue(validationResult.contains("If field 33B and/or field 71B and/or field 73 is/are present, field 34B must also be present (Error C13)|"));
    }

    @Test
    public void invalidField73Present750(){
    	String validationResult = validate("/swift/messages/mt750/field73-present.xml");
    	assertTrue(validationResult.contains("If field 33B and/or field 71B and/or field 73 is/are present, field 34B must also be present (Error C13)|"));
    }
    
    @Test
    public void invalidField33BPresent750(){
    	String validationResult = validate("/swift/messages/mt750/field33B-present.xml");
    	assertTrue(validationResult.contains("If field 33B and/or field 71B and/or field 73 is/are present, field 34B must also be present (Error C13)|"));
    }
    
    @Test
    public void invalidField32BField34BUnequal750(){
    	String validationResult = validate("/swift/messages/mt750/field32B-34B-unequal.xml");
    	assertTrue(validationResult.contains("The currency code in the amount fields 32B, and 34B must be the same (Error C02)"));
    }

    @Test
    public void validField32BField33AEqual752(){
    	String validationResult = validate("/swift/messages/mt752/field32B-33A-equal.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField32BField33APresent752(){
    	String validationResult = validate("/swift/messages/mt752/field32B-33A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField32BPresent752(){
    	String validationResult = validate("/swift/messages/mt752/field32B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField33APresent752(){
    	String validationResult = validate("/swift/messages/mt752/field33A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField71BPresent752(){
    	String validationResult = validate("/swift/messages/mt752/field71B-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField32BField71BField33APresent752(){
    	String validationResult = validate("/swift/messages/mt752/field32B-71B-33A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void invalidField32BField33AUnequal752(){
    	String validationResult = validate("/swift/messages/mt752/field32B-33A-unequal.xml");
    	assertTrue(validationResult.contains("The currency code in the amount fields 32B and 33a must be the same (Error C02)|"));
    }

    @Test
    public void invalidField32BField71BPresent752(){
    	String validationResult = validate("/swift/messages/mt752/field32B-71B-present.xml");
    	assertTrue(validationResult.contains("The currency code in the amount fields 32B and 33a must be the same (Error C02)|"));
    }
    
//    If field 73 is present, field 33a must also be present (Error code(s): C17).
//    C2 The currency code in the amount fields 32A and 33a must be the same (Error code(s): C02).

    @Test
    public void validField73Field33APresent734(){
    	String validationResult = validate("/swift/messages/mt734/field73-field33A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void validField33APresent734(){
    	String validationResult = validate("/swift/messages/mt734/field33A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void invalidField73Present734(){
    	String validationResult = validate("/swift/messages/mt734/field73-present.xml");
    	assertTrue(validationResult.contains("If field 73 is present, field 33a must also be present (Error code(s): C17)|"));
    }


    @Test
    public void validField33AField32ASameCurrency734(){
    	String validationResult = validate("/swift/messages/mt734/field33A-field32A-same-currency.xml");
    	assertTrue(validationResult.isEmpty());
    }

    @Test
    public void invalidField33AField32ADiffCurrency734(){
    	String validationResult = validate("/swift/messages/mt734/field33A-field32A-diff-currency.xml");
    	assertTrue(validationResult.contains("The currency code in the amount fields 32A and 33a must be the same (Error code(s): C02)|"));
    }
    
    private String validate(String sourceXml){
        String validationResult = xmlFormatter.formatFile(sourceXml);
        return validationResult;
    }
}