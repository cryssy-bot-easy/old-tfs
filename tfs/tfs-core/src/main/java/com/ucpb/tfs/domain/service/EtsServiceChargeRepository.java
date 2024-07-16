package com.ucpb.tfs.domain.service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Giancarlo Angulo
 * Date: 1/7/14
 * Time: 5:39 PM
 */
public interface EtsServiceChargeRepository {
//    public List<Map<String, Object>> getEtsServiceCharges(TradeServiceId tradeServiceId);

    public List<EtsServiceCharge> getEtsServiceChargeList(TradeServiceId tradeServiceId);

    public void persist(EtsServiceCharge etsServiceCharge);

    public void delete(TradeServiceId tradeServiceId);

}
