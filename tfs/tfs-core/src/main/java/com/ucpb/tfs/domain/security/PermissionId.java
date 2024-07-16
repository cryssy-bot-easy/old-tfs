package com.ucpb.tfs.domain.security;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 9/21/12
 */
public class PermissionId implements Serializable {

    String permissionId;

    public PermissionId() {
    }

    public PermissionId(final String permissionId) {
        this.permissionId = permissionId;
    }

}
