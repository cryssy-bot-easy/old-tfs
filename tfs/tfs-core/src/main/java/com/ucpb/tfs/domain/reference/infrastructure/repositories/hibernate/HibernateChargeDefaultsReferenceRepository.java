package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.ChargeDefaultsReference;
import com.ucpb.tfs.domain.reference.ChargeDefaultsReferenceRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: angulo
 * Date: 7/8/13
 * Time: 3:26 PM
 */
public class HibernateChargeDefaultsReferenceRepository implements ChargeDefaultsReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public List<ChargeDefaultsReference> getList() {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(ChargeDefaultsReference.class);

        List<ChargeDefaultsReference> results = c.list();

        if(results.size() > 0) {
            //System.out.println("i found something");
            return results;
        }
        else {
            //System.out.println("i found nothing");
            return null;
        }

    }


    @Override
    public ChargeDefaultsReference get(long id) {
        return (ChargeDefaultsReference) this.sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.reference.ChargeDefaultsReference where id = ?").setParameter(0, id).uniqueResult();
    }

    @Override
    public ChargeDefaultsReference get(String matcher) {
        return (ChargeDefaultsReference) this.sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.reference.ChargeDefaultsReference where matcher = ?").setParameter(0, matcher).uniqueResult();
    }

    @Override
    public void persist(ChargeDefaultsReference chargeDefaultsReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(chargeDefaultsReference);
    }

    @Override
    public void merge(ChargeDefaultsReference chargeDefaultsReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(chargeDefaultsReference);
    }

    @Override
    public void update(ChargeDefaultsReference chargeDefaultsReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(chargeDefaultsReference);
    }

    @Override
    public int clear(){
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.reference.ChargeDefaultsReference cdr");
        int tempint = qry.executeUpdate();
        System.out.println("cleared TradeServiceChargeReference:"+tempint);
        return tempint;

    }

    @Override
    public void delete(long id) {
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.reference.ChargeDefaultsReference cdr where id = ?").setParameter(0,id);
        qry.executeUpdate();
    }

}
