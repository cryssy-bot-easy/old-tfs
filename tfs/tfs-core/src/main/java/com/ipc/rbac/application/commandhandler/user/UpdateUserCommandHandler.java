package com.ipc.rbac.application.commandhandler.user;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.user.UpdateUserCommand;
import com.ipc.rbac.application.command.user.enumTypes.UserParameterEnum;
import com.ipc.rbac.domain.User;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ipc.rbac.domain.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class UpdateUserCommandHandler implements CommandHandler<UpdateUserCommand> {

	@Inject
    private UserRepository userRepository;
    
	@Override
	public void handle(UpdateUserCommand command) {

		// retrieves value from parameters
    	String userActiveDirectoryId = command.getParameterValue(UserParameterEnum.USER_ACTIVE_DIRECTORY_ID);
    	String firstName = command.getParameterValue(UserParameterEnum.FIRST_NAME);
    	String lastName = command.getParameterValue(UserParameterEnum.LAST_NAME);
    	
		User user = userRepository.getUser(new UserActiveDirectoryId(userActiveDirectoryId));
		
		user.setUserActiveDirectoryId(new UserActiveDirectoryId(userActiveDirectoryId));
		user.setFirstName(firstName);
		user.setLastName(lastName);
		//userFactory.createUser(activeDirectoryUid, firstName, lastName);
		
    	// saves instance of user
    	userRepository.persistChanges(user);
	}	
	
}
