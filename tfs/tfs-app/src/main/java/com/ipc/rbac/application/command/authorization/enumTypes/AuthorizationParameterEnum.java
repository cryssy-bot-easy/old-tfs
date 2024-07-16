package com.ipc.rbac.application.command.authorization.enumTypes;

public enum AuthorizationParameterEnum {

	AUTHORIZATION_ID, USER, AUTHORITIES, EFFECTIVE_FROM, EFFECTIVE_TO;

	@Override
	public String toString() {
		
		String str = "";
		
		switch (this) {
		
		    case AUTHORIZATION_ID:
			    str = "id";
			break;		

		    case USER:
			    str = "user";
			break;

		    case AUTHORITIES:
			    str = "authorities";
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
