package com.ucpb.tfs.domain.audit;

import org.springframework.util.StringUtils;

import java.io.Serializable;

public class Bank implements Serializable {
	
	private String name;
	private String countryCode;
	private Address address;

	public Bank() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
        if (name != null) {
            this.name = StringUtils.trimWhitespace(name);
        }
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
        if (countryCode != null) {
            this.countryCode = StringUtils.trimWhitespace(countryCode);
        }
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}