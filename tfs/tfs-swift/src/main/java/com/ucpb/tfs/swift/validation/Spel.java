package com.ucpb.tfs.swift.validation;

import javax.validation.Payload;
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
@javax.validation.Constraint(validatedBy = SpelValidator.class)
public @interface Spel {

    String message() default "Spel validation failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String spelExpression();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        Spel[] value();
    }

}