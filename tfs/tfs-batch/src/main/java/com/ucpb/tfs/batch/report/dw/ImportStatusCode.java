package com.ucpb.tfs.batch.report.dw;

public enum ImportStatusCode {
	
	AMENDED_DMLC("2"),SELF_FUNDED("4"),SIGHT_NOT_SELFUNDED("7"),USANCE_NOT_SELFFUNDED("8"),DEFERRED_NOT_SELFUNDED("8");
	
	private String statusCode;
	
	private ImportStatusCode(String statusCode){
		this.statusCode = statusCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

}
