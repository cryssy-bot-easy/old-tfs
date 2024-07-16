package com.ipc.rbac.domain;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

public class UserFactory {

    public com.ipc.rbac.domain.User createUser(UserActiveDirectoryId userActiveDirectoryId, String firstName, String lastName) {

        User user = new User(userActiveDirectoryId, firstName, lastName);

        return user;
    }		
	
}
