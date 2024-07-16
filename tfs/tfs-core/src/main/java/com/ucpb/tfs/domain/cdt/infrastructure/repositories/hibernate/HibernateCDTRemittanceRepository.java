package com.ucpb.tfs.domain.cdt.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.cdt.CDTRemittance;
import com.ucpb.tfs.domain.cdt.CDTRemittanceRepository;
import com.ucpb.tfs.domain.cdt.enums.PaymentRequestType;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Transactional
public class HibernateCDTRemittanceRepository implements CDTRemittanceRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(CDTRemittance cdtRemittance) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(cdtRemittance);
    }

    @Override
    public void merge(CDTRemittance cdtRemittance) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(cdtRemittance);
    }

    @Override
    public void update(CDTRemittance cdtRemittance) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(cdtRemittance);
    }

//    @Override
//    public List<CDTRemittance> getAllBy(String reportType,
//                                        Date remittanceDateFrom,
//                                        Date remittanceDateTo,
//                                        Date collectionFrom,
//                                        Date collectionTo) {
//        System.out.println("CDTRemittance getAllBy");
//        System.out.println("reportType:"+reportType);
//        System.out.println("remittanceDateFrom:"+remittanceDateFrom);
//        System.out.println("remittanceDateTo:"+remittanceDateTo);
//        System.out.println("collectionFrom:"+collectionFrom);
//        System.out.println("collectionTo:"+collectionTo);
//
//        Session session = this.mySessionFactory.getCurrentSession();
//
//        Criteria criteria = session.createCriteria(CDTRemittance.class);
//
//        if (reportType != null) {
//            criteria.add(Restrictions.eq("paymentRequestType", PaymentRequestType.valueOf(reportType)));
//        }
//
//        if (remittanceDateFrom != null && remittanceDateTo != null) {
//            criteria.add(Restrictions.between("remittanceDate", remittanceDateFrom, remittanceDateTo));
//        }
//
//        if (remittanceDateFrom == null && remittanceDateTo != null) {
//            criteria.add(Restrictions.le("remittanceDate", remittanceDateTo));
//        }
//
//        if (remittanceDateFrom != null && remittanceDateTo == null) {
//            criteria.add(Restrictions.ge("remittanceDate", remittanceDateFrom));
//        }
//
//        if (collectionFrom != null) {
//            criteria.add(Restrictions.ge("collectionFrom", collectionFrom));
//        }
//
//
//        if (collectionTo != null) {
//            criteria.add(Restrictions.le("collectionTo", collectionTo));
//        }
//
//
//        return criteria.list();
//    }


    @Override
    public List<CDTRemittance> getAllBy(List<PaymentRequestType> reportTypeList,
                                        Date remittanceDateFrom,
                                        Date remittanceDateTo,
                                        Date collectionFrom,
                                        Date collectionTo) {
        System.out.println("CDTRemittance getAllBy");
        System.out.println("reportType:"+reportTypeList);
        System.out.println("remittanceDateFrom:"+remittanceDateFrom);
        System.out.println("remittanceDateTo:"+remittanceDateTo);
        System.out.println("collectionFrom:"+collectionFrom);
        System.out.println("collectionTo:"+collectionTo);

        Session session = this.mySessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(CDTRemittance.class);

        if (reportTypeList != null) {
            criteria.add(Restrictions.in("paymentRequestType",reportTypeList));
//            criteria.add(Restrictions.eq("paymentRequestType", PaymentRequestType.valueOf(reportType)));
        }

        if (remittanceDateFrom != null && remittanceDateTo != null) {
            criteria.add(Restrictions.between("remittanceDate", remittanceDateFrom, remittanceDateTo));
        }

        if (remittanceDateFrom == null && remittanceDateTo != null) {
            criteria.add(Restrictions.le("remittanceDate", remittanceDateTo));
        }

        if (remittanceDateFrom != null && remittanceDateTo == null) {
            criteria.add(Restrictions.ge("remittanceDate", remittanceDateFrom));
        }

        if (collectionFrom != null) {
            criteria.add(Restrictions.ge("collectionFrom", collectionFrom));
        }


        if (collectionTo != null) {
            criteria.add(Restrictions.le("collectionTo", collectionTo));
        }


        return criteria.list();
    }

}
