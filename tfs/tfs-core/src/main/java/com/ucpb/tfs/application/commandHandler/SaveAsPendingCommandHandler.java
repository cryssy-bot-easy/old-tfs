package com.ucpb.tfs.application.commandHandler;

/**
 * @author Marvin Volante
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.instruction.SaveAsPendingCommand;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionCreatedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionRoutedEvent;
import com.ucpb.tfs.domain.instruction.utils.EtsNumberGenerator;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.event.TradeServiceSavedEvent;
import com.ucpb.tfs.domain.service.utils.TradeServiceReferenceNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveAsPendingCommandHandler implements CommandHandler<SaveAsPendingCommand> {

    // private static final Logger logger = Logger.getLogger(SaveAsPendingCommandHandler.class);

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

/*
    @Inject
    ChargesService chargesService;
*/

    @Autowired
    ChargesService chargesService;

    @Autowired
    EtsNumberGenerator etsNumberGenerator;

    @Autowired
    TradeServiceReferenceNumberGenerator tradeServiceReferenceNumberGenerator;

    @Override
    public void handle(SaveAsPendingCommand command) {

        // TODO handle save ets basic details command

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
        UserId userId = new UserId(parameterMap.get("username").toString());

        if ("ETS".equals(parameterMap.get("referenceType"))) {

//            ServiceInstruction ets = new ServiceInstruction((HashMap)command.getParameterMap(), userActiveDirectoryId);
            String serviceInstructionId = etsNumberGenerator.generateServiceInstructionId((String)parameterMap.get("unitcode"));
            ServiceInstruction ets = new ServiceInstruction(serviceInstructionId, (HashMap)command.getParameterMap(), userId);

            // Set status
            if (command.isDraft()) {
                System.out.println("command is draft");
                ets.tagStatus(ServiceInstructionStatus.DRAFT);
            } else {
                ets.tagStatus(ServiceInstructionStatus.PENDING);
            }

            // Persist
            serviceInstructionRepository.persist(ets);
            System.out.println("Persisted ServiceInstruction ETS!");

            // Fire event
            ServiceInstructionCreatedEvent etsCreatedEvent = new ServiceInstructionCreatedEvent(ets, (command.isDraft() ? ServiceInstructionStatus.DRAFT : ServiceInstructionStatus.PENDING), (command.isDraft() ? userActiveDirectoryId : new UserActiveDirectoryId("TSD")));
            eventPublisher.publish(etsCreatedEvent);

            // create a routed event. when an SI is created when it is saved as pending, it is technically routed to user who created it
            ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, (command.isDraft() ? ServiceInstructionStatus.DRAFT : ServiceInstructionStatus.PENDING), new UserActiveDirectoryId(userId.toString()), userActiveDirectoryId);
            eventPublisher.publish(siRoutedEvent);
            System.out.println("COMMAND TOKEN:"+command.getToken());
            // Add to token registry
            tokenProvider.addTokenForId(command.getToken(), ets.getServiceInstructionId().toString());
            
        } else {
            // Called for Products that does not require ETS
            System.out.println("TOKEN TOKEN:");

        	
        	try {
            // TODO: I think this is where the Document Number must be generated instead of getting the value from the parameter
            DocumentNumber documentNumber = new DocumentNumber((String)parameterMap.get("documentNumber"));
            TradeProductNumber tradeProductNumber = new TradeProductNumber(documentNumber.toString());
                System.out.println("ANGULO ANGULO ANGULO");

                if(parameterMap.get("documentClass") != null && parameterMap.get("documentType") != null && parameterMap.get("serviceType") != null){
                    String documentClassTemp = (String) parameterMap.get("documentClass");
                    String documentTypeTemp = (String) parameterMap.get("documentType");
                    String serviceTypeTemp = (String) parameterMap.get("serviceType");
                    System.out.println("documentClassTemp:"+documentClassTemp);
                    System.out.println("documentTypeTemp:"+documentTypeTemp);
                    System.out.println("serviceTypeTemp:"+serviceTypeTemp);
                    if(documentClassTemp.equalsIgnoreCase("INDEMNITY") && serviceTypeTemp.equalsIgnoreCase("Cancellation")){
                        if (parameterMap.get("referenceNumber") != null) {
                            DocumentNumber docNum = new DocumentNumber((String) parameterMap.get("referenceNumber"));
                            tradeProductNumber = new TradeProductNumber(docNum.toString());
                        }
                    }
                }


            System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> TRADE PRODUCT NUMBER (SaveAsPending) = " + tradeProductNumber.toString() + "\n");

            // When the logged-in user is TSD, the user's unit code is the processing unit code
            String processingUnitCode = (String)parameterMap.get("unitcode");
            String tradeServiceReferenceNumber = tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode);
            System.out.println("TRADESERVICEREFERENCENUMBER: " + tradeServiceReferenceNumber);
            System.out.println("\nprocessingUnitCode = " + processingUnitCode + "\n");

            String pucInMap = (String)parameterMap.get("processingUnitCode");
            if (pucInMap == null || pucInMap.isEmpty()) {
                parameterMap.put("processingUnitCode", processingUnitCode);
            }

            // Create and persist TradeService.
            TradeService tradeService = TradeServiceService.createTradeService(parameterMap, documentNumber, tradeProductNumber, userActiveDirectoryId, tradeServiceReferenceNumber);
            tradeService.updateProductCharge(parameterMap, userActiveDirectoryId);

            // TODO: Applying charges results in error in JUnit
            // a trade service item was modified, we call the service to delete old charges
            //tradeServiceRepository.deleteServiceCharges(tradeService.getTradeServiceId());
            // we add charges to it
            chargesService.applyCharges(tradeService, parameterMap);
            tradeService.getDetails().put("chargesOverridenFlag","N");
            tradeServiceRepository.persist(tradeService);
            System.out.println("Persisted TradeService!");

            // Fire event
            TradeServiceSavedEvent tradeServiceSavedEvent = new TradeServiceSavedEvent(tradeService.getTradeServiceId(), tradeService.getDetails(), tradeService.getStatus(), userActiveDirectoryId);
            eventPublisher.publish(tradeServiceSavedEvent);

            // Add to token registry
            tokenProvider.addTokenForId(command.getToken(), tradeService.getTradeServiceId().toString());
            
        	}
        	catch(Exception e) {
        		System.out.println("ERROR ====================== ");
        		e.printStackTrace();
        	}
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside save as pending command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
