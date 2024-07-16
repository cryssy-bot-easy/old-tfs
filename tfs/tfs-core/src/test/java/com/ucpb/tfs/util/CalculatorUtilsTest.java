package com.ucpb.tfs.util;

import com.ucpb.tfs.utils.CalculatorUtils;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * User: giancarlo
 * Date: 2/20/13
 * Time: 4:58 PM
 */
public class CalculatorUtilsTest {


    @Test
    public void test_getBankCommission_FX_NON_LC_SETTLEMENT() {

        assertEquals(new BigDecimal("1926.25"),
                CalculatorUtils.getBankCommission_FX_NON_LC_SETTLEMENT(
                        new BigDecimal("1491000"),
                        new BigDecimal("1"),
                        new BigDecimal("8"),
                        new BigDecimal("0.01"),
                        "N",
                        new BigDecimal("0.98")
                )
        );
    }

    @Test
    public void test_getBankCommission_DM_NON_LC_SETTLEMENT() {

        assertEquals(
                new BigDecimal("1926.25"),

                CalculatorUtils.getBankCommission_DM_NON_LC_SETTLEMENT(
                        new BigDecimal("1491000"),
                        new BigDecimal("1"),
                        new BigDecimal("8"),
                        new BigDecimal("0.01"),
                        "N",
                        new BigDecimal("0.98")
                )
        );
    }



}
