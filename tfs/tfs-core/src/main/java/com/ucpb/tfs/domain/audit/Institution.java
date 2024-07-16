package com.ucpb.tfs.domain.audit;

import org.springframework.util.StringUtils;

import java.io.Serializable;

public class Institution implements Serializable {
	private String name;
	private String country;
    private Address address;

	public Institution() {
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
        if (name != null) {
            this.name = trimWhitespaceAndNewLine(name);
        }
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
        if (country != null) {
            this.country = trimWhitespaceAndNewLine(country);
        }
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
}