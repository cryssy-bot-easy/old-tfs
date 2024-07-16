package com.ucpb.tfs.swift.message;

import com.ucpb.tfs.swift.validation.DependentField;
import com.ucpb.tfs.swift.validation.ExclusiveOr;
import com.ucpb.tfs.swift.validation.Or;
import com.ucpb.tfs.swift.validation.Spel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * Modified.
 * Modfied by: Rafael Ski Poblete
 * Description: Added field23S.
 */
@DependentField.List(
    {
        @DependentField(dependentField = "field34B", targetFields = {"field32B","field33B"}),
        @DependentField(dependentField = "field52A", targetFields = {"field23"})
    }
)
@ExclusiveOr.List(
    {
        @ExclusiveOr(fields = {"field39A","field39B"}),
        @ExclusiveOr(fields = {"field44C","field44D"})
    }
)
@Or(fields = {"field31E","field32B","field33B","field34B","field39A","field39B","field39C","field44A","field44E","field44F","field44B","field44C","field44D","field79","field72"})
@Spel(spelExpression = "#this?.field32B? != null ? #this?.field32B?.substring(1,3) == #this?.field34B?.substring(1,3) : #this?.field33B?.substring(1,3) == #this?.field34B?.substring(1,3)", message = "currency codes of amount fields do not match ")
public class MT707 extends SwiftMessage {

    @NotEmpty
    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @NotEmpty
    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field21;

    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field23;

    private String field52A;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field31C;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field30;

    @Pattern(regexp = SwiftFields.NUMBER_OF_AMENDMENT)
    private String field26E;

    @NotEmpty
    @Pattern(regexp = SwiftFields.PARTY_IDENTIFIER)
    private String field59;

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

    @Pattern(regexp = SwiftFields.ADDRESS)
    private String field44A;

    @Pattern(regexp = SwiftFields.ADDRESS)
    private String field44E;

    @Pattern(regexp = SwiftFields.ADDRESS)
    private String field44F;

    @Pattern(regexp = SwiftFields.ADDRESS)
    private String field44B;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field44C;

    @Pattern(regexp = SwiftFields.SHIPMENT_PERIOD)
    private String field44D;

    @Pattern(regexp = SwiftFields.NARRATIVE)
    private String field79;

    @Pattern(regexp = SwiftFields.SENDER_TO_RECEIVER_INFO)
    private String field72;

    private String field23S;

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

    public String getField52A() {
        return field52A;
    }

    public void setField52A(String field52A) {
        this.field52A = field52A;
    }

    public String getField31C() {
        return field31C;
    }

    public void setField31C(String field31C) {
        this.field31C = field31C;
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

    public String getField59() {
        return field59;
    }

    public void setField59(String field59) {
        this.field59 = field59;
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

    public String getField44A() {
        return field44A;
    }

    public void setField44A(String field44A) {
        this.field44A = field44A;
    }

    public String getField44E() {
        return field44E;
    }

    public void setField44E(String field44E) {
        this.field44E = field44E;
    }

    public String getField44F() {
        return field44F;
    }

    public void setField44F(String field44F) {
        this.field44F = field44F;
    }

    public String getField44B() {
        return field44B;
    }

    public void setField44B(String field44B) {
        this.field44B = field44B;
    }

    public String getField44C() {
        return field44C;
    }

    public void setField44C(String field44C) {
        this.field44C = field44C;
    }

    public String getField44D() {
        return field44D;
    }

    public void setField44D(String field44D) {
        this.field44D = field44D;
    }

    public String getField79() {
        return field79;
    }

    public void setField79(String field79) {
        this.field79 = field79;
    }

    public String getField72() {
        return field72;
    }

    public void setField72(String field72) {
        this.field72 = field72;
    }

	public String getField23S() {
		return field23S;
	}

	public void setField23S(String field23s) {
		field23S = field23s;
	}
}
