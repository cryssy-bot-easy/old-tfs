package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.DailyBalance;
import com.ucpb.tfs.domain.reference.DailyBalanceRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Component
@Repository
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateDailyBalanceRepository implements DailyBalanceRepository {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public void save(DailyBalance balance) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(balance);
    }
}
