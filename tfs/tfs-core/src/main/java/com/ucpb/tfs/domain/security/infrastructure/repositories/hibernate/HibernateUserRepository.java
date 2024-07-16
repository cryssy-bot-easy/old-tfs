package com.ucpb.tfs.domain.security.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.security.User;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.security.UserRepository;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * User: Jett
 * Date: 9/21/12
 */
@Transactional
public class HibernateUserRepository implements UserRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(User user) {
        sessionFactory.getCurrentSession().merge(user);
    }

    @Override
    public User getUser(UserId userId) {

        // return (User) this.sessionFactory.getCurrentSession().createQuery(
        //         "from com.ucpb.tfs.domain.security.User where userId = :id").setParameter("id", userId).uniqueResult();

        Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(User.class);
        crit.add(Restrictions.eq("userId.id", userId).ignoreCase());

        User user = null;
        Object userObj = crit.uniqueResult();
        if (userObj != null && userObj instanceof User) {
            user = (User)userObj;
        }

        return user;
    }

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.security.User").iterate().next() ).longValue();
    }

    @Override
    public Map getUserById(UserId userId) {

        User user = getUser(userId);

        // eagerly load all references
        Hibernate.initialize(user);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(user);
        Map returnClass = gson.fromJson(result, Map.class);

        return returnClass;

    }

	@Override
	public void persist(User user) {
		// TODO Auto-generated method stub
		 sessionFactory.getCurrentSession().persist(user);
	}

}
