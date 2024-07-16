package com.ipc.rbac.application.commandhandler.permission;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.permission.UpdatePermissionCommand;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;
import com.ipc.rbac.domain.Permission;
import com.ipc.rbac.domain.PermissionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class UpdatePermissionCommandHandler implements CommandHandler<UpdatePermissionCommand>  {

    @Inject
    private PermissionRepository permissionRepository;

	@Override
	public void handle(UpdatePermissionCommand command) {

		// retrieves value from parameters
		Long permissionId = Long.valueOf(command.getParameterValue(PermissionParameterEnum.PERMISSION_ID));
    	String name = command.getParameterValue(PermissionParameterEnum.PERMISSION_NAME);
    	String description = command.getParameterValue(PermissionParameterEnum.PERMISSION_DESCRIPTION);		
		
		Permission permission = permissionRepository.getPermission(permissionId);
		
    	//Permission permission = new Permission();
    	//permission.setId(permissionId); 
		permission.setName(name);
		permission.setDescription(description);

    	// saves instance of permission
    	permissionRepository.persistChanges(permission);
	}	
	
}
