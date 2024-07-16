package com.ucpb.tfs.domain.security.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.security.Role;
import com.ucpb.tfs.domain.security.RoleId;
import com.ucpb.tfs.domain.security.RoleRepository;
import org.hibernate.*;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Jett
 * Date: 9/21/12
 */
@Transactional
public class HibernateRoleRepository implements RoleRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Role role) {
        sessionFactory.getCurrentSession().persist(role);
    }

    @Override
    public Role getRole(RoleId roleId) {
        return (Role) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.security.Role where roleId = ?").setParameter(0, roleId).uniqueResult();

    }

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.security.Role").iterate().next() ).longValue();
    }


    @Override
    public List getAllRoles() {

//        Gson gson = new Gson();

        Session session  = this.sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Role.class);

        criteria.setFetchMode("permissions", FetchMode.JOIN);

        List<Role> roles = criteria.list();

        Hibernate.initialize(roles);

//        Map returnClass = gson.fromJson(gson.toJson(roles), Map.class);
//        return returnClass;

        return roles;

    }

    @Override
    public List getRolesMatchingADGroups(List<String> adGroups) {

        Session session  = this.sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Role.class);

        Disjunction or = Restrictions.disjunction();

        criteria.setFetchMode("permissions", FetchMode.JOIN);

        for(String group : adGroups) {
            or.add(Restrictions.eq("adGroupName", group));
        }

        criteria.add(or);

        List<Role> roles = criteria.list();

        Hibernate.initialize(roles);

        return roles;

    }
}
