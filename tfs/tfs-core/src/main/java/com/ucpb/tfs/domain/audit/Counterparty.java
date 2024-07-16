package com.ucpb.tfs.domain.audit;

import org.springframework.util.StringUtils;

import java.io.Serializable;

public class Counterparty implements Serializable {
	private String accountNo;
	private String name1;
	private String name2;
	private String name3;
	private Institution institution;
    private Address address;
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
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
        if (name3 != null) {
            this.name3 = trimWhitespaceAndNewLine(name3);
        }
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
	public Institution getInstitution() {
		return institution;
	}
	public void setInstitution(Institution institution) {
		this.institution = institution;
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