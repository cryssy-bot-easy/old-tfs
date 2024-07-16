package com.ucpb.tfs.domain.security;

/**
 * User: Jett
 * Date: 9/21/12
 */
public interface PermissionRepository {

    public void save(Permission permission);

    public Permission getPermission(PermissionId permissionId);

    public Long getCount();

}
