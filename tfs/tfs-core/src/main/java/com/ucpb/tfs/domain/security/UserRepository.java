package com.ucpb.tfs.domain.security;

import java.util.Map;

/**
 * User: Jett
 * Date: 9/21/12
 */
public interface UserRepository {

    public void save(User user);
    
    public void persist(User user);

    public User getUser(UserId userId);

    public Long getCount();

    public Map getUserById(UserId userId);
}