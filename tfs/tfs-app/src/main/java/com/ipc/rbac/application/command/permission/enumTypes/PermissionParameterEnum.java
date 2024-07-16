package com.ipc.rbac.application.command.permission.enumTypes;

public enum PermissionParameterEnum {

	PERMISSION_ID, PERMISSION_NAME, PERMISSION_DESCRIPTION;

	@Override
	public String toString() {
		
		String str = "";
		
		switch (this) {

		    case PERMISSION_ID:
			    str = "permissionId";
			break;

		    case PERMISSION_NAME:
			    str = "permissionName";
			break;
		
		    case PERMISSION_DESCRIPTION:
		    	str = "permissionDescription";
		    break;
		}
		
		return str;
	}
	
}
