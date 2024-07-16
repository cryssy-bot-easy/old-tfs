package com.ucpb.tfs.swift.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 */
public class MultiRegexValidator implements ConstraintValidator<MultiRegex,Object> {

    private String[] regularExpressions;


    @Override
    public void initialize(MultiRegex multiRegex) {
        this.regularExpressions = multiRegex.regularExpressions();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = false;
        if(value instanceof  String){
            String string = (String)value;
            for(String regex : regularExpressions){
                isValid = isValid || string.matches(regex);
            }
        }

        return isValid;
    }

}