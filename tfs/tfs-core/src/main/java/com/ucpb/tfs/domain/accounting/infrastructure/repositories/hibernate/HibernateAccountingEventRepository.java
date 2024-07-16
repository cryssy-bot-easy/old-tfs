package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.AccountingEntry;
import com.ucpb.tfs.domain.accounting.AccountingEvent;
import com.ucpb.tfs.domain.accounting.AccountingEventRepository;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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
public class HibernateAccountingEventRepository implements AccountingEventRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    public void save(AccountingEvent accountingEvent) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(accountingEvent);
    }

    @Override
    public List<AccountingEvent> getEvents(ProductId productId, ServiceType serviceType) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));

        List<AccountingEvent> results = c.list();

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

        return ((Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.accounting.AccountingEvent").iterate().next()).longValue();

    }

    @Override
    public void clear(){

        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.accounting.AccountingEvent ae");
        int tempint = qry.executeUpdate();
        System.out.println("cleared AccountingEvent:"+tempint);

    }
}
