/**
 * 
 */
package com.ipc.rbac.application.commandhandler.user;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.user.AddUserCommand;
import com.ipc.rbac.application.command.user.enumTypes.UserParameterEnum;
import com.ipc.rbac.domain.User;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ipc.rbac.domain.UserFactory;
import com.ipc.rbac.domain.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * @author Val
 *
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class AddUserCommandHandler implements CommandHandler<AddUserCommand> {

	@Inject
	private UserFactory userFactory;
	
	@Inject
    private UserRepository userRepository;
    
	@Override
	public void handle(AddUserCommand command) {

		// retrieves value from parameters
    	String userActiveDirectoryId = command.getParameterValue(UserParameterEnum.USER_ACTIVE_DIRECTORY_ID);
    	String firstName = command.getParameterValue(UserParameterEnum.FIRST_NAME);
    	String lastName = command.getParameterValue(UserParameterEnum.LAST_NAME);
    	
    	// creates new instance of user from factory
    	User user = userFactory.createUser(new UserActiveDirectoryId(userActiveDirectoryId), firstName, lastName);
    	
    	// saves instance of user
    	userRepository.persist(user);
	}
	
}
