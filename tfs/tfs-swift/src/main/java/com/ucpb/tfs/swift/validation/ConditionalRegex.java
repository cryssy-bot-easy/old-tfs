package com.ucpb.tfs.swift.validation;

import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 */
@Target({FIELD,TYPE})
@Retention(RUNTIME)
@Documented
@javax.validation.Constraint(validatedBy = ConditionalRegexValidator.class)
public @interface ConditionalRegex {

    String message() default "Field does not match the given pattern";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String condition();

    String ifTtrue();

    String ifFalse();

    @Target({FIELD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        ConditionalRegex[] value();
    }
}
