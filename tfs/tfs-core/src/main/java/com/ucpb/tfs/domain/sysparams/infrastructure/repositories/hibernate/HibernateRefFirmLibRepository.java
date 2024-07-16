package com.ucpb.tfs.domain.sysparams.infrastructure.repositories.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ucpb.tfs.domain.sysparams.RefFirmLib;
import com.ucpb.tfs.domain.sysparams.RefFirmLibRepository;

@Transactional
public class HibernateRefFirmLibRepository implements RefFirmLibRepository{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void saveOrUpdate(RefFirmLib refFirmLib) {
		sessionFactory.getCurrentSession().saveOrUpdate(refFirmLib);
	}
	
	@Override
	public void delete(String firmCode) {
		RefFirmLib refFirmLib = (RefFirmLib) sessionFactory.getCurrentSession().load(RefFirmLib.class, firmCode);
	    sessionFactory.getCurrentSession().delete(refFirmLib);
	}

	
	@Override
	public RefFirmLib getRefFirmLib(String firmCode) {
		Session session = this.sessionFactory.getCurrentSession();
		
		try {
			Criteria crit = session.createCriteria(RefFirmLib.class);
			if(firmCode != null && !firmCode.trim().isEmpty()) {
				crit.add(Restrictions.eq("firmCode", firmCode).ignoreCase());
			}
			
			RefFirmLib refFirmLib = (RefFirmLib) crit.uniqueResult();
			
			return refFirmLib;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<RefFirmLib> getRequestsMatching(String firmCode, String firmDescription) {
		
		Session session = this.sessionFactory.getCurrentSession();
		
		try {
			Criteria crit = session.createCriteria(RefFirmLib.class).addOrder(Order.asc("firmCode"));
			if(firmCode != null && !firmCode.trim().isEmpty()) {
				crit.add(Restrictions.ilike("firmCode", firmCode, MatchMode.ANYWHERE));
			}
			if(firmDescription != null && !firmDescription.trim().isEmpty()) {
				crit.add(Restrictions.ilike("firmDescription", firmDescription, MatchMode.ANYWHERE));
			}
			
			List<RefFirmLib> refFirmLib = (List<RefFirmLib>) crit.list();
			
			return refFirmLib;			
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
