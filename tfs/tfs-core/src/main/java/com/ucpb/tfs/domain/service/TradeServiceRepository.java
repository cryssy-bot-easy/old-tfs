package com.ucpb.tfs.domain.service;

import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
	Project Name: TradeServiceRepository
 */
 
public interface TradeServiceRepository {

    public void persist(TradeService tradeService);

    public void update(TradeService tradeService);

    public void merge(TradeService tradeService);

    public void sessionFlush();

    public void saveOrUpdate(TradeService tradeService);

    public void deleteServiceCharges(TradeServiceId tradeServiceId);

    public TradeService load(TradeServiceId tradeServiceId);

    public TradeService load(ServiceInstructionId serviceInstructionId);

    public TradeService load(DocumentNumber documentNumber, ServiceType serviceType);

    public TradeService load(TradeProductNumber tradeProductNumber, ServiceType serviceType,
    		DocumentType documentType,DocumentClass documentClass);

    public List<TradeService> load(DocumentNumber documentNumber);

    public List<TradeService> load(String cifNumber,String mainCifNumber,String facilityType,String facilityId);

    public TradeService load(TradeServiceReferenceNumber tradeServiceReferenceNumber);

    public TradeService load(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType);

    public TradeService load2(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType);

    public TradeService getTradeServiceByPaymentDetailId(Long id);

    // for required documents
    public void deleteRequiredDocuments(TradeServiceId tradeServiceId);

    // for instructions to bank
    public void deleteInstructionsToBank(TradeServiceId tradeServiceId);

    // for additional conditions
    public void deleteAdditionalConditions(TradeServiceId tradeServiceId);

    public List<TradeService> getUnapprovedTradeServices();
    
    // for transmittal letter
    public void deleteTransmittalLetters(TradeServiceId tradeServiceId);

    // for swift charge
    public void deleteSwiftCharges(TradeServiceId tradeServiceId);

    // these are the new methods used by the RESTful API
    public List<TradeService> getAllTradeService();
    public Map getTradeServiceBy(TradeServiceReferenceNumber tradeServiceReferenceNumber);
    public Map getTradeServiceBy(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType);
    
    public Map getTradeServiceByUnitCode(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType, String processingUnitCode);
    
    public Map getTradeServiceBy2(TradeServiceReferenceNumber tradeServiceReferenceNumber, ServiceType serviceType);

    public Map getTradeServiceBy(TradeServiceId tradeServiceId);
    public Map getTradeServiceBy(ServiceInstructionId serviceInstructionId);
    public Map getTradeServiceBy(TradeProductNumber tradeProductNumber, ServiceType serviceType,
			DocumentType documentType,DocumentClass documentClass);
    public List<TradeService> list();
    public Boolean exists(TradeServiceId tradeServiceId);

    public List<TradeService> getAllApprovedExportAdvising(DocumentNumber documentNumber);

    public List<ServiceInstructionId> getAllActiveServiceInstructionIdsByTradeProductNumber(TradeProductNumber tradeProductNumber);

    public List<TradeService> getAllActiveTradeService(TradeProductNumber tradeProductNumber);
    
    public List<TradeService> getAllActiveTradeService(TradeServiceId tradeServiceId, TradeProductNumber tradeProductNumber, ServiceType serviceType, Boolean isNotPrepared);

    public List<Map<String, Object>> getAllApprovedTradeServiceIds(TradeProductNumber tradeProductNumber);

    public List<Map<String, Object>> getAllApprovedTradeServiceIdsForImportCharges(TradeProductNumber tradeProductNumber);

    public List<Map<String, Object>> getAllApprovedTradeServiceIdsForLcRefund(TradeProductNumber tradeProductNumber);

    public List<Map<String, Object>> getAllApprovedTradeServiceIdsForExportCharges(String tradeProductNumber);
    public TradeService getCurrentTradeService(Date processDate, String processingUnitCode);

    public List<TradeService> getAllApprovedTradeServiceByDate(Date date);

    public List<TradeService> getAllOriginalTradeServiceMigratedData();

    public List<TradeService> getAllNonMigratedDataFirstBatch();
    
    public String[] getDocumentNumbersOfUnapprovedEts(Date date);
    
    public List<TradeService> getAllTradeWithEarmarking();

    public List<TradeService> getAllActiveTradeService(String cifNumber);
    
    public List<String> getAllTradeServiceIdForAmla(TradeProductNumber tradeProductNumber);
    
    public TradeService getAmlaTradeServiceOpening(TradeProductNumber tradeProductNumber, ServiceType serviceType,
    		DocumentType documentType,DocumentClass documentClass);
}
