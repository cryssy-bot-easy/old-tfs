package com.ucpb.tfs2.application.service;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsContextMapperImpl implements UserDetailsContextMapper, Serializable{

    private static final long serialVersionUID = 3962976258168853954L;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authority) {

        // http://stackoverflow.com/questions/8835818/handling-roles-when-authenticated-to-active-directory-with-spring-security-3-1
        List<GrantedAuthority> mappedAuthorities = new ArrayList<GrantedAuthority>();


        for (GrantedAuthority granted : authority) {

            System.out.println("authority: " + granted.getAuthority());

        }
        return new User(username, "", true, true, true, true, mappedAuthorities);
    }

    @Override
    public void mapUserToContext(UserDetails arg0, DirContextAdapter arg1) {
    }

}
