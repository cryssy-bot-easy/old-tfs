package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.InstructionToBankReference;
import com.ucpb.tfs.domain.reference.InstructionToBankReferenceRepository;
import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Marv
 * Date: 11/4/12
 */

public class HibernateInstructionToBankReferenceRepository implements InstructionToBankReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void save(InstructionToBankReference instructionToBankReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(instructionToBankReference);
    }

    @Override
    public InstructionToBankReference load(InstructionToBankCode instructionToBankCode) {
        return (InstructionToBankReference) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.InstructionToBankReference where instructionToBankCode = :instructionToBankCode").setParameter("instructionToBankCode", instructionToBankCode).uniqueResult();
    }

    @Override
    public List<InstructionToBankReference> getInstructionsToBankReference() {
        return (List<InstructionToBankReference>)this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.InstructionToBankReference");
    }

    @Override
    public void clear() {
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("delete from com.ucpb.tfs.domain.reference.InstructionToBankReference ibr");
        qry.executeUpdate();
    }
    
}
