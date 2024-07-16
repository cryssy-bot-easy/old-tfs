package com.ucpb.tfs.swift.message;

import javax.validation.constraints.Pattern;

/**
 */
public class MT412 extends SwiftMessage{

    //TODO: field20,21,32A group is repeatable
    private String field20;

    private String field21;

    private String field32A;

    @Pattern(regexp = SwiftFields.SENDER_TO_RECEIVER_INFO)
    private String field72;


    public String getField72() {
        return field72;
    }

    public void setField72(String field72) {
        this.field72 = field72;
    }
}
