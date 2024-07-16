package com.ipc.rbac.domain;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

public interface AuthorityRepository {
	
    public Authority getAuthority(Long id);
    
    public void delete(Authority authority);
    
    public void persist(Authority authority);
	
	public Permission getPermission(Long id);
    
    public Role getRole(Long id);
    
}
