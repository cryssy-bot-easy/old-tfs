package com.ucpb.tfs.domain.letter.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.letter.TransmittalLetter;
import com.ucpb.tfs.domain.letter.TransmittalLetterCode;
import com.ucpb.tfs.domain.letter.TransmittalLetterRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Marv
 * Date: 11/7/12
 */

public class HibernateTransmittalLetterRepository implements TransmittalLetterRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(TransmittalLetter transmittalLetter) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(transmittalLetter);
    }

    @Override
    public void merge(TransmittalLetter transmittalLetter) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(transmittalLetter);
    }

    @Override
    public void update(TransmittalLetter transmittalLetter) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(transmittalLetter);
    }

    @Override
    @Transactional
    public TransmittalLetter load(TransmittalLetterCode transmittalLetterCode) {
        return (TransmittalLetter) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.letter.TransmittalLetter where transmittalLetterCode = ?").setParameter(0, transmittalLetterCode).uniqueResult();
    }

}
