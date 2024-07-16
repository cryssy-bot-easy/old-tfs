package com.ucpb.tfs.application.commandHandler;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.instruction.EmailCommand;
import com.ucpb.tfs.domain.email.Email;
import com.ucpb.tfs.domain.email.MailFrom;
import com.ucpb.tfs.domain.email.RoutingEmail;
import com.ucpb.tfs.domain.email.SmtpAuthenticator;
import com.ucpb.tfs.domain.email.service.EmailService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.EmployeeRepository;
import com.ucpb.tfs.domain.security.UserId;





@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class EmailCommandHandler implements CommandHandler<EmailCommand>{

	@Inject
    ServiceInstructionRepository serviceInstructionRepository;
	
    @Inject
    TradeServiceRepository tradeServiceRepository;
	
	@Autowired
	SmtpAuthenticator smtpAuthenticator;

	@Autowired
	MailFrom mailFrom;

	@Autowired
	MailSender mailSender;

	@Autowired
	EmployeeRepository employeeRepository;
	
	@Override
	public void handle(EmailCommand command) {
		// TODO Auto-generated method stub
		
        Map<String, Object> parameterMap = command.getParameterMap();

        printParameters(parameterMap);
        
        
        TradeServiceId tradeServiceId;
        
        ServiceInstructionId etsNumber;
        UserActiveDirectoryId userActiveDirectoryId = null;
        if (parameterMap.get("statusAction").toString().equals("Return")){
        	if(parameterMap.get("referenceType").equals("ETS")){
	        
	        	if(parameterMap.get("reversalEtsNumber") != null) {
	                etsNumber = new ServiceInstructionId((String)parameterMap.get("reversalEtsNumber"));
	            } else {
	                etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
	            }
	        	ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);
	            userActiveDirectoryId = new UserActiveDirectoryId(ets.getCreatedBy().toString());	
        	}else{
        		tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
        		TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

                userActiveDirectoryId = new UserActiveDirectoryId(tradeService.getCreatedBy().toString());
        	}
         
        }else if( parameterMap.get("statusAction").toString().equals("ReturnToBranch")){
        	  if(parameterMap.get("reversalTradeServiceId") != null) {
                  tradeServiceId = new TradeServiceId((String)parameterMap.get("reversalTradeServiceId"));
              } else {
                  tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
              }
        	
        	TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        	ServiceInstruction ets = serviceInstructionRepository.load(tradeService.getServiceInstructionId());

             userActiveDirectoryId = new UserActiveDirectoryId(ets.getCreatedBy().toString());
           
        }else{
        	userActiveDirectoryId = new UserActiveDirectoryId(parameterMap.get("routeTo").toString());
        }
        	

       
        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(parameterMap.get("username").toString());  
		
        EmailService emailService = new EmailService();
        try{
	        Email routingEmail = null;
	        
	        Employee employeeReceiver = employeeRepository.getEmployee(new UserId(userActiveDirectoryId.toString()));
	        Employee employeeSender = employeeRepository.getEmployee(new UserId(parameterMap.get("username").toString())); //ibdmuc
	        
	        if (employeeReceiver.getReceiveEmail() != null && employeeReceiver.getReceiveEmail() == true){
	    		routingEmail = new RoutingEmail(parameterMap, employeeSender, employeeReceiver);

	    		emailService.sendEmail(smtpAuthenticator, mailFrom, mailSender,routingEmail);
	        }
        }catch(Exception e){
        	//Print error only.
        	e.printStackTrace();
        	throw new RuntimeException("~~~" + e.getMessage() + "~~~" + emailService.getDefaultAddress() + "~~~");
        }
        
	}
	
	private void printParameters(Map<String, Object> parameterMap) {
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}

	
}
