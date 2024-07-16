package com.ucpb.tfs.swift.message;


import com.ucpb.tfs.swift.validation.ConditionalRegex;
import com.ucpb.tfs.swift.validation.DependentField;
import com.ucpb.tfs.swift.validation.ExclusiveOr;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * 	Modified by: Rafael Ski Poblete
    Date : 8/28/18
    Description : Changed Pattern of field45A, field46A, field47A to Z character.
    			  Changed field71B to field71D.
    			  Changed field72 to field72Z and changed pattern to Z character.
    			  Added field49G, field49H, field58A with getter and setter.
 */
@ExclusiveOr.List(
    {
        @ExclusiveOr(fields = {"field42A", "field42M", "field42P"}, message = "only one of fields 42A, 42M, 42P must be present"),
//        @ExclusiveOr(fields = {"field39A","field39B"}, message = "Either fields 39A or 39B must be present but not both"),
        @ExclusiveOr(fields = {"field44C","field44D"}, message = "Either fields 44C or 44D must be present but not both")
    }
)
@DependentField.List(
        @DependentField(dependentField = "field42C", targetFields = {"field42A"})
)
public class MT700 extends SwiftMessage {

    private static final String X_DATA_TYPE = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]";

//    @NotEmpty
    @Pattern(regexp = SwiftFields.SEQUENCE_NUMBER)
    private String field27;

//    @NotEmpty
    @Pattern(regexp= SwiftFields.FORM_OF_DOCUMENTARY_CREDIT)
    private String field40A;

//    @NotEmpty
    @Pattern(regexp= SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    @Pattern(regexp= SwiftFields.PRE_ADVICE)
    private String field23;

    @Pattern(regexp=SwiftFields.DATE_6)
    private String field31C;

//    @NotEmpty
    @ConditionalRegex(condition = "!#this.contains('OTHR')", ifTtrue = SwiftFields.APPLICABLE_RULES_ONLY, ifFalse = SwiftFields.RULES_WITH_NARRATIVE)
    private String field40E;

//    @NotEmpty
    @Pattern(regexp= "\\d{6}" + X_DATA_TYPE + "{0,29}")
    private String field31D;

    private String field51A;

    private String field51D;

//    @NotEmpty
    @Pattern(regexp = SwiftFields.COMMENTS)
    private String field50;

//    @NotEmpty
    @Pattern(regexp = SwiftFields.COMMENTS)
    private String field59;

//    @NotEmpty
    @Pattern(regexp = SwiftFields.MONEY)
    private String field32B;

    private String field41D;

    private String field39A;

    @Pattern(regexp = X_DATA_TYPE + "{0,13}")
    private String field39B;

    @Pattern(regexp = SwiftFields.COMMENTS)
    private String field39C;

//    @NotEmpty
    @Pattern(regexp = X_DATA_TYPE + "{0,35}")
    private String field41A;

    @Pattern(regexp = SwiftFields.NARRATIVE)
    private String field42C;

    @Pattern(regexp = X_DATA_TYPE + "{0,35}")
    private String field42A;

    private String field42D;

    @Pattern(regexp = SwiftFields.COMMENTS)
    private String field42M;

    @Pattern(regexp = SwiftFields.PARTY_IDENTIFIER)
    private String field42P;

    @Pattern(regexp = SwiftFields.PARTY_IDENTIFIER)
    private String field43P;

    @Pattern(regexp = SwiftFields.PARTY_IDENTIFIER)
    private String field43T;

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

    @Pattern(regexp = "(" + X_DATA_TYPE + "{0,65}){0,6}")
    private String field44D;
    
    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,65}){0,100}")
    private String field45A;

    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,65}){0,100}")
    private String field46A;

    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,65}){0,100}")
    private String field47A;

    @Pattern(regexp = SwiftFields.COMMENTS)
    private String field71D;

    @Pattern(regexp = SwiftFields.COMMENTS)
    private String field48;

//    @NotEmpty
    @Pattern(regexp = X_DATA_TYPE + "{0,7}")
    private String field49;

    @Pattern(regexp = X_DATA_TYPE + "{0,35}")
    private String field53A;

    private String field53D;

//    @Pattern(regexp = "([a-zA-Z\\Q/-?:().,'+\\E\\s]{0,65}){0,12}")
    private String field78;

    @Pattern(regexp = X_DATA_TYPE + "{0,35}")
    private String field57A;

    private String field57B;

    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,35}){0,6}")
    private String field72Z;

    private String field57D;
    

    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,100}){0,65}")
    private String field49G;
    
    @Pattern(regexp = SwiftFields.Z_DATA_TYPE + "{0,100}){0,65}")
    private String field49H;
    

    @Pattern(regexp = X_DATA_TYPE + "{0,35}")
    private String field58A;

    public MT700() {
        super();
    }

    public String getField49G() {
		return field49G;
	}

	public void setField49G(String field49g) {
		field49G = field49g;
	}

	public String getField49H() {
		return field49H;
	}

	public void setField49H(String field49h) {
		field49H = field49h;
	}

	@Override
    public int messageLimit(){
        return 10000;
    }

//    public List<SwiftMessage> getChainSwiftMessages(){
//        List<SwiftMessage> messages = new ArrayList<SwiftMessage>();
//        if(messageLimitReached()){
//            MT701 mt701 = new MT701();
//            mt701.setField20(this.getField20());
//            mt701.setField27("2/2");
//            mt701.setField45B(this.getField45A());
//            mt701.setField47B(this.getField47A());
//
//            this.setField45A(null);
//            this.setField47A(null);
//            this.setField27("1/2");
//
//            messages.add(mt701);
//        }
//
//        return messages;
//    }

    public String getField27() {
        return field27;
    }

    public void setField27(String field27) {
        this.field27 = field27;
    }

    public String getField40A() {
        return field40A;
    }

    public void setField40A(String field40A) {
        this.field40A = field40A;
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

    public String getField31C() {
        return field31C;
    }

    public void setField31C(String field31C) {
        this.field31C = field31C;
    }

    public String getField40E() {
        return field40E;
    }

    public void setField40E(String field40E) {
        this.field40E = field40E;
    }

    public String getField31D() {
        return field31D;
    }

    public void setField31D(String field31D) {
        this.field31D = field31D;
    }

    public String getField51A() {
        return field51A;
    }

    public void setField51A(String field51A) {
        this.field51A = field51A;
    }

    public String getField51D() {
        return field51D;
    }

    public void setField51D(String field51D) {
        this.field51D = field51D;
    }

    public String getField50() {
        return field50;
    }

    public void setField50(String field50) {
        this.field50 = field50;
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

    public String getField43P() {
        return field43P;
    }

    public void setField43P(String field43P) {
        this.field43P = field43P;
    }

    public String getField43T() {
        return field43T;
    }

    public void setField43T(String field43T) {
        this.field43T = field43T;
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

    public String getField45A() {
        return field45A;
    }

    public void setField45A(String field45A) {
        this.field45A = field45A;
    }

    public String getField46A() {
        return field46A;
    }

    public void setField46A(String field46A) {
        this.field46A = field46A;
    }

    public String getField47A() {
        return field47A;
    }

    public void setField47A(String field47A) {
        this.field47A = field47A;
    }

    public String getField71D() {
        return field71D;
    }

    public void setField71D(String field71D) {
        this.field71D = field71D;
    }

    public String getField48() {
        return field48;
    }

    public void setField48(String field48) {
        this.field48 = field48;
    }

    public String getField49() {
        return field49;
    }

    public void setField49(String field49) {
        this.field49 = field49;
    }

    public String getField58A() {
        return field58A;
    }

    public void setField58A(String field58A) {
        this.field58A = field58A;
    }

    public String getField53A() {
        return field53A;
    }

    public void setField53A(String field53A) {
        this.field53A = field53A;
    }

    public String getField78() {
        return field78;
    }

    public void setField78(String field78) {
        this.field78 = field78;
    }

    public String getField57A() {
        return field57A;
    }

    public void setField57A(String field57A) {
        this.field57A = field57A;
    }

    public String getField72Z() {
        return field72Z;
    }

    public void setField72Z(String field72Z) {
        this.field72Z = field72Z;
    }

    public String getField57D() {
        return field57D;
    }

    public void setField57D(String field57D) {
        this.field57D = field57D;
    }

    public String getField41D() {
        return field41D;
    }

    public void setField41D(String field41D) {
        this.field41D = field41D;
    }

    public String getField42D() {
        return field42D;
    }

    public void setField42D(String field42D) {
        this.field42D = field42D;
    }

    public String getField57B() {
        return field57B;
    }

    public void setField57B(String field57B) {
        this.field57B = field57B;
    }

    public String getField53D() {
        return field53D;
    }

    public void setField53D(String field53D) {
        this.field53D = field53D;
    }
}
