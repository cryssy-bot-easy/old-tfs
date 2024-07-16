package com.ucpb.tfs.domain.audit;

public enum TransactionType {

	DEBIT("D"),CREDIT("C"),REVERSE_DEBIT("RD"),REVERSE_CREDIT("RC"),INQUIRY("INQ");

    private final String code;

    private TransactionType(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static TransactionType getTransactionTypeByCode(String code){
        for(TransactionType transactionType : TransactionType.values()){
            if(transactionType.getCode().equalsIgnoreCase(code)){
                return transactionType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
