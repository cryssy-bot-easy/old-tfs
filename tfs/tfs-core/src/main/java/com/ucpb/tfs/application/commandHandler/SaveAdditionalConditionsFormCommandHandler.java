package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveAdditionalConditionsFormCommand;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.condition.AdditionalCondition;
import com.ucpb.tfs.domain.condition.ConditionCode;
import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;
import com.ucpb.tfs.domain.reference.AdditionalConditionReference;
import com.ucpb.tfs.domain.reference.AdditionalConditionReferenceRepository;
import com.ucpb.tfs.domain.reference.SwiftChargeReference;
import com.ucpb.tfs.domain.reference.SwiftChargeReferenceRepository;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.event.TradeServiceUpdatedEvent;
import com.ucpb.tfs.domain.swift.SwiftCharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

/**
 * Description:   Added setting of amend id and amend code for MT707.
 * Modified by:   Cedrick C. Nungay
 * Date Modified: 09/03/2018
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveAdditionalConditionsFormCommandHandler implements CommandHandler<SaveAdditionalConditionsFormCommand> {

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    AdditionalConditionReferenceRepository additionalConditionReferenceRepository;

    @Inject
    SwiftChargeReferenceRepository swiftChargeReferenceRepository;

    @Override
	public void handle(SaveAdditionalConditionsFormCommand command) {

        try {
            Map<String, Object> parameterMap = command.getParameterMap();

            // temporary prints parameters
            printParameters(parameterMap);

            UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());

            TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            List<AdditionalCondition> additionalConditionList = new ArrayList<AdditionalCondition>();

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
            
            for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("additionalConditionsList")){

                ConditionCode conditionCode = new ConditionCode((String)map.get("conditionCode"));

                AdditionalConditionReference additionalConditionReference = additionalConditionReferenceRepository.load(conditionCode);

                ConditionType conditionType = null;

                if(additionalConditionReference != null) {
                    conditionType = ConditionType.DEFAULT;
                }else{
                    conditionType = ConditionType.NEW;
                }

                AdditionalCondition ac = new AdditionalCondition(conditionType, conditionCode, isMt707 ? (String)map.get("description") : (String)map.get("condition"), isMt707 ? (Integer)map.get("sequenceNumber") : sequenceNumber);
                sequenceNumber++;
                if (isMt707) {
                    amendCode = (String)map.get("amendCode");
                    if (!(amendCode).equalsIgnoreCase("")) {
                        ac.setAmendCode(amendCode);
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
                        ac.setAmendId(amendId);
                    }
                }
                additionalConditionList.add(ac);
            }

            
            sequenceNumber = 1;
            // add new additional condition
            for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("addedAdditionalConditionsList")){
                AdditionalCondition ac = new AdditionalCondition(ConditionType.NEW, null, isMt707 ? (String)map.get("description") : (String)map.get("condition"), isMt707 ? (Integer)map.get("sequenceNumber") : sequenceNumber);
                sequenceNumber++;
                if (isMt707) {
                    ac.setAmendCode((String)map.get("amendCode"));
                }
                additionalConditionList.add(ac);
            }

            if(tradeService.getAdditionalCondition() != null || !tradeService.getAdditionalCondition().isEmpty()) {
                tradeServiceRepository.deleteAdditionalConditions(tradeService.getTradeServiceId());
            }

            tradeService.addAdditionalCondition(additionalConditionList);

            if(parameterMap.containsKey("additionalConditionsList")) {
                parameterMap.remove("additionalConditionsList");
            }

            if(parameterMap.containsKey("addedAdditionalConditionsList")) {
                parameterMap.remove("addedAdditionalConditionsList");
            }

            // add swift charges
            System.out.println("adding swift charges...");
            List<SwiftCharge> swiftChargeList = new ArrayList<SwiftCharge>();

            for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("swiftChargesList")){
                SwiftChargeReference swiftChargeReference = swiftChargeReferenceRepository.load((String)map.get("code"));

                Currency swiftCurrency = Currency.getInstance((String)map.get("swiftCurrency"));

                SwiftCharge swiftCharge = new SwiftCharge((String)map.get("code"), swiftChargeReference.getDescription(), swiftCurrency, new BigDecimal(map.get("swiftAmount").toString().replaceAll(",", "")));

                swiftChargeList.add(swiftCharge);
            }

            if(tradeService.getSwiftCharge() != null || !tradeService.getSwiftCharge().isEmpty()) {
                tradeServiceRepository.deleteSwiftCharges(tradeService.getTradeServiceId());
            }

            tradeService.addSwiftCharge(swiftChargeList);

            if(parameterMap.containsKey("swiftChargesList")) {
                parameterMap.remove("swiftChargesList");
            }

            System.out.println("new parameterMap");
            printParameters(parameterMap);

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
		System.out.println("inside save additional conditions form command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}	
	
}
