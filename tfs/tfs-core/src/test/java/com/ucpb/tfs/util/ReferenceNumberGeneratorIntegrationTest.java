package com.ucpb.tfs.util;

import com.ucpb.tfs.domain.service.utils.ReferenceNumberGenerator;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import java.sql.SQLException;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
public class ReferenceNumberGeneratorIntegrationTest {

    private ReferenceNumberGenerator generator;

    @Autowired
    private DataSource dataSource;

    private SessionImplementor sessionImplementor;

    @Before
    public void setup() throws SQLException {
        sessionImplementor = mock(SessionImplementor.class);
        when(sessionImplementor.connection()).thenReturn(dataSource.getConnection());

        generator = new ReferenceNumberGenerator();
    }



    @Test
    public void successfullyGenerateReferenceNumber(){
        String referenceNumber = (String) generator.generate(sessionImplementor, new Object());
        System.out.println("****** REF NUMBER: " + referenceNumber);
        assertTrue(referenceNumber.matches("TF[\\d]{14}"));
    }

}
