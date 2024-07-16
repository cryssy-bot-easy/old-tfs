package com.ucpb.tfs.domain.sysparams.infrastructure.repositories.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ucpb.tfs.domain.sysparams.CutOff;
import com.ucpb.tfs.domain.sysparams.CutOffRepository;

@Transactional
public class HibernateCutOffRepository implements CutOffRepository {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public CutOff getCutOffTime() {
		
		Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CutOff.class);
                
		return (CutOff) crit.uniqueResult();
	}

	@Override
	public void save(CutOff cutOff) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().merge(cutOff);
	}
}
