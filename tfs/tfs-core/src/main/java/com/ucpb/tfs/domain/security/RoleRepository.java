package com.ucpb.tfs.domain.security;

import java.util.List;

/**
 * User: Jett
 * Date: 9/21/12
 */
public interface RoleRepository {

    public void save(Role role);

    public Role getRole(RoleId role);

    public Long getCount();

    public List getAllRoles();

    public List getRolesMatchingADGroups(List<String> adGroups);

}
