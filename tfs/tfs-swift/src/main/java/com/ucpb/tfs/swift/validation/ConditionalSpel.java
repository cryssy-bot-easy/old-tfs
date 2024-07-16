package com.ucpb.tfs.swift.validation;

import javax.validation.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
@javax.validation.Constraint(validatedBy = ConditionalSpelValidator.class)
public @interface ConditionalSpel {

    String message() default "Conditional Spel validation failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String condition();

    String spelValidation();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        ConditionalSpel[] value();
    }

}