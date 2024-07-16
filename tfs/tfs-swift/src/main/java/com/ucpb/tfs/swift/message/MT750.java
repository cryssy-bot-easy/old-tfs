package com.ucpb.tfs.swift.message;


import com.ucpb.tfs.swift.validation.DependentField;
import com.ucpb.tfs.swift.validation.Spel;

import javax.validation.constraints.Pattern;

/**
 */
@DependentField(dependentField = "field34B", targetFields = {"field33B","field71B","field73B"})
@Spel(spelExpression = "#this.field32B.substring(1,3) == #this.field34B.substring(1,3)", message = "currency codes of fields 32B and 34B must be equal")
public class MT750 extends SwiftMessage {

    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field21;

    @Pattern(regexp = SwiftFields.MONEY)
    private String field32B;

    @Pattern(regexp = SwiftFields.MONEY)
    private String field33B;

    @Pattern(regexp = SwiftFields.CHARGES)
    private String field71B;

    @Pattern(regexp = SwiftFields.CHARGES)
    private String field73;

    @Pattern(regexp = SwiftFields.MONEY)
    private String field34B;

    private String field57A;

    @Pattern(regexp = SwiftFields.SENDER_TO_RECEIVER_INFO)
    private String field72;

    @Pattern(regexp = SwiftFields.DISCREPANCIES)
    private String field77J;


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

    public String getField71B() {
        return field71B;
    }

    public void setField71B(String field71B) {
        this.field71B = field71B;
    }

    public String getField73() {
        return field73;
    }

    public void setField73(String field73) {
        this.field73 = field73;
    }

    public String getField34B() {
        return field34B;
    }

    public void setField34B(String field34B) {
        this.field34B = field34B;
    }

    public String getField57A() {
        return field57A;
    }

    public void setField57A(String field57A) {
        this.field57A = field57A;
    }

    public String getField72() {
        return field72;
    }

    public void setField72(String field72) {
        this.field72 = field72;
    }

    public String getField77J() {
        return field77J;
    }

    public void setField77J(String field77J) {
        this.field77J = field77J;
    }
}
