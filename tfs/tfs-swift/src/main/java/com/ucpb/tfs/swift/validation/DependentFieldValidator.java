package com.ucpb.tfs.swift.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

/**
 */
public class DependentFieldValidator implements ConstraintValidator<DependentField,Object> {

    private String dependentField;

    private String[] targetFields;

    @Override
    public void initialize(DependentField dependentField) {
        this.dependentField = dependentField.dependentField();
        this.targetFields = dependentField.targetFields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        Object field =  wrapper.getPropertyValue(dependentField);
        if(field != null){
            return true;
        }
        for(String targetField : targetFields){
            if(wrapper.getPropertyValue(targetField) != null){
                return false;
            }
        }

        return false;
    }
}
