package com.ipc.rbac.application.commandhandler.role;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.role.UpdateRoleCommand;
import com.ipc.rbac.application.command.role.enumTypes.RoleParameterEnum;
import com.ipc.rbac.domain.Role;
import com.ipc.rbac.domain.RoleRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class UpdateRoleCommandHandler implements CommandHandler<UpdateRoleCommand> {

    @Inject
    private RoleRepository roleRepository;

	@Override
	public void handle(UpdateRoleCommand command) {

		// retrieves value from parameters
    	Long roleId = Long.valueOf(command.getParameterValue(RoleParameterEnum.ROLE_ID));		
    	String name = command.getParameterValue(RoleParameterEnum.ROLE_NAME);
    	String description = command.getParameterValue(RoleParameterEnum.ROLE_DESCRIPTION);

    	Role role = roleRepository.getRole(roleId);

    	role.setName(name);
    	role.setDescription(description);
    	
    	// saves instance of permission
    	roleRepository.persistChanges(role);
	}	
	
}
