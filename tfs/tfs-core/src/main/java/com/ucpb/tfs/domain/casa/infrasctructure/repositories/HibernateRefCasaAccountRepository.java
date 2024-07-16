package com.ucpb.tfs.domain.casa.infrasctructure.repositories;

import com.ucpb.tfs.domain.casa.RefCasaAccount;
import com.ucpb.tfs.domain.casa.enums.CasaAccountType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.List;

/**
 * Created by Marv on 2/27/14.
 */

@Transactional
@Component
public class HibernateRefCasaAccountRepository implements RefCasaAccountRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void deleteAllRefCasaAccount() {
        Session session  = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery("delete from com.ucpb.tfs.domain.casa.RefCasaAccount");
        query.executeUpdate();
    }

    @Override
    public List<RefCasaAccount> findRefCasaAccountMatching(String cifNumber, String currency) {
        Session session  = this.sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(RefCasaAccount.class);

        criteria.add(Restrictions.eq("cifNumber", cifNumber));
        criteria.add(Restrictions.eq("currency", Currency.getInstance(currency)));

        criteria.add(Restrictions.or(
                Restrictions.eq("accountType", CasaAccountType.D),
                Restrictions.eq("accountType", CasaAccountType.S)
        ));

        return criteria.list();
    }

    @Override
    public void persist(RefCasaAccount refCasaAccount) {
        Session session  = this.sessionFactory.getCurrentSession();
        session.persist(refCasaAccount);
    }

}
