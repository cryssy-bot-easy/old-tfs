package com.ucpb.tfs.domain.settlementaccount.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.settlementaccount.AccountsReceivable;
import com.ucpb.tfs.domain.settlementaccount.AccountsReceivableRepository;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Component
@Repository
@Transactional
public class HibernateAccountsReceivableRepository implements AccountsReceivableRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public AccountsReceivable load(SettlementAccountNumber settlementAccountNumber) {
        return (AccountsReceivable) this.sessionFactory.getCurrentSession().createQuery(
        "from com.ucpb.tfs.domain.settlementaccount.AccountsReceivable where settlementAccountNumber = :settlementAccountNumber").setParameter("settlementAccountNumber", settlementAccountNumber).uniqueResult();
    }

    @Override
    public AccountsReceivable load(String id) {
        return (AccountsReceivable) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.settlementaccount.AccountsReceivable where id = :id").setParameter("id", id).uniqueResult();
    }

    @Override
    public void persist(AccountsReceivable ar) {
        this.sessionFactory.getCurrentSession().persist(ar);
    }

    @Override
    public void update(AccountsReceivable ar) {
        this.sessionFactory.getCurrentSession().update(ar);
    }

    @Override
    public void merge(AccountsReceivable ar) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(ar);
    }

}
