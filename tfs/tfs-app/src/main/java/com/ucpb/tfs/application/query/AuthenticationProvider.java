package com.ucpb.tfs.application.query;

import java.util.Map;

/**
 * User: Jett
 * Date: 9/20/12
 */
public interface AuthenticationProvider {

    public Map<String, Object> authenticate(String userid, String password);

}
