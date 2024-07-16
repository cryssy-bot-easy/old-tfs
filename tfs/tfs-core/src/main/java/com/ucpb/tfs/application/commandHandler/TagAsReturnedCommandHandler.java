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
import com.ucpb.tfs.application.command.instruction.TagAsReturnedCommand;
import com.ucpb.tfs.domain.email.Email;
import com.ucpb.tfs.domain.email.MailFrom;
import com.ucpb.tfs.domain.email.RoutingEmail;
import com.ucpb.tfs.domain.email.SmtpAuthenticator;
import com.ucpb.tfs.domain.email.service.EmailService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionRoutedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionTaggedEvent;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.EmployeeRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.TradeServiceRoutedEvent;
import com.ucpb.tfs.domain.service.event.TradeServiceTaggedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TagAsReturnedCommandHandler implements CommandHandler<TagAsReturnedCommand>{

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

	@Autowired
	SmtpAuthenticator smtpAuthenticator;

	@Autowired
	MailFrom mailFrom;

	@Autowired
	MailSender mailSender;
	
	@Autowired
	EmployeeRepository employeeRepository;
	
    @Override
	public void handle(TagAsReturnedCommand command) {

        /*
         * Combines ETS and Data Entry handling
         */

        // TODO handle save ets basic details command

        Map<String, Object> parameterMap = command.getParameterMap();

        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(parameterMap.get("username").toString());

        // a return will always require that we look at the eTS that started all this so we can get the creator
        // Load from repository
        // this will be directed to the person who created the SI
        UserActiveDirectoryId userActiveDirectoryId = null;
        
        if (((String)parameterMap.get("referenceType")).equals("ETS")) {
            ServiceInstructionId etsNumber;

            if(parameterMap.get("reversalEtsNumber") != null) {
                System.out.println("i am reversal return");
                etsNumber = new ServiceInstructionId((String)parameterMap.get("reversalEtsNumber"));
            } else {
                etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
            }

            ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

            userActiveDirectoryId = new UserActiveDirectoryId(ets.getCreatedBy().toString());

            ets.resetApprovers();
            // Set status
//            ets.updateStatus(ServiceInstructionStatus.RETURNED, userActiveDirectoryId);
            ets.updateStatus(ServiceInstructionStatus.RETURNED, userId);

            // sets last user
            ets.setLastUser(userId);

            // Persist
            serviceInstructionRepository.merge(ets);
            System.out.println("Persisted!");

            // Fire event
            ServiceInstructionTaggedEvent etsUpdatedEvent = new ServiceInstructionTaggedEvent(ets, ServiceInstructionStatus.RETURNED, userActiveDirectoryId);
            eventPublisher.publish(etsUpdatedEvent);

            // create a routed event. the user passed is the user the SI will be routed to
//            ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ServiceInstructionStatus.RETURNED, userActiveDirectoryId);
            ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ServiceInstructionStatus.RETURNED, fromADUser, userActiveDirectoryId);
            eventPublisher.publish(siRoutedEvent);

            // Add to token registry
            tokenProvider.addTokenForId(command.getToken(), ets.getServiceInstructionId().toString());

        } else {

            TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            userActiveDirectoryId = new UserActiveDirectoryId(tradeService.getCreatedBy().toString());
            // last user
            System.out.println("lastUser will be : " + fromADUser);
            tradeService.updateLastUser(fromADUser);

//            tradeService.updateStatus(TradeServiceStatus.RETURNED, userActiveDirectoryId);
//            tradeService.updateStatus(TradeServiceStatus.RETURNED, userActiveDirectoryId);
            System.out.println("userActiveDirectory will be : " + fromADUser);
            tradeService.updateStatus(TradeServiceStatus.RETURNED, fromADUser);

            tradeService.resetApprovers();

            tradeServiceRepository.merge(tradeService);

            // Fire event
            String gltsNumber = gltsSequenceRepository.getGltsSequence();
//            TradeServiceTaggedEvent tradeServiceUpdatedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.RETURNED, userActiveDirectoryId);
            TradeServiceTaggedEvent tradeServiceUpdatedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.RETURNED, tradeService.getPreparedBy(), gltsNumber);
            eventPublisher.publish(tradeServiceUpdatedEvent);

            TradeServiceRoutedEvent tradeServiceRoutedEvent = new TradeServiceRoutedEvent(tradeService, TradeServiceStatus.RETURNED, fromADUser, tradeService.getPreparedBy());
            eventPublisher.publish(tradeServiceRoutedEvent);

        }
       
    }
	
	// temporary prints parameters
	private void printParameters(Map<String, Object> parameterMap) {
		System.out.println("inside tag as returned command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}
}
