package com.ucpb.tfs.swift.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 */
public class JointFieldValidator implements ConstraintValidator<JointField,Object> {

    private String field;

    private String[] jointFields;

    @Override
    public void initialize(JointField jointField) {
        this.field = jointField.field();
        this.jointFields = jointField.jointFields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        Object fieldValue =  getValueFromProperty(value, field);
        if(fieldValue == null){
            boolean isValid = false;
            for(String jointField : jointFields){
                if(getValueFromProperty(value,jointField) == null){
                    isValid = true;
                }
            }
            return isValid;
        }

        return true;
    }

    private Object getValueFromProperty(Object bean, String fieldName){
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        return wrapper.getPropertyValue(fieldName);
    }
}
