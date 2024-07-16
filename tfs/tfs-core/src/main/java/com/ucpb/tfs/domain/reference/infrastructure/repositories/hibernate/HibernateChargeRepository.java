package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.Charge;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.reference.ChargeRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: JAVA_training
 * Date: 10/8/12
 * Time: 7:40 PM
 */

@Transactional
public class HibernateChargeRepository implements ChargeRepository {


    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public List<Charge> getChargeIdList() {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(Charge.class);

        List<Charge> results = c.list();

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
    public Charge load(ChargeId chargeId) {
        return (Charge) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.Charge where chargeId = :chargeId").setParameter("chargeId", chargeId).uniqueResult();
    }

    @Override
    public Charge getByName(String displayName) {
        return (Charge) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.Charge where displayName = :displayName").setParameter("displayName", displayName).uniqueResult();
    }

}
