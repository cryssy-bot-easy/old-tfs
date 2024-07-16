package com.ucpb.tfs.interfaces.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ucpb.tfs.interfaces.domain.enums.RateType;

public interface RatesService {

	public List<Map<String,?>> getDailyRates();

    public List<Map<String,?>> getDailyRatesActual();

    public List<Map<String, ?>> getDailyURR();

	public List<Map<String,?>> getRatesByBaseCurrency(String baseCurrency);

    public List<Map<String,?>> getRatesByBaseCurrencyActual(String baseCurrency);

    public Map<String,?> getConversionRateByType(String sourceCurrency,String targetCurrency,RateType rateType);

    public List<Map<String,?>> getAllConversionRateByRateNumber(Date date,int rateType);

    public List<Map<String,?>> getAllConversionRateByRateNumberHistorical(Date date,int rateType);

    public Map<String,?> getUrrConversionRate(String sourceCurrency,String targetCurrency);

    public BigDecimal getConversionRate(String sourceCurrency,String targetCurrency,int rateType);

    public BigDecimal getConversionRateByType(String sourceCurrency,String targetCurrency,int rateType);

    public BigDecimal getConversionRateByTypeToday(String sourceCurrency,String targetCurrency,int rateType);

    public List<Map<String,?>> getAllUrr();

    public BigDecimal getUrrConversionRateToday();

    public BigDecimal getAngolConversionRate(String sourceCurrency,String targetCurrency,int rateType);

    public List<Map<String,?>> getAllCurrency();
}
