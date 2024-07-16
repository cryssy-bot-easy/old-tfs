package com.ucpb.tfs.application.query.service;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 8/19/12
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: 
	SCR/ER Description: IC Document - Inquiry 
	[Created by:] John Patrick C. Bautista
	[Date Revised:] 08/16/2017
	Program [Revision] Details: Added map for IC Inquiry.
	PROJECT: tfs-app
	MEMBER TYPE  : Java
	Project Name: ITradeServiceFinder
 */
@Finder
public interface ITradeServiceFinder {

    Map<String,?> findTradeService(@Param("tradeServiceId") String tradeServiceId);

    Map<String,?> findTradeServiceByServiceInstructionId(@Param("serviceInstructionId") String serviceInstructionId);

    Map<String,?> findTradeServiceByReferenceNumber(@Param("tradeServiceReferenceNumber") String tradeServiceReferenceNumber);

    // for bgbe
    Map<String,?> findIndemnity(@Param("referenceNumber") String referenceNumber, @Param("indemnityNumber") String indemnityNumber);
    
    // for lc
//    Map<String,?> findLetterOfCredit(@Param("tradeServiceId") String tradeServiceId);
    Map<String,?> findLetterOfCredit(@Param("documentNumber") String documentNumber);

    // for negotiation
    Map<String,?> findLcNegotiation(@Param("id") String id);
    
    // for ua loan maturity adjustment
    List<String> findAllNegotiationNumbers(@Param("documentNumber") String documentNumber);

    Map<String, ?> findNonEtsTradeService(@Param("tradeServiceId") String tradeServiceId);
    
    List<Map<String,?>> icInquiry(
            @Param("documentNumber") String documentNumber,
            @Param("cifName") String cifName,
            @Param("currency") String currency,
            @Param("lcAmountFrom") String lcAmountFrom,
            @Param("lcAmountTo") String lcAmountTo,
            @Param("icNumber") String icNumber,
            @Param("icAmountFrom") String icAmountFrom,
            @Param("icAmountTo") String icAmountTo,
            @Param("icDateFrom") String icDateFrom,
            @Param("icDateTo") String icDateTo,
            @Param("unitCode") String unitCode
    );
    
    List<Map<String,?>> lcInquiry(
            @Param("documentNumber") String documentNumber,
            @Param("cifName") String cifName,
            @Param("expiryDate") String expiryDate,
            @Param("currency") String currency,
            @Param("status") String status,
            @Param("openingDateFrom") String openingDateFrom,
            @Param("openingDateTo") String openingDateTo,
            @Param("outstandingLcAmountFrom") String outstandingLcAmountFrom,
            @Param("outstandingLcAmountTo") String outstandingLcAmountTo,
            @Param("unitCode") String unitCode,
            @Param("unitcode") String unitcode
    );
    
    List<Map<String,?>> nonLcInquiry(
			@Param("documentNumber") String documentNumber,
			@Param("status") String status,
			@Param("cifName") String cifName,
			@Param("negotiationDateFrom") String negotiationDateFrom,
			@Param("negotiationDateTo") String negotiationDateTo,
			@Param("userrole") String userrole,
			@Param("unitCode") String unitCode,
            @Param("unitcode") String unitcode
	);

    List<Map<String,?>> indemnityInquiry(
            @Param("documentNumber") String documentNumber,
            @Param("cifName") String cifName,
            @Param("originalLcAmount") String originalLcAmountFrom,
            @Param("indemnityNumber") String indemnityNumber,
            @Param("shipmentNumber") String shipmentNumber,
            @Param("shipmentAmount") String shipmentAmount,
            @Param("status") String status,
            @Param("unitCode") String unitCode,
            @Param("unitcode") String unitcode
    );

    List<Map<String,?>> negotiationInquiry(
            @Param("documentNumber") String documentNumber,
            @Param("clientName") String clientName,
            @Param("negotiationNumber") String negotiationNumber,
            @Param("cifName") String cifName,
            @Param("negotiationDateFrom") String negotiationDateFrom,
            @Param("negotiationDateTo") String negotiationDateTo,
            @Param("unitCode") String unitCode,
            @Param("unitcode") String unitcode
    );
    
    Map<String,?> findLetterOfCreditCriteria(@Param("documentNumber") String documentNumber);

    Map<String,?> findPaymentStatus(@Param("tradeServiceId") String tradeServiceId);

    public long getApprovedAmmendments(String documentNumber);

    List<Map<String,?>> findAllRelatedLc(@Param("cifNumber") String cifNumber);
    
    List<Map<String,?>> findAllIcNumbers(@Param("documentNumber") String documentNumber);
    
    Map<String,?> findNegotiationDiscrepancyByIcNumber(@Param("icNumber") String icNumber);
    
    Map<String,?> findUsancePeriodByDocumentNumber(@Param("documentNumber") String documentNumber);

    List<Map<String,?>> dataEntryInquiry(
    		@Param("unitCode") String unitCode,
            @Param("documentNumber") String documentNumber,
            @Param("cifName") String cifName,
            @Param("status") String status,
            @Param("documentClass") ArrayList<String> documentClass,
            @Param("documentType") String documentType,
            @Param("documentSubType1") String documentSubType1,
            @Param("serviceType") String serviceType,
            @Param("dateOfTransaction") String dateOfTransaction
    );
    
    Map<String,?> findDocumentAgainstAcceptance(
    		@Param("documentNumber") String documentNumber
    );
    
    Map<String,?> findDocumentAgainstPayment(
    		@Param("documentNumber") String documentNumber
	);
    
    Map<String,?> findOpenAccount(
    		@Param("documentNumber") String documentNumber
	);
    
    Map<String,?> findDirectRemittance(
    		@Param("documentNumber") String documentNumber
	);

    List<Map<String, ?>> findAllTransactionsByDocumentNumber(@Param("documentNumber") String documentNumber);

    Map<String,?> countNumberOfAmendments(@Param("documentNumber") String documentNumber);

    List<Map<String,?>> findAllExportAdvisingForPayment(@Param("documentNumber") String documentNumber,
                                                        @Param("lcNumber") String lcNumber,
                                                        @Param("exporterName") String exporterName,
                                                        @Param("processDate") String processDate,
                                                        @Param("unitCode") String unitCode);

    List<Map<String,?>> findAllExportBills(@Param("documentNumber") String documentNumber,
                                           @Param("currency") String currency,
                                           @Param("amountFrom") BigDecimal amountFrom,
                                           @Param("amountTo") BigDecimal amountTo,
                                           @Param("cifName") String cifName,
                                           @Param("corresBankCode") String corresBankCode,
                                           @Param("serviceType") String serviceType,
                                           @Param("transaction") String transaction,
                                           @Param("status") String status
    );
        
    List<Map<String,?>> findAllDefaultDocuments(@Param("tradeServiceId") String tradeServiceId, @Param("documentType") String documentType, @Param("documentNumber") String documentNumber);

    List<Map<String,?>> findAllSavedRequiredDocuments(@Param("documentNumber") String documentNumber, @Param("tradeServiceId") String tradeServiceId);
    
    List<Map<String,?>> findAllNewDocuments(@Param("documentNumber") String documentNumber, @Param("tradeServiceId") String tradeServiceId);
    
    List<Map<String,?>> findAllSavedAdditionalCondition(@Param("documentNumber") String documentNumber, @Param("tradeServiceId") String tradeServiceId);
    
    List<Map<String,?>> findAllNewAdditionalCondition(@Param("documentNumber") String documentNumber, @Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findTotalIcAmount(@Param("documentNumber") String documentNumber);
    
    List<Map<String,?>> findRegualarAndCashIcAmount(@Param("documentNumber") String documentNumber);
}
