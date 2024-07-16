package com.ucpb.tfs.swift.validator.xml;

import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.writer.JaxbXmlSwiftMessageWriter;
import com.ucpb.tfs.swift.message.writer.XmlWriter;
import com.ucpb.tfs.swift.validator.SwiftValidator;
import com.ucpb.tfs.swift.validator.ValidationError;
import com.ucpb.tfs.util.XmlFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class SchematronXmlValidator implements SwiftValidator {

    private XmlWriter xmlWriter;
    private XmlFormatter formatter;
    private XmlFormatter schematronFormatter;
    private static final Pattern ERROR_MESSAGE_PATTERN = Pattern.compile("([a-zA-Z0-9\\s,\\(\\)]+?)\\|");

    public SchematronXmlValidator(XmlFormatter swiftXmlFormatter,XmlFormatter schematronFormatter,XmlWriter xmlWriter){
        this.xmlWriter = xmlWriter;
        this.formatter = swiftXmlFormatter;
        this.schematronFormatter = schematronFormatter;
    }

    public SchematronXmlValidator(String templateLocation,String swiftFormatXslLocation){
        this.xmlWriter = new JaxbXmlSwiftMessageWriter();
        this.formatter = new XmlFormatter(swiftFormatXslLocation);
        this.schematronFormatter = new XmlFormatter(templateLocation);
    }

    @Override
    public List<ValidationError> validate(RawSwiftMessage message) {
    	System.out.println("start schematron validation: ");
        String unformattedResult = formatter.formatXmlString(xmlWriter.write(message));
        String validationResult = schematronFormatter.formatXmlString(unformattedResult);
        
        List<ValidationError> errors = new ArrayList<ValidationError>();
        Matcher matcher = ERROR_MESSAGE_PATTERN.matcher(validationResult);
        while(matcher.find()){
            errors.add(new ValidationError(matcher.group(1)));
        }
        return errors;
    }
}
