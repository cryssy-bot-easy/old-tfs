package com.ipc.rbac.domain.infrastructure.repositories.hibernate;

import com.ipc.rbac.domain.Permission;
import com.ipc.rbac.domain.PermissionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernatePermissionRepository implements PermissionRepository {
	
    @Autowired(required=true)
    private SessionFactory sessionFactory;	
	
    // get permission from parameter id
    @Override
	public Permission getPermission(Long id){
        return (Permission) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.Permission where id = ?").setParameter(0, id).uniqueResult();
	}
    
    // persists permission
	@Override
	public void persist(Permission permission){
        Session session = this.sessionFactory.getCurrentSession(); 
        session.persist(permission);
	}
	
	// persists changes to permission
	@Override
	public Permission persistChanges(Permission permission){
        Session session = this.sessionFactory.getCurrentSession();
        Permission p = (Permission) session.merge(permission);
        return p;
	}

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ipc.rbac.domain.Permission").iterate().next() ).longValue();
    }
	
}
