package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.*;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: giancarlo
 * Date: 10/11/12
 * Time: 12:11 PM
 */

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateAccountingEntryActualVariablesRepository implements AccountingEntryActualVariablesRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public void save(AccountingEntryActualVariables accountingEntryActualVariables) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(accountingEntryActualVariables);
    }

    @Override
    @Transactional
    public List<AccountingEntryActualVariables> getEntries(TradeServiceId tradeServiceId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActualVariables.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));

        List<AccountingEntryActualVariables> results = c.list();

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
    public List<AccountingEntryActualVariables> getTransactionEntries(TradeServiceId tradeServiceId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActualVariables.class);
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

        List<AccountingEntryActualVariables> results = c.list();

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
    public List<AccountingEntryActualVariables> getPaymentEntries(TradeServiceId tradeServiceId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActualVariables.class);
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));
//        c.add(Restrictions.sqlRestriction(" accEvtRanId like '%PAYMENT%'"));
        c.add(Restrictions.not(Restrictions.sqlRestriction(" (accEvtRanId like '%CL%' or accEvtRanId like '%SET-UP%' or accEvtRanId like '%CONTINGENT%')")));

        List<AccountingEntryActualVariables> results = c.list();

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
    public List<AccountingEntryActualVariables> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransaction accountingEventTransaction, TradeServiceId tradeServiceId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntryActualVariables.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));
        c.add(Restrictions.eq("accountingEventTransaction", accountingEventTransaction));
        c.add(Restrictions.eq("tradeServiceId", tradeServiceId));

        List<AccountingEntryActualVariables> results = c.list();

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

        return ((Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.accounting.AccountingEntryActualVariables").iterate().next()).longValue(); //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional
    public void clear() {

        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.accounting.AccountingEntryActualVariables aea");
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

        Criteria c = session.createCriteria(AccountingEntryActualVariables.class);
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

        Criteria c = session.createCriteria(AccountingEntryActualVariables.class);
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

        Query qry = session.createQuery("select SUM(aae.pesoAmount) from com.ucpb.tfs.domain.accounting.AccountingEntryActualVariables aae where aae.entryType='Credit' AND aae.tradeServiceId= ?");
        qry.setString(0,tradeServiceId.toString());
        List results = qry.list();
        BigDecimal result = BigDecimal.ZERO;
        if(results.get(0)!=null){
            result = new BigDecimal (results.get(0).toString());
        }

        return result;


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
}
