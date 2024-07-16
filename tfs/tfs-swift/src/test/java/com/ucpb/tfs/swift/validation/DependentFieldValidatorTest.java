package com.ucpb.tfs.swift.validation;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintValidatorContext;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the depedent field validator. The class to be tested is the Family class.
 * The child field is required if either the mother or the father field is present.
 */
public class DependentFieldValidatorTest {

    private DependentFieldValidator dependentFieldValidator = new DependentFieldValidator();

    @Before
    public void setup(){
        DependentField dependentField = mock(DependentField.class);
        when(dependentField.dependentField()).thenReturn("child");
        String[] parents = new String[]{"mother","father"};
        when(dependentField.targetFields()).thenReturn(parents);

        dependentFieldValidator.initialize(dependentField);
    }

    @Test
    public void validAsLongAsDependentFieldIsNotNull(){
        Family family = new Family();
        family.setChild("Jesus");
        assertTrue(dependentFieldValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void childMustBePresentIfThereIsAFather(){
        Family family = new Family();
        family.setFather("Joseph");
        assertFalse(dependentFieldValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void childMustBePresentIfThereIsAMother(){
        Family family = new Family();
        family.setMother("Mary");
        assertFalse(dependentFieldValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void childMustBePresentIfBothParentsArePresent(){
        Family family = new Family();
        family.setMother("Mary");
        family.setFather("Joseph");
        assertFalse(dependentFieldValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }


}
