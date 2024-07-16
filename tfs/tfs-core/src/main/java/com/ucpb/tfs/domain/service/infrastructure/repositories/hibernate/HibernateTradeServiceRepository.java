package com.ucpb.tfs.domain.service.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: IPCVal
 * Date: 8/15/12
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
	Project Name: HibernateTradeServiceRepository
 */
 
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateTradeServiceRepository implements TradeServiceRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    public void persist(TradeService tradeService) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(tradeService);
    }

    public void update(TradeService tradeService) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(tradeService);
    }

    public void merge(TradeService tradeService) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(tradeService);
    }

    public void sessionFlush(){
        Session session = this.mySessionFactory.getCurrentSession();
        session.flush();
    }

    public void saveOrUpdate(TradeService tradeService) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.saveOrUpdate(tradeService);
    }

    public void deleteServiceCharges(TradeServiceId tradeServiceId) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.createQuery("delete from com.ucpb.tfs.domain.service.ServiceCharge where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId.toString()).executeUpdate();
    }

    public TradeService load(TradeServiceId tradeServiceId) {
        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).uniqueResult();
    }

    public TradeService load(ServiceInstructionId serviceInstructionId) {
        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where serviceInstructionId = :serviceInstructionId").setParameter("serviceInstructionId", serviceInstructionId).uniqueResult();
    }

    public TradeService load(DocumentNumber documentNumber, ServiceType serviceType) {
        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where documentNumber = :documentNumber and serviceType = :serviceType").setParameter("documentNumber", documentNumber).setParameter("serviceType", serviceType).uniqueResult();
    }

    @Override
    public TradeService load(TradeProductNumber tradeProductNumber, ServiceType serviceType,
    					DocumentType documentType,DocumentClass documentClass) {
    	return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
    			"from com.ucpb.tfs.domain.service.TradeService where tradeProductNumber = :tradeProductNumber and" +
    			" serviceType = :serviceType and" +
    			" documentType = :documentType and"+
    			" documentClass = :documentClass and"+
    			" status in ('APPROVED','POSTED')").
    			setParameter("tradeProductNumber", tradeProductNumber).
    			setParameter("serviceType", serviceType).
    			setParameter("documentType", documentType).
    			setParameter("documentClass", documentClass).
    			uniqueResult();
    }

    @Override
    public List<TradeService> load(DocumentNumber documentNumber) {
        return this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).list();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<TradeService> load(String cifNumber,String mainCifNumber,String facilityType,String facilityId){
//    	return this.mySessionFactory.getCurrentSession().createQuery(
//    			"from com.ucpb.tfs.domain.service.TradeService where cifNumber = :cifNumber," +
//    			"mainCifNumber = :mainCifNumber,facilityType = :facilityType,facilityId = :facilityId").
//    			setParameter("cifNumber", cifNumber).setParameter("mainCifNumber", mainCifNumber).
//    			setParameter("facilityType", facilityType).setParameter("facilityId", facilityId).list();

        return this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where (status != :status1 and status != :status2 and status != :status3) and cifNumber = :cifNumber and " +
                        "mainCifNumber = :mainCifNumber and facilityType = :facilityType and facilityId = :facilityId").
                setParameter("status1", TradeServiceStatus.APPROVED).setParameter("status2", TradeServiceStatus.POST_APPROVED).setParameter("status3", TradeServiceStatus.POSTED).
                setParameter("cifNumber", cifNumber).setParameter("mainCifNumber", mainCifNumber).
                setParameter("facilityType", facilityType).setParameter("facilityId", facilityId).list();
    }

    public TradeService load(TradeServiceReferenceNumber tradeServiceReferenceNumber) {
        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where tradeServiceReferenceNumber = :tradeServiceReferenceNumber").setParameter("tradeServiceReferenceNumber", tradeServiceReferenceNumber).uniqueResult();
    }

    public TradeService load(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType) {
        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where tradeServiceReferenceNumber = :tradeServiceReferenceNumber and serviceType = :serviceType").setParameter("tradeServiceReferenceNumber", tradeServiceReferenceNumber).setParameter("serviceType", serviceType).uniqueResult();
    }
    
    public TradeService load(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType, String processingUnitCode) {
        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where tradeServiceReferenceNumber = :tradeServiceReferenceNumber and serviceType = :serviceType and processingUnitCode = :processingUnitCode and status != :status").setParameter("tradeServiceReferenceNumber", tradeServiceReferenceNumber).setParameter("serviceType", serviceType).setParameter("processingUnitCode", processingUnitCode).setParameter("status", TradeServiceStatus.ABORTED).uniqueResult();
    }

//    public TradeService load2(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType) {
//        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
//                "from com.ucpb.tfs.domain.service.TradeService where tradeServiceReferenceNumber = :tradeServiceReferenceNumber and serviceType = :serviceType and status = :status").
//                setParameter("tradeServiceReferenceNumber", tradeServiceReferenceNumber).
//                setParameter("serviceType", serviceType).
//                setParameter("status", TradeServiceStatus.APPROVED).uniqueResult();
//    }

    public TradeService load2(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType) {
        Session session  = this.mySessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(TradeService.class);

        criteria.add(Restrictions.eq("tradeServiceReferenceNumber", tradeServiceReferenceNumber));
        criteria.add(Restrictions.eq("serviceType", serviceType));
        criteria.add(Restrictions.eq("status", TradeServiceStatus.APPROVED));

        criteria.addOrder(Order.desc("createdDate"));

        List<TradeService> tsList = criteria.list();

        if (!tsList.isEmpty()) {
            return tsList.get(0);
        }

        return null;
    }

    @Override
    public TradeService getTradeServiceByPaymentDetailId(Long id) {
        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where tradeServiceId = (" +
                        "SELECT payment.tradeServiceId FROM com.ucpb.tfs.domain.payment.Payment payment where " +
                        "id = (SELECT paymentDetail.paymentId from com.ucpb.tfs.domain.payment.PaymentDetail paymentDetail where id =  :id))").setParameter("id", id).uniqueResult();
    }

    // for required documents
    public void deleteRequiredDocuments(TradeServiceId tradeServiceId) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.createQuery("delete from com.ucpb.tfs.domain.documents.RequiredDocument where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId.toString()).executeUpdate();
    }

    // for instructions to bank
    public void deleteInstructionsToBank(TradeServiceId tradeServiceId) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.createQuery("delete from com.ucpb.tfs.domain.reimbursing.InstructionToBank where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId.toString()).executeUpdate();
    }

    // for additional conditions
    public void deleteAdditionalConditions(TradeServiceId tradeServiceId) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.createQuery("delete from com.ucpb.tfs.domain.condition.AdditionalCondition where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId.toString()).executeUpdate();
    }

    @Override
    public List<TradeService> getUnapprovedTradeServices() {
        return mySessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.service.TradeService " +
                "where STATUS = 'CHECKED' OR STATUS = 'PREPARED' AND SERVICETYPE NOT LIKE '%_REVERSAL'").list();
    }

    // for transmittal letter
    public void deleteTransmittalLetters(TradeServiceId tradeServiceId) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.createQuery("delete from com.ucpb.tfs.domain.letter.TransmittalLetter where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId.toString()).executeUpdate();
    }

    // for swift charge
    public void deleteSwiftCharges(TradeServiceId tradeServiceId) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.createQuery("delete from com.ucpb.tfs.domain.swift.SwiftCharge where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId.toString()).executeUpdate();
    }


    private List findTradeServiceBy(Criteria criteria) {

        List tradeServices = criteria.list();

        // eagerly load all references
        Hibernate.initialize(tradeServices);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeServices);
        List returnMap = gson.fromJson(result, List.class);

        return returnMap;
    }

    @Override
    public List<TradeService> getAllTradeService() {
        Session session  = this.mySessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(TradeService.class);

        return findTradeServiceBy(criteria);

    }

    @Override
    public List<TradeService> list() {
        Session session  = this.mySessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(TradeService.class);
        List tradeServices = criteria.list();
        return tradeServices;

    }


    @Override
    public Map getTradeServiceBy(TradeServiceReferenceNumber tradeServiceReferenceNumber) {

        TradeService tradeService = load(tradeServiceReferenceNumber);

        // eagerly load all references
        Hibernate.initialize(tradeService);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeService);
        Map returnClass = gson.fromJson(result, Map .class);

        return returnClass;
    }

    @Override
    public Map getTradeServiceBy(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType) {

        TradeService tradeService = load(tradeServiceReferenceNumber, serviceType);

        // eagerly load all references
        Hibernate.initialize(tradeService);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeService);
        Map returnClass = gson.fromJson(result, Map .class);

        return returnClass;
    }
    
    @Override
    public Map getTradeServiceByUnitCode(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType, String processingUnitCode) {

        TradeService tradeService = load(tradeServiceReferenceNumber, serviceType, processingUnitCode);

        // eagerly load all references
        Hibernate.initialize(tradeService);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeService);
        Map returnClass = gson.fromJson(result, Map .class);

        return returnClass;
    }

    @Override
    public Map getTradeServiceBy2(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType) {

        TradeService tradeService = load2(tradeServiceReferenceNumber, serviceType);

        // eagerly load all references
        Hibernate.initialize(tradeService);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeService);
        Map returnClass = gson.fromJson(result, Map .class);

        return returnClass;
    }

    @Override
    public Map getTradeServiceBy(TradeServiceId tradeServiceId) {
        TradeService tradeService = load(tradeServiceId);

        // eagerly load all references
        Hibernate.initialize(tradeService);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeService);
        Map returnClass = gson.fromJson(result, Map .class);

        return returnClass;

    }

    @Override
    public Map getTradeServiceBy(ServiceInstructionId serviceInstructionId) {
        TradeService tradeService = load(serviceInstructionId);

        // eagerly load all references
        Hibernate.initialize(tradeService);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeService);
        Map returnClass = gson.fromJson(result, Map.class);

        return returnClass;

    }

    public Map getTradeServiceBy(TradeProductNumber tradeProductNumber, ServiceType serviceType,
			DocumentType documentType,DocumentClass documentClass) {
    	TradeService tradeService = load(tradeProductNumber,serviceType,documentType,documentClass);
    	
    	// eagerly load all references
    	Hibernate.initialize(tradeService);
    	
    	// we cannot return the list so we use gson to serialize then deserialize back to a list
    	Gson gson = new Gson();
    	
    	String result = gson.toJson(tradeService);
    	Map returnClass = gson.fromJson(result, Map.class);
    	
    	return returnClass;
    }
    
    
    @Override
    public Boolean exists(TradeServiceId tradeServiceId){
        TradeService tradeService = load(tradeServiceId);

        if(tradeService!=null){
            System.out.println("TRUE");
            return Boolean.TRUE;
        } else {
            System.out.println("FALSE");
            return Boolean.FALSE;
        }


    }

    @Override
    public List<TradeService> getAllApprovedExportAdvising(DocumentNumber documentNumber) {
        Session session  = this.mySessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(TradeService.class);

        //criteria.add(Restrictions.eq("status", TradeServiceStatus.APPROVED));
        List<TradeServiceStatus> tradeServiceStatusList = new ArrayList<TradeServiceStatus>();
        tradeServiceStatusList.add(TradeServiceStatus.APPROVED);
        tradeServiceStatusList.add(TradeServiceStatus.POST_APPROVED);
        tradeServiceStatusList.add(TradeServiceStatus.POSTED);

        criteria.add(Restrictions.in("status", tradeServiceStatusList));

        criteria.add(Restrictions.eq("documentClass", DocumentClass.EXPORT_ADVISING));
        criteria.add(Restrictions.eq("documentNumber", documentNumber));

        return criteria.list();
    }

    @Override
    public List<ServiceInstructionId> getAllActiveServiceInstructionIdsByTradeProductNumber(TradeProductNumber tradeProductNumber) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(TradeService.class).
                setProjection(Projections.projectionList().
                add(Projections.property("serviceInstructionId")));

        criteria.add(Restrictions.eq("tradeProductNumber", tradeProductNumber));

        return criteria.list();
    }

	@Override
	public List<TradeService> getAllActiveTradeService(TradeProductNumber tradeProductNumber) {
		Session session  = this.mySessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(TradeService.class);
        
        criteria.add(Restrictions.eq("tradeProductNumber", tradeProductNumber));

        List<TradeServiceStatus> tradeServiceStatusList = new ArrayList<TradeServiceStatus>();
        tradeServiceStatusList.add(TradeServiceStatus.APPROVED);
        tradeServiceStatusList.add(TradeServiceStatus.POST_APPROVED);
        tradeServiceStatusList.add(TradeServiceStatus.POSTED);
        tradeServiceStatusList.add(TradeServiceStatus.ABORTED);
        tradeServiceStatusList.add(TradeServiceStatus.MARV);
        tradeServiceStatusList.add(TradeServiceStatus.RETURNED_TO_BRANCH);
        tradeServiceStatusList.add(TradeServiceStatus.REVERSED);
        tradeServiceStatusList.add(TradeServiceStatus.DRAFT);

        criteria.add(Restrictions.not(
                Restrictions.in("status", tradeServiceStatusList))
        );

        criteria.add(Restrictions.isNotNull("status"));
        
        return findTradeServiceBy(criteria);
	}

    @Override
    public List<TradeService> getAllActiveTradeService(TradeServiceId tradeServiceId,
                                                       TradeProductNumber tradeProductNumber,
                                                       ServiceType serviceType,
                                                       Boolean isNotPrepared) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(TradeService.class);

        criteria.add(Restrictions.ne("tradeServiceId", tradeServiceId));

        criteria.add(Restrictions.eq("tradeProductNumber", tradeProductNumber));

        if (isNotPrepared == Boolean.FALSE) {
            criteria.add(Restrictions.eq("status", TradeServiceStatus.PREPARED));
        }

        if (serviceType != null) {
            if (serviceType.toString().contains("_ADVISING")) {
                List<ServiceType> serviceTypeList = new ArrayList<ServiceType>();
                serviceTypeList.add(ServiceType.AMENDMENT_ADVISING);
                serviceTypeList.add(ServiceType.CANCELLATION_ADVISING);

                criteria.add(Restrictions.in("serviceType", serviceTypeList));
            } else {
                criteria.add(Restrictions.eq("serviceType", serviceType));
            }
        }

        List<TradeServiceStatus> tradeServiceStatusList = new ArrayList<TradeServiceStatus>();
        tradeServiceStatusList.add(TradeServiceStatus.APPROVED);
        tradeServiceStatusList.add(TradeServiceStatus.POST_APPROVED);
        tradeServiceStatusList.add(TradeServiceStatus.POSTED);
        tradeServiceStatusList.add(TradeServiceStatus.ABORTED);
        tradeServiceStatusList.add(TradeServiceStatus.RETURNED_TO_BRANCH);
        

        criteria.add(Restrictions.not(
                Restrictions.in("status", tradeServiceStatusList))
        );

        criteria.add(Restrictions.isNotNull("status"));

        return findTradeServiceBy(criteria);
    }
//
//    public String getUpdateAccountingEntries(String statement){
//        Session session  = this.mySessionFactory.getCurrentSession();
//        try {
//            System.out.println("getUpdateAccountingEntries statement:"+statement);
//            int temp = session.createQuery(statement).executeUpdate();
//            System.out.println(temp);
//            return "OK";
//        } catch (Exception e){
//            e.printStackTrace();
//            return e.getMessage();
//        }
//
//    }

    @Override
    public List<Map<String, Object>> getAllApprovedTradeServiceIds(TradeProductNumber tradeProductNumber) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(TradeService.class);

        criteria.add(Restrictions.eq("tradeProductNumber", tradeProductNumber));
        criteria.add(Restrictions.or(
                Restrictions.eq("status", TradeServiceStatus.POSTED),
                Restrictions.eq("status", TradeServiceStatus.APPROVED),
                Restrictions.eq("status", TradeServiceStatus.POST_APPROVED)
        ));

        criteria.add(Restrictions.not(
                Restrictions.or(
                        Restrictions.eq("serviceType", ServiceType.REFUND),
                        Restrictions.eq("serviceType", ServiceType.OPENING_REVERSAL)
                )
        ));

        List<TradeService> approvedTradeService = criteria.list();

        List<Map<String, Object>> tradeServiceMapList = new ArrayList<Map<String, Object>>();

        for (TradeService tradeService : approvedTradeService) {
            Map<String, Object> tradeServiceMap = new HashMap<String, Object>();
            tradeServiceMap.put("tradeServiceId", tradeService.getTradeServiceId().toString());
            tradeServiceMap.put("serviceType", tradeService.getServiceType());

            tradeServiceMapList.add(tradeServiceMap);
        }

        return tradeServiceMapList;
    }

    private List<ServiceType> getAvailableLCServiceTypes(DocumentType documentType) {

        List<ServiceType> serviceTypeList = new ArrayList<ServiceType>();

        switch (documentType) {
            case FOREIGN:
                serviceTypeList.add(ServiceType.OPENING);
                serviceTypeList.add(ServiceType.AMENDMENT);
                serviceTypeList.add(ServiceType.ADJUSTMENT);
                serviceTypeList.add(ServiceType.NEGOTIATION);
                break;

            case DOMESTIC:
                serviceTypeList.add(ServiceType.OPENING);
                serviceTypeList.add(ServiceType.AMENDMENT);
                serviceTypeList.add(ServiceType.NEGOTIATION);
                break;
        }

        return serviceTypeList;
    }

    private List<ServiceType> getAvailableServiceTypes(DocumentClass documentClass,
                                                       DocumentType documentType) {
        List<ServiceType> serviceTypeList = new ArrayList<ServiceType>();

        switch (documentClass) {
            case LC:
                return getAvailableLCServiceTypes(documentType);

            case INDEMNITY:
                serviceTypeList.add(ServiceType.ISSUANCE);
                serviceTypeList.add(ServiceType.CANCELLATION);
                break;

            case DA:
            case DR:
            case DP:
            case OA:
                serviceTypeList.add(ServiceType.SETTLEMENT);
                break;
        }

        return serviceTypeList;
    }

    @Override
    public List<Map<String, Object>> getAllApprovedTradeServiceIdsForImportCharges(TradeProductNumber tradeProductNumber) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(TradeService.class);

        criteria.add(Restrictions.eq("tradeProductNumber", tradeProductNumber));
        criteria.add(Restrictions.or(
                Restrictions.eq("status", TradeServiceStatus.POSTED),
                Restrictions.eq("status", TradeServiceStatus.APPROVED),
                Restrictions.eq("status", TradeServiceStatus.POST_APPROVED)
        ));

//        criteria.add(Restrictions.or(
//                        Restrictions.eq("serviceType", ServiceType.OPENING),
//                        Restrictions.eq("serviceType", ServiceType.ADJUSTMENT),
//                        Restrictions.eq("serviceType", ServiceType.AMENDMENT),
//                        Restrictions.eq("serviceType", ServiceType.NEGOTIATION),
//                        Restrictions.eq("serviceType", ServiceType.ISSUANCE),
//                        Restrictions.eq("serviceType", ServiceType.CANCELLATION),
//                        Restrictions.eq("serviceType", ServiceType.SETTLEMENT)
//        ));
//
//        criteria.add(Restrictions.or(
//                Restrictions.eq("documentClass", DocumentClass.LC),
//                Restrictions.eq("documentClass", DocumentClass.INDEMNITY),
//                Restrictions.eq("documentClass", DocumentClass.DA),
//                Restrictions.eq("documentClass", DocumentClass.DP),
//                Restrictions.eq("documentClass", DocumentClass.DR),
//                Restrictions.eq("documentClass", DocumentClass.OA)
//        ));

        List<TradeService> approvedTradeService = criteria.list();

        Iterator it = approvedTradeService.iterator();

        while (it.hasNext()) {
            TradeService tradeService = (TradeService) it.next();

            List<ServiceType> availableServiceTypes = getAvailableServiceTypes(tradeService.getDocumentClass(), tradeService.getDocumentType());

            if (!availableServiceTypes.contains(tradeService.getServiceType())) {
                it.remove();
            }
        }

        List<Map<String, Object>> tradeServiceMapList = new ArrayList<Map<String, Object>>();

        for (TradeService tradeService : approvedTradeService) {
            Map<String, Object> tradeServiceMap = new HashMap<String, Object>();
            tradeServiceMap.put("tradeServiceId", tradeService.getTradeServiceId().toString());
            tradeServiceMap.put("serviceType", tradeService.getServiceType());

            tradeServiceMapList.add(tradeServiceMap);
        }

        return tradeServiceMapList;
    }

    @Override
    public List<Map<String, Object>> getAllApprovedTradeServiceIdsForLcRefund(TradeProductNumber tradeProductNumber) {
        Session session  = this.mySessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(TradeService.class);

        criteria.add(Restrictions.eq("tradeProductNumber", tradeProductNumber));
        criteria.add(Restrictions.or(
                Restrictions.eq("status", TradeServiceStatus.POSTED),
                Restrictions.eq("status", TradeServiceStatus.APPROVED),
                Restrictions.eq("status", TradeServiceStatus.POST_APPROVED)
        ));

        criteria.add(Restrictions.or(
                Restrictions.eq("serviceType", ServiceType.OPENING),
                Restrictions.eq("serviceType", ServiceType.ADJUSTMENT),
                Restrictions.eq("serviceType", ServiceType.AMENDMENT),
                Restrictions.eq("serviceType", ServiceType.NEGOTIATION),
                Restrictions.eq("serviceType", ServiceType.SETTLEMENT),
                Restrictions.eq("serviceType", ServiceType.CANCELLATION)
        ));

        List<DocumentClass> documentClassList = new ArrayList<DocumentClass>();
        documentClassList.add(DocumentClass.LC);
        documentClassList.add(DocumentClass.DA);
        documentClassList.add(DocumentClass.DP);
        documentClassList.add(DocumentClass.DR);
        documentClassList.add(DocumentClass.OA);
        criteria.add(Restrictions.in("documentClass", documentClassList));

        List<TradeService> approvedTradeService = criteria.list();

        List<Map<String, Object>> tradeServiceMapList = new ArrayList<Map<String, Object>>();

        for (TradeService tradeService : approvedTradeService) {
            Map<String, Object> tradeServiceMap = new HashMap<String, Object>();
            tradeServiceMap.put("tradeServiceId", tradeService.getTradeServiceId().toString());
            tradeServiceMap.put("serviceType", tradeService.getServiceType());

            tradeServiceMapList.add(tradeServiceMap);
        }

        return tradeServiceMapList;
    }

    @Override
    public List<Map<String, Object>> getAllApprovedTradeServiceIdsForExportCharges(String tradeProductNumber) {
        Session session  = this.mySessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append("select distinct(ts.tradeServiceId), ts.serviceType from TradeService ts ");
        queryStatement.append("inner join ServiceCharge sc ");
        queryStatement.append("on ts.tradeServiceId = sc.tradeServiceId ");
        queryStatement.append("where ts.status in ('POSTED', 'APPROVED', 'POST_APPROVED') ");

        queryStatement.append("and ts.documentClass in ('EXPORT_ADVISING', 'BC', 'BP') ");
        queryStatement.append("and ts.tradeProductNumber = '" + tradeProductNumber + "'");

        Query query = session.createSQLQuery(queryStatement.toString());

        List<Map<String, Object>> tradeServiceMapList = new ArrayList<Map<String, Object>>();

        Iterator it = query.list().iterator();

        while (it.hasNext()) {
            Object[] obj = (Object[]) it.next();

            Map<String, Object> tradeServiceMap = new HashMap<String, Object>();
            tradeServiceMap.put("tradeServiceId", obj[0]);
            tradeServiceMap.put("serviceType", obj[1]);

            tradeServiceMapList.add(tradeServiceMap);
        }

        return tradeServiceMapList;
    }

    @Override
    public List<TradeService> getAllApprovedTradeServiceByDate(Date date) {

        Session session = this.mySessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(TradeService.class);

        GregorianCalendar plusOneDay = (GregorianCalendar)GregorianCalendar.getInstance();
        plusOneDay.setTime(date);
        plusOneDay.add(GregorianCalendar.DATE, 1);

        System.out.println("\n >>>>>>>>>>>>>>>>>>>> date = " + date.toString());
        System.out.println("\n >>>>>>>>>>>>>>>>>>>> plusOneDay = " + plusOneDay.getTime().toString());

        crit.add(Restrictions.eq("status", TradeServiceStatus.APPROVED));
        crit.add(Restrictions.between("modifiedDate", date, plusOneDay.getTime()));

        return crit.list();
    }

    @Override
    public List<TradeService> getAllOriginalTradeServiceMigratedData() {

        Session session = this.mySessionFactory.getCurrentSession();

        // Migration cut-off was on January 24, 2014
        /*
        GregorianCalendar migrationCutOff = (GregorianCalendar)GregorianCalendar.getInstance();
        migrationCutOff.set(Calendar.MONTH, Calendar.JANUARY);
        migrationCutOff.set(Calendar.DATE, 24);
        migrationCutOff.set(Calendar.YEAR, 2014);
        migrationCutOff.set(Calendar.HOUR, 0);
        migrationCutOff.set(Calendar.MINUTE, 0);
        migrationCutOff.set(Calendar.SECOND, 0);
        migrationCutOff.set(Calendar.MILLISECOND, 0);

        GregorianCalendar toMax = (GregorianCalendar)GregorianCalendar.getInstance();
        toMax.setTime(migrationCutOff.getTime());
        toMax.add(GregorianCalendar.DATE, 1);

        System.out.println("migrationCutOff.getTime() = " + migrationCutOff.getTime());
        System.out.println("toMax.getTime() = " + toMax.getTime());
        */

        Criteria crit = session.createCriteria(TradeService.class, "ts");

        DetachedCriteria d = DetachedCriteria.forClass(TradeService.class, "tsIn");
        d.setProjection(Projections.projectionList().add(Projections.property("tsIn.documentNumber")));

        Criterion one = Restrictions.and(Restrictions.isNull("status"),
                                         Restrictions.in("documentClass", new Object[]{DocumentClass.INDEMNITY, DocumentClass.DA, DocumentClass.DP, DocumentClass.OA, DocumentClass.BC}));

        Criterion two = Restrictions.and(Restrictions.isNull("status"), Restrictions.isNull("documentClass"));

        d.add(Restrictions.or(one, two));

        crit.add(Subqueries.propertyIn("ts.documentNumber", d));

        crit.add(Restrictions.in("status", new Object[]{TradeServiceStatus.APPROVED, TradeServiceStatus.POSTED, TradeServiceStatus.POST_APPROVED}));

        crit.addOrder(Order.asc("documentNumber"));
        crit.addOrder(Order.asc("createdDate"));

        // crit.add(Restrictions.le("modifiedDate", toMax.getTime()));

        return crit.list();
    }

    @Override
    public List<TradeService> getAllNonMigratedDataFirstBatch() {

        Session session = this.mySessionFactory.getCurrentSession();

        // Migration cut-off was on January 24, 2014
        /*
        GregorianCalendar migrationCutOff = (GregorianCalendar)GregorianCalendar.getInstance();
        migrationCutOff.set(Calendar.MONTH, Calendar.JANUARY);
        migrationCutOff.set(Calendar.DATE, 24);
        migrationCutOff.set(Calendar.YEAR, 2014);
        migrationCutOff.set(Calendar.HOUR, 0);
        migrationCutOff.set(Calendar.MINUTE, 0);
        migrationCutOff.set(Calendar.SECOND, 0);
        migrationCutOff.set(Calendar.MILLISECOND, 0);

        GregorianCalendar toMax = (GregorianCalendar)GregorianCalendar.getInstance();
        toMax.setTime(migrationCutOff.getTime());
        toMax.add(GregorianCalendar.DATE, 1);

        System.out.println("migrationCutOff.getTime() = " + migrationCutOff.getTime());
        System.out.println("toMax.getTime() = " + toMax.getTime());
        */

        Criteria crit = session.createCriteria(TradeService.class, "ts");

        DetachedCriteria d = DetachedCriteria.forClass(TradeService.class, "tsIn");
        d.setProjection(Projections.projectionList().add(Projections.property("tsIn.documentNumber")));

        Criterion one = Restrictions.and(Restrictions.isNull("status"),
                                         Restrictions.in("documentClass", new Object[]{DocumentClass.LC, DocumentClass.INDEMNITY, DocumentClass.DA, DocumentClass.DP, DocumentClass.OA, DocumentClass.BC}));

        Criterion two = Restrictions.and(Restrictions.isNull("status"), Restrictions.isNull("documentClass"));

        d.add(Restrictions.or(one, two));

        // This is the difference...
        crit.add(Subqueries.propertyNotIn("ts.documentNumber", d));

        // ...and this
        crit.add(Restrictions.in("documentClass", new Object[]{DocumentClass.INDEMNITY, DocumentClass.DA, DocumentClass.DP, DocumentClass.OA, DocumentClass.BC}));

        crit.add(Restrictions.in("status", new Object[]{TradeServiceStatus.APPROVED, TradeServiceStatus.POSTED, TradeServiceStatus.POST_APPROVED}));

        crit.addOrder(Order.asc("documentNumber"));
        crit.addOrder(Order.asc("createdDate"));

        // crit.add(Restrictions.le("modifiedDate", toMax.getTime()));

        return crit.list();
    }

    @Override
    public TradeService getCurrentTradeService(Date processDate, String processingUnitCode) {
        return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where processDate = :processDate and documentClass = :documentClass and serviceType = :serviceType and processingUnitCode = :processingUnitCode").
                setParameter("processDate", processDate).
                setParameter("documentClass", DocumentClass.CDT).
                setParameter("serviceType", ServiceType.PAYMENT).
                setParameter("processingUnitCode", processingUnitCode).uniqueResult();
    }

    @Override
    public String[] getDocumentNumbersOfUnapprovedEts(Date date) {
    	Session session  = this.mySessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append("SELECT TS.DOCUMENTNUMBER FROM TRADESERVICE TS ");
        queryStatement.append("WHERE TS.SERVICEINSTRUCTIONID IN ");
        queryStatement.append("(SELECT SERVICEINSTRUCTIONID FROM SERVICEINSTRUCTION SI ");
        queryStatement.append("INNER JOIN ");
        queryStatement.append("(SELECT MAX(DATESENT) LASTROUTE,ROUTINGINFORMATIONID FROM ROUTES GROUP BY ROUTINGINFORMATIONID) MOVEMENT ");
        queryStatement.append("ON MOVEMENT.ROUTINGINFORMATIONID = SI.SERVICEINSTRUCTIONID ");
        queryStatement.append("WHERE DAYS(cast(:sqlDate as TIMESTAMP)) - DAYS(LASTROUTE) > 29 AND SI.STATUS IN ('PENDING', 'PREPARED', 'CHECKED')) ");
        queryStatement.append("AND TS.DOCUMENTCLASS = 'LC' ");
        queryStatement.append("AND TS.DOCUMENTSUBTYPE1 IN ('REGULAR','STANDBY') ");
        queryStatement.append("AND TS.SERVICETYPE = 'OPENING'");

        Query query = session.createSQLQuery(queryStatement.toString()).setParameter("sqlDate", new java.sql.Date(date.getTime()));
        List results = query.list();
        System.out.println("documentNumbers results: " + results);
        String[] documentNumbers = new String[results.size()];
        for(int i = 0; i < results.size(); i++){
        	documentNumbers[i] = results.get(i).toString().replaceAll("-", "");
        }
        return documentNumbers;

    }

	@Override
	public List<TradeService> getAllTradeWithEarmarking() {

		return this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.service.TradeService where documentClass = :documentClass and status in ('PENDING','RETURNED') and serviceType in ('OPENING','ADJUSTMENT','AMENDMENT','NEGOTIATION') and documentSubType1 in ('REGULAR','STANDBY')").
                setParameter("documentClass", DocumentClass.LC).list();
	}

    @Override
    public List<TradeService> getAllActiveTradeService(String cifNumber) {

        Session session = this.mySessionFactory.getCurrentSession();

        /*
        return session.createQuery("from com.ucpb.tfs.domain.service.TradeService " +
                "where documentClass in ('LC', 'INDEMNITY', 'DA','DP','OA','DR', 'EXPORT_ADVISING', 'BC', 'BP', 'CORRES_CHARGE', 'IMPORT_ADVANCE', 'EXPORT_ADVANCE') " +
                "and status in ('MARV', 'PENDING', 'CHECKED', 'PREPARED', 'RETURNED', 'RETURNED_TO_BRANCH', 'REVERSAL') " +
                "and cifNumber = :cifNumber")
                .setParameter("cifNumber", cifNumber)
                .list();
         */

        // Explicitly use lazy loading
        Criteria crit = session.createCriteria(TradeService.class)
            .setFetchMode("serviceCharges", FetchMode.LAZY)
            .setFetchMode("attachments", FetchMode.LAZY)
            .setFetchMode("requiredDocument", FetchMode.LAZY)
            .setFetchMode("instructionToBank", FetchMode.LAZY)
            .setFetchMode("additionalCondition", FetchMode.LAZY)
            .setFetchMode("transmittalLetter", FetchMode.LAZY)
            .setFetchMode("swiftCharge", FetchMode.LAZY)
            .setFetchMode("productRefundDetails", FetchMode.LAZY)
            .setFetchMode("productCollectibleDetails", FetchMode.LAZY)
            .setFetchMode("otherChargesDetails", FetchMode.LAZY);

        Object[] documentClassArray = new Object[] {
                DocumentClass.LC, DocumentClass.INDEMNITY,
                DocumentClass.DA, DocumentClass.DP, DocumentClass.OA, DocumentClass.DR,
                DocumentClass.EXPORT_ADVISING,
                DocumentClass.BC, DocumentClass.BP,
                DocumentClass.CORRES_CHARGE,
                DocumentClass.IMPORT_ADVANCE,
                DocumentClass.EXPORT_ADVANCE
        };
        Object[] statusArray = new Object[] {
                TradeServiceStatus.MARV,
                TradeServiceStatus.PENDING,
                TradeServiceStatus.CHECKED,
                TradeServiceStatus.PREPARED,
                TradeServiceStatus.RETURNED,
                TradeServiceStatus.RETURNED_TO_BRANCH,
                TradeServiceStatus.FOR_REVERSAL
        };

        crit.add(Restrictions.in("documentClass", documentClassArray));

        crit.add(Restrictions.in("status", statusArray));

        crit.add(Restrictions.eq("cifNumber", cifNumber));

        List<TradeService> tradeServices = crit.list();

        // Hibernate.initialize(tradeServices);

        return tradeServices;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllTradeServiceIdForAmla(TradeProductNumber tradeProductNumber) {
		
		Session session  = this.mySessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder();
        queryStatement.append("SELECT TS.TRADESERVICEID FROM TRADESERVICE TS ");
        queryStatement.append("INNER JOIN ");
        queryStatement.append("PAYMENT P ON TS.TRADESERVICEID = P.TRADESERVICEID ");
        queryStatement.append("WHERE TS.TRADEPRODUCTNUMBER = '" + tradeProductNumber + "' ");
        queryStatement.append("AND TS.STATUS IN ('APPROVED', 'POSTED', 'POST_APPROVED') ");
        queryStatement.append("AND TS.DOCUMENTCLASS = 'LC' ");
        queryStatement.append("AND (TS.SERVICETYPE = 'ADJUSTMENT' OR TS.SERVICETYPE = 'AMENDMENT') ");
        queryStatement.append("AND P.CHARGETYPE = 'PRODUCT' ");
        queryStatement.append("ORDER BY P.PAIDDATE");
        
        Query query = session.createSQLQuery(queryStatement.toString());
        System.out.println("tradeServiceId query: " + query);
        System.out.println("tradeServiceId query.list(): " + query.list());
        List results = query.list();
        System.out.println("tradeServiceId results: " + results);
        
        return results;
        
	}
	
    @Override
    public TradeService getAmlaTradeServiceOpening(TradeProductNumber tradeProductNumber, ServiceType serviceType,
    					DocumentType documentType,DocumentClass documentClass) {
    	return (TradeService) this.mySessionFactory.getCurrentSession().createQuery(
    			"from com.ucpb.tfs.domain.service.TradeService where tradeProductNumber = :tradeProductNumber and" +
    			" serviceType = :serviceType and" +
    			" documentType = :documentType and"+
    			" documentClass = :documentClass and"+
    			" status in ('APPROVED','POSTED','POST_APPROVED')").
    			setParameter("tradeProductNumber", tradeProductNumber).
    			setParameter("serviceType", serviceType).
    			setParameter("documentType", documentType).
    			setParameter("documentClass", documentClass).
    			uniqueResult();
    }

}

