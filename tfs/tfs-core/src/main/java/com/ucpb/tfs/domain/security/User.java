package com.ucpb.tfs.domain.security;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements Serializable {

    UserId userId;
    
	Set<Role> roles;
    //Set<Permission> permissions;

    // We'll create setters for these, DDD-style
    private Boolean isLoggedIn;
    private Timestamp lastLogin;
    private Timestamp lastLogout;

	public User() {
    }

    public User(String userId) {
        this.userId = new UserId(userId);

        this.roles = new HashSet<Role>();
        //this.permissions = new HashSet<Permission>();
    }

    public UserId getUserId() {
        return userId;
    }
    
    public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public void addRole(Role role) {
        roles.add(role);
    }

    public void addPermission(Permission permission) {
        //permissions.add(permission);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void updateRoles(List<Role> newSet) {

        // retain only those that we still have
        roles.retainAll(newSet);

        // remove all that are already with us
        newSet.removeAll(roles);

        // add all that we have left
        roles.addAll(newSet);
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public Timestamp getLastLogout() {
		return lastLogout;
	}
    
    public Boolean isLoggedIn() {
        if (this.isLoggedIn == null) {
            return Boolean.FALSE;
        }
        return this.isLoggedIn;
    }

    public void recordLogIn() {

        // Encapsulate saving of last login and isLoggedIn tagging

        // Set isLoggedIn = true
        this.isLoggedIn = Boolean.TRUE;
        this.lastLogin = new Timestamp(new Date().getTime());
    }

    public void removeLogIn() {
        // Set isLoggedIn = false
        this.isLoggedIn = Boolean.FALSE;
        this.lastLogout = new Timestamp(new Date().getTime());
    }
}

