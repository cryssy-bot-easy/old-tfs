package com.ipc.rbac.domain;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.ddd.domain.annotations.DomainFactory;

@DomainFactory
public class AuthorizationFactory {
	
    public Authorization createAuthorization(UserActiveDirectoryId userId) {
    	Authorization authorization = new Authorization(userId);
    	return authorization;
    }	

}
