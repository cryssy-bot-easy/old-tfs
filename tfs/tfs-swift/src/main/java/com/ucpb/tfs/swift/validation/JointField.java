package com.ucpb.tfs.swift.validation;

import javax.validation.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = JointFieldValidator.class)
public @interface JointField {

    String message() default "field must be present is all other fields are present";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field();

    String[] jointFields();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        JointField[] value();
    }

}
