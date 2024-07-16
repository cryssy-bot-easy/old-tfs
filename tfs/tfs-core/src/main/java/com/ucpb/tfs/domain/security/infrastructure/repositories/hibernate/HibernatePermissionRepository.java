package com.ucpb.tfs.domain.security.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.security.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Jett
 * Date: 9/21/12
 */
public class HibernatePermissionRepository implements PermissionRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Permission permission) {
        sessionFactory.getCurrentSession().persist(permission);
    }

    @Override
    public Permission getPermission(PermissionId permissionId) {
        return (Permission) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.security.Permission where permissionId = ?").setParameter(0, permissionId).uniqueResult();

    }

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.security.Permission").iterate().next() ).longValue();
    }

}
