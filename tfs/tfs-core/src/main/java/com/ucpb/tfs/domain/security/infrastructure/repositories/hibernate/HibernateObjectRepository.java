package com.ucpb.tfs.domain.security.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.security.*;
import com.ucpb.tfs.domain.security.Object;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Jett
 * Date: 9/21/12
 */
public class HibernateObjectRepository implements ObjectRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(com.ucpb.tfs.domain.security.Object object) {
        sessionFactory.getCurrentSession().persist(object);
    }

    @Override
    public Object getObject(String code) {
        return (Object) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.security.Object where code = ?").setParameter(0, code).uniqueResult();

    }

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.security.Object").iterate().next() ).longValue();
    }

}
