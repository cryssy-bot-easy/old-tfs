package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.TagAsPostedCommand;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.TradeServiceRoutedEvent;
import com.ucpb.tfs.domain.service.event.TradeServiceTaggedEvent;
import com.ucpb.tfs2.application.service.ServiceInstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/27/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TagAsPostedCommandHandler implements CommandHandler<TagAsPostedCommand> {

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ServiceInstructionService serviceInstructionService;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Override
    public void handle(TagAsPostedCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(parameterMap.get("routeTo").toString());
        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(parameterMap.get("username").toString());

        TradeServiceId tradeServiceId; // = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
        TradeService tradeService;

        Boolean isReversal = false;

        ServiceInstructionId originalEts = null;

        if(parameterMap.get("reversalDENumber") != null) {
            originalEts = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
            tradeServiceId  = new TradeServiceId((String)parameterMap.get("reversalDENumber"));

            isReversal = true;

        } else {
            tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
        }

        tradeService = tradeServiceRepository.load(tradeServiceId);

        // last user
        tradeService.updateLastUser(fromADUser);

        //tradeService.updateStatus(TradeServiceStatus.APPROVED, userActiveDirectoryId);
        tradeService.updateStatus(TradeServiceStatus.POSTED, userId);

        tradeServiceRepository.merge(tradeService);

        if(isReversal) {
            serviceInstructionService.reverseDE(originalEts);
        }

        // Fire event
        String gltsNumber = gltsSequenceRepository.getGltsSequence();
        TradeServiceTaggedEvent tradeServiceUpdatedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.POSTED, userActiveDirectoryId, gltsNumber);
        eventPublisher.publish(tradeServiceUpdatedEvent);

        TradeServiceRoutedEvent tradeServiceRoutedEvent = new TradeServiceRoutedEvent(tradeService, TradeServiceStatus.POSTED, fromADUser, userActiveDirectoryId);
        eventPublisher.publish(tradeServiceRoutedEvent);
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside tag as posted command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
