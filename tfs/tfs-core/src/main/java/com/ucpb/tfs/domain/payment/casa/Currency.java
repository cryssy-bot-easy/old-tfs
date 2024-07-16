package com.ucpb.tfs.domain.payment.casa;

/**
 */
public enum Currency {

    USD("1","USD"),PHP("0","PHP"),JPY("2","JPY"),SGD("8","SGD");

   private String name;

   private String code;

   private Currency(String code,String name){
    this.code = code;
    this.name = name;
   }

    public String getCode() {
        return code;
    }

    public String getName(){
        return name;
    }

    public static Currency getCurrencyByCode(String code){
        for(Currency currency : Currency.values()){
            if(currency.getCode().equals(code)){
                System.out.println("currencyCode:"+currency);
                return currency;
            }
        }
        return null;
    }

}
