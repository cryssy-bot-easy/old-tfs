package com.ucpb.tfs.swift.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 */
public class ExclusiveOrValidator implements ConstraintValidator<ExclusiveOr,Object> {

    private String[] fields;

    @Override
    public void initialize(ExclusiveOr exclusiveOr) {
        this.fields = exclusiveOr.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = false;
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        for(String field : fields){
            Object fieldValue = wrapper.getPropertyValue(field);
            if(fieldValue != null){
                if(isValid == false){
                    isValid = true;
                }else{
                    return false;
                }
            }
        }
        return isValid;
    }
}