package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class InvalidContentParser extends ErrorMessageParser {

    @Override
    protected ValidationError mapToValidationError(String[] response) {
        String field = extractField(response[1]);
        String invalidElement = removeNamespaceTag(response[0]);
		ValidationError validationError=new ValidationError(field,null,
				"Invalid content found around "+ invalidElement + ", " +
				field + " is expected.");
		return validationError;
    }

    @Override
    protected String getFormat() {
    	//cvc-complex-type.2.4.a: Invalid content was found starting with element 'ns3:message_priority'. One of '{"http://www.ucpb.com.ph/tfs/schemas/swift-message":receiver_address}' is expected.
        return "cvc-complex-type.2.4.a: Invalid content was found starting with element '(.+)'. One of '(.+)' is expected.";
    }

    private String extractField(String field){
        Matcher matcher = Pattern.compile("\\{\".+\":(.+)\\}").matcher(field);
        if(matcher.find()){
            return matcher.group(1);
        }
        return field;
    }

    private String removeNamespaceTag(String field){
        if(field.contains(":")){
            return field.split(":")[1];
        }
        return field;
    }
}