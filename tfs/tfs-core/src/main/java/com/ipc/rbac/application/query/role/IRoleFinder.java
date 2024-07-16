package com.ipc.rbac.application.query.role;

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
public interface IRoleFinder {
	
	Map<String,?> findRole(@Param("roleId") Long roleId);
	
    List<Map<String,?>> findAllPermissionsByRole(@Param("roleId") Long roleId);
    
    List<Map<String,?>> findAllRolesByName(@Param("roleName") String roleName);
	
	List<Map<String,?>> findAllRoles();
    
}
