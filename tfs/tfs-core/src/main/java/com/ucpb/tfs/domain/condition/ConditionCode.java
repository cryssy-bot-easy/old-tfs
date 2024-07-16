package com.ucpb.tfs.domain.condition;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: Marv
 * Date: 11/4/12
 */

public class ConditionCode implements Serializable {

    private String conditionCode;

    public ConditionCode() {}

    public ConditionCode(final String conditionCode) {
        Validate.notNull(conditionCode);
        this.conditionCode = conditionCode;
    }

    @Override
    public String toString() {
        return conditionCode;
    }

}
