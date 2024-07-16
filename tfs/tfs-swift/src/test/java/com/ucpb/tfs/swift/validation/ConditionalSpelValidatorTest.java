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
public class ConditionalSpelValidatorTest {

    private ConditionalSpelValidator validator;

    private ConditionalSpel conditionalSpel;

    @Before
    public void setup(){
        conditionalSpel = mock(ConditionalSpel.class);
        validator = new ConditionalSpelValidator();
        when(conditionalSpel.condition()).thenReturn("#this.father == 'God'");
        when(conditionalSpel.spelValidation()).thenReturn("#this.child == 'Jesus'");
        validator.initialize(conditionalSpel);
    }


    @Test
    public void ignoreValidationIfConditionFails(){
        Family family = new Family();
        family.setFather("Not God");
        family.setChild("Not jesus");

        assertTrue(validator.isValid(family, mock(ConstraintValidatorContext.class)));

    }

    @Test
    public void validateIfConditionIsTrue(){
        Family family = new Family();
        family.setFather("God");
        family.setChild("Not jesus");

        assertFalse(validator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void passRequiredValidation(){
        Family family = new Family();
        family.setFather("God");
        family.setChild("Jesus");

        assertTrue(validator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void spelSubstringTest(){
        when(conditionalSpel.condition()).thenReturn("#this?.father?.length() > 100");
        when(conditionalSpel.spelValidation()).thenReturn("#this.child == 'Jesus'");
        validator.initialize(conditionalSpel);

        Family family = new Family();
        family.setFather("");
        validator.isValid(family,mock(ConstraintValidatorContext.class));
    }
}
