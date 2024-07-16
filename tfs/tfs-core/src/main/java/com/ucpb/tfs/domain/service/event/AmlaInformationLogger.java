package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ucpb.tfs.application.query2.ReferenceFinder;
import com.ucpb.tfs.application.service.ConversionService;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.audit.*;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.AccountLogRepository;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.CustomerAccountLogRepository;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.CustomerLogRepository;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.TransactionLogRepository;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository;
import com.ucpb.tfs.domain.cdt.CDTRemittance;
import com.ucpb.tfs.domain.cdt.event.CDTPaymentRequestPaidEvent;
import com.ucpb.tfs.domain.cdt.event.CDTPaymentRequestUnpaidEvent;
import com.ucpb.tfs.domain.cdt.event.CDTRefundCreatedEvent;
import com.ucpb.tfs.domain.corresCharges.event.CorresChargeActualApprovedEvent;
import com.ucpb.tfs.domain.payment.*;
import com.ucpb.tfs.domain.product.*;
import com.ucpb.tfs.domain.product.enums.TradeProductStatus;
import com.ucpb.tfs.domain.product.event.*;
import com.ucpb.tfs.domain.reference.ProductReferenceRepository;
import com.ucpb.tfs.domain.reference.event.CustomerCreatedEvent;
import com.ucpb.tfs.domain.reference.event.CustomerUpdatedEvent;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.utils.ReferenceNumberGenerator;
import com.ucpb.tfs.domain.settlementaccount.MarginalDeposit;
import com.ucpb.tfs.domain.settlementaccount.event.MarginalDepositUpdatedEvent;
import com.ucpb.tfs.interfaces.services.CustomerInformationFileService;
import com.ucpb.tfs.interfaces.services.RatesService;
import com.ucpb.tfs.utils.AmlaLoggingUtil;
import com.ucpb.tfs.utils.BeanMapper;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Modified by: Rafael T. Poblete
 * Date modified: 03/22/2018
 */
public class AmlaInformationLogger {

    private TransactionLogRepository transactionLogRepository;

    private AccountLogRepository accountLogRepository;

    private CustomerAccountLogRepository customerAccountLogRepository;

    private CustomerLogRepository customerLogRepository;

    private Map<Class, BeanMapper> transactionLogMappers;

    private Map<Class, BeanMapper> accountLogMappers;

    private Map<Class, BeanMapper> customerAccountLogMappers;

    private Map<Class, BeanMapper> customerLogMappers;

    private ConversionService conversionService;

    private CustomerInformationFileService customerInformationFileService;
    
    @Autowired
    private TradeServiceRepository tradeServiceRepository;
    
    @Autowired
    private ReferenceFinder referenceFinder;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    ProductReferenceRepository productReferenceRepository;

    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository;

    @Autowired
    RatesService ratesService;

    private boolean enabled = true;

    private static final String PHP = "PHP";
    public static final String NO_BRANCH_CODE = "XXX";
    private static final int RATE_PRECISION = 6;
    private static final String CURRENT_RATE_KEY = "currentRate";
    private static final String CIF_NUMBER_FIELD_ID = "cifNumber";

    // Must be refactored
    private List<String> govtList       = new ArrayList<String>(Arrays.asList("BSP"));
    private List<String> collectionList = new ArrayList<String>(Arrays.asList("DOCSTAMPS"));
    private List<String> incomeList     = new ArrayList<String>(Arrays.asList("BC", "CF", "CILEX", "BOOKING", "CANCEL", "INTEREST"));
    private List<String> miscList       = new ArrayList<String>(Arrays.asList("CABLE", "SUP", "NOTARIAL", "POSTAGE", "COURIER", "CORRES-ADVISING",
                                                                              "CORRES-CONFIRMING", "ADVISING-EXPORT", "OTHER-EXPORT",
                                                                              "CORRES-ADDITIONAL", "CORRES-EXPORT", "REMITTANCE"));

    @EventListener
    public void logLcCreatedEvent(LetterOfCreditCreatedEvent event) {
    	
    	System.out.println("=========INSIDE logLcCreatedEvent===============");

    	
        if (enabled) {
            Assert.isTrue(transactionLogMappers.containsKey(event.getClass()), "Event type is not supported");
//            addConversionRate(event.getTradeService().getDetails(), "currency");
            event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

            // addPaymentDetails(event.getTradeService(), "paymentDetails");

//            TransactionLog log = mapEventToTransactionlog(event);
            // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
            // log.setCifBirthday(getBirthday(cifNumber));

//            persistTransactionLogDetails(log, event.getGltsNumber());
            if(!event.getTradeService().getDocumentSubType2().equals(DocumentSubType2.USANCE) && !event.getTradeService().getDocumentSubType1().equals(DocumentSubType1.STANDBY)){
            	persistAccountLogDetails(event);
            }
            // Persist for product and charges payments
            // See bug #2039 in Redmine
//            persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
        }
    }

    @EventListener
    public void logLcNegotiationEvent(LCNegotiationCreatedEvent event) {

    	System.out.println("=========INSIDE logLcNegotiationEvent===============");
    	
        Assert.isTrue(transactionLogMappers.containsKey(event.getClass()), "Event type is not supported");
        if(!event.getTradeService().getDocumentSubType2().equals(DocumentSubType2.USANCE) && !event.getTradeService().getDocumentSubType1().equals(DocumentSubType1.STANDBY)){
	     	String individualCorporateFlag = "C";
	     	
	        if(event.getTradeService().getDocumentSubType1().equals(DocumentSubType1.CASH)){
	            TradeService openingTradeService = tradeServiceRepository.load(new DocumentNumber(event.getTradeService().getTradeProductNumber().toString()), ServiceType.OPENING);           
	            TradeService cashTradeService = tradeServiceRepository.getAmlaTradeServiceOpening(event.getTradeService().getTradeProductNumber(), ServiceType.OPENING, event.getTradeService().getDocumentType(), event.getTradeService().getDocumentClass());
	 
	            String transactionCurrency = (String)event.getTradeService().getDetails().get("negotiationCurrency");
	           	BigDecimal negotiationAmount = BigDecimal.ZERO;
	           	BigDecimal outstandingBalance = event.getNegotiatedLetterOfCredit().getOutstandingBalance();
	           	
	           	if(event.getTradeService().getDetails().get("negotiationAmount") != null){
	           		negotiationAmount = new BigDecimal(event.getTradeService().getDetails().get("negotiationAmount").toString());
	           	}
	           	if(event.getTradeService().getDetails().get("outstandingBalance") != null){
	           		outstandingBalance = new BigDecimal(event.getTradeService().getDetails().get("outstandingBalance").toString());
	           		if(outstandingBalance.signum() == -1){
	           			outstandingBalance = BigDecimal.ZERO;
	            	}
	           	}          	
	           	BigDecimal totalNegotiatedAmount = event.getNegotiatedLetterOfCredit().getCashAmount().subtract(outstandingBalance);
	           	Integer numberOfPayments = 0;
	           	Integer numberOfAdjustments = 0;
	           	
	           	//CHECK IF REGULAR ADJUSTED TO FULL CASH
	           	if(openingTradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR)){
	           		System.out.println("==ADJUSTED TO FULL CASH==");
	           		Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
			    	if (productPayment != null && outstandingBalance.compareTo(BigDecimal.ZERO) == 1) {
			    		List<PaymentDetail> productPaymentList = new ArrayList<PaymentDetail>(productPayment.getDetails());
			    		if (productPaymentList != null && !productPaymentList.isEmpty()) {
		                    Collections.sort(productPaymentList, new PaymentDetailComparator());
		                    
		                    Iterator<PaymentDetail> productPaymentDetails = productPaymentList.iterator();
		                    while (productPaymentDetails.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {
				            	PaymentDetail productPaymentDetail = productPaymentDetails.next();
				            	
				            	BigDecimal paymentInOriginalCurrency = productPaymentDetail.getAmountInLcCurrency();
				            	totalNegotiatedAmount = totalNegotiatedAmount.subtract(productPaymentDetail.getAmountInLcCurrency());
				            	if(totalNegotiatedAmount.signum() == -1){
				            		totalNegotiatedAmount = BigDecimal.ZERO;
				            	}
				            	saveAmlaLcPayment(event.getTradeService(), productPaymentDetail, paymentInOriginalCurrency);
				            		
				            	event.getTradeService().getDetails().put("currentRate", getRateForCashLc(openingTradeService, productPaymentDetail, transactionCurrency));
				            	
				            	if(openingTradeService.getDetails().get("individualCorporateFlag") != null){
				             		individualCorporateFlag = (String) openingTradeService.getDetails().get("individualCorporateFlag");
				             	}
				             	
				             	if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
			             			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
			             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
			             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
			             			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
			             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
			             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
			             			} else {
			             				if(event.getNegotiatedLetterOfCredit().getBeneficiaryName() != null){
			             					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
				             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
			             				} else {
				             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
				             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
			             				}
			             			}
			             		}
				             	event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
				             	
				            	TransactionLog log = mapEventToTransactionlog(event);
				                persistTransactionLogDetails(log, event.getGltsNumber());
					                
					            negotiationAmount = negotiationAmount.subtract(paymentInOriginalCurrency);
		                    }
			    		}
			    	}
	           		List<String> listOfTradeService = tradeServiceRepository.getAllTradeServiceIdForAmla(event.getTradeService().getTradeProductNumber());
	           		
	           		if (listOfTradeService != null && !listOfTradeService.isEmpty()) {
	           			Iterator<String> listOfTradeServiceIterator = listOfTradeService.iterator();
	                    while (listOfTradeServiceIterator.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {
	                    	String tradeServiceAdjustment = listOfTradeServiceIterator.next();
			       			productPayment = paymentRepository.get(new TradeServiceId(tradeServiceAdjustment), ChargeType.PRODUCT);
					    	if (productPayment != null && outstandingBalance.compareTo(BigDecimal.ZERO) == 1) {
					    		List<PaymentDetail> productPaymentList = new ArrayList<PaymentDetail>(productPayment.getDetails());
					    		if (productPaymentList != null && !productPaymentList.isEmpty()) {
					    			numberOfAdjustments = numberOfAdjustments + 1;
				                    numberOfPayments = 0;  //sets number of payments to Zero
				                    Collections.sort(productPaymentList, new PaymentDetailComparator());
				                    
				                    Iterator<PaymentDetail> productPaymentDetails = productPaymentList.iterator();
				                    while (productPaymentDetails.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {
						            	PaymentDetail productPaymentDetail = productPaymentDetails.next();					            	
						            	BigDecimal paymentInOriginalCurrency = productPaymentDetail.getAmountInLcCurrency();
						            	paymentInOriginalCurrency = paymentInOriginalCurrency.subtract(totalNegotiatedAmount);
						            	totalNegotiatedAmount = totalNegotiatedAmount.subtract(productPaymentDetail.getAmountInLcCurrency());
						            	if(totalNegotiatedAmount.signum() == -1){
						            		totalNegotiatedAmount = BigDecimal.ZERO;
						            	}
						            	
					                    numberOfPayments = numberOfPayments + 1;
					                    
						            	if((totalNegotiatedAmount.compareTo(BigDecimal.ZERO) <= 0 && paymentInOriginalCurrency.compareTo(BigDecimal.ZERO) == 1)
						            			|| (numberOfPayments.compareTo(productPaymentList.size()) == 0 && numberOfAdjustments.compareTo(listOfTradeService.size()) == 0)){
						            		//check if nego amount is less than payment
						            		if(negotiationAmount.compareTo(paymentInOriginalCurrency) == -1 || (numberOfPayments.compareTo(productPaymentList.size()) == 0 && numberOfAdjustments.compareTo(listOfTradeService.size()) == 0)){
						            			paymentInOriginalCurrency = negotiationAmount;
						            		}
						            		
						            		saveAmlaLcPayment(event.getTradeService(), productPaymentDetail, paymentInOriginalCurrency);
						            		
						            		event.getTradeService().getDetails().put("currentRate", getRateForCashLc(openingTradeService, productPaymentDetail, transactionCurrency));
						            					             	
							             	if(openingTradeService.getDetails().get("individualCorporateFlag") != null){
							             		individualCorporateFlag = (String) openingTradeService.getDetails().get("individualCorporateFlag");
							             	}
							             	
							             	if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
						             			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
						             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
						             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
						             			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
						             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
						             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
						             			} else {
						             				if(event.getNegotiatedLetterOfCredit().getBeneficiaryName() != null){
						             					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
							             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
						             				} else {
							             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
							             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
						             				}
						             			}
						             		}
							             	event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
							             	
							            	TransactionLog log = mapEventToTransactionlog(event);
							                persistTransactionLogDetails(log, event.getGltsNumber());
							                
							             	negotiationAmount = negotiationAmount.subtract(paymentInOriginalCurrency);
						            	}
				                    }
					    		}
					    	}
	                    }
	           		}
	           	} else { 
	           		System.out.println("==CASH ONLY==");
		           	//CHECK IF MIGRATED LC
		       		if(cashTradeService == null){
		       			System.out.println("==MIGRATED CASH ONLY==");
		       			if(transactionCurrency.equalsIgnoreCase("PHP")){
			       			 event.getTradeService().getDetails().put("amlaCasaFlagPhp", "1");
			       			 event.getTradeService().getDetails().put("amlaCasaFlagFx", "0");
			       		 } else {
			       			 event.getTradeService().getDetails().put("amlaCasaFlagFx", "1");
			       			 event.getTradeService().getDetails().put("amlaCasaFlagPhp", "0");
			       		 }
			       		 event.getTradeService().getDetails().put("amlaCheckFlagPhp", "0");
			       		 event.getTradeService().getDetails().put("amlaCashFlagPhp", "0");
			       		 event.getTradeService().getDetails().put("amlaRemittanceFlagPhp", "0");
			       		 event.getTradeService().getDetails().put("amlaCheckFlagFx", "0");
			       		 event.getTradeService().getDetails().put("amlaCashFlagFx", "0");
			       		 event.getTradeService().getDetails().put("amlaRemittanceFlagFx", "0");
			       		 event.getTradeService().getDetails().put("amlaCasaFlagAmount", negotiationAmount);
			       		 event.getTradeService().getDetails().put("amlaSettlementCurrency", transactionCurrency);
			       		 event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));
			       		 
			       		TransactionLog log = mapEventToTransactionlog(event);
		                persistTransactionLogDetails(log, event.getGltsNumber());
		           	} else {
		           		System.out.println("==CASH ONLY TALAGA==");
		           		//Over Negotiation Payment for Cash
		           		Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
				    	if (productPayment != null && outstandingBalance.compareTo(BigDecimal.ZERO) == 1) {
				    		List<PaymentDetail> productPaymentList = new ArrayList<PaymentDetail>(productPayment.getDetails());
				    		if (productPaymentList != null && !productPaymentList.isEmpty()) {
			                    Collections.sort(productPaymentList, new PaymentDetailComparator());
			                    
			                    Iterator<PaymentDetail> productPaymentDetails = productPaymentList.iterator();
			                    while (productPaymentDetails.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {
					            	PaymentDetail productPaymentDetail = productPaymentDetails.next();
					            	
					            	BigDecimal paymentInOriginalCurrency = productPaymentDetail.getAmountInLcCurrency();
					            	paymentInOriginalCurrency = paymentInOriginalCurrency.subtract(totalNegotiatedAmount);
					            	totalNegotiatedAmount = totalNegotiatedAmount.subtract(productPaymentDetail.getAmountInLcCurrency());
					            	if(totalNegotiatedAmount.signum() == -1){
					            		totalNegotiatedAmount = BigDecimal.ZERO;
					            	}			            		
				            		saveAmlaLcPayment(event.getTradeService(), productPaymentDetail, paymentInOriginalCurrency);
				            		
				            	   	event.getTradeService().getDetails().put("currentRate", getRateForCashLc(event.getTradeService(), productPaymentDetail, transactionCurrency));
				            					             	
					             	if(cashTradeService.getDetails().get("individualCorporateFlag") != null){
					             		individualCorporateFlag = (String) cashTradeService.getDetails().get("individualCorporateFlag");
					             	}
					             	
					             	if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
				             			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
				             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
				             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
				             			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
				             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
				             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
				             			} else {
				             				if(event.getNegotiatedLetterOfCredit().getBeneficiaryName() != null){
				             					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
					             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
				             				} else {
					             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
					             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
				             				}
				             			}
				             		}
					             	event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
					             	
					            	TransactionLog log = mapEventToTransactionlog(event);
					                persistTransactionLogDetails(log, event.getGltsNumber());					                
				            		
					             	negotiationAmount = negotiationAmount.subtract(paymentInOriginalCurrency);
			                    }
				    		}
				    	}
				    	//Opening Payment for Cash
			            productPayment = paymentRepository.get(cashTradeService.getTradeServiceId(), ChargeType.PRODUCT);
				    	if (productPayment != null && outstandingBalance.compareTo(BigDecimal.ZERO) == 1) {
				    		List<PaymentDetail> productPaymentList = new ArrayList<PaymentDetail>(productPayment.getDetails());
				    		if (productPaymentList != null && !productPaymentList.isEmpty()) {
			                    Collections.sort(productPaymentList, new PaymentDetailComparator());
			                    
			                    Iterator<PaymentDetail> productPaymentDetails = productPaymentList.iterator();
			                    while (productPaymentDetails.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {
					            	PaymentDetail productPaymentDetail = productPaymentDetails.next();
					            	
					            	BigDecimal paymentInOriginalCurrency = productPaymentDetail.getAmountInLcCurrency();
					            	paymentInOriginalCurrency = paymentInOriginalCurrency.subtract(totalNegotiatedAmount);
					            	totalNegotiatedAmount = totalNegotiatedAmount.subtract(productPaymentDetail.getAmountInLcCurrency());
					            	if(totalNegotiatedAmount.signum() == -1){
					            		totalNegotiatedAmount = BigDecimal.ZERO;
					            	}
					            	
					            	if((totalNegotiatedAmount.compareTo(BigDecimal.ZERO) <= 0 && paymentInOriginalCurrency.compareTo(BigDecimal.ZERO) == 1)){           		
					            		//check if nego amount is less than payment
					            		if(negotiationAmount.compareTo(paymentInOriginalCurrency) == -1){
					            			paymentInOriginalCurrency = negotiationAmount;
					            		}
					            		saveAmlaLcPayment(event.getTradeService(), productPaymentDetail, paymentInOriginalCurrency);
					            		
					            	   	event.getTradeService().getDetails().put("currentRate", getRateForCashLc(openingTradeService, productPaymentDetail, transactionCurrency));
					            					             	
						             	if(cashTradeService.getDetails().get("individualCorporateFlag") != null){
						             		individualCorporateFlag = (String) cashTradeService.getDetails().get("individualCorporateFlag");
						             	}
						             	
						             	if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
					             			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
					             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
					             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
					             			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
					             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
					             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
					             			} else {
					             				if(event.getNegotiatedLetterOfCredit().getBeneficiaryName() != null){
					             					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
						             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
					             				} else {
						             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
						             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
					             				}
					             			}
					             		}
						             	event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
						             	
						            	TransactionLog log = mapEventToTransactionlog(event);
						                persistTransactionLogDetails(log, event.getGltsNumber());						                
					            		
						             	negotiationAmount = negotiationAmount.subtract(paymentInOriginalCurrency);						             	
					            	}
			                    }
				    		}
				    	}
				    	//Adjustment and Amendment Payment for Cash
				    	List<String> listOfTradeService = tradeServiceRepository.getAllTradeServiceIdForAmla(event.getTradeService().getTradeProductNumber());
				    	
		           		if (listOfTradeService != null && !listOfTradeService.isEmpty()) {
		           			Iterator<String> listOfTradeServiceIterator = listOfTradeService.iterator();
		                    while (listOfTradeServiceIterator.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {
		                    	String tradeServiceAdjustment = listOfTradeServiceIterator.next();
				       			productPayment = paymentRepository.get(new TradeServiceId(tradeServiceAdjustment), ChargeType.PRODUCT);
						    	if (productPayment != null && outstandingBalance.compareTo(BigDecimal.ZERO) == 1) {
						    		List<PaymentDetail> productPaymentList = new ArrayList<PaymentDetail>(productPayment.getDetails());
						    		if (productPaymentList != null && !productPaymentList.isEmpty()) {
					                    Collections.sort(productPaymentList, new PaymentDetailComparator());
					                    numberOfAdjustments = numberOfAdjustments + 1;
					                    numberOfPayments = 0;  //sets number of payments to Zero
					                    
					                    Iterator<PaymentDetail> productPaymentDetails = productPaymentList.iterator();					                    
					                    while (productPaymentDetails.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {
							            	PaymentDetail productPaymentDetail = productPaymentDetails.next();
							            	
							            	BigDecimal paymentInOriginalCurrency = productPaymentDetail.getAmountInLcCurrency();
							            	paymentInOriginalCurrency = paymentInOriginalCurrency.subtract(totalNegotiatedAmount);
							            	totalNegotiatedAmount = totalNegotiatedAmount.subtract(productPaymentDetail.getAmountInLcCurrency());
							            	if(totalNegotiatedAmount.signum() == -1){
							            		totalNegotiatedAmount = BigDecimal.ZERO;
							            	}
						                    numberOfPayments = numberOfPayments + 1;
							            	if((totalNegotiatedAmount.compareTo(BigDecimal.ZERO) <= 0 && paymentInOriginalCurrency.compareTo(BigDecimal.ZERO) == 1)
							            			|| (numberOfPayments.compareTo(productPaymentList.size()) == 0 && numberOfAdjustments.compareTo(listOfTradeService.size()) == 0)){
							            		//check if nego amount is less than payment
							            		if(negotiationAmount.compareTo(paymentInOriginalCurrency) == -1 || (numberOfPayments.compareTo(productPaymentList.size()) == 0 && numberOfAdjustments.compareTo(listOfTradeService.size()) == 0)){
							            			paymentInOriginalCurrency = negotiationAmount;
							            		}
							            		
							            		saveAmlaLcPayment(event.getTradeService(), productPaymentDetail, paymentInOriginalCurrency);
							            		
							            		event.getTradeService().getDetails().put("currentRate", getRateForCashLc(openingTradeService, productPaymentDetail, transactionCurrency));
							            					             	
								             	if(cashTradeService.getDetails().get("individualCorporateFlag") != null){
								             		individualCorporateFlag = (String) cashTradeService.getDetails().get("individualCorporateFlag");
								             	}
								             	
								             	if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
							             			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
							             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
							             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
							             			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
							             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
							             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
							             			} else {
							             				if(event.getNegotiatedLetterOfCredit().getBeneficiaryName() != null){
							             					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
								             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
							             				} else {
								             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
								             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
							             				}
							             			}
							             		}
								             	event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
								             	
								            	TransactionLog log = mapEventToTransactionlog(event);
								                persistTransactionLogDetails(log, event.getGltsNumber());								                
							            		
								             	negotiationAmount = negotiationAmount.subtract(paymentInOriginalCurrency);
								             	
							            	}
					                    }
						    		}
						    	}
		                    }
		           		}
	           		}
	           	}
			} else {
				TradeService tradeServiceOpening = tradeServiceRepository.getAmlaTradeServiceOpening(event.getTradeService().getTradeProductNumber(), ServiceType.OPENING, event.getTradeService().getDocumentType(), event.getTradeService().getDocumentClass());
				if(tradeServiceOpening.getDetails().get("individualCorporateFlag") != null){
	         		individualCorporateFlag = (String) tradeServiceOpening.getDetails().get("individualCorporateFlag");
	         	}
				
				System.out.println("event.getNegotiatedLetterOfCredit().getCashFlag()::::" + event.getNegotiatedLetterOfCredit().getCashFlag());
				if(event.getNegotiatedLetterOfCredit().getCashFlag() != null){
					if(event.getNegotiatedLetterOfCredit().getCashFlag()){		
						String transactionCurrency = (String)event.getTradeService().getDetails().get("negotiationCurrency");
						BigDecimal negotiationAmount = BigDecimal.ZERO;
			           	BigDecimal outstandingBalance = event.getNegotiatedLetterOfCredit().getOutstandingBalance();
			           	Integer numberOfPayments = 0;
		       			Integer numberOfAdjustments = 0;
			           	
			           	if(event.getTradeService().getDetails().get("negotiationAmount") != null){
			           		negotiationAmount = new BigDecimal(event.getTradeService().getDetails().get("negotiationAmount").toString());
			           	}
			           	
			           	if(event.getTradeService().getDetails().get("outstandingBalance") != null){
			           		outstandingBalance = new BigDecimal(event.getTradeService().getDetails().get("outstandingBalance").toString());
			           		if(outstandingBalance.signum() == -1){
			           			outstandingBalance = BigDecimal.ZERO;
			            	}
			           	}          	
		       			
			       		System.out.println("LC Cash Amount" + event.getNegotiatedLetterOfCredit().getCashAmount());
			         	System.out.println("LC Total Negotiated Cash Amount" + event.getNegotiatedLetterOfCredit().getTotalNegotiatedCashAmount());
			          	System.out.println("negotiationAmount" + event.getTradeService().getDetails().get("negotiationAmount"));
			           	System.out.println("cashAmount" + event.getTradeService().getDetails().get("cashAmount"));
			          	System.out.println("cashAmountORIG" + event.getTradeService().getDetails().get("cashAmountORIG"));
			           	System.out.println("outstandingBalance" + event.getTradeService().getDetails().get("outstandingBalance"));
		
						Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
				    	 if (productPayment != null) {
				
				             Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();
				             
				             while (productPaymentDetails.hasNext()) {
				            	PaymentDetail productPaymentDetail = productPaymentDetails.next();
				            	System.out.println("Settlement Currency" + productPaymentDetail.getCurrency().getCurrencyCode());
				            	System.out.println("Settlement Amount" + productPaymentDetail.getAmount());
				            	System.out.println("PaymentInstrumentType" + productPaymentDetail.getPaymentInstrumentType());
				            	System.out.println("Original Currency" + event.getTradeService().getDetails().get("negotiationCurrency"));
				            	System.out.println("Original Amount" + productPaymentDetail.getAmountInLcCurrency());
				            	
				            	BigDecimal paymentInOriginalCurrency = productPaymentDetail.getAmountInLcCurrency();
		
				            	 saveAmlaLcPayment(event.getTradeService(), productPaymentDetail, paymentInOriginalCurrency);
				            	 
				            	 if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")
				            			 && event.getTradeService().getDocumentSubType1().equals(DocumentSubType1.REGULAR)
				            			 && event.getTradeService().getDocumentType().equals(DocumentType.FOREIGN)){
				            		 event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRateFromPayment(event.getTradeService(), Boolean.TRUE));
				             	 } else {
				             		event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRateFromPayment(event.getTradeService(), Boolean.FALSE));
				             	 }
				            	 
				            	 if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
			             			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
			             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
			             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
			             			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
			             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
			             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
			             			} else {
			             				if(event.getNegotiatedLetterOfCredit().getBeneficiaryName() != null){
			             					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
				             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
			             				} else {
				             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
				             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
			             				}
			             			}
			             		 }
				            	 event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
				            	 
				            	 TransactionLog log = mapEventToTransactionlog(event);
				                 persistTransactionLogDetails(log, event.getGltsNumber());
				                 
				                 negotiationAmount = negotiationAmount.subtract(paymentInOriginalCurrency);
				             }
				    	 }
				    	 BigDecimal totalNegotiatedAmount = event.getNegotiatedLetterOfCredit().getTotalNegotiatedCashAmount().subtract(negotiationAmount);
				    	 
				    	 if(totalNegotiatedAmount.compareTo(event.getNegotiatedLetterOfCredit().getCashAmount()) == -1 && outstandingBalance.compareTo(BigDecimal.ZERO) == 1){
				    		 List<String> listOfTradeService = tradeServiceRepository.getAllTradeServiceIdForAmla(event.getTradeService().getTradeProductNumber());
		
			           		if (listOfTradeService != null && !listOfTradeService.isEmpty()) {	           			
			           			Iterator<String> listOfTradeServiceIterator = listOfTradeService.iterator();
			                    while (listOfTradeServiceIterator.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {                    	
			                    	String tradeServiceAdjustment = listOfTradeServiceIterator.next();
					       			productPayment = paymentRepository.get(new TradeServiceId(tradeServiceAdjustment), ChargeType.PRODUCT);
							    	if (productPayment != null && outstandingBalance.compareTo(BigDecimal.ZERO) == 1) {
							    		List<PaymentDetail> productPaymentList = new ArrayList<PaymentDetail>(productPayment.getDetails());
							    		if (productPaymentList != null && !productPaymentList.isEmpty()) {
						                    Collections.sort(productPaymentList, new PaymentDetailComparator());
						                    numberOfAdjustments = numberOfAdjustments + 1;
						                    
						                    Iterator<PaymentDetail> productPaymentDetails = productPaymentList.iterator();
						                    while (productPaymentDetails.hasNext() && negotiationAmount.compareTo(BigDecimal.ZERO) == 1) {
								            	PaymentDetail productPaymentDetail = productPaymentDetails.next();
								            	System.out.println("totalNegotiatedAmount :::::" + totalNegotiatedAmount);
								            	BigDecimal paymentInOriginalCurrency = productPaymentDetail.getAmountInLcCurrency();
								            	System.out.println("paymentInOriginalCurrency before" + paymentInOriginalCurrency);
								            	paymentInOriginalCurrency = paymentInOriginalCurrency.subtract(totalNegotiatedAmount);
								            	System.out.println("paymentInOriginalCurrency after" + paymentInOriginalCurrency);
								            	totalNegotiatedAmount = totalNegotiatedAmount.subtract(productPaymentDetail.getAmountInLcCurrency());
								            	System.out.println("totalNegotiatedAmount before" + totalNegotiatedAmount);
								            	if(totalNegotiatedAmount.signum() == -1){
								            		totalNegotiatedAmount = BigDecimal.ZERO;
								            	}
								            	System.out.println("totalNegotiatedAmount after" + totalNegotiatedAmount);
								            	System.out.println("numberOfPayments" + numberOfPayments);
								            	System.out.println("numberOfAdjustments" + numberOfAdjustments);
							                    numberOfPayments = numberOfPayments + 1;
							                    
								            	if((totalNegotiatedAmount.compareTo(BigDecimal.ZERO) <= 0 && paymentInOriginalCurrency.compareTo(BigDecimal.ZERO) == 1)
								            			|| (numberOfPayments.compareTo(productPaymentList.size()) == 0 && numberOfAdjustments.compareTo(listOfTradeService.size()) == 0)){
								            		//check if nego amount is less than payment
								            		if(negotiationAmount.compareTo(paymentInOriginalCurrency) == -1 
								            				|| (numberOfPayments.compareTo(productPaymentList.size()) == 0 && numberOfAdjustments.compareTo(listOfTradeService.size()) == 0)){
								            			paymentInOriginalCurrency = negotiationAmount;
								            		}
								            		System.out.println("paymentInOriginalCurrency before" + paymentInOriginalCurrency);
								            		saveAmlaLcPayment(event.getTradeService(), productPaymentDetail, paymentInOriginalCurrency);
								            		
								            		event.getTradeService().getDetails().put("currentRate", getRateForCashLc(tradeServiceOpening, productPaymentDetail, transactionCurrency));
								            		System.out.println("currentRate" + event.getTradeService().getDetails().get("currentRate"));
								            		
									             	if(tradeServiceOpening.getDetails().get("individualCorporateFlag") != null){
									             		individualCorporateFlag = (String) tradeServiceOpening.getDetails().get("individualCorporateFlag");
									             	}
									             	
								             		if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
								             			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
								             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
								             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
								             			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
								             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
								             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
								             			} else {
								             				if(event.getNegotiatedLetterOfCredit().getBeneficiaryName() != null){
								             					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
									             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
								             				} else {
									             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
									             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
								             				}
								             			}
								             		}
									             	event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
									             	
									            	TransactionLog log = mapEventToTransactionlog(event);
									                persistTransactionLogDetails(log, event.getGltsNumber());
									                
									             	negotiationAmount = negotiationAmount.subtract(paymentInOriginalCurrency);
									             	System.out.println("paymentInOriginalCurrency after" + paymentInOriginalCurrency);
								            	}
						                    }
							    		}
							    	}
			                    }
			           		}
		           		}
					}
				} else {
			    	Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
			    	 if (productPayment != null) {
			
			             Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();
			             
			             while (productPaymentDetails.hasNext()) {
			            	 PaymentDetail productPaymentDetail = productPaymentDetails.next();
			            	 
			            	 saveAmlaLcPayment(event.getTradeService(), productPaymentDetail, productPaymentDetail.getAmountInLcCurrency());
			            	 
			            	 if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")
			            			 && event.getTradeService().getDocumentSubType1().equals(DocumentSubType1.REGULAR)
			            			 && event.getTradeService().getDocumentType().equals(DocumentType.FOREIGN)){
			            		 event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRateFromPayment(event.getTradeService(), Boolean.TRUE));
			             	 } else {
			             		event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRateFromPayment(event.getTradeService(), Boolean.FALSE));
			             	 }
			            	 
			            	 if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
		             			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
		             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
		             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
		             			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
		             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
		             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
		             			} else {
		             				if(event.getNegotiatedLetterOfCredit().getBeneficiaryName() != null){
		             					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
			             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getBeneficiaryName().toUpperCase()));
		             				} else {
			             				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));
			             				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getNegotiatedLetterOfCredit().getExporterName().toUpperCase()));  
		             				}
		             			}
			             	 }
			            	 event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
			            	 
			            	 TransactionLog log = mapEventToTransactionlog(event);
			                 persistTransactionLogDetails(log, event.getGltsNumber());
			             }
			    	 }
				}			
			}
	        persistAccountLogDetails(event);
        
        }

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    public BigDecimal getRateForCashLc(TradeService openingTradeService, PaymentDetail productPaymentDetail, String transactionCurrency){
    	BigDecimal rate = BigDecimal.ZERO;
    	
	   	 if (transactionCurrency != null && !"PHP".equals(transactionCurrency.trim())) {
	            BigDecimal thirdToUsdRate = BigDecimal.ONE;
	            BigDecimal usdToPhpRate = BigDecimal.ZERO;
	            
         if ("USD".equals(transactionCurrency)) {
           	 thirdToUsdRate = BigDecimal.ONE;
         } else {
           	 if (productPaymentDetail.getSpecialRateThirdToUsd() != null) {
           		 thirdToUsdRate = productPaymentDetail.getSpecialRateThirdToUsd();
           	 } else if (openingTradeService.getSpecialRateThirdToUsd() != null){
           		 thirdToUsdRate = openingTradeService.getSpecialRateThirdToUsd();
           	 } else {
           		 thirdToUsdRate = BigDecimal.ONE;
           	 }
         } 
         
     	 if (productPaymentDetail.getUrr() != null) {
     		usdToPhpRate = productPaymentDetail.getUrr();
         } else if (openingTradeService.getSpecialRateUrr() != null){
         	usdToPhpRate = openingTradeService.getSpecialRateUrr();
         } else {
         	usdToPhpRate = BigDecimal.ZERO;
         }
         	rate = thirdToUsdRate.multiply(usdToPhpRate);
	   	 }
	   	 
    	return rate;
    }

    public void saveAmlaLcPayment(TradeService negotiationTradeService, PaymentDetail productPaymentDetail, BigDecimal paymentInOriginalCurrency){
		negotiationTradeService.getDetails().put("amlaCasaFlagPhp", "0");
		negotiationTradeService.getDetails().put("amlaCasaFlagFx", "0");
  		negotiationTradeService.getDetails().put("amlaCheckFlagPhp", "0");
  		negotiationTradeService.getDetails().put("amlaCashFlagPhp", "0");
  		negotiationTradeService.getDetails().put("amlaRemittanceFlagPhp", "0");
  		negotiationTradeService.getDetails().put("amlaCheckFlagFx", "0");
  		negotiationTradeService.getDetails().put("amlaCashFlagFx", "0");
  		negotiationTradeService.getDetails().put("amlaRemittanceFlagFx", "0");

    	if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASA) || productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.MD)){
	   		 if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
	   			negotiationTradeService.getDetails().put("amlaCasaFlagPhp", "1");
	   			negotiationTradeService.getDetails().put("amlaCasaFlagFx", "0");
	   		 } else {
	   			negotiationTradeService.getDetails().put("amlaCasaFlagFx", "1");
	   			negotiationTradeService.getDetails().put("amlaCasaFlagPhp", "0");
	   		 }
	   		 negotiationTradeService.getDetails().put("amlaCheckFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCashFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaRemittanceFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCheckFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaCashFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaRemittanceFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaCasaFlagAmount", paymentInOriginalCurrency.toString());
	   		 negotiationTradeService.getDetails().put("amlaSettlementCurrency", productPaymentDetail.getCurrency().getCurrencyCode());
	   	 }
	   	 if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASH)){
	   		 if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
	   			negotiationTradeService.getDetails().put("amlaCashFlagPhp", "1");
	   			negotiationTradeService.getDetails().put("amlaCashFlagFx", "0");
	   		 } else {
	   			negotiationTradeService.getDetails().put("amlaCashFlagFx", "1");
	   			negotiationTradeService.getDetails().put("amlaCashFlagPhp", "0");
	   		 }
	   		 negotiationTradeService.getDetails().put("amlaCasaFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCheckFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaRemittanceFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCasaFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaCheckFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaRemittanceFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaCashFlagAmount", paymentInOriginalCurrency.toString());
	   		 negotiationTradeService.getDetails().put("amlaSettlementCurrency", productPaymentDetail.getCurrency().getCurrencyCode());
	   	 }
	   	 if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CHECK)){
	   		 if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
	   			negotiationTradeService.getDetails().put("amlaCheckFlagPhp", "1");
	   			negotiationTradeService.getDetails().put("amlaCheckFlagFx", "0");
	   		 } else {
	   			negotiationTradeService.getDetails().put("amlaCheckFlagFx", "1");
	   			negotiationTradeService.getDetails().put("amlaCheckFlagPhp", "0");
	   		 }
	   		 negotiationTradeService.getDetails().put("amlaCasaFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCashFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaRemittanceFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCasaFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaCashFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaRemittanceFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaCheckFlagAmount", paymentInOriginalCurrency.toString());
	   		 negotiationTradeService.getDetails().put("amlaSettlementCurrency", productPaymentDetail.getCurrency().getCurrencyCode());
	   	 }
	   	 if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE)
	   			|| productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.IBT_BRANCH)
	   			|| productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.MC_ISSUANCE)
	   			|| productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.PDDTS)){
	   		 if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
	   			negotiationTradeService.getDetails().put("amlaRemittanceFlagPhp", "1");
	   			negotiationTradeService.getDetails().put("amlaRemittanceFlagFx", "0");
	   		 } else {
	   			negotiationTradeService.getDetails().put("amlaRemittanceFlagFx", "1");
	   			negotiationTradeService.getDetails().put("amlaRemittanceFlagPhp", "0");
	   		 }
	   		 negotiationTradeService.getDetails().put("amlaCasaFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCheckFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCashFlagPhp", "0");
	   		 negotiationTradeService.getDetails().put("amlaCasaFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaCheckFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaCashFlagFx", "0");
	   		 negotiationTradeService.getDetails().put("amlaRemittanceFlagAmount", paymentInOriginalCurrency.toString());
	   		 negotiationTradeService.getDetails().put("amlaSettlementCurrency", productPaymentDetail.getCurrency().getCurrencyCode());
	   	 }		 	 			            	 
    }
    
    private String getFirstBeneficiaryName(String beneficiaryName) {
    	int i = beneficiaryName.indexOf(' ');
    	
        if (i > -1) { 								// Check if there is more than one word.
          return beneficiaryName.substring(0, i); 	// Extract first word.
        } else {
          return beneficiaryName; 					// Text is the first word itself.
        }
    }
    
    private String getLastBeneficiaryName(String beneficiaryName) {
    	int i = beneficiaryName.indexOf(' ');
    	
        if (i > -1) { 								// Check if there is more than one word.
          return beneficiaryName.substring(i); 		// Extract remaining words after first word.
        } else {
          return beneficiaryName; 					// Text is the word itself.
        }
    }
    
    /*
    @EventListener
    public void logUaLoanAdjustedEvent(UALoanAdjustedEvent event) {
    	Assert.isTrue(transactionLogMappers.containsKey(event.getClass()), "Event type is not supported");
    	
    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	// log.setCifBirthday(getBirthday(cifNumber));
    	
    	persistTransactionLogDetails(log, event.getGltsNumber());
    	persistAccountLogDetails(event);
    	
    	// Persist for product and charges payments
    	// See bug #2039 in Redmine
    	persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logUaLoanPaidEvent(UALoanPaidEvent event) {
    	Assert.isTrue(transactionLogMappers.containsKey(event.getClass()), "Event type is not supported");
    	
    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	// log.setCifBirthday(getBirthday(cifNumber));
    	
    	persistTransactionLogDetails(log, event.getGltsNumber());
    	persistAccountLogDetails(event);
    	
    	// Persist for product and charges payments
    	// See bug #2039 in Redmine
    	persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    */

    @EventListener
    public void logLcAmendedEvent(LCAmendedEvent event) {

    	System.out.println("=========INSIDE logLcAmendedEvent===============");

        try {

//            addConversionRate(event.getTradeService().getDetails(), "negotiationCurrency");

            event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

            // addPaymentDetails(event.getTradeService(), "paymentDetails");

            TransactionLog log = mapEventToTransactionlog(event);

            // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
            // log.setCifBirthday(getBirthday(cifNumber));

            TradeService tradeService = event.getTradeService();

            Map<String, Object> detailsAmend = tradeService.getDetails();

            if (detailsAmend.get("amountSwitch") != null && (!((String)detailsAmend.get("amountSwitch")).equals("")) && (!((String)detailsAmend.get("amountSwitch")).toLowerCase().equals("off"))) {

                if (detailsAmend.get("amountTo") != null && (!((String)detailsAmend.get("amountTo")).equals(""))) {

                    String amountFrom = (String)detailsAmend.get("amountFrom");
                    String amountTo = (String)detailsAmend.get("amountTo");

                    System.out.println("\n>>>>> AMLA LCAmendedEvent");
                    System.out.println("amountFrom = " + amountFrom);
                    System.out.println("amountTo = " + amountTo + "\n");

                    BigDecimal amountFromBd = new BigDecimal(amountFrom.replaceAll(",",""));
                    BigDecimal amountToBd = new BigDecimal(amountTo.replaceAll(",",""));

                    // Increase
                    if (amountToBd.compareTo(amountFromBd) > 0) {

                        BigDecimal increase = amountToBd.subtract(amountFromBd);
                        if (log.getSettlementCurrency().getCurrencyCode().equals("PHP")) {
                            log.setSettlementAmount(BigDecimal.ZERO);
                        } else {
                            log.setSettlementAmount(increase);
                        }

                        BigDecimal transactionAmount = AmlaLoggingUtil.getTransactionAmount(log.getSettlementCurrency().getCurrencyCode(), increase.toPlainString(), log.getExchangeRate());
                        log.setTransactionAmount(transactionAmount);

                        log.setTransactionType(TransactionType.CREDIT.toString());

                    // Decrease
                    } else if (amountFromBd.compareTo(amountToBd) > 0) {

                        BigDecimal decrease = amountFromBd.subtract(amountToBd);

                        if (log.getSettlementCurrency().getCurrencyCode().equals("PHP")) {
                            log.setSettlementAmount(BigDecimal.ZERO);
                        } else {
                            log.setSettlementAmount(decrease);
                        }

                        BigDecimal transactionAmount = AmlaLoggingUtil.getTransactionAmount(log.getSettlementCurrency().getCurrencyCode(), decrease.toPlainString(), log.getExchangeRate());
                        log.setTransactionAmount(transactionAmount);

                        log.setTransactionType(TransactionType.DEBIT.toString());
                    }

                    // If amount is amended, report contingent (use same Tran Code as CMISC) and charges.
                    // Report increase (Credit) or decrease (Debit).
                    //persistTransactionLogDetails(log, event.getGltsNumber());
                }
            }
            if(!event.getTradeService().getDocumentSubType2().equals(DocumentSubType2.USANCE) && !event.getTradeService().getDocumentSubType1().equals(DocumentSubType1.STANDBY)){
            	persistAccountLogDetails(event);
            }

            // Persist for product and charges payments
            // See bug #2039 in Redmine
            //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logLcAmendedEvent()");
            e.printStackTrace();
        }
    }

    @EventListener
    public void logLcAdjustedEvent(LCAdjustedEvent event) {

    	System.out.println("=========INSIDE logLcAdjustedEvent===============");
    	
        try {

//            addConversionRate(event.getTradeService().getDetails(), "negotiationCurrency");

            TradeService tradeService = event.getTradeService();

            String cifNumberFrom = (String)tradeService.getDetails().get("cifNumberFrom");
            String cifNumberTo = (String)tradeService.getDetails().get("cifNumberTo");

            if (cifNumberFrom != null && cifNumberTo != null) {

                System.out.println("\n>>>>> AMLA LCAdjustedEvent");
                System.out.println("cifNumberFrom = " + cifNumberFrom);
                System.out.println("cifNumberTo = " + cifNumberTo + "\n");

                // If there is change in CIF, log in Account and CustomerAccount ONLY
                if (!cifNumberFrom.trim().equals(cifNumberTo.trim())) {

                    event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

                    // addPaymentDetails(event.getTradeService(), "paymentDetails");

                    TransactionLog log = mapEventToTransactionlog(event);
                    // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
                    // log.setCifBirthday(getBirthday(cifNumber));

                    // persistTransactionLogDetails(log, event.getGltsNumber());
                    if(!event.getTradeService().getDocumentSubType2().equals(DocumentSubType2.USANCE) && !event.getTradeService().getDocumentSubType1().equals(DocumentSubType1.STANDBY)){
                    	persistAccountLogDetails(event);
                    }

                    // Persist for product and charges payments
                    // See bug #2039 in Redmine
                    //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
                }
            }

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logLcAdjustedEvent()");
            e.printStackTrace();
        }
    }

    @EventListener
    public void logLcCancelledEvent(LCCancelledEvent event) {
        Assert.isTrue(transactionLogMappers.containsKey(event.getClass()), "Event type is not supported");
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        
        String individualCorporateFlag = "C";
        TradeService openingTradeService = tradeServiceRepository.load(new DocumentNumber(event.getTradeService().getTradeProductNumber().toString()), ServiceType.OPENING);
        
        if(openingTradeService.getDetails().get("individualCorporateFlag") != null){
     		individualCorporateFlag = (String) openingTradeService.getDetails().get("individualCorporateFlag");
     	}
        
        if(individualCorporateFlag.trim().equalsIgnoreCase("I")){
			if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("DOMESTIC")){
				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getLetterOfCredit().getBeneficiaryName().toUpperCase()));
				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getLetterOfCredit().getBeneficiaryName().toUpperCase()));
			} else if(event.getTradeService().getDetails().get("documentType").toString().equalsIgnoreCase("FOREIGN")){
				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getLetterOfCredit().getExporterName().toUpperCase()));
				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getLetterOfCredit().getExporterName().toUpperCase()));  
			} else {
				if(event.getLetterOfCredit().getBeneficiaryName() != null){
					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getLetterOfCredit().getBeneficiaryName().toUpperCase()));
	 				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getLetterOfCredit().getBeneficiaryName().toUpperCase()));
				} else {
	 				event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getLetterOfCredit().getExporterName().toUpperCase()));
	 				event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getLetterOfCredit().getExporterName().toUpperCase()));  
				}
			}
     	}
     	event.getTradeService().getDetails().put("individualCorporateFlag", individualCorporateFlag);
     	
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }


    @EventListener
    public void logIndemnityCreatedEvent(IndemnityCreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

/*
    @EventListener
    public void logIndemnityCancelledEvent(IndemnityCancelledEvent event) {
    	//            addConversionRate(event.getTradeService().getDetails(), "currency");
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	// log.setCifBirthday(getBirthday(cifNumber));
    	
    	persistTransactionLogDetails(log, event.getGltsNumber());
    	persistAccountLogDetails(event);
    	
    	// Persist for product and charges payments
    	// See bug #2039 in Redmine
    	persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
*/

    // Non-LC DA
    /* Commented out; should only log AMLA when Accepted
    @EventListener
    public void logDaCreatedEvent(DACreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    */

    @EventListener
    public void logDaAcceptedEvent(DAAcceptedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

//        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

//        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
//        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logDaSettlementCreatedEvent(DASettlementCreatedEvent event) {
    	

        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        //event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.FALSE));
    	Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
   	 	if (productPayment != null) {
            Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();
            
            while (productPaymentDetails.hasNext()) {
	           	PaymentDetail productPaymentDetail = productPaymentDetails.next();
	           	saveAmlaNonLcTransaction(event.getTradeService(), productPaymentDetail);
		        // addPaymentDetails(event.getTradeService(), "paymentDetails");
		
		        if(event.getTradeService().getDetails().get("individualCorporateFlag").toString().trim().equalsIgnoreCase("I")){
					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getTradeService().getDetails().get("beneficiaryName").toString().toUpperCase()));
					event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getTradeService().getDetails().get("beneficiaryName").toString().toUpperCase()));
		     	}
		        BigDecimal negoAmount = new BigDecimal(event.getTradeService().getDetails().get("amount").toString().replaceAll(",", "")); // nego amt
	        	BigDecimal settlementAmount = productPaymentDetail.getAmount(); // settlement amt
	        	BigDecimal accountBal = new BigDecimal(0);
		        if(productPaymentDetail.getPaymentInstrumentType() == PaymentInstrumentType.TR_LOAN){
		        	if (negoAmount.compareTo(settlementAmount) == 0) { 
		        		event.getTradeService().getDetails().put("accountBalance", BigDecimal.ZERO);
		        	} else if( negoAmount.compareTo(settlementAmount) > 0 ){
		        		accountBal = negoAmount.subtract(settlementAmount);
		        		event.getTradeService().getDetails().put("accountBalance", accountBal);
		        	}
		        	persistAccountLogDetails(event);
		        } else {
		        	event.getTradeService().getDetails().put("transactionAmount", settlementAmount); 
		        	event.getTradeService().getDetails().put("accountBalance", negoAmount);
		        	TransactionLog log = mapEventToTransactionlog(event);
			        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
			        // log.setCifBirthday(getBirthday(cifNumber));
			        persistTransactionLogDetails(log, event.getGltsNumber()); 
		        	persistAccountLogDetails(event);
		        }
	   	 	}
   	 	}
        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logDaCancelledEvent(DACancelledEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    // Non-LC DP
    @EventListener
    public void logDpCreatedEvent(DPCreatedEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        //TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

//        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
//        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logDpSettlementCreatedEvent(DPSettlementCreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        //event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.FALSE));
    	Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
   	 	if (productPayment != null) {
            Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();
            
            while (productPaymentDetails.hasNext()) {         		
	           	PaymentDetail productPaymentDetail = productPaymentDetails.next();
	           	saveAmlaNonLcTransaction(event.getTradeService(), productPaymentDetail);
		        // addPaymentDetails(event.getTradeService(), "paymentDetails");
	           	
		        if(event.getTradeService().getDetails().get("individualCorporateFlag") != null){
			        if(event.getTradeService().getDetails().get("individualCorporateFlag").toString().trim().equalsIgnoreCase("I")){
						event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getTradeService().getDetails().get("beneficiaryName").toString().toUpperCase()));
						event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getTradeService().getDetails().get("beneficiaryName").toString().toUpperCase()));
			     	}
		        }
		        BigDecimal negoAmount = new BigDecimal(event.getTradeService().getDetails().get("amount").toString().replaceAll(",", "")); // nego amt
	        	BigDecimal settlementAmount = productPaymentDetail.getAmount(); // settlement amt
	        	BigDecimal accountBal = new BigDecimal(0);
		        if(productPaymentDetail.getPaymentInstrumentType() == PaymentInstrumentType.TR_LOAN){
		        	if (negoAmount.compareTo(settlementAmount) == 0) { 
		        		event.getTradeService().getDetails().put("accountBalance", BigDecimal.ZERO);
		        	} else if( negoAmount.compareTo(settlementAmount) > 0 ){
		        		accountBal = negoAmount.subtract(settlementAmount);
		        		event.getTradeService().getDetails().put("accountBalance", accountBal);
		        	}
		        	persistAccountLogDetails(event);
		        } else {
		        	event.getTradeService().getDetails().put("transactionAmount", settlementAmount); 
		        	event.getTradeService().getDetails().put("accountBalance", negoAmount);
		        	TransactionLog log = mapEventToTransactionlog(event);
			        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
			        // log.setCifBirthday(getBirthday(cifNumber));
			        persistTransactionLogDetails(log, event.getGltsNumber()); 
		        	persistAccountLogDetails(event);
		        }
            }
   	 	}
        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logDpCancelledEvent(DPCancelledEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    // Non-LC DR 
    @EventListener
    public void logDirectRemittanceCreatedEvent(DRCreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

//        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

//        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
//        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logDirectRemittanceSettlementCreatedEvent(DRSettlementCreatedEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
    // event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.FALSE));
    	Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
   	 	if (productPayment != null) {
            Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();
            
            while (productPaymentDetails.hasNext()) {         		
	           	PaymentDetail productPaymentDetail = productPaymentDetails.next();
	           	saveAmlaNonLcTransaction(event.getTradeService(), productPaymentDetail);
		        // addPaymentDetails(event.getTradeService(), "paymentDetails");
	
			    if(event.getTradeService().getDetails().get("individualCorporateFlag").toString().trim().equalsIgnoreCase("I")){
					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getTradeService().getDetails().get("beneficiaryName").toString().toUpperCase()));
					event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getTradeService().getDetails().get("beneficiaryName").toString().toUpperCase()));
			 	}
			    BigDecimal negoAmount = new BigDecimal(event.getTradeService().getDetails().get("amount").toString().replaceAll(",", "")); // nego amt
	        	BigDecimal settlementAmount = productPaymentDetail.getAmount(); // settlement amt
	        	BigDecimal accountBal = new BigDecimal(0);
		        if(productPaymentDetail.getPaymentInstrumentType() == PaymentInstrumentType.TR_LOAN){
		        	if (negoAmount.compareTo(settlementAmount) == 0) { 
		        		event.getTradeService().getDetails().put("accountBalance", BigDecimal.ZERO);
		        	} else if( negoAmount.compareTo(settlementAmount) > 0 ){
		        		accountBal = negoAmount.subtract(settlementAmount);
		        		event.getTradeService().getDetails().put("accountBalance", accountBal);
		        	}
		        	persistAccountLogDetails(event);
		        } else {
		        	event.getTradeService().getDetails().put("transactionAmount", settlementAmount); 
		        	event.getTradeService().getDetails().put("accountBalance", negoAmount);
		        	TransactionLog log = mapEventToTransactionlog(event);
			        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
			        // log.setCifBirthday(getBirthday(cifNumber));
			        persistTransactionLogDetails(log, event.getGltsNumber()); 
		        	persistAccountLogDetails(event);
		        }
            }
   	 	}

 	// Persist for product and charges payments
  	// See bug #2039 in Redmine
  	//persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logDirectRemittanceCancelledEvent(DRCancelledEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    // Non-LC OA
    @EventListener
    public void logOACreatedEvent(OACreatedEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

//        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

//        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
//        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logOASettlementCreatedEvent(OASettlementCreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        //event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.FALSE));
    	Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
   	 	if (productPayment != null) {
            Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();
            
            while (productPaymentDetails.hasNext()) {         		
	           	PaymentDetail productPaymentDetail = productPaymentDetails.next();
	           	saveAmlaNonLcTransaction(event.getTradeService(), productPaymentDetail);
		        // addPaymentDetails(event.getTradeService(), "paymentDetails");
	
		        if(event.getTradeService().getDetails().get("individualCorporateFlag").toString().trim().equalsIgnoreCase("I")){
					event.getTradeService().getDetails().put("beneficiaryFirstName", getFirstBeneficiaryName(event.getTradeService().getDetails().get("beneficiaryName").toString().toUpperCase()));
					event.getTradeService().getDetails().put("beneficiaryLastName", getLastBeneficiaryName(event.getTradeService().getDetails().get("beneficiaryName").toString().toUpperCase()));
		     	}
		        BigDecimal negoAmount = new BigDecimal(event.getTradeService().getDetails().get("amount").toString().replaceAll(",", "")); // nego amt
	        	BigDecimal settlementAmount = productPaymentDetail.getAmount(); // settlement amt
	        	BigDecimal accountBal = new BigDecimal(0);
		        if(productPaymentDetail.getPaymentInstrumentType() == PaymentInstrumentType.TR_LOAN){
		        	if (negoAmount.compareTo(settlementAmount) == 0) { 
		        		event.getTradeService().getDetails().put("accountBalance", BigDecimal.ZERO);
		        	} else if( negoAmount.compareTo(settlementAmount) > 0 ){
		        		accountBal = negoAmount.subtract(settlementAmount);
		        		event.getTradeService().getDetails().put("accountBalance", accountBal);
		        	}
		        	persistAccountLogDetails(event);
		        } else {
		        	event.getTradeService().getDetails().put("transactionAmount", settlementAmount); 
		        	event.getTradeService().getDetails().put("accountBalance", negoAmount);
		        	TransactionLog log = mapEventToTransactionlog(event);
			        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
			        // log.setCifBirthday(getBirthday(cifNumber));
			        persistTransactionLogDetails(log, event.getGltsNumber()); 
		        	persistAccountLogDetails(event);
		        }
            }
   	 	}

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logOACancelledEvent(OACancelledEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    // Bills for Collection
    @EventListener
    public void logBCCreatedEvent(BCNegotiatedEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        ExportBills exportBills = event.getExportBills();
        Date inceptionDate = null;
        Date maturityDate = null;
        if (exportBills.getPaymentMode() != null) {
            // LC
            if (exportBills.getPaymentMode().trim().equalsIgnoreCase("LC") && exportBills.getLcDetails() != null) {
                inceptionDate = exportBills.getLcDetails().getLcIssueDate();
                maturityDate = exportBills.getLcDetails().getLcExpiryDate();
                log.setInceptionDate(inceptionDate);
                log.setMaturityDate(maturityDate);
            // Non-LC
            } else {
                inceptionDate = exportBills.getNegotiationDate();
                if (exportBills.getNonLcDetails() != null) {
                    maturityDate = exportBills.getNonLcDetails().getDueDate();
                }
                log.setInceptionDate(inceptionDate);
                log.setMaturityDate(maturityDate);
            }
        }

        //persistTransactionLogDetails(log, event.getGltsNumber());

        AccountLog accountLog = mapEventToAccountLog(event);
        // accountLog.setOpeningDate(inceptionDate);

        persistAccountLogDetails(event, accountLog);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logBCSettledEvent(BCSettledPriorBCEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
    	String transactionCurrency ="PHP";
        
    	if(event.getTradeService().getDetails().get("currency") != null){
    		transactionCurrency = (String) event.getTradeService().getDetails().get("currency");
    	}
		
    	if(event.getTradeService().getDocumentType().equals(DocumentType.FOREIGN)){
	    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), transactionCurrency, Boolean.TRUE));
	
	        // addPaymentDetails(event.getTradeService(), "paymentDetails");
	    	Payment productPayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.SETTLEMENT);
	   	 	if (productPayment != null) {
	
	   	 		event.getTradeService().getDetails().put("amlaIBTFlagPhp", "0");
				event.getTradeService().getDetails().put("amlaIBTFlagFx", "0");
				event.getTradeService().getDetails().put("amlaIBTFlagLc", "0");
	   			event.getTradeService().getDetails().put("amlaIBTFlagNLc", "0");
	   			event.getTradeService().getDetails().put("amlaIBTFlag", "0");
	       		event.getTradeService().getDetails().put("amlaIBTFlagFxCurrency", "PHP");
	       		event.getTradeService().getDetails().put("amlaIBTFlagAmount", "0.00");
				event.getTradeService().getDetails().put("amlaCasaFlagPhp", "0");
	   			event.getTradeService().getDetails().put("amlaCasaFlagFx", "0");
	   			event.getTradeService().getDetails().put("amlaCasaFlagLc", "0");
	   			event.getTradeService().getDetails().put("amlaCasaFlagNlc", "0");
	   			event.getTradeService().getDetails().put("amlaCasaFlag", "0");
	       		event.getTradeService().getDetails().put("amlaCasaFlagFxCurrency", "PHP");
	       		event.getTradeService().getDetails().put("amlaCasaFlagAmount", "0.00");
	       		event.getTradeService().getDetails().put("amlaCasaFlagAccountNo", "");
	       		
	            Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();
	
	            while (productPaymentDetails.hasNext()) {
	           	 PaymentDetail productPaymentDetail = productPaymentDetails.next();
		           	if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASA)){
		           		if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
		           			event.getTradeService().getDetails().put("amlaCasaFlagPhp", "1");
		           			event.getTradeService().getDetails().put("amlaCasaFlagFx", "0");
		           		} else {
		           			event.getTradeService().getDetails().put("amlaCasaFlagPhp", "0");
		           			event.getTradeService().getDetails().put("amlaCasaFlagFx", "1");
		           		}
		           		
		           		if(event.getExportBills().getPaymentMode().trim().equalsIgnoreCase("LC")){
		           			event.getTradeService().getDetails().put("amlaCasaFlagLc", "1");
		           			event.getTradeService().getDetails().put("amlaCasaFlagNlc", "0");
		           		} else {
		           			event.getTradeService().getDetails().put("amlaCasaFlagLc", "0");
		           			event.getTradeService().getDetails().put("amlaCasaFlagNlc", "1");
		           		}
		           		event.getTradeService().getDetails().put("amlaCasaFlag", "1");
		           		event.getTradeService().getDetails().put("amlaCasaFlagFxCurrency", productPaymentDetail.getCurrency().getCurrencyCode());
		           		event.getTradeService().getDetails().put("amlaCasaFlagAmount", productPaymentDetail.getAmount().toString());
		           		event.getTradeService().getDetails().put("amlaCasaFlagAccountNo", productPaymentDetail.getReferenceNumber().toString());
		           	}
		           	
		           	if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASH)){
		           		if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
		           			event.getTradeService().getDetails().put("amlaCashFlagPhp", "1");
		           			event.getTradeService().getDetails().put("amlaCashFlagFx", "0");
		           		} else {
		           			event.getTradeService().getDetails().put("amlaCashFlagPhp", "0");
		           			event.getTradeService().getDetails().put("amlaCashFlagFx", "1");
		           		}
		           		
		           		if(event.getExportBills().getPaymentMode().trim().equalsIgnoreCase("LC")){
		           			event.getTradeService().getDetails().put("amlaCashFlagLc", "1");
		           			event.getTradeService().getDetails().put("amlaCashFlagNlc", "0");
		           		} else {
		           			event.getTradeService().getDetails().put("amlaCashFlagLc", "0");
		           			event.getTradeService().getDetails().put("amlaCashFlagNlc", "1");
		           		}
		           		event.getTradeService().getDetails().put("amlaCashFlag", "1");
		           		event.getTradeService().getDetails().put("amlaCashFlagFxCurrency", productPaymentDetail.getCurrency().getCurrencyCode());
		           		event.getTradeService().getDetails().put("amlaCashFlagAmount", productPaymentDetail.getAmount().toString());
		           	}
		           	
		            if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.IBT_BRANCH)
		            		|| productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CHECK)
		            		|| productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.MC_ISSUANCE)
		            		|| productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.PDDTS)){
		           		if(productPaymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
		           			event.getTradeService().getDetails().put("amlaIBTFlagPhp", "1");
		           			event.getTradeService().getDetails().put("amlaIBTFlagFx", "0");
		           		} else {
		           			event.getTradeService().getDetails().put("amlaIBTFlagPhp", "0");
		           			event.getTradeService().getDetails().put("amlaIBTFlagFx", "1");
		           		}
		           		
		           		if(event.getExportBills().getPaymentMode().trim().equalsIgnoreCase("LC")){
		           			event.getTradeService().getDetails().put("amlaIBTFlagLc", "1");
		           			event.getTradeService().getDetails().put("amlaIBTFlagNLc", "0");
		           		} else {
		           			event.getTradeService().getDetails().put("amlaIBTFlagLc", "0");
		           			event.getTradeService().getDetails().put("amlaIBTFlagNLc", "1");
		           		}
		           		event.getTradeService().getDetails().put("amlaIBTFlag", "1");
		           		event.getTradeService().getDetails().put("amlaIBTFlagFxCurrency", productPaymentDetail.getCurrency().getCurrencyCode());
		           		event.getTradeService().getDetails().put("amlaIBTFlagAmount", productPaymentDetail.getAmount().toString());
					}
	            }
	   	 	}
	           	 
	        TransactionLog log = mapEventToTransactionlog(event);
	    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
	    	// log.setCifBirthday(getBirthday(cifNumber));
	
	        ExportBills exportBills = event.getExportBills();
	        Date inceptionDate = null;
	        Date maturityDate = null;
	        if (exportBills.getPaymentMode() != null) {
	            // LC
	            if (exportBills.getPaymentMode().trim().equalsIgnoreCase("LC") && exportBills.getLcDetails() != null) {
	                inceptionDate = exportBills.getLcDetails().getLcIssueDate();
	                maturityDate = exportBills.getLcDetails().getLcExpiryDate();
	                log.setInceptionDate(inceptionDate);
	                log.setMaturityDate(maturityDate);
	                // Non-LC
	            } else {
	                inceptionDate = exportBills.getNegotiationDate();
	                if (exportBills.getNonLcDetails() != null) {
	                    maturityDate = exportBills.getNonLcDetails().getDueDate();
	                }
	                log.setInceptionDate(inceptionDate);
	                log.setMaturityDate(maturityDate);
	            }
	        }
	
	        persistTransactionLogDetails(log, event.getGltsNumber());
	
	        AccountLog accountLog = mapEventToAccountLog(event);
	        accountLog.setOpeningDate(inceptionDate);
	
	        persistAccountLogDetails(event, accountLog);
		}
        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logBCCancellledEvent(BCCancelledEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        ExportBills exportBills = event.getExportBills();
        Date inceptionDate = null;
        Date maturityDate = null;
        if (exportBills.getPaymentMode() != null) {
            // LC
            if (exportBills.getPaymentMode().trim().equalsIgnoreCase("LC") && exportBills.getLcDetails() != null) {
                inceptionDate = exportBills.getLcDetails().getLcIssueDate();
                maturityDate = exportBills.getLcDetails().getLcExpiryDate();
                log.setInceptionDate(inceptionDate);
                log.setMaturityDate(maturityDate);
                // Non-LC
            } else {
                inceptionDate = exportBills.getNegotiationDate();
                if (exportBills.getNonLcDetails() != null) {
                    maturityDate = exportBills.getNonLcDetails().getDueDate();
                }
                log.setInceptionDate(inceptionDate);
                log.setMaturityDate(maturityDate);
            }
        }

        persistTransactionLogDetails(log, event.getGltsNumber());

        AccountLog accountLog = mapEventToAccountLog(event);
        accountLog.setOpeningDate(inceptionDate);

        persistAccountLogDetails(event, accountLog);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    //Bills for Purchase
    @EventListener
    public void logBPCreatedEvent(BPNegotiatedPriorBCEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        //TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        //persistTransactionLogDetails(log, event.getGltsNumber());
        AccountLog accountLog = mapEventToAccountLog(event);
        
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logBPSettledEvent(BPSettledPriorBCEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");
    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        //TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	// log.setCifBirthday(getBirthday(cifNumber));

    	//persistTransactionLogDetails(log, event.getGltsNumber());
    	//AccountLog accountLog = mapEventToAccountLog(event);
    	persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logMarginalDepositUpdatedEvent(MarginalDepositUpdatedEvent event) {

        try {

            //            addConversionRate(event.getTradeService().getDetails(), "currency");
            event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

            // addPaymentDetails(event.getTradeService(), "paymentDetails");

            TradeService tradeService = event.getTradeService();
            TradeProduct tradeProduct = event.getTradeProduct();
            MarginalDeposit md = event.getMd();

            if (tradeProduct != null && tradeProduct instanceof LetterOfCredit) {

                LetterOfCredit letterOfCredit = (LetterOfCredit)tradeProduct;

                TransactionLog log = mapEventToTransactionlog(event);
                AccountLog accountLog = mapEventToAccountLog(event);

                LinkedList<LinkedList<Object>> linkedListMda = event.getLinkedListMda();

                // Can have multiple collections in one transaction
                for(LinkedList<Object> linkedObj : linkedListMda) {

                    TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);

                    BigDecimal amount = (BigDecimal)linkedObj.get(0);
                    String currency = (String)linkedObj.get(1);

                    BigDecimal exchangeRate = getConversionRate(tradeService, currency, Boolean.TRUE);
                    BigDecimal transactionAmount = amount.multiply(exchangeRate);

                    exchangeRate = exchangeRate.setScale(8, RoundingMode.HALF_UP);
                    transactionAmount = transactionAmount.setScale(2, RoundingMode.HALF_UP);

                    clonedLog.setTransactionAmount(transactionAmount);
                    clonedLog.setExchangeRate(exchangeRate);
                    clonedLog.setSettlementCurrency(Currency.getInstance(currency));

                    if (currency.equals("PHP")) {
                        clonedLog.setSettlementAmount(BigDecimal.ZERO);
                    } else {
                        clonedLog.setSettlementAmount(amount);
                    }

                    if (tradeService.getServiceType() != null && tradeService.getServiceType().equals(ServiceType.COLLECTION)) {
                        // If collection, Credit
                        clonedLog.setTransactionType(TransactionType.CREDIT.toString());
                    } else if (tradeService.getServiceType() != null && tradeService.getServiceType().equals(ServiceType.APPLICATION)) {
                        // If application, Debit
                        clonedLog.setTransactionType(TransactionType.DEBIT.toString());
                    }

                    persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                }

                /*
                if (md.getCreatedDate() != null) {
                    accountLog.setOpeningDate(md.getCreatedDate());
                } else {
                    accountLog.setOpeningDate(letterOfCredit.getProcessDate());
                }
                if (md.getMdOutstandingBalance().compareTo(BigDecimal.ZERO) < 1) {
                    String processDate = (tradeService.getDetails().get("processDate") != null) ? tradeService.getDetails().get("processDate").toString() : null;
                    if (processDate != null) {
                        accountLog.setClosingDate(DateUtil.convertToDate(processDate,"MM/dd/yyyy"));
                    } else {
                        accountLog.setClosingDate(new Date());
                    }
                    accountLog.setAccountBalance(BigDecimal.ZERO);
                    accountLog.setStatus("C");
                } else {
                    accountLog.setStatus("A");
                }
                */

                accountLog.setOpeningDate(letterOfCredit.getProcessDate());
                accountLog.setAccountBalance(letterOfCredit.getOutstandingBalance());
                accountLog.setAccountCurrency(letterOfCredit.getCurrency().getCurrencyCode());
                if (letterOfCredit.getStatus().equals(TradeProductStatus.EXPIRED)) {
                    accountLog.setClosingDate(letterOfCredit.getExpiryDate());
                    accountLog.setAccountBalance(BigDecimal.ZERO);
                    accountLog.setStatus("C");
                } else if (letterOfCredit.getStatus().equals(TradeProductStatus.CLOSED)) {
                    accountLog.setClosingDate(letterOfCredit.getDateClosed());
                    accountLog.setAccountBalance(BigDecimal.ZERO);
                    accountLog.setStatus("C");
                } else if (letterOfCredit.getStatus().equals(TradeProductStatus.CANCELLED)) {
                    accountLog.setClosingDate(letterOfCredit.getCancellationDate());
                    accountLog.setAccountBalance(BigDecimal.ZERO);
                    accountLog.setStatus("C");
                } else {
                    accountLog.setStatus("A");
                }

                persistAccountLogDetails(event, accountLog);

                // Persist for product and charges payments
                // See bug #2039 in Redmine
                persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
            }

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logMaginalDepositUpdatedEvent()");
            e.printStackTrace();
        }
    }

    @EventListener
    public void logImportAdvancePaymentCreatedEvent(ImportAdvancePaymentCreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);

        // log.setCifBirthday(getBirthday(cifNumber));
        persistTransactionLogDetails(log, event.getGltsNumber());

        // as said by users
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logImportAdvancePaymentRefundCreatedEvent(ImportAdvancePaymentRefundCreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);

        // log.setCifBirthday(getBirthday(cifNumber));
        persistTransactionLogDetails(log, event.getGltsNumber());

        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logExportAdvancePaymentCreatedEvent(ExportAdvancePaymentCreatedEvent event) {
    	try {
    		// addConversionRate(event.getTradeService().getDetails(), "currency");
		  	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));
		  	
		  	// addPaymentDetails(event.getTradeService(), "paymentDetails");
		  	
		  	TransactionLog log = mapEventToTransactionlog(event);
		  	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
		  	
		  	// log.setCifBirthday(getBirthday(cifNumber));
		  	persistTransactionLogDetails(log, event.getGltsNumber());
		
		      persistAccountLogDetails(event);
		
		  	// Persist for product and charges payments
		  	// See bug #2039 in Redmine
		  	persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    	}catch(Exception e){
    		e.getCause();
    		e.printStackTrace();
    	
    	}
    	System.out.println("No Error for Log Export Advance Payment Created Event");
    }
    
    @EventListener
    public void logExportAdvancePaymentRefundCreatedEvent(ExportAdvancePaymentRefundCreatedEvent event) {
    	//            addConversionRate(event.getTradeService().getDetails(), "currency");
    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	
    	// log.setCifBirthday(getBirthday(cifNumber));
    	persistTransactionLogDetails(log, event.getGltsNumber());

        persistAccountLogDetails(event);

    	// Persist for product and charges payments
    	// See bug #2039 in Redmine
    	persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }

    @EventListener
    public void logCorresChargeActualApprovedEvent(CorresChargeActualApprovedEvent event) {

        System.out.println("\nSTART AmlaInformationLogger.logCorresChargeActualApprovedEvent()\n");

        try {

            TradeProduct tradeProduct = event.getTradeProduct();
            String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);

            System.out.println("reference = " + event.getHasReference());

            if (tradeProduct != null) {

                if (cifNumber == null || cifNumber.isEmpty()) {
                    cifNumber = tradeProduct.getCifNumber();
                }

                System.out.println("tradeProduct NOT NULL!");

                //            addConversionRate(event.getTradeService().getDetails(), "currency");
                event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

                // addPaymentDetails(event.getTradeService(), "paymentDetails");

                TransactionLog log = mapEventToTransactionlog(event);
                AccountLog accountLog = mapEventToAccountLog(event);

                // log.setCifBirthday(getBirthday(cifNumber));

                LetterOfCredit letterOfCredit = null;
                DocumentAgainstAcceptance documentAgainstAcceptance = null;
                DocumentAgainstPayment documentAgainstPayment = null;
                OpenAccount openAccount = null;
                DirectRemittance directRemittance = null;

                if (tradeProduct instanceof LetterOfCredit) {

                    letterOfCredit = (LetterOfCredit)tradeProduct;
                    accountLog.setOpeningDate(letterOfCredit.getProcessDate());
                    accountLog.setAccountBalance(letterOfCredit.getOutstandingBalance());
                    accountLog.setAccountCurrency(letterOfCredit.getCurrency().getCurrencyCode());
                    if (letterOfCredit.getStatus().equals(TradeProductStatus.EXPIRED)) {
                        accountLog.setClosingDate(letterOfCredit.getExpiryDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else if (letterOfCredit.getStatus().equals(TradeProductStatus.CLOSED)) {
                        accountLog.setClosingDate(letterOfCredit.getDateClosed());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else if (letterOfCredit.getStatus().equals(TradeProductStatus.CANCELLED)) {
                        accountLog.setClosingDate(letterOfCredit.getCancellationDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else {
                        accountLog.setStatus("A");
                    }

                } else if (tradeProduct instanceof DocumentAgainstAcceptance) {

                    documentAgainstAcceptance = (DocumentAgainstAcceptance)tradeProduct;
                    accountLog.setOpeningDate(documentAgainstAcceptance.getProcessDate());
                    accountLog.setAccountBalance(documentAgainstAcceptance.getOutstandingAmount());
                    accountLog.setAccountCurrency(documentAgainstAcceptance.getCurrency().getCurrencyCode());
                    if (documentAgainstAcceptance.getCancelledDate() != null) {
                        accountLog.setClosingDate(documentAgainstAcceptance.getCancelledDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else if (documentAgainstAcceptance.getSettledDate() != null) {
                        accountLog.setClosingDate(documentAgainstAcceptance.getSettledDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else {
                        accountLog.setStatus("A");
                    }

                } else if (tradeProduct instanceof DocumentAgainstPayment) {

                    documentAgainstPayment = (DocumentAgainstPayment)tradeProduct;
                    accountLog.setOpeningDate(documentAgainstPayment.getProcessDate());
                    accountLog.setAccountBalance(documentAgainstPayment.getOutstandingAmount());
                    accountLog.setAccountCurrency(documentAgainstPayment.getCurrency().getCurrencyCode());
                    if (documentAgainstPayment.getCancelledDate() != null) {
                        accountLog.setClosingDate(documentAgainstPayment.getCancelledDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else if (documentAgainstPayment.getSettledDate() != null) {
                        accountLog.setClosingDate(documentAgainstPayment.getSettledDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else {
                        accountLog.setStatus("A");
                    }

                } else if (tradeProduct instanceof OpenAccount) {

                    openAccount = (OpenAccount)tradeProduct;
                    accountLog.setOpeningDate(openAccount.getProcessDate());
                    accountLog.setAccountBalance(openAccount.getOutstandingAmount());
                    accountLog.setAccountCurrency(openAccount.getCurrency().getCurrencyCode());
                    if (openAccount.getCancelledDate() != null) {
                        accountLog.setClosingDate(openAccount.getCancelledDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else if (openAccount.getSettledDate() != null) {
                        accountLog.setClosingDate(openAccount.getSettledDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else {
                        accountLog.setStatus("A");
                    }

                } else if (tradeProduct instanceof DirectRemittance) {

                    directRemittance = (DirectRemittance)tradeProduct;
                    accountLog.setOpeningDate(directRemittance.getProcessDate());
                    accountLog.setAccountBalance(directRemittance.getOutstandingAmount());
                    accountLog.setAccountCurrency(directRemittance.getCurrency().getCurrencyCode());
                    if (directRemittance.getCancelledDate() != null) {
                        accountLog.setClosingDate(directRemittance.getCancelledDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else if (directRemittance.getSettledDate() != null) {
                        accountLog.setClosingDate(directRemittance.getSettledDate());
                        accountLog.setAccountBalance(BigDecimal.ZERO);
                        accountLog.setStatus("C");
                    } else {
                        accountLog.setStatus("A");
                    }
                }

                CustomerAccount customerAccount = mapEventToCustomerAccount(event);
                customerAccount.setCustomerNumber(cifNumber);

                //persistTransactionLogDetails(log, event.getGltsNumber());

                //persistAccountLogDetails(accountLog, customerAccount);

                // Persist for product and charges payments
                // See bug #2039 in Redmine
                // persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());

            } else {
                System.out.println("tradeProduct NULL! AMLA entries not logged.");
            }

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logCorresChargeActualApprovedEvent()");
            e.printStackTrace();
        }

        System.out.println("\nEND AmlaInformationLogger.logCorresChargeActualApprovedEvent()\n");
    }
    
    @EventListener
    public void logExportAdvisingCreatedEvent(ExportAdvisingCreatedEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");

        TradeService tradeService = event.getTradeService();
        String lcCurrency = (String)tradeService.getDetails().get("lcCurrency");

        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), lcCurrency, Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        // persistTransactionLogDetails(log, event.getGltsNumber());
        //persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logExportAdvisingAmendedEvent(ExportAdvisingAmendedEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");

        TradeService tradeService = event.getTradeService();
        String lcCurrency = (String)tradeService.getDetails().get("lcCurrency");

        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), lcCurrency, Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
        // log.setCifBirthday(getBirthday(cifNumber));

        // persistTransactionLogDetails(log, event.getGltsNumber());
        //persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        //persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logExportAdvisingCancelledEvent(ExportAdvisingCancelledEvent event) {
//            addConversionRate(event.getTradeService().getDetails(), "currency");

        TradeService tradeService = event.getTradeService();
        String lcCurrency = (String)tradeService.getDetails().get("lcCurrency");

    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), lcCurrency, Boolean.TRUE));
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	// log.setCifBirthday(getBirthday(cifNumber));
    	
    	// persistTransactionLogDetails(log, event.getGltsNumber());
    	//persistAccountLogDetails(event);
    	
    	// Persist for product and charges payments
    	// See bug #2039 in Redmine
    	//persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logCDTPaymentRequestPaidEvent(CDTPaymentRequestPaidEvent event) {

        try {

            System.out.println("tradeservice Details: " + event.getTradeService().getDetails());
            System.out.println("totalAmountPaid; " + event.getTotalAmountPaid());

//            addConversionRate(event.getTradeService().getDetails(), "currency");
            event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

            // addPaymentDetails(event.getTradeService(), "paymentDetails");

            TransactionLog log = mapEventToTransactionlog(event);

            // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
            String cifNumber = event.getTradeService().getCifNumber();

            // As of 2/7/2014: Do not log for walk-in clients
            if (cifNumber != null && !cifNumber.trim().isEmpty() && !cifNumber.trim().equalsIgnoreCase("NONE")) {

                // log.setCifBirthday(getBirthday(cifNumber));

                CDTPaymentRequest cdtPaymentRequest = event.getCdtPaymentRequest();

                // Bank charge
                BigDecimal bankCharge = BigDecimal.ZERO;
                if (cdtPaymentRequest.getBankCharge() != null && cdtPaymentRequest.getBankCharge().compareTo(BigDecimal.ZERO) > 0) {
                    bankCharge = cdtPaymentRequest.getBankCharge();
                }

                // Actual duty amount (minus bank charge)
                BigDecimal dutyAmount = event.getTotalAmountPaid().subtract(bankCharge);
                log.setTransactionAmount(dutyAmount);

                persistTransactionLogDetails(log, event.getGltsNumber());

                // If has bank charge
                if (cdtPaymentRequest.getBankCharge() != null && cdtPaymentRequest.getBankCharge().compareTo(BigDecimal.ZERO) > 0) {

                    // Clone for charges payment
                    TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                    clonedLog.setTransactionReferenceNumber(null);
                    clonedLog.setRemarks("Charges payment");

                    clonedLog.setTransactionAmount(bankCharge);

                    Payment bankChargePayment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.PRODUCT);
                    List<PaymentDetail> chargesPaymentList = new ArrayList<PaymentDetail>(bankChargePayment.getDetails());
                    Collections.sort(chargesPaymentList, new PaymentDetailComparator());

                    String incomeTranCode = getIncomeChargesTransactionTypeCode((chargesPaymentList.get(0)).getPaymentInstrumentType().toString());

                    clonedLog.setTransactionTypeCode(incomeTranCode);

                    clonedLog.setSettlementAmount(BigDecimal.ZERO);
                    clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));

                    persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                }

                AccountLog accountLog = mapEventToAccountLog(event);
                accountLog.setAccountBalance(dutyAmount);

                persistAccountLogDetails(event, accountLog);

                // No ServiceCharges for CDT Payment
                // persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
            }

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logCDTPaymentRequestPaidEvent()");
            e.printStackTrace();
        }
    }

    @EventListener
    public void logCDTRefundEvent(CDTRefundCreatedEvent event) {

        try {

//            addConversionRate(event.getTradeService().getDetails(), "currency");
            event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

            // addPaymentDetails(event.getTradeService(), "paymentDetails");

            TransactionLog log = mapEventToTransactionlog(event);

            // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
            String cifNumber = event.getTradeService().getCifNumber();

            // As of 2/7/2014: Do not log for walk-in clients
            if (cifNumber != null && !cifNumber.trim().isEmpty()) {

                // log.setCifBirthday(getBirthday(cifNumber));

                CDTPaymentRequest cdtPaymentRequest = event.getCdtPaymentRequest();

                log.setTransactionAmount(cdtPaymentRequest.getAmount());

                persistTransactionLogDetails(log, event.getGltsNumber());

                persistAccountLogDetails(event);

                // No ServiceCharges for CDT Refund
                // persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
            }

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logCDTRefundEvent()");
            e.printStackTrace();
        }
    }

    @EventListener
    public void logCDTPaymentRequestUnpaidEvent(CDTPaymentRequestUnpaidEvent event) {

        // Delete in all

        try {

            TradeServiceId tradeServiceId = event.getTradeServiceId();

            transactionLogRepository.delete(tradeServiceId);
            accountLogRepository.delete(tradeServiceId);
            customerAccountLogRepository.delete(tradeServiceId);
            // customerLogRepository.delete(tradeServiceId);

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logCDTPaymentRequestUnpaidEvent()");
            e.printStackTrace();
        }
    }

    @EventListener
    public void logCDTRemittanceCreatedEvent(CDTRemittanceCreatedEvent event) {

        System.out.println("\n>>>>>>>>>>> Inside logCDTRemittanceCreatedEvent\n");

        try {

            event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

            TradeServiceId tradeServiceId = event.getTradeService().getTradeServiceId();

            // As per Tony Romero, do not log in TRN
            // TransactionLog log = mapEventToTransactionlog(event);
            // log.setTransactionAmount(cdtRemittance.getTotalRemitted());
            // persistTransactionLogDetails(log, event.getGltsNumber());

            CDTRemittance cdtRemittance = event.getCdtRemittance();

            // Get all PaymentRequests remitted on remittanceDate

            Calendar calCollectionTo = Calendar.getInstance();
            calCollectionTo.setTime(cdtRemittance.getCollectionTo());
            calCollectionTo.set(Calendar.HOUR_OF_DAY, 0);
            calCollectionTo.set(Calendar.MINUTE, 0);
            calCollectionTo.set(Calendar.SECOND, 0);
            calCollectionTo.set(Calendar.MILLISECOND, 0);

            calCollectionTo.add(Calendar.DATE, 1);
            calCollectionTo.add(Calendar.MILLISECOND, -1);

            Date dateCollectionTo = calCollectionTo.getTime();

            System.out.println("\n cdtRemittance.getCollectionFrom() = " + cdtRemittance.getCollectionFrom().toString());
            System.out.println("dateCollectionTo =  " + dateCollectionTo.toString() + "\n");

            List<CDTPaymentRequest> cdtPaymentRequests = cdtPaymentRequestRepository.getAllRemittedRequestsWithCif(cdtRemittance.getCollectionFrom(), dateCollectionTo);

            // Get all CDT Accounts with closingDate on remittanceDate
            List<AccountLog> accountLogs = accountLogRepository.getAccountLogsByCdtClosingDate(cdtRemittance.getCollectionFrom(), cdtRemittance.getCollectionTo());

            // Log all the ACC (only) of the PaymentRequests included in this remittance
            // Do not log ACC/CAC of the remittance itself (since this should be reported by CASA/SIBS)

            // If a PaymentRequest IEIRD does not exist in AccountNumbers in Accounts, log it in AMLA
            mainLoop:
            for (CDTPaymentRequest cdtPaymentRequest : cdtPaymentRequests) {

                if (cdtPaymentRequest.getIedieirdNumber() != null && !cdtPaymentRequest.getIedieirdNumber().trim().isEmpty()) {

                    String accountNumber = "TFSS" + cdtPaymentRequest.getIedieirdNumber().toUpperCase();

                    for (AccountLog accountLog : accountLogs) {

                        // Matched
                        if ((accountLog.getAccountNumber() != null && !accountLog.getAccountNumber().isEmpty()) &&
                                (accountNumber.equals(accountLog.getAccountNumber().toUpperCase()))
                                ) {

                            continue mainLoop;
                        }
                    }

                    // If code reached here, it means we did not hit a match, so log in AMLA

                    // Retrieve first the old AccountLog
                    AccountLog accountLogOld = accountLogRepository.getAccountLogByAccountNumber(accountNumber);

                    // Then clone
                    AccountLog accountLogNew = (AccountLog)SerializationUtils.clone(accountLogOld);

                    // Necessary setters
                    accountLogNew.setId(null);
                    accountLogNew.setTradeServiceId(tradeServiceId);  // We need the original TradeService ID for easy deletion during error correction of remittance
                    accountLogNew.setClosingDate(cdtRemittance.getRemittanceDate());
                    accountLogNew.setAccountBalance(BigDecimal.ZERO);
                    accountLogNew.setStatus("C");

                    // Log to AMLA!
                    persistAccountLogDetailsWithoutCustomerAccount(accountLogNew);
                    // persistAccountLogDetails(event);
                }
            }

            // No ServiceCharges for CDT Remittance
            // persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logCDTRemittanceCreatedEvent()");
            e.printStackTrace();
        }
    }

    @EventListener
    public void cdtRemittanceErrorCorrectedEvent(CDTRemittanceErrorCorrectedEvent event) {

        // Delete in all

        try {

            TradeServiceId tradeServiceId = event.getTradeService().getTradeServiceId();

            transactionLogRepository.delete(tradeServiceId);
            accountLogRepository.delete(tradeServiceId);
            customerAccountLogRepository.delete(tradeServiceId);
            // customerLogRepository.delete(tradeServiceId);

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.cdtRemittanceErrorCorrectedEvent()");
            e.printStackTrace();
        }
    }


/* Not reportable to AMLA as per Ma'am Juliet 2/8/2014
    @EventListener
    public void logRebateCreatedEvent(RebateCreatedEvent event) {
    	//            addConversionRate(event.getTradeService().getDetails(), "currency");
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	// log.setCifBirthday(getBirthday(cifNumber));
    	
    	persistTransactionLogDetails(log, event.getGltsNumber());
    	persistAccountLogDetails(event);

    	// No charges for Rebates
    	// persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
*/

    @EventListener
    public void logAPCreatedEvent(APCreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);

        // log.setCifBirthday(getBirthday(cifNumber));
        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logAPAppliedEvent(APAppliedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);

        // log.setCifBirthday(getBirthday(cifNumber));
        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logARCreatedEvent(ARCreatedEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);

        // log.setCifBirthday(getBirthday(cifNumber));
        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logARSettledEvent(ARSettledEvent event) {
        //            addConversionRate(event.getTradeService().getDetails(), "currency");
        event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

        // addPaymentDetails(event.getTradeService(), "paymentDetails");

        TransactionLog log = mapEventToTransactionlog(event);
        // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);

        // log.setCifBirthday(getBirthday(cifNumber));
        persistTransactionLogDetails(log, event.getGltsNumber());
        persistAccountLogDetails(event);

        // Persist for product and charges payments
        // See bug #2039 in Redmine
        persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logCashLcRefundEvent(CashLcRefundEvent event) {
    	//            addConversionRate(event.getTradeService().getDetails(), "currency");
    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	
    	// log.setCifBirthday(getBirthday(cifNumber));
    	persistTransactionLogDetails(log, event.getGltsNumber());
    	persistAccountLogDetails(event);
    	
    	// Persist for product and charges payments
    	// See bug #2039 in Redmine
    	persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logImportChargesPaidEvent(ImportChargesPaidEvent event) {

        try {

            //            addConversionRate(event.getTradeService().getDetails(), "currency");
            event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));

            // addPaymentDetails(event.getTradeService(), "paymentDetails");

            TransactionLog log = mapEventToTransactionlog(event);
            // String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
            // log.setCifBirthday(getBirthday(cifNumber));

            TradeService tradeService = event.getTradeService();
            LetterOfCredit letterOfCredit = event.getLetterOfCredit();

            Date closingDate = null;
            String status = "";
            String accountNumber = "";
            Currency currency = null;

            List<String> tranCodes = new ArrayList<String>();

            if (letterOfCredit != null) {
                accountNumber = "TFSS" + letterOfCredit.getDocumentNumber().toString();
                if (letterOfCredit.getStatus().equals(TradeProductStatus.EXPIRED)) {
                    closingDate = letterOfCredit.getExpiryDate();
                    status = "C";
                } else if (letterOfCredit.getStatus().equals(TradeProductStatus.CLOSED)) {
                    closingDate = letterOfCredit.getDateClosed();
                    status = "C";
                } else if (letterOfCredit.getStatus().equals(TradeProductStatus.CANCELLED)) {
                    closingDate = letterOfCredit.getCancellationDate();
                    status = "C";
                } else {
                    status = "A";
                }
            } else {
                if (tradeService.getDocumentNumber() != null) {
                    accountNumber = "TFSS" + tradeService.getDocumentNumber().toString();
                } else {
                    accountNumber = "TFSS" + (String)tradeService.getDetails().get("cifNumber");
                }
                status = "A";
            }

            log.setAccountNumber(accountNumber);

            Payment chargesPayment = null;
            if (tradeService.getServiceType() != null && !tradeService.getServiceType().equals(ServiceType.REFUND)) {
                chargesPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
            }

            if (tradeService.getServiceType().equals(ServiceType.PAYMENT)) {

                // Branch initiated
                // Get amount from Collectible domain

                if (chargesPayment != null) {

                    // Always PESO
                    Set<ServiceCharge> serviceCharges = tradeService.getServiceCharge();
                    if (serviceCharges != null) {

                        BigDecimal totalIncomeChargesPeso     = BigDecimal.ZERO;
                        BigDecimal totalGovtChargesPeso       = BigDecimal.ZERO;
                        BigDecimal totalCollectionChargesPeso = BigDecimal.ZERO;
                        BigDecimal totalMiscChargesPeso       = BigDecimal.ZERO;

                        mainLoop:
                        for (ServiceCharge serviceCharge : serviceCharges) {

                            if ((serviceCharge.getNewCollectibleAmount() != null && serviceCharge.getOriginalAmount() != null) &&
                                    serviceCharge.getNewCollectibleAmount().compareTo(serviceCharge.getOriginalAmount()) > 0) {

                                BigDecimal chargePeso = serviceCharge.getNewCollectibleAmount().subtract(serviceCharge.getOriginalAmount());

                                for (String income : incomeList) {
                                    if (serviceCharge.getChargeId().toString().equalsIgnoreCase(income)) {
                                        totalIncomeChargesPeso = totalIncomeChargesPeso.add(chargePeso);
                                        continue mainLoop;
                                    }
                                }
                                for (String govt : govtList) {
                                    if (serviceCharge.getChargeId().toString().equalsIgnoreCase(govt)) {
                                        totalGovtChargesPeso = totalGovtChargesPeso.add(chargePeso);
                                        continue mainLoop;
                                    }
                                }
                                for (String collection : collectionList) {
                                    if (serviceCharge.getChargeId().toString().equalsIgnoreCase(collection)) {
                                        totalCollectionChargesPeso = totalCollectionChargesPeso.add(chargePeso);
                                        continue mainLoop;
                                    }
                                }
                                for (String misc : miscList) {
                                    if (serviceCharge.getChargeId().toString().equalsIgnoreCase(misc)) {
                                        totalMiscChargesPeso = totalMiscChargesPeso.add(chargePeso);
                                        continue mainLoop;
                                    }
                                }
                            }
                        }

                        if (totalGovtChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                            // Make Hibernate insert the record
                            TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                            clonedLog.setTransactionReferenceNumber(null);

                            clonedLog.setTransactionTypeCode("CPYGA");
                            tranCodes.add("CPYGA");
                            clonedLog.setSettlementAmount(BigDecimal.ZERO);
                            clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));

                            totalGovtChargesPeso = totalGovtChargesPeso.setScale(2, RoundingMode.UP);
                            clonedLog.setTransactionAmount(totalGovtChargesPeso);

                            clonedLog.setRemarks("Charges payment");

                            clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                            persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                        }

                        if (totalCollectionChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                            // Make Hibernate insert the record
                            TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                            clonedLog.setTransactionReferenceNumber(null);

                            clonedLog.setTransactionTypeCode("CCOL");
                            tranCodes.add("CCOL");
                            clonedLog.setSettlementAmount(BigDecimal.ZERO);
                            clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));

                            totalCollectionChargesPeso = totalCollectionChargesPeso.setScale(2, RoundingMode.UP);
                            clonedLog.setTransactionAmount(totalCollectionChargesPeso);

                            clonedLog.setRemarks("Charges payment");

                            clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                            persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                        }

                        if (totalMiscChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                            // Make Hibernate insert the record
                            TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                            clonedLog.setTransactionReferenceNumber(null);

                            clonedLog.setTransactionTypeCode("CMISC");
                            tranCodes.add("CMISC");
                            clonedLog.setSettlementAmount(BigDecimal.ZERO);
                            clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));

                            totalMiscChargesPeso = totalMiscChargesPeso.setScale(2, RoundingMode.UP);
                            clonedLog.setTransactionAmount(totalMiscChargesPeso);

                            clonedLog.setRemarks("Charges payment");

                            clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                            persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                        }

                        List<PaymentDetail> chargesPaymentList = new ArrayList<PaymentDetail>(chargesPayment.getDetails());
                        Collections.sort(chargesPaymentList, new PaymentDetailComparator());

                        String incomeTranCode = getIncomeChargesTransactionTypeCode((chargesPaymentList.get(0)).getPaymentInstrumentType().toString());

                        if (totalIncomeChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                            // Make Hibernate insert the record
                            TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                            clonedLog.setTransactionReferenceNumber(null);

                            clonedLog.setTransactionTypeCode(incomeTranCode);
                            tranCodes.add(incomeTranCode);

                            clonedLog.setSettlementAmount(BigDecimal.ZERO);

                            totalIncomeChargesPeso = totalIncomeChargesPeso.setScale(2, RoundingMode.UP);
                            clonedLog.setTransactionAmount(totalIncomeChargesPeso);
                            clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));

                            clonedLog.setRemarks("Charges payment");

                            clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                            persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                        }
                    }
                }

            } else if (tradeService.getServiceType().equals(ServiceType.PAYMENT_OTHER)) {

                // TSD initiated
                // Get amount from OtherChargeDetail domain

                currency = Currency.getInstance("PHP");  // always PHP

                if (chargesPayment != null) {

                    // Always PESO
                    Set<OtherChargesDetail> otherChargesDetails = tradeService.getOtherChargesDetails();
                    if (otherChargesDetails != null) {

                        BigDecimal totalIncomeChargesPeso     = BigDecimal.ZERO;
                        BigDecimal totalCollectionChargesPeso = BigDecimal.ZERO;
                        BigDecimal totalMiscChargesPeso       = BigDecimal.ZERO;

                        mainLoop:
                        for (OtherChargesDetail otherChargesDetail : otherChargesDetails) {

                            BigDecimal chargePeso = otherChargesDetail.getAmount();

                            if (otherChargesDetail.getChargeType().toString().equalsIgnoreCase("BANK COMMISSION") ||
                                    otherChargesDetail.getChargeType().toString().equalsIgnoreCase("CABLE FEE")) {
                                totalIncomeChargesPeso = totalIncomeChargesPeso.add(chargePeso);
                                continue mainLoop;
                            }

                            if (otherChargesDetail.getChargeType().toString().equalsIgnoreCase("DOCUMENTARY STAMP")) {
                                totalCollectionChargesPeso = totalCollectionChargesPeso.add(chargePeso);
                                continue mainLoop;
                            }

                            if (otherChargesDetail.getChargeType().toString().equalsIgnoreCase("OTHER LOCAL BANK'S CHARGES") ||
                                    otherChargesDetail.getChargeType().toString().equalsIgnoreCase("MARINE / FIRE INSURANCE")) {
                                totalMiscChargesPeso = totalMiscChargesPeso.add(chargePeso);
                                continue mainLoop;
                            }
                        }

                        if (totalCollectionChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                            // Make Hibernate insert the record
                            TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                            clonedLog.setTransactionReferenceNumber(null);

                            clonedLog.setTransactionTypeCode("CCOL");
                            tranCodes.add("CCOL");
                            clonedLog.setSettlementAmount(BigDecimal.ZERO);
                            clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));

                            totalCollectionChargesPeso = totalCollectionChargesPeso.setScale(2, RoundingMode.UP);
                            clonedLog.setTransactionAmount(totalCollectionChargesPeso);

                            clonedLog.setRemarks("Charges payment");

                            clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                            persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                        }

                        if (totalMiscChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                            // Make Hibernate insert the record
                            TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                            clonedLog.setTransactionReferenceNumber(null);

                            clonedLog.setTransactionTypeCode("CMISC");
                            tranCodes.add("CMISC");
                            clonedLog.setSettlementAmount(BigDecimal.ZERO);
                            clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));

                            totalMiscChargesPeso = totalMiscChargesPeso.setScale(2, RoundingMode.UP);
                            clonedLog.setTransactionAmount(totalMiscChargesPeso);

                            clonedLog.setRemarks("Charges payment");

                            clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                            persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                        }

                        List<PaymentDetail> chargesPaymentList = new ArrayList<PaymentDetail>(chargesPayment.getDetails());

                        String incomeTranCode = "CMISC";
                        if (chargesPaymentList != null && !chargesPaymentList.isEmpty()) {
                            Collections.sort(chargesPaymentList, new PaymentDetailComparator());
                            incomeTranCode = getIncomeChargesTransactionTypeCode((chargesPaymentList.get(0)).getPaymentInstrumentType().toString());
                        }

                        if (totalIncomeChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                            // Make Hibernate insert the record
                            TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                            clonedLog.setTransactionReferenceNumber(null);

                            clonedLog.setTransactionTypeCode(incomeTranCode);
                            tranCodes.add(incomeTranCode);

                            clonedLog.setSettlementAmount(BigDecimal.ZERO);

                            totalIncomeChargesPeso = totalIncomeChargesPeso.setScale(2, RoundingMode.UP);
                            clonedLog.setTransactionAmount(totalIncomeChargesPeso);
                            clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));

                            clonedLog.setRemarks("Charges payment");

                            clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                            persistTransactionLogDetails(clonedLog, event.getGltsNumber());
                        }
                    }
                }
            }

            AccountLog accountLog = mapEventToAccountLog(event);
            CustomerAccount customerAccount = mapEventToCustomerAccount(event);

            if (currency != null) {
                accountLog.setAccountCurrency(currency.getCurrencyCode());
            }

            accountLog.setClosingDate(closingDate);
            accountLog.setAccountNumber(accountNumber);

            if (status.equals("C")) {
                accountLog.setAccountBalance(BigDecimal.ZERO);
            }
            accountLog.setStatus(status);

            if (tranCodes.size() == 1) {
                accountLog.setTransactionTypes(tranCodes.get(0));
            } else if (tranCodes.size() > 1) {
                for (String tranCode : tranCodes) {
                    if (tranCode.equals("CMISC")) {
                        accountLog.setTransactionTypes("CMISC");
                    }
                }
                if (accountLog.getTransactionTypes() == null || accountLog.getTransactionTypes().isEmpty()) {
                    accountLog.setTransactionTypes(tranCodes.get(0));
                }
            }

            customerAccount.setAccountNumber(accountNumber);

            persistAccountLogDetails(accountLog, customerAccount);

            // persistTransactionLogDetails(log, event.getGltsNumber());

            // Persist for product and charges payments
            // See bug #2039 in Redmine
            // persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.logImportChargesPaidEvent()");
            e.printStackTrace();
        }
    }
    
    @EventListener
    public void logExportChargesPaidEvent(ExportChargesPaidEvent event) {
    	//            addConversionRate(event.getTradeService().getDetails(), "currency");
    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	
    	// log.setCifBirthday(getBirthday(cifNumber));
    	persistTransactionLogDetails(log, event.getGltsNumber());
    	persistAccountLogDetails(event);
    	
    	// Persist for product and charges payments
    	// See bug #2039 in Redmine
    	persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logExportChargesRefundEvent(ExportChargesRefundEvent event) {
    	//            addConversionRate(event.getTradeService().getDetails(), "currency");
    	event.getTradeService().getDetails().put(CURRENT_RATE_KEY, getConversionRate(event.getTradeService(), Boolean.TRUE));
    	
    	// addPaymentDetails(event.getTradeService(), "paymentDetails");
    	
    	TransactionLog log = mapEventToTransactionlog(event);
    	// String cifNumber = (String) event.getTradeService().getDetails().get(CIF_NUMBER_FIELD_ID);
    	
    	// log.setCifBirthday(getBirthday(cifNumber));
    	persistTransactionLogDetails(log, event.getGltsNumber());
    	persistAccountLogDetails(event);
    	
    	// Persist for product and charges payments
    	// See bug #2039 in Redmine
    	persistTransactionLogPerPayment(event.getTradeService(), log, event.getGltsNumber());
    }
    
    @EventListener
    public void logCustomerCreatedEvent(CustomerCreatedEvent event) {
        persistCustomerLogDetails(event);
    }

    @EventListener
    public void logCustomerUpdatedEvent(CustomerUpdatedEvent event) {
        persistCustomerLogDetails(event);
    }

    public void setTransactionLogRepository(TransactionLogRepository transactionLogRepository) {
        this.transactionLogRepository = transactionLogRepository;
    }

    public void setAccountLogRepository(AccountLogRepository accountLogRepository) {
        this.accountLogRepository = accountLogRepository;
    }

    public void setTransactionLogMappers(Map<Class, BeanMapper> transactionLogMappers) {
        this.transactionLogMappers = transactionLogMappers;
    }

    public void setAccountLogMappers(Map<Class, BeanMapper> accountLogMappers) {
        this.accountLogMappers = accountLogMappers;
    }

    public void setCustomerAccountLogMappers(Map<Class, BeanMapper> customerAccountLogMappers) {
        this.customerAccountLogMappers = customerAccountLogMappers;
    }

    public void setCustomerAccountLogRepository(CustomerAccountLogRepository customerAccountLogRepository) {
        this.customerAccountLogRepository = customerAccountLogRepository;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public void setPaymentRepository(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void setCustomerLogMappers(Map<Class, BeanMapper> customerLogMappers) {
        this.customerLogMappers = customerLogMappers;
    }

    public void setCustomerLogRepository(CustomerLogRepository customerLogRepository) {
        this.customerLogRepository = customerLogRepository;
    }

    private <T extends DomainEvent> TransactionLog mapEventToTransactionlog(T event) {
        BeanMapper mapper = transactionLogMappers.get(event.getClass());
        TransactionLog log = (TransactionLog) mapper.map(event);
        Address address = getCorrespondentBankAddress(log.getCorrespondentBank());

        if(log.getCorrespondentBank() != null){
            log.getCorrespondentBank().setAddress(address);
        }

        return log;
    }

    private <T extends DomainEvent> AccountLog mapEventToAccountLog(T event) {
        BeanMapper mapper = accountLogMappers.get(event.getClass());
        AccountLog accountLog = (AccountLog) mapper.map(event);
        return accountLog;
    }

    private <T extends DomainEvent> CustomerAccount mapEventToCustomerAccount(T event) {
        BeanMapper mapper = customerAccountLogMappers.get(event.getClass());
        CustomerAccount customerAccount = (CustomerAccount) mapper.map(event);
        return customerAccount;
    }

    private Date getBirthday(String cifNumber){
        if(!StringUtils.isEmpty(cifNumber)){
            return customerInformationFileService.getBirthdayByCifNumber(cifNumber);
        }
        return null;
    }

    private Address getCorrespondentBankAddress(Bank correspondentBank){
        if (correspondentBank != null && correspondentBank.getName() != null) {
            String bic = StringUtils.left(correspondentBank.getName(), 6);
            String branchCode;
            if (correspondentBank.getName().length() == 11 || correspondentBank.getName().length() == 12) {
                branchCode = StringUtils.right(correspondentBank.getName(), 3);
            } else {
                branchCode = NO_BRANCH_CODE;
            }

            Address bankAddress = getSwiftAddress(bic, branchCode);
            return bankAddress;
        }
        return null;
    }

    private void persistTransactionLogDetails(TransactionLog log, String gltsNumber) {
        if (enabled) {
            log.setTransactionReferenceNumber(new ReferenceNumberGenerator().generate(gltsNumber));
            transactionLogRepository.persist(log);
        }
    }

    private <T extends DomainEvent> void persistAccountLogDetails(T event) {
        if (enabled) {
            BeanMapper mapper = accountLogMappers.get(event.getClass());

            BeanMapper customerAccountMapper = customerAccountLogMappers.get(event.getClass());
            Boolean sheesh = (mapper.map(event) == null);

            System.out.println("NULLLLLL BA KO: ");
            System.out.println(sheesh);
            System.out.println("NULLLLLL BA KO: "+ (mapper.map(event) == null));
            accountLogRepository.persist((AccountLog) mapper.map(event));
            
            AccountLog accountLog = (AccountLog) mapper.map(event);
            
            TradeService tradeService = tradeServiceRepository.load(accountLog.getTradeServiceId());
            
        	System.out.println("tradeService.getServiceType()" + tradeService.getServiceType());
        	System.out.println("tradeService.getDocumentClass()" + tradeService.getDocumentClass());
            
            if (tradeService.getServiceType() != null && !tradeService.getDocumentClass().equals("BP")) {
            	System.out.println("Transaction is not BP");
            	customerAccountLogRepository.persist((CustomerAccount) customerAccountMapper.map(event));
            }
            
            
            
        }
    }

    private <T extends DomainEvent> void persistAccountLogDetails(T event, AccountLog accountLog) {

        if (enabled) {
        	TradeService tradeService = tradeServiceRepository.load(accountLog.getTradeServiceId());
        	//As per maam Juliet .. not to report CAC during EBC settlement HENRY
            BeanMapper customerAccountMapper = customerAccountLogMappers.get(event.getClass());
            System.out.println("Inside private <T extends DomainEvent> void persistAccountLogDetails(T event, AccountLog accountLog)  if (enabled)");
            accountLogRepository.persist(accountLog);
            if (tradeService.getServiceType().equals(ServiceType.NEGOTIATION) && 
            		(tradeService.getDocumentClass().equals("BP") || tradeService.getDocumentClass().equals("BC"))) {
            	System.out.println("Transaction is under NEGOTIATION of BC or BP");
            	customerAccountLogRepository.persist((CustomerAccount) customerAccountMapper.map(event));
            } else if (!tradeService.getDocumentClass().equals("BP") && !tradeService.getDocumentClass().equals("BC")) {
            	System.out.println("Transaction is neither BC nor BP");
            	customerAccountLogRepository.persist((CustomerAccount) customerAccountMapper.map(event));
            }
        }
    }

    private <T extends DomainEvent> void persistAccountLogDetails(AccountLog accountLog, CustomerAccount customerAccount) {

        if (enabled) {
            accountLogRepository.persist(accountLog);
            customerAccountLogRepository.persist(customerAccount);
        }
    }

    private <T extends DomainEvent> void persistAccountLogDetailsWithoutCustomerAccount(AccountLog accountLog) {

        if (enabled) {
            accountLogRepository.persist(accountLog);
        }
    }

    private <T extends DomainEvent> void persistCustomerLogDetails(T event) {
        if (enabled) {
            BeanMapper mapper = customerLogMappers.get(event.getClass());
            customerLogRepository.persist((CustomerLog) mapper.map(event));
        }
    }

    private Address getSwiftAddress(String bic, String branchCode) {
        Address address = new Address();
        if (bic != null) {
            List<Map<String, ?>> bankInformation = referenceFinder.findBankBySwiftAddress(bic, branchCode);
            if (!bankInformation.isEmpty()) {
                Map<String, ?> bankData = bankInformation.get(0);
                address.setAddress1((String) bankData.get("ADDRESS_1"));
                address.setAddress3((String) bankData.get("ADDRESS_2") + (String) bankData.get("ADDRESS_3") + (String) bankData.get("ADDRESS_4"));
            }
        }
        return address;

    }

    private BigDecimal getConversionRate(TradeService tradeService, Boolean isContingent) {

        String transactionCurrency = (String) tradeService.getDetails().get("currency");

        if (transactionCurrency != null && !"PHP".equals(transactionCurrency.trim())) {
            BigDecimal thirdToUsdRate = getThirdToUsdRate(tradeService, transactionCurrency);
            BigDecimal usdToPhpRate = BigDecimal.ZERO;
            if (isContingent) {
                usdToPhpRate = getUsdToPhpUrrRate(tradeService);
            } else {
                usdToPhpRate = getUsdToPhpSpecialRate(tradeService);
            }
            BigDecimal rate = thirdToUsdRate.multiply(usdToPhpRate);

            if (rate.compareTo(BigDecimal.ZERO) > 0) {
                return rate;
            } else if (rate.compareTo(BigDecimal.ZERO) == 0) {
                if ("USD".equals(transactionCurrency)) {
                    return ratesService.getUrrConversionRateToday();
                } else {
                    BigDecimal urr = ratesService.getUrrConversionRateToday();
                    BigDecimal thirdToUSD = ratesService.getAngolConversionRate(transactionCurrency,"USD",2);
                    return thirdToUSD.multiply(urr);
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getConversionRate(TradeService tradeService, String transactionCurrency, Boolean isContingent) {

        if (transactionCurrency != null && !"PHP".equals(transactionCurrency.trim())) {
            BigDecimal thirdToUsdRate = getThirdToUsdRate(tradeService, transactionCurrency);
            BigDecimal usdToPhpRate = BigDecimal.ZERO;
            if (isContingent) {
                usdToPhpRate = getUsdToPhpUrrRate(tradeService);
            } else {
                usdToPhpRate = getUsdToPhpSpecialRate(tradeService);
            }
            BigDecimal rate = thirdToUsdRate.multiply(usdToPhpRate);

            if (rate.compareTo(BigDecimal.ZERO) > 0) {
                return rate;
            } else if (rate.compareTo(BigDecimal.ZERO) == 0) {
                if ("USD".equals(transactionCurrency)) {
                    return ratesService.getUrrConversionRateToday();
                } else {
                    BigDecimal urr = ratesService.getUrrConversionRateToday();
                    BigDecimal thirdToUSD = ratesService.getAngolConversionRate(transactionCurrency,"USD",2);
                    return thirdToUSD.multiply(urr);
                }
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getThirdToUsdRate(TradeService tradeService, String originalCurrency) {

        BigDecimal rate;

        if ("USD".equals(originalCurrency)) {
            return BigDecimal.ONE;
        }

        if (tradeService.getDetails().get(originalCurrency + "-USD_special_rate_cash") != null) {
            rate = new BigDecimal((String) tradeService.getDetails().get(originalCurrency + "-USD_special_rate_cash"));
        } else if (tradeService.getDetails().get(originalCurrency + "-USD_special_rate_charges") != null) {
            rate = new BigDecimal((String) tradeService.getDetails().get(originalCurrency + "-USD_special_rate_charges"));
        } else if (tradeService.getDetails().get(originalCurrency + "-USD_urr") != null) {
            rate = new BigDecimal((String) tradeService.getDetails().get(originalCurrency + "-USD_urr"));
        } else {
            try {
                rate = new BigDecimal((String) tradeService.getDetails().get(originalCurrency + "-USD"));
            } catch (Exception e) {
                rate = BigDecimal.ONE;
            }
        }

        return rate;
    }

    public BigDecimal getUsdToPhpSpecialRate(TradeService tradeService) {

        BigDecimal rate;

        if (tradeService.getDetails().get("USD-PHP_special_rate_cash") != null) {
            rate = new BigDecimal((String) tradeService.getDetails().get("USD-PHP_special_rate_cash"));
        } else { // else if (tradeService.getDetails().get("USD-PHP_pass_on_rate_charges") != null) {
            rate = new BigDecimal((String) tradeService.getDetails().get("USD-PHP_special_rate_charges"));
        }

        return rate;
    }

    public BigDecimal getUsdToPhpUrrRate(TradeService tradeService) {

        BigDecimal rate;

        if (tradeService.getDetails().get("USD-PHP_urr") != null) {
            rate = new BigDecimal((String) tradeService.getDetails().get("USD-PHP_urr"));
        } else {
            if (tradeService.getSpecialRateUrr() != null) {
                rate = tradeService.getSpecialRateUrr();
            } else {
                rate = BigDecimal.ZERO;
            }
        }

        return rate;
    }

    private void addPaymentDetails(TradeService tradeService, String paymentDetailsTag) {
        tradeService.getDetails().put(paymentDetailsTag, getPaymentDetails(tradeService.getTradeServiceId()));
    }

    private String getPaymentDetails(TradeServiceId tradeServiceId) {
        Payment payment = paymentRepository.get(tradeServiceId, ChargeType.PRODUCT);
        if (payment != null) {
            Iterator<PaymentDetail> details = payment.getDetails().iterator();
            StringBuilder sb = new StringBuilder();
            while (details.hasNext()) {
                PaymentDetail detail = details.next();
                sb.append(detail.getPaymentInstrumentType().toString());
                if (details.hasNext()) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }
        return "";
    }

    private BigDecimal getConversionRateFromPayment(TradeService tradeService, Boolean isSellingRate){
    	String transactionCurrency = (String) tradeService.getDetails().get("negotiationCurrency");
    	
    	Payment productPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
    	
    	BigDecimal rate = BigDecimal.ZERO;
    	
   	 	if (productPayment != null) {

            Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();
            
            while (productPaymentDetails.hasNext()) {
           	 PaymentDetail productPaymentDetail = productPaymentDetails.next();

		        if (transactionCurrency != null && !"PHP".equals(transactionCurrency.trim())) {
		            BigDecimal thirdToUsdRate = BigDecimal.ONE;
		            BigDecimal usdToPhpRate = BigDecimal.ZERO;
		            
		            if ("USD".equals(transactionCurrency)) {
		              	 thirdToUsdRate = BigDecimal.ONE;
		            } else {
		              	 if (productPaymentDetail.getSpecialRateThirdToUsd() != null) {
		              		 thirdToUsdRate = productPaymentDetail.getSpecialRateThirdToUsd();
		              	 } else if (tradeService.getSpecialRateThirdToUsd() != null){
		              		 thirdToUsdRate = tradeService.getSpecialRateThirdToUsd();
		              	 } else {
		              		 thirdToUsdRate = BigDecimal.ONE;
		              	 }
		            } 
		            
		            if (isSellingRate) {               
		                if (productPaymentDetail.getSpecialRateUsdToPhp() != null) {
		                	usdToPhpRate = productPaymentDetail.getSpecialRateUsdToPhp();
		                } else if (tradeService.getSpecialRateUsdToPhp() != null){
		                    usdToPhpRate = tradeService.getSpecialRateUsdToPhp();
		                } else {
		               	 	usdToPhpRate = BigDecimal.ZERO;
		                }
		            } else {
		            	if (productPaymentDetail.getUrr() != null) {
		            		usdToPhpRate = productPaymentDetail.getUrr();
		                } else if (tradeService.getSpecialRateUrr() != null){
		                	usdToPhpRate = tradeService.getSpecialRateUrr();
		                } else {
		                	usdToPhpRate = BigDecimal.ZERO;
		                }
		            }
		            rate = thirdToUsdRate.multiply(usdToPhpRate);
		        }
            }
        }

        return rate;
    }
    
    private void saveAmlaNonLcTransaction(TradeService tradeService, PaymentDetail productPaymentDetail){
    	tradeService.getDetails().put("amlaCasaFlag", "0");
 		tradeService.getDetails().put("amlaCheckFlag", "0");
		tradeService.getDetails().put("amlaCashFlag", "0");
  		tradeService.getDetails().put("amlaCashFlagPhp", "0");
  		tradeService.getDetails().put("amlaRemittanceFlag", "0");
          		
		 if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASA)){
			 tradeService.getDetails().put("amlaCasaFlag", "1");
			 tradeService.getDetails().put("amlaCheckFlag", "0");
			 tradeService.getDetails().put("amlaCashFlag", "0");
			 tradeService.getDetails().put("amlaRemittanceFlag", "0");
			 tradeService.getDetails().put("amlaCheckFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaCasaFlagAmount", productPaymentDetail.getAmountInLcCurrency().toString());
			 tradeService.getDetails().put("amlaCashFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaRemittanceFlagAmount", "0.00");
		 }
		 if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASH)){
			 tradeService.getDetails().put("amlaCashFlag", "1");
			 tradeService.getDetails().put("amlaCasaFlag", "0");
			 tradeService.getDetails().put("amlaCheckFlag", "0");
			 tradeService.getDetails().put("amlaRemittanceFlag", "0");
			 tradeService.getDetails().put("amlaCheckFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaCasaFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaCashFlagAmount", productPaymentDetail.getAmountInLcCurrency().toString());
			 tradeService.getDetails().put("amlaRemittanceFlagAmount", "0.00");
		 }
		 if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CHECK)){
			 tradeService.getDetails().put("amlaCheckFlag", "1");
			 tradeService.getDetails().put("amlaCasaFlag", "0");
			 tradeService.getDetails().put("amlaCashFlag", "0");
			 tradeService.getDetails().put("amlaRemittanceFlag", "0");
			 tradeService.getDetails().put("amlaCheckFlagAmount", productPaymentDetail.getAmountInLcCurrency().toString());
			 tradeService.getDetails().put("amlaCasaFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaCashFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaRemittanceFlagAmount", "0.00");
		 }
		 if(productPaymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE)){
			 tradeService.getDetails().put("amlaRemittanceFlag", "1");
			 tradeService.getDetails().put("amlaCasaFlag", "0");
			 tradeService.getDetails().put("amlaCheckFlag", "0");
			 tradeService.getDetails().put("amlaCashFlag", "0");
			 tradeService.getDetails().put("amlaCheckFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaCasaFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaCashFlagAmount", "0.00");
			 tradeService.getDetails().put("amlaRemittanceFlagAmount", productPaymentDetail.getAmountInLcCurrency().toString());
		 }
		 
		 String transactionCurrency = (String) tradeService.getDetails().get("currency");
		 
		 if (transactionCurrency != null && !"PHP".equals(transactionCurrency.trim())) {
	         BigDecimal thirdToUsdRate = BigDecimal.ONE;
	         BigDecimal usdToPhpRate = BigDecimal.ZERO;
	         
	         if ("USD".equals(transactionCurrency)) {
	        	 thirdToUsdRate = BigDecimal.ONE;
	         } else {
	        	 if (productPaymentDetail.getSpecialRateUsdToPhp() != null) {
	        		 thirdToUsdRate = productPaymentDetail.getSpecialRateThirdToUsd();
	        	 } else if (tradeService.getSpecialRateThirdToUsd() != null){
	        		 thirdToUsdRate = tradeService.getSpecialRateThirdToUsd();
	        	 } else {
	        		 thirdToUsdRate = BigDecimal.ONE;
	        	 }
	         }                                     
	         
	         if (productPaymentDetail.getSpecialRateUsdToPhp() != null) {
	        	 usdToPhpRate = productPaymentDetail.getSpecialRateUsdToPhp();
	         } else if (tradeService.getSpecialRateUsdToPhp() != null){
	             usdToPhpRate = tradeService.getSpecialRateUsdToPhp();
	         } else {
	        	 usdToPhpRate = BigDecimal.ZERO;
	         }
	         
	         BigDecimal rate = thirdToUsdRate.multiply(usdToPhpRate);
	         
	         tradeService.getDetails().put(CURRENT_RATE_KEY, rate);
	     }
    }
    
    private void persistTransactionLogPerPayment(TradeService tradeService, TransactionLog log, String gltsNumber) {

        try {

            System.out.println("\n >>>>>>>>>>>>>>>>> In persistTransactionLogPerPayment() \n");

            Payment productPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);

            Payment chargesPayment = null;
            /*
            if (tradeService.getServiceType() != null && (!tradeService.getServiceType().equals(ServiceType.PAYMENT) ||
                    !tradeService.getServiceType().equals(ServiceType.PAYMENT_OTHER) ||
                    !tradeService.getServiceType().equals(ServiceType.REFUND))) {
                chargesPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
            }
            */
            if (tradeService.getServiceType() != null && (!tradeService.getServiceType().equals(ServiceType.PAYMENT) ||
                    !tradeService.getServiceType().equals(ServiceType.PAYMENT_OTHER))) {
                chargesPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
            }

            BigDecimal totalFxProfitAmount = BigDecimal.ZERO;  // In PESO

            String productCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                productCurrency = tradeService.getProductChargeCurrency().toString();  // Get LC Currency
            } else {
                if (productCurrency.equalsIgnoreCase("")) {
                    if (tradeService.getDetails().containsKey("currency")) {
                        productCurrency = tradeService.getDetails().get("currency").toString();
                    }
                }
            }

            if (productPayment != null) {

                Iterator<PaymentDetail> productPaymentDetails = productPayment.getDetails().iterator();

                while (productPaymentDetails.hasNext()) {

                    PaymentDetail productPaymentDetail = productPaymentDetails.next();

                    // FX profit
                    BigDecimal fxProfitAmount = placeFxProfitOrLossInPaymentMap(productPaymentDetail, productCurrency);
                    totalFxProfitAmount = totalFxProfitAmount.add(fxProfitAmount);

                /*
                BigDecimal amount = productPaymentDetail.getAmount();
                Currency currency = productPaymentDetail.getCurrency();

                BigDecimal transactionAmount;
                BigDecimal rate = BigDecimal.ZERO;

                if (currency.equals(Currency.getInstance("PHP"))) {
                    transactionAmount = amount;
                } else {
                    rate = getConversionRate(tradeService, currency.getCurrencyCode(), Boolean.FALSE);
                    transactionAmount = amount.multiply(rate);
                    transactionAmount = transactionAmount.setScale(2, RoundingMode.UP);
                }

                // Make Hibernate insert the record
                TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                clonedLog.setTransactionReferenceNumber(null);

                clonedLog.setSettlementAmount(amount);
                clonedLog.setSettlementCurrency(currency);
                clonedLog.setExchangeRate(rate);

                clonedLog.setTransactionAmount(transactionAmount);

                clonedLog.setRemarks("Product payment");

                persistTransactionLogDetails(clonedLog, gltsNumber);
                */
                }
            }

            if (chargesPayment != null) {

                Set<ServiceCharge> serviceCharges = tradeService.getServiceCharges();

                BigDecimal totalIncomeCharges     = BigDecimal.ZERO;
                BigDecimal totalGovtCharges       = BigDecimal.ZERO;
                BigDecimal totalCollectionCharges = BigDecimal.ZERO;
                BigDecimal totalMiscCharges       = BigDecimal.ZERO;

                BigDecimal totalIncomeChargesPeso     = BigDecimal.ZERO;
                BigDecimal totalGovtChargesPeso       = BigDecimal.ZERO;
                BigDecimal totalCollectionChargesPeso = BigDecimal.ZERO;
                BigDecimal totalMiscChargesPeso       = BigDecimal.ZERO;

                Currency chargesSettlementCurrency = null;
                BigDecimal rate = null;

                mainLoop:
                for (ServiceCharge serviceCharge : serviceCharges) {

                    chargesSettlementCurrency = serviceCharge.getOriginalCurrency();
                    rate = getConversionRate(tradeService, chargesSettlementCurrency.getCurrencyCode(), Boolean.FALSE);

                    BigDecimal chargePeso;
                    BigDecimal diffAbs = serviceCharge.getAmount().subtract(serviceCharge.getDefaultAmount()).abs();
                    if (diffAbs.doubleValue() > 1) {
                        chargePeso = serviceCharge.getAmount();
                    } else {
                        chargePeso = serviceCharge.getDefaultAmount();
                    }

                    for (String income : incomeList) {
                        if (serviceCharge.getChargeId().toString().equalsIgnoreCase(income)) {
                            totalIncomeCharges = totalIncomeCharges.add(serviceCharge.getOriginalAmount());
                            totalIncomeChargesPeso = totalIncomeChargesPeso.add(chargePeso);
                            continue mainLoop;
                        }
                    }
                    for (String govt : govtList) {
                        if (serviceCharge.getChargeId().toString().equalsIgnoreCase(govt)) {
                            totalGovtCharges = totalGovtCharges.add(serviceCharge.getOriginalAmount());
                            totalGovtChargesPeso = totalGovtChargesPeso.add(chargePeso);
                            continue mainLoop;
                        }
                    }
                    for (String collection : collectionList) {
                        if (serviceCharge.getChargeId().toString().equalsIgnoreCase(collection)) {
                            totalCollectionCharges = totalCollectionCharges.add(serviceCharge.getOriginalAmount());
                            totalCollectionChargesPeso = totalCollectionChargesPeso.add(chargePeso);
                            continue mainLoop;
                        }
                    }
                    for (String misc : miscList) {
                        if (serviceCharge.getChargeId().toString().equalsIgnoreCase(misc)) {
                            totalMiscCharges = totalMiscCharges.add(serviceCharge.getOriginalAmount());
                            totalMiscChargesPeso = totalMiscChargesPeso.add(chargePeso);
                            continue mainLoop;
                        }
                    }
                }

                if (totalGovtChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                    // Make Hibernate insert the record
                    TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                    clonedLog.setTransactionReferenceNumber(null);

                    clonedLog.setTransactionTypeCode("CPYGA");
                    if (!chargesSettlementCurrency.getCurrencyCode().equals("PHP")) {
                        clonedLog.setSettlementAmount(totalGovtCharges);
                    } else {
                        clonedLog.setSettlementAmount(BigDecimal.ZERO);
                    }
                    clonedLog.setSettlementCurrency(chargesSettlementCurrency);
                    clonedLog.setExchangeRate(rate);

                    totalGovtChargesPeso = totalGovtChargesPeso.setScale(2, RoundingMode.UP);
                    clonedLog.setTransactionAmount(totalGovtChargesPeso);

                    clonedLog.setRemarks("Charges payment");

                    clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                    persistTransactionLogDetails(clonedLog, gltsNumber);
                }

                if (totalCollectionChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                    // Make Hibernate insert the record
                    TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                    clonedLog.setTransactionReferenceNumber(null);

                    clonedLog.setTransactionTypeCode("CCOL");
                    if (!chargesSettlementCurrency.getCurrencyCode().equals("PHP")) {
                        clonedLog.setSettlementAmount(totalCollectionCharges);
                    } else {
                        clonedLog.setSettlementAmount(BigDecimal.ZERO);
                    }
                    clonedLog.setSettlementCurrency(chargesSettlementCurrency);
                    clonedLog.setExchangeRate(rate);

                    totalCollectionChargesPeso = totalCollectionChargesPeso.setScale(2, RoundingMode.UP);
                    clonedLog.setTransactionAmount(totalCollectionChargesPeso);

                    clonedLog.setRemarks("Charges payment");

                    clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                    persistTransactionLogDetails(clonedLog, gltsNumber);
                }

                if (totalMiscChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                    // Make Hibernate insert the record
                    TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                    clonedLog.setTransactionReferenceNumber(null);

                    clonedLog.setTransactionTypeCode("CMISC");
                    if (!chargesSettlementCurrency.getCurrencyCode().equals("PHP")) {
                        clonedLog.setSettlementAmount(totalMiscCharges);
                    } else {
                        clonedLog.setSettlementAmount(BigDecimal.ZERO);
                    }
                    clonedLog.setSettlementCurrency(chargesSettlementCurrency);
                    clonedLog.setExchangeRate(rate);

                    totalMiscChargesPeso = totalMiscChargesPeso.setScale(2, RoundingMode.UP);
                    clonedLog.setTransactionAmount(totalMiscChargesPeso);

                    clonedLog.setRemarks("Charges payment");

                    clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                    persistTransactionLogDetails(clonedLog, gltsNumber);
                }

                List<PaymentDetail> chargesPaymentList = new ArrayList<PaymentDetail>(chargesPayment.getDetails());

                String incomeTranCode = "CMISC";
                if (chargesPaymentList != null && !chargesPaymentList.isEmpty()) {
                    Collections.sort(chargesPaymentList, new PaymentDetailComparator());
                    incomeTranCode = getIncomeChargesTransactionTypeCode((chargesPaymentList.get(0)).getPaymentInstrumentType().toString());
                }

                if (totalIncomeChargesPeso.compareTo(BigDecimal.ZERO) > 0) {

                    // Make Hibernate insert the record
                    TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                    clonedLog.setTransactionReferenceNumber(null);

                    clonedLog.setTransactionTypeCode(incomeTranCode);

                    if (!chargesSettlementCurrency.getCurrencyCode().equals("PHP")) {
                        clonedLog.setSettlementAmount(totalIncomeCharges);
                    } else {
                        clonedLog.setSettlementAmount(BigDecimal.ZERO);
                    }
                    clonedLog.setSettlementCurrency(chargesSettlementCurrency);
                    clonedLog.setExchangeRate(rate);

                    totalIncomeChargesPeso = totalIncomeChargesPeso.setScale(2, RoundingMode.UP);
                    clonedLog.setTransactionAmount(totalIncomeChargesPeso);

                    clonedLog.setRemarks("Charges payment");

                    clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                    persistTransactionLogDetails(clonedLog, gltsNumber);
                }

                Iterator<PaymentDetail> chargesPaymentDetails = chargesPayment.getDetails().iterator();
                while (chargesPaymentDetails.hasNext()) {

                    PaymentDetail chargesPaymentDetail = chargesPaymentDetails.next();

                    BigDecimal fxProfitAmount = placeFxProfitOrLossInPaymentMap(chargesPaymentDetail, chargesSettlementCurrency.toString());
                    totalFxProfitAmount = totalFxProfitAmount.add(fxProfitAmount);
                }

                if (totalFxProfitAmount.compareTo(BigDecimal.ZERO) > 0) {

                    // Make Hibernate insert the record
                    TransactionLog clonedLog = (TransactionLog)SerializationUtils.clone(log);
                    clonedLog.setTransactionReferenceNumber(null);

                    clonedLog.setTransactionTypeCode(incomeTranCode);

                    clonedLog.setSettlementAmount(BigDecimal.ZERO);
                    clonedLog.setSettlementCurrency(Currency.getInstance("PHP"));
                    clonedLog.setExchangeRate(BigDecimal.ZERO);  // No single rate; numerous rates may have been used

                    totalFxProfitAmount = totalFxProfitAmount.setScale(2, RoundingMode.UP);
                    clonedLog.setTransactionAmount(totalFxProfitAmount);

                    clonedLog.setRemarks("Total FX profit");

                    clonedLog.setTransactionType(TransactionType.CREDIT.toString());

                    persistTransactionLogDetails(clonedLog, gltsNumber);
                }
            }

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.persistTransactionLogPerPayment()");
            e.printStackTrace();
        }
    }

    /**
     * This is a "clone" of the method of the same name in AccountingService class. FX profit is also needed in AMLA.
     * Needed to define here because the accounting entries and AMLA inserts were generated "simultaneously" via
     * the same fired event.
     *
     * @param paymentDetail      PaymentDetail object which will be charged for excess
     * @param lcCurrency         Currency of the Product in String
     */
    private BigDecimal placeFxProfitOrLossInPaymentMap(PaymentDetail paymentDetail, String lcCurrency) {

        // String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
        // System.out.println("paymentName:" + paymentName);

        String profitString;
        BigDecimal amount = BigDecimal.ZERO;

        try {

            if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {

                if (paymentDetail.getUrr() != null && paymentDetail.getSpecialRateUsdToPhp() != null) {

                    BigDecimal differenceMultiplier = paymentDetail.getSpecialRateUsdToPhp().subtract(paymentDetail.getUrr());
                    System.out.println("differenceMultiplier:" + differenceMultiplier);

                    if (differenceMultiplier.compareTo(BigDecimal.ZERO) == 1) {

                        profitString = "fxProfitPHP";
                        System.out.println(differenceMultiplier);
                        System.out.println("profitString:" + profitString);

                        if (!lcCurrency.equalsIgnoreCase("PHP")) {

                            if (lcCurrency.equalsIgnoreCase("USD")) {

                                System.out.println("USD in FX profit loss");

                                // To avoid rounding off
                                double denom = paymentDetail.getSpecialRateUsdToPhp().doubleValue();
                                double quotient = paymentDetail.getAmount().doubleValue() / denom;
                                BigDecimal amountOrig = new BigDecimal(quotient);

                                System.out.println("paymentDetail.getAmount() in php:" + paymentDetail.getAmount());
                                System.out.println("amountOrig :" + amountOrig);

                                amount = differenceMultiplier.multiply(amountOrig);

                                System.out.println("fx profit loss amount :" + amount);

                            } else { //if (!lcCurrency.equalsIgnoreCase("USD") && !lcCurrency.equalsIgnoreCase("PHP")) {

                                //THIRD
                                System.out.println("THIRD in FX profit loss");
                                System.out.println("paymentDetail.getAmount() in php:" + paymentDetail.getAmount());
                                System.out.println("paymentDetail.getSpecialRateThirdToUsd():" + paymentDetail.getSpecialRateThirdToUsd());

                                // To avoid rounding off
                                double denom = paymentDetail.getSpecialRateUsdToPhp().multiply(paymentDetail.getSpecialRateThirdToUsd()).doubleValue();
                                double quotient = paymentDetail.getAmount().doubleValue() / denom;
                                BigDecimal amountOrig = new BigDecimal(quotient);

                                System.out.println("paymentDetail.getAmount() in php: " + paymentDetail.getAmount());
                                System.out.println("amountOrig: " + amountOrig);

                                amount = differenceMultiplier.multiply(amountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd()));

                                System.out.println("fx profit loss amount: " + amount);
                            }
                        }

                        System.out.println("profitString: " + profitString);
                        System.out.println("amount: " + amount);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("\n###### Exception occured in AmlaInformationLogger.placeFxProfitOrLossInPaymentMap()");
            e.printStackTrace();
        }

        return amount;  // In PESO
    }

    private String getIncomeChargesTransactionTypeCode(String paymentMode) {

        String tranCode = "";
        if (paymentMode != null) {
            if (paymentMode.equals(PaymentInstrumentType.CASA.toString())) {
                tranCode = "IIPD";
            } else if (paymentMode.equals(PaymentInstrumentType.CHECK.toString())) {
                tranCode = "IIPM";
            } else if (paymentMode.equals(PaymentInstrumentType.CASH.toString())) {
                tranCode = "IIPC";
            } else {
                tranCode = "CMISC";
            }
        } else {
            tranCode = "CMISC";
        }

        return tranCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setReferenceFinder(ReferenceFinder referenceFinder) {
        this.referenceFinder = referenceFinder;
    }

    public CustomerInformationFileService getCustomerInformationFileService() {
        return customerInformationFileService;
    }

    public void setCustomerInformationFileService(CustomerInformationFileService customerInformationFileService) {
        this.customerInformationFileService = customerInformationFileService;
    }
}
