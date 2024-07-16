package com.ucpb.tfs.interfaces.services.exception;

import com.ucpb.tfs.swift.validator.ValidationError;

import java.util.List;

/**
 */
public class ValidationException extends Exception{

    private List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors, String message){
        super(message);
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
