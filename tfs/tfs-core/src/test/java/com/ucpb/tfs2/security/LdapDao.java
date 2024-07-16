package com.ucpb.tfs2.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

import javax.naming.directory.Attributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Component
public class LdapDao {

    LdapTemplate template;

    @Autowired
    public LdapDao(LdapContextSource contextSource) {
        template = new LdapTemplate(contextSource);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getUserAttributes(String username) {
        Map<String, String> results = new HashMap<String, String>();

        String objectClass = "samAccountName=" + username;
        template.setIgnorePartialResultException(true);
        LinkedList<Map<String, String>> list = (LinkedList<Map<String, String>>) template.search("", objectClass, new UserAttributesMapper());


        if (!list.isEmpty()) {
            // Should only return one item
            results = list.get(0);
        }
        return results;
    }

    private class UserAttributesMapper implements AttributesMapper {

        @Override
        public Map<String, String> mapFromAttributes(Attributes attributes) throws javax.naming.NamingException {
            Map<String, String> map = new HashMap<String, String>();

            String fullname = (String) attributes.get("displayName").get();
//            String email = (String) attributes.get("mail").get();
//            String title = (String) attributes.get("title").get();

            map.put("fullname", fullname);
//            map.put("email", email);
//            map.put("title", title);
            return map;
        }
    }
}