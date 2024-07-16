package com.ucpb.tfs.swift.message;

import javax.validation.constraints.Pattern;

/**
 * 	Modified by: Rafael Ski Poblete
    Date : 7/26/18
    Description : Changed field1B to field1D  and field72 to field72Z to accept Z character set.
 */
public class MT742 extends SwiftMessage {

    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field21;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field31C;

    private String field52A;

    private String field32B;

    private String field33B;

    @Pattern(regexp = SwiftFields.Z_DATA_TYPE)
    private String field71D;

	private String field34A;

    private String field34B;

    private String field57A;

    private String field57B;

    private String field57D;

    @Pattern(regexp = SwiftFields.Z_DATA_TYPE)
    private String field72Z;

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

    public String getField31C() {
        return field31C;
    }

    public void setField31C(String field31C) {
        this.field31C = field31C;
    }

    public String getField52A() {
        return field52A;
    }

    public void setField52A(String field52A) {
        this.field52A = field52A;
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

    public String getField34A() {
        return field34A;
    }

    public void setField34A(String field34A) {
        this.field34A = field34A;
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

    public String getField57B() {
        return field57B;
    }

    public void setField57B(String field57B) {
        this.field57B = field57B;
    }

    public String getField57D() {
        return field57D;
    }

    public void setField57D(String field57D) {
        this.field57D = field57D;
    }

    public String getField71D() {
        return field71D;
    }

	public void setField71D(String field71d) {
		field71D = field71d;
	}

    public String getField72Z() {
		return field72Z;
	}

	public void setField72Z(String field72z) {
		field72Z = field72z;
	}

}
