package com.ucpb.tfs.swift.message.constants;

/**
 */
public enum AvailableBy {

    BY_ACCEPTANCE("A","BY ACCEPTANCE"),
    BY_DEF_PAYMENT("D","BY DEF PAYMENT"),
    BY_MIXED_PYMT("M","BY MIXED PYMT"),
    BY_NEGOTIATION("N","BY NEGOTIATION"),
    BY_PAYMENT("P","BY PAYMENT");


    private String code;
    private String label;

    private AvailableBy(String code, String label){
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static String getLabel(String code){
        for(AvailableBy rule : AvailableBy.values()){
            if(rule.getCode().equalsIgnoreCase(code)){
                return rule.getLabel();
            }
        }
        return null;
    }
}
