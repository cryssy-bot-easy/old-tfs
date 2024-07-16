package com.ipc.rbac.domain;

import com.incuventure.ddd.domain.annotations.DomainEntity;
import com.ipc.rbac.domain.enumTypes.AuthorityTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
// import

/**
 * User: Jett
 * Date: 6/20/12
 */
@DomainEntity
public class Role extends AuthorityType implements Serializable {

    private List<Permission> permissions;
    
    public Role() {
    	super.setType(AuthorityTypeEnum.ROLE);
        permissions = new ArrayList<Permission>();
    }

    public Role(String name, String description) {
        this();
        super.setName(name);
        super.setDescription(description);
    }

    // add a permission
    public void addPermission(Permission permission) {
        permissions.add(permission);
    }
    
    // explicitly removes permission
    public void removePermission(Permission permission) {
    	permissions.remove(permission);
    }

    // return list of authorities
    public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
}
