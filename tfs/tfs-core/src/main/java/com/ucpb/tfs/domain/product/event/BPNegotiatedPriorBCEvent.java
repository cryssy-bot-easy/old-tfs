package com.ucpb.tfs.domain.product.event;

import java.math.BigDecimal;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.ExportBills;
import com.ucpb.tfs.domain.service.TradeService;

/**
(revision)
SCR/ER Number: 
SCR/ER Description: To handle Negotiation of EBP transaction with prior EBC transaction.
[Revised by:] Jonh Henry Alabin
[Date deployed:] June 16,2017
Program [Revision] Details: Add new Java(Event) for handling AMLA.
Member Type: Java
Project: CORE
Project Name: BPNegotiatedPriorBCEvent.java
 */

public class BPNegotiatedPriorBCEvent implements DomainEvent {

	private final TradeService tradeService;
	private final ExportBills exportBills;
    private String gltsNumber;
    
	
	public BPNegotiatedPriorBCEvent(TradeService tradeService, ExportBills exportBills, String gltsNumber) {
		this.tradeService = tradeService;
		this.exportBills = exportBills;
        this.gltsNumber = gltsNumber;       
	}
	
	public TradeService getTradeService(){
		return tradeService;
	}
	
	public ExportBills getExportBills(){
		return exportBills;
	}

    public String getGltsNumber() {
        return gltsNumber;
    }
    
   
}
