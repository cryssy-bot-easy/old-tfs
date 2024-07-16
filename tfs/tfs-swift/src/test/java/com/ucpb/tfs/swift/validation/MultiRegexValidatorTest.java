package com.ucpb.tfs.swift.validation;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintValidatorContext;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class MultiRegexValidatorTest {

    private MultiRegexValidator validator;

    private MultiRegex multiRegex;

    @Before
    public void setup(){
        validator = new MultiRegexValidator();
        multiRegex = mock(MultiRegex.class);
        when(multiRegex.regularExpressions()).thenReturn(new String[]{"\\d","\\w"});
        validator.initialize(multiRegex);
    }

    @Test
    public void successfullyPassOneValidation(){
        assertTrue(validator.isValid("1", mock(ConstraintValidatorContext.class)));
        assertTrue(validator.isValid("a", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void failAllValdations(){
        assertFalse(validator.isValid("%",mock(ConstraintValidatorContext.class)));
    }



}
