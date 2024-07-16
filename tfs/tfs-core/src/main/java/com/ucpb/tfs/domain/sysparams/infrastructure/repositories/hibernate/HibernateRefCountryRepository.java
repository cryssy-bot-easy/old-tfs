package com.ucpb.tfs.domain.sysparams.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.sysparams.RefCountryRepository;
import com.ucpb.tfs.domain.sysparams.RefCountry;
import com.ucpb.tfs.domain.sysparams.RefCountryRepository;
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
public class HibernateRefCountryRepository implements RefCountryRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public RefCountry save(RefCountry refCountry) {
        Session session = this.sessionFactory.getCurrentSession();
        session.save(refCountry);
        return refCountry;
    }

    @Override
    public Map getCountryByISO(String countryISO) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
        // todo

    }


    @Override
    public void merge(RefCountry refCountry) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(refCountry);
    }

    @Override
    public void update(RefCountry refCountry) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(refCountry);
    }

    @Override
    public Long checkIfCountryExists(String countryCode) {
        Session session = this.sessionFactory.getCurrentSession();
        return ((Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.sysparams.RefCountry where countryCode = ?").setParameter(0, countryCode).iterate().next()).longValue();
    }

    @Override
    public RefCountry getCountry(String countryCode) {

        return (RefCountry) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.sysparams.RefCountry where countryCode = ?").setParameter(0, countryCode).uniqueResult();
    }

    @Override
    public Map getCountryByCode(String countryCode) {

        RefCountry refCountry = getCountry(countryCode);

        // eagerly load all references
        // Hibernate.initialize(refCustomer);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(refCountry);
        Map returnClass = gson.fromJson(result, Map.class);

        return returnClass;
    }

    @Override
    public List<RefCountry> getRequestsMatching(String countryCode,
                                                 String countryName,
                                                 String countryISO) {

        //System.out.println("angol");
        Session session = this.sessionFactory.getCurrentSession();

        try {
            Criteria crit = session.createCriteria(RefCountry.class);

            // Query query = session.createQuery("from com.ucpb.tfs.domain.sysparams.RefCustomer");

        // add criteria if parameter was specified
        if(countryCode != null && !countryCode.trim().isEmpty()) {
            // crit.add(Restrictions.eq("centralBankCode", centralBankCode));
            crit.add(Restrictions.ilike("countryCode", countryCode, MatchMode.ANYWHERE));
        }

        if(countryName != null && !countryName.trim().isEmpty()) {
            // crit.add(Restrictions.eq("clientTaxAccountNumber", clientTaxAccountNumber));
            crit.add(Restrictions.ilike("countryName", countryName, MatchMode.ANYWHERE));
        }

        if(countryISO != null && !countryISO.trim().isEmpty()) {
            // crit.add(Restrictions.like("cifLongName", "%" + cifLongName + "%"));
            crit.add(Restrictions.ilike("countryISO", countryISO, MatchMode.ANYWHERE));
        }



            //System.out.println("ito yan");
            List<RefCountry> refCountries =  (List<RefCountry>) crit.list();
            //System.out.println(refCountries);
            return refCountries;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RefCountry> getAllCountries() {
        Session session = sessionFactory.getCurrentSession();
       // Query queryResult = session.createQuery("from com.ucpb.tfs.domain.sysparams.RefCountry");
       // return session.createCriteria(RefCountry.class).list();
       // return session.createSQLQuery("SELECT * FROM REF_TFCNTRY").list();
       Query queryResult = session.createQuery("from com.ucpb.tfs.domain.sysparams.RefCountry");
        return queryResult.list();

    }
}
