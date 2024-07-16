package com.ipc.rbac.application.command.role;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.Command;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;
import com.ipc.rbac.application.command.role.enumTypes.RoleParameterEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command
public class RemovePermissionsFromRoleCommand {

	private Map<RoleParameterEnum, String> parameterMap;
	
	private List<Map<PermissionParameterEnum, String>> permissionList;

	public RemovePermissionsFromRoleCommand() {
		this.parameterMap = new HashMap<RoleParameterEnum, String>();
	}
	
	public Map<RoleParameterEnum, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<RoleParameterEnum, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	public String getParameterValue(RoleParameterEnum key) {
		return this.parameterMap.get(key);
	}
	
	public void putParameter(RoleParameterEnum key, String value) {
		this.parameterMap.put(key, value);
	}

	public List<Map<PermissionParameterEnum, String>> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(
		List<Map<PermissionParameterEnum, String>> permissionList) {
		this.permissionList = permissionList;
	}		
	
}
