package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.*;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: giancarlo
 * Date: 10/9/12
 * Time: 4:45 PM
 */
public class HibernateAccountingDefaultAccountingCodeRepository implements AccountingDefaultAccountingCodeRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    public List<AccountingDefaultAccountingCode> getAccountingCodeDefaults(ProductId productId, ServiceType serviceType, AccountingEventId accountingEventId, AccountingEventTransactionId accountingEventTransactionId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingDefaultAccountingCode.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));
        c.add(Restrictions.eq("accountingEventId", accountingEventId));
        c.add(Restrictions.eq("accountingEventTransactionId", accountingEventTransactionId));

        List<AccountingDefaultAccountingCode> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }

    }

    @Override
    public List<AccountingDefaultAccountingCode> getAccountingCodeDefaults(){

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingDefaultAccountingCode.class);
        c.add(Restrictions.eq("productId", null));
        c.add(Restrictions.eq("serviceType", null));
        c.add(Restrictions.eq("accountingEventId", null));
        c.add(Restrictions.eq("accountingEventTransactionId", null));

        List<AccountingDefaultAccountingCode> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }
    }

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.accounting.AccountingDefaultAccountingCode").iterate().next() ).longValue();

    }

    @Override
    public void clear() {

        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.accounting.AccountingDefaultAccountingCode adac");
        int tempint = qry.executeUpdate();
        System.out.println("cleared AccountingDefaultAccountingCode :"+tempint);


    }
}
