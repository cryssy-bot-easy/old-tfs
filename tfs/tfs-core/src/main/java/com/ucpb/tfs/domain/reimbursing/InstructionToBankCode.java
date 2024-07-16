package com.ucpb.tfs.domain.reimbursing;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: Marv
 * Date: 11/4/12
 */

public class InstructionToBankCode implements Serializable {

    private String instructionToBankCode;

    public InstructionToBankCode() {}

    public InstructionToBankCode(final String instructionToBankCode) {
        Validate.notNull(instructionToBankCode);
        this.instructionToBankCode = instructionToBankCode;
    }

    @Override
    public String toString() {
        return instructionToBankCode;
    }

}
