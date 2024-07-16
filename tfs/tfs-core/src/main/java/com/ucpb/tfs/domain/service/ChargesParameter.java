package com.ucpb.tfs.domain.service;

/**
 * Created by: Cedrick C. Nungay
 * Details: Model for Charges Parameter database record.
 * Date created: 02/01/2018
*/
public class ChargesParameter {
    private String id;
    private String parameterName;
    private String parameterValue;
    private String parameterChargeType;
    private String parameterDocumentType;
    private String parameterDocumentClass;
    private String parameterDocumentSubType1;
    private String parameterDocumentSubType2;
    private String parameterServiceType;
    private String parameterDescription;

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterChargeType(String parameterChargeType) {
        this.parameterChargeType = parameterChargeType;
    }

    public String getParameterChargeType() {
        return parameterChargeType;
    }

    public void setParameterDocumentType(String parameterDocumentType) {
        this.parameterDocumentType = parameterDocumentType;
    }

    public String getParameterDocumentType() {
        return parameterDocumentType;
    }

    public void setParameterDocumentClass(String parameterDocumentClass) {
        this.parameterDocumentClass = parameterDocumentClass;
    }

    public String getParameterDocumentClass() {
        return parameterDocumentClass;
    }

    public void setParameterDocumentSubType1(String parameterDocumentSubType1) {
        this.parameterDocumentSubType1 = parameterDocumentSubType1;
    }

    public String getParameterDocumentSubType1() {
        return parameterDocumentSubType1;
    }

    public void setParameterDocumentSubType2(String parameterDocumentSubType2) {
        this.parameterDocumentSubType2 = parameterDocumentSubType2;
    }

    public String getParameterDocumentSubType2() {
        return parameterDocumentSubType2;
    }

    public void setParameterServiceType(String parameterServiceType) {
        this.parameterServiceType = parameterServiceType;
    }

    public String getParameterServiceType() {
        return parameterServiceType;
    }

    public void setParameterDescription(String parameterDescription) {
        this.parameterDescription = parameterDescription;
    }

    public String setParameterDescription() {
        return parameterDescription;
    }
}
