package com.ucpb.tfs.swift.message;

import com.ucpb.tfs.swift.validation.JointField;

import javax.validation.constraints.Pattern;

/**
 */
public class MT760 extends SwiftMessage {

    private String field27;

    @Pattern(regexp= SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.PRE_ADVICE)
    private String field23;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field30;

    @Pattern(regexp = SwiftFields.APPLICABLE_RULES)
    private String field40C;

    @Pattern(regexp = SwiftFields.DETAILS_OF_GUARANTEE)
    private String field77C;

    @Pattern(regexp = "(" + SwiftFields.X_DATA_TYPE + "{0,35}){0,6}")
    private String field72;


    public String getField27() {
        return field27;
    }

    public void setField27(String field27) {
        this.field27 = field27;
    }

    public String getField20() {
        return field20;
    }

    public void setField20(String field20) {
        this.field20 = field20;
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

    public String getField40C() {
        return field40C;
    }

    public void setField40C(String field40C) {
        this.field40C = field40C;
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
}
