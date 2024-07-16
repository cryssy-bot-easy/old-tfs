package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.service.TradeService;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 10/29/13
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CDTPaidEvent implements DomainEvent {

	private final TradeService tradeService;
    private String iedieirdNumber;
    private Payment payment;
    private String processingUnitCode;


    public CDTPaidEvent(String iedieirdNumber, Payment payment, String processingUnitCode, TradeService tradeService) {
        this.iedieirdNumber = iedieirdNumber;
        this.payment = payment;
        this.processingUnitCode = processingUnitCode;
        this.tradeService = tradeService;
    }

    public String getIedieirdNumber() {
        return iedieirdNumber;
    }

    public Payment getPayment() {
        return payment;
    }

    public String getProcessingUnitCode() {
        return processingUnitCode;
    }
    
    public TradeService getTradeService() {
    	return tradeService;
    }
}
