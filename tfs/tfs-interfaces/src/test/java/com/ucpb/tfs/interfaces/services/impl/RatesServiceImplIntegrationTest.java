package com.ucpb.tfs.interfaces.services.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
@Ignore("Database must be running")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-local-integration-test.context.xml")
public class RatesServiceImplIntegrationTest {

    @Autowired
    private RatesServiceImpl ratesService;


    @Before
    public void setup(){
        assertNotNull("rates serivce not injected",ratesService);
    }

    @Test
    public void testGetUrrConversionRate(){
        Map<String,?> rates = ratesService.getUrrConversionRate("EUR","PHP");
        assertNotNull(rates);
        assertEquals("PHP", StringUtils.trim((String)rates.get("CURRENCY_CODE")));
    }
}
