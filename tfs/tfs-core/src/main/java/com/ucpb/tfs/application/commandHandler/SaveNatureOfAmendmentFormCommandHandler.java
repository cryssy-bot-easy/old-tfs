package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveNatureOfAmendmentFormCommand;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionUpdatedEvent;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.event.TradeServiceUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 8/13/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveNatureOfAmendmentFormCommandHandler implements CommandHandler<SaveNatureOfAmendmentFormCommand> {

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    ChargesService chargesService;

    @Override
    public void handle(SaveNatureOfAmendmentFormCommand command) {
        try {

            /*
            * Combines ETS and Data Entry handling
            */

            Map<String, Object> parameterMap = command.getParameterMap();


            String username = parameterMap.get("username").toString();
            System.out.println("username:" + username);

            UserActiveDirectoryId userActiveDirectoryId;


            userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());


            // temporary prints parameters
            printParameters(parameterMap);

            if (((String) parameterMap.get("referenceType")).equals("ETS")) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ETS");

                // Load from repository
                ServiceInstructionId etsNumber = new ServiceInstructionId((String) parameterMap.get("etsNumber"));
                ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

                // Update details
                // setDetails = false if update details
                ets.updateDetails(parameterMap, userActiveDirectoryId);

                // Persist update
                serviceInstructionRepository.merge(ets);


                TradeServiceId tradeServiceId = new TradeServiceId((String) parameterMap.get("tradeServiceId"));
                TradeService tradeService = tradeServiceRepository.load(tradeServiceId);


                parameterMap.put("outstandingBalance", tradeService.getDetails().get("outstandingBalance"));
                parameterMap.put("amount", tradeService.getDetails().get("outstandingBalance"));
                parameterMap.put("amountFrom", tradeService.getDetails().get("outstandingBalance"));
                tradeService = TradeServiceService.updateTradeServiceDetails(tradeService, parameterMap, userActiveDirectoryId, "Y");

                tradeService.getDetails().put("chargesOverridenFlag","N");
                chargesService.applyCharges(tradeService, ets, parameterMap);

                System.out.println("save or update");
                tradeServiceRepository.merge(tradeService);


                // Fire event
                ServiceInstructionUpdatedEvent etsUpdatedEvent = new ServiceInstructionUpdatedEvent(ets, userActiveDirectoryId);
                eventPublisher.publish(etsUpdatedEvent);

            } else {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>TRADESERVICE");
                TradeServiceId tradeServiceId = new TradeServiceId((String) parameterMap.get("tradeServiceId"));
                TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

                // Load from repository
                ServiceInstructionId etsNumber = new ServiceInstructionId((String) parameterMap.get("etsNumber"));
                ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

                // During AMENDMENT, only save the amended values in TradeService
                // The LC will be updated after approval
                tradeService = TradeServiceService.updateTradeServiceDetails(tradeService, parameterMap, userActiveDirectoryId, "N");

                tradeService.getDetails().put("chargesOverridenFlag","N");
                chargesService.applyCharges(tradeService, ets, parameterMap);
                tradeServiceRepository.merge(tradeService);

                // Fire event
                // This updates nature of amendment details so no status is passed.
                TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, userActiveDirectoryId);
                eventPublisher.publish(tradeServiceUpdatedEvent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside save nature of amendment command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
