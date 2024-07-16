package com.ucpb.tfs.domain.mtmessage;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.audit.Direction;
import com.ucpb.tfs.domain.mtmessage.enumTypes.MessageClass;
import com.ucpb.tfs.domain.mtmessage.enumTypes.MtStatus;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceReferenceNumber;
import com.ucpb.tfs.utils.UtilSetFields;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Marv
 * Date: 10/10/12
 */

public class MtMessage implements Serializable {

    private Long id;

    private TradeServiceReferenceNumber tradeServiceReferenceNumber;

    private TradeServiceId tradeServiceId;

    private DocumentNumber documentNumber;

    private MtStatus mtStatus;

    private Date dateReceived;
    
    private String mtType; // TODO: make this an enum?

    private String message;
    
    private String instruction;

    private UserActiveDirectoryId userRoutedTo;
    
    private Date modifiedDate;

    private Direction mtDirection;

    private String filename;

    private Integer sequenceNumber;

    private Integer sequenceTotal;

    private MessageClass messageClass;

    public MtMessage() {
    }

    public MtMessage(String documentNumber, MessageClass messageClass, String message, String mtType){
        this.messageClass = messageClass;
        this.documentNumber = new DocumentNumber(documentNumber);
        this.message = message;
        this.mtType = mtType;
        this.mtStatus = MtStatus.NEW;
        this.dateReceived = new Date();
    }

    public void updateDetails(Map<String, Object> parameterMap) {
        this.mtStatus = MtStatus.UPDATED;
        this.modifiedDate = new Date();
        
        UtilSetFields.copyMapToObject(this, (HashMap) parameterMap);
    }

    public void closeMessage(Map<String, Object> parameterMap, TradeServiceReferenceNumber tradeServiceReferenceNumber, DocumentNumber documentNumber) {
        this.mtStatus = MtStatus.DONE;
        this.modifiedDate = new Date();

        this.documentNumber = documentNumber;
        this.tradeServiceReferenceNumber = tradeServiceReferenceNumber;

        UtilSetFields.copyMapToObject(this, (HashMap) parameterMap);
    }
    
    public void routeMessage(UserActiveDirectoryId userRoutedTo) {
        this.userRoutedTo = userRoutedTo;
        this.modifiedDate = new Date();

        this.mtStatus = MtStatus.PREPARED;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public void returnMessage() {
        this.modifiedDate = new Date();

        this.mtStatus = MtStatus.RETURNED;
    }

    public void transmitMessage() {
        this.modifiedDate = new Date();

        this.mtStatus = MtStatus.TRANSMITTED;
    }
    
	public void discardMessage() {
		this.modifiedDate = new Date();
		
		this.mtStatus = MtStatus.DISCARDED;
	}
	
	public void reverseMessage() {
		this.modifiedDate = new Date();
		
		this.mtStatus = MtStatus.NEW;
	}

    public String getMessage() {
        return message;
    }

    public String getMtType() {
        return  mtType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getSequenceTotal() {
        return sequenceTotal;
    }

    public void setSequenceTotal(Integer sequenceTotal) {
        this.sequenceTotal = sequenceTotal;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public MessageClass getMessageClass() {
        return messageClass;
    }

    public Long getId() {
        return id;
    }

    public TradeServiceReferenceNumber getTradeServiceReferenceNumber() {
        return tradeServiceReferenceNumber;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public MtStatus getMtStatus() {
        return mtStatus;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public String getInstruction() {
        return instruction;
    }

    public UserActiveDirectoryId getUserRoutedTo() {
        return userRoutedTo;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public Direction getMtDirection() {
        return mtDirection;
    }
}
