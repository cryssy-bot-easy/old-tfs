package com.ucpb.tfs.swift;

import org.springframework.util.Assert;

/**
 */
public class SwiftMessageUtil {

    private SwiftMessageUtil(){
        //do not instantiate
    }

    public static String convertToSwiftAmount(String currency, String amount){
        Assert.isTrue(amount.matches("[0-9]+([\\.][0-9]{1,2})?"));
        if(amount.contains(".")){
            amount.replace('.',',');
        }else{
            amount += ",";
        }
        return currency + amount;
    }
}
