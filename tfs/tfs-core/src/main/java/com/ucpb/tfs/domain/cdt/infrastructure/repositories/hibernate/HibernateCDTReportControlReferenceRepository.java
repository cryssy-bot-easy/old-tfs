package com.ucpb.tfs.domain.cdt.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.cdt.CDTReportControlReference;
import com.ucpb.tfs.domain.cdt.CDTReportControlReferenceRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/23/14
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class HibernateCDTReportControlReferenceRepository implements CDTReportControlReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void persist(CDTReportControlReference cdtReportControlReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(cdtReportControlReference);
    }

    @Override
    public void merge(CDTReportControlReference cdtReportControlReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(cdtReportControlReference);
    }

    @Override
    public CDTReportControlReference load(String unitCode) {
        return (CDTReportControlReference) sessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.cdt.CDTReportControlReference where unitCode = :unitCode").setParameter(unitCode, unitCode).uniqueResult();
    }

}
