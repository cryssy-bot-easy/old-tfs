package com.ipc.rbac.application.commandhandler.role;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;
import com.ipc.rbac.application.command.role.AddPermissionsToRoleCommand;
import com.ipc.rbac.application.command.role.enumTypes.RoleParameterEnum;
import com.ipc.rbac.domain.Permission;
import com.ipc.rbac.domain.Role;
import com.ipc.rbac.domain.RoleRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class AddPermissionsToRoleCommandHandler implements CommandHandler<AddPermissionsToRoleCommand>  {
	
    @Inject
    private RoleRepository roleRepository;

	@Override
	public void handle(AddPermissionsToRoleCommand command) {

		// retrieves value from parameters
    	Long roleId = Long.valueOf(command.getParameterValue(RoleParameterEnum.ROLE_ID));

    	Role role = roleRepository.getRole(roleId);
    	
    	// retrieves permissions from permission list parameter
		List<Map<PermissionParameterEnum, String>> permissionList = command.getPermissionList();

		for (Map<PermissionParameterEnum, String> permissionParameterMap : permissionList) {
			// retrieve id of permission from parameter
			Long permissionId = Long.valueOf(permissionParameterMap.get(PermissionParameterEnum.PERMISSION_ID));

			// creates instance of permission from the retrieved id
			Permission permission = new Permission();
			permission.setId(permissionId);
			
			// adds permission to role
			role.addPermission(permission);
		}
		
		// saves instance of permission
		roleRepository.persistChanges(role);			
	}
	
}
