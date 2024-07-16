package com.ucpb.tfs.batch.report.dw;

public enum EntryType {

    DEBIT("Debit","D"),CREDIT("Credit","C");

    private String label;

    private String code;

    private EntryType(String label,String code){
        this.label = label;
        this.code = code;
    }

    public static EntryType getEntryType(String entryType){
        for(EntryType type : EntryType.values()){
            if(type.getLabel().equalsIgnoreCase(entryType)){
                return type;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
