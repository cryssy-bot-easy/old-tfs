package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.AccountingEntry;
import com.ucpb.tfs.domain.accounting.ProductServiceAccountingEventTransactionReference;
import com.ucpb.tfs.domain.accounting.ProductServiceAccountingEventTransactionReferenceRepository;
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
 * Date: 11/23/12
 * Time: 11:23 PM
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateProductServiceAccountingEventTransactionReferenceRepository implements ProductServiceAccountingEventTransactionReferenceRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    public void save(ProductServiceAccountingEventTransactionReference productServiceAccountingEventTransactionReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(productServiceAccountingEventTransactionReference);
    }

    @Override
    public List<ProductServiceAccountingEventTransactionReference> getProductServiceAccountingEventTransactionReference(ProductId productId, ServiceType serviceType){

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(ProductServiceAccountingEventTransactionReference.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));

        List<ProductServiceAccountingEventTransactionReference> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }
    }

    @Override
    public List<ProductServiceAccountingEventTransactionReference> getProductServiceAccountingEventTransactionReference(){

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(ProductServiceAccountingEventTransactionReference.class);

        List<ProductServiceAccountingEventTransactionReference> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }
    }

    @Override
    public Long getCount(){

        Session session = this.sessionFactory.getCurrentSession();

        return ((Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.accounting.ProductServiceAccountingEventTransactionReference").iterate().next()).longValue();
    }

    @Override
    public void clear(){

        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.accounting.ProductServiceAccountingEventTransactionReference psaetr");
        int tempint = qry.executeUpdate();
        System.out.println("cleared ProductServiceAccountingEventTransactionReference:"+tempint);

    }
}
