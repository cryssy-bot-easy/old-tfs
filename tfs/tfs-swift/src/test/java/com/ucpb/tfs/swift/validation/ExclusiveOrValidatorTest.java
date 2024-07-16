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
public class ExclusiveOrValidatorTest {

    private ExclusiveOrValidator exclusiveOrValidator = new ExclusiveOrValidator();

    @Before
    public void setup(){
        ExclusiveOr xor = mock(ExclusiveOr.class);
        when(xor.fields()).thenReturn(new String[] {"mother","father","child"});
        exclusiveOrValidator.initialize(xor);
    }

    @Test
    public void successfulIfOneFieldIsPresent(){
        Family family = new Family();
        family.setChild("child");
        assertTrue(exclusiveOrValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void failIfTwoArePresent(){
        Family family = new Family();
        family.setChild("child");
        family.setFather("father");
        assertFalse(exclusiveOrValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void failIfNoneArePresent(){
        Family family = new Family();
        assertFalse(exclusiveOrValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void failIfAllArePresent(){
        Family family = new Family();
        family.setChild("child");
        family.setFather("father");
        family.setMother("mother");
        assertFalse(exclusiveOrValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

}
