package com.ipc.rbac.domain.infrastructure.repositories.hibernate;

import com.ipc.rbac.domain.AuthorityType;
import com.ipc.rbac.domain.AuthorityType;
import com.ipc.rbac.domain.AuthorityTypeRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Component
public class HibernateAuthorityTypeRepository implements AuthorityTypeRepository {
	
    @Autowired(required=true)
    private SessionFactory sessionFactory;	

	@Override
	public AuthorityType getAuthorityType(Long id) {
        return (AuthorityType) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.AuthorityType where id = ?").setParameter(0, id).uniqueResult();
	}

	@Override
	public AuthorityType persist(AuthorityType authorityType) {
        return (AuthorityType) this.sessionFactory.getCurrentSession().merge(authorityType);
	}

	@Override
	public List<AuthorityType> listAuthorityTypes() {
        return (List<AuthorityType>) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ipc.rbac.domain.AuthorityType").list();
	}
	
}
