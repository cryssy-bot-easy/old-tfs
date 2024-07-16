package com.ucpb.tfs.domain.security.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.security.Designation;
import com.ucpb.tfs.domain.security.DesignationRepository;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 12/27/13
 * Time: 7:10 PM
 * To change this template use File | Settings | File Templates.
 */

@Transactional
public class HibernateDesignationRepository implements DesignationRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Designation> getAllDesignationMatching(String description) {
        Session session  = this.sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Designation.class);

        if (description != null && StringUtils.isNotBlank(description)) {
            criteria.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
        }

        return criteria.list();
    }

    @Override
    public Designation load(Long id) {
        return (Designation) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.security.Designation where id = :id").setParameter("id", id).uniqueResult();
    }

    @Override
    public void merge(Designation designation) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(designation);
    }
}
