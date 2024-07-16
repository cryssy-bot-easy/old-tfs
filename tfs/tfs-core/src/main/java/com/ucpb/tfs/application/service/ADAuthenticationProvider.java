package com.ucpb.tfs.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.ucpb.tfs2.application.service.CustomAdAuthenticationProvider;

/**
 * User: Jett
 * Date: 9/26/12
 */
public class ADAuthenticationProvider implements UserAuthenticationProvider {

    @Autowired
    CustomAdAuthenticationProvider adAuthenticationProvider;

    @Override
    public Boolean authenticate(String userid, String password) {

        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userid, password);

        try
        {
            Authentication authentication = adAuthenticationProvider.authenticate(userToken);
            return authentication.isAuthenticated();

        }
        catch(BadCredentialsException e)
        {
            String exceptionMessage = e.getCause().getMessage();

            // if cause of the exception is error code 531, it means a workstation restriction
            // we just ignore it
            if(exceptionMessage.contains("data 531")) {
                return true;
            }

            return false;
        }

    }
}
