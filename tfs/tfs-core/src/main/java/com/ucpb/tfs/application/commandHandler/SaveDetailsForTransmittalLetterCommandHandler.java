package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveDetailsForTransmittalLetterCommand;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.letter.TransmittalLetter;
import com.ucpb.tfs.domain.letter.TransmittalLetterCode;
import com.ucpb.tfs.domain.letter.enumTypes.LetterType;
import com.ucpb.tfs.domain.reference.TransmittalLetterReference;
import com.ucpb.tfs.domain.reference.TransmittalLetterReferenceRepository;
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
 * Date: 11/7/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveDetailsForTransmittalLetterCommandHandler implements CommandHandler<SaveDetailsForTransmittalLetterCommand> {

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    TransmittalLetterReferenceRepository transmittalLetterReferenceRepository;

    @Override
    public void handle(SaveDetailsForTransmittalLetterCommand command) {

        try {
            Map<String, Object> parameterMap = command.getParameterMap();

            // temporary prints parameters
            printParameters(parameterMap);

            UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());

            TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            // add default transmittal letter
            List<TransmittalLetter> transmittalLetterList = new ArrayList<TransmittalLetter>();
            
            int sequenceNumber = 1;

            for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("transmittalLetterList")){
                TransmittalLetterCode transmittalLetterCode = new TransmittalLetterCode((String)map.get("transmittalLetterCode"));

                TransmittalLetterReference transmittalLetterReference = transmittalLetterReferenceRepository.load(transmittalLetterCode);

                LetterType letterType = null;

                if(transmittalLetterReference != null) {
                    letterType = LetterType.DEFAULT;
                } else {
                    letterType = LetterType.NEW;
                }

                TransmittalLetter tl = new TransmittalLetter(transmittalLetterCode,
                        (String)map.get("letterDescription"),
                        letterType,
                        map.get("originalCopy").toString(),
                        map.get("duplicateCopy").toString(),
                        sequenceNumber);
//                        map.get("originalCopy").toString().replaceAll(",",""),
//                        map.get("duplicateCopy").toString().replaceAll(",",""));
                sequenceNumber++;
                transmittalLetterList.add(tl);
            }

            // add all new transmittal letter
            for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("addedTransmittalLetterList")){
                TransmittalLetter tl = new TransmittalLetter(null,
                        (String)map.get("letterDescription"),
                        LetterType.NEW,
                        map.get("originalCopy").toString(), 
                        map.get("duplicateCopy").toString(),
                        sequenceNumber);
//                        new Long(map.get("originalCopy").toString().replaceAll(",","")),
//                        new Long(map.get("duplicateCopy").toString().replaceAll(",","")));
                sequenceNumber++;
                transmittalLetterList.add(tl);
            }

            if(tradeService.getTransmittalLetter() != null || !tradeService.getTransmittalLetter().isEmpty()) {
                tradeServiceRepository.deleteTransmittalLetters(tradeService.getTradeServiceId());
            }

            tradeService.addTransmittalLetter(transmittalLetterList);

            if(parameterMap.containsKey("transmittalLetterList")) {
                parameterMap.remove("transmittalLetterList");
            }

            if(parameterMap.containsKey("addedTransmittalLetterList")) {
                parameterMap.remove("addedTransmittalLetterList");
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
        System.out.println("inside save details for transmittal letter command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
