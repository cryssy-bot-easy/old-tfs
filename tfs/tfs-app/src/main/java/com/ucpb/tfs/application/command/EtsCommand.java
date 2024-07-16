/**
 * 
 */
package com.ucpb.tfs.application.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author itdipc6
 *
 */
public abstract class EtsCommand implements Serializable {
    
    protected String token;
    protected String userActiveDirectoryId;
    protected Map<String, Object> parameterMap = new HashMap<String,Object>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }

    public void setUserActiveDirectoryId(String userActiveDirectoryId) {
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	public Object getParameterValue(String key) {
		return this.parameterMap.get(key);
	}
	
	public void putParameter(String key, Object value) {
		this.parameterMap.put(key, value);
	}
}
