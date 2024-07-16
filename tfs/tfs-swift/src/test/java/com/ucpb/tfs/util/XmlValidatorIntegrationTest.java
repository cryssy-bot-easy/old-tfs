package com.ucpb.tfs.util;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class XmlValidatorIntegrationTest {


    private XmlValidator xmlValidator;


    @Before
    public void setup(){
        xmlValidator = new XmlValidator("/validator/shapes.xsd");
    }


    @Test
    public void produceNoErrorsOnValidXml() throws IOException {
        List<String> errors = xmlValidator.validate(FileUtil.getFileAsString("/validator/valid-square.xml"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidXmlProducesErrors() throws IOException {
        List<String> errors = xmlValidator.validate(FileUtil.getFileAsString("/validator/invalid-square.xml"));
        assertFalse(errors.isEmpty());
        printErrors(errors);
        assertEquals(1,errors.size());
        assertEquals("cvc-complex-type.2.4.d: Invalid content was found starting with element 'shapes:side'. No child element is expected at this point.",errors.get(0));
    }


    private void printErrors(List<String> errors){
        for(String error : errors){
            System.out.println(error);
        }
    }


}
