package com.ucpb.tfs.domain.cdt.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.cdt.CDTPaymentHistory;
import com.ucpb.tfs.domain.cdt.CDTPaymentHistoryRepository;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/13/14
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
 
 /**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: HibernateCDTPaymentHistoryRepository
 */

@Transactional
public class HibernateCDTPaymentHistoryRepository implements CDTPaymentHistoryRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public CDTPaymentHistory load(String iedieirdNumber) {
        return (CDTPaymentHistory) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.cdt.CDTPaymentHistory where iedieirdNumber = :iedieirdNumber").setParameter("iedieirdNumber", iedieirdNumber).uniqueResult();
    }

    @Override
    public CDTPaymentHistory load(String iedieirdNumber, String unitCode) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentHistory.class);

        Date dateUploaded = new Date();

        System.out.println(getMinDate(dateUploaded));
        System.out.println(getMaxDate(dateUploaded));

        crit.add(Restrictions.between("dateUploaded", getMinDate(dateUploaded), getMaxDate(dateUploaded)));
        crit.add(Restrictions.eq("unitCode", unitCode));

        crit.add(Restrictions.eq("iedieirdNumber", iedieirdNumber));

        if (crit.list().isEmpty()) {
            return null;
        }

        return (CDTPaymentHistory) crit.list().get(0);
    }
    
    
    @Override
    public CDTPaymentHistory load(String iedieirdNumber, String unitCode, Date confDate) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentHistory.class);

        Date dateUploaded = confDate;

        System.out.println(getMinDate(dateUploaded));
        System.out.println(getMaxDate(dateUploaded));

        crit.add(Restrictions.between("dateUploaded", getMinDate(dateUploaded), getMaxDate(dateUploaded)));
        crit.add(Restrictions.eq("unitCode", unitCode));

        crit.add(Restrictions.eq("iedieirdNumber", iedieirdNumber));

        if (crit.list().isEmpty()) {
            return null;
        }

        return (CDTPaymentHistory) crit.list().get(0);
    }
    

    public void delete(String unitCode, Date confDate) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentHistory.class);

        Date dateUploaded = confDate;
        
  
        
        

//        System.out.println(getMinDate(dateUploaded));
//        System.out.println(getMaxDate(dateUploaded));
//
//        crit.add(Restrictions.between("dateUploaded", getMinDate(dateUploaded), getMaxDate(dateUploaded)));
//        crit.add(Restrictions.eq("unitCode", unitCode));
//
//        
//        session.delete(crit);
//        
//
//        
//        session.flush();
        
        
//        Query q = session.createQuery("from CDTPAYMENTHISTORY where date(DATE_UPLOADED) = date(:dateUploaded) and UNITCODE = :unitCode ");
       
        
        
        Query q = session.createQuery("delete from com.ucpb.tfs.domain.cdt.CDTPaymentHistory where date(DATE_UPLOADED) = date(:dateUploaded) and UNITCODE = :unitCode ");
        q.setParameter("dateUploaded", dateUploaded);
        q.setParameter("unitCode", unitCode);
        int rowCount = q.executeUpdate();
        System.out.println("Rows affected: " + rowCount);



    }

    @Override
    public void persist(CDTPaymentHistory cdtPaymentHistory) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(cdtPaymentHistory);
    }

    @Override
    public void merge(CDTPaymentHistory cdtPaymentHistory) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(cdtPaymentHistory);
    }

    @Override
    public BigDecimal getTotalAmount(Date dateUploaded, String unitCode) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentHistory.class);

        System.out.println(getMinDate(dateUploaded));
        System.out.println(getMaxDate(dateUploaded));

        crit.add(Restrictions.between("dateUploaded", getMinDate(dateUploaded), getMaxDate(dateUploaded)));
        crit.add(Restrictions.eq("unitCode", unitCode));

        crit.setProjection(Projections.sum("amount"));

        List cr = crit.list();

        System.out.println(cr.size());
        System.out.println(crit.list().get(0));
        return (BigDecimal) crit.list().get(0);
    }

    private Date getMinDate(Date dateToGet) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(dateToGet);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private Date getMaxDate(Date dateToGet) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(dateToGet);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //calendar.add(Calendar.DATE, 1);
        Calendar tomorrow = calendar;
        tomorrow.add(Calendar.DATE, 1);

        tomorrow.add(Calendar.MILLISECOND, -1);

        return tomorrow.getTime();
    }

}
