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
public class OrValidatorTest {

    private OrValidator orValidator = new OrValidator();

    @Before
    public void setup(){
        Or or = mock(Or.class);
        when(or.fields()).thenReturn(new String[]{"child","mother","father"});
        orValidator.initialize(or);
    }

    @Test
    public void successfulIfOneIsPresent(){
        Family family = new Family();
        family.setChild("child");
        assertTrue(orValidator.isValid(family,mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void successfulIfAllArePresent(){
        Family family = new Family();
        family.setChild("child");
        family.setMother("mother");
        family.setFather("father");
        assertTrue(orValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void failIfNoneArePresent(){
        Family family = new Family();
        assertFalse(orValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }
}
