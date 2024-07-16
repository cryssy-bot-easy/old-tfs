package com.ucpb.tfs.domain.corresCharges.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.corresCharges.CorresChargeActual;
import com.ucpb.tfs.domain.corresCharges.CorresChargeActualRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: IPCVal
 * Date: 11/4/12
 */
public class HibernateCorresChargeActualRepository implements CorresChargeActualRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public CorresChargeActual load(Long id) {
        return (CorresChargeActual) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.corresCharges.CorresChargeActual where id = :id").setParameter("id", id).uniqueResult();
    }

    @Override
    public void save(CorresChargeActual corresChargeActual) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.save(corresChargeActual);
    }

    @Override
    public List<CorresChargeActual> getAllByDocumentNumber(DocumentNumber documentNumber) {

        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CorresChargeActual.class);

        if(documentNumber != null && !documentNumber.toString().isEmpty()) {
            crit.add(Restrictions.eq("documentNumber", documentNumber));
        }

        return crit.list();
    }

}
