package com.ucpb.tfs.swift.validation;

import com.ucpb.tfs.swift.message.MT747;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class SwiftValidationIntegrationTest {


    @Test
    public void successfullyDetectInvalidField32BError(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        MT747 message = new MT747();
        message.setField33B("SOMEBODYY");

        Set<ConstraintViolation<MT747>> valdiationErrors = validator.validate(message);
        System.out.println("MESSAGE: " + valdiationErrors.iterator().next().getMessage());
        assertFalse(valdiationErrors.isEmpty());

    }

    @Ignore
    @Test
    public void field31BIsValid(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        MT747 message = new MT747();
        message.setField33B("SOMEBODYY");
        message.setField31E("Has Value");

        Set<ConstraintViolation<MT747>> valdiationErrors = validator.validate(message);
        assertTrue(valdiationErrors.isEmpty());
    }

}
