package com.ucpb.tfs.swift.message;

import com.ucpb.tfs.swift.validation.ConditionalSpel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 */
@ConditionalSpel.List(
    {
        @ConditionalSpel(condition = "#this?.field23B?.contains('SPRI')", spelValidation = "#this?.field23E")
    }

)
public class MT103 extends SwiftMessage {

    @NotEmpty
    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    private String field13C;

    @NotEmpty
    private String field23B;

    private String field23E;

    private String field26T;

    @NotEmpty
    private String field32A;

    private String field33B;

    private String field36;

    private String field50A;

    private String field50F;

    private String field50K;

    private String field51A;

    private String field52A;

    private String field53A;

    private String field54A;

    private String field55A;

    private String field56A;

    private String field57A;

    private String field57B;

    private String field57C;

    private String field57D;

    private String field59;

    private String field59A;

    private String field70;

    @NotEmpty
    private String field71A;

    private String field71F;

    private String field71G;

    private String field72;

    private String field77B;

    private String field77T;


    public String getField20() {
        return field20;
    }

    public void setField20(String field20) {
        this.field20 = field20;
    }

    public String getField13C() {
        return field13C;
    }

    public void setField13C(String field13C) {
        this.field13C = field13C;
    }

    public String getField23B() {
        return field23B;
    }

    public void setField23B(String field23B) {
        this.field23B = field23B;
    }

    public String getField23E() {
        return field23E;
    }

    public void setField23E(String field23E) {
        this.field23E = field23E;
    }

    public String getField26T() {
        return field26T;
    }

    public void setField26T(String field26T) {
        this.field26T = field26T;
    }

    public String getField32A() {
        return field32A;
    }

    public void setField32A(String field32A) {
        this.field32A = field32A;
    }

    public String getField33B() {
        return field33B;
    }

    public void setField33B(String field33B) {
        this.field33B = field33B;
    }

    public String getField36() {
        return field36;
    }

    public void setField36(String field36) {
        this.field36 = field36;
    }

    public String getField50A() {
        return field50A;
    }

    public void setField50A(String field50A) {
        this.field50A = field50A;
    }

    public String getField51A() {
        return field51A;
    }

    public void setField51A(String field51A) {
        this.field51A = field51A;
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

    public String getField55A() {
        return field55A;
    }

    public void setField55A(String field55A) {
        this.field55A = field55A;
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

    public String getField59A() {
        return field59A;
    }

    public void setField59A(String field59A) {
        this.field59A = field59A;
    }

    public String getField70() {
        return field70;
    }

    public void setField70(String field70) {
        this.field70 = field70;
    }

    public String getField71A() {
        return field71A;
    }

    public void setField71A(String field71A) {
        this.field71A = field71A;
    }

    public String getField71F() {
        return field71F;
    }

    public void setField71F(String field71F) {
        this.field71F = field71F;
    }

    public String getField71G() {
        return field71G;
    }

    public void setField71G(String field71G) {
        this.field71G = field71G;
    }

    public String getField72() {
        return field72;
    }

    public void setField72(String field72) {
        this.field72 = field72;
    }

    public String getField77B() {
        return field77B;
    }

    public void setField77B(String field77B) {
        this.field77B = field77B;
    }

    public String getField77T() {
        return field77T;
    }

    public void setField77T(String field77T) {
        this.field77T = field77T;
    }

    public String getField50F() {
        return field50F;
    }

    public void setField50F(String field50F) {
        this.field50F = field50F;
    }

    public String getField50K() {
        return field50K;
    }

    public void setField50K(String field50K) {
        this.field50K = field50K;
    }

    public String getField57B() {
        return field57B;
    }

    public void setField57B(String field57B) {
        this.field57B = field57B;
    }

    public String getField57C() {
        return field57C;
    }

    public void setField57C(String field57C) {
        this.field57C = field57C;
    }

    public String getField57D() {
        return field57D;
    }

    public void setField57D(String field57D) {
        this.field57D = field57D;
    }

    public String getField59() {
        return field59;
    }

    public void setField59(String field59) {
        this.field59 = field59;
    }
}
