package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.service.TradeServiceId;

/**
 * User: IPCVal
 * Date: 9/22/12
 */
public interface LCNegotiationRepository {

    public void persist(LCNegotiation lcNegotiation);

    public void update(LCNegotiation lcNegotiation);

    public void merge(LCNegotiation lcNegotiation);

    public LCNegotiation load(TradeServiceId tradeServiceId);
}
