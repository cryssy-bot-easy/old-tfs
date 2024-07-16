package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.Rebate;
import com.ucpb.tfs.domain.product.RebateRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/20/13
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional
public class HibernateRebateRepository implements RebateRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public Rebate load(String id) {
        return (Rebate) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.product.Rebate where id = :id").setParameter("id", id).uniqueResult();
    }
//    public Rebate load(DocumentNumber documentNumber) {
//        return (Rebate) mySessionFactory.getCurrentSession().
//                createQuery("from com.ucpb.tfs.domain.product.Rebate where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
//    }

    @Override
    public List<Rebate> getAllRebateBy(String corresBankCode, String unitCode) {

        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(Rebate.class);

        if(corresBankCode != null) {
            crit.add(Restrictions.eq("corresBankCode", corresBankCode));
        }
        if(unitCode != null) {
        	crit.add(Restrictions.eq("ccbdBranchUnitCode", unitCode));
        }
        
        crit.addOrder(Order.desc("processDate"));

        return crit.list();
    }

    @Override
    public void persist(Rebate rebate) {
        Session session = this.mySessionFactory.getCurrentSession();

        session.persist(rebate);
    }
}
