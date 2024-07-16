package com.ucpb.tfs.domain.documents;

import com.ucpb.tfs.domain.documents.enumTypes.RequiredDocumentType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

/**
 * User: Marv
 * Date: 10/31/12
 */

/**
 * Description:   Added amendId and amendCode
 * Modified by:   Cedrick C. Nungay
 * Date Modified: 08/24/18
 */
public class RequiredDocument implements Serializable {
    
    private String id;

    private DocumentCode documentCode;

    private String description;

    private RequiredDocumentType requiredDocumentType;

    private int sequenceNumber;

    private BigDecimal amendId;

    private String amendCode;

	public RequiredDocument() {}
    
    public RequiredDocument(DocumentCode documentCode, String description, RequiredDocumentType requiredDocumentType) {
        this.documentCode = documentCode;
        this.description = description;
        this.requiredDocumentType = requiredDocumentType;
    }

    public RequiredDocument(DocumentCode documentCode, String description, RequiredDocumentType requiredDocumentType,int sequenceNumber) {
    	this.documentCode = documentCode;
    	this.description = description;
    	this.requiredDocumentType = requiredDocumentType;
    	this.sequenceNumber = sequenceNumber;
    }
    
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        if(documentCode != null) {
            map.put("documentCode", documentCode.toString());
        }

        map.put("description", description);
        map.put("requiredDocumentType", requiredDocumentType.toString());

        return map;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
    public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

    public BigDecimal getAmendId() {
        return amendId;
    }

    public void setAmendId(BigDecimal amendId) {
        this.amendId = amendId;
    }

    public String getAmendCode() {
        return amendCode;
    }

    public void setAmendCode(String amendCode) {
        this.amendCode = amendCode;
    }
}
