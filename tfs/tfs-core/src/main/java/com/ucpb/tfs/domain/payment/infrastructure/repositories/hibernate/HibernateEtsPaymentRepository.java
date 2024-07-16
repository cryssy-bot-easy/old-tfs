package com.ucpb.tfs.domain.payment.infrastructure.repositories.hibernate;

import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ucpb.tfs.domain.payment.EtsPayment;
import com.ucpb.tfs.domain.payment.EtsPaymentRepository;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;

@Transactional
public class HibernateEtsPaymentRepository implements EtsPaymentRepository {
	
	@Autowired(required=true)
    private SessionFactory mySessionFactory;
	
	@Override
    public EtsPayment get(TradeServiceId tradeServiceId, ChargeType chargeType) {
        return (EtsPayment) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.payment.EtsPayment where tradeServiceId = :tradeServiceId and chargeType = :chargeType").setParameter("tradeServiceId", tradeServiceId).setParameter("chargeType", chargeType).uniqueResult();
    }
	
    public void persist(EtsPayment etsPayment) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(etsPayment);
    }
    
    @Override
    public void delete(EtsPayment etsPayment) {
        Session session = this.mySessionFactory.getCurrentSession();
        try {
        	session.delete(etsPayment);
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
}
