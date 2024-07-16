package com.ipc.rbac.domain;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.ddd.domain.annotations.DomainFactory;

@DomainFactory
public class PermissionFactory {

    public com.ipc.rbac.domain.Permission createPermission(String name, String description) {
    	com.ipc.rbac.domain.Permission permission = new com.ipc.rbac.domain.Permission(name, description);
    	
        return permission;
    }	
	
}
