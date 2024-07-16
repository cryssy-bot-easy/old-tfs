package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.security.*;
import com.ucpb.tfs.domain.security.enums.LdapDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import com.ucpb.tfs2.application.service.CustomAdAuthenticationProvider;

import java.lang.Object;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ADAuthenticationService implements UserAuthenticationProvider {
//
//	@Resource
//    ActiveDirectoryLdapAuthenticationProvider adAuthenticationProvider;
//
//	@Resource
//    ActiveDirectoryLdapAuthenticationProvider adAuthenticationProviderBranch;
	
	@Autowired
	CustomAdAuthenticationProvider adAuthenticationProvider;
	
	@Autowired
	CustomAdAuthenticationProvider adAuthenticationProviderBranch;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public Map authenticate(String userid, String password) {

        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userid, password);

        Boolean authenticated = false;

        List<String> adGroups = new ArrayList<String>();
        String username = "";

        System.out.println("AD Authentication");

        Map<String, Object> returnMap = new HashMap<String, Object>();

        System.out.println("authenticating");

        try
        {
            System.out.println("checking if user exist in tfs");
            // check first if user is registered in the TFS user table
            Map user = userRepository.getUserById(new UserId(userid));
            System.out.println("user " + user);

            if(user != null) {

                Employee employee = employeeRepository.getEmployee(new UserId(userid));

                if (employee.isSuspended()) {
                    throw new Exception("The User is currently suspended.");
                }

                Authentication authentication = adAuthenticationProvider.authenticate(userToken);

                List<GrantedAuthority> mappedAuthorities = (List<GrantedAuthority>) authentication.getAuthorities();

                if (mappedAuthorities.isEmpty()) {
                    throw new Exception("User is not associated with any valid group in Active Directory.");
                }

                for (GrantedAuthority granted : mappedAuthorities) {
                    System.out.println("granted authority " + granted.getAuthority());
                    adGroups.add(granted.getAuthority());
                }

                authenticated = authentication.isAuthenticated();

                System.out.println("authenticated: " + authenticated.toString());
            }

        }
        catch(BadCredentialsException e)
        {
            System.out.println("BadCredentialsException");
            String exceptionMessage = e.getCause().getMessage();
            System.out.println("error: " + exceptionMessage);

            e.printStackTrace();

            // set default to false since if the exception message contains data 531, it will be overriden
            authenticated = false;

            // if cause of the exception is error code 531, it means a workstation restriction
            // we just ignore it

            if(exceptionMessage.contains("data 531")) {
                authenticated = true;
            }
        } catch(Exception e) {

//            System.out.println("LDAP connection exception");
            System.out.println(e.getMessage());

            e.printStackTrace();

//            returnMap.put("authenticated", false);
            authenticated = false;
            returnMap.put("errorMessage", e.getMessage());

        }

        returnMap.put("username", userid);
        returnMap.put("authenticated", authenticated);
        returnMap.put("groups", adGroups);

        if(!adGroups.isEmpty()) {
            List roles = roleRepository.getRolesMatchingADGroups(adGroups);
            returnMap.put("roles", roles);
        }

        return returnMap;

    }

    @Override
    public Map authenticate(String ldapDomain, String userid, String password) {
        System.out.println("new AD Authentication");
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userid, password);

        Boolean authenticated = false;

        List<String> adGroups = new ArrayList<String>();
        String username = "";

        System.out.println("AD Authentication");

        Map<String, Object> returnMap = new HashMap<String, Object>();

        System.out.println("authenticating");

        Authentication authentication = null; //adAuthenticationProvider.authenticate(userToken);

        try
        {
            System.out.println("checking if user exist in tfs");
            // check first if user is registered in the TFS user table
            Map user = userRepository.getUserById(new UserId(userid));
            System.out.println("user " + user);

            if(user != null) {

                Employee employee = employeeRepository.getEmployee(new UserId(userid));

                if (employee.isSuspended()) {
                    throw new Exception("The User is currently suspended.");
                }

//                Authentication authentication = adAuthenticationProvider.authenticate(userToken);

                if (LdapDomain.BRANCH.equals(LdapDomain.valueOf(ldapDomain))) {
                    System.out.println("authenticating branch");
                    authentication = adAuthenticationProviderBranch.authenticate(userToken);

                } else if (LdapDomain.UCPB8.equals(LdapDomain.valueOf(ldapDomain))) {
                    System.out.println("authenticating main");
                    authentication = adAuthenticationProvider.authenticate(userToken);
                }

                List<GrantedAuthority> mappedAuthorities = (List<GrantedAuthority>) authentication.getAuthorities();

                if (mappedAuthorities.isEmpty()) {
                    throw new Exception("User is not associated with any valid group in Active Directory.");
                }

                for (GrantedAuthority granted : mappedAuthorities) {
                    System.out.println("granted authority " + granted.getAuthority());
                    adGroups.add(granted.getAuthority());
                }

                authenticated = authentication.isAuthenticated();

                System.out.println("authenticated: " + authenticated.toString());
            }

        }
        catch(BadCredentialsException e)
        {
            System.out.println("BadCredentialsException");
            String exceptionMessage = e.getCause().getMessage();
            System.out.println("error: " + exceptionMessage);

            e.printStackTrace();

            // set default to false since if the exception message contains data 531, it will be overriden
            authenticated = false;

            // if cause of the exception is error code 531, it means a workstation restriction
            // we just ignore it

            if(exceptionMessage.contains("data 531")) {
                authenticated = true;
            }

            if (authenticated == false) {
                returnMap.put("errorMessage", exceptionMessage);
            }
        } catch(Exception e) {

//            System.out.println("LDAP connection exception");
            System.out.println(e.getMessage());

            e.printStackTrace();

//            returnMap.put("authenticated", false);
            authenticated = false;
            returnMap.put("errorMessage", e.getMessage());

        }

        returnMap.put("username", userid);
        returnMap.put("authenticated", authenticated);
        returnMap.put("groups", adGroups);

        if(!adGroups.isEmpty()) {
            List roles = roleRepository.getRolesMatchingADGroups(adGroups);
            returnMap.put("roles", roles);
        }

        return returnMap;

    }

    @Override
    public Map authenticateLdap(String userid, String password) {
        System.out.println("authenticateLdap");

        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userid, password);

        Boolean authenticated = false;

        List<String> adGroups = new ArrayList<String>();
        String username = "";

        System.out.println("AD Authentication");

        Map<String, Object> returnMap = new HashMap<String, Object>();

        System.out.println("authenticating");

        try
        {
            Authentication authentication = adAuthenticationProvider.authenticate(userToken);

            List<GrantedAuthority> mappedAuthorities = (List<GrantedAuthority>) authentication.getAuthorities();

            if (mappedAuthorities.isEmpty()) {
                throw new Exception("User is not associated with any valid group in Active Directory.");
            }

            for (GrantedAuthority granted : mappedAuthorities) {
                System.out.println("granted authority " + granted.getAuthority());
                adGroups.add(granted.getAuthority());
            }

            authenticated = authentication.isAuthenticated();

            System.out.println("authenticated: " + authenticated.toString());
        }
        catch(BadCredentialsException e)
        {
            System.out.println("BadCredentialsException");
            String exceptionMessage = e.getCause().getMessage();
            System.out.println("error: " + exceptionMessage);

            e.printStackTrace();

            // set default to false since if the exception message contains data 531, it will be overriden
            authenticated = false;

            // if cause of the exception is error code 531, it means a workstation restriction
            // we just ignore it

            if(exceptionMessage.contains("data 531")) {
                authenticated = true;
            }
        } catch(Exception e) {

//            System.out.println("LDAP connection exception");
            System.out.println(e.getMessage());

            e.printStackTrace();

//            returnMap.put("authenticated", false);
            authenticated = false;
            returnMap.put("errorMessage", e.getMessage());

        }

        returnMap.put("username", userid);
        returnMap.put("authenticated", authenticated);
        returnMap.put("groups", adGroups);

        if(!adGroups.isEmpty()) {
            List roles = roleRepository.getRolesMatchingADGroups(adGroups);
            returnMap.put("roles", roles);
        }

        return returnMap;

    }
}
