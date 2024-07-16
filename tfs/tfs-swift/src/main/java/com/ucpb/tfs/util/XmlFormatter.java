package com.ucpb.tfs.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Applies the specified styleSheet to the source xml
 */
public class XmlFormatter {

    private static final String DEFAULT_TRANSFORMER = "net.sf.saxon.TransformerFactoryImpl";
    private Transformer transformer;

    public XmlFormatter(String styleSheetUrl){
        TransformerFactory transformerFactory = TransformerFactory.newInstance(DEFAULT_TRANSFORMER, null);
        transformerFactory.setURIResolver(new ClasspathResourceURIResolver());
        Source xsltSource = new StreamSource(FileUtil.getSystemId(styleSheetUrl));
        try {
            System.setProperty("javax.xml.transform.TransformerFactory",
                    "net.sf.saxon.TransformerFactoryImpl");
            transformer = transformerFactory.newTransformer(xsltSource);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    public String formatXmlString(String sourceXmlString){
        InputStream sourceXmlStringStream = null;
        try {
            sourceXmlStringStream = IOUtils.toInputStream(sourceXmlString);
            //TODO: determine if an inputStream.close is needed if you're going to use it as
            //TODO: input to the constructor of stream source.
            Source xmlSource = new StreamSource(IOUtils.toInputStream(sourceXmlString));
            return format(xmlSource);
        } finally {
            IOUtils.closeQuietly(sourceXmlStringStream);
        }
    }

    public String formatFile(String sourceXml) {
        try {
            Source xmlSource = new StreamSource(FileUtil.getFile(sourceXml));
            return format(xmlSource);
        }catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String format(Source sourceXml){

        Writer writer = new StringWriter();

        try {
            transformer.transform(sourceXml, new StreamResult(writer));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    private class ClasspathResourceURIResolver implements URIResolver {

        @Override
        public Source resolve(String href, String base) throws TransformerException {
            int lastFolderIndex = StringUtils.lastIndexOf(base,'/');
            if (lastFolderIndex > 0) {
                int hrefLastFolderIndex = StringUtils.lastIndexOf(href,'/');
                if(hrefLastFolderIndex > 0){
                    return new StreamSource(StringUtils.left(base,lastFolderIndex + 1) + href.substring(hrefLastFolderIndex + 1));
                }
                return new StreamSource(StringUtils.left(base, lastFolderIndex + 1) + href);
            }
            return new StreamSource(href);
        }
    }

}
