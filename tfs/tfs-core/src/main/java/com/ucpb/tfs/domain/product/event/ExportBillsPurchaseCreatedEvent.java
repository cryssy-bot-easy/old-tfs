package com.ucpb.tfs.domain.product.event;

import java.util.Map;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;

public class ExportBillsPurchaseCreatedEvent implements DomainEvent {
	
	private TradeServiceId tradeServiceId;
    
    public ExportBillsPurchaseCreatedEvent(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }
    
    public TradeServiceId getTradeServiceId() {
        return this.tradeServiceId;
    }
}
