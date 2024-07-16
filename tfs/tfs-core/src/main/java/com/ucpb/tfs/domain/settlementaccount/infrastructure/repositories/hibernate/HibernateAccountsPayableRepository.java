package com.ucpb.tfs.domain.settlementaccount.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.settlementaccount.AccountsPayable;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayableRepository;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class HibernateAccountsPayableRepository implements AccountsPayableRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public AccountsPayable load(SettlementAccountNumber settlementAccountNumber) {
        return (AccountsPayable) this.sessionFactory.getCurrentSession().createQuery(
        "from com.ucpb.tfs.domain.settlementaccount.AccountsPayable where settlementAccountNumber = :settlementAccountNumber").setParameter("settlementAccountNumber", settlementAccountNumber).uniqueResult();
    }

    @Override
    public AccountsPayable load(String id) {
        return (AccountsPayable) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.settlementaccount.AccountsPayable where id = :id").setParameter("id", id).uniqueResult();
    }
    
    @Override
    public AccountsPayable load(SettlementAccountNumber settlementAccountNumber, String id) {
    	return (AccountsPayable) this.sessionFactory.getCurrentSession().createQuery(
    	        "from com.ucpb.tfs.domain.settlementaccount.AccountsPayable where settlementAccountNumber = :settlementAccountNumber and id = :id").setParameter("settlementAccountNumber", settlementAccountNumber).setParameter("id", id).uniqueResult();
    }

    @Override
    public void persist(AccountsPayable ap) {
        this.sessionFactory.getCurrentSession().persist(ap);
    }

    @Override
    public void update(AccountsPayable ap) {
        this.sessionFactory.getCurrentSession().update(ap);
    }

    @Override
    public void merge(AccountsPayable ap) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(ap);
    }

}
