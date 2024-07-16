package com.ucpb.tfs.domain.documents.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.documents.DocumentCode;
import com.ucpb.tfs.domain.documents.LcRequiredDocument;
import com.ucpb.tfs.domain.documents.RequiredDocument;
import com.ucpb.tfs.domain.documents.RequiredDocumentRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Marv
 * Date: 10/31/12
 */

public class HibernateRequiredDocumentRepository implements RequiredDocumentRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(RequiredDocument requiredDocument) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(requiredDocument);
    }

    @Override
    public void merge(RequiredDocument requiredDocument) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(requiredDocument);
    }

    @Override
    public void update(RequiredDocument requiredDocument) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(requiredDocument);
    }

    @Override
    @Transactional
    public RequiredDocument load(DocumentCode documentCode) {
        return (RequiredDocument) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.documents.RequiredDocument where documentCode = ?").setParameter(0, documentCode).uniqueResult();
    }

}
