package com.ucpb.tfs.domain.cdt.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.cdt.RefPas5Client;
import com.ucpb.tfs.domain.cdt.RefPas5ClientRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
public class HibernateRefPas5ClientRepository implements RefPas5ClientRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(RefPas5Client refPas5Client) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(refPas5Client);

//        session.flush();
    }

    @Override
    public void merge(RefPas5Client refPas5Client) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(refPas5Client);
    }

    @Override
    public void update(RefPas5Client refPas5Client) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(refPas5Client);
    }

    @Override
    public List<RefPas5Client> getClientsMatching(String importerName) {

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(RefPas5Client.class);

        //Query query = session.createQuery("from com.ucpb.tfs.domain.cdt.RefPas5Client");

        // add criteria if parameter was specified
        if(importerName != null) {
            crit.add(Restrictions.like("clientName", "%" + importerName + "%"));
        }

        List cdtImporters = crit.list();

        return cdtImporters;

    }
    
    @Override
    public List<RefPas5Client> getClientsMatching(String agentBankCode, String clientName){
    	
    	Session session = this.mySessionFactory.getCurrentSession();
    	
    	Criteria crit = session.createCriteria(RefPas5Client.class);
    	
    	if (agentBankCode != null){
    		crit.add(Restrictions.eq("agentBankCode", agentBankCode));
    	}
    	
    	List cdtImporters = crit.list();
    	
    	return cdtImporters;
    }

//    @Override
//    public List<RefPas5Client> getClientsMatching(String importerName, String uploader) {
//
//        Session session  = this.mySessionFactory.getCurrentSession();
//
//        Criteria crit = session.createCriteria(RefPas5Client.class);
//
//        //Query query = session.createQuery("from com.ucpb.tfs.domain.cdt.RefPas5Client");
//
//        // add criteria if parameter was specified
//        if(importerName != null) {
//            crit.add(Restrictions.like("clientName", "%" + importerName + "%"));
//        }
//
////        crit.add(Restrictions.eq("uploadedBy", uploader));
//        crit.add(Restrictions.eq("upload"));
//
//        List cdtImporters = crit.list();
//
//        return cdtImporters;
//
//    }

    @Override
    public List<RefPas5Client> getClientsMatching(String importerName, String aabRefCode, String importerTin, String customsClientNumber, String unitCode) {

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(RefPas5Client.class);
        System.out.println("importerName " + importerName);
        System.out.println("aabRefCode " + aabRefCode);
        System.out.println("importer Tin" + importerTin);
        System.out.println("ccn" + customsClientNumber);
        // add criteria if parameter was specified
        if(importerName != null) {
            crit.add(Restrictions.ilike("clientName", "%" + importerName + "%"));
        }

        if(aabRefCode != null) {
            crit.add(Restrictions.ilike("agentBankCode", "%" + aabRefCode + "%"));
        }
        
        if(importerTin != null) {
            crit.add(Restrictions.ilike("tin", "%" + importerTin + "%"));
        }
        
        if (customsClientNumber != null){
        	crit.add(Restrictions.ilike("ccn", "%" + customsClientNumber + "%"));
        }
        
        crit.add(Restrictions.eq("unitCode", unitCode));
        crit.addOrder(Order.desc("agentBankCode"));

        List cdtImporters = crit.list();

        return cdtImporters;

    }

    @Override
    public RefPas5Client load(String agentBankCode) {
        return (RefPas5Client) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.cdt.RefPas5Client where agentBankCode = ?").setParameter(0, agentBankCode).uniqueResult();
    }
}
