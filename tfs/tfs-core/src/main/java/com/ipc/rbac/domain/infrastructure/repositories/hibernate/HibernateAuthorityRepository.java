package com.ipc.rbac.domain.infrastructure.repositories.hibernate;

import com.ipc.rbac.domain.Authority;
import com.ipc.rbac.domain.Role;
import com.ipc.rbac.domain.Authority;
import com.ipc.rbac.domain.AuthorityRepository;
import com.ipc.rbac.domain.Permission;
import com.ipc.rbac.domain.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public class HibernateAuthorityRepository implements AuthorityRepository {
	
    @Autowired(required=true)
    private SessionFactory sessionFactory;	

	@Override
	public Authority getAuthority(Long id) {
        return (Authority) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.Authority where id = ?").setParameter(0, id).uniqueResult();
	}
	
	@Override
	public void delete(Authority authority) {
        Session session = this.sessionFactory.getCurrentSession(); 
        session.delete(authority);
	}	

	@Override
	public void persist(Authority authority) {
        Session session = this.sessionFactory.getCurrentSession(); 
        session.persist(authority);
	}

	@Override
	public Permission getPermission(Long id) {
        return (Permission) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.Permission where id = ?").setParameter(0, id).uniqueResult();
	}

	@Override
	public Role getRole(Long id) {
        return (Role) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.Role where id = ?").setParameter(0, id).uniqueResult();
	}

}
