package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.product.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Transactional
public class HibernateExportAdvanceRepository implements ExportAdvanceRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(ExportAdvancePayment exportAdvancePayment) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(exportAdvancePayment);
    }

    @Override
    public void merge(ExportAdvancePayment exportAdvancePayment) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(exportAdvancePayment);
    }

    @Override
    public void update(ExportAdvancePayment exportAdvancePayment) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(exportAdvancePayment);
    }

    @Override
    public ExportAdvancePayment  load(DocumentNumber documentNumber) {
        return (ExportAdvancePayment) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.product.ExportAdvancePayment where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
    }

    @Override
    public List<ExportAdvancePayment> getAllExportAdvancePayments(String cifName,
                                                                  DocumentNumber documentNumber,
                                                                  Currency currency,
                                                                  BigDecimal amountFrom,
                                                                  BigDecimal amountTo,
                                                                  String unitCode,
                                                                  String unitcode) {

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ExportAdvancePayment.class);

//        Query query = session.createQuery("from com.ucpb.tfs.domain.product.ExportAdvancePayment as ep inner join TradeService as ts on ep.documentNumber=ts.tradeProductNumber order by ts.modifiedDate desc");

        // add criteria if parameter was specified
        if(cifName != null) {
            crit.add(Restrictions.like("cifName", "%" + cifName + "%"));
        }

        if(documentNumber != null) {
            crit.add(Restrictions.eq("documentNumber", documentNumber));
        }

        if(currency != null) {
            crit.add(Restrictions.eq("currency", currency));
        }

        if(amountFrom != null && amountTo != null) {
            crit.add(Restrictions.between("amount", amountFrom, amountTo));
        } else if (amountFrom != null && amountTo == null) {
            crit.add(Restrictions.ge("amount", amountFrom));
        } else if (amountFrom == null && amountTo != null) {
            crit.add(Restrictions.le("amount", amountTo));
        }
        
        if(unitCode != null) {
        	crit.add(Restrictions.eq("ccbdBranchUnitCode", unitCode));
        }else if (unitcode != null && !unitcode.equals("909")) {
        	crit.add(Restrictions.eq("ccbdBranchUnitCode", unitcode));
        }

        List exportAdvancePaymentList = crit.list();

        return exportAdvancePaymentList;
    }
}
