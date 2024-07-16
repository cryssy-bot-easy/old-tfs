package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CustomerAccount;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
@Transactional
public class HibernateCustomerAccountLogRepository implements CustomerAccountLogRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void persist(CustomerAccount log) {
        this.sessionFactory.getCurrentSession().persist(log);
    }

    @Override
    public void delete(TradeServiceId tradeServiceId) {
        List<CustomerAccount> customerAccountList = (List<CustomerAccount>)sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.audit.CustomerAccount where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).list();
        if (customerAccountList != null && !customerAccountList.isEmpty()) {
            sessionFactory.getCurrentSession().createQuery("delete from com.ucpb.tfs.domain.audit.CustomerAccount where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).executeUpdate();
        }
    }
	
	@Override
	public void deleteBatchFlag(int flag) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().createQuery("UPDATE com.ucpb.tfs.domain.audit.CustomerAccount set batchFlag = 0 where batchFlag = :batchFlag").setParameter("batchFlag", flag).executeUpdate();
		
	}

}
