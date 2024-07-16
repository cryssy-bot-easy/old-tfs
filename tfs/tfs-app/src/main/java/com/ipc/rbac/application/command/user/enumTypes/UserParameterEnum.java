/**
 *
 */
package com.ipc.rbac.application.command.user.enumTypes;

/**
 * @author Val
 *
 */
public enum UserParameterEnum {

	USER_ID, USER_ACTIVE_DIRECTORY_ID, FIRST_NAME, LAST_NAME;

	@Override
	public String toString() {
		
		String str = "";
		
		switch (this) {

		    case USER_ID:
			    str = "userId";
			break;

		    case USER_ACTIVE_DIRECTORY_ID:
			    str = "userActiveDirectoryId";
			break;
		
		    case FIRST_NAME:
		    	str = "firstName";
		    break;
		    
		    case LAST_NAME:
		    	str = "lastName";
		    break;		    
		}
		
		return str;
	}
	
}
