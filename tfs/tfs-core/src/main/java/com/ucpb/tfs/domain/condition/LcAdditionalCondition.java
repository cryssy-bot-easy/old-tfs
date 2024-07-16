package com.ucpb.tfs.domain.condition;

import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/4/12
 */

@Audited
public class LcAdditionalCondition implements Serializable {

    private String id;

    private ConditionType conditionType;

    private ConditionCode conditionCode;

    private String condition;

    public LcAdditionalCondition() {
    }

    public LcAdditionalCondition(ConditionType conditionType, ConditionCode conditionCode, String condition) {
        this.conditionType = conditionType;
        this.conditionCode = conditionCode;
        this.condition = condition;
    }
    
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("conditionType", conditionType.toString());
        
        if(conditionCode != null) {
            map.put("conditionCode", conditionCode.toString());
        }
        
        map.put("condition", condition);

        return  map;
    }

    public String getCondition() {
        return condition;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public void setConditionCode(ConditionCode conditionCode) {
        this.conditionCode = conditionCode;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
