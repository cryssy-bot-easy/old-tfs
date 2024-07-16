package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;

import java.util.Map;

/**
 * User: IPCVal
 * Date: 8/17/12
 */
public class TradeServiceUpdatedEvent implements DomainEvent {

    private TradeServiceId tradeServiceId;
    private TradeServiceStatus tradeServiceStatus;
    private UserActiveDirectoryId userActiveDirectoryId;

    private Map<String, Object> parameterMap;

    public TradeServiceUpdatedEvent() {}

    public TradeServiceUpdatedEvent(TradeServiceId tradeServiceId, Map<String, Object> parameterMap, UserActiveDirectoryId userActiveDirectoryId) {
        this.tradeServiceId = tradeServiceId;
        this.parameterMap = parameterMap;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public TradeServiceUpdatedEvent(TradeServiceId tradeServiceId, Map<String, Object> parameterMap, TradeServiceStatus tradeServiceStatus, UserActiveDirectoryId userActiveDirectoryId) {
        this.tradeServiceId = tradeServiceId;
        this.parameterMap = parameterMap;
        this.tradeServiceStatus = tradeServiceStatus;
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public TradeServiceId getTradeServiceId() {
        return this.tradeServiceId;
    }

    public Map<String, Object> getParameterMap() {
        return this.parameterMap;
    }

    public TradeServiceStatus getTradeServiceStatus() {
        return this.tradeServiceStatus;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return this.userActiveDirectoryId;
    }
}
