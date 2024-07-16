package com.ucpb.tfs.domain.condition.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.condition.AdditionalCondition;
import com.ucpb.tfs.domain.condition.AdditionalConditionRepository;
import com.ucpb.tfs.domain.condition.ConditionCode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Marv
 * Date: 11/4/12
 */

public class HibernateAdditionalConditionRepository implements AdditionalConditionRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(AdditionalCondition additionalCondition) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(additionalCondition);
    }

    @Override
    public void merge(AdditionalCondition additionalCondition) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(additionalCondition);
    }

    @Override
    public void update(AdditionalCondition additionalCondition) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(additionalCondition);
    }

    @Override
    @Transactional
    public AdditionalCondition load(ConditionCode conditionCode) {
        return (AdditionalCondition) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.condition.AdditionalCondition where conditionCode = ?").setParameter(0, conditionCode).uniqueResult();
    }

}
