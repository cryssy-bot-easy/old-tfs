package com.ucpb.tfs.interfaces.gateway;

import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;

/**
 */
public class FinRequestTest {

    @Test
    public void successfullyFormatCasaString(){
        FinRequest request = new FinRequest();
        request.setUsername("TFSCASA");
        request.setPassword("ipc123$");
        request.setTransactionCode(TransactionCode.DEBIT_TO_SAVINGS);
        request.setUserId("0001");
        request.setAccountNumber("202400000144");
        request.setBranchCode("909");
        request.setAmount(new BigDecimal("1"));
        assertEquals(66,request.doBuild().length());
        assertEquals("TFSCASA ipc123$ 90900012610202400000144 00000000000100            ",request.doBuild());
    }

    @Test
    public void successfullyFormatFcduCasaString(){
        FinRequest request = new FinRequest();
        request.setUsername("TFSCASA");
        request.setPassword("ipc123$");
        request.setTransactionCode(TransactionCode.DEBIT_TO_FOREIGN);
        request.setUserId("0001");
        request.setAccountNumber("182400000014");
        request.setBranchCode("909");
        request.setAmount(new BigDecimal("1"));
        assertEquals(66,request.doBuild().length());
        System.out.println(request.doBuild());
        assertEquals("TFSCASA ipc123$ 90900013610182400000014 00000000000100            ",request.doBuild());
    }


}
