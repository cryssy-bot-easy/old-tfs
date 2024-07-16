package com.ucpb.tfs.domain.product.enums;

import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

/**
 * User: IPCVal
 * Date: 11/6/12
 */
public enum DocumentCodeEnum {

    /*
     * 01 = LC REGULAR/CASH
     * 02 = LC STANDBY
     * 15 = LC NEGO IC
     */
    REGULAR("01"),
    CASH("01"),
    STANDBY("02"),
    NEGOTIATION_DISCREPANCY("15"),
    
    // indemnity
    BG("01"),
    BE("02"),
    
    // nonlc(temporary)
    DC("01");

    private String documentCode;

    private DocumentCodeEnum(String documentCode) {
        this.documentCode = documentCode;
    }

    @Override
    public String toString() {
        return this.documentCode;
    }

    public static String toString(DocumentClass documentClass, ServiceType serviceType, DocumentSubType1 documentSubType1) {

        String documentCode = null;

        switch (documentClass) {

            case LC:

                if (serviceType != null) {

                    if (serviceType.equals(ServiceType.NEGOTIATION_DISCREPANCY)) {
                        documentCode = DocumentCodeEnum.NEGOTIATION_DISCREPANCY.toString();
                    }

                } else {

                    documentCode = DocumentCodeEnum.REGULAR.toString();

                    if (documentSubType1.equals(DocumentSubType1.CASH)) {
                        documentCode = DocumentCodeEnum.CASH.toString();
                    } else if (documentSubType1.equals(DocumentSubType1.STANDBY)) {
                        documentCode = DocumentCodeEnum.STANDBY.toString();
                    }

                }

                break;
                
            case DA:
            case DP:
            case OA:
            case DR:
            	documentCode = DocumentCodeEnum.DC.toString();
            	
            	break;
            
        }

        return documentCode;
    }
    
    public static String toString(IndemnityType indemnityType) {
        String documentCode = null;

        switch (indemnityType) {
            case BG:
                documentCode = DocumentCodeEnum.BG.toString();
                break;
            case BE:
                documentCode = DocumentCodeEnum.BE.toString();
                break;
        }

        return documentCode;
    }
}

