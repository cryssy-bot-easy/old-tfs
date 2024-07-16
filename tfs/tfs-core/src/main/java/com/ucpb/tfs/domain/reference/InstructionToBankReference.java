package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode;

/**
 * User: Marv
 * Date: 11/4/12
 */

public class InstructionToBankReference {

    private Long id;
    
    private InstructionToBankCode instructionToBankCode;
    
    private String instruction;
    
    public InstructionToBankReference() {
    }
    
    public InstructionToBankReference(InstructionToBankCode instructionToBankCode, String instruction) {
        this.instructionToBankCode = instructionToBankCode;
        this.instruction = instruction;
    }
    
}
