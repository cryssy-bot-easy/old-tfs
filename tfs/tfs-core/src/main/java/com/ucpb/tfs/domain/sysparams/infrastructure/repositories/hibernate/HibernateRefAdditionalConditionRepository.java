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
import com.ucpb.tfs.domain.sysparams.RefAdditionalCondition;
import com.ucpb.tfs.domain.sysparams.RefAdditionalConditionRepository;

@Transactional
public class HibernateRefAdditionalConditionRepository implements RefAdditionalConditionRepository{

	@Autowired
	private SessionFactory sessionFactory;
	@Override
	public void saveOrUpdate(RefAdditionalCondition refAdditionalCondition) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(refAdditionalCondition);
	}

	@Override
	public RefAdditionalCondition getRefAdditionalCondition(String conditionCode) {
		// TODO Auto-generated method stub
		Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(RefAdditionalCondition.class);
        crit.add(Restrictions.eq("conditionCode", conditionCode).ignoreCase());

        RefAdditionalCondition refAdditionalCondition = null;
        Object refAdditionalConditionObj = crit.uniqueResult();
        
        if (refAdditionalConditionObj != null && refAdditionalConditionObj instanceof RefAdditionalCondition) {
        	refAdditionalCondition = (RefAdditionalCondition)refAdditionalConditionObj;
        }

        return refAdditionalCondition;
	}

	@SuppressWarnings("unchecked")
    private List<RefAdditionalCondition> findRefRefAdditionalCondition(Criteria criteria) {
        
		List<RefAdditionalCondition> refAdditionalCondition = criteria.list();

        Hibernate.initialize(refAdditionalCondition);

        Gson gson = new Gson();

        String result = gson.toJson(refAdditionalCondition);

		List<RefAdditionalCondition> returnMap = gson.fromJson(result, List.class);

        return returnMap;
    }
	
	@Override
    public List<RefAdditionalCondition> getAllRefAdditionalCondition() {
		Session session  = this.sessionFactory.getCurrentSession();
		
        Criteria criteria = session.createCriteria(RefAdditionalCondition.class);
        
        criteria.addOrder(Order.asc("id"));

        return findRefRefAdditionalCondition(criteria);

    }

	@Override
	public void delete(Long id) {
		RefAdditionalCondition refAdditionalCondition = (RefAdditionalCondition) sessionFactory.getCurrentSession().load(RefAdditionalCondition.class,id);
	    sessionFactory.getCurrentSession().delete(refAdditionalCondition);
	}

}
