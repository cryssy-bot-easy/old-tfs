package com.ipc.rbac.domain;

public interface UserRepository {
	
	public User getUser(UserActiveDirectoryId userActiveDirectoryId);
	
	public void persist(User user);
	
	public User persistChanges(User user);

    public Long getCount();
}
