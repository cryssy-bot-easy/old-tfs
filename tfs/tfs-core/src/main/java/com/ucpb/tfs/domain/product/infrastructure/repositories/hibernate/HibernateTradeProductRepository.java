package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.product.*;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: IPCVal
 * Date: 8/6/12
 */
@Transactional
public class HibernateTradeProductRepository implements TradeProductRepository {

	/**
		PROLOGUE:
		(revision)
		SCR/ER Number: ER# 20160620-066 
		SCR/ER Description: Discrepancy under gl code 561201680000 which is not included in the TF Alloc and TFS exception report of May 2016 but reflected in GL RM4105
		[Revised by:] Lymuel Arrome Saul
		[Date revised:] 06/16/2016
		Program [Revision] Details: Removed the condition in the select query which replaces the dash with blanks and exact match only when searching for a document number.
		Date deployment: 6/17/2016
		Member Type: JAVA
		Project: CORE
		Project Name: HibernateTradeProductRepository.java
	*/

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    public void persist(TradeProduct tradeProduct) {
        if (tradeProduct instanceof LetterOfCredit) {        	
        	if (((LetterOfCredit) tradeProduct).isLastModifiedUpdated()==false) {
                ((LetterOfCredit) tradeProduct).updateLastModifiedDate();
        	}
        }
        System.out.println("DOCUMENT_NUMBER::"+tradeProduct.getDocumentNumber());
        System.out.println("ALLOCATION_UNIT_CODE::"+tradeProduct.getAllocationUnitCode());
        System.out.println("AMOUNT::"+tradeProduct.getAmount());
        System.out.println("CIF_NAME::"+tradeProduct.getCifName());
        System.out.println("CIF_NUMBER::"+tradeProduct.getCifNumber());
        System.out.println("CURRENCY::"+tradeProduct.getCurrency());
        System.out.println("FACILITY_ID::"+tradeProduct.getFacilityId());
        System.out.println("FACILITY_REFERENCE_NUMBER::"+tradeProduct.getFacilityReferenceNumber());
        System.out.println("FACILITY_TYPE::"+tradeProduct.getFacilityType());
        System.out.println("MAIN_CIF_NAME::"+tradeProduct.getMainCifName());
        System.out.println("MAIN_CIF_NUMBER::"+tradeProduct.getMainCifNumber());
        System.out.println("PRODUCT_TYPE::"+tradeProduct.getProductType());
        System.out.println("STATUS::"+tradeProduct.getStatus());
        System.out.println("CLASS::"+tradeProduct.getClass());
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(tradeProduct);
    }

    @Override
    public void update(TradeProduct tradeProduct) {
        if (tradeProduct instanceof LetterOfCredit) {
        	if (((LetterOfCredit) tradeProduct).isLastModifiedUpdated()==false) {
                ((LetterOfCredit) tradeProduct).updateLastModifiedDate();
        	}
        }
        Session session = this.sessionFactory.getCurrentSession();
        session.update(tradeProduct);
    }

    @Override
    public void merge(TradeProduct tradeProduct) {
        if (tradeProduct instanceof LetterOfCredit) {
        	if (((LetterOfCredit) tradeProduct).isLastModifiedUpdated()==false) {
                ((LetterOfCredit) tradeProduct).updateLastModifiedDate();
        	}
        }
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(tradeProduct);
    }

    @Override
    public void mergeFlush(TradeProduct tradeProduct) {
    	
        try{
            System.out.println("=============TRADEPRODUCT DETAILS=============");
            System.out.println("1: " + tradeProduct.getAllocationUnitCode() + ", 2: "+tradeProduct.getCifName());
            System.out.println("3: " + tradeProduct.getCifNumber() + ", 4: "+tradeProduct.getFacilityId());
            System.out.println("5: " + tradeProduct.getFacilityReferenceNumber() + ", 6: "+tradeProduct.getFacilityType());
            System.out.println("7: " + tradeProduct.getMainCifName() + ", 8: "+tradeProduct.getMainCifNumber());
            System.out.println("9: " + tradeProduct.getProcessingUnitCode() + ", 10: "+tradeProduct.getAmount());
            System.out.println("11: " + tradeProduct.getCurrency() + ", 12: "+tradeProduct.getDocumentNumber());
            System.out.println("13: " + tradeProduct.getPassOnRateThirdToPhp() + ", 14: "+tradeProduct.getPassOnRateThirdToUsd());
            System.out.println("15: " + tradeProduct.getPassOnRateUsdToPhp() + ", 16: "+tradeProduct.getProductType());
            System.out.println("17: " + tradeProduct.getSpecialRateThirdToPhp() + ", 18: "+tradeProduct.getSpecialRateThirdToUsd());
            System.out.println("19: " + tradeProduct.getSpecialRateUsdToPhp() + ", 20: "+tradeProduct.getStatus());
            System.out.println("21: " + tradeProduct.getUrr());
            System.out.println("=============TRADEPRODUCT DETAILS=============");
            }catch(Exception e){
            	System.out.println(e);
            	System.out.println("=============TRADEPRODUCT DETAILS FAILED=============");
            	
            }
        
        try{
        if (tradeProduct instanceof LetterOfCredit) {        	
        	if (((LetterOfCredit) tradeProduct).isLastModifiedUpdated()==false) {
                ((LetterOfCredit) tradeProduct).updateLastModifiedDate();
        	}
            
        }
        }catch(Exception e){
        	System.out.println(new Date() + "date to Cast");
        	System.out.println(e);
        	
        	System.out.println("=============FAILED TO CAST LASTMODIFIEDDATE=============");
        	
        }

//        System.out.println(letterOfCredit.getAdditionalAmountsCovered());
   
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(tradeProduct);
        session.flush();
    }

    @Override
    public TradeProduct load(DocumentNumber documentNumber) {

        return (TradeProduct) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.product.TradeProduct where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
    }
    
    @Override
    public Map load(String documentNumber) {
        System.out.println("documentNumber:"+documentNumber);
        Object[] obj = (Object[]) this.sessionFactory.getCurrentSession().createSQLQuery(
    			"SELECT CIFNUMBER, CIFNAME, ACCOUNTOFFICER, CCBDBRANCHUNITCODE, LONGNAME, " +
    			"ADDRESS1, ADDRESS2 FROM TRADEPRODUCT WHERE DOCUMENTNUMBER = ?")
    			.setParameter(0, documentNumber).uniqueResult();

        System.out.println("obj:"+obj);
        Map<String, Object> map = new HashMap<String, Object>();
        if(obj!=null){
            map.put("cifNumber", obj[0]);
            map.put("cifName", obj[1]);
            map.put("accountOfficer", obj[2]);
            map.put("ccbdBranchUnitCode", obj[3]);
            map.put("longName", obj[4]);
            map.put("address1", obj[5]);
            map.put("address2", obj[6]);
        }
        return map;
    }

    @Override
    public Map loadToMap(DocumentNumber documentNumber) {
        LetterOfCredit tradeProduct = (LetterOfCredit) this.load(documentNumber);

        if (tradeProduct != null) {
            // eagerly load all references
            Hibernate.initialize(tradeProduct);

            // manually set this - does not retrieve the correct value
            // todo: why is this happening?
            //tradeProduct.setLcOutstandingBalance(tradeProduct.getOutstandingBalance());

            // seems to be working now but in any case, updated the procedure of setting lc outstanding balance
            tradeProduct.setOutstandingBalance(tradeProduct.retrieveOutstandingBalance());

            // we cannot return the list so we use gson to serialize then deserialize back to a list
            Gson gson = new Gson();
            Map returnClass = gson.fromJson(gson.toJson(tradeProduct), Map.class);

            return returnClass;
        } else {
            return null;
        }

    }

    @Override
    public String getDocumentNumberSequence(String documentCode, String processingUnitCode, int year) {
        Object sequence = sessionFactory.getCurrentSession().createSQLQuery("SELECT SEQUENCE FROM DOC_NUM_SEQUENCE WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year)
                .uniqueResult();
        return sequence != null ? sequence.toString() : null;
    }

    @Override
    public void incrementDocumentNumberSequence(String documentCode, String processingUnitCode, int year) {
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE DOC_NUM_SEQUENCE SET SEQUENCE = (SEQUENCE + 1) WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year).executeUpdate();
    }

    @Override
    public String getIndemnityNumberSequence(String documentCode, String processingUnitCode, int year) {
        Object sequence = sessionFactory.getCurrentSession().createSQLQuery("SELECT SEQUENCE FROM INDEMNITY_NUM_SEQUENCE WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year)
                .uniqueResult();
        return sequence != null ? sequence.toString() : null;
    }

    @Override
    public void incrementIndemnityNumberSequence(String documentCode, String processingUnitCode, int year) {
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE INDEMNITY_NUM_SEQUENCE SET SEQUENCE = (SEQUENCE + 1) WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year).executeUpdate();
    }

    @Override
    public String getIcNumberSequence(String documentCode, String processingUnitCode, int year) {
        Object sequence = sessionFactory.getCurrentSession().createSQLQuery("SELECT SEQUENCE FROM IC_NUM_SEQUENCE WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year)
                .uniqueResult();
        return sequence != null ? sequence.toString() : null;
    }

    @Override
    public void incrementIcNumberSequence(String documentCode, String processingUnitCode, int year) {
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE IC_NUM_SEQUENCE SET SEQUENCE = (SEQUENCE + 1) WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year).executeUpdate();
    }

    @Override
    public String getNonLcNumberSequence(String documentCode, String processingUnitCode, int year) {
        Object sequence = sessionFactory.getCurrentSession().createSQLQuery("SELECT SEQUENCE FROM NON_LC_NUM_SEQUENCE WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year)
                .uniqueResult();
        return sequence != null ? sequence.toString() : null;
    }

    @Override
    public void incrementNonLcNumberSequence(String documentCode, String processingUnitCode, int year) {
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE NON_LC_NUM_SEQUENCE SET SEQUENCE = (SEQUENCE + 1) WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year).executeUpdate();
    }

    @Override
    public String getNegotiationNumberSequence(String documentCode, String processingUnitCode, int year) {
        Object sequence = sessionFactory.getCurrentSession().createSQLQuery("SELECT SEQUENCE FROM NEGOTIATION_NUM_SEQUENCE WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year)
                .uniqueResult();
        return sequence != null ? sequence.toString() : null;
    }

    @Override
    public void incrementNegotiationNumberSequence(String documentCode, String processingUnitCode, int year) {
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE NEGOTIATION_NUM_SEQUENCE SET SEQUENCE = (SEQUENCE + 1) WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year).executeUpdate();
    }


    @Override
    public String getImportAdvanceSequence(String documentCode, String processingUnitCode, int year) {
        Object sequence = sessionFactory.getCurrentSession().createSQLQuery("SELECT SEQUENCE FROM DOC_NUM_SEQUENCE WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year)
                .uniqueResult();
        return sequence != null ? sequence.toString() : null;
    }

    @Override
    public void incrementImportAdvanceNumberSequence(String documentCode, String processingUnitCode, int year) {
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE DOC_NUM_SEQUENCE SET SEQUENCE = (SEQUENCE + 1) WHERE UNIT_CODE = ? " +
                "and DOCUMENT_TYPE = ? AND SEQUENCE_YEAR = ?")
                .setParameter(0, processingUnitCode)
                .setParameter(1, documentCode)
                .setParameter(2, year).executeUpdate();
    }

    @Override
    public String getSettlementAccountSequence(String documentCode) {
        Object sequence = sessionFactory.getCurrentSession().createSQLQuery("SELECT SEQUENCE FROM DOC_NUM_SEQUENCE WHERE DOCUMENT_TYPE = :documentCode")
                .setParameter("documentCode", documentCode).uniqueResult();
        return sequence != null ? sequence.toString() : null;
    }

    @Override
    public void incrementSettlementAccountNumber(String settlementAccount) {
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE DOC_NUM_SEQUENCE SET SEQUENCE = (SEQUENCE + 1) WHERE DOCUMENT_TYPE = :settlementAccount")
                .setParameter("settlementAccount", settlementAccount).executeUpdate();
    }

    @Override
    public Map loadToMapExportAdvising(DocumentNumber documentNumber) {
        ExportAdvising tradeProduct = (ExportAdvising) this.load(documentNumber);

        if (tradeProduct != null) {
            // eagerly load all references
            Hibernate.initialize(tradeProduct);

            // we cannot return the list so we use gson to serialize then deserialize back to a list
            Gson gson = new Gson();
            Map returnClass = gson.fromJson(gson.toJson(tradeProduct), Map.class);

            return returnClass;
        } else {
            return null;
        }

    }

    @Override
    public Map loadToMapExportBills(DocumentNumber documentNumber) {
        ExportBills tradeProduct = (ExportBills) this.load(documentNumber);

        if (tradeProduct != null) {
            // eagerly load all references
            Hibernate.initialize(tradeProduct);

            // we cannot return the list so we use gson to serialize then deserialize back to a list
            Gson gson = new Gson();
            Map returnClass = gson.fromJson(gson.toJson(tradeProduct), Map.class);

            return returnClass;
        } else {
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> searchAllImportProducts(String documentNumber, String cifName, String cifNumber, String unitcode) {
        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append("select tp.documentNumber, tp.cifName, ip.outstandingBalance, ip.lastTransaction, tp.productType from TradeProduct tp ");
        // lc
        queryStatement.append("inner join ((select lc.documentnumber, lc.outstandingBalance, lc.lastTransaction from LetterOfCredit lc) " +
        		"union (select da.documentnumber, da.outstandingAmount as outstandingBalnace, da.lastTransaction from DocumentAgainstAcceptance da) " +
        		"union (select oa.documentnumber, oa.outstandingAmount as outstandingBalnace, oa.lastTransaction from OpenAccount oa) " +
        		"union (select dp.documentnumber, dp.outstandingAmount as outstandingBalnace, dp.lastTransaction from DocumentAgainstPayment dp) " +
        		"union (select dr.documentnumber, dr.outstandingAmount as outstandingBalnace, dr.lastTransaction from DirectRemittance dr)) as ip ");
        queryStatement.append("on ip.documentNumber = tp.documentNumber ");

        queryStatement.append("where tp.documentNumber is not null ");

        if (documentNumber != null) {
            queryStatement.append("and tp.documentNumber = '" + documentNumber + "' ");
        }

        if (cifName != null) {
            queryStatement.append("and tp.cifName like '%" + cifName + "%' ");
        }

        if (cifNumber != null) {
            queryStatement.append("and tp.cifNumber = '" + cifNumber + "' ");
        }
        
        if (unitcode != null) {
        	if (!unitcode.equals("909")){
        		queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitcode + "' ");
        	}
        }

        Query query = session.createSQLQuery(queryStatement.toString());

        List<Map<String, Object>> lcList = new ArrayList<Map<String, Object>>();

        Iterator it = query.list().iterator();

        while (it.hasNext()) {
            Object[] obj = (Object[]) it.next();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("documentNumber", obj[0]);
            map.put("cifName", obj[1]);
            map.put("outstandingBalance", obj[2]);
            map.put("lastTransaction", obj[3]);
            map.put("documentClass", obj[4]);
            lcList.add(map);
        }

        return lcList;
    }

    @Override
    public List<Map<String, Object>> findAllImportProducts(String documentNumber, String productType, String cifName, String cifNumber, String unitCode, String unitcode) {
        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append("select tp.documentNumber, tp.productType,  tp.cifName, tp.cifNumber, tp.ccbdBranchUnitCode from TradeProduct tp ");
        // lc
        queryStatement.append("left join LetterOfCredit lc ");
        queryStatement.append("on lc.documentNumber = tp.documentNumber ");
        queryStatement.append("and tp.productType = 'LC' ");

        // bgbe
        queryStatement.append("left join Indemnity bgbe ");
        queryStatement.append("on bgbe.indemnityNumber = tp.documentNumber ");
        queryStatement.append("and tp.productType = 'INDEMNITY' ");

        // da
        queryStatement.append("left join DocumentAgainstAcceptance da ");
        queryStatement.append("on da.documentNumber = tp.documentNumber ");
        queryStatement.append("and tp.productType = 'DA' ");

        // dp
        queryStatement.append("left join DocumentAgainstPayment dp ");
        queryStatement.append("on dp.documentNumber = tp.documentNumber ");
        queryStatement.append("and tp.productType = 'DP' ");

        // dr
        queryStatement.append("left join DirectRemittance dr ");
        queryStatement.append("on dr.documentNumber = tp.documentNumber ");
        queryStatement.append("and tp.productType = 'DR' ");

        // oa
        queryStatement.append("left join OpenAccount oa ");
        queryStatement.append("on oa.documentNumber = tp.documentNumber ");
        queryStatement.append("and tp.productType = 'OA' ");

        queryStatement.append("where tp.productType not in ('EXPORT_BILLS', 'EXPORT_ADVISING', 'IADVPAYMENT', 'IADVREFUND', 'BP', 'BC', 'REBATE') ");

        if (documentNumber != null) {
            queryStatement.append("and tp.documentNumber = '" + documentNumber + "' ");
        }
        
        if (cifName != null) {
            queryStatement.append("and tp.cifName like '%" + cifName + "%' ");
        }

        if (cifNumber != null) {
            queryStatement.append("and tp.cifNumber = '" + cifNumber + "' ");
        }
        
        if (unitCode != null) {
        	queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitCode + "'");
        }
        
        if (unitcode != null && !unitcode.equals("909")) {
        	queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitcode + "'");
        }

        Query query = session.createSQLQuery(queryStatement.toString());

        List<Map<String, Object>> allImportsList = new ArrayList<Map<String, Object>>();

        Iterator it = query.list().iterator();

        while (it.hasNext()) {
            Object[] obj = (Object[]) it.next();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("documentNumber", obj[0]);
            map.put("productType", obj[1]);
            map.put("cifName", obj[2]);
            map.put("cifNumber", obj[3]);
            map.put("unitCode", obj[4]);
            allImportsList.add(map);
        }

        return allImportsList;
    }

    @Override
    public List<Map<String, Object>> findAllExportProducts(String documentNumber,
    													   String cifName,
                                                           String importersName,
                                                           String exportersName,
                                                           String transaction,
                                                           String unitCode,
                                                           String unitcode) {

        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append("select tp.documentNumber, tp.cifName, tp.cifNumber, tp.productType, tp.amount, ea.exporterName, ea.importerName, eb.buyerName, eb.sellerName, eb.exportBillType, tp.ccbdBranchUnitCode ");
        queryStatement.append("from TradeProduct tp ");

        // export advising
        queryStatement.append("left join ExportAdvising ea ");
        queryStatement.append("on ea.documentNumber = tp.documentNumber ");
        queryStatement.append("and tp.productType = 'EXPORT_ADVISING' ");

        // export bills
        queryStatement.append("left join ExportBills eb ");
        queryStatement.append("on eb.documentNumber = tp.documentNumber ");
        queryStatement.append("and tp.productType = 'EXPORT_BILLS' ");

        queryStatement.append("where tp.productType not in ('LC', 'INDEMNITY', 'DA', 'DP', 'OA', 'DR', 'IADVPAYMENT', 'IADVREFUND', 'REBATE') ");

        if (documentNumber != null) {
            queryStatement.append("and replace(tp.documentNumber,'-','') like replace('%" + documentNumber + "%','-','') ");
        }

        if (cifName != null) {
            queryStatement.append("and tp.cifName like '%" + cifName + "%' ");
        }

        if (transaction != null) {
            if ("EBP".equals(transaction)) {
                queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BP') ");
                queryStatement.append("and eb.exportBillType = 'EBP' ");
            } else if ("DBP".equals(transaction)) {
                queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BP') ");
                queryStatement.append("and eb.exportBillType = 'DBP' ");
            } else if ("EBC".equals(transaction)) {
                queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BC') ");
                queryStatement.append("and eb.exportBillType = 'EBC' ");
            } else if ("DBC".equals(transaction)) {
                queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BC') ");
                queryStatement.append("and eb.exportBillType = 'DBC' ");
            } else if ("EXPORT_ADVISING".equals(transaction)) {
                queryStatement.append("and tp.productType = 'EXPORT_ADVISING' ");
            }
        }

        if (importersName != null) {
            if (transaction != null) {
                if ("EBP".equals(transaction)) {
                    queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BP') ");
                    queryStatement.append("and eb.exportBillType = 'EBP' ");
                    queryStatement.append("and eb.buyerName like '%" + importersName + "%' ");
                } else if ("DBP".equals(transaction)) {
                    queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BP') ");
                    queryStatement.append("and eb.exportBillType = 'DBP' ");
                    queryStatement.append("and eb.buyerName like '%" + importersName + "%' ");
                } else if ("EBC".equals(transaction)) {
                    queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BC') ");
                    queryStatement.append("and eb.exportBillType = 'EBC' ");
                    queryStatement.append("and eb.buyerName like '%" + importersName + "%' ");
                } else if ("DBC".equals(transaction)) {
                    queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BC') ");
                    queryStatement.append("and eb.exportBillType = 'DBC' ");
                    queryStatement.append("and eb.buyerName like '%" + importersName + "%' ");
                } else if ("EXPORT_ADVISING".equals(transaction)) {
                    queryStatement.append("and tp.productType = 'EXPORT_ADVISING' ");
                    queryStatement.append("and ea.importerName like '%" + importersName + "%' ");
                }
            } else {
                queryStatement.append("and (ea.importerName like '%" + importersName + "%' ");
                queryStatement.append("or eb.buyerName like '%" + importersName + "%') ");
            }
        }

        if (exportersName != null) {
            if (transaction != null) {
                if ("EBP".equals(transaction)) {
                    queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BP') ");
                    queryStatement.append("and eb.exportBillType = 'EBP' ");
                    queryStatement.append("and eb.sellerName like '%" + exportersName + "%' ");
                } else if ("DBP".equals(transaction)) {
                    queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BP') ");
                    queryStatement.append("and eb.exportBillType = 'DBP' ");
                    queryStatement.append("and eb.sellerName like '%" + exportersName + "%' ");
                } else if ("EBC".equals(transaction)) {
                    queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BC') ");
                    queryStatement.append("and eb.exportBillType = 'EBC' ");
                    queryStatement.append("and eb.sellerName like '%" + exportersName + "%' ");
                } else if ("DBC".equals(transaction)) {
                    queryStatement.append("and tp.productType in ('EXPORT_BILLS', 'BC') ");
                    queryStatement.append("and eb.exportBillType = 'DBC' ");
                    queryStatement.append("and eb.sellerName like '%" + exportersName + "%' ");
                } else if ("EXPORT_ADVISING".equals(transaction)) {
                    queryStatement.append("and tp.productType = 'EXPORT_ADVISING' ");
                    queryStatement.append("and ea.exporterName like '%" + exportersName + "%' ");
                }
            } else {
                queryStatement.append("and (ea.exporterName like '%" + exportersName + "%' ");
                queryStatement.append("or eb.sellerName like '%" + exportersName + "%') ");
            }
        }
        
        if (unitCode != null) {
        	queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitCode + "'");
        }
        
        if (unitcode != null && !unitcode.equals("909")) {
        	queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitcode + "'");
        }

        Query query = session.createSQLQuery(queryStatement.toString());

        List<Map<String, Object>> allExportsList = new ArrayList<Map<String, Object>>();

        Iterator it = query.list().iterator();

        while (it.hasNext()) {
            Object[] obj = (Object[]) it.next();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("documentNumber", obj[0]);
            map.put("cifName", obj[1]);
            map.put("cifNumber", obj[2]);
            map.put("amount", obj[4]);
            map.put("productType", obj[3]);
            map.put("unitCode", obj[10]);

            String productType = (String) obj[3];

            if ("EXPORT_BILLS".equals(productType)) {
                map.put("importerName", obj[7]);
                map.put("exporterName", obj[8]);
                map.put("productType", obj[9]);
            } else if ("EXPORT_ADVISING".equals(transaction)) {
                map.put("importerName", obj[6]);
                map.put("exporterName", obj[5]);
                map.put("productType", obj[3]);
            }

            allExportsList.add(map);
        }

        return allExportsList;
    }

    public Map<String, Object> getImport(String documentNumber) {
        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append("select tp.documentNumber, tp.amount, tp.currency, tp.cifNumber, tp.cifName, tp.accountOfficer, tp.ccbdBranchUnitCode, tp.longName, tp.address1, tp.address2 from TradeProduct tp ");

        queryStatement.append("where tp.productType not in ('EXPORT_BILLS', 'EXPORT_ADVISING', 'IADVPAYMENT', 'IADVREFUND', 'BP', 'BC', 'REBATE') ");

        queryStatement.append("and tp.documentNumber = '" + documentNumber + "' ");

        Query query = session.createSQLQuery(queryStatement.toString());

        Object[] obj = (Object[]) query.uniqueResult();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("documentNumber", obj[0]);
        map.put("amount", obj[1]);
        map.put("currency", obj[2]);

        map.put("cifNumber", obj[3]);
        map.put("cifName", obj[4]);
        map.put("accountOfficer", obj[5]);
        map.put("ccbdBranchUnitCode", obj[6]);

        map.put("longName", obj[7]);
        map.put("address1", obj[8]);
        map.put("address2", obj[9]);


        return map;
    }

    @Override
    public Map<String, Object> getExport(String documentNumber) {

        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append("select tp.documentNumber, tp.amount, tp.currency, tp.cifNumber, tp.cifName, tp.accountOfficer, tp.ccbdBranchUnitCode, tp.longName, tp.address1, tp.address2  ");
        queryStatement.append("from TradeProduct tp ");

        queryStatement.append("where tp.productType not in ('LC', 'INDEMNITY', 'DA', 'DP', 'OA', 'DR', 'IADVPAYMENT', 'IADVREFUND', 'REBATE') ");

        queryStatement.append("and tp.documentNumber = '" + documentNumber + "' ");

        Query query = session.createSQLQuery(queryStatement.toString());

        Object[] obj = (Object[]) query.uniqueResult();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("documentNumber", obj[0]);
        map.put("amount", obj[1]);
        map.put("currency", obj[2]);

        map.put("cifNumber", obj[3]);
        map.put("cifName", obj[4]);
        map.put("accountOfficer", obj[5]);
        map.put("ccbdBranchUnitCode", obj[6]);

        map.put("longName", obj[7]);
        map.put("address1", obj[8]);
        map.put("address2", obj[9]);

        return map;
    }

    @Override
    public List<String> getTradeProductToBeExpired(String sql, String reportDate, Date lastExpiredDate) {
        System.out.println("getTradeProductToBeExpired");
        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append(sql);
        System.out.println("queryStatement.toString(): " + queryStatement.toString());
        //todo convert reportDate to new Date
        //MM-dd-yyyy
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date rDate = new Date();
        try{
            rDate = simpleDateFormat.parse(reportDate);
            //added to match date parameter in expired lcs
            Calendar cal = Calendar.getInstance();
            cal.setTime(rDate);
            //cal.add(Calendar.DATE, -1);
            rDate = cal.getTime();
        }catch(Exception e){
            rDate = new Date();
        }
        System.out.println(rDate.getTime());
        System.out.println("new Timestamp(rDate.getTime()):"+new Timestamp(rDate.getTime()));
        System.out.println("new Timestamp(lastExpiredDate.getTime()): "+new Timestamp(lastExpiredDate.getTime()));
        //Query query = session.createSQLQuery(queryStatement.toString()).setParameter("ts", new Timestamp(rDate.getTime())).setParameter("ls", new Timestamp(lastExpiredDate.getTime()));
		Query query = session.createSQLQuery(queryStatement.toString()).setParameter("ts", new Timestamp(rDate.getTime()));
        List<String> expiryList = new ArrayList<String>();

        Iterator it = query.list().iterator();
        int count = 0;
        while (it.hasNext()) {
//            Object[] obj = (Object[]) it.next();
            String documentNumber = (String)it.next();
            System.out.println("documentNumber:"+documentNumber);
            expiryList.add(documentNumber);
            count++;
        }
        System.out.println("Total: " + count);
        System.out.println("expiryList:"+expiryList);
        return expiryList;
    }

	@Override
    public List<String> getTradeProductToBeExpired2(String sql) {
        System.out.println("getTradeProductToBeExpired");
        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append(sql);
        System.out.println("queryStatement.toString(): " + queryStatement.toString());
        //todo convert reportDate to new Date
        //MM-dd-yyyy
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date rDate = new Date();
        try{
       
        }catch(Exception e){
         
        }
      
        Query query = session.createSQLQuery(queryStatement.toString());
        List<String> expiryList = new ArrayList<String>();

        Iterator it = query.list().iterator();
        int count = 0;
        while (it.hasNext()) {
//            Object[] obj = (Object[]) it.next();
            String documentNumber = (String)it.next();
            System.out.println("documentNumber:"+documentNumber);
            expiryList.add(documentNumber);
            count++;
        }
        System.out.println("Total: " + count);
        System.out.println("expiryList:"+expiryList);
        return expiryList;
    }
	
	
    @Override
    public void expireDocNum(String docNum) {
        this.sessionFactory.getCurrentSession().createSQLQuery("UPDATE TRADEPRODUCT SET STATUS = 'EXPIRED' WHERE DOCUMENTNUMBER = :docNum")
                .setParameter("docNum", docNum).executeUpdate();
    }

    @Override
    public Map<String, Object> getLC(String documentNumber) {

        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
		queryStatement.append("select tp.DOCUMENTNUMBER,tp.STATUS,lc.STANDBYTAGGING, lc.tenor, lc.type,lc.documentType,lc.outstandingBalance,tp.currency, date(lc.expirydate),coalesce(lc.CASHFLAG, 0) as CASHFLAG,coalesce(lc.CASHAMOUNT, 0) as CASHAMOUNT,coalesce(lc.TOTALNEGOTIATEDCASHAMOUNT, 0) as TOTALNEGOTIATEDCASHAMOUNT,coalesce(lc.CURRENTAMOUNT , 0) as CURRENTAMOUNT,coalesce(lc.TOTALNEGOTIATEDAMOUNT, 0) as TOTALNEGOTIATEDAMOUNT,tp.URR  ");
 		queryStatement.append("from TradeProduct tp  inner JOIN LetterOfCredit lc ON lc.documentNumber = tp.documentNumber ");

        queryStatement.append("where tp.productType = 'LC'  ");

        queryStatement.append("and tp.documentNumber = '" + documentNumber + "' ");

        Query query = session.createSQLQuery(queryStatement.toString());

        Object[] obj = (Object[]) query.uniqueResult();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("documentNumber", obj[0]);
        map.put("status", obj[1]);
        map.put("standbyTagging", obj[2]);
        map.put("tenor", obj[3]);
        map.put("type", obj[4]);
        map.put("documentType", obj[5]);
        map.put("outstandingBalance", obj[6]);
        map.put("currency", obj[7]);
		map.put("expiryDate", obj[8]);
        map.put("cashFlag",  obj[9]);
        map.put("cashAmount",  obj[10]);
        map.put("totalNegotiatedCashAmount",  obj[11]);
        map.put("currentAmount",  obj[12]);
        map.put("totalnegoAmount", obj[13]);
		map.put("urr", obj[14]);


        return map;
    }

    @Override
    public void updateTrade(String line){
        Session session = this.sessionFactory.getCurrentSession();

        System.out.println("updateTrade line:"+line);
        Query query = session.createSQLQuery(line.replace(";",""));
        int i = query.executeUpdate();

    }

	@Override
	public Date getLastExpiredDate() {
		 Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT MAX(LC.EXPIRYDATE) FROM LETTEROFCREDIT LC INNER JOIN TRADEPRODUCT TP ON LC.DOCUMENTNUMBER = TP.DOCUMENTNUMBER WHERE TP.STATUS = 'EXPIRED'" +
				 "AND LC.TYPE <> 'CASH'");
		 Object obj = query.uniqueResult();
		 System.out.println("lastExpiredDate " +  obj);
	        Date lDate = new Date();
	        try{
	            lDate = (Date) obj;
	        }catch(Exception e){
	        	//null last expired date
	            lDate = new Date();
	        }    
		 return lDate;
	}

}
