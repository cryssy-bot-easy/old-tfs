package com.ucpb.tfs.domain.service.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.Charge;
import com.ucpb.tfs.domain.reference.ChargeRepository;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Giancarlo Angulo
 * Date: 1/7/14
 * Time: 5:39 PM
 */
@Transactional
public class HibernateEtsServiceChargeRepository implements EtsServiceChargeRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public List<EtsServiceCharge> getEtsServiceChargeList(TradeServiceId tradeServiceId) {
        return this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.EtsServiceCharge where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).list();
    }


    public void persist(EtsServiceCharge etsServiceCharge) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(etsServiceCharge);
    }


    @Override
    public void delete(TradeServiceId tradeServiceId) {
        Session session = this.mySessionFactory.getCurrentSession();
        Query qry = session.createQuery("DELETE FROM  com.ucpb.tfs.domain.service.EtsServiceCharge where tradeServiceId = :tradeServiceId ").setParameter("tradeServiceId", tradeServiceId);

        qry.executeUpdate();
    }


}
