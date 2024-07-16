package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;

import com.incuventure.cqrs.token.TokenProvider;
import com.ucpb.tfs.application.command.CloseIncomingMtCommand;
import com.ucpb.tfs.domain.mtmessage.MtMessage;
import com.ucpb.tfs.domain.mtmessage.MtMessageRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceReferenceNumber;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CloseIncomingMtCommandHandler implements CommandHandler<CloseIncomingMtCommand> {

    @Inject
    MtMessageRepository mtMessageRepository;
    
    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Override
    public void handle(CloseIncomingMtCommand command) {

        try {
            Map<String, Object> parameterMap = command.getParameterMap();
            printParameters(parameterMap);

            Long id = Long.parseLong((String)parameterMap.get("id"));

            MtMessage mtMessage = mtMessageRepository.load(id);

            TradeServiceReferenceNumber tradeServiceReferenceNumber = new TradeServiceReferenceNumber((String) parameterMap.get("tradeServiceReferenceNumber"));

            DocumentNumber documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
            mtMessage.closeMessage(parameterMap, tradeServiceReferenceNumber, documentNumber);

            mtMessageRepository.merge(mtMessage);
        } catch (Exception e) {
        }
    }

    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside close incoming mt command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
