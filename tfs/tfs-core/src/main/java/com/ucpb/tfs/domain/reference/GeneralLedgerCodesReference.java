package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.service.enumTypes.*;

/**
 */
public class GeneralLedgerCodesReference {

    private Long id;

    private String productId;

    private ServiceType serviceType;

    private DocumentType documentType;

    private DocumentClass documentClass;

    private DocumentSubType1 documentSubType1;

    private DocumentSubType2 documentSubType2;

    private String productCode;

    private String bookCode;

    private String debitCode;

    private String creditCode;

    private String unitCode;


    public String getUnitCode() {
        return unitCode;
    }


    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getDebitCode() {
        return debitCode;
    }

    public void setDebitCode(String debitCode) {
        this.debitCode = debitCode;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public DocumentClass getDocumentClass() {
        return documentClass;
    }

    public void setDocumentClass(DocumentClass documentClass) {
        this.documentClass = documentClass;
    }

    public DocumentSubType1 getDocumentSubType1() {
        return documentSubType1;
    }

    public void setDocumentSubType1(DocumentSubType1 documentSubType1) {
        this.documentSubType1 = documentSubType1;
    }

    public DocumentSubType2 getDocumentSubType2() {
        return documentSubType2;
    }

    public void setDocumentSubType2(DocumentSubType2 documentSubType2) {
        this.documentSubType2 = documentSubType2;
    }
}
