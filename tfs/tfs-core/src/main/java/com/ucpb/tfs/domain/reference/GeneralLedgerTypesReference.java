package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.reference.enumTypes.RecordType;

/**
 */
public class GeneralLedgerTypesReference {

    private Long id;

    private String glCode;

    private RecordType recordType;

    private String description;

    public String getGlCode() {
        return glCode;
    }

    public void setGlCode(String glCode) {
        this.glCode = glCode;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
