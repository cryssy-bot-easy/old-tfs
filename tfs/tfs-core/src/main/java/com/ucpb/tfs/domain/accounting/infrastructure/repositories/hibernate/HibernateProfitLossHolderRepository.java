package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.ProfitLossHolder;
import com.ucpb.tfs.domain.accounting.ProfitLossHolderRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: giancarlo
 * Date: 10/5/12
 * Time: 5:54 PM
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateProfitLossHolderRepository implements ProfitLossHolderRepository {


    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    public void save(ProfitLossHolder profitLossHolder) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(profitLossHolder);
    }

    @Override
    public void delete(String tradeServiceId) {
        Session session = this.sessionFactory.getCurrentSession();
        Query qry = session.createQuery("DELETE FROM com.ucpb.tfs.domain.accounting.ProfitLossHolder where tradeServiceId = ? ").setParameter(0, tradeServiceId);
        qry.executeUpdate();
    }

    @Override
    public void delete(String tradeServiceId, String paymentId) {
        Session session = this.sessionFactory.getCurrentSession();
        Query qry = session.createQuery("DELETE FROM com.ucpb.tfs.domain.accounting.ProfitLossHolder where tradeServiceId = ? and paymentDetailId = ? ").setParameter(0, tradeServiceId);
        qry.setParameter(1,paymentId);
        qry.executeUpdate();
    }
}
