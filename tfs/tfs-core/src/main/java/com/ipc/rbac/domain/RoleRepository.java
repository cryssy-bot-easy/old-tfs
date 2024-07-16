package com.ipc.rbac.domain;

public interface RoleRepository {

    public com.ipc.rbac.domain.Role getRole(Long id);

    public void persist(com.ipc.rbac.domain.Role role);
    
    public com.ipc.rbac.domain.Role persistChanges(Role role);

    public Long getCount();
}
