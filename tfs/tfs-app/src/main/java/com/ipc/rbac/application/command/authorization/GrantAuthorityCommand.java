package com.ipc.rbac.application.command.authorization;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.Command;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorityParameterEnum;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorizationParameterEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command
public class GrantAuthorityCommand {

	private Map<AuthorizationParameterEnum, String> parameterMap;
	
	private List<Map<AuthorityParameterEnum, String>> authorityList;

	public GrantAuthorityCommand() {
		this.parameterMap = new HashMap<AuthorizationParameterEnum, String>();
	}
	
	public Map<AuthorizationParameterEnum, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<AuthorizationParameterEnum, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	public String getParameterValue(AuthorizationParameterEnum key) {
		return this.parameterMap.get(key);
	}
	
	public void putParameter(AuthorizationParameterEnum key, String value) {
		this.parameterMap.put(key, value);
	}
	
	public List<Map<AuthorityParameterEnum, String>> getAuthorityList() {
		return authorityList;
	}

	public void setAuthorityList(
		List<Map<AuthorityParameterEnum, String>> authorityList) {
		this.authorityList = authorityList;
	}		
	
}
