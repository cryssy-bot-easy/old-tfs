package com.ipc.rbac.application.query.permission;

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
public interface IPermissionFinder {
	
	Map<String,?> findPermission(@Param("permissionId") Long permissionId);
	
	List<Map<String,?>> findAllPermissionsByName(@Param("permissionName") String permissionName);
	
	List<Map<String,?>> findAllPermissions();	

}
