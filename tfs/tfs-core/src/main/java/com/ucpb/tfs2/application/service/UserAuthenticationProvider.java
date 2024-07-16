package com.ucpb.tfs2.application.service;

import java.util.Map;

public interface UserAuthenticationProvider {

    public Map authenticate(String userid, String password);

    public Map authenticate(String ldapDomain, String userid, String password);

    public Map authenticateLdap(String userid, String password);

}
