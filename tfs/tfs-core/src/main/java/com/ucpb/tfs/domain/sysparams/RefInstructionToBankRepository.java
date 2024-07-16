package com.ucpb.tfs.domain.sysparams;

import java.util.List;
import java.util.Map;

public interface RefInstructionToBankRepository {
	public void saveOrUpdate(RefInstructionToBank refInstructionToBank);
    
    public RefInstructionToBank getRefInstructionToBank(String instructionToBankCode);
    
    public List<RefInstructionToBank> getAllRefInstructionToBank();
    
    public void delete(Long id);
}
