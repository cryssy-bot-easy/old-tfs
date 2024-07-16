package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode;

import java.util.List;

/**
 * User: Marv
 * Date: 11/4/12
 */

public interface InstructionToBankReferenceRepository {

    public void save(InstructionToBankReference instructionToBankReference);

    public InstructionToBankReference load(InstructionToBankCode instructionToBankCode);

    public List<InstructionToBankReference> getInstructionsToBankReference();

    public void clear();
    
}
