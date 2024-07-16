package com.ucpb.tfs.domain.reimbursing;

import com.ucpb.tfs.domain.product.DocumentNumber;

import java.util.Set;

/**
 * User: Marv
 * Date: 11/4/12
 */

public interface LcInstructionToBankRepository {

    public void persist(LcInstructionToBank lcInstructionToBank);

    public void merge(LcInstructionToBank lcInstructionToBank);

    public void update(LcInstructionToBank lcInstructionToBank);

    public LcInstructionToBank load(InstructionToBankCode instructionToBankCode);

    public Set<LcInstructionToBank> load(DocumentNumber documentNumber);
}
