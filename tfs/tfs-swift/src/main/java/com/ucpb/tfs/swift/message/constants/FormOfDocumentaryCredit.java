package com.ucpb.tfs.swift.message.constants;

/**
 */
public enum FormOfDocumentaryCredit {

    IRREVOCABLE("I","IRREVOCABLE"),
    REVOCABLE("R","REVOCABLE"),
    IRREVOCABLE_TRANSFERABLE("IT","IRREVOCABLE TRANSFERABLE"),
    REVOCABLE_TRANSFERABLE("RT","REVOCABLE TRANSFERABLE"),
    IRREVOCABLE_STANDBY("IS","IRREVOCABLE STANDBY"),
    REVOCABLE_STANDBY("RS","REVOCABLE STANDBY"),
    IRREVOC_TRANS_STANDBY("ITS","IRREVOC TRANS STANDBY");

    private String code;
    private String label;

    private FormOfDocumentaryCredit(String code, String label){
        this.code = code;
        this.label = label;
    }


    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static FormOfDocumentaryCredit getValue(String code){
        for(FormOfDocumentaryCredit fodc : FormOfDocumentaryCredit.values()){
            if(fodc.getCode().equalsIgnoreCase(code)){
                return fodc;
            }
        }
        return null;
    }

    public static String getLabel(String code){
        for(FormOfDocumentaryCredit fodc : FormOfDocumentaryCredit.values()){
            if(fodc.getCode().equalsIgnoreCase(code)){
                return fodc.getLabel();
            }
        }
        return null;
    }

}
