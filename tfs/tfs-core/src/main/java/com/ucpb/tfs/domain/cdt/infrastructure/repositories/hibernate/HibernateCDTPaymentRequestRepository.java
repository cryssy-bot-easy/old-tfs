package com.ucpb.tfs.domain.cdt.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository;
import com.ucpb.tfs.domain.cdt.EmailNotif;
import com.ucpb.tfs.domain.cdt.enums.CDTStatus;
import com.ucpb.tfs.domain.cdt.enums.PaymentRequestType;

import net.sf.saxon.functions.IndexOf;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: HibernateCDTPaymentRequestRepository
 */

/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# 20170214-093
	SCR/ER Description: To limit the records to show on CDT inquiry.
	[Created by:] Jesse James Joson
	[Date Deployed:] 2/20/2017
	Program [Revision] Details: To limit the records to show on CDT inquiry.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: HibernateCDTPaymentRequestRepository
 */


@Transactional
public class HibernateCDTPaymentRequestRepository implements CDTPaymentRequestRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(CDTPaymentRequest cdtPaymentRequest) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(cdtPaymentRequest);
    }

    @Override
    public void merge(CDTPaymentRequest cdtPaymentRequest) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(cdtPaymentRequest);
    }

    @Override
    public void update(CDTPaymentRequest cdtPaymentRequest) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(cdtPaymentRequest);
    }

    @Override
    public List<CDTPaymentRequest> getAllRequests() {

        Session session  = this.mySessionFactory.getCurrentSession();

        Query query = session.createQuery("from com.ucpb.tfs.domain.cdt.CDTPaymentRequest");

//        query.setParameter("price", new Float(1000));

        List paymentRequests = query.list();

        return paymentRequests;
    }

    @Override
    public List<CDTPaymentRequest> getRequestsMatching(String refNumber, String iedNumber, String importer, String requestType, String status, Date txDateFrom, Date txDateTo, String unitCode, Date uploadDate, String aabRefCode) {

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Query query = session.createQuery("from com.ucpb.tfs.domain.cdt.CDTPaymentRequest");
        
        List<CDTStatus> onLoad = Arrays.asList(CDTStatus.PENDING,CDTStatus.NEW,CDTStatus.PAID);
        boolean noFilter = true;

        // add criteria if parameter was specified
        if(refNumber != null) {
            crit.add(Restrictions.ilike("paymentReferenceNumber", refNumber, MatchMode.ANYWHERE));
            noFilter = false;
        }

        if(iedNumber != null) {
            crit.add(Restrictions.ilike("iedieirdNumber", iedNumber, MatchMode.ANYWHERE));
            noFilter = false;
        }

        if(importer != null) {
            crit.add(Restrictions.ilike("clientName", "%" + importer + "%"));
            noFilter = false;
        }

        if(requestType != null) {
            crit.add(Restrictions.eq("paymentRequestType", PaymentRequestType.valueOf(requestType.toUpperCase())));
            noFilter = false;
        }

        if(status != null) {
            crit.add(Restrictions.eq("status", CDTStatus.valueOf(status.toUpperCase())));
            noFilter = false;
        } 
        if(status != null){
	        if(!unitCode.equalsIgnoreCase("909") || (unitCode.equalsIgnoreCase("909") && (status.equalsIgnoreCase("NEW") || status.equalsIgnoreCase("PAID")))) {
	        	System.out.println("unitCode: " + unitCode + " status: " + status);
	        	crit.add(Restrictions.eq("unitCode", unitCode));
	        }
        } else {
        	crit.add(Restrictions.eq("unitCode", unitCode));
        }

        if(uploadDate != null) {
            crit.add(Restrictions.ge("dateUploaded", getDateWithoutTime(uploadDate)));
            noFilter = false;
        }

        if (txDateFrom != null) {
            crit.add(Restrictions.ge("datePaid", getDateWithoutTime(txDateFrom)));
            noFilter = false;
        }

        if (txDateTo != null) {
            crit.add(Restrictions.le("datePaid", getDateWithoutTime(txDateTo)));
            noFilter = false;
        }
        
        if (aabRefCode != null) {
            crit.add(Restrictions.ilike("agentBankCode", aabRefCode, MatchMode.ANYWHERE));
            noFilter = false;
        }
        
        if (status == null && noFilter==true) {
        	crit.add(Restrictions.in("status", onLoad)); 
        }

        crit.addOrder(Order.desc("dateUploaded"));
        crit.addOrder(Order.desc("datePaid"));

        // This will set the max limit of the Inquiry.
        crit.setMaxResults(50);
        
        List paymentRequests = crit.list();

        return paymentRequests;

    }
    

	@Override
	public List<EmailNotif> getRequestsMatchingEmailTable(
			String iedieirdNumber,
	           String emailAddress,
	           String emailStatus,
	           Date sentTime) {
		Session session  = this.mySessionFactory.getCurrentSession();
		
        Criteria crit = session.createCriteria(EmailNotif.class);
		
		Date myDate = new Date();
	    String today = new SimpleDateFormat("yyyy-MM-dd").format(myDate);
	    
	    Query query = session.createQuery("SELECT E FROM com.ucpb.tfs.domain.cdt.EmailNotif E, com.ucpb.tfs.domain.cdt.CDTPaymentRequest C WHERE E.iedieirdNumber = C.iedieirdNumber AND date(C.dateUploaded)  = '"+ today +"'");

        List paymentRequests = query.list();
	    
	    return paymentRequests;
	    
	}
 
    @Override
    public List<CDTPaymentRequest> getcdtTodays(String refNumber, String iedNumber, String importer, String requestType, String status, Date txDateFrom, Date txDateTo, String unitCode, Date uploadDate, String aabRefCode) {

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Query query = session.createQuery("from com.ucpb.tfs.domain.cdt.CDTPaymentRequest");

        // add criteria if parameter was specified
        if(refNumber != null) {
            crit.add(Restrictions.ilike("paymentReferenceNumber", refNumber, MatchMode.ANYWHERE));
        }

        if(iedNumber != null) {
            crit.add(Restrictions.ilike("iedieirdNumber", iedNumber, MatchMode.ANYWHERE));
        }

        if(importer != null) {
            crit.add(Restrictions.ilike("clientName", "%" + importer + "%"));
        }

        if(requestType != null) {
            crit.add(Restrictions.eq("paymentRequestType", PaymentRequestType.valueOf(requestType.toUpperCase())));
        }

        if(status != null) {
            crit.add(Restrictions.eq("status", CDTStatus.valueOf(status.toUpperCase())));
        } else {
        	crit.add(Restrictions.ne("status", CDTStatus.SENTTOBOC));
        }

        if(status != null){
	        if(!unitCode.equalsIgnoreCase("909") || (unitCode.equalsIgnoreCase("909") && (status.equalsIgnoreCase("NEW") || status.equalsIgnoreCase("PAID")))) {
	        	System.out.println("unitCode: " + unitCode + " status: " + status);
	        	crit.add(Restrictions.eq("unitCode", unitCode));
	        }
        } else {
        	crit.add(Restrictions.eq("unitCode", unitCode));
        }

        if(uploadDate != null) {
            crit.add(Restrictions.ge("dateUploaded", getDateWithoutTime(uploadDate)));
        }

        if (txDateFrom != null) {
            crit.add(Restrictions.ge("datePaid", getDateWithoutTime(txDateFrom)));
        }

        if (txDateTo != null) {
            crit.add(Restrictions.le("datePaid", getDateWithoutTime(txDateTo)));
        }
        
        if (aabRefCode != null) {
            crit.add(Restrictions.ilike("agentBankCode", aabRefCode, MatchMode.ANYWHERE));
        }

        
   
        crit.addOrder(Order.desc("datePaid"));
        crit.addOrder(Order.desc("dateUploaded"));

      
        crit.add(Restrictions.ne("status", CDTStatus.REMITTED));
        
        
        List paymentRequests = crit.list();

        return paymentRequests;

    }
    
    

    @Override
    @Transactional
    public CDTPaymentRequest load(String iedieirdNumber) {

        System.out.println("looking for " + iedieirdNumber);

        return (CDTPaymentRequest) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.cdt.CDTPaymentRequest where iedieirdNumber = ?").setParameter(0, iedieirdNumber).uniqueResult();
    }

    @Override
    public CDTPaymentRequest getPaymentRequestDetails(String iedieirdNumber) {

        return (CDTPaymentRequest) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.cdt.CDTPaymentRequest where iedieirdNumber = ?").setParameter(0, iedieirdNumber).uniqueResult();

    }

    @Override
    public List<CDTPaymentRequest> getHistoryUpdatedToday(String unitCode) {


        Date today = getDateWithoutTime(new Date());

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);
        crit.add(Restrictions.or(
                Restrictions.between("datePaymentHistoryUploaded", today, getMaxDateWithoutTime(today)),
                Restrictions.between("datePaid", today, getMaxDateWithoutTime(today))
        ));

        crit.add(Restrictions.eq("unitCode", unitCode));

        crit.addOrder(Order.desc("datePaid"));
        crit.addOrder(Order.desc("dateUploaded"));

        return crit.list();
    }
    
    @Override
    public List<CDTPaymentRequest> getHistoryUpdatedToday(String unitCode, Date bocDate) {

        Date today = getDateWithoutTime(bocDate);

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);
//        crit.add(Restrictions.or(
        
//                Restrictions.le("datePaid", today)
     
//        ));
        
        crit.add(Restrictions.or(
//                Restrictions.between("datePaymentHistoryUploaded", today, getMaxDateWithoutTime(today)),
                Restrictions.between("confDate", today, getMaxDateWithoutTime(today)),
                Restrictions.between("datePaid", today, getMaxDateWithoutTime(today))
        ));
        
//        crit.add(Restrictions.between("datePaymentHistoryUploaded", fromDate,toDate));
//        crit.add(Restrictions.eq("status", CDTStatus.PAID));
//        crit.add(Restrictions.eq("status", CDTStatus.NEW));
//        crit.add(Restrictions.eq("status", CDTStatus.));
        crit.add(Restrictions.eq("unitCode", unitCode));
        
        crit.addOrder(Order.desc("datePaid"));
        crit.addOrder(Order.desc("dateUploaded"));
        
        

        return crit.list();
    }

    
    
    @Override
    public List<CDTPaymentRequest> getHistoryUpdatedToday() {


        Date today = getDateWithoutTime(new Date());

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);
//        crit.add(Restrictions.or(
//                Restrictions.between("datePaymentHistoryUploaded", today, getMaxDateWithoutTime(today)),
//                Restrictions.between("datePaid", today, getMaxDateWithoutTime(today))
//        ));
        crit.add(Restrictions.between("datePaid", today, getMaxDateWithoutTime(today)));

        crit.addOrder(Order.desc("datePaid"));
        crit.addOrder(Order.desc("dateUploaded"));

        return crit.list();
    }


    private static Date getDateWithoutTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static Date getMaxDateWithoutTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    @Override
    public List<CDTPaymentRequest> getAllPaidRequests() {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Date today = getDateWithoutTime(new Date());

        crit.add(Restrictions.eq("status", CDTStatus.PAID));

        crit.add(Restrictions.or(
                Restrictions.between("datePaymentHistoryUploaded", today, getMaxDateWithoutTime(today)),
                Restrictions.between("datePaid", today, getMaxDateWithoutTime(today))
        ));
//        crit.add(Restrictions.or(
//                Restrictions.le("datePaymentHistoryUploaded", getMaxDateWithoutTime(today)),
//                Restrictions.le("datePaid", getMaxDateWithoutTime(today))
//        ));

        return crit.list();
    }

    @Override
    public List<CDTPaymentRequest> getAllPaidRequests(String unitCode) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Date today = getDateWithoutTime(new Date());

        crit.add(Restrictions.eq("status", CDTStatus.PAID));
        crit.add(Restrictions.eq("unitCode", unitCode));

        crit.add(Restrictions.or(
                Restrictions.between("datePaymentHistoryUploaded", today, getMaxDateWithoutTime(today)),
                Restrictions.between("datePaid", today, getMaxDateWithoutTime(today))
        ));
//        crit.add(Restrictions.or(
//                Restrictions.le("datePaymentHistoryUploaded", getMaxDateWithoutTime(today)),
//                Restrictions.le("datePaid", getMaxDateWithoutTime(today))
//        ));

        return crit.list();
    }
    
    
    @Override
    public List<CDTPaymentRequest> getAllPaidRequests(String unitCode, Date confdate) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Date today = getDateWithoutTime(confdate);

        crit.add(Restrictions.eq("status", CDTStatus.PAID));
        crit.add(Restrictions.eq("unitCode", unitCode));

        crit.add(Restrictions.or(
                Restrictions.between("confDate", today, getMaxDateWithoutTime(today))
//                Restrictions.between("datePaid", today, getMaxDateWithoutTime(today))
        ));
//        crit.add(Restrictions.or(
//                Restrictions.le("datePaymentHistoryUploaded", getMaxDateWithoutTime(today)),
//                Restrictions.le("datePaid", getMaxDateWithoutTime(today))
//        ));

        return crit.list();
    }

    @Override
    public List<CDTPaymentRequest> getAllSentToMobBoc() {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Date today = getDateWithoutTime(new Date());

        crit.add(Restrictions.eq("status", CDTStatus.SENTTOBOC));

        crit.add(Restrictions.or(
                Restrictions.between("datePaymentHistoryUploaded", today, getMaxDateWithoutTime(today)),
                Restrictions.between("datePaid", today, getMaxDateWithoutTime(today))
        ));
//        crit.add(Restrictions.or(
//                Restrictions.le("datePaymentHistoryUploaded", getMaxDateWithoutTime(today)),
//                Restrictions.le("datePaid", getMaxDateWithoutTime(today))
//        ));

        return crit.list();
    }

    @Override
    public List<CDTPaymentRequest> getAllSentToMobBoc(String unitCode) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Date today = getDateWithoutTime(new Date());

        crit.add(Restrictions.eq("status", CDTStatus.SENTTOBOC));
        crit.add(Restrictions.eq("unitCode", unitCode));

        crit.add(Restrictions.or(
                Restrictions.between("datePaymentHistoryUploaded", today, getMaxDateWithoutTime(today)),
                Restrictions.between("datePaid", today, getMaxDateWithoutTime(today))
        ));
//        crit.add(Restrictions.or(
//                Restrictions.le("datePaymentHistoryUploaded", getMaxDateWithoutTime(today)),
//                Restrictions.le("datePaid", getMaxDateWithoutTime(today))
//        ));

        return crit.list();
    }
    
    
    @Override		
    public List<CDTPaymentRequest> getAllBranch() {		
        Session session  = this.mySessionFactory.getCurrentSession();		
        Criteria crit = session.createCriteria(CDTPaymentRequest.class);		
  		
        crit.setProjection(Projections.distinct(Projections.property("unitCode")));		
        		
        		
        System.out.println("After distinct list: " + crit.list().toString());		
        return crit.list();		
    }

    @Override
    public List<CDTPaymentRequest> getAllSentRequests(Date from, Date to, List<PaymentRequestType> paymentRequestTypeList) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        crit.add(Restrictions.eq("status", CDTStatus.SENTTOBOC));
//        crit.add(Restrictions.ge("pchcConfirmationDate", from));
//        crit.add(Restrictions.le("pchcConfirmationDate", to));
        crit.add(Restrictions.ge("confDate", from));
        crit.add(Restrictions.le("confDate", to));       
        crit.add(Restrictions.or(Restrictions.eq("isRemitted", Boolean.FALSE),
                Restrictions.isNull("isRemitted")));

        System.out.println(paymentRequestTypeList);

        if (!paymentRequestTypeList.isEmpty()) {
            crit.add(Restrictions.in("paymentRequestType", paymentRequestTypeList));
        }

        return crit.list();
    }

    // Used by AMLA
    @Override
    public List<CDTPaymentRequest> getAllRemittedRequestsWithCif(Date from, Date to) {

        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        crit.add(Restrictions.eq("status", CDTStatus.SENTTOBOC));
        crit.add(Restrictions.ge("dateSent", from));
        crit.add(Restrictions.le("dateSent", to));
        crit.add(Restrictions.eq("isRemitted", Boolean.TRUE));
        crit.add(Restrictions.and(Restrictions.ne("cifNumber",""), Restrictions.isNotNull("cifNumber")));

        return crit.list();
    }

    @Override
    public List<CDTPaymentRequest> getAllRemittedRequests(Date from, Date to, List<PaymentRequestType> paymentRequestTypeList) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        crit.add(Restrictions.eq("status", CDTStatus.SENTTOBOC));
        crit.add(Restrictions.ge("dateSent", from));
        crit.add(Restrictions.le("dateSent", to));
        crit.add(Restrictions.eq("isRemitted", Boolean.TRUE));
//        crit.add(Restrictions.or(Restrictions.eq("isRemitted", Boolean.FALSE),
//                Restrictions.isNull("isRemitted")));

        System.out.println(paymentRequestTypeList);

        if (!paymentRequestTypeList.isEmpty()) {
            crit.add(Restrictions.in("paymentRequestType", paymentRequestTypeList));
        }

        return crit.list();
    }


    @Override
    public CDTPaymentRequest getOwnPaymentRequestDetails(String iedieirdNumber, String unitCode) {
        return (CDTPaymentRequest) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.cdt.CDTPaymentRequest where iedieirdNumber = ? and unitCode = ?").
                setParameter(0, iedieirdNumber).setParameter(1, unitCode).uniqueResult();
    }

    public List<CDTPaymentRequest> getNewPaymentRequestsYesterday() {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Calendar today = Calendar.getInstance();

        // get yesterday

        Calendar yesterdayCal = today;
        yesterdayCal.add(Calendar.DATE, -1);
        Date yesterday = yesterdayCal.getTime();

        yesterday = getDateWithoutTime(yesterday);

        Calendar maxYesterdayCal = Calendar.getInstance();
        maxYesterdayCal.setTime(getDateWithoutTime(new Date()));

        maxYesterdayCal.add(Calendar.MILLISECOND, -1);

        Date maxYesterday = maxYesterdayCal.getTime();

        crit.add(Restrictions.between("dateUploaded", yesterday, maxYesterday));
        crit.add(Restrictions.eq("status", CDTStatus.NEW));

        return crit.list();
    }

    public List<CDTPaymentRequest> getNewPaymentRequestsToday() {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Date minToday = today.getTime();

        Calendar tomorrow = today;
        tomorrow.add(Calendar.DATE, 1);
        tomorrow.add(Calendar.MILLISECOND, -1);

        Date maxToday = tomorrow.getTime();

        System.out.println("minToday " + minToday);
        System.out.println("maxToday " + maxToday);

//        crit.add(Restrictions.between("dateUploaded", minToday, maxToday));
        crit.add(Restrictions.le("dateUploaded", maxToday));
        crit.add(Restrictions.eq("status", CDTStatus.NEW));

        return crit.list();
    }

    @Override
    public List<CDTPaymentRequest> getAllUploadedToday(String unitCode) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Date minToday = today.getTime();

        Calendar tomorrow = today;
        tomorrow.add(Calendar.DATE, 1);
        tomorrow.add(Calendar.MILLISECOND, -1);

        Date maxToday = tomorrow.getTime();

        System.out.println("minToday " + minToday);
        System.out.println("maxToday " + maxToday);

        crit.add(Restrictions.between("dateUploaded", minToday, maxToday));
        crit.add(Restrictions.eq("unitCode", unitCode));
//        crit.add(Restrictions.le("dateUploaded", maxToday));
//        crit.add(Restrictions.eq("status", CDTStatus.NEW));

        return crit.list();
    }


    @Override
    public List<CDTPaymentRequest> getConfirmedPayments(Date dateGenerated) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Calendar today = Calendar.getInstance();
        today.setTime(dateGenerated);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Date minToday = today.getTime();

        Calendar tomorrow = today;
        tomorrow.add(Calendar.DATE, 1);
        tomorrow.add(Calendar.MILLISECOND, -1);

        Date maxToday = tomorrow.getTime();

        System.out.println("minToday " + minToday);
        System.out.println("maxToday " + maxToday);

//        crit.add(Restrictions.between("dateUploaded", minToday, maxToday));
        crit.add(Restrictions.between("datePaid", minToday, maxToday));
        crit.add(Restrictions.eq("e2mStatus", "CONFIRMED"));

        crit.addOrder(Order.desc("collectionLine"));

        return crit.list();
    }

    @Override
    public List<CDTPaymentRequest> getRejectedPayments(Date dateGenerated) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CDTPaymentRequest.class);

        Calendar today = Calendar.getInstance();
        today.setTime(dateGenerated);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Date minToday = today.getTime();

        Calendar tomorrow = today;
        tomorrow.add(Calendar.DATE, 1);
        tomorrow.add(Calendar.MILLISECOND, -1);

        Date maxToday = tomorrow.getTime();

        System.out.println("minToday " + minToday);
        System.out.println("maxToday " + maxToday);

//        crit.add(Restrictions.between("dateUploaded", minToday, maxToday));
        crit.add(Restrictions.between("datePaid", minToday, maxToday));
        crit.add(Restrictions.eq("e2mStatus", "Rejected"));

        crit.addOrder(Order.desc("collectionLine"));

        return crit.list();
    }





}
