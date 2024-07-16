package com.ucpb.tfs.swift.validator.xml;

import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.writer.XmlWriter;
import com.ucpb.tfs.swift.validator.ValidationError;
import com.ucpb.tfs.util.XmlFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class SchematronXmlValidatorTest {

    private SchematronXmlValidator validator;

    private XmlFormatter swiftXmlFormatter;

    private XmlFormatter schematronXmlFormatter;

    private XmlWriter xmlWriter;

    @Before
    public void setup(){
        xmlWriter = mock(XmlWriter.class);
        swiftXmlFormatter = mock(XmlFormatter.class);
        schematronXmlFormatter = mock(XmlFormatter.class);
        validator = new SchematronXmlValidator(swiftXmlFormatter,schematronXmlFormatter,xmlWriter);
    }

    @Test
    public void delegateValidationToTheFormatterAndWriter(){
        when(swiftXmlFormatter.formatXmlString(anyString())).thenReturn("");
        when(schematronXmlFormatter.formatXmlString(anyString())).thenReturn("");
        validator.validate(new RawSwiftMessage());
        verify(xmlWriter).write(any(RawSwiftMessage.class));
        verify(swiftXmlFormatter).formatXmlString(anyString());
        verify(schematronXmlFormatter).formatXmlString(anyString());

    }

    @Test
    public void returnSchematronErrorsAsValidationErrors(){
        when(schematronXmlFormatter.formatXmlString(anyString()))
                .thenReturn("<?xml version=\"1.0\" encoding=\"UTF-8\"?>Field 57A must be present if Field 56A is present|Field 56A must not be present if Field 23B is equal to SPRI|Field 53D must not be used if the value of Field 23B is either SPRI,SSTD or SPAY|");

        List<ValidationError> errors = validator.validate(new RawSwiftMessage());
        printErrors(errors);
        assertEquals(3,errors.size());
        assertEquals("Field 57A must be present if Field 56A is present",errors.get(0).getMessage());
        assertEquals("Field 56A must not be present if Field 23B is equal to SPRI",errors.get(1).getMessage());
        assertEquals("Field 53D must not be used if the value of Field 23B is either SPRI,SSTD or SPAY",errors.get(2).getMessage());
    }

    @Test
    public void returnEmptyListForEmptyValidationResult(){
        when(schematronXmlFormatter.formatXmlString(anyString())).thenReturn("");

        List<ValidationError> errors = validator.validate(new RawSwiftMessage());
        assertTrue(errors.isEmpty());
    }

    private void printErrors(List<ValidationError> errorList){
        for(ValidationError error : errorList){
            System.out.println(error.getMessage());
        }
    }

}
