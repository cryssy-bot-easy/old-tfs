package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.service.TradeService;

import java.math.BigDecimal;

public class CDTPaymentRequestPaidEvent implements DomainEvent {
	
	private final CDTPaymentRequest cdtPaymentRequest;
	private final TradeService tradeService;
	private BigDecimal totalAmountPaid;
    private String gltsNumber;
    
    public CDTPaymentRequestPaidEvent(CDTPaymentRequest cdtPaymentRequest, BigDecimal totalAmountPaid, String gltsNumber, TradeService tradeService){
    	this.cdtPaymentRequest = cdtPaymentRequest;
    	this.totalAmountPaid = totalAmountPaid;
    	this.gltsNumber = gltsNumber;
    	this.tradeService = tradeService;
    }
    
    public CDTPaymentRequest getCdtPaymentRequest() {
    	return cdtPaymentRequest;
    }
    
    public BigDecimal getTotalAmountPaid(){
    	return totalAmountPaid;
    }
    
    public String getGltsNumber(){
    	return gltsNumber;
    }
    
    public TradeService getTradeService() {
    	return tradeService;
    }
}
