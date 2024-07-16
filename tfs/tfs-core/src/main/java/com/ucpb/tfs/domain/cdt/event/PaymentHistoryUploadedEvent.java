package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 11/27/13
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentHistoryUploadedEvent implements DomainEvent {

    private String iedieirdNumber;
    private String collectionLine;
    private String collectionAgencyCode;
    private String collectionChannel;
    //private Boolean isCheck;

    public PaymentHistoryUploadedEvent(String iedieirdNumber,
                                       String collectionLine,
                                       String collectionAgencyCode,
                                       String collectionChannel) {
//                                       Boolean isCheck) {
        this.iedieirdNumber = iedieirdNumber;
        this.collectionLine = collectionLine;
        this.collectionAgencyCode = collectionAgencyCode;
        this.collectionChannel = collectionChannel;
//        this.isCheck = isCheck;
    }

    public String getIedieirdNumber() {
        return iedieirdNumber;
    }

    public String getCollectionLine() {
        return collectionLine;
    }

    public String getCollectionAgencyCode() {
        return collectionAgencyCode;
    }

    public String getCollectionChannel() {
        return collectionChannel;
    }

//    public Boolean getIsCheck() {
//        return isCheck;
//    }
}
