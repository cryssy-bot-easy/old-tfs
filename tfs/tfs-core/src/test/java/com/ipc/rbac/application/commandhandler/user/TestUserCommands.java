package com.ipc.rbac.application.commandhandler.user;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.command.CommandBus;
import com.ipc.rbac.application.command.user.AddUserCommand;
import com.ipc.rbac.application.command.user.UpdateUserCommand;
import com.ipc.rbac.application.command.user.enumTypes.UserParameterEnum;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestUserCommands {

	@Autowired
	CommandBus commandBus;
	
	@Autowired
    private IUserFinder userFinder;

	@Test
	public void AddUserContext() {

		// instance of user 1
    	Map<UserParameterEnum, String> param1 = new HashMap<UserParameterEnum, String>();
    	param1.put(UserParameterEnum.USER_ACTIVE_DIRECTORY_ID, "0001");
    	param1.put(UserParameterEnum.FIRST_NAME, "Marvin");
    	param1.put(UserParameterEnum.LAST_NAME, "Volante");
    	
    	AddUserCommand command1 = new AddUserCommand();
    	command1.setParameterMap(param1);
    	
    	// saves instance of user
    	commandBus.dispatch(command1);
    	
		// instance of user 2
    	Map<UserParameterEnum, String> param2 = new HashMap<UserParameterEnum, String>();
    	param2.put(UserParameterEnum.USER_ACTIVE_DIRECTORY_ID, "0002");
    	param2.put(UserParameterEnum.FIRST_NAME, "Angelica");
    	param2.put(UserParameterEnum.LAST_NAME, "Panganiban");
    	
    	AddUserCommand command2 = new AddUserCommand();
    	command2.setParameterMap(param2);
    	
    	// saves instance of user
    	commandBus.dispatch(command2);    	
    	
    	// retrieves list of saved users from iBatis    	
        List users = userFinder.findAllUsers();

    	// displays list of saved users
        System.out.println("TOTAL NUMBER OF USERS: " + String.valueOf(users.size()));
        for(int ctr=0; ctr<users.size();ctr++){
            HashMap us = (HashMap) users.get(ctr);
            System.out.println("ID: " + us.get("USERACTIVEDIRECTORYID") + " | USERACTIVEDIRECTORYID: " + us.get("USERACTIVEDIRECTORYID") + " | FIRSTNAME: "  + us.get("FIRSTNAME") + " | LASTNAME: "  + us.get("LASTNAME"));
        }
        
        System.out.println("--------------------------------------------------");
        

        /*
        // assume there is user id = 1
        Map<UserParameterEnum, String> userParam = new HashMap<UserParameterEnum, String>();
        userParam.put(UserParameterEnum.USER_ID, "1");
        
        Long userId = Long.valueOf(userParam.get(UserParameterEnum.USER_ID));
        
        HashMap savedUser = (HashMap) userFinder.findUser(userId);
        
        System.out.println("THE USER WITH ID# " + userId + " IS:");
        System.out.println("ID: " + savedUser.get("USERACTIVEDIRECTORYID") + " | USERACTIVEDIRECTORYID: " + savedUser.get("USERACTIVEDIRECTORYID") + " | FIRSTNAME: "  + savedUser.get("FIRSTNAME") + " | LASTNAME: "  + savedUser.get("LASTNAME"));

        System.out.println("--------------------------------------------------");
        */
        
        Map<UserParameterEnum, String> userParamLastName = new HashMap<UserParameterEnum, String>();
        userParamLastName.put(UserParameterEnum.LAST_NAME, "niban");
        
        String userLastName = userParamLastName.get(UserParameterEnum.LAST_NAME);
        
        // retrieves list of saved users from iBatis  by last name
        List savedUsersByLastName = userFinder.findAllUsersByLastName("%"+userLastName+"%");
        
        System.out.println("USERS WITH LAST NAME LIKE %" + userLastName + "%");
        for(int ctr=0; ctr<savedUsersByLastName.size();ctr++){
            HashMap usByLastName = (HashMap) savedUsersByLastName.get(ctr);
            System.out.println("ID: " + usByLastName.get("USERACTIVEDIRECTORYID") + " | USERACTIVEDIRECTORYID: " + usByLastName.get("USERACTIVEDIRECTORYID") + " | FIRSTNAME: "  + usByLastName.get("FIRSTNAME") + " | LASTNAME: "  + usByLastName.get("LASTNAME"));
        }  
        
        System.out.println("--------------------------------------------------");
        
        
        Map<UserParameterEnum, String> userParamADUId = new HashMap<UserParameterEnum, String>();
        userParamADUId.put(UserParameterEnum.USER_ACTIVE_DIRECTORY_ID, "1");
        
        String userADUId = userParamADUId.get(UserParameterEnum.USER_ACTIVE_DIRECTORY_ID);
        
        // retrieves list of saved users from iBatis  by active directory id
        List savedUsersByADUId = userFinder.findAllUsersByActiveDirectoryUid("%"+userADUId+"%");
        
        System.out.println("USERS WITH ACTIVE DIRECTORY ID LIKE %" + userADUId + "%");
        for(int ctr=0; ctr<savedUsersByADUId.size();ctr++){
            HashMap usByADUId = (HashMap) savedUsersByADUId.get(ctr);
            System.out.println("ID: " + usByADUId.get("USERACTIVEDIRECTORYID") + " | USERACTIVEDIRECTORYID: " + usByADUId.get("USERACTIVEDIRECTORYID") + " | FIRSTNAME: "  + usByADUId.get("FIRSTNAME") + " | LASTNAME: "  + usByADUId.get("LASTNAME"));
        }
        
        System.out.println("---------------------------------------------------");
        
        
        Map<UserParameterEnum, String> userParamEdit = new HashMap<UserParameterEnum, String>();
        
        // assume that there is permission id = 1
        userParamEdit.put(UserParameterEnum.USER_ID, "1");
        userParamEdit.put(UserParameterEnum.USER_ACTIVE_DIRECTORY_ID, "0001");
        userParamEdit.put(UserParameterEnum.FIRST_NAME, "Derek");
        userParamEdit.put(UserParameterEnum.LAST_NAME, "Ramsay");
        
        UpdateUserCommand updateCommand = new UpdateUserCommand();
        updateCommand.setParameterMap(userParamEdit);
        
        // updates permission with id = 1
        commandBus.dispatch(updateCommand);
	}
	
}
