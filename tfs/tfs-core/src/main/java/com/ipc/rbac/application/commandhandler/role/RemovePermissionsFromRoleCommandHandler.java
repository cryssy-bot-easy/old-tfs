package com.ipc.rbac.application.commandhandler.role;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;
import com.ipc.rbac.application.command.role.RemovePermissionsFromRoleCommand;
import com.ipc.rbac.application.command.role.enumTypes.RoleParameterEnum;
import com.ipc.rbac.domain.Permission;
import com.ipc.rbac.domain.PermissionRepository;
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
public class RemovePermissionsFromRoleCommandHandler implements CommandHandler<RemovePermissionsFromRoleCommand> {

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private PermissionRepository permissionRepository;

    @Override
	public void handle(RemovePermissionsFromRoleCommand command) {

		// retrieves value from parameters
    	Long roleId = Long.valueOf(command.getParameterValue(RoleParameterEnum.ROLE_ID));

    	Role role = roleRepository.getRole(roleId);
		
    	// retrieves permissions from permission list parameter
		List<Map<PermissionParameterEnum, String>> permissionList = command.getPermissionList();

		for (Map<PermissionParameterEnum, String> permissionParameterMap : permissionList) {
			// retrieve id of permission from parameter
			Long permissionId = Long.valueOf(permissionParameterMap.get(PermissionParameterEnum.PERMISSION_ID));
			
			// retrieve permission from id
			Permission permission = permissionRepository.getPermission(permissionId);

			// removes permission to role
			role.removePermission(permission);
		}
		
		// saves instance of permission
		roleRepository.persistChanges(role);
	}	
	
}
