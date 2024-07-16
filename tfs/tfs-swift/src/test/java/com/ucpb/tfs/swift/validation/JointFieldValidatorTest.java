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
public class JointFieldValidatorTest {


    private JointFieldValidator jointFieldValidator;

    private JointField jointField;

    @Before
    public void setup(){
        jointFieldValidator = new JointFieldValidator();
        jointField = mock(JointField.class);
        when(jointField.field()).thenReturn("child");
        when(jointField.jointFields()).thenReturn(new String[] {"mother","father"});
        jointFieldValidator.initialize(jointField);
    }


    @Test
    public void successfulWhenAllFieldsArePresent(){
        Family family = new Family();
        family.setChild("child");
        family.setFather("father");
        family.setMother("mother");

        assertTrue(jointFieldValidator.isValid(family,mock(ConstraintValidatorContext.class)));

    }

    @Test
    public void targetFieldCanBeNullIfAtLeastOneJointFieldIsNull(){
        Family family = new Family();
        family.setChild(null);
        family.setFather("father");
        family.setMother(null);

        assertTrue(jointFieldValidator.isValid(family,mock(ConstraintValidatorContext.class)));

    }

    @Test
    public void targetFieldCannotBeNullIfAllJointFieldsAreNotNull(){
        Family family = new Family();
        family.setChild(null);
        family.setFather("father");
        family.setMother("mother");

        assertFalse(jointFieldValidator.isValid(family, mock(ConstraintValidatorContext.class)));
    }




}
