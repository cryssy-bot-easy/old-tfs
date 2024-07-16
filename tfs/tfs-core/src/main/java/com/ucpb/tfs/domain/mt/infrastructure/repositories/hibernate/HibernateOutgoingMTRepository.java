package com.ucpb.tfs.domain.mt.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.cdt.RefPas5Client;
import com.ucpb.tfs.domain.mt.OutgoingMT;
import com.ucpb.tfs.domain.mt.OutgoingMTRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
public class HibernateOutgoingMTRepository implements OutgoingMTRepository{

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(OutgoingMT outgoingMT) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(outgoingMT);
    }

    @Override
    public void merge(OutgoingMT outgoingMT) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(outgoingMT);
    }

    @Override
    public void update(OutgoingMT outgoingMT) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(outgoingMT);
    }

    @Override
    public OutgoingMT load(Long id) {
        return (OutgoingMT) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.mt.OutgoingMT where id = ?").setParameter(0, id).uniqueResult();

    }

    @Override
    public List getAllOutgoingMT() {

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(OutgoingMT.class);

        Query query = session.createQuery("from com.ucpb.tfs.domain.mt.OutgoingMT");

        List outgoingMts = crit.list();

        return outgoingMts;

    }
}
