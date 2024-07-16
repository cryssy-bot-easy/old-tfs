package com.ucpb.tfs.util;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.utils.AmlaLoggingUtil;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 */
public class AmlaLoggingUtilTest {


    @Test
    public void successfullyGetConvertedTransactionAmount(){
        TradeService ts = new TradeService();
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("currency","USD");
        details.put("amount","50.31");
        details.put("currentRate","47.2222333322223333232323232324231341314131313");
        ts.updateDetails(details, new UserActiveDirectoryId("someone"));

        BigDecimal computed = AmlaLoggingUtil.getTransactionAmount("PHP","50.31",new BigDecimal("47.2222333322223333232323232324231341314131313"));
        System.out.println("****** " + computed.toString());
        assertEquals(2,computed.toString().split("\\.")[1].length());
//        assertEquals(2,computed.precision());
    }

    @Test
    public void formatBigDecimalZero(){
        BigDecimal number = new BigDecimal("0.00000000");
        assertEquals("0.00",AmlaLoggingUtil.getAmountString(number,2));
    }

    @Test
    public void formatBigDecimalAmount(){
        BigDecimal number = new BigDecimal("0.5456431");
        assertEquals("0.55",AmlaLoggingUtil.getAmountString(number,2));
    }

    @Test
    public void returnZeroOnNull(){
        assertEquals("0.00",AmlaLoggingUtil.getAmountString(null,2));
    }

    @Test
    public void formatBigDecimalAmount2(){
        BigDecimal number = new BigDecimal("0.545643114674");
        assertEquals("0.54564311",AmlaLoggingUtil.getAmountString(number,8));

    }

    @Test
    public void setPrecisionTo8DecimalPlaces(){
        BigDecimal original = new BigDecimal("0.47246856000000");
        BigDecimal output = AmlaLoggingUtil.setPrecision(original,8);
        assertEquals(new BigDecimal("0.47246856"),output);
        assertEquals("0.47246856",output.toPlainString());

    }

    @Test
    public void bigDecimalFormattingTest(){
        BigDecimal number = new BigDecimal("0.00000000");
        assertEquals("0.00000000",number.setScale(8,BigDecimal.ROUND_HALF_UP).toPlainString());
    }




}
