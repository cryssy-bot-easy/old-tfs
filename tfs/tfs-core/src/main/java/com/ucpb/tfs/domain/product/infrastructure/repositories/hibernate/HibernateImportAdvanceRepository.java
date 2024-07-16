package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.ImportAdvancePayment;
import com.ucpb.tfs.domain.product.ImportAdvanceRepository;
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
public class HibernateImportAdvanceRepository implements ImportAdvanceRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(ImportAdvancePayment importAdvancePayment) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(importAdvancePayment);
    }

    @Override
    public void merge(ImportAdvancePayment importAdvancePayment) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(importAdvancePayment);
    }

    @Override
    public void update(ImportAdvancePayment importAdvancePayment) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(importAdvancePayment);
    }

    @Override
    public ImportAdvancePayment  load(DocumentNumber documentNumber) {
        return (ImportAdvancePayment) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.product.ImportAdvancePayment where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
    }

    @Override
    public List<ImportAdvancePayment> getAllImportAdvancePayments(String cifName,
                                                                  DocumentNumber documentNumber,
                                                                  Currency currency,
                                                                  BigDecimal amountFrom,
                                                                  BigDecimal amountTo,
                                                                  String unitcode,
                                                                  String unitCode) {

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ImportAdvancePayment.class);

        Query query = session.createQuery("from com.ucpb.tfs.domain.product.ImportAdvancePayment");

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

        List importAdvancePaymentList = crit.list();

        return importAdvancePaymentList;
    }
}
