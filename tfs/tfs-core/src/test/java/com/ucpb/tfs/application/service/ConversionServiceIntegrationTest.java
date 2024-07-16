package com.ucpb.tfs.application.service;

import com.ucpb.tfs.interfaces.services.RatesService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 */
//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:interfaces-context.xml")
public class ConversionServiceIntegrationTest {

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private RatesService ratesService;


    @Test
    public void getRates(){
        List<Map<String,?>> rates = ratesService.getDailyRates();
        assertNotNull(rates);
        assertTrue(rates.size() > 0);
    }

    @Test
    public void getUrrConversionRateForHkdToUsd(){
        Map<String,?> rate = ratesService.getUrrConversionRate("HKD","USD");
        assertNull(rate);
    }

    @Test
    public void successfullyGetHkdToPhpConversionRate(){
        BigDecimal rate =  conversionService.getPhpConversionRate("HKD");
        assertNotNull(rate);
        assertTrue(rate.compareTo(BigDecimal.ZERO) > 0);
    }

}
