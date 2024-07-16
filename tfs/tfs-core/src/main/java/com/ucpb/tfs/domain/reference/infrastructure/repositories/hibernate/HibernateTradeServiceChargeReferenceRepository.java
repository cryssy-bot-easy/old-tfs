package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.ProductReference;
import com.ucpb.tfs.domain.reference.TradeServiceChargeReference;
import com.ucpb.tfs.domain.reference.TradeServiceChargeReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Jett
 * Date: 8/23/12
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateTradeServiceChargeReferenceRepository implements TradeServiceChargeReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void save(TradeServiceChargeReference tradeServiceCharge) {

        Session session = this.sessionFactory.getCurrentSession();
        session.persist(tradeServiceCharge);

    }

    @Override
    public List<TradeServiceChargeReference> getCharges(ProductId productId, ServiceType serviceType) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(TradeServiceChargeReference.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));
//        c.setMaxResults(1);

        List<TradeServiceChargeReference> results = c.list();

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
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.reference.TradeServiceChargeReference").iterate().next() ).longValue();

    }
    @Override
    public void clear(){

        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.reference.TradeServiceChargeReference tsc");
        int tempint = qry.executeUpdate();
        System.out.println("cleared TradeServiceChargeReference:"+tempint);

    }

}
