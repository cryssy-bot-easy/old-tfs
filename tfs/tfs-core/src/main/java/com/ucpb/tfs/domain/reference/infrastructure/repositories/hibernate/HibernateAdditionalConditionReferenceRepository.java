package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.condition.ConditionCode;
import com.ucpb.tfs.domain.reference.AdditionalConditionReference;
import com.ucpb.tfs.domain.reference.AdditionalConditionReferenceRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Marv
 * Date: 11/4/12
 */

public class HibernateAdditionalConditionReferenceRepository implements AdditionalConditionReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void save(AdditionalConditionReference conditionReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(conditionReference);
    }

    @Override
    public AdditionalConditionReference load(ConditionCode conditionCode) {
        return (AdditionalConditionReference) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.AdditionalConditionReference where conditionCode = :conditionCode").setParameter("conditionCode", conditionCode).uniqueResult();
    }

    @Override
    public List<AdditionalConditionReference> getAllConditionReference() {
        return (List<AdditionalConditionReference>)this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.AdditionalConditionReference");
    }

    @Override
    public void clear() {
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("delete from com.ucpb.tfs.domain.reference.AdditionalConditionReference acr");
        qry.executeUpdate();
    }

}
