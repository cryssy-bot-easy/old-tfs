package com.ucpb.tfs.interfaces.gateway;

/**
 */
public enum TransactionCode {

    DEBIT_TO_CURRENT("1610","D"),
    DEBIT_TO_SAVINGS("2610","D"),
    CREDIT_TO_CURRENT("1710","C"),
    CREDIT_TO_SAVINGS("2710","C"),
    DEBIT_TO_FOREIGN("3610","D"),
    CREDIT_TO_FOREIGN("3830","C"),
    CREDIT_ERROR_CORRECT_CURRENT("1618","RC"),
    DEBIT_ERROR_CORRECT_SAVINGS("2718","RD"),
    CREDIT_ERROR_CORRECT_FOREIGN("3838","RC"),
    DEBIT_ERROR_CORRECT_FOREIGN("3618","RD"),
    INQUIRE_STATUS_CURRENT("1101","INQ"),
    INQUIRE_STATUS_SAVINGS("2101","INQ"),
    INQ_STATUS_SAVINGS("3101","INQ");

    private String code;

    private String type;

    private TransactionCode(String code,String type){
        this.code = code;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
