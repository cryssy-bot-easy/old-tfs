package com.ucpb.tfs.swift.message;

import com.ucpb.tfs.swift.validation.JointField;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 	Modified by: Rafael Ski Poblete
    Date : 9/11/18
    Description : Changed field71B to field71D.
    			  Changed field72 to field72Z and changed pattern to Z character.
    			  Added field79Z with getter and setter.
 */
@JointField(field = "field33A", jointFields = {"field32B","field71D"})
public class MT752 extends SwiftMessage {

    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @Pattern(regexp = SwiftFields.REFERENCE_NUMBER)
    private String field21;

    @Pattern(regexp = SwiftFields.IDENTIFICATION)
    private String field23;

    @Pattern(regexp = SwiftFields.DATE_6)
    private String field30;

    @Pattern(regexp = SwiftFields.MONEY)
    private String field32B;

    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,35}){0,6}")
    private String field71D;

    private String field33A;

    private String field53A;

    private String field53B;

    private String field53D;

    private String field54A;

    private String field54B;

    private String field54D;

    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,35}){0,6}")
    private String field72Z;
    
    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,50}){0,35}")
    private String field79Z;

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

    public String getField32B() {
        return field32B;
    }

    public void setField32B(String field32B) {
        this.field32B = field32B;
    }

    public String getField71D() {
        return field71D;
    }

    public void setField71D(String field71D) {
        this.field71D = field71D;
    }

    public String getField33A() {
        return field33A;
    }

    public void setField33A(String field33A) {
        this.field33A = field33A;
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

    public String getField72Z() {
        return field72Z;
    }

    public void setField72Z(String field72Z) {
        this.field72Z = field72Z;
    }

    public String getField53B() {
        return field53B;
    }

    public void setField53B(String field53B) {
        this.field53B = field53B;
    }

    public String getField53D() {
        return field53D;
    }

    public void setField53D(String field53D) {
        this.field53D = field53D;
    }

    public String getField54B() {
        return field54B;
    }

    public void setField54B(String field54B) {
        this.field54B = field54B;
    }

    public String getField54D() {
        return field54D;
    }

    public void setField54D(String field54D) {
        this.field54D = field54D;
    }

	public String getField79Z() {
		return field79Z;
	}

	public void setField79Z(String field79z) {
		field79Z = field79z;
	}
}
