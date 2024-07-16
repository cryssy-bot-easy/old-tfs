package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CasaTransactionLog;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 */
public class HibernateCasaTransactionLogRepository implements CasaTransactionLogRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void save(CasaTransactionLog transaction) {
        sessionFactory.getCurrentSession().persist(transaction);
    }
}
