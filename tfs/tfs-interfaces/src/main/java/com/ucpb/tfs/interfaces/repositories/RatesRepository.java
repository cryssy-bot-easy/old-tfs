package com.ucpb.tfs.interfaces.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ucpb.tfs.interfaces.domain.enums.RateType;

public interface RatesRepository {

//	public List<Map<String,?>> getRates(@Param("date")int date);
	public List<Map<String,?>> getRates();

    public List<Map<String,?>> getRatesURR(@Param("date")int date);

    public List<Map<String,?>> getRatesActual();

    public List<Map<String,?>> getRatesByBaseCurrency(@Param("date")int date,@Param("baseCurrency")String baseCurrency);

    public List<Map<String,?>> getRatesByBaseCurrencyActual(@Param("date")int date,@Param("baseCurrency")String baseCurrency);

    public BigDecimal getConversionRate(@Param("sourceCurrency")String sourceCurrency, @Param("targetCurrency")String targetCurrency, @Param("rateNumber")int rateNumber);
//    public BigDecimal getConversionRate(@Param("sourceCurrency")String sourceCurrency, @Param("targetCurrency")String targetCurrency,@Param("date")int date, @Param("rateNumber")int rateNumber);

    public Map<String,?> getConversionRateByType(@Param("sourceCurrency")String sourceCurrency,@Param("targetCurrency")String targetCurrency,@Param("type")RateType type);
//    public Map<String,?> getConversionRateByType(@Param("sourceCurrency")String sourceCurrency,@Param("targetCurrency")String targetCurrency,@Param("type")RateType type,@Param("date")int date);
//    public Map<String,Object> getConversionRateByType(@Param("sourceCurrency")String sourceCurrency,@Param("targetCurrency")String targetCurrency,@Param("type")RateType type,@Param("date")int date, @Param("rateNumber") int rateNumber);

    public List<Map<String,?>> getAllConversionRateByRateNumber(@Param("date")int date, @Param("rateNumber") int rateNumber);
    
    public List<Map<String,?>> getAllConversionRateByRateNumberHistorical(@Param("date")int date, @Param("rateNumber") int rateNumber);

//    public Map<String,Object> getUrrConversionRate(@Param("sourceCurrency")String sourceCurrency,@Param("targetCurrency")String targetCurrency,@Param("date")int date);
    public Map<String,Object> getUrrConversionRate(@Param("sourceCurrency")String sourceCurrency,@Param("targetCurrency")String targetCurrency);

    public List<Map<String,?>> getAllUrr();

    public BigDecimal getUrrConversionRateToday();

    public BigDecimal getAngolConversionRate(@Param("sourceCurrency")String sourceCurrency, @Param("targetCurrency")String targetCurrency,@Param("rateNumber")int rateNumber);
    
    public List<Map<String,?>> getAllCurrency();
}
