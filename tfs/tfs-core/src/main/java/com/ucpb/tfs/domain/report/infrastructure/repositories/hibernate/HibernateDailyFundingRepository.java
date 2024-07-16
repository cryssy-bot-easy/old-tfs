package com.ucpb.tfs.domain.report.infrastructure.repositories.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ucpb.tfs.domain.report.DailyFunding;
import com.ucpb.tfs.domain.report.DailyFundingRepository;
import com.ucpb.tfs.domain.service.TradeServiceId;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateDailyFundingRepository implements DailyFundingRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;
    
	@Override
	public void persist(DailyFunding dailyFunding) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(dailyFunding);
	}

	@Override
	public List<DailyFunding> getDailyFundingBySettledDate(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DailyFunding getDailyFundingById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DailyFunding getDailyFundingByTradeServiceId(
			TradeServiceId tradeServiceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
