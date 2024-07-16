package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CustomerLog;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 */
public class HibernateCustomerLogRepository implements CustomerLogRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;


    @Override
    public void persist(CustomerLog customerLog) {
        customerLog.setLastUpdated(new Date());
        sessionFactory.getCurrentSession().persist(customerLog);
    }

    @Override
    public void delete(TradeServiceId tradeServiceId) {
        sessionFactory.getCurrentSession().createQuery("delete from com.ucpb.tfs.domain.audit.CustomerLog where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).executeUpdate();
    }
}
