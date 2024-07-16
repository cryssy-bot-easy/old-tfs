package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.*;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Date;

/**
 * User: giancarlo
 * Date: 10/11/12
 * Time: 12:11 PM
 */

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateAccountingEntryActualRepository implements AccountingEntryActualRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public void save(AccountingEntryActual accountingEntryActual) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(accountingEntryActual);
    }

    @Override
    @Transactional
    public List<AccountingEntryActual> getEntries(TradeServiceId tradeServiceId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActual.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));

        List<AccountingEntryActual> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }

    }

    @Override
    @Transactional
    public List<AccountingEntryActual> getTransactionEntries(TradeServiceId tradeServiceId) {
//    public List<AccountingEntryActual> getTransactionEntries(TradeServiceId tradeServiceId, String... accEvtRanIdList) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActual.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));
        c.add(Restrictions.sqlRestriction(" (accEvtRanId like '%CL%' or accEvtRanId like '%SET-UP%' or accEvtRanId like '%CONTINGENT%')"));

//        StringBuilder sqlRestriction = new StringBuilder();
//        sqlRestriction.append(" (");
//        for (String accEvtRanId : accEvtRanIdList) {
//            sqlRestriction.append(" accEvtRanId like '%"+accEvtRanId+"%' or ");
//        }
//        sqlRestriction.delete(sqlRestriction.length()-3, sqlRestriction.length()).toString();
//        sqlRestriction.append(")");
//
//        c.add(Restrictions.sqlRestriction(sqlRestriction.toString()));

        List<AccountingEntryActual> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }

    }

    @Override
    @Transactional
    public List<AccountingEntryActual> getPaymentEntries(TradeServiceId tradeServiceId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActual.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));
//        c.add(Restrictions.sqlRestriction(" accEvtRanId like '%PAYMENT%'"));
        c.add(Restrictions.not(Restrictions.sqlRestriction(" (accEvtRanId like '%CL%' or accEvtRanId like '%SET-UP%' or accEvtRanId like '%CONTINGENT%')")));
        c.addOrder(Order.asc("id"));
        List<AccountingEntryActual> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }

    }

    @Override
    @Transactional
    public List<AccountingEntryActual> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransaction accountingEventTransaction, TradeServiceId tradeServiceId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActual.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));
        c.add(Restrictions.eq("accountingEventTransaction", accountingEventTransaction));
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));

        List<AccountingEntryActual> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }

    }

    @Override
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ((Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.accounting.AccountingEntryActual").iterate().next()).longValue(); //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional
    public void clear() {

        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.accounting.AccountingEntryActual aea");
        int tempint = qry.executeUpdate();
        System.out.println("cleared AccountingEntryActual:"+tempint);

    }

    @Override
    @Transactional
    public void delete(TradeServiceId tradeServiceId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActual.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));
        List<AccountingEntryActual> results = c.list();
        for(AccountingEntryActual accountingEntryActual: results){
            System.out.println("particulars:"+accountingEntryActual.getParticulars());
            session.delete(accountingEntryActual);
        }

        System.out.println("cleared AccountingEntryActual with id:"+ tradeServiceId);

    }

    public BigDecimal getTotalOriginalDebit(TradeServiceId tradeServiceId){
        Session session = this.sessionFactory.getCurrentSession();

        Criteria c = session.createCriteria(AccountingEntryActual.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));
        c.add(Restrictions.eq("entryType", "Debit"));
        c.setProjection(Projections.projectionList().add(Projections.sum("originalAmount")));
        List results = c.list();
        BigDecimal result = BigDecimal.ZERO;
        if(results.get(0)!=null){
            result = new BigDecimal (results.get(0).toString());
        }
        return result;
    }

    public BigDecimal getTotalOriginalCredit(TradeServiceId tradeServiceId){
        Session session = this.sessionFactory.getCurrentSession();

        Criteria c = session.createCriteria(AccountingEntryActual.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));
        c.add(Restrictions.eq("entryType", "Credit"));
        c.setProjection(Projections.projectionList().add(Projections.sum("originalAmount")));
        List results = c.list();
        BigDecimal result = BigDecimal.ZERO;
        if(results.get(0)!=null){
            result = new BigDecimal (results.get(0).toString());
        }

        return result;
    }
	
	@Override
	public List<AccountingEntryActual> getAllByDate(Date dateFrom, Date dateTo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date2From = sdf.format(dateFrom);
		String date2To = sdf.format(dateTo);
		  Session session = this.sessionFactory.getCurrentSession();
		  
		 Query qry = session.createQuery("SELECT documentNumberStr,gltsNumber, originalAmount, pesoAmount,originalCurrency,unitCode,date(effectiveDate) From com.ucpb.tfs.domain.accounting.AccountingEntryActual where (date(effectivedate) between ? and ? ) and ACCEVTRANID = 'CANCELLATION-EXPIRED-LC' group by documentNumberStr,gltsNumber, originalAmount, pesoAmount,originalCurrency,unitCode,date(effectiveDate)" );
		 qry.setString(0,date2From);
		 qry.setString(1,date2To);

		  
		List <AccountingEntryActual>l = qry.list();
				
		return l;
	}


 
	public BigDecimal getTotalPesoDebit(TradeServiceId tradeServiceId){
//        Query qry = session.createQuery("select SUM(aae.pesoAmount) from com.ucpb.tfs.domain.accounting.AccountingEntryActual aae where aae.entryType='Debit' AND aae.tradeServiceId= ?");
//        qry.setString(0,tradeServiceId.toString());
//        List results = qry.list();
//        BigDecimal result = BigDecimal.ZERO;
//        if(results.get(0)!=null){
//            result = new BigDecimal (results.get(0).toString());
//        }
//
//        return result;


        Session session = this.sessionFactory.getCurrentSession();

        Criteria c = session.createCriteria(AccountingEntryActual.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));
        c.add(Restrictions.eq("entryType", "Debit"));
        c.setProjection(Projections.projectionList().add(Projections.sum("pesoAmount")));
        List results = c.list();
        BigDecimal result = BigDecimal.ZERO;
        if(results.get(0)!=null){
            result = new BigDecimal (results.get(0).toString());
        }
        return result;
    }


	public BigDecimal getTotalPesoCredit(TradeServiceId tradeServiceId){

        Session session = this.sessionFactory.getCurrentSession();
        
        return (BigDecimal) session.createQuery("select SUM(aae.pesoAmount) from com.ucpb.tfs.domain.accounting.AccountingEntryActual aae where aae.entryType='Credit' AND aae.tradeServiceId= ?")
        		.setString(0,tradeServiceId.toString()).uniqueResult();
//       	setString(0, tradeServiceId.toString()).uniqueResult();

        		

//        Session session = this.sessionFactory.getCurrentSession();
//
//        Criteria c = session.createCriteria(AccountingEntryActual.class);
//        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));
//        c.add(Restrictions.eq("entryType", "Credit"));
//        c.setProjection(Projections.projectionList().add(Projections.sum("pesoAmount")));
//        List results = c.list();
//        BigDecimal result = BigDecimal.ZERO;
//        if(results.get(0)!=null){
//            result = new BigDecimal (results.get(0).toString());
//        }
//
//        return result;
    }

	@Override
	public void updateIsPosted(String tradeServiceId, Boolean isPosted) {
		
		this.sessionFactory.getCurrentSession().createSQLQuery("UPDATE INT_ACCENTRYACTUAL SET ISPOSTED=? WHERE TRADESERVICEID=?")
        .setParameter(0, isPosted)
        .setParameter(1, tradeServiceId)
        .executeUpdate();
	}

	@Override
	public void updateIsPostedTrue(Boolean isPostedValue) {
		this.sessionFactory.getCurrentSession().createSQLQuery("UPDATE INT_ACCENTRYACTUAL SET ISPOSTED=?")
		.setParameter(0, isPostedValue)
		.executeUpdate();
		
	}
	
	@Override
	public void updateWithError(String tradeServiceId) {
		this.sessionFactory.getCurrentSession().createSQLQuery("UPDATE INT_ACCENTRYACTUAL SET WITHERROR='NOT_BALANCE' WHERE TRADESERVICEID=?")
		.setParameter(0, tradeServiceId)
		.executeUpdate();
		
	}

	@Override
	public BigDecimal getAllTotalOrigDebit(String postingDate) {
		
		return (BigDecimal)	this.sessionFactory.getCurrentSession().createSQLQuery("SELECT SUM(ORIGINALAMOUNT) FROM INT_ACCENTRYACTUAL " +
					 "WHERE UCASE(ENTRYTYPE) = UCASE('Debit') AND DAYS(EFFECTIVEDATE) = DAYS(CAST(? AS TIMESTAMP)) " +
					 "AND STATUS IN ('APPROVED','PRE_APPROVED','POST_APPROVED','POSTED', 'EXPIRED','REINSTATED') " +
					 "AND TRADESERVICEID NOT IN (SELECT GLSUB.TRADESERVICEID FROM INT_ACCENTRYACTUAL GLSUB WHERE (GLSUB.WITHERROR IS NOT NULL AND (LENGTH(TRIM(GLSUB.WITHERROR)) > 0)))")
					 .setParameter(0, postingDate)
					 .uniqueResult();
		
	}

	@Override
	public BigDecimal getAllTotalOrigCredit(String postingDate) {
		
		return (BigDecimal)	this.sessionFactory.getCurrentSession().createSQLQuery("SELECT SUM(ORIGINALAMOUNT) FROM INT_ACCENTRYACTUAL " +
				 "WHERE UCASE(ENTRYTYPE) = UCASE('Credit') AND DAYS(EFFECTIVEDATE) = DAYS(CAST(? AS TIMESTAMP)) " +
				 "AND STATUS IN ('APPROVED','PRE_APPROVED','POST_APPROVED','POSTED', 'EXPIRED','REINSTATED') " +
				 "AND TRADESERVICEID NOT IN (SELECT GLSUB.TRADESERVICEID FROM INT_ACCENTRYACTUAL GLSUB WHERE (GLSUB.WITHERROR IS NOT NULL AND (LENGTH(TRIM(GLSUB.WITHERROR)) > 0)))")
				 .setParameter(0, postingDate)
				 .uniqueResult();

	}

	@Override
	public BigDecimal getAllTotalPesoDebit(String postingDate) {
		
		return (BigDecimal)	this.sessionFactory.getCurrentSession().createSQLQuery("SELECT SUM(PESOAMOUNT) FROM INT_ACCENTRYACTUAL " +
				 "WHERE UCASE(ENTRYTYPE) = UCASE('Debit') AND DAYS(EFFECTIVEDATE) = DAYS(CAST(? AS TIMESTAMP)) " +
				 "AND STATUS IN ('APPROVED','PRE_APPROVED','POST_APPROVED','POSTED', 'EXPIRED','REINSTATED') " +
				 "AND TRADESERVICEID NOT IN (SELECT GLSUB.TRADESERVICEID FROM INT_ACCENTRYACTUAL GLSUB WHERE (GLSUB.WITHERROR IS NOT NULL AND (LENGTH(TRIM(GLSUB.WITHERROR)) > 0)))")
				 .setParameter(0, postingDate)
				 .uniqueResult();

	}

	@Override
	public BigDecimal getAllTotalPesoCredit(String postingDate) {
		
		return (BigDecimal)	this.sessionFactory.getCurrentSession().createSQLQuery("SELECT SUM(PESOAMOUNT) FROM INT_ACCENTRYACTUAL " +
				 "WHERE UCASE(ENTRYTYPE) = UCASE('Credit') AND DAYS(EFFECTIVEDATE) = DAYS(CAST(? AS TIMESTAMP)) " +
				 "AND STATUS IN ('APPROVED','PRE_APPROVED','POST_APPROVED','POSTED', 'EXPIRED','REINSTATED') " +
				 "AND TRADESERVICEID NOT IN (SELECT GLSUB.TRADESERVICEID FROM INT_ACCENTRYACTUAL GLSUB WHERE (GLSUB.WITHERROR IS NOT NULL AND (LENGTH(TRIM(GLSUB.WITHERROR)) > 0)))")
				 .setParameter(0, postingDate)
				 .uniqueResult();
		
	}

    @Override
    public List<GlMapping> getGlMapping(String tradeServiceId) {
        List<GlMapping> mappings = new ArrayList<GlMapping>();

        Iterator it = this.sessionFactory.getCurrentSession().createSQLQuery(
            "SELECT A.ACCOUNTINGCODE, A.BOOKCODE, A.BOOKCURRENCY" +
                ", A.LBP_ACCOUNTINGCODE, A.LBP_PARTICULARS " +
                "FROM TFSDB2S.REF_GLMAPPING A" +
                ", INT_ACCENTRYACTUAL B " +
            "WHERE A.ACCOUNTINGCODE = B.ACCOUNTINGCODE " +
                "AND A.BOOKCODE = B.BOOKCODE " +
                "AND A.BOOKCURRENCY = B.BOOKCURRENCY " +
                "AND B.TRADESERVICEID = ?")
             .setParameter(0, tradeServiceId).list().iterator();

        GlMapping mapping;
        Object[] obj;
        while (it.hasNext()) {
            obj = (Object[]) it.next();
            mapping = new GlMapping();
            mapping.setAccountingCode((String) obj[0]);
            mapping.setBookCode((String) obj[1]);
            mapping.setBookCurrency((String) obj[2]);
            mapping.setLbpAccountingCode((String) obj[3]);
            mapping.setLbpParticulars((String) obj[4]);
            mappings.add(mapping);
        }

        return mappings;
    }
}
