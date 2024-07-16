package com.ipc.rbac.application.commandhandler.role;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.command.CommandBus;
import com.ipc.rbac.application.command.permission.AddPermissionCommand;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;
import com.ipc.rbac.application.command.role.AddPermissionsToRoleCommand;
import com.ipc.rbac.application.command.role.AddRoleCommand;
import com.ipc.rbac.application.command.role.RemovePermissionsFromRoleCommand;
import com.ipc.rbac.application.command.role.UpdateRoleCommand;
import com.ipc.rbac.application.command.role.enumTypes.RoleParameterEnum;
import com.ipc.rbac.application.query.permission.IPermissionFinder;
import com.ipc.rbac.application.query.role.IRoleFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestRoleCommands {
	
	@Autowired
	CommandBus commandBus;
	
	@Autowired
	private IPermissionFinder permissionFinder;
	
	@Autowired
	private IRoleFinder roleFinder;
	
	@Test
	public void AddRoleContext() {

		// ADD PERMISSION COMMAND
    	// instance of permission 1
    	Map<PermissionParameterEnum, String> permissionParam1 = new HashMap<PermissionParameterEnum, String>();
    	permissionParam1.put(PermissionParameterEnum.PERMISSION_NAME, "Add");
    	permissionParam1.put(PermissionParameterEnum.PERMISSION_DESCRIPTION, "Create eTS");
    	
    	AddPermissionCommand permissionCommand1 = new AddPermissionCommand();
    	permissionCommand1.setParameterMap(permissionParam1);
    	
    	// saves instance of permission 1
    	commandBus.dispatch(permissionCommand1);
    	
    	// instance of permission 2  
    	Map<PermissionParameterEnum, String> permissionParam2 = new HashMap<PermissionParameterEnum, String>();
    	permissionParam2.put(PermissionParameterEnum.PERMISSION_NAME, "Edit");
    	permissionParam2.put(PermissionParameterEnum.PERMISSION_DESCRIPTION, "Update eTS");
    	
    	AddPermissionCommand permissionCommand2 = new AddPermissionCommand();
    	permissionCommand2.setParameterMap(permissionParam2);
    	
    	// saves instance of permission 2
    	commandBus.dispatch(permissionCommand2);    	

    	// instance of permission 3
    	Map<PermissionParameterEnum, String> permissionParam3 = new HashMap<PermissionParameterEnum, String>();
    	permissionParam3.put(PermissionParameterEnum.PERMISSION_NAME, "Delete");
    	permissionParam3.put(PermissionParameterEnum.PERMISSION_DESCRIPTION, "Remove eTS");
    	
    	AddPermissionCommand permissionCommand3 = new AddPermissionCommand();
    	permissionCommand3.setParameterMap(permissionParam3);
    	
    	// saves instance of permission 3
    	commandBus.dispatch(permissionCommand3);
    	
        System.out.println("-------------------------------------------------------------");
        
    	// PERMISSION FINDER
    	// retrieves list of saved permissions from iBatis    	
        List permissions = permissionFinder.findAllPermissions();

    	// displays list of saved permissions
        System.out.println("TOTAL NUMBER OF PERMISSIONS: " + String.valueOf(permissions.size()));
        for(int ctr=0; ctr<permissions.size();ctr++){
            HashMap pm = (HashMap) permissions.get(ctr);
            System.out.println("ID: " + pm.get("ID") + " | NAME: " + pm.get("NAME") + " | DESCRIPTION: "  + pm.get("DESCRIPTION"));        	
        }
        
        System.out.println("-------------------------------------------------------------");
        
    	
    	// ADD ROLE COMMAND
    	// instance of role
    	Map<RoleParameterEnum, String> roleParam = new HashMap<RoleParameterEnum, String>();
    	roleParam.put(RoleParameterEnum.ROLE_NAME, "Maker");
    	roleParam.put(RoleParameterEnum.ROLE_DESCRIPTION, "Branch Maker");
    	
    	AddRoleCommand roleCommand = new AddRoleCommand();
    	roleCommand.setParameterMap(roleParam);
    	
    	// saves instance of role
    	commandBus.dispatch(roleCommand);
    	
        System.out.println("-------------------------------------------------------------");
        
    	
        // ROLE FINDER
    	// retrieves list of saved roles from iBatis    	
        List roles = roleFinder.findAllRoles();

    	// displays list of saved roles
        System.out.println("TOTAL NUMBER OF ROLES: " + String.valueOf(roles.size()));
        for(int ctr=0; ctr<roles.size();ctr++){
            HashMap rl = (HashMap) roles.get(ctr);
            System.out.println("ID: " + rl.get("ID") + " | NAME: " + rl.get("NAME") + " | DESCRIPTION: "  + rl.get("DESCRIPTION"));        	
        }    	

        System.out.println("----------------------------------------------------------");
        

        Map<RoleParameterEnum, String> roleParameter = new HashMap<RoleParameterEnum, String>();
        
        // assume that there is role id = 4
        roleParameter.put(RoleParameterEnum.ROLE_ID, "4");
        
        Long savedRoleId = Long.valueOf(roleParameter.get(RoleParameterEnum.ROLE_ID));

        // retrieves permission per id        
        HashMap savedRole = (HashMap) roleFinder.findRole(savedRoleId);
        System.out.println("THE ROLE WITH ID# " + savedRoleId + " IS:");
        System.out.println("ID: " + savedRole.get("ID") + " | NAME: " + savedRole.get("NAME") + " | DESCRIPTION: "  + savedRole.get("DESCRIPTION"));
        
        
        //ADD PERMISSIONS TO ROLE COMMAND
        // assume that there is permission id = 1
    	Map<PermissionParameterEnum, String> permissionToAddParam1 = new HashMap<PermissionParameterEnum, String>();
    	permissionToAddParam1.put(PermissionParameterEnum.PERMISSION_ID, "1");

        // assume that there is permission id = 3
    	Map<PermissionParameterEnum, String> permissionToAddParam2 = new HashMap<PermissionParameterEnum, String>();
    	permissionToAddParam2.put(PermissionParameterEnum.PERMISSION_ID, "3");

    	// adds permissions 1 and 3 to permission list
    	List<Map<PermissionParameterEnum, String>> permissionsToAddList = new ArrayList<Map<PermissionParameterEnum, String>>();
    	permissionsToAddList.add(permissionToAddParam1);
    	permissionsToAddList.add(permissionToAddParam2);
    	
    	AddPermissionsToRoleCommand addPermissionCommand = new AddPermissionsToRoleCommand();
    	
    	addPermissionCommand.setParameterMap(roleParameter);
    	addPermissionCommand.setPermissionList(permissionsToAddList);
    	
    	commandBus.dispatch(addPermissionCommand);
        
        System.out.println("-------------------------------------------------------------");
        
        
        Map<RoleParameterEnum, String> roleParamName = new HashMap<RoleParameterEnum, String>();
        
        // assume that there is role id = 1
        roleParamName.put(RoleParameterEnum.ROLE_NAME, "Make");
        
        String roleName = roleParamName.get(RoleParameterEnum.ROLE_NAME);

        // retrieves list of saved roles from iBatis by name
        List rolesByName = roleFinder.findAllRolesByName("%"+roleName+"%");
        
        System.out.println("ROLES WITH NAME LIKE %" + roleName + "%");        
        for(int ctr=0; ctr<rolesByName.size();ctr++){
            HashMap roleByName = (HashMap) rolesByName.get(ctr);
            System.out.println("ID: " + roleByName.get("ID") + " | NAME: " + roleByName.get("NAME") + " | DESCRIPTION: "  + roleByName.get("DESCRIPTION"));        	
        }           
        
        System.out.println("-------------------------------------------------------------");
        
        
        Map<RoleParameterEnum, String> retrieveRoleParam = new HashMap<RoleParameterEnum, String>();
        
        // assume that the role id saved is 4
        retrieveRoleParam.put(RoleParameterEnum.ROLE_ID,"4");
        Long roleId = Long.valueOf(retrieveRoleParam.get(RoleParameterEnum.ROLE_ID));
        
        
    	// retrieves list of permissions of saved role from iBatis        
        List permissionsOfRole = roleFinder.findAllPermissionsByRole(roleId);
        
        // displays list of permissions of saved role
        System.out.println("TOTAL NUMBER OF PERMISSIONS OF SAVED ROLE: " + String.valueOf(permissionsOfRole.size()));
        for(int ctr=0; ctr<permissionsOfRole.size();ctr++){
            HashMap pr = (HashMap) permissionsOfRole.get(ctr);
            System.out.println("ID: " + pr.get("ID") + " | NAME: " + pr.get("NAME") + " | DESCRIPTION: "  + pr.get("DESCRIPTION"));        	
        }
        
        
        // REMOVE PERMISSIONS FROM ROLE COMMAND
        
        // assume that there is permission id = 1
    	Map<PermissionParameterEnum, String> permissionToRemoveParam1 = new HashMap<PermissionParameterEnum, String>();
    	permissionToRemoveParam1.put(PermissionParameterEnum.PERMISSION_ID, "1");

    	// adds permissions 1 and 3 to permission list
    	List<Map<PermissionParameterEnum, String>> permissionsToRemoveList = new ArrayList<Map<PermissionParameterEnum, String>>();
    	permissionsToRemoveList.add(permissionToRemoveParam1);
    	
    	RemovePermissionsFromRoleCommand removePermissionCommand = new RemovePermissionsFromRoleCommand();
    	
    	removePermissionCommand.setParameterMap(roleParameter);
    	removePermissionCommand.setPermissionList(permissionsToRemoveList);
    	
    	commandBus.dispatch(removePermissionCommand);
    	
    	
    	// retrieves list of permissions of saved role from iBatis        
        List newPermissionsOfRole = roleFinder.findAllPermissionsByRole(roleId);

        // displays list of permissions of saved role
        System.out.println("NEW TOTAL NUMBER OF PERMISSIONS OF SAVED ROLE: " + String.valueOf(newPermissionsOfRole.size()));
        for(int ctr=0; ctr<newPermissionsOfRole.size();ctr++){
            HashMap npr = (HashMap) newPermissionsOfRole.get(ctr);
            System.out.println("ID: " + npr.get("ID") + " | NAME: " + npr.get("NAME") + " | DESCRIPTION: "  + npr.get("DESCRIPTION"));        	
        }    	
        
        System.out.println("-------------------------------------------------------------");
        
        
        // UPDATE ROLE
        Map<RoleParameterEnum, String> roleParamEdit = new HashMap<RoleParameterEnum, String>();
        
        // assume that there is role id = 4
        roleParamEdit.put(RoleParameterEnum.ROLE_ID, "4");
        roleParamEdit.put(RoleParameterEnum.ROLE_NAME, "Branch Maker");
        roleParamEdit.put(RoleParameterEnum.ROLE_DESCRIPTION, "Branch Maker");
        
        UpdateRoleCommand updateCommand = new UpdateRoleCommand();
        updateCommand.setParameterMap(roleParamEdit);
        
        // updates role with id = 4
        commandBus.dispatch(updateCommand);
	}
	
}
