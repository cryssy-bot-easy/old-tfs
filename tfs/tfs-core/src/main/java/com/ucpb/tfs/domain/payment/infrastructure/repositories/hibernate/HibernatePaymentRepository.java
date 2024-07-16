package com.ucpb.tfs.domain.payment.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: IPCVal
 * Date: 8/24/12
 */

/** 
   SCR/ER Number: 20170725-096  Redmine 6497
   SCR/ER Description: Credit to CASA as payment for Domestic regular sight LC negotiation was effected but status on TFS remain unpaid.
   Revised by: Jesse James Joson
   Date Deployed: 
   Program Revision Details: Show print lines of ID and status whenever there would be update/save on Payment and PaymentDetail tables.
   PROJECT: CORE
   MEMBER TYPE  : Java
   Project Name: HibernatePaymentRepository.java
 */

@Transactional
public class HibernatePaymentRepository implements PaymentRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public Payment get(TradeServiceId tradeServiceId, ChargeType chargeType) {
        return (Payment) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.payment.Payment where tradeServiceId = :tradeServiceId and chargeType = :chargeType").setParameter("tradeServiceId", tradeServiceId).setParameter("chargeType", chargeType).uniqueResult();
    }

    @Override
    public List<Payment> getAllPayments(TradeServiceId tradeServiceId) {
        return this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.payment.Payment where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).list();
    }


    @Override
    public PaymentDetail getPaymentDetail(Long id) {
        return (PaymentDetail) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.payment.PaymentDetail where id = :id").setParameter("id", id).uniqueResult();
    }

    @Override
    public Payment getPaymentByPaymentDetailId(Long paymentDetailId) {
        Session session = this.mySessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Payment.class);
        criteria.createAlias("details","paymentDetail");
        criteria.add(Restrictions.eq("paymentDetail.id", paymentDetailId));

        return (Payment) criteria.uniqueResult();
    }

    @Override
    public void saveOrUpdate(Payment payment) {
    	System.out.println("HibernatePaymentRepository.saveOrUpdate(Payment payment) >>> Payment.ID = " + payment.getId() + "    Status = " + payment.getStatus());
    	for (PaymentDetail detail : payment.getDetails()) {
    		System.out.println("HibernatePaymentRepository.saveOrUpdate(Payment payment) >>> PaymentDetail.ID = " + detail.getId() + "    Status = " + detail.getStatus());
    	}    	
        Session session = this.mySessionFactory.getCurrentSession();
        session.saveOrUpdate(payment);
    }

    @Override
    public void saveOrUpdate(PaymentDetail paymentDetail) {
    	System.out.println("HibernatePaymentRepository.saveOrUpdate(PaymentDetail paymentDetail) >>> PaymentDetail.ID = " + paymentDetail.getId() + "    Status = " + paymentDetail.getStatus());
        Session session = this.mySessionFactory.getCurrentSession();
        session.saveOrUpdate(paymentDetail);
    }

    @Override
    public void delete(Payment payment) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.delete(payment);
    }

    // for UA Loan Maturity Adjustment : loads Payment
    @Override
    public Payment load(TradeServiceId tradeServiceId) {
        return (Payment) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.payment.Payment where chargeType = 'PRODUCT' and tradeServiceId = :tradeServiceId)").setParameter("tradeServiceId", tradeServiceId).uniqueResult();
    }

    // added this so we get all payment types for a given TradeServiceId
    @Override
    public List loadAllPayment(TradeServiceId tradeServiceId) {
//        return this.mySessionFactory.getCurrentSession().createQuery(
//                "from com.ucpb.tfs.domain.payment.Payment where tradeServiceId = :tradeServiceId)").setParameter("tradeServiceId", tradeServiceId).list();

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(Payment.class);

        Query query = session.createQuery("from com.ucpb.tfs.domain.payment.Payment");
        crit.add(Restrictions.eq("tradeServiceId", tradeServiceId));
        crit.setFetchMode("details", FetchMode.JOIN);

        return crit.list();
    }


    @Override
    public List<Payment> getPaymentBy(TradeServiceId tradeServiceId) {

        List<Payment> payments = loadAllPayment(tradeServiceId);

        // eagerly load all references
        //Hibernate.initialize(payments);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(payments);
        List returnClassx = gson.fromJson(result, List.class);

        System.out.println(returnClassx);

//        return returnClassx;

        return payments;
    }

    @Override
    public void merge(Payment payment) {
    	System.out.println("HibernatePaymentRepository.saveOrUpdate(Payment payment) >>> Payment.ID = " + payment.getId() + "    Status = " + payment.getStatus());
    	for (PaymentDetail detail : payment.getDetails()) {
    		System.out.println("HibernatePaymentRepository.saveOrUpdate(Payment payment) >>> PaymentDetail.ID = " + detail.getId() + "    Status = " + detail.getStatus());
    	}  
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(payment);
    }

    // loads ua loan payment
    @Override
    public PaymentDetail getUaLoanPayment(String referenceNumber) {
        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(PaymentDetail.class);

        crit.add(Restrictions.eq("referenceNumber", referenceNumber));
        crit.add(Restrictions.eq("paymentInstrumentType", PaymentInstrumentType.UA_LOAN));

        return (PaymentDetail) crit.uniqueResult();
    }

    @Override
    public List<ChargeType> getAllPaymentChargeTypesPerTradeService(TradeServiceId tradeServiceId) {
        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(Payment.class);

        crit.add(Restrictions.eq("tradeServiceId", tradeServiceId));

        List<Payment> paymentList = crit.list();

        List<ChargeType> chargeTypeList = new ArrayList<ChargeType>();

        for (Payment payment : paymentList) {
            chargeTypeList.add(payment.getChargeType());
        }

        return chargeTypeList;
    }

    @Override
    public Boolean checkIfHasPaidPayment(TradeServiceId tradeServiceId) {
        List<Payment> paymentList = getPaymentBy(tradeServiceId);

        for (Payment payment : paymentList) {
            for (PaymentDetail paymentDetail : payment.getDetails()) {
                if (paymentDetail.getStatus().equals(PaymentStatus.PAID)) {
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }

    @Override
    public Map<String, Object> getServiceChargeRates(TradeServiceId tradeServiceId) {
        Payment payment = get(tradeServiceId, ChargeType.SERVICE);

        Map<String, Object> rateMap = new HashMap<String, Object>();

        for (PaymentDetail paymentDetail : payment.getDetails()) {
            if (!rateMap.containsKey("settlementCurrency")) {
                rateMap.put("settlementCurrency", paymentDetail.getCurrency());
            }

            // third to usd
            if (!rateMap.containsKey("passOnRateThirdToUsd")) {
                rateMap.put("passOnRateThirdToUsd", paymentDetail.getPassOnRateThirdToUsd());
            }

            if (!rateMap.containsKey("specialRateThirdToUsd")) {
                rateMap.put("specialRateThirdToUsd", paymentDetail.getSpecialRateThirdToUsd());
            }

            if (!rateMap.containsKey("thirdToUsdRateName")) {
                rateMap.put("thirdToUsdRateName", paymentDetail.getThirdToUsdRateName());
            }

            if (!rateMap.containsKey("thirdToUsdRateDescription")) {
                rateMap.put("thirdToUsdRateDescription", paymentDetail.getThirdToUsdRateDescription());
            }

            // third to php
            if (!rateMap.containsKey("passOnRateThirdToPhp")) {
                rateMap.put("passOnRateThirdToPhp", paymentDetail.getPassOnRateThirdToPhp());
            }

            if (!rateMap.containsKey("specialRateThirdToPhp")) {
                rateMap.put("specialRateThirdToPhp", paymentDetail.getSpecialRateThirdToPhp());
            }

            if (!rateMap.containsKey("thirdToPhpRateName")) {
                rateMap.put("thirdToPhpRateName", paymentDetail.getThirdToPhpRateName());
            }

            if (!rateMap.containsKey("thirdToPhpRateDescription")) {
                rateMap.put("thirdToPhpRateDescription", paymentDetail.getThirdToPhpRateDescription());
            }

            // usd to php
            if (!rateMap.containsKey("passOnRateUsdToPhp")) {
                rateMap.put("passOnRateUsdToPhp", paymentDetail.getPassOnRateUsdToPhp());
            }

            if (!rateMap.containsKey("specialRateUsdToPhp")) {
                rateMap.put("specialRateUsdToPhp", paymentDetail.getSpecialRateUsdToPhp());
            }

            if (!rateMap.containsKey("usdToPhpRateName")) {
                rateMap.put("usdToPhpRateName", paymentDetail.getUsdToPhpRateName());
            }

            if (!rateMap.containsKey("usdToPhpRateDescription")) {
                rateMap.put("usdToPhpRateDescription", paymentDetail.getUsdToPhpRateDescription());
            }

            // urr
            if (!rateMap.containsKey("urr")) {
                rateMap.put("urr", paymentDetail.getUrr());
            }

            if (!rateMap.containsKey("urrRateName")) {
                rateMap.put("urrRateName", paymentDetail.getUrrRateName());
            }

            if (!rateMap.containsKey("urrRateDescription")) {
                rateMap.put("urrRateDescription", paymentDetail.getUrrRateDescription());
            }
        }

        return rateMap;
    }

    @Override
    public Boolean checkIfHasUnpaidPayment(TradeServiceId tradeServiceId) {
        List<Payment> paymentList = getPaymentBy(tradeServiceId);

        for (Payment payment : paymentList) {
            for (PaymentDetail paymentDetail : payment.getDetails()) {
                if (paymentDetail.getStatus().equals(PaymentStatus.UNPAID)) {
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }

    public Set<PaymentDetail> getSavedProductPayments(TradeServiceId tradeServiceId) {
        Payment payment = get(tradeServiceId, ChargeType.PRODUCT);

        return payment.getDetails();
    }

	@Override
	public Map<String, Object> getCDTPaymentDetails(String iedieirdNo) {
		Session session  = this.mySessionFactory.getCurrentSession();

        Map<String, Object> cdtPaymentDetails = new HashMap<String, Object>();

        StringBuilder queryStatement = new StringBuilder();
        
        queryStatement.append("select p.PAIDDATE, cpr.CLIENT_NAME,  pd.REFERENCENUMBER, pd.amount,  pd.currency,  cpr.BANKCHARGE, cpr.IEDIEIRDNO from paymentdetail pd ");
        queryStatement.append("left join payment p on p.id = pd.paymentid ");
        queryStatement.append("left join tradeservice ts  on ts.tradeserviceid = p.tradeserviceid ");
        queryStatement.append("left join CDTPAYMENTREQUEST cpr on cpr.IEDIEIRDNO = ts.TRADESERVICEREFERENCENUMBER ");
        queryStatement.append("where cpr.IEDIEIRDNO = '" + iedieirdNo + "'");
        
        Query query = session.createSQLQuery(queryStatement.toString());
        
        Iterator it = query.list().iterator();
        
    	while (it.hasNext()) {
            Object[] obj = (Object[]) it.next();
            cdtPaymentDetails.put("paidDate", obj[0]);
            cdtPaymentDetails.put("clientName", obj[1]);
            cdtPaymentDetails.put("accountNumber", obj[2]);
            cdtPaymentDetails.put("amount", obj[3]);
            cdtPaymentDetails.put("currency", obj[4]);
            cdtPaymentDetails.put("bankCharge", obj[5]);
            cdtPaymentDetails.put("iedieirdNo", obj[6]);
        }
        
        return cdtPaymentDetails;
	}
}
