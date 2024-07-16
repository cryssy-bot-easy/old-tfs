package com.ucpb.tfs.domain.documents.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.documents.DocumentCode;
import com.ucpb.tfs.domain.documents.LcRequiredDocument;
import com.ucpb.tfs.domain.documents.LcRequiredDocumentRepository;
import com.ucpb.tfs.domain.documents.enumTypes.RequiredDocumentType;
import com.ucpb.tfs.domain.product.DocumentNumber;
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
public class HibernateLcRequiredDocumentRepository implements LcRequiredDocumentRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(LcRequiredDocument lcRequiredDocument) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(lcRequiredDocument);
    }

    @Override
    public void merge(LcRequiredDocument lcRequiredDocument) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(lcRequiredDocument);
    }

    @Override
    public void update(LcRequiredDocument lcRequiredDocument) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(lcRequiredDocument);
    }

    @Override
    public LcRequiredDocument load(DocumentCode documentCode) {
        return (LcRequiredDocument) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.documents.LcRequiredDocument where documentCode = ?").setParameter(0, documentCode).uniqueResult();
    }

    @Override
    public Set<LcRequiredDocument> load(DocumentNumber documentNumber) {

        Session session = this.mySessionFactory.getCurrentSession();

        // id is intentionally left out of the select
        String queryStatement = new String("select documentCode, cast(description as varchar(6500)) as description, requiredDocumentType from LcRequiredDocument where documentNumber = ?");
        Query query = session.createSQLQuery(queryStatement.toString()).setParameter(0, documentNumber.toString());

        Set<LcRequiredDocument> lcRequiredDocumentSet = new HashSet<LcRequiredDocument>();

        Iterator it = query.list().iterator();

        while(it.hasNext()) {

            Object[] obj = (Object[]) it.next();

            LcRequiredDocument lcRequiredDocument = new LcRequiredDocument();

            if (obj[0] != null) {
                DocumentCode documentCode = new DocumentCode((String)obj[0]);
                lcRequiredDocument.setDocumentCode(documentCode);
            }

            lcRequiredDocument.setDescription((String) obj[1]);

            RequiredDocumentType requiredDocumentType = RequiredDocumentType.valueOf((String) obj[2]);
            lcRequiredDocument.setRequiredDocumentType(requiredDocumentType);

            lcRequiredDocumentSet.add(lcRequiredDocument);
        }

        return lcRequiredDocumentSet;
    }
}
