package com.ucpb.tfs2.application.service;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.domain.payment.*;
import com.ucpb.tfs.domain.payment.casa.CasaAccount;
import com.ucpb.tfs.domain.payment.casa.parser.exception.InvalidAccountNumberFormatException;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentEventListeners;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaidEvent;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaymentReversedEvent;
import com.ucpb.tfs.domain.payment.modes.Loan;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.EmployeeRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.event.TradeServiceEventListeners;
import com.ucpb.tfs.domain.service.event.TradeServiceSingleItemPaidEvent;
import com.ucpb.tfs.interfaces.services.CustomerInformationFileService;
import com.ucpb.tfs.interfaces.services.ServiceException;
import com.ucpb.tfs.interfaces.services.exception.LoanAlreadyReleasedException;
import com.ucpb.tfs.interfaces.services.impl.NonExistentLoanException;
import com.ucpb.tfs2.application.service.casa.CasaService;
import com.ucpb.tfs2.application.service.casa.exception.CasaServiceException;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static com.ucpb.tfs.domain.service.ChargeType.*;

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: PaymentService
 */

/** 
   SCR/ER Number: 20170725-096  Redmine 6497
   SCR/ER Description: Credit to CASA as payment for Domestic regular sight LC negotiation was effected but status on TFS remain unpaid.
   Revised by: Jesse James Joson
   Date Deployed: 
   Program Revision Details: Include the status of PaymentDetail on the condition for updating the payment tables.
   PROJECT: CORE
   MEMBER TYPE  : Java
   Project Name: PaymentService.java
 */

@Component
@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    private CasaService casaService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Autowired
    private LoanService loanService;

    @Autowired
    CustomerInformationFileService customerInformationFileService;

    @Autowired
    TradeServiceEventListeners tradeServiceEventListeners;
    
    @Autowired
    PaymentEventListeners paymentEventListeners;

    public Payment createProductPaymentFromMap(TradeService tradeService, Map paymentDetails) {

        // attempt to load payment record
        Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);

        // if one does not exist,
        if (payment == null) {
            // we create a new one
            payment = new Payment(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
        } else {
            payment.deleteAllPaymentDetails();
        }

        List<Map> details = (List) paymentDetails.get("paymentDetails");

        // iterate through the payment details
        for (Map detail : details) {

            String paymentMode = (String) detail.get("paymentMode");
            PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(paymentMode);

            String referenceNumber = null;

            String accountNumber = (String) detail.get("accountNumber");
            String currency = (String) detail.get("currency");
            String amount = (String) detail.get("amount");
            String tradeSuspenseAccount = (String) detail.get("tradeSuspenseAccount");
            String accountName = (String)detail.get("accountName");

            // depending on the payment mode, add to the payment details for this payment
            switch (paymentInstrumentType) {
                case CASA:
                    referenceNumber = accountNumber;

                    // add a payment of this type, all conversion rates are null since we pay in PHP
                    payment.addOrUpdateItem(paymentInstrumentType,
                            referenceNumber,
                            new BigDecimal(amount),
                            Currency.getInstance(currency),
                            null, null, null, null, null, null, null,accountName);
                    break;

                case CHECK:
                case CASH:
                case IBT_BRANCH:

                    referenceNumber = tradeSuspenseAccount;

                    // add a payment of this type, all conversion rates are null since we pay in PHP
                    payment.addOrUpdateItem(paymentInstrumentType,
                            referenceNumber,
                            new BigDecimal(amount),
                            Currency.getInstance(currency),
                            null, null, null, null, null, null, null,accountName);
                    break;
            }
        }

        // return our payment record to the caller
        return payment;
    }

    public long payByLoan(Long paymentDetailId, Loan loan, String transactingUser, TradeService tradeService) {

        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        PaymentDetail detail = payment.getPaymentDetail(paymentDetailId);

        long sequenceNumber = loanService.createLoan(loan, transactingUser, detail, tradeService);
        detail.setSequenceNumber(sequenceNumber);
        detail.setFacilityId(loan.getFacility().getFacilityId());
        detail.setFacilityType(loan.getFacility().getFacilityType());
        detail.setFacilityReferenceNumber(loan.getFacility().getFacilityReferenceNumber());
        detail.setPaymentCode(loan.getPaymentCode());
        detail.setForInquiry();
        detail.setWithCramApproval(loan.getApprovedByCram());
        payment.put(detail);

        paymentRepository.saveOrUpdate(payment);
        payTransaction(payment,paymentDetailId, tradeService);

        return sequenceNumber;
    }

    //TODO: Throw exceptions for invalid userId/paymentDetailId
    public String payViaCasaAccount(Payment payment, Long paymentDetailId, String transactingUser, TradeService tradeService, String supervisorId) throws CasaServiceException {
        PaymentDetail detail = payment.getPaymentDetail(paymentDetailId);
        Employee employee = employeeRepository.getEmployee(new UserId(transactingUser));

        System.out.println("transactingUser is " + employee.getFirstName() + " " + employee.getLastName());
        System.out.println("unit code is " + employee.getUnitCode());
        PaymentStatus detailStatus = detail.getStatus();

        String transactionNumber;

        try{
        	payment.putById(detail);
        	payCasaTransaction(payment,paymentDetailId, tradeService, detailStatus);
        	if (SETTLEMENT.equals(payment.getChargeType())){
        		System.out.println("crediting account...");
        		transactionNumber = casaService.creditAccount(payment,paymentDetailId, employee,supervisorId);
        	} else {
        		System.out.println("debitting account...");
        		transactionNumber = casaService.debitAccount(payment,paymentDetailId, employee,supervisorId);
        	}
        	
        	setAfterPaymentPnNumber(Long.valueOf(transactionNumber),detail.getId());
        }catch(CasaServiceException e){
        	if(e.getErrorCode().equalsIgnoreCase("DEADMA") || e.getErrorCode().equalsIgnoreCase("DUPLICATE")){
        		setAfterPaymentPnNumber(Long.valueOf(-9999),detail.getId());        		
        	}else{
        		reversePayment(paymentDetailId, tradeService, null);
        	}
        	throw new CasaServiceException(e.getMessage(),e.getCasaErrorMessage(),e.getErrorCode());
        }catch(Exception e){
        	e.printStackTrace();
        	reversePayment(paymentDetailId, tradeService, null);
        	throw new CasaServiceException("Exception in paying Casa Accounts","Please try again.","DB ERROR");
        }
        return transactionNumber;
    }

    public long reverseLoan(Long paymentDetailId, String transactingUser) throws LoanAlreadyReleasedException, NonExistentLoanException {
        PaymentDetail detail = paymentRepository.getPaymentDetail(paymentDetailId);
        long sequenceNumber =  loanService.createLoanReversalRequest(detail.getPnNumber(),transactingUser);
        detail.setSequenceNumber(sequenceNumber);
        detail.setForInquiry();
        paymentRepository.saveOrUpdate(detail);
        return sequenceNumber;
    }

    public boolean reverseCasaPayment(Long paymentDetailId, String transactingUser, TradeService tradeService,
                                      TradeServiceId reversalTradeServiceId) throws CasaServiceException {
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        PaymentDetail detail = payment.getPaymentDetail(paymentDetailId);
        Employee employee = employeeRepository.getEmployee(new UserId(transactingUser));
        String workTaskId;

        if (SETTLEMENT.equals(payment.getChargeType())) {
            workTaskId = casaService.reverseCreditTransaction(payment,paymentDetailId, employee);
        } else {
            workTaskId = casaService.reverseDebitTransaction(payment,paymentDetailId, employee);
        }
        detail.setPnNumber(Long.valueOf(workTaskId));
        paymentRepository.saveOrUpdate(detail);
        return reversePayment(paymentDetailId,tradeService,reversalTradeServiceId);
    }

    public boolean reverseCasaPayment(Long paymentDetailId, String transactingUser, TradeService tradeService,
                                      TradeServiceId reversalTradeServiceId, String supervisorId) throws CasaServiceException {
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        PaymentDetail detail = payment.getPaymentDetail(paymentDetailId);
        Employee employee = employeeRepository.getEmployee(new UserId(transactingUser));
        Long pnNumber = detail.getPnNumber();
        String workTaskId;
        boolean result = false;
        try{
        	result = reverseCasaPayment(paymentDetailId,tradeService,reversalTradeServiceId);
        	if (SETTLEMENT.equals(payment.getChargeType())) {
        		workTaskId = casaService.reverseCreditTransaction(payment,paymentDetailId, employee, supervisorId,pnNumber);
        	} else {
        		workTaskId = casaService.reverseDebitTransaction(payment,paymentDetailId, employee, supervisorId,pnNumber);
        	}
        	detail.setPnNumber(Long.valueOf(workTaskId));
        	paymentRepository.saveOrUpdate(detail);
        }catch(CasaServiceException e){
        	if(!e.getErrorCode().isEmpty() && !e.getErrorCode().equals("CASA")){
        		throw new CasaServiceException("DB Exception.Transaction assumed reversed. Please check BDS for CASA posting status.",e);
        	}else{
        		payTransactionAfterException(paymentDetailId, tradeService,pnNumber);
        		throw new CasaServiceException(e.getMessage(),e.getCasaErrorMessage(),e.getErrorCode());        		
        	}
        }catch(Exception e){
        	payTransactionAfterException(paymentDetailId, tradeService,pnNumber);
        	throw new CasaServiceException("DB Exception. Please try again.",e);
        }
        return result;
    }
    
    public void payCasaTransaction(Payment payment, Long paymentDetailId, TradeService tradeService, PaymentStatus detailStatus) {
    	System.out.println(">>>>> payment: " + payment.getStatus() + " >>>>>  paymentDetail: " + detailStatus);
    	if(!payment.getStatus().equals(PaymentStatus.PAID) || !detailStatus.equals(PaymentStatus.PAID)){
			payment.payItem(paymentDetailId);
			paymentRepository.saveOrUpdate(payment);
			
			paymentEventListeners.itemPaid(new PaymentItemPaidEvent(tradeService.getTradeServiceId(),
					tradeService.getDocumentNumber().toString(), payment.getPaymentDetail(paymentDetailId)));
			tradeServiceEventListeners.afterPaymentOrReversalGenerateAccounting(new TradeServiceSingleItemPaidEvent(tradeService.getTradeServiceId(),
					tradeService.getDetails(), tradeService.getStatus(),tradeService.getLastUser()));
    	}
    }

    public void payTransaction(Payment payment, Long paymentDetailId, TradeService tradeService) {
    	if(!payment.getStatus().equals(PaymentStatus.PAID)){
			payment.payItem(paymentDetailId);
			paymentRepository.saveOrUpdate(payment);
			eventPublisher.publish(new PaymentItemPaidEvent(tradeService.getTradeServiceId(),
					tradeService.getDocumentNumber().toString(), payment.getPaymentDetail(paymentDetailId)));
			eventPublisher.publish(new TradeServiceSingleItemPaidEvent(tradeService.getTradeServiceId(),
					tradeService.getDetails(), tradeService.getStatus(),tradeService.getLastUser()));
    	}
    }
    
    public void payTransactionAfterException(Long paymentDetailId,TradeService tradeService,Long pnNumber) {
    	Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        payTransaction(payment,paymentDetailId, tradeService);
        setAfterPaymentPnNumber(pnNumber, paymentDetailId);
    }

    public void payTransaction(Payment payment, Long paymentDetailId, TradeService tradeService, String settlementAccountNumber) {
    	if(!payment.getStatus().equals(PaymentStatus.PAID)){
	    	payment.payItem(paymentDetailId);
	        paymentRepository.saveOrUpdate(payment);
	        eventPublisher.publish(new PaymentItemPaidEvent(tradeService.getTradeServiceId(),
	                settlementAccountNumber, payment.getPaymentDetail(paymentDetailId)));
	        eventPublisher.publish(new TradeServiceSingleItemPaidEvent(tradeService.getTradeServiceId(),
	                tradeService.getDetails(), tradeService.getStatus(),tradeService.getLastUser()));
    	}
    }
    
    public void payTransaction(Payment payment, Long paymentDetailId, TradeService tradeService, String settlementAccountNumber, String referenceId) {
    	if(!payment.getStatus().equals(PaymentStatus.PAID)){
    		payment.payItem(paymentDetailId);
    		paymentRepository.saveOrUpdate(payment);
    		eventPublisher.publish(new PaymentItemPaidEvent(tradeService.getTradeServiceId(),
    				settlementAccountNumber, payment.getPaymentDetail(paymentDetailId), referenceId));
    		eventPublisher.publish(new TradeServiceSingleItemPaidEvent(tradeService.getTradeServiceId(),
    				tradeService.getDetails(), tradeService.getStatus(),tradeService.getLastUser()));
    	}
    }

    //manually reverse casa payment to catch the exception in payment listener
    public boolean reverseCasaPayment(Long paymentDetailId, TradeService tradeService, TradeServiceId reversalTradeServiceId) {
    	Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
    	boolean result = payment.reverseItemPayment(paymentDetailId);
    	if(result){
    		paymentRepository.saveOrUpdate(payment);
    		paymentEventListeners.itemPaymentReversed(new PaymentItemPaymentReversedEvent(tradeService.getTradeServiceId(),
    				payment.getPaymentDetail(paymentDetailId),reversalTradeServiceId));
    	}
    	return result;
    }
    
    public boolean reversePayment(Long paymentDetailId, TradeService tradeService, TradeServiceId reversalTradeServiceId) {
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        boolean result = payment.reverseItemPayment(paymentDetailId);
        if(result){
            paymentRepository.saveOrUpdate(payment);
            eventPublisher.publish(new PaymentItemPaymentReversedEvent(tradeService.getTradeServiceId(),
                    payment.getPaymentDetail(paymentDetailId),reversalTradeServiceId));
        }
        return result;
    }

    public boolean reversePayment(Long paymentDetailId, TradeService tradeService, TradeServiceId reversalTradeServiceId, String settlementAccountNumber) {
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        boolean result = payment.reverseItemPayment(paymentDetailId);
        if(result){
            paymentRepository.saveOrUpdate(payment);
            eventPublisher.publish(new PaymentItemPaymentReversedEvent(tradeService.getTradeServiceId(),
                    payment.getPaymentDetail(paymentDetailId),reversalTradeServiceId, settlementAccountNumber));
        }

        return result;
    }

//    public CasaAccount getAccountDetails(String accountNumber, String transactingUser) throws ServiceException, CasaServiceException {
//        System.out.println("getting account details...");
//        Employee employee = employeeRepository.getEmployee(new UserId(transactingUser));
//        return casaService.getCasaAccountDetails(accountNumber,employee);
//    }

    @Deprecated
    private void payTimeOutTransaction(Payment payment, Long paymentDetailId, TradeService tradeService){
    	if(!payment.getStatus().equals(PaymentStatus.PAID)){
    		System.out.println("PAYING TRANSACTION KASI DINEDMA...");
    		PaymentDetail detail = payment.getPaymentDetail(paymentDetailId);
    		//SET A UNIQUE PN NUMBER FOR IDENTIFYING A TFS ONLY PAID TRANSACTION TO BE USED ON PAYMENT RETRIEVAL
    		//TODO:refactor
    		detail.setPnNumber(Long.valueOf(-9999));
    		payment.putById(detail);
    		
    		payment.payItem(paymentDetailId);
    		paymentRepository.saveOrUpdate(payment);
    		eventPublisher.publish(new PaymentItemPaidEvent(tradeService.getTradeServiceId(),
    				tradeService.getDocumentNumber().toString(), payment.getPaymentDetail(paymentDetailId)));
    		eventPublisher.publish(new TradeServiceSingleItemPaidEvent(tradeService.getTradeServiceId(),
    				tradeService.getDetails(), tradeService.getStatus(),tradeService.getLastUser()));
    	}
    }
    
    private boolean isValidAccountType(String accountNumber) {
        if (accountNumber.startsWith("01") && !StringUtils.substring(accountNumber, 5, 6).equals("3")) { // fcdu
            return false;
        }

        if (accountNumber.startsWith("00") &&
                (!StringUtils.substring(accountNumber, 5, 6).equals("1") &&
                !StringUtils.substring(accountNumber, 5, 6).equals("2"))) { // current/savings
            return false;
        }

        return true;
    }

    // MARV: added currency code for message string
    public CasaAccount getAccountDetails(String accountNumber, String transactingUser, Currency currency) throws ServiceException, CasaServiceException {
        System.out.println("getting account details...");
        System.out.println("account type flag: [" + StringUtils.substring(accountNumber, 5, 6) + "]");

        // checks if account number is FCDU; if so, checks the correct currency code
//        if (!isValidAccountType(accountNumber)) {
//            throw new CasaServiceException("Account Type is invalid", "Account Type is invalid");
//        }
//        if (!StringUtils.substring(accountNumber, 5, 6).equals("0") &&
//                !StringUtils.substring(accountNumber, 5, 6).equals("1") &&
//                !StringUtils.substring(accountNumber, 5, 6).equals("2")) {
//            throw new CasaServiceException("Account Type is invalid", "Account Type is invalid");
//        }

        Employee employee = employeeRepository.getEmployee(new UserId(transactingUser));
        return casaService.getCasaAccountDetails(accountNumber,employee, currency);
    }


    // send to mob boc
    public String sendToMobBoc(BigDecimal amount, String userId, String supervisorId, String casaAccountNumber, TradeService tradeService, String accountName) throws CasaServiceException {
        System.out.println("sending to mob-boc");
        Employee employee = employeeRepository.getEmployee(new UserId(userId));
        String transactionNumber;

        transactionNumber = casaService.creditAccountMobBoc(amount, employee, supervisorId, casaAccountNumber, tradeService, accountName);

        Map<String, Object> tsDetails = tradeService.getDetails();

        tsDetails.put("referenceNumber", transactionNumber);
        tsDetails.put("sentAmount", amount);


        tradeService.setPaymentStatus(PaymentStatus.PAID);

        tradeService.setDetails(tsDetails);

        tradeServiceRepository.update(tradeService);

        return transactionNumber;
    }

    // debit from remittance
    public String debitFromRemittance(BigDecimal amount, String userId, String supervisorId, String casaAccountNumber, TradeService tradeService, String accountName) throws CasaServiceException {
        System.out.println("debitting from remittance");
        Employee employee = employeeRepository.getEmployee(new UserId(userId));
        String transactionNumber;

        tradeService.setPaymentStatus(PaymentStatus.PAID);

        tradeServiceRepository.merge(tradeService);
        transactionNumber = casaService.debitAccount(amount, employee, supervisorId, casaAccountNumber, tradeService, accountName);
        return transactionNumber;
    }

    public String creditAccount(BigDecimal amount, Currency currency, String userId, String supervisorId, String casaAccountNumber, TradeService tradeService, String accountName) throws CasaServiceException {
        System.out.println("creditting...");
        Employee employee = employeeRepository.getEmployee(new UserId(userId));
        String transactionNumber;

        transactionNumber = casaService.creditAccount(amount, currency, employee, supervisorId, casaAccountNumber, tradeService, accountName);
        return transactionNumber;
    }

    public String debitAccount(BigDecimal amount, String userId, String supervisorId, String casaAccountNumber, TradeService tradeService, String accountName) throws CasaServiceException {
        System.out.println("debitting...");
        Employee employee = employeeRepository.getEmployee(new UserId(userId));
        String transactionNumber;

        transactionNumber = casaService.debitAccount(amount, employee, supervisorId, casaAccountNumber, tradeService, accountName);
        return transactionNumber;
    }


    // refund
    public String reverseCasa(String type,
                               String transactingUser,
                               TradeService tradeService,
                               String supervisorId,
                               String accountNumber,
                               Currency currency,
                               BigDecimal amount,
                               String accountName,
                               String pnNumber) throws CasaServiceException {

        Employee employee = employeeRepository.getEmployee(new UserId(transactingUser));
        String workTaskId = null;

        Map details = tradeService.getDetails();

        if ("CREDIT".equals(type)) {
            workTaskId = casaService.reverseCreditTransaction(employee, supervisorId, accountNumber, currency, amount, tradeService.getTradeServiceId(), accountName, pnNumber);
            details.put("creditTransactionStatus", "ERROR_CORRECTED");
            details.put("referenceNumberCredit", workTaskId);
        } else if ("DEBIT".equals(type)) {
            workTaskId = casaService.reverseDebitTransaction(employee, supervisorId, accountNumber, currency, amount, tradeService.getTradeServiceId(), accountName, pnNumber);
            details.put("debitTransactionStatus", "ERROR_CORRECTED");
            details.put("referenceNumberDebit", workTaskId);
        }

        tradeService.setDetails(details);

        tradeServiceRepository.merge(tradeService);

        return workTaskId;
    }

    // error correct mob-boc
    public String reverseCasaMobBoc(String transactingUser,
                              TradeService tradeService,
                              String supervisorId,
                              String accountNumber,
                              BigDecimal amount,
                              String accountName,
                              String pnNumber) throws CasaServiceException {

        Employee employee = employeeRepository.getEmployee(new UserId(transactingUser));

        Map details = tradeService.getDetails();

        String workTaskId = casaService.reverseCreditTransaction(employee, supervisorId, accountNumber, Currency.getInstance("PHP"), amount, tradeService.getTradeServiceId(), accountName, pnNumber);
        details.put("creditTransactionStatus", "ERROR_CORRECTED");

        details.put("referenceNumber", workTaskId);

        tradeService.setDetails(details);

        tradeService.setPaymentStatus(PaymentStatus.UNPAID);

        tradeServiceRepository.merge(tradeService);

        return workTaskId;
    }

    public String reverseCasaRemittance(String transactingUser,
                                    TradeService tradeService,
                                    String supervisorId,
                                    String accountNumber,
                                    BigDecimal amount,
                                    String accountName,
                                    String pnNumber) throws CasaServiceException {

        Employee employee = employeeRepository.getEmployee(new UserId(transactingUser));

        Map details = tradeService.getDetails();

        String workTaskId = casaService.reverseDebitTransaction(employee, supervisorId, accountNumber, Currency.getInstance("PHP"), amount, tradeService.getTradeServiceId(), accountName, pnNumber);

        details.put("referenceNumber", workTaskId);

        tradeService.setDetails(details);

        tradeService.setPaymentStatus(PaymentStatus.UNPAID);

        tradeServiceRepository.merge(tradeService);

        return workTaskId;
    }
    
    
    private void setAfterPaymentPnNumber(Long pnNumber,Long detailId){
    	Payment payment = paymentRepository.getPaymentByPaymentDetailId(detailId);
    	PaymentDetail paymentDetail = null;
    	for(PaymentDetail detail: payment.getDetails()){
    		if(detail.getId().equals(detailId)){
    			paymentDetail = detail;
    		}
    	}
    	
    	if(paymentDetail != null){
    		paymentDetail.setPnNumber(pnNumber);
			payment.putById(paymentDetail);
    		paymentRepository.saveOrUpdate(payment);    		
    	}
    }
}