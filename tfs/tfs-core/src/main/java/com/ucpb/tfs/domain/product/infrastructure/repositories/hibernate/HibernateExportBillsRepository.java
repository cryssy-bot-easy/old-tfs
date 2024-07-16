package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.ExportBills;
import com.ucpb.tfs.domain.product.ExportBillsRepository;
import com.ucpb.tfs.domain.product.LcDetails;
import com.ucpb.tfs.domain.product.NonLcDetails;
import com.ucpb.tfs.domain.product.enums.ExportBillType;
import com.ucpb.tfs.domain.product.enums.TradeProductStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/20/13
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: 
	SCR/ER Description: Redmine #4118 - If with outstanding EBC is tagged as Yes, the drop down lists of EBC document numbers 
	are not complete. Example: Document number 909-11-307-17-00004-2 is not included in the list but it should be part of the 
	drop down list since this is an approved EBC Nego and it is still outstanding.
	[Revised by:] John Patrick C. Bautista
	[Date Deployed:] 06/16/2017
	Program [Revision] Details: Added new method to query from Export Bills without the BP Currency restriction.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: HibernateExportBillsRepository
 */

@Transactional
public class HibernateExportBillsRepository implements ExportBillsRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public ExportBills load(DocumentNumber documentNumber) {
        return (ExportBills) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.product.ExportBills where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
    }
    
    @Override
    public List<ExportBills> loadByNegotiationNumber(DocumentNumber documentNumber) {
    	System.out.println("Loading DocumentNumeber: " + documentNumber);    	
        return (List<ExportBills>) mySessionFactory.getCurrentSession().
        		createQuery("from com.ucpb.tfs.domain.product.ExportBills where negotiationNumber = :documentNumber").setParameter("documentNumber", documentNumber).list();
    }

    @Override
    public List<ExportBills> getAllExportBills(
            DocumentNumber documentNumber,
            String clientName,
            String corresBankCode,
            String transaction,
            String transactionType,
            String status,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            String currency,
            String unitCode,
            String unitcode
    ) {

        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ExportBills.class);

        if(documentNumber != null) {
            crit.add(Restrictions.ilike("documentNumber.documentNumber", "%" + documentNumber.toString() + "%"));
        }

        if(clientName != null) {
            crit.add(Restrictions.ilike("cifName", "%" + clientName + "%"));
        }

        if(corresBankCode != null) {
            crit.add(Restrictions.ilike("corresBankCode", "%" + corresBankCode + "%"));
        }

//        if(transaction != null) {
//            crit.add(Restrictions.eq("exportBillsType", transaction));
//        }

        if(transactionType != null) {
            crit.add(Restrictions.eq("exportBillType", ExportBillType.valueOf(transactionType)));
        }

        if(status != null) {
            crit.add(Restrictions.eq("status", TradeProductStatus.valueOf(status)));
        }
        
        if((amountFrom != null && amountFrom.compareTo(new BigDecimal("0")) > 0) &&
        		(amountTo != null && amountTo.compareTo(new BigDecimal("0")) > 0)) {
        	crit.add(Restrictions.between("amount", amountFrom, amountTo));
        }else if((amountFrom != null && amountTo == null) && amountFrom.compareTo(new BigDecimal("0")) > 0){
        	crit.add(Restrictions.ge("amount", amountFrom));
        }else if((amountTo != null && amountFrom == null) && amountTo.compareTo(new BigDecimal("0")) > 0){
        	crit.add(Restrictions.le("amount", amountTo));
        }

        if(currency != null) {
        	crit.add(Restrictions.eq("currency", java.util.Currency.getInstance(currency)));
        }
        
        if(unitCode != null) {
        	crit.add(Restrictions.eq("ccbdBranchUnitCode", unitCode));
        }
        else if(unitcode != null && !unitcode.equals("909")) {
        	crit.add(Restrictions.eq("ccbdBranchUnitCode", unitcode));
        }
        
        crit.addOrder(Order.desc("processDate"));
        
//        crit.add(Restrictions.isNull("negotiationNumber"));

        List critList = crit.list();
        
        List<ExportBills> exportBillsList = new ArrayList<ExportBills>();

        if (!crit.list().isEmpty()) {
            for(int ctr = 0; ctr < critList.size(); ctr ++) {
                ExportBills exportBills = (ExportBills) critList.get(ctr);
                
                Hibernate.initialize(exportBills.getDocumentsEnclosed());
                Hibernate.initialize(exportBills.getEnclosedInstructions());

                exportBillsList.add(exportBills);
            }
        }

        return exportBillsList;
    }

    public List<ExportBills> getAllExportBillsByCifNumber(String cifNumber, String exportBillType) {
        Session session = this.mySessionFactory.getCurrentSession();
        System.out.println("exportBillType : " + ExportBillType.valueOf(exportBillType));
        Criteria crit = session.createCriteria(ExportBills.class);
        crit.add(Restrictions.eq("cifNumber", cifNumber));
//        crit.add(Restrictions.or(
//        		Restrictions.eq("exportBillType", ExportBillType.EBC),
//        		Restrictions.eq("exportBillType", ExportBillType.DBC)
//        		));
        // can only return Bills for Collection with no existing Bills for Purchase
        crit.add(Restrictions.isNull("bpCurrency"));
        // can only return Bills for Collection with the same documentType
        crit.add(Restrictions.eq("exportBillType", ExportBillType.valueOf(exportBillType)));
        // can only return Bills for Collection that are still negotiated
        crit.add(Restrictions.eq("status", TradeProductStatus.NEGOTIATED));

        return crit.list();
    }
    
    // 01242017 - Redmine 4118: Remove restriction on BP Currency
    public List<ExportBills> getAllExportBillsByCifNumberNoRestrictionOnBpCurrency(String cifNumber, String exportBillType) {
        Session session = this.mySessionFactory.getCurrentSession();
        System.out.println("exportBillType : " + ExportBillType.valueOf(exportBillType));
        Criteria crit = session.createCriteria(ExportBills.class);
        crit.add(Restrictions.eq("cifNumber", cifNumber));
//        crit.add(Restrictions.or(
//        		Restrictions.eq("exportBillType", ExportBillType.EBC),
//        		Restrictions.eq("exportBillType", ExportBillType.DBC)
//        		));
        // can only return Bills for Collection with no existing Bills for Purchase
        // TODO: remove restriction if EBC must have no existing EBP
//        crit.add(Restrictions.isNull("bpCurrency"));
        // can only return Bills for Collection with the same documentType
        crit.add(Restrictions.eq("exportBillType", ExportBillType.valueOf(exportBillType)));
        // can only return Bills for Collection that are still negotiated
        crit.add(Restrictions.eq("status", TradeProductStatus.NEGOTIATED));

        return crit.list();
    }
    
    public List<ExportBills> retrieveAllExportBills(String exportBillType) {
        Session session = this.mySessionFactory.getCurrentSession();
        System.out.println("exportBillType : " + ExportBillType.valueOf(exportBillType));
        Criteria crit = session.createCriteria(ExportBills.class);
//        crit.add(Restrictions.or(
//        		Restrictions.eq("exportBillType", ExportBillType.EBC),
//        		Restrictions.eq("exportBillType", ExportBillType.DBC)
//        		));
        // can only return Bills for Collection with no existing Bills for Purchase
        crit.add(Restrictions.isNull("bpCurrency"));
        // can only return Bills for Collection with the same documentType
        crit.add(Restrictions.eq("exportBillType", ExportBillType.valueOf(exportBillType)));
        // can only return Bills for Collection that are still negotiated
        crit.add(Restrictions.eq("status", TradeProductStatus.NEGOTIATED));

        return crit.list();
    }

    public List<ExportBills> getAllExportBillsByNegotiationNumber(DocumentNumber negotiationNumber) {
        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ExportBills.class);

        crit.add(Restrictions.eq("negotiationNumber", negotiationNumber));

        Restrictions.or(
            Restrictions.eq("exportBillType", ExportBillType.EBP),
            Restrictions.eq("exportBillType", ExportBillType.DBP)
        );

        return crit.list();
    }

	@Override
	public Map<String, Object> loadToMapLcDetails(DocumentNumber documentNumber) {
		ExportBills exportBills = this.load(documentNumber);
		LcDetails lcDetails = exportBills.getLcDetails();
		
		if(lcDetails != null) {
            // eagerly load all references
            Hibernate.initialize(lcDetails);

            // we cannot return the list so we use gson to serialize then deserialize back to a list
            Gson gson = new Gson();
            Map returnClass = gson.fromJson(gson.toJson(lcDetails), Map.class);

            return returnClass;
        }
        else {
            return null;
        }
	}

	@Override
	public Map<String, Object> loadToMapNonLcDetails(DocumentNumber documentNumber) {
		ExportBills exportBills = this.load(documentNumber);
		NonLcDetails nonLcDetails = exportBills.getNonLcDetails();
		
		if(nonLcDetails != null) {
            // eagerly load all references
            Hibernate.initialize(nonLcDetails);

            // we cannot return the list so we use gson to serialize then deserialize back to a list
            Gson gson = new Gson();
            Map returnClass = gson.fromJson(gson.toJson(nonLcDetails), Map.class);

            return returnClass;
        }
        else {
            return null;
        }
	}
}
