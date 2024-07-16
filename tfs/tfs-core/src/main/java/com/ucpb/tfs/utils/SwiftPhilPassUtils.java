package com.ucpb.tfs.utils;

import org.apache.commons.lang.StringUtils;

import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.util.SwiftUtil;

public class SwiftPhilPassUtils {

	private static final String EMPTY = "";
	
    private static PaymentRepository paymentRepository;
    private static TradeServiceRepository tradeServiceRepository;
	
	public SwiftPhilPassUtils(PaymentRepository paymentRepositorySource,
			TradeServiceRepository tradeServiceRepositorySource){
		paymentRepository = paymentRepositorySource;
		tradeServiceRepository = tradeServiceRepositorySource;
	}
	
	//Ugly implementation @_@
	public static String evaluateHeader(String tradeServiceId,String messageType){
		if(tradeServiceId == null || messageType == null){
			return EMPTY;
		}
		TradeService tradeService=tradeServiceRepository.load(new TradeServiceId(tradeServiceId));
		if(isPhilPass(tradeService)){
			return messageType;
		}else{
			return EMPTY;			
		}
	}
	
	public static String evaluateDocumentNumber(String oldDocumentNumber,String tradeServiceId){
		if(oldDocumentNumber != null && StringUtils.isBlank(tradeServiceId)){
			return oldDocumentNumber;
		}else if(StringUtils.isBlank(tradeServiceId)){
			return EMPTY;
		}
		
		TradeService tradeService=tradeServiceRepository.load(new TradeServiceId(tradeServiceId));
		if(isPhilPass(tradeService)){
			return generatePhilPassDocumentNumber(tradeService);
		}
		return oldDocumentNumber;
	}

	public static String evaluateRemittanceInfo(String remittanceInfoCode,String remittanceInfoText,String tradeServiceId){
		if(StringUtils.isBlank(tradeServiceId)){
			return SwiftUtil.formatRemittanceInfo(remittanceInfoCode,remittanceInfoText);
		}
		TradeService tradeService=tradeServiceRepository.load(new TradeServiceId(tradeServiceId));

		if(isPhilPass(tradeService) || (tradeService != null && tradeService.getTradeProductNumber().toString().
				toUpperCase().contains("IBCP") && StringUtils.isBlank(remittanceInfoText))){
			return SwiftUtil.formatRemittanceInfo("RFB", "PROCEEDS OF DOMESTIC LC\nNEGO UNDER DMLC " +
					tradeService.getTradeProductNumber().toString().replaceAll("-", ""));
		}
		return SwiftUtil.formatRemittanceInfo(remittanceInfoCode,remittanceInfoText);
	}
	
	
	/**
	 * IBCP-AAA-YY-NNNNN-D<br/>
		where:<br/>
		IBCP is the BSP code for customer fund transfer<br/>
		AAA - Processing Unit Code (i.e., Unit Code of TSD/ FD or Foreign Desk)<br/>
		YY - Last 2 digits of the Current Year<br/>
		NNNNN - Sequence (maximum of 5 digits) based on Processing Unit<br/>
		D - Check digit
		
		FX56202014001234=IBCP562014001234
		
	 * @param tradeService
	 * @return Document Number String
	 */
	public static String generatePhilPassDocumentNumber(TradeService tradeService){
		if(tradeService.getTradeProductNumber() == null){
			return EMPTY;
		}
		if(tradeService.getTradeProductNumber().toString().toUpperCase().startsWith("IBCP")){
			return tradeService.getTradeProductNumber().toString();
		}
		String documentNumber = EMPTY;
		StringBuilder builder = new StringBuilder(tradeService.getTradeProductNumber().
				toString().replaceAll("-", ""));
		
		if(tradeService.getTradeProductNumber().toString().toUpperCase().startsWith("DM") ||
			tradeService.getTradeProductNumber().toString().toUpperCase().startsWith("FX")){
			builder.delete(0, 2);
			builder.delete(4, 6);
			builder.insert(0,"IBCP");
		}else{
			builder.delete(3,8);
			builder.insert(0,"IBCP");
		}
		documentNumber = builder.toString();					
		return documentNumber;
	}
	
	
	private static boolean isPhilPass(TradeService tradeService){
		if(tradeService == null){
			return false;
		}
		if(tradeService.getDetails() != null && tradeService.getDetails().get("messageType") != null 
				&& tradeService.getDetails().get("currency") != null && 
				tradeService.getDetails().get("currency").toString().equals("PHP")){
			return true;
		}
		else if(tradeService.getDocumentType() != null && 
				tradeService.getDocumentType().equals(DocumentType.DOMESTIC)){
			Payment payment = paymentRepository.get(tradeService.getTradeServiceId(),ChargeType.SETTLEMENT);
			if(payment != null && payment.hasSwift()){
				for(PaymentDetail paymentDetail: payment.getDetails()){
					if(paymentDetail.getBookingCurrency() != null && 
							paymentDetail.getBookingCurrency().getCurrencyCode().equals("PHP")){
						return true;
					}else if(paymentDetail.getCurrency() != null && 
							paymentDetail.getCurrency().getCurrencyCode().equals("PHP")){
						return true;					
					}
				}
			}
		}
		return false;
	}
	
	public static String returnTradeServiceId(String tradeServiceId, String detail){
		if(tradeServiceId != null && !tradeServiceId.equalsIgnoreCase("")){
			return tradeServiceId.toLowerCase();
		} else if (detail != null && !detail.equalsIgnoreCase("")){
			return detail.toLowerCase();			
		} else {		
			return EMPTY;
		}
	}
	
//	@Deprecated
//	private static String generatePhilPassDocumentNumberFromScratch(TradeService tradeService){
//		String documentNumber = EMPTY;
//		if(!StringUtils.isBlank(tradeService.getIbcpDocumentNumber())){
//			documentNumber=tradeService.getIbcpDocumentNumber();
//		}else{
//			String documentCode = DocumentCodeEnum.toString(tradeService.getDocumentClass(), null, 
//					tradeService.getDocumentSubType1());
//			String sequenceNumber = tradeProductRepository.getDocumentNumberSequence(documentCode, 
//					tradeService.getProcessingUnitCode(), Calendar.getInstance().get(Calendar.YEAR));
//			if (sequenceNumber == null){
//				return EMPTY;
//			}
//			tradeProductRepository.incrementDocumentNumberSequence(documentCode, tradeService.getProcessingUnitCode(), 
//					Calendar.getInstance().get(Calendar.YEAR));
//			
//			documentNumber= String.format("%1$s%2$s%3$s%4$s",
//					"IBCP",
//					tradeService.getProcessingUnitCode(),
//					DateUtil.getLastTwoDigitsOfYear(new Date()),
//					StringUtils.leftPad(sequenceNumber, MAX_SEQUENCE_LENGTH, '0')
//					);
//			documentNumber = documentNumber + LuhnUtil.getCheckDigit(documentNumber, EMPTY);
//			tradeService.setIbcpDocumentNumber(documentNumber);
//			tradeServiceRepository.merge(tradeService);			
//		}
//		return documentNumber;
//	}
}