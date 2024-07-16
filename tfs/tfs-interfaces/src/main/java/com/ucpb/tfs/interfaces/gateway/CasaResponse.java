package com.ucpb.tfs.interfaces.gateway;

public class CasaResponse {

	public static final String SUCCESSFUL = "0000";
	public static final String[] TIMEOUT = {"0003","0077"};
	
	
	private String responseCode;
    private String referenceNumber;
    private String workTaskId;
    private String errorMessage;
    private AccountStatus accountStatus;
    private String accountName;

	public CasaResponse(){
		//default constructor
	}

    public boolean isSuccessful(){
        return SUCCESSFUL.equals(responseCode);
    }
	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public void setWorkTaskId(String workTaskId) {
		this.workTaskId = workTaskId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public String getWorkTaskId() {
		return workTaskId;
	}

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        System.out.println("account name passed is : " + accountName);
        this.accountName = accountName;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setAccountStatus(String code) {
        AccountStatus as = null;
        for(AccountStatus status : AccountStatus.values()){
            if(status.getCode().equalsIgnoreCase(code)){
                as = AccountStatus.getAccountStatusByCode(code);
                break;
            }
        }
        this.accountStatus = as;
    }
    
    public boolean hasTimedOut(){
    	boolean result = false;
    	for(String s:TIMEOUT){
    		if(s.equals(responseCode)){
    			result = true;
    		}
    	}
    	return result;
    }

    @Override
    public String toString() {
        return "CasaResponse{" +
                "accountName='" + accountName + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", workTaskId='" + workTaskId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", accountStatus=" + accountStatus +
                '}';
    }
}
