package com.ucpb.tfs.domain.security;

/**
 * User: Jett
 * Date: 9/21/12
 */
public interface ObjectRepository {

    public void save(Object object);

    public Object getObject(String code);

    Long getCount();
}
