package com.ucpb.tfs.domain.security;

import java.io.Serializable;
import java.lang.*;
import java.lang.Object;

/**
 * User: Jett
 * Date: 9/21/12
 */
public class UserId implements Serializable {

    String id;

    public UserId() {
    }

    public UserId(final String userid) {
        this.id = userid;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserId userId = (UserId) o;

        if (id != null ? !id.equals(userId.id) : userId.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
