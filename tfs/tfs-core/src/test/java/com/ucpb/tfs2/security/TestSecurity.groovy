package com.ucpb.tfs2.security

import com.ucpb.tfs.application.service.UserAuthenticationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.NamingException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

import javax.naming.Context
import javax.naming.NamingEnumeration
import javax.naming.directory.*

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:postlc-unitTestContext.xml")
@Transactional
class TestSecurity {

    @Autowired
    UserAuthenticationProvider authenticationProvider;

    @Autowired
    LdapDao ldapDao;

    @Test
    public void testAuthentication() {

        // println authenticationProvider.authenticate("branchm", "branchm");
        println authenticationProvider.authenticate("mcueto", "password\$1");

    }

    @Test
    public void testADSearch() {


        // adding custom attributes
        // http://virtualizesharepoint.com/2011/07/04/adding-custom-attributes-to-active-directory-user-profile/

        println ldapDao.getUserAttributes("mcueto");

    }


    @Test
    public void testSearch() {
        Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        //env.put(Context.PROVIDER_URL, "ldap://localhost:10389/ou=system");
        env.put(Context.PROVIDER_URL, "ldap://192.168.1.146");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
//        env.put(Context.SECURITY_PRINCIPAL, "uid=administrator,ou=system");
//        env.put(Context.SECURITY_PRINCIPAL, "uid=Administrator,ou=ucpb.com");
//        env.put(Context.SECURITY_CREDENTIALS, "password\$1");
        DirContext ctx = null;
        NamingEnumeration results = null;
        try {
            ctx = new InitialDirContext(env);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = ctx.search("", "(objectclass=person)", controls);
            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute attr = attributes.get("cn");
                String cn = (String) attr.get();
                System.out.println(" Person Common Name = " + cn);
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
