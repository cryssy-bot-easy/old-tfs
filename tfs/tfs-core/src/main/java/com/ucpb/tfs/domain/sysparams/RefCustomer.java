package com.ucpb.tfs.domain.sysparams;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/18/13
 */
public class RefCustomer implements Serializable {

    private Long customerId;

    private String customerType;

    private String centralBankCode;

    private String clientCifNumber;

    private String clientShortName; // address for corporate
    private String clientAddress1;
    private String clientAddress2;
    private String clientAddress3;
    private String clientAddress4;
    private String clientZipCode;

    private String homeAddress1;
    private String homeAddress2;
    private String homeAddress3;
    private String homeAddress4;
    private String homeZipCode;

    private String residencePhoneNumber;
    private String officePhoneNumber;
    private String mobileNumber;

    private String occupation;
    private String natureOfBusiness;
    private String natureOfWork;
    private String natureOfSelfEmployment;

    private String sourceOfFunds;
    private BigDecimal annualIncome;
    private BigDecimal monthlyIncome;
    private BigDecimal financialStatus;


    private BigInteger accountType;
    private BigInteger accountOfficerCode;

    private BigDecimal monthToDateExportAmount;
    private BigDecimal yearToDateExportAmount;

    private BigDecimal exportAdvanceBalance;
    private BigDecimal redClauseAdvanceBalance;

    private String evatFlag;

    private String residentClassification;

    private String clientLongName;
    private BigInteger clientType;
    private Date clientBirthday; // date of birth // date of incorporation
    private BigInteger clientNumber;

    private String clientTaxAccountNumber; // tax number = tin number
//    private String ucpbCifNumber;

    private String cifShortName; // shortname
    private String cifLongName; // firstname
    private String cifLongNameA; // middlename
    private String cifLongNameB; // last name

    private String gender;
    private String placeOfBirth;
    private String maritalStatus;
    private String nationality;
    private String nationOfBirth; // country for corporate

    private String ucpbCifNumber;

    private Date dateCreated;

    public RefCustomer() {
        this.dateCreated = new Date();
    }
    
    public RefCustomer(Map parameters) {
        this.dateCreated = new Date();
        this.centralBankCode = (String)parameters.get("centralBankCode");
        this.clientCifNumber = (String)parameters.get("cifNumber");
        this.clientAddress1 = (String)parameters.get("clientAddress1");
        this.clientAddress2 = (String)parameters.get("clientAddress2");
        this.clientAddress3 = (String)parameters.get("clientAddress3");
        this.clientAddress4 = (String)parameters.get("clientAddress4");
        this.clientZipCode = (String)parameters.get("clientZipCode");
        this.residencePhoneNumber = (String)parameters.get("residencePhoneNumber");
        this.officePhoneNumber = (String) parameters.get("officePhoneNumber");
        this.mobileNumber = (String)parameters.get("mobileNumber");

        this.occupation = (String)parameters.get("occupation");
        this.natureOfBusiness = (String)parameters.get("natureOfBusiness");
        this.natureOfWork = (String)parameters.get("natureOfWork");
        this.natureOfSelfEmployment = (String)parameters.get("natureOfSelfEmployment");

        this.sourceOfFunds = (String)parameters.get("sourceOfFunds");

        if (parameters.get("annualIncome") != null && !((String)parameters.get("annualIncome")).isEmpty()) {
            this.annualIncome = new BigDecimal(parameters.get("annualIncome").toString().replaceAll(",",""));
        }

        if (parameters.get("monthlyIncome") != null && !((String)parameters.get("monthlyIncome")).isEmpty()) {
            this.monthlyIncome = new BigDecimal(parameters.get("monthlyIncome").toString().replaceAll(",",""));
        }

        if (parameters.get("financialStatus") != null && !((String)parameters.get("financialStatus")).isEmpty()) {
            this.financialStatus = new BigDecimal(parameters.get("financialStatus").toString().replaceAll(",",""));
        }

        if (parameters.get("accountType") != null && !((String)parameters.get("accountType")).isEmpty()) {
            this.accountType = new BigInteger((String)parameters.get("accountType"));
        }

        if (parameters.get("accountOfficerCode") != null && !((String)parameters.get("accountOfficerCode")).isEmpty()) {
            this.accountOfficerCode = new BigInteger((String)parameters.get("accountOfficerCode"));
        }

        if (parameters.get("exportAmountMonthToDate") != null && !((String)parameters.get("exportAmountMonthToDate")).isEmpty()) {
            this.monthToDateExportAmount = new BigDecimal(parameters.get("exportAmountMonthToDate").toString().replaceAll(",",""));
        }

        if (parameters.get("exportAmountYearToDate") != null && !((String)parameters.get("exportAmountYearToDate")).isEmpty()) {
            this.yearToDateExportAmount = new BigDecimal(parameters.get("exportAmountYearToDate").toString().replaceAll(",",""));
        }

        if (parameters.get("exportAdvanceBalance") != null && !((String)parameters.get("exportAdvanceBalance")).isEmpty()) {
            this.exportAdvanceBalance = new BigDecimal(parameters.get("exportAdvanceBalance").toString().replaceAll(",",""));
        }

        if (parameters.get("redClauseAdvanceBalance") != null && !((String)parameters.get("redClauseAdvanceBalance")).isEmpty()) {
            this.redClauseAdvanceBalance = new BigDecimal(parameters.get("redClauseAdvanceBalance").toString().replaceAll(",",""));
        }

        this.evatFlag = (String)parameters.get("evatFlag");

        this.residentClassification = (String)parameters.get("residentClassification");

        this.clientType = new BigInteger((String)parameters.get("clientType"));

        if (parameters.get("clientType").toString().equals("1")) {

            this.customerType = "302";

            this.cifShortName = (String)parameters.get("shortName");
            this.cifLongName = (String)parameters.get("firstName");
            this.cifLongNameA = (String)parameters.get("middleName");
            this.cifLongNameB = (String)parameters.get("lastName");

            this.gender = (String)parameters.get("gender");
            this.placeOfBirth = (String)parameters.get("placeOfBirth");
            this.maritalStatus = (String)parameters.get("maritalStatus");
            this.nationality = (String)parameters.get("nationality");
            this.nationOfBirth = (String)parameters.get("nationOfBirth");

            this.homeAddress1 = (String)parameters.get("homeAddress1");
            this.homeAddress2 = (String)parameters.get("homeAddress2");
            this.homeAddress3 = (String)parameters.get("homeAddress3");
            this.homeAddress4 = (String)parameters.get("homeAddress4");
            this.homeZipCode = (String)parameters.get("homeZipCode");

            this.clientLongName = (String)parameters.get("clientLongName");

        } else {

            this.customerType = "400";

            this.cifLongNameB = (String)parameters.get("nameOfCorporation");

            this.nationOfBirth = (String)parameters.get("country");
        }

        this.clientTaxAccountNumber = (String)parameters.get("taxAccountNumber");

        if (parameters.get("clientBirthday") != null) {
            this.clientBirthday = (java.util.Date)parameters.get("clientBirthday");
        }
    }
    public void deleteDetails(Map parameters){
        this.centralBankCode = (String)parameters.get("centralBankCode");
    }

    public void updateDetails(Map parameters) {

        this.centralBankCode = (String)parameters.get("centralBankCode");

        this.clientCifNumber = (String)parameters.get("cifNumber");

        this.clientAddress1 = (String)parameters.get("clientAddress1");
        this.clientAddress2 = (String)parameters.get("clientAddress2");
        this.clientAddress3 = (String)parameters.get("clientAddress3");
        this.clientAddress4 = (String)parameters.get("clientAddress4");
        this.clientZipCode = (String)parameters.get("clientZipCode");

        this.residencePhoneNumber = (String)parameters.get("residencePhoneNumber");
        this.officePhoneNumber = (String) parameters.get("officePhoneNumber");
        this.mobileNumber = (String)parameters.get("mobileNumber");

        this.occupation = (String)parameters.get("occupation");
        this.natureOfBusiness = (String)parameters.get("natureOfBusiness");
        this.natureOfWork = (String)parameters.get("natureOfWork");
        this.natureOfSelfEmployment = (String)parameters.get("natureOfSelfEmployment");

        this.sourceOfFunds = (String)parameters.get("sourceOfFunds");

        if (parameters.get("annualIncome") != null && !((String)parameters.get("annualIncome")).isEmpty()) {
            this.annualIncome = new BigDecimal(parameters.get("annualIncome").toString().replaceAll(",",""));
        }

        if (parameters.get("monthlyIncome") != null && !((String)parameters.get("monthlyIncome")).isEmpty()) {
            this.monthlyIncome = new BigDecimal(parameters.get("monthlyIncome").toString().replaceAll(",",""));
        }

        if (parameters.get("financialStatus") != null && !((String)parameters.get("financialStatus")).isEmpty()) {
            this.financialStatus = new BigDecimal(parameters.get("financialStatus").toString().replaceAll(",",""));
        }

        if (parameters.get("accountType") != null && !((String)parameters.get("accountType")).isEmpty()) {
            this.accountType = new BigInteger((String)parameters.get("accountType"));
        }

        if (parameters.get("accountOfficerCode") != null && !((String)parameters.get("accountOfficerCode")).isEmpty()) {
            this.accountOfficerCode = new BigInteger((String)parameters.get("accountOfficerCode"));
        }

        if (parameters.get("exportAmountMonthToDate") != null && !((String)parameters.get("exportAmountMonthToDate")).isEmpty()) {
            this.monthToDateExportAmount = new BigDecimal(parameters.get("exportAmountMonthToDate").toString().replaceAll(",",""));
        }

        if (parameters.get("exportAmountYearToDate") != null && !((String)parameters.get("exportAmountYearToDate")).isEmpty()) {
            this.yearToDateExportAmount = new BigDecimal(parameters.get("exportAmountYearToDate").toString().replaceAll(",",""));
        }

        if (parameters.get("exportAdvanceBalance") != null && !((String)parameters.get("exportAdvanceBalance")).isEmpty()) {
            this.exportAdvanceBalance = new BigDecimal(parameters.get("exportAdvanceBalance").toString().replaceAll(",",""));
        }

        if (parameters.get("redClauseAdvanceBalance") != null && !((String)parameters.get("redClauseAdvanceBalance")).isEmpty()) {
            this.redClauseAdvanceBalance = new BigDecimal(parameters.get("redClauseAdvanceBalance").toString().replaceAll(",",""));
        }

        this.evatFlag = (String)parameters.get("evatFlag");

        this.residentClassification = (String)parameters.get("residentClassification");

        this.clientType = new BigInteger((String)parameters.get("clientType"));

        if (parameters.get("clientType").toString().equals("1")) {

            this.customerType = "302";

            this.cifShortName = (String)parameters.get("shortName");
            this.cifLongName = (String)parameters.get("firstName");
            this.cifLongNameA = (String)parameters.get("middleName");
            this.cifLongNameB = (String)parameters.get("lastName");

            this.gender = (String)parameters.get("gender");
            this.placeOfBirth = (String)parameters.get("placeOfBirth");
            this.maritalStatus = (String)parameters.get("maritalStatus");
            this.nationality = (String)parameters.get("nationality");
            this.nationOfBirth = (String)parameters.get("nationOfBirth");

            this.homeAddress1 = (String)parameters.get("homeAddress1");
            this.homeAddress2 = (String)parameters.get("homeAddress2");
            this.homeAddress3 = (String)parameters.get("homeAddress3");
            this.homeAddress4 = (String)parameters.get("homeAddress4");
            this.homeZipCode = (String)parameters.get("homeZipCode");

            this.clientLongName = (String)parameters.get("clientLongName");

        } else {

            this.customerType = "400";

            this.cifLongNameB = (String)parameters.get("nameOfCorporation");

            this.nationOfBirth = (String)parameters.get("country");
        }

        this.clientTaxAccountNumber = (String)parameters.get("taxAccountNumber");

        if (parameters.get("clientBirthday") != null) {
            this.clientBirthday = (java.util.Date)parameters.get("clientBirthday");
        }
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getCentralBankCode() {
        return centralBankCode;
    }

    public String getClientCifNumber() {
        return clientCifNumber;
    }

    public String getClientShortName() {
        return clientShortName;
    }

    public String getClientAddress1() {
        return clientAddress1;
    }

    public String getClientAddress2() {
        return clientAddress2;
    }

    public String getClientAddress3() {
        return clientAddress3;
    }

    public String getClientAddress4() {
        return clientAddress4;
    }

    public String getClientZipCode() {
        return clientZipCode;
    }

    public String getHomeAddress1() {
        return homeAddress1;
    }

    public String getHomeAddress2() {
        return homeAddress2;
    }

    public String getHomeAddress3() {
        return homeAddress3;
    }

    public String getHomeAddress4() {
        return homeAddress4;
    }

    public String getHomeZipCode() {
        return homeZipCode;
    }

    public String getResidencePhoneNumber() {
        return residencePhoneNumber;
    }

    public String getOfficePhoneNumber() {
        return officePhoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getNatureOfBusiness() {
        return natureOfBusiness;
    }

    public String getNatureOfWork() {
        return natureOfWork;
    }

    public String getNatureOfSelfEmployment() {
        return natureOfSelfEmployment;
    }

    public String getSourceOfFunds() {
        return sourceOfFunds;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public BigDecimal getFinancialStatus() {
        return financialStatus;
    }

    public BigInteger getAccountType() {
        return accountType;
    }

    public BigInteger getAccountOfficerCode() {
        return accountOfficerCode;
    }

    public BigDecimal getMonthToDateExportAmount() {
        return monthToDateExportAmount;
    }

    public BigDecimal getYearToDateExportAmount() {
        return yearToDateExportAmount;
    }

    public BigDecimal getExportAdvanceBalance() {
        return exportAdvanceBalance;
    }

    public BigDecimal getRedClauseAdvanceBalance() {
        return redClauseAdvanceBalance;
    }

    public String getEvatFlag() {
        return evatFlag;
    }

    public String getResidentClassification() {
        return residentClassification;
    }

    public String getClientLongName() {
        return clientLongName;
    }

    public BigInteger getClientType() {
        return clientType;
    }

    public Date getClientBirthday() {
        return clientBirthday;
    }

    public BigInteger getClientNumber() {
        return clientNumber;
    }

    public String getClientTaxAccountNumber() {
        return clientTaxAccountNumber;
    }

    public String getCifShortName() {
        return cifShortName;
    }

    public String getCifLongName() {
        return cifLongName;
    }

    public String getCifLongNameA() {
        return cifLongNameA;
    }

    public String getCifLongNameB() {
        return cifLongNameB;
    }

    public String getGender() {
        return gender;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getNationality() {
        return nationality;
    }

    public String getNationOfBirth() {
        return nationOfBirth;
    }

    public String getUcpbCifNumber() {
        return ucpbCifNumber;
    }
}
