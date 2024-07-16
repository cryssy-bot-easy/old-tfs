package com.ucpb.tfs.swift.message;

import javax.validation.constraints.Pattern;

/**
 */
public class MT202 extends SwiftMessage {

    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field21;

    @Pattern(regexp = SwiftFields.TIME_INDICATION)
    private String field13C;

    @Pattern(regexp = SwiftFields.DATE_AND_AMOUNT)
    private String field32A;

    private String field52A;

    private String field53A;

    private String field54A;

    private String field56A;

    private String field57A;

    private String field58A;

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

    public String getField13C() {
        return field13C;
    }

    public void setField13C(String field13C) {
        this.field13C = field13C;
    }

    public String getField32A() {
        return field32A;
    }

    public void setField32A(String field32A) {
        this.field32A = field32A;
    }

    public String getField52A() {
        return field52A;
    }

    public void setField52A(String field52A) {
        this.field52A = field52A;
    }

    public String getField53A() {
        return field53A;
    }

    public void setField53A(String field53A) {
        this.field53A = field53A;
    }

    public String getField54A() {
        return field54A;
    }

    public void setField54A(String field54A) {
        this.field54A = field54A;
    }

    public String getField56A() {
        return field56A;
    }

    public void setField56A(String field56A) {
        this.field56A = field56A;
    }

    public String getField57A() {
        return field57A;
    }

    public void setField57A(String field57A) {
        this.field57A = field57A;
    }

    public String getField58A() {
        return field58A;
    }

    public void setField58A(String field58A) {
        this.field58A = field58A;
    }

    public String getField72() {
        return field72;
    }

    public void setField72(String field72) {
        this.field72 = field72;
    }

}
