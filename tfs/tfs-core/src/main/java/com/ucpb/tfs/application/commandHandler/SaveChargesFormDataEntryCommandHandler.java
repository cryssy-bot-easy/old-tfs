package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveChargesFormCommand;
import com.ucpb.tfs.application.command.SaveChargesFormDataEntryCommand;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
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
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/20/13
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveChargesFormDataEntryCommandHandler implements CommandHandler<SaveChargesFormDataEntryCommand> {

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;


    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Override
    public void handle(SaveChargesFormDataEntryCommand command) {
        System.out.println("SaveChargesFormDataEntryCommand");

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        System.out.println("referenceType:" + parameterMap.get("referenceType").toString() + "|");


        try {

            String username;
            UserActiveDirectoryId userActiveDirectoryId;

            if (command.getUserActiveDirectoryId() == null) {
                username = parameterMap.get("username").toString();
                userActiveDirectoryId = new UserActiveDirectoryId(username);
            } else {
                userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
            }

            /*
            * Currently this is only invoked from ETS, but check just the same :)
            */
            System.out.println("true or false:" + parameterMap.get("referenceType").toString().equalsIgnoreCase("ETS"));

            //TODO Fix this. SaveChargesFormCommandHandler must overwrite charges
            System.out.println("TODO Fix this. SaveChargesFormCommandHandler must overwrite charges");

            // Load from repository using ETS number
            TradeServiceId tradeServiceId = new TradeServiceId((String) parameterMap.get("tradeServiceId"));
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);


            // 1) Delete charges first before saving a new set
            tradeServiceRepository.deleteServiceCharges(tradeService.getTradeServiceId());

            //1.1) get all fees into one map
            //1.2) compare with current service charges
            parameterMap.remove("mainCifNumber");
            parameterMap.remove("mainCifName");
            parameterMap.remove("amount");
            parameterMap.remove("amountFrom");
            parameterMap.remove("productAmount");
            tradeService.updateDetails(parameterMap, userActiveDirectoryId);

            // 2) Now add Service Charges
            tradeService = TradeServiceService.addServiceChargesForParamMap(tradeService, tradeService.getDetails(), parameterMap, userActiveDirectoryId);

            tradeService.getDetails().put("chargesOverridenFlag", "Y");

            //Save Rates in Trade Service
            tradeService.updateServiceChargeRates(parameterMap);
            //tradeService.printRates();

            // 3) Use saveOrUpdate to persist
            tradeServiceRepository.saveOrUpdate(tradeService);


            // Add to token registry
            tokenProvider.addTokenForId(command.getToken(), tradeService.getDocumentNumber().toString());

            // Fire event
            // This updates Service Charges so no status is passed.
            TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, userActiveDirectoryId);
            eventPublisher.publish(tradeServiceUpdatedEvent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside save charges form data entry command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
