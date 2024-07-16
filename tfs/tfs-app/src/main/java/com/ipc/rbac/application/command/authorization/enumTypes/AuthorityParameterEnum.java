package com.ipc.rbac.application.command.authorization.enumTypes;

public enum AuthorityParameterEnum {
	
	AUTHORITY_ID, AUTHORITY_TYPE, EFFECTIVE_FROM, EFFECTIVE_TO;
	
	@Override
	public String toString() {
		
		String str = "";
		
		switch (this) {

		    case AUTHORITY_ID:
			    str = "id";
			break;

		    case AUTHORITY_TYPE:
			    str = "authorityType";
			break;
			
		    case EFFECTIVE_FROM:
			    str = "effectiveFrom";
			break;
			
		    case EFFECTIVE_TO:
			    str = "effectiveTo";
			break;			
			
		}
		
		return str;
	}	

}
