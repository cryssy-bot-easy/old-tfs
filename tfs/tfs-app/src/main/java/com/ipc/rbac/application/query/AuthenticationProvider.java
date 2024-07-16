package com.ipc.rbac.application.query;

/**
 * User: Jett
 * Date: 9/20/12
 */
public interface AuthenticationProvider {

    public Boolean authenticate(String userid, String password);

}
