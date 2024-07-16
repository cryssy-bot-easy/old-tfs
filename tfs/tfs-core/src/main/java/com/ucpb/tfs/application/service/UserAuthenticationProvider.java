package com.ucpb.tfs.application.service;

/**
 * User: Jett
 * Date: 9/26/12
 */
public interface UserAuthenticationProvider {

    public Boolean authenticate(String userid, String password);

}
