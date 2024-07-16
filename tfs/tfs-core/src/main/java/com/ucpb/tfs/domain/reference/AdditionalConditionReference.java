package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.condition.ConditionCode;
import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;

/**
 * User: Marv
 * Date: 11/4/12
 */

public class AdditionalConditionReference {
    
    private Long id;

    private ConditionType conditionType;

    private ConditionCode conditionCode;

    private String condition;
    
    public AdditionalConditionReference() {
    }
    
    public AdditionalConditionReference(ConditionType conditionType, ConditionCode conditionCode, String condition) {
        this.conditionType = conditionType;
        this.conditionCode = conditionCode;
        this.condition = condition;
    }
    
}
