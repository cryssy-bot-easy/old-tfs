package com.ipc.rbac.application.commandhandler.authorization;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */
import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.authorization.AddAuthorizationCommand;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorizationParameterEnum;
import com.ipc.rbac.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class AddAuthorizationCommandHandler implements CommandHandler<AddAuthorizationCommand>{

    @Inject
    private AuthorizationFactory authorizationFactory;
	
    @Inject
    private AuthorizationRepository authorizationRepository;

    @Override
	public void handle(AddAuthorizationCommand command) {
		
		String userActiveDirectoryId = command.getParameterValue(AuthorizationParameterEnum.USER);
		
		Authorization authorization = authorizationFactory.createAuthorization(new UserActiveDirectoryId(userActiveDirectoryId));
		
		authorizationRepository.persist(authorization);
	}
}
