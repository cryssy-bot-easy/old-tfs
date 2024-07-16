package com.ucpb.tfs.utils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ucpb.tfs.domain.service.TradeServiceRepository;

import java.math.BigDecimal;

import com.ucpb.tfs.domain.service.TradeService;

public class TradeServiceUtils {
    private static Object defaultIfNull(Object object, Object defaultValue) {
        return object != null ? object : defaultValue;
    }
    private static Object defaultIfNullArray(Object[] object, Object[] defaultValue) {
        return object != null ? object : defaultValue;
    }
	
	public static TradeService getSplittedValues(TradeService tradeService, Integer sequenceNumber){
		if(tradeService.getDetails().get("serviceType").toString().equalsIgnoreCase("OPENING")){
			if(tradeService.getDetails().get("priceTerm").toString().equalsIgnoreCase("OTH")){
				
				tradeService.getDetails().put("generalDescriptionOfGoods", tradeService.getDetails().get("generalDescriptionOfGoods").toString() +
					"\r\n+" + (tradeService.getDetails().get("priceTermNarrative") != null && tradeService.getDetails().get("priceTermNarrative").toString().length() > 0 ? tradeService.getDetails().get("priceTermNarrative") : tradeService.getDetails().get("otherPriceTerm")));
			}else {
				tradeService.getDetails().put("generalDescriptionOfGoods", tradeService.getDetails().get("generalDescriptionOfGoods").toString() +
					"\r\n+" + tradeService.getDetails().get("priceTerm"));
			}
			tradeService.getDetails().put("generalDescriptionOfGoods", getNext100Lines(defaultIfNull(tradeService.getDetails().get("generalDescriptionOfGoods"), "").toString(), sequenceNumber));
			tradeService.getDetails().put("specialPaymentConditionsForBeneficiary", getNext100Lines(defaultIfNull(tradeService.getDetails().get("specialPaymentConditionsForBeneficiary"), "").toString(), sequenceNumber));
			tradeService.getDetails().put("specialPaymentConditionsForReceivingBank", getNext100Lines(defaultIfNull(tradeService.getDetails().get("specialPaymentConditionsForReceivingBank"), "").toString(), sequenceNumber));				
		}else if(tradeService.getDetails().get("serviceType").toString().equalsIgnoreCase("AMENDMENT")){
			tradeService.getDetails().put("generalDescriptionOfGoodsTo", getNext100Lines(defaultIfNull(tradeService.getDetails().get("generalDescriptionOfGoodsTo"), "").toString(), sequenceNumber));
			tradeService.getDetails().put("specialPaymentConditionsForBeneficiaryTo", getNext100Lines(defaultIfNull(tradeService.getDetails().get("specialPaymentConditionsForBeneficiaryTo"), "").toString(), sequenceNumber));
			tradeService.getDetails().put("specialPaymentConditionsForReceivingBankTo", getNext100Lines(defaultIfNull(tradeService.getDetails().get("specialPaymentConditionsForReceivingBankTo"), "").toString(), sequenceNumber));
		}
		return tradeService;
	}
	
	private static Integer getMax(Integer[] arr) {
		int max = 0;
		for (Integer i : arr) {
			max = max > i ? max : i;
		}
		return max;
	}
	
	public static Integer getSequenceNumber(TradeService tradeService){
		int sequenceNumber = 0, longest;
		
		if(tradeService.getDetails().get("serviceType").toString().equalsIgnoreCase("OPENING")){
			String[] DescLines = tradeService.getDetails().get("generalDescriptionOfGoods").toString().split("\r\n");
			String[] SpBenifLines = tradeService.getDetails().get("specialPaymentConditionsForBeneficiary").toString().split("\r\n");
			String[] SpBankLines = tradeService.getDetails().get("specialPaymentConditionsForReceivingBank").toString().split("\r\n");

			Integer[] fieldList = new Integer[] {DescLines.length, SpBenifLines.length, SpBankLines.length};
//			fieldList.sort();
			longest = getMax(fieldList);
			
			sequenceNumber = longest / 100;
        	if(longest % 100 > 0) { sequenceNumber =  sequenceNumber + 1; }
			
			
		}else if(tradeService.getDetails().get("serviceType").toString().equalsIgnoreCase("AMENDMENT")){
			String[] DescLines = tradeService.getDetails().get("generalDescriptionOfGoods").toString().split("\r\n");
			String[] SpBenifLines = tradeService.getDetails().get("specialPaymentConditionsForBeneficiary").toString().split("\r\n");
			String[] SpBankLines = tradeService.getDetails().get("specialPaymentConditionsForReceivingBank").toString().split("\r\n");
			
			Integer[] fieldList = new Integer[] {DescLines.length, SpBenifLines.length, SpBankLines.length};
//			fieldList.sort();
			longest = getMax(fieldList);
			
			sequenceNumber = longest / 100;
        	if(longest % 100 > 0) { sequenceNumber =  sequenceNumber + 1; }
		}
		return sequenceNumber;
	}
	private static String getNext100Lines(String field, Integer sequenceNumber){
		String[] lines = field.split("\r\n");
		
		int totalLines = (lines.length / 100) + ((lines.length % 100 > 0) ? 1 : 0);
		
		if(sequenceNumber > (totalLines - 1)){
			return "";
		} else if(lines.length > 100) {
			StringBuilder finalField = new StringBuilder();
			for(int p = (sequenceNumber * 100); p < ((sequenceNumber + 1) * 100) && p < lines.length; p++){
				finalField.append(lines[p]+"\r\n");
			}
			return finalField.toString();
		}
		return field;
	}
}
