package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveReimbursementDetailsFormCommand;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.reimbursing.InstructionToBank;
import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.event.TradeServiceUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/4/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveReimbursementDetailsFormCommandHandler implements CommandHandler<SaveReimbursementDetailsFormCommand> {

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Override
    public void handle(SaveReimbursementDetailsFormCommand command) {

        try {

            /*
            * Only called from Data Entry
            */

            Map<String, Object> parameterMap = command.getParameterMap();

            // temporary prints parameters
            printParameters(parameterMap);

            UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());

            TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            List<InstructionToBank> instructionToBankList = new ArrayList<InstructionToBank>();

            for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("instructionToBankList")) {
                InstructionToBankCode instructionToBankCode = new InstructionToBankCode((String) map.get("instructionToBankCode"));

                InstructionToBank instructionToBank = new InstructionToBank(instructionToBankCode, (String) map.get("instruction"));

                instructionToBankList.add(instructionToBank);
            }

            // clears all instructions to bank if exists
            if(tradeService.getInstructionToBank() != null || !tradeService.getInstructionToBank().isEmpty()) {
                tradeServiceRepository.deleteInstructionsToBank(tradeService.getTradeServiceId());
            }

            // adds new set of instructions to bank
            tradeService.addInstructionToBank(instructionToBankList);

            // remove instructionToBankList from parameterMap to update tradeService details
            if(parameterMap.containsKey("instructionToBankList")) {
                parameterMap.remove("instructionToBankList");
            }

            // Update TradeService
            tradeService = TradeServiceService.updateTradeServiceDetails(tradeService, parameterMap, userActiveDirectoryId, "N");
            tradeServiceRepository.merge(tradeService);

            // Fire event
            // This updates basic details so no status is passed.
            TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, userActiveDirectoryId);
            eventPublisher.publish(tradeServiceUpdatedEvent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside save reimbursement details form command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
