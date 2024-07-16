package com.ucpb.tfs.swift.validation;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 */
public class SpelValidator implements ConstraintValidator<Spel,Object> {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Expression spelExpression;
    @Override
    public void initialize(Spel spel) {
        this.spelExpression = parser.parseExpression(spel.spelExpression());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        EvaluationContext evaluationContext = new StandardEvaluationContext(value);
        if(Boolean.TRUE.equals(spelExpression.getValue(evaluationContext))){
            return true;
        }
        return false;
    }
}