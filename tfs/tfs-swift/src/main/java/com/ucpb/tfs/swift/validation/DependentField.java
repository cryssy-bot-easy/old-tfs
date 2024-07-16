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
@Constraint(validatedBy = DependentFieldValidator.class)
public @interface DependentField {

    String message() default "{com.ucpb.tfs.interfaces.swift.validation.dependentfield}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String dependentField();

    String[] targetFields();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        DependentField[] value();
    }

}
