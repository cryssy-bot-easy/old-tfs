package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;

public class ProductReference {

    // UCPB Product Id
    private ProductId productId;

    private String ucpbProductId;

    // document type (Foreign, Domestic)
    private DocumentType documentType;

    // document class (LC, etc)
    private DocumentClass documentClass;

    // Regular, Cash, Standby
    private DocumentSubType1 documentSubType1;

    // Sight, Usance
    private DocumentSubType2 documentSubType2;

    private String documentSubType3;

    private String contingentAccountingCode;

    private String contraContingentAccountingCode;

    private String discrepancyContingentAccountingCode;

    private String discrepancyContraContingentAccountingCode;

    private String name;

    private String bookCode;

    private String documentCode;

    private String shortName;

    private String glAccountType;

    public ProductReference() {
    }

    public ProductReference(String productId, DocumentClass documentClass, DocumentType documentType, DocumentSubType1 documentSubType1, DocumentSubType2 documentSubType2) {

        this.productId = new ProductId(productId);
        this.documentClass = documentClass;
        this.documentType = documentType;
        this.documentSubType1 = documentSubType1;
        this.documentSubType2 = documentSubType2;

    }

    public ProductId getProductId() {
        return productId;
    }

    public String getUcpbProductId() {
        return ucpbProductId;
    }


    public String getContraContingentAccountingCode() {
        return contraContingentAccountingCode;
    }

    public void setContraContingentAccountingCode(String contraContingentAccountingCode) {
        this.contraContingentAccountingCode = contraContingentAccountingCode;
    }

    public String getContingentAccountingCode() {
        return contingentAccountingCode;
    }

    public void setContingentAccountingCode(String contingentAccountingCode) {
        this.contingentAccountingCode = contingentAccountingCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public void setProductId(ProductId productId) {
        this.productId = productId;
    }

    public void setUcpbProductId(String ucpbProductId) {
        this.ucpbProductId = ucpbProductId;
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

    public String getDocumentSubType3() {
        return documentSubType3;
    }

    public void setDocumentSubType3(String documentSubType3) {
        this.documentSubType3 = documentSubType3;
    }

    public String getDiscrepancyContraContingentAccountingCode() {
        return discrepancyContraContingentAccountingCode;
    }

    public void setDiscrepancyContraContingentAccountingCode(String discrepancyContraContingentAccountingCode) {
        this.discrepancyContraContingentAccountingCode = discrepancyContraContingentAccountingCode;
    }

    public String getDiscrepancyContingentAccountingCode() {
        return discrepancyContingentAccountingCode;
    }

    public void setDiscrepancyContingentAccountingCode(String discrepancyContingentAccountingCode) {
        this.discrepancyContingentAccountingCode = discrepancyContingentAccountingCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getGlAccountType() {
        return glAccountType;
    }

    public void setGlAccountType(String glAccountType) {
        this.glAccountType = glAccountType;
    }
}
