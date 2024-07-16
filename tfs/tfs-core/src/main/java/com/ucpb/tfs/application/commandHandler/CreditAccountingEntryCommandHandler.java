package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ucpb.tfs.application.command.CreditAccountingEntryCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 8/28/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class CreditAccountingEntryCommandHandler implements CommandHandler<CreditAccountingEntryCommand> {

    @Override
    public void handle(CreditAccountingEntryCommand command) {
        // TODO handle save ets basic details command

        // temporary prints parameters
        printParameters(command.getParameterMap());
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside credit accounting entry command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
