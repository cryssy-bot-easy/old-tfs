package com.ipc.rbac.domain.enumTypes;

public enum AuthorityTypeEnum {

	ROLE, PERMISSION;

	@Override
	public String toString() {
		String str = "";
		switch (this) {
		case ROLE:
			str = "ROLE";
			break;
		case PERMISSION:
			str = "PERMISSION";
			break;
		}
		
		return str;
	}
	
}
