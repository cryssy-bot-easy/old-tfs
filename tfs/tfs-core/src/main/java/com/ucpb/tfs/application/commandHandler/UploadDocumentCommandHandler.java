package com.ucpb.tfs.application.commandHandler;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.UploadDocumentCommand;
import com.ucpb.tfs.application.service.TradeProductService;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionUpdatedEvent;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.product.TradeProduct;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.event.TradeServiceUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;


@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class UploadDocumentCommandHandler implements CommandHandler<UploadDocumentCommand> {
	
	
    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    TradeProductRepository tradeProductRepository;	
	

	@Override
	public void handle(UploadDocumentCommand command) {
		// TODO handle save ets basic details command
		
		System.out.println("UploadDocumentCommand");
		
		// temporary prints parameters
		printParameters(command.getParameterMap());
		
		Map<String, Object> parameterMap = command.getParameterMap();
		
		parameterMap.put("serviceType", ((String)parameterMap.get("serviceType")).replaceAll(" ", "_"));
		
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date createdDate = cal.getTime();

        TradeService tradeService = null;
        TradeServiceId tradeServiceId;


        tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
        tradeService = tradeServiceRepository.load(tradeServiceId);
        tradeService.addAttachment((String)parameterMap.get("filename"),"", createdDate ,(String)parameterMap.get("docType"));

        // Update TradeService
//        tradeService = TradeServiceService.updateTradeServiceDetails(tradeService, parameterMap, userActiveDirectoryId);
        tradeServiceRepository.merge(tradeService);

        // Fire event
        // This updates basic details so no status is passed.
        TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, userActiveDirectoryId);
        eventPublisher.publish(tradeServiceUpdatedEvent);
	}
	
	// temporary prints parameters
	private void printParameters(Map<String, Object> parameterMap) {
		System.out.println("inside upload document command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}	
	
}
