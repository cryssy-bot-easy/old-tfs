package com.ipc.rbac.application.command.role.enumTypes;

public enum RoleParameterEnum {

	ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION;

	@Override
	public String toString() {
		
		String str = "";
		
		switch (this) {

		    case ROLE_ID:
			    str = "roleName";
			break;

		    case ROLE_NAME:
			    str = "roleName";
			break;

		    case ROLE_DESCRIPTION:
		    	str = "roleDescription";
		    break;
		}
		
		return str;
	}
	
}
