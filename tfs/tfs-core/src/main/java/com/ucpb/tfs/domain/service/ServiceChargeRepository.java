package com.ucpb.tfs.domain.service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 7/29/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ServiceChargeRepository {

    public List<Map<String, Object>> getServiceChargeBaseAmount(TradeServiceId tradeServiceId);

    public List<Map<String, Object>> getSavedNewServiceCharges(TradeServiceId tradeServiceId);

    public Map<String, Object> getSavedNewCollectibleCharges(TradeServiceId tradeServiceId);
}
