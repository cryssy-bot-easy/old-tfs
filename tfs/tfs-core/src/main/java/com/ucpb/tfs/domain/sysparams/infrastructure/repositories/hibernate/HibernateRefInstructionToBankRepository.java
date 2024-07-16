package com.ucpb.tfs.domain.sysparams.infrastructure.repositories.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.sysparams.RefInstructionToBank;
import com.ucpb.tfs.domain.sysparams.RefInstructionToBankRepository;

@Transactional
public class HibernateRefInstructionToBankRepository implements RefInstructionToBankRepository{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void saveOrUpdate(RefInstructionToBank refInstructionToBank) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(refInstructionToBank);
	}

	@Override
	public RefInstructionToBank getRefInstructionToBank(
			String instructionToBankCode) {
		Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(RefInstructionToBank.class);
        crit.add(Restrictions.eq("instructionToBankCode", instructionToBankCode).ignoreCase());

        RefInstructionToBank refInstructionToBank = null;
        Object refInstructionToBankObj = crit.uniqueResult();
        
        if (refInstructionToBankObj != null && refInstructionToBankObj instanceof RefInstructionToBank) {
        	refInstructionToBank = (RefInstructionToBank)refInstructionToBankObj;
        }

        return refInstructionToBank;
	}

	@SuppressWarnings("unchecked")
    private List<RefInstructionToBank> findRefInstructionToBank(Criteria criteria) {
        
		List<RefInstructionToBank> refInstructionToBank = criteria.list();

        Hibernate.initialize(refInstructionToBank);

        Gson gson = new Gson();

        String result = gson.toJson(refInstructionToBank);

		List<RefInstructionToBank> returnMap = gson.fromJson(result, List.class);

        return returnMap;
    }
	
	@Override
    public List<RefInstructionToBank> getAllRefInstructionToBank() {
		Session session  = this.sessionFactory.getCurrentSession();
		
        Criteria criteria = session.createCriteria(RefInstructionToBank.class);
        
        criteria.addOrder(Order.asc("id"));

        return findRefInstructionToBank(criteria);

    }
	
	@Override
	public void delete(Long id) {
		RefInstructionToBank refInstructionToBank = (RefInstructionToBank) sessionFactory.getCurrentSession().load(RefInstructionToBank.class,id);
	    sessionFactory.getCurrentSession().delete(refInstructionToBank);
	}

}
