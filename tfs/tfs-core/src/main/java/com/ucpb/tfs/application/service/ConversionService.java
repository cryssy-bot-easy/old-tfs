package com.ucpb.tfs.application.service;

import com.ucpb.tfs.interfaces.services.RatesService;
import com.ucpb.tfs.interfaces.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Map;

/**
 */
public class ConversionService {

    private static final String PHP = "PHP";
    private static final String USD = "USD";
    private static final Currency USD_CURRENCY = Currency.getInstance("USD");
    private static final Currency PHP_CURRENCY = Currency.getInstance("PHP");
    public static final String CONVERSION_RATE = "CONVERSION_RATE";

    @Autowired
    private RatesService ratesService;

    public BigDecimal getPhpConversionRate(String sourceCurrency){
        Assert.notNull(sourceCurrency, "Source currency must not be null");
        Assert.isTrue(!PHP.equals(sourceCurrency),"Source currency is already in PHP");

        BigDecimal directToPhp = getConversionRate(sourceCurrency,PHP);
        if(directToPhp != null){
            return directToPhp;
        }
        BigDecimal thirdToUsd = getConversionRate(sourceCurrency,USD);
        BigDecimal usdToPhp = getConversionRate(USD,PHP);
        if(thirdToUsd == null || usdToPhp == null){
            return null;
        }

        return thirdToUsd.multiply(usdToPhp);
    }


    public BigDecimal convertToPhpUsingUrr(Currency sourceCurrency, BigDecimal amount){
        Assert.notNull(sourceCurrency,"Source currency must not be null.");
        Assert.notNull(amount,"Base amount must not be null.");
        Assert.isTrue(!PHP_CURRENCY.equals(sourceCurrency),"Source currency must not be equal to PHP");


        BigDecimal conversionRate = getConversionRate(USD,PHP);
        if(!USD_CURRENCY.equals(sourceCurrency)){
            conversionRate = conversionRate.multiply(getConversionRate(sourceCurrency.toString(),USD));
        }
        return amount.multiply(conversionRate);
    }

    public void setRatesService(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    private BigDecimal getConversionRate(String base,String target){
        Map<String,?> rates = ratesService.getUrrConversionRate(base,target);
        if(rates != null){
            return (BigDecimal)rates.get(CONVERSION_RATE);
        }
        return null;
    }



}
