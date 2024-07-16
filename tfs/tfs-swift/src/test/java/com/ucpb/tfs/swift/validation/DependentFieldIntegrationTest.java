package com.ucpb.tfs.swift.validation;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.Set;

import static junit.framework.Assert.assertFalse;

/**
 */
public class DependentFieldIntegrationTest {

    @Test
    public void successfullyValidateDependentField(){
        Trinity trinity = new Trinity();
        trinity.setHolySpirit("HS");

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        javax.validation.Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Trinity>> errors =  validator.validate(trinity);
        assertFalse(errors.isEmpty());

        Iterator<ConstraintViolation<Trinity>> iterator = errors.iterator();
        while(iterator.hasNext()){
            ConstraintViolation violation = iterator.next();
            System.out.println("*************" + violation.getMessage());
        }
    }
}
