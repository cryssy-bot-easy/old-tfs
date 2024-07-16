package com.ipc.rbac.application.command.permission;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.Command;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;

import java.util.HashMap;
import java.util.Map;

@Command
public class UpdatePermissionCommand {
	
	private Map<PermissionParameterEnum, String> parameterMap;

	public UpdatePermissionCommand() {
		this.parameterMap = new HashMap<PermissionParameterEnum, String>();
	}
	
	public Map<PermissionParameterEnum, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<PermissionParameterEnum, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	public String getParameterValue(PermissionParameterEnum key) {
		return this.parameterMap.get(key);
	}
	
	public void putParameter(PermissionParameterEnum key, String value) {
		this.parameterMap.put(key, value);
	}

}
