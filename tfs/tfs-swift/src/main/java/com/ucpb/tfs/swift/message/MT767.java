package com.ucpb.tfs.swift.message;

import javax.validation.constraints.Pattern;

/**
 */
public class MT767 extends SwiftMessage {

    private String field27;

    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field21;

    @Pattern(regexp = SwiftFields.IDENTIFICATION)
    private String field23;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field30;

    @Pattern(regexp = SwiftFields.NUMBER_OF_AMENDMENT)
    private String field26E;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field31C;

    @Pattern(regexp = SwiftFields.AMMENDMENT_DETAILS)
    private String field77C;

    @Pattern(regexp = SwiftFields.SENDER_TO_RECEIVER_INFO)
    private String field72;

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

    public String getField23() {
        return field23;
    }

    public void setField23(String field23) {
        this.field23 = field23;
    }

    public String getField30() {
        return field30;
    }

    public void setField30(String field30) {
        this.field30 = field30;
    }

    public String getField26E() {
        return field26E;
    }

    public void setField26E(String field26E) {
        this.field26E = field26E;
    }

    public String getField31C() {
        return field31C;
    }

    public void setField31C(String field31C) {
        this.field31C = field31C;
    }

    public String getField77C() {
        return field77C;
    }

    public void setField77C(String field77C) {
        this.field77C = field77C;
    }

    public String getField72() {
        return field72;
    }

    public void setField72(String field72) {
        this.field72 = field72;
    }

    public String getField27() {
        return field27;
    }

    public void setField27(String field27) {
        this.field27 = field27;
    }
}
