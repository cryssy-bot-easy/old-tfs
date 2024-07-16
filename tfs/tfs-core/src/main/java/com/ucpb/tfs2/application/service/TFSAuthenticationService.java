package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.security.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.Object;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFSAuthenticationService implements UserAuthenticationProvider {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public Map authenticate(String userid, String password) {

        Boolean authenticated = false;
        Map<String, Object> returnMap = new HashMap<String, Object>();

        try
        {
            Map user = userRepository.getUserById(new UserId(userid));

            if(user != null) {

            returnMap.put("username", ((Map)user.get("userId")).get("id"));

            // mock password checking
            if(userid.equalsIgnoreCase(password)) {
                authenticated = true;

                returnMap.put("roles", user.get("roles"));
            }
        }
        }
        catch(Exception e)
        {
            authenticated = false;
        }

        returnMap.put("authenticated", authenticated);

        return returnMap;

    }

    @Override
    public Map authenticate(String ldapDomain, String userid, String password) {
        Boolean authenticated = false;
        Map<String, Object> returnMap = new HashMap<String, Object>();

        try
        {
            Map user = userRepository.getUserById(new UserId(userid));

            if(user != null) {

                returnMap.put("username", ((Map)user.get("userId")).get("id"));

                // mock password checking
                if(userid.equalsIgnoreCase(password)) {
                    authenticated = true;

                    returnMap.put("roles", user.get("roles"));
                }
            }
        }
        catch(Exception e)
        {
            authenticated = false;
        }

        returnMap.put("authenticated", authenticated);

        return returnMap;
    }

    @Override
    public Map authenticateLdap(String userid, String password) {
        Boolean authenticated = false;
        Map<String, Object> returnMap = new HashMap<String, Object>();

        try
        {
            Map user = userRepository.getUserById(new UserId(userid));

            if(user != null) {

                returnMap.put("username", ((Map)user.get("userId")).get("id"));

                // mock password checking
                if(userid.equalsIgnoreCase(password)) {
                    authenticated = true;

                    returnMap.put("roles", user.get("roles"));
                }
            }
        }
        catch(Exception e)
        {
            authenticated = false;
        }

        returnMap.put("authenticated", authenticated);

        return returnMap;
    }
}
