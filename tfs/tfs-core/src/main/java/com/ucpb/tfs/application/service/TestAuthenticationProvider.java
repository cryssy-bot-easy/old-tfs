package com.ucpb.tfs.application.service;


public class TestAuthenticationProvider implements UserAuthenticationProvider {

    @Override
    public Boolean authenticate(String userid, String password) {

        // used for testing only, return true if password matches userid
        return(userid.equals(password));

    }
}
