package com.ucpb.tfs.swift.message.constants;

/**
 */
public enum ApplicableRules {

    EUCP_LATEST_VERSION("EUCP","EUCP LATEST VERSION"),
    EUCPURR_LATEST_VERSION("EURR","EUCPURR LATEST VERSION"),
    ISP_LATEST_VERSION("ISP","ISP LATEST VERSION"),
    OTHR("OTHR","OTHR"),
    UCP_LATEST_VERSION("UCP","UCP LATEST VERSION"),
    UCPURR_LATEST_VERSION("UCUR","UCPURR LATEST VERSION");
    
    private String code;
    private String label;

    private ApplicableRules(String code, String label){
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
        for(ApplicableRules rule : ApplicableRules.values()){
            if(rule.getCode().equalsIgnoreCase(code)){
                return rule.getLabel();
            }
        }
        return null;
    }
}
