package com.ucpb.tfs.util;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
//@Ignore
public class XmlFormatterIntegrationTest {

    private XmlFormatter xmlFormatter = new XmlFormatter("/swift/formatter/swift-format.xsl");

    private XmlValidator xmlValidator = new XmlValidator("/swift/schemas/mt7series.xsd");

    @Test
    public void transformMt700() throws IOException {
        String result = xmlFormatter.formatFile("/swift/messages/raw-mt700.xml");
        System.out.println(result);

        //all tags need to be removed
        assertFalse(result.contains("<tagName>27</tagName>"));
        assertFalse(result.contains("<tagName>40A</tagName>"));
        assertFalse(result.contains("<tagName>20</tagName>"));
        assertFalse(result.contains("<tagName>23</tagName>"));
        assertFalse(result.contains("<tagName>31C</tagName>"));
        assertFalse(result.contains("<tagName>40E</tagName>"));
        assertFalse(result.contains("<tagName>31D</tagName>"));
        assertFalse(result.contains("<tagName>50</tagName>"));
        assertFalse(result.contains("<tagName>59</tagName>"));
        assertFalse(result.contains("<tagName>32B</tagName>"));

        //and replaced with a fieldXX tag
        assertTrue(result.contains("field27"));
        assertTrue(result.contains("field40"));
        assertTrue(result.contains("field20"));
        assertTrue(result.contains("field23"));
        assertTrue(result.contains("field40E"));
        assertTrue(result.contains("field31D"));
        assertTrue(result.contains("field50"));
        assertTrue(result.contains("field59"));
        assertTrue(result.contains("field32B"));


    }

}
