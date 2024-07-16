package com.ucpb.tfs.swift.validation;

import javax.validation.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
@javax.validation.Constraint(validatedBy = JointFieldValidator.class)
public @interface MultiRegex {

    String message() default "field must be present is all other fields are present";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] regularExpressions();


}