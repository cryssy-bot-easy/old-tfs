package com.ucpb.tfs.swift.validator.xml;

import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.writer.JaxbXmlSwiftMessageWriter;
import com.ucpb.tfs.swift.validator.SwiftValidator;
import com.ucpb.tfs.swift.validator.ValidationError;
import com.ucpb.tfs.swift.validator.xml.parser.ErrorMessageParser;
import com.ucpb.tfs.swift.validator.xml.parser.ErrorMessageParserFactory;
import com.ucpb.tfs.util.XmlFormatter;
import com.ucpb.tfs.util.XmlValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class SwiftXmlValidator implements SwiftValidator {

    private JaxbXmlSwiftMessageWriter xmlWriter = new JaxbXmlSwiftMessageWriter();
    private XmlValidator xmlValidator;
    private XmlFormatter formatter;

    public SwiftXmlValidator(String sourceSchema,String transformationSchema){
        this.xmlValidator = new XmlValidator(sourceSchema);
        this.formatter = new XmlFormatter(transformationSchema);
    }

    @Override
    public List<ValidationError> validate(RawSwiftMessage message) {
        List<String> xmlErrors = xmlValidator.validate(formatter.formatXmlString(xmlWriter.write(message)));
        List<ValidationError> errors = new ArrayList<ValidationError>();
        for(String error : xmlErrors){
            ErrorMessageParser parser = ErrorMessageParserFactory.getInstance(error);
            errors.add(parser.parse(error));
        }
        return errors;
    }
}
