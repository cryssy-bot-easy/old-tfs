package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;

/**
 * The default error message parser to be used when no applicable parser can be found for a
 * given error message. This class simply retrieves the entire (unparsed) error message then places it in
 * the message field of the ValidationError class.
 */
public class DefaultErrorMessageParser extends ErrorMessageParser {


    @Override
    protected ValidationError mapToValidationError(String[] response) {
        return new ValidationError(response[0]);
    }

    @Override
    protected String getFormat() {
        return "(.*)";
    }
}
