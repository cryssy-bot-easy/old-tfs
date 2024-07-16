package com.ucpb.tfs.swift.validation;

/**
 */

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
@javax.validation.Constraint(validatedBy = OrValidator.class)
public @interface Or {

    String message() default "At least one of the specified fields must be present";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        Or[] value();
    }

}