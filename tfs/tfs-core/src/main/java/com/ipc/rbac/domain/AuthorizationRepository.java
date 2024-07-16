package com.ipc.rbac.domain;

/**
 * User: Jett
 * Date: 6/20/12
 */
public interface AuthorizationRepository {
	
	public AuthorityType getAuthorityType(Long authorityTypeId);	
	
	public Authorization getAuthorization(Long id);

	public void persist(Authorization authorization);
	
	public Authorization persistChanges(Authorization authorization);
	
//	public Authority getAuthority(Long id);

}
