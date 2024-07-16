package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.Branch;
import com.ucpb.tfs.domain.reference.RefBranchRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Transactional
public class HibernateRefBranchRepository implements RefBranchRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;


    @Override
    public Branch getBranchById(Long id) {
        return (Branch) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.Branch where id = ?")
                .setParameter(0, id).uniqueResult();
    }

    @Override
    public Branch getBranchByUnitCode(String unitCode) {
        return (Branch) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.Branch where unitCode = ?")
                .setParameter(0, unitCode).uniqueResult();
    }

    @Override
    public void persist(Branch branch) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(branch);
    }
}
