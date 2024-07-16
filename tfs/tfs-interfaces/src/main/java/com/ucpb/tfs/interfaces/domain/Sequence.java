package com.ucpb.tfs.interfaces.domain;

import java.util.Date;

/**
 */
public class Sequence {

    private long sequenceNumber;

    private Date dateInitialized;

    private String sequenceType;

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Date getDateInitialized() {
        return dateInitialized;
    }

    public void setDateInitialized(Date dateInitialized) {
        this.dateInitialized = dateInitialized;
    }

    public void increment(){
        this.sequenceNumber++;
//        this.dateInitialized = new Date();
    }

    public String getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(String sequenceType) {
        this.sequenceType = sequenceType;
    }
}
