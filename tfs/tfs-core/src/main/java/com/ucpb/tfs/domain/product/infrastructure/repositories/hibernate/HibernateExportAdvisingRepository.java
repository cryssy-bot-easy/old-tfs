package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.product.*;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 2/22/13
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional
public class HibernateExportAdvisingRepository implements ExportAdvisingRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public List<ExportAdvising> getAllExportAdvising(DocumentNumber documentNumber, DocumentNumber lcNumber, String exporterName, Date processDate, String unitCode) {
        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ExportAdvising.class);

        if(documentNumber != null) {
            crit.add(Restrictions.ilike("documentNumber.documentNumber", "%" + documentNumber.toString() + "%"));
        }

        if(lcNumber != null) {
            crit.add(Restrictions.ilike("lcNumber.documentNumber", "%" + lcNumber.toString() + "%"));
        }

        if(exporterName != null) {
            crit.add(Restrictions.ilike("exporterName", "%" + exporterName + "%"));
        }
        
        if (processDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(processDate);

            Date from = calendar.getTime();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            Date to = calendar.getTime();

            crit.add(Restrictions.between("processDate", from, to));
        }
        
        if (unitCode != null) {
        	crit.add(Restrictions.eq("ccbdBranchUnitCode", unitCode));
        }
        
        crit.addOrder(Order.desc("processDate"));

        return crit.list();
    }

    @Override
    public List<ExportAdvising> autoCompleteExportAdvising(String documentNumber) {
        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ExportAdvising.class);

        if(documentNumber != "") {
//            crit.add(Restrictions.like("documentNumber".toString(), documentNumber));
            crit.add(Restrictions.like("documentNumber", new DocumentNumber(documentNumber)));
        }

        return crit.list();
    }

    @Override
    public List<ExportAdvising> autoCompleteExportAdvising(String cifNumber, String documentNumber) {
        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ExportAdvising.class);

        if(documentNumber != "") {
//            crit.add(Restrictions.like("documentNumber".toString(), documentNumber));
            crit.add(Restrictions.like("documentNumber", new DocumentNumber(documentNumber)));
        }

        if(cifNumber != "") {
            crit.add(Restrictions.eq("cifNumber", cifNumber));
        }


        return crit.list();
    }
}
