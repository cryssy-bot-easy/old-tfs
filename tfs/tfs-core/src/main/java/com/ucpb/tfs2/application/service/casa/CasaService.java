package com.ucpb.tfs2.application.service.casa;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.service.ConversionService;
import com.ucpb.tfs.domain.audit.CasaTransactionLog;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.CasaTransactionLogRepository;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.casa.CasaAccount;
import com.ucpb.tfs.domain.payment.casa.CasaAccountFactory;
import com.ucpb.tfs.domain.payment.casa.parser.exception.InvalidAccountNumberFormatException;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.reference.Branch;
import com.ucpb.tfs.domain.reference.RefBranchRepository;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.EmployeeRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.interfaces.gateway.*;
import com.ucpb.tfs.interfaces.services.ServiceException;
import com.ucpb.tfs.interfaces.services.exception.MessageTimeoutException;
import com.ucpb.tfs2.application.service.casa.exception.CasaServiceException;
import com.ucpb.tfs2.application.service.casa.exception.CreditLimitNotSetException;
import com.ucpb.tfs2.application.service.casa.exception.NonExistentBranchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ucpb.tfs.domain.security.UserId;
/**
 */
@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class CasaService {

    private com.ucpb.tfs.interfaces.services.CasaService casaService;

    private CasaTransactionLogRepository casaTransactionLogRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private RefBranchRepository refBranchRepository;

    private ConversionService conversionService;

    private String username;

    private String password;

//    private static Long lastPaymentDetailId = 0L;
//    
//    private static UserId lastUserId = new UserId();
//    
//    private static TransactionCode lastTransactionCode = null;
    
    
    

//    public BigDecimal getCasaTransactionLimit(String allocationUnitCode) throws NonExistentBranchException {
//        Branch branch = refBranchRepository.getBranchByUnitCode(allocationUnitCode);
//        if(branch == null){
//            throw new NonExistentBranchException("Allocation unit code '" + allocationUnitCode + "' does not exist");
//        }
//        return branch.getCasaCreditLimit();
//    }

    public BigDecimal getCasaTransactionLimit(String userId) throws CreditLimitNotSetException {
        Employee employee = employeeRepository.getEmployee(new UserId(userId));

        if (employee.getCasaLimit() == null) {
            throw new CreditLimitNotSetException("Credit Limit not set.");
        }

        return employee.getCasaLimit();
    }


    public String debitAccount(Payment payment,Long paymentDetailId,Employee user, String supervisorId) throws CasaServiceException {
        System.out.println("debit account in process...");
        TransactionCode transactionCode = null;
        PaymentDetail transactionDetails = payment.getPaymentDetail(paymentDetailId);
        CasaAccount account = CasaAccountFactory.getInstance(transactionDetails.getReferenceNumber());
        if(!account.isForeign()){
            if(account.isCurrent()){
                System.out.println("debit to current...");
                transactionCode = TransactionCode.DEBIT_TO_CURRENT;
            }else{
                System.out.println("debit to savings...");
                transactionCode = TransactionCode.DEBIT_TO_SAVINGS;
            }
        }else{
            System.out.println("debit to foreign...");
            transactionCode = TransactionCode.DEBIT_TO_FOREIGN;
        }

        return sendFinancialRequest(payment,paymentDetailId,user,transactionCode,supervisorId);
    }

    public String creditAccount(Payment payment,Long paymentDetailId,Employee user,String supervisorId) throws CasaServiceException {
        System.out.println("credit account in process...");
        TransactionCode transactionCode = null;
        PaymentDetail transactionDetails = payment.getPaymentDetail(paymentDetailId);
        CasaAccount account = CasaAccountFactory.getInstance(transactionDetails.getReferenceNumber());
        if(!account.isForeign()){
            if(account.isCurrent()){
                System.out.println("credit to current...");
                transactionCode = TransactionCode.CREDIT_TO_CURRENT;
            }else{
                System.out.println("credit to savings...");
                transactionCode = TransactionCode.CREDIT_TO_SAVINGS;
            }
        }else{
            System.out.println("credit to foreign...");
            transactionCode = TransactionCode.CREDIT_TO_FOREIGN;
        }

        return sendFinancialRequest(payment,paymentDetailId,user,transactionCode,supervisorId);
    }

    public String reverseDebitTransaction(Payment payment,Long paymentDetailId,Employee user) throws CasaServiceException {
        System.out.println("reversing debit transaction...");
        TransactionCode transactionCode = null;
        PaymentDetail transactionDetails = payment.getPaymentDetail(paymentDetailId);
        CasaAccount account = CasaAccountFactory.getInstance(transactionDetails.getReferenceNumber());
        if(!account.isForeign()){
            System.out.println("debit error correct savings...");
            transactionCode = TransactionCode.DEBIT_ERROR_CORRECT_SAVINGS;
        }else{
            System.out.println("debit error correct foreign...");
            transactionCode = TransactionCode.DEBIT_ERROR_CORRECT_FOREIGN;
        }

        ReversalRequest reversalRequest = new ReversalRequest();
        reversalRequest.setAccountNumber(account.getAccountNumber());
        // MARV: added currency code for message string
        reversalRequest.setCurrency(transactionDetails.getCurrency());

        reversalRequest.setAmount(transactionDetails.getAmount());
//        reversalRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        reversalRequest.setUserId(user.getTruncatedTellerId());
        reversalRequest.setBranchCode(user.getUnitCode());
        reversalRequest.setUsername(username);
        reversalRequest.setPassword(password);
        reversalRequest.setTransactionCode(transactionCode);
        reversalRequest.setWorkTaskId(String.valueOf(transactionDetails.getPnNumber()));

        return sendReversalRequest(reversalRequest, payment.getTradeServiceId(), transactionDetails.getAccountName());
    }

    public String reverseDebitTransaction(Payment payment,Long paymentDetailId,Employee user, String supervisorId,Long pnNumber) throws CasaServiceException {
        System.out.println("reversing debit transaction...");
        TransactionCode transactionCode = null;
        PaymentDetail transactionDetails = payment.getPaymentDetail(paymentDetailId);
        CasaAccount account = CasaAccountFactory.getInstance(transactionDetails.getReferenceNumber());
        if(!account.isForeign()){
            System.out.println("debit error correct savings...");
            transactionCode = TransactionCode.DEBIT_ERROR_CORRECT_SAVINGS;
        }else{
            System.out.println("debit error correct foreign...");
            transactionCode = TransactionCode.DEBIT_ERROR_CORRECT_FOREIGN;
        }

        ReversalRequest reversalRequest = new ReversalRequest();
        reversalRequest.setAccountNumber(account.getAccountNumber());
        // MARV: added currency code for message string
        reversalRequest.setCurrency(transactionDetails.getCurrency());

        reversalRequest.setAmount(transactionDetails.getAmount());
//        reversalRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        reversalRequest.setUserId(user.getTruncatedTellerId());
        reversalRequest.setBranchCode(user.getUnitCode());
        reversalRequest.setUsername(username);
        reversalRequest.setPassword(password);
        reversalRequest.setTransactionCode(transactionCode);
        reversalRequest.setWorkTaskId(String.valueOf(pnNumber));

//        return sendReversalRequest(reversalRequest, payment.getTradeServiceId(), transactionDetails.getAccountName());
        return sendReversalRequest(reversalRequest, payment.getTradeServiceId(), transactionDetails.getAccountName(), supervisorId);
    }

    public String reverseCreditTransaction(Payment payment,Long paymentDetailId,Employee user) throws CasaServiceException {
        System.out.println("reversing credit transaction...");
        TransactionCode transactionCode = null;
        PaymentDetail transactionDetails = payment.getPaymentDetail(paymentDetailId);
        CasaAccount account = CasaAccountFactory.getInstance(transactionDetails.getReferenceNumber());
        if(!account.isForeign()){
            System.out.println("credit error correct current...");
            transactionCode = TransactionCode.CREDIT_ERROR_CORRECT_CURRENT;
        }else{
            System.out.println("credit error correct foreign...");
            transactionCode = TransactionCode.CREDIT_ERROR_CORRECT_FOREIGN;
        }

        ReversalRequest reversalRequest = new ReversalRequest();
        reversalRequest.setAccountNumber(account.getAccountNumber());

        reversalRequest.setCurrency(transactionDetails.getCurrency());

        reversalRequest.setAmount(transactionDetails.getAmount());
//        reversalRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        reversalRequest.setUserId(user.getTruncatedTellerId());

        reversalRequest.setBranchCode(user.getUnitCode());
        reversalRequest.setUsername(username);
        reversalRequest.setPassword(password);
        reversalRequest.setTransactionCode(transactionCode);
        reversalRequest.setWorkTaskId(String.valueOf(transactionDetails.getPnNumber()));

        return sendReversalRequest(reversalRequest, payment.getTradeServiceId(), transactionDetails.getAccountName());
    }

    public String reverseCreditTransaction(Payment payment,Long paymentDetailId,Employee user, String supervisorId,Long pnNumber) throws CasaServiceException {
        System.out.println("reversing credit transaction...");
        TransactionCode transactionCode = null;
        PaymentDetail transactionDetails = payment.getPaymentDetail(paymentDetailId);
        CasaAccount account = CasaAccountFactory.getInstance(transactionDetails.getReferenceNumber());
        if(!account.isForeign()){
            System.out.println("credit error correct current...");
            transactionCode = TransactionCode.CREDIT_ERROR_CORRECT_CURRENT;
        }else{
            System.out.println("credit error correct foreign...");
            transactionCode = TransactionCode.CREDIT_ERROR_CORRECT_FOREIGN;
        }

        ReversalRequest reversalRequest = new ReversalRequest();
        reversalRequest.setAccountNumber(account.getAccountNumber());

        reversalRequest.setCurrency(transactionDetails.getCurrency());

        reversalRequest.setAmount(transactionDetails.getAmount());
//        reversalRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        reversalRequest.setUserId(user.getTruncatedTellerId());

        reversalRequest.setBranchCode(user.getUnitCode());
        reversalRequest.setUsername(username);
        reversalRequest.setPassword(password);
        reversalRequest.setTransactionCode(transactionCode);
        reversalRequest.setWorkTaskId(String.valueOf(pnNumber));

//        return sendReversalRequest(reversalRequest, payment.getTradeServiceId(), transactionDetails.getAccountName());
        return sendReversalRequest(reversalRequest, payment.getTradeServiceId(), transactionDetails.getAccountName(), supervisorId);

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CasaAccount getCasaAccountDetails(String accountNumber, Employee user) throws ServiceException, CasaServiceException {
        System.out.println("getting account details in process...");
        NonFinRequest request = new NonFinRequest();
        CasaAccount account = CasaAccountFactory.getInstance(accountNumber);
        TransactionCode transactionCode;
        if(account.isCurrent()){
            System.out.println("account is current...");
            transactionCode = TransactionCode.INQUIRE_STATUS_CURRENT;
        }else{
            System.out.println("account is savings...");
            transactionCode = TransactionCode.INQUIRE_STATUS_SAVINGS;
        }
        request.setAccountNumber(accountNumber);
//        request.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        request.setUserId(user.getTruncatedTellerId());
        request.setBranchCode("909");
        request.setUsername(username);
        request.setPassword(password);
        request.setTransactionCode(transactionCode);
        CasaResponse response = casaService.sendCasaRequest(request);
        
        if(response.isSuccessful()){
            System.out.println("response is successful...");
            account.setAccountName(response.getAccountName());
            account.setAccountStatus(response.getAccountStatus());
//            account.setAccountStatus(AccountStatus.ACTIVE);
            return account;
        }
        throw new CasaServiceException("Transaction was rejected by the casa system",response.getErrorMessage(),response.getResponseCode());
    }

    public CasaAccount getCasaAccountDetails(String accountNumber, Employee user, Currency currency) throws ServiceException, CasaServiceException {
        System.out.println("getting account details in process...");
        NonFinRequest request = new NonFinRequest();
        CasaAccount account = CasaAccountFactory.getInstance(accountNumber);
        TransactionCode transactionCode;
        if(account.isCurrent()){
            System.out.println("account is current...");
            transactionCode = TransactionCode.INQUIRE_STATUS_CURRENT;
        }else{
            System.out.println("account is savings...");
            transactionCode = TransactionCode.INQUIRE_STATUS_SAVINGS;
        }


        request.setAccountNumber(accountNumber);

        // Throws exception if user does not have tellerId. This means that the user cannot transact using CASA
        if (user.getTellerId() == null) {
            throw new CasaServiceException("User not allowed to transact in CASA.", "User not allowed to transact in CASA.");
        }

//        request.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        request.setUserId(user.getTruncatedTellerId());
        request.setBranchCode("909");
        request.setUsername(username);
        request.setPassword(password);
        request.setTransactionCode(transactionCode);
        request.setCurrency(currency);
        CasaResponse response = casaService.sendCasaRequest(request);
        
        if(response.isSuccessful()){
            System.out.println("response is successful...");
            account.setAccountName(response.getAccountName());
            account.setAccountStatus(response.getAccountStatus());
//            account.setAccountStatus(AccountStatus.ACTIVE);
            return account;
        }
        throw new CasaServiceException("Transaction was rejected by the casa system",response.getErrorMessage(),response.getResponseCode());
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCasaService(com.ucpb.tfs.interfaces.services.CasaService casaService) {
        this.casaService = casaService;
    }

    private String sendFinancialRequest(Payment payment,Long paymentDetailId,Employee user, TransactionCode transactionCode,String supervisorId) throws CasaServiceException {
        System.out.println("sending financial request...");
//        if(isDuplicateTransaction(paymentDetailId,user.getUserId(),transactionCode)){
//        	System.out.println("DUPLICATE TRANSACTION: " + lastPaymentDetailId.toString() + " | " + lastTransactionCode.toString() + " | " + lastUserId.toString());
//        	throw new CasaServiceException("Possible Duplicate. Duplicate transaction will be ignored.","Possible Duplicate, Duplicate transaction will be ignored." +
//        			"Please check BDS for CASA posting status.","DUPLICATE");
//        }
        PaymentDetail transactionDetails = payment.getPaymentDetail(paymentDetailId);
        String accountName = transactionDetails.getAccountName();
        FinRequest finRequest = new FinRequest();        
        finRequest.setAccountNumber(transactionDetails.getReferenceNumber());
        // MARV: added currency code for message string
        finRequest.setCurrency(transactionDetails.getCurrency());

        finRequest.setAmount(transactionDetails.getAmount());
//        finRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        finRequest.setUserId(user.getTruncatedTellerId());

        System.out.println("retrieved unit code is " + user.getUnitCode());
        finRequest.setBranchCode(user.getUnitCode());
        finRequest.setUsername(username);
        finRequest.setPassword(password);
        finRequest.setTransactionCode(transactionCode);

        return sendCasaRequest(finRequest, payment.getTradeServiceId(),accountName,supervisorId);
    }


    //TODO: this is a dupe method. for now this shall be as is, until i finalize the domain model for the CASA transactions
    private String sendReversalRequest(ReversalRequest request, TradeServiceId tradeServiceId, String accountName) throws CasaServiceException {
        System.out.println("sending casa request...");
        try {
            CasaResponse response = casaService.sendCasaRequest(request);
//            casaTransactionLogRepository.save(mapToCasaLog(request,request.getAmount(), tradeServiceId, response,accountName,null));
            if(!response.isSuccessful()){
                System.out.println("response is not successful...");
                System.out.println("Received an error message of: " + response.getErrorMessage() + " \nCode: " + response.getResponseCode());
                System.out.println("throwing CasaServiceException...");
                throw new CasaServiceException("Received an error message of: " + response.getErrorMessage(),response.getErrorMessage(),response.getResponseCode());
            } else {
                casaTransactionLogRepository.save(mapToCasaLog(request,request.getAmount(), tradeServiceId, response,accountName,null));
            }
            return response.getWorkTaskId();
        } catch (ServiceException e) {
            System.out.println("ServiceException caught...");
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(e);
        } catch (InvalidAccountNumberFormatException e){
            System.out.println("InvalidAccountNumberFormatException caught...");
            String exceptionMessage = "Account number '" + request.getAccountNumber() + "' format is invalid";
            System.out.println(exceptionMessage);
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(exceptionMessage,exceptionMessage,"");
        } catch (MessageTimeoutException e){
            System.out.println("MessageTimeoutException caught...");
            String message = "Failed to receive a response from SIBS";
            System.out.println(message);
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(message,message,"");
        }
    }

    private String sendReversalRequest(ReversalRequest request, TradeServiceId tradeServiceId, String accountName, String supervisorId) throws CasaServiceException {
        System.out.println("sending casa request with supervisorId...");
        try {
            CasaResponse response = casaService.sendCasaRequest(request);
//            casaTransactionLogRepository.save(mapToCasaLog(request,request.getAmount(), tradeServiceId, response,accountName,supervisorId));
            if(!response.isSuccessful()){
                System.out.println("response is not successful...");
                System.out.println("Received an error message of: " + response.getErrorMessage() + " \nCode: " + response.getResponseCode());
                System.out.println("throwing CasaServiceException...");
                throw new CasaServiceException("Received an error message of: " + response.getErrorMessage(),response.getErrorMessage(),response.getResponseCode());
            } else {
                casaTransactionLogRepository.save(mapToCasaLog(request,request.getAmount(), tradeServiceId, response,accountName,supervisorId));
            }
            return response.getWorkTaskId();
        } catch (ServiceException e) {
            System.out.println("ServiceException caught...");
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(e.getMessage(),"Service Exception","CASA");
        } catch (InvalidAccountNumberFormatException e){
            System.out.println("InvalidAccountNumberFormatException caught...");
            String exceptionMessage = "Account number '" + request.getAccountNumber() + "' format is invalid";
            System.out.println(exceptionMessage);
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(exceptionMessage,"Account Number Exception","CASA");
        } catch (MessageTimeoutException e){
            System.out.println("MessageTimeoutException caught...");
            String message = "Failed to receive a response from SIBS";
            System.out.println(message);
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(message,"Timeout Exception","CASA");
        }catch(CasaServiceException e){
        	e.printStackTrace();
        	throw new CasaServiceException(e.getErrorCode() + " : " + e.getCasaErrorMessage(),"MANUALLY THROWN CASA ERROR","CASA");
        }catch(Exception e){
        	//throw casa exception for non casa exception to classify 803 error
        	e.printStackTrace();
        	throw new CasaServiceException(e.getMessage(),"NOT CASA EXCEPTION","NOT CASA");
        }
    }

    private String sendCasaRequest(FinRequest request,TradeServiceId tradeServiceId,String accountName,String supervisorId) throws CasaServiceException {
        System.out.println("sending casa request...");
        try {
            CasaResponse response = casaService.sendCasaRequest(request);
//            casaTransactionLogRepository.save(mapToCasaLog(request, request.getAmount(), tradeServiceId, response, accountName, supervisorId));
            System.out.println("response is: " + response.toString());
            if(!response.isSuccessful()){
                System.out.println("response is not successful...");
                System.out.println("Received an error message of: " + response.getErrorMessage() + " \nCode: " + response.getResponseCode());
                System.out.println("throwing CasaServiceException...");
                if(response.hasTimedOut()){
                	throw new CasaServiceException("SIBS did not receive a reply from host. Transaction will be assumed successful.",response.getErrorMessage(),"DEADMA");
                }else{
                	throw new CasaServiceException("Received an error message of: " + response.getErrorMessage(),response.getErrorMessage(),response.getResponseCode());
                }
            }  else {
                casaTransactionLogRepository.save(mapToCasaLog(request, request.getAmount(), tradeServiceId, response, accountName, supervisorId));
            }
            return response.getWorkTaskId();
        } catch (ServiceException e) {
            System.out.println("ServiceException caught...");
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(e);
        } catch (InvalidAccountNumberFormatException e){
            System.out.println("InvalidAccountNumberFormatException caught...");
            String exceptionMessage = "Account number '" + request.getAccountNumber() + "' format is invalid";
            System.out.println(exceptionMessage);
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(exceptionMessage,exceptionMessage,"");
        } catch (MessageTimeoutException e){
            System.out.println("MessageTimeoutException caught...");
            String message = "TFS did not receive a reply from SIBS. Transaction will be assumed successful.";
            System.out.println(message);
            System.out.println("throwing CasaServiceException...");
            throw new CasaServiceException(message,e.getMessage(),"DEADMA");
        } catch(CasaServiceException e){
        	throw new CasaServiceException(e.getMessage(),e.getCasaErrorMessage(),e.getErrorCode());
        } catch(Exception e){
        	e.printStackTrace();
        	System.out.println("Throwing Exception. Assume successful if the exception is not caught.");
        	String message = "DB Exception. To prevent double entry, transaction will be assumed successful.";
        	throw new CasaServiceException(message,message,"DEADMA");
        }
    }

    private CasaTransactionLog mapToCasaLog(CasaRequest casaRequest,BigDecimal amount, TradeServiceId tradeServiceId,CasaResponse response,String accountName,String supervisorId) throws InvalidAccountNumberFormatException {
    	CasaTransactionLog log = new CasaTransactionLog();
        CasaAccount account = CasaAccountFactory.getInstance(casaRequest.getAccountNumber());
        log.setCurrency(account.getCurrency().getName());
        log.setTellerId(casaRequest.getUserId());
        log.setTransactionAmount(amount);
        log.setTransactionTime(new Date());
        log.setTradeServiceId(tradeServiceId);
        log.setAccountNumber(casaRequest.getAccountNumber());
        log.setTransactionType(casaRequest.getTransactionCode().getType());
        log.setHostStatus(response.getResponseCode());
        log.setWorkTaskId(response.getWorkTaskId());
        log.setAccountName(accountName);
        log.setSupId(supervisorId);
        System.out.println("#############################");
        System.out.println("# MAPPING TO CASA LOG START #");
        System.out.println("#############################");
        System.out.println("currency : " + account.getCurrency().getName());
        System.out.println("tellerId : " + casaRequest.getUserId());
        System.out.println("tradeServiceId : " + tradeServiceId);
        System.out.println("accountNumber : " + casaRequest.getAccountNumber());
        System.out.println("transactionType : " + casaRequest.getTransactionCode().getType());
        System.out.println("hostStatus : " + response.getResponseCode());
        System.out.println("workTaskId : " + response.getWorkTaskId());
        System.out.println("accountName : " + accountName);
        System.out.println("supId : " + supervisorId);
        System.out.println("###########################");
        System.out.println("# MAPPING TO CASA LOG END #");
        System.out.println("###########################");
        return log;
    }

    public void setCasaTransactionLogRepository(CasaTransactionLogRepository casaTransactionLogRepository) {
        this.casaTransactionLogRepository = casaTransactionLogRepository;
    }

    public void setRefBranchRepository(RefBranchRepository refBranchRepository) {
        this.refBranchRepository = refBranchRepository;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    // send to mob boc
    public String creditAccountMobBoc(BigDecimal amount, Employee user, String supervisorId, String casaAccountNumber, TradeService tradeService, String accountName) throws CasaServiceException {
        System.out.println("credit account in process mob-boc...");
        TransactionCode transactionCode = null;
        CasaAccount account = CasaAccountFactory.getInstance(casaAccountNumber);

        if(!account.isForeign()){
            if(account.isCurrent()){
                System.out.println("credit to current...");
                transactionCode = TransactionCode.CREDIT_TO_CURRENT;
            }else{
                System.out.println("credit to savings...");
                transactionCode = TransactionCode.CREDIT_TO_SAVINGS;
            }
        }else{
            System.out.println("credit to foreign...");
            transactionCode = TransactionCode.CREDIT_TO_FOREIGN;
        }

        return sendFinancialRequestMobBoc(amount, user, transactionCode, supervisorId, casaAccountNumber, tradeService, accountName);
    }
    
    private String sendFinancialRequestMobBoc(BigDecimal amount, Employee user, TransactionCode transactionCode, String supervisorId, String casaAccountNumber, TradeService tradeService, String accountName) throws CasaServiceException {
        System.out.println("sending financial request mob-boc...");
        FinRequest finRequest = new FinRequest();
        finRequest.setAccountNumber(casaAccountNumber);
        // MARV: added currency code for message string
        finRequest.setCurrency(Currency.getInstance("PHP"));

        finRequest.setAmount(amount);
//        finRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        finRequest.setUserId(user.getTruncatedTellerId());
        finRequest.setBranchCode(user.getUnitCode());
        finRequest.setUsername(username);
        finRequest.setPassword(password);
        finRequest.setTransactionCode(transactionCode);

        return sendCasaRequest(finRequest, tradeService.getTradeServiceId(), accountName, supervisorId);
    }

    // remittance
    public String debitAccount(BigDecimal amount, Employee user, String supervisorId, String casaAccountNumber, TradeService tradeService, String accountName) throws CasaServiceException {
        System.out.println("debit account in remittance...");
        TransactionCode transactionCode = null;
        CasaAccount account = CasaAccountFactory.getInstance(casaAccountNumber);

        if(!account.isForeign()){
            if(account.isCurrent()){
                System.out.println("credit to current...");
                transactionCode = TransactionCode.DEBIT_TO_CURRENT;
            }else{
                System.out.println("credit to savings...");
                transactionCode = TransactionCode.DEBIT_TO_SAVINGS;
            }
        }else{
            System.out.println("credit to foreign...");
            transactionCode = TransactionCode.DEBIT_TO_FOREIGN;
        }

        return sendFinancialRequestMobBoc(amount, user, transactionCode, supervisorId, casaAccountNumber, tradeService, accountName);
    }

    public String creditAccount(BigDecimal amount, Currency currency, Employee user, String supervisorId, String casaAccountNumber, TradeService tradeService, String accountName) throws CasaServiceException {
        System.out.println("credit account ");
        TransactionCode transactionCode = null;
        CasaAccount account = CasaAccountFactory.getInstance(casaAccountNumber);

        if(!account.isForeign()){
            if(account.isCurrent()){
                System.out.println("credit to current...");
                transactionCode = TransactionCode.CREDIT_TO_CURRENT;
            }else{
                System.out.println("credit to savings...");
                transactionCode = TransactionCode.CREDIT_TO_SAVINGS;
            }
        }else{
            System.out.println("credit to foreign...");
            transactionCode = TransactionCode.CREDIT_TO_FOREIGN;
        }

        return sendFinancialRequest(amount, currency, user, transactionCode, supervisorId, casaAccountNumber, tradeService, accountName);
    }

    private String sendFinancialRequest(BigDecimal amount, Currency currency, Employee user, TransactionCode transactionCode, String supervisorId, String accountNumber, TradeService tradeservice, String accountName) throws CasaServiceException {
        System.out.println("sending financial request...");
        FinRequest finRequest = new FinRequest();
        finRequest.setAccountNumber(accountNumber);
        // MARV: added currency code for message string
        finRequest.setCurrency(currency);

        finRequest.setAmount(amount);
//        finRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        finRequest.setUserId(user.getTruncatedTellerId());
        System.out.println("retrieved unit code is " + user.getUnitCode());
        finRequest.setBranchCode(user.getUnitCode());
        finRequest.setUsername(username);
        finRequest.setPassword(password);
        finRequest.setTransactionCode(transactionCode);

        return sendCasaRequest(finRequest, tradeservice.getTradeServiceId(),accountName,supervisorId);
    }

    // refund

    public String reverseCreditTransaction(Employee user,
                                           String supervisorId,
                                           String casaAccountNumber,
                                           Currency currency,
                                           BigDecimal amount,
                                           TradeServiceId tradeServiceId,
                                           String accountName,
                                           String pnNumber) throws CasaServiceException {
        System.out.println("reversing credit transaction...");
        TransactionCode transactionCode = null;

        CasaAccount account = CasaAccountFactory.getInstance(casaAccountNumber);
        if(!account.isForeign()){
            System.out.println("credit error correct current...");
            transactionCode = TransactionCode.CREDIT_ERROR_CORRECT_CURRENT;
        }else{
            System.out.println("credit error correct foreign...");
            transactionCode = TransactionCode.CREDIT_ERROR_CORRECT_FOREIGN;
        }

        ReversalRequest reversalRequest = new ReversalRequest();
        reversalRequest.setAccountNumber(account.getAccountNumber());

        reversalRequest.setCurrency(currency);

        reversalRequest.setAmount(amount);
//        reversalRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        reversalRequest.setUserId(user.getTruncatedTellerId());
        reversalRequest.setBranchCode(user.getUnitCode());
        reversalRequest.setUsername(username);
        reversalRequest.setPassword(password);
        reversalRequest.setTransactionCode(transactionCode);
        reversalRequest.setWorkTaskId(pnNumber);

        return sendReversalRequest(reversalRequest, tradeServiceId, accountName, supervisorId);
    }

    public String reverseDebitTransaction(Employee user,
                                           String supervisorId,
                                           String casaAccountNumber,
                                           Currency currency,
                                           BigDecimal amount,
                                           TradeServiceId tradeServiceId,
                                           String accountName,
                                           String pnNumber) throws CasaServiceException {
        System.out.println("reversing debit transaction...");
        TransactionCode transactionCode = null;
        CasaAccount account = CasaAccountFactory.getInstance(casaAccountNumber);
        if(!account.isForeign()){
            System.out.println("debit error correct savings...");
            transactionCode = TransactionCode.DEBIT_ERROR_CORRECT_SAVINGS;
        }else{
            System.out.println("debit error correct foreign...");
            transactionCode = TransactionCode.DEBIT_ERROR_CORRECT_FOREIGN;
        }

        ReversalRequest reversalRequest = new ReversalRequest();
        reversalRequest.setAccountNumber(account.getAccountNumber());
        // MARV: added currency code for message string
        reversalRequest.setCurrency(currency);

        reversalRequest.setAmount(amount);
//        reversalRequest.setUserId(user.getTellerId());
        // Marv (02/10/2014) - truncates full teller id from employee
        reversalRequest.setUserId(user.getTruncatedTellerId());

        reversalRequest.setBranchCode(user.getUnitCode());
        reversalRequest.setUsername(username);
        reversalRequest.setPassword(password);
        reversalRequest.setTransactionCode(transactionCode);
        reversalRequest.setWorkTaskId(pnNumber);

        return sendReversalRequest(reversalRequest, tradeServiceId, accountName, supervisorId);
    }
    
//    private boolean isDuplicateTransaction(Long paymentDetailId,UserId userId,TransactionCode transactionCode){
//    	boolean result = false;
//    	if(lastPaymentDetailId.equals(paymentDetailId) && lastUserId.equals(userId) && lastTransactionCode.equals(transactionCode)){
//    		result = true;
//    	}else{
//    		lastPaymentDetailId = paymentDetailId;
//    		lastUserId = userId;
//    		lastTransactionCode = transactionCode;
//    		result = false;
//    	}
//    	return result;
//    }
//    
//    public static void resetDuplicateFilterVariables(){
//    	lastPaymentDetailId = 0L;
//    	lastUserId = null;
//		lastTransactionCode = null;
//    }
}
