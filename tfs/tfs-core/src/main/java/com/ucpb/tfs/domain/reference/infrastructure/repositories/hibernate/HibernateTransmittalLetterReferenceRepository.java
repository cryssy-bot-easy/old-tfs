package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.letter.TransmittalLetterCode;
import com.ucpb.tfs.domain.reference.TransmittalLetterReference;
import com.ucpb.tfs.domain.reference.TransmittalLetterReferenceRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Marv
 * Date: 11/7/12
 */

public class HibernateTransmittalLetterReferenceRepository implements TransmittalLetterReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void save(TransmittalLetterReference transmittalLetterReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(transmittalLetterReference);
    }

    @Override
    public TransmittalLetterReference load(TransmittalLetterCode transmittalLetterCode) {
        return (TransmittalLetterReference) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.TransmittalLetterReference where transmittalLetterCode = :transmittalLetterCode").setParameter("transmittalLetterCode", transmittalLetterCode).uniqueResult();
    }

    @Override
    public List<TransmittalLetterReference> getTransmittalLetter() {
        return (List<TransmittalLetterReference>)this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.TransmittalLetterReference");
    }

    @Override
    public void clear() {
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("delete from com.ucpb.tfs.domain.reference.TransmittalLetterReference tlr");
        qry.executeUpdate();
    }

}
