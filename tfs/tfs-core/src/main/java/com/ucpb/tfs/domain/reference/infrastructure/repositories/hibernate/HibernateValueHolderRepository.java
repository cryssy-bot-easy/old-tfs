package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.ProductReference;
import com.ucpb.tfs.domain.reference.ValueHolder;
import com.ucpb.tfs.domain.reference.ValueHolderRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: giancarlo
 * Date: 10/10/12
 * Time: 1:21 PM
 */
public class HibernateValueHolderRepository implements ValueHolderRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    public void save(ValueHolder valueHolder) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(valueHolder);
    }

    @Override
    public ValueHolder find(String token) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(ValueHolder.class);
        c.add(token == null ? Restrictions.isNull("token") : Restrictions.eq("token", token));
        c.setMaxResults(1);

        System.out.println("finding marching token .... ");

        List<ValueHolder> results = c.list();

        if(results.size() == 1) {
            return results.get(0);
        }
        else {
            for(ValueHolder valueHolder : results){
                System.out.println("Token : "+valueHolder.getToken()+ "Value"+ valueHolder.getValue());
            }
            return null;
        }


    }

    @Override
    public Long getCount() {
        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.reference.ValueHolder ").iterate().next() ).longValue();
    }

    @Override
    public void clear() {
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.reference.ValueHolder vh");
        int tempint = qry.executeUpdate();
        System.out.println("cleared ValueHolder:"+tempint);
    }


}
