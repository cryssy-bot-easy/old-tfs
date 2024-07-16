package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ucpb.tfs.application.command.TransmitOutgoingMtCommand;
import com.ucpb.tfs.domain.mtmessage.MtMessage;
import com.ucpb.tfs.domain.mtmessage.MtMessageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 10/11/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TransmitOutgoingMtCommandHandler implements CommandHandler<TransmitOutgoingMtCommand> {

    @Inject
    MtMessageRepository mtMessageRepository;

    @Override
    public void handle(TransmitOutgoingMtCommand command) {

        try {
            Map<String, Object> parameterMap = command.getParameterMap();
            printParameters(parameterMap);

            Long id = Long.parseLong((String)parameterMap.get("id"));

            MtMessage mtMessage = mtMessageRepository.load(id);

            mtMessage.transmitMessage();

            mtMessageRepository.merge(mtMessage);

        } catch (Exception e) {

        }
    }

    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside transmit outgoing mt command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
