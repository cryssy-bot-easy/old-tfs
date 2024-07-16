package com.ipc.rbac.application.commandhandler.authorization;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.command.CommandBus;
import com.ipc.rbac.application.command.authorization.AddAuthorizationCommand;
import com.ipc.rbac.application.command.authorization.ExtendAuthorityCommand;
import com.ipc.rbac.application.command.authorization.GrantAuthorityCommand;
import com.ipc.rbac.application.command.authorization.RevokeAuthorityCommand;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorityParameterEnum;
import com.ipc.rbac.application.command.authorization.enumTypes.AuthorizationParameterEnum;
import com.ipc.rbac.application.command.permission.AddPermissionCommand;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;
import com.ipc.rbac.application.command.role.AddPermissionsToRoleCommand;
import com.ipc.rbac.application.command.role.AddRoleCommand;
import com.ipc.rbac.application.command.role.enumTypes.RoleParameterEnum;
import com.ipc.rbac.application.command.user.AddUserCommand;
import com.ipc.rbac.application.command.user.enumTypes.UserParameterEnum;
import com.ipc.rbac.application.query.authorization.IAuthorizationFinder;
import com.ipc.rbac.application.query.permission.IPermissionFinder;
import com.ipc.rbac.application.query.role.IRoleFinder;
import com.ipc.rbac.application.query.user.IUserFinder;
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
public class TestAuthorizationCommands {
	
	@Autowired
	CommandBus commandBus;
	
	@Autowired
	private IPermissionFinder permissionFinder;
	
	@Autowired
	private IRoleFinder roleFinder;
	
    @Autowired
    private IUserFinder userFinder;
    
    @Autowired
    private IAuthorizationFinder authorizationFinder;
    
    @Test
    public void AddAuthorizationContext() {

    	// PERMISSION CONTEXT
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
    	permissionParam2.put(PermissionParameterEnum.PERMISSION_NAME, "View");
    	permissionParam2.put(PermissionParameterEnum.PERMISSION_DESCRIPTION, "Read eTS");
    	
    	AddPermissionCommand permissionCommand2 = new AddPermissionCommand();
    	permissionCommand2.setParameterMap(permissionParam2);
    	
    	// saves instance of permission 2
    	commandBus.dispatch(permissionCommand2);    	

    	// instance of permission 3
    	Map<PermissionParameterEnum, String> permissionParam3 = new HashMap<PermissionParameterEnum, String>();
    	permissionParam3.put(PermissionParameterEnum.PERMISSION_NAME, "Edit");
    	permissionParam3.put(PermissionParameterEnum.PERMISSION_DESCRIPTION, "Update eTS");
    	
    	AddPermissionCommand permissionCommand3 = new AddPermissionCommand();
    	permissionCommand3.setParameterMap(permissionParam3);
    	
    	// saves instance of permission 3
    	commandBus.dispatch(permissionCommand3); 
    	
    	// instance of permission 4
    	Map<PermissionParameterEnum, String> permissionParam4 = new HashMap<PermissionParameterEnum, String>();
    	permissionParam4.put(PermissionParameterEnum.PERMISSION_NAME, "Delete");
    	permissionParam4.put(PermissionParameterEnum.PERMISSION_DESCRIPTION, "Delete eTS");
    	
    	AddPermissionCommand permissionCommand4 = new AddPermissionCommand();
    	permissionCommand4.setParameterMap(permissionParam4);
    	
    	// saves instance of permission 4
    	commandBus.dispatch(permissionCommand4);      	
    	
    	// retrieves list of saved permissions from iBatis    	
        List permissions = permissionFinder.findAllPermissions();

    	// displays list of saved permissions
        System.out.println("TOTAL NUMBER OF PERMISSIONS: " + String.valueOf(permissions.size()));
        for(int ctr=0; ctr<permissions.size();ctr++){
            HashMap pm = (HashMap) permissions.get(ctr);
            System.out.println("ID: " + pm.get("ID") + " | NAME: " + pm.get("NAME") + " | DESCRIPTION: "  + pm.get("DESCRIPTION"));        	
        }
    	
    	// ROLE CONTEXT
    	// instance of role
    	Map<RoleParameterEnum, String> roleParam = new HashMap<RoleParameterEnum, String>();
    	roleParam.put(RoleParameterEnum.ROLE_NAME, "Maker");
    	roleParam.put(RoleParameterEnum.ROLE_DESCRIPTION, "Branch Maker");
    	
    	AddRoleCommand roleCommand = new AddRoleCommand();
    	roleCommand.setParameterMap(roleParam);
    	
    	// saves instance of role
    	commandBus.dispatch(roleCommand);
    	
    	// ADDS PERMISSION TO ROLE
        // assume that there is permission id = 1
    	Map<PermissionParameterEnum, String> permissionToAddParam1 = new HashMap<PermissionParameterEnum, String>();
    	permissionToAddParam1.put(PermissionParameterEnum.PERMISSION_ID, "1");

        // assume that there is permission id = 3
    	Map<PermissionParameterEnum, String> permissionToAddParam2 = new HashMap<PermissionParameterEnum, String>();
    	permissionToAddParam2.put(PermissionParameterEnum.PERMISSION_ID, "3");

    	// adds permissions 1 and 3 to permission list
    	List<Map<PermissionParameterEnum, String>> permissionList = new ArrayList<Map<PermissionParameterEnum, String>>();
    	permissionList.add(permissionToAddParam1);
    	permissionList.add(permissionToAddParam2);      	
    	
    	Map<RoleParameterEnum, String> roleMap = new HashMap<RoleParameterEnum, String>();
    	roleMap.put(RoleParameterEnum.ROLE_ID, "5");
    	
    	AddPermissionsToRoleCommand addPermissionToRole = new AddPermissionsToRoleCommand();
    	addPermissionToRole.setParameterMap(roleMap);
    	addPermissionToRole.setPermissionList(permissionList);
    	
    	commandBus.dispatch(addPermissionToRole);
    	
    	// retrieves list of saved roles from iBatis    	
        List roles = roleFinder.findAllRoles();

    	// displays list of saved roles
        System.out.println("TOTAL NUMBER OF ROLES: " + String.valueOf(roles.size()));
        for(int ctr=0; ctr<roles.size();ctr++){
            HashMap rl = (HashMap) roles.get(ctr);
            System.out.println("ID: " + rl.get("ID") + " | NAME: " + rl.get("NAME") + " | DESCRIPTION: "  + rl.get("DESCRIPTION"));        	
        }    	
    	
        Map<RoleParameterEnum, String> retrieveRoleParam = new HashMap<RoleParameterEnum, String>();
        
        // assume that the role id saved is 5
        retrieveRoleParam.put(RoleParameterEnum.ROLE_ID,"5");
        Long roleId = Long.valueOf(retrieveRoleParam.get(RoleParameterEnum.ROLE_ID));
        
    	// retrieves list of permissions of saved role from iBatis        
        List permissionsOfRole = roleFinder.findAllPermissionsByRole(roleId);
        
        // displays list of permissions of saved role
        System.out.println("TOTAL NUMBER OF PERMISSIONS OF SAVED ROLE: " + String.valueOf(permissionsOfRole.size()));
        for(int ctr=0; ctr<permissionsOfRole.size();ctr++){
            HashMap pr = (HashMap) permissionsOfRole.get(ctr);
            System.out.println("ID: " + pr.get("ID") + " | NAME: " + pr.get("NAME") + " | DESCRIPTION: "  + pr.get("DESCRIPTION"));        	
        }
        
        
        // USER CONTEXT
		// instance of user
    	Map<UserParameterEnum, String> param = new HashMap<UserParameterEnum, String>();
    	param.put(UserParameterEnum.USER_ACTIVE_DIRECTORY_ID, "0001");
    	param.put(UserParameterEnum.FIRST_NAME, "Marvin");
    	param.put(UserParameterEnum.LAST_NAME, "Volante");
    	
    	AddUserCommand command = new AddUserCommand();
    	command.setParameterMap(param);
    	
    	// saves instance of user
    	commandBus.dispatch(command);
    	
    	// retrieves list of saved users from iBatis    	
        List users = userFinder.findAllUsers();

    	// displays list of saved users
        System.out.println("TOTAL NUMBER OF USERS: " + String.valueOf(users.size()));
        for(int ctr=0; ctr<users.size();ctr++){
            HashMap us = (HashMap) users.get(ctr);
            System.out.println("ID: " + us.get("ID") + " | USERACTIVEDIRECTORYUID: " + us.get("USERACTIVEDIRECTORYUID") + " | FIRSTNAME: "  + us.get("FIRSTNAME") + " | LASTNAME: "  + us.get("LASTNAME"));
        }
        
        
        // AUTHORIZATION CONTEXT
        Map<AuthorizationParameterEnum,String> authorizationParam = new HashMap<AuthorizationParameterEnum, String>();
        authorizationParam.put(AuthorizationParameterEnum.USER, "1");
        
        AddAuthorizationCommand addCommand = new AddAuthorizationCommand();
        
        addCommand.setParameterMap(authorizationParam);
        commandBus.dispatch(addCommand);
        
        System.out.println("----------------------------------------------------");

        // ADD AUTHORITY TO AUTHORIZATION COMMAND        
        // adds permission id 2 to authority list        
        Map<AuthorityParameterEnum, String> permissionParamAuthority2 = new HashMap<AuthorityParameterEnum, String>();
        permissionParamAuthority2.put(AuthorityParameterEnum.AUTHORITY_TYPE, "2");
        permissionParamAuthority2.put(AuthorityParameterEnum.EFFECTIVE_FROM, "01/01/2012");
        permissionParamAuthority2.put(AuthorityParameterEnum.EFFECTIVE_TO, "10/01/2012");

    	List<Map<AuthorityParameterEnum, String>> authorityParam2 = new ArrayList<Map<AuthorityParameterEnum, String>>();
        authorityParam2.add(permissionParamAuthority2);
        
        Map<AuthorizationParameterEnum, String> savedAuthParam = new HashMap<AuthorizationParameterEnum, String>();
        savedAuthParam.put(AuthorizationParameterEnum.AUTHORIZATION_ID, "1");
        
        GrantAuthorityCommand addAuthorityCommand = new GrantAuthorityCommand();
        addAuthorityCommand.setParameterMap(savedAuthParam);
        addAuthorityCommand.setAuthorityList(authorityParam2);
        
        // adds authority to authorization
        commandBus.dispatch(addAuthorityCommand);
        
        System.out.println("----------------------------------------------------");
        
        
        // ADD AUTHORITY TO AUTHORIZATION COMMAND
        // adds role id 5 and permission id 4 to authority list
        Map<AuthorityParameterEnum, String> roleParamAuthority = new HashMap<AuthorityParameterEnum, String>();
        roleParamAuthority.put(AuthorityParameterEnum.AUTHORITY_TYPE, "5");
        roleParamAuthority.put(AuthorityParameterEnum.EFFECTIVE_FROM, "02/02/2012");
        roleParamAuthority.put(AuthorityParameterEnum.EFFECTIVE_TO, "11/02/2012");
        
        Map<AuthorityParameterEnum, String> permissionParamAuthority = new HashMap<AuthorityParameterEnum, String>();
        permissionParamAuthority.put(AuthorityParameterEnum.AUTHORITY_TYPE, "4");
        permissionParamAuthority.put(AuthorityParameterEnum.EFFECTIVE_FROM, "03/03/2012");
        permissionParamAuthority.put(AuthorityParameterEnum.EFFECTIVE_TO, "12/03/2012");
        
        
    	List<Map<AuthorityParameterEnum, String>> authorityParam = new ArrayList<Map<AuthorityParameterEnum, String>>();
    	authorityParam.add(roleParamAuthority);
    	authorityParam.add(permissionParamAuthority);
    	
        GrantAuthorityCommand addAuthorityCommand2 = new GrantAuthorityCommand();
        addAuthorityCommand2.setParameterMap(savedAuthParam);
        addAuthorityCommand2.setAuthorityList(authorityParam);
    	
        // adds authority to authorization        
    	commandBus.dispatch(addAuthorityCommand2);
    	
        System.out.println("----------------------------------------------------");
    	
        // REMOVE AUTHORITY FROM AUTHORIZATION COMMAND
        Map<AuthorityParameterEnum, String> permissionRemove = new HashMap<AuthorityParameterEnum, String>();
        permissionRemove.put(AuthorityParameterEnum.AUTHORITY_ID, "1");

        // adds authority id 1 to authorities to remove
    	List<Map<AuthorityParameterEnum, String>> authParm = new ArrayList<Map<AuthorityParameterEnum, String>>();
    	authParm.add(permissionRemove);
        
        RevokeAuthorityCommand removeAuthorityCommand = new RevokeAuthorityCommand();
        removeAuthorityCommand.setParameterMap(savedAuthParam);
        removeAuthorityCommand.setAuthorityList(authParm);
        
        // removes authority from authorization
        commandBus.dispatch(removeAuthorityCommand);
        
        System.out.println("----------------------------------------------------");
        
        // EXTEND AUTHORITY EFFECTIVE TO COMMAND
        Map<AuthorityParameterEnum, String> permissionExtend = new HashMap<AuthorityParameterEnum, String>();
        permissionExtend.put(AuthorityParameterEnum.AUTHORITY_ID, "3");
        permissionExtend.put(AuthorityParameterEnum.EFFECTIVE_TO, "12/31/2012");

    	List<Map<AuthorityParameterEnum, String>> authorityExtendList = new ArrayList<Map<AuthorityParameterEnum, String>>();
    	authorityExtendList.add(permissionExtend);
        
        ExtendAuthorityCommand extendCommand = new ExtendAuthorityCommand();
        extendCommand.setParameterMap(savedAuthParam);        
        extendCommand.setAuthorityList(authorityExtendList);
        
        // saves changes to effective to of saved authorization
    	commandBus.dispatch(extendCommand);
    	
        System.out.println("----------------------------------------------------");
        
    	// FINDER
    	// retrieves list of saved permissions from iBatis
    	HashMap savedAuthorization = (HashMap) authorizationFinder.findAuthorization(new Long(1));
    	
    	// retrieves active permissions by user id from iBatis
/*
    	List authorities = authorizationFinder.findAllActivePermissionsByUser("0001");
        
    	System.out.println("LIST OF ACTIVE PERMISSIONS:");
    	
    	// displays list of active permissions
        for(int ctr=0; ctr<authorities.size();ctr++){
            HashMap auth = (HashMap) authorities.get(ctr);
            System.out.println("FULLNAME: " + auth.get("FIRSTNAME") + " " + auth.get("LASTNAME")
            		+ " | USERACTIVEDIRECTORYID: " + auth.get("USERACTIVEDIRECTORYUID") + " | AUTHORITYTYPENAME: " + auth.get("NAME")
            		+ " | EFFECTIVEFROM: " + auth.get("EFFECTIVEFROM") + " | EFFECTIVETO: " + auth.get("EFFECTIVETO"));
        }
*/
    }

}
