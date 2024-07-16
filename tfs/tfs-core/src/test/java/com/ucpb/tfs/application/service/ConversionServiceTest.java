package com.ucpb.tfs.application.service;

import com.ucpb.tfs.interfaces.services.RatesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.internal.util.Assert;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

import static org.mockito.Mockito.mock;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class ConversionServiceTest {

    @InjectMocks
    private ConversionService conversionService;

    @Mock
    private RatesService ratesService;

    @Before
    public void verifyInjection(){
        assertNotNull(ratesService);
    }

    @Before
    public void setup(){
        when(ratesService.getUrrConversionRate("USD","PHP")).thenReturn(wrapInResultMap(ConversionService.CONVERSION_RATE,new BigDecimal("41.432585")));
        when(ratesService.getUrrConversionRate("EUR","USD")).thenReturn(wrapInResultMap(ConversionService.CONVERSION_RATE,new BigDecimal("1.295499")));

        when(ratesService.getUrrConversionRate("JPY","PHP")).thenReturn(wrapInResultMap(ConversionService.CONVERSION_RATE,new BigDecimal("0.528510")));
        when(ratesService.getUrrConversionRate("JPY","USD")).thenReturn(wrapInResultMap(ConversionService.CONVERSION_RATE,new BigDecimal("0.012756")));

    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionWhenSourceCurrencyIsPhp(){
        conversionService.getPhpConversionRate("PHP");
    }

    @Test
    public void covertEuroToPhp(){
        BigDecimal amount = conversionService.convertToPhpUsingUrr(Currency.getInstance("EUR"),new BigDecimal("124.78"));
        assertEquals(new BigDecimal("6697.67536242869370"),amount);
    }

    @Test
    public void convertUsdToPhp(){
        BigDecimal amount = conversionService.convertToPhpUsingUrr(Currency.getInstance("USD"),new BigDecimal("124.78"));
        assertEquals(new BigDecimal("5169.95795630"),amount);
    }

    @Test
    public void successfullyGetUsdRate(){
        BigDecimal rate = conversionService.getPhpConversionRate("USD");
        assertEquals(new BigDecimal("41.432585"),rate);
    }

    @Test
    public void successfullyGetConversionRateFromThirdCurrency(){
        BigDecimal rate = conversionService.getPhpConversionRate("EUR");
        //41.432585 * 1.295499
        assertEquals(new BigDecimal("53.675872434915"),rate);
    }

    @Test
    public void useDirectConversionIfThirdToPhpRateIsPresent(){
        //JPY to php has direct conversion but also JPY to USD
        //service must return
        assertEquals(new BigDecimal("0.528510"),conversionService.getPhpConversionRate("JPY"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionWhenPassingANullConversionRate(){
        conversionService.getPhpConversionRate(null);
    }

    @Test
    public void returnNullWhenQueryingForNonExistingCurrency(){
        assertNull(conversionService.getPhpConversionRate("SHADALOODOLLARS"));
    }


    private Map wrapInResultMap(Object... data){
        Assert.notNull(data);
        Assert.isTrue(data.length % 2 == 0);
        Map<String,Object> resultMap = new HashMap<String,Object>();

        for(int ctr = 0; ctr < data.length; ctr = ctr + 2){
            resultMap.put((String)data[ctr],data[ctr+1]);
        }
        return resultMap;
    }


}
