package com.ucpb.tfs.domain.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
	Project Name: ExportBillsRepository
 */

public interface ExportBillsRepository {

    public ExportBills load(DocumentNumber documentNumber);

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
    );

    public List<ExportBills> getAllExportBillsByCifNumber(String cifNumber, String exportBillType);
    
    // 01242017 - Redmine 4118: Remove restriction on BP Currency
    public List<ExportBills> getAllExportBillsByCifNumberNoRestrictionOnBpCurrency(String cifNumber, String exportBillType);
    
    public List<ExportBills> retrieveAllExportBills(String exportBillType);

    public List<ExportBills> getAllExportBillsByNegotiationNumber(DocumentNumber negotiationNumber);

    public Map<String, Object> loadToMapLcDetails(DocumentNumber documentNumber);
    
    public Map<String, Object> loadToMapNonLcDetails(DocumentNumber documentNumber);

    public List<ExportBills> loadByNegotiationNumber(DocumentNumber documentNumber);
}
