package com.ucpb.tfs.domain.reimbursing.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode;
import com.ucpb.tfs.domain.reimbursing.LcInstructionToBank;
import com.ucpb.tfs.domain.reimbursing.LcInstructionToBankRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: Marv
 * Date: 10/31/12
 */
@Transactional
public class HibernateLcInstructionToBankRepository implements LcInstructionToBankRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(LcInstructionToBank lcInstructionToBank) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(lcInstructionToBank);
    }

    @Override
    public void merge(LcInstructionToBank lcInstructionToBank) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(lcInstructionToBank);
    }

    @Override
    public void update(LcInstructionToBank lcInstructionToBank) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(lcInstructionToBank);
    }

    @Override
    public LcInstructionToBank load(InstructionToBankCode instructionToBankCode) {
        return (LcInstructionToBank) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.reimbursing.LcInstructionToBank where instructionToBankCode = ?").setParameter(0, instructionToBankCode).uniqueResult();
    }

    @Override
    public Set<LcInstructionToBank> load(DocumentNumber documentNumber) {
        Session session = this.mySessionFactory.getCurrentSession();

        // id is intentionally left out of the select
        String queryStatement = new String("select instructionToBankCode, cast(instruction as varchar(6500)) as instruction from LcInstructionToBank where documentNumber = ?");
        Query query = session.createSQLQuery(queryStatement.toString()).setParameter(0, documentNumber.toString());

        Set<LcInstructionToBank> lcRequiredDocumentSet = new HashSet<LcInstructionToBank>();

        Iterator it = query.list().iterator();

        while(it.hasNext()) {

            Object[] obj = (Object[]) it.next();

            LcInstructionToBank lcInstructionToBank = new LcInstructionToBank();

            if (obj[0] != null) {
                InstructionToBankCode instructionToBankCode = new InstructionToBankCode((String)obj[0]);
                lcInstructionToBank.setInstructionToBankCode(instructionToBankCode);
            }

            lcInstructionToBank.setInstruction((String) obj[1]);

            lcRequiredDocumentSet.add(lcInstructionToBank);
        }

        return lcRequiredDocumentSet;
    }
}
