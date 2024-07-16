package com.ucpb.tfs.interfaces.domain.enums;

/**
 */
public enum Currency {

    PHP("PHP","0"),USD("USD","1");

    private String currencyCode;

    private String casaCurrencyCode;


    private Currency(String currencyCode,String casaCurrencyCode){
        this.currencyCode = currencyCode;
        this.casaCurrencyCode = casaCurrencyCode;
    }

    public static Currency getCurrency(String casaCurrencyCode){
        for(Currency currency : Currency.values()){
            if(currency.getCasaCurrencyCode().equalsIgnoreCase(casaCurrencyCode)){
                return currency;
            }
        }
        throw new IllegalArgumentException("Casa currency code: " + casaCurrencyCode + " does not exist");
    }

    public static String getCasaCurrencyCode(String isoCurrencyCode){
        for(Currency currency : Currency.values()){
            if(currency.getCurrencyCode().equalsIgnoreCase(isoCurrencyCode)){
                return currency.getCasaCurrencyCode();
            }
        }
        return null;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCasaCurrencyCode() {
        return casaCurrencyCode;
    }
}
