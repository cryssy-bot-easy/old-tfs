package com.ucpb.tfs.swift.validation;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 */
public class ConditionalSpelValidator implements ConstraintValidator<ConditionalSpel,Object> {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Expression condition;

    private Expression spelValidation;

    @Override
    public void initialize(ConditionalSpel conditionalSpel) {
        this.condition = parser.parseExpression(conditionalSpel.condition());
        this.spelValidation = parser.parseExpression(conditionalSpel.spelValidation());
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        if(Boolean.TRUE.equals(condition.getValue(o))){
            return Boolean.TRUE.equals(spelValidation.getValue(o));
        }

        return true;
    }
}
