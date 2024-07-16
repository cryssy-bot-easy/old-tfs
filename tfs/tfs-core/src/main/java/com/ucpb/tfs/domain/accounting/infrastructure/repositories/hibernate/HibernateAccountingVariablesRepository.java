package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.AccountingVariable;
import com.ucpb.tfs.domain.accounting.AccountingVariablesRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: giancarlo
 * Date: 10/5/12
 * Time: 5:54 PM
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateAccountingVariablesRepository implements AccountingVariablesRepository {


    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public List<AccountingVariable> list() {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingVariable.class);

        List<AccountingVariable> results = c.list();

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
    public AccountingVariable get(long id) {
        return (AccountingVariable) this.sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.accounting.AccountingVariable where id = ?").setParameter(0, id).uniqueResult();
    }

    @Override
    public void persist(AccountingVariable accountingVariable) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(accountingVariable);
    }

    @Override
    public void merge(AccountingVariable accountingVariable) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(accountingVariable);
    }

    @Override
    public void update(AccountingVariable accountingVariable) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(accountingVariable);
    }

    @Override
    public int clear(){
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.accounting.AccountingVariable av");
        int tempint = qry.executeUpdate();
        System.out.println("cleared TradeServiceChargeReference:"+tempint);
        return tempint;

    }

    @Override
    public void delete(long id) {
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("com.ucpb.tfs.domain.accounting.AccountingVariables av where id = ?").setParameter(0, id);
        qry.executeUpdate();

    }
}
