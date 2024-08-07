package com.ucpb.tfs2.security

import com.ucpb.tfs2.application.service.SecurityService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:postlc-unitTestContext.xml")
@Transactional
class testSecurityService {

    @Autowired
    SecurityService securityService

    @Test
    @Rollback(true)
    public void testSave() {

        Map userDetails = [
            userId : "jett",
            firstName : "Jett",
            lastName : "Gamboa",
            unitCode : "909",
            postingAuthority : "Y",
            postingLimit : "10000000"
        ]

        securityService.saveUserAndRoles(userDetails);

    }


}
