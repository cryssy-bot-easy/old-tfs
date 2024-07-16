/**
 * 
 */
package com.ipc.rbac.application.command.user;

import com.incuventure.cqrs.annotation.Command;
import com.ipc.rbac.application.command.user.enumTypes.UserParameterEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Val
 *
 */
@Command
public class AddUserCommand {
	
	private Map<UserParameterEnum, String> parameterMap;

	public AddUserCommand() {
		this.parameterMap = new HashMap<UserParameterEnum, String>();
	}
	
	public Map<UserParameterEnum, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<UserParameterEnum, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	public String getParameterValue(UserParameterEnum key) {
		return this.parameterMap.get(key);
	}
	
	public void putParameter(UserParameterEnum key, String value) {
		this.parameterMap.put(key, value);
	}
	
}
