package com.ipc.rbac.domain;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import java.util.List;

public interface AuthorityTypeRepository {
	
    public AuthorityType getAuthorityType(Long id);
    
    public AuthorityType persist(AuthorityType authorityType);
    
    public List<AuthorityType> listAuthorityTypes();
    
}
