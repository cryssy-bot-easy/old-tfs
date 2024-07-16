package com.ucpb.tfs.domain.sysparams;

import java.util.List;
import java.util.Map;

public interface RefAdditionalConditionRepository {
	public void saveOrUpdate(RefAdditionalCondition refAdditionalCondition);
    
    public RefAdditionalCondition getRefAdditionalCondition(String conditionCode);
    
    public List<RefAdditionalCondition> getAllRefAdditionalCondition();
    
    public void delete(Long id);
}
