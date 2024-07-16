package com.ucpb.tfs.swift.message;

import com.ucpb.tfs.swift.validation.DependentField;
import com.ucpb.tfs.swift.validation.ExclusiveOr;

import javax.validation.constraints.Pattern;

/**
 */
@ExclusiveOr.List(
        {
                @ExclusiveOr(fields = {"field39A", "field39B"}, message = "Either field 39A or 39B must be present, but not both"),
                @ExclusiveOr(fields = {"field42C","field42M","field42P"}, message = "Only one of the fields 42C, 42M and 42P must be present"),
        @ExclusiveOr(fields = {"field58A","field59"}, message = "Only one of the fields 58A or 59 must be present")
        }

)
@DependentField.List(
        @DependentField(dependentField = "field42A", targetFields = {"field42C"})
)
public class MT740 extends SwiftMessage {

    @Pattern(regexp= SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.PARTY_IDENTIFIER)
    private String field25;

    @Pattern(regexp = SwiftFields.APPLICABLE_RULES_ONLY)
    private String field40F;

    @Pattern(regexp= SwiftFields.DATE_AND_PLACE)
    private String field31D;

    private String field58A;

    private String field58D;

    private String field59;

    @Pattern(regexp = SwiftFields.MONEY)
    private String field32B;

    private String field39A;

    @Pattern(regexp =SwiftFields.X_DATA_TYPE + "{0,13}")
    private String field39B;

    @Pattern(regexp = SwiftFields.COMMENTS)
    private String field39C;

    @Pattern(regexp = SwiftFields.X_DATA_TYPE + "{0,35}")
    private String field41A;

    @Pattern(regexp = SwiftFields.NARRATIVE)
    private String field42C;

    @Pattern(regexp = SwiftFields.X_DATA_TYPE + "{0,35}")
    private String field42A;

    @Pattern(regexp = SwiftFields.COMMENTS)
    private String field42M;

    @Pattern(regexp = SwiftFields.PARTY_IDENTIFIER)
    private String field42P;

    private String field71A;

    private String field71B;

    @Pattern(regexp = SwiftFields.SENDER_TO_RECEIVER_INFO)
    private String field72;


    public String getField20() {
        return field20;
    }

    public void setField20(String field20) {
        this.field20 = field20;
    }

    public String getField25() {
        return field25;
    }

    public void setField25(String field25) {
        this.field25 = field25;
    }

    public String getField40F() {
        return field40F;
    }

    public void setField40F(String field40F) {
        this.field40F = field40F;
    }

    public String getField31D() {
        return field31D;
    }

    public void setField31D(String field31D) {
        this.field31D = field31D;
    }

    public String getField58A() {
        return field58A;
    }

    public void setField58A(String field58A) {
        this.field58A = field58A;
    }

    public String getField59() {
        return field59;
    }

    public void setField59(String field59) {
        this.field59 = field59;
    }

    public String getField32B() {
        return field32B;
    }

    public void setField32B(String field32B) {
        this.field32B = field32B;
    }

    public String getField39A() {
        return field39A;
    }

    public void setField39A(String field39A) {
        this.field39A = field39A;
    }

    public String getField39B() {
        return field39B;
    }

    public void setField39B(String field39B) {
        this.field39B = field39B;
    }

    public String getField39C() {
        return field39C;
    }

    public void setField39C(String field39C) {
        this.field39C = field39C;
    }

    public String getField41A() {
        return field41A;
    }

    public void setField41A(String field41A) {
        this.field41A = field41A;
    }

    public String getField42C() {
        return field42C;
    }

    public void setField42C(String field42C) {
        this.field42C = field42C;
    }

    public String getField42A() {
        return field42A;
    }

    public void setField42A(String field42A) {
        this.field42A = field42A;
    }

    public String getField42M() {
        return field42M;
    }

    public void setField42M(String field42M) {
        this.field42M = field42M;
    }

    public String getField42P() {
        return field42P;
    }

    public void setField42P(String field42P) {
        this.field42P = field42P;
    }

    public String getField71A() {
        return field71A;
    }

    public void setField71A(String field71A) {
        this.field71A = field71A;
    }

    public String getField71B() {
        return field71B;
    }

    public void setField71B(String field71B) {
        this.field71B = field71B;
    }

    public String getField72() {
        return field72;
    }

    public void setField72(String field72) {
        this.field72 = field72;
    }

    public String getField58D() {
        return field58D;
    }

    public void setField58D(String field58D) {
        this.field58D = field58D;
    }
}
