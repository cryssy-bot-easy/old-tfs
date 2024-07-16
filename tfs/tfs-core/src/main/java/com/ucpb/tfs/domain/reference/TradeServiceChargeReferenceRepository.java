package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.List;

/**
 * User: Jett
 * Date: 7/24/12
 */
public interface TradeServiceChargeReferenceRepository {

    public void save(TradeServiceChargeReference tradeServiceCharge);

    public List<TradeServiceChargeReference> getCharges(ProductId productId, ServiceType serviceType);

    public Long getCount();

    public void clear();

}
