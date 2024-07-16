package com.ucpb.tfs.swift.validator;

import com.ucpb.tfs.swift.message.RawSwiftMessage;

import java.util.List;

/**
 */
public interface SwiftValidator {

    /**
     *  Validates the input swift message.
     * @param swiftMessage The message to be validated
     * @return a list of ValidationErrors
     */

    public List<ValidationError> validate(RawSwiftMessage swiftMessage);
}
