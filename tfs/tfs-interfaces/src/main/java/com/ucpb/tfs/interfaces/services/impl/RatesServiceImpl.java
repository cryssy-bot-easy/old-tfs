package com.ucpb.tfs.interfaces.services.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ucpb.tfs.interfaces.domain.enums.RateType;
import com.ucpb.tfs.interfaces.repositories.RatesRepository;
import com.ucpb.tfs.interfaces.services.RatesService;
import com.ucpb.tfs.interfaces.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.derby.client.am.SqlException;

public class RatesServiceImpl implements RatesService{

	private static final String DATE_FORMAT = "MMddyy";
	private RatesRepository ratesRepository;
	
	public List<Map<String, ?>> getDailyRates() {
//		return ratesRepository.getRates(DateUtil.formatToInt(DATE_FORMAT, new Date()));
		return ratesRepository.getRates();
	}

    public List<Map<String, ?>> getDailyURR() {
//        return ratesRepository.getRates(DateUtil.formatToInt(DATE_FORMAT, new Date()));
        return ratesRepository.getRates();
    }

    @Override
    public List<Map<String, ?>> getDailyRatesActual() {
//        return ratesRepository.getRatesActual(DateUtil.formatToInt(DATE_FORMAT, new Date()));
        return ratesRepository.getRatesActual();
    }

    public void setRatesRepository(RatesRepository ratesRepository) {
		this.ratesRepository = ratesRepository;
	}

	public List<Map<String, ?>> getRatesByBaseCurrency(String baseCurrency) {
		return ratesRepository.getRatesByBaseCurrency(DateUtil.formatToInt(DATE_FORMAT,new Date()), baseCurrency);
	}

    @Override
    public List<Map<String, ?>> getRatesByBaseCurrencyActual(String baseCurrency) {
        return ratesRepository.getRatesByBaseCurrencyActual(DateUtil.formatToInt(DATE_FORMAT,new Date()),baseCurrency);
    }

    public Map<String, ?> getConversionRateByType(String sourceCurrency,String targetCurrency, RateType rateType) {
        System.out.println("getConversionRateByType:"+sourceCurrency);
        System.out.println("targetCurrency:"+targetCurrency);
        System.out.println("rateType:"+rateType);
        System.out.println("DateUtil.formatToInt(DATE_FORMAT,new Date()):"+DateUtil.formatToInt(DATE_FORMAT,new Date()));
//        return ratesRepository.getConversionRateByType(append(sourceCurrency), append(targetCurrency), rateType, DateUtil.formatToInt(DATE_FORMAT,new Date()));
        return ratesRepository.getConversionRateByType(append(sourceCurrency), append(targetCurrency), rateType);
	}

    public List<Map<String, ?>> getAllConversionRateByRateNumber(Date date,int rateType) {
    	return ratesRepository.getAllConversionRateByRateNumber(DateUtil.formatToInt(DATE_FORMAT,date),rateType);
    }

    public List<Map<String, ?>> getAllConversionRateByRateNumberHistorical(Date date,int rateType) {
    	return ratesRepository.getAllConversionRateByRateNumberHistorical(DateUtil.formatToInt(DATE_FORMAT,date),rateType);
    }

    @Override
    public Map<String, ?> getUrrConversionRate(String sourceCurrency, String targetCurrency) {
//        return ratesRepository.getUrrConversionRate(append(sourceCurrency),append(targetCurrency),DateUtil.formatToInt(DATE_FORMAT, new Date()));
        return ratesRepository.getUrrConversionRate(append(sourceCurrency),append(targetCurrency));
    }

    @Override
    public BigDecimal getConversionRate(String sourceCurrency, String targetCurrency, int rateType) {
//        return ratesRepository.getConversionRate(append(sourceCurrency), append(targetCurrency), rateType, DateUtil.formatToInt(DATE_FORMAT,new Date()));
        return ratesRepository.getConversionRate(append(sourceCurrency), append(targetCurrency), rateType);
    }

    @Override
    public BigDecimal getConversionRateByType(String sourceCurrency, String targetCurrency, int rateType) {
        System.out.println("getConversionRateByType");
//        return ratesRepository.getConversionRate(append(sourceCurrency), append(targetCurrency), 83112,rateType);
        return ratesRepository.getConversionRate(append(sourceCurrency), append(targetCurrency), rateType);
    }

    @Override
    public BigDecimal getConversionRateByTypeToday(String sourceCurrency, String targetCurrency, int rateType) {
    	return ratesRepository.getConversionRate(append(sourceCurrency), append(targetCurrency), rateType);
//    	return ratesRepository.getConversionRate(append(sourceCurrency), append(targetCurrency), DateUtil.formatToInt(DATE_FORMAT, new Date()),rateType);
//    	return ratesRepository.getConversionRate(append(sourceCurrency), append(targetCurrency),83112 ,rateType);
    }

    private String append(String value){
        return "%" + StringUtils.trim(value) + "%";
    }


    public List<Map<String,?>> getAllUrr() {
        return ratesRepository.getAllUrr();
    }

    @Override
    public BigDecimal getUrrConversionRateToday() {
    	try{
        System.out.println("getConversionRateByType");
        return ratesRepository.getUrrConversionRateToday();
    	}catch(Exception e){
   		 e.printStackTrace();
   	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
           
   	}
    }

    @Override
    public BigDecimal getAngolConversionRate(String sourceCurrency, String targetCurrency, int rateType) {
    	try{
        System.out.println("getAngolConversionRate");
        return ratesRepository.getAngolConversionRate(sourceCurrency.trim(), targetCurrency.trim(),rateType);
    	}catch(Exception e){
    		 e.printStackTrace();
    	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
            
    	}
    }
    
    @Override
    public List<Map<String,?>> getAllCurrency(){
    	return ratesRepository.getAllCurrency();
    }

}
