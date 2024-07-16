package com.ucpb.tfs.application.commandHandler;

/**
 *
 * @author Marvin Volante <marvin.volante@incuventure.net>
 *
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.instruction.TagAsAbortedCommand;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionTaggedEvent;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.TradeServiceTaggedEvent;
import com.ucpb.tfs2.application.service.TradeServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TagAsAbortedCommandHandler implements CommandHandler<TagAsAbortedCommand>{

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Inject
    TradeServiceService tradeServiceService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Override
	public void handle(TagAsAbortedCommand command) {

        // TODO handle save ets basic details command

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        // printParameters(parameterMap);

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
        UserId userId = new UserId(parameterMap.get("username").toString());

        if (((String)parameterMap.get("referenceType")).equals("ETS")) {

            // Load from repository
            ServiceInstructionId etsNumber;

            if(parameterMap.get("reversalEtsNumber") != null) {
                etsNumber = new ServiceInstructionId((String)parameterMap.get("reversalEtsNumber"));
                System.out.println("reversal: " + etsNumber.toString());

                tradeServiceService.returnToDefaultStatus(new ServiceInstructionId((String)parameterMap.get("etsNumber")));
            } else {
                etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
                System.out.println("normal: " + etsNumber.toString());
            }

            ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

            // Set status
//            ets.updateStatus(ServiceInstructionStatus.ABORTED, userActiveDirectoryId);
            ets.updateStatus(ServiceInstructionStatus.ABORTED, userId);

            // sets last user
            ets.setLastUser(userId);

            // Persist
            serviceInstructionRepository.merge(ets);

            // Fire event
            ServiceInstructionTaggedEvent etsUpdatedEvent = new ServiceInstructionTaggedEvent(ets, ServiceInstructionStatus.ABORTED, userActiveDirectoryId);
            eventPublisher.publish(etsUpdatedEvent);

            // Add to token registry
            tokenProvider.addTokenForId(command.getToken(), ets.getServiceInstructionId().toString());

        } else {

            TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            // last user
            tradeService.updateLastUser(userActiveDirectoryId);

            //tradeService.updateStatus(TradeServiceStatus.ABORTED, userActiveDirectoryId);
            tradeService.updateStatus(TradeServiceStatus.ABORTED, userId);

            tradeServiceRepository.merge(tradeService);

            // Fire event
            String gltsNumber = gltsSequenceRepository.getGltsSequence();
            TradeServiceTaggedEvent tradeServiceUpdatedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.ABORTED, userActiveDirectoryId, gltsNumber);
            eventPublisher.publish(tradeServiceUpdatedEvent);
        }
    }
	
	// temporary prints parameters
	private void printParameters(Map<String, Object> parameterMap) {
		System.out.println("inside tag as aborted command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}
}
