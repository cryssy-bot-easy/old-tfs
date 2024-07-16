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
public class ConditionalRegexValidatorTest {

    private ConditionalRegexValidator conditionalRegexValidator = new ConditionalRegexValidator();

    private ConditionalRegex conditionalRegex;

    @Before
    public void setup(){
        conditionalRegex = mock(ConditionalRegex.class);
        when(conditionalRegex.condition()).thenReturn("#this.contains('BANANA')");
        when(conditionalRegex.ifTtrue()).thenReturn("(BANANA)(\\d{1})");
        when(conditionalRegex.ifFalse()).thenReturn("[a-zA-Z\\s]{6}");
        conditionalRegexValidator.initialize(conditionalRegex);
    }

    @Test
    public void passWhenValidatedAgainstTrueCondition(){
        assertTrue(conditionalRegexValidator.isValid("BANANA1",mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void failWhenValidatedAgainstTrueCondition(){
        assertFalse(conditionalRegexValidator.isValid("BANANA234",mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void passWhenValidatedAgainstFalseCondition(){
        assertTrue(conditionalRegexValidator.isValid("ABCDEF",mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void failWhenValidatedAgainstFalseCondition(){
        assertFalse(conditionalRegexValidator.isValid("ABCDEFASASASADFAS",mock(ConstraintValidatorContext.class)));
    }

}
