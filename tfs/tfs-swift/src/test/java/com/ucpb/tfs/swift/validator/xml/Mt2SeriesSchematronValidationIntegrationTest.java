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
public class Mt2SeriesSchematronValidationIntegrationTest {

    private static final String SWIFT_SCHEMATRON_LOCATION = "/swift/schematron/compiled/swift-master.xsl";
    private XmlFormatter xmlFormatter = new XmlFormatter(SWIFT_SCHEMATRON_LOCATION);

    
    @Test
    public void validMt202(){
    	String validationResult = validate("/swift/messages/mt202/valid-mt202.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void validField57AAndField56AIsPresent(){
    	String validationResult = validate("/swift/messages/mt202/field56A-field57A-present.xml");
    	assertTrue(validationResult.isEmpty());
    }
    
    @Test
    public void invalidField57AMustExistIfField56AExist(){
    	String validationResult = validate("/swift/messages/mt202/field56A-and-field57A-must-be-present.xml");
        assertTrue(validationResult.contains("Field 57a must be present if Field 56a is present (Error C81)|"));
    }

    @Test
    public void invalidField56AIsNotRequired(){
    	String validationResult = validate("/swift/messages/mt202/field57A-and-field56A-must-be-present.xml");
    	assertFalse(validationResult.contains("Field 57a must be present if Field 56a is present (Error C81)"));
    }

    @Test
    public void noErrorForField56AAnd57B(){
        String validationResult = validate("/swift/messages/mt202/field56a-and-field57B.xml");
        assertFalse(validationResult.contains("Field 57a must be present if Field 56a is present (Error C81)"));
    }

    @Test
    public void noErrorForField56AAnd57D(){
        String validationResult = validate("/swift/messages/mt202/field56a-and-field57D.xml");
        assertFalse(validationResult.contains("Field 57a must be present if Field 56a is present (Error C81)"));
    }
    
    private String validate(String sourceXml){
        String validationResult = xmlFormatter.formatFile(sourceXml);
        System.out.println(validationResult);
        return validationResult;
    }
}