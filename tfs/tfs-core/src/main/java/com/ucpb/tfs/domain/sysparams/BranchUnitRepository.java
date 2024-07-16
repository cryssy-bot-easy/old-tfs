package com.ucpb.tfs.domain.sysparams;

import java.util.List;
import java.util.Map;

public interface BranchUnitRepository {
	
	public void saveOrUpdate(BranchUnit branchUnit);
    
    public BranchUnit getBranchUnit(String unitCode);
    
    public List<Map<String, Object>> getBranchUnit(String unitCode, String unitName);
    
}
