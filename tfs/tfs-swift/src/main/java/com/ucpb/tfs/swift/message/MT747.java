package com.ucpb.tfs.swift.message;


import com.ucpb.tfs.swift.validation.DependentField;
import com.ucpb.tfs.swift.validation.ExclusiveOr;
import com.ucpb.tfs.swift.validation.Or;

import javax.validation.constraints.Pattern;

/**
 */
//@DependentField(dependentField = "field31E", targetFields = {"field32B","field33B"})
@DependentField.List(
    {
        @DependentField(dependentField = "field31E", targetFields = {"field32B", "field33B"})
    }
)
@Or(fields = {"field31E","field32B","field33B","field34B","field39A","field39B","field39C","field72","field77A"})
@ExclusiveOr(fields = {"field39A","field39B"})
@DependentField(dependentField = "field34B",targetFields = {"field32B","field33B"})
public class MT747 extends SwiftMessage {

    @Pattern(regexp = SwiftFields.SEQUENCE_NUMBER)
    private String field27;

    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field21;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field30;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field31E;

    @Pattern(regexp = SwiftFields.MONEY)
    private String field32B;

    @Pattern(regexp = SwiftFields.MONEY)
    private String field33B;

    @Pattern(regexp = SwiftFields.MONEY)
    private String field34B;

    @Pattern(regexp = SwiftFields.PERCENTAGE)
    private String field39A;

    @Pattern(regexp = SwiftFields.MAX_CREDIT_AMOUNT)
    private String field39B;

    @Pattern(regexp = SwiftFields.ADDITIONAL_AMOUNTS_COVERED)
    private String field39C;

    @Pattern(regexp = SwiftFields.SENDER_TO_RECEIVER_INFO)
    private String field72;

    @Pattern(regexp = SwiftFields.NARRATIVE)
    private String field77A;

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

    public String getField30() {
        return field30;
    }

    public void setField30(String field30) {
        this.field30 = field30;
    }

    public String getField31E() {
        return field31E;
    }

    public void setField31E(String field31E) {
        this.field31E = field31E;
    }

    public String getField32B() {
        return field32B;
    }

    public void setField32B(String field32B) {
        this.field32B = field32B;
    }

    public String getField33B() {
        return field33B;
    }

    public void setField33B(String field33B) {
        this.field33B = field33B;
    }

    public String getField34B() {
        return field34B;
    }

    public void setField34B(String field34B) {
        this.field34B = field34B;
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

    public String getField72() {
        return field72;
    }

    public void setField72(String field72) {
        this.field72 = field72;
    }

    public String getField77A() {
        return field77A;
    }

    public void setField77A(String field77A) {
        this.field77A = field77A;
    }

    public String getField27() {
        return field27;
    }

    public void setField27(String field27) {
        this.field27 = field27;
    }
}
