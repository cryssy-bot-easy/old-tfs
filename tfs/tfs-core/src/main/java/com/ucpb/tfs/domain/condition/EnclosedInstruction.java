package com.ucpb.tfs.domain.condition;

import com.ucpb.tfs.domain.condition.enumTypes.InstructionType;
import org.hibernate.envers.Audited;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/19/13
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Audited
public class EnclosedInstruction implements Serializable {

    private String id;

    private String instruction;

    private InstructionType instructionType;

    public EnclosedInstruction() {}

    public EnclosedInstruction(String instruction, InstructionType instructionType) {
        this.instruction = instruction;

        this.instructionType = instructionType;
    }
}
