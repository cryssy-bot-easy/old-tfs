package com.ucpb.tfs.domain.product;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 8/6/12
 */
public interface TradeProductRepository {

    public void persist(TradeProduct tradeProduct);

    public void update(TradeProduct tradeProduct);

    public void merge(TradeProduct tradeProduct);

    public void mergeFlush(TradeProduct tradeProduct);

    public TradeProduct load(DocumentNumber documentNumber);
    
    public Map load(String documentNumber);

    public Map loadToMap(DocumentNumber documentNumber);
    
    public String getDocumentNumberSequence(String documentCode, String processingUnitCode, int year);

    public void incrementDocumentNumberSequence(String documentCode, String processingUnitCode, int year);

    public String getIndemnityNumberSequence(String documentCode, String processingUnitCode, int year);

    public void incrementIndemnityNumberSequence(String documentCode, String processingUnitCode, int year);

    public String getIcNumberSequence(String documentCode, String processingUnitCode, int year);

    public void incrementIcNumberSequence(String documentCode, String processingUnitCode, int year);
    
    public String getNonLcNumberSequence(String documentCode, String processingUnitCode, int year);
    
    public void incrementNonLcNumberSequence(String documentCode, String processingUnitCode, int year);

    public String getImportAdvanceSequence(String documentCode, String processingUnitCode, int year);

    public void incrementImportAdvanceNumberSequence(String documentCode, String processingUnitCode, int year);

    public String getNegotiationNumberSequence(String documentCode, String processingUnitCode, int year);

    public void incrementNegotiationNumberSequence(String documentCode, String processingUnitCode, int year);


    public String getSettlementAccountSequence(String documentCode);

    public void incrementSettlementAccountNumber(String settlementAccount);

    public Map loadToMapExportAdvising(DocumentNumber documentNumber);

    public Map loadToMapExportBills(DocumentNumber documentNumber);

    public List<Map<String, Object>> searchAllImportProducts(String documentNumber, String cifName, String cifNumber, String unitcode);

    public List<Map<String, Object>> findAllImportProducts(String documentNumber, String productType, String cifName, String cifNumber, String unitCode, String unitcode);

    public Map<String, Object> getImport(String documentNumber);

    public List<Map<String, Object>> findAllExportProducts(String documentNumber, String cifName, String importersName, String exportersName, String transaction, String unitCode, String unitcode);

    public Map<String, Object> getExport(String documentNumber);

    public List<String> getTradeProductToBeExpired(String sql, String reportDate, Date lastExpiredDate);
	
	public List<String> getTradeProductToBeExpired2(String sql);

    public void expireDocNum(String docNum);

    public Map<String, Object> getLC(String documentNumber);

    public void updateTrade(String line);
    
    public Date getLastExpiredDate();
}
