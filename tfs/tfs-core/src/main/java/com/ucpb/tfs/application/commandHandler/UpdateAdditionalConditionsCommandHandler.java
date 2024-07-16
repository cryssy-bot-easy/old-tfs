package com.ucpb.tfs.application.commandHandler;

import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ucpb.tfs.application.command.UpdateAdditionalConditionsCommand;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class UpdateAdditionalConditionsCommandHandler implements CommandHandler<UpdateAdditionalConditionsCommand> {

	@Override
	public void handle(UpdateAdditionalConditionsCommand command) {
		// TODO handle save ets basic details command
		
		// temporary prints parameters
		printParameters(command.getParameterMap());
	}
	
	// temporary prints parameters
	private void printParameters(Map<String, Object> parameterMap) {
		System.out.println("inside update additional conditions command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}	
	
}
