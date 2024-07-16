package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.service.AccountingService;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.application.service.TradeProductService;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository;
import com.ucpb.tfs.domain.cdt.CDTRemittance;
import com.ucpb.tfs.domain.cdt.CDTRemittanceRepository;
import com.ucpb.tfs.domain.cdt.enums.CDTStatus;
import com.ucpb.tfs.domain.cdt.enums.PaymentRequestType;
import com.ucpb.tfs.domain.cdt.event.CDTRefundCreatedEvent;
import com.ucpb.tfs.domain.condition.*;
import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;
import com.ucpb.tfs.domain.condition.enumTypes.InstructionType;
import com.ucpb.tfs.domain.corresCharges.CorresChargeActual;
import com.ucpb.tfs.domain.corresCharges.CorresChargeActualRepository;
import com.ucpb.tfs.domain.corresCharges.CorresChargeAdvanceRepository;
import com.ucpb.tfs.domain.corresCharges.event.CorresChargeActualApprovedEvent;
import com.ucpb.tfs.domain.documents.DocumentCode;
import com.ucpb.tfs.domain.documents.DocumentsEnclosed;
import com.ucpb.tfs.domain.documents.LcRequiredDocument;
import com.ucpb.tfs.domain.documents.RequiredDocument;
import com.ucpb.tfs.domain.documents.enumTypes.RequiredDocumentType;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionCreatedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionCurrencyOrAmountUpdatedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionUpdatedEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.product.*;
import com.ucpb.tfs.domain.product.enums.*;
import com.ucpb.tfs.domain.product.event.*;
import com.ucpb.tfs.domain.product.utils.DocumentNumberGenerator;
import com.ucpb.tfs.domain.product.utils.ICNumberGenerator;
import com.ucpb.tfs.domain.product.utils.NegotiationNumberGenerator;
import com.ucpb.tfs.domain.product.utils.NonLcNumberGenerator;
import com.ucpb.tfs.domain.reference.*;
import com.ucpb.tfs.domain.reimbursing.InstructionToBank;
import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode;
import com.ucpb.tfs.domain.reimbursing.LcInstructionToBank;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.domain.service.utils.TradeServiceReferenceNumberGenerator;
import com.ucpb.tfs.domain.settlementaccount.*;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.domain.task.enumTypes.TaskReferenceType;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;
import com.ucpb.tfs.interfaces.domain.Availment;
import com.ucpb.tfs.interfaces.domain.enums.EarmarkingStatusDescription;
import com.ucpb.tfs.interfaces.services.impl.FacilityServiceImpl;
import com.ucpb.tfs.utils.UtilSetFields;
import com.ucpb.tfs2.application.service.DocumentNumberService;
import com.ucpb.tfs2.infrastructure.rest.CDTRestServices;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: IPCVal
 * Date: 8/15/12
 */
//PROLOGUE:
//* 	(revision)
//SCR/ER Number:20160414-052
//SCR/ER Description: Transaction was approved in TSD, but is found on TSD Makers' screen the next day, with Pending status.
//[Revised by:] Allan Comboy Jr.
//[Date Deployed:] 04/14/2016
//Program [Revision] Details: Add condition to check if task has already been run(TSD only)
//PROJECT: CORE
//MEMBER TYPE  : JAVA
//Project Name: TradeServiceEventListeners.java

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: TradeServiceEventListeners
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: ER# 20160622-077
	SCR/ER Description: EBC Settlement - wrongly reverted to Ets maker instaed of TSD maker
	[Created by:] Jesse James Joson
	[Date Deployed:] 
	Program [Revision] Details: Add handling for EBC settlement
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: TradeServiceEventListeners
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: 
	SCR/ER Description: CollectingBankCode and Address not save in Database, 
	[Created by:] Jonh Henry Alabin
	[Date Deployed:] 
	Program [Revision] Details: Add handling for AMLA(new Event), DW and saving collecting Bank Code and Address to Exportbills table
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: TradeServiceEventListeners.java
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: 
	SCR/ER Description: Redmine Issue #4143 - EBP Nego(ets) to EBP Nego(data entry) to EBP Settlement transaction cannot be prepared. The Prepare button is disabled.
	[Created by:] John Patrick C. Bautista
	[Date Deployed:] 06/16/2017
	Program [Revision] Details: Added switch case scenario for SETTLEMENT to set noPaymentAtAll field to true. 
								This is needed so that prepare button is enabled, because there is no more payment tab in settlement.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: TradeServiceEventListeners
 */
 
 /**
 * (revision)
 *	SCR/ER Number:
 *	SCR/ER Description: Batch - Error in Executing Allocation File (Redmine# 4183)
 *	[Revised by:] Brian Harold A. Aquino
 *	[Date revised:] 04/19/2017 (tfs Rev# 7314)
 *	[Date deployed:] 06/16/2017
 *	Program [Revision] Details: Negotiation Date was set to date today.
 *	Member Type: Java
 *	Project: WEB
 *	Project Name: TradeServiceEventListeners.java
 */
/**  PROLOGUE:
 * 	(revision)
	Reference Number: ITDJCH-2018-03-001
	Task Description: Add new fields on screen of different modules to comply with the requirements of ITRS.
	[Created by:] Jaivee Hipolito
	[Date Revised:] 03/06/2018
	Program [Revision] Details: Add method(updateItrsFields) that update ITRS fields and updateItrsParticulars method to save particulars for Export Bills.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: TradeServiceEventListeners
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: 
	SCR/ER Description: 
	[Created by:] John Patrick C. Bautista
	[Date Revised:] 07/27/2017
	[Date Deployed:]
	Program [Revision] Details: Set values for new fields (including computations).
	PROJECT: tfs-core
	MEMBER TYPE  : Java
	Project Name: TradeServiceEventListeners
 */

/**
* (revision)
*	SCR/ER Number:
*	SCR/ER Description: Redmine# 6590
*	[Revised by:] Cedrick C. Nungay
*	[Date revised:] 09/28/2017 (tfs Rev# 7398)
*	[Date deployed:]
*	Program [Revision] Details: On approval of negotiation, some fields for
*								letter of credit does not update if the record
*								has IC Number.
*	Member Type: Java
*	Project: WEB
*	Project Name: TradeServiceEventListeners.java
*/

/**
 * (revision)
 *	SCR/ER Number:
 *	SCR/ER Description:
 *	[Revised by:] Cedrick C. Nungay
 *	[Date revised:] 08/28/18
 *	[Date deployed:]
 *	Program [Revision] Details: Modified amendment of required documents for MT707.
 *	Member Type: Java
 *	Project: WEB
 *	Project Name: TradeServiceEventListeners.java
 */

@Component
public class TradeServiceEventListeners {

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

    @Autowired
    LCNegotiationRepository lcNegotiationRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    SettlementAccountRepository settlementAccountRepository;

    @Autowired
    MarginalDepositRepository marginalDepositRepository;

    @Autowired
    AccountsPayableRepository accountsPayableRepository;

    @Autowired
    AccountsReceivableRepository accountsReceivableRepository;

    @Autowired
    ExportBillsRepository exportBillsRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Autowired
    ChargesService chargesService;

    @Autowired
    AccountingService accountingService;

    // lc additional conditions
    @Autowired
    LcAdditionalConditionRepository lcAdditionalConditionRepository;

    @Autowired
    private FacilityServiceImpl facilityService;

    @Autowired
    CorresChargeAdvanceRepository corresChargeAdvanceRepository;

    @Autowired
    CorresChargeActualRepository corresChargeActualRepository;

    @Autowired
    DocumentNumberGenerator documentNumberGenerator;

    @Autowired
    NonLcNumberGenerator nonLcNumberGenerator;

    @Autowired
    TradeServiceReferenceNumberGenerator tradeServiceReferenceNumberGenerator;

    @Autowired
    DocumentNumberService documentNumberService;    // this unifies all doc number generators post-LC products

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Autowired
    private NonLcRepository nonLcRepository;

    @Autowired
    RebateRepository rebateRepository;

    @Autowired
    ProductReferenceRepository productReferenceRepository;

    @Autowired
    ProductServiceReferenceRepository productServiceReferenceRepository;

    @Autowired
    CDTRemittanceRepository cdtRemittanceRepository;

    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository;
    
    @Autowired
    BookingSettlementRepository bookingSettlementRepository;
    
    @Autowired		
    CDTRestServices cdtRestServices;
    
    @EventListener
    public void updateCharges(ServiceInstructionCurrencyOrAmountUpdatedEvent serviceInstructionCurrencyOrAmountUpdatedEvent) {

        System.out.println("\n INSIDE updateCharges(serviceInstructionCurrencyOrAmountUpdatedEvent)\n");

        try {

            ServiceInstruction ets = serviceInstructionCurrencyOrAmountUpdatedEvent.getServiceInstruction();
            UserActiveDirectoryId userActiveDirectoryId = serviceInstructionCurrencyOrAmountUpdatedEvent.getUserActiveDirectoryId();

            TradeService tradeService = tradeServiceRepository.load(ets.getServiceInstructionId());

            // a trade service item's lc currency or amount has been modified, we clear the previous charges
            tradeService.removeServiceCharges();

            //remove saved fee values in map
            ets.clearChargesSavedInDetails(serviceInstructionCurrencyOrAmountUpdatedEvent.getUserActiveDirectoryId());

            // a trade service item's lc currency or amount has been modified, we call the service to add charges to it
            chargesService.applyCharges(tradeService, ets);
            tradeService.getDetails().put("chargesOverridenFlag", "N");
            tradeServiceRepository.saveOrUpdate(tradeService);
            System.out.println("Persisted TradeService!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("updateCharges(serviceInstructionCurrencyOrAmountUpdatedEvent)",e);
        }

    }

    @EventListener
    public void saveTask(TradeServiceSavedEvent tradeServiceSavedEvent) {

        System.out.println("\n INSIDE saveTask(TradeServiceSavedEvent)\n");

        TradeServiceId tradeServiceId = tradeServiceSavedEvent.getTradeServiceId();
        UserActiveDirectoryId userActiveDirectoryId = tradeServiceSavedEvent.getUserActiveDirectoryId();
        TradeServiceStatus tradeServiceStatus = tradeServiceSavedEvent.getTradeServiceStatus();


        TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeServiceId.toString());
        TaskStatus taskStatus = null;

        if (tradeServiceStatus != null) {
            taskStatus = TaskStatus.valueOf(tradeServiceStatus.toString());
        }

        System.out.println();
        System.out.println("TradeServiceEventListeners.saveTask(): BEFORE PERSIST");
        System.out.println();

        Task task = new Task(taskReferenceNumber, TaskReferenceType.DATA_ENTRY, taskStatus, taskStatus.equals(TaskStatus.DRAFT) ? new UserActiveDirectoryId("TSD") : userActiveDirectoryId);
        taskRepository.persist(task);

        System.out.println();
        System.out.println("TradeServiceEventListeners.saveTask(): AFTER PERSIST");
        System.out.println();
    }

    @EventListener
    public void updateTask(ServiceInstructionUpdatedEvent serviceInstructionUpdatedEvent) {

        System.out.println("\n INSIDE updateTask(ServiceInstructionUpdatedEvent)\n");

        ServiceInstructionId serviceInstructionId = serviceInstructionUpdatedEvent.getServiceInstruction().getServiceInstructionId();
        ServiceInstructionStatus serviceInstructionStatus = serviceInstructionUpdatedEvent.getServiceInstructionStatus();

        TradeServiceId tradeServiceId = ((TradeService) tradeServiceRepository.load(serviceInstructionId)).getTradeServiceId();

        TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeServiceId.toString());

        Task savedTask = taskRepository.load(taskReferenceNumber);

        // if a reference already exists, update the task
        if (serviceInstructionStatus != null && savedTask != null) {

            if (serviceInstructionStatus.equals(ServiceInstructionStatus.DRAFT) && !savedTask.getTaskStatus().equals(TaskStatus.PENDING)) {
                if (savedTask.getUserActiveDirectoryId().toString().equals("TSD")) {
                    savedTask.updateStatus(TaskStatus.DRAFT, savedTask.getUserActiveDirectoryId());
                    taskRepository.merge(savedTask);
                }
            }
        }
    }

    @EventListener
    public void updateTask(TradeServiceRoutedEvent tradeServiceRoutedEvent) {
    	//to catch exception to feed to WEB
    	try{
    				System.out.println("**********in update task handler ********");
    				//to identify if task has been run.
  		      		if(!tradeServiceRoutedEvent.getTask()){	
  		      		System.out.println("Task run");     		  		      		
  		      	    TradeService tradeService = tradeServiceRoutedEvent.getTradeService();
  		            TradeServiceStatus tradeServiceStatus = tradeServiceRoutedEvent.getTradeServiceStatus();
  		            UserActiveDirectoryId targetUser = tradeServiceRoutedEvent.getRoutedToUser();


  		            // use the SI id as the task reference number
  		            TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeService.getTradeServiceId().toString());
  		            //used to check if task is already updated by the 
  		            Task tmpTask = taskRepository.load(taskReferenceNumber);
  		           
  		    //lalala
  		            // set the value of task status field
  		            TaskStatus taskStatus = null;
  		            if (tradeServiceStatus != null) {
  		                taskStatus = TaskStatus.valueOf(tradeServiceStatus.toString());
  		            
  		            }
  		            
  		            Task savedTask = taskRepository.load(taskReferenceNumber);
  		            System.out.println("-----------------------------------------------------");
  		            System.out.println("TO BE STATUS: " + taskStatus + "Reference : " + taskReferenceNumber.toString());
  		            System.out.println("Tradeservice status: " + tradeServiceStatus + "Reference : " + taskReferenceNumber.toString());		           
  		            System.out.println("-----------------------------------------------------");
  		          
//  		            if(!tradeServiceStatus.toString().equalsIgnoreCase(savedTask.getTaskStatus().toString())){
  		            // if a reference already exists, update the task
  		            if (savedTask != null) {
  		                if (taskStatus != null) {
  		                	System.out.println("taskStatus");
  		                    savedTask.updateStatus(taskStatus, targetUser);                                              
  		                }
  		       
  		               taskRepository.merge(savedTask);
  		               System.out.println("duhhh");

  		            } else {
  		            	System.out.println("else");
  		                // otherwise, create it
  		                Task task = new Task(taskReferenceNumber, TaskReferenceType.ETS, taskStatus, targetUser);
  		                taskRepository.persist(task);


  		            }
  		            
  		          Task finalsavedTask = taskRepository.load(taskReferenceNumber);
  		            if(finalsavedTask.getTaskStatus().toString().equalsIgnoreCase(tradeServiceStatus.toString())){
  		            	System.out.println("Success Task");
	            }else{
	            	System.out.println("Failed Task");
	            }
  		      		 		      		
  		      		}else{
  		      		tradeServiceRoutedEvent.setTask(false);   
  		      		System.out.println("Task Already submitted");
  		      		    
  		      		}
    
    	}catch(Exception e){
    	
    e.printStackTrace();
    System.out.println("Task Failed");
    throw new IndexOutOfBoundsException("Transaction Failed on TASK. Please contact TFS support!");
    	}
    
  		      	
    
  		            
    }
    
    
  

    @EventListener
    public void updateServiceCharges(TradeServiceUpdatedEvent tradeServiceUpdatedEvent) {
        System.out.println(">>>>>> public void updateServiceCharges(TradeServiceUpdatedEvent tradeServiceUpdatedEvent)");
        System.out.println(">>>>>> THIS ACTUALLY DOES NOTHING)");
//        TradeServiceId tradeServiceId = tradeServiceUpdatedEvent.getTradeServiceId();
//        UserActiveDirectoryId userActiveDirectoryId = tradeServiceUpdatedEvent.getUserActiveDirectoryId();
//        TradeServiceStatus tradeServiceStatus = tradeServiceUpdatedEvent.getTradeServiceStatus();
//        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
//        Map<String, Object> details = tradeService.getDetails();
//        Payment paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
//
//        if(paymentProduct != null){
//            BigDecimal totalAmountPaidInPHP = paymentProduct.getTotalPrePayment(Currency.getInstance("PHP"));
//            System.out.println("totalAmountPaidInPHP :" + totalAmountPaidInPHP);
//            BigDecimal pesoAmountPaid = paymentProduct.getTotalPrePaymentWithCurrency(Currency.getInstance("PHP"));
//            System.out.println("pesoAmountPaid :" + pesoAmountPaid);
//            BigDecimal productChargeAmountNetOfPesoAmountPaid = BigDecimal.ZERO;
//            if (totalAmountPaidInPHP != null && pesoAmountPaid != null) {
//                productChargeAmountNetOfPesoAmountPaid = totalAmountPaidInPHP.subtract(pesoAmountPaid);
//                System.out.println("productChargeAmountNetOfPesoAmountPaid:" + productChargeAmountNetOfPesoAmountPaid);
//                details.put("productChargeAmountNetOfPesoAmountPaid", productChargeAmountNetOfPesoAmountPaid);
//            } else {
//                details.put("productChargeAmountNetOfPesoAmountPaid", BigDecimal.ZERO);
//            }
//        }
//
//
//        tradeService.updateDetails(details, userActiveDirectoryId);
//
//
//        // a trade service item was modified, we call the service to delete old charges
//        tradeServiceRepository.deleteServiceCharges(tradeService.getTradeServiceId());
//
//        // we add charges to it
//        chargesService.applyCharges(tradeService, tradeService.getDetails());
//
//        tradeServiceRepository.merge(tradeService);
//
//        TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeServiceId.toString());
//        TaskStatus taskStatus = null;
//
//        if (tradeServiceStatus != null) {
//            taskStatus = TaskStatus.valueOf(tradeServiceStatus.toString());
//        }
//
//        System.out.println();
//        System.out.println("TradeServiceEventListeners.updateTask(): BEFORE PERSIST");
//        System.out.println();
//
//        Task savedTask = taskRepository.load(taskReferenceNumber);
//
//        if (savedTask != null ) {
//
//            if (taskStatus != null) {
//                savedTask.updateStatus(taskStatus, userActiveDirectoryId);
//            }
//
//            taskRepository.merge(savedTask);
//        }
//
//        System.out.println();
//        System.out.println("TradeServiceEventListeners.updateTask(): AFTER PERSIST");
//        System.out.println();
    }

    @EventListener
    public void createTradeService(ServiceInstructionCreatedEvent serviceInstructionCreatedEvent) {

        System.out.println("\n INSIDE createTradeService(ServiceInstructionCreatedEvent)\n");

        try {

            ServiceInstruction ets = serviceInstructionCreatedEvent.getServiceInstruction();
            UserActiveDirectoryId userActiveDirectoryId = serviceInstructionCreatedEvent.getUserActiveDirectoryId();

            Map<String, Object> parameterMap = ets.getDetails();
            ServiceType serviceType = ServiceType.valueOf(((String) parameterMap.get("serviceType")).toUpperCase());
            DocumentClass documentClass = DocumentClass.valueOf(((String) parameterMap.get("documentClass")).toUpperCase());
            DocumentType documentType = null;
            DocumentSubType1 documentSubType1 = null;
            DocumentSubType2 documentSubType2 = null;

            if (parameterMap.get("documentType") != null && !parameterMap.get("documentType").toString().equals("")) {
                documentType = DocumentType.valueOf(((String) parameterMap.get("documentType")).toUpperCase());
            }
            if (parameterMap.get("documentSubType1") != null && !parameterMap.get("documentSubType1").toString().equals("")) {
                documentSubType1 = DocumentSubType1.valueOf(((String) parameterMap.get("documentSubType1")).toUpperCase());
            }
            if (parameterMap.get("documentSubType2") != null && !parameterMap.get("documentSubType2").toString().equals("")) {
                documentSubType2 = DocumentSubType2.valueOf(((String) parameterMap.get("documentSubType2")).toUpperCase());
            }
            String branchUnitCode = (String) parameterMap.get("unitcode");
            String processingUnitCode = (String) parameterMap.get("processingUnitCode");

            DocumentNumber documentNumber = null;
            String negotiationNumber = "";

            TradeProductNumber tradeProductNumber = null;

            Boolean noPaymentAtAll = Boolean.FALSE;

            // list of required documents
            List<RequiredDocument> requiredDocumentList = new ArrayList<RequiredDocument>();

            // list of instructions to bank
            List<InstructionToBank> instructionToBankList = new ArrayList<InstructionToBank>();

            // list of additional conditions
            List<AdditionalCondition> additionalConditionList = new ArrayList<AdditionalCondition>();

            // By default, DocumentNumber is passed as parameter
            if (parameterMap.get("documentNumber") != null) {
                documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));

                // By default, set TradeProductNumber equal to DocumentNumber
                tradeProductNumber = new TradeProductNumber(documentNumber.toString());
            }

            if (documentClass.equals(DocumentClass.LC)) {

                if (serviceType.equals(ServiceType.OPENING)) {

                    // If LC Opening, generate DocumentNumber
                    //TODO: get from REFPRODUCT
                    String documentCode = DocumentCodeEnum.toString(documentClass, null, documentSubType1);
                    ProductReference productReference =  productReferenceRepository.find(documentClass, documentType, documentSubType1, documentSubType2);
                    if(productReference!=null){
                        documentCode = productReference.getDocumentCode();
                    }

                    String documentNumberStr = documentNumberGenerator.generateDocumentNumber(branchUnitCode, documentCode, processingUnitCode);

                    documentNumber = new DocumentNumber(documentNumberStr);
                    tradeProductNumber = new TradeProductNumber(documentNumberStr);

                } else {
                    switch (serviceType) {

                        case NEGOTIATION:


                            //TODO

                            ProductReference productReference =  productReferenceRepository.find(documentClass, documentType, documentSubType1, documentSubType2);
                            System.out.println("productReference.getProductId():"+productReference.getProductId());
                            System.out.println("serviceType:"+serviceType);
                            ProductServiceReference productServiceReference = productServiceReferenceRepository.getProductService(productReference.getProductId(),serviceType);
                            String docCode = productServiceReference.getDocumentCode();

                            String sequenceNumber = tradeProductRepository.getNegotiationNumberSequence(docCode, processingUnitCode, Calendar.getInstance().get(Calendar.YEAR));
                            tradeProductRepository.incrementNegotiationNumberSequence(docCode,processingUnitCode,Calendar.getInstance().get(Calendar.YEAR));

                            // In Negotiation, TradeService DocumentNumber = NegotiationNumber
                            // Generate NegotiationNumber in advance
                            negotiationNumber = NegotiationNumberGenerator.generateNegotiationNumber(branchUnitCode, processingUnitCode,docCode, sequenceNumber);


                            // Set lcNumber with documentNumber
                            // Set tradeProductNumber with documentNumber
                            parameterMap.put("lcNumber", documentNumber.toString());
                            parameterMap.put("tradeProductNumber", documentNumber.toString());

                            // After setting, set the documentNumber as the negotiationNumber
                            documentNumber = new DocumentNumber(negotiationNumber);

                            parameterMap.put("documentNumber", negotiationNumber);
                            parameterMap.put("negotiationNumber", negotiationNumber);

                            ets.updateDetails(parameterMap, userActiveDirectoryId);

                            if (documentType.equals(DocumentType.DOMESTIC) &&
                                    documentSubType1.equals(DocumentSubType1.REGULAR) &&
                                    documentSubType2.equals(DocumentSubType2.USANCE)) {
                                noPaymentAtAll = true;
                            }

                            break;

                        case ADJUSTMENT:

                            if (documentSubType1.equals(DocumentSubType1.REGULAR)) {

                                String partialCashSettlementFlag = (String) parameterMap.get("partialCashSettlementFlag");

                                System.out.println("partialCashSettlementFlag = " + partialCashSettlementFlag);

                                if (partialCashSettlementFlag == null || partialCashSettlementFlag.equals("")) {
                                    noPaymentAtAll = Boolean.TRUE;
                                }

                                if (partialCashSettlementFlag != null && !(partialCashSettlementFlag.equals(""))) {
                                    noPaymentAtAll = Boolean.FALSE;
                                }

                            } else {
                                noPaymentAtAll = Boolean.TRUE;
                            }

                            break;

                        // add required documents, additional conditions, and instructions to bank if amendment
                        case AMENDMENT:
                            DocumentNumber docNum = new DocumentNumber((String) parameterMap.get("documentNumber"));
                            LetterOfCredit lc = (LetterOfCredit) tradeProductRepository.load(docNum);

                            // copies required documents from letter of credit to trade service
                            for (LcRequiredDocument lcRequiredDocument : lc.getRequiredDocuments()) {
                                Map<String, Object> lcRequiredDocumentFields = lcRequiredDocument.getFields();

                                DocumentCode documentCode = null;

                                RequiredDocumentType requiredDocumentType = RequiredDocumentType.valueOf((String) lcRequiredDocumentFields.get("requiredDocumentType"));

                                if (requiredDocumentType.equals(RequiredDocumentType.DEFAULT)) {
                                    documentCode = new DocumentCode((String) lcRequiredDocumentFields.get("documentCode"));
                                }

                                RequiredDocument requiredDocument = new RequiredDocument(documentCode, (String) lcRequiredDocumentFields.get("description"), requiredDocumentType);

                                requiredDocumentList.add(requiredDocument);
                            }

                            // copies instructions to bank from letter of credit to trade service
                            for (LcInstructionToBank lcInstructionToBank : lc.getInstructionsToBank()) {
                                Map<String, Object> lcInstructionToBankFields = lcInstructionToBank.getFields();

                                InstructionToBankCode instructionToBankCode = new InstructionToBankCode((String) lcInstructionToBankFields.get("instructionToBankCode"));

                                InstructionToBank instructionToBank = new InstructionToBank(instructionToBankCode, (String) lcInstructionToBankFields.get("instruction"));

                                instructionToBankList.add(instructionToBank);
                            }

                            // copies additional conditions from letter of credit to trade service to
                            for (LcAdditionalCondition lcAdditionalCondition : lc.getAdditionalCondition()) {
                                Map<String, Object> lcAdditionalConditionFields = lcAdditionalCondition.getFields();

                                ConditionCode conditionCode = null;

                                ConditionType conditionType = ConditionType.valueOf((String) lcAdditionalConditionFields.get("conditionType"));

                                if (conditionType.equals(ConditionType.DEFAULT)) {
                                    conditionCode = new ConditionCode((String) lcAdditionalConditionFields.get("conditionCode"));
                                }

                                AdditionalCondition additionalCondition = new AdditionalCondition(conditionType, conditionCode, (String) lcAdditionalConditionFields.get("condition"));

                                additionalConditionList.add(additionalCondition);
                            }

                            break;
                    }
                }

            } else if (documentClass.equals(DocumentClass.INDEMNITY)) {

                DocumentNumber docNum = null;

                if (parameterMap.get("referenceNumber") != null) {
                    docNum = new DocumentNumber((String) parameterMap.get("referenceNumber"));
                    tradeProductNumber = new TradeProductNumber(docNum.toString());
                }

                // Set lcNumber with documentNumber
                // set tradeProductNumber with documentNumber
                parameterMap.put("lcNumber", docNum.toString());
                parameterMap.put("tradeProductNumber", docNum.toString());

                System.out.println("####################");
                System.out.println("# INSIDE INDEMNITY #");
                System.out.println("####################");

                if (serviceType.equals(ServiceType.ISSUANCE)) {
                    System.out.println("ISSUANCE");

//                    System.out.println("ISSUANCE");

                    String indemnityType = (String) parameterMap.get("indemnityType");

//                    IndemnityNumberGenerator indemnityNumberGenerator = new IndemnityNumberGenerator();
//                    indemnityNumberGenerator.setTradeProductRepository(tradeProductRepository);

                    if (indemnityType.equals("BG")) {
                        System.out.println("BG");
                        // documentNumber = new BGNumber(IndemnityNumberGenerator.generateBGNumber());
//                        String bgNumber = indemnityNumberGenerator.generateBgNumber(branchUnitCode, processingUnitCode);
                        //documentNumber = new BGNumber(bgNumber);
                        documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, DocumentClass.BG, DocumentType.FOREIGN, null, null, ServiceType.ISSUANCE);
                    } else if (indemnityType.equals("BE")) {
                        System.out.println("BE");
                        // documentNumber = new BENumber(IndemnityNumberGenerator.generateBENumber());
//                        String beNumber = indemnityNumberGenerator.generateBeNumber(branchUnitCode, processingUnitCode);
//                        documentNumber = new BENumber(beNumber);
                        documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, DocumentClass.BE, DocumentType.FOREIGN, null, null, ServiceType.ISSUANCE);
                    }

                } else if (serviceType.equals(ServiceType.CANCELLATION)) {
                    System.out.println("CANCELLATION");
                }

                ets.updateDetails(parameterMap, userActiveDirectoryId);

            } else if (documentClass.equals(DocumentClass.MD)) {
//                if(serviceType.equals(ServiceType.COLLECTION)) {
                documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                tradeProductNumber = new TradeProductNumber(documentNumber.toString());
//                }
            } else if (documentClass.equals(DocumentClass.AP)) {

                documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                tradeProductNumber = new TradeProductNumber(documentNumber.toString());

            } else if (documentClass.equals(DocumentClass.AR)) {

                documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                tradeProductNumber = new TradeProductNumber(documentNumber.toString());

            } else if (documentClass.equals(DocumentClass.DA)) {
                System.out.println("Loaded " + serviceType);
                switch (serviceType) {
                    case NEGOTIATION_ACKNOWLEDGEMENT:
                        branchUnitCode = (String) parameterMap.get("unitcode");
                        processingUnitCode = (String) parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, serviceType, null);
//					String documentNumberStr = nonLcNumberGenerator.generateNonLcNumber(branchUnitCode, documentCode, processingUnitCode);

                        // refactored to use the unified approach
                        documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
                        tradeProductNumber = new TradeProductNumber(documentNumber.toString());

//					documentNumber = new DocumentNumber(documentNumberStr);
                        break;
                    // TODO: FOR ARVIN
                    case NEGOTIATION_ACCEPTANCE:
                        System.out.println("Should not Be Triggered!");
                        break;
                    case SETTLEMENT:
                        System.out.println("Settlement Triggered!");
                        break;
                    case CANCELLATION:
                        break;
                }
            } else if (documentClass.equals(DocumentClass.DP)) {
                System.out.println("Loaded " + serviceType);
                switch (serviceType) {
                    case NEGOTIATION:
                        branchUnitCode = (String) parameterMap.get("unitcode");
                        processingUnitCode = (String) parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, serviceType, null);
//					String documentNumberStr = nonLcNumberGenerator.generateNonLcNumber(branchUnitCode, documentCode, processingUnitCode);
//					documentNumber = new DocumentNumber(documentNumberStr);

                        // refactored to use the unified approach
                        documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
                        tradeProductNumber = new TradeProductNumber(documentNumber.toString());

//					branchUnitCode = (String)parameterMap.get("unitcode");
//					processingUnitCode = (String)parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, null, documentSubType1);
//
//					String documentNumberStr = documentNumberGenerator.generateDocumentNumber(branchUnitCode, documentCode, processingUnitCode);
//
//					documentNumber = new DocumentNumber(documentNumberStr);
                        break;
                    // TODO: FOR ARVIN
                    case SETTLEMENT:
                        break;
                    case CANCELLATION:
                        break;
                }
            } else if (documentClass.equals(DocumentClass.OA)) {
                switch (serviceType) {
                    case NEGOTIATION:
                        branchUnitCode = (String) parameterMap.get("unitcode");
                        processingUnitCode = (String) parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, serviceType, null);
//					String documentNumberStr = nonLcNumberGenerator.generateNonLcNumber(branchUnitCode, documentCode, processingUnitCode);
//					documentNumber = new DocumentNumber(documentNumberStr);

                        // refactored to use the unified approach
                        documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
                        tradeProductNumber = new TradeProductNumber(documentNumber.toString());

//					branchUnitCode = (String)parameterMap.get("unitcode");
//					processingUnitCode = (String)parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, null, documentSubType1);
//
//					String documentNumberStr = documentNumberGenerator.generateDocumentNumber(branchUnitCode, documentCode, processingUnitCode);
//
//					documentNumber = new DocumentNumber(documentNumberStr);
                        break;
                    // TODO: FOR ARVIN
                    case SETTLEMENT:
                        break;
                    case CANCELLATION:
                        break;
                }
            } else if (documentClass.equals(DocumentClass.DR)) {
                switch (serviceType) {
                    case NEGOTIATION:
                        branchUnitCode = (String) parameterMap.get("unitcode");
                        processingUnitCode = (String) parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, serviceType, null);
//					String documentNumberStr = nonLcNumberGenerator.generateNonLcNumber(branchUnitCode, documentCode, processingUnitCode);

                        // refactored to use the unified approach
                        documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
                        tradeProductNumber = new TradeProductNumber(documentNumber.toString());

//					documentNumber = new DocumentNumber(documentNumberStr);
//					branchUnitCode = (String)parameterMap.get("unitcode");
//					processingUnitCode = (String)parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, null, documentSubType1);
//
//					String documentNumberStr = documentNumberGenerator.generateDocumentNumber(branchUnitCode, documentCode, processingUnitCode);
//
//					documentNumber = new DocumentNumber(documentNumberStr);
                        break;
                    // TODO: FOR ARVIN
                    case SETTLEMENT:
                        break;
                    case CANCELLATION:
                        break;
                }

            } else if (documentClass.equals(DocumentClass.IMPORT_ADVANCE)) {

                branchUnitCode = (String) parameterMap.get("unitcode");
                processingUnitCode = (String) parameterMap.get("processingUnitCode");

                if (serviceType.equals(ServiceType.PAYMENT)) {

                    documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, DocumentClass.IMPORT_ADVANCE, null, null, null, ServiceType.PAYMENT);

                } else if (serviceType.equals(ServiceType.REFUND)) {

                    documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));

                    /*
                    Map iadvEtsDetails = ets.getDetails();
                    Object valueDateObj = iadvEtsDetails.get("valueDate");
                    if (valueDateObj != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
                        Date valueDate = sdf.parse(valueDateObj.toString());
                        sdf.applyPattern("MM/dd/yyyy");
                        iadvEtsDetails.put("valueDate", sdf.format(valueDate));
                    }
                    */
                }

                tradeProductNumber = new TradeProductNumber(documentNumber.toString());
				
            } else if (documentClass.equals(DocumentClass.EXPORT_ADVANCE)) {
 
                branchUnitCode = (String) parameterMap.get("unitcode");
                processingUnitCode = (String) parameterMap.get("processingUnitCode");

                if (serviceType.equals(ServiceType.PAYMENT)) {

                    documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, DocumentClass.EXPORT_ADVANCE, null, null, null, ServiceType.PAYMENT);

                } else if (serviceType.equals(ServiceType.REFUND)) {

                    documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));

                    /*
                    Map eadvEtsDetails = ets.getDetails();
                    Object valueDateObj = eadvEtsDetails.get("valueDate");
                    if (valueDateObj != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
                        Date valueDate = sdf.parse(valueDateObj.toString());
                        sdf.applyPattern("MM/dd/yyyy");
                        eadvEtsDetails.put("valueDate", sdf.format(valueDate));
                    }
                    */
                }

                tradeProductNumber = new TradeProductNumber(documentNumber.toString());
				
            } else if (documentClass.equals(DocumentClass.BP)) {

                if (serviceType.equals(ServiceType.NEGOTIATION)) {
                    branchUnitCode = (String) parameterMap.get("unitcode");
                    processingUnitCode = (String) parameterMap.get("processingUnitCode");

                    documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, DocumentClass.BP, documentType, null, null, ServiceType.NEGOTIATION);
                    tradeProductNumber = new TradeProductNumber(documentNumber.toString());

                    System.out.println("EBP NEGO");
                    if (parameterMap.get("exlcAdviseNumber") != null && parameterMap.get("paymentMode").equals("LC")) {
                        ExportAdvising exportAdvising = (ExportAdvising) tradeProductRepository.load(new DocumentNumber((String) parameterMap.get("exlcAdviseNumber")));

                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                        Map details = ets.getDetails();
                        if (details != null && exportAdvising!=null) {
                        	if(exportAdvising.getDocumentNumber() != null){
                        		details.put("adviseNumber", exportAdvising.getDocumentNumber().toString());
                        	}
                        	if(exportAdvising.getLcNumber() != null){
                        		details.put("lcNumber", exportAdvising.getLcNumber().toString());
                        	}
                        	if(exportAdvising.getLcIssueDate() != null){
                        		details.put("lcIssueDate", sdf.format(exportAdvising.getLcIssueDate()));
                        	}
                        	if(exportAdvising.getLcType() != null){
	                            details.put("lcType", exportAdvising.getLcType());
	                            if (exportAdvising.getLcTenor() != null && LCType.REGULAR.equals(exportAdvising.getLcType())) {
	                            	details.put("lcTenor", exportAdvising.getLcTenor().toString());
	                            	if (exportAdvising.getUsanceTerm() != null && LCTenor.USANCE.equals(exportAdvising.getLcTenor())) {
	                            		details.put("usanceTerm", exportAdvising.getUsanceTerm().toString());
	                            	}
	                            }
                        	}
                        	if(exportAdvising.getLcCurrency() != null){
                        		details.put("lcCurrency", exportAdvising.getLcCurrency().toString());
                        	}
                        	if(exportAdvising.getLcAmount() != null){
                        		details.put("lcAmount", exportAdvising.getLcAmount().toString());
                        	}
                        	if(exportAdvising.getLcExpiryDate() != null){
                        		details.put("lcExpiryDate", sdf.format(exportAdvising.getLcExpiryDate()));
                        	}
                        	if(exportAdvising.getLcExpiryDate() != null){
                        		details.put("issuingBankCode", exportAdvising.getIssuingBank());
                        	}
                        	if(exportAdvising.getIssuingBankAddress() != null){
                        		details.put("issuingBankAddress", exportAdvising.getIssuingBankAddress());
                        	}
                        	if(exportAdvising.getReimbursingBank() != null){
                        		details.put("reimbursingBankCode", exportAdvising.getReimbursingBank());
                        	}
                            ets.updateDetails(details, userActiveDirectoryId);
                        }
                    } else {
                        Map details = ets.getDetails();

                        if (details != null) {
                            details.remove("adviseNumber");
                            details.remove("lcNumber");
                            details.remove("lcIssueDate");
                            details.remove("lcType");
                            details.remove("lcTenor");
                            details.remove("usanceTerm");
                            details.remove("lcCurrency");
                            details.remove("lcAmount");
                            details.remove("lcExpiryDate");
                            details.remove("issuingBankCode");
                            details.remove("issuingBankAddress");
                            details.remove("reimbursingBankCode");

                            ets.updateDetails(details, userActiveDirectoryId);
                        }
                    }

                } else if (serviceType.equals(ServiceType.SETTLEMENT)) {
                    documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                }
            } else {
                // TODO: Other Products
            }

            // Create and save TradeService.
            // Since an ETS was saved, this userActiveDirectoryId belongs to a Branch user (the "last touch").
            System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> TRADE PRODUCT NUMBER = " + tradeProductNumber.toString() + "\n");
            TradeService tradeService = TradeServiceService.createTradeService(ets, documentNumber, tradeProductNumber, userActiveDirectoryId);

            tradeService = TradeServiceService.updateProductCharge(tradeService, parameterMap, userActiveDirectoryId);

            // add required documents from lc to trade service
            tradeService.addRequiredDocuments(requiredDocumentList);

            // add additional conditions from lc to trade service
            tradeService.addInstructionToBank(instructionToBankList);

            // add additional conditions from lc to trade service
            tradeService.addAdditionalCondition(additionalConditionList);

            System.out.println("\n)))))))))))))))))))))) noPaymentAtAll = " + noPaymentAtAll + "\n");

            if (noPaymentAtAll) {
                System.out.println("setting no payment required #1");
                tradeService.setAsNoPaymentRequired();
            }

            // a trade service item was , we call the service to add charges to it
            chargesService.applyCharges(tradeService, ets);
            tradeService.getDetails().put("chargesOverridenFlag", "N");

            tradeServiceRepository.persist(tradeService);
            System.out.println("Persisted TradeService!");

            // Fire event
            TradeServiceSavedEvent tradeServiceSavedEvent = new TradeServiceSavedEvent(tradeService.getTradeServiceId(), tradeService.getDetails(), tradeService.getStatus(), userActiveDirectoryId);
            eventPublisher.publish(tradeServiceSavedEvent);
            System.out.println("TRADESERVICEID >>>>> " + tradeService.getTradeServiceId());

            /*// for ap refund
            if (documentClass.equals(DocumentClass.AP) || documentClass.equals(DocumentClass.AR)) {
                try {
                    TradeService savedTradeService = tradeServiceRepository.load(tradeService.getTradeServiceId());
                    System.out.println("SAVED TRADESERVICEID >>> " + savedTradeService.getTradeServiceId());
                    Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                    if (payment == null) {
                        payment = new Payment(savedTradeService.getTradeServiceId(), ChargeType.PRODUCT);
                    }

                    // Create payment
                    List<Map<String, Object>> productPaymentListMap = (List<Map<String, Object>>) parameterMap.get("documentPaymentSummary");

                    Set<PaymentDetail> tempDetails = new HashSet<PaymentDetail>();
                    for (Map<String, Object> productPaymentMap : productPaymentListMap) {
                        PaymentDetail tempDetail = null;

                        String paymentMode = (String) productPaymentMap.get("paymentMode");

                        PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(paymentMode);

                        String referenceNumber = null;

                        BigDecimal amount = new BigDecimal(((String) productPaymentMap.get("amount")).trim());
                        Currency settlementCurrency = Currency.getInstance(((String) productPaymentMap.get("currency")).trim());

                        // Rates
                        BigDecimal passOnRateThirdToUsd = null;
                        BigDecimal passOnRateThirdToPhp = null;
                        BigDecimal passOnRateUsdToPhp = null;
                        BigDecimal specialRateThirdToUsd = null;
                        BigDecimal specialRateThirdToPhp = null;
                        BigDecimal specialRateUsdToPhp = null;
                        BigDecimal urr = null;

                        if (productPaymentMap.get("text_pass_on_rate1") != null && !((String) productPaymentMap.get("text_pass_on_rate1")).equals("")) {
                            passOnRateThirdToUsd = new BigDecimal(((String) productPaymentMap.get("text_pass_on_rate1")).trim());
                            specialRateThirdToUsd = new BigDecimal(((String) productPaymentMap.get("text_pass_on_rate1")).trim());
                        }
                        if (productPaymentMap.get("text_pass_on_rate2") != null && !((String) productPaymentMap.get("text_pass_on_rate2")).equals("")) {
                            passOnRateThirdToPhp = new BigDecimal(((String) productPaymentMap.get("text_pass_on_rate2")).trim());
                            specialRateThirdToPhp = new BigDecimal(((String) productPaymentMap.get("text_pass_on_rate2")).trim());
                        }
                        if (productPaymentMap.get("text_pass_on_rate17") != null && !((String) productPaymentMap.get("text_pass_on_rate17")).equals("")) {
                            passOnRateUsdToPhp = new BigDecimal(((String) productPaymentMap.get("text_pass_on_rate17")).trim());
                            specialRateUsdToPhp = new BigDecimal(((String) productPaymentMap.get("text_pass_on_rate17")).trim());
                        }
                        if (productPaymentMap.get("USD-PHP_urr") != null && !((String) productPaymentMap.get("USD-PHP_urr")).equals("")) {
                            urr = new BigDecimal(((String) productPaymentMap.get("USD-PHP_urr")).trim());
                        }

                        switch (paymentInstrumentType) {

                            case CASA:
                                // For Product charge, only MD and AP are used to pay; AR is not.
                                referenceNumber = (String) productPaymentMap.get("accountNumber");

                                tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);

                                break;

                            case MC_ISSUANCE:
                                referenceNumber = (String) productPaymentMap.get("tradeSuspenseAccount");

                                tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);
                                break;
                        }

                        System.out.println("\n");
                        System.out.println("paymentInstrumentType = " + paymentInstrumentType);
                        System.out.println("referenceNumber = " + referenceNumber);

                        tempDetails.add(tempDetail);
                    }
                    System.out.println("\n");

                    // Persist payment

                    // 1) Remove all items first
                    payment.deleteAllPaymentDetails();
                    // 2) Add new PaymentDetails
                    payment.addNewPaymentDetails(tempDetails);
                    // 3) Use saveOrUpdate
                    paymentRepository.saveOrUpdate(payment);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error in createTradeService(ServiceInstruction)",e);
        }
    }

    @EventListener
    public void createTradeService(TradeServiceSavedEvent tradeServiceSavedEvent) {

        System.out.println("\n INSIDE createTradeService(TradeServiceSavedEvent)\n");

        try {

            Map<String, Object> parameterMap = tradeServiceSavedEvent.getParameterMap();
            UserActiveDirectoryId userActiveDirectoryId = tradeServiceSavedEvent.getUserActiveDirectoryId();

            ServiceType serviceType = ServiceType.valueOf(((String) parameterMap.get("serviceType")).toUpperCase());
            DocumentClass documentClass = DocumentClass.valueOf(((String) parameterMap.get("documentClass")).toUpperCase());

            DocumentType documentType = null;
            DocumentSubType1 documentSubType1 = null;
            DocumentSubType2 documentSubType2 = null;
//            if(((String) parameterMap.get("documentType")) != null && (String)parameterMap.get("documentType") != ""){
//                documentType = DocumentType.valueOf(((String) parameterMap.get("documentType")).toUpperCase());
//            }
//            if(((String) parameterMap.get("documentSubType1")) != null && (String)parameterMap.get("documentSubType1") != ""){
//                documentSubType1 = DocumentSubType1.valueOf(((String) parameterMap.get("documentSubType1")).toUpperCase());
//            }
//            if(((String) parameterMap.get("documentSubType2")) != null && (String)parameterMap.get("documentSubType2") != ""){
//                documentSubType2 = DocumentSubType2.valueOf(((String) parameterMap.get("documentSubType2")).toUpperCase());
//            }
            if (parameterMap.get("documentType") != null && !parameterMap.get("documentType").toString().equals("")) {
                documentType = DocumentType.valueOf(((String) parameterMap.get("documentType")).toUpperCase());
            }
            if (parameterMap.get("documentSubType1") != null && !parameterMap.get("documentSubType1").toString().equals("")) {
                documentSubType1 = DocumentSubType1.valueOf(((String) parameterMap.get("documentSubType1")).toUpperCase());
            }
            if (parameterMap.get("documentSubType2") != null && !parameterMap.get("documentSubType2").toString().equals("")) {
                documentSubType2 = DocumentSubType2.valueOf(((String) parameterMap.get("documentSubType2")).toUpperCase());
            }

            TradeService tradeService = tradeServiceRepository.load(tradeServiceSavedEvent.getTradeServiceId());

            Boolean noPaymentAtAll = Boolean.FALSE;

            if (documentClass.equals(DocumentClass.LC)) {

                switch (serviceType) {

                    case NEGOTIATION_DISCREPANCY:

                        System.out.println("\n)))))))))))))))))))))) NEGOTIATION DISCREPANCY!\n");

                        ICNumberGenerator icNumberGenerator = new ICNumberGenerator();
                        icNumberGenerator.setTradeProductRepository(tradeProductRepository);

                        // Negotiation Discrepancy is TSD-initiated
                        // So there is no Branch Unit Code
                        String processingUnitCode = (String) parameterMap.get("unitcode");
                        String branchUnitCode = (String) parameterMap.get("ccbdBranchUnitCode");

                        ProductReference productReference =  productReferenceRepository.find(documentClass, documentType, documentSubType1, documentSubType2);
                        System.out.println("productReference:"+productReference);
                        ProductServiceReference productServiceReference = productServiceReferenceRepository.getProductService(productReference.getProductId(),serviceType);
                        String docCode = productServiceReference.getDocumentCode();

                        System.out.println("productServiceReference:"+productServiceReference);
                        System.out.println("productServiceReference.getProductId():"+productServiceReference.getProductId());
                        System.out.println("productServiceReference.getServiceType():"+productServiceReference.getServiceType());
                        System.out.println("docCode:"+ docCode);
                        String icNumber = icNumberGenerator.generateIcNumber(docCode, processingUnitCode, branchUnitCode);
                        System.out.println("icNumber:"+icNumber);
                        //OLD
//                        String icNumber = icNumberGenerator.generateIcNumber(DocumentCodeEnum.NEGOTIATION_DISCREPANCY.toString(), processingUnitCode);


                        // Set icNumber to map
                        parameterMap.put("icNumber", icNumber);

                        tradeService.updateDetails(parameterMap, userActiveDirectoryId);

                        noPaymentAtAll = Boolean.TRUE;

                        break;

                    case AMENDMENT:
                    	if (tradeService.getServiceInstructionId() == null) {

	                        // list of required documents
	                        List<RequiredDocument> requiredDocumentList = new ArrayList<RequiredDocument>();
	
	                        // list of instructions to bank
	                        List<InstructionToBank> instructionToBankList = new ArrayList<InstructionToBank>();
	
	                        // list of additional conditions
	                        List<AdditionalCondition> additionalConditionList = new ArrayList<AdditionalCondition>();
	
	                    	DocumentNumber docNum = new DocumentNumber((String) parameterMap.get("documentNumber"));
	                        LetterOfCredit lc = (LetterOfCredit) tradeProductRepository.load(docNum);
	
	                        // copies required documents from letter of credit to trade service
	                        for (LcRequiredDocument lcRequiredDocument : lc.getRequiredDocuments()) {
	                            Map<String, Object> lcRequiredDocumentFields = lcRequiredDocument.getFields();
	
	                            DocumentCode documentCode = null;
	
	                            RequiredDocumentType requiredDocumentType = RequiredDocumentType.valueOf((String) lcRequiredDocumentFields.get("requiredDocumentType"));
	
	                            if (requiredDocumentType.equals(RequiredDocumentType.DEFAULT)) {
	                                documentCode = new DocumentCode((String) lcRequiredDocumentFields.get("documentCode"));
	                            }
	
	                            RequiredDocument requiredDocument = new RequiredDocument(documentCode, (String) lcRequiredDocumentFields.get("description"), requiredDocumentType);
	
	                            requiredDocumentList.add(requiredDocument);
	                        }
	
	                        // copies instructions to bank from letter of credit to trade service
	                        for (LcInstructionToBank lcInstructionToBank : lc.getInstructionsToBank()) {
	                            Map<String, Object> lcInstructionToBankFields = lcInstructionToBank.getFields();
	
	                            InstructionToBankCode instructionToBankCode = new InstructionToBankCode((String) lcInstructionToBankFields.get("instructionToBankCode"));
	
	                            InstructionToBank instructionToBank = new InstructionToBank(instructionToBankCode, (String) lcInstructionToBankFields.get("instruction"));
	
	                            instructionToBankList.add(instructionToBank);
	                        }
	
	                        // copies additional conditions from letter of credit to trade service to
	                        for (LcAdditionalCondition lcAdditionalCondition : lc.getAdditionalCondition()) {
	                            Map<String, Object> lcAdditionalConditionFields = lcAdditionalCondition.getFields();
	
	                            ConditionCode conditionCode = null;
	
	                            ConditionType conditionType = ConditionType.valueOf((String) lcAdditionalConditionFields.get("conditionType"));
	
	                            if (conditionType.equals(ConditionType.DEFAULT)) {
	                                conditionCode = new ConditionCode((String) lcAdditionalConditionFields.get("conditionCode"));
	                            }
	
	                            AdditionalCondition additionalCondition = new AdditionalCondition(conditionType, conditionCode, (String) lcAdditionalConditionFields.get("condition"));
	
	                            additionalConditionList.add(additionalCondition);
	                        }
	
	                        // add required documents from lc to trade service
	                        tradeService.addRequiredDocuments(requiredDocumentList);
	
	                        // add additional conditions from lc to trade service
	                        tradeService.addInstructionToBank(instructionToBankList);
	
	                        // add additional conditions from lc to trade service
	                        tradeService.addAdditionalCondition(additionalConditionList);

                        
                            noPaymentAtAll = Boolean.TRUE; // sets no payment for tsd initiated transactions
                        }
                        
                        break;
                        /*DocumentSubType1 documentSubType1Amendment = DocumentSubType1.valueOf(((String) parameterMap.get("documentSubType1")).toUpperCase());

                        switch (documentSubType1Amendment) {

                            case CASH:
                                if (tradeService.getDetails().get("amountSwitch") == null || (tradeService.getDetails().get("amountSwitch") != null && ((String) tradeService.getDetails().get("amountSwitch")).equals(""))) {
                                    noPaymentAtAll = Boolean.TRUE;
                                }
                                break;

                            default:
                                noPaymentAtAll = Boolean.TRUE;
                                break;
                        }
                        break;*/

                    case ADJUSTMENT:

                        DocumentSubType1 documentSubType1Adjustment = DocumentSubType1.valueOf(((String) parameterMap.get("documentSubType1")).toUpperCase());

                        if (documentSubType1Adjustment.equals(DocumentSubType1.REGULAR)) {

                            String partialCashSettlementFlag = (String) parameterMap.get("partialCashSettlementFlag");

                            System.out.println("partialCashSettlementFlag = " + partialCashSettlementFlag);

                            if (partialCashSettlementFlag == null || (partialCashSettlementFlag != null && partialCashSettlementFlag.equals(""))) {
                                noPaymentAtAll = Boolean.TRUE;
                            }

                        } else {
                            noPaymentAtAll = Boolean.TRUE;
                        }

                        break;

                    case NEGOTIATION:
                        if (documentType.equals(DocumentType.DOMESTIC) &&
                                documentSubType1.equals(DocumentSubType1.REGULAR) &&
                                documentSubType2.equals(DocumentSubType2.USANCE)) {
                            noPaymentAtAll = true;
                        }

                        break;
                }

            } else if (documentClass.equals(DocumentClass.AP)) {

                if (serviceType.equals(ServiceType.SETUP)) {
                    noPaymentAtAll = Boolean.TRUE;
                } else if (serviceType.equals(ServiceType.APPLY)) {
                    noPaymentAtAll = Boolean.TRUE;
                }

            } else if (documentClass.equals(DocumentClass.AR)) {

                if (serviceType.equals(ServiceType.SETUP)) {
                    noPaymentAtAll = Boolean.TRUE;
                }
            } else if (documentClass.equals(DocumentClass.DA)) {
                System.out.println("Loaded " + serviceType);
                switch (serviceType) {
                    case NEGOTIATION_ACKNOWLEDGEMENT:
                        // TODO: which one is this? what do we do if client is walk-in
                        String branchUnitCode = (String) parameterMap.get("ccbdBranchUnitCode");
                        String processingUnitCode = (String) parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(DocumentClass.DA, ServiceType.NEGOTIATION_ACKNOWLEDGEMENT, null);
//					System.out.println("pattern for doc number =" + branchUnitCode + "-" + processingUnitCode + "-" + documentCode);
//					String documentNumberStr = nonLcNumberGenerator.generateNonLcNumber(branchUnitCode, documentCode, processingUnitCode);

//					DocumentNumber documentNumber = new DocumentNumber(documentNumberStr);

                        // refactored to use the unified approach
                        DocumentNumber documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);

                        tradeService.setDocumentNumber(documentNumber);
                        tradeService.setTradeProductNumber(new TradeProductNumber(documentNumber.toString()));

                        noPaymentAtAll = Boolean.TRUE;
                        break;
                    // TODO: FOR ARVIN
                    case NEGOTIATION_ACCEPTANCE:
                        noPaymentAtAll = Boolean.TRUE;
                        break;
                    case SETTLEMENT:
                        break;
                    case CANCELLATION:
                        break;
                }
            } else if (documentClass.equals(DocumentClass.DP)) {
                switch (serviceType) {
                    case NEGOTIATION:
                        // TODO: which one is this? what do we do if client is walk-in
                        String branchUnitCode = (String) parameterMap.get("ccbdBranchUnitCode");
                        String processingUnitCode = (String) parameterMap.get("processingUnitCode");
                        String documentCode = DocumentCodeEnum.toString(DocumentClass.DP, ServiceType.NEGOTIATION, null);
//					System.out.println("pattern for doc number =" + branchUnitCode + "-" + processingUnitCode + "-" + documentCode);
//					String documentNumberStr = nonLcNumberGenerator.generateNonLcNumber(branchUnitCode, documentCode, processingUnitCode);
//					DocumentNumber documentNumber = new DocumentNumber(documentNumberStr);

                        // refactored to use the unified approach
                        DocumentNumber documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);

                        tradeService.setDocumentNumber(documentNumber);
                        tradeService.setTradeProductNumber(new TradeProductNumber(documentNumber.toString()));

                        noPaymentAtAll = Boolean.TRUE;
//					branchUnitCode = (String)parameterMap.get("unitcode");
//					processingUnitCode = (String)parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, null, documentSubType1);
//
//					String documentNumberStr = documentNumberGenerator.generateDocumentNumber(branchUnitCode, documentCode, processingUnitCode);
//
//					documentNumber = new DocumentNumber(documentNumberStr);
                        break;
                    // TODO: FOR ARVIN
                    case SETTLEMENT:
                    //Removed.. not needed for DP anymore
//                        System.out.println("SETTLE FLAG IS " + tradeService.getDetails().get("settleFlag"));
//                        System.out.println("tradeService.getDetails().get(\"settleFlag\") > " + tradeService.getDetails().get("settleFlag"));
//                        if (tradeService.getDetails().get("settleFlag") != null && "Y".equals(tradeService.getDetails().get("settleFlag"))) {
//                            System.out.println("nopayment");
//                            noPaymentAtAll = Boolean.TRUE;
//                        } else {
//                            System.out.println("haspayment");
//                            noPaymentAtAll = Boolean.FALSE;
//                        }

                        break;
                    case CANCELLATION:
                        break;
                }
            } else if (documentClass.equals(DocumentClass.OA)) {
                switch (serviceType) {
                    case NEGOTIATION:
                        // TODO: which one is this? what do we do if client is walk-in
                        String branchUnitCode = (String) parameterMap.get("ccbdBranchUnitCode");
                        String processingUnitCode = (String) parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(DocumentClass.OA, ServiceType.NEGOTIATION, null);
//					System.out.println("pattern for doc number =" + branchUnitCode + "-" + processingUnitCode + "-" + documentCode);
//					String documentNumberStr = nonLcNumberGenerator.generateNonLcNumber(branchUnitCode, documentCode, processingUnitCode);

//					DocumentNumber documentNumber = new DocumentNumber(documentNumberStr);

                        // refactored to use the unified approach
                        DocumentNumber documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);

                        tradeService.setDocumentNumber(documentNumber);
                        tradeService.setTradeProductNumber(new TradeProductNumber(documentNumber.toString()));

                        noPaymentAtAll = Boolean.TRUE;
//					branchUnitCode = (String)parameterMap.get("unitcode");
//					processingUnitCode = (String)parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, null, documentSubType1);
//
//					String documentNumberStr = documentNumberGenerator.generateDocumentNumber(branchUnitCode, documentCode, processingUnitCode);
//
//					documentNumber = new DocumentNumber(documentNumberStr);
                        break;
                    // TODO: FOR ARVIN
                    case SETTLEMENT:
                        break;
                    case CANCELLATION:
                        break;
                }
            } else if (documentClass.equals(DocumentClass.DR)) {
                switch (serviceType) {
                    case NEGOTIATION:
                        // TODO: which one is this? what do we do if client is walk-in
                        String branchUnitCode = (String) parameterMap.get("ccbdBranchUnitCode");
                        String processingUnitCode = (String) parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(DocumentClass.DR, ServiceType.NEGOTIATION, null);
//					System.out.println("pattern for doc number =" + branchUnitCode + "-" + processingUnitCode + "-" + documentCode);
//					String documentNumberStr = nonLcNumberGenerator.generateNonLcNumber(branchUnitCode, documentCode, processingUnitCode);

//					DocumentNumber documentNumber = new DocumentNumber(documentNumberStr);

                        // refactored to use the unified approach
                        DocumentNumber documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);

                        tradeService.setDocumentNumber(documentNumber);
                        tradeService.setTradeProductNumber(new TradeProductNumber(documentNumber.toString()));

                        noPaymentAtAll = Boolean.TRUE;
//					branchUnitCode = (String)parameterMap.get("unitcode");
//					processingUnitCode = (String)parameterMap.get("processingUnitCode");
//					String documentCode = DocumentCodeEnum.toString(documentClass, null, documentSubType1);
//
//					String documentNumberStr = documentNumberGenerator.generateDocumentNumber(branchUnitCode, documentCode, processingUnitCode);
//
//					documentNumber = new DocumentNumber(documentNumberStr);
                        break;
                    // TODO: FOR ARVIN
                    case SETTLEMENT:
                        break;
                    case CANCELLATION:
                        break;
                }

            } else if (documentClass.equals(DocumentClass.EXPORT_ADVISING)) {
                String branchUnitCode = null;
                String processingUnitCode = null;

                String tradeServiceReferenceNumber = null;

                switch (serviceType) {
                    case OPENING_ADVISING:
                        branchUnitCode = (String) parameterMap.get("ccbdBranchUnitCode");
                        processingUnitCode = (String) parameterMap.get("unitcode");

                        DocumentNumber documentNumber = null;

                        if (parameterMap.get("documentNumber") != null && !parameterMap.get("documentNumber").toString().isEmpty()) {
                            documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                        } else {
                            documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, DocumentClass.EXPORT_ADVISING, null, null, null, ServiceType.OPENING_ADVISING);
                        }

                        tradeService.setDocumentNumber(documentNumber);
                        tradeService.setTradeProductNumber(new TradeProductNumber(documentNumber.toString()));

                        tradeServiceReferenceNumber = tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode);

                        tradeService.setTradeServiceReferenceNumber(new TradeServiceReferenceNumber(tradeServiceReferenceNumber));
                        break;

                    case CANCELLATION_ADVISING:
                    case AMENDMENT_ADVISING:
                        processingUnitCode = (String) parameterMap.get("unitcode");

                        tradeService.setTradeProductNumber(new TradeProductNumber(tradeService.getDocumentNumber().toString()));

                        tradeServiceReferenceNumber = tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode);

                        tradeService.setTradeServiceReferenceNumber(new TradeServiceReferenceNumber(tradeServiceReferenceNumber));
                        break;
                }

            } else if (documentClass.equals(DocumentClass.BP)) {
            	
            	// 02082017 - Added by pat for Redmine Issue #4143
        		System.out.println("SERVICETYPE >>>>>>>>>>>>>> " + serviceType);
        		switch (serviceType) {
                case SETTLEMENT:
                	noPaymentAtAll = Boolean.TRUE;
                	break;
        		}
        		tradeService.setTSDOwner(new UserId("TSD"));
            	if(null != tradeService.getDetails().get("negotiationNumber") && (null != tradeService.getDetails().get("paymentMode") || DocumentType.DOMESTIC.equals(documentType))){
            		Map<String, Object> map = tradeService.getDetails();
            		
            		if ("LC".equals((String) tradeService.getDetails().get("paymentMode")) || DocumentType.DOMESTIC.equals(documentType)) {
            			Map lcDetails = exportBillsRepository.loadToMapLcDetails(new DocumentNumber(tradeService.getDetails().get("negotiationNumber").toString()));
            			Map ebcTradeServiceDetails = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(tradeService.getDetails().get("negotiationNumber").toString()), ServiceType.NEGOTIATION, DocumentType.FOREIGN, DocumentClass.BC);
            			
            			Map ebcDetails = (Map) ebcTradeServiceDetails.get("details");
            			
            			String issuingBankName = ebcDetails.get("issuingBankName").toString();
            			
            			map.put("adviseNumber", lcDetails.get("adviseNumber"));

            			Map lcNumber = (Map) lcDetails.get("lcNumber");
                        map.put("lcNumber", lcNumber.get("documentNumber"));
                        
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");

                        Date lcIssueDate = simpleDateFormat.parse((String) lcDetails.get("lcIssueDate"));
                        Date lcExpiryDate = simpleDateFormat.parse((String) lcDetails.get("lcExpiryDate"));

                        simpleDateFormat.applyPattern("MM/dd/yyyy");
                        
                        map.put("lcIssueDate", simpleDateFormat.format(lcIssueDate));
            			map.put("lcType", lcDetails.get("lcType"));
            			map.put("lcTenor", lcDetails.get("lcTenor"));
            			map.put("usanceTerm", lcDetails.get("usanceTerm"));

                        Map lcCurrency = (Map) lcDetails.get("lcCurrency");
                        map.put("lcCurrency", lcCurrency.get("currencyCode"));

            			map.put("lcAmount", lcDetails.get("lcAmount"));
            			map.put("lcExpiryDate", simpleDateFormat.format(lcExpiryDate));
            			
            			map.put("issuingBankCode", lcDetails.get("issuingBankCode"));
            			map.put("issuingBankName", issuingBankName);
            			map.put("issuingBankAddress", lcDetails.get("issuingBankAddress"));
            			map.put("reimbursingBankCode", lcDetails.get("reimbursingBankCode"));
            			map.put("lcDescriptionOfGoods", lcDetails.get("lcDescriptionOfGoods"));
            			//added by Henry
            			map.put("collectingBankCode", lcDetails.get("swiftAddress"));
            			map.put("collectingBankAddress", lcDetails.get("negoAdviceAddresseeAddress"));
            			//end
            		} else if (("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                      		 	"DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                      		 	"OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                      		 	"DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
            			Map nonLcDetails = exportBillsRepository.loadToMapNonLcDetails(new DocumentNumber(tradeService.getDetails().get("negotiationNumber").toString()));
            			
            			map.put("tenor", nonLcDetails.get("tenor"));
            			map.put("tenorTerm", nonLcDetails.get("tenorTerm"));
            			
            			Map draftCurrency = (Map) nonLcDetails.get("draftCurrency");
            			map.put("draftCurrency", draftCurrency.get("currencyCode"));
            			
            			map.put("draftAmount", nonLcDetails.get("draftAmount"));
            			
            			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
            			
            			if(nonLcDetails.get("dueDate") != null){
	            			Date dueDate = simpleDateFormat.parse((String) nonLcDetails.get("dueDate"));
	            			
	            			simpleDateFormat.applyPattern("MM/dd/yyyy");
	            			
	            			map.put("dueDate", simpleDateFormat.format(dueDate));
            			}
            			
            			map.put("collectingBankCode", nonLcDetails.get("collectingBankCode"));
            			map.put("collectingBankAddress", nonLcDetails.get("collectingBankAddress"));
            			map.put("nonLcDescriptionOfGoods", nonLcDetails.get("nonLcDescriptionOfGoods"));
            		}
            		tradeService.updateDetails(map, userActiveDirectoryId);
            	}
            } else if (documentClass.equals(DocumentClass.BC)) {
                String branchUnitCode = null;
                String processingUnitCode = null;

                String tradeServiceReferenceNumber = null;

                branchUnitCode = (String) parameterMap.get("ccbdBranchUnitCode");
                processingUnitCode = (String) parameterMap.get("unitcode");

                DocumentNumber documentNumber = null;

                switch (serviceType) {
                    case NEGOTIATION:
                        if (parameterMap.get("documentNumber") != null && !parameterMap.get("documentNumber").toString().isEmpty()) {
                            documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                        } else {
                            documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, DocumentClass.BC, DocumentType.valueOf((String)parameterMap.get("documentType")), null, null, ServiceType.NEGOTIATION);
                        }

                        tradeService.setDocumentNumber(documentNumber);
                        tradeService.setTradeProductNumber(new TradeProductNumber(documentNumber.toString()));
                        tradeService.getDetails().put("documentNumber", tradeService.getDocumentNumber().toString());
                        
                        // set Payment Status of BC Negotiation
                        noPaymentAtAll = Boolean.TRUE;

                        // set LC Details if there is an EXLC Advise Number selected
                        if(null != tradeService.getDetails().get("exlcAdviseNumber")){
	                        Map exportAdvising = tradeProductRepository.loadToMapExportAdvising(new DocumentNumber((String) tradeService.getDetails().get("exlcAdviseNumber")));
	
	                        if (exportAdvising != null) {
	                            Map<String, Object> map = tradeService.getDetails();
	
	                            map.put("adviseNumber", tradeService.getDetails().get("exlcAdviseNumber"));
	
	                            Map lcNumber = (Map) exportAdvising.get("lcNumber");
	                            map.put("lcNumber", lcNumber.get("documentNumber"));
	
	                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
	
	                            Date lcIssueDate = simpleDateFormat.parse((String) exportAdvising.get("lcIssueDate"));
	                            Date lcExpiryDate = simpleDateFormat.parse((String) exportAdvising.get("lcExpiryDate"));
	
	                            simpleDateFormat.applyPattern("MM/dd/yyyy");
	
	                            map.put("lcIssueDate", simpleDateFormat.format(lcIssueDate));
	                            map.put("lcType", exportAdvising.get("lcType"));
	                            map.put("lcTenor", exportAdvising.get("lcTenor"));
	                            map.put("usanceTerm", exportAdvising.get("usanceTerm"));
	
	                            Map lcCurrency = (Map) exportAdvising.get("lcCurrency");
	                            map.put("lcCurrency", lcCurrency.get("currencyCode"));
	
	                            map.put("lcAmount", exportAdvising.get("lcAmount"));
	                            map.put("lcExpiryDate", simpleDateFormat.format(lcExpiryDate));
	                            map.put("issuingBankCode", exportAdvising.get("issuingBank"));
	                            map.put("issuingBankAddress", exportAdvising.get("issuingBankAddress"));
	
	                            tradeService.updateDetails(map, userActiveDirectoryId);
	                        }
                        }


                        tradeServiceReferenceNumber = tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode);

                        tradeService.setTradeServiceReferenceNumber(new TradeServiceReferenceNumber(tradeServiceReferenceNumber));
                        break;

                    case CANCELLATION:
                        if (parameterMap.get("documentNumber") != null && !parameterMap.get("documentNumber").toString().isEmpty()) {
                            documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                        } else {
                            documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, branchUnitCode, DocumentClass.BC, DocumentType.valueOf((String)parameterMap.get("documentType")), null, null, ServiceType.CANCELLATION);
                        }

                        tradeService.setDocumentNumber(documentNumber);
                        tradeService.setTradeProductNumber(new TradeProductNumber(documentNumber.toString()));

                        tradeServiceReferenceNumber = tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode);

                        tradeService.setTradeServiceReferenceNumber(new TradeServiceReferenceNumber(tradeServiceReferenceNumber));
                        break;
                        
                    case SETTLEMENT:  //Create new case for handling EBC settlement ER# 20160622-077
                    	tradeService.setTSDOwner(new UserId("TSD"));
                    	System.out.println("###GET GET DETAILS###");
                    	System.out.println(tradeService.getDetails());
                    	//Added By Henry Alabin

					if (tradeService.getDetails().get("bpAmount").toString() != null) {
						System.out.println("PASOK");
						if(!tradeService.getDetails().get("bpAmount").toString().isEmpty()){
							System.out.println("PASOK2");
							BigDecimal proceedsAmount = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get("proceedsAmount").toString()));
							BigDecimal bpAmount = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get("bpAmount").toString()));
							// Brian ieedit para sa pagtanggal ng nego tab
							System.out.println(proceedsAmount + " HENRY");
							if (proceedsAmount.compareTo(bpAmount) == 0) {
								noPaymentAtAll = Boolean.TRUE;
							} else if (proceedsAmount.compareTo(bpAmount) == -1) {
								noPaymentAtAll = Boolean.FALSE;
								System.out.println("PASOK LANG DITO");
							}
						}
						noPaymentAtAll = Boolean.TRUE;
					}
					
					// 03222017 - RM 4176 Edit by Pat - Check if bpAmount and proceedsAmount is not empty before parsing
					if ( !(tradeService.getDetails().get("bpAmount").toString()).equals("") && 
							!(tradeService.getDetails().get("proceedsAmount").toString()).equals("") ){
						BigDecimal proceedsAmount = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get("proceedsAmount").toString()));
						System.out.println(tradeService.getDetails().get("bpAmount").toString() +" PAT");
						BigDecimal bpAmount = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get("bpAmount").toString()));
						System.out.println(proceedsAmount +" HENRY");
							if (proceedsAmount.compareTo(bpAmount) == 0){
								noPaymentAtAll = Boolean.TRUE;
							} else if(proceedsAmount.compareTo(bpAmount) == -1){
								noPaymentAtAll = Boolean.FALSE;
								System.out.println("PASOK LANG DITO");
							}
						}
                        break;
                }
            } else if (documentClass.equals(DocumentClass.REBATE)) {

                if (parameterMap.get("rebateDocumentNumber") != null && !parameterMap.get("rebateDocumentNumber").toString().isEmpty()) {
                    DocumentNumber documentNumber = new DocumentNumber((String) parameterMap.get("rebateDocumentNumber"));

                    tradeService.setDocumentNumber(documentNumber);

                    tradeService.setTradeProductNumber(new TradeProductNumber(documentNumber.toString()));
                }

                String processingUnitCode = (String) parameterMap.get("unitcode");
                TradeServiceReferenceNumber tradeServiceReferenceNumber = new TradeServiceReferenceNumber(tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode));

                tradeService.setTradeServiceReferenceNumber(tradeServiceReferenceNumber);

                noPaymentAtAll = Boolean.TRUE;
            } else if (documentClass.equals(DocumentClass.MD)) {
                if (serviceType.equals(ServiceType.APPLICATION)) {
                    noPaymentAtAll = Boolean.TRUE;
                }
            } else if (DocumentClass.CORRES_CHARGE.equals(documentClass)) {
                System.out.println("correschrge oye");
                System.out.println(tradeService.getServiceInstructionId());
                if (tradeService.getDetails().get("withoutReference") == null && tradeService.getServiceInstructionId() == null) {
                    System.out.println("setting no payment boo ya!");
                    noPaymentAtAll = Boolean.TRUE;
                }
            } else if (DocumentClass.CDT.equals(documentClass) && ServiceType.REMITTANCE.equals(serviceType)) {
                noPaymentAtAll = Boolean.TRUE;
            }

            if (noPaymentAtAll) {
                System.out.println("setting no payment required #2");
                tradeService.setAsNoPaymentRequired();
                tradeServiceRepository.merge(tradeService);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error in createTradeService(tradeServiceSavedEvent)",e);
        }
    }

    @EventListener
    public void updateTradeService(ServiceInstructionUpdatedEvent serviceInstructionUpdatedEvent) {

        System.out.println("\n INSIDE updateTradeService(ServiceInstructionUpdatedEvent)\n");

        // 1. If ETS PREPARED or APPROVED, update TradeService object.
        // 2. Fire TradeServiceUpdatedEvent.

        try {

            ServiceInstruction ets = serviceInstructionUpdatedEvent.getServiceInstruction();
            ServiceInstructionStatus status = serviceInstructionUpdatedEvent.getServiceInstructionStatus();

            UserActiveDirectoryId userActiveDirectoryId = serviceInstructionUpdatedEvent.getUserActiveDirectoryId();

            Map<String, Object> parameterMap = ets.getDetails();

            TradeService tradeService = tradeServiceRepository.load(ets.getServiceInstructionId());
            //Commented this out it seems the charges are being overwritten with the incorrect
            //chargesService.applyCharges(tradeService, ets);

            // Get the old Facility Reference Number from TradeService
            // For CASH LC's this should be null
            String oldFacilityReferenceNumber = (String) tradeService.getDetails().get("facilityReferenceNumber");

            tradeService = TradeServiceService.updateTradeServiceDetails(tradeService, parameterMap, userActiveDirectoryId, "N");
            tradeService = TradeServiceService.updateProductCharge(tradeService, parameterMap, userActiveDirectoryId);

            DocumentClass documentClass = tradeService.getDocumentClass();
            ServiceType serviceType = tradeService.getServiceType();
            System.out.println("HAAAAAAAAAAAAAAAAAA " + status);

            if (status != null) {

                Boolean persistTradeService = Boolean.FALSE;

                if (status.equals(ServiceInstructionStatus.DRAFT)) {

                    System.out.println("\n###################### SAVE AS DRAFT!\n");

                    // Just change status to DRAFT
                    tradeService.updateStatus(TradeServiceStatus.DRAFT, userActiveDirectoryId);
                    persistTradeService = Boolean.TRUE;

                } else if (status.equals(ServiceInstructionStatus.PREPARED) || status.equals(ServiceInstructionStatus.APPROVED)) {

                    if (documentClass.equals(DocumentClass.LC)) {

                        if (status.equals(ServiceInstructionStatus.PREPARED)) {

                            // Earmark to Facility: For REGULAR and STANDBY LCs only
                        	DocumentSubType1 documentSubType1 = null;
                        	if (parameterMap.get("documentSubType1") != null){
                        		documentSubType1 = DocumentSubType1.valueOf(((String) parameterMap.get("documentSubType1")).toUpperCase());
                        	}

                            if (documentSubType1 != null && (documentSubType1.equals(DocumentSubType1.REGULAR) || documentSubType1.equals(DocumentSubType1.STANDBY))) {

                                Map<String, Object> details = tradeService.getDetails();

                                String newFacilityReferenceNumber = (String) tradeService.getDetails().get("facilityReferenceNumberTo");

                                System.out.println("\n##### ADJUSTMENT: Facility Reference Number #####");
                                System.out.println("oldFacilityReferenceNumber = " + oldFacilityReferenceNumber);
                                System.out.println("newFacilityReferenceNumber = " + newFacilityReferenceNumber);
                                System.out.println("##########\n");

                                String amountSwitch = (String)details.get("amountSwitch");

                                // For reinstatement
                                if (tradeService.isForReinstatement()) {

                                    /*
                                    if (serviceType.equals(ServiceType.ADJUSTMENT) ||
                                        serviceType.equals(ServiceType.AMENDMENT) ||
                                        serviceType.equals(ServiceType.CANCELLATION) ||
                                        serviceType.equals(ServiceType.NEGOTIATION)) {
                                    */

                                        Availment availment = new Availment();

                                        availment.setDocumentNumber(tradeService.getTradeProductNumber().toString());

                                        availment.setCifNumber((String) details.get("cifNumber"));

                                        String amountStr = (String) details.get("amount");
                                        String productAmountStr = (String) details.get("productAmount");
                                        BigDecimal amountBd = null;
                                        String outstandingBalanceStr = (String) details.get("outstandingBalance");
                                        BigDecimal outstandingBd = null;

                                        if (amountStr != null) {
                                            amountBd = new BigDecimal(amountStr.replaceAll(",",""));
                                        } else if (productAmountStr != null) {
                                            amountBd = new BigDecimal(productAmountStr.replaceAll(",",""));
                                        }
                                        
                                        if (outstandingBalanceStr != null){
                                        	outstandingBd = new BigDecimal(outstandingBalanceStr.replaceAll(",",""));
                                        } else {
                                        	outstandingBd = amountBd;
                                        }

                                        String currencyStr = (String) details.get("currency");
                                        Currency currency = null;
                                        if (currencyStr != null) {
                                            currency = Currency.getInstance(currencyStr);
                                        }

                                        availment.setFacilityReferenceNumber((String) details.get("facilityReferenceNumber"));

                                        if (serviceType.equals(ServiceType.ADJUSTMENT)) {

                                            if (!newFacilityReferenceNumber.isEmpty() && !oldFacilityReferenceNumber.equals(newFacilityReferenceNumber)) {
                                                availment.setFacilityReferenceNumber(newFacilityReferenceNumber);
                                            }

                                            String partialCashSettlementFlag = (String) details.get("partialCashSettlementFlag");
                                            if (partialCashSettlementFlag != null && partialCashSettlementFlag.equals("partialCashSettlementEnabled")) {

                                                String exchangeRate = tradeService.getDetails().get("creationExchangeRate") != null ? (String) tradeService.getDetails().get("creationExchangeRate") : "0";
                                                availment.setExchangeRate(new BigDecimal(exchangeRate));

                                                BigDecimal outstandingBalance = new BigDecimal(((String) details.get("outstandingBalance")).replaceAll(",",""));
                                                BigDecimal cashAmount = new BigDecimal(((String) details.get("cashAmount")).replaceAll(",",""));

                                                // If REGULAR to CASH, update the earmarked amount with the difference of outstanding balance
                                                // minus the cash amount.
                                                System.out.println("\n$$$ REINSTATEMENT : FACILITY EARMARKING (Change Facility and REGULAR to CASH Adjustment) :::::::::\n");
                                                outstandingBd = outstandingBalance.subtract(cashAmount);
                                            }

                                            String cifNumberFlag = (String) details.get("cifNumberFlag");
                                            if (cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled")) {
                                                String cifNumberFrom = (String) details.get("cifNumberFrom");
                                                String cifNumberTo = (String) details.get("cifNumberTo");
                                                String cifNumber = null;
                                                if (cifNumberTo != null && !cifNumberTo.isEmpty()) {
                                                    cifNumber = cifNumberTo;
                                                } else if (cifNumberFrom != null && !cifNumberFrom.isEmpty()) {
                                                    cifNumber = cifNumberFrom;
                                                }
                                                if (cifNumber != null && !cifNumber.isEmpty()) {
                                                    availment.setCifNumber(cifNumber);
                                                }
                                            }

                                        } else if (serviceType.equals(ServiceType.AMENDMENT)) {
                                        	BigDecimal outstandingBalance = new BigDecimal((String) details.get("outstandingBalance"));
                                        	BigDecimal amountFrom = new BigDecimal(((String) details.get("amountFrom")).replaceAll(",",""));

                                            if (amountSwitch != null && amountSwitch.equalsIgnoreCase("on")) {

                                                String lcAmountFlag = (String) details.get("lcAmountFlag");
                                                System.out.println("\n###################### serviceType = " + serviceType.toString());
                                                System.out.println("###################### amountSwitch = " + amountSwitch);
                                                System.out.println("###################### lcAmountFlag = " + lcAmountFlag + "\n");

                                                String amountTo = (String)details.get("amountTo");
                                                System.out.println("\n$$$ REINSTATEMENT : FACILITY EARMARKING: AMENDMENT ::::::::::");
                                                System.out.println("$$$ amountTo = " + amountTo + "\n");
                                                amountBd = new BigDecimal(amountTo.replaceAll(",",""));

                                                // Currency currency = Currency.getInstance((String) details.get("currency"));
                                            } else {
                                            	System.out.println("\n$$$ REINSTATEMENT : FACILITY EARMARKING: AMENDMENT ::::::::::");
                                            	System.out.println("$$$ amountFrom = " + amountFrom.toPlainString() + "\n");
                                            	amountBd = amountFrom;
                                            }
                                            
                                            outstandingBd = amountBd.subtract(amountFrom.subtract(outstandingBalance));

                                        } else if (serviceType.equals(ServiceType.NEGOTIATION)) {

                                             BigDecimal outstandingBalance = new BigDecimal((String) details.get("outstandingBalance"));
                                             BigDecimal negotiationAmount = new BigDecimal((String) details.get("negotiationAmount"));
                                             BigDecimal cashAmount = details.get("cashAmount") != null ? new BigDecimal(((String) details.get("cashAmount")).replaceAll(",","")) : BigDecimal.ZERO;

                                            String negotiationCurrencyStr = (String) details.get("negotiationCurrency");
                                            if (negotiationCurrencyStr != null) {
                                                currency = Currency.getInstance(negotiationCurrencyStr);
                                            }

                                            // If NEGOTIATION, the earmarked amount is the difference of outstanding balance
                                            // minus the negotiation amount.
                                            System.out.println("\n$$$ REINSTATEMENT : FACILITY EARMARKING NEGOTIATION :::::::::\n");
                                            if(PaymentStatus.PAID.equals(tradeService.getPaymentStatus())){
                                            	outstandingBd = outstandingBalance.subtract(negotiationAmount);
                                            } else {
                                            	outstandingBd = outstandingBalance;
                                            }
                                            outstandingBd = outstandingBd.subtract(cashAmount);
                                            	
                                        }

                                        availment.setOriginalAmount(amountBd);

                                        availment.setOutstandingBalance(outstandingBd);

                                        availment.setCurrencyCode(currency.getCurrencyCode());
                                        availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());

                                        System.out.println("\n$$$ REINSTATEMENT : FACILITY EARMARKING ::::::: ");
                                        System.out.println("$$$ documentNumber = " + availment.getDocumentNumber());
                                        System.out.println("$$$ cifNumber = " + availment.getCifNumber());
                                        System.out.println("$$$ facilityReferenceNumber = " + availment.getFacilityReferenceNumber());
                                        System.out.println("$$$ amount = " + availment.getOriginalAmount().toPlainString());
                                        System.out.println("$$$ outstanding balance = " + availment.getOutstandingBalance().toPlainString());
                                        System.out.println("$$$ currency = " + availment.getCurrencyCode());
                                        System.out.println("$$$ statusDescription = " + availment.getStatusDescription() + "\n");

                                        // This method updates a previous earmark if existing
                                        facilityService.earmarkAvailment(availment);
                                    // }

                                // Not for reinstatement
                                } else {

                                    String cifNumberFlag = (String) details.get("cifNumberFlag");

                                    if (serviceType.equals(ServiceType.OPENING) || (serviceType.equals(ServiceType.ADJUSTMENT) && (!oldFacilityReferenceNumber.equals(newFacilityReferenceNumber) ||
                                                                                                                                   (cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled"))))) {

                                        Availment availment = new Availment();

                                        availment.setDocumentNumber(tradeService.getTradeProductNumber().toString());

                                        availment.setCifNumber((String) details.get("cifNumber"));

                                        if (serviceType.equals(ServiceType.ADJUSTMENT) && (cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled"))) {
                                            String cifNumberFrom = (String) details.get("cifNumberFrom");
                                            String cifNumberTo = (String) details.get("cifNumberTo");
                                            String cifNumber = null;
                                            if (cifNumberTo != null && !cifNumberTo.isEmpty()) {
                                                cifNumber = cifNumberTo;
                                            } else if (cifNumberFrom != null && !cifNumberFrom.isEmpty()) {
                                                cifNumber = cifNumberFrom;
                                            }
                                            if (cifNumber != null && !cifNumber.isEmpty()) {
                                                availment.setCifNumber(cifNumber);
                                            }
                                        }

                                        availment.setFacilityReferenceNumber((String) details.get("facilityReferenceNumber"));
                                        if ((serviceType.equals(ServiceType.ADJUSTMENT) && !newFacilityReferenceNumber.isEmpty() && !oldFacilityReferenceNumber.equals(newFacilityReferenceNumber))) {
                                            availment.setFacilityReferenceNumber(newFacilityReferenceNumber);
                                        }

                                        String amount = (String) details.get("amount");
                                        BigDecimal amountBd = null;
                                        if (amount != null) {
                                            amountBd = new BigDecimal(amount.replaceAll(",",""));
                                        } else {
                                            String productAmount = (String) details.get("productAmount");
                                            amountBd = new BigDecimal(productAmount.replaceAll(",",""));
                                        }
                                        
                                        BigDecimal outstandingBd = amountBd;
                                        if(details.get("outstandingBalance") != null){
                                        	outstandingBd = new BigDecimal(((String) details.get("outstandingBalance")).replaceAll(",",""));
                                        }

                                        String partialCashSettlementFlag = (String) details.get("partialCashSettlementFlag");
                                        if (partialCashSettlementFlag != null && partialCashSettlementFlag.equals("partialCashSettlementEnabled")) {

                                            // BigDecimal outstandingBalance = new BigDecimal((String) details.get("outstandingBalance"));
                                            BigDecimal cashAmount = new BigDecimal((String) details.get("cashAmount"));

                                            // If REGULAR to CASH, update the earmarked amount with the difference of original
                                            // minus the cash amount and the outstanding balance with the difference of the outstanding
                                            // minus the cash amount.
                                            System.out.println("\n$$$ FACILITY EARMARKING (Change Facility and REGULAR to CASH Adjustment) :::::::::\n");
                                            outstandingBd = outstandingBd.subtract(cashAmount);
                                        }

                                        availment.setOriginalAmount(amountBd);
                                        availment.setOutstandingBalance(outstandingBd);
                                        availment.setCurrencyCode(Currency.getInstance((String) details.get("currency")).getCurrencyCode());
                                        availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());

                                        System.out.println("\n$$$ FACILITY EARMARKING ::::::: ");
                                        System.out.println("$$$ documentNumber = " + availment.getDocumentNumber());
                                        System.out.println("$$$ cifNumber = " + availment.getCifNumber());
                                        System.out.println("$$$ facilityReferenceNumber = " + availment.getFacilityReferenceNumber());
                                        System.out.println("$$$ amount = " + availment.getOriginalAmount().toPlainString());
                                        System.out.println("$$$ outstanding balance = " + availment.getOutstandingBalance().toPlainString());
                                        System.out.println("$$$ currency = " + availment.getCurrencyCode());
                                        System.out.println("$$$ statusDescription = " + availment.getStatusDescription() + "\n");

                                        // This method updates a previous earmark if existing
                                        facilityService.earmarkAvailment(availment);

                                    } else if (serviceType.equals(ServiceType.AMENDMENT) && (amountSwitch != null && amountSwitch.equalsIgnoreCase("on"))) {

                                    	Availment availment = new Availment();

                                        availment.setDocumentNumber(tradeService.getTradeProductNumber().toString());
                                        availment.setCifNumber((String) details.get("cifNumber"));
                                        availment.setFacilityReferenceNumber((String) details.get("facilityReferenceNumber"));
                                        
                                        String lcAmountFlag = (String) details.get("lcAmountFlag");
                                        System.out.println("\n###################### serviceType = " + ServiceType.AMENDMENT);
                                        System.out.println("###################### amountSwitch = " + amountSwitch);
                                        System.out.println("###################### lcAmountFlag = " + lcAmountFlag + "\n");

                                        String amountTo = (String)details.get("amountTo");
                                        BigDecimal amountToBd = new BigDecimal(amountTo.replaceAll(",",""));
                                        availment.setOriginalAmount(amountToBd);
                                        
                                        BigDecimal oldAmount = new BigDecimal(((String)details.get("amount")).replaceAll(",",""));
                                        BigDecimal outstandingAmount = new BigDecimal(((String)details.get("outstandingBalance")).replaceAll(",",""));
                                        
                                        availment.setOutstandingBalance(amountToBd.subtract(oldAmount.subtract(outstandingAmount)));
                                        
                                        Currency currency = Currency.getInstance((String) details.get("currency"));
                                        availment.setCurrencyCode(currency.getCurrencyCode());
                                        availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());
                                        
                                        System.out.println("\n$$$ FACILITY EARMARKING: AMENDMENT ::::::::::");
                                        System.out.println("$$$ documentNumber = " + availment.getDocumentNumber());
                                        System.out.println("$$$ cifNumber = " + availment.getCifNumber());
                                        System.out.println("$$$ facilityReferenceNumber = " + availment.getFacilityReferenceNumber());
                                        System.out.println("$$$ amount to = " + availment.getOriginalAmount().toPlainString());
                                        System.out.println("$$$ outstanding balance to = " + availment.getOutstandingBalance().toPlainString());
                                        System.out.println("$$$ currency = " + availment.getCurrencyCode());
                                        System.out.println("$$$ statusDescription = " + availment.getStatusDescription() + "\n");

                                        // This method updates a previous earmark if existing
                                        facilityService.earmarkAvailment(availment);
                                        /*
                                        // Reinstatement
                                        Boolean isReinstated = Boolean.FALSE;
                                        if (tradeService.isForReinstatement()) {
                                            isReinstated = Boolean.TRUE;
                                        }
                                        */

                                        //// facilityService.updateAvailmentAmount(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), amountToBd, isReinstated);
                                        // facilityService.updateAvailmentAmount(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), amountToBd, Boolean.FALSE);
                                    	
                                    	

                                    /*} else if (serviceType.equals(ServiceType.ADJUSTMENT)) {

                                        String partialCashSettlementFlag = (String) details.get("partialCashSettlementFlag");

                                        if (partialCashSettlementFlag != null && partialCashSettlementFlag.equals("partialCashSettlementEnabled")) {

                                            BigDecimal outstandingBalance = new BigDecimal((String) details.get("outstandingBalance"));
                                            BigDecimal cashAmount = new BigDecimal((String) details.get("cashAmount"));
                                            Currency currency = Currency.getInstance((String) details.get("currency"));

                                            
                                            // Reinstatement
                                            if (tradeService.isForReinstatement()) {

                                                // If reinstatement and adjustment from REGULAR to CASH, earmark the "regular" amount minus the "cash" amount.
                                                // The outstanding balance should had been un-earmarked when the LC expired.

                                                System.out.println("\n$$$ FACILITY EARMARKING (Reinstatement and REGULAR to CASH Adjustment) :::::::::\n");
                                                String exchangeRate = tradeService.getDetails().get("creationExchangeRate") != null ? (String) tradeService.getDetails().get("creationExchangeRate") : "0";

                                                Availment availment = new Availment();
                                                availment.setExchangeRate(new BigDecimal(exchangeRate));
                                                availment.setDocumentNumber(tradeService.getTradeProductNumber().toString());
                                                availment.setCifNumber((String) details.get("cifNumber"));
                                                availment.setFacilityReferenceNumber((String) details.get("facilityReferenceNumber"));

                                                availment.setOriginalAmount(outstandingBalance);
                                                System.out.println("##################### availment.getOriginalAmount() = " + availment.getOriginalAmount().toPlainString());

                                                // Earmark the "regular" amount minus the "cash" amount
                                                availment.setOutstandingBalance(outstandingBalance.subtract(cashAmount));
                                                System.out.println("##################### availment.getOutstandingBalance() = " + availment.getOutstandingBalance().toPlainString());

                                                availment.setCurrencyCode(Currency.getInstance((String) details.get("currency")).getCurrencyCode());
                                                availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());

                                                System.out.println("\n$$$ FACILITY EARMARKING ::::::: ");
                                                System.out.println("$$$ documentNumber = " + availment.getDocumentNumber());
                                                System.out.println("$$$ cifNumber = " + availment.getCifNumber());
                                                System.out.println("$$$ facilityReferenceNumber = " + availment.getFacilityReferenceNumber());
                                                System.out.println("$$$ amount = " + availment.getOriginalAmount().toPlainString());
                                                System.out.println("$$$ outstanding balance = " + availment.getOutstandingBalance().toPlainString());
                                                System.out.println("$$$ currency = " + availment.getCurrencyCode());
                                                System.out.println("$$$ statusDesc = " + availment.getStatusDescription());
                                                System.out.println();

                                                facilityService.earmarkAvailment(availment);

                                            } else {
                                            

                                                // If REGULAR to CASH, update the earmarked amount with the difference of outstanding balance
                                                // minus the cash amount.
                                                System.out.println("\n$$$ FACILITY EARMARKING (REGULAR to CASH Adjustment) :::::::::\n");
                                                facilityService.updateAvailmentAmount(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), outstandingBalance.subtract(cashAmount), Boolean.FALSE);
                                            // }
                                        }*/
                                    }
                                }  // Not for reinstatement

                            }  // If REGULAR or STANDBY
                        }
                    }

                    // a trade service item was updated, we call the service to add charges to it
                    // todo:1 improve logic so that we determine if whatever changed affected any of the computations
                    // todo:2 when tagging as APPROVED or PREPARED no change is needed for the charges.
                    //chargesService.applyCharges(tradeService, ets);

                    // Since an ETS was updated, this userActiveDirectoryId belongs to a Branch user.
                    // If ETS was PREPARED or APPROVED, set status of TradeService to PENDING.
                    tradeService.updateStatus(TradeServiceStatus.PENDING, userActiveDirectoryId);

                    persistTradeService = Boolean.TRUE;

                } else if (status.equals(ServiceInstructionStatus.ABORTED) || status.equals(ServiceInstructionStatus.DISAPPROVED) || status.equals(ServiceInstructionStatus.RETURNED)) {

                    if (documentClass.equals(DocumentClass.LC)) {

                        DocumentSubType1 documentSubType1 = DocumentSubType1.valueOf(((String) parameterMap.get("documentSubType1")).toUpperCase());

                        // Un-earmark from Facility: For REGULAR and STANDBY LCs only
                        if (documentSubType1 != null && (documentSubType1.equals(DocumentSubType1.REGULAR) || documentSubType1.equals(DocumentSubType1.STANDBY))) {

                            // For reinstatement
                            if (tradeService.isForReinstatement()) {

                                System.out.println("\n$$$ REINSTATEMENT : FACILITY UN-EARMARKING :::::::::");
                                System.out.println("$$$ ServiceInstructionStatus = " + status);
                                System.out.println("$$$ DocumentClass = " + documentClass);
                                System.out.println("$$$ DocumentSubType1 = " + documentSubType1 + "\n");

                                String newFacilityReferenceNumber = (String) tradeService.getDetails().get("facilityReferenceNumberTo");
                                String cifNumberFlag = (String) tradeService.getDetails().get("cifNumberFlag");

                                if (serviceType.equals(ServiceType.ADJUSTMENT) &&
                                    (!oldFacilityReferenceNumber.equals(newFacilityReferenceNumber) || (cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled")))) {

                                    if (!oldFacilityReferenceNumber.equals(newFacilityReferenceNumber)) {
                                        facilityService.updateAvailmentFacilityReferenceNumber(tradeService.getTradeProductNumber().toString(), oldFacilityReferenceNumber);
                                    }

                                    if (cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled")) {
                                        String cifNumberFrom = (String) tradeService.getDetails().get("cifNumberFrom");
                                        facilityService.updateAvailmentCif(tradeService.getTradeProductNumber().toString(), cifNumberFrom);
                                    }
                                }

                                facilityService.unearmarkAvailment(tradeService.getTradeProductNumber().toString());

                            // Not for reinstatement
                            } else {

                                System.out.println("\n$$$ FACILITY UN-EARMARKING :::::::::");
                                System.out.println("$$$ ServiceInstructionStatus = " + status);
                                System.out.println("$$$ DocumentClass = " + documentClass);
                                System.out.println("$$$ DocumentSubType1 = " + documentSubType1 + "\n");

                                Map<String, Object> details = tradeService.getDetails();

                                if (serviceType.equals(ServiceType.OPENING)) {

                                    // If OPENING, un-earmark
                                    System.out.println("\n$$$ FACILITY UN-EARMARKING (Opening) :::::::::\n");
                                    facilityService.unearmarkAvailment(tradeService.getTradeProductNumber().toString());

                                } else if (serviceType.equals(ServiceType.ADJUSTMENT)) {

                                    String newFacilityReferenceNumber = (String) parameterMap.get("facilityReferenceNumberTo");
                                    String partialCashSettlementFlag = (String) details.get("partialCashSettlementFlag");
                                    String cifNumberFlag = (String) details.get("cifNumberFlag");

                                    if (!oldFacilityReferenceNumber.equals(newFacilityReferenceNumber) || (cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled"))) {

                                        String exchangeRate = tradeService.getDetails().get("creationExchangeRate") != null ? (String) tradeService.getDetails().get("creationExchangeRate") : "0";

                                        Availment availment = new Availment();
                                        availment.setExchangeRate(new BigDecimal(exchangeRate));
                                        availment.setDocumentNumber(tradeService.getTradeProductNumber().toString());

//                                        if (cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled")) {
                                            availment.setCifNumber((String) details.get("cifNumberFrom"));
//                                        }

                                        // Revert to the old Facility Reference Number
                                        availment.setFacilityReferenceNumber(oldFacilityReferenceNumber);

                                        BigDecimal amount = new BigDecimal(((String) details.get("amount")).replaceAll(",",""));
                                        BigDecimal outstandingBalance = new BigDecimal(((String) details.get("outstandingBalance")).replaceAll(",",""));
                                        System.out.println("##################### amount = " + amount);
                                        System.out.println("##################### outstandingBalance = " + outstandingBalance);

                                        availment.setOriginalAmount(amount);
                                        System.out.println("##################### availment.getOriginalAmount() = " + availment.getOriginalAmount().toPlainString());

                                        availment.setOutstandingBalance(outstandingBalance);
                                        System.out.println("##################### availment.getOutstandingBalance() = " + availment.getOutstandingBalance().toPlainString());

                                        availment.setCurrencyCode(Currency.getInstance((String) details.get("currency")).getCurrencyCode());
                                        availment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());

                                        System.out.println("\n$$$ FACILITY RE-EARMARKING (Adjustment) ::::::: ");
                                        System.out.println("$$$ documentNumber = " + availment.getDocumentNumber());
                                        System.out.println("$$$ cifNumber = " + availment.getCifNumber());
                                        System.out.println("$$$ facilityReferenceNumber = " + availment.getFacilityReferenceNumber());
                                        System.out.println("$$$ amount = " + availment.getOriginalAmount().toPlainString());
                                        System.out.println("$$$ outstanding balance = " + availment.getOutstandingBalance().toPlainString());
                                        System.out.println("$$$ currency = " + availment.getCurrencyCode());
                                        System.out.println("$$$ statusDesc = " + availment.getStatusDescription());
                                        System.out.println();

                                        facilityService.earmarkAvailment(availment);

                                    } else if (partialCashSettlementFlag != null && partialCashSettlementFlag.equals("partialCashSettlementEnabled")) {

                                        BigDecimal outstandingBalance = new BigDecimal((String) details.get("outstandingBalance"));
                                        Currency currency = Currency.getInstance((String) details.get("currency"));

                                        // If REGULAR to CASH, revert the earmark to the original outstanding balance
                                        System.out.println("\n$$$ FACILITY RE-EARMARKING (REGULAR to CASH) :::::::::\n");
                                        facilityService.updateAvailmentAmount(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), outstandingBalance, Boolean.FALSE);
                                    }

                                } else if (serviceType.equals(ServiceType.AMENDMENT)) {

                                    String amountSwitch = (String)details.get("amountSwitch");

                                    if (amountSwitch != null && amountSwitch.equalsIgnoreCase("on")) {

                                        // amount is the field of the original amount
                                        // amountTo is the field of the new amount
                                        String amount = (String) details.get("amount");
                                        BigDecimal amountBd = new BigDecimal(amount.replaceAll(",",""));
                                        Currency currency = Currency.getInstance((String) details.get("currency"));

                                        // If AMENDMENT, revert the earmark to the original amount
                                        System.out.println("\n$$$ FACILITY RE-EARMARKING (Amendment) :::::::::\n");
                                        facilityService.updateAvailmentAmount(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), amountBd, Boolean.FALSE);
                                    }
                                }
                            }

                        }  // If REGULAR or STANDBY
                    }
                    
                    // should service instruction be returned not by TSD, tradeService will reset to MARV. BY: ARVIN
                    if(status.equals(ServiceInstructionStatus.RETURNED) && !tradeService.getStatus().equals(TradeServiceStatus.RETURNED_TO_BRANCH)){
	                    tradeService.updateStatus(TradeServiceStatus.MARV, userActiveDirectoryId);
	                    persistTradeService = Boolean.TRUE;
                    }
                }

                if (persistTradeService) {

                    tradeServiceRepository.merge(tradeService);

                    TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), tradeService.getDetails(), tradeService.getStatus(), userActiveDirectoryId);
                    eventPublisher.publish(tradeServiceUpdatedEvent);
                }

            } else {
                System.out.println("status is null " + status);
                System.out.println( "documentClass" + documentClass);

                if (documentClass.equals(DocumentClass.LC)) {

                    switch (serviceType) {

                        case ADJUSTMENT:

                            Boolean noPaymentAtAll = Boolean.FALSE;

                            if (tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR)) {

                                String partialCashSettlementFlag = (String) parameterMap.get("partialCashSettlementFlag");

                                System.out.println("partialCashSettlementFlag = " + partialCashSettlementFlag);

                                if (partialCashSettlementFlag == null || (partialCashSettlementFlag != null && partialCashSettlementFlag.equals(""))) {

                                    System.out.println("\n``````````````` (ADJUSTMENT) REVERSING AND DELETING ALL ITEM PAYMENTS `````````````````\n");

                                    // If Partial Cash Settlement is disabled, reverse all Service and Product payments
                                    // made for the current Adjustment, then delete their Payment objects.
                                    Payment paymentService = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
                                    Payment paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);

                                    if (paymentService != null) {

                                        System.out.println("\n############ Reversing and deleting all Service Charges payments NOW...\n");
                                        paymentService.reverseAllItemPayments();
                                        paymentRepository.delete(paymentService);
                                    }

                                    if (paymentProduct != null) {

                                        System.out.println("\n############ Reversing and deleting all Product payments NOW...\n");
                                        paymentProduct.reverseAllItemPayments();
                                        paymentRepository.delete(paymentProduct);
                                    }

                                }

                                if (partialCashSettlementFlag == null || partialCashSettlementFlag.equals("")) {
                                    noPaymentAtAll = Boolean.TRUE;
                                }

                                if (partialCashSettlementFlag != null && !(partialCashSettlementFlag.equals(""))) {
                                    noPaymentAtAll = Boolean.FALSE;
                                }

                            } else {
                                noPaymentAtAll = Boolean.TRUE;
                            }

                            if (noPaymentAtAll) {
                                System.out.println("setting no payment required #3");
                                tradeService.setAsNoPaymentRequired();
                                tradeServiceRepository.merge(tradeService);
                            }

                            break;
                    }

                } else if (documentClass.equals(DocumentClass.AP)) {

                    try {

                        TradeService savedTradeService = tradeServiceRepository.load(tradeService.getTradeServiceId());
                        System.out.println("SAVED TRADESERVICEID >>> " + savedTradeService.getTradeServiceId());
                        Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                        if (payment == null) {
                            payment = new Payment(savedTradeService.getTradeServiceId(), ChargeType.PRODUCT);
                        }

                        // Create payment
                        List<Map<String, Object>> productPaymentListMap = (List<Map<String, Object>>) parameterMap.get("documentPaymentSummary");

                        Set<PaymentDetail> tempDetails = new HashSet<PaymentDetail>();
                        for (Map<String, Object> productPaymentMap : productPaymentListMap) {

                            PaymentDetail tempDetail = null;

                            String paymentMode = (String) productPaymentMap.get("paymentMode");

                            PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(paymentMode);

                            String referenceNumber = null;

                            BigDecimal amount = new BigDecimal(((String) productPaymentMap.get("amount")).trim());
                            Currency settlementCurrency = Currency.getInstance(((String) productPaymentMap.get("currency")).trim());

                            //TODO: Someone look at this

                            // Rates
                            String ratesString = (String) productPaymentMap.get("rates");
                            String[] ratesArray = ratesString.split(",");

                            String strPassOnRateUsdToPhp = null;
                            String strPassOnRateThirdToUsd = null;
                            String strPassOnRateThirdToPhp = null;
                            String strSpecialRateUsdToPhp = null;
                            String strSpecialRateThirdToUsd = null;
                            String strSpecialRateThirdToPhp = null;
                            String strUrr = null;

                            String[] usdToPhp = ratesArray[0].split("=");
                            if (usdToPhp.length > 1) {
                                strPassOnRateUsdToPhp = usdToPhp[1];
                            }
                            String[] thirdToUsd = ratesArray[1].split("=");
                            if (thirdToUsd.length > 1) {
                                strPassOnRateThirdToUsd = thirdToUsd[1];
                            }
                            String[] thirdToPhp = ratesArray[2].split("=");
                            if (thirdToPhp.length > 1) {
                                strPassOnRateThirdToPhp = thirdToPhp[1];
                            }

                            String[] specialUsdToPhp = ratesArray[3].split("=");
                            if (specialUsdToPhp.length > 1) {
                                strSpecialRateUsdToPhp = specialUsdToPhp[1];
                            }
                            String[] specialThirdToUsd = ratesArray[4].split("=");
                            if (specialThirdToUsd.length > 1) {
                                strSpecialRateThirdToUsd = specialThirdToUsd[1];
                            }
                            String[] specialThirdToPhp = ratesArray[5].split("=");
                            if (specialThirdToPhp.length > 1) {
                                strSpecialRateThirdToPhp = specialThirdToPhp[1];
                            }

                            String[] urrString = ratesArray[6].split("=");
                            if (thirdToPhp.length > 1) {
                                strUrr = urrString[1];
                            }

                            BigDecimal passOnRateThirdToUsd = null;
                            BigDecimal passOnRateThirdToPhp = null;
                            BigDecimal passOnRateUsdToPhp = null;
                            BigDecimal specialRateThirdToUsd = null;
                            BigDecimal specialRateThirdToPhp = null;
                            BigDecimal specialRateUsdToPhp = null;
                            BigDecimal urr = null;

                            if (strPassOnRateThirdToUsd != null && !strPassOnRateThirdToUsd.equals("")) {
                                passOnRateThirdToUsd = new BigDecimal(strPassOnRateThirdToUsd.trim());
                            }
                            if (strPassOnRateThirdToPhp != null && !strPassOnRateThirdToPhp.equals("")) {
                                passOnRateThirdToPhp = new BigDecimal(strPassOnRateThirdToPhp.trim());
                            }
                            if (strPassOnRateUsdToPhp != null && !strPassOnRateUsdToPhp.equals("")) {
                                passOnRateUsdToPhp = new BigDecimal(strPassOnRateUsdToPhp.trim());
                            }

                            if (strSpecialRateThirdToUsd != null && !strSpecialRateThirdToUsd.equals("")) {
                                specialRateThirdToUsd = new BigDecimal(strSpecialRateThirdToUsd.trim());
                            }
                            if (strSpecialRateThirdToPhp != null && !strSpecialRateThirdToPhp.equals("")) {
                                specialRateThirdToPhp = new BigDecimal(strSpecialRateThirdToPhp.trim());
                            }
                            if (strSpecialRateUsdToPhp != null && !strSpecialRateUsdToPhp.equals("")) {
                                specialRateUsdToPhp = new BigDecimal(strSpecialRateUsdToPhp.trim());
                            }

                            if (strUrr != null && !strUrr.equals("")) {
                                urr = new BigDecimal(strUrr.trim());
                            }

                            switch (paymentInstrumentType) {

                                case CASA:
                                    // For Product charge, only MD and AP are used to pay; AR is not.
                                    referenceNumber = (String) productPaymentMap.get("accountNumber");

                                    tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                            passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);

                                    break;

                                case MC_ISSUANCE:
                                    referenceNumber = (String) productPaymentMap.get("tradeSuspenseAccount");

                                    tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                            passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);
                                    break;
                            }

                            System.out.println("\n");
                            System.out.println("paymentInstrumentType = " + paymentInstrumentType);
                            System.out.println("referenceNumber = " + referenceNumber);

                            tempDetails.add(tempDetail);
                        }
                        System.out.println("\n");

                        // Persist payment
                        // 1) Remove all items first
                        payment.deleteAllPaymentDetails();
                        // 2) Add new PaymentDetails
                        payment.addNewPaymentDetails(tempDetails);
                        // 3) Use saveOrUpdate
                        paymentRepository.saveOrUpdate(payment);

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("error in updateTradeService(ServiceInstructionUpdatedEvent)",e);
                    }
                } else if (documentClass.equals(DocumentClass.DP)) {
                	//Removed.. not needed for DP anymore.
                    //System.out.println("helloasd");
//                    if (tradeService.getDetails().get("settleFlag") != null && "Y".equals(tradeService.getDetails().get("settleFlag"))) {
//                        System.out.println("setting no payment required #4");
//                        tradeService.setAsNoPaymentRequired();
//
//                        tradeServiceRepository.merge(tradeService);
//                    } else {
                        tradeService.unPay();

                        tradeServiceRepository.merge(tradeService);
//                    }
                } else if (documentClass.equals(DocumentClass.BP)) {
                    System.out.println(parameterMap.get("exlcAdviseNumber"));
                    System.out.println(parameterMap.get("exlcAdviseNumber") != null);
                    System.out.println(parameterMap.get("paymentMode"));
                    System.out.println(parameterMap.get("paymentMode").equals("LC"));

//                    Map tsDetails = tradeService.getDetails();

                    System.out.println("hello parameter " + parameterMap.get("negotiationNumber"));

                    if (parameterMap.get("negotiationNumber") == null &&
                            DocumentClass.BP.equals(tradeService.getDocumentClass()) &&
                            DocumentType.FOREIGN.equals(tradeService.getDocumentType()) &&
                            ServiceType.NEGOTIATION.equals(tradeService.getServiceType())) {

                        System.out.println("nego is null");

                        Map tsDetails = tradeService.getDetails();

                        tsDetails.remove("negotiationNumber");

                        tradeService.setDetails(tsDetails);

                        tradeServiceRepository.merge(tradeService);
                    }


                    if ((parameterMap.get("exlcAdviseNumber") != null && !parameterMap.get("exlcAdviseNumber").toString().trim().equals("")) && parameterMap.get("paymentMode").equals("LC")) {
                        System.out.println("exlcAdviseNumber : " + parameterMap.get("exlcAdviseNumber"));

                        if (!(parameterMap.get("exlcAdviseNumber").equals(ets.getDetails().get("adviseNumber")))) {
                            ExportAdvising exportAdvising = (ExportAdvising) tradeProductRepository.load(new DocumentNumber((String) parameterMap.get("exlcAdviseNumber")));

                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                            Map details = ets.getDetails();

                            System.out.println("details : " + details);

                            if (details != null) {
                                System.out.println("updating ets details...");

                                details.put("adviseNumber", exportAdvising.getDocumentNumber().toString());
                                details.put("lcNumber", exportAdvising.getLcNumber().toString());
                                details.put("lcIssueDate", sdf.format(exportAdvising.getLcIssueDate()));
                                details.put("lcType", exportAdvising.getLcType());

                                if (exportAdvising.getLcType().equals(LCType.REGULAR)) {
                                    details.put("lcTenor", exportAdvising.getLcTenor().toString());
                                } else {
                                    details.remove("lcTenor");
                                }

                                if (exportAdvising.getLcTenor() != null) {
                                    details.put("usanceTerm", exportAdvising.getUsanceTerm().toString());
                                }

                                details.put("lcCurrency", exportAdvising.getLcCurrency().toString());
                                details.put("lcAmount", exportAdvising.getLcAmount().toString());
                                details.put("lcExpiryDate", sdf.format(exportAdvising.getLcExpiryDate()));
                                details.put("issuingBankCode", exportAdvising.getIssuingBank());
                                details.put("issuingBankAddress", exportAdvising.getIssuingBankAddress());
                                details.put("reimbursingBankCode", exportAdvising.getReimbursingBank());

                                ets.updateDetails(details, userActiveDirectoryId);
                                serviceInstructionRepository.merge(ets);

                                Map tsDetails = tradeService.getDetails();

                                tsDetails.put("adviseNumber", exportAdvising.getDocumentNumber().toString());
                                tsDetails.put("lcNumber", exportAdvising.getLcNumber().toString());
                                tsDetails.put("lcIssueDate", sdf.format(exportAdvising.getLcIssueDate()));
                                tsDetails.put("lcType", exportAdvising.getLcType());

                                if (exportAdvising.getLcType().equals(LCType.REGULAR)) {
                                    tsDetails.put("lcTenor", exportAdvising.getLcTenor().toString());
                                } else {
                                    tsDetails.remove("lcTenor");
                                }

                                if (exportAdvising.getLcTenor() != null) {
                                    tsDetails.put("usanceTerm", exportAdvising.getUsanceTerm().toString());
                                }
                                tsDetails.put("lcCurrency", exportAdvising.getLcCurrency().toString());
                                tsDetails.put("lcAmount", exportAdvising.getLcAmount().toString());
                                tsDetails.put("lcExpiryDate", sdf.format(exportAdvising.getLcExpiryDate()));
                                tsDetails.put("issuingBankCode", exportAdvising.getIssuingBank());
                                tsDetails.put("issuingBankAddress", exportAdvising.getIssuingBankAddress());
                                tsDetails.put("reimbursingBankCode", exportAdvising.getReimbursingBank());

                                tradeService.setDetails(tsDetails);

                                tradeServiceRepository.merge(tradeService);

                                System.out.println("eto na ang bago : " + ets.getDetails().get("adviseNumber"));
                            }
                        }
                    } else {
                    	
                        Map details = ets.getDetails();

                        if (details != null) {
                        	
                        	// Why remove?? prevents saving of details in EBP and DBP details Tab
//                            details.remove("adviseNumber");
//                            details.remove("lcNumber");
//                            details.remove("lcIssueDate");
//                            details.remove("lcType");
//                            details.remove("lcTenor");
//                            details.remove("usanceTerm");
//                            details.remove("lcCurrency");
//                            details.remove("lcAmount");
//                            details.remove("lcExpiryDate");
//                            details.remove("issuingBankCode");
//                            details.remove("issuingBankAddress");
//                            details.remove("reimbursingBankCode");

                            ets.updateDetails(details, userActiveDirectoryId);
                            serviceInstructionRepository.merge(ets);

                            Map tsDetails = tradeService.getDetails();
                            
                            // Why remove?? prevents saving of details in EBP and DBP details Tab
//                            tsDetails.remove("adviseNumber");
//                            tsDetails.remove("lcNumber");
//                            tsDetails.remove("lcIssueDate");
//                            tsDetails.remove("lcType");
//                            tsDetails.remove("lcTenor");
//                            tsDetails.remove("usanceTerm");
//                            tsDetails.remove("lcCurrency");
//                            tsDetails.remove("lcAmount");
//                            tsDetails.remove("lcExpiryDate");
//                            tsDetails.remove("issuingBankCode");
//                            tsDetails.remove("issuingBankAddress");
//                            tsDetails.remove("reimbursingBankCode");

                            tradeService.setDetails(tsDetails);

                            tradeServiceRepository.merge(tradeService);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error in updateTradeService(ServiceInstructionUpdatedEvent)",e);
        }
    }

    @EventListener
    public void updateTradeService(TradeServiceUpdatedEvent tradeServiceUpdatedEvent) {

        System.out.println("\n INSIDE updateTradeService(TradeServiceUpdatedEvent)\n");

        try {

            TradeServiceStatus tradeServiceStatus = tradeServiceUpdatedEvent.getTradeServiceStatus();
            UserActiveDirectoryId userActiveDirectoryId = tradeServiceUpdatedEvent.getUserActiveDirectoryId();
            TradeServiceId tradeServiceId = tradeServiceUpdatedEvent.getTradeServiceId();

            Map<String, Object> parameterMap = tradeServiceUpdatedEvent.getParameterMap();

            System.out.println(parameterMap.get("documentClass"));
            System.out.println(parameterMap.get("serviceType"));
            System.out.println("Before Removing: \n" + parameterMap);

            // Needed for Advance Copy of LC or Draft of LC Opening
            if (parameterMap.get("documentClass") != null && parameterMap.get("serviceType") != null){
	            if(parameterMap.get("documentClass").toString().toUpperCase().equals(DocumentClass.LC) && parameterMap.get("serviceType").toString().toUpperCase().equals(ServiceType.OPENING)){
	            	// We Remove parameters in parameterMap
	            	System.out.println("REMOVING PARAMETERS NOT NEEDED.");
	            	parameterMap.remove("cifNumber");
	            	parameterMap.remove("cifName");
	            	parameterMap.remove("accountOfficer");
	            	parameterMap.remove("ccbdBranchUnitCode");
	            	parameterMap.remove("currency");
	            	parameterMap.remove("amount");
	            	parameterMap.remove("marineInsurance");
	            	parameterMap.remove("advanceCorresChargesFlag");
	            	parameterMap.remove("cwtFlag");
	            	parameterMap.remove("mainCifNumber");
	            	parameterMap.remove("mainCifName");
	            	parameterMap.remove("expiryDate");
	            	parameterMap.remove("tenor");
	            	parameterMap.remove("usancePeriod");
	            	parameterMap.remove("cramFlag");
	            	parameterMap.remove("facilityType");
	            	
	            	if (parameterMap.get("documentSubType1").toString().toUpperCase().equals(DocumentSubType1.REGULAR) &&
	            			parameterMap.get("documentSubType1").toString().toUpperCase().equals(DocumentSubType1.CASH) ){
	            		parameterMap.remove("facilityId");
	            	}
	            }
            }
            
            System.out.println("After Removing: \n" + parameterMap);
            
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
            tradeService.updateDetails(parameterMap, userActiveDirectoryId);
            
            ServiceType serviceType = tradeService.getServiceType();
            DocumentClass documentClass = tradeService.getDocumentClass();
            DocumentType documentType = tradeService.getDocumentType();
            DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
            DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();

            Map<String, Object> negotiationDiscrepancyDetails = tradeService.getDetails();
            
//            if (tradeServiceStatus != null && (tradeServiceStatus.equals(TradeServiceStatus.APPROVED) || tradeServiceStatus.equals(TradeServiceStatus.POSTED) || tradeServiceStatus.equals(TradeServiceStatus.PRE_APPROVED))) {
            if (tradeServiceStatus != null && (tradeServiceStatus.equals(TradeServiceStatus.APPROVED) || tradeServiceStatus.equals(TradeServiceStatus.POSTED))) {

                // This should be true for APPROVED and POSTED
                TradeServiceTaggedEvent tsTaggedEvent = (TradeServiceTaggedEvent)tradeServiceUpdatedEvent;

                // For LC
                if (documentClass.equals(DocumentClass.LC)) {

                    switch (serviceType) {

                        case OPENING:
                            System.out.println("\n^^^^^^^^^^^^^^^^ OPENING!\n");

                            tradeService.getDetails().put("reimbursingBankNameAndAddress", tradeService.getDetails().get("reimbursingBankName"));
                            tradeService.getDetails().put("reimbursingAccountType", tradeService.getDetails().get("accountType"));
                            // Create and saveOrUpdate LC.
                            LetterOfCredit lc = TradeProductService.createLetterOfCredit(tradeService.getDocumentNumber(), tradeService.getDetails());

                            System.out.println("^^^^^^^^^^^^^^^^ lc.getDocumentNumber() = " + lc.getDocumentNumber() + "\n");

                            lc.updateStatus(TradeProductStatus.OPEN);

                            String lastTransactionOpening = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                            lc.updateLastTransaction(lastTransactionOpening);


                            // copies required documents from trade service to letter of credit
                            List<LcRequiredDocument> lcRequiredDocumentList = new ArrayList<LcRequiredDocument>();

                            for (RequiredDocument requiredDocument : tradeService.getRequiredDocument()) {
                                Map<String, Object> requiredDocumentFields = requiredDocument.getFields();

                                DocumentCode documentCode = null;

                                RequiredDocumentType requiredDocumentType = RequiredDocumentType.valueOf((String) requiredDocumentFields.get("requiredDocumentType"));

                                if (requiredDocumentType.equals(RequiredDocumentType.DEFAULT)) {
                                    documentCode = new DocumentCode((String) requiredDocumentFields.get("documentCode"));
                                }

                                LcRequiredDocument lcRequiredDocument = new LcRequiredDocument(documentCode, (String) requiredDocumentFields.get("description"), requiredDocumentType);

                                lcRequiredDocumentList.add(lcRequiredDocument);
                            }

                            lc.addRequiredDocuments(lcRequiredDocumentList);

                            // copies instructions to bank from trade service to letter of credit
                            List<LcInstructionToBank> lcInstructionToBankList = new ArrayList<LcInstructionToBank>();

                            for (InstructionToBank instructionToBank : tradeService.getInstructionToBank()) {
                                Map<String, Object> instructionsToBankFields = instructionToBank.getFields();

                                InstructionToBankCode instructionToBankCode = new InstructionToBankCode((String) instructionsToBankFields.get("instructionToBankCode"));

                                LcInstructionToBank lcInstructionToBank = new LcInstructionToBank(instructionToBankCode, (String) instructionsToBankFields.get("instruction"));

                                lcInstructionToBankList.add(lcInstructionToBank);
                            }

                            lc.addInstructionToBank(lcInstructionToBankList);

                            // copies additional conditions from trade service to letter of credit
                            List<LcAdditionalCondition> lcAdditionalConditionList = new ArrayList<LcAdditionalCondition>();

                            for (AdditionalCondition additionalCondition : tradeService.getAdditionalCondition()) {
                                Map<String, Object> additionalConditionFields = additionalCondition.getFields();

                                ConditionCode conditionCode = null;

                                ConditionType conditionType = ConditionType.valueOf((String) additionalConditionFields.get("conditionType"));

                                if (conditionType.equals(ConditionType.DEFAULT)) {
                                    conditionCode = new ConditionCode((String) additionalConditionFields.get("conditionCode"));
                                }

                                LcAdditionalCondition lcAdditionalCondition = new LcAdditionalCondition(conditionType, conditionCode, (String) additionalConditionFields.get("condition"));

                                lcAdditionalConditionList.add(lcAdditionalCondition);
                            }

                            lc.addAdditionalCondition(lcAdditionalConditionList);
                            
                            
                            tradeProductRepository.persist(lc);
                        	
                            System.out.println(">>>>>>>>>>>>>> Persisted LetterOfCredit!");

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            // Fire LC Created Event for cash opening only
//                            if(documentSubType1.equals(DocumentSubType1.CASH)) {
                            	LetterOfCreditCreatedEvent letterOfCreditCreatedEvent = new LetterOfCreditCreatedEvent(tradeService, lc, tsTaggedEvent.getGltsNumber());
                                eventPublisher.publish(letterOfCreditCreatedEvent);
                                
                                System.out.println(">>>>>>>>>>>>>> Published LetterOfCreditEvent for AMLA!");
//                            }
                            break;

                        case NEGOTIATION:

                            System.out.println("\n^^^^^^^^^^^^^^^^ NEGOTIATION!\n");

                            LetterOfCredit lcNegotiation = (LetterOfCredit) tradeProductRepository.load(new DocumentNumber((String) tradeService.getDetails().get("lcNumber")));
                            BigDecimal originalOutstandingBalance = lcNegotiation.getOutstandingBalance();
                            BigDecimal originalTotalNegotiatedAmount = lcNegotiation.getTotalNegotiatedAmountResult();
                            BigDecimal originalCashAmount = lcNegotiation.getCashAmount();
                            BigDecimal originalTotalNegotiatedCashAmount = lcNegotiation.getTotalNegotiatedCashAmount();
                        	BigDecimal lcAmount = lcNegotiation.getCurrentAmount();

                            // Create Negotiation through LC
                            // The NegotiationNumber was previously created during TradeService creation
                            String negotiationNumber = (String) tradeService.getDetails().get("negotiationNumber");
                            Map<String, Object> negotiationDetails = tradeService.getDetails();
                            //TODO INSERT LOAN MATURITY DATE

                            Payment paymentNego = (Payment) paymentRepository.load(tradeService.getTradeServiceId());
                            System.out.println("PAYMENT = " + paymentNego);
                            if(paymentNego !=null){
                                if(paymentNego.hasTrLoan()){
                                    PaymentDetail pd = paymentNego.getTRLoanPayment();
                                    negotiationDetails.put("loanMaturityDate",pd.getLoanMaturityDate());
                                }
                                if(paymentNego.hasUaLoan()){
                                    PaymentDetail pd = paymentNego.getUALoanPayment();
                                    negotiationDetails.put("loanMaturityDate",pd.getLoanMaturityDate());
                                }
                                paymentRepository.saveOrUpdate(paymentNego);
                            }


                            // Reinstatement
                            if (lcNegotiation.getStatus().equals(TradeProductStatus.EXPIRED) && tradeService.isForReinstatement()) {
                                lcNegotiation.reinstate();
                            }

                            
                            lcNegotiation.negotiate(negotiationNumber, negotiationDetails);
                            
                            // updates shipment count every negotations made
//                            lcNegotiation.negotiate();

                            String lastTransactionNegotiation = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                            System.out.println("=================orayt=================" + lastTransactionNegotiation + "=================orayt=================");
                            
                            lcNegotiation.updateLastTransaction(lastTransactionNegotiation);

                            ICNumber icNumber = new ICNumber((String) negotiationDiscrepancyDetails.get("icNumber"));
                            if (!icNumber.toString().equalsIgnoreCase("")) {
                            	
                            	LCNegotiationDiscrepancy lcDiscrepancy = lcNegotiation.getNegotiationDiscrepancy(icNumber);
                            	BigDecimal icCashAmount = lcDiscrepancy.getCashAmount();
                            	BigDecimal newTotalNegotiatedCashAmount = originalTotalNegotiatedCashAmount;
                            	BigDecimal newTotalNegotiatedAmount = originalTotalNegotiatedAmount;
                            	BigDecimal newOsBalance = originalOutstandingBalance;
                            	
                            	if (icCashAmount.add(originalTotalNegotiatedCashAmount).compareTo(originalCashAmount) > 0) {
                            		newTotalNegotiatedCashAmount = originalCashAmount;
                            		newOsBalance = originalOutstandingBalance.subtract(originalCashAmount.subtract(originalTotalNegotiatedCashAmount));
                            	} else {
                            		newTotalNegotiatedCashAmount = originalTotalNegotiatedCashAmount.add(icCashAmount);
                            		newOsBalance = originalOutstandingBalance.subtract(icCashAmount);
                            	}
                            	
                            	if (originalTotalNegotiatedAmount.add(icCashAmount).compareTo(lcAmount) > 0) {
                            		newTotalNegotiatedAmount = lcAmount;
                            	} else {
                            		newTotalNegotiatedAmount = originalTotalNegotiatedAmount.add(icCashAmount);
                            	}
                            	
                            	lcNegotiation.setTotalNegotiatedCashAmount(newTotalNegotiatedCashAmount);
                            	lcNegotiation.setTotalNegotiatedAmount(newTotalNegotiatedAmount);
                            	lcNegotiation.setOutstandingBalance(newOsBalance);               		
                            	
                            	lcNegotiation.validateForClosure();
                            }
                            
                            lcNegotiation.setLastModifiedUpdated("NEGOTIATION");
                            tradeProductRepository.mergeFlush(lcNegotiation); 
                            
                            // Fire LC Negotiation Created Event
                            try{
                                System.out.println("=============LetterOfCredit lcNegotiation DETAILS=============");
                                System.out.println("1: " + lcNegotiation.getAdditionalAmountsCovered() + ", 2: "+lcNegotiation.getAdviseThroughBankFlag());
                                System.out.println("3: " + lcNegotiation.getAdviseThroughBankIdentifierCode() + ", 4: "+lcNegotiation.getAdviseThroughBankLocation());
                                System.out.println("4: " + lcNegotiation.getAdviseThroughBankNameAndAddress() + ", 6: "+lcNegotiation.getAdvisingBankCode());
                                System.out.println("7: " + lcNegotiation.getAllocationUnitCode() + ", 8: "+lcNegotiation.getApplicableRules());
                                System.out.println("7: " + lcNegotiation.getApplicantAddress() + ", 8: "+lcNegotiation.getApplicantCifNumber());
                                System.out.println("9: " + lcNegotiation.getApplicantName() + ", 10: "+lcNegotiation.getAvailableBy());
                                System.out.println("11: " + lcNegotiation.getAvailableWith() + ", 12: "+lcNegotiation.getAvailableWithFlag());
                                System.out.println("13: " + lcNegotiation.getBeneficiaryAddress() + ", 14: "+lcNegotiation.getBeneficiaryName());
                                System.out.println("15: " + lcNegotiation.getBspCountryCode() + ", 16: "+lcNegotiation.getCifName());
                                System.out.println("17: " + lcNegotiation.getCifNumber() + ", 18: "+lcNegotiation.getConfirmationInstructionsFlag());
                                System.out.println("19: " + lcNegotiation.getConfirmingBankCode() + ", 20: "+lcNegotiation.getDeferredPaymentDetails());
                                System.out.println("=============LetterOfCredit lcNegotiation DETAILS=============");
                            }catch(Exception e){
                            	System.out.println(e);
                            	System.out.println("=============LetterOfCredit lcNegotiation DETAILS FAILED=============");
                            	
                            }
                            try{
                            System.out.println("==========================DETAILS OF TRADESERVICE==========================");
                            System.out.println("1: " + tradeService.getAccountOfficer() + "2: " + tradeService.getAdditionalCondition()+ "3: " + tradeService.getAllocationUnitCode());
                            System.out.println("4: " + tradeService.getCifName() + "5: " + tradeService.getCifNumber()+  "6: " + tradeService.getFacilityId());
                            System.out.println("7: " + tradeService.getFacilityType() + "8: " +tradeService.getMainCifName()+  "9: " + tradeService.getMainCifNumber());
                            System.out.println("10: " + tradeService.getProcessingUnitCode()+ "11: " +tradeService.getTradeServiceDetails()+  "12: " + tradeService.getAdditionalCondition());
                            System.out.println("13: " + tradeService.getAttachments() + "14: " +tradeService.getChargesCurrency()+  "15: " + tradeService.getChargesSummary());
                            System.out.println("16: " + tradeService.getCreatedBy() + "17: " +tradeService.getDetails()+  "18: " + tradeService.getDocumentClass());
                            System.out.println("19: " + tradeService.getDocumentNumber() + "20: " +tradeService.getDocumentSubType1()+  "21: " + tradeService.getDocumentSubType2());
                            System.out.println("22: " + tradeService.getDocumentType() + "23: " +tradeService.getIndemnityNumber()+  "24: " + tradeService.getInstructionToBank());
                            System.out.println("25: " + tradeService.getLastUser() + "26: " +tradeService.getModifiedDate()+  "27: " + tradeService.getOtherChargesDetails());
                            System.out.println("28: " + tradeService.getPassOnRateThirdToPhp() + "29: " +tradeService.getPassOnRateThirdToPhpServiceCharge()+  "30: " + tradeService.getPassOnRateThirdToUSD());
                            System.out.println("31: " + tradeService.getPassOnRateThirdToUsdServiceCharge() + "32: " +tradeService.getPassOnRateUsdToPhp()+  "33: " + tradeService.getPassOnRateUsdToPhpServiceCharge());
                            System.out.println("34: " + tradeService.getPassOnUrr() + "35: " +tradeService.getSpecialRateUrrServiceCharge()+  "36: " + tradeService.getPaymentStatus());
                            System.out.println("37: " + tradeService.getPreparedBy() + "38: " +tradeService.getProcessDate()+  "39: " + tradeService.getProductChargeAmount());
                            System.out.println("40: " + tradeService.getProductChargeCurrency() + "41: " +tradeService.getProductCollectibleDetails()+  "42: " + tradeService.getProductRefundDetails());
                            System.out.println("43: " + tradeService.getRequiredDocument() + "44: " +tradeService.getSavedRates()+  "45: " + tradeService.getServiceCharge());
                            System.out.println("46: " + tradeService.getServiceChargeCurrency() + "47: " +tradeService.getServiceCharges()+  "48: " + tradeService.getServiceChargesCurrency());
                            System.out.println("49: " + tradeService.getServiceInstructionId() + "50: " +tradeService.getServiceType()+  "51: " + tradeService.getSpecialRateThirdToPhp());
                            System.out.println("52: " + tradeService.getSpecialRateThirdToPhpServiceCharge()+ "53: " +tradeService.getSpecialRateThirdToPhpServiceCharge()+  "54: " + tradeService.getSpecialRateThirdToUsd());
                            System.out.println("55: " + tradeService.getSpecialRateThirdToUsdServiceCharge()+ "56: " +tradeService.getSpecialRateUrr()+  "57: " + tradeService.getSpecialRateUrrServiceCharge());
                            System.out.println("58: " + tradeService.getSpecialRateUsdToPhp()+ "59: " +tradeService.getSpecialRateUsdToPhpServiceCharge()+  "60: " + tradeService.getStatus());
                            System.out.println("61: " + tradeService.getSwiftCharge()+ "62: " +tradeService.getTotalServiceChargesAmount()+  "63: " + tradeService.getTotalServiceChargesAmount());
                            System.out.println("64: " + tradeService.getTradeProductNumber()+ "65: " +tradeService.getTradeServiceId()+  "66: " + tradeService.getTradeServiceReferenceNumber());
                            System.out.println("67: " + tradeService.getTransmittalLetter()+ "68: " +tradeService.getUserActiveDirectoryId());
                            System.out.println("==========================DETAILS OF TRADESERVICE==========================");
                            
                            }catch(Exception e){
                            	System.out.println(e);
                            	System.out.println("==========================DETAILS OF TRADESERVICE FAILED==========================");
                            	
                            }
                            
                            // ITRS
                            // Document number is in getTradeProductNumber. Negotiation Number is in tradeService.getDocumentNumber
                            DocumentNumber dn = new DocumentNumber(tradeService.getTradeProductNumber().toString()); 
                            updateItrsFields(dn, tradeService.getDetails());

                            LCNegotiationCreatedEvent lcNegotiationCreatedEvent = new LCNegotiationCreatedEvent(tradeService, lcNegotiation, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(lcNegotiationCreatedEvent);
                            
                            
                            System.out.println(">>>>>>>>>>>>>> Persisted LC Negotiation!");

                            break;

                        case NEGOTIATION_DISCREPANCY:

                            System.out.println("\n^^^^^^^^^^^^^^^^ NEGOTIATION DISCREPANCY!");

                            LetterOfCredit letterOfCredit = (LetterOfCredit) tradeProductRepository.load(tradeService.getDocumentNumber());
                            TradeProduct tradeProduct = tradeProductRepository.load(new DocumentNumber(tradeService.getDocumentNumber().toString()));
                           
                            if (letterOfCredit.getStatus().equals(TradeProductStatus.EXPIRED) && tradeService.isForReinstatement()) {
                            	letterOfCredit.reinstate();
                            }
                            
                            BigDecimal negotiationAmount = null;
                            String negotiationAmountStr = (String) negotiationDiscrepancyDetails.get("negotiationAmount");
                            if (negotiationAmountStr != null && !negotiationAmountStr.equals("")) {
                                negotiationAmount = new BigDecimal(negotiationAmountStr);
                            }

                            Currency negotiationCurrency = null;
                            String negotiationCurrencyStr = (String) negotiationDiscrepancyDetails.get("negotiationCurrency");
                            if (negotiationCurrencyStr != null && !negotiationCurrencyStr.equals("")) {
                                negotiationCurrency = Currency.getInstance(negotiationCurrencyStr);
                            }

                            String negotiationBank = (String) negotiationDiscrepancyDetails.get("negotiationBank");
                            String negotiationBankRefNumber = (String) negotiationDiscrepancyDetails.get("negotiatingBanksReferenceNumber");
                            String senderToReceiverInformation = (String) negotiationDiscrepancyDetails.get("senderToReceiverInformation");

                            Boolean expiredLc = null;
                            if (negotiationDiscrepancyDetails.get("expiredLcSwitch") != null && ((String) negotiationDiscrepancyDetails.get("expiredLcSwitch")).equals("on")) {
                                expiredLc = Boolean.TRUE;
                            }

                            Boolean overdrawnFor = null;
                            BigDecimal overdrawnAmount = null;
                            if (negotiationDiscrepancyDetails.get("overdrawnForAmountSwitch") != null && ((String) negotiationDiscrepancyDetails.get("overdrawnForAmountSwitch")).equals("on")) {
                                overdrawnFor = Boolean.TRUE;
                                if (negotiationDiscrepancyDetails.get("overdrawnForAmount") != null && !((String) negotiationDiscrepancyDetails.get("overdrawnForAmount")).equals("")) {
                                    overdrawnAmount = new BigDecimal((String) negotiationDiscrepancyDetails.get("overdrawnForAmount"));
                                }
                            }

                            Boolean descriptionOfGoodsNotPerLc = null;
                            if (negotiationDiscrepancyDetails.get("descriptionOfGoodsNotPerLcSwitch") != null && ((String) negotiationDiscrepancyDetails.get("descriptionOfGoodsNotPerLcSwitch")).equals("on")) {
                                descriptionOfGoodsNotPerLc = Boolean.TRUE;
                            }

                            Boolean documentsNotPresented = null;
                            if (negotiationDiscrepancyDetails.get("documentsNotPresentedSwitch") != null && ((String) negotiationDiscrepancyDetails.get("documentsNotPresentedSwitch")).equals("on")) {
                                documentsNotPresented = Boolean.TRUE;
                            }

                            Boolean others = null;
                            String othersNarrative = null;
                            if (negotiationDiscrepancyDetails.get("othersSwitch") != null && ((String) negotiationDiscrepancyDetails.get("othersSwitch")).equals("on")) {
                                others = Boolean.TRUE;
                                if (negotiationDiscrepancyDetails.get("others") != null && ((String) negotiationDiscrepancyDetails.get("others")).equals("")) {
                                    othersNarrative = (String) negotiationDiscrepancyDetails.get("others");
                                }
                            }
                            LCNegotiationDiscrepancy lcDiscrepancy = new LCNegotiationDiscrepancy(
                                    tradeService.getDocumentNumber(),
                                    new ICNumber((String) negotiationDiscrepancyDetails.get("icNumber")),
                                    negotiationAmount,
                                    negotiationCurrency,
                                    negotiationBank,
                                    negotiationBankRefNumber,
                                    senderToReceiverInformation,
                                    expiredLc,
                                    overdrawnFor,
                                    overdrawnAmount,
                                    descriptionOfGoodsNotPerLc,
                                    documentsNotPresented,
                                    others,
                                    othersNarrative 
                            );
                              
                            lcDiscrepancy.setLastModifiedDate(new Date());
                            lcDiscrepancy.setReasonForCancellation(null);
                            
                            String icDateStr = (String) negotiationDiscrepancyDetails.get("icDate");
                            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                            if( icDateStr != null ){
                            	try{
                            		Date icDate = (Date)formatter.parse(icDateStr);  
                            		lcDiscrepancy.setIcDate(icDate);
                            	} catch (ParseException e) {
                            		System.err.println("Cannot format String to Date because Date is empty or null value. See stacktrace below.");
                            		e.printStackTrace(System.err);
                            	}
                            } else {
                            	lcDiscrepancy.setIcDate(null);
                            }
                            
                            BigDecimal osBalance = letterOfCredit.getOutstandingBalance();
                            BigDecimal regularPart = BigDecimal.ZERO;
                            BigDecimal cashPart = BigDecimal.ZERO;
                            BigDecimal osCashAmount = letterOfCredit.getCashAmount().subtract(letterOfCredit.getTotalNegotiatedCashAmount());
                            BigDecimal osRegularAmount = osBalance.subtract(osCashAmount);   
                            BigDecimal totalCashIcAmount = letterOfCredit.getOutstandingCashIcAmounts();
                            
                            
                            if (letterOfCredit.getType().equals(LCType.REGULAR) && !letterOfCredit.getCashFlagForNull()) {
                            	System.out.println("NEGOTIATION_DISCREPANCY : LC is Regular");
                            	regularPart = (negotiationAmount.compareTo(osBalance) >= 0) ? osBalance : negotiationAmount;                               	
                            
                            } else if (letterOfCredit.getType().equals(LCType.REGULAR) && letterOfCredit.getCashFlagForNull()) {
                            	System.out.println("NEGOTIATION_DISCREPANCY : LC is Partial Cash");
                            	if (osCashAmount.compareTo(totalCashIcAmount) > 0) {
                            		osCashAmount = osCashAmount.subtract(totalCashIcAmount);                            		
                            		cashPart = (osCashAmount.compareTo(negotiationAmount) >= 0) ? negotiationAmount : osCashAmount;
                            	}
                            	
                            	BigDecimal tempRegularPart = negotiationAmount.subtract(cashPart);   
                            	if (tempRegularPart.compareTo(BigDecimal.ZERO) > 0) {
                            		regularPart = (osRegularAmount.compareTo(tempRegularPart) > 0) ? tempRegularPart : osRegularAmount;
                            	}
                            	
                            } else if (letterOfCredit.getType().equals(LCType.CASH)) {
                            	System.out.println("NEGOTIATION_DISCREPANCY : LC is Full Cash");
                            	if (osCashAmount.compareTo(totalCashIcAmount) > 0) {
                            		osCashAmount = osCashAmount.subtract(totalCashIcAmount);                            		
                            		cashPart = (osCashAmount.compareTo(negotiationAmount) >= 0) ? negotiationAmount : osCashAmount;
                            	}
                            	
                            }
                            
                            osBalance = osBalance.subtract(regularPart);
                    		letterOfCredit.setOutstandingBalance(osBalance); 
                        	letterOfCredit.addTotalNegotiatedAmount(regularPart);  
                        	
                            lcDiscrepancy.setRegularAmount(regularPart);
                            lcDiscrepancy.setCashAmount(cashPart);
                            
                            letterOfCredit.addNegotiationDiscrepancy(lcDiscrepancy); 
                            
                            lastTransactionNegotiation = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                            letterOfCredit.updateLastTransaction(lastTransactionNegotiation);                            
                          
                            
//                            BigDecimal osBalance = new BigDecimal((String) negotiationDiscrepancyDetails.get("outstandingBalance"));
//                            lcDiscrepancy.setReasonForCancellation(null);
//                            lcDiscrepancy.setOutstandingBalance(osBalance);
//                            
//                            BigDecimal cashAmountForNd = null;
//                            BigDecimal totalNegotiatedCashAmount = letterOfCredit.getTotalNegotiatedCashAmount();
//                            cashAmountForNd = letterOfCredit.getCashAmount(); 
//                            totalNegotiatedCashAmount = letterOfCredit.getTotalNegotiatedCashAmount(); 
//                            BigDecimal difference = ((cashAmountForNd.subtract(totalNegotiatedCashAmount)).compareTo(BigDecimal.ZERO) <= 0) ? BigDecimal.ZERO : ((cashAmountForNd.subtract(totalNegotiatedCashAmount)).compareTo(negotiationAmount) >= 0) ? negotiationAmount : cashAmountForNd.subtract(totalNegotiatedCashAmount);
//                            BigDecimal differenceBd = new BigDecimal(difference.toString());
//                            lcDiscrepancy.setCashAmount(differenceBd);
//                            
//                            letterOfCredit.addNegotiationDiscrepancy(lcDiscrepancy); 
//                            
//                            BigDecimal deductedOsBalance = ((osBalance.subtract(negotiationAmount)).compareTo(BigDecimal.ZERO) > 0) ? osBalance.subtract(negotiationAmount) : BigDecimal.ZERO;
//                            BigDecimal deductedOsBalanceBd = new BigDecimal(deductedOsBalance.toString());
//                            letterOfCredit.setOutstandingBalance(deductedOsBalanceBd);
//                            
//                            BigDecimal totalNegotiatedCashAmt = ((cashAmountForNd.subtract(totalNegotiatedCashAmount)).compareTo(BigDecimal.ZERO) <= 0) ? BigDecimal.ZERO : (cashAmountForNd.subtract(totalNegotiatedCashAmount.add(negotiationAmount))).compareTo(BigDecimal.ZERO) >= 0 ? totalNegotiatedCashAmount.add(negotiationAmount) : cashAmountForNd;
//                            BigDecimal totalNegotiatedCashAmtBd = new BigDecimal(totalNegotiatedCashAmt.toString());
//                            letterOfCredit.setTotalNegotiatedCashAmount(totalNegotiatedCashAmtBd); 
//                            
//                            BigDecimal tpAmt = tradeProduct.getAmount();
//                            BigDecimal totalNegotiatedAmt = (tpAmt.subtract((letterOfCredit.getTotalNegotiatedAmountOnThis()).add(negotiationAmount))).compareTo(BigDecimal.ZERO) >= 0 ? (letterOfCredit.getTotalNegotiatedAmountOnThis()).add(negotiationAmount) : tpAmt;
//                            letterOfCredit.setTotalNegotiatedAmount(totalNegotiatedAmt);
//                            
//                            letterOfCredit.setLastModifiedDate(new Date());
//                            letterOfCredit.setLastTransaction("NEGOTIATION DISCREPANCY");
//                            
//                            lastTransactionNegotiation = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
//                            letterOfCredit.updateLastTransaction(lastTransactionNegotiation);                            
//                            letterOfCredit.negotiateDiscrepancy(icNumber.toString(),negotiationAmount);
                            
                            System.out.println("++++++++++ LC NEGO DISCREPANCY ++++++++++");
                            System.out.println("Regular = " + lcDiscrepancy.getRegularAmount());
                            System.out.println("Cash Amount = " + lcDiscrepancy.getCashAmount());
                            System.out.println("++++++++++ LETTER OF CREDIT ++++++++++");
                            System.out.println("Outstanding Balance = " + letterOfCredit.getOutstandingBalance());
                            System.out.println("Total Negotiated Cash Amount = " + letterOfCredit.getTotalNegotiatedCashAmount());
                            System.out.println("Total Negotiated Amount = " + letterOfCredit.getTotalNegotiatedAmountOnThis());
                            System.out.println("Last Modified Date = " + letterOfCredit.getLastModifiedDate());
                            System.out.println("Last Transaction = " + letterOfCredit.getLastTransaction());
                            // Persist Negotiation Discrepancy
                            tradeProductRepository.persist(letterOfCredit);

                            eventPublisher.publish(new LCNegotiationDiscrepancyCreatedEvent(tradeService, lcDiscrepancy));
                            break;

                        case AMENDMENT:

                            System.out.println("\n^^^^^^^^^^^^^^^^ AMENDMENT!");
                            System.out.println("^^^^^^^^^^^^^^^^ tradeService.getDocumentNumber() = " + tradeService.getDocumentNumber() + "\n");

                            LetterOfCredit lcAmendment = (LetterOfCredit) tradeProductRepository.load(tradeService.getDocumentNumber());
                            LetterOfCredit originalLc = new LetterOfCredit(lcAmendment);
                            lcAmendment = TradeProductService.amendLc(lcAmendment, tradeService.getDetails());

                            // updates the number of amendment fields
//                            lcAmendment.increaseAmendmentCount();

                            // Reinstatement
                            if (lcAmendment.getStatus().equals(TradeProductStatus.EXPIRED) && tradeService.isForReinstatement()) {
                                lcAmendment.reinstate();
                            }

                            boolean isMt707 = documentType.equals(DocumentType.FOREIGN) && (documentSubType1.equals(DocumentSubType1.CASH) || 
                                documentSubType1.equals(DocumentSubType1.REGULAR));
                         // copies required documents from trade service to letter of credit
                            List<LcRequiredDocument> lcAmendmentRequiredDocumentList = new ArrayList<LcRequiredDocument>();

                            for (RequiredDocument requiredDocument : tradeService.getRequiredDocument()) {
                                Map<String, Object> requiredDocumentFields = requiredDocument.getFields();

                                DocumentCode documentCode = null;

                                RequiredDocumentType requiredDocumentType = RequiredDocumentType.valueOf((String) requiredDocumentFields.get("requiredDocumentType"));

                                if (requiredDocumentType.equals(RequiredDocumentType.DEFAULT)) {
                                    documentCode = new DocumentCode((String) requiredDocumentFields.get("documentCode"));
                                }

                                LcRequiredDocument lcRequiredDocument = new LcRequiredDocument(documentCode, (String) requiredDocumentFields.get("description"), requiredDocumentType);

                                if (isMt707) {
                                    if (requiredDocument.getAmendCode() == null || !requiredDocument.getAmendCode().equalsIgnoreCase("DELETE")) {
                                        lcAmendmentRequiredDocumentList.add(lcRequiredDocument);
                                    }
                                } else {
                                    lcAmendmentRequiredDocumentList.add(lcRequiredDocument);
                                }
                            }

                            lcAmendment.amendRequiredDocuments(lcAmendmentRequiredDocumentList);

                            // copies instructions to bank from trade service to letter of credit
                            List<LcInstructionToBank> lcAmendmentInstructionToBankList = new ArrayList<LcInstructionToBank>();

                            for (InstructionToBank instructionToBank : tradeService.getInstructionToBank()) {
                                Map<String, Object> instructionsToBankFields = instructionToBank.getFields();

                                InstructionToBankCode instructionToBankCode = new InstructionToBankCode((String) instructionsToBankFields.get("instructionToBankCode"));

                                LcInstructionToBank lcInstructionToBank = new LcInstructionToBank(instructionToBankCode, (String) instructionsToBankFields.get("instruction"));

                                lcAmendmentInstructionToBankList.add(lcInstructionToBank);
                            }

                            lcAmendment.amendInstructionToBank(lcAmendmentInstructionToBankList);

                            // copies additional conditions from trade service to letter of credit
                            List<LcAdditionalCondition> lcAmendmentAdditionalConditionList = new ArrayList<LcAdditionalCondition>();

                            for (AdditionalCondition additionalCondition : tradeService.getAdditionalCondition()) {
                                Map<String, Object> additionalConditionFields = additionalCondition.getFields();

                                ConditionCode conditionCode = null;

                                ConditionType conditionType = ConditionType.valueOf((String) additionalConditionFields.get("conditionType"));

                                if (conditionType.equals(ConditionType.DEFAULT)) {
                                    conditionCode = new ConditionCode((String) additionalConditionFields.get("conditionCode"));
                                }

                                LcAdditionalCondition lcAdditionalCondition = new LcAdditionalCondition(conditionType, conditionCode, (String) additionalConditionFields.get("condition"));

                                if (isMt707) {
                                    if (additionalCondition.getAmendCode() == null || !additionalCondition.getAmendCode().equalsIgnoreCase("DELETE")) {
                                        lcAmendmentAdditionalConditionList.add(lcAdditionalCondition);
                                    }
                                } else {
                                    lcAmendmentAdditionalConditionList.add(lcAdditionalCondition);
                                }
                            }

                            lcAmendment.amendAdditionalCondition(lcAmendmentAdditionalConditionList);
                            
                            String lastTransactionAmendment = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                            lcAmendment.updateLastTransaction(lastTransactionAmendment);

                            lcAmendment.setLastModifiedUpdated("AMENDMENT");
                            
                            lcAmendment.printContent();

                            // Persist LC
                            tradeProductRepository.persist(lcAmendment);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            // Fire LC Amended Event
                            eventPublisher.publish(new LCAmendedEvent(tradeService, originalLc, lcAmendment, tsTaggedEvent.getGltsNumber()));
                            break;

                        case ADJUSTMENT:

                            System.out.println("\n^^^^^^^^^^^^^^^^ ADJUSTMENT!\n");

                            LetterOfCredit lcAdjustment = (LetterOfCredit) tradeProductRepository.load(tradeService.getDocumentNumber());

                            Map<String, Object> detailsAdj = tradeService.getDetails();

                            String partialCashSettlementFlag = (String) detailsAdj.get("partialCashSettlementFlag");
                            String facilityIdFlag = (String) detailsAdj.get("facilityIdFlag");
                            String cifNumberFlag = (String) detailsAdj.get("cifNumberFlag");
                            String mainCifNumberFlag = (String) detailsAdj.get("mainCifNumberFlag");

                            String purposeOfStandby = (String) detailsAdj.get("purposeOfStandby");
                            String standbyTagging = (String) detailsAdj.get("standbyTagging");
                            String narrative = (String) detailsAdj.get("narrative");

                            if (partialCashSettlementFlag != null && partialCashSettlementFlag.equals("partialCashSettlementEnabled")) {

                                BigDecimal cashAmount = new BigDecimal((String) detailsAdj.get("cashAmount"));
                                lcAdjustment.adjustAsCash(cashAmount);
                                if (cashAmount.compareTo(lcAdjustment.getOutstandingBalance()) >= 0){
                                	lcAdjustment.setType(LCType.CASH);
                                	lcAdjustment.setTenor(LCTenor.SIGHT);
                                	//since trade product is now cash, facility balance is no longer needed.
                                	facilityService.unearmarkAvailment(lcAdjustment.getDocumentNumber().toString());
                                }
                            }

                            if ((facilityIdFlag != null && facilityIdFlag.equals("facilityIdEnabled")) ||
                                    ((cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled")) || mainCifNumberFlag != null && mainCifNumberFlag.equals("mainCifNumberEnabled"))) {

                                if (facilityIdFlag != null && facilityIdFlag.equals("facilityIdEnabled")) {

                                    String facilityIdTo = (String) tradeService.getDetails().get("facilityIdTo");
                                    String facilityTypeTo = (String) tradeService.getDetails().get("facilityTypeTo");
                                    String facilityReferenceNumber = (String) tradeService.getDetails().get("facilityReferenceNumberTo");

                                    tradeService.setFacilityDetails(facilityIdTo, facilityTypeTo);
                                    lcAdjustment.updateFacilityDetails(facilityIdTo, facilityTypeTo, facilityReferenceNumber);
                                }

                                if (cifNumberFlag != null && cifNumberFlag.equals("cifNumberEnabled")) {

                                    String cifNumberTo = (String) tradeService.getDetails().get("cifNumberTo");
                                    String cifNameTo = (String) tradeService.getDetails().get("cifNameTo");
                                    String accountOfficerTo = (String) tradeService.getDetails().get("accountOfficerTo");
                                    String ccbdBranchUnitCodeTo = (String) tradeService.getDetails().get("ccbdBranchUnitCodeTo");
                                    String allocationUnitCodeTo = (String) tradeService.getDetails().get("allocationUnitCodeTo");
                                    String longNameTo = (String) tradeService.getDetails().get("longNameTo");
                                    String address1To = (String) tradeService.getDetails().get("address1To");
                                    String address2To = (String) tradeService.getDetails().get("address2To");
                                    String officerCodeTo = (String) tradeService.getDetails().get("officerCodeTo");
                                    String exceptionCodeTo = (String) tradeService.getDetails().get("exceptionCodeTo");

                                    tradeService.setCifDetails(cifNumberTo, cifNameTo, accountOfficerTo, ccbdBranchUnitCodeTo, allocationUnitCodeTo);
//                                    lcAdjustment.updateCifDetails(cifNumberTo, cifNameTo, accountOfficerTo, ccbdBranchUnitCodeTo);

                                    lcAdjustment.updateCifDetails(cifNumberTo, cifNameTo, accountOfficerTo, ccbdBranchUnitCodeTo, longNameTo, address1To, address2To, officerCodeTo, exceptionCodeTo);
                                }

                                if (mainCifNumberFlag != null && mainCifNumberFlag.equals("mainCifNumberEnabled")) {

                                    String mainCifNumberTo = (String) tradeService.getDetails().get("mainCifNumberTo");
                                    String mainCifNameTo = (String) tradeService.getDetails().get("mainCifNameTo");

                                    tradeService.setMainCifDetails(mainCifNumberTo, mainCifNameTo);
                                    lcAdjustment.updateMainCifDetails(mainCifNumberTo, mainCifNameTo);
                                }
                            }

                            if (purposeOfStandby != null && !purposeOfStandby.equals("")) {
                                lcAdjustment.updatePurposeOfStandby(purposeOfStandby);
                            }
                            
                            if (standbyTagging != null && !standbyTagging.equals("")) {
                            	lcAdjustment.updateStandbyTagging(standbyTagging);
                            }

                            if (narrative != null && !narrative.equals("")) {
                                lcAdjustment.updateNarrative(narrative);
                            }

                            // Reinstate
                            if (lcAdjustment.getStatus().equals(TradeProductStatus.EXPIRED) && tradeService.isForReinstatement()) {
                                lcAdjustment.reinstate();
                            }

                            String lastTransactionAdjustment = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);

                            lcAdjustment.updateLastTransaction(lastTransactionAdjustment);

                            tradeProductRepository.persist(lcAdjustment);
                            tradeServiceRepository.persist(tradeService);

                            // Fire LC Adjusted event for Cash Adjustment only
	                            LCAdjustedEvent lcAdjustedEvent = new LCAdjustedEvent(tradeService, lcAdjustment, tsTaggedEvent.getGltsNumber());
	                            eventPublisher.publish(lcAdjustedEvent);

                            System.out.println(">>>>>>>>>>>>>> Persisted LC Adjustment!");

                            break;

                        case CANCELLATION:

                            System.out.println("\n^^^^^^^^^^^^^^^^ CANCELLATION!\n");

                            LetterOfCredit lcCancellation = (LetterOfCredit) tradeProductRepository.load(tradeService.getDocumentNumber());

                            String reasonForCancellation = (String) tradeService.getDetails().get("reasonForCancellation");

                            // Cancel LC
                            lcCancellation.cancelLc(reasonForCancellation);

                            ServiceType st = tradeService.getServiceType();
                            System.out.println("SERVICE TYPE OF CANCELLATION >>> " + st.toString());

                            if (st.equals(ServiceType.AMENDMENT)) {
                                lcCancellation.decreaseAmendmentCount();
                            }

                            // For REGULAR or STANDBY LC's, un-earmark
                            if (tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR) || tradeService.getDocumentSubType1().equals(DocumentSubType1.STANDBY)) {

                                System.out.println("\n$$$ FACILITY UN-EARMARKING :::::::::");
                                System.out.println("$$$ DocumentClass = " + documentClass);
                                System.out.println("$$$ DocumentSubType1 = " + documentSubType1 + "\n");

                                Map<String, Object> details = tradeService.getDetails();

                                // If OPENING, un-earmark
                                System.out.println("\n$$$ FACILITY UN-EARMARKING (Cancellation) :::::::::\n");
                                facilityService.unearmarkAvailment(tradeService.getTradeProductNumber().toString());
                            }

                            String lastTransactionCancellation = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                            lcCancellation.updateLastTransaction(lastTransactionCancellation);
                            lcCancellation.setLastModifiedUpdated("CANCELLATION");
                            // Persist LC
                            tradeProductRepository.persist(lcCancellation);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            // Fire LC Cancelled Event
                            LCCancelledEvent lcCancelledEvent = new LCCancelledEvent(tradeService, lcCancellation, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(lcCancelledEvent);

                            System.out.println(">>>>>>>>>>>>>> Persisted LC Cancellation!");

                            break;

                        case UA_LOAN_MATURITY_ADJUSTMENT:
                            System.out.println("#########################################");
                            System.out.println("# APPROVING UA LOAN MATURITY ADJUSTMENT #");
                            System.out.println("#########################################");

                            System.out.println("TRADE SERVICE ID = " + tradeServiceId);
                            System.out.println("REFERENCE NUMBER = " + tradeService.getDetails().get("referenceNumber"));

                            String referenceNumber = (String) tradeService.getDetails().get("referenceNumber");
                            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            Date newUaloanMaturityDate = df.parse((String) tradeService.getDetails().get("loanMaturityDateTo"));

                            TradeServiceId negoTradeServiceId = new TradeServiceId((String) tradeService.getDetails().get("negoTradeServiceId"));

                            Payment payment = (Payment) paymentRepository.load(negoTradeServiceId);
                            System.out.println("PAYMENT = " + payment);
                            payment.adjustUaLoanMaturityDate(referenceNumber, newUaloanMaturityDate);

                            paymentRepository.saveOrUpdate(payment);
                            
                            LetterOfCredit lcUaMat = (LetterOfCredit) tradeProductRepository.load(new DocumentNumber(tradeService.getTradeProductNumber().toString()));
                            
                            UALoanAdjustedEvent uaLoanAdjustedEvent = new UALoanAdjustedEvent(tradeService, lcUaMat, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(uaLoanAdjustedEvent);

                            break;
                            
                        case UA_LOAN_SETTLEMENT:
                            System.out.println("#########################################");
                            System.out.println("# APPROVING UA LOAN SETTLEMENT #");
                            System.out.println("#########################################");
                            if(TradeServiceStatus.APPROVED.equals(tradeServiceStatus)){
                                // Fire UA LOAN SETTLED Event
                            	UALoanSettledEvent uaLoanSettledEvent = new UALoanSettledEvent(tradeService);
                                eventPublisher.publish(uaLoanSettledEvent);
                            }

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            LetterOfCredit lcUaSet = (LetterOfCredit) tradeProductRepository.load(new DocumentNumber(tradeService.getTradeProductNumber().toString()));
                            UALoanPaidEvent uaLoanPaidEvent = new UALoanPaidEvent(tradeService, lcUaSet, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(uaLoanPaidEvent);

                            break;
                            
                        case REFUND:
                        	System.out.println("##############################################");
                            System.out.println("# APPROVING REFUND OF CASH LC AND/OR CHARGES #");
                            System.out.println("##############################################");
                            
                            LetterOfCredit lcRef = (LetterOfCredit) tradeProductRepository.load(tradeService.getDocumentNumber());
                            eventPublisher.publish(new CashLcRefundEvent(tradeService, lcRef, tsTaggedEvent.getGltsNumber()));
                    }
                    // For MD
                } else if (documentClass.equals(DocumentClass.MD)) {

                    SettlementAccountNumber settlementAccountNumber = new SettlementAccountNumber((String) parameterMap.get("documentNumber"));
                    MarginalDeposit md = marginalDepositRepository.load(settlementAccountNumber);
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>> " + settlementAccountNumber);
                    if (md == null) {
                        System.out.println("NO MD DETECTED");
                        md = new MarginalDeposit(settlementAccountNumber);
                    }

                    // For AMLA
                    TradeProduct tradeProduct = tradeProductRepository.load(new DocumentNumber(md.getSettlementAccountNumber().toString()));
                    LinkedList<LinkedList<Object>> linkedListMda = new LinkedList<LinkedList<Object>>();

//                    md.setCifNumber((String) tradeService.getDetails().get("cifNumber"));
                    md.setCifNumber((String) tradeService.getDetails().get("cifNumber"),
                            (String) tradeService.getDetails().get("cifName"),
                            (String) tradeService.getDetails().get("accountOfficer"),
                            (String) tradeService.getDetails().get("ccbdBranchUnitCode"));

                    md.setLongName((String) tradeService.getDetails().get("longName"));
                    md.setAddress1((String) tradeService.getDetails().get("address1"));
                    md.setAddress2((String) tradeService.getDetails().get("address2"));

                    System.out.println("MARGINAL DEPOSIT >>>>>>>>>>>>>>>>> " + md);

                    switch (serviceType) {

                        case COLLECTION:

                            // Add activities from Payment AR
                            Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                            Set<PaymentDetail> paymentDetailSet = payment.getDetails();

                            Iterator<PaymentDetail> it = paymentDetailSet.iterator();
                            while (it.hasNext()) {

                                PaymentDetail detail = it.next();
                                PaymentInstrumentType paymentInstrumentType = detail.getPaymentInstrumentType();
                                ReferenceType referenceType = null;

                                switch (paymentInstrumentType) {

                                    case CASA:
                                        referenceType = ReferenceType.CASA;
                                        break;

                                    case CASH:
                                        referenceType = ReferenceType.CASH;
                                        break;

                                    case IBT_BRANCH:
                                        referenceType = ReferenceType.IBT_BRANCH;
                                        break;
                                }

                                System.out.println("reference >>>>>>>>>>>>>. " + detail.getReferenceNumber().toString());

                                // If COLLECTION, credit

                                LinkedList<Object> linkedList = new LinkedList<Object>();
                                linkedList.add(0, detail.getAmount());
                                linkedList.add(1, detail.getCurrency().getCurrencyCode());
                                linkedListMda.add(0, linkedList);

                                md.credit(detail.getAmount(), detail.getCurrency(), referenceType, detail.getReferenceNumber().toString());
                            }

                            break;

                        case APPLICATION:

                            Map<String, Object> tradeServiceParams = tradeService.getDetails();
                            BigDecimal amount = new BigDecimal((String) tradeServiceParams.get("amountOfMdToApply"));
                            Currency currency = Currency.getInstance((String) tradeServiceParams.get("mdCurrency"));
                            ReferenceType referenceType = null;
                            String referenceNumber = "";
                            
                            if ("REFUND".equals(tradeService.getDocumentType().toString())){
                            	
                            	amount = new BigDecimal((String) tradeServiceParams.get("amountOfMdToApply"));
                            	
                                LinkedList<Object> linkedList = new LinkedList<Object>();
                                linkedList.add(0, amount);
                                linkedList.add(1, currency.getCurrencyCode());
                                linkedListMda.add(0, linkedList);

                                md.debit(amount, currency, referenceType, referenceNumber);

                                break;
                            	
                            } else {
                            	
                                if (tradeServiceParams.get("modeOfRefund") != null && !((String) tradeServiceParams.get("modeOfRefund")).equals("")) {
                                    referenceType = ReferenceType.valueOf((String) tradeServiceParams.get("modeOfRefund"));
                                } else if (tradeServiceParams.get("modeOfApplication") != null && !((String) tradeServiceParams.get("modeOfApplication")).equals("")) {
                                    referenceType = ReferenceType.valueOf((String) tradeServiceParams.get("modeOfApplication"));
                                }
                                
                                switch (referenceType) {
                                    case CASA:
                                        referenceNumber = (String) tradeServiceParams.get("casaAccountNumber");
                                        break;
                                    case REFUND_TO_CLIENT_ISSUE_MC:
                                        // TODO: What is the reference number? (=Accounting entry number)
                                        break;
                                    case APPLY_TO_LOAN:
                                        referenceNumber = (String) tradeServiceParams.get("pnNumber");
                                        break;
                                }

                                // If APPLICATION, debit
                                LinkedList<Object> linkedList = new LinkedList<Object>();
                                linkedList.add(0, amount);
                                linkedList.add(1, currency.getCurrencyCode());
                                linkedListMda.add(0, linkedList);

                                md.debit(amount, currency, referenceType, referenceNumber);

                                break;
                            }
                    }

                    // Persist
                    marginalDepositRepository.persist(md);

                    // There is only an "updated" MD event, no created, applied, refunded
                    // eventPublisher.publish(new MarginalDepositUpdatedEvent(tradeService, tradeProduct, md, linkedListMda, tsTaggedEvent.getGltsNumber()));

                    // For AP
                } else if (documentClass.equals(DocumentClass.AP)) {
                    System.out.println("################");
                    System.out.println("# APPROVING AP #");
                    System.out.println("################");

                    SettlementAccountNumber settlementAccountNumber = null;
                    Currency currency = Currency.getInstance(tradeService.getDetails().get("currency").toString().trim());
                    BigDecimal amount = new BigDecimal((String) tradeService.getDetails().get("amount"));

                    AccountsPayable ap = null;

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    Date bookingDate = simpleDateFormat.parse(tradeService.getDetails().get("bookingDate").toString());

                    String referenceNumber = null;

                    switch (serviceType) {
                        case SETUP:
                            System.out.println(">>>>>>>>>>>>>>>> SETUP <<<<<<<<<<<<<<<<");
                            settlementAccountNumber = new SettlementAccountNumber(tradeService.getTradeProductNumber().toString());

                            ap = new AccountsPayable(settlementAccountNumber,
                                    currency,
                                    (String) tradeService.getDetails().get("cifNumber"),
                                    (String) tradeService.getDetails().get("cifName"),
                                    (String) tradeService.getDetails().get("accountOfficer"),
                                    (String) tradeService.getDetails().get("ccbdBranchUnitCode"),
                                    bookingDate,
                                    (String) tradeService.getDetails().get("natureOfTransaction"),
                                    amount,
                                    tradeService.getTradeServiceId());

                            referenceNumber = tradeService.getTradeProductNumber().toString();

                            ap.credit(amount, currency, ReferenceType.OUTSIDE_SETUP_AP, bookingDate, referenceNumber);
                            System.out.println("persisting AP");
                            accountsPayableRepository.persist(ap);
                            // eventPublisher.publish(new APCreatedEvent(tradeService, ap, tsTaggedEvent.getGltsNumber()));
                            break;

                        case APPLY:
                            System.out.println(">>>>>>>>>>>>>>>> APPLY <<<<<<<<<<<<<<<<");
                            String accountsPayableId = tradeService.getDetails().get("id").toString();
                            ap = accountsPayableRepository.load(accountsPayableId);

                            referenceNumber = (String) tradeService.getDetails().get("documentNumber"); // TSD/FD to input PN Number of loan used during set-up.
                            String applicationReferenceNumber = (String) tradeService.getDetails().get("setupApplicationReferenceNumber"); // Reference number of LRS for the application of AP in SIBS-Loan

                            ap.debit(amount, currency, ReferenceType.APPLY_AP, bookingDate, referenceNumber, applicationReferenceNumber);

                            if (ap.getApOutstandingBalance().compareTo(BigDecimal.ZERO) < 1) {
                                ap.refundAccountsPayable();
                            }

                            accountsPayableRepository.persist(ap);
                            // eventPublisher.publish(new APAppliedEvent(tradeService, ap, tsTaggedEvent.getGltsNumber()));
                            break;

                        case REFUND:
                            System.out.println(">>>>>>>>>>>>>>>> REFUND <<<<<<<<<<<<<<<<");
                            System.out.println(tradeService.getTradeServiceId());
                            Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
                            System.out.println("payment >> " + payment);


                            String apId = tradeService.getDetails().get("id").toString();
                            ap = accountsPayableRepository.load(apId);

                            Set<PaymentDetail> paymentDetails = payment.getDetails();
                            for (PaymentDetail pd : paymentDetails) {
                                // create activity from all paid payment details
                                if (pd.getStatus().equals(PaymentStatus.PAID)) {
                                    currency = pd.getCurrency();

                                    applicationReferenceNumber = (String) tradeService.getDetails().get("documentNumber");

                                    ap.debit(pd.getAmount(), currency, ReferenceType.OUTSIDE_SETUP_AP, bookingDate, tradeService.getDocumentNumber().toString(), applicationReferenceNumber);
                                    if (ap.getApOutstandingBalance().compareTo(BigDecimal.ZERO) < 1) {
                                        ap.refundAccountsPayable();
                                    }
                                    accountsPayableRepository.persist(ap);
                                }
                            }

                            break;

                    }

                    // For AR
                } else if (documentClass.equals(DocumentClass.AR)) {

                    System.out.println("################");
                    System.out.println("# APPROVING AR #");
                    System.out.println("################");

                    SettlementAccountNumber settlementAccountNumber = null;
                    Currency currency = Currency.getInstance(tradeService.getDetails().get("currency").toString().trim());
                    BigDecimal amount = new BigDecimal((String) tradeService.getDetails().get("amount"));

                    AccountsReceivable ar = null;

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    Date bookingDate = simpleDateFormat.parse(tradeService.getDetails().get("bookingDate").toString());

                    String referenceNumber = null;

                    switch (serviceType) {
                        case SETUP:
                            System.out.println(">>>>>>>>>>>>>>>> SETUP <<<<<<<<<<<<<<<<");

                            settlementAccountNumber = new SettlementAccountNumber(tradeService.getTradeProductNumber().toString());

                            ar = new AccountsReceivable(settlementAccountNumber,
                                    currency,
                                    (String) tradeService.getDetails().get("cifNumber"),
                                    (String) tradeService.getDetails().get("cifName"),
                                    (String) tradeService.getDetails().get("accountOfficer"),
                                    (String) tradeService.getDetails().get("ccbdBranchUnitCode"),
                                    bookingDate,
                                    (String) tradeService.getDetails().get("natureOfTransaction"),
                                    amount,
                                    tradeService.getTradeServiceId());

                            referenceNumber = tradeService.getTradeProductNumber().toString();

                            ar.credit(amount, currency, ReferenceType.OUTSIDE_SETUP_AR, bookingDate, referenceNumber);
                            System.out.println("persisting AR");
                            accountsReceivableRepository.persist(ar);
                            // eventPublisher.publish(new ARCreatedEvent(tradeService, ar, tsTaggedEvent.getGltsNumber()));

                            break;

                        case SETTLE:
                            System.out.println(">>>>>>>>>>>>>>>> SETTLE <<<<<<<<<<<<<<<<");
                            System.out.println(tradeService.getTradeServiceId());
                            Payment payment = paymentRepository.load(tradeService.getTradeServiceId());
                            System.out.println("payment >> " + payment);


                            String arId = tradeService.getDetails().get("id").toString();
//                            SettlementAccountNumber san = new SettlementAccountNumber(tradeService.getDocumentNumber().toString());
//                            ar = accountsReceivableRepository.load(san);
                            ar = accountsReceivableRepository.load(arId);

                            Set<PaymentDetail> paymentDetails = payment.getDetails();
                            for (PaymentDetail pd : paymentDetails) {
                                // create activity from all paid payment details
                                if (pd.getStatus().equals(PaymentStatus.PAID)) {
                                    currency = pd.getCurrency();

                                    String applicationReferenceNumber = (String) tradeService.getDetails().get("documentNumber");

//                                    ar.credit(pd.getAmount(), currency, ReferenceType.OUTSIDE_SETUP_AR, tradeService.getDocumentNumber().toString(), bookingDate, applicationReferenceNumber);
                                    ar.debit(pd.getAmount(), currency, ReferenceType.OUTSIDE_SETUP_AR, bookingDate, tradeService.getDocumentNumber().toString(), applicationReferenceNumber);
                                    ar.closeAccountsReceivable();
                                    accountsReceivableRepository.persist(ar);
                                    eventPublisher.publish(new ARSettledEvent(tradeService, ar, tsTaggedEvent.getGltsNumber()));
                                }
                            }

                            break;
                    }

                } else if (documentClass.equals(DocumentClass.INDEMNITY)) {

                    System.out.println("#######################");
                    System.out.println("# APPROVING INDEMNITY #");
                    System.out.println("#######################");

                    Indemnity indemnity = null;
                    DocumentNumber indemnityNumber = null;
                    DocumentNumber referenceNumber = null;

                    TradeProduct tradeProduct = null;

                    LetterOfCredit lc = null;

                    switch (serviceType) {
                        case ISSUANCE: // for BG/BE Issuance
                            System.out.println("################ ISSUANCE ################");
                            System.out.println("DETAILS: " + tradeService.getDetails());

                            indemnityNumber = tradeService.getDocumentNumber();
                            referenceNumber = new DocumentNumber((String) tradeService.getDetails().get("referenceNumber"));

                            String indemnityType = (String) tradeService.getDetails().get("indemnityType");
                            if (indemnityType.equals("BG")) {
                                System.out.println("BG");

                                indemnity = new Indemnity(indemnityNumber, IndemnityType.BG, referenceNumber);

                                System.out.println("BG PERSISTING...");
                            } else if (indemnityType.equals("BE")) {
                                System.out.println("BE");

                                indemnity = new Indemnity(indemnityNumber, IndemnityType.BE, referenceNumber);
                                System.out.println("BE PERSISTING...");
                            }

                            tradeService.getDetails().put("currency", (String) tradeService.getDetails().get("shipmentCurrency"));
                            tradeService.getDetails().put("amount", (String) tradeService.getDetails().get("shipmentAmount"));

                            System.out.println(tradeService.getDetails());
                            indemnity.updateDetails(tradeService.getDetails());
                            indemnity.updateStatus(TradeProductStatus.OPEN);

                            tradeProductRepository.persist(indemnity);

                            tradeProduct = tradeProductRepository.load(referenceNumber);

                            // cast trade product to letter of credit
                            lc = (LetterOfCredit) tradeProduct;
                            // updates shipment count
                            lc.availIndemnity();

                            String lastTransactionIndemnity = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                            lc.updateLastTransaction(lastTransactionIndemnity);

                            // saves shipment count
                            tradeProductRepository.persist(lc);
                            //This is not included in AMLA format 1.0 
                            //as per dicussion with maam juliet 02/20/2015
//                            eventPublisher.publish(new IndemnityCreatedEvent(tradeService, indemnity, lc, tsTaggedEvent.getGltsNumber()));
                            System.out.println("INDEMNITY PERSISTED...");
                            break;

                        case CANCELLATION: // for BG/BE
                            System.out.println("################ CANCELLATION ################");
                            System.out.println("DETAILS: " + tradeService.getDetails());

                            indemnityNumber = tradeService.getDocumentNumber();
                            referenceNumber = new DocumentNumber((String) tradeService.getDetails().get("referenceNumber"));

                            // indemnity trade product
                            TradeProduct tradeProductIndemnity = tradeProductRepository.load(indemnityNumber);
                            indemnity = (Indemnity) tradeProductIndemnity;

                            indemnity.updateDetails(tradeService.getDetails());
                            indemnity.updateStatus(TradeProductStatus.CANCELLED);

                            System.out.println("CANCELLING INDEMNITY...");

                            tradeProductRepository.persist(indemnity);

                            // letter of credit trade product
                            TradeProduct tradeProductLetterOfCredit = tradeProductRepository.load(referenceNumber);
                            lc = (LetterOfCredit) tradeProductLetterOfCredit;
                            // updates shipment count
                            lc.cancelIndemnity();

                            // saves shipment count
                            tradeProductRepository.persist(lc);
                            //This is not included in AMLA format 1.0 
                            //as per dicussion with maam juliet 02/20/2015
//                            eventPublisher.publish(new IndemnityCancelledEvent(tradeService, indemnity, lc, tsTaggedEvent.getGltsNumber()));
                            System.out.println("INDEMNITY CANCELLED...");
                            break;
                    }

                } else if (documentClass.equals(DocumentClass.CORRES_CHARGE)) {
                    System.out.println("##################################");
                    System.out.println("# APPROVING CORRES CHARGE ACTUAL #");
                    System.out.println("##################################");

                    CorresChargeActual corresChargeActual = new CorresChargeActual(tradeService.getDocumentNumber(),
                            tradeService.getTradeServiceId(),
                            new BigDecimal(tradeService.getDetails().get("totalBillingAmountInPhp").toString().replaceAll(",", "")),
                            Currency.getInstance("PHP"),
                            null,
                            null,
                            null,
                            null);

                    String remitCorresCharges = (tradeService.getDetails().get("remitCorresCharges") != null) ?
                            tradeService.getDetails().get("remitCorresCharges").toString() :
                            null;

                    if (remitCorresCharges != null && "Y".equals(remitCorresCharges)) {
                        corresChargeActual.setMt202Details(tradeService.getDetails());
                    }

                    corresChargeActualRepository.save(corresChargeActual);

                    Boolean withReference = Boolean.TRUE;

                    if (parameterMap.get("withoutReference") != null) {
                        withReference = Boolean.FALSE;
                    }

                    TradeProduct tradeProduct = tradeProductRepository.load(new DocumentNumber(tradeService.getTradeProductNumber().toString()));
                    //This is not included in AMLA format 1.0 
                    //as per dicussion with maam juliet 02/20/2015
                    CorresChargeActualApprovedEvent corresChargeActualApprovedEvent = new CorresChargeActualApprovedEvent(corresChargeActual, withReference, tradeService, tradeProduct, tsTaggedEvent.getGltsNumber());
                    eventPublisher.publish(corresChargeActualApprovedEvent);

                    // for nonlc
                } else if (documentClass.equals(DocumentClass.DA)) {

                    System.out.println("#######################");
                    System.out.println("## APPROVING NON LCS ##");
                    System.out.println("#######################");

                    switch (serviceType) {
                        case NEGOTIATION_ACKNOWLEDGEMENT:

                            System.out.println("\n<><><><><><><> NEGOTIATION ACKNOWLEDGEMENT!\n");
                            //Create and saveOrUpdate Non-LC.

                            DocumentAgainstAcceptance da = new DocumentAgainstAcceptance(tradeService.getDocumentNumber(), tradeService.getDetails());

                            System.out.println("<><><><><><><> da.getDocumentNumber() = " + da.getDocumentNumber() + "\n");
                            da.updateStatus(TradeProductStatus.ACKNOWLEDGED);

                            String lastTransactionNegotiationAcknowledgement = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            da.updateLastTransaction(lastTransactionNegotiationAcknowledgement);

                            tradeProductRepository.persist(da);

                            System.out.println("DocumentAgainstAcceptance Persisted! <<<<<<<<<<<");

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            // Fire DC Created Event
                            DACreatedEvent daCreatedEvent = new DACreatedEvent(tradeService, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(daCreatedEvent);

                            break;
                        //TODO: refractor and finish (Arvin)
                        case NEGOTIATION_ACCEPTANCE:

                            System.out.println("\n<><><><><><><> NEGOTIATION ACCEPTANCE!\n");
                            //Create and saveOrUpdate Non-LC.

                            DocumentAgainstAcceptance daNegoAccept = (DocumentAgainstAcceptance) tradeProductRepository.load(tradeService.getDocumentNumber());
                            daNegoAccept.accept(tradeService.getDetails());
                            daNegoAccept.updateStatus(TradeProductStatus.ACCEPTED);
                            String lastTransactionNegotiationAcceptance = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            daNegoAccept.updateLastTransaction(lastTransactionNegotiationAcceptance);

                            tradeProductRepository.persist(daNegoAccept);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //AMLA
                            eventPublisher.publish(new DAAcceptedEvent(daNegoAccept, tradeService, tsTaggedEvent.getGltsNumber()));

                            break;
                        case SETTLEMENT:

                            System.out.println("\n<><><><><><><> SETTLEMENT!\n");
                            //Create and saveOrUpdate Non-Lc
                            DocumentAgainstAcceptance daSettle = (DocumentAgainstAcceptance) tradeProductRepository.load(tradeService.getDocumentNumber());
                            String lastTransactionSettlement = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            daSettle.updateLastTransaction(lastTransactionSettlement);

//                		daSettle.settle(new BigDecimal((String)tradeService.getDetails().get("productAmount")));
                            daSettle.settle(tradeService.getDetails());

                            //Persist DA
                            tradeProductRepository.persist(daSettle);
                            
//                            String bookingAccountType = (String) tradeService.getDetails().get("accountType");
//
//                            System.out.println("bookingAccountType:"+bookingAccountType);
//                            if(bookingAccountType !=null){
//                                if(bookingAccountType.equalsIgnoreCase("FCDU")){
//                                    bookingAccountType = "FC";
//                                } else if(bookingAccountType.equalsIgnoreCase("RBU")){
//                                    bookingAccountType = "RG";
//                                }
//                            } else {
//                                bookingAccountType = "FC"; //DEFAULT IS FC
//                            }
//
//                            BookingSettlement bookingSettlement = bookingSettlementRepository.load(tradeService.getDocumentNumber());
//                            if (bookingSettlement != null){
//                            	bookingSettlement.updateBookingAccountType(bookingAccountType);
//                            } else {
//                            	bookingSettlement = new BookingSettlement(tradeService.getDocumentNumber(), bookingAccountType);
//                            }
//                            bookingSettlementRepository.persist(bookingSettlement);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //Fire DC Created Event
                            System.out.println("firing DASettlementCreatedEvent event...");
                            DASettlementCreatedEvent daSettlementCreatedEvent = new DASettlementCreatedEvent(tradeService, daSettle, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(daSettlementCreatedEvent);

                            break;
                        case CANCELLATION:

                            System.out.println("\n<><><><><><><> CANCELLATION!\n");
                            //Create and saveOrUpdate Non-Lc
                            DocumentAgainstAcceptance daCancel = nonLcRepository.getDocumentAgainstAcceptance(tradeService.getDocumentNumber());

                            daCancel.cancelDa();

                            String lastTransactionCancellation = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            daCancel.updateLastTransaction(lastTransactionCancellation);

                            //Persist DA
                            tradeProductRepository.persist(daCancel);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //Fire DC Created Event
                            System.out.println("firing DACancelledEvent event...");
                            DACancelledEvent daCancelledEvent = new DACancelledEvent(tradeService, daCancel, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(daCancelledEvent);

                            break;
                    }
                } else if (documentClass.equals(DocumentClass.DP)) {
                    switch (serviceType) {
                        case NEGOTIATION:

                            System.out.println("\n<><><><><><><> NEGOTIATION!\n");
                            //Create and saveOrUpdate Non-LC.

                            DocumentAgainstPayment dp = new DocumentAgainstPayment(tradeService.getDocumentNumber(), tradeService.getDetails());

                            System.out.println("<><><><><><><> dp.getDocumentNumber() = " + dp.getDocumentNumber() + "\n");
                            dp.updateStatus(TradeProductStatus.NEGOTIATED);

                            String lastTransactionNegotiation = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            dp.updateLastTransaction(lastTransactionNegotiation);

                            tradeProductRepository.persist(dp);

                            System.out.println("DocumentAgainstPayment Persisted! <<<<<<<<<<<");

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            // Fire DC Created Event
                            DPCreatedEvent dpCreatedEvent = new DPCreatedEvent(tradeService, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(dpCreatedEvent);
                            break;
                        case SETTLEMENT:

                            System.out.println("\n<><><><><><><> SETTLEMENT!\n");
                            //Create and saveOrUpdate Non-Lc
                            DocumentAgainstPayment dpSettle = (DocumentAgainstPayment) tradeProductRepository.load(tradeService.getDocumentNumber());
                            String lastTransactionSettlement = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            dpSettle.updateLastTransaction(lastTransactionSettlement);

//                		dpSettle.settle(new BigDecimal((String)tradeService.getDetails().get("productAmount")));
                            dpSettle.settle(tradeService.getDetails());

                            //Persist DA
                            tradeProductRepository.persist(dpSettle);
                            
//                            String bookingAccountType = null;
//                            switch(tradeService.getDocumentType()){
//                            case FOREIGN:
//                            	bookingAccountType = (String) tradeService.getDetails().get("accountType");
//                                System.out.println("bookingAccountType:"+bookingAccountType);
//                                if(bookingAccountType !=null){
//                                    if(bookingAccountType.equalsIgnoreCase("FCDU")){
//                                        bookingAccountType = "FC";
//                                    } else if(bookingAccountType.equalsIgnoreCase("RBU")){
//                                        bookingAccountType = "RG";
//                                    }
//                                } else {
//                                    bookingAccountType = "FC"; //DEFAULT IS FC
//                                }
//                            	break;
//                            case DOMESTIC:
//                            	Payment paymentSettDomestic = (Payment) paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
//                            	Set<PaymentDetail> paymentSettDetails = paymentSettDomestic.getDetails();
//                            	if(paymentSettDetails.size() == 1){
//                            	for(PaymentDetail paymentSettDetail: paymentSettDetails){
//                            		switch (paymentSettDetail.getPaymentInstrumentType()){
//                            		case MC_ISSUANCE: case REMITTANCE:
//                            			bookingAccountType = "RG";
//                            			break;
//                            		case CASA: case IBT_BRANCH: case PDDTS: case SWIFT:
//                            			if ("PHP".equals(paymentSettDetail.getCurrency().getCurrencyCode())){
//                            				bookingAccountType = "RG";
//                            			} else {
//                            				bookingAccountType = "FC";
//                            			}
//                            		}
//                            	}
//                            	}
//                            	break;
//                            }
//                            BookingSettlement bookingSettlement = bookingSettlementRepository.load(tradeService.getDocumentNumber());
//                            if (bookingSettlement != null){
//                            	bookingSettlement.updateBookingAccountType(bookingAccountType);
//                            } else {
//                            	bookingSettlement = new BookingSettlement(tradeService.getDocumentNumber(), bookingAccountType);
//                            }
//                            bookingSettlementRepository.persist(bookingSettlement);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //Fire DC Created Event
                            DPSettlementCreatedEvent dpSettlementCreatedEvent = new DPSettlementCreatedEvent(tradeService, dpSettle, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(dpSettlementCreatedEvent);

                            break;
                        case CANCELLATION:

                            System.out.println("\n<><><><><><><> CANCELLATION!\n");
                            //Create and saveOrUpdate Non-Lc
                            DocumentAgainstPayment dpCancel = nonLcRepository.getDocumentAgainstPayment(tradeService.getDocumentNumber());

                            dpCancel.cancelDp();

                            String lastTransactionCancellation = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            dpCancel.updateLastTransaction(lastTransactionCancellation);

                            //Persist DA
                            tradeProductRepository.persist(dpCancel);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //Fire DC Created Event
                            DPCancelledEvent dpCancelledEvent = new DPCancelledEvent(tradeService, dpCancel, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(dpCancelledEvent);

                            break;
                    }
                } else if (documentClass.equals(DocumentClass.OA)) {
                    switch (serviceType) {
                        case NEGOTIATION:

                            System.out.println("\n<><><><><><><> NEGOTIATION!\n");
                            //Create and saveOrUpdate Non-LC.

                            OpenAccount oa = new OpenAccount(tradeService.getDocumentNumber(), tradeService.getDetails());

                            System.out.println("<><><><><><><> oa.getDocumentNumber() = " + oa.getDocumentNumber() + "\n");
                            oa.updateStatus(TradeProductStatus.NEGOTIATED);

                            String lastTransactionNegotiation = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            oa.updateLastTransaction(lastTransactionNegotiation);

                            tradeProductRepository.persist(oa);

                            System.out.println("OpenAccount Persisted! <<<<<<<<<<<");
                            
                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            // Fire DC Created Event
                            OACreatedEvent oaCreatedEvent = new OACreatedEvent(tradeService, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(oaCreatedEvent);
                            break;
                        case SETTLEMENT:

                            System.out.println("\n<><><><><><><> SETTLEMENT!\n");
                            //Create and saveOrUpdate Non-Lc
                            OpenAccount oaSettle = (OpenAccount) tradeProductRepository.load(tradeService.getDocumentNumber());
                            String lastTransactionSettlement = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            oaSettle.updateLastTransaction(lastTransactionSettlement);

//                		oaSettle.settle(new BigDecimal((String)tradeService.getDetails().get("productAmount")));
                            oaSettle.settle(tradeService.getDetails());

                            //Persist DA
                            tradeProductRepository.persist(oaSettle);
                            
//                            String bookingAccountType = (String) tradeService.getDetails().get("accountType");
//                            BookingSettlement bookingSettlement = bookingSettlementRepository.load(tradeService.getDocumentNumber());
//                            if (bookingSettlement != null){
//                            	bookingSettlement.updateBookingAccountType(bookingAccountType);
//                            } else {
//                            	bookingSettlement = new BookingSettlement(tradeService.getDocumentNumber(), bookingAccountType);
//                            }
//                            bookingSettlementRepository.persist(bookingSettlement);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //Fire DC Created Event
                            OASettlementCreatedEvent oaSettlementCreatedEvent = new OASettlementCreatedEvent(tradeService, oaSettle, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(oaSettlementCreatedEvent);

                            break;
                        case CANCELLATION:

                            System.out.println("\n<><><><><><><> CANCELLATION!\n");
                            //Create and saveOrUpdate Non-Lc
                            OpenAccount oaCancel = nonLcRepository.getOpenAccount(tradeService.getDocumentNumber());

                            oaCancel.cancelOa();

                            String lastTransactionCancellation = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            oaCancel.updateLastTransaction(lastTransactionCancellation);

                            //Persist DA
                            tradeProductRepository.persist(oaCancel);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //Fire DC Created Event
                            // this is not included in AMLA Format 1.0
//                            OACancelledEvent oaCancelledEvent = new OACancelledEvent(tradeService, oaCancel, tsTaggedEvent.getGltsNumber());
//                            eventPublisher.publish(oaCancelledEvent);

                            break;
                    }
                } else if (documentClass.equals(DocumentClass.DR)) {
                    switch (serviceType) {
                        case NEGOTIATION:

                            System.out.println("\n<><><><><><><> NEGOTIATION!\n");
                            //Create and saveOrUpdate Non-LC.

                            DirectRemittance dr = new DirectRemittance(tradeService.getDocumentNumber(), tradeService.getDetails());

                            System.out.println("<><><><><><><> dr.getDocumentNumber() = " + dr.getDocumentNumber() + "\n");
                            dr.updateStatus(TradeProductStatus.NEGOTIATED);

                            String lastTransactionNegotiation = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            dr.updateLastTransaction(lastTransactionNegotiation);

                            tradeProductRepository.persist(dr);

                            System.out.println("OpenAccount Persisted! <<<<<<<<<<<");

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            // Fire DC Created Event
                            DRCreatedEvent drCreatedEvent = new DRCreatedEvent(tradeService, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(drCreatedEvent);
                            break;
                        case SETTLEMENT:

                            System.out.println("\n<><><><><><><> SETTLEMENT!\n");
                            //Create and saveOrUpdate Non-Lc
                            DirectRemittance drSettle = (DirectRemittance) tradeProductRepository.load(tradeService.getDocumentNumber());
                            String lastTransactionSettlement = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            drSettle.updateLastTransaction(lastTransactionSettlement);

//                		drSettle.settle(new BigDecimal((String)tradeService.getDetails().get("productAmount")));
                            drSettle.settle(tradeService.getDetails());

                            //Persist DA
                            tradeProductRepository.persist(drSettle);
                            
//                            String bookingAccountType = (String) tradeService.getDetails().get("accountType");
//                            BookingSettlement bookingSettlement = bookingSettlementRepository.load(tradeService.getDocumentNumber());
//                            if (bookingSettlement != null){
//                            	bookingSettlement.updateBookingAccountType(bookingAccountType);
//                            } else {
//                            	bookingSettlement = new BookingSettlement(tradeService.getDocumentNumber(), bookingAccountType);
//                            }
//                            bookingSettlementRepository.persist(bookingSettlement);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //Fire DC Created Event
                            DRSettlementCreatedEvent drSettlementCreatedEvent = new DRSettlementCreatedEvent(tradeService, drSettle, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(drSettlementCreatedEvent);

                            break;
                        case CANCELLATION:

                            System.out.println("\n<><><><><><><> CANCELLATION!\n");
                            //Create and saveOrUpdate Non-Lc
                            DirectRemittance drCancel = nonLcRepository.getDirectRemittance(tradeService.getDocumentNumber());
                            System.out.println("******** DR CURRENCY: " + drCancel.getCurrency());

                            drCancel.cancelDr();

                            String lastTransactionCancellation = buildLastLcTransactionString(serviceType, documentClass, documentType);
                            drCancel.updateLastTransaction(lastTransactionCancellation);

                            //Persist DA
                            tradeProductRepository.persist(drCancel);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //Fire DC Created Event
                         // this is not included in AMLA Format 1.0
//                            DRCancelledEvent drCancelledEvent = new DRCancelledEvent(tradeService, drCancel, tsTaggedEvent.getGltsNumber());
//                            eventPublisher.publish(drCancelledEvent);

                            break;
                    }
                } else if (documentClass.equals(DocumentClass.IMPORT_ADVANCE)) {
                    System.out.println("APPROVING IMPORT ADVANCE...");

                    ImportAdvancePayment importAdvancePayment = null;
                    DocumentNumber documentNumber = null;

                    switch (serviceType) {
                        case PAYMENT:
                            System.out.println("################ PAYMENT ################");
                            System.out.println("DETAILS: " + tradeService.getDetails());

                            documentNumber = tradeService.getDocumentNumber();

                            importAdvancePayment = new ImportAdvancePayment(tradeService.getDetails(), documentNumber);

                            importAdvancePayment.updateStatus(TradeProductStatus.OPEN);

                            tradeProductRepository.persist(importAdvancePayment);
                            System.out.println("IMPORT ADVANCE PAYMENT PERSISTED...");
                            eventPublisher.publish(new ImportAdvancePaymentCreatedEvent(tradeService,importAdvancePayment, tsTaggedEvent.getGltsNumber()));
                            break;

                        case REFUND:
                            System.out.println("################ REFUND ################");
                            System.out.println("DETAILS: " + tradeService.getDetails());

                            importAdvancePayment = (ImportAdvancePayment) tradeProductRepository.load(tradeService.getDocumentNumber());

                            importAdvancePayment.refundPayment(tradeService.getDetails());

                            tradeProductRepository.persist(importAdvancePayment);

                            System.out.println("IMPORT ADVANCE REFUND PERSISTED...");
                            eventPublisher.publish(new ImportAdvancePaymentRefundCreatedEvent(tradeService,importAdvancePayment, tsTaggedEvent.getGltsNumber()));
                            break;
                    }

                } else if (documentClass.equals(DocumentClass.EXPORT_ADVANCE)) {
                    System.out.println("APPROVING EXPORT ADVANCE...");

                    ExportAdvancePayment exportAdvancePayment = null;
                    DocumentNumber documentNumber = null;

                    switch (serviceType) {
                        case PAYMENT:
                            System.out.println("################ PAYMENT ################");
                            System.out.println("DETAILS: " + tradeService.getDetails());

                            documentNumber = tradeService.getDocumentNumber();

                            exportAdvancePayment = new ExportAdvancePayment(tradeService.getDetails(), documentNumber);

                            exportAdvancePayment.updateStatus(TradeProductStatus.OPEN);

                            tradeProductRepository.persist(exportAdvancePayment);
                            System.out.println("EXPORT ADVANCE PAYMENT PERSISTED...");
                            eventPublisher.publish(new ExportAdvancePaymentCreatedEvent(tradeService,exportAdvancePayment, tsTaggedEvent.getGltsNumber()));
                            break;

                        case REFUND:
                            System.out.println("################ REFUND ################");
                            System.out.println("DETAILS: " + tradeService.getDetails());

                            exportAdvancePayment = (ExportAdvancePayment) tradeProductRepository.load(tradeService.getDocumentNumber());

                            exportAdvancePayment.refundPayment(tradeService.getDetails());

                            tradeProductRepository.persist(exportAdvancePayment);

                            System.out.println("EXPORT ADVANCE REFUND PERSISTED...");
                            eventPublisher.publish(new ExportAdvancePaymentRefundCreatedEvent(tradeService,exportAdvancePayment, tsTaggedEvent.getGltsNumber()));
                            break;
                    }
                } else if (documentClass.equals(DocumentClass.EXPORT_ADVISING)) {
                    System.out.println("*******************");
                    System.out.println("* EXPORT ADVISING *");
                    System.out.println("*******************");


                    DocumentNumber documentNumber = null;

                    ExportAdvising exportAdvising = null;
                    Boolean withMt730 = Boolean.FALSE;

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

                    switch (serviceType) {
                        case OPENING_ADVISING:
                            System.out.println("***** OPENING *****");
                            documentNumber = tradeService.getDocumentNumber();

                            AdvisingBankType advisingBankType = null;

                            if (tradeService.getDetails().get("documentSubType1").toString().equals("FIRST_ADVISING")) {
                                advisingBankType = AdvisingBankType.FIRST;

                                tradeService.getDetails().put("totalBankCharges", "0");

                            } else if (tradeService.getDetails().get("documentSubType1").toString().equals("SECOND_ADVISING")) {
                                advisingBankType = AdvisingBankType.SECOND;
                            }

                            if (tradeService.getDetails().get("sendMt730Flag") != null && tradeService.getDetails().get("sendMt730Flag").toString().equals("1")) {
                                withMt730 = Boolean.TRUE;
                            }


                            if (tradeService.getDetails().get("approvedOnce") != null) {
                                exportAdvising = (ExportAdvising) tradeProductRepository.load(tradeService.getDocumentNumber());

                                List<TradeService> tradeServiceList = tradeServiceRepository.getAllApprovedExportAdvising(tradeService.getDocumentNumber());

                                for (TradeService tradeServiceItem : tradeServiceList) {
                                    tradeServiceItem.setPaymentStatus(PaymentStatus.PAID);

                                    tradeServiceRepository.merge(tradeServiceItem);
                                }
                            } else {
                                exportAdvising = new ExportAdvising(documentNumber, tradeService.getDetails(), advisingBankType);
                            }

                            exportAdvising.setWithMt730(withMt730);

                            tradeProductRepository.persist(exportAdvising);
                          //This is not included in AMLA format 1.0 
                            //as per dicussion with maam juliet 02/20/2015
                            eventPublisher.publish(new ExportAdvisingCreatedEvent(tradeService, exportAdvising, tsTaggedEvent.getGltsNumber()));

                            break;

                        case AMENDMENT_ADVISING:
                            System.out.println("***** AMENDMENT *****");

                            documentNumber = tradeService.getDocumentNumber();

                            exportAdvising = (ExportAdvising) tradeProductRepository.load(documentNumber);

                            exportAdvising.updateDetails(tradeService.getDetails());

                            exportAdvising.amendExportAdvising(new BigDecimal((String) tradeService.getDetails().get("lcAmount")),
                                    Currency.getInstance((String) tradeService.getDetails().get("lcCurrency")));

                            exportAdvising.amendExportAdvising((String) tradeService.getDetails().get("newExporterName"),
                                    (String) tradeService.getDetails().get("newExporterAddress"),
                                    simpleDateFormat.parse((String) tradeService.getDetails().get("lastAmendmentDate")));

                            if (tradeService.getDetails().get("sendMt730Flag") != null && tradeService.getDetails().get("sendMt730Flag").toString().equals("1")) {
                                withMt730 = Boolean.TRUE;
                            }

                            exportAdvising.setWithMt730(withMt730);

                            tradeProductRepository.merge(exportAdvising);
                          //This is not included in AMLA format 1.0 
                            //as per dicussion with maam juliet 02/20/2015
                            eventPublisher.publish(new ExportAdvisingAmendedEvent(tradeService, exportAdvising, tsTaggedEvent.getGltsNumber()));
                            break;

                        case CANCELLATION_ADVISING:
                            System.out.println("***** CANCELLATION *****");

                            documentNumber = tradeService.getDocumentNumber();

                            exportAdvising = (ExportAdvising) tradeProductRepository.load(documentNumber);

                            exportAdvising.updateDetails(tradeService.getDetails());

                            exportAdvising.cancelExportAdvising(simpleDateFormat.parse((String) tradeService.getDetails().get("dateOfCancellation")));

                            Boolean withMt799 = Boolean.FALSE;

                            if (tradeService.getDetails().get("sendMt799Flag") != null && tradeService.getDetails().get("sendMt799Flag").toString().equals("1")) {
                                withMt799 = Boolean.TRUE;
                            }

                            exportAdvising.setWithMt799(withMt799);

                            tradeProductRepository.merge(exportAdvising);
                          //This is not included in AMLA format 1.0 
                            //as per dicussion with maam juliet 02/20/2015
                            eventPublisher.publish(new ExportAdvisingCancelledEvent(tradeService, exportAdvising, tsTaggedEvent.getGltsNumber()));
                            break;

                    }
                } else if (documentClass.equals(DocumentClass.BP)) {
                    DocumentNumber documentNumber = null;

                    if (documentType.equals(DocumentType.FOREIGN)) {
                        System.out.println("APPROVING EBP");
                        if (serviceType.equals(ServiceType.NEGOTIATION)) {
                            System.out.println("NEGOTIATION");

                            documentNumber = tradeService.getDocumentNumber();

                            ExportBills exportBills = new ExportBills(documentNumber, tradeService.getDetails(), ProductType.BP, ExportBillType.EBP);

                            System.out.println(tradeService.getDetails().get("negotiationNumber"));

                            //String negotiationNumber = tradeService.getDetails().get("negotiationNumber");
                            if (tradeService.getDetails().get("negotiationNumber") != null) {
                            	String negotiationNumber = tradeService.getDetails().get("negotiationNumber").toString();
                                exportBills.setNegotiationNumber(new DocumentNumber(negotiationNumber));
                            }

                            // enclosed documents
                            if (tradeService.getDetails().get("documentsEnclosed") != null && !tradeService.getDetails().get("documentsEnclosed").toString().isEmpty()) {
                                String documentsEnclosedString = (String) tradeService.getDetails().get("documentsEnclosed");
                                documentsEnclosedString = documentsEnclosedString.substring(1, documentsEnclosedString.length());

                                List<Map<String, Object>> documentsEnclosed = UtilSetFields.stringOfListMapToListMap(documentsEnclosedString);

                                List<DocumentsEnclosed> documentsEnclosedList = new ArrayList<DocumentsEnclosed>();
                                for (Map<String, Object> map : documentsEnclosed) {
                                    DocumentsEnclosed documentsEnclosedItem = new DocumentsEnclosed((String) map.get("documentName"),
//                                            new Long((String) map.get("original1")),
//                                            new Long((String) map.get("original2")),
//                                            new Long((String) map.get("duplicate1")),
//                                            new Long((String) map.get("duplicate2")));
		                                      (String) map.get("original1"),
		                                      (String) map.get("original2"),
		                                      (String) map.get("duplicate1"),
		                                      (String) map.get("duplicate2"));

                                    documentsEnclosedList.add(documentsEnclosedItem);
                                }

                                exportBills.addDocumentsEnclosed(documentsEnclosedList);
                            }

                            // enclosed instruction
                            List<EnclosedInstruction> instructionList = new ArrayList<EnclosedInstruction>();

                            if (tradeService.getDetails().get("enclosedInstruction") != null && !tradeService.getDetails().get("enclosedInstruction").toString().isEmpty()) {
                                String enclosedInstrString = (String) tradeService.getDetails().get("enclosedInstruction");
                                enclosedInstrString = enclosedInstrString.substring(1, enclosedInstrString.length());

                                List<Map<String, Object>> enclosedInstruction = UtilSetFields.stringOfListMapToListMap(enclosedInstrString);


                                for (Map<String, Object> map : enclosedInstruction) {
                                    System.out.println((String) map.get("instruction"));
                                    EnclosedInstruction enclosedInstructionItem = new EnclosedInstruction((String) map.get("instruction"), InstructionType.DEFAULT);

                                    instructionList.add(enclosedInstructionItem);
                                }
                                System.out.println("adding enclosed instructions... " + instructionList.size());
                            }


                            // additional instruction
                            if (tradeService.getDetails().get("additionalInstruction") != null && !tradeService.getDetails().get("additionalInstruction").toString().isEmpty()) {
                                String additionalInstructionString = (String) tradeService.getDetails().get("additionalInstruction");
                                additionalInstructionString = additionalInstructionString.substring(1, additionalInstructionString.length());

                                List<Map<String, Object>> additionalInstruction = UtilSetFields.stringOfListMapToListMap(additionalInstructionString);

                                for (Map<String, Object> map : additionalInstruction) {
                                    EnclosedInstruction additionalInstructionItem = new EnclosedInstruction((String) map.get("instruction"), InstructionType.NEW);

                                    instructionList.add(additionalInstructionItem);
                                }
                                System.out.println("adding additional instructions... " + instructionList.size());
                            }

                            exportBills.addEnclosedInstructions(instructionList);

                            Map<String, Object> details = tradeService.getDetails();
                            if (null != details.get("loanMaturityDate") && details.get("loanMaturityDate").toString() != ""){
                            	details.put("loanMaturityDate", new SimpleDateFormat("MM/dd/yyyy").format(new Date(details.get("loanMaturityDate").toString())));
                            }
                            
                            Payment savedProductPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);

                            if (savedProductPayment != null) {
                            	Set<PaymentDetail> savedPaymentDetails = savedProductPayment.getDetails();
                            	for (PaymentDetail pd : savedPaymentDetails) {
                                    if (pd.getStatus().equals(PaymentStatus.PAID)) {
                                        if (pd.getPaymentInstrumentType().equals(PaymentInstrumentType.EBP)) {
                                        	details.put("facilityType", pd.getFacilityType());
                                        	details.put("facilityId", pd.getFacilityId().toString());
                                        	details.put("loanAmount", pd.getAmount());
                                        	details.put("agriAgraTagging", pd.getAgriAgraTagging());
                                        	details.put("paymentCode", pd.getPaymentCode().toString());
                                        	details.put("pnNumber", pd.getPnNumber().toString());
                                        }
                                    }
                            	}
                            }
                            exportBills.setLoanDetails(details);

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                                //added by Henry
                                exportBills.setCollectingbankcode(tradeService.getDetails().get("swiftAddress").toString());
                                exportBills.setCollectingbankaddress(tradeService.getDetails().get("negoAdviceAddresseeAddress").toString());
                                //end
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                            		("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                               		 "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                               		 "DR".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")))) {
                            	exportBills.setCollectingbankcode(tradeService.getDetails().get("collectingBankCode").toString());
                                exportBills.setCollectingbankaddress(tradeService.getDetails().get("collectingBankAddress").toString());
                            	exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            exportBills.updateStatus(TradeProductStatus.NEGOTIATED); 
                            tradeProductRepository.persist(exportBills);
                            System.out.println("Persisted EBP");
                            
                            ExportBillsPurchaseCreatedEvent exportBillsPurchaseCreatedEvent = new ExportBillsPurchaseCreatedEvent(tradeService.getTradeServiceId());
                            eventPublisher.publish(exportBillsPurchaseCreatedEvent);
                            //Added By henry
                            ExportBills eb = null;
                            
                            if (tradeService.getDetails().containsKey("negotiationNumber")){
	                            if ((tradeService.getDetails().get("negotiationNumber") != null)){
	                            		DocumentNumber negoNumber = new DocumentNumber(tradeService.getDetails().get("negotiationNumber").toString());
	                            		TradeService ts = tradeServiceRepository.load(negoNumber, ServiceType.NEGOTIATION);
	                            		eb = exportBillsRepository.load(negoNumber);
	                            		BigDecimal bcAmount;
	                            		BigDecimal ebcAmount = BigDecimal.valueOf(Double.parseDouble(ts.getDetails().get("amount").toString()));
	                            		BigDecimal bpAmount = exportBills.getOutstandingAmount();
	                            		System.out.println(ebcAmount.compareTo(bpAmount));
	                            		if (ebcAmount.compareTo(bpAmount) != 0){
	                            			bcAmount = ebcAmount.subtract(bpAmount).abs();
	                            	    	System.out.println("((((((((((((((((HENRY))))))))))");
	                            	    	System.out.println(ebcAmount);
	                            	    	System.out.println(bpAmount);
	                            	    	ts.getDetails().put("bcAmount",bcAmount);
	                            	    	ts.getDetails().put("bpNegoNumber", tradeService.getDetails().get("documentNumber"));
	                            	    	System.out.println(eb.getDocumentNumber()+"EBC HENRY");
	                            	    	System.out.println(eb.getOutstandingAmount());
	                            	    	eb.setOutstandingAmount(bcAmount);
	                                	} else {
	                                		ts.getDetails().put("bcAmount",BigDecimal.ZERO);
	                                		eb.setOutstandingAmount(BigDecimal.ZERO);
	                                		ts.getDetails().put("bpNegoNumber", tradeService.getDetails().get("documentNumber"));
	                                		eb.updateStatus(TradeProductStatus.CLOSED);
	                                		System.out.println("ELSE");
	                                		//tradeProductRepository.merge(eb);
	                                		System.out.println(eb.getStatus());
	                                	}
	                            		
	                            		System.out.println("Trade Service Details: " + ts.getDetails());
	                            		BPNegotiatedPriorBCEvent bPNegotiatedPriorBCEvent = new BPNegotiatedPriorBCEvent(ts, exportBills, tsTaggedEvent.getGltsNumber());
	                            		eventPublisher.publish(bPNegotiatedPriorBCEvent);
	                            		System.out.println("PASOKAN");
	                            		tradeProductRepository.persist(eb);
	                            		System.out.println(eb.getStatus());
	                            }			
                            } 
                        	// Saving the negotiation date to date today
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            
                	    	tradeService.getDetails().put("negotiationDate", dateFormat.format(new Date()));
                	    	exportBills.setNegotiationDate(new Date(dateFormat.format(new Date())));
                	    	System.out.println("Negotiation Date: \n" + dateFormat.format(new Date()));
                	    	System.out.println("Negotiation Date EB: \n" + new Date(dateFormat.format(new Date())));

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                            updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                            BPNegotiatedEvent bpNegotiatedEvent = new BPNegotiatedEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(bpNegotiatedEvent);
//                            throw new Exception("SAKIN LANG to");
                        } else if (serviceType.equals(ServiceType.SETTLEMENT)) {
                            System.out.println("SETTLEMENT");

                            ExportBills exportBills = exportBillsRepository.load(tradeService.getDocumentNumber());

                            exportBills.settleExportBills(new BigDecimal((String) tradeService.getDetails().get("proceedsAmount")),
                                    (String) tradeService.getDetails().get("partialNego"));
                            
                            exportBills.updateDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.SETTLED);

                            tradeProductRepository.merge(exportBills);

                            BPSettledEvent bpSettledEvent = new BPSettledEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(bpSettledEvent);
                        }
                    } else if (documentType.equals(DocumentType.DOMESTIC)) {
                        System.out.println("APPROVING DBP");
                        if (serviceType.equals(ServiceType.NEGOTIATION)) {
                            System.out.println("NEGOTIATION");

                            documentNumber = tradeService.getDocumentNumber();

                            ExportBills exportBills = new ExportBills(documentNumber, tradeService.getDetails(), ProductType.BP, ExportBillType.DBP);

                            System.out.println(tradeService.getDetails().get("negotiationNumber"));

                            if (null != tradeService.getDetails().get("negotiationNumber")) {
                            	String negotiationNumber = tradeService.getDetails().get("negotiationNumber").toString();
                                exportBills.setNegotiationNumber(new DocumentNumber(negotiationNumber));
                            }

                            
                            System.out.println(tradeService.getDetails().get("documentsEnclosed"));
                            // enclosed documents
                            if (null != tradeService.getDetails().get("documentsEnclosed") && !tradeService.getDetails().get("documentsEnclosed").toString().isEmpty()) {
                                String documentsEnclosedString = (String) tradeService.getDetails().get("documentsEnclosed");
                                documentsEnclosedString = documentsEnclosedString.substring(1, documentsEnclosedString.length());

                                List<Map<String, Object>> documentsEnclosed = UtilSetFields.stringOfListMapToListMap(documentsEnclosedString);

                                List<DocumentsEnclosed> documentsEnclosedList = new ArrayList<DocumentsEnclosed>();
                                for (Map<String, Object> map : documentsEnclosed) {
                                    DocumentsEnclosed documentsEnclosedItem = new DocumentsEnclosed((String) map.get("documentName"),
//                                          new Long((String) map.get("original1")),
//                                          new Long((String) map.get("original2")),
//                                          new Long((String) map.get("duplicate1")),
//                                          new Long((String) map.get("duplicate2")));
		                                      (String) map.get("original1"),
		                                      (String) map.get("original2"),
		                                      (String) map.get("duplicate1"),
		                                      (String) map.get("duplicate2"));
                                    documentsEnclosedList.add(documentsEnclosedItem);
                                }

                                exportBills.addDocumentsEnclosed(documentsEnclosedList);
                            }

                            // enclosed instruction
                            List<EnclosedInstruction> instructionList = new ArrayList<EnclosedInstruction>();

                            if (null != tradeService.getDetails().get("enclosedInstruction") && !tradeService.getDetails().get("enclosedInstruction").toString().isEmpty()) {
                                String enclosedInstrString = (String) tradeService.getDetails().get("enclosedInstruction");
                                enclosedInstrString = enclosedInstrString.substring(1, enclosedInstrString.length());

                                List<Map<String, Object>> enclosedInstruction = UtilSetFields.stringOfListMapToListMap(enclosedInstrString);


                                for (Map<String, Object> map : enclosedInstruction) {
                                    System.out.println((String) map.get("instruction"));
                                    EnclosedInstruction enclosedInstructionItem = new EnclosedInstruction((String) map.get("instruction"), InstructionType.DEFAULT);

                                    instructionList.add(enclosedInstructionItem);
                                }
                                System.out.println("adding enclosed instructions... " + instructionList.size());
                            }


                            // additional instruction
                            if (null != tradeService.getDetails().get("additionalInstruction") && !tradeService.getDetails().get("additionalInstruction").toString().isEmpty()) {
                                String additionalInstructionString = (String) tradeService.getDetails().get("additionalInstruction");
                                additionalInstructionString = additionalInstructionString.substring(1, additionalInstructionString.length());

                                List<Map<String, Object>> additionalInstruction = UtilSetFields.stringOfListMapToListMap(additionalInstructionString);

                                for (Map<String, Object> map : additionalInstruction) {
                                    EnclosedInstruction additionalInstructionItem = new EnclosedInstruction((String) map.get("instruction"), InstructionType.NEW);

                                    instructionList.add(additionalInstructionItem);
                                }
                                System.out.println("adding additional instructions... " + instructionList.size());
                            }

                            exportBills.addEnclosedInstructions(instructionList);
                            
                            Map<String, Object> details = tradeService.getDetails();
                            if (null != details.get("loanMaturityDate") && details.get("loanMaturityDate").toString() != ""){
                            	details.put("loanMaturityDate", new SimpleDateFormat("MM/dd/yyyy").format(new Date(details.get("loanMaturityDate").toString())));
                            }
                            
                            Payment savedProductPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);

                            if (savedProductPayment != null) {
                            	Set<PaymentDetail> savedPaymentDetails = savedProductPayment.getDetails();
                            	for (PaymentDetail pd : savedPaymentDetails) {
                                    if (pd.getStatus().equals(PaymentStatus.PAID)) {
                                        if (pd.getPaymentInstrumentType().equals(PaymentInstrumentType.DBP)) {
                                        	details.put("facilityType", pd.getFacilityType());
                                        	details.put("facilityId", pd.getFacilityId().toString());
                                        	details.put("loanAmount", pd.getAmount());
                                        	details.put("agriAgraTagging", pd.getAgriAgraTagging());
                                        	details.put("paymentCode", pd.getPaymentCode().toString());
                                        	details.put("pnNumber", pd.getPnNumber().toString());
                                        }
                                    }
                            	}
                            }
                            exportBills.setLoanDetails(details);

//                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            /*} else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                            		 "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                            		 "DR".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }*/

                            exportBills.updateStatus(TradeProductStatus.NEGOTIATED);

                            tradeProductRepository.merge(exportBills);
                            System.out.println("Persisted DBP");
                            
                            ExportBillsPurchaseCreatedEvent exportBillsPurchaseCreatedEvent = new ExportBillsPurchaseCreatedEvent(tradeService.getTradeServiceId());
                            eventPublisher.publish(exportBillsPurchaseCreatedEvent);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                            updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                            BPNegotiatedEvent bpNegotiatedEvent = new BPNegotiatedEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(bpNegotiatedEvent);
                        } else if (serviceType.equals(ServiceType.SETTLEMENT)) {
                            System.out.println("SETTLEMENT");

                            ExportBills exportBills = exportBillsRepository.load(tradeService.getDocumentNumber());

                            exportBills.settleExportBills(new BigDecimal((String) tradeService.getDetails().get("proceedsAmount")),
                                    (String) tradeService.getDetails().get("partialNego"));

                            tradeProductRepository.merge(exportBills);
                            System.out.println("Persisted DBP");

                            BPSettledEvent bpSettledEvent = new BPSettledEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(bpSettledEvent);
                        }
                    }
                } else if (documentClass.equals(DocumentClass.BC)) {
                    DocumentNumber documentNumber = null;

                    if (documentType.equals(DocumentType.FOREIGN)) {
                        System.out.println("APPROVING EBC");

                        if (serviceType.equals(ServiceType.NEGOTIATION)) {
                            System.out.println("NEGOTIATION");

                            documentNumber = tradeService.getDocumentNumber();

                            ExportBills exportBills = new ExportBills(documentNumber, tradeService.getDetails(), ProductType.BC, ExportBillType.EBC);

                            // enclosed documents
                            if (tradeService.getDetails().get("documentsEnclosed") != null && !tradeService.getDetails().get("documentsEnclosed").toString().isEmpty()) {
                                String documentsEnclosedString = (String) tradeService.getDetails().get("documentsEnclosed");
                                documentsEnclosedString = documentsEnclosedString.substring(1, documentsEnclosedString.length());

                                List<Map<String, Object>> documentsEnclosed = UtilSetFields.stringOfListMapToListMap(documentsEnclosedString);

                                List<DocumentsEnclosed> documentsEnclosedList = new ArrayList<DocumentsEnclosed>();
                                for (Map<String, Object> map : documentsEnclosed) {
                                    DocumentsEnclosed documentsEnclosedItem = new DocumentsEnclosed((String) map.get("documentName"),
//                                          new Long((String) map.get("original1")),
//                                          new Long((String) map.get("original2")),
//                                          new Long((String) map.get("duplicate1")),
//                                          new Long((String) map.get("duplicate2")));
		                                      (String) map.get("original1"),
		                                      (String) map.get("original2"),
		                                      (String) map.get("duplicate1"),
		                                      (String) map.get("duplicate2"));

                                    documentsEnclosedList.add(documentsEnclosedItem);
                                }

                                exportBills.addDocumentsEnclosed(documentsEnclosedList);
                            }

                            // enclosed instruction
                            List<EnclosedInstruction> instructionList = new ArrayList<EnclosedInstruction>();

                            if (tradeService.getDetails().get("enclosedInstruction") != null && !tradeService.getDetails().get("enclosedInstruction").toString().isEmpty()) {
                                String enclosedInstrString = (String) tradeService.getDetails().get("enclosedInstruction");
                                enclosedInstrString = enclosedInstrString.substring(1, enclosedInstrString.length());

                                List<Map<String, Object>> enclosedInstruction = UtilSetFields.stringOfListMapToListMapInstructions(enclosedInstrString);
//                                List<Map<String, Object>> enclosedInstruction = UtilSetFields.stringOfListMapToListMap(enclosedInstrString);

                                for (Map<String, Object> map : enclosedInstruction) {
                                    System.out.println((String) map.get("instruction"));
                                    EnclosedInstruction enclosedInstructionItem = new EnclosedInstruction((String) map.get("instruction"), InstructionType.DEFAULT);

                                    instructionList.add(enclosedInstructionItem);
                                }
                                System.out.println("adding enclosed instructions... " + instructionList.size());
                            }


                            // additional instruction
                            if (tradeService.getDetails().get("additionalInstruction") != null && !tradeService.getDetails().get("additionalInstruction").toString().isEmpty()) {
                                String additionalInstructionString = (String) tradeService.getDetails().get("additionalInstruction");
                                additionalInstructionString = additionalInstructionString.substring(1, additionalInstructionString.length());
                                System.out.println("testing additional instruction");
//                                List<Map<String, Object>> additionalInstruction = UtilSetFields.stringOfListMapToListMap(additionalInstructionString);
                                List<Map<String, Object>> additionalInstruction = UtilSetFields.stringOfListMapToListMapInstructions(additionalInstructionString);

                                for (Map<String, Object> map : additionalInstruction) {
                                    EnclosedInstruction additionalInstructionItem = new EnclosedInstruction((String) map.get("instruction"), InstructionType.NEW);

                                    instructionList.add(additionalInstructionItem);
                                }
                                System.out.println("adding additional instructions... " + instructionList.size());
                            }

                            exportBills.addEnclosedInstructions(instructionList);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                                //added by Henry
                                exportBills.setCollectingbankcode(tradeService.getDetails().get("swiftAddress").toString());
                                exportBills.setCollectingbankaddress(tradeService.getDetails().get("negoAdviceAddresseeAddress").toString());
                                tradeProductRepository.merge(exportBills);
                                //end
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                               		 "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                               		 "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                            	exportBills.setCollectingbankcode(tradeService.getDetails().get("collectingBankCode").toString());
                                exportBills.setCollectingbankaddress(tradeService.getDetails().get("collectingBankAddress").toString());
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }


                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.updateStatus(TradeProductStatus.NEGOTIATED);

                            tradeProductRepository.merge(exportBills);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                            updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //This is not included in AMLA Format 1.0
                            BCNegotiatedEvent bcNegotiatedEvent = new BCNegotiatedEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(bcNegotiatedEvent);

                        } else if (serviceType.equals(ServiceType.SETTLEMENT)) {
                            System.out.println("SETTLEMENT");

                            ExportBills exportBills = exportBillsRepository.load(tradeService.getDocumentNumber());

                            exportBills.settleExportBills(new BigDecimal((String) tradeService.getDetails().get("proceedsAmount")),
                                    (String) tradeService.getDetails().get("partialNego"));
                           System.out.println("BEFORE");
                            // added by henry
                           
                            ExportBills ebp = null;
                            TradeService tss = tradeServiceRepository.load(tradeService.getDocumentNumber(), ServiceType.NEGOTIATION);
                            System.out.println(tss.getServiceInstructionId());
                            System.out.println(tradeService.getServiceInstructionId());
                            System.out.println(tss.getDetails().get("bpNegoNumber"));
                            if (tss.getDetails().containsKey("bpNegoNumber")){
                            	if(tss.getDetails().get("bpNegoNumber") != null){
                            		DocumentNumber bpNegoNumber = new DocumentNumber(tss.getDetails().get("bpNegoNumber").toString());
                            		ebp = exportBillsRepository.load(bpNegoNumber);
                            		System.out.println(">>>HENRY<<<");
                            		System.out.println(ebp.getOutstandingAmount());
                            		exportBills.setOutstandingAmount(BigDecimal.ZERO);
                            		ebp.setOutstandingAmount(BigDecimal.ZERO);
                            		ebp.updateStatus(TradeProductStatus.SETTLED);
                            		System.out.println("PASOK DITO");
                            		tradeProductRepository.persist(ebp);
                            		System.out.println(ebp.getStatus());
                            	}
                            }
                            
                            System.out.println();
                            exportBills.updateDetails(tradeService.getDetails());
                            //additional condition for Status of BC
                            BigDecimal ebcAmount = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get("amount").toString()));
                            System.out.println(ebcAmount);
                    		BigDecimal bpAmount = exportBills.getBpAmount();
                    		System.out.println(bpAmount);
                    		if (bpAmount != null){
                    			System.out.println("PASOK");
	                    		if (ebcAmount.compareTo(bpAmount) == 0){
	                    			exportBills.updateStatus(TradeProductStatus.CLOSED);
	                    			//No reporting on Amla
	                    			System.out.println("Nothing To Report");
	                    		} else	{
	                    			exportBills.updateStatus(TradeProductStatus.SETTLED);
	                    			BCSettledPriorBCEvent bcSettledPriorBCEvent = new BCSettledPriorBCEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
	                                eventPublisher.publish(bcSettledPriorBCEvent);
	                    		}
                    		} else {
                    			//if BPamount is null and no Prior EBC
                    			BCSettledPriorBCEvent bcSettledPriorBCEvent = new BCSettledPriorBCEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                                eventPublisher.publish(bcSettledPriorBCEvent);
                    			exportBills.updateStatus(TradeProductStatus.SETTLED);
                    		} 
                    			
                            tradeProductRepository.merge(exportBills);
                            System.out.println(exportBills.getStatus());

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                            updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                            BCSettledEvent bcSettledEvent = new BCSettledEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(bcSettledEvent);

                        } else if (serviceType.equals(ServiceType.CANCELLATION)) {
                            System.out.println("CANCELLATION");

                            ExportBills exportBills = exportBillsRepository.load(tradeService.getDocumentNumber());

                            exportBills.cancelExportBills((String) tradeService.getDetails().get("reasonForCancellation"));

                            exportBills.updateDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.CANCELLED);

                            tradeProductRepository.merge(exportBills);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                            updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                            //This is not INcluded in AMLA Format 1.0
//                            BCCancelledEvent bcCancelledEvent = new BCCancelledEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
//                            eventPublisher.publish(bcCancelledEvent);
                        }
                    } else if (documentType.equals(DocumentType.DOMESTIC)) {
                        System.out.println("APPROVING DBC");

                        if (serviceType.equals(ServiceType.NEGOTIATION)) {
                            System.out.println("NEGOTIATION");

                            documentNumber = tradeService.getDocumentNumber();

                            ExportBills exportBills = new ExportBills(documentNumber, tradeService.getDetails(), ProductType.BC, ExportBillType.DBC);

                            // enclosed documents
                            if (tradeService.getDetails().get("documentsEnclosed") != null && !tradeService.getDetails().get("documentsEnclosed").toString().isEmpty()) {
                                String documentsEnclosedString = (String) tradeService.getDetails().get("documentsEnclosed");
                                documentsEnclosedString = documentsEnclosedString.substring(1, documentsEnclosedString.length());

                                List<Map<String, Object>> documentsEnclosed = UtilSetFields.stringOfListMapToListMap(documentsEnclosedString);

                                List<DocumentsEnclosed> documentsEnclosedList = new ArrayList<DocumentsEnclosed>();
                                for (Map<String, Object> map : documentsEnclosed) {
                                    DocumentsEnclosed documentsEnclosedItem = new DocumentsEnclosed((String) map.get("documentName"),
//                                          new Long((String) map.get("original1")),
//                                          new Long((String) map.get("original2")),
//                                          new Long((String) map.get("duplicate1")),
//                                          new Long((String) map.get("duplicate2")));
		                                      (String) map.get("original1"),
		                                      (String) map.get("original2"),
		                                      (String) map.get("duplicate1"),
		                                      (String) map.get("duplicate2"));

                                    documentsEnclosedList.add(documentsEnclosedItem);
                                }

                                exportBills.addDocumentsEnclosed(documentsEnclosedList);
                            }

                            // enclosed instruction
                            List<EnclosedInstruction> instructionList = new ArrayList<EnclosedInstruction>();

                            if (tradeService.getDetails().get("enclosedInstruction") != null && !tradeService.getDetails().get("enclosedInstruction").toString().isEmpty()) {
                                String enclosedInstrString = (String) tradeService.getDetails().get("enclosedInstruction");
                                enclosedInstrString = enclosedInstrString.substring(1, enclosedInstrString.length());

                                List<Map<String, Object>> enclosedInstruction = UtilSetFields.stringOfListMapToListMap(enclosedInstrString);


                                for (Map<String, Object> map : enclosedInstruction) {
                                    System.out.println((String) map.get("instruction"));
                                    EnclosedInstruction enclosedInstructionItem = new EnclosedInstruction((String) map.get("instruction"), InstructionType.DEFAULT);

                                    instructionList.add(enclosedInstructionItem);
                                }
                                System.out.println("adding enclosed instructions... " + instructionList.size());
                            }


                            // additional instruction
                            if (tradeService.getDetails().get("additionalInstruction") != null && !tradeService.getDetails().get("additionalInstruction").toString().isEmpty()) {
                                String additionalInstructionString = (String) tradeService.getDetails().get("additionalInstruction");
                                additionalInstructionString = additionalInstructionString.substring(1, additionalInstructionString.length());

                                List<Map<String, Object>> additionalInstruction = UtilSetFields.stringOfListMapToListMap(additionalInstructionString);

                                for (Map<String, Object> map : additionalInstruction) {
                                    EnclosedInstruction additionalInstructionItem = new EnclosedInstruction((String) map.get("instruction"), InstructionType.NEW);

                                    instructionList.add(additionalInstructionItem);
                                }
                                System.out.println("adding additional instructions... " + instructionList.size());
                            }

                            exportBills.addEnclosedInstructions(instructionList);

                            exportBills.setLcDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.NEGOTIATED);

                            tradeProductRepository.merge(exportBills);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                            updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                            BCNegotiatedEvent bcNegotiatedEvent = new BCNegotiatedEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(bcNegotiatedEvent);
                        } else if (serviceType.equals(ServiceType.SETTLEMENT)) {
                            System.out.println("SETTLEMENT");

                            ExportBills exportBills = exportBillsRepository.load(tradeService.getDocumentNumber());

                            exportBills.settleExportBills(new BigDecimal((String) tradeService.getDetails().get("proceedsAmount")),
                                    (String) tradeService.getDetails().get("partialNego"));

                            exportBills.updateDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.SETTLED);

                            tradeProductRepository.merge(exportBills);

                            // ITRS
                            updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                            updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                            BCSettledEvent bcSettledEvent = new BCSettledEvent(tradeService, exportBills, tsTaggedEvent.getGltsNumber());
                            eventPublisher.publish(bcSettledEvent);
                        }
                    }
                } else if (documentClass.equals(DocumentClass.REBATE)){
                    String rebateDocumentNumber = (String) tradeService.getDetails().get("rebateDocumentNumber");

                    Rebate rebate = null;

                    if (rebateDocumentNumber != null) {
                        DocumentNumber documentNumber = new DocumentNumber(rebateDocumentNumber);

                        rebate= new Rebate(documentNumber, tradeService.getDetails());
                    } else {
                        rebate= new Rebate(tradeService.getDetails());
                    }

                    rebateRepository.persist(rebate);
                    eventPublisher.publish(new RebateCreatedEvent(tradeService, rebate, tsTaggedEvent.getGltsNumber()));

                } else if (DocumentClass.CDT.equals(documentClass)) {
                    if (ServiceType.REMITTANCE.equals(serviceType)) {
                        Map<String, Object> details = tradeService.getDetails();

                        PaymentRequestType paymentRequestType = null;
                        if (tradeService.getDetails().get("reportType").equals("IPF")) {
                            paymentRequestType = PaymentRequestType.IPF;
                        } else if (tradeService.getDetails().get("reportType").equals("FINAL_CDT")) {
                            paymentRequestType = PaymentRequestType.FINAL;
                        } else if (tradeService.getDetails().get("reportType").equals("ADVANCE_CDT")) {
                            paymentRequestType = PaymentRequestType.ADVANCE;
                        } else if (tradeService.getDetails().get("reportType").equals("EXPORT_CHARGES")) {
                            paymentRequestType = PaymentRequestType.EXPORT;
                        } else if (tradeService.getDetails().get("reportType").equals("IPF_EXPORT_CHARGES")) {
                            paymentRequestType = PaymentRequestType.IPF_EXPORT_CHARGES;
                        } else if (tradeService.getDetails().get("reportType").equals("FINAL_ADVANCE_CDT")) {
                            paymentRequestType = PaymentRequestType.FINAL_ADVANCE_CDT;
                        }
                        
                        
                        cdtRestServices.testRem(tradeServiceId.toString());		
                        

                        CDTRemittance cdtRemittance = new CDTRemittance(paymentRequestType, details);

                        cdtRemittanceRepository.persist(cdtRemittance);
						
                      //This is not included in AMLA format 1.0 
                        //as per dicussion with maam juliet 02/20/2015
//                        eventPublisher.publish(new CDTRemittanceCreatedEvent(tradeService, cdtRemittance, tsTaggedEvent.getGltsNumber()));
                    } else if (ServiceType.REFUND.equals(serviceType)) {
                        CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails(tradeService.getDetails().get("iedieirdNumber").toString());
                        
                        Boolean isCollectedInBranch = Boolean.FALSE;
                        if(tradeService.getDetails().get("modeOfRefund") != null){
                        	if(tradeService.getDetails().get("modeOfRefund").toString().equalsIgnoreCase("IBT_BRANCH")){
                        		isCollectedInBranch = Boolean.TRUE;
                        	}
                       
//                        paymentRequest.setStatus(CDTStatus.REFUNDED);
                        if(isCollectedInBranch){
                        	paymentRequest.forRefundPayment();
                        } else {
                        	paymentRequest.refundPayment();
                        }
//                        Map paymentTradeServiceMap = tradeServiceRepository.getTradeServiceBy2(new TradeServiceReferenceNumber(paymentRequest.getIedieirdNumber()), ServiceType.PAYMENT);
//                        System.out.println("paymentTradeServiceMap " + paymentTradeServiceMap.get("tradeServiceId").toString());

//                        Map<String, Object> tradeServiceIdMap = (Map<String, Object>) paymentTradeServiceMap.get("tradeServiceId");

//                        TradeService paymentTradeService = tradeServiceRepository.load(new TradeServiceId((String) tradeServiceIdMap.get("tradeServiceId")));

//                        paymentTradeService.tagStatus(null);

//                        tradeServiceRepository.merge(paymentTradeService);

//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(Calendar.HOUR_OF_DAY, 0);
//                        calendar.set(Calendar.MINUTE, 0);
//                        calendar.set(Calendar.SECOND, 0);
//                        calendar.set(Calendar.MILLISECOND, 0);
//
//                        TradeService currentTradeService = tradeServiceRepository.getCurrentTradeService(calendar.getTime(), paymentRequest.getUnitCode());
//                        currentTradeService.setPaymentStatus(PaymentStatus.UNPAID);
//
//                        tradeServiceRepository.merge(currentTradeService);

                        cdtPaymentRequestRepository.merge(paymentRequest);
                        } 
                      //This is not included in AMLA format 1.0 
                        //as per dicussion with maam juliet 02/20/2015
//                        eventPublisher.publish(new CDTRefundCreatedEvent(tradeService, paymentRequest, tsTaggedEvent.getGltsNumber()));
                    }
                } else if(DocumentClass.IMPORT_CHARGES.equals(documentClass)) {
                	if (ServiceType.PAYMENT.equals(serviceType) || ServiceType.PAYMENT_OTHER.equals(serviceType)) {
                		System.out.println("APPROVING PAYMENT OF OTHER IMPORT CHARGES");
                        LetterOfCredit letterOfCredit = null;
                        if (tradeService.getDocumentNumber() != null) {
                            DocumentNumber importChargesDocumentNumber = new DocumentNumber(tradeService.getDocumentNumber().toString().toUpperCase());
                            letterOfCredit = (LetterOfCredit)tradeProductRepository.load(importChargesDocumentNumber);
                        }
                        // letterOfCredit might be null; handled in the mapping
                        eventPublisher.publish(new ImportChargesPaidEvent(tradeService, letterOfCredit, tsTaggedEvent.getGltsNumber()));
                	}
                } else if(DocumentClass.EXPORT_CHARGES.equals(documentClass)) {
                	if (ServiceType.PAYMENT.equals(serviceType) || ServiceType.PAYMENT_OTHER.equals(serviceType)) {
                		System.out.println("APPROVING PAYMENT OF OTHER EXPORT CHARGES");
                		eventPublisher.publish(new ExportChargesPaidEvent(tradeService, tsTaggedEvent.getGltsNumber()));
                	} else if (ServiceType.REFUND.equals(serviceType)) {
                		System.out.println("APPROVING REFUND OF OTHER EXPORT CHARGES");
                		eventPublisher.publish(new ExportChargesRefundEvent(tradeService, tsTaggedEvent.getGltsNumber()));
                	}
                } else {
                    // TODO: For other TradeProducts
                }

                // checks if there is excess payment
                // ATTEMPTING TO SETUP AP FOR PRODUCT
                Payment savedProductPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                System.out.println("ATTEMPTING TO SETUP AP FOR PRODUCT");
                System.out.println("(savedProductPayment != null) " + savedProductPayment != null);
                String accountsPayableId = null;
                
                if (savedProductPayment != null) {
                	
                    BigDecimal amountDue; 
                    
                    amountDue = tradeService.getProductChargeAmount();
                    
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                	symbols.setGroupingSeparator(',');
                	symbols.setDecimalSeparator('.');
                	String pattern = "#,##0.0#";
                	DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
                	decimalFormat.setParseBigDecimal(true);
                    
                    if (documentClass.equals(DocumentClass.CORRES_CHARGE)){                                            	
                    	amountDue = (BigDecimal) decimalFormat.parse(tradeService.getDetails().get("amount").toString());
                    }else if(documentClass.equals(DocumentClass.EXPORT_ADVISING)){
                    	amountDue = (BigDecimal) decimalFormat.parse(tradeService.getDetails().get("totalAmountCharges").toString());
                    }
                                 
                    System.out.println("amountDue: "+amountDue);
                    
                    if (amountDue != null) {

                        System.out.println("TOTAL AMOUNT DUE: " + amountDue.toPlainString());

                        Set<PaymentDetail> savedPaymentDetails = savedProductPayment.getDetails();

                        Currency currency = null;

                        Boolean hasCheck = Boolean.FALSE;
                        BigDecimal paymentAmount = BigDecimal.ZERO;
                        AmountComputationService acs = null;
                        

                        for (PaymentDetail pd : savedPaymentDetails) {
                            if (pd.getStatus().equals(PaymentStatus.PAID)) {
                                if (pd.getPaymentInstrumentType().equals(PaymentInstrumentType.CHECK) ||
                                        pd.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE)) {
                                    hasCheck = Boolean.TRUE;
                                    if(pd.getAmountInLcCurrency().compareTo(paymentAmount) > 0){
                                    	paymentAmount = pd.getAmountInLcCurrency();
                                    	currency = pd.getCurrency();
                                    	acs = new AmountComputationService(pd.getSpecialRateThirdToUsd(), pd.getSpecialRateThirdToPhp(), pd.getSpecialRateUsdToPhp(), pd.getUrr());
                                    }
                                }
                            }
                        }
                        
                        //since excess payment requires a payment type of check or remittance, Accounts Payable will only be computed if either exists.
                        if(acs != null){
                        System.out.println("-----currency: " + currency + "-----");
                        
                        BigDecimal totalAmountPaid = savedProductPayment.getTotalPaid(currency != null ? currency : tradeService.getProductChargeCurrency());
                        
                        System.out.println("hasCheck: " + hasCheck);

                        Currency tradeServiceCurrency = null;
                        if (tradeService.getDetails().get("currency") != null) {
                        	tradeServiceCurrency = Currency.getInstance((String) tradeService.getDetails().get("currency"));
                        } else if (tradeService.getDetails().get("negotiationCurrency") != null) {
                        	tradeServiceCurrency = Currency.getInstance((String) tradeService.getDetails().get("negotiationCurrency"));
                        } else if (tradeService.getDetails().get("lcCurrency") != null) {
                        	tradeServiceCurrency = Currency.getInstance((String) tradeService.getDetails().get("lcCurrency"));
                        } else if (tradeService.getDetails().get("productCurrency") != null) {
                        	tradeServiceCurrency = Currency.getInstance((String) tradeService.getDetails().get("productCurrency"));
                        }
                        System.out.println("tradeServiceCurrency: " + tradeServiceCurrency.toString()+"\n");
                        System.out.println("TOTAL AMOUNT PAID: " + (acs.computeEquivalentAmount(totalAmountPaid, currency, tradeServiceCurrency)).toPlainString());

                        BigDecimal excessPayment = (totalAmountPaid.subtract((acs.computeEquivalentAmount(amountDue, tradeServiceCurrency, currency)))).setScale(2, BigDecimal.ROUND_HALF_UP);
                        System.out.println("excessPayment: " + (acs.computeEquivalentAmount(excessPayment, currency, tradeServiceCurrency)));
                        System.out.println("(excessPayment.compareTo(BigDecimal.ZERO) > 0 && hasCheck.equals(Boolean.TRUE)): " + (excessPayment.compareTo(BigDecimal.ZERO) > 0 && hasCheck.equals(Boolean.TRUE)));
                        if (excessPayment.compareTo(BigDecimal.ZERO) > 0 && hasCheck.equals(Boolean.TRUE)) {
                            System.out.println(">>>>>>>>>>>>>>>> AUTO SETUP <<<<<<<<<<<<<<<<");
                            SettlementAccountNumber settlementAccountNumber = new SettlementAccountNumber(tradeService.getTradeProductNumber().toString());


                            Date bookingDate = new Date();

                            String natureOfTransaction = null;

                            if(containsNonLCDocumentClasses(documentClass)) {
                                natureOfTransaction = buildLastNonLCTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                            }

                            if(containsLCDocumentClasses(documentClass)) {
                                natureOfTransaction = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                            }
                            System.out.println("natureOfTransaction: "+natureOfTransaction);
                            AccountsPayable accountsPayable = new AccountsPayable(settlementAccountNumber,
                                    currency,
                                    (String) tradeService.getDetails().get("cifNumber"),
                                    (String) tradeService.getDetails().get("cifName"),
                                    (String) tradeService.getDetails().get("accountOfficer"),
                                    (String) tradeService.getDetails().get("ccbdBranchUnitCode"),
                                    bookingDate,
                                    natureOfTransaction,
                                    excessPayment,
                                    tradeService.getTradeServiceId());

//                            accountsPayable.credit(excessPayment, currency, ReferenceType.TFS_SETUP_AP_PRODUCT, bookingDate, tradeService.getTradeProductNumber().toString());
                            accountsPayable.credit(excessPayment, currency, ReferenceType.TFS_SETUP_AP_PRODUCT, bookingDate, tradeService.getTradeProductNumber().toString(), natureOfTransaction);
                            accountsPayableRepository.persist(accountsPayable);

                            accountsPayableId = accountsPayable.getId();
                        }

                    }
                    }
                }

                // ATTEMPTING TO SETUP AP FOR SERVICE
                Payment savedServicePayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
                System.out.println("ATTEMPTING TO SETUP AP FOR SERVICE");
                System.out.println("(savedServicePayment != null) " + savedServicePayment != null);
                if (savedServicePayment != null) {

                    BigDecimal amountDue = tradeService.getTotalServiceChargesAmount();

                    Boolean isProductPaymentCurrencyNonPhp = Boolean.FALSE;
                    if (savedProductPayment != null) {
                        isProductPaymentCurrencyNonPhp = savedProductPayment.checkIfPaymentCurrenciesIsNonPhp();
                    }

                    Currency paymentCurrency = Currency.getInstance("PHP");
                    Boolean isServicePaymentCurrencyNonPhp = Boolean.FALSE;
                    if (savedServicePayment != null) {
                        isServicePaymentCurrencyNonPhp = savedServicePayment.checkIfPaymentCurrenciesIsNonPhp();

                        if (isServicePaymentCurrencyNonPhp) {
                            paymentCurrency = Currency.getInstance("USD");
                        }
                        amountDue = tradeService.getTotalServiceChargesAmount(isProductPaymentCurrencyNonPhp); //CILEX if non php payment is present
                    }


                    Boolean isProductPaymentDTRLOAN = Boolean.FALSE;
                    if (savedProductPayment != null &&
                            (
                                    (tradeService.getDocumentClass().equals(DocumentClass.DP) || tradeService.getDocumentClass().equals(DocumentClass.DA)
                                            || tradeService.getDocumentClass().equals(DocumentClass.OA) || tradeService.getDocumentClass().equals(DocumentClass.DR))
                                            && tradeService.getDocumentType() != null && tradeService.getDocumentType().equals(DocumentType.DOMESTIC)
                            )
                            ) {
                        isProductPaymentDTRLOAN = savedProductPayment.hasTrLoan();
                        amountDue = tradeService.getTotalServiceChargesAmount(isProductPaymentCurrencyNonPhp, isProductPaymentDTRLOAN);
                    }

                    if (amountDue != null) {

                        System.out.println("TOTAL AMOUNT DUE: " + amountDue.toPlainString());

                        Set<PaymentDetail> savedPaymentDetails = savedServicePayment.getDetails();

                        if (tradeService.getServiceChargesCurrency() != null) {

                            BigDecimal totalAmountPaid = savedServicePayment.getTotalPaid(tradeService.getServiceChargesCurrency());
                            Currency currency = null;

                            Boolean hasCheck = Boolean.FALSE;

                            for (PaymentDetail pd : savedPaymentDetails) {
                                if (pd.getStatus().equals(PaymentStatus.PAID)) {
                                    if (pd.getPaymentInstrumentType().equals(PaymentInstrumentType.CHECK) ||
                                            pd.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE)) {
                                        hasCheck = Boolean.TRUE;
                                        currency = pd.getCurrency();
                                    }
                                }
                            }

                            System.out.println("TOTAL AMOUNT PAID: " + totalAmountPaid.toPlainString());

                            BigDecimal excessPayment = totalAmountPaid.subtract(amountDue);

                            if (excessPayment.compareTo(BigDecimal.ZERO) > 0 && hasCheck.equals(Boolean.TRUE)) {
                                System.out.println(">>>>>>>>>>>>>>>> AUTO SETUP <<<<<<<<<<<<<<<<");

                                Date bookingDate = new Date();

//                            if (tradeService.getDetails().get("currency") != null) {
//                                currency = Currency.getInstance((String) tradeService.getDetails().get("currency"));
//                            } else if (tradeService.getDetails().get("negotiationCurrency") != null) {
//                                currency = Currency.getInstance((String) tradeService.getDetails().get("negotiationCurrency"));
//                            } else if (tradeService.getDetails().get("lcCurrency") != null) {
//                                currency = Currency.getInstance((String) tradeService.getDetails().get("lcCurrency"));
//                            }
//                            String currencyDetails = tradeService.getDetails().get("currency").toString();
//
//                            if (tradeService.getDetails().get("negotiationCurrency") != null) {
//                                currencyDetails = tradeService.getDetails().get("negotiationCurrency").toString();
//                            }
//
//                            if (currencyDetails != null) {
//                                currency = Currency.getInstance(currencyDetails);
//                            }

                                String natureOfTransaction = null;

                                if(containsNonLCDocumentClasses(documentClass)) {
                                    natureOfTransaction = buildLastNonLCTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                                }

                                if(containsLCDocumentClasses(documentClass)) {
                                    natureOfTransaction = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                                }

                                AccountsPayable accountsPayable = new AccountsPayable(new SettlementAccountNumber(tradeService.getTradeProductNumber().toString()),
                                        currency,
                                        (String) tradeService.getDetails().get("cifNumber"),
                                        (String) tradeService.getDetails().get("cifName"),
                                        (String) tradeService.getDetails().get("accountOfficer"),
                                        (String) tradeService.getDetails().get("ccbdBranchUnitCode"),
                                        bookingDate,
                                        natureOfTransaction,
                                        excessPayment,
                                        tradeService.getTradeServiceId());

//                            accountsPayable.credit(excessPayment, currency, ReferenceType.TFS_SETUP_AP_SERVICE, bookingDate, tradeService.getTradeProductNumber().toString());
                                accountsPayable.credit(excessPayment, currency, ReferenceType.TFS_SETUP_AP_SERVICE, bookingDate, tradeService.getTradeProductNumber().toString(), natureOfTransaction);
                                accountsPayableRepository.persist(accountsPayable);
                            }
                        }
                    }
                }

//            } else if (tradeServiceStatus != null && tradeServiceStatus.equals(TradeServiceStatus.PREPARED)) {
//
//                if (documentClass.equals(DocumentClass.LC)) {
//
//                    switch (serviceType) {
//
//                        case NEGOTIATION:
//
//                            // Un-earmark from Facility
//                            if (tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR) || tradeService.getDocumentSubType1().equals(DocumentSubType1.STANDBY)) {
//
//                                BigDecimal negotiationAmount = new BigDecimal((String) tradeService.getDetails().get("negotiationAmount"));
//
//                                Currency currency = null;
//                                if (tradeService.getProductChargeCurrency() != null) {
//                                    currency = Currency.getInstance(tradeService.getProductChargeCurrency().getCurrencyCode()); // Use tradeservice
//                                }
//
//                                if (tradeService.getDetails().get("currency") != null) {
//                                    currency = Currency.getInstance((String) tradeService.getDetails().get("currency"));
//                                } else if (tradeService.getDetails().get("negotiationCurrency") != null) {
//                                    currency = Currency.getInstance((String) tradeService.getDetails().get("negotiationCurrency"));
//                                }
//
//                                // Reinstatement
//                                Boolean isReinstated = Boolean.FALSE;
//                                if (tradeService.isForReinstatement()) {
//                                    isReinstated = Boolean.TRUE;
//                                }
//
//                                // If NEGOTIATION, subtract the Negotiation Amount from Outstanding Balance and update the earmark.
//                                BigDecimal outstandingBalance = new BigDecimal((String) tradeService.getDetails().get("outstandingBalance"));
//                                System.out.println("\n$$$ FACILITY UN-EARMARKING (LC Negotiation) :::::::::\n");
//                                facilityService.updateAvailmentAmount(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), outstandingBalance.subtract(negotiationAmount), isReinstated);
//                            }
//
//                            break;
//                    }
//                }
//
//            } else if (tradeServiceStatus != null && tradeServiceStatus.equals(TradeServiceStatus.RETURNED_TO_BRANCH)) {
//
//                System.out.println("\n$$$ TradeService RETURNED :::::::::\n");
//
//                if (documentClass.equals(DocumentClass.LC)) {
//
//                    switch (serviceType) {
//
//                        case NEGOTIATION:
//
//                            // Return the earmark (revert to outstanding balance)
//                            if (tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR) || tradeService.getDocumentSubType1().equals(DocumentSubType1.STANDBY)) {
//
//                                Currency currency = null;
//                                if (tradeService.getProductChargeCurrency() != null) {
//                                    currency = Currency.getInstance(tradeService.getProductChargeCurrency().getCurrencyCode()); // Use tradeservice
//                                }
//
//                                if (tradeService.getDetails().get("currency") != null) {
//                                    currency = Currency.getInstance((String) tradeService.getDetails().get("currency"));
//                                } else if (tradeService.getDetails().get("negotiationCurrency") != null) {
//                                    currency = Currency.getInstance((String) tradeService.getDetails().get("negotiationCurrency"));
//                                }
//
//                                // Reinstatement
//                                Boolean isReinstated = Boolean.FALSE;
//                                if (tradeService.isForReinstatement()) {
//                                    isReinstated = Boolean.TRUE;
//                                }
//
//                                BigDecimal outstandingBalance = new BigDecimal((String) tradeService.getDetails().get("outstandingBalance"));
//                                System.out.println("\n$$$ FACILITY RE-EARMARKING (LC Negotiation) :::::::::\n");
//                                facilityService.updateAvailmentAmount(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), outstandingBalance, isReinstated);
//                            }
//                            
//                            break;
//                    }
//                }

            } else {
                System.out.println("hahahahahhahahaahahahahaaha");
                if (tradeServiceStatus == null) {
                    System.out.println("heheheehhehehehehehehehehehe");
                    if (documentClass.equals(DocumentClass.LC)) {

                        switch (serviceType) {

                            case NEGOTIATION:

                                if (tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR)) {

                                    BigDecimal overdrawnAmount = new BigDecimal((String) tradeService.getDetails().get("overdrawnAmount"));

                                    if (overdrawnAmount == null || (overdrawnAmount != null && (overdrawnAmount.doubleValue() == 0.0D))) {

                                        System.out.println("\n``````````````` (NEGOTIATION) REVERSING AND DELETING ALL PRODUCT PAYMENTS `````````````````\n");

                                        // If LC Ap Cash Amount is equal or greater than the Nego Amount, reverse all Product payments
                                        // made for the current Nego, then delete the Payment object.
                                        Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);

                                        if (payment != null) {

                                            System.out.println("\n############ Reversing and deleting all Product payments NOW...\n");
                                            payment.reverseAllItemPayments();
                                            paymentRepository.delete(payment);
                                        }
                                    }
                                }
                                tradeService = TradeServiceService.setSpecialRates(tradeService, tradeService.getDetails());
                                tradeService.updateDetails(parameterMap, userActiveDirectoryId);
                                tradeServiceRepository.merge(tradeService);

                                break;
                        }
                    } else if (documentClass.equals(DocumentClass.BC)) {
                        if (serviceType.equals(ServiceType.NEGOTIATION)) {
                            if (parameterMap.get("exlcAdviseNumber") != null && parameterMap.get("paymentMode").equals("LC")) {
                                System.out.println("exlcAdviseNumber : " + parameterMap.get("exlcAdviseNumber"));

                                if (!(parameterMap.get("exlcAdviseNumber").equals(tradeService.getDetails().get("adviseNumber")))) {
                                    ExportAdvising exportAdvising = (ExportAdvising) tradeProductRepository.load(new DocumentNumber((String) parameterMap.get("exlcAdviseNumber")));

                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                                    Map details = tradeService.getDetails();

                                    System.out.println("details : " + details);

                                    Map tsDetails = tradeService.getDetails();

                                    tsDetails.put("adviseNumber", exportAdvising.getDocumentNumber().toString());
                                    tsDetails.put("lcNumber", exportAdvising.getLcNumber().toString());
                                    tsDetails.put("lcIssueDate", sdf.format(exportAdvising.getLcIssueDate()));
                                    tsDetails.put("lcType", exportAdvising.getLcType());

                                    if (exportAdvising.getLcType().equals(LCType.REGULAR)) {
                                        tsDetails.put("lcTenor", exportAdvising.getLcTenor().toString());
                                    } else {
                                        tsDetails.remove("lcTenor");
                                    }

                                    if (exportAdvising.getLcTenor() != null) {
                                        tsDetails.put("usanceTerm", exportAdvising.getUsanceTerm().toString());
                                    }

                                    tsDetails.put("lcCurrency", exportAdvising.getLcCurrency().toString());
                                    tsDetails.put("lcAmount", exportAdvising.getLcAmount().toString());
                                    tsDetails.put("lcExpiryDate", sdf.format(exportAdvising.getLcExpiryDate()));
                                    tsDetails.put("issuingBankCode", exportAdvising.getIssuingBank());
                                    tsDetails.put("issuingBankAddress", exportAdvising.getIssuingBankAddress());
                                    tsDetails.put("reimbursingBankCode", exportAdvising.getReimbursingBank());

                                    tradeService.setDetails(tsDetails);

                                    //ITRS
                                    updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                                    updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                                    tradeServiceRepository.merge(tradeService);
                                }
                            } else {
                                Map tsDetails = tradeService.getDetails();

                                if (!"LC".equals(tsDetails.get("paymentMode"))) {
                                    tsDetails.remove("adviseNumber");
                                    tsDetails.remove("lcNumber");
                                    tsDetails.remove("lcIssueDate");
                                    tsDetails.remove("lcType");
                                    tsDetails.remove("lcTenor");
                                    tsDetails.remove("usanceTerm");
                                    tsDetails.remove("lcCurrency");
                                    tsDetails.remove("lcAmount");
                                    tsDetails.remove("lcExpiryDate");
                                    tsDetails.remove("issuingBankCode");
                                    tsDetails.remove("issuingBankAddress");
                                    tsDetails.remove("reimbursingBankCode");

                                    tradeService.setDetails(tsDetails);

                                    // ITRS
                                    updateItrsFields(tradeService.getDocumentNumber(), tradeService.getDetails());
                                    updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());

                                    tradeServiceRepository.merge(tradeService);
                                }
                            }
                        } else if (serviceType.equals(ServiceType.SETTLEMENT)) {
                        	//JJADD
                        	if (tradeService.getDetails().containsKey("bpAmount") && tradeService.getDetails().containsKey("proceedsAmount")) {
                        		if(!(tradeService.getDetails().get("bpAmount").toString()).equals("") && !(tradeService.getDetails().get("proceedsAmount").toString()).equals("")) {
                        			
                        			BigDecimal proceedsAmount = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get("proceedsAmount").toString()));
            						BigDecimal bpAmount = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get("bpAmount").toString()));
            						
            						System.out.println("bpAmount = " + bpAmount + "\t proceedsAmount = " + proceedsAmount);
            						Boolean noPaymentAtAll = Boolean.FALSE;
        							if (proceedsAmount.compareTo(bpAmount) == 0){
        								noPaymentAtAll = Boolean.TRUE;
        							} else if(proceedsAmount.compareTo(bpAmount) == -1){
        								noPaymentAtAll = Boolean.TRUE;
        							}            						
        							
        							if (noPaymentAtAll) {
        				                tradeService.setAsNoPaymentRequired();

        				                // ITRS
                                        updateItrsParticulars(tradeService.getDocumentNumber(), tradeService.getDetails());
        				                tradeServiceRepository.merge(tradeService);
        				            }
                        		}
                        		
                        	}
                        	
                        }
                    }

                    System.out.println(documentClass);
                    System.out.println(tradeServiceId);
                    System.out.println(tradeService.getStatus());

                    String gltsNumber = gltsSequenceRepository.getGltsSequence();
                    if(gltsNumber!=null && documentClass !=null&&  tradeService.getStatus()!=null && !(tradeService.getStatus().equals(TradeServiceStatus.RETURNED_TO_BRANCH) || tradeService.getStatus().equals(TradeServiceStatus.MARV)) ){
                        accountingService.deleteActualEntries(tradeServiceId);
                        accountingService.generateActualEntriesWebService(tradeServiceId, gltsNumber, tradeService.getStatus().toString());
                        gltsSequenceRepository.incrementGltsSequence();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error in updateTradeService(TradeServiceUpdatedEvent)",e);
        }
    }

    @EventListener
    public void taggedTradeService(TradeServiceTaggedEvent tradeServiceTaggedEvent) {

        System.out.println("in taggedTradeService");
        Map<String, Object> paramMap = tradeServiceTaggedEvent.getParameterMap();
        TradeServiceId tradeServiceId = tradeServiceTaggedEvent.getTradeServiceId();
        TradeServiceStatus tradeServiceStatus = tradeServiceTaggedEvent.getTradeServiceStatus();
        UserActiveDirectoryId userActiveDirectoryId = tradeServiceTaggedEvent.getUserActiveDirectoryId();
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        // String gltsNumber = gltsSequenceRepository.getGltsSequence();
        String gltsNumber = tradeServiceTaggedEvent.getGltsNumber();

        //TradeService taggedTradeService = tradeServiceRepository.load(tradeServiceId);
//        Payment paymentProduct = paymentRepository.get(taggedTradeService.getTradeServiceId(), ChargeType.PRODUCT);
        Payment paymentProduct = paymentRepository.get(tradeServiceId, ChargeType.PRODUCT);
        if (tradeServiceStatus == TradeServiceStatus.PREPARED) {
            System.out.println("in taggedTradeService with TradeServiceStatus.PREPARED");
            String referenceType = (String) paramMap.get("referenceType");
            accountingService.deleteActualEntries(tradeServiceId);
            
            // generates actual entries when payment status is not unpaid
            if(!(tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING) && tradeService.getPaymentStatus().equals(PaymentStatus.UNPAID))){
            	accountingService.generateActualEntriesWebService(tradeServiceId, gltsNumber, tradeServiceStatus.toString());
            }
            
            //accountingService.generateActualEntries(taggedTradeService, gltsNumber, tradeServiceStatus.toString());
            gltsSequenceRepository.incrementGltsSequence();
//            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
//            if(!DocumentClass.BC.equals(tradeService.getDocumentClass())){
//                chargesService.removeChargesNotUsed(tradeServiceId, paymentProduct);
//            }
        } else if (tradeServiceStatus == TradeServiceStatus.RETURNED) {
            System.out.println("in taggedTradeService with TradeServiceStatus.RETURNED");
            accountingService.deleteActualEntries(tradeServiceId);
            UserActiveDirectoryId targetUser = tradeServiceTaggedEvent.getUserActiveDirectoryId();
            System.out.println("targetUser >> " + targetUser);
        } else if (tradeServiceStatus == TradeServiceStatus.APPROVED || tradeServiceStatus == TradeServiceStatus.POSTED) {
            System.out.println("in taggedTradeService with tradeServiceStatus == TradeServiceStatus.APPROVED || tradeServiceStatus == TradeServiceStatus.POSTED");
            System.out.println("tradeServiceStatus >> " + tradeServiceStatus);
            UserActiveDirectoryId targetUser = tradeServiceTaggedEvent.getUserActiveDirectoryId();
            System.out.println("targetUser >> " + targetUser);
            String referenceType = (String) paramMap.get("referenceType");
            accountingService.deleteActualEntries(tradeServiceId);
            //accountingService.deleteActualEntries(taggedTradeService.getTradeServiceId());
            
            // generates actual entries when payment status is not unpaid
            if(!(tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING) && tradeService.getPaymentStatus().equals(PaymentStatus.UNPAID))){
            	accountingService.generateActualEntriesWebService(tradeServiceId, gltsNumber, tradeServiceStatus.toString());
            }
            
            //accountingService.generateActualEntries(taggedTradeService, gltsNumber, tradeServiceStatus.toString());
            gltsSequenceRepository.incrementGltsSequence();
//            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
//            if(!DocumentClass.BC.equals(tradeService.getDocumentClass())){
//                chargesService.removeChargesNotUsed(tradeServiceId, paymentProduct);
//            }
        }
    }

    @EventListener
    public void afterPaymentOrReversalGenerateAccounting(TradeServiceSingleItemPaidEvent tradeServiceSingleItemPaidEvent) {

        System.out.println("in afterPaymentOrReversalGenerateAccounting");
        TradeServiceId tradeServiceId = tradeServiceSingleItemPaidEvent.getTradeServiceId();
        TradeServiceStatus tradeServiceStatus = tradeServiceSingleItemPaidEvent.getTradeServiceStatus();
        UserActiveDirectoryId userActiveDirectoryId = tradeServiceSingleItemPaidEvent.getUserActiveDirectoryId();
        String gltsNumber = gltsSequenceRepository.getGltsSequence();

        TradeService taggedTradeService = tradeServiceRepository.load(tradeServiceId);
        Payment paymentProduct = paymentRepository.get(taggedTradeService.getTradeServiceId(), ChargeType.PRODUCT);

        accountingService.deleteActualEntries(tradeServiceId);

        // set tradeServiceStatus to empty string. this is special case for CDT since CDT has no routing, therefore there
        // is no status applicable
        String tradeServiceStatusString = "";

        if (tradeServiceStatus != null) {
            tradeServiceStatusString = tradeServiceStatus.toString();
        }

        accountingService.generateActualEntries(taggedTradeService, gltsNumber, tradeServiceStatusString);

        gltsSequenceRepository.incrementGltsSequence();

    }

    private String buildLastNonLCTransactionString(
            ServiceType serviceType,
            DocumentClass documentClass,
            DocumentType documentType,
            DocumentSubType1 documentSubType1,
            DocumentSubType2 documentSubType2) {

        String docTypeStr = "FX";

        System.out.println("serviceType.toString() >> " + (serviceType != null ? serviceType.toString() : ""));
        System.out.println("documentClass.toString() >> " + (documentClass != null ? documentClass.toString() : ""));
        System.out.println("documentType.toString() >> " +  (documentType != null ? documentType.toString() : ""));
        System.out.println("documentSubType1.toString() >> " +  (documentSubType1 != null ? documentSubType1.toString() : ""));
        System.out.println("documentSubType2.toString() >> " +  (documentSubType2 != null ? documentSubType2.toString() : ""));

        if (documentType.equals(DocumentType.DOMESTIC)) {
            docTypeStr = "DM";
        }

        StringBuilder builder = new StringBuilder("");
        builder.append(docTypeStr);

        builder.append(documentClass.toString().toUpperCase());
        builder.append(" ");

        if (serviceType.equals(ServiceType.NEGOTIATION_ACCEPTANCE)) {
            builder.append("Negotiation Acceptance");
        } else if (serviceType.equals(ServiceType.NEGOTIATION_ACKNOWLEDGEMENT)) {
            builder.append("Negotiation Acknowledgement");
        } else {
            builder.append(WordUtils.capitalizeFully(serviceType.toString()));
        }

        return builder.toString();
    }

    private String buildLastLcTransactionString(
            ServiceType serviceType,
            DocumentClass documentClass,
            DocumentType documentType,
            DocumentSubType1 documentSubType1,
            DocumentSubType2 documentSubType2) {

//        String docTypeStr = "FXLC";
        String docTypeStr = "FX";

        System.out.println("serviceType.toString() >> " + serviceType.toString());
        System.out.println("documentClass.toString() >> " + documentClass.toString());
        System.out.println("documentType.toString() >> " + documentType.toString());
        System.out.println("documentSubType1.toString() >> " + documentSubType1.toString());
        System.out.println("documentSubType2.toString() >> " + documentSubType2.toString());

        if (documentType.equals(DocumentType.DOMESTIC)) {
            docTypeStr = "DM";
        }

        StringBuilder builder = new StringBuilder("");
        builder.append(docTypeStr);

        if (documentClass.equals(DocumentClass.INDEMNITY)) {
            builder.append("LC ");
            builder.append(WordUtils.capitalizeFully(documentClass.toString()));
        } else {
            builder.append(documentClass.toString().toUpperCase());

            builder.append(" ");
            builder.append(WordUtils.capitalizeFully(documentSubType1.toString()));
        }

        builder.append(" ");



        if (serviceType.equals(ServiceType.NEGOTIATION_DISCREPANCY)) {
            builder.append("Negotiation Discrepancy");
        } else {
            builder.append(WordUtils.capitalizeFully(serviceType.toString()));
        }

        return builder.toString();
    }

    private String buildLastLcTransactionString(ServiceType serviceType,
                                                DocumentClass documentClass, DocumentType documentType) {

        String docTypeStr = "FX";

        System.out.println("serviceType.toString() >> " + serviceType.toString());
        System.out.println("documentClass.toString() >> " + documentClass.toString());
        System.out.println("documentType.toString() >> " + documentType.toString());

        if (documentType.equals(DocumentType.DOMESTIC)) {
            docTypeStr = "DM";
        }

        StringBuilder builder = new StringBuilder("");
        builder.append(docTypeStr);

        switch (documentClass) {
            case DA:
            case DP:
            case OA:
            case DR:
                builder.append(" ");
                builder.append(documentClass.toString().toUpperCase());
                break;
        }

        builder.append(" ");
        builder.append(WordUtils.capitalizeFully(serviceType.toString().replaceAll("_", " ")));
        System.out.println("builder.toString() >> " + builder.toString());
        return builder.toString();
    }

    @EventListener
    public void abortTsdInitiated(TradeServiceTaggedEvent tradeServiceTaggedEvent) {
        System.out.println("ABORTING TSD INITIATED");
        if (tradeServiceTaggedEvent.getTradeServiceStatus().equals(TradeServiceStatus.ABORTED) &&
                ("DATA_ENTRY".equals(tradeServiceTaggedEvent.getParameterMap().get("referenceType").toString())) ||
                "OUTGOING_MT".equals(tradeServiceTaggedEvent.getParameterMap().get("referenceType").toString())) {

            UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId("NA");

            // updates tradeService
            TradeService tradeService = tradeServiceRepository.load(tradeServiceTaggedEvent.getTradeServiceId());
            tradeService.setUserActiveDirectoryId(userActiveDirectoryId);

            if (tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING)) {
                List<TradeService> tradeServiceList = tradeServiceRepository.load(tradeService.getDocumentNumber());

                for (TradeService tradeServiceItem : tradeServiceList) {
                    Map<String, Object> tradeServiceDetails = tradeServiceItem.getDetails();

                    if (tradeServiceDetails != null && tradeServiceItem.getStatus() != null) {
                        if (tradeServiceDetails.containsKey("hasDuplicate") && tradeServiceItem.getStatus().equals(TradeServiceStatus.APPROVED)) {
                            tradeServiceDetails.remove("hasDuplicate");
                        }
                    }

                    tradeService.setDetails(tradeServiceDetails);

                    tradeServiceRepository.merge(tradeServiceItem);
                }
            }

            tradeServiceRepository.merge(tradeService);

            // updates task
            Task task = taskRepository.load(new TaskReferenceNumber(tradeServiceTaggedEvent.getTradeServiceId().toString()));
            task.updateStatus(TaskStatus.ABORTED, userActiveDirectoryId);

            taskRepository.merge(task);
        }
    }

    private Boolean containsNonLCDocumentClasses(DocumentClass documentClass) {
        if (documentClass.equals(DocumentClass.DA) ||
                documentClass.equals(DocumentClass.DP) ||
                documentClass.equals(DocumentClass.OA) ||
                documentClass.equals(DocumentClass.DR)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private Boolean containsLCDocumentClasses(DocumentClass documentClass) {
        if (documentClass.equals(DocumentClass.LC) ||
                documentClass.equals(DocumentClass.BG) ||
                documentClass.equals(DocumentClass.BE) ||
                documentClass.equals(DocumentClass.BGBE) ||
                documentClass.equals(DocumentClass.INDEMNITY)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private void updateItrsFields(DocumentNumber documentNumber,  Map< String, Object> map) {
    	TradeProduct tradeProduct = tradeProductRepository.load(documentNumber);

    	if(map.get("commodityCode") != null && !map.get("commodityCode").toString().equalsIgnoreCase("")
    			&& map.get("serviceType") != null && !map.get("serviceType").toString().equalsIgnoreCase("AMENDMENT")) {
    		tradeProduct.setCommodityCode(map.get("commodityCode").toString().trim());
    	}
    	if(map.get("participantCode") != null && !map.get("participantCode").toString().equalsIgnoreCase(""))
    		tradeProduct.setParticipantCode(map.get("participantCode").toString().trim());
    	if(map.get("tinNumber") != null && !map.get("tinNumber").toString().equalsIgnoreCase(""))
    		tradeProduct.setTinNumber(map.get("tinNumber").toString().trim());
    	if(map.get("commodityCode") != null && !map.get("commodityCode").toString().equalsIgnoreCase("")
			 || map.get("tinNumber") != null && !map.get("tinNumber").toString().equalsIgnoreCase("")
			 || map.get("participantCode") != null && !map.get("participantCode").toString().equalsIgnoreCase(""))
    		tradeProductRepository.persist(tradeProduct);
    }

    private void updateItrsParticulars(DocumentNumber documentNumber,  Map< String, Object> map) {
    	ExportBills exportBills = (ExportBills) tradeProductRepository.load(documentNumber);
    	if(map.get("particulars") != null && !map.get("particulars").toString().equalsIgnoreCase("")) {
    		exportBills.setParticulars(map.get("particulars").toString().trim());
    		tradeProductRepository.persist(exportBills);
    	}
    }

    class AmountComputationService {
    	private BigDecimal thirdToUsd;
    	private BigDecimal thirdToPhp;
    	private BigDecimal usdToPhp;
    	private BigDecimal urr;
    	
    	AmountComputationService(BigDecimal thirdToUsd, BigDecimal thirdToPhp, BigDecimal usdToPhp, BigDecimal urr){
    		this.thirdToUsd = thirdToUsd;
    		this.thirdToPhp = thirdToPhp;
    		this.usdToPhp = usdToPhp;
    		this.urr = urr;
    	}
    	
    	private BigDecimal computeEquivalentAmount(BigDecimal amount, Currency originalCurrency, Currency equivalentCurrency){
    		if(originalCurrency != null && equivalentCurrency != null)
    		if(!originalCurrency.equals(equivalentCurrency)){
            	if(originalCurrency.equals(Currency.getInstance("USD")) && equivalentCurrency.equals(Currency.getInstance("PHP"))){
            		return amount.multiply(usdToPhp != null ? usdToPhp : urr).setScale(2, BigDecimal.ROUND_HALF_UP);
            	} else if(originalCurrency.equals(Currency.getInstance("PHP")) && equivalentCurrency.equals(Currency.getInstance("USD"))){
            		return amount.divide(usdToPhp != null ? usdToPhp : urr, 2, BigDecimal.ROUND_HALF_UP);
            	} else if(!originalCurrency.equals(Currency.getInstance("USD")) && !originalCurrency.equals(Currency.getInstance("PHP"))){
            		if(equivalentCurrency.equals(Currency.getInstance("PHP"))){
            			if(thirdToPhp != null)
            				return amount.multiply(thirdToPhp).setScale(2, BigDecimal.ROUND_HALF_UP);
            			else if(thirdToUsd != null)
            				return amount.multiply(thirdToUsd).multiply(usdToPhp != null ? usdToPhp : urr).setScale(2, BigDecimal.ROUND_HALF_UP);
            		} else if(equivalentCurrency.equals(Currency.getInstance("USD"))){
            			if(thirdToUsd != null){
            				return amount.multiply(thirdToUsd).setScale(2, BigDecimal.ROUND_HALF_UP);
            			} else {
            				return amount.multiply(thirdToPhp).divide(usdToPhp != null ? usdToPhp : urr, 2, BigDecimal.ROUND_HALF_UP);
            			}
            		}
            	}
            }
    		return amount;
    	}
    }
}
