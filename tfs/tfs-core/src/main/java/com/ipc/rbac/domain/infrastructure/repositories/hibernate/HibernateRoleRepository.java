package com.ipc.rbac.domain.infrastructure.repositories.hibernate;

import com.ipc.rbac.domain.Role;
import com.ipc.rbac.domain.RoleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateRoleRepository implements RoleRepository {
	
    @Autowired(required=true)
    private SessionFactory sessionFactory;	

    // get role from parameter id
    @Override
	public Role getRole(Long id) {
        return (Role) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.Role where id = ?").setParameter(0, id).uniqueResult();
	}

    // persists role
    @Override
	public void persist(Role role) {
        Session session = this.sessionFactory.getCurrentSession(); 
        session.persist(role);
	}
    
    // persists changes to role
	@Override
	public Role persistChanges(Role role){
        Session session = this.sessionFactory.getCurrentSession();
        //session.saveOrUpdate(role);
        Role r = (Role) session.merge(role);
        return r;
	}

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ipc.rbac.domain.Role").iterate().next() ).longValue();
    }
	
}
