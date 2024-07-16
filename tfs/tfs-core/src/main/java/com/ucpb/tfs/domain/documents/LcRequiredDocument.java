package com.ucpb.tfs.domain.documents;

import com.ucpb.tfs.domain.documents.enumTypes.RequiredDocumentType;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Marv
 * Date: 10/31/12
 */

@Audited
public class LcRequiredDocument implements Serializable {

    private String id;

    private DocumentCode documentCode;

    private String description;

    private RequiredDocumentType requiredDocumentType;

    public LcRequiredDocument() {}

    public LcRequiredDocument(DocumentCode documentCode, String description, RequiredDocumentType requiredDocumentType) {
        this.documentCode = documentCode;
        this.description = description;
        this.requiredDocumentType = requiredDocumentType;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setDocumentCode(DocumentCode documentCode) {
        this.documentCode = documentCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequiredDocumentType(RequiredDocumentType requiredDocumentType) {
        this.requiredDocumentType = requiredDocumentType;
    }
}
