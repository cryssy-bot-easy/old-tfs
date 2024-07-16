package com.ucpb.tfs.domain.accounting.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.accounting.AccountingEntry;
import com.ucpb.tfs.domain.accounting.AccountingEntryRepository;
import com.ucpb.tfs.domain.accounting.AccountingEventTransaction;
import com.ucpb.tfs.domain.accounting.AccountingEventTransactionId;
import com.ucpb.tfs.domain.accounting.enumTypes.AccountingEntryType;
import com.ucpb.tfs.domain.accounting.enumTypes.BookCurrency;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

/**
 * User: giancarlo
 * Date: 10/5/12
 * Time: 5:56 PM
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateAccountingEntryRepository implements AccountingEntryRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;


    @Override
    public void save(AccountingEntry accountingEntry) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(accountingEntry);
    }

    @Override
    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId) {

        System.out.println("in public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId) ");
        System.out.println("productId:"+productId);
        System.out.println("serviceType:"+serviceType);
        System.out.println("accountingEventTransactionId:"+accountingEventTransactionId.getAccountingEventTransactionId());

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));
        c.add(Restrictions.eq("accountingEventTransactionId", accountingEventTransactionId));
        c.addOrder(Order.asc("id"));

        List<AccountingEntry> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }

    }

    @Override
    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType) {

        System.out.println("in public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType) ");
        System.out.println("productId:"+productId);
        System.out.println("serviceType:"+serviceType);


        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.eq("productId", productId));
        c.add(Restrictions.eq("serviceType", serviceType));
        c.addOrder(Order.asc("id"));

        List<AccountingEntry> results = c.list();

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }

    }

    @Override
    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, BookCurrency originalCurrency, BookCurrency settlementCurrency) {
        System.out.println("in HibernateAccountingEntryRepository");
        System.out.println("productId:"+productId);
        System.out.println("serviceType:"+serviceType);
        System.out.println("originalCurrency:"+originalCurrency);
        System.out.println("settlementCurrency:"+settlementCurrency);

        Session session = this.sessionFactory.getCurrentSession();

        List<AccountingEntry> results = new ArrayList<AccountingEntry>();

        if(!productId.toString().contains("CASH")){
            System.out.println("not CASH");
            // use criteria since we may have null values for some fields
            Criteria c = session.createCriteria(AccountingEntry.class);
            c.add(Restrictions.eq("productId", productId));
            c.add(Restrictions.eq("serviceType", serviceType));
            c.addOrder(Order.asc("id"));
            List<AccountingEntry> results1 = c.list();
            System.out.println("size result1:"+results1.size());
            results = results1;
        } else {
            System.out.println("WITH CASH");
            Criteria c02 = session.createCriteria(AccountingEntry.class);
            c02.add(Restrictions.eq("productId", productId));
            c02.add(Restrictions.eq("serviceType", serviceType));
            c02.add(Restrictions.eq("lcCurrency", originalCurrency));
            c02.add(Restrictions.eq("settlementCurrency", settlementCurrency));
            c02.addOrder(Order.asc("id"));
            List<AccountingEntry> results2 = c02.list();
            System.out.println("size result2:"+results2.size());
            results = results2;
        }


        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }

    }

    @Override
    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency) {
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("in public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency) ");
        System.out.println("productId:"+productId);
        System.out.println("serviceType:"+serviceType);
        System.out.println("accountingEventTransactionId:"+accountingEventTransactionId.getAccountingEventTransactionId());
        System.out.println("originalCurrency:"+originalCurrency);
        System.out.println("settlementCurrency:"+settlementCurrency);

        List<AccountingEntry> results = new ArrayList<AccountingEntry>();

        if(!productId.toString().contains("CASH")){
            System.out.println("not CASH");
            // use criteria since we may have null values for some fields
            Criteria c = session.createCriteria(AccountingEntry.class);
            c.add(Restrictions.eq("productId", productId));
            c.add(Restrictions.eq("serviceType", serviceType));
            c.add(Restrictions.eq("accountingEventTransactionId", accountingEventTransactionId));
            c.addOrder(Order.asc("id"));
            List<AccountingEntry> results1 = c.list();
            System.out.println("size result1:"+results1.size());
            results = results1;
        } else {
            System.out.println("WITH CASH");
            Criteria c = session.createCriteria(AccountingEntry.class);
            c.add(Restrictions.eq("productId", productId));
            c.add(Restrictions.eq("serviceType", serviceType));
            c.add(Restrictions.eq("accountingEventTransactionId", accountingEventTransactionId));
            c.add(Restrictions.eq("lcCurrency", originalCurrency));
            c.add(Restrictions.eq("settlementCurrency", settlementCurrency));
            c.addOrder(Order.asc("id"));
            List<AccountingEntry> results2 = c.list();
            System.out.println("size result2:"+results2.size());
            results = results2;
        }



        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }
    }


    @Override
    public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency, AccountingEntryType accountingEntryType) {
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("in public List<AccountingEntry> getEntries(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency, AccountingEntryType accountingEntryType)");
        System.out.println("productId:"+productId);
        System.out.println("serviceType:"+serviceType);
        System.out.println("accountingEventTransactionId:"+accountingEventTransactionId.getAccountingEventTransactionId());
        System.out.println("originalCurrency:"+originalCurrency);
        System.out.println("settlementCurrency:"+settlementCurrency);
        System.out.println("accountingEntryType:"+accountingEntryType);

        List<AccountingEntry> results = new ArrayList<AccountingEntry>();

        if(!productId.toString().contains("CASH")){
            System.out.println("not CASH");
            // use criteria since we may have null values for some fields
            Criteria c = session.createCriteria(AccountingEntry.class);
            c.add(Restrictions.eq("productId", productId));
            c.add(Restrictions.eq("serviceType", serviceType));
            c.add(Restrictions.eq("accountingEventTransactionId", accountingEventTransactionId));
            c.add(Restrictions.eq("lcCurrency", originalCurrency));
            c.add(Restrictions.eq("settlementCurrency", settlementCurrency));
            c.add(Restrictions.eq("accountingEntryType", accountingEntryType));
            c.addOrder(Order.asc("id"));
            List<AccountingEntry> results1 = c.list();
            System.out.println("size result1:"+results1.size());
            results = results1;
        } else {
            System.out.println("WITH CASH");
            Criteria c = session.createCriteria(AccountingEntry.class);
            c.add(Restrictions.eq("productId", productId));
            c.add(Restrictions.eq("serviceType", serviceType));
            c.add(Restrictions.eq("accountingEventTransactionId", accountingEventTransactionId));
            c.add(Restrictions.eq("lcCurrency", originalCurrency));
            c.add(Restrictions.eq("settlementCurrency", settlementCurrency));
            c.add(Restrictions.eq("accountingEntryType", accountingEntryType));
            c.addOrder(Order.asc("id"));
            List<AccountingEntry> results2 = c.list();
            System.out.println("size result2:"+results2.size());
            results = results2;
        }


        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }
    }

    @Override
    public List<AccountingEntry> getEntries(AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency, AccountingEntryType accountingEntryType) {
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("in public List<AccountingEntry> getEntries(AccountingEventTransactionId accountingEventTransactionId, BookCurrency originalCurrency, BookCurrency settlementCurrency, AccountingEntryType accountingEntryType)");
        System.out.println("accountingEventTransactionId:" + accountingEventTransactionId.getAccountingEventTransactionId());
        System.out.println("originalCurrency:" + originalCurrency);
        System.out.println("settlementCurrency:" + settlementCurrency);
        System.out.println("accountingEntryType:" + accountingEntryType);

        List<AccountingEntry> results = new ArrayList<AccountingEntry>();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.eq("accountingEventTransactionId", accountingEventTransactionId));
        c.add(Restrictions.eq("lcCurrency", originalCurrency));
        c.add(Restrictions.eq("settlementCurrency", settlementCurrency));
        c.add(Restrictions.eq("accountingEntryType", accountingEntryType));
        c.addOrder(Order.asc("id"));
        List<AccountingEntry> results1 = c.list();
        System.out.println("size result1:" + results1.size());
        results = results1;


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

        return ((Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.accounting.AccountingEntry").iterate().next()).longValue();

    }


    @Override
    public void clear(){

        Session session = this.sessionFactory.getCurrentSession();

        Query qry = session.createQuery("DELETE from com.ucpb.tfs.domain.accounting.AccountingEntry accEn");
        int tempint = qry.executeUpdate();
        System.out.println("cleared AccountingEntry:"+tempint);
    }

    @Override
    public HashMap<String,String> getEntriesAll() {
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("in public List<AccountingEntry> getEntriesAll()");
        HashMap<String,String> results = new HashMap<String,String>();

        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.isNotNull("productId"));
        c.setProjection(Projections.projectionList()
                .add(Projections.property("accountingCode"), "accountingCode")
                .add(Projections.property("formulaParticulars"), "formulaParticulars"));

        List<Object> results1 = c.list();
        System.out.println("size result1:" + results1.size());
        for(Object r: results1){
            Object[] row = (Object[]) r;
            results.put(row[0].toString(),row[1].toString());
            //System.out.println(":::::::"+row[0].toString()+"--"+row[1].toString());
        }

        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }
    }

    @Override
    public List<String> getEntriesAllForChecking() {
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("in public List<AccountingEntry> getEntriesAll()");

        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.isNotNull("productId"));
        c.setProjection(Projections.distinct(Projections.projectionList()
                .add(Projections.property("accountingCode"), "accountingCode")));
        List<String> results = c.list();
        System.out.println("size result:" + results.size());


        if (results.size() > 0) {
            //System.out.println("i found something");
            return results;
        } else {
            //System.out.println("i found nothing");
            return null;
        }
    }


    @Override
    public void updateAccountingCode(long id,String code){
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("in public void updateAccountingCode(long id,String code)");
        HashMap<String,String> results = new HashMap<String,String>();

        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.eq("id", id));
        List tempList = c.list();
        if(tempList!=null){
            if(tempList.get(0)!=null && tempList.get(0).getClass().equals(AccountingEntry.class)){
                System.out.println("YEAH GOLS");
                AccountingEntry accountingEntry = (AccountingEntry)tempList.get(0);
                accountingEntry.setAccountingCode(code);
                session.merge(accountingEntry);
            } else {
                System.out.println("accountingEntry with id "+id+" not found.");
            }
        }
    }

    @Override
    public void updateFormulaValue(long id,String code){
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("in public List<AccountingEntry> getEntriesAll()");
        HashMap<String,String> results = new HashMap<String,String>();

        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.eq("id", id));
        List tempList = c.list();
        if(tempList!=null){
            if(tempList.get(0)!=null && tempList.get(0).getClass().equals(AccountingEntry.class)){
                System.out.println("YEAH GOLS");
                AccountingEntry accountingEntry = (AccountingEntry)tempList.get(0);
                accountingEntry.setFormulaValue(code);
                session.merge(accountingEntry);
            } else {
                System.out.println("accountingEntry with id "+id+" not found.");
            }
        }
    }

    @Override
    public void updateFormulaPesoValue(long id,String code){
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("in public List<AccountingEntry> getEntriesAll()");
        HashMap<String,String> results = new HashMap<String,String>();

        Criteria c = session.createCriteria(AccountingEntry.class);
        c.add(Restrictions.eq("id", id));
        List tempList = c.list();
        if(tempList!=null){
            if(tempList.get(0)!=null && tempList.get(0).getClass().equals(AccountingEntry.class)){
                System.out.println("YEAH GOLS");
                AccountingEntry accountingEntry = (AccountingEntry)tempList.get(0);
                accountingEntry.setFormulaPesoValue(code);
                session.merge(accountingEntry);
            } else {
                System.out.println("accountingEntry with id "+id+" not found.");
            }
        }
    }

    @Override
    public void updateAccounting(String line){
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("updateAccounting(String line)");
        System.out.println("updateAccounting line:"+line);
        Query query = session.createSQLQuery(line.replace(";",""));
        int i = query.executeUpdate();
        System.out.println("i:"+i);


    }
}
