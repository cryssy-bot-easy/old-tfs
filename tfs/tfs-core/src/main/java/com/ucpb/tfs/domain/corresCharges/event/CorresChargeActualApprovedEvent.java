package com.ucpb.tfs.domain.corresCharges.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.corresCharges.CorresChargeActual;
import com.ucpb.tfs.domain.product.TradeProduct;
import com.ucpb.tfs.domain.service.TradeService;

/**
 * User: IPCVal
 * Date: 1/31/13
 */
public class CorresChargeActualApprovedEvent implements DomainEvent {

    private CorresChargeActual corresChargeActual;
    private Boolean hasReference;
    private TradeService tradeService;
    private TradeProduct tradeProduct;
    private String gltsNumber;

    public CorresChargeActualApprovedEvent(CorresChargeActual corresChargeActual, Boolean hasReference, TradeService tradeService, TradeProduct tradeProduct, String gltsNumber) {
        this.corresChargeActual = corresChargeActual;
        this.hasReference = hasReference;
        this.tradeService = tradeService;
        this.tradeProduct = tradeProduct;
        this.gltsNumber = gltsNumber;
    }

    public CorresChargeActual getCorresChargeActual() {
        return corresChargeActual;
    }

    public Boolean getHasReference() {
        return hasReference;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public TradeProduct getTradeProduct() {
        return tradeProduct;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
