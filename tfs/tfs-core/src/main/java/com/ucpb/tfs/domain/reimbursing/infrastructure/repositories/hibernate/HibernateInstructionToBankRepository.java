package com.ucpb.tfs.domain.reimbursing.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reimbursing.InstructionToBank;
import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode;
import com.ucpb.tfs.domain.reimbursing.InstructionToBankRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Marv
 * Date: 10/31/12
 */

public class HibernateInstructionToBankRepository implements InstructionToBankRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(InstructionToBank instructionToBank) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(instructionToBank);
    }

    @Override
    public void merge(InstructionToBank instructionToBank) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(instructionToBank);
    }

    @Override
    public void update(InstructionToBank instructionToBank) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(instructionToBank);
    }

    @Override
    @Transactional
    public InstructionToBank load(InstructionToBankCode instructionToBankCode) {
        return (InstructionToBank) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.reimbursing.InstructionToBank where instructionToBankCode = ?").setParameter(0, instructionToBankCode).uniqueResult();
    }

}
