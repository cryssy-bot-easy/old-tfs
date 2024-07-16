package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.documents.DocumentCode;
import com.ucpb.tfs.domain.reference.RequiredDocumentsReference;
import com.ucpb.tfs.domain.reference.RequiredDocumentsReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Marv
 * Date: 10/31/12
 */

public class HibernateRequiredDocumentsReferenceRepository implements RequiredDocumentsReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void save(RequiredDocumentsReference requiredDocumentsReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(requiredDocumentsReference);
    }

    @Override
    public RequiredDocumentsReference load(DocumentCode documentCode) {
        return (RequiredDocumentsReference) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.RequiredDocumentsReference where documentCode = :documentCode").setParameter("documentCode", documentCode).uniqueResult();
    }

    @Override
    public List<RequiredDocumentsReference> getRequiredDocuments(DocumentType documentType) {
        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(RequiredDocumentsReference.class);
        c.add(Restrictions.eq("documentType", documentType));

        List<RequiredDocumentsReference> results = c.list();

        if(results.size() > 0) {
            return results;
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("delete from com.ucpb.tfs.domain.reference.RequiredDocumentsReference rdr");
        qry.executeUpdate();
    }

}
