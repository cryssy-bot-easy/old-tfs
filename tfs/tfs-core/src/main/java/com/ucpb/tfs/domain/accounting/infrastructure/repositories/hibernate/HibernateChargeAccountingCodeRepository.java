package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.AccountingEntry;
import com.ucpb.tfs.domain.accounting.ChargeAccountingCode;
import com.ucpb.tfs.domain.accounting.ChargeAccountingCodeRepository;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.ProductReference;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
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
public class HibernateChargeAccountingCodeRepository implements ChargeAccountingCodeRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    public void save(ChargeAccountingCode chargeAccountingCode) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(chargeAccountingCode);
    }

    @Override
    public ChargeAccountingCode getChargeAccountingCode(ProductId productId, ServiceType serviceType, ChargeId chargeId) {
        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(ChargeAccountingCode.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));
        c.add(Restrictions.eq("chargeId", chargeId));
        c.setMaxResults(1);



        List<ChargeAccountingCode> results = c.list();

        System.out.println("getChargeAccountingCode results:"+results);
        if(results.size() == 1) {
            return results.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<String> getChargeAccountingCodeList() {
        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(ChargeAccountingCode.class);
        c.setProjection(Projections.distinct(Projections.projectionList()
                .add(Projections.property("accountingCode"), "accountingCode")));
        List<String> results = c.list();
        System.out.println("size result:" + results.size());


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

        return ((Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.accounting.ChargeAccountingCode").iterate().next()).longValue();

    }

    @Override
    public void clear() {

        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.accounting.ChargeAccountingCode cac");
        int tempint = qry.executeUpdate();
        System.out.println("cleared ChargeAccountingCode:" + tempint);

    }
}
