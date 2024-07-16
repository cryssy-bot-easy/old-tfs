package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.TagAsPreApprovedCommand;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.TradeServiceRoutedEvent;
import com.ucpb.tfs.domain.service.event.TradeServiceTaggedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/24/12
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TagAsPreApprovedCommandHandler implements CommandHandler<TagAsPreApprovedCommand> {

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Override
    public void handle(TagAsPreApprovedCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(parameterMap.get("routeTo").toString());
        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(parameterMap.get("username").toString());

        TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        // last user
        tradeService.updateLastUser(fromADUser);

        tradeService.updateStatus(TradeServiceStatus.PRE_APPROVED, userId);

        tradeServiceRepository.merge(tradeService);

        // Fire event
        String gltsNumber = gltsSequenceRepository.getGltsSequence();
        TradeServiceTaggedEvent tradeServiceUpdatedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.PRE_APPROVED, userActiveDirectoryId, gltsNumber);
        eventPublisher.publish(tradeServiceUpdatedEvent);

        TradeServiceRoutedEvent tradeServiceRoutedEvent = new TradeServiceRoutedEvent(tradeService, TradeServiceStatus.PRE_APPROVED, fromADUser, userActiveDirectoryId);
        eventPublisher.publish(tradeServiceRoutedEvent);
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside tag as pre approved command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
