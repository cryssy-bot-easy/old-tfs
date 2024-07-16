package com.ucpb.tfs.application.batch;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ucpb.tfs.batch.job.SpringJob;
import com.ucpb.tfs.batch.report.dw.service.BatchFacilityReearmarkService;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.product.LetterOfCreditRepository;
import com.ucpb.tfs.domain.product.enums.TradeProductStatus;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import com.ucpb.tfs.batch.facility.Availment;
import com.ucpb.tfs.interfaces.domain.enums.EarmarkingStatusDescription;

/**
 * <pre>
 * Program_id    : BatchFacilityRevaluationJob
 * Program_name  : TFS Batch Facility Reevaluation Process
 * SCR_Number    : IBD-12-0502-01
 * Process_Mode  : BATCH
 * Frequency     : Daily
 * Input         : N/A
 * Output        : N/A
 * Description   : Batch job that Revalues Contingent Availment Earmarking in SIBS
 * Called In     : BatchRestService.groovy
 * </pre>
 * @author Arvin Patrick Guiam
 * @see com.ucpb.tfs.batch.job.SpringJob
 */
public class BatchFacilityRevaluationJob implements SpringJob {

	/**
	* Definition of the TradeServiceRepository.java
	* @see com.ucpb.tfs.domain.service.TradeServiceRepository
	* @see com.ucpb.tfs.domain.service.infrastructure.repositories.hibernate.HibernateTradeServiceRepository
	*/
	private TradeServiceRepository tradeServiceRepository;
	/**
	* Definition of the LetterOfCreditRepository.java
	* @see com.ucpb.tfs.domain.product.LetterOfCreditRepository
	* @see com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate.HibernateLetterOfCreditRepository
	*/
	private LetterOfCreditRepository letterOfCreditRepository;
	/**
	* Definition of the PaymentRepository.java
	* @see com.ucpb.tfs.domain.payment.PaymentRepository
	* @see com.ucpb.tfs.domain.payment.infrastructure.repositories.hibernate.HibernatePaymentRepository
	*/
	private PaymentRepository paymentRepository;
	/**
	* Definition of the BatchFacilityReearmarkService.java that handles the batch queries to the SIBS database
	* @see com.ucpb.tfs.batch.report.dw.service.BatchFacilityReearmarkService
	* @see com.ucpb.tfs.batch.report.dw.service.BatchFacilityReearmarkServiceImpl
	*/
	private BatchFacilityReearmarkService batchFacilityReearmarkService;

	/**Container of conversion rates from foreign to local */
	private List<Map<String, Object>> historicalRates = new ArrayList<Map<String, Object>>();
	
	@Override
	public void execute() throws Exception {
		Date reearmarkDate = new Date();
		System.out.println("reearmarkDate: " + reearmarkDate);		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(reearmarkDate);
		String sibsDate = ((Integer)cal.get(Calendar.YEAR)).toString() + new DecimalFormat("000").format(cal.get(Calendar.DAY_OF_YEAR)).toString();
		System.out.println("sibsDate: " + sibsDate);		
		
		//gets all Letter of Credit with outstanding earmarking or with earmarking closed within the month.
		List<LetterOfCredit> lettersOfCredit = letterOfCreditRepository.getLcsWithEarmarking(new Date());
		
		//gets all outstanding trade services with eligible earmarking.
		List<TradeService> tradeServices = tradeServiceRepository.getAllTradeWithEarmarking();
		
		System.out.println("lettersOfCredit.size() = " + lettersOfCredit.size());
		System.out.println("tradeServices.size() = " + tradeServices.size());

		//Preparation of the Availment objects to be used for LNCLST 
		Map<String, Availment> availmentList = new HashMap<String, Availment>();
		try{
		historicalRates = batchFacilityReearmarkService.getConversionRateHistorical(sibsDate);
		}catch(Exception e){
	   		 e.printStackTrace();
	   	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
		}
		System.out.println("HISTORICAL RATES LIST: "+ historicalRates.toString());
		
		for(LetterOfCredit letterOfCredit : lettersOfCredit){
			Availment availment = new Availment(reearmarkDate);
			
			availment.setDocumentNumber(letterOfCredit.getDocumentNumber().toString());
			availment.setCifNumber(letterOfCredit.getCifNumber());
			availment.setFacilityReferenceNumber(letterOfCredit.getFacilityReferenceNumber().toString());
			availment.setCurrencyCode(letterOfCredit.getCurrency().toString());
			availment.setOriginalAmount(letterOfCredit.getAmount());
			
			BigDecimal outstandingAmount = BigDecimal.ZERO;
			String statusDescription = EarmarkingStatusDescription.CURRENT.toString();
			
			if (letterOfCredit.getStatus().equals(TradeProductStatus.CANCELLED)){
				statusDescription = EarmarkingStatusDescription.CANCELLED.toString();
			} else if (letterOfCredit.getStatus().equals(TradeProductStatus.CLOSED)){
				statusDescription = EarmarkingStatusDescription.CLOSED.toString();
			} else if (letterOfCredit.getStatus().equals(TradeProductStatus.EXPIRED)){
				statusDescription = EarmarkingStatusDescription.MATURED.toString();
			} else if (letterOfCredit.getStatus().equals(TradeProductStatus.OPEN) || letterOfCredit.getStatus().equals(TradeProductStatus.REINSTATED)){
				BigDecimal cashAmount = BigDecimal.ZERO;
				if(letterOfCredit.getCashAmount() != null && letterOfCredit.getCashAmount().compareTo(BigDecimal.ZERO) > 0)
					cashAmount = letterOfCredit.getCashAmount();
				outstandingAmount = letterOfCredit.getOutstandingBalance().subtract(cashAmount);
			}
			
			availment.setOutstandingBalance(outstandingAmount);
			availment.setStatusDescription(statusDescription);
			
			availment = setPhpValues(availment, sibsDate);
			System.out.println("letterOfCredit: " + availment.getDocumentNumber() + ", " + availment.getCifNumber() + ", " + availment.getFacilityReferenceNumber() + ", " + availment.getCurrencyCode() + ", " + availment.getOriginalAmount() + ", " + availment.getOutstandingBalance() + ", " + availment.getPhpAmount() + ", " + availment.getPhpOutstandingBalance() + ", " + availment.getStatusDescription());
			
			
			availmentList.put(letterOfCredit.getDocumentNumber().toString(), availment);
		}
		
		//Updates or additional Availment objects to be used for LNCLST 
		for(TradeService tradeService : tradeServices){
			Map<String, Object> details = tradeService.getDetails();
			
			//checks if the trade service has complete data to create availment. Otherwise, trade service will be skiped.
			if((details.containsKey("originalCurrency") || details.containsKey("currency")) && details.containsKey("facilityReferenceNumber") && (details.containsKey("originalAmount") || details.containsKey("amount")) && (details.containsKey("outstandingBalance") || details.containsKey("amount"))) {
				Availment availment = new Availment(reearmarkDate);
				availment.setDocumentNumber(tradeService.getTradeProductNumber().toString());
				availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());
				
				ServiceType serviceType = tradeService.getServiceType();
				
				String currencyCode = serviceType.equals(ServiceType.NEGOTIATION) ? details.get("originalCurrency").toString() : details.get("currency").toString();
				String cifNumber = tradeService.getCifNumber();
				String facilityReferenceNumber = details.get("facilityReferenceNumber").toString();
				BigDecimal originalAmount = new BigDecimal((serviceType.equals(ServiceType.NEGOTIATION) ? details.get("originalAmount") : (serviceType.equals(ServiceType.AMENDMENT) && details.get("amountFrom") != null) ? details.get("amountFrom") : details.get("amount")).toString().replaceAll(",",""));
				BigDecimal outstandingAmount = new BigDecimal((serviceType.equals(ServiceType.OPENING) ? details.get("amount") : details.get("outstandingBalance")).toString().replaceAll(",",""));
				
				if(serviceType.equals(ServiceType.ADJUSTMENT)){
					if(details.get("cifNumberTo") != null && !details.get("cifNumberTo").toString().isEmpty()){
						cifNumber = details.get("cifNumberTo").toString();
					}
					if(details.get("facilityReferenceNumberTo") != null && !details.get("facilityReferenceNumberTo").toString().isEmpty()){				
						facilityReferenceNumber = details.get("facilityReferenceNumberTo").toString();
					}
				} else if(serviceType.equals(ServiceType.AMENDMENT)){
					if(details.get("amountSwitch") != null && details.get("amountSwitch").toString().equals("on")){				
						outstandingAmount = outstandingAmount.add(new BigDecimal(details.get("amountTo").toString().replaceAll(",","")).subtract(originalAmount));
						originalAmount = new BigDecimal(details.get("amountTo").toString().replaceAll(",",""));
					}
				} else if(serviceType.equals(ServiceType.NEGOTIATION)){
					Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
					if(payment != null){
						Set<PaymentDetail> paymentDetails = payment.getDetails();
						
						for(PaymentDetail paymentDetail : paymentDetails){
							if (paymentDetail.isPaid()){
								outstandingAmount = outstandingAmount.subtract(paymentDetail.getAmountInLcCurrency()).compareTo(BigDecimal.ZERO) >= 0 ? outstandingAmount.subtract(paymentDetail.getAmountInLcCurrency()) : BigDecimal.ZERO;
							}
						}
					}
				}
				
				availment.setCurrencyCode(currencyCode);
				availment.setCifNumber(cifNumber);
				availment.setFacilityReferenceNumber(facilityReferenceNumber);
				availment.setOriginalAmount(originalAmount);
				availment.setOutstandingBalance(outstandingAmount);
				
				availment = setPhpValues(availment, sibsDate);
				System.out.println("tradeService: " + availment.getDocumentNumber() + ", " + serviceType + ", " + availment.getCifNumber() + ", " + availment.getFacilityReferenceNumber() + ", " + availment.getCurrencyCode() + ", " + availment.getOriginalAmount() + ", " + availment.getOutstandingBalance() + ", " + availment.getPhpAmount() + ", " + availment.getPhpOutstandingBalance() + ", " + availment.getStatusDescription());
				availmentList.put(tradeService.getTradeProductNumber().toString(), availment);
			} else {
				System.out.println("excluded: " + tradeService.getTradeProductNumber());
			}
		}
		
		System.out.println("availmentList.size() = " + availmentList.size());
		try{

		if (!batchFacilityReearmarkService.reearmark(new ArrayList<Availment>(availmentList.values()))){
			throw new Exception("Error in inserting updated availment values");
		}
		}catch(Exception e){
	   		 e.printStackTrace();
	   	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
		}

		
	}

	@Override
	public void execute(String reportDate) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * computes the PHP values of the amounts set in the Availment object
	 * @param availment the Availment object to set the PHP values
	 * @param sibsDate the date to be used to obtain the exchange rates for the computation of the PHP values
	 * @return the Availment object with the PHP values set
	 */
	public Availment setPhpValues(Availment availment, String sibsDate){
		String currencyCode = availment.getCurrencyCode(); 
		
		//if the currency of the availment is already PHP, then the local values is the same as the set values.
		if(currencyCode.equals("PHP")){
			availment.setPhpAmount(availment.getOriginalAmount());
			availment.setPhpOutstandingBalance(availment.getOutstandingBalance());
		} else {
			
			BigDecimal conversionRate = BigDecimal.ONE;
			for(Map<String,Object> conversionMap : historicalRates){
				if(conversionMap.get("SOURCE_CURRENCY") != null && conversionMap.get("CONVERSION_RATE") != null && 
						conversionMap.get("SOURCE_CURRENCY").toString().trim().equalsIgnoreCase(currencyCode.trim())){
					conversionRate = new BigDecimal(conversionMap.get("CONVERSION_RATE").toString());
				}
			}
			System.out.println("AFTER EVALUATION CURRENCY: " + currencyCode + " CONVERSION RATE: " + conversionRate.toString());
			//checks if their is an archived version of the rates
			
			availment.setPhpAmount(availment.getOriginalAmount().multiply(conversionRate));
			availment.setPhpOutstandingBalance(availment.getOutstandingBalance().multiply(conversionRate));
			
//			//will only archive the rates if there is no archived entry for particular rate
//			if(!historicalRates.containsKey(currencyCode)){
//				historicalRates.put(currencyCode, conversionRate);
//			}
		}
		return availment;
	}

	/**
     * Sets up the TradeServiceRepository upon initialization of the class
     * @param tradeServiceRepository the TradeServiceRepository
     */
	public void setTradeServiceRepository(TradeServiceRepository tradeServiceRepository) {
		this.tradeServiceRepository = tradeServiceRepository;
	}

	/**
     * Sets up the LetterOfCreditRepository upon initialization of the class
     * @param letterOfCreditRepository the LetterOfCreditRepository
     */
	public void setLetterOfCreditRepository(LetterOfCreditRepository letterOfCreditRepository) {
		this.letterOfCreditRepository = letterOfCreditRepository;
	}
	
	/**
     * Sets up the PaymentRepository upon initialization of the class
     * @param paymentRepository the PaymentRepository
     */
	public void setPaymentRepository(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}
	
	/**
     * Sets up the BatchFacilityReearmarkService upon initialization of the class
     * @param batchFacilityReearmarkService the BatchFacilityReearmarkService
     */
	public void setBatchFacilityReearmarkService(BatchFacilityReearmarkService batchFacilityReearmarkService) {
		this.batchFacilityReearmarkService = batchFacilityReearmarkService;
	}

}
