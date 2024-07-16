package com.ipc.rbac.domain;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 7/27/12
 */
public class AuthorizationId implements Serializable {

    private String id;

    public AuthorizationId() {}

    public AuthorizationId(final String id) {
        Validate.notNull(id);
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
