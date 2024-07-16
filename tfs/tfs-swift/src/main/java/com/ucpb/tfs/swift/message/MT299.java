package com.ucpb.tfs.swift.message;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 */
public class MT299 extends SwiftMessage {

    @NotEmpty
    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field21;

    @NotEmpty
    @Pattern(regexp = SwiftFields.NARRATIVE)
    private String field79;


    public String getField20() {
        return field20;
    }

    public void setField20(String field20) {
        this.field20 = field20;
    }

    public String getField21() {
        return field21;
    }

    public void setField21(String field21) {
        this.field21 = field21;
    }

    public String getField79() {
        return field79;
    }

    public void setField79(String field79) {
        this.field79 = field79;
    }
}
