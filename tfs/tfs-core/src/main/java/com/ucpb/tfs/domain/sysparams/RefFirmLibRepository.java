package com.ucpb.tfs.domain.sysparams;

import java.util.List;

public interface RefFirmLibRepository {
	public void saveOrUpdate(RefFirmLib refFirmLib);
	
	public void delete(String firmCode);
	
	public RefFirmLib getRefFirmLib(String firmCode);
	
	public List<RefFirmLib> getRequestsMatching(String firmCode, String firmDescription);
	
}
