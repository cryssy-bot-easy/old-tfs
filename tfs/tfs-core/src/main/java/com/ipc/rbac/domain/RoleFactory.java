package com.ipc.rbac.domain;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.ddd.domain.annotations.DomainFactory;

@DomainFactory
public class RoleFactory {

    public com.ipc.rbac.domain.Role createRole(String name, String description) {
    	com.ipc.rbac.domain.Role role = new com.ipc.rbac.domain.Role(name, description);
    	
        return role;
    }		
	
}
