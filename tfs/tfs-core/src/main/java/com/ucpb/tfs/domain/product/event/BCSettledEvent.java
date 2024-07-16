package com.ucpb.tfs.domain.product.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.ExportBills;
import com.ucpb.tfs.domain.service.TradeService;

public class BCSettledEvent implements DomainEvent {

	private final TradeService tradeService;
	private final ExportBills exportBills;
    private String gltsNumber;
	
	public BCSettledEvent(TradeService tradeService, ExportBills exportBills, String gltsNumber) {
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
