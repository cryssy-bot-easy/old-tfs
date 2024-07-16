package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ucpb.tfs.domain.product.UALoanSettledEvent;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccount;
import com.ucpb.tfs.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus.APPROVED;
import static com.ucpb.tfs.domain.service.enumTypes.DocumentClass.UA;
import static com.ucpb.tfs.domain.service.enumTypes.ServiceType.SETTLEMENT;

/**
 */
@Component
public class LoanSettlementEventListener {


    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

//    @EventListener
    public void generateSwiftForUALoanSettlement(TradeServiceTaggedEvent tradeServiceTaggedEvent){
         if(APPROVED.equals(tradeServiceTaggedEvent.getTradeServiceStatus())){
             TradeService tradeService = tradeServiceRepository.load(tradeServiceTaggedEvent.getTradeServiceId());

             if(UA.equals(tradeService.getDocumentNumber()) && SETTLEMENT.equals(tradeService.getServiceType())){
                //TODO: persist tradeservice to a UA Loan Domain Object.

                 eventPublisher.publish(new UALoanSettledEvent(tradeService));
             }
         }
    }

}
