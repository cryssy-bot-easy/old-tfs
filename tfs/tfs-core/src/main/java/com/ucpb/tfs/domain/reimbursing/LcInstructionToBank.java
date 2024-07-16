package com.ucpb.tfs.domain.reimbursing;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/5/12
 */

@Audited
public class LcInstructionToBank implements Serializable {

    private String id;

    private InstructionToBankCode instructionToBankCode;

    private String instruction;

    public LcInstructionToBank() {}

    public LcInstructionToBank(InstructionToBankCode instructionToBankCode, String instruction) {
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

    public void setInstructionToBankCode(InstructionToBankCode instructionToBankCode) {
        this.instructionToBankCode = instructionToBankCode;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
