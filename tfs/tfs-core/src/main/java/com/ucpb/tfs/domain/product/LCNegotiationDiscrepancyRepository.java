package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 * User: IPCVal
 * Date: 10/23/12
 */
public interface LCNegotiationDiscrepancyRepository {

    public void persist(LCNegotiationDiscrepancy lcNegotiationDiscrepancy);

    public void update(LCNegotiationDiscrepancy lcNegotiationDiscrepancy);

    public void merge(LCNegotiationDiscrepancy lcNegotiationDiscrepancy);

    public LCNegotiationDiscrepancy load(TradeServiceId tradeServiceId);
}
