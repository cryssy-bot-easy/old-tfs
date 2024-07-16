package com.ucpb.tfs.domain.service.event.charge;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.service.ServiceCharge;
import com.ucpb.tfs.domain.service.TradeServiceId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 7/9/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */

public class ChargesTabSavedEvent implements DomainEvent {

    private ServiceInstructionId serviceInstructionId;
    private TradeServiceId tradeServiceId;

    public ChargesTabSavedEvent() {}

    public ChargesTabSavedEvent(ServiceInstructionId serviceInstructionId, TradeServiceId tradeServiceId) {
        this.serviceInstructionId = serviceInstructionId;
        this.tradeServiceId = tradeServiceId;
    }

    public ServiceInstructionId getServiceInstructionId() {
        return serviceInstructionId;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

}
