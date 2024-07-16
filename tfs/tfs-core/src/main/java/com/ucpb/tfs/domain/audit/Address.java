package com.ucpb.tfs.domain.audit;

import org.springframework.util.StringUtils;

import java.io.Serializable;

public class Address implements Serializable {
	private String address1;
	private String address2;
	private String address3;
    private String address4;
    private String zipCode;

	public Address() {
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
        if (address1 != null) {
            if(trimWhitespaceAndNewLine(address1).length() > 100){
                String[] splitString = splitString(trimWhitespaceAndNewLine(address1));
                this.address1 = splitString[0];
                setAddress2(splitString[1]);
            } else {
                this.address1 = trimWhitespaceAndNewLine(address1);
            }
        }
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
        if (address2 != null) {
            if(trimWhitespaceAndNewLine(address2).length() > 100){
                String[] splitString = splitString(trimWhitespaceAndNewLine(address2));
                this.address2 = splitString[0];
                setAddress3(splitString[1]);
            } else {
                this.address2 = trimWhitespaceAndNewLine(address2);
            }
        }
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
        if (address3 != null) {
            if(trimWhitespaceAndNewLine(address3).length() > 100){
                String[] splitString = splitString(trimWhitespaceAndNewLine(address3));
                this.address3 = splitString[0];
                setAddress4(splitString[1]);
            } else {
                this.address3 = trimWhitespaceAndNewLine(address3);
            }
        }
	}

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = trimWhitespaceAndNewLine(address4);
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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