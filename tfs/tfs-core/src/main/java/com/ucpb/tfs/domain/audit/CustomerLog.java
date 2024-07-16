package com.ucpb.tfs.domain.audit;

import com.ucpb.tfs.domain.service.TradeServiceId;

import java.math.BigDecimal;
import java.util.Date;

/**
 */
public class CustomerLog {

    private Long id;

    private TradeServiceId tradeServiceId;

    private Date dateCreated = new Date();
    private String customerType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String surname;
    private String fatherName;
    private String motherName;
    private String gender;

    private Date dateOfBirth;
    private String placeOfBirth;
    private String maritalStatus;
    private String domicileCountry;
    private String nationality;
    private String nationOfBirth;
    private Address businessAddress;
    private Integer lengthOfStayInPresentAddress;
    private Address permanentAddress;
    private Integer lengthOfStayInPermanentAddress;
    private String educationQualification;
    private String educationSpecialization;
    private String service;
    private String exService;
    private String occupation;
    private String sector;
    private String natureOfBusiness;
    private Boolean isEmployed;
    private String employerName;
    private Integer yearsInCompany;
    private BigDecimal monthlyIncome;
    private BigDecimal annualIncome;
    private BigDecimal financialStatus;
    private Boolean isMinor;
    private String introducersName;
    private String introducersCustomerId;
    private String introductersRelationship;
    private Address introducersAddress;
    private String individualOrCorporate;
    private String principalBankersName;
    private String principalBankersAddress;
    private String residencePhoneNumber;
    private String officePhoneNumber;
    private String mobileNumber;
    private String faxNumber;
    private String modeOfAccountOfPayment;
    private String natureOfWork;
    private String natureOfSelfEmployment;
    private String sourceOfFunds;

    private Date lastUpdated;

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public Address getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(Address businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDomicileCountry() {
        return domicileCountry;
    }

    public void setDomicileCountry(String domicileCountry) {
        this.domicileCountry = domicileCountry;
    }

    public String getEducationQualification() {
        return educationQualification;
    }

    public void setEducationQualification(String educationQualification) {
        this.educationQualification = educationQualification;
    }

    public String getEducationSpecialization() {
        return educationSpecialization;
    }

    public void setEducationSpecialization(String educationSpecialization) {
        this.educationSpecialization = educationSpecialization;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getExService() {
        return exService;
    }

    public void setExService(String exService) {
        this.exService = exService;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public BigDecimal getFinancialStatus() {
        return financialStatus;
    }

    public void setFinancialStatus(BigDecimal financialStatus) {
        this.financialStatus = financialStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIndividualOrCorporate() {
        return individualOrCorporate;
    }

    public void setIndividualOrCorporate(String individualOrCorporate) {
        this.individualOrCorporate = individualOrCorporate;
    }

    public Address getIntroducersAddress() {
        return introducersAddress;
    }

    public void setIntroducersAddress(Address introducersAddress) {
        this.introducersAddress = introducersAddress;
    }

    public String getIntroducersCustomerId() {
        return introducersCustomerId;
    }

    public void setIntroducersCustomerId(String introducersCustomerId) {
        this.introducersCustomerId = introducersCustomerId;
    }

    public String getIntroducersName() {
        return introducersName;
    }

    public void setIntroducersName(String introducersName) {
        this.introducersName = introducersName;
    }

    public String getIntroductersRelationship() {
        return introductersRelationship;
    }

    public void setIntroductersRelationship(String introductersRelationship) {
        this.introductersRelationship = introductersRelationship;
    }

    public Boolean getEmployed() {
        return isEmployed;
    }

    public void setEmployed(Boolean employed) {
        isEmployed = employed;
    }

    public Boolean getMinor() {
        return isMinor;
    }

    public void setMinor(Boolean minor) {
        isMinor = minor;
    }

    public Integer getLengthOfStayInPermanentAddress() {
        return lengthOfStayInPermanentAddress;
    }

    public void setLengthOfStayInPermanentAddress(Integer lengthOfStayInPermanentAddress) {
        this.lengthOfStayInPermanentAddress = lengthOfStayInPermanentAddress;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getModeOfAccountOfPayment() {
        return modeOfAccountOfPayment;
    }

    public void setModeOfAccountOfPayment(String modeOfAccountOfPayment) {
        this.modeOfAccountOfPayment = modeOfAccountOfPayment;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationOfBirth() {
        return nationOfBirth;
    }

    public void setNationOfBirth(String nationOfBirth) {
        this.nationOfBirth = nationOfBirth;
    }

    public String getNatureOfBusiness() {
        return natureOfBusiness;
    }

    public void setNatureOfBusiness(String natureOfBusiness) {
        this.natureOfBusiness = natureOfBusiness;
    }

    public String getNatureOfSelfEmployment() {
        return natureOfSelfEmployment;
    }

    public void setNatureOfSelfEmployment(String natureOfSelfEmployment) {
        this.natureOfSelfEmployment = natureOfSelfEmployment;
    }

    public String getNatureOfWork() {
        return natureOfWork;
    }

    public void setNatureOfWork(String natureOfWork) {
        this.natureOfWork = natureOfWork;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getOfficePhoneNumber() {
        return officePhoneNumber;
    }

    public void setOfficePhoneNumber(String officePhoneNumber) {
        this.officePhoneNumber = officePhoneNumber;
    }

    public Address getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(Address permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getPrincipalBankersAddress() {
        return principalBankersAddress;
    }

    public void setPrincipalBankersAddress(String principalBankersAddress) {
        this.principalBankersAddress = principalBankersAddress;
    }

    public String getPrincipalBankersName() {
        return principalBankersName;
    }

    public void setPrincipalBankersName(String principalBankersName) {
        this.principalBankersName = principalBankersName;
    }

    public String getResidencePhoneNumber() {
        return residencePhoneNumber;
    }

    public void setResidencePhoneNumber(String residencePhoneNumber) {
        this.residencePhoneNumber = residencePhoneNumber;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSourceOfFunds() {
        return sourceOfFunds;
    }

    public void setSourceOfFunds(String sourceOfFunds) {
        this.sourceOfFunds = sourceOfFunds;
    }

    public Integer getLengthOfStayInPresentAddress() {
        return lengthOfStayInPresentAddress;
    }

    public void setLengthOfStayInPresentAddress(Integer lengthOfStayInPresentAddress) {
        this.lengthOfStayInPresentAddress = lengthOfStayInPresentAddress;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getYearsInCompany() {
        return yearsInCompany;
    }

    public void setYearsInCompany(Integer yearsInCompany) {
        this.yearsInCompany = yearsInCompany;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
