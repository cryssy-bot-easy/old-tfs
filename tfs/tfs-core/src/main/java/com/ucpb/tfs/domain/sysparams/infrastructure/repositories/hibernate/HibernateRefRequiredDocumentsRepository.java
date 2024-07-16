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
import com.ucpb.tfs.domain.sysparams.RefRequiredDocuments;
import com.ucpb.tfs.domain.sysparams.RefRequiredDocumentsRepository;

@Transactional
public class HibernateRefRequiredDocumentsRepository implements RefRequiredDocumentsRepository{

	@Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public void saveOrUpdate(RefRequiredDocuments refRequiredDocuments) {
		sessionFactory.getCurrentSession().saveOrUpdate(refRequiredDocuments);
	}

	@Override
	public RefRequiredDocuments getRefRequiredDocument(String documentCode) {
        Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(RefRequiredDocuments.class);
        crit.add(Restrictions.eq("documentCode", documentCode).ignoreCase());

        RefRequiredDocuments refRequiredDocument = null;
        Object refRequiredDocumentsObj = crit.uniqueResult();
        
        if (refRequiredDocumentsObj != null && refRequiredDocumentsObj instanceof RefRequiredDocuments) {
        	refRequiredDocument = (RefRequiredDocuments)refRequiredDocumentsObj;
        }

        return refRequiredDocument;
	}

	@SuppressWarnings("unchecked")
    private List<RefRequiredDocuments> findRefRequiredDocuments(Criteria criteria) {
        
		List<RefRequiredDocuments> refRequiredDocuments = criteria.list();

        Hibernate.initialize(refRequiredDocuments);

        Gson gson = new Gson();

        String result = gson.toJson(refRequiredDocuments);

		List<RefRequiredDocuments> returnMap = gson.fromJson(result, List.class);

        return returnMap;
    }

    @Override
    public List<RefRequiredDocuments> getAllRefRequiredDocuments() {
		Session session  = this.sessionFactory.getCurrentSession();
		
        Criteria criteria = session.createCriteria(RefRequiredDocuments.class);
        
        criteria.addOrder(Order.asc("id"));

        return findRefRequiredDocuments(criteria);

    }

	@Override
	public void delete(Long id) {
		RefRequiredDocuments refRequiredDocuments = (RefRequiredDocuments) sessionFactory.getCurrentSession().load(RefRequiredDocuments.class,id);
	    sessionFactory.getCurrentSession().delete(refRequiredDocuments);
	}

}
