package com.ucpb.tfs.domain.security;

import java.io.Serializable;
import java.util.Set;

/**
 * User: Jett
 * Date: 9/21/12
 */
public class Role implements Serializable {

    RoleId roleId;

    String description;

    String adGroupName;

    RoleId parentRoleId;

    //    Set<PermissionId> permissions;
    Set<Permission> permissions;

    public Role() {
    }

    public Role(String roleId, String description) {
        this.roleId = new RoleId(roleId);
        this.description = description;

//        permissions = new HashSet<PermissionId>();
//        permissions = new HashSet<Permission>();
    }

//    public void addPermission(PermissionId permission) {
    public void addPermission(Permission permission) {
//        permissions.add(permission);
    }

    public String getId() {
        return roleId.toString();
    }

    public String getDescription() {
        return description;
    }

    public String getAdGroupName() {
        return adGroupName;
    }
}
