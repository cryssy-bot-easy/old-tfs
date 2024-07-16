package com.ucpb.tfs.domain.security;

import java.io.Serializable;

// a permission is an operation allowed on an object
public class Permission implements Serializable {

    PermissionId permissionId;

    Operation operation;
    Object object;

    public Permission(){
    }

    public Permission(final Operation operation, final Object object) {

        this.operation = operation;
        this.object = object;

        // create a new permissionId using the operation and object as the key
        this.permissionId = new PermissionId(operation.toString() + "-" + object.toString());

    }

    public PermissionId getPermissionId() {
        return this.permissionId;
    }

}
