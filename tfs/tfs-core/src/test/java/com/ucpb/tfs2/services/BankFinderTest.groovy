package com.ucpb.tfs2.services

import com.ucpb.tfs.application.query2.ReferenceFinder
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:postlc-unitTestContext.xml")
@Transactional
class BankFinderTest {

    @Autowired
    ReferenceFinder referenceFinder;

    @Test
    public void testDepositoryBank() {

        List x = referenceFinder.findDepositoryBanksByKeyword("W");

        x.each() { item ->
            println item
        }

    }

}
