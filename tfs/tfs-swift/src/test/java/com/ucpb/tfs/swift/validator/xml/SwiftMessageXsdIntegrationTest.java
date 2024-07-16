package com.ucpb.tfs.swift.validator.xml;

import com.ucpb.tfs.util.FileUtil;
import com.ucpb.tfs.util.XmlValidator;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 */
public class SwiftMessageXsdIntegrationTest {

    private XmlValidator xmlValidator = new XmlValidator("/swift/schemas/swift-message.xsd");

    @Test
    public void passValidSwiftXml() throws IOException {

        List<String> errors = validate(FileUtil.getFileAsString("/swift/messages/valid-generic-swift-message.xml"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidSwiftBasicHeader() throws IOException{
        List<String> errors = validate(FileUtil.getFileAsString("/swift/messages/valid-swift-basic-header.xml"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidSwiftApplicationHeader() throws IOException{
        List<String> errors = validate(FileUtil.getFileAsString("/swift/messages/valid-swift-application-header.xml"));
        assertTrue(errors.isEmpty());
    }

    private List<String> validate(String xmlString){
        List<String> errors = xmlValidator.validate(xmlString);
        System.out.println(xmlString);
        for(String error : errors){
            System.out.println(error);
        }
        return errors;
    }
}
