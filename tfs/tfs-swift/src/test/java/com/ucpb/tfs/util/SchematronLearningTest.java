package com.ucpb.tfs.util;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class SchematronLearningTest {

    private XmlFormatter xmlFormatter;

    @Ignore
    @Test
    public void failInvaildXml() throws IOException {
        String xmlSource = FileUtil.getFileAsString("/misc/schematron/add_bad.xml");
//        System.out.println(xmlSource);
        String baseSchematron = FileUtil.getFileAsString("/misc/schematron/add.sch");
//        System.out.println(baseSchematron);
        xmlFormatter = new XmlFormatter("/swift/schematron/iso/xslt1/iso_dsdl_include.xsl");
        String preprocessedSchema = xmlFormatter.formatFile("/misc/schematron/add.sch");
        System.out.println("***************PREPROCESSED**************");
        System.out.println(preprocessedSchema);
        System.out.println("****************END***************");
        assertTrue(StringUtils.hasText(preprocessedSchema));

        xmlFormatter = new XmlFormatter("/swift/schematron/iso/xslt1/iso_schematron_skeleton_for_xslt1.xsl");
        String expanded = xmlFormatter.formatXmlString(preprocessedSchema);
        System.out.println("***************EXPANDED**************");
        System.out.println(expanded);
        System.out.println("****************END***************");
        assertTrue(StringUtils.hasText(expanded));


    }

    @Test
    public void transformAddWithNamespace(){
//        String formattedSchema = xmlFormatter.formatFile("/misc/schematron/add.sch", "/swift/schematron/skeleton1-5.xsl");
        xmlFormatter = new XmlFormatter("/swift/schematron/skeleton1-5.xsl");
        String formattedSchema = xmlFormatter.formatFile("/swift/schematron/source/mt1series.xsl");

        System.out.println(formattedSchema);
    }

    @Test
    public void validateSchematron15WithNamespace(){
        xmlFormatter = new XmlFormatter("/swift/schematron/iso/schema5/add-namespace.xsl");
        String validatedSchema = xmlFormatter.formatFile("/misc/schematron/add_bad_namespace.xml");
        System.out.println(validatedSchema);

    }

    @Test
    public void compileMt1SeriesSchematron(){
//        String formattedSchema = xmlFormatter.formatFile("/misc/schematron/add.sch", "/swift/schematron/skeleton1-5.xsl");
        xmlFormatter = new XmlFormatter("/swift/schematron/skeleton1-5.xsl");
        String formattedSchema = xmlFormatter.formatFile("/swift/schematron/source/mt1series.xsl");

        System.out.println(formattedSchema);
    }

    @Test
    public void validateValidMt103(){
        xmlFormatter = new XmlFormatter("/swift/schematron/compiled/mt1series.xsl");
        String validatedSchema = xmlFormatter.formatFile("/swift/messages/valid-mt103.xml");
        System.out.println(validatedSchema);
        assertFalse(StringUtils.hasText(validatedSchema));
    }




}
