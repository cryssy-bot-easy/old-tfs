package com.ucpb.tfs.domain.sysparams;

import java.util.List;
import java.util.Map;

public interface RefRequiredDocumentsRepository {
	
	public void saveOrUpdate(RefRequiredDocuments refRequiredDocuments);
    
    public RefRequiredDocuments getRefRequiredDocument(String documentCode);
    
    public List<RefRequiredDocuments> getAllRefRequiredDocuments();
    
    public void delete(Long id);
    
}
