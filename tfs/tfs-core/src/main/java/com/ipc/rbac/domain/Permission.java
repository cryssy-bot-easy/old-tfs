package com.ipc.rbac.domain;

import com.incuventure.ddd.domain.annotations.DomainEntity;
import com.ipc.rbac.domain.enumTypes.AuthorityTypeEnum;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 6/20/12
 */
@DomainEntity
public class Permission extends AuthorityType implements Serializable {
	
    public Permission() {
    	super.setType(AuthorityTypeEnum.PERMISSION);
    }
    
    public Permission(String name, String description) {
    	this();
    	super.setName(name);
    	super.setDescription(description);
    }
}
