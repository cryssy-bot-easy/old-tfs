package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.FormatCode;
import com.ucpb.tfs.domain.reference.DocumentFormatReference;
import com.ucpb.tfs.domain.reference.DocumentFormatReferenceRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Marv
 * Date: 11/10/12
 */

public class HibernateDocumentFormatReferenceRepository implements DocumentFormatReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void save(DocumentFormatReference documentFormatReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(documentFormatReference);
    }

    @Override
    public DocumentFormatReference load(FormatCode formatCode) {
        return (DocumentFormatReference) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.DocumentFormatReference where formatCode = :formatCode").setParameter("formatCode", formatCode).uniqueResult();
    }

}
