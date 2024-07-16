/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ucpb.tfs.batch.job;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author raymu
 */
public class RerouteTradeServiceJobTest {
    
    public RerouteTradeServiceJobTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of execute method, of class RerouteTradeServiceJob.
     */
    @Test
    public void testExecute() {
        System.out.println("execute");
        String documentNumber = "";
        String targetUser = "";
        RerouteTradeServiceJob instance = null;
        instance.execute(documentNumber, targetUser);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
