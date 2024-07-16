package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveDocumentsRequiredCommand;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.documents.DocumentCode;
import com.ucpb.tfs.domain.documents.RequiredDocument;
import com.ucpb.tfs.domain.documents.enumTypes.RequiredDocumentType;
import com.ucpb.tfs.domain.reference.RequiredDocumentsReference;
import com.ucpb.tfs.domain.reference.RequiredDocumentsReferenceRepository;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.event.TradeServiceUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description:   Added setting of amend id and amend code for MT707.
 * Modified by:   Cedrick C. Nungay
 * Date Modified: 08/24/2018
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveDocumentsRequiredCommandHandler implements CommandHandler<SaveDocumentsRequiredCommand> {

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    RequiredDocumentsReferenceRepository requiredDocumentsReferenceRepository;

    @Override
	public void handle(SaveDocumentsRequiredCommand command) {

        try {
            Map<String, Object> parameterMap = command.getParameterMap();

            // temporary prints parameters
            printParameters(parameterMap);

            UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());

            TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            List<RequiredDocument> requiredDocumentsList = new ArrayList<RequiredDocument>();

            int sequenceNumber = 1;
            String documentSubType1 = (String)parameterMap.get("documentSubType1");
            String documentType = (String)parameterMap.get("documentType");
            String serviceType = (String)parameterMap.get("serviceType");
            boolean isMt707 = (documentSubType1.equalsIgnoreCase("REGULAR") ||
                documentSubType1.equalsIgnoreCase("CASH")) &&
                documentType.equalsIgnoreCase("FOREIGN") &&
                serviceType.equalsIgnoreCase("AMENDMENT");
            String amendCode;
            Boolean hasAmendId;
            // add default required documents
            for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("requiredDocumentsList")){

                DocumentCode documentCode = new DocumentCode((String)map.get("documentCode"));

                RequiredDocumentsReference requiredDocumentsReference = requiredDocumentsReferenceRepository.load(documentCode);

                RequiredDocumentType requiredDocumentType = null;

                if(requiredDocumentsReference != null) {
                    requiredDocumentType = RequiredDocumentType.DEFAULT;
                }else{
                    requiredDocumentType = RequiredDocumentType.NEW;
                }

                RequiredDocument rd = new RequiredDocument(documentCode, (String)map.get("description"), requiredDocumentType, isMt707 ? (Integer)map.get("sequenceNumber") : sequenceNumber);
                sequenceNumber++;
                if (isMt707) {
                    amendCode = (String)map.get("amendCode");
                    if (!(amendCode).equalsIgnoreCase("")) {
                        rd.setAmendCode(amendCode);
                    }

                    try {
                        hasAmendId = !((String) map.get("amendId")).equalsIgnoreCase("");
                    } catch (ClassCastException e) {
                        hasAmendId = true;
                    }
                    if (hasAmendId) {
                        BigDecimal amendId;
                        try {
                            amendId = BigDecimal.valueOf((Double) map.get("amendId"));
                        } catch (ClassCastException e) {
                            amendId = BigDecimal.valueOf((Integer) map.get("amendId"));
                        }
                        rd.setAmendId(amendId);
                    }
                }
                requiredDocumentsList.add(rd);
            }

            sequenceNumber = 1;
            // add new required document
            for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("addedDocumentsList")){
                RequiredDocument rd = new RequiredDocument(null, (String)map.get("description"), RequiredDocumentType.NEW, isMt707 ? (Integer)map.get("sequenceNumber") : sequenceNumber);
                sequenceNumber++;
                if (isMt707) {
                    rd.setAmendCode((String)map.get("amendCode"));
                }
                requiredDocumentsList.add(rd);
            }

            if(tradeService.getRequiredDocument() != null || !tradeService.getRequiredDocument().isEmpty()) {
                tradeServiceRepository.deleteRequiredDocuments(tradeService.getTradeServiceId());
            }

            tradeService.addRequiredDocuments(requiredDocumentsList);

            if(parameterMap.containsKey("requiredDocumentsList")) {
                parameterMap.remove("requiredDocumentsList");
            }

            if(parameterMap.containsKey("addedDocumentsList")) {
                parameterMap.remove("addedDocumentsList");
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
		System.out.println("inside save documents required command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}	
	
}
