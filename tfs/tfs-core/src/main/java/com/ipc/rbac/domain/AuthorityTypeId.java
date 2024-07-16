package com.ipc.rbac.domain;

import org.apache.commons.lang.Validate;

/**
 * User: IPCVal
 * Date: 7/27/12
 */
public class AuthorityTypeId {

    private String id;

    public AuthorityTypeId() {}

    public AuthorityTypeId(String id) {
        Validate.notNull(id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return String representation of this AuthorityTypeId
     */
    public String idString() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
