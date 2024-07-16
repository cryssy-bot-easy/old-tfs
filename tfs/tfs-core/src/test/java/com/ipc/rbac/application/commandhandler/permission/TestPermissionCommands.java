package com.ipc.rbac.application.commandhandler.permission;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.command.CommandBus;
import com.ipc.rbac.application.command.permission.AddPermissionCommand;
import com.ipc.rbac.application.command.permission.UpdatePermissionCommand;
import com.ipc.rbac.application.command.permission.enumTypes.PermissionParameterEnum;
import com.ipc.rbac.application.query.permission.IPermissionFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestPermissionCommands {

	@Autowired
	CommandBus commandBus;
	
	@Autowired
	private IPermissionFinder permissionFinder;
	
    @Test
    public void AddPermissionContext() {

    	// instance of permission 1
    	Map<PermissionParameterEnum, String> permissionParam1 = new HashMap<PermissionParameterEnum, String>();
    	permissionParam1.put(PermissionParameterEnum.PERMISSION_NAME, "Add");
    	permissionParam1.put(PermissionParameterEnum.PERMISSION_DESCRIPTION, "Create eTS");
    	
    	AddPermissionCommand permissionCommand1 = new AddPermissionCommand();
    	permissionCommand1.setParameterMap(permissionParam1);
    	
    	// saves instance of permission 1
    	commandBus.dispatch(permissionCommand1);
    	
    	// instance of permission context 2  
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
            HashMap permission = (HashMap) permissions.get(ctr);
            System.out.println("ID: " + permission.get("ID") + " | NAME: " + permission.get("NAME") + " | DESCRIPTION: "  + permission.get("DESCRIPTION"));        	
        }
        
        System.out.println("-------------------------------------------------------------");
        

        Map<PermissionParameterEnum, String> permissionParam = new HashMap<PermissionParameterEnum, String>();
        
        // assume that there is permission id = 1
        permissionParam.put(PermissionParameterEnum.PERMISSION_ID, "1");
        
        Long permissionId = Long.valueOf(permissionParam.get(PermissionParameterEnum.PERMISSION_ID));

        // retrieves permission per id        
        HashMap savedPermission = (HashMap) permissionFinder.findPermission(permissionId);
        System.out.println("THE PERMISSION WITH ID# " + permissionId + " IS:");
        System.out.println("ID: " + savedPermission.get("ID") + " | NAME: " + savedPermission.get("NAME") + " | DESCRIPTION: "  + savedPermission.get("DESCRIPTION"));
        
        Map<PermissionParameterEnum, String> permissionParamName = new HashMap<PermissionParameterEnum, String>();
        
        // assume that there is permission id = 1
        permissionParamName.put(PermissionParameterEnum.PERMISSION_NAME, "A");
        
        String permissionName= permissionParamName.get(PermissionParameterEnum.PERMISSION_NAME);
        
        // retrieves list of saved permissions from iBatis by name
        List savedPermissionsByName = permissionFinder.findAllPermissionsByName("%"+permissionName+"%");
        
        // displays list of saved permissions by name
        System.out.println("PERMISSIONS WITH NAME LIKE %" + permissionName + "%");
        for(int ctr=0; ctr<savedPermissionsByName.size();ctr++){
            HashMap permissionByName = (HashMap) savedPermissionsByName.get(ctr);
            System.out.println("ID: " + permissionByName.get("ID") + " | NAME: " + permissionByName.get("NAME") + " | DESCRIPTION: "  + permissionByName.get("DESCRIPTION"));        	
        }        

        System.out.println("-------------------------------------------------------------");
        
        
        Map<PermissionParameterEnum, String> permissionParamEdit = new HashMap<PermissionParameterEnum, String>();
        
        // assume that there is permission id = 1
        permissionParamEdit.put(PermissionParameterEnum.PERMISSION_ID, "1");
        permissionParamEdit.put(PermissionParameterEnum.PERMISSION_NAME, "Add eTS");
        permissionParamEdit.put(PermissionParameterEnum.PERMISSION_DESCRIPTION, "Create eTS");
        
        UpdatePermissionCommand updateCommand = new UpdatePermissionCommand();
        updateCommand.setParameterMap(permissionParamEdit);
        
        // updates permission with id = 1
        commandBus.dispatch(updateCommand);
    }
    
}
