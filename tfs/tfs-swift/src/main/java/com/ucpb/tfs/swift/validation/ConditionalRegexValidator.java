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
public class ConditionalRegexValidator implements ConstraintValidator<ConditionalRegex,Object> {

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private Expression condition;
    private String ifTrue;
    private String ifFalse;


    @Override
    public void initialize(ConditionalRegex conditionalRegex) {
        this.condition = parser.parseExpression(conditionalRegex.condition());
        this.ifTrue = conditionalRegex.ifTtrue();
        this.ifFalse = conditionalRegex.ifFalse();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = false;
        EvaluationContext evaluationContext = new StandardEvaluationContext(value);
        if(value == null || (value instanceof String == false)){
            return true;
        }

        if(Boolean.TRUE.equals(condition.getValue(evaluationContext))){
            result = ((String) value).matches(ifTrue);
        }else{
            result = ((String)value).matches(ifFalse);
        }
        return result;
    }
}
