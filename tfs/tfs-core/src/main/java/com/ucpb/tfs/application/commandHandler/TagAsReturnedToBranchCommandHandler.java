package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.TagAsReturnedToBranchCommand;
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
import com.ucpb.tfs2.application.service.TradeServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/23/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TagAsReturnedToBranchCommandHandler implements CommandHandler<TagAsReturnedToBranchCommand> {

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    TradeServiceService tradeServiceService;

    @Autowired
    DomainEventPublisher eventPublisher;

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
    public void handle(TagAsReturnedToBranchCommand command) {
        Map<String, Object> parameterMap = command.getParameterMap();
        printParameters(parameterMap);
        UserActiveDirectoryId updatedBy = new UserActiveDirectoryId(parameterMap.get("username").toString());

//        ServiceInstructionId etsNumber;
        ServiceInstruction ets;

        TradeServiceId tradeServiceId;

        if(parameterMap.get("reversalTradeServiceId") != null) {
            System.out.println("i am reversal return");
            tradeServiceId = new TradeServiceId((String)parameterMap.get("reversalTradeServiceId"));
            ServiceInstructionId originalEtsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
            tradeServiceService.unReverseTradeService(originalEtsNumber);
        } else {
//            etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
            tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
        }

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        ets = serviceInstructionRepository.load(tradeService.getServiceInstructionId());

        UserActiveDirectoryId createdBy = new UserActiveDirectoryId(ets.getCreatedBy().toString());

        ets.resetApprovers();

        tradeService.updateLastUser(updatedBy);

        tradeService.updateStatus(TradeServiceStatus.RETURNED_TO_BRANCH, tradeService.getPreparedBy());

        //Added because when negotiation is reversed currency field is null
        if(tradeService.getDetails().containsKey("negotiationCurrency")){
            tradeService.getDetails().put("currency",tradeService.getDetails().get("negotiationCurrency"));
        }

        tradeServiceRepository.merge(tradeService);

        String gltsNumber = gltsSequenceRepository.getGltsSequence();
        TradeServiceTaggedEvent tradeServiceUpdatedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.MARV, createdBy, gltsNumber);
        eventPublisher.publish(tradeServiceUpdatedEvent);

        TradeServiceRoutedEvent tradeServiceRoutedEvent = new TradeServiceRoutedEvent(tradeService, TradeServiceStatus.MARV, updatedBy, createdBy);
        eventPublisher.publish(tradeServiceRoutedEvent);

        ServiceInstructionTaggedEvent etsUpdatedEvent = new ServiceInstructionTaggedEvent(ets, ServiceInstructionStatus.RETURNED, createdBy);
        eventPublisher.publish(etsUpdatedEvent);

        ets.tagStatus(ServiceInstructionStatus.RETURNED);
        
        ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ServiceInstructionStatus.RETURNED, updatedBy, createdBy);
        eventPublisher.publish(siRoutedEvent);

        
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside tag as returned to branch command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
