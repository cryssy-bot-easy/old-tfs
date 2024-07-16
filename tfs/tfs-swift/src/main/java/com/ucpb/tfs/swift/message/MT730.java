package com.ucpb.tfs.swift.message;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * Modified by: Rafael Ski Poblete
 * Date : 7/26/18
 * Description : Changed field1B to field1D  and field72 to field72Z
 */
public class MT730 extends SwiftMessage{

    @NotEmpty
    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field20;

    @NotEmpty
    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field21;

    @Pattern(regexp = SwiftFields.ACCOUNT_IDENTIFICATION)
    private String field25;

    @NotEmpty
    @Pattern(regexp = SwiftFields.DATE_6)
    private String field30;

    private String field32A;

    private String field57A;
    
    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,35}){0,6}")
    private String field71D;
    
    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,35}){0,6}")
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

    public String getField25() {
        return field25;
    }

    public void setField25(String field25) {
        this.field25 = field25;
    }

    public String getField30() {
        return field30;
    }

    public void setField30(String field30) {
        this.field30 = field30;
    }

    public String getField32A() {
        return field32A;
    }

    public void setField32A(String field32A) {
        this.field32A = field32A;
    }

    public String getField57A() {
        return field57A;
    }

    public void setField57A(String field57A) {
        this.field57A = field57A;
    }

    public String getField71D() {
        return field71D;
    }

    public void setField71D(String field71D) {
        this.field71D = field71D;
    }

	public String getField72Z() {
		return field72Z;
	}

	public void setField72Z(String field72z) {
		field72Z = field72z;
	}
    

}
