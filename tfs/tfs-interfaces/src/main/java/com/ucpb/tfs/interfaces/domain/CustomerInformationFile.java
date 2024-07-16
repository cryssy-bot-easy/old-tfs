/**
 * PROLOGUE:
 * (revision)
 * SCR/ER Number:
 * SCR/ER Description: (Redmine #4134) Exporter CB Code field should be disabled and extracted from sibs LNSCOD base on CIF Number.
 * [Revised by:] Ludovico Anton Apilado
 * [Date revised:] 5/25/2017
 * Program [Revision] Details: added String cbCode and getter setter.
 * Date deployment: 6/16/2017
 * Member Type: Java
 * Project: Core
 * Project Name: CustomerInformationFile.java
 */

package com.ucpb.tfs.interfaces.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomerInformationFile {

    public static final String DATE_FORMAT = "yyyyDDD";
    private static final SimpleDateFormat SIBS_DATEFORMATER = new SimpleDateFormat(DATE_FORMAT);

	private String cifName;
	
	private String lastName;
	
	private String branchUnitCode;
	
	private String dorsiCode;
	
	private String cifNumber;
	
	private String firstName;
	
	private String middleName;
	
	private String tinNumber;
	
	private String residentCode;
	
	private int incorporationDate;
	
	private String individual;
	
	private String cbCode;
	
	public String getCbCode() {
		return cbCode;
	}

	public void setCbCode(String cbCode) {
		this.cbCode = cbCode;
	}

	public String getCifName() {
		return cifName;
	}

	public void setCifName(String cifName) {
		this.cifName = cifName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBranchUnitCode() {
		return branchUnitCode;
	}

	public void setBranchUnitCode(String branchUnitCode) {
		this.branchUnitCode = branchUnitCode;
	}

	public String getDorsiCode() {
		return dorsiCode;
	}

	public void setDorsiCode(String dorsiCode) {
		this.dorsiCode = dorsiCode;
	}

	public String getCifNumber() {
		return cifNumber;
	}

	public void setCifNumber(String cifNumber) {
		this.cifNumber = cifNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getTinNumber() {
		return tinNumber;
	}

	public void setTinNumber(String tinNumber) {
		this.tinNumber = tinNumber;
	}

	public String getResidentCode() {
		return residentCode;
	}

	public void setResidentCode(String residentCode) {
		this.residentCode = residentCode;
	}

	public int getIncorporationDate() {
		return incorporationDate;
	}

    public Date getFormattedIncorporationDate(){
        try {
            return SIBS_DATEFORMATER.parse(Integer.toString(incorporationDate));
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: " + incorporationDate + " using " + DATE_FORMAT + " format",e);
        }
    }

	public void setIncorporationDate(int incorporationDate) {
		this.incorporationDate = incorporationDate;
	}

	public String getIndividual() {
		return individual;
	}

	public void setIndividual(String individual) {
		this.individual = individual;
	}

}
