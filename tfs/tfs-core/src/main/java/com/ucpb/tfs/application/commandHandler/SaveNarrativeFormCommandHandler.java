package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveNarrativeFormCommand;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 9/17/12
 */
/**
 * Description:   Added saving of narrative for 747.
 * Modified by:   Cedrick C. Nungay
 * Date Modified: 09/13/2018
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveNarrativeFormCommandHandler implements CommandHandler<SaveNarrativeFormCommand> {

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Override
    public void handle(SaveNarrativeFormCommand command) {
        // TODO handle save ets basic details command

        // temporary prints parameters
        printParameters(command.getParameterMap());

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());

        Map<String, Object> parameterMap = command.getParameterMap();

        if (((String)parameterMap.get("referenceType")).equals("DATA_ENTRY")) {

            TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            Map<String,Object> narrative = new HashMap<String,Object>();

            narrative.put("narrative", parameterMap.get("narrative"));
            narrative.put("narrativeFor747", parameterMap.get("narrativeFor747"));

            tradeService.updateDetails(narrative, userActiveDirectoryId);

            tradeServiceRepository.merge(tradeService);
        }

    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside save narrative form command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
