package com.ipc.rbac.application.commandhandler.role;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.role.AddRoleCommand;
import com.ipc.rbac.application.command.role.enumTypes.RoleParameterEnum;
import com.ipc.rbac.domain.Role;
import com.ipc.rbac.domain.RoleFactory;
import com.ipc.rbac.domain.RoleRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class AddRoleCommandHandler implements CommandHandler<AddRoleCommand> {
    
	@Inject
	private RoleFactory roleFactory;
	
    @Inject
    private RoleRepository roleRepository;

	@Override
	public void handle(AddRoleCommand command) {

		// retrieves value from parameters
    	String name = command.getParameterValue(RoleParameterEnum.ROLE_NAME);
    	String description = command.getParameterValue(RoleParameterEnum.ROLE_DESCRIPTION);

    	// creates new instance of role from factory
    	Role role = roleFactory.createRole(name, description);

    	// saves instance of permission
    	roleRepository.persist(role);
	}
	
}
