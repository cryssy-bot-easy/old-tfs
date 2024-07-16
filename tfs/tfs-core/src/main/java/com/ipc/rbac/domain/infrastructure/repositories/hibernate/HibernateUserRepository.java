package com.ipc.rbac.domain.infrastructure.repositories.hibernate;

import com.ipc.rbac.domain.User;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ipc.rbac.domain.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateUserRepository implements UserRepository {
	
    @Autowired(required=true)
    private SessionFactory sessionFactory;	

    // get user from parameter id
	@Override
	public User getUser(UserActiveDirectoryId userActiveDirectoryId) {
        return (User) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.User where userActiveDirectoryId = ?").setParameter(0, userActiveDirectoryId).uniqueResult();
	}

	// saveOrUpdate user
	@Override
	public void persist(User user) {
        Session session = this.sessionFactory.getCurrentSession(); 
        session.persist(user);
	}
	
	// saveOrUpdate changes to user
	@Override
	public User persistChanges(User user) {
        Session session = this.sessionFactory.getCurrentSession(); 
        User u = (User) session.merge(user);
        return u;
	}

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ipc.rbac.domain.User").iterate().next() ).longValue();
    }

}
