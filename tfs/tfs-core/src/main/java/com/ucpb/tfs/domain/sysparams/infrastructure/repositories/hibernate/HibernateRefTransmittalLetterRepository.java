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
import com.ucpb.tfs.domain.sysparams.RefTransmittalLetter;
import com.ucpb.tfs.domain.sysparams.RefTransmittalLetterRepository;

@Transactional
public class HibernateRefTransmittalLetterRepository implements RefTransmittalLetterRepository{

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void saveOrUpdate(RefTransmittalLetter refTransmittalLetter) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(refTransmittalLetter);
	}

	@Override
	public RefTransmittalLetter getRefTransmittalLetter(
			String transmittalLetterCode) {
        Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(RefTransmittalLetter.class);
        crit.add(Restrictions.eq("transmittalLetterCode", transmittalLetterCode).ignoreCase());

        RefTransmittalLetter refTransmittalLetter = null;
        Object refTransmittalLetterObj = crit.uniqueResult();
        
        if (refTransmittalLetterObj != null && refTransmittalLetterObj instanceof RefTransmittalLetter) {
        	refTransmittalLetter = (RefTransmittalLetter)refTransmittalLetterObj;
        }

        return refTransmittalLetter;
	}

	@Override
	public void delete(Long id) {
		RefTransmittalLetter refTransmittalLetter = (RefTransmittalLetter) sessionFactory.getCurrentSession().load(RefTransmittalLetter.class,id);
	    sessionFactory.getCurrentSession().delete(refTransmittalLetter);
	}

	@SuppressWarnings("unchecked")
    private List<RefTransmittalLetter> findRefTransmittalLetter(Criteria criteria) {
        
		List<RefTransmittalLetter> refTransmittalLetters = criteria.list();

        Hibernate.initialize(refTransmittalLetters);

        Gson gson = new Gson();

        String result = gson.toJson(refTransmittalLetters);

		List<RefTransmittalLetter> returnMap = gson.fromJson(result, List.class);

        return returnMap;
    }

    @Override
    public List<RefTransmittalLetter> getAllRefTransmittalLetter() {
		Session session  = this.sessionFactory.getCurrentSession();
		
        Criteria criteria = session.createCriteria(RefTransmittalLetter.class);
        
        criteria.addOrder(Order.asc("id"));

        return findRefTransmittalLetter(criteria);

    }
	
}
