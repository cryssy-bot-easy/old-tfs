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
public class Mt1SeriesSchematronValidationIntegrationTest {

    private static final String SWIFT_SCHEMATRON_LOCATION = "/swift/schematron/compiled/swift-master.xsl";
    private XmlFormatter xmlFormatter = new XmlFormatter(SWIFT_SCHEMATRON_LOCATION);


    @Test
    public void passValidMt103Xml(){
        String validationResult = validate("/swift/messages/valid-mt103.xml");
        assertTrue(StringUtils.isEmpty(validationResult));
    }

    @Test
    public void field53BAccountNumberIsRequired(){
        String validationResult = validate("/swift/messages/mt103/53B-party-identifier-required.xml");
        assertTrue(validationResult.contains("Party Identifier for Field 53B is required if Field 23B contains either SPRI, SSTD or SPAY (Error E04)"));
    }

    @Test
    public void field53aAnd54aRequired(){
        String validationResult = validate("/swift/messages/mt103/53a-54a-required.xml");
        assertTrue(validationResult.contains("Fields 53a and 54a must be present if Field 55a is present (Error E06)"));
    }

    @Test
    public void passValidationForField55AAnd54BAnd54D(){
        String validationResult = validate("/swift/messages/mt103/53B-54A-valid-for-55A.xml");
        assertFalse(validationResult.contains("Fields 53a and 54a must be present if Field 55a is present (Error E06)"));
    }

    @Test
    public void requireValidationForField55B(){
        String validationResult = validate("/swift/messages/mt103/require-validation-for-55B.xml");
        assertTrue(validationResult.contains("Fields 53a and 54a must be present if Field 55a is present (Error E06)"));
    }

    @Test
    public void field53BAccountNumberIsRequiredValidationPasses(){
        String validationResult = validate("/swift/messages/mt103/53B-contains-party-identifier.xml");
        assertFalse(validationResult.contains("Party Identifier for Field 53B is required if Field 23B contains either SPRI, SSTD or SPAY (Error E04)"));
    }


    @Test
    public void field56AMustNotBePresentIfField23BIsEqualToSpri(){
        String validationResult = validate("/swift/messages/mt103/56A-must-not-be-present.xml");
        assertTrue(validationResult.contains("Field 56A must not be present if Field 23B is equal to SPRI"));
    }

    @Test
    public void field57AMustBePresentIfField56AIsPresent(){
        String validationResult = validate("/swift/messages/mt103/field57A-must-be-present.xml");
        assertTrue(validationResult.contains("Field 57a must be present if Field 56a is present (Error C81)|"));
    }

    @Test
    public void accountSubfieldOf59AIsRequiredIf23BIsEqualToSpri(){
        String validationResult = validate("/swift/messages/mt103/account-field-is-required.xml");
        assertTrue(validationResult.contains("Account field of Field 59a is Mandatory if Field 23B is equal to SPRI, SSTD or SPAY (Error E10)|"));
    }

    @Test
    public void accountSubfieldOf59AIsNotAllowedIf23eIsEqualToChqb(){
        String validationResult = validate("/swift/messages/mt103/account-field-is-not-allowed.xml");
        assertTrue(validationResult.contains("Account Field in Field 59A is not allowed if Field 23E is equal to CHQB (Error E18)"));
    }

    @Test
    public void field36IsNotAllowedIfTheCurrenciesOfField32AAnd33BAreEqual(){
        String validationResult = validate("/swift/messages/mt103/exchange-rate-needed.xml");
        assertTrue(validationResult.contains("Field 36 is required only if the currencies of Field 32A and Field 33B are different|"));
    }

    @Test
    public void field36ShouldBePresentIfTheCurrenciesOfField32AAnd33BAreNotEqual(){
        String validationResult = validate("/swift/messages/mt103/exchange-rate-not-needed.xml");
        assertTrue(validationResult.contains("Field 36 is required only if the currencies of Field 32A and Field 33B are different|"));
    }

    @Test
    public void field36IsRequiredForUnequalCurrencies(){
        String validationResult = validate("/swift/messages/mt103/exchange-rate-validation-passed.xml");
        assertFalse(validationResult.contains("Field 36 is required only if the currencies of Field 32A and Field 33B are different|"));
    }

    @Test
    public void field59IsRequired(){
    	String validationResult = validate("/swift/messages/mt103/field59-required.xml");
    	assertTrue(validationResult.contains("Account field of Field 59a is Mandatory if Field 23B is equal to SPRI, SSTD or SPAY (Error E10)|"));
    }
    
    private String validate(String sourceXml){
        String validationResult = xmlFormatter.formatFile(sourceXml);
        System.out.println(validationResult);
        return validationResult;
    }



}
