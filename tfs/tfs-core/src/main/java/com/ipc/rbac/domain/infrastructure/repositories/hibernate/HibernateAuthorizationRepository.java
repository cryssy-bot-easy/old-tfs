package com.ipc.rbac.domain.infrastructure.repositories.hibernate;

import com.ipc.rbac.domain.AuthorityType;
import com.ipc.rbac.domain.Authorization;
import com.ipc.rbac.domain.AuthorizationRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * User: Jett
 * Date: 6/20/12
 */
@Repository
@Component
public class HibernateAuthorizationRepository implements AuthorizationRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    // get authority type (role / permission) from parameter authority type id
    @Override
    public AuthorityType getAuthorityType(Long authorityTypeId) {
        return (AuthorityType) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.AuthorityType where id = ?").setParameter(0, authorityTypeId).uniqueResult();
    }      

    // get authorization from parameter id
    @Override
    public Authorization getAuthorization(Long id) {
        return (Authorization) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.Authorization where id = ?").setParameter(0, id).uniqueResult();
    }

    // persists authorization
    @Override
    public void persist(Authorization authorization) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(authorization);
    }
    
    // persists changes to authorization
    @Override
    public Authorization persistChanges(Authorization authorization) {
        Session session = this.sessionFactory.getCurrentSession();
        Authorization a = (Authorization) session.merge(authorization);
        return a;
    }

}
