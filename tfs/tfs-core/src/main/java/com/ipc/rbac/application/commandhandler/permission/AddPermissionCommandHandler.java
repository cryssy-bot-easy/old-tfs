package com.ipc.rbac.application.commandhandler.permission;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.permission.AddPermissionCommand;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;
import com.ipc.rbac.domain.Permission;
import com.ipc.rbac.domain.PermissionFactory;
import com.ipc.rbac.domain.PermissionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class AddPermissionCommandHandler implements CommandHandler<AddPermissionCommand> {
    
	@Inject
	private PermissionFactory permissionFactory;
	
    @Inject
    private PermissionRepository permissionRepository;

	@Override
	public void handle(AddPermissionCommand command) {

		// retrieves value from parameters
    	String name = command.getParameterValue(PermissionParameterEnum.PERMISSION_NAME);
    	String description = command.getParameterValue(PermissionParameterEnum.PERMISSION_DESCRIPTION);
    	
    	// creates permission from factory
    	Permission permission = permissionFactory.createPermission(name, description);
    	
    	// saves instance of permission
    	permissionRepository.persist(permission);
	}
	
}
