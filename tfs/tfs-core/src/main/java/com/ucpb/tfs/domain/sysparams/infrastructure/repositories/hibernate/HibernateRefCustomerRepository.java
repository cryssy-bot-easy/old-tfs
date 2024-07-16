package com.ucpb.tfs.domain.sysparams.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.sysparams.RefCustomer;
import com.ucpb.tfs.domain.sysparams.RefCustomerRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/21/13
 */
@Transactional
public class HibernateRefCustomerRepository implements RefCustomerRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public RefCustomer save(RefCustomer refCustomer) {
        Session session = this.sessionFactory.getCurrentSession();
        session.save(refCustomer);
        return refCustomer;
    }

    @Override
    public void delete(RefCustomer refCustomer){
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(refCustomer);

    }

    @Override
    public void merge(RefCustomer refCustomer) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(refCustomer);
    }

    @Override
    public void update(RefCustomer refCustomer) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(refCustomer);
    }

    @Override
    public Long checkIfCustomerExists(String centralBankCode) {
        Session session = this.sessionFactory.getCurrentSession();
        return ((Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.sysparams.RefCustomer where centralBankCode = ?").setParameter(0, centralBankCode).iterate().next()).longValue();
    }

    @Override
    public RefCustomer getCustomer(Long customerId) {

        return (RefCustomer) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.sysparams.RefCustomer where customerId = ?").setParameter(0, customerId).uniqueResult();
    }

    @Override
    public Map getCustomerById(Long customerId) {

        RefCustomer refCustomer = getCustomer(customerId);

        // eagerly load all references
        // Hibernate.initialize(refCustomer);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(refCustomer);
        Map returnClass = gson.fromJson(result, Map.class);

        return returnClass;
    }

    @Override
    public List<RefCustomer> getRequestsMatching(String centralBankCode,
                                                 String clientTaxAccountNumber,
                                                 String cifLongName,
                                                 String cifLongNameB) {

        Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(RefCustomer.class);

        // Query query = session.createQuery("from com.ucpb.tfs.domain.sysparams.RefCustomer");

        // add criteria if parameter was specified
        if(centralBankCode != null && !centralBankCode.trim().isEmpty()) {
            // crit.add(Restrictions.eq("centralBankCode", centralBankCode));
            crit.add(Restrictions.ilike("centralBankCode", centralBankCode, MatchMode.ANYWHERE));
        }

        if(clientTaxAccountNumber != null && !clientTaxAccountNumber.trim().isEmpty()) {
            // crit.add(Restrictions.eq("clientTaxAccountNumber", clientTaxAccountNumber));
            crit.add(Restrictions.ilike("clientTaxAccountNumber", clientTaxAccountNumber, MatchMode.ANYWHERE));
        }

        if(cifLongName != null && !cifLongName.trim().isEmpty()) {
            // crit.add(Restrictions.like("cifLongName", "%" + cifLongName + "%"));
            crit.add(Restrictions.ilike("cifLongName", cifLongName, MatchMode.ANYWHERE));
        }

        if(cifLongNameB != null && !cifLongNameB.trim().isEmpty()) {
            // crit.add(Restrictions.like("cifLongNameB", "%" + cifLongNameB + "%"));
            crit.add(Restrictions.ilike("cifLongNameB", cifLongNameB, MatchMode.ANYWHERE));
        }

        List refCustomers = crit.list();

        return refCustomers;
    }

    @Override
    public List<RefCustomer> getAllCustomers() {
        Session session = sessionFactory.getCurrentSession();
//        Query queryResult = session.createQuery("from com.ucpb.tfs.domain.sysparams.RefCustomer");
//        return session.createCriteria(RefCustomer.class).list();
//        return session.createSQLQuery("SELECT * FROM REF_TFCUSTMR").list();
        Query queryResult = session.createQuery("from com.ucpb.tfs.domain.sysparams.RefCustomer");
        return queryResult.list();

    }
}
