package com.ucpb.tfs.domain.audit;

import org.springframework.util.StringUtils;

import java.io.Serializable;

public class Beneficiary implements Serializable {
	
	private String name1;
	private String name2;
	private String name3;
	private String country;
	private Address address;
	

	public Beneficiary() {
	}


	public String getName1() {
		return name1;
	}


	public void setName1(String name1) {
        if (name1 != null) {
            if(trimWhitespaceAndNewLine(name1).length() > 100){
                String[] splitString = splitString(trimWhitespaceAndNewLine(name1));
                this.name1 = splitString[0];
                setName2(splitString[1]);
            } else {
                this.name1 = trimWhitespaceAndNewLine(name1);
            }
        }
	}


	public String getName2() {
		return name2;
	}


	public void setName2(String name2) {
        if (name2 != null) {
            if(trimWhitespaceAndNewLine(name2).length() > 100){
                String[] splitString = splitString(trimWhitespaceAndNewLine(name2));
                this.name2 = splitString[0];
                setName3(splitString[1]);
            } else {
                this.name2 = trimWhitespaceAndNewLine(name2);
            }
        }
	}


	public String getName3() {
		return name3;
	}


	public void setName3(String name3) {
		this.name3 = trimWhitespaceAndNewLine(trimWhitespaceAndNewLine(name3));
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = StringUtils.trimWhitespace(country);
	}


	public Address getAddress() {
		return address;
	}


	public void setAddress(Address address) {
		this.address = address;
	}

    private String trimWhitespaceAndNewLine(String sourceString){
        if(sourceString == null){
            return null;
        }
        return StringUtils.trimWhitespace(sourceString.replaceAll("\n"," "));
    }
    
    private String[] splitString(String string){
    	String[] _string = new String[2];
    	_string[0] = string.substring(0, 100);
    	_string[1] = string.substring(100, string.length());
    	return _string;
    }
}