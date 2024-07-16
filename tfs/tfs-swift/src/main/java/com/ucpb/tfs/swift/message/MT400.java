package com.ucpb.tfs.swift.message;

import javax.validation.constraints.Pattern;

/**
 */
public class MT400 extends SwiftMessage{

    private String field20;

    private String field21;

    private String field32A;

    private String field32B;

    private String field32K;

    private String field33A;

    private String field52A;

    private String field52D;

    private String field53A;

    private String field53B;

    private String field53D;

    private String field54A;

    private String field54B;

    private String field54D;

    private String field57A;

    private String field57D;

    private String field58A;

    private String field58B;

    private String field58D;

    private String field71B;

    @Pattern(regexp = SwiftFields.SENDER_TO_RECEIVER_INFO)
    private String field72;

    private String field73;


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

    public String getField32A() {
        return field32A;
    }

    public void setField32A(String field32A) {
        this.field32A = field32A;
    }

    public String getField32B() {
        return field32B;
    }

    public void setField32B(String field32B) {
        this.field32B = field32B;
    }

    public String getField32K() {
        return field32K;
    }

    public void setField32K(String field32K) {
        this.field32K = field32K;
    }

    public String getField33A() {
        return field33A;
    }

    public void setField33A(String field33A) {
        this.field33A = field33A;
    }

    public String getField52A() {
        return field52A;
    }

    public void setField52A(String field52A) {
        this.field52A = field52A;
    }

    public String getField52D() {
        return field52D;
    }

    public void setField52D(String field52D) {
        this.field52D = field52D;
    }

    public String getField53A() {
        return field53A;
    }

    public void setField53A(String field53A) {
        this.field53A = field53A;
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

    public String getField54A() {
        return field54A;
    }

    public void setField54A(String field54A) {
        this.field54A = field54A;
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

    public String getField57A() {
        return field57A;
    }

    public void setField57A(String field57A) {
        this.field57A = field57A;
    }

    public String getField57D() {
        return field57D;
    }

    public void setField57D(String field57D) {
        this.field57D = field57D;
    }

    public String getField58A() {
        return field58A;
    }

    public void setField58A(String field58A) {
        this.field58A = field58A;
    }

    public String getField58B() {
        return field58B;
    }

    public void setField58B(String field58B) {
        this.field58B = field58B;
    }

    public String getField58D() {
        return field58D;
    }

    public void setField58D(String field58D) {
        this.field58D = field58D;
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

    public String getField73() {
        return field73;
    }

    public void setField73(String field73) {
        this.field73 = field73;
    }
}
