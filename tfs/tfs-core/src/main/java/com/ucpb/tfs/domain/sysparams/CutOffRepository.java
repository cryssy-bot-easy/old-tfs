package com.ucpb.tfs.domain.sysparams;

public interface CutOffRepository {
	
	public CutOff getCutOffTime();
	
	public void save(CutOff cutOff);
}
