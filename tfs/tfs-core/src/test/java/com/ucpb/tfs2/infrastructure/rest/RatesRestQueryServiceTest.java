package com.ucpb.tfs2.infrastructure.rest;

import com.ucpb.tfs.interfaces.services.RatesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class RatesRestQueryServiceTest {


    @MockitoAnnotations.Mock
    private RatesService ratesService;

    @InjectMocks
    private RatesRestQueryService ratesRestQueryService;


    @Before
    public void setup(){
        List returnedRates = new ArrayList<Map<String,Object>>();
        Map<String,Object> euroToUsdSell = new HashMap<String,Object>();
        euroToUsdSell.put("CURRENCY_CODE","EUR");
        euroToUsdSell.put("BASE_CURRENCY","USD");
        euroToUsdSell.put("RATE_NUMBER",2);
        euroToUsdSell.put("RATE_DEFINITION","REGULAR SOMETHING SOMETHING");
        euroToUsdSell.put("CONVERSION_RATE",new BigDecimal(123124));

        returnedRates.add(euroToUsdSell);

        when(ratesService.getDailyRates()).thenReturn(returnedRates);
    }


    @Test
    public void successfullyRetrieveRequiredCashLcRates(){

        UriInfo input = mock(UriInfo.class);
        MultivaluedMap<String,String> params = mock(MultivaluedMap.class);

        Set<String> keys = new HashSet<String>();
        keys.add("currency");
        when(params.keySet()).thenReturn(keys);
        when(params.getFirst("currency")).thenReturn("EUR");
        when(input.getQueryParameters()).thenReturn(params);

        Response response = ratesRestQueryService.getCashSellRates(input);

        System.out.println(response.getEntity().toString());
        assertTrue(response.getEntity().toString().contains("EUR-USD"));
        assertTrue(response.getEntity().toString().contains("REGULAR SOMETHING SOMETHING"));
    }




}
