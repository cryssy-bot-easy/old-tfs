package com.ucpb.tfs.domain.routing.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.routing.Remark;
import com.ucpb.tfs.domain.routing.RemarksRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 */
public class HibernateRemarksRepository implements RemarksRepository {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public void addRemark(Remark remark) {
        sessionFactory.getCurrentSession().persist(remark);
    }

    @Override
    public void editRemark(Remark remark) {
        sessionFactory.getCurrentSession().update(remark);
    }

    @Override
    public List<Remark> getRemarks(String remarkId) {
        return sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.routing.Remark where remarkId = ?")
                .setParameter(0,remarkId).list();
    }
}
