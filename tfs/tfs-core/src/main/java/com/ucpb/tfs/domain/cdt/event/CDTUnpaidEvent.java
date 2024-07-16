package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 10/31/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class CDTUnpaidEvent implements DomainEvent {

    private String iedieirdNumber;
    private TradeServiceId tradeServiceId;

    public CDTUnpaidEvent(String iedieirdNumber, TradeServiceId tradeServiceId) {
        this.iedieirdNumber = iedieirdNumber;
        this.tradeServiceId = tradeServiceId;
    }

    public String getIedieirdNumber() {
        return iedieirdNumber;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }
}
