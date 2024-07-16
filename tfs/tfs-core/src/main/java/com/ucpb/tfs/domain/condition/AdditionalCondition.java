package com.ucpb.tfs.domain.condition;

import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/4/12
 */

/**
 * Description:   Added amendId and amendCode
 * Modified by:   Cedrick C. Nungay
 * Date Modified: 09/03/18
 */

public class AdditionalCondition implements Serializable {
    
    private String id;

    private ConditionType conditionType;
    
    private ConditionCode conditionCode;
    
    private String condition;
    
    private int sequenceNumber = 0;

    private BigDecimal amendId;

    private String amendCode;
    
    public AdditionalCondition() {
    }

	public AdditionalCondition(ConditionType conditionType, ConditionCode conditionCode, String condition) {
        this.conditionType = conditionType;
        this.conditionCode = conditionCode;
        this.condition = condition;
    }

    public AdditionalCondition(ConditionType conditionType, ConditionCode conditionCode, String condition,int sequenceNumber) {
    	this.conditionType = conditionType;
    	this.conditionCode = conditionCode;
    	this.condition = condition;
    	this.sequenceNumber = sequenceNumber;
    }
    
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("conditionType", conditionType.toString());

        if(conditionCode != null) {
            map.put("conditionCode", conditionCode.toString());
        }

        map.put("condition", condition);

        return map;
    }

    public String getCondition() {
        return condition;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

    public BigDecimal getAmendId() {
        return amendId;
    }

    public void setAmendId(BigDecimal amendId) {
        this.amendId = amendId;
    }

    public String getAmendCode() {
        return amendCode;
    }

    public void setAmendCode(String amendCode) {
        this.amendCode = amendCode;
    }
}
