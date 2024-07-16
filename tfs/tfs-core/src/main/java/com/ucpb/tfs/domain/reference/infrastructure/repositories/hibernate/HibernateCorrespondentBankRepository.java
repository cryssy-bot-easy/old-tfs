package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.CorrespondentBank;
import com.ucpb.tfs.domain.reference.CorrespondentBankRepository;
import com.ucpb.tfs.domain.reference.RequiredDocumentsReference;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 */
public class HibernateCorrespondentBankRepository implements CorrespondentBankRepository{

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public CorrespondentBank getCorrespondentBankByBankCode(String bankCode) {
        Session session = this.sessionFactory.getCurrentSession();
        return (CorrespondentBank) session.createQuery(
                "from com.ucpb.tfs.domain.reference.CorrespondentBank where bankCode = :bankCode").setParameter("bankCode", bankCode).uniqueResult();
    }

    @Override
    public void saveCorrespondentBank(CorrespondentBank correspondentBank) {
        this.sessionFactory.getCurrentSession().persist(correspondentBank);
    }
}
