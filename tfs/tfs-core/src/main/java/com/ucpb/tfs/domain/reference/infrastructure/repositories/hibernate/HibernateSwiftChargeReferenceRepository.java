package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.SwiftChargeReference;
import com.ucpb.tfs.domain.reference.SwiftChargeReferenceRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Marv
 * Date: 11/28/12
 */

public class HibernateSwiftChargeReferenceRepository implements SwiftChargeReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void save(SwiftChargeReference swiftChargeReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(swiftChargeReference);
    }

    @Override
    public SwiftChargeReference load(String code) {
        return (SwiftChargeReference) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.SwiftChargeReference where code = :code").setParameter("code", code).uniqueResult();
    }

    @Override
    public List<SwiftChargeReference> getSwiftChargeReferences() {
        return (List<SwiftChargeReference>)this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.reference.SwiftChargeReference");
    }

    @Override
    public void clear() {
        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("delete from com.ucpb.tfs.domain.reference.SwiftChargeReference tlr");
        qry.executeUpdate();
    }

}
