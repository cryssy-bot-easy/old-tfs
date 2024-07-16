package com.ucpb.tfs.application.query.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface RatesService {

	public List<Map<String,?>> getDailyRates();
	
	public List<Map<String,?>> getRatesByBaseCurrency(String baseCurrency);

    public List<Map<String, ?>> getDailyURR();

    public BigDecimal getConversionRateByType(String sourceCurrency, String targetCurrency, RateType rateType);

    public BigDecimal getConversionRate(String sourceCurrency, String targetCurrency, RateType rateType);

    public List<Map<String,?>> getDailyRatesActual();

    public List<Map<String,?>> getRatesByBaseCurrencyActual(String baseCurrency);

    public Map<String,?> getConversionRateByTypeActual(String sourceCurrency, String targetCurrency, RateType rateType);

    public Map<String, ?> getUrrConversionRate(String sourceCurrency, String targetCurrency);

    public BigDecimal getUrrConversionRateToday();

}
