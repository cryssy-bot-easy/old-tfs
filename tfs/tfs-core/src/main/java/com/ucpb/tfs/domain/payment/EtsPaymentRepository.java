package com.ucpb.tfs.domain.payment;

import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;

public interface EtsPaymentRepository {
	
	public EtsPayment get(TradeServiceId serviceId, ChargeType chargeType);
	
    public void persist(EtsPayment etsPayment);

    public void delete(EtsPayment EtsPayment);

}
