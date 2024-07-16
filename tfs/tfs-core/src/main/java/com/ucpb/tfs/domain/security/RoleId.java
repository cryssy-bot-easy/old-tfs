package com.ucpb.tfs.domain.security;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 9/21/12
 */
public class RoleId implements Serializable {

    String roleId;

    RoleId() {
    }

    public RoleId(final String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return roleId;
    }
}
