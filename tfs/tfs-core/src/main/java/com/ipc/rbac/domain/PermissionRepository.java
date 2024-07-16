package com.ipc.rbac.domain;

public interface PermissionRepository {

    public com.ipc.rbac.domain.Permission getPermission(Long permissionId);

    public void persist(com.ipc.rbac.domain.Permission permission);
    
    public com.ipc.rbac.domain.Permission persistChanges(com.ipc.rbac.domain.Permission permission);

    public Long getCount();
    
}
