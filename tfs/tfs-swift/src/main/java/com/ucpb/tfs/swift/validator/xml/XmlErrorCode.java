package com.ucpb.tfs.swift.validator.xml;

/**
 */
public enum XmlErrorCode {

    INVALID_PATTERN("cvc-pattern-valid"),MAX_LENGTH_INVALID("cvc-max-Length-valid"),
    ELEMENT_INVALID("cvc-type.3.1.3"),MIN_LENGTH_INVALID("cvc-minLength-valid"),
    INVALID_CONTENT("cvc-complex-type.2.4.a"),
    INCOMPLETE_TYPE("cvc-complex-type.2.4.b"),
    UNKNOWN("unknown");

    private String errorCode;

    private XmlErrorCode(String errorCode){
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static XmlErrorCode getXmlErrorCode(String errorCode){
        for(XmlErrorCode xmlErrorCode : XmlErrorCode.values()){
            if(xmlErrorCode.getErrorCode().equalsIgnoreCase(errorCode)){
                return xmlErrorCode;
            }
        }
        return UNKNOWN;
    }
}