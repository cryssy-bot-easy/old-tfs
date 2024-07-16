package com.ucpb.tfs.domain.product;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Transactional
public class HibernateNonLcRepository implements NonLcRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public DirectRemittance getDirectRemittance(DocumentNumber documentNumber) {
        return (DirectRemittance) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.product.DirectRemittance where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
    }

    @Override
    public OpenAccount getOpenAccount(DocumentNumber documentNumber) {
        return (OpenAccount) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.product.OpenAccount where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
    }

    @Override
    public DocumentAgainstPayment getDocumentAgainstPayment(DocumentNumber documentNumber) {
        return (DocumentAgainstPayment) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.product.DocumentAgainstPayment where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
    }

    @Override
    public DocumentAgainstAcceptance getDocumentAgainstAcceptance(DocumentNumber documentNumber) {
        return (DocumentAgainstAcceptance) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.product.DocumentAgainstAcceptance where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
    }
}
