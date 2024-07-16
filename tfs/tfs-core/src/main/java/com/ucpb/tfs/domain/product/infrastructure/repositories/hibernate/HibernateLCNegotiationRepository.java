package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.product.LCNegotiation;
import com.ucpb.tfs.domain.product.LCNegotiationRepository;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: IPCVal
 * Date: 9/22/12
 */

public class HibernateLCNegotiationRepository implements LCNegotiationRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void persist(LCNegotiation lcNegotiation) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(lcNegotiation);
    }

    @Override
    public void update(LCNegotiation lcNegotiation) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(lcNegotiation);
    }

    @Override
    public void merge(LCNegotiation lcNegotiation) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(lcNegotiation);
    }

    @Override
    public LCNegotiation load(TradeServiceId tradeServiceId) {
        return (LCNegotiation) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.product.LCNegotiation where negotiationNumber in (select negotiationNumber from com.ucpb.tfs.domain.service.TradeService where tradeServiceId = :tradeServiceId)").setParameter("tradeServiceId", tradeServiceId).uniqueResult();
    }
}

