package com.ucpb.tfs.domain.sysparams;

public class CutOff {

	private Long id;
	
	private String cutOffTime;

	public Long getId() {
		return id;
	}

	public void setCutOffCode(Long id) {
		this.id = id;
	}

	public String getCutOffTime() {
		return cutOffTime;
	}

	public void setCutOffTime(String cutOffTime) {
		this.cutOffTime = cutOffTime;
	}
	
}
