package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;

/**
 */
public class InvalidPatternParser extends ErrorMessageParser {


    @Override
    protected ValidationError mapToValidationError(String[] response) {
        ValidationError validationError = new ValidationError(response[2],response[0], response[2] + " is invalid because it does not match the given pattern: " +
                response[1]);
        return validationError;
    }

    @Override
    protected String getFormat() {

        return "cvc-pattern-valid: Value '(.*)' is not facet-valid with respect to pattern '(.*)' for type '(.+)'.";
    }
}