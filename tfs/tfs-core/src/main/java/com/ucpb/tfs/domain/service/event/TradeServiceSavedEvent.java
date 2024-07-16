package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;

import java.util.Map;

/**
 * User: IPCVal
 * Date: 8/15/12
 */
public class TradeServiceSavedEvent implements DomainEvent {

    private TradeServiceId tradeServiceId;
    private Map<String, Object> parameterMap;
    private TradeServiceStatus tradeServiceStatus;
    private UserActiveDirectoryId userActiveDirectoryId;

    public TradeServiceSavedEvent() {}

    public TradeServiceSavedEvent(TradeServiceId tradeServiceId, Map<String, Object> parameterMap, UserActiveDirectoryId userActiveDirectoryId) {
        this.tradeServiceId = tradeServiceId;
        this.parameterMap = parameterMap;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public TradeServiceSavedEvent(TradeServiceId tradeServiceId, Map<String, Object> parameterMap, TradeServiceStatus tradeServiceStatus, UserActiveDirectoryId userActiveDirectoryId) {
        this.tradeServiceId = tradeServiceId;
        this.parameterMap = parameterMap;
        this.tradeServiceStatus = tradeServiceStatus;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public TradeServiceStatus getTradeServiceStatus() {
        return tradeServiceStatus;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }
}
