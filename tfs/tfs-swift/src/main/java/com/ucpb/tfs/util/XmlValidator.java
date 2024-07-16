package com.ucpb.tfs.util;

import org.apache.commons.io.IOUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class XmlValidator {

    private Schema schema;

    public XmlValidator(String sourceSchema){
        this(sourceSchema,XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    public XmlValidator(String sourceSchema,String schemaUrl){
        //Schema Factory is not thread safe
        synchronized (SchemaFactory.class){
            SchemaFactory factory = SchemaFactory.newInstance(schemaUrl);
            try {
                this.schema = factory.newSchema(new StreamSource(FileUtil.getSystemId(sourceSchema)));
            } catch (SAXException e) {
                throw new RuntimeException("Failed to initialize the xml validator",e);
            }
        }
    }

    public List<String> validate(String source) {
        InputStream sourceStream = IOUtils.toInputStream(source);
        Source messageSource = new StreamSource(sourceStream);
        SilentErrorHandler errorHandler = new SilentErrorHandler();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(errorHandler);
        try {
            validator.validate(messageSource);
        } catch (SAXException e) {
            throw new RuntimeException("An error occurred while attempting to validate the input xml",e);
        } catch (IOException e) {
            throw new RuntimeException("Source schema does not exist",e);
        }finally{
            IOUtils.closeQuietly(sourceStream);
        }
        return errorHandler.getErrors();
    }

    private class SilentErrorHandler implements ErrorHandler {

        private List<String> errors = new ArrayList<String>();

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            errors.add(exception.getMessage());
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            errors.add(exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            errors.add(exception.getMessage());
        }

        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        public void clear(){
            errors.clear();
        }
    }
}
