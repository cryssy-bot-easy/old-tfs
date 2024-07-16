package com.ucpb.tfs.domain.reimbursing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/4/12
 */

public class InstructionToBank implements Serializable {

    private String id;

    protected InstructionToBankCode instructionToBankCode;

    protected String instruction;

    public InstructionToBank() {}

    public InstructionToBank(InstructionToBankCode instructionToBankCode, String instruction) {
        this.instructionToBankCode = instructionToBankCode;
        this.instruction = instruction;
    }
    
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("instructionToBankCode", instructionToBankCode.toString());
        map.put("instruction", instruction);

        return map;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setId(String id) {
        this.id = id;
    }
}
