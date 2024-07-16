package com.ucpb.tfs.domain.service.event;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 */
@Component
public class TradeServiceTaggedEvent extends TradeServiceUpdatedEvent {

    private String gltsNumber;

    public TradeServiceTaggedEvent() {}

    public TradeServiceTaggedEvent(TradeServiceId tradeServiceId, Map<String, Object> parameterMap, UserActiveDirectoryId userActiveDirectoryId, String gltsNumber) {
        super(tradeServiceId, parameterMap, userActiveDirectoryId);
        this.gltsNumber = gltsNumber;
    }

    public TradeServiceTaggedEvent(TradeServiceId tradeServiceId, Map<String, Object> parameterMap, TradeServiceStatus tradeServiceStatus, UserActiveDirectoryId userActiveDirectoryId, String gltsNumber) {

        super(tradeServiceId, parameterMap, tradeServiceStatus, userActiveDirectoryId);
        this.gltsNumber = gltsNumber;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}