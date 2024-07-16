package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.product.LCNegotiationDiscrepancy;
import com.ucpb.tfs.domain.product.LCNegotiationDiscrepancyRepository;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: IPCVal
 * Date: 10/23/12
 */
public class HibernateLCNegotiationDiscrepancyRepository implements LCNegotiationDiscrepancyRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void persist(LCNegotiationDiscrepancy lcNegotiationDiscrepancy) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(lcNegotiationDiscrepancy);
    }

    @Override
    public void update(LCNegotiationDiscrepancy lcNegotiationDiscrepancy) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(lcNegotiationDiscrepancy);
    }

    @Override
    public void merge(LCNegotiationDiscrepancy lcNegotiationDiscrepancy) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(lcNegotiationDiscrepancy);
    }

    @Override
    public LCNegotiationDiscrepancy load(TradeServiceId tradeServiceId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
