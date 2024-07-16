package com.ipc.rbac.application.query.authorization;

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
public interface IAuthorizationFinder {
	
	Map<String,?> findAuthorization(@Param("authorizationId") Long authorizationId);
	
	List<Map<String,?>> findAllActivePermissionsByUser(@Param("userActiveDirectoryId") String userActiveDirectoryId);

}
