package com.ucpb.tfs.swift.message;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 */
public class MT750Test {

    private MT750 mt750;

    @Before
    public void setup(){
        mt750 = new MT750();
        mt750.setField20("USD123,00");
        mt750.setField21("USD123,00");
        mt750.setField32B("USD123,00");
        mt750.setField33B("USD123,00");
        mt750.setField34B("USD123,00");
        mt750.setField57A("USD123,00");
        mt750.setField71B("USD123,00");
        mt750.setField72("USD123,00");
        mt750.setField73("USD123,00");
        mt750.setField77J("USD123,00");
    }


    @Test
    public void successfullyValidateValidMt750(){

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<MT750>> errors =  validator.validate(mt750);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void validationErrorWhenCurrencyCodesAreDifferent(){
        mt750.setField34B("PHP123,00");
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<MT750>> errors =  validator.validate(mt750);
        assertFalse(errors.isEmpty());
        assertEquals("currency codes of fields 32B and 34B must be equal",errors.iterator().next().getMessage());

    }



}
