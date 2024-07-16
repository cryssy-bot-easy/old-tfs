package com.ipc.rbac.application.query.user;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Finder
public interface IUserFinder {
	
	Map<String,?> findUser(@Param("userActiveDirectoryId") String userActiveDirectoryId);
	
	List<Map<String,?>> findAllUsersByActiveDirectoryUid(@Param("userActiveDirectoryId") String userActiveDirectoryUid);
	
	List<Map<String,?>> findAllUsersByLastName(@Param("userLastName") String userLastName);
	
	List<Map<String,?>> findAllUsers();

}
