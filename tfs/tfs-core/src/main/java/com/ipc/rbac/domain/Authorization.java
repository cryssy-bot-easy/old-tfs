package com.ipc.rbac.domain;

import com.incuventure.ddd.domain.annotations.DomainAggregateRoot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Jett
 * Date: 6/20/12
 */
@DomainAggregateRoot
public class Authorization implements Serializable {

    private Long id;

    private UserActiveDirectoryId userActiveDirectoryId;

    private List<Authority> authorities;

    public Authorization() {
        authorities = new ArrayList<Authority>();
    }
    
    public Authorization(UserActiveDirectoryId userActiveDirectoryId) {
    	this();
    	this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public UserActiveDirectoryId getUserId() {
        return userActiveDirectoryId;
    }

    public void setUserId(UserActiveDirectoryId userId) {
        this.userActiveDirectoryId = userId;
    }

    public void addAuthority(Authority authority) {
		authorities.add(authority);
	}
	
	public void removeAuthority(Authority authority) {
		authorities.remove(authority);
	}
}
