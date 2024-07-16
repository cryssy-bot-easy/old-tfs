package com.ucpb.tfs.domain.reimbursing;

/**
 * User: Marv
 * Date: 11/4/12
 */

public interface InstructionToBankRepository {

    public void persist(InstructionToBank instructionToBank);

    public void merge(InstructionToBank instructionToBank);

    public void update(InstructionToBank instructionToBank);

    public InstructionToBank load(InstructionToBankCode instructionToBankCode);
    
}
