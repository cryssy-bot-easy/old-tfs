package com.ucpb.tfs.domain.settlementaccount.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.settlementaccount.SettlementAccount;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * User: IPCVal
 * Date: 8/31/12
 */
@Component
@Repository
public class HibernateSettlementAccountRepository implements SettlementAccountRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public SettlementAccount load(SettlementAccountNumber settlementAccountNumber) {
        return (SettlementAccount) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.settlementaccount.SettlementAccount where settlementAccountNumber = :settlementAccountNumber").setParameter("settlementAccountNumber", settlementAccountNumber).uniqueResult();
    }

    @Override
    public void persist(SettlementAccount settlementAccount) {
        this.sessionFactory.getCurrentSession().persist(settlementAccount);
    }

    @Override
    public void update(SettlementAccount settlementAccount) {
        this.sessionFactory.getCurrentSession().update(settlementAccount);
    }
}
