package com.ucpb.tfs.domain.sysparams;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;
import java.util.Map;


/**
 * User: IPCVal
 * Date: 1/18/13
 */

/*
 * 	PROLOGUE:
	revision
	SCR/ER Number: ER#: 20151014-052
	SCR/ER Description: Pick list for country code provides upper case only
	[Created by:] Jesse James Joson
	[Date revised:] 11/10/2015
	Program [Revision] Details: Always saved in database upper case.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
*/


public class RefCountry implements Serializable {

   private String countryCode;
   private String countryName;
   private String countryISO;

    public RefCountry() {
        this.countryCode = null;
        this.countryName = null;
        this.countryISO = null;
    }

    public RefCountry(String countryCode, String countryName, String countryISO) {
    	this.countryCode = countryCode.toUpperCase();
        this.countryName = countryName.toUpperCase();
        this.countryISO = countryISO.toUpperCase();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode.toUpperCase();
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName.toUpperCase();
    }

    public String getCountryISO() {
        return countryISO;
    }

    public void setCountryISO(String countryISO) {
        this.countryISO = countryISO.toUpperCase();
    }

    public void checkif(String countryISO) {
        this.countryISO = countryISO;
    }

    public RefCountry(Map parameters) {

    	this.countryCode = parameters.get("countryCode").toString().toUpperCase();
        this.countryName = parameters.get("countryName").toString().toUpperCase();
        this.countryISO = parameters.get("countryISO").toString().toUpperCase();

    }
    public void updateDetails(Map parameters) {

    	this.countryCode = parameters.get("countryCode").toString().toUpperCase();
        this.countryName = parameters.get("countryName").toString().toUpperCase();
        this.countryISO = parameters.get("countryISO").toString().toUpperCase();
    }
}
