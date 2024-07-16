package com.ucpb.tfs.swift.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

/**
 */
public class OrValidator implements ConstraintValidator<Or,Object> {

    private String[] fields;

    @Override
    public void initialize(Or or) {
        this.fields = or.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        for(String field : fields){
            Object fieldValue = wrapper.getPropertyValue(field);
            if(fieldValue != null){
                return true;
            }
        }

        return false;
    }
}