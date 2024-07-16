package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ucpb.tfs.application.command.SaveSettlementToBeneficiaryFormCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 8/13/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveSettlementToBeneficiaryFormCommandHandler implements CommandHandler<SaveSettlementToBeneficiaryFormCommand> {

    @Override
    public void handle(SaveSettlementToBeneficiaryFormCommand command) {
        // TODO handle save ets basic details command

        // temporary prints parameters
        printParameters(command.getParameterMap());
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside save settlement to beneficiary form command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
