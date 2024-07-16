package com.ipc.rbac.domain;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 7/27/12
 */
public class UserActiveDirectoryId implements Serializable {

    private String userActiveDirectoryId;

    public UserActiveDirectoryId() {}

    public UserActiveDirectoryId(final String userActiveDirectoryId) {
        Validate.notNull(userActiveDirectoryId);
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    @Override
    public String toString() {
        return userActiveDirectoryId;
    }

    public String getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }
}
