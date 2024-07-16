package com.ucpb.tfs.application.service;

import com.ucpb.tfs.application.bootstrap.AccountingLookup;
import com.ucpb.tfs.application.bootstrap.ChargesLookup;
import com.ucpb.tfs.domain.accounting.*;
import com.ucpb.tfs.domain.accounting.enumTypes.AccountingEntryType;
import com.ucpb.tfs.domain.accounting.enumTypes.BookCode;
import com.ucpb.tfs.domain.accounting.enumTypes.BookCurrency;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository;
import com.ucpb.tfs.domain.cdt.RefPas5Client;
import com.ucpb.tfs.domain.cdt.RefPas5ClientRepository;
import com.ucpb.tfs.domain.corresCharges.CorresChargeActual;
import com.ucpb.tfs.domain.corresCharges.CorresChargeActualRepository;
import com.ucpb.tfs.domain.corresCharges.CorresChargeAdvance;
import com.ucpb.tfs.domain.corresCharges.CorresChargeAdvanceRepository;
import com.ucpb.tfs.domain.corresCharges.enumTypes.CorresChargeType;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.ExportBills;
import com.ucpb.tfs.domain.product.ExportBillsRepository;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.product.LetterOfCreditRepository;
import com.ucpb.tfs.domain.product.TradeProduct;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.product.enums.LCType;
import com.ucpb.tfs.domain.reference.*;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.domain.sysparams.RefBank;
import com.ucpb.tfs.domain.sysparams.RefBankRepository;
import com.ucpb.tfs.interfaces.repositories.GlMastRepository;
import com.ucpb.tfs.interfaces.services.RatesService;
import com.ucpb.tfs2.application.service.OtherChargesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: giancarlo
 * Date: 9/29/12
 * Time: 10:13 PM
 * Service that is used in the generation of Accounting Entries.
 */
 
 /**
 	(revision)
	SCR/ER Number: ER# 20151009-040
	SCR/ER Description: Effective date of cancellation entries = expiry date, which is wrong.
	[Revised by:] Jesse James Joson
	[Date revised:] 10/21/2015
	Program [Revision] Details: Set effective date = batch run date + 1.
	PROJECT: CORE
	MEMBER TYPE  : JAVA

 */
 
/**
 	(revision)
	SCR/ER Number: ER# 20151113-054
	SCR/ER Description: No reinstatement entry for Reinstated LC thru amendment where amendment date = expiry date + 1.
	[Revised by:] Jesse James Joson
	[Date revised:] 11/17/2015
	Program [Revision] Details: Aside from checking reinstateFlag column in DB, also include checking of expirydate, if expired and amended consider as reinstated.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	
 */

/**
 	(revision)
	SCR/ER Number: 20160111-041 
	SCR/ER Description: Wrong accounting entries for DMLC Negotiation of Adjusted to Cash Transactions.
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 1/7/2016
	Program [Revision] Details: Added condition in function genAccountingEntries_LC_DOMESTIC_NEGOTIATION to not include the cash part in the reversal of the contingent accounting entries 
								and including the payment reversal of the cash part in the payment accounting entries.
	Date deployment: 1/14/2016
	Member Type: JAVA
	Project: CORE
	Project Name: AccountingService.java
 
 */
 
 /**
	(revision)
	SCR/ER Number: 20160114-049 
	SCR/ER Description: Unreconciled balances on  FX Standby between TFS-DW and GL.  Wrong cancellation entries found due to  the wrong  GL Account code used  (Redmine 4090).
	[Revised by:] Jesse James Joson
	[Date revised:] 1/18/2016
	Program [Revision] Details: Checking of Standby tagging was added for cancellation entries of FX Standby LC
	Date deployment: 1/28/2016
	Member Type: JAVA
	Project: CORE
	Project Name: AccountingService.java

 */

/**
	(revision)
	SCR/ER Number:  
	SCR/ER Description: Wrong computation and no accounting entry was generated for Doc Stamp fee.
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 2/5/2016
	Program [Revision] Details: Change the value used for the computation of Doc Stamp fee from proceeds amount to negotiation amount. Added condition which retrieve the rates from other
	 							details and use the default amount(peso) rather than the rounded-off amount(peso) for Doc Stamps fee in EBC Settlement.
	Date deployment: 2/9/2016
	Member Type: JAVA
	Project: CORE
	Project Name: AccountingService.java

*/

/**
	(revision)
	SCR/ER Number: 20160330-119  
	SCR/ER Description: account name should be BANK-COM CILEX instead of BC-OA/DA/DP-FUND; 
						kindly change gl code and name from (561501030000) FX P/L-IMPORTS to (561501010000) FX P/L-EXPORTS
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 3/8/2016
	Program [Revision] Details: Set AccountingCode to 561501010000 in REFACCENTRY table where product id is EBC, service type is SETTLEMENT, accounting entry type is 
								PAYMENT and formula particulars are FX Profit and FX Loss. Added condition to check if the transaction is EBC Settlement and use 561501010000 
								as accounting code and FX P/L-EXPORT as particular.
	Date deployment: 3/31/2016 
	Member Type: JAVA
	Project: CORE
	Project Name: AccountingService.java

*/

/**
(revision)
SCR/ER Number: 20160523-117
SCR/ER Description: No AE was generated for Adjustment of standby tagging on Standby LCs for third currencies
[Revised by:] Jesse James Joson
[Date revised:] 5/18/2016
Program [Revision] Details: Add checking on LC currency and create AE for third currencies.
Date deployment: 5/24/2016 
Member Type: JAVA
Project: CORE
Project Name: AccountingService.java

*/

/**
	(revision)
	SCR/ER Number:  
	SCR/ER Description: In FX UA Settlement, system generated incorrect and unbalance entry. The difference was booked to FX P/L - Imports to make it balance.
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 5/31/2016
	Program [Revision] Details: Used the conventional way of rounding-off (ROUND-HALF-EVEN). Revised the balancing entry function by subtracting or adding the 
								difference of the credit and debit original amount to the entry which should not be contingent, Spot Trade and starts with "185". 
								If both peso amount and original amount is disbalance, the entry in the original amount where the disbalance was subtracted/added 
								should be also the entry in peso amount where the disbalance should be subtracted/added.
	Date deployment: 7/18/2016
	Member Type: JAVA
	Project: CORE
	Project Name: AccountingService.java

*/

/**
	(revision)
	SCR/ER Number:  
	SCR/ER Description: Wrong accounting entries for FXLC Negotiation of Adjusted to Cash Transactions.
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 5/31/2016
	Program [Revision] Details: Only the Regular Part in FXLC Negotiation of Adjusted to Cash transactions will be reverse in contingent accounting entries and 
								if there is still cash part, it will be reversed in the payment accounting entries.
	Date deployment: 7/18/2016
	Member Type: JAVA
	Project: CORE
	Project Name: AccountingService.java

*/

/**
	(revision)
	SCR/ER Number:  
	SCR/ER Description: Validation in GLMAST of TFS AE should be using the ff fields - ACCTNO+GMCTYP+BRANCH+BOOKCD.
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 10/05/2016
	Program [Revision] Details: Added BRANCH unit code in validation in GLMAST of TFS AE.
	Date deployment: 11/8/2016
	Member Type: JAVA
	Project: CORE
	Project Name: AccountingService.java

*/


/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: AccountingService
 */
 
 /**
 * PROLOGUE
 * SCR/ER Description: To comply with the computation of accounting entries for export bills product
 *	[Revised by:] Jesse James Joson
 *	Program [Revision] Details: Revision on the amount that will be based on what amount shoud be use in computing AE.
 *	Date deployment: 6/16/2016 
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: AccountingService
*/

/**
 *  (revision) - 9/26/2016
 *  Redmine Issue Number: 6566 - Correct the accounting entries amount during LC amendment 
 *  [Revised by:] Jaivee Hipolito
 *  Program [Revision] Details: Change getStringOrReturnEmptyString(tradeService.getDetails(), "amount") to getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom")
 *  							Under genAccountingEntries_LC_FOREIGN_AMENDMENT & genAccountingEntries_LC_DOMESTIC_AMENDMENT(increase and decrease) 
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: AccountingService
*/
 
@Component
public class AccountingService {

    @Autowired
    AccountingLookup accountingLookup;

    @Autowired
    ProductReferenceRepository productReferenceRepository;

    @Autowired
    AccountingEntryActualRepository accountingEntryActualRepository;

    @Autowired
    AccountingEntryRepository accountingEntryRepository;

    @Autowired
    ChargesLookup chargeLookup;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

    @Autowired
    LetterOfCreditRepository letterOfCreditRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ProductServiceAccountingEventTransactionReferenceRepository productServiceAccountingEventTransactionReferenceRepository;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Autowired
    GlMastRepository glMastRepository;

    @Autowired
    ValueHolderRepository valueHolderRepository;

    @Autowired
    RefBankRepository refBankRepository;

    @Autowired
    ChargeAccountingCodeRepository chargeAccountingCodeRepository;

    @Autowired
    CorresChargeActualRepository corresChargeActualRepository;

    @Autowired
    CorresChargeAdvanceRepository corresChargeAdvanceRepository;

    @Autowired
    AccountingEntryActualVariablesRepository accountingEntryActualVariablesRepository;

    @Autowired
    RatesService ratesService;

    @Autowired
    OtherChargesService otherChargesService;

    @Autowired
    ProductServiceReferenceRepository productServiceReferenceRepository;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository;

    @Autowired
    ProfitLossHolderRepository profitLossHolderRepository;

    @Autowired
    RefPas5ClientRepository refPas5ClientRepository;

    @Autowired
    ExportBillsRepository exportBillsRepository;   

    
    
    // A global variables thats sets/determined if there error throws during runtime (creation of accounting entires at the back-end)
    
    //public static String errorExceptionMessage = "NONE";

    
//    private static final String DATE_FORMAT = "MMddyy";
//    private static final String PHP = "PHP";
//    private static final String USD = "USD";
//    private static final String CONVERSION_RATE = "CONVERSION_RATE";
    
    private String actualImportChargesProductID="";

    /**
     * Generates accounting entry using details map of serviceinstruction
     *
     * @param tradeService       the TradeService instance you will generate accounting entries for
     * @param serviceInstruction the ServiceInstruction instance you will generate accounting entries for
     * @param paymentProduct     the Payment object for Product
     * @param paymentService     the Payment object for Service
     * @param paymentSettlement  the Payment object for Settlement
     * @param paymentRefund      the Payment object for Refund
     * @param gltsNumber         the gltsnumber based on sequence generator
     * @param tradeServiceStatus the Status of the TradeService
     */
    @Transactional
    public void generateActualEntries(TradeService tradeService, ServiceInstruction serviceInstruction, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, Payment paymentRefund, String gltsNumber, String tradeServiceStatus) {
        generateActualEntries(tradeService, serviceInstruction.getDetails(), paymentProduct, paymentService, paymentSettlement, paymentRefund, gltsNumber, tradeServiceStatus);
    }

    /**
     * This was created to handle TSD initiated transactions that do not have a service instruction
     *
     * @param tradeService       the TradeService instance you will generate accounting entries for
     * @param gltsNumber         the gltsnumber based on sequence generator
     * @param tradeServiceStatus the Status of the TradeService
     */
    @Transactional
    public void generateActualEntries(TradeService tradeService, String gltsNumber, String tradeServiceStatus) {
    	
        
        Map<String, Object> details = tradeService.getDetails();
        Payment paymentService = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
        Payment paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
        Payment paymentSettlement = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
        Payment paymentRefund = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.REFUND);
        
        System.out.println("================================================================================");
        System.out.println("====================== Begin generateActualEntries =============================");
        System.out.println("================================================================================");
        
        System.out.println("=tradeServiceId= " + tradeService.getTradeServiceId());
        System.out.println("=tradeService= " + tradeService);
        System.out.println("=details= "+details);
        System.out.println("=paymentProduct= "+paymentProduct);
        System.out.println("=paymentService= "+paymentService);
        System.out.println("=paymentSettlement= "+paymentSettlement);
        System.out.println("=paymentRefund= "+paymentRefund);
        System.out.println("=gltsNumber= "+gltsNumber);
        System.out.println("=tradeServiceStatus= "+tradeServiceStatus);
        System.out.println("================================================================================");
   
        generateActualEntries(tradeService, details, paymentProduct, paymentService, paymentSettlement, paymentRefund, gltsNumber, tradeServiceStatus);
        tagWithError(tradeService.getTradeServiceId());
        System.out.println("================================================================================");
        System.out.println("=tradeServiceId= " + tradeService.getTradeServiceId());
        System.out.println("=gltsNumber= "+gltsNumber);
        System.out.println("================================================================================");
        System.out.println("=============================== Goodbye ! ======================================");
        System.out.println("================================================================================");

    }

    /**
     * For web service
     *
     * @param tradeServiceId     TradeServiceId of TradeService whose Entries are to be generated
     * @param gltsNumber         the current gltsNumber based on query in sequence generator
     * @param tradeServiceStatus the TradeServiceStatus of the TradeService corresponding to the TradeServiceId specified
     */
    @Transactional
    public void generateActualEntriesWebService(TradeServiceId tradeServiceId, String gltsNumber, String tradeServiceStatus) {
    	

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        Map<String, Object> details = tradeService.getDetails();
        Payment paymentService = paymentRepository.get(tradeServiceId, ChargeType.SERVICE);
        Payment paymentProduct = paymentRepository.get(tradeServiceId, ChargeType.PRODUCT);
        Payment paymentSettlement = paymentRepository.get(tradeServiceId, ChargeType.SETTLEMENT);
        Payment paymentRefund = paymentRepository.get(tradeServiceId, ChargeType.REFUND);
        
        System.out.println("================================================================================");
        System.out.println("=================== Begin generateActualEntriesWebService ======================");
        System.out.println("================================================================================");
        
        System.out.println("=tradeServiceId= " + tradeServiceId);
        System.out.println("=tradeService= " + tradeService);
        System.out.println("=details= "+details);
        System.out.println("=paymentProduct= "+paymentProduct);
        System.out.println("=paymentService= "+paymentService);
        System.out.println("=paymentSettlement= "+paymentSettlement);
        System.out.println("=paymentRefund= "+paymentRefund);
        System.out.println("=gltsNumber= "+gltsNumber);
        System.out.println("=tradeServiceStatus= "+tradeServiceStatus);
        System.out.println("================================================================================");
        generateActualEntries(tradeService, details, paymentProduct, paymentService, paymentSettlement, paymentRefund, gltsNumber, tradeServiceStatus);
		tagWithError(tradeServiceId);
        System.out.println("================================================================================");
        System.out.println("=tradeServiceId= " + tradeServiceId);
        System.out.println("=gltsNumber= "+gltsNumber);
        System.out.println("================================================================================");
        System.out.println("=============================== Goodbye ! ======================================");
        System.out.println("================================================================================");
    }

    /**
     * Forces the accounting entry to balance out by inserting a balancing entry
     *
     * @param tradeService       TradeService whose Entries are to be generated
     * @param gltsNumber         the current gltsNumber based on query in sequence generator
     * @param tradeServiceStatus the TradeServiceStatus of the TradeService corresponding to the TradeServiceId specified
     */
    @Transactional
    void generateActualEntriesBalancingFxProfitOrLoss(TradeService tradeService, String gltsNumber, String tradeServiceStatus) {
        System.out.println("generateActualEntriesBalancingFxProfitOrLoss");
        TradeServiceId tradeServiceId = tradeService.getTradeServiceId();

        DocumentType documentType = null;
        DocumentSubType1 documentSubType1 = null;
        DocumentSubType2 documentSubType2 = null;
        if (!tradeService.getDocumentClass().equals(DocumentClass.MD)) {
            documentType = tradeService.getDocumentType();
            documentSubType1 = tradeService.getDocumentSubType1();
            documentSubType2 = tradeService.getDocumentSubType2();
        }

        try {
            // find the UCPB product reference for this combination
            ProductReference productRef = productReferenceRepository.find(tradeService.getDocumentClass(), documentType, documentSubType1, documentSubType2);

            if (productRef != null) {
                System.out.println("Product ID:" + productRef.getProductId());
                System.out.println("product found:" + productRef.getProductId());

                try {
                    AccountingEntryActual accountingEntryActual;// = null;
                                        
                    BigDecimal creditPeso = accountingEntryActualRepository.getTotalPesoCredit(tradeServiceId);
                    if(creditPeso == null){
                    	creditPeso = BigDecimal.ZERO;                  	
                    }
                    System.out.println("creditPeso:" + creditPeso);
                    BigDecimal debitPeso = accountingEntryActualRepository.getTotalPesoDebit(tradeServiceId);
                    System.out.println("debitPeso:" + debitPeso);
                    String entryType = "";
                    BigDecimal profitOrLossPeso = BigDecimal.ZERO;
                    if (creditPeso.compareTo(debitPeso) == 1) {
                        entryType = "Debit";
                        profitOrLossPeso = creditPeso.subtract(debitPeso);
                    } else if (creditPeso.compareTo(debitPeso) == -1) {
                        entryType = "Credit";
                        profitOrLossPeso = debitPeso.subtract(creditPeso);
                    }
                    System.out.println("entryType:" + entryType);
                    System.out.println("profitOrLossPeso:" + profitOrLossPeso);

                    BigDecimal creditOriginal = accountingEntryActualRepository.getTotalOriginalCredit(tradeServiceId);
                    System.out.println("creditOriginal:" + creditOriginal);
                    BigDecimal debitOriginal = accountingEntryActualRepository.getTotalOriginalDebit(tradeServiceId);
                    System.out.println("debitOriginal:" + debitOriginal);
                    BigDecimal profitOrLossOriginal = BigDecimal.ZERO;

                    if (creditOriginal.compareTo(debitOriginal) == 1) {
                        entryType = "Debit";
                        profitOrLossOriginal = creditOriginal.subtract(debitOriginal);
                    } else if (creditOriginal.compareTo(debitOriginal) == -1) {
                        entryType = "Credit";
                        profitOrLossOriginal = debitOriginal.subtract(creditOriginal);
                    }


                    if(BigDecimal.ZERO.compareTo(profitOrLossOriginal)!=0 || BigDecimal.ZERO.compareTo(profitOrLossPeso)!=0){
                    }


                    System.out.println("WITHIN BALANCING OF ENTRIES!!!");
                    List<AccountingEntryActual> accountingEntryActualList = accountingEntryActualRepository.getEntries(tradeServiceId);
                    List<AccountingEntryActual> accountingEntryActualListPHP = new ArrayList<AccountingEntryActual>();
                    List<AccountingEntryActual> accountingEntryActualListUSD = new ArrayList<AccountingEntryActual>();
                    List<AccountingEntryActual> accountingEntryActualList3RD = new ArrayList<AccountingEntryActual>();
                    for (AccountingEntryActual entryActual : accountingEntryActualList) {
                        if(entryActual.getBookCurrency().equalsIgnoreCase("PHP")){
                            accountingEntryActualListPHP.add(entryActual);
                        } else if(entryActual.getBookCurrency().equalsIgnoreCase("USD")){
                            accountingEntryActualListUSD.add(entryActual);
                        } else {
                            accountingEntryActualList3RD.add(entryActual);
                        }
                    }

                    // balance thirds
                    BigDecimal Debit3rdOriginal = BigDecimal.ZERO;
                    BigDecimal Credit3rdOriginal = BigDecimal.ZERO;
                    BigDecimal Debit3rdPeso = BigDecimal.ZERO;
                    BigDecimal Credit3rdPeso = BigDecimal.ZERO;
                    if(!accountingEntryActualList3RD.isEmpty()){

                    }

                    BigDecimal oneCent = new BigDecimal("0.01");

                    List<AccountingEntryActual> accountingEntryActualList3RDScratch = new ArrayList<AccountingEntryActual>();
                    accountingEntryActualList3RDScratch.addAll(accountingEntryActualList3RD);


                    for (AccountingEntryActual entryActualOuter : accountingEntryActualList3RD) {
                        System.out.println("WITHIN BALANCING OF ENTRIES 3RD!!!");
//                            accountingEntryActualList3RDScratch.remove(entryActualOuter); // We remove to minimize duplicate checking

                        for (AccountingEntryActual entryActualInner : accountingEntryActualList3RDScratch) {
                            if(entryActualInner.getOriginalAmount().compareTo(entryActualOuter.getOriginalAmount()) == 0){
                                if(entryActualInner.getPesoAmount().compareTo(entryActualOuter.getPesoAmount()) != 0){
                                    System.out.println("equal ang original but not the peso amount");
                                    //Handling find the correct peso amount
                                    //Loop through PESO and USD amount if you hit a similar value then that is most likely the working correct amount
                                    int innerVote=0;
                                    int outerVote=0;
                                    for (AccountingEntryActual entryActualVoting : accountingEntryActualListUSD) {
                                        if(entryActualVoting.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==0){
                                            innerVote++;
                                        } else if (entryActualVoting.getPesoAmount().compareTo(entryActualOuter.getPesoAmount())==0){
                                            outerVote++;
                                        }
                                    }

                                    for (AccountingEntryActual entryActualVoting : accountingEntryActualListPHP) {
                                        if(entryActualVoting.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==0){
                                            innerVote++;
                                        } else if (entryActualVoting.getPesoAmount().compareTo(entryActualOuter.getPesoAmount())==0){
                                            outerVote++;
                                        }
                                    }

                                    if(innerVote>outerVote){
                                        entryActualOuter.setPesoAmount(entryActualInner.getPesoAmount());
                                        accountingEntryActualRepository.save(entryActualOuter);
                                    } else if(innerVote<outerVote){
                                        entryActualInner.setPesoAmount(entryActualOuter.getPesoAmount());
                                        accountingEntryActualRepository.save(entryActualInner);
                                    } else if(innerVote==outerVote){
                                        //get the smaller amount
                                        if(entryActualOuter.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==1){
                                            entryActualOuter.setPesoAmount(entryActualInner.getPesoAmount());
                                            accountingEntryActualRepository.save(entryActualInner);
                                        } else {
                                            entryActualInner.setPesoAmount(entryActualOuter.getPesoAmount());
                                            accountingEntryActualRepository.save(entryActualOuter);
                                        }

                                    }


                                }
                            } else if(entryActualInner.getPesoAmount().compareTo(entryActualOuter.getPesoAmount()) == 0 &&
                                    entryActualInner.getBookCurrency().equalsIgnoreCase(entryActualOuter.getBookCurrency())){
                                System.out.println("equal ang peso but not the original amount");
                                //Adjust the original amount
                                //If only one cent then get the lesser amount

                                if(entryActualInner.getOriginalAmount().compareTo(entryActualOuter.getOriginalAmount())==1){
//                                        BigDecimal diff = entryActualInner.getOriginalAmount().subtract(entryActualOuter.getOriginalAmount());
//                                        if(oneCent.compareTo(diff)==0){ }
                                    entryActualInner.setOriginalAmount(entryActualOuter.getOriginalAmount());
                                    accountingEntryActualRepository.save(entryActualInner);
                                } else {
//                                        BigDecimal diff = entryActualInner.getOriginalAmount().subtract(entryActualOuter.getOriginalAmount());
//                                        if(oneCent.compareTo(diff)==0){ }
                                    entryActualOuter.setOriginalAmount(entryActualInner.getOriginalAmount());
                                    accountingEntryActualRepository.save(entryActualOuter);
                                }
                            } else if(entryActualInner.getOriginalAmount().add(oneCent).compareTo(entryActualOuter.getOriginalAmount()) == 0 ||
                                    entryActualInner.getOriginalAmount().compareTo(entryActualOuter.getOriginalAmount().add(oneCent)) == 0){
                                System.out.println("of by 1 cent ang original but not the peso amount");
                                int innerVote=0;
                                int outerVote=0;
                                for (AccountingEntryActual entryActualVoting : accountingEntryActualListPHP) {
                                    if(entryActualVoting.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==0){
                                        innerVote++;
                                    } else if (entryActualVoting.getPesoAmount().compareTo(entryActualOuter.getPesoAmount())==0){
                                        outerVote++;
                                    }
                                }

                                System.out.println("innerVote:"+innerVote);
                                System.out.println("outerVote:"+outerVote);
                                if(innerVote>outerVote){
                                    entryActualOuter.setOriginalAmount(entryActualInner.getOriginalAmount());
                                    entryActualOuter.setPesoAmount(entryActualInner.getPesoAmount());
                                    accountingEntryActualRepository.save(entryActualOuter);
                                } else if(innerVote<outerVote){
                                    entryActualInner.setOriginalAmount(entryActualOuter.getOriginalAmount());
                                    entryActualInner.setPesoAmount(entryActualOuter.getPesoAmount());
                                    accountingEntryActualRepository.save(entryActualInner);
                                } else if(innerVote==outerVote){
                                    //get the smaller amount
                                    if(entryActualOuter.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==1){
                                        entryActualOuter.setOriginalAmount(entryActualInner.getOriginalAmount());
                                        entryActualOuter.setPesoAmount(entryActualInner.getPesoAmount());
                                        accountingEntryActualRepository.save(entryActualInner);
                                    } else {
                                        entryActualInner.setOriginalAmount(entryActualOuter.getOriginalAmount());
                                        entryActualInner.setPesoAmount(entryActualOuter.getPesoAmount());
                                        accountingEntryActualRepository.save(entryActualOuter);
                                    }

                                }
                            }
                        }
                    }


                    List<AccountingEntryActual> accountingEntryActualListUSDScratch = new ArrayList<AccountingEntryActual>();
                    accountingEntryActualListUSDScratch.addAll(accountingEntryActualListUSD);

                    for (AccountingEntryActual entryActualOuter : accountingEntryActualListUSDScratch) {

                        System.out.println("WITHIN BALANCING OF ENTRIES USD!!!");
//                            accountingEntryActualListUSDScratch.remove(entryActualOuter); // We remove to minimize duplicate checking

                        for (AccountingEntryActual entryActualInner : accountingEntryActualListUSDScratch) {
                            if(entryActualInner.getOriginalAmount().compareTo(entryActualOuter.getOriginalAmount()) == 0){
                                if(entryActualInner.getPesoAmount().compareTo(entryActualOuter.getPesoAmount()) != 0){
                                    System.out.println("equal ang original but not the peso amount");
                                    //Handling find the correct peso amount
                                    //Loop through PESO and USD amount if you hit a similar value then that is most likely the working correct amount
                                    int innerVote=0;
                                    int outerVote=0;
                                    for (AccountingEntryActual entryActualVoting : accountingEntryActualList3RD) {
                                        if(entryActualVoting.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==0){
                                            innerVote++;
                                        } else if (entryActualVoting.getPesoAmount().compareTo(entryActualOuter.getPesoAmount())==0){
                                            outerVote++;
                                        }
                                    }

                                    for (AccountingEntryActual entryActualVoting : accountingEntryActualListPHP) {
                                        if(entryActualVoting.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==0){
                                            innerVote++;
                                        } else if (entryActualVoting.getPesoAmount().compareTo(entryActualOuter.getPesoAmount())==0){
                                            outerVote++;
                                        }
                                    }

                                    System.out.println("innerVote:"+innerVote);
                                    System.out.println("outerVote:"+outerVote);

                                    if(innerVote>outerVote){
                                        entryActualOuter.setPesoAmount(entryActualInner.getPesoAmount());
                                        accountingEntryActualRepository.save(entryActualOuter);
                                    } else if(innerVote<outerVote){
                                        entryActualInner.setPesoAmount(entryActualOuter.getPesoAmount());
                                        accountingEntryActualRepository.save(entryActualInner);
                                    } else if(innerVote==outerVote){
                                        //get the smaller amount
                                        if(entryActualOuter.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==1){
                                            entryActualOuter.setPesoAmount(entryActualInner.getPesoAmount());
                                            accountingEntryActualRepository.save(entryActualInner);
                                        } else {
                                            entryActualInner.setPesoAmount(entryActualOuter.getPesoAmount());
                                            accountingEntryActualRepository.save(entryActualOuter);
                                        }
                                    }
                                }
                            } else if(entryActualInner.getPesoAmount().compareTo(entryActualOuter.getPesoAmount()) == 0 &&
                                    entryActualInner.getBookCurrency().equalsIgnoreCase(entryActualOuter.getBookCurrency())){
                                System.out.println("equal ang peso but not the original amount");
                                //Adjust the original amount
                                //If only one cent then get the lesser amount

                                if(entryActualInner.getOriginalAmount().compareTo(entryActualOuter.getOriginalAmount())==1){
//                                        BigDecimal diff = entryActualInner.getOriginalAmount().subtract(entryActualOuter.getOriginalAmount());
//                                        if(oneCent.compareTo(diff)==0){ }
                                    entryActualInner.setOriginalAmount(entryActualOuter.getOriginalAmount());
                                    accountingEntryActualRepository.save(entryActualInner);
                                } else {
//                                        BigDecimal diff = entryActualInner.getOriginalAmount().subtract(entryActualOuter.getOriginalAmount());
//                                        if(oneCent.compareTo(diff)==0){ }
                                    entryActualOuter.setOriginalAmount(entryActualInner.getOriginalAmount());
                                    accountingEntryActualRepository.save(entryActualOuter);
                                }
                            }   else if(entryActualInner.getOriginalAmount().add(oneCent).compareTo(entryActualOuter.getOriginalAmount()) == 0 ||
                                    entryActualInner.getOriginalAmount().compareTo(entryActualOuter.getOriginalAmount().add(oneCent)) == 0){
                                System.out.println("of by 1 cent ang original but not the peso amount");
                                int innerVote=0;
                                int outerVote=0;
                                for (AccountingEntryActual entryActualVoting : accountingEntryActualListPHP) {
                                    if(entryActualVoting.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==0){
                                        innerVote++;
                                    } else if (entryActualVoting.getPesoAmount().compareTo(entryActualOuter.getPesoAmount())==0){
                                        outerVote++;
                                    }
                                }

                                System.out.println("innerVote:"+innerVote);
                                System.out.println("outerVote:"+outerVote);
                                if(innerVote>outerVote){
                                    entryActualOuter.setOriginalAmount(entryActualInner.getOriginalAmount());
                                    entryActualOuter.setPesoAmount(entryActualInner.getPesoAmount());
                                    accountingEntryActualRepository.save(entryActualOuter);
                                } else if(innerVote<outerVote){
                                    entryActualInner.setOriginalAmount(entryActualOuter.getOriginalAmount());
                                    entryActualInner.setPesoAmount(entryActualOuter.getPesoAmount());
                                    accountingEntryActualRepository.save(entryActualInner);
                                } else if(innerVote==outerVote){
                                    //get the smaller amount
                                    if(entryActualOuter.getPesoAmount().compareTo(entryActualInner.getPesoAmount())==1){
                                        entryActualOuter.setOriginalAmount(entryActualInner.getOriginalAmount());
                                        entryActualOuter.setPesoAmount(entryActualInner.getPesoAmount());
                                        accountingEntryActualRepository.save(entryActualInner);
                                    } else {
                                        entryActualInner.setOriginalAmount(entryActualOuter.getOriginalAmount());
                                        entryActualInner.setPesoAmount(entryActualOuter.getPesoAmount());
                                        accountingEntryActualRepository.save(entryActualOuter);
                                    }
                                }
                            }
                        }
                    }


                    //BALANCING SPOT TRADE ENTRIES
                    List<AccountingEntryActual> accountingEntryActualListAfterModification = accountingEntryActualRepository.getEntries(tradeServiceId);
                    List<AccountingEntryActual> accountingEntryActualListSpot = new ArrayList<AccountingEntryActual>();
                    for (AccountingEntryActual entryActual : accountingEntryActualListAfterModification) {
                        if(entryActual.getAccountingCode().equalsIgnoreCase("280111010000")){
                            accountingEntryActualListSpot.add(entryActual);
                        }
                    }
                    BigDecimal debitAmount = BigDecimal.ZERO;
                    BigDecimal creditAmount = BigDecimal.ZERO;
                    BigDecimal adjustAmount = BigDecimal.ZERO;
                    for (AccountingEntryActual entryActual : accountingEntryActualListSpot) {
                        if(entryActual.getEntryType().equalsIgnoreCase("Debit")){
                            System.out.println("Debit()"+entryActual.getPesoAmount());
                            debitAmount= debitAmount.add(entryActual.getPesoAmount());
                        } else if(entryActual.getEntryType().equalsIgnoreCase("Credit")){
                            creditAmount= creditAmount.add(entryActual.getPesoAmount());
                            System.out.println("Credit()"+entryActual.getPesoAmount());
                        }
                    }

                    String accountingEntryType="Credit";
                    String accountingEntryTypeContra="Debit";
                    if(creditAmount.compareTo(debitAmount)!=0){
                        System.out.println("creditAmount"+creditAmount);
                        System.out.println("debitAmount"+debitAmount);
                        if(creditAmount.compareTo(debitAmount)==1){
                            adjustAmount= creditAmount.subtract(debitAmount);
                            accountingEntryType="Debit";
                            accountingEntryTypeContra="Credit";
                        } else if(creditAmount.compareTo(debitAmount)==-1){
                            adjustAmount= debitAmount.subtract(creditAmount);
                            accountingEntryType="Credit";
                            accountingEntryTypeContra="Debit";
                        }

                        System.out.println("adjustAmount:"+adjustAmount);
                        adjustAmount = adjustAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
                        System.out.println("adjustAmount:"+adjustAmount);
                        //OVERRIDE ALWAYS FX P/L-IMPORTS


                        String profitOrLossUcpbProductId = getUcpbProductIdString(tradeService, tradeService.getDetails(), productRef);

                        String spotTradeAccountingCode = "280111010000";
                        String spotTradeParticulars = "SPOT TRADE";

                        if (adjustAmount.compareTo(BigDecimal.ZERO) == 1) {
                            AccountingEntryActual accountingEntryActualSpotAdjustment = new AccountingEntryActual(
                                    tradeService.getCcbdBranchUnitCode(),
                                    "909",
                                    "FC",
                                    "PHP",
                                    "PHP",
                                    accountingEntryType,
                                    spotTradeAccountingCode,
                                    spotTradeParticulars,
                                    adjustAmount,
                                    adjustAmount,
                                    tradeService.getTradeServiceId(),
                                    productRef.getProductId(),
                                    tradeService.getServiceType(),
                                    new AccountingEventTransactionId("BALANCING-ENTRY-SPOT-TRADE"),
                                    new Date(),
                                    gltsNumber,
                                    "I",
                                    "*POST",
                                    profitOrLossUcpbProductId,
                                    tradeServiceStatus,
                                    "",
                                    tradeService.getTradeProductNumber().toString()
                            );
                            accountingEntryActualRepository.save(accountingEntryActualSpotAdjustment);


                            String fxProfitLossAccountingCode = "561501030000";
                            String fxProfitLossParticulars = "FX P/L-IMPORTS";

                            AccountingEntryActual accountingEntryActualSpotAdjustmentFxProfitOrLoss = new AccountingEntryActual(
                                    tradeService.getCcbdBranchUnitCode(),
                                    "909",
                                    "FC",
                                    "PHP",
                                    "PHP",
                                    accountingEntryTypeContra,
                                    fxProfitLossAccountingCode,
                                    fxProfitLossParticulars,
                                    adjustAmount,
                                    adjustAmount,
                                    tradeService.getTradeServiceId(),
                                    productRef.getProductId(),
                                    tradeService.getServiceType(),
                                    new AccountingEventTransactionId("BALANCING-ENTRY-SPOT-TRADE"),
                                    new Date(),
                                    gltsNumber,
                                    "I",
                                    "*POST",
                                    profitOrLossUcpbProductId,
                                    tradeServiceStatus,
                                    "",
                                    tradeService.getTradeProductNumber().toString()
                            );
                            accountingEntryActualRepository.save(accountingEntryActualSpotAdjustmentFxProfitOrLoss);
                        }

                    }

                    creditPeso = accountingEntryActualRepository.getTotalPesoCredit(tradeServiceId);
                    if(creditPeso == null) {
                    	creditPeso = BigDecimal.ZERO;
                    }
                    System.out.println("creditPeso:" + creditPeso);
                    debitPeso = accountingEntryActualRepository.getTotalPesoDebit(tradeServiceId);
                    if(debitPeso == null) {
                    	debitPeso = BigDecimal.ZERO;
                    }
                    System.out.println("debitPeso:" + debitPeso);
                    entryType = "";
                    String entryTypeContra = "";
                    profitOrLossPeso = BigDecimal.ZERO;
                    if (creditPeso.compareTo(debitPeso) == 1) {
                        entryType = "Debit";
                        entryTypeContra = "Credit";
                        profitOrLossPeso = creditPeso.subtract(debitPeso);
                    } else if (creditPeso.compareTo(debitPeso) == -1) {
                        entryType = "Credit";
                        entryTypeContra = "Debit";
                        profitOrLossPeso = debitPeso.subtract(creditPeso);
                    }
                    System.out.println("entryType:" + entryType);
                    System.out.println("profitOrLossPeso:" + profitOrLossPeso);

                    creditOriginal = accountingEntryActualRepository.getTotalOriginalCredit(tradeServiceId);
                    System.out.println("creditOriginal:" + creditOriginal);
                    debitOriginal = accountingEntryActualRepository.getTotalOriginalDebit(tradeServiceId);
                    System.out.println("debitOriginal:" + debitOriginal);
                    profitOrLossOriginal = BigDecimal.ZERO;
                    if (creditOriginal.compareTo(debitOriginal) == 1) {
                        entryType = "Debit";
                        entryTypeContra = "Credit";
                        profitOrLossOriginal = creditOriginal.subtract(debitOriginal);
                    } else if (creditOriginal.compareTo(debitOriginal) == -1) {
                        entryType = "Credit";
                        entryTypeContra = "Debit";
                        profitOrLossOriginal = debitOriginal.subtract(creditOriginal);
                    }



                    System.out.println("entryType:" + entryType);
                    System.out.println("profitOrLossOriginal:" + profitOrLossOriginal);

                    BigDecimal profitLossToBeUsed = BigDecimal.ZERO;
                    BigDecimal differenceToBeSubtractedOnPesoAmount = BigDecimal.ZERO;
                    if(profitOrLossOriginal.compareTo(profitOrLossPeso)==1){
                    	
                    	System.out.println("ProfitOrLoss1");
                    	
                        profitLossToBeUsed = profitOrLossOriginal;
                        differenceToBeSubtractedOnPesoAmount = profitOrLossOriginal.subtract(profitOrLossPeso);
                        

                        //Instead of adding FX Profit or Loss I will Just Modify the                    
                        List<AccountingEntryActual> accountingEntryActualListAfterModification02 = accountingEntryActualRepository.getEntries(tradeServiceId);
                        for (AccountingEntryActual entryActual : accountingEntryActualListAfterModification02) {
                            if(!entryActual.getBookCurrency().equalsIgnoreCase("PHP") && !entryActual.getBookCurrency().equalsIgnoreCase("PHP")){
                            	if(!entryActual.getAccountingCode().startsWith("83") && !entryActual.getAccountingCode().startsWith("82") && 
                            			!entryActual.getAccountingCode().startsWith("81") && !entryActual.getAccountingCode().startsWith("246") &&
                            			!entryActual.getAccountingCode().startsWith("280111010000") && !entryActual.getAccountingCode().startsWith("185")){
                            		if(creditPeso.compareTo(debitPeso) == -1 && entryActual.getEntryType().equalsIgnoreCase("CREDIT")){
		                            	entryActual.setPesoAmount(entryActual.getPesoAmount().add(profitOrLossPeso));
		                                accountingEntryActualRepository.save(entryActual);
		                                if(creditOriginal.compareTo(debitOriginal) == -1 && entryActual.getEntryType().equalsIgnoreCase("CREDIT")){
			                            	entryActual.setOriginalAmount(entryActual.getOriginalAmount().add(profitOrLossOriginal));
			                                accountingEntryActualRepository.save(entryActual);
	                            		}
		                                break;
                            		} else if(creditPeso.compareTo(debitPeso) == 1 && entryActual.getEntryType().equalsIgnoreCase("DEBIT")){
		                            	entryActual.setPesoAmount(entryActual.getPesoAmount().add(profitOrLossPeso));
		                                accountingEntryActualRepository.save(entryActual);
		                                if(creditOriginal.compareTo(debitOriginal) == 1 && entryActual.getEntryType().equalsIgnoreCase("DEBIT")){
			                            	entryActual.setOriginalAmount(entryActual.getOriginalAmount().add(profitOrLossOriginal));
			                                accountingEntryActualRepository.save(entryActual);
	                            		}
		                                break;
                            		}
                            	}
                            }
                        }                        
                        
                        
                    } else  if(profitOrLossOriginal.compareTo(profitOrLossPeso)==-1){
                        profitLossToBeUsed = profitOrLossPeso;
                        differenceToBeSubtractedOnPesoAmount = profitOrLossPeso.subtract(profitOrLossOriginal);
                        System.out.println("ProfitOrLoss2");

                        //Instead of adding FX Profit or Loss I will Just Modify the                    
                        List<AccountingEntryActual> accountingEntryActualListAfterModification02 = accountingEntryActualRepository.getEntries(tradeServiceId);
                        for (AccountingEntryActual entryActual : accountingEntryActualListAfterModification02) {
                             if(!entryActual.getBookCurrency().equalsIgnoreCase("PHP") && !entryActual.getBookCurrency().equalsIgnoreCase("PHP")){
                            	if(!entryActual.getAccountingCode().startsWith("83") && !entryActual.getAccountingCode().startsWith("82") && 
                            			!entryActual.getAccountingCode().startsWith("81") && !entryActual.getAccountingCode().startsWith("246") &&
                            			!entryActual.getAccountingCode().startsWith("280111010000") && !entryActual.getAccountingCode().startsWith("185")){
                            		if(creditPeso.compareTo(debitPeso) == -1 && entryActual.getEntryType().equalsIgnoreCase("DEBIT")){
		                                entryActual.setPesoAmount(entryActual.getPesoAmount().subtract(profitOrLossPeso));
		                                accountingEntryActualRepository.save(entryActual);
		                                if(creditOriginal.compareTo(debitOriginal) == -1 && entryActual.getEntryType().equalsIgnoreCase("DEBIT")){
			                            	entryActual.setOriginalAmount(entryActual.getOriginalAmount().subtract(profitOrLossOriginal));
			                                accountingEntryActualRepository.save(entryActual);
	                            		}
		                                break;
                            		} else if(creditPeso.compareTo(debitPeso) == 1 && entryActual.getEntryType().equalsIgnoreCase("CREDIT")){
		                                entryActual.setPesoAmount(entryActual.getPesoAmount().subtract(profitOrLossPeso));
		                                accountingEntryActualRepository.save(entryActual);
		                                if(creditOriginal.compareTo(debitOriginal) == 1 && entryActual.getEntryType().equalsIgnoreCase("CREDIT")){
			                                entryActual.setOriginalAmount(entryActual.getOriginalAmount().subtract(profitOrLossOriginal));
			                                accountingEntryActualRepository.save(entryActual);
	                            		}
		                                break;
                            		}
                            	}
                            }
                        }
                        
                    } else if(profitOrLossOriginal.compareTo(profitOrLossPeso)==0){
                    	System.out.println("ProfitOrLoss1");
                        profitLossToBeUsed = profitOrLossOriginal;
                        //Determine if Debit or Credit -->EntryType
                        //Determine if amount to be balanced -->accountingEntry.computePesoValue(details)


                        // 561501030000 -> FX P/L-IMPORTS
                        // 561501010000 -> FX P/L-EXPORTS
                        // 561501050000 -> FX P/L-OTHERS
                        //TODO: must convert to config like in the future

                        String accCode;// = "";
                        String particulars;// = "";
                        //Maybe make this a function
                        if (
                                tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING) ||
                                        tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVANCE) ||
                                        tradeService.getDocumentClass().equals(DocumentClass.BC) ||
                                        tradeService.getDocumentClass().equals(DocumentClass.BP)
                                ) {
                            accCode = "561501010000";
                            particulars = "FX P/L-EXPORTS";
                        } else if (tradeService.getDocumentClass().equals(DocumentClass.AP) ||
                                tradeService.getDocumentClass().equals(DocumentClass.AR) ||
                                tradeService.getDocumentClass().equals(DocumentClass.MD)) {
                            accCode = "561501050000";
                            particulars = "FX P/L-OTHERS";
                        } else {
                            accCode = "561501030000";
                            particulars = "FX P/L-IMPORTS";
                        }
                        //OVERRIDE ALWAYS FX P/L-IMPORTS
                        accCode = "561501030000";
                        particulars = "FX P/L-IMPORTS";
                        
                        //OVERRIDE ALWAYS FX P/L-EXPORT for EBC SETTLEMENT
                        if(tradeService.getDocumentType() != null){
	                        if (tradeService.getDocumentType().equals(DocumentType.FOREIGN) &&
	                        		tradeService.getDocumentClass().equals(DocumentClass.BC) &&
	                        		tradeService.getServiceType().equals(ServiceType.SETTLEMENT)) {
	                            accCode = "561501010000";
	                            particulars = "FX P/L-EXPORT";
	                        }               
                        }

                        String tmpUcpbProductId = getUcpbProductIdString(tradeService, tradeService.getDetails(), productRef);


                        if (profitLossToBeUsed.compareTo(BigDecimal.ZERO) == 1) {
                            accountingEntryActual = new AccountingEntryActual(
                                    tradeService.getCcbdBranchUnitCode(),
                                    "909",
                                    "RG",
                                    "PHP",
                                    "PHP",
                                    entryType,
                                    accCode,
                                    particulars,
                                    profitLossToBeUsed,//profitOrLossPeso,
                                    profitLossToBeUsed,//profitOrLossOriginal, profitOrLossPeso //Forces original and PESO value to be equal
                                    tradeService.getTradeServiceId(),
                                    productRef.getProductId(),
                                    tradeService.getServiceType(),
                                    new AccountingEventTransactionId("BALANCING-ENTRY"),
                                    new Date(),
                                    gltsNumber,
                                    "I",
                                    "*POST",
                                    tmpUcpbProductId,
                                    tradeServiceStatus,
                                    "",
                                    tradeService.getTradeProductNumber().toString()
                            );
                            accountingEntryActualRepository.save(accountingEntryActual);

//                            AccountingEntryActualVariables accountingEntryActualVariables = new AccountingEntryActualVariables(
//                                    "RG",
//                                    "PHP",
//                                    entryType,//accountingEntry.getEntryType().toString(),
//                                    profitOrLossOriginal,
//                                    tradeService.getTradeServiceId(),
//                                    productRef.getProductId(),
//                                    tradeService.getServiceType(),
//                                    new AccountingEventTransactionId("BALANCING-ENTRY"),
//                                    "fxProfitLossPHP",
//                                    "NP"
//                            );
//
//                            accountingEntryActualVariablesRepository.save(accountingEntryActualVariables);
                        }


                        accountingEntryActualList = accountingEntryActualRepository.getEntries(tradeServiceId);
                        accountingEntryActualListUSD = new ArrayList<AccountingEntryActual>();
                        for (AccountingEntryActual entryActual : accountingEntryActualList) {
                            if(entryActual.getBookCurrency().equalsIgnoreCase("USD") &&
                                    !entryActual.getAccountingCode().equalsIgnoreCase("280111010000") &&
                                    entryActual.getEntryType().equalsIgnoreCase(entryTypeContra)){
                                if(accountingEntryActualListUSD.isEmpty()){
                                    accountingEntryActualListUSD.add(entryActual);
                                    entryActual.setPesoAmount(entryActual.getPesoAmount().add(differenceToBeSubtractedOnPesoAmount));                                
                                    accountingEntryActualRepository.save(entryActual);
                                }
                            }
                    }

             }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Exception in accounting entries generateActualEntriesBalancingFxProfitOrLoss",e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
           // throw new RuntimeException("Exception in accounting entries generateActualEntriesBalancingFxProfitOrLoss",e);
        }
    }

    /**
     * Primary function that calls the specific method used to generate Actual Accounting Entry
     * One method
     *
     * @param tradeService       TradeService instance whose accounting entries will be generated
     * @param details            the details of the TradeService instance
     * @param paymentProduct     Payment instance for product charges payment
     * @param paymentService     Payment instance for service charges payment
     * @param paymentSettlement  Payment instance for settlement to beneficiary
     * @param paymentRefund      Payment instance for refund to beneficiary
     * @param gltsNumber         the current gltsNumber based on query in sequence generator
     * @param tradeServiceStatus the TradeServiceStatus of the TradeService corresponding to the TradeServiceId specified
     */
    @Transactional
    private void generateActualEntries(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, Payment paymentRefund, String gltsNumber, String tradeServiceStatus) {
        
    	//errorExceptionMessage = "NONE";
    	
    	
    	System.out.println("tradeService:" + tradeService.getTradeServiceId());
        System.out.println("tradeService Reference Number:" + tradeService.getTradeServiceReferenceNumber());
        System.out.println("paymentProduct:" + paymentProduct);
        System.out.println("paymentService:" + paymentService);
        System.out.println("paymentRefund:" + paymentRefund);
        System.out.println("paymentSettlement:" + paymentSettlement);

        System.out.println("Generate Accounting Entries:");
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());
        DocumentType documentType = null;
        DocumentSubType1 documentSubType1 = null;
        DocumentSubType2 documentSubType2 = null;
        
        
        if (!tradeService.getDocumentClass().equals(DocumentClass.MD)) {
            documentType = tradeService.getDocumentType();
            documentSubType1 = tradeService.getDocumentSubType1();
            documentSubType2 = tradeService.getDocumentSubType2();
        }
        if (tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)) {
            documentType = tradeService.getDocumentType();
            documentSubType1 = null;
            documentSubType2 = null;
        }


        try {
            // find the UCPB product reference for this combination
            ProductReference productRef = productReferenceRepository.find(tradeService.getDocumentClass(), documentType, documentSubType1, documentSubType2);

            System.out.println("Product Reference: "+ productRef);
            
            if (productRef != null) {
                System.out.println("Product ID:" + productRef.getProductId());
                System.out.println("product found:" + productRef.getProductId());

                if (tradeService.getDocumentClass().equals(DocumentClass.LC)
                        && tradeService.getServiceType().equals(ServiceType.OPENING)) {
                    System.out.println("IN HERE 01");
                    genAccountingEntries_LC_OPENING(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.LC)
                        && tradeService.getServiceType().equals(ServiceType.AMENDMENT)
                        && tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
                    System.out.println("IN HERE 02");
                    genAccountingEntries_LC_FOREIGN_AMENDMENT(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.LC)
                        && tradeService.getServiceType().equals(ServiceType.AMENDMENT)
                        && tradeService.getDocumentType().equals(DocumentType.DOMESTIC)) {
                    System.out.println("IN HERE 03");
                    genAccountingEntries_LC_DOMESTIC_AMENDMENT(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.LC)
                        && tradeService.getServiceType().equals(ServiceType.ADJUSTMENT)
                        ) {
                    System.out.println("IN HERE 04");
                    genAccountingEntries_LC_ADJUSTMENT(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.LC)
                        && tradeService.getServiceType().equals(ServiceType.CANCELLATION)) {
                    System.out.println("IN HERE 05");
                    genAccountingEntries_LC_CANCELLATION(tradeService, details, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.LC)
                        && tradeService.getServiceType().equals(ServiceType.NEGOTIATION)
                        && tradeService.getDocumentType().equals(DocumentType.DOMESTIC)) {
                    System.out.println("IN HERE 06");
                    genAccountingEntries_LC_DOMESTIC_NEGOTIATION(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.LC)
                        && tradeService.getServiceType().equals(ServiceType.NEGOTIATION)
                        && tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
                    System.out.println("IN HERE 07");
                    genAccountingEntries_LC_FOREIGN_NEGOTIATION(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.LC)
                        && tradeService.getServiceType().equals(ServiceType.NEGOTIATION_DISCREPANCY)) {
//                        && tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
                    System.out.println("IN HERE 08");
                    genAccountingEntries_LC_NEGOTIATION_DISCREPANCY(tradeService, details, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)
                        && tradeService.getServiceType().equals(ServiceType.CANCELLATION)
                        && tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
                    System.out.println("IN HERE 09");
                    genAccountingEntries_INDEMNITY_FOREIGN_CANCELLATION(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)
                        && tradeService.getServiceType().equals(ServiceType.ISSUANCE)
                        && tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
                    System.out.println("IN HERE 10");
                    genAccountingEntries_INDEMNITY_FOREIGN_ISSUANCE(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if ((tradeService.getDocumentClass().equals(DocumentClass.DP))
                        && tradeService.getServiceType().equals(ServiceType.NEGOTIATION)) {
                    System.out.println("IN HERE 11");
                    genAccountingEntries_NON_LC_NEGOTIATION(tradeService, details, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.DA)
                        && tradeService.getServiceType().equals(ServiceType.NEGOTIATION_ACCEPTANCE)) {
                    System.out.println("IN HERE 12");
                    genAccountingEntries_NON_LC_NEGOTIATION_ACCEPTANCE(tradeService, details, productRef, gltsNumber, tradeServiceStatus);
                } else if ((tradeService.getDocumentClass().equals(DocumentClass.DP) || tradeService.getDocumentClass().equals(DocumentClass.DA) || tradeService.getDocumentClass().equals(DocumentClass.OA) || tradeService.getDocumentClass().equals(DocumentClass.DR))
                        && tradeService.getServiceType().equals(ServiceType.SETTLEMENT)) {
                    System.out.println("IN HERE 13");
                    genAccountingEntries_NON_LC_SETTLEMENT(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getServiceType().equals(ServiceType.UA_LOAN_MATURITY_ADJUSTMENT)) {
                    System.out.println("IN HERE 14");
                    genAccountingEntries_UA_LOAN_MATURITY_ADJUSTMENT(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getServiceType().equals(ServiceType.UA_LOAN_SETTLEMENT)) {
                    System.out.println("IN HERE 15");
                    genAccountingEntries_UA_LOAN_SETTLEMENT(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if ((tradeService.getDocumentClass().equals(DocumentClass.DP) || tradeService.getDocumentClass().equals(DocumentClass.DA))
                        && tradeService.getServiceType().equals(ServiceType.CANCELLATION)) {
                    System.out.println("IN HERE 16");
                    genAccountingEntries_NON_LC_CANCELLATION(tradeService, details, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.MD)
                        && tradeService.getServiceType().equals(ServiceType.COLLECTION)) {
                    System.out.println("IN HERE 17");
                    genAccountingEntries_MD_COLLECTION(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.MD)
                        && tradeService.getServiceType().equals(ServiceType.APPLICATION)) {
                    System.out.println("IN HERE 18");
                    genAccountingEntries_MD_APPLICATION(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.AP)
                        && tradeService.getServiceType().equals(ServiceType.REFUND)) {
                    System.out.println("IN HERE 19");
                    genAccountingEntries_AP_REFUND(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.AR)
                        && tradeService.getServiceType().equals(ServiceType.SETTLE)) {
                    System.out.println("IN HERE 20");
                    genAccountingEntries_AR_SETTLE(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING)
                        && tradeService.getServiceType().equals(ServiceType.OPENING_ADVISING)) {
                    System.out.println("IN HERE 21");
                    genAccountingEntries_EXPORT_ADVISING_OPENING_ADVISING(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING)
                        && tradeService.getServiceType().equals(ServiceType.AMENDMENT_ADVISING)) {
                    System.out.println("IN HERE 22");
                    genAccountingEntries_EXPORT_ADVISING_AMENDMENT_ADVISING(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING)
                        && tradeService.getServiceType().equals(ServiceType.CANCELLATION_ADVISING)) {
                    System.out.println("IN HERE 23");
                    genAccountingEntries_EXPORT_ADVISING_CANCELLATION_ADVISING(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.CDT)
                        && tradeService.getServiceType().equals(ServiceType.COLLECTION)) {
                    System.out.println("IN HERE 24");
                    genAccountingEntries_CDT_COLLECTION(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                    //accountingProfitLossBalance = false; this was already done inside genAccountingEntries_CDT_COLLECTION
                }else if (tradeService.getDocumentClass().equals(DocumentClass.CDT)
                        && tradeService.getServiceType().equals(ServiceType.PAYMENT)) {
                    System.out.println("IN HERE 25");
                    genAccountingEntries_CDT_PAYMENT(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                    //accountingProfitLossBalance = false; this was already done inside genAccountingEntries_CDT_PAYMENT
                } else if (DocumentClass.CDT.equals(tradeService.getDocumentClass())
                        && tradeService.getServiceType().equals(ServiceType.REMITTANCE)) {
                    System.out.println("IN HERE 26");
                    genAccountingEntries_CDT_REMITTANCE(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.CDT)
                        && tradeService.getServiceType().equals(ServiceType.REFUND)) {
                    System.out.println("IN HERE 27");
                    genAccountingEntries_CDT_REFUND(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.CORRES_CHARGE)
                        && tradeService.getServiceType().equals(ServiceType.SETTLEMENT)) {
                    System.out.println("IN HERE 28");
                    genAccountingEntries_CORRES_CHARGE_SETTLEMENT(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.IMPORT_ADVANCE)
                        && tradeService.getServiceType().equals(ServiceType.PAYMENT)) {
                    System.out.println("IN HERE 29");
                    genAccountingEntries_IMPORT_ADVANCE_PAYMENT(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if ( DocumentClass.BC.equals(tradeService.getDocumentClass())
                        && DocumentType.DOMESTIC.equals(tradeService.getDocumentType())
                        && ServiceType.SETTLEMENT.equals(tradeService.getServiceType())) {
                    System.out.println("IN HERE 30");
                    genAccountingEntries_DOMESTIC_BC_SETTLEMENT(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if (DocumentClass.BC.equals(tradeService.getDocumentClass())
                        && DocumentType.FOREIGN.equals(tradeService.getDocumentType())
                        && ServiceType.SETTLEMENT.equals(tradeService.getServiceType()) ) {
                    System.out.println("IN HERE 31");
                    genAccountingEntries_FOREIGN_BC_SETTLEMENT(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if ( DocumentClass.BP.equals(tradeService.getDocumentClass())
                        && DocumentType.DOMESTIC.equals(tradeService.getDocumentType())
                        && ServiceType.SETTLEMENT.equals(tradeService.getServiceType())) {
                    System.out.println("IN HERE 32");
                    genAccountingEntries_DOMESTIC_BP_SETTLEMENT(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if (DocumentClass.BP.equals(tradeService.getDocumentClass())
                        && DocumentType.FOREIGN.equals(tradeService.getDocumentType())
                        && ServiceType.SETTLEMENT.equals(tradeService.getServiceType())) {
                    System.out.println("IN HERE 33");
                    genAccountingEntries_FOREIGN_BP_SETTLEMENT(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if ( DocumentClass.BP.equals(tradeService.getDocumentClass())
                        && DocumentType.DOMESTIC.equals(tradeService.getDocumentType())
                        && ServiceType.NEGOTIATION.equals(tradeService.getServiceType())) {
                    System.out.println("IN HERE 34");
                    genAccountingEntries_DOMESTIC_BP_NEGOTIATION(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if (DocumentClass.BP.equals(tradeService.getDocumentClass())
                        && DocumentType.FOREIGN.equals(tradeService.getDocumentType())
                        && ServiceType.NEGOTIATION.equals(tradeService.getServiceType())) {
                    System.out.println("IN HERE 35");
                    genAccountingEntries_FOREIGN_BP_NEGOTIATION(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if ( DocumentClass.BC.equals(tradeService.getDocumentClass())
                        && DocumentType.DOMESTIC.equals(tradeService.getDocumentType())
                        && ServiceType.NEGOTIATION.equals(tradeService.getServiceType())) {
                    System.out.println("IN HERE 36");
                    genAccountingEntries_DOMESTIC_BC_NEGOTIATION(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (DocumentClass.BC.equals(tradeService.getDocumentClass())
                        && DocumentType.FOREIGN.equals(tradeService.getDocumentType())
                        && ServiceType.NEGOTIATION.equals(tradeService.getServiceType())) {
                    System.out.println("IN HERE 37");
                    genAccountingEntries_FOREIGN_BC_NEGOTIATION(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                } else if (DocumentClass.REBATE.equals(tradeService.getDocumentClass())
                        && ServiceType.REBATE.equals(tradeService.getServiceType())) {
                    System.out.println("IN HERE 38");
                    genAccountingEntries_REBATE_PROCESS(tradeService, details, productRef, gltsNumber, tradeServiceStatus);
                } else if (DocumentClass.BC.equals(tradeService.getDocumentClass())
                        && DocumentType.FOREIGN.equals(tradeService.getDocumentType())
                        && ServiceType.CANCELLATION.equals(tradeService.getServiceType()) ) {
                    System.out.println("IN HERE 39");
                    genAccountingEntries_FOREIGN_BC_CANCELLATION(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if (DocumentClass.IMPORT_CHARGES.equals(tradeService.getDocumentClass())
                        && ServiceType.PAYMENT.equals(tradeService.getServiceType()) ) {
                    System.out.println("IN HERE 40");
                    genAccountingEntries_IMPORT_CHARGES_PAYMENT(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if (DocumentClass.IMPORT_CHARGES.equals(tradeService.getDocumentClass())
                        && ServiceType.PAYMENT_OTHER.equals(tradeService.getServiceType()) ) {
                    System.out.println("IN HERE 41");
                    genAccountingEntries_IMPORT_CHARGES_PAYMENT_OTHER(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                } else if (tradeService.getDocumentClass().equals(DocumentClass.AP)
                        && tradeService.getServiceType().equals(ServiceType.SETUP)) {
                    System.out.println("IN HERE 42");
                    genAccountingEntries_AP_SETUP(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                }else if (tradeService.getDocumentClass().equals(DocumentClass.AP)
                        && tradeService.getServiceType().equals(ServiceType.APPLY)) {
                    System.out.println("IN HERE 43");
                    genAccountingEntries_AP_APPLY(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                }else if (tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVANCE)
                        && tradeService.getServiceType().equals(ServiceType.PAYMENT)) {
                    System.out.println("IN HERE 44");
                    genAccountingEntries_EXPORT_ADVANCE_PAYMENT(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                }else if ((tradeService.getDocumentClass().equals(DocumentClass.LC) 
                			|| tradeService.getDocumentClass().equals(DocumentClass.DP)
                			|| tradeService.getDocumentClass().equals(DocumentClass.DA)
                			|| tradeService.getDocumentClass().equals(DocumentClass.DR)
                			|| tradeService.getDocumentClass().equals(DocumentClass.OA)) 
                			&& tradeService.getServiceType().equals(ServiceType.REFUND)) {
                    System.out.println("IN HERE 45");
                    genAccountingEntries_Refund_CASHLC_CHARGES(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                }else if (tradeService.getDocumentClass().equals(DocumentClass.EXPORT_CHARGES)
                        && tradeService.getServiceType().equals(ServiceType.REFUND)) {
                    System.out.println("IN HERE 46");
                    genAccountingEntries_Refund_EXPORT_CHARGES(tradeService, details, paymentProduct, paymentService, paymentSettlement, productRef, gltsNumber, tradeServiceStatus);
                }else if (tradeService.getDocumentClass().equals(DocumentClass.AR)
                        && tradeService.getServiceType().equals(ServiceType.SETUP)) {
                    System.out.println("IN HERE 47");
                    genAccountingEntries_AR_SETUP(tradeService, details, paymentProduct, paymentService, productRef, gltsNumber, tradeServiceStatus);
                }else {
                    System.out.println("IN HERE 00: NO Matching Method for Generation of Accounting Entries");
                    System.out.println("tradeService:" + tradeService.getTradeServiceId());
                    System.out.println("tradeService Reference Number:" + tradeService.getTradeServiceReferenceNumber());
                    System.out.println("paymentProduct:" + paymentProduct);
                    System.out.println("paymentService:" + paymentService);
                    System.out.println("paymentRefund:" + paymentRefund);
                    System.out.println("paymentSettlement:" + paymentSettlement);

                    System.out.println("Generate Accounting Entries:");
                    System.out.println("DocumentClass:" + tradeService.getDocumentClass());
                    System.out.println("DocumentType:" + tradeService.getDocumentType());
                    System.out.println("DocumentSubType1:" + documentSubType1);
                    System.out.println("DocumentSubType2:" + documentSubType2);
                    System.out.println("ServiceType:" + tradeService.getServiceType());
                    
                    System.out.println("No Accounting Entries!");

                }
                
                	if(!(tradeService.getDocumentClass().equals(DocumentClass.CDT) || (tradeService.getDocumentClass().equals(DocumentClass.AR)
                            && tradeService.getServiceType().equals(ServiceType.SETUP)))){
                		generateActualEntriesBalancingFxProfitOrLoss(tradeService, gltsNumber, tradeServiceStatus); //balancing function not applicable to CDT
                	}
                	
                	//Update isPosted column to 0
                    isPosted(tradeService.getTradeServiceId().toString().trim(), new Boolean(false));
                	
            } else {
                System.out.println("!!!!!!!!!!!!!! product reference not found");
            }   
            
        } catch (Exception e) {
            System.out.println("_____________________________________________EEEEEEEERRRRRRRRRRRRRROOOOOOOOOOOOORRRRRRRRRRRRRRRRRR ");
            System.out.println("Find:"+tradeService.getTradeServiceId().toString());
            e.printStackTrace();
            System.out.println("_____________________________________________EEEEEEEERRRRRRRRRRRRRROOOOOOOOOOOOORRRRRRRRRRRRRRRRRR ");
            throw new RuntimeException("Exception in accounting entries generateActualEntries",e);
        }
    }

    @Transactional
    private void genAccountingEntries_IMPORT_CHARGES_PAYMENT_OTHER(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_IMPORT_CHARGES_PAYMENT_OTHER");
        System.out.println("paymentProduct:"+paymentProduct);
        System.out.println("paymentService:"+paymentService);
        System.out.println("paymentSettlement:"+paymentSettlement);
        System.out.println("productRef:"+productRef);
        System.out.println("gltsNumber:"+gltsNumber);
        System.out.println("tradeServiceStatus:"+tradeServiceStatus);
        System.out.println();
        System.out.println("details:"+tradeService.getDetails());
        System.out.println();
        try {


            System.out.println("transactionType:"+tradeService.getDetails().get("transactionType"));
            System.out.println("documentNumber:"+tradeService.getDetails().get("documentNumber"));

            List<Map<String, Object>> tradeServiceList = tradeServiceRepository.getAllApprovedTradeServiceIds(new TradeProductNumber(tradeService.getDetails().get("documentNumber").toString()));
            if(tradeServiceList!=null && !tradeServiceList.isEmpty()){
                for (Iterator<Map<String, Object>> iterator = tradeServiceList.iterator(); iterator.hasNext(); ) {
                    Map<String, Object> next = iterator.next();
                    for (String s : next.keySet()) {
                        System.out.println("keys:"+s);
                        System.out.println("value:"+next.get(s));
                    }

                }
            } else {
                //use drop down values
            }

            BigDecimal bankCommissionAmount = BigDecimal.ZERO;
            BigDecimal insuranceAmount = BigDecimal.ZERO;
            String accountingCode = "";
            String bcAccountingCode = "";

            String lcCurrency = "PHP";
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            Map<String, Object> chargeMap = new HashMap<String, Object>();


            //PAYMENT-CHARGES-OTHERS-OTHERS
            //Booking is done from payment currency to lc currency
            System.out.println("Payment of Service Charges Start");

            String paymentSettlementCurrency ="";
            BookCurrency payBookCurrency = null;
            Boolean PaymentChargeOnce = Boolean.FALSE;
            Set<PaymentDetail> temp = paymentService.getDetails();
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);

                String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
                System.out.println("paymentName:" + paymentName);


                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                System.out.println("specificPaymentMap:" + specificPaymentMap);
                System.out.println("---------------------------");

                paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

                genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-OTHERS-OTHERS"), gltsNumber, tradeServiceStatus);

                System.out.println("specificPaymentMap PaymentCharge:" + specificPaymentMap);
                System.out.println("---------------------------");
            }


            Set<OtherChargesDetail> otherChargesDetailSet = otherChargesService.getAllDetails(tradeService.getTradeServiceId());
            for (OtherChargesDetail otherChargesDetail: otherChargesDetailSet){
                System.out.println(otherChargesDetail.getId());
                System.out.println(otherChargesDetail.getAmount());
                System.out.println(otherChargesDetail.getChargeType());
                System.out.println(otherChargesDetail.getCurrency());
                System.out.println(otherChargesDetail.getTransactionType());
                System.out.println(otherChargesDetail.getCwtFlag());

                //get ID value instead of productID 
                String tempTransactionType = otherChargesDetail.getTransactionType();
                String splitTransactionType = tempTransactionType.substring(tempTransactionType.indexOf("|")+1);
               
                System.out.println("Product ID >>> "+ splitTransactionType);
                
                
                
                ProductServiceReference productServiceReference =  productServiceReferenceRepository.getProductService(new Long(splitTransactionType));
                System.out.println("productServiceReference Product Id:" + productServiceReference.getProductId());
                System.out.println("productServiceReference Service Type:" + productServiceReference.getServiceType());
                
                String tempProductId = productServiceReference.getProductId().toString();
                if(productServiceReference.getProductId().toString().equalsIgnoreCase("DM-LC-STANDBY") || productServiceReference.getProductId().toString().equalsIgnoreCase("FX-LC-STANDBY")){
                	DocumentNumber docNum = new DocumentNumber((String) tradeService.getDetails().get("documentNumber"));
                    LetterOfCredit lc = (LetterOfCredit) tradeProductRepository.load(docNum);
                    if(lc.getStandbyTagging() != null){
                    	if(lc.getStandbyTagging().equalsIgnoreCase("FINANCIAL")){
                    		tempProductId = tempProductId + "-FINANCIAL";
                    	} else {
                    		tempProductId = tempProductId + "-PERFORMANCE";
                    	}
                    }
                }
                
                String setUcpbProdId = getImportChargesProductId(tempProductId);
                setImportChargesProductId(setUcpbProdId);
                System.out.println("Product ID >>>>" + setUcpbProdId);
                
                //USE DISPLAY NAME TO CONVERT TO CHARGEID
//                Charge charge = chargeRepository.getByName(otherChargesDetail.getChargeType());
                Charge charge = null;
                System.out.println("charge:"+charge);

                String cwtFlag = otherChargesDetail.getCwtFlag();
                if("Y".equalsIgnoreCase(otherChargesDetail.getCwtFlag()) || "Yes".equalsIgnoreCase(otherChargesDetail.getCwtFlag()) || "1".equalsIgnoreCase(otherChargesDetail.getCwtFlag()) ){
                    System.out.println("With cwt");

                }

                chargeMap = new HashMap<String, Object>();//Resetting Charge Map

                if(charge!=null){
                    System.out.println("ANGOL 00");
                    ChargeAccountingCode chargeAccountingCode = chargeAccountingCodeRepository.getChargeAccountingCode(productServiceReference.getProductId(),productServiceReference.getServiceType(),charge.getChargeId());
                    System.out.println("chargeAccountingCode:"+chargeAccountingCode.getAccountingCode());
                    accountingCode = chargeAccountingCode.getAccountingCode();
//                    bankCommissionAmount = bankCommissionAmount.add(otherChargesDetail.getAmount());
                    bankCommissionAmount = otherChargesDetail.getAmount();
                    chargeMap.put(charge.getFormulaName()+"PHP",otherChargesDetail.getAmount());
                    chargeMap.put(charge.getChargeId().toString()+"AccountingCode",accountingCode);
                    if(charge.getChargeId().toString().equalsIgnoreCase("BC")){
                        bcAccountingCode = accountingCode;
                    }
                    System.out.println("bankCommissionAmount:"+bankCommissionAmount);

                    if(!chargeMap.containsKey("BCAccountingCode")){
                        chargeMap.put("BCAccountingCode",bcAccountingCode);
                    }

                    if(cwtFlag !=null && "Y".equalsIgnoreCase(cwtFlag)){
                        BigDecimal originalAmount = bankCommissionAmount.divide(new BigDecimal("0.98"),2,BigDecimal.ROUND_HALF_UP);
                        BigDecimal cwtAmount = originalAmount.subtract(bankCommissionAmount);
                        System.out.println("originalAmount:"+originalAmount);
                        System.out.println("bankCommissionAmount:"+bankCommissionAmount);
                        System.out.println("cwtAmount:"+cwtAmount);
                        chargeMap.put("bankCommissionGrossPHP",originalAmount);
                        chargeMap.put("chargesAmountCWTPHP",cwtAmount);
                    } else {
                        chargeMap.put("bankCommissionGrossPHP",bankCommissionAmount);
                    }


                    genAccountingEntryCharge_charges(tradeService, chargeMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-OTHERS-OTHERS"), gltsNumber, tradeServiceStatus);
                } else {
                    System.out.println("ANGOL 01");
                    if(otherChargesDetail.getChargeType().contains("BC")||otherChargesDetail.getChargeType().contains("BANK")||otherChargesDetail.getChargeType().contains("Bank")){
                        System.out.println("ANGOL 02");
                        ChargeAccountingCode chargeAccountingCode = chargeAccountingCodeRepository.getChargeAccountingCode(productServiceReference.getProductId(),productServiceReference.getServiceType(),new ChargeId("BC"));
                        System.out.println("chargeAccountingCode:"+chargeAccountingCode);
                        bcAccountingCode = chargeAccountingCode.getAccountingCode();
                        System.out.println("bcAccountingCode:"+bcAccountingCode);
//                        bankCommissionAmount = bankCommissionAmount.add(otherChargesDetail.getAmount());
                        bankCommissionAmount = otherChargesDetail.getAmount();
                        System.out.println("bankCommissionAmount:" + bankCommissionAmount);

                        if(!chargeMap.containsKey("BCAccountingCode")){
                            chargeMap.put("BCAccountingCode",bcAccountingCode);
                        }

                        if(cwtFlag !=null && "Y".equalsIgnoreCase(cwtFlag)){
                            BigDecimal originalAmount = bankCommissionAmount.divide(new BigDecimal("0.98"),2,BigDecimal.ROUND_HALF_UP);
                            BigDecimal cwtAmount = originalAmount.subtract(bankCommissionAmount);
                            System.out.println("originalAmount:"+originalAmount);
                            System.out.println("bankCommissionAmount:"+bankCommissionAmount);
                            System.out.println("cwtAmount:"+cwtAmount);
                            chargeMap.put("bankCommissionGrossPHP",originalAmount);
                            chargeMap.put("chargesAmountCWTPHP",cwtAmount);
                        } else {
                            chargeMap.put("bankCommissionGrossPHP",bankCommissionAmount);
                        }

                        genAccountingEntryCharge_charges(tradeService, chargeMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-OTHERS-OTHERS"), gltsNumber, tradeServiceStatus);

                    } else {
                        System.out.println("ANGOL 03");
                        insuranceAmount = insuranceAmount.add(otherChargesDetail.getAmount());
                        System.out.println("insuranceAmount:"+insuranceAmount);
                        chargeMap.put("marineFireInsurancePHP",insuranceAmount);
                        //Generate Accounting Entry Related to Charges since all LC opening has charges
                        System.out.println("chargeMap:"+chargeMap);
                        genAccountingEntryCharge_charges(tradeService, chargeMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-OTHERS-OTHERS"), gltsNumber, tradeServiceStatus);
                    }
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_IMPORT_CHARGES_PAYMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_IMPORT_CHARGES_PAYMENT");
        System.out.println("details:"+tradeService.getDetails());

        String currency = tradeService.getDetails().get("currency").toString();
        System.out.println("currency:"+currency);

        BookCurrency bookCurrency = determineBookCurrency(currency);
        String settlementCurrencyCharges = "";
        
        System.out.println("bookCurrency other import charges: "+bookCurrency.toString());
        for (ServiceCharge charge : tradeService.getServiceCharge()){
            settlementCurrencyCharges = charge.getCurrency().getCurrencyCode();
        }
        BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        Boolean cilexCharged = willWeChargeCilex(paymentProduct);
        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();
    
        //since there are no product payment in this module, 
        //alternative way to identify cilex charge
        for (String s : chargesSummaryMap.keySet()) {
            List<Object> tempList = chargesSummaryMap.get(s);

            for (Object o : tempList) {
                if (o != null) {

                    if (s.equalsIgnoreCase("SC")) {
                        ServiceCharge tempSC = (ServiceCharge) o;
                        
                        String willWeChargeCilex = "-"+tempSC.getChargeId();
                        System.out.println("ChargeId:" + tempSC.getChargeId() + "-Currency:" + tempSC.getCurrency() + "-Amount:" + tempSC.getAmount() + "-getOriginalAmount:" + tempSC.getOriginalAmount());
                        
                        if(willWeChargeCilex.equalsIgnoreCase("-CILEX")) {
                        	cilexCharged = true;
                        }
                       
                    }
                }
            }
        }
        
        DocumentNumber docNum = new DocumentNumber((String) tradeService.getDetails().get("documentNumber"));
        System.out.println(docNum);
        LetterOfCredit lc = (LetterOfCredit) tradeProductRepository.load(docNum);
        System.out.println(lc.getDocumentType().toString() + "<><><><>" + lc.getType().toString() + "<><><><>" + lc.getTenor().getDisplayText().toUpperCase());
        String tempProductId = lc.getDocumentType().toString() + "-LC-" + lc.getType().toString();
        if(lc.getType().toString().equalsIgnoreCase("REGULAR")){
        	tempProductId = tempProductId + "-" + lc.getTenor().getDisplayText().toUpperCase();
        } else if(lc.getType().toString().equalsIgnoreCase("STANDBY")){
	        if(lc.getStandbyTagging() != null){
	        	if(lc.getStandbyTagging().equalsIgnoreCase("FINANCIAL")){
	        		tempProductId = tempProductId + "-FINANCIAL";
	        	} else {
	        		tempProductId = tempProductId + "-PERFORMANCE";
	        	}
	        }
	    }
        System.out.println(tempProductId);
        String setUcpbProdId = getImportChargesProductId(tempProductId);
        setImportChargesProductId(setUcpbProdId);
        System.out.println("Product ID >>>>" + setUcpbProdId);     
        
        Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, currency, "lcAmount");
        Map<String, Object> chargeMap = generate_OTHER_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);


        //Settlement of Charges
        if (paymentService != null) {
            generateChargesAndChargesPaymentOtherImportChargesAccountingEntries(tradeService, paymentService, productRef, currency, bookCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
        }

    }

    @Transactional
    private void genAccountingEntries_FOREIGN_BC_CANCELLATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_FOREIGN_BC_CANCELLATION");
        System.out.println("details:"+tradeService.getDetails());

        String currency = tradeService.getDetails().get("currency").toString();
        BookCurrency bcLcCurrency = determineBookCurrency(currency);
        BigDecimal amount = new BigDecimal(tradeService.getDetails().get("amount").toString().replace(",",""));
        BigDecimal pesoAmount = amount;

        System.out.println("amount:"+tradeService.getDetails().get("amount").toString());
        System.out.println("currency:"+tradeService.getDetails().get("currency").toString());
        BigDecimal urr = BigDecimal.ZERO;
        BigDecimal thirdToUSD = BigDecimal.ZERO;


        System.out.println("ratesService.getAngolConversionRate():"+ratesService.getAngolConversionRate(currency,"USD",2));
        System.out.println("ratesService.getUrrConversionRateToday();:"+ratesService.getUrrConversionRateToday());

        //TODO INSERT RATE HERE
        if(currency.equalsIgnoreCase("PHP")){
            System.out.println("LC PHP");
            urr = ratesService.getUrrConversionRateToday();
        } else if(currency.equalsIgnoreCase("USD")){
            System.out.println("LC USD");
            urr = ratesService.getUrrConversionRateToday();
        } else {
            System.out.println("LC THIRD");
            urr = ratesService.getUrrConversionRateToday();
            thirdToUSD = ratesService.getAngolConversionRate(currency,"USD",2);
        }



        //TODO Convert these to correct peso amounts
        if(currency.equalsIgnoreCase("PHP")){
            //DO NOTHING Peso Amount SAME AS Original Amount
        } else if(currency.equalsIgnoreCase("USD")) {
            pesoAmount = amount.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
        } else {
            pesoAmount = amount.multiply(thirdToUSD.multiply(urr)).setScale(2,BigDecimal.ROUND_UP);
        }

        try {

            String accountingEventTransactionId = "REVERSAL-CONTINGENT-ENTRY";
            if (productRef != null) {
                System.out.println("Product ID:" + productRef.getProductId());
                System.out.println("product found:" + productRef.getProductId());

                Map<String, Object> lcMap = new HashMap<String, Object>();
                lcMap.put("cancellationAmount"+currency,amount);
                lcMap.put("cancellationAmountPHP",pesoAmount);
                System.out.println("lcMap:"+lcMap);
                genAccountingEntryLC(tradeService, lcMap, productRef, currency, bcLcCurrency, currency, bcLcCurrency, new AccountingEventTransactionId(accountingEventTransactionId), gltsNumber, tradeServiceStatus);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_REBATE_PROCESS(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_REBATE_PROCESS");

        System.out.println("details:"+tradeService.getDetails());

        String accountType = tradeService.getDetails().get("accountType").toString();
        String currency = tradeService.getDetails().get("currency").toString();
        BookCurrency bcLcCurrency = determineBookCurrency(currency);
        BigDecimal amount = new BigDecimal(tradeService.getDetails().get("amount").toString().replace(",",""));
        BigDecimal pesoAmount = amount;

        System.out.println("amount:"+tradeService.getDetails().get("amount").toString());
        System.out.println("currency:"+tradeService.getDetails().get("currency").toString());
        BigDecimal urr = BigDecimal.ZERO;
        BigDecimal thirdToUSD = BigDecimal.ZERO;


        System.out.println("ratesService.getAngolConversionRate():"+ratesService.getAngolConversionRate(currency,"USD",2));
        System.out.println("ratesService.getUrrConversionRateToday();:"+ratesService.getUrrConversionRateToday());

        //TODO INSERT RATE HERE
        if(currency.equalsIgnoreCase("PHP")){
            System.out.println("LC PHP");
            urr = ratesService.getUrrConversionRateToday();
        } else if(currency.equalsIgnoreCase("USD")){
            System.out.println("LC USD");
            urr = ratesService.getUrrConversionRateToday();
        } else {
            System.out.println("LC THIRD");
            urr = ratesService.getUrrConversionRateToday();
            thirdToUSD = ratesService.getAngolConversionRate(currency,"USD",2);
        }



        //TODO Convert these to correct peso amounts
        if(currency.equalsIgnoreCase("PHP")){
            //DO NOTHING Peso Amount SAME AS Original Amount
        } else if(currency.equalsIgnoreCase("USD")) {
            pesoAmount = amount.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
        } else {
            pesoAmount = amount.multiply(thirdToUSD.multiply(urr)).setScale(2,BigDecimal.ROUND_UP);
        }

        try {
            String ucpbProductId = productRef.getUcpbProductId();

            String accountingEventTransactionId = "";
            if("RBU".equalsIgnoreCase(accountType)){
                accountingEventTransactionId = "REBATES-RBU";
            } else {
                accountingEventTransactionId = "REBATES-FCDU";
            }
            if (productRef != null) {
                System.out.println("Product ID:" + productRef.getProductId());
                System.out.println("product found:" + productRef.getProductId());

                Map<String, Object> lcMap = new HashMap<String, Object>();
                if(currency.equalsIgnoreCase("USD")||currency.equalsIgnoreCase("PHP")){
                    lcMap.put("rebatesAmount"+currency,amount);
                } else {
                    lcMap.put("rebatesAmountTHIRD",amount);
                }

                lcMap.put("rebatesAmountPHP",pesoAmount);
                System.out.println("lcMap:"+lcMap);
                genAccountingEntryLC(tradeService, lcMap, productRef, currency, bcLcCurrency, currency, bcLcCurrency, new AccountingEventTransactionId(accountingEventTransactionId), gltsNumber, tradeServiceStatus);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Transactional
    private void genAccountingEntries_FOREIGN_BC_NEGOTIATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_FOREIGN_BC_NEGOTIATION");
        //Creation of SIBS-LOAN if any

        //Settlement of Charges
        //Get LC Currency
        String lcCurrency = "";
        if (tradeService.getProductChargeCurrency() != null) {
            lcCurrency = tradeService.getProductChargeCurrency().toString();
        }

        if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }
        }

        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        System.out.println("totalAmountClaimedA:"+tradeService.getDetails().get("totalAmountClaimedA").toString());
        System.out.println("amount:"+tradeService.getDetails().get("amount").toString());
        System.out.println("currency:"+tradeService.getDetails().get("currency").toString());

        Map<String, Object> lcMap = new HashMap<String, Object>();

        BigDecimal amount = new BigDecimal(tradeService.getDetails().get("amount").toString().replace(",",""));
        BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));


        String valueName = "negoAmount";
        insertValueNameToValueMapUrr(details, amount, lcCurrency, lcMap, urr, valueName);

        lcMap.put("urr",urr);

        System.out.println("lcMap:"+lcMap);
        genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);

    }

    @Transactional
    private void genAccountingEntries_DOMESTIC_BC_NEGOTIATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_DOMESTIC_BC_NEGOTIATION");
        //Creation of SIBS-LOAN if any

        //Settlement of Charges
        //Get LC Currency
        String lcCurrency = "";
        if (tradeService.getProductChargeCurrency() != null) {
            lcCurrency = tradeService.getProductChargeCurrency().toString();
        }

        if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }
        }

        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());
        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

        Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");


        BigDecimal amount = new BigDecimal(tradeService.getDetails().get("amount").toString().replace(",",""));
        BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));


        String valueName = "negoAmount";
        insertValueNameToValueMapUrr(details, amount, lcCurrency, lcMap, urr, valueName);

        lcMap.put("urr",urr);

        System.out.println("lcMap:"+lcMap);
        genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);

    }

    @Transactional
    private void genAccountingEntries_FOREIGN_BC_SETTLEMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_FOREIGN_BC_SETTLEMENT");

        //For all Reverse Nego Amount in Contingent Field

        //Settlement of Charges
        //Get LC Currency
        String lcCurrency = "";
        if (tradeService.getProductChargeCurrency() != null) {
            lcCurrency = tradeService.getProductChargeCurrency().toString();
        }

        if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }
        }

        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        //Get Settlement Currency
        String settlementCurrencyCharges = "";
        if(tradeService.getServiceChargeCurrency()!=null){
            settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
        }
        if(tradeService.getDetails().get("settlementCurrency")!=null){
            settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
        } else{
            settlementCurrencyCharges = "PHP";
        }
        System.out.println("settlementCurrencyCharges:" + lcCurrency);
        BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        Boolean cilexCharged = willWeChargeCilex(paymentProduct);
        if(!cilexCharged){ //FOR BC and BP only
            cilexCharged = willWeChargeCilex(paymentSettlement);
        }

        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();
        
        System.out.println("getChargesMap: "+chargesSummaryMap);

        Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
        Map<String, Object> chargeMap = generate_EXPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);
        System.out.println("chargeMap:"+chargeMap);

        //REVERSAL-OF-CONTINGENT
        BigDecimal amount = BigDecimal.ZERO;
        if(tradeService.getDetails().containsKey("amount")){
            amount = getBigDecimalOrZero(tradeService.getDetails().get("amount"));
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO NEGO AMOUNT TO REVERSE");
        }
        System.out.println("EBC Negotiation Amount:"+ amount);
        System.out.println("THIRD-USD:"+tradeService.getDetails().get(lcCurrency+"-USD"));
        System.out.println("urr:"+tradeService.getDetails().get("urr"));
        BigDecimal urr = new BigDecimal(tradeService.getDetails().get("urr").toString().replace(",",""));

        Map<String, Object> negotiationMap = new HashMap<String, Object>();
        insertValueNameToValueMapUrr(tradeService, amount, lcCurrency, negotiationMap, urr, "negoAmount");

        System.out.println("EBC Negotiation Map:"+ negotiationMap );

        //Settlement to Exporter
        ///DUEFromFB

        //Settlement of Product Charges

        //WITH EBP
        //bpCurrency and bpAmount is not null/empty
        //WITHOUT EBP
        //bpCurrency and bpAmount is null/empty

        String bpCurrency = "";
        if(tradeService.getDetails().containsKey("bpCurrency") && tradeService.getDetails().get("bpCurrency")!=null){
            bpCurrency = tradeService.getDetails().get("bpCurrency").toString();
        }



        if(bpCurrency.equalsIgnoreCase("")){
            //NO EBP
            //Reversal of Contingent Entries
            genAccountingEntryLC(tradeService, negotiationMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);

            //DUE from Foreign Bank ->  Settlement to Beneficiary

            String settlementName = "";
            //Booking is done from payment currency to lc currency
            System.out.println("Settlement to Beneficiary of Product Charges Start EBC SETTLEMENT NO EBP");
            String settlementBaseName="";
            String productSettlementCurrency = "";
            BigDecimal amountToBeSettledWithoutProfitOrLossIfInPhp = BigDecimal.ZERO;
            String paymentId = "";
            BookCurrency bcProductSettlementCurrency;
            if( paymentSettlement !=null ){
                Set<PaymentDetail> temp = paymentSettlement.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    //NOTE: from FSD only one settlement to beneficiary
                    System.out.println("---------------------------");
                    settlementName = getSettlementName(paymentDetail.getPaymentInstrumentType().toString());
                    settlementBaseName = paymentDetail.getPaymentInstrumentType().toString();
                    productSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    amountToBeSettledWithoutProfitOrLossIfInPhp = paymentDetail.getAmount();
                    paymentId = paymentDetail.getId().toString();
  
                    System.out.println("settlementName: "+settlementName);
                    System.out.println("settlementBaseName: "+settlementBaseName);
                                
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);
                    System.out.println("---------------------------");
                    
                }

                bcProductSettlementCurrency = determineBookCurrency(productSettlementCurrency);


                Map<String, Object> proceedsMap = new HashMap<String, Object>();
                BigDecimal proceedsAmount = BigDecimal.ZERO;
                if(tradeService.getDetails().containsKey("proceedsAmount")){
                    proceedsAmount = getBigDecimalOrZero(tradeService.getDetails().get("proceedsAmount"));
                } else {
                    System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS AMOUNT");
                }

                insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "DFFBproductPaymentTotal");
                insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "productPaymentTotal");
                if(productSettlementCurrency.equalsIgnoreCase("PHP")){
                    BigDecimal profitOrLossTotal = BigDecimal.ZERO;
                    BigDecimal amountWithProfitOrLoss = BigDecimal.ZERO;
                    //amountToBeSettledWithProfitOrLossIfInPhp
                    if(lcCurrency.equalsIgnoreCase("USD")){
                        amountWithProfitOrLoss = proceedsAmount.multiply(urr);
                    }
                    BigDecimal passOnBuyingRate = amountToBeSettledWithoutProfitOrLossIfInPhp.divide(proceedsAmount,8,BigDecimal.ROUND_HALF_UP);

                    BigDecimal passOnUrrDiff =  urr.subtract(passOnBuyingRate);
                    BigDecimal amountCentOther = proceedsAmount.multiply(passOnUrrDiff.subtract( new BigDecimal("0.01"))); //Parameterize this
                    BigDecimal amountOneCent = amountWithProfitOrLoss.subtract(amountCentOther);

                    profitOrLossTotal = amountWithProfitOrLoss.subtract(amountToBeSettledWithoutProfitOrLossIfInPhp);

                    ProfitLossHolder profitLossHolder = new ProfitLossHolder(
                            tradeService.getTradeServiceId().toString(),
                            paymentId,
                            proceedsAmount,
                            amountToBeSettledWithoutProfitOrLossIfInPhp ,
                            profitOrLossTotal, profitOrLossTotal, BigDecimal.ZERO, amountOneCent,amountCentOther);


                    profitLossHolderRepository.delete(profitLossHolder.getTradeServiceId(),profitLossHolder.getPaymentDetailId());
                    profitLossHolderRepository.save(profitLossHolder);



                    proceedsMap.put("fxProfitPHP", profitOrLossTotal);
                    proceedsMap.put(settlementName+ "PHP", amountToBeSettledWithoutProfitOrLossIfInPhp);
                } else {
                    insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, settlementName);
                }
                System.out.println("proceedsMap"+proceedsMap);

                //handling of FCDU and RBU Due from foreign bank - jj
                if (tradeService.getDetails().containsKey("accountType")==true) {
                	System.out.println("AccountType:" + tradeService.getDetails().get("accountType"));
                	if (tradeService.getDetails().get("accountType").toString().equals("RBU")) {
                		genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS-RBU"), gltsNumber, tradeServiceStatus);
//                		BookCode bookCode = BookCode.RG ;
//                		genAccountingEntryPaymentExports(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS"), gltsNumber, tradeServiceStatus, bookCode);
                		
                	} else if (tradeService.getDetails().get("accountType").toString().equals("FCDU")) {
                		genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS-FCDU"), gltsNumber, tradeServiceStatus);
//                		BookCode bookCode = BookCode.FC ;
//                		genAccountingEntryPaymentExports(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS"), gltsNumber, tradeServiceStatus, bookCode);
                		
                	}
                }
                
//                genAccountingEntryPayment_charges(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-TO-EXPORTER"), gltsNumber, tradeServiceStatus);
                genAccountingEntrySettlement_settlement(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-TO-EXPORTER"), gltsNumber, tradeServiceStatus);

            }

        } else {
        	
            Map<String, Object> ebcMap = new HashMap<String, Object>();
            BigDecimal ebcLessEbpAmount = BigDecimal.ZERO;
            BigDecimal ebpAmount = getBigDecimalOrZero(tradeService.getDetails().get("bpAmount"));
            
            //For re3versal of contingent
            if (ebpAmount.compareTo(BigDecimal.ZERO)==1) {
            	ebcLessEbpAmount = amount.subtract(ebpAmount);
                insertValueNameToValueMapUrr(tradeService, ebcLessEbpAmount, lcCurrency, ebcMap, urr, "negoAmount");

                System.out.println("EBC Negotiation Map:"+ ebcMap );
                
            	genAccountingEntryLC(tradeService, ebcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);

            }
            		
            //DUE from Foreign Bank ->  Trade Suspense for loan
            //Payment for Loan -> Trade Suspense for loan
            //Settlement of amount due
            //


            //Booking is done from payment currency to lc currency
            System.out.println("Settlement to Beneficiary of Product Charges Start EBP SETTLEMENT ANGOL");
            //DUE to PROCEEDS

            Map<String, Object> proceedsMap = new HashMap<String, Object>();
            BigDecimal proceedsAmount = BigDecimal.ZERO;
            if(tradeService.getDetails().containsKey("proceedsAmount")){
                proceedsAmount = getBigDecimalOrZero(tradeService.getDetails().get("proceedsAmount"));
            } else {
                System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS AMOUNT");
            }
            System.out.println("proceedsAmount:"+proceedsAmount);

            String proceedsCurrency = "";
            if(tradeService.getDetails().containsKey("proceedsCurrency") && tradeService.getDetails().get("proceedsCurrency")!=null){
                proceedsCurrency = tradeService.getDetails().get("proceedsCurrency").toString();
            } else {
            	//proceedsCurrency
                System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS CURRENCY");
            }
            System.out.println("proceedsCurrency:"+proceedsCurrency);

            
            
          //handling of FCDU and RBU Due from foreign bank - jj
            String acctIdPayment = "SETTLEMENT-OF-LOAN-FROM-PROCEEDS-";
            String acctIdSettlement = "SETTLEMENT-TO-EXPORTER-FCDU-";
            if (tradeService.getDetails().containsKey("accountType")==true) {
            	System.out.println("AccountType:" + tradeService.getDetails().get("accountType"));
            	if (tradeService.getDetails().get("accountType").toString().equals("RBU")) {
            		acctIdPayment = acctIdPayment.concat("RBU");
            		acctIdSettlement = acctIdSettlement.concat("RBU");
            		//            		genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS-RBU"), gltsNumber, tradeServiceStatus);
//            		BookCode bookCode = BookCode.RG ;
//            		genAccountingEntryPaymentExports(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS"), gltsNumber, tradeServiceStatus, bookCode);
            		            	
            	} else if (tradeService.getDetails().get("accountType").toString().equals("FCDU")) {
            		acctIdPayment = acctIdPayment.concat("FCDU");
            		acctIdSettlement = acctIdSettlement.concat("FCDU");
//            		genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS-FCDU"), gltsNumber, tradeServiceStatus);
//            		BookCode bookCode = BookCode.FC ;
//            		genAccountingEntryPaymentExports(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS"), gltsNumber, tradeServiceStatus, bookCode);
            		
            	}
            }
            
            // jj - handling of loan reversal
            
            BigDecimal amountForCredit = BigDecimal.ZERO; 

            
            if (proceedsAmount.compareTo(ebpAmount) == 1) {
            	amountForCredit = proceedsAmount.subtract(ebpAmount);
                insertValueNameToValueMapUrr(tradeService, amountForCredit, lcCurrency, proceedsMap, urr, "DFFBproductPaymentTotal");
                insertValueNameToValueMapUrr(tradeService, amountForCredit, lcCurrency, proceedsMap, urr, "productPaymentTotal");
                insertValueNameToValueMapUrr(tradeService, amountForCredit, lcCurrency, proceedsMap, urr, "settlementTotal");
                
                String settlementToBeneCurrencyStr = tradeService.getDetails().get("newProceedsCurrency").toString();
                BookCurrency settlementToBeneCurrency = determineBookCurrency(settlementToBeneCurrencyStr);
                
                System.out.println("proceedsMap: "+proceedsMap);
                System.out.println("acctIdPayment: "+acctIdPayment+ "\t acctIdSettlement: " +acctIdSettlement);
                System.out.println("AE for settlement to bene");
                genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, settlementToBeneCurrencyStr, settlementToBeneCurrency, new AccountingEventTransactionId(acctIdPayment), gltsNumber, tradeServiceStatus);
//                System.out.println("AE for settlement");
//                genAccountingEntrySettlement(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId(acctIdSettlement), gltsNumber, tradeServiceStatus);               
                String settlementName = "";
                String productSettlementCurrency = "";
                BookCurrency bcProductSettlementCurrency;
                
                if( paymentSettlement !=null ){
                    Set<PaymentDetail> temp = paymentSettlement.getDetails();
                    for (PaymentDetail paymentDetail : temp) {
                        //NOTE: from FSD only one settlement to beneficiary
                        System.out.println("---------------------------");
                        settlementName = getSettlementName(paymentDetail.getPaymentInstrumentType().toString());
                        productSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
      
                        System.out.println("settlementName: "+settlementName);
                                    
                        System.out.println("---------------------------");
                        printPaymentDetails(paymentDetail);
                        System.out.println("---------------------------");
                        
                    }
                
                    bcProductSettlementCurrency = determineBookCurrency(productSettlementCurrency);
                    
                    insertValueNameToValueMapUrr(tradeService, amountForCredit, lcCurrency, proceedsMap, urr, settlementName);
                    
                    genAccountingEntrySettlement_settlement(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-TO-EXPORTER"), gltsNumber, tradeServiceStatus);

                }
            } else {
            	ebpAmount = proceedsAmount;
            }
            
            System.out.println("EBC Doc Num: " + new DocumentNumber(tradeService.getDetails().get("documentNumber").toString()));
            
            List<ExportBills> ebpRecords = exportBillsRepository.loadByNegotiationNumber(new DocumentNumber(tradeService.getDetails().get("documentNumber").toString()));
            
            DocumentNumber ebpNumber = new DocumentNumber("");
            if (!ebpRecords.isEmpty()) {
            	for (ExportBills ebp : ebpRecords) {
            		ebpNumber = ebp.getDocumentNumber();
            	}            	
            }
            
            System.out.println("EBP Doc Num: " + new TradeProductNumber(ebpNumber.toString()));
                       
            TradeService tradeServiceEbpNego = tradeServiceRepository.load(new TradeProductNumber(ebpNumber.toString()), ServiceType.NEGOTIATION, DocumentType.FOREIGN, DocumentClass.BP);
            
            
            insertValueNameToValueMapUrr(tradeService, ebpAmount, lcCurrency, proceedsMap, urr, "DFFBproductPaymentTotalEBP");
            insertValueNameToValueMapUrr(tradeService, ebpAmount, lcCurrency, proceedsMap, urr, "productPaymentTotalEBP");
            insertValueNameToValueMapUrr(tradeServiceEbpNego, ebpAmount, lcCurrency, proceedsMap, urr, "settlementTotalEBP");
            System.out.println("AE for loan reversal");
            System.out.println("proceedsMap: "+proceedsMap);
            
            BookCurrency ebpLoanCurrency = determineBookCurrency("USD");
            
            genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, "USD", ebpLoanCurrency, new AccountingEventTransactionId(acctIdPayment.concat("-EBP")), gltsNumber, tradeServiceStatus);
            
            //Own Balancing entries            
            BigDecimal creditOriginal = accountingEntryActualRepository.getTotalOriginalCredit(tradeService.getTradeServiceId());
            System.out.println("creditOriginal:" + creditOriginal);
            BigDecimal debitOriginal = accountingEntryActualRepository.getTotalOriginalDebit(tradeService.getTradeServiceId());
            System.out.println("debitOriginal:" + debitOriginal);
            BigDecimal profitOrLossOriginal = BigDecimal.ZERO;
            String creditTo = "";
            
            if (creditOriginal.compareTo(debitOriginal)==1) {
            	profitOrLossOriginal = creditOriginal.subtract(debitOriginal);
            	creditTo = "Debit";            	
            } else if (creditOriginal.compareTo(debitOriginal)==-1) {
            	profitOrLossOriginal = debitOriginal.subtract(creditOriginal);
            	creditTo = "Credit";     
            }
            
            if (!creditTo.equalsIgnoreCase("")) {
            	insertValueNameToValueMapUrr(tradeService, profitOrLossOriginal, "USD", proceedsMap, urr, "balancing"+creditTo);
            	System.out.println("For Balancing USD amount proceedsMap: "+proceedsMap);
                genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, "USD", ebpLoanCurrency, new AccountingEventTransactionId("BALANCING-ENTRY"), gltsNumber, tradeServiceStatus);
                
            }
            

            //Section for Payment
            String paymentName = "";
            String paymentBaseName="";
            String productSettlementCurrency = "";
            BookCurrency bcProductSettlementCurrency;
            if( paymentProduct !=null ){
                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    //NOTE: from FSD only one settlement to beneficiary
                    System.out.println("---------------------------");
                    paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
                    paymentBaseName = paymentDetail.getPaymentInstrumentType().toString();
                    productSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    System.out.println("paymentName:"+paymentName);
                    System.out.println("productSettlementCurrency:"+productSettlementCurrency);
                    bcProductSettlementCurrency = determineBookCurrency(productSettlementCurrency);
                    insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap, urr, paymentName);
                    insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap , urr, "productPaymentTotal");
                    System.out.println("specificPaymentMap :"+specificPaymentMap );
                    System.out.println("---------------------------");

                    genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PAYMENT"), gltsNumber, tradeServiceStatus);
                    specificPaymentMap = new HashMap<String, Object>();
                    insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap , urr, "settlementTotal");
                    System.out.println("specificPaymentMap :"+specificPaymentMap );
                    genAccountingEntrySettlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PAYMENT"), gltsNumber, tradeServiceStatus);
                }
            }
            
                                  
        }



        //Settlement of Charges
        if (paymentService != null) {
            generateChargesAndChargesPaymentAccountingEntriesExport(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct, paymentSettlement);
        }

        //Own Balancing entries            
        BigDecimal creditOriginal = accountingEntryActualRepository.getTotalOriginalCredit(tradeService.getTradeServiceId());
        System.out.println("creditOriginal:" + creditOriginal);
        BigDecimal debitOriginal = accountingEntryActualRepository.getTotalOriginalDebit(tradeService.getTradeServiceId());
        System.out.println("debitOriginal:" + debitOriginal);
        
        if (creditOriginal.compareTo(debitOriginal)!=0) {
        	generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, paymentSettlement);
         	
        } 
        
        
    }

    
    @Transactional/*TODO TEST*/
    private void genAccountingEntries_DOMESTIC_BC_SETTLEMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_DOMESTIC_BC_SETTLEMENT");

        //For all Reverse Nego Amount in Contingent Field

        //Settlement of Charges
        //Get LC Currency
        String lcCurrency = "";
        if (tradeService.getProductChargeCurrency() != null) {
            lcCurrency = tradeService.getProductChargeCurrency().toString();
        }

        if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }
        }

        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        //Get Settlement Currency
        String settlementCurrencyCharges = "";
        if(tradeService.getServiceChargeCurrency()!=null){
            settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
        }
        if(tradeService.getDetails().get("settlementCurrency")!=null){
            settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
        } else{
            settlementCurrencyCharges = "PHP";
        }
        System.out.println("settlementCurrencyCharges:" + lcCurrency);
        BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        Boolean cilexCharged = willWeChargeCilex(paymentProduct);
        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

        Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
        Map<String, Object> chargeMap = generate_EXPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

        //REVERSAL-OF-CONTINGENT
        BigDecimal amount = BigDecimal.ZERO;
        if(tradeService.getDetails().containsKey("amount")){
            amount = getBigDecimalOrZero(tradeService.getDetails().get("amount"));
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO NEGO AMOUNT TO REVERSE");
        }
        System.out.println("DBC Negotiation Amount:"+ amount);
        System.out.println("THIRD-USD:"+tradeService.getDetails().get(lcCurrency+"-USD"));
        System.out.println("urr:"+tradeService.getDetails().get("USD-PHP_urr"));
        BigDecimal urr = new BigDecimal(tradeService.getDetails().get("USD-PHP_urr").toString().replace(",",""));

        Map<String, Object> negotiationMap = new HashMap<String, Object>();
        insertValueNameToValueMapUrr(tradeService, amount, lcCurrency, negotiationMap, urr, "negoAmount");


        //Settlement to Exporter
        ///DUEFromFB

        //Settlement of Product Charges

        //WITH EBP
        //bpCurrency and bpAmount is not null/empty
        //WITHOUT EBP
        //bpCurrency and bpAmount is null/empty

        String bpCurrency = "";
        if(tradeService.getDetails().containsKey("bpCurrency") && tradeService.getDetails().get("bpCurrency")!=null){
            bpCurrency = tradeService.getDetails().get("bpCurrency").toString();
        }



        if(bpCurrency.equalsIgnoreCase("")){
            //NO EBP
            //Reversal of Contingent Entries
            genAccountingEntryLC(tradeService, negotiationMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);

            //DUE from Foreign Bank ->  Settlement to Beneficiary

            String settlementName = "";
            //Booking is done from payment currency to lc currency
            System.out.println("Settlement to Beneficiary of Product Charges Start EBC SETTLEMENT NO EBP");
            String settlementBaseName="";
            String productSettlementCurrency = "";
            BookCurrency bcProductSettlementCurrency;
            if( paymentSettlement !=null ){
                Set<PaymentDetail> temp = paymentSettlement.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    //NOTE: from FSD only one settlement to beneficiary
                    System.out.println("---------------------------");
                    settlementName = getSettlementName(paymentDetail.getPaymentInstrumentType().toString());
                    settlementBaseName = paymentDetail.getPaymentInstrumentType().toString();
                    productSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    System.out.println("---------------------------");
                }

                bcProductSettlementCurrency = determineBookCurrency(productSettlementCurrency);


                Map<String, Object> proceedsMap = new HashMap<String, Object>();
                BigDecimal proceedsAmount = BigDecimal.ZERO;
                if(tradeService.getDetails().containsKey("proceedsAmount")){
                    proceedsAmount = getBigDecimalOrZero(tradeService.getDetails().get("proceedsAmount"));
                } else {
                    System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS AMOUNT");
                }

                insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "DFFBproductPaymentTotal");
                insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "productPaymentTotal");
                insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, settlementName);
                System.out.println("proceedsMap"+proceedsMap);

                genAccountingEntryPayment_charges(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-TO-EXPORTER"), gltsNumber, tradeServiceStatus);
                genAccountingEntrySettlement_settlement(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-TO-EXPORTER"), gltsNumber, tradeServiceStatus);

            }




        } else {
            //WITH EBP
            //NO Reversal of Contingent Entries

            //DUE from Foreign Bank ->  Trade Suspense for loan
            //Payment for Loan -> Trade Suspense for loan
            //Settlement of amount due
            //


            //Booking is done from payment currency to lc currency
            System.out.println("Settlement to Beneficiary of Product Charges Start EBP SETTLEMENT ANGOL");
            //DUE to PROCEEDS

            Map<String, Object> proceedsMap = new HashMap<String, Object>();
            BigDecimal proceedsAmount = BigDecimal.ZERO;
            if(tradeService.getDetails().containsKey("proceedsAmount")){
                proceedsAmount = getBigDecimalOrZero(tradeService.getDetails().get("proceedsAmount"));
            } else {
                System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS AMOUNT");
            }
            System.out.println("proceedsAmount:"+proceedsAmount);

            String proceedsCurrency = "";
            if(tradeService.getDetails().containsKey("proceedsCurrency") && tradeService.getDetails().get("proceedsCurrency")!=null){
                proceedsCurrency = tradeService.getDetails().get("proceedsCurrency").toString();
            } else {
                System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS AMOUNT");
            }
            System.out.println("proceedsCurrency:"+proceedsCurrency);

            BookCurrency bcProceedsCurrency = determineBookCurrency(proceedsCurrency);

            System.out.println("settlementName");
            insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "DFFBproductPaymentTotal");
            insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "productPaymentTotal");
            insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "settlementTotal");
            System.out.println("proceedsMap"+proceedsMap);

            genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS"), gltsNumber, tradeServiceStatus);
            genAccountingEntrySettlement(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS"), gltsNumber, tradeServiceStatus);


            //Section for Payment
            String paymentName = "";
            String paymentBaseName="";
            String productSettlementCurrency = "";
            BookCurrency bcProductSettlementCurrency;
            if( paymentProduct !=null ){
                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    //NOTE: from FSD only one settlement to beneficiary
                    System.out.println("---------------------------");
                    paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
                    paymentBaseName = paymentDetail.getPaymentInstrumentType().toString();
                    productSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    System.out.println("paymentName:"+paymentName);
                    System.out.println("productSettlementCurrency:"+productSettlementCurrency);
                    bcProductSettlementCurrency = determineBookCurrency(productSettlementCurrency);
                    insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap, urr, paymentName);
                    insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap , urr, "productPaymentTotal");
                    System.out.println("specificPaymentMap :"+specificPaymentMap );
                    System.out.println("---------------------------");

                    genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PAYMENT"), gltsNumber, tradeServiceStatus);
                    specificPaymentMap = new HashMap<String, Object>();
                    insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap , urr, "settlementTotal");
                    System.out.println("specificPaymentMap :"+specificPaymentMap );
                    genAccountingEntrySettlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PAYMENT"), gltsNumber, tradeServiceStatus);
                }
            }


        }



        //Settlement of Charges
        if (paymentService != null) {
            generateChargesAndChargesPaymentAccountingEntriesExport(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct, paymentSettlement);
        }
    }

    @Transactional
    private void genAccountingEntries_FOREIGN_BP_NEGOTIATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {

        //Creation of SIBS-LOAN if any

        //Settlement of Charges
        //Get LC Currency
        String lcCurrency = "";
        if (tradeService.getProductChargeCurrency() != null) {
            lcCurrency = tradeService.getProductChargeCurrency().toString();
        }

        if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }
        }

        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        //Get Settlement Currency
        String settlementCurrencyCharges = "";
        if(tradeService.getServiceChargeCurrency()!=null){
            settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
        }
        if(tradeService.getDetails().get("settlementCurrency")!=null){
            settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
        } else{
            settlementCurrencyCharges = "PHP";
        }
        System.out.println("settlementCurrencyCharges:" + lcCurrency);
        BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        Boolean cilexCharged = willWeChargeCilex(paymentProduct);
        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

        Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
        Map<String, Object> chargeMap = generate_EXPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

        //REVERSAL-OF-CONTINGENT
        BigDecimal bcAmount = BigDecimal.ZERO;
        BigDecimal bpAmount = BigDecimal.ZERO;
        if(tradeService.getDetails().containsKey("bcAmount")){
            bcAmount = getBigDecimalOrZero(tradeService.getDetails().get("bcAmount"));
            bpAmount = bcAmount;
            if(tradeService.getDetails().containsKey("amount")){ 
            	bpAmount = getBigDecimalOrZero(tradeService.getDetails().get("amount"));
            }
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO NEGO AMOUNT TO REVERSE");
        }
        
        
        System.out.println("EBC Negotiation Amount:"+ bcAmount);
        System.out.println("EBP Negotiation Amount:"+ bpAmount);
        System.out.println("THIRD-USD:"+tradeService.getDetails().get(lcCurrency+"-USD"));
        System.out.println("urr:"+tradeService.getDetails().get("USD-PHP_urr"));
        BigDecimal urr = new BigDecimal(tradeService.getDetails().get("USD-PHP_urr").toString().replace(",",""));

        Map<String, Object> negotiationMap = new HashMap<String, Object>();
        insertValueNameToValueMapUrr(tradeService, bpAmount, lcCurrency, negotiationMap, urr, "negoAmount");
        System.out.println("negotiationMap:"+negotiationMap);

        String bcCurrency = "";
        if(tradeService.getDetails().containsKey("bcCurrency") && tradeService.getDetails().get("bcCurrency")!=null){
            bcCurrency = tradeService.getDetails().get("bcCurrency").toString();
        }
        System.out.println("bcCurrency:"+bcCurrency);

        String negotiationNumber = "";
        if(tradeService.getDetails().containsKey("negotiationNumber") && tradeService.getDetails().get("negotiationNumber")!=null){
            negotiationNumber = tradeService.getDetails().get("negotiationNumber").toString();
        }
        System.out.println("negotiationNumber:"+negotiationNumber);

        if(!negotiationNumber.equalsIgnoreCase("")){
            //WITH EBC
            //Reversal of Contingent Entries
            genAccountingEntryLC(tradeService, negotiationMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
        } else {
            //NO EBC
            //NO Reversal of Contingent Entries
        }



        BigDecimal amount = BigDecimal.ZERO;
        if(tradeService.getDetails().containsKey("amount")){
            amount = getBigDecimalOrZero(tradeService.getDetails().get("amount"));
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO NEGO AMOUNT TO REVERSE");
        }

        String currency = "";
        if(tradeService.getDetails().containsKey("currency") && tradeService.getDetails().get("currency")!=null){
            currency = tradeService.getDetails().get("currency").toString();
        }

        //Trade Suspense for SIBS Loan -> Settlement to Beneficiary
        //SIBS Loan in USD   to Settlement to Beneficiary
        //newProceedsCurrency -> currency of settlement to beneficiary
        //proceedsAmount -> amount for settlement to beneficiary
        //PaymentInstructionType -> EBP

        BigDecimal advanceInterestUSD = BigDecimal.ZERO;
        BigDecimal advanceInterestPHP = BigDecimal.ZERO;

        if(tradeService.getDetails().containsKey("advanceInterest") && tradeService.getDetails().get("advanceInterest")!=null){
            advanceInterestUSD = new BigDecimal(tradeService.getDetails().get("advanceInterest").toString());
            advanceInterestPHP = advanceInterestUSD.multiply(urr);
        }

        String paymentSettlementCurrency = null;
        BookCurrency paySettleBookCurrency = null;
        if(paymentSettlement!=null){
            Set<PaymentDetail> temp = paymentSettlement.getDetails();
            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);
                paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                paySettleBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                System.out.println("---------------------------");
            }
        }

        String accountingEventTypeIdString ="SETTLEMENT-TO-EXPORTER-SIBS";
        if(paymentProduct!=null){
            //Booking is done from payment currency to lc currency
            System.out.println("Payment of Product Charges Start EBP LOAN");
            Set<PaymentDetail> temp = paymentProduct.getDetails();
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);

                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                System.out.println("specificPaymentMap:" + specificPaymentMap);

                specificPaymentMap.put("advanceInterestUSD",advanceInterestUSD);
                specificPaymentMap.put("advanceInterestPHP",advanceInterestPHP);
                System.out.println("---------------------------");

//                String paymentProductCurrency = paymentDetail.getCurrency().getCurrencyCode();
//                BookCurrency payBookCurrency = determineBookCurrency(paymentProductCurrency);

                genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, paySettleBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
            }
        }




        if(paymentProduct!=null && paymentSettlement!=null){
            //Booking is done from payment currency to lc currency
            System.out.println("Settlement to Beneficiary of EBP LOAN");

            BigDecimal settlementAmount = BigDecimal.ZERO;
            String settlementName ="";
            Set<PaymentDetail> temp = paymentSettlement.getDetails();
            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);
//                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
//                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                settlementAmount = paymentDetail.getAmount();
                paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                paySettleBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                settlementName = getSettlementNameEBP(paymentDetail.getPaymentInstrumentType().toString());
                if (paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("CASA") && (!tradeService.getDetails().containsKey("CASAAccountNo") &&
                		tradeService.getDetails().get("CASAAccountNo") == null)) {
                	tradeService.getDetails().put("CASAAccountNo", paymentDetail.getReferenceNumber());
                }
                
                System.out.println("settlementName:"+settlementName);
            }

            specificPaymentMap.put(settlementName+paySettleBookCurrency.toString(),settlementAmount);
            if(paymentSettlementCurrency.equalsIgnoreCase("PHP")){
                specificPaymentMap.put(settlementName+"PHP",settlementAmount);
            } else if(paymentSettlementCurrency.equalsIgnoreCase("USD")){
                specificPaymentMap.put(settlementName+"USD",settlementAmount);
                specificPaymentMap.put(settlementName+"PHP",settlementAmount.multiply(urr));
            } else { //THIRD
                System.out.println( "THIRD CURRENCY EBP HAS BEEN DEFERRED" );
            }

            System.out.println("specificPaymentMap:" + specificPaymentMap);
            System.out.println("---------------------------");
            genAccountingEntrySettlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, paySettleBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
        }


        //Settlement of Charges
        if (paymentService != null) {
            generateChargesAndChargesPaymentAccountingEntriesExport(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct, paymentSettlement);
        }
    }

    @Transactional
    private void genAccountingEntries_DOMESTIC_BP_NEGOTIATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {

        //Creation of SIBS-LOAN if any

        //Settlement of Charges
        //Get LC Currency
        String lcCurrency = "";
        if (tradeService.getProductChargeCurrency() != null) {
            lcCurrency = tradeService.getProductChargeCurrency().toString();
        }

        if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }
        }

        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        //Get Settlement Currency
        String settlementCurrencyCharges = "";
        if(tradeService.getServiceChargeCurrency()!=null){
            settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
        }
        if(tradeService.getDetails().get("settlementCurrency")!=null){
            settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
        } else{
            settlementCurrencyCharges = "PHP";
        }
        System.out.println("settlementCurrencyCharges:" + lcCurrency);
        BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        Boolean cilexCharged = willWeChargeCilex(paymentProduct);
        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

        Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
        Map<String, Object> chargeMap = generate_EXPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

        //REVERSAL-OF-CONTINGENT
        BigDecimal bcAmount = BigDecimal.ZERO;
        if(tradeService.getDetails().containsKey("bcAmount")){
            bcAmount = getBigDecimalOrZero(tradeService.getDetails().get("bcAmount"));
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO NEGO AMOUNT TO REVERSE");
        }
        System.out.println("EBC Negotiation Amount:"+ bcAmount);
        System.out.println("THIRD-USD:"+tradeService.getDetails().get(lcCurrency+"-USD"));
        System.out.println("urr:"+tradeService.getDetails().get("USD-PHP_urr"));
        BigDecimal urr = new BigDecimal(tradeService.getDetails().get("USD-PHP_urr").toString().replace(",",""));

        Map<String, Object> negotiationMap = new HashMap<String, Object>();
        insertValueNameToValueMapUrr(tradeService, bcAmount, lcCurrency, negotiationMap, urr, "negoAmount");
        System.out.println("negotiationMap:"+negotiationMap);

        String bcCurrency = "";
        if(tradeService.getDetails().containsKey("bcCurrency") && tradeService.getDetails().get("bcCurrency")!=null){
            bcCurrency = tradeService.getDetails().get("bcCurrency").toString();
        }
        System.out.println("bcCurrency:"+bcCurrency);

        String negotiationNumber = "";
        if(tradeService.getDetails().containsKey("negotiationNumber") && tradeService.getDetails().get("negotiationNumber")!=null){
            negotiationNumber = tradeService.getDetails().get("negotiationNumber").toString();
        }
        System.out.println("negotiationNumber:"+negotiationNumber);

        if(!negotiationNumber.equalsIgnoreCase("")){
            //WITH EBC
            //Reversal of Contingent Entries
            genAccountingEntryLC(tradeService, negotiationMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
        } else {
            //NO EBC
            //NO Reversal of Contingent Entries
        }



        BigDecimal amount = BigDecimal.ZERO;
        if(tradeService.getDetails().containsKey("amount")){
            amount = getBigDecimalOrZero(tradeService.getDetails().get("amount"));
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO NEGO AMOUNT TO REVERSE");
        }

        String currency = "";
        if(tradeService.getDetails().containsKey("currency") && tradeService.getDetails().get("currency")!=null){
            currency = tradeService.getDetails().get("currency").toString();
        }

        //Trade Suspense for SIBS Loan -> Settlement to Beneficiary
        //SIBS Loan in USD   to Settlement to Beneficiary
        //newProceedsCurrency -> currency of settlement to beneficiary
        //proceedsAmount -> amount for settlement to beneficiary
        //PaymentInstructionType -> EBP

        String paymentSettlementCurrency = null;
        BookCurrency paySettleBookCurrency = null;
        if(paymentSettlement!=null){
            Set<PaymentDetail> temp = paymentSettlement.getDetails();
            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);
                paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                paySettleBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                System.out.println("---------------------------");
            }
        }

        String dbpCurrency = "";
        BookCurrency bcDbpCurrency = null;
        //TODO: Handle PHP DBP loans by configuring the RefAccEntry
        String accountingEventTypeIdString ="SETTLEMENT-TO-EXPORTER-SIBS";
        if(paymentProduct!=null){
            //Booking is done from payment currency to lc currency
            System.out.println("Payment of Product Charges Start DBP LOAN");
            Set<PaymentDetail> temp = paymentProduct.getDetails();
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);

                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                System.out.println("specificPaymentMap:" + specificPaymentMap);
                System.out.println("---------------------------");

                dbpCurrency =  paymentDetail.getCurrency().getCurrencyCode();
                bcDbpCurrency = determineBookCurrency(dbpCurrency);


                //Usually the currency from is based on the LC Currency
                //Here since this will be a DBP loan the base becomes the dbp loan currency
                genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, dbpCurrency, bcDbpCurrency, paymentSettlementCurrency, paySettleBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
            }
        }




        if(paymentProduct!=null && paymentSettlement!=null){
            //Booking is done from payment currency to lc currency
            System.out.println("Settlement to Beneficiary of DBP LOAN");

            BigDecimal settlementAmount = BigDecimal.ZERO;
            String settlementName ="";
            Set<PaymentDetail> temp = paymentSettlement.getDetails();
            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);
//                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
//                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                settlementAmount = paymentDetail.getAmount();
                paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                paySettleBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                settlementName = paymentDetail.getPaymentInstrumentType().toString();

            }

            specificPaymentMap.put(settlementName+"settlementTotal"+paySettleBookCurrency.toString(),settlementAmount);
            if(paySettleBookCurrency.toString().equalsIgnoreCase("PHP")){
                specificPaymentMap.put(settlementName+"settlementTotal"+"PHP",settlementAmount);
            } else if(paySettleBookCurrency.toString().equalsIgnoreCase("USD")){
                specificPaymentMap.put(settlementName+"settlementTotal"+"PHP",settlementAmount.multiply(urr));
            } else { //THIRD
                System.out.println( "THIRD CURRENCY EBP HAS BEEN DEFERRED" );
            }

            System.out.println("specificPaymentMap:" + specificPaymentMap);
            System.out.println("---------------------------");
            //Usually the currency from is based on the LC Currency
            //Here since this will be a DBP loan the base becomes the dbp loan currency
            genAccountingEntrySettlement_settlement(tradeService, specificPaymentMap, productRef, dbpCurrency, bcDbpCurrency, paymentSettlementCurrency, paySettleBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
        }


        //Settlement of Charges
        if (paymentService != null) {
            generateChargesAndChargesPaymentAccountingEntriesExport(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct, paymentSettlement);
        }
    }

    @Transactional
    private void genAccountingEntries_FOREIGN_BP_SETTLEMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_FOREIGN_BP_SETTLEMENT");
        //Settlement of SIBS-LOAN if any
        //Settlement to Exporter

        //Create Product/Charges Summary here
        //Get LC Currency
        String lcCurrency = "";
        if (tradeService.getProductChargeCurrency() != null) {
            lcCurrency = tradeService.getProductChargeCurrency().toString();
        }

        if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }
        }

        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        //Get Settlement Currency
        String settlementCurrencyCharges = "";
        if(tradeService.getServiceChargeCurrency()!=null){
            settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
        }
        if(tradeService.getDetails().get("settlementCurrency")!=null){
            settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
        } else{
            settlementCurrencyCharges = "PHP";
        }
        System.out.println("settlementCurrencyCharges:" + lcCurrency);
        BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        Boolean cilexCharged = willWeChargeCilex(paymentProduct);
        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

        Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
        Map<String, Object> chargeMap = generate_EXPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

        System.out.println("tradeService.getDetails():" + tradeService.getDetails());

        System.out.println("THIRD-USD:"+tradeService.getDetails().get(lcCurrency+"-USD"));
        System.out.println("urr:"+tradeService.getDetails().get("USD-PHP_urr"));
        BigDecimal urr = new BigDecimal(tradeService.getDetails().get("USD-PHP_urr").toString().replace(",",""));


        //Booking is done from payment currency to lc currency
        System.out.println("Settlement to Beneficiary of Product Charges Start EBP SETTLEMENT ANGOL");
        //DUE to PROCEEDS

        Map<String, Object> proceedsMap = new HashMap<String, Object>();
        BigDecimal proceedsAmount = BigDecimal.ZERO;
        if(tradeService.getDetails().containsKey("proceedsAmount")){
            proceedsAmount = getBigDecimalOrZero(tradeService.getDetails().get("proceedsAmount"));
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS AMOUNT");
        }
        System.out.println("proceedsAmount:"+proceedsAmount);

        BookCurrency bcProceedsCurrency = determineBookCurrency("USD");

        System.out.println("settlementName");
//        insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "DFFBproductPaymentTotal");
//        insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "productPaymentTotal");
//        insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "settlementTotal");

        System.out.println("EBP Doc Num: " + new TradeProductNumber(tradeService.getDetails().get("documentNumber").toString()));
                   
        TradeService tradeServiceEbpNego = tradeServiceRepository.load(new TradeProductNumber(tradeService.getDetails().get("documentNumber").toString()), ServiceType.NEGOTIATION, DocumentType.FOREIGN, DocumentClass.BP);
                
        insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "DFFBproductPaymentTotal");
        insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "productPaymentTotal");
        insertValueNameToValueMapUrr(tradeServiceEbpNego, proceedsAmount, lcCurrency, proceedsMap, urr, "settlementTotal");
        System.out.println("AE for loan reversal");
        System.out.println("proceedsMap: "+proceedsMap);      
        
        //handling of FCDU and RBU Due from foreign bank - jj
        String accountingEventTransactionId = "";
        if (tradeService.getDetails().containsKey("accountType")==true) {
        	System.out.println("AccountType:" + tradeService.getDetails().get("accountType"));
        	if (tradeService.getDetails().get("accountType").toString().equals("RBU")) {
        		accountingEventTransactionId = "SETTLEMENT-OF-LOAN-FROM-PROCEEDS-RBU";
        		genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, "USD", bcProceedsCurrency, new AccountingEventTransactionId(accountingEventTransactionId), gltsNumber, tradeServiceStatus);
  		        	
        	} else if (tradeService.getDetails().get("accountType").toString().equals("FCDU")) {
        		accountingEventTransactionId = "SETTLEMENT-OF-LOAN-FROM-PROCEEDS-FCDU";
        		genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, "USD", bcProceedsCurrency, new AccountingEventTransactionId(accountingEventTransactionId), gltsNumber, tradeServiceStatus);

        	}
        }
        genAccountingEntrySettlement(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, "USD", bcProceedsCurrency, new AccountingEventTransactionId(accountingEventTransactionId), gltsNumber, tradeServiceStatus);

        
        //Own Balancing entries            
        BigDecimal creditOriginal = accountingEntryActualRepository.getTotalOriginalCredit(tradeService.getTradeServiceId());
        System.out.println("creditOriginal:" + creditOriginal);
        BigDecimal debitOriginal = accountingEntryActualRepository.getTotalOriginalDebit(tradeService.getTradeServiceId());
        System.out.println("debitOriginal:" + debitOriginal);
        BigDecimal profitOrLossOriginal = BigDecimal.ZERO;
        String creditTo = "";
        
        if (creditOriginal.compareTo(debitOriginal)==1) {
        	profitOrLossOriginal = creditOriginal.subtract(debitOriginal);
        	creditTo = "Debit";            	
        } else if (creditOriginal.compareTo(debitOriginal)==-1) {
        	profitOrLossOriginal = debitOriginal.subtract(creditOriginal);
        	creditTo = "Credit";     
        }
        
        if (!creditTo.equalsIgnoreCase("")) {
        	insertValueNameToValueMapUrr(tradeService, profitOrLossOriginal, "USD", proceedsMap, urr, "balancing"+creditTo);
        	System.out.println("For Balancing USD amount proceedsMap: "+proceedsMap);
            genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, "USD", bcProceedsCurrency, new AccountingEventTransactionId("BALANCING-ENTRY"), gltsNumber, tradeServiceStatus);
            
        }
        

        //Section for Payment
        String paymentName = "";
        String paymentBaseName="";
        String productSettlementCurrency = "";
        BookCurrency bcProductSettlementCurrency;
        if( paymentProduct !=null ){
            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            Set<PaymentDetail> temp = paymentProduct.getDetails();
            for (PaymentDetail paymentDetail : temp) {
                //NOTE: from FSD only one settlement to beneficiary
                System.out.println("---------------------------");
                paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
                paymentBaseName = paymentDetail.getPaymentInstrumentType().toString();
                productSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                System.out.println("paymentName:"+paymentName);
                System.out.println("productSettlementCurrency:"+productSettlementCurrency);
                bcProductSettlementCurrency = determineBookCurrency(productSettlementCurrency);
                insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap, urr, paymentName);
                insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap , urr, "productPaymentTotal");
                System.out.println("specificPaymentMap :"+specificPaymentMap );
                System.out.println("---------------------------");

                genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PAYMENT"), gltsNumber, tradeServiceStatus);
                specificPaymentMap = new HashMap<String, Object>();
                insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap , urr, "settlementTotal");
                System.out.println("specificPaymentMap :"+specificPaymentMap );
                genAccountingEntrySettlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PAYMENT"), gltsNumber, tradeServiceStatus);
            }
        }




        //Settlement of Charges
        if (paymentService != null) {
            generateChargesAndChargesPaymentAccountingEntriesExport(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct, paymentSettlement);
        }
    }

    @Transactional/*TODO*/
    private void genAccountingEntries_DOMESTIC_BP_SETTLEMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_FOREIGN_BP_SETTLEMENT");
        //Settlement of SIBS-LOAN if any
        //Settlement to Exporter

        //Create Product/Charges Summary here
        //Get LC Currency
        String lcCurrency = "";
        if (tradeService.getProductChargeCurrency() != null) {
            lcCurrency = tradeService.getProductChargeCurrency().toString();
        }

        if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }
        }

        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        //Get Settlement Currency
        String settlementCurrencyCharges = "";
        if(tradeService.getServiceChargeCurrency()!=null){
            settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
        }
        if(tradeService.getDetails().get("settlementCurrency")!=null){
            settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
        } else{
            settlementCurrencyCharges = "PHP";
        }
        System.out.println("settlementCurrencyCharges:" + lcCurrency);
        BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        Boolean cilexCharged = willWeChargeCilex(paymentProduct);
        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

        Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
        Map<String, Object> chargeMap = generate_EXPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

        System.out.println("tradeService.getDetails():" + tradeService.getDetails());

        System.out.println("THIRD-USD:"+tradeService.getDetails().get(lcCurrency+"-USD"));
        System.out.println("urr:"+tradeService.getDetails().get("USD-PHP_urr"));
        BigDecimal urr = new BigDecimal(tradeService.getDetails().get("USD-PHP_urr").toString().replace(",",""));


        //Booking is done from payment currency to lc currency
        System.out.println("Settlement to Beneficiary of Product Charges Start EBP SETTLEMENT ANGOL");
        //DUE to PROCEEDS

        Map<String, Object> proceedsMap = new HashMap<String, Object>();
        BigDecimal proceedsAmount = BigDecimal.ZERO;
        if(tradeService.getDetails().containsKey("proceedsAmount")){
            proceedsAmount = getBigDecimalOrZero(tradeService.getDetails().get("proceedsAmount"));
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS AMOUNT");
        }
        System.out.println("proceedsAmount:"+proceedsAmount);

        String proceedsCurrency = "";
        if(tradeService.getDetails().containsKey("proceedsCurrency") && tradeService.getDetails().get("proceedsCurrency")!=null){
            proceedsCurrency = tradeService.getDetails().get("proceedsCurrency").toString();
        } else {
            System.out.println("ERROR >>>>>>>>>>> NO PROCEEDS currency");
            proceedsCurrency = "PHP";
        }
        System.out.println("proceedsCurrency:"+proceedsCurrency);

        BookCurrency bcProceedsCurrency = determineBookCurrency(proceedsCurrency);

        System.out.println("settlementName");
        insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "DFFBproductPaymentTotal");
        insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "productPaymentTotal");
        insertValueNameToValueMapUrr(tradeService, proceedsAmount, lcCurrency, proceedsMap, urr, "settlementTotal");
        System.out.println("proceedsMap"+proceedsMap);
        


        genAccountingEntryPayment(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS"), gltsNumber, tradeServiceStatus);
        genAccountingEntrySettlement(tradeService, proceedsMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PROCEEDS"), gltsNumber, tradeServiceStatus);


        //Section for Payment
        String paymentName = "";
        String paymentBaseName="";
        String productSettlementCurrency = "";
        BookCurrency bcProductSettlementCurrency;
        if( paymentProduct !=null ){
            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            Set<PaymentDetail> temp = paymentProduct.getDetails();
            for (PaymentDetail paymentDetail : temp) {
                //NOTE: from FSD only one settlement to beneficiary
                System.out.println("---------------------------");
                paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
                paymentBaseName = paymentDetail.getPaymentInstrumentType().toString();
                productSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                System.out.println("paymentName:"+paymentName);
                System.out.println("productSettlementCurrency:"+productSettlementCurrency);
                bcProductSettlementCurrency = determineBookCurrency(productSettlementCurrency);
                insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap, urr, paymentName);
                insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap , urr, "productPaymentTotal");
                System.out.println("specificPaymentMap :"+specificPaymentMap );
                System.out.println("---------------------------");

                genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PAYMENT"), gltsNumber, tradeServiceStatus);
                specificPaymentMap = new HashMap<String, Object>();
                insertValueNameToValueMapUrr(tradeService, paymentDetail.getAmountInLcCurrency(), lcCurrency, specificPaymentMap , urr, "settlementTotal");
                System.out.println("specificPaymentMap :"+specificPaymentMap );
                genAccountingEntrySettlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, productSettlementCurrency, bcProductSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-OF-LOAN-FROM-PAYMENT"), gltsNumber, tradeServiceStatus);
            }
        }




        //Settlement of Charges
        if (paymentService != null) {
            generateChargesAndChargesPaymentAccountingEntriesExport(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct, paymentSettlement);
        }
    }

    @Transactional
    private void genAccountingEntries_IMPORT_ADVANCE_PAYMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {

        System.out.println("genAccountingEntries_IMPORT_ADVANCE_PAYMENT");
        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            }

            if(lcCurrency==null || "".equalsIgnoreCase(lcCurrency)){
                if(tradeService.getDetails().containsKey("currency")){
                    lcCurrency = tradeService.getDetails().get("currency").toString();
                }
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getServiceChargeCurrency()!=null){
                settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
            }
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + settlementCurrencyCharges);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

            BigDecimal negotiationAmount;
            if(details.containsKey("amount") && !details.get("amount").toString().equalsIgnoreCase("")){
                negotiationAmount = new BigDecimal(details.get("amount").toString().replace(",",""));
            } else {
                negotiationAmount = BigDecimal.ZERO;
                System.out.println("MISSING AMOUNT FIELD");
            }

//            BigDecimal outstandingBalance = getBigDecimalOrZero(details.get("outstandingBalance"));

            System.out.println("negotiationAmount:" + negotiationAmount);
//            System.out.println("outstandingBalance:" + outstandingBalance);

            String negoCurrency = getStringOrReturnEmptyString(details, "negotiationCurrency");
            if (negoCurrency == null) {
                negoCurrency = "PHP";
                System.out.println("No nego currency found defaulting to PHP");
            }
//            BookCurrency bcNegoCurrency = determineBookCurrency(negoCurrency);

//            Map<String, Object> negoMapOutstandingBalance = new HashMap<String, Object>();
//            Map<String, Object> negoMapProductAmount = new HashMap<String, Object>();
//            Map<String, Object> negoMap = new HashMap<String, Object>();

            BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));

//            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMap, urr, "negoAmount");
//            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMap, urr, "settlementAmount");
//            System.out.println("foreign negoMap:" + negoMap);

//            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMapProductAmount, urr, "negoAmount");
//            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMapProductAmount, urr, "settlementAmount");
//            System.out.println("negoMapProductAmount:" + negoMapProductAmount);//This is the nego map based on the product amount

//            insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, negoMapOutstandingBalance, urr, "negoAmount");
//            insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, negoMapOutstandingBalance, urr, "settlementAmount");
//            System.out.println("negoMapOutstandingBalance:" + negoMapOutstandingBalance);//This is the nego map based on the outstanding amount


            //Settlement of Nego Amount
            String accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR-FC-BOOK";
            if (paymentProduct != null) {
                //Booking is done from payment currency to lc currency
                System.out.println("Payment of Product Charges Start NEGOTIATION");
                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);

                    Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                    placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                    placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                    placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency);
                    System.out.println("specificPaymentMap:" + specificPaymentMap);
                    System.out.println("---------------------------");

                    String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                    accountingEventTypeIdString = getAccountingEventIdStringPaymentOrLoan_FX(paymentDetail);
                    String accountType = "RBU";
                    if (tradeService.getDetails().containsKey("accountType")) {
                        accountType = tradeService.getDetails().get("accountType").toString();
                        System.out.println("accountType:"+accountType);
                    }

                    accountingEventTypeIdString = getBookCodeStringPostFix(accountType, accountingEventTypeIdString);
                    System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);

                    genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                    genAccountingEntryPaymentCharge_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                }
            }


            Map<String, Object> negotiationCashMap = new HashMap<String, Object>();
//            System.out.println("negotiationCashMap:" + negoMap);
            //outstandingBalanceUSD -> What will be reversed in AP CASH LC Domestic
            //negoAmountTHIRD -> What will be remitted
            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negotiationCashMap, urr, "DUEFromFBsettlementTotal");
            //Generate Accounting Entry Related to payment of cash fxlc

            //ONLY ONCE FOR EVERYTHING
            genAccountingEntrySettlement_settlement(tradeService, negotiationCashMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Transactional												
    private void genAccountingEntries_EXPORT_ADVANCE_PAYMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    	 System.out.println("genAccountingEntries_EXPORT_ADVANCE_PAYMENT");
    	 
    	 try {
    		 
    		 String exportCurrency = "";
             if (tradeService.getProductChargeCurrency() != null) {
                 exportCurrency = tradeService.getProductChargeCurrency().toString();
             }

             if(exportCurrency==null || "".equalsIgnoreCase(exportCurrency)){
                 if(tradeService.getDetails().containsKey("currency")){
                     exportCurrency = tradeService.getDetails().get("currency").toString();
                 }
             }
    		 
             System.out.println("exportCurrency:" + exportCurrency);
             BookCurrency bcexportCurrency = determineBookCurrency(exportCurrency);

             BigDecimal amountProceeeds = getBigDecimalOrZero(details.get("amount"));
             
             //String accountType = tradeService.getDetails().get("accountType").toString();
             BigDecimal urr = tradeService.getSpecialRateUrr();
             BigDecimal thirdToUSD = tradeService.getSpecialRateThirdToUsd();
             
             
             //Get Settlement Currency
             String settlementCurrencyCharges = "";
             if(tradeService.getServiceChargeCurrency()!=null){
                 settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
             }
             if(tradeService.getDetails().get("settlementCurrency")!=null){
                 settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
             } else{
                 settlementCurrencyCharges = "PHP";
             }
             System.out.println("settlementCurrencyCharges:" + settlementCurrencyCharges);
             BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

             System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

             Boolean cilexCharged = willWeChargeCilex(paymentProduct);
             Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

             Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, exportCurrency, "lcAmount");
             Map<String, Object> chargeMap = generate_EXPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, exportCurrency, paymentSettlement);

             
             String accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR-FC-BOOK";
             
           

             BigDecimal settleInPHP = BigDecimal.ZERO;
             BigDecimal settleInUSD = BigDecimal.ZERO;
             BigDecimal settleInTHIRD = BigDecimal.ZERO;
             
             Map<String, Object> specificSettleMap = new HashMap<String, Object>();
             
             if(bcexportCurrency.equals(BookCurrency.PHP)) {
            	 settleInPHP = amountProceeeds;
            	 accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR-RG-BOOK";
            	 

            	 specificSettleMap.put("APRemmittanceproductPaymentTotalPHP", settleInPHP);
            	 specificSettleMap.put("CASAproductPaymentTotalCreditPHP", settleInPHP);
            	 
             }else if(bcexportCurrency.equals(BookCurrency.USD)) {
            	 settleInUSD = amountProceeeds;
            	 settleInPHP = amountProceeeds.multiply(urr).setScale(2, BigDecimal.ROUND_UP);
            	 
            	 specificSettleMap.put("APRemmittanceproductPaymentTotalUSD", settleInUSD);
            	 specificSettleMap.put("APRemmittanceproductPaymentTotalPHP", settleInPHP);
            	 specificSettleMap.put("CASAproductPaymentTotalCreditUSD", settleInUSD);
            	 specificSettleMap.put("CASAproductPaymentTotalCreditPHP", settleInPHP);
            	 
             }else {
            	 settleInTHIRD = amountProceeeds;        	 
            	 settleInUSD = settleInTHIRD.multiply(thirdToUSD).setScale(2, BigDecimal.ROUND_UP);
            	 settleInPHP = settleInUSD.multiply(urr).setScale(2, BigDecimal.ROUND_UP);
            	 
            	 specificSettleMap.put("APRemmittanceproductPaymentTotalTHIRD", settleInUSD);
            	 specificSettleMap.put("APRemmittanceproductPaymentTotalPHP", settleInPHP);
            	 specificSettleMap.put("CASAproductPaymentTotalCreditTHIRD", settleInUSD);
            	 specificSettleMap.put("CASAproductPaymentTotalCreditPHP", settleInPHP);
            	 
             }
            
             genAccountingEntryPayment_settlement(tradeService, specificSettleMap, productRef, exportCurrency, bcexportCurrency, exportCurrency, bcSettlementCurrencyCharges, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
            
             
             
             System.out.println("accountingEventTypeIdString: "+accountingEventTypeIdString);
             System.out.println("--------Charges Map Start-----------");
             
	            for(Map.Entry<String, List<Object>> chargesDetails : chargesSummaryMap.entrySet()) {	
	            	
	            	System.out.println("Key = " + chargesDetails.getKey() + ", Value = " + chargesDetails.getValue());	            	
	            }
	         System.out.println("--------Charges Map end-----------");
            
             System.out.println("--------Export Map Start-----------");
	            for(Map.Entry<String, Object> exportMaps : chargeMap.entrySet()) {  	
	            	System.out.println("Key = " + exportMaps.getKey() + ", Value = " + exportMaps.getValue());     	
	            }
	         System.out.println("--------Export Map end-----------");
             
	         //System.out.println("payment product: "+paymentProduct.toString());
	         System.out.println("payment service: "+paymentService.toString());
	         Set<PaymentDetail> paymentServiceMap = paymentService.getDetails();
	         for (PaymentDetail paymentDetail : paymentServiceMap) {
                 System.out.println("--------paymentServiceMap-----------");
                 printPaymentDetails(paymentDetail);
                 System.out.println("---------------------------");
	         }
	         
	         System.out.println("payment settlement: "+paymentSettlement.toString());
	         Set<PaymentDetail> paymentSettlementMap = paymentSettlement.getDetails();
	         for (PaymentDetail paymentDetail : paymentSettlementMap) {
                 System.out.println("--------paymentSettlementMap-----------");
                 printPaymentDetails(paymentDetail);
                 System.out.println("---------------------------");
	         }
	         
	         String chargeId = "";
	         BigDecimal chargeAmount = BigDecimal.TEN;
	         Map <String, Object> exportChargeMap = new HashMap <String, Object>();
		  	  for (ServiceCharge charge : tradeService.getServiceCharge()){
		  	            chargeId = charge.getChargeId().toString();
		  	            chargeAmount = charge.getAmount();
		  	            System.out.println("Charges to be refund: " +chargeId +" amounted to "+ chargeAmount);
		  	            
		  	            exportChargeMap.put(chargeId,chargeAmount);
		  	  }
		  	  
		  	//setup chargesMap
		  	Map <String, Object> specificPaymentMap = generateExportPaymentChargesMap(exportChargeMap, settlementCurrencyCharges);
	         
		  	if (paymentService != null) {
	        	 	generateChargesAndChargesPaymentAccountingEntriesExport(tradeService, paymentService, productRef, exportCurrency, bcexportCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct, paymentSettlement);
		        }
            
	                

    	 } catch (Exception e) {
             e.printStackTrace();
         }
    }

    /**
     * Method used to generate Accounting Entries for CORRES_CHARGE SETTLEMENT
     *
     * @param tradeService       TradeService to generate accounting entries for
     * @param details            the details object of the TradeService
     * @param paymentProduct     Payment object for Product Charge Payment
     * @param paymentService     Payment object for Service Charge Payment
     * @param productRef         ProductId of TradeService
     * @param gltsNumber         the gltsNumber generated from sequence sequence generator
     * @param tradeServiceStatus the status of the TradeService
     */
    @Transactional
        private void genAccountingEntries_CORRES_CHARGE_SETTLEMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_CORRES_CHARGE_SETTLEMENT");
        //TODO Figure out how to differentiate between different types of corres charge settlement
    
        if (!tradeService.getDetails().containsKey("currency")) {
            System.out.println("Missing Billing Currency Field!!!");
        }
        if (!tradeService.getDetails().containsKey("amount")) {
            System.out.println("Missing Billing Amount Field!!!");
        }
        System.out.println("details:"+tradeService.getDetails());

        String billingCurrency = tradeService.getDetails().get("currency").toString();
        BigDecimal billingAmount = new BigDecimal(tradeService.getDetails().get("amount").toString().replace(",",""));
        BigDecimal urr = new BigDecimal(tradeService.getDetails().get("USD-PHP_urr").toString());
        BigDecimal usd_php_rate = new BigDecimal(tradeService.getDetails().get("USD-PHP_urr").toString());
        BookCurrency bcBillingCurrency = determineBookCurrency(billingCurrency);
        System.out.println("billingCurrency:" + billingCurrency);
        System.out.println("billingAmount:" + billingAmount);
        BigDecimal billingAmountPHP = BigDecimal.ZERO;
        BigDecimal billingAmountUSD = BigDecimal.ZERO;
        BigDecimal billingAmountTHIRD = BigDecimal.ZERO;
        if(billingCurrency.equalsIgnoreCase("PHP")){
            billingAmountPHP = billingAmount;
        } else if(billingCurrency.equalsIgnoreCase("USD")){
            billingAmountUSD = billingAmount;
            billingAmountPHP = billingAmount.multiply(usd_php_rate);
        } else {
            String third_usd_rate_str = billingCurrency+"-USD_special_rate_cash";
            BigDecimal third_usd_rate = new BigDecimal(tradeService.getDetails().get(third_usd_rate_str).toString());
            System.out.println("third_usd_rate"+third_usd_rate);
            System.out.println("usd_php_rate"+usd_php_rate);
            billingAmountTHIRD = billingAmount;
            billingAmountUSD = billingAmount.multiply(third_usd_rate);
            billingAmountPHP = billingAmountUSD.multiply(usd_php_rate);
        }
        System.out.println("billingAmountPHP"+billingAmountPHP);
        System.out.println("billingAmountUSD"+billingAmountUSD);
        System.out.println("billingAmount3RD"+billingAmountTHIRD);
        //Billing Amount and Currency will be used in the Settlement related entries.
        

       String accountType = "RBU";
        if (tradeService.getDetails().containsKey("accountType")) {
            accountType = tradeService.getDetails().get("accountType").toString();
        } else {//no accountType this means we have to base this on the lc currency
            if (!billingCurrency.equalsIgnoreCase("PHP")) {
                accountType = "FCDU";
            }
        }

        if(tradeService.getDetails().get("withoutReference")!=null && "true".equalsIgnoreCase(tradeService.getDetails().get("withoutReference").toString())){
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            String noRefSettlementCurrency = tradeService.getDetails().get("settlementCurrency").toString();
            BookCurrency bcNoRefSettlementCurrency = determineBookCurrency(noRefSettlementCurrency);


            String accEvntidNoRef="SETTLEMENT-ACTUAL-CORRES-BANK";
            if(accountType.equalsIgnoreCase("RBU")){
                accEvntidNoRef=accEvntidNoRef+"-RG-BOOK";
            } else {
                accEvntidNoRef=accEvntidNoRef+"-FC-BOOK";
            }

            //DONE :: Amount to be remitted to Other Bank
            //totalBillingAmountInPhp
            Map<String, Object> remitMap = new HashMap<String, Object>();
            //changing billing to noRefSettlementCurrency
            if(billingCurrency.equalsIgnoreCase("PHP")){
                //PHP
                remitMap.put("RebateproductPaymentTotalPHP",billingAmountPHP);
                remitMap.put("DUEFromFBsettlementTotalPHP",billingAmountPHP);
                System.out.println("billingAmountPHP:"+billingAmountPHP);
            } else if(billingCurrency.equalsIgnoreCase("USD")){
                //USD
                remitMap.put("DUEFromFBsettlementTotalUSD", billingAmountUSD);
                remitMap.put("RebateproductPaymentTotalUSD", billingAmountUSD);
                remitMap.put("productPaymentTotalUSD", billingAmountUSD);
                remitMap.put("DUEFromFBsettlementTotalPHP", billingAmountPHP);
                remitMap.put("RebateproductPaymentTotalPHP", billingAmountPHP);
                remitMap.put("productPaymentTotalPHP", billingAmountPHP);
                System.out.println("billingAmountUSD:" + billingAmountUSD);
                System.out.println("billingAmountPHP:" + billingAmountPHP);
            } else {
                //THIRD
                remitMap.put("DUEFromFBsettlementTotalTHIRD",billingAmountTHIRD);
                remitMap.put("RebateproductPaymentTotalTHIRD",billingAmountTHIRD);
                remitMap.put("productPaymentTotalTHIRD",billingAmountTHIRD);
                remitMap.put("DUEFromFBsettlementTotalUSD",billingAmountUSD);
                remitMap.put("RebateproductPaymentTotalUSD",billingAmountUSD);
                remitMap.put("productPaymentTotalUSD",billingAmountUSD);
                remitMap.put("DUEFromFBsettlementTotalPHP",billingAmountPHP);
                remitMap.put("RebateproductPaymentTotalPHP",billingAmountPHP);
                remitMap.put("productPaymentTotalPHP",billingAmountPHP);

                System.out.println("billingAmountTHIRD:"+billingAmountTHIRD);
                System.out.println("billingAmountUSD:"+billingAmountUSD);
                System.out.println("billingAmountPHP:"+billingAmountPHP);
            }

            accEvntidNoRef="SETTLEMENT-ACTUAL-CORRES-BANK";
            if(accountType.equalsIgnoreCase("RBU")){
                accEvntidNoRef=accEvntidNoRef+"-RG-BOOK";
            } else {
                accEvntidNoRef=accEvntidNoRef+"-FC-BOOK";
            }
            genAccountingEntryPayment_charges(tradeService, remitMap, productRef, billingCurrency, bcBillingCurrency,  noRefSettlementCurrency, bcNoRefSettlementCurrency, new AccountingEventTransactionId(accEvntidNoRef), gltsNumber, tradeServiceStatus);
            genAccountingEntrySettlement_settlement(tradeService, remitMap, productRef, billingCurrency, bcBillingCurrency,  noRefSettlementCurrency, bcNoRefSettlementCurrency, new AccountingEventTransactionId(accEvntidNoRef), gltsNumber, tradeServiceStatus);




        } else {
        	
            BigDecimal totalBillingAmountInPhp = new BigDecimal(tradeService.getDetails().get("totalBillingAmountInPhp").toString().replace(",", ""));
            System.out.println("totalBillingAmountInPhp:"+totalBillingAmountInPhp);

            List<CorresChargeAdvance> corresChargeAdvanceList = corresChargeAdvanceRepository.getAllByDocumentNumber(tradeService.getDocumentNumber());

            BigDecimal advisingFee = BigDecimal.ZERO;
            BigDecimal confirmingFee = BigDecimal.ZERO;
            for (CorresChargeAdvance corresChargeAdvance : corresChargeAdvanceList) {
                if (corresChargeAdvance.getCorresChargeType().equals(CorresChargeType.ADVISING)) {
                    advisingFee = advisingFee.add(corresChargeAdvance.getAmount());
                } else if (corresChargeAdvance.getCorresChargeType().equals(CorresChargeType.CONFIRMING)) {
                    confirmingFee = confirmingFee.add(corresChargeAdvance.getAmount());
                }
            }
            System.out.println("advisingFee:"+advisingFee);
            System.out.println("confirmingFee:"+confirmingFee);

            BigDecimal totalAdvanceCorres = advisingFee.add(confirmingFee);
            System.out.println("totalAdvanceCorres:" + totalAdvanceCorres);

            BigDecimal actualCorres = BigDecimal.ZERO;
            List<CorresChargeActual> corresChargeActualList = corresChargeActualRepository.getAllByDocumentNumber(tradeService.getDocumentNumber());
            for (CorresChargeActual corresChargeActual : corresChargeActualList) {
                actualCorres = actualCorres.add(corresChargeActual.getAmount());
                System.out.println("corresChargeActual.getCurrency():" + corresChargeActual.getCurrency());
            }

            BigDecimal corresToBeSettled=BigDecimal.ZERO;
            BigDecimal corresExcess=BigDecimal.ZERO;
            if(totalBillingAmountInPhp.compareTo(totalAdvanceCorres)==1){
                corresToBeSettled = totalBillingAmountInPhp.subtract(totalAdvanceCorres);
            } else {
                corresExcess = totalAdvanceCorres.subtract(totalBillingAmountInPhp);
            }
            System.out.println("corresToBeSettled:" + corresToBeSettled);
            System.out.println("corresExcess:" + corresExcess);

            System.out.println("totalAdvanceCorres.compareTo(BigDecimal.ZERO):" + totalAdvanceCorres.compareTo(BigDecimal.ZERO));
            if (totalAdvanceCorres.compareTo(BigDecimal.ZERO) <= 0) {
                //no advance corres
                System.out.println("no advance corres");
            }

            System.out.println("totalAdvanceCorres.compareTo(BigDecimal.ZERO):" + totalAdvanceCorres.compareTo(BigDecimal.ZERO));
            if (totalAdvanceCorres.compareTo(BigDecimal.ZERO) == 1) {
                //There is some corres to be covered by advance, hence a reversal will be necessary
                System.out.println("There is some corres to be covered by advance, hence a reversal will be necessary");
            }

            System.out.println("corresToBeCharged.compareTo(BigDecimal.ZERO):" + corresToBeSettled.compareTo(BigDecimal.ZERO));
            if (corresToBeSettled.compareTo(BigDecimal.ZERO) <= 0) {
                //this means all corres will be charged against advanced
                System.out.println("this means all corres will be charged against advanced");
            }

            System.out.println("corresToBeSettled:" + corresToBeSettled);

//        String accountingEventTransactionIdClient = "SETTLEMENT-ACTUAL-CORRES-ALL-CUSTOMER";
//        String accountingEventTransactionIdReversal = "SETTLEMENT-ACTUAL-CORRES-PRE-COLLECTION-REVERSAL";
//        String accountingEventTransactionIdBank = "SETTLEMENT-ACTUAL-CORRES-BANK";

            //Necessary to be asked from SA
            //Where to get rates?

            String withoutReference = "false";
            System.out.println("withoutReference:" + withoutReference);
            if (tradeService.getDetails().containsKey("withoutReference")) {
                withoutReference = tradeService.getDetails().get("withoutReference").toString();
            }
            System.out.println("withoutReference:" + withoutReference);

            if (withoutReference.equalsIgnoreCase("true") && tradeService.getServiceInstructionId() == null) {
                System.out.println("TSD Initiated SETTLEMENT-ACTUAL-CORRES-BANK");
                //TSD Initiated SETTLEMENT-ACTUAL-CORRES-BANK

                //Get amount by bank accounting reversal from AP
                //totalAdvanceCorres
                //SETTLEMENT-ACTUAL-CORRES-PRE-COLLECTION-REVERSAL
                String apCurrency = "PHP";
                BookCurrency bcApCurrency = determineBookCurrency(apCurrency);
                Map<String, Object> apReversalMap = new HashMap<String, Object>();
//            apReversalMap.put("APCorresAdvisingFeeproductPaymentTotalPHP",advisingFee);
//            apReversalMap.put("APCorresConfirmingFeeproductPaymentTotalPHP",confirmingFee);
                System.out.println("corresToBeSettled:"+corresToBeSettled);
                apReversalMap.put("productPaymentTotalPHP",corresToBeSettled);
                BigDecimal totalAdvanceCorresUSD = corresToBeSettled.divide(usd_php_rate,2,BigDecimal.ROUND_UP);
                System.out.println("totalAdvanceCorresUSD:"+totalAdvanceCorresUSD);
                if(billingCurrency.equalsIgnoreCase("USD")){
                    //USD
                    apReversalMap.put("productPaymentTotalUSD", totalAdvanceCorresUSD);
                } else {
                    String third_usd_rate_str = billingCurrency+"-USD_special_rate_cash";
                    BigDecimal third_usd_rate = new BigDecimal(tradeService.getDetails().get(third_usd_rate_str).toString());
                    System.out.println("third_usd_rate:"+third_usd_rate);

                    BigDecimal totalAdvanceCorresTHIRD = totalAdvanceCorresUSD.divide(third_usd_rate,2,BigDecimal.ROUND_UP);
                    //THIRD
                    apReversalMap.put("productPaymentTotalUSD",totalAdvanceCorresUSD);
                    apReversalMap.put("productPaymentTotalTHIRD",totalAdvanceCorresTHIRD);

                    System.out.println("productPaymentTotalTHIRD:"+totalAdvanceCorresTHIRD);
                }

                String accEvntid="SETTLEMENT-ACTUAL-CORRES-BANK";
                if(accountType.equalsIgnoreCase("RBU")){
                    accEvntid=accEvntid+"-RG-BOOK";
                } else {
                    accEvntid=accEvntid+"-FC-BOOK";
                }

                genAccountingEntryPayment_charges(tradeService, apReversalMap, productRef, billingCurrency, bcBillingCurrency, apCurrency, bcApCurrency, new AccountingEventTransactionId(accEvntid), gltsNumber, tradeServiceStatus);

                //DONE :: Amount to be remitted to Other Bank
                //totalBillingAmountInPhp
                Map<String, Object> remitMap = new HashMap<String, Object>();
                
                if(billingCurrency.equalsIgnoreCase("PHP")){
                    //PHP
                    remitMap.put("DUEFromFBsettlementTotalPHP",billingAmountPHP);
                    System.out.println("billingAmountPHP:"+billingAmountPHP);
                } else if(billingCurrency.equalsIgnoreCase("USD")){
                    //USD
                    remitMap.put("DUEFromFBsettlementTotalUSD", billingAmountUSD);
                    remitMap.put("DUEFromFBsettlementTotalPHP", billingAmountPHP);
                    System.out.println("billingAmountUSD:" + billingAmountUSD);
                    System.out.println("billingAmountPHP:" + billingAmountPHP);
                } else {
                    //THIRD
                    remitMap.put("DUEFromFBsettlementTotalTHIRD",billingAmountTHIRD);
                    remitMap.put("DUEFromFBsettlementTotalUSD",billingAmountUSD);
                    remitMap.put("DUEFromFBsettlementTotalPHP",billingAmountPHP);

                    System.out.println("billingAmountTHIRD:"+billingAmountTHIRD);
                    System.out.println("billingAmountUSD:"+billingAmountUSD);
                    System.out.println("billingAmountPHP:"+billingAmountPHP);
                }

                accEvntid="SETTLEMENT-ACTUAL-CORRES-BANK";
                if(accountType.equalsIgnoreCase("RBU")){
                    accEvntid=accEvntid+"-RG-BOOK";
                } else {
                    accEvntid=accEvntid+"-FC-BOOK";
                }
                genAccountingEntrySettlement_settlement(tradeService, remitMap, productRef, billingCurrency, bcBillingCurrency, billingCurrency, bcBillingCurrency, new AccountingEventTransactionId(accEvntid), gltsNumber, tradeServiceStatus);


            } else {
                Boolean isPaymentEnough = Boolean.FALSE;
                BigDecimal totalThroughPayment = BigDecimal.ZERO;

                if(corresToBeSettled.compareTo(BigDecimal.ZERO)==1){
                    System.out.println("corresToBeSettled will be covered by a Product Payment with some amount covered by REVERSAL");

                    //Get amount by payment
                    //corresToBeSettled will be covered by a Product Payment
                    //SETTLEMENT-ACTUAL-CORRES-ALL-CUSTOMER
                    Map<String, Object> paymentMap = new HashMap<String, Object>();
                    String settlementCurrency = tradeService.getDetails().get("settlementCurrency").toString();
                    BookCurrency bcSettlementCurrency = determineBookCurrency(settlementCurrency);
                    //Settlement of Nego Amount
                    if (paymentProduct != null) {
                        //Booking is done from payment currency to lc currency
                        System.out.println("Payment of Product Charges Start ACTUAL CORRES");
                        Set<PaymentDetail> temp = paymentProduct.getDetails();
                        
                        for (PaymentDetail paymentDetail : temp) {
                            System.out.println("---------------------------");
                            printPaymentDetails(paymentDetail);

                            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                            placePaymentsInPaymentMapCorres(tradeService, paymentDetail, specificPaymentMap);
                            placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, billingCurrency);
                            totalThroughPayment = totalThroughPayment.add((BigDecimal)specificPaymentMap.get("productPaymentTotalPHP"));
                            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                            System.out.println("specificPaymentMap:" + specificPaymentMap);
                            System.out.println("---------------------------");

                            String accEvntid="SETTLEMENT-ACTUAL-CORRES-ALL-CUSTOMER";

                            // As per users: all payment in usd and third is booked in FC
                            if(billingCurrency.equalsIgnoreCase("PHP")){
                            	 accEvntid=accEvntid+"-RG-BOOK";
                            } else {
                                //USD and THIRD
                            	accEvntid=accEvntid+"-FC-BOOK";	                       			             	
                            }
                            	genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, billingCurrency, bcBillingCurrency, settlementCurrency, bcSettlementCurrency, new AccountingEventTransactionId(accEvntid), gltsNumber, tradeServiceStatus);                
                            	
                            	//TODO: to be re-program if Actual Corres charge is fully develop
                        		if(accountType.equalsIgnoreCase("RBU") && !billingCurrency.equalsIgnoreCase("PHP")){
                        			genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, billingCurrency, bcBillingCurrency, settlementCurrency, bcSettlementCurrency, new AccountingEventTransactionId("SETTLEMENT-ACTUAL-CORRES-CROSS-FC-BOOK"), gltsNumber, tradeServiceStatus);	 
                        		}  
                        
                        }
                    }

                    if(totalThroughPayment.compareTo(billingAmountPHP)==1){
                        isPaymentEnough = Boolean.TRUE;
                    }

                    String accEvntid="SETTLEMENT-ACTUAL-CORRES-PRE-COLLECTION-REVERSAL";
                    if(!isPaymentEnough){
                        //Get amount by bank accounting reversal from AP
                        //totalAdvanceCorres
                        //SETTLEMENT-ACTUAL-CORRES-PRE-COLLECTION-REVERSAL
                        String apCurrency = "PHP";
                        BookCurrency bcApCurrency = determineBookCurrency(apCurrency);
                        Map<String, Object> apReversalMap = new HashMap<String, Object>();
                        apReversalMap.put("APCorresAdvisingFeeproductPaymentTotalPHP",advisingFee);
                        apReversalMap.put("APCorresConfirmingFeeproductPaymentTotalPHP",confirmingFee);
                        apReversalMap.put("productPaymentTotalPHP",totalAdvanceCorres);
                        BigDecimal totalAdvanceCorresUSD = totalAdvanceCorres.divide(usd_php_rate,2,BigDecimal.ROUND_UP);
                        System.out.println("totalAdvanceCorresUSD:"+totalAdvanceCorresUSD);
                        if(billingCurrency.equalsIgnoreCase("USD")){
                            //USD
                            apReversalMap.put("productPaymentTotalUSD", totalAdvanceCorresUSD);
                        } else {
                            String third_usd_rate_str = billingCurrency+"-USD_special_rate_cash";
                            BigDecimal third_usd_rate = new BigDecimal(tradeService.getDetails().get(third_usd_rate_str).toString());
                            System.out.println("third_usd_rate:"+third_usd_rate);

                            BigDecimal totalAdvanceCorresTHIRD = totalAdvanceCorresUSD.divide(third_usd_rate,2,BigDecimal.ROUND_UP);
                            //THIRD
                            apReversalMap.put("productPaymentTotalUSD",totalAdvanceCorresUSD);
                            apReversalMap.put("productPaymentTotalTHIRD",totalAdvanceCorresTHIRD);

                            System.out.println("productPaymentTotalTHIRD:"+totalAdvanceCorresTHIRD);
                        }

                       
                        if(accountType.equalsIgnoreCase("RBU")){
                            accEvntid=accEvntid+"-RG-BOOK";
                        } else {
                            accEvntid=accEvntid+"-FC-BOOK";
                        }
                           
                        genAccountingEntryPayment_charges(tradeService, apReversalMap, productRef, billingCurrency, bcBillingCurrency, apCurrency, bcApCurrency, new AccountingEventTransactionId(accEvntid), gltsNumber, tradeServiceStatus);
                    }           
    
        
                    //DONE :: Amount to be remitted to Other Bank
                    //totalBillingAmountInPhp
                    Map<String, Object> remitMap = new HashMap<String, Object>();
                    if(billingCurrency.equalsIgnoreCase("PHP")){
                        //PHP
                        remitMap.put("DUEFromFBsettlementTotalPHP",billingAmountPHP);
                        System.out.println("billingAmountPHP:"+billingAmountPHP);
                    } else if(billingCurrency.equalsIgnoreCase("USD")){
                        //USD
                    	remitMap.put("DUEFromFBsettlementTotalUSD", billingAmountUSD);
                        remitMap.put("DUEFromFBsettlementTotalPHP", billingAmountPHP);
                        System.out.println("billingAmountUSD:" + billingAmountUSD);
                        System.out.println("billingAmountPHP:" + billingAmountPHP);
                        
                    } else {
                        //THIRD
                    	remitMap.put("DUEFromFBsettlementTotalTHIRD",billingAmountTHIRD);
                        remitMap.put("DUEFromFBsettlementTotalUSD",billingAmountUSD);
                        remitMap.put("DUEFromFBsettlementTotalPHP",billingAmountPHP);

                        System.out.println("billingAmountTHIRD:"+billingAmountTHIRD);
                        System.out.println("billingAmountUSD:"+billingAmountUSD);
                        System.out.println("billingAmountPHP:"+billingAmountPHP);
                    }
                    
                     //for the bank booking
                    accEvntid="SETTLEMENT-ACTUAL-CORRES-ALL-CUSTOMER";
            
                    if(accountType.equalsIgnoreCase("RBU")){                                     
                        accEvntid=accEvntid+"-RG-BOOK";
                    } else {
                        accEvntid=accEvntid+"-FC-BOOK";
                    }
                    
                    genAccountingEntrySettlement_settlement(tradeService, remitMap, productRef, billingCurrency, bcBillingCurrency, billingCurrency, bcBillingCurrency, new AccountingEventTransactionId(accEvntid), gltsNumber, tradeServiceStatus);
 
                } else {
                    System.out.println("Get all Actual Corres from Corres Pre Collection!!!!");
                    //Get all Actual Corres from Corres Pre Collection
                    
                    String apCurrency = "PHP";
                    BookCurrency bcApCurrency = determineBookCurrency(apCurrency);
                    Map<String, Object> apReversalMap = new HashMap<String, Object>();

                    if(billingAmountPHP.compareTo(advisingFee.add(confirmingFee))==-1){
                        if(billingAmountPHP.compareTo(advisingFee)==1 && billingAmountPHP.compareTo(confirmingFee)==1 ){
                            apReversalMap.put("APCorresAdvisingFeeproductPaymentTotalPHP",advisingFee);
                            apReversalMap.put("APCorresConfirmingFeeproductPaymentTotalPHP",totalBillingAmountInPhp.subtract(advisingFee));
                        } else  if(billingAmountPHP.compareTo(advisingFee)==-1 ){
                            apReversalMap.put("APCorresAdvisingFeeproductPaymentTotalPHP",totalBillingAmountInPhp);
                        } else  if(billingAmountPHP.compareTo(confirmingFee)==-1 ){
                            apReversalMap.put("APCorresConfirmingFeeproductPaymentTotalPHP",totalBillingAmountInPhp);
                        }
//                        apReversalMap.put("APCorresAdvisingFeeproductPaymentTotalPHP",advisingFee);
//                        apReversalMap.put("APCorresConfirmingFeeproductPaymentTotalPHP",confirmingFee);
                    } else if(billingAmountPHP.compareTo(advisingFee.add(confirmingFee))==0){
                        apReversalMap.put("APCorresAdvisingFeeproductPaymentTotalPHP",advisingFee);
                        apReversalMap.put("APCorresConfirmingFeeproductPaymentTotalPHP",confirmingFee);

                    }

                    apReversalMap.put("productPaymentTotalPHP",billingAmountPHP);
                    apReversalMap.put("productPaymentTotalUSD",billingAmountUSD);


                    String accevtranid = "SETTLEMENT-ACTUAL-CORRES-PRE-COLLECTION-REVERSAL-RG-BOOK";
                    
                    genAccountingEntryPayment_charges(tradeService, apReversalMap, productRef, billingCurrency, bcBillingCurrency, apCurrency, bcApCurrency, new AccountingEventTransactionId(accevtranid), gltsNumber, tradeServiceStatus);


                    //DONE :: Amount to be remitted to Other Bank
                    //totalBillingAmountInPhp
                    Map<String, Object> remitMap = new HashMap<String, Object>();

                    if(billingCurrency.equalsIgnoreCase("PHP")){
                        //PHP
                        remitMap.put("DUEFromFBsettlementTotalPHP",billingAmountPHP);
                        System.out.println("billingAmountPHP:"+billingAmountPHP);
                    } else if(billingCurrency.equalsIgnoreCase("USD")){
                        //USD
                        remitMap.put("DUEFromFBsettlementTotalUSD", billingAmountUSD);
                        remitMap.put("DUEFromFBsettlementTotalPHP", billingAmountPHP);
                        System.out.println("billingAmountUSD:" + billingAmountUSD);
                        System.out.println("billingAmountPHP:" + billingAmountPHP);
                    } else {
                        //THIRD
                        remitMap.put("DUEFromFBsettlementTotalTHIRD",billingAmountTHIRD);
                        remitMap.put("DUEFromFBsettlementTotalUSD",billingAmountUSD);
                        remitMap.put("DUEFromFBsettlementTotalPHP",billingAmountPHP);

                        System.out.println("billingAmountTHIRD:"+billingAmountTHIRD);
                        System.out.println("billingAmountUSD:"+billingAmountUSD);
                        System.out.println("billingAmountPHP:"+billingAmountPHP);
                    }

                    String accEvntid="SETTLEMENT-ACTUAL-CORRES-ALL-CUSTOMER";
                    if(accountType.equalsIgnoreCase("RBU")){
                        accEvntid=accEvntid+"-RG-BOOK";
                    } else {
                        accEvntid=accEvntid+"-FC-BOOK";
                    }



                    genAccountingEntrySettlement_settlement(tradeService, remitMap, productRef, billingCurrency, bcBillingCurrency, billingCurrency, bcBillingCurrency, new AccountingEventTransactionId(accEvntid), gltsNumber, tradeServiceStatus);
                }

            }
        }

    }

    //TODO:Implement THIS
    /**
     * Method used to generate Accounting Entries for CDT Refund
     *
     * @param tradeService       TradeService to generate accounting entries for
     * @param details            the details object of the TradeService
     * @param paymentProduct     Payment object for Product Charge Payment
     * @param paymentService     Payment object for Service Charge Payment
     * @param productRef         ProductId of TradeService
     * @param gltsNumber         the gltsNumber generated from sequence sequence generator
     * @param tradeServiceStatus the status of the TradeService
     */
    @Transactional
    private void genAccountingEntries_CDT_REFUND(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    	System.out.println("genAccountingEntries_CDT_REFUND");
    	
    	String refundCurrency = "PHP";
    	BookCurrency bcCurrency = determineBookCurrency(refundCurrency);
    	BigDecimal refundedAmount = getBigDecimalOrZero(details.get("totalAmountOfPayment"));
    	BigDecimal cableOrSuppliesFee = new BigDecimal("20");
    	BigDecimal totalRefundedAmount = getBigDecimalOrZero(details.get("amount"));
    	String modeOfRefund = tradeService.getDetails().get("modeOfRefund").toString();
    	String processingUnitCode = tradeService.getProcessingUnitCode();
    	
    	String iedieirdNumber = tradeService.getTradeServiceReferenceNumber().toString();
    	
    	//must determined if there was an existing bankcom charged during collection
        CDTPaymentRequest cdtPaymentRequest =  cdtPaymentRequestRepository.load(iedieirdNumber);
    	BigDecimal chargesBankCom = cdtPaymentRequest.getBankChargeViaIedieirdNumber(iedieirdNumber);
    	
    	if (chargesBankCom != null || chargesBankCom == BigDecimal.ZERO) {		
    			chargesBankCom = chargesBankCom.subtract(cableOrSuppliesFee);   		
    	}else {   		
    		chargesBankCom = BigDecimal.ZERO;
    		//verify if SSU-supply is refundable
    	}

    	totalRefundedAmount = refundedAmount.add(chargesBankCom);
    	
    	System.out.println("Mode of Refund: ==>>> "+ modeOfRefund);
    	System.out.println("Amount to Refund: ==>>> "+ refundedAmount);
    	System.out.println("BankCom to refund: ==>>> "+ chargesBankCom);
    	System.out.println("Amount with BankCom: ==>>> "+ totalRefundedAmount);
    	
    	 Map<String, Object> specificRefundMap = new HashMap<String, Object>();   	     	
    	 
    	 if(modeOfRefund.equalsIgnoreCase("CASA")) {
    		 specificRefundMap.put("CASArefundAmountTotalPHP", refundedAmount); 	
    	 }else if (modeOfRefund.equalsIgnoreCase("MC_ISSUANCE")) {	 
    		 specificRefundMap.put("CheckrefundAmountTotalPHP", refundedAmount); 		 
    	 }else {//IBT-Branch	 
    		 specificRefundMap.put("IBTrefundAmountTotalPHP", refundedAmount);
    	 } 
		 if(processingUnitCode.equalsIgnoreCase("909")){
	    	 specificRefundMap.put("CASAMOBBOCrefundAmountPHP", refundedAmount);
			 genAccountingEntryRefund_charges(tradeService,  specificRefundMap, productRef, refundCurrency, bcCurrency,refundCurrency,bcCurrency, new AccountingEventTransactionId("CDT-COLLECTION-PROCESSING-OF-REFUND-TSD"), gltsNumber, tradeServiceStatus);
		 } else {
			 specificRefundMap.put("IBTrefundAmountTotalPHP", refundedAmount);
			 genAccountingEntryRefund_charges(tradeService,  specificRefundMap, productRef, refundCurrency, bcCurrency,refundCurrency,bcCurrency, new AccountingEventTransactionId("CDT-COLLECTION-PROCESSING-OF-REFUND-FD-OR-AABR"), gltsNumber, tradeServiceStatus);
		 }
    }

    /**
     * Method used to generate Accounting Entries for CDT REMITTANCE
     *
     * @param tradeService       TradeService to generate accounting entries for
     * @param details            the details object of the TradeService
     * @param paymentProduct     Payment object for Product Charge Payment
     * @param paymentService     Payment object for Service Charge Payment
     * @param productRef         ProductId of TradeService
     * @param gltsNumber         the gltsNumber generated from sequence sequence generator
     * @param tradeServiceStatus the status of the TradeService
     */
    @Transactional
    private void genAccountingEntries_CDT_REMITTANCE(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_CDT_REMITTANCE");
        
        String remittanceCurrency = "PHP";
        BigDecimal remittanceAmount = getBigDecimalOrZero(details.get("remittanceAmount"));
        
        BookCurrency bcCurrency = determineBookCurrency(remittanceCurrency);
        Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
        
        specificPaymentMap.put("remittanceAmountPHP", remittanceAmount);
        
        genAccountingEntryPayment_settlement(tradeService, specificPaymentMap, productRef, remittanceCurrency, bcCurrency, remittanceCurrency, bcCurrency, new AccountingEventTransactionId("REMITTANCE-OF-CDT-COLLECTIONS-TO-BSP"), gltsNumber, tradeServiceStatus);
    }

    /**
     * Method used to generate accounting entries for CDT COLLECTION
     *
     * @param tradeService       TradeService to generate accounting entries for
     * @param details            the details object of the TradeService
     * @param paymentProduct     Payment object for Product Charge Payment
     * @param paymentService     Payment object for Service Charge Payment
     * @param productRef         ProductId of TradeService
     * @param gltsNumber         the gltsNumber generated from sequence sequence generator
     * @param tradeServiceStatus the status of the TradeService
     */
    @Transactional
    private void genAccountingEntries_CDT_COLLECTION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_CDT_COLLECTION");
        try {
            String nameOfAmount = "loanAmount";

            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            } else {//because this is CDT assume PHP payment
                lcCurrency = "PHP";
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getServiceChargeCurrency()!=null){
                settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
            }
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + settlementCurrencyCharges);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, nameOfAmount);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }


            if (paymentProduct != null) {
                System.out.println();
                //TODO: Fix for Actual
                String accountingEventTypeIdString; //Booking is done from payment currency to lc currency
                System.out.println("Payment of Product Charges Start");
                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);
                    Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                    placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                    placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                    placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency); //orig profit or loss based on difference between urr and buy or sell rate

                    System.out.println("specificPaymentMap:" + specificPaymentMap);
                    System.out.println("---------------------------");

                    String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                    accountingEventTypeIdString = getAccountingEventIdStringPaymentOrLoan_DM(lcCurrency, paymentDetail.getPaymentInstrumentType().toString());
                    System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);

                    String accountType = "RBU";
                    if (tradeService.getDetails().containsKey("accountType")) {
                        accountType = tradeService.getDetails().get("accountType").toString();
                    } else {//no accountType this means we have to base this on the lc currency
                        if (!lcCurrency.equalsIgnoreCase("PHP")) {
                            accountType = "FCDU";
                        }

                    }
                    if (accountingEventTypeIdString.equalsIgnoreCase("SETTLEMENT-NEGO-AMOUNT-VIA-TR-LOAN")) {
                        if (accountType.equalsIgnoreCase("FCDU")) {
                            accountingEventTypeIdString += "-FC-BOOK";
                        } else {
                            accountingEventTypeIdString += "-RG-BOOK";
                        }
                    }

                    genAccountingEntryPayment_settlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                }
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Method used to generate accounting entries for CDT COLLECTION
     *
     * @param tradeService       TradeService to generate accounting entries for
     * @param details            the details object of the TradeService
     * @param paymentProduct     Payment object for Product Charge Payment
     * @param paymentService     Payment object for Service Charge Payment
     * @param productRef         ProductId of TradeService
     * @param gltsNumber         the gltsNumber generated from sequence sequence generator
     * @param tradeServiceStatus the status of the TradeService
     */
    @Transactional
    private void genAccountingEntries_CDT_PAYMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_CDT_PAYMENT");
        try {

            System.out.println("details:"+tradeService.getDetails());

            BigDecimal finalDutyAmount = parseOrReturnZero((String)tradeService.getDetails().get("finalDutyAmount"));
//            String cdtBookCode = (String)tradeService.getDetails().get("cdtBookCode");
            String unitcode = (String)tradeService.getDetails().get("unitcode");
            
            System.out.println("Unitcode >>>>>>>>> "+unitcode);

//            String nameOfAmount = "loanAmount";

            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "PHP";

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "PHP";

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());


            System.out.println("tradeService.getDetails()>>>>>>>>>>>>>>>>>>>"+tradeService.getDetails());
            System.out.println("tradeService.getDetails().get(\"paymentRequestType\")" +tradeService.getDetails().get("paymentRequestType"));
            String paymentRequestType = (String) tradeService.getDetails().get("paymentRequestType");
            String iedieirdNumber = tradeService.getTradeServiceReferenceNumber().toString();

            CDTPaymentRequest cdtPaymentRequest =  cdtPaymentRequestRepository.load(iedieirdNumber);

            System.out.println("cdtPaymentRequest.getAmount()"+cdtPaymentRequest.getAmount());
            System.out.println("cdtPaymentRequest.getBankCharge()"+cdtPaymentRequest.getBankCharge());
            System.out.println("cdtPaymentRequest.getAgentBankCode()"+cdtPaymentRequest.getAgentBankCode());
            System.out.println("cdtPaymentRequest.getBranchUnitCode()"+cdtPaymentRequest.getBranchUnitCode());
            
            String branchUnitCode = cdtPaymentRequest.getBranchUnitCode();

//            RefPas5Client refPas5Client = refPas5ClientRepository.load(cdtPaymentRequest.getAgentBankCode());
//
//            System.out.println("refPas5Client.getCcbdBranchUnitCode() "+refPas5Client.getCcbdBranchUnitCode());
//            String branchUnitCode = refPas5Client.getCcbdBranchUnitCode();
            
            /*applicable only for TSD 
             * for branch all income should booked to there own unit code 
             * regardless of AO the client belongs
             * 
             */
            if((branchUnitCode == null || branchUnitCode.isEmpty()) && unitcode.equalsIgnoreCase("909") ){           	
            	System.out.println("Branch Unit Code is: "+ branchUnitCode + "income will go to 001");
                branchUnitCode = "001"; //Head Office Branch      
            }
            
            

            BigDecimal amount = BigDecimal.ZERO;
            if(cdtPaymentRequest.getAmount() !=null || cdtPaymentRequest.getAmount().compareTo(BigDecimal.ZERO)>0){
                amount = cdtPaymentRequest.getAmount();
            }

            BigDecimal cableFee = new BigDecimal("20");//Almost fixed amount

            if(!unitcode.equalsIgnoreCase("909")){
                cableFee = BigDecimal.ZERO;
            }


            Map<String, Object> specificChargeMap = new HashMap<String, Object>();
            BigDecimal bankCharge = BigDecimal.ZERO;
            if(cdtPaymentRequest.getBankCharge() != null && BigDecimal.ZERO.compareTo(cdtPaymentRequest.getBankCharge())<0){
                bankCharge = cdtPaymentRequest.getBankCharge();
                bankCharge = bankCharge.subtract(cableFee);
            }

            specificChargeMap.put("APBOCproductPaymentTotalPHP",amount);
            specificChargeMap.put("cableFeePHP",cableFee);
            if(!unitcode.equalsIgnoreCase("909")){
            	// for brnach processing; booked to branch unit code
            	specificChargeMap.put("bankCommissionPHPCDTBranch",bankCharge);
            }else{
            	// for TSD processing; booked to client's unit code
            	specificChargeMap.put("bankCommissionPHP",bankCharge);
            }
            

            System.out.println("specificChargeMap :" + specificChargeMap);
            String accEventTransactionId ="";

            if(cdtPaymentRequest.getBankCharge() ==null ||cdtPaymentRequest.getBankCharge().compareTo(BigDecimal.ZERO)<1){
                accEventTransactionId ="CDT-COLLECTION-PAYMENT-OF-FINAL-CDT-BC-IS-WAIVED";
            } else {
                accEventTransactionId ="CDT-COLLECTION-PAYMENT-OF-FINAL-CDT-BC-NOT-WAIVED";
            }

//            if("FINAL".equalsIgnoreCase(paymentRequestType)){
//                accEventTransactionId ="CDT-COLLECTION-PAYMENT-OF-FINAL-CDT-BC-NOT-WAIVED";
//            } else if("ADVANCE".equalsIgnoreCase(paymentRequestType)){
//                accEventTransactionId ="CDT-COLLECTION-PAYMENT-OF-ADVANCE-CDT";
//
//            } else if("EXPORT".equalsIgnoreCase(paymentRequestType)){
//                accEventTransactionId ="CDT-COLLECTION-PAYMENT-OF-FINAL-CDT-BC-NOT-WAIVED";
//
//            } else {
//
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                System.out.println("NOT ADVANCE|NOT FINAL|NOT EXPORT|THERE IS A PROBLEM");
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//
//                accEventTransactionId ="CDT-COLLECTION-PAYMENT-OF-FINAL-CDT-BC-NOT-WAIVED";
//            }

            //Booking is done from payment currency to lc currency
            System.out.println("Payment of Service Charges Start");

            String paymentSettlementCurrency ="";
            BookCurrency payBookCurrency = null;
            if(paymentProduct!=null){
                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);

                    String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
                    System.out.println("paymentName:" + paymentName);


                    Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                    placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                    placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                    System.out.println("specificPaymentMap:" + specificPaymentMap);
                    System.out.println("---------------------------");

                    paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

                    genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accEventTransactionId), gltsNumber, tradeServiceStatus);
                    System.out.println("---------------------------");

                }
                

            }

            genAccountingEntrySettlement(tradeService, specificChargeMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accEventTransactionId), gltsNumber, tradeServiceStatus);

            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_EXPORT_ADVISING_AMENDMENT_ADVISING(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_EXPORT_ADVISING_AMENDMENT_ADVISING");
        try {

            String nameOfAmount = "loanAmount";

            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getServiceChargeCurrency()!=null){
                settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
            }
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, nameOfAmount);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_EXPORT_ADVISING_CANCELLATION_ADVISING(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_EXPORT_ADVISING_CANCELLATION_ADVISING");
        try {

            String nameOfAmount = "loanAmount";

            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getServiceChargeCurrency()!=null){
                settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
            }
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, nameOfAmount);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_EXPORT_ADVISING_OPENING_ADVISING(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_EXPORT_ADVISING_OPENING_ADVISING");
        try {

            String nameOfAmount = "loanAmount";

            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getServiceChargeCurrency()!=null){
                settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
            }
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + settlementCurrencyCharges);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, nameOfAmount);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);
            System.out.println("chargeMap:" + chargeMap);

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**Accounting Entries for Setup AP,  excess payment manual payment of loans in SIBS
     * 
     * 
     * @param tradeService
     * @param details
     * @param paymentProduct
     * @param paymentService
     * @param productRef
     * @param gltsNumber
     * @param tradeServiceStatus
     */
    @Transactional
   private void genAccountingEntries_AP_SETUP(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    	System.out.println("genAccountingEntries_AP_SETUP");
    	

        String currency = details.get("currency").toString();
        BookCurrency bcLcCurrency = determineBookCurrency(currency);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());
        BigDecimal productAmount = getBigDecimalOrZero(details.get("amount"));
        BigDecimal urr = getBigDecimalOrZero(details.get("urr"));
        BigDecimal thirdToUSD = getBigDecimalOrZero(details.get(currency + "-USD"));
        
        BigDecimal setUpApInPHP;
        BigDecimal setUpApInUSD;
        BigDecimal setUpApInThird;
        
        Map<String, Object> specificSetUpMap = new HashMap<String, Object>();
        
        if(currency.equalsIgnoreCase("PHP")) {
        	
        	setUpApInPHP = productAmount;
        	specificSetUpMap.put("APRESOTHERSsetupAmountPHP", setUpApInPHP);
        	specificSetUpMap.put("CheckproductPaymentTotalPHP", setUpApInPHP);
        	

        }else if(currency.equalsIgnoreCase("USD")){
        	
        	setUpApInUSD = productAmount;
        	setUpApInPHP = productAmount.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
        	
        	specificSetUpMap.put("APRESOTHERSsetupAmountUSD", setUpApInUSD);
        	specificSetUpMap.put("APRESOTHERSsetupAmountPHP", setUpApInPHP);
        	specificSetUpMap.put("CheckproductPaymentTotalUSD", setUpApInUSD);
        	specificSetUpMap.put("CheckproductPaymentTotalPHP", setUpApInPHP);
        }else {

        	setUpApInThird = productAmount;
        	setUpApInUSD = productAmount.multiply(thirdToUSD).setScale(2, BigDecimal.ROUND_UP);
        	setUpApInPHP = setUpApInUSD.multiply(urr).setScale(2, BigDecimal.ROUND_UP);
        	
        	specificSetUpMap.put("APRESOTHERSsetupAmountTHIRD", setUpApInThird);
        	specificSetUpMap.put("APRESOTHERSsetupAmountPHP", setUpApInPHP);
        	specificSetUpMap.put("CheckproductPaymentTotalTHIRD", setUpApInThird);
        	specificSetUpMap.put("CheckproductPaymentTotalPHP", setUpApInPHP);

        	
        }
        

        AccountingEventTransactionId accountingEventTransactionId = new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT");
        
       genAccountingEntryPayment_charges(tradeService, specificSetUpMap, productRef, currency, bcLcCurrency,currency,bcLcCurrency, accountingEventTransactionId, gltsNumber, tradeServiceStatus);
       genAccountingEntryRefund_charges(tradeService, specificSetUpMap, productRef, currency, bcLcCurrency,currency,bcLcCurrency, accountingEventTransactionId, gltsNumber, tradeServiceStatus);
            
    }
    
    /***Accounting Entries for Apply AP to loans,
     * 
     * 
     * @param tradeService
     * @param details
     * @param paymentProduct
     * @param paymentService
     * @param productRef
     * @param gltsNumber
     * @param tradeServiceStatus
     */
    @Transactional
   private void genAccountingEntries_AP_APPLY(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    	System.out.println("genAccountingEntries_AP_APPLY");
    	

        String currency = details.get("currency").toString();
        BookCurrency bcLcCurrency = determineBookCurrency(currency);

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());
        BigDecimal productAmount = getBigDecimalOrZero(details.get("amount"));
        BigDecimal urr = getBigDecimalOrZero(details.get("urr"));
        BigDecimal thirdToUSD = getBigDecimalOrZero(details.get(currency + "-USD"));

        BigDecimal applyAPInPHP;
        BigDecimal applyAPInUSD;
        BigDecimal applyAPInThird;
        
        Map<String, Object> specificSetUpMap = new HashMap<String, Object>();
        
        if(currency.equalsIgnoreCase("PHP")) {
        	
        	applyAPInPHP = productAmount;
        	specificSetUpMap.put("APRESOTHERSapplyAmountPHP", applyAPInPHP);
        	specificSetUpMap.put("CheckrefundAmountPHP", applyAPInPHP);

        }else  if(currency.equalsIgnoreCase("USD")){
        	applyAPInUSD = productAmount;
        	applyAPInPHP = productAmount.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
        	
        	specificSetUpMap.put("APRESOTHERSapplyAmountUSD", applyAPInUSD);
        	specificSetUpMap.put("APRESOTHERSapplyAmountPHP", applyAPInPHP);
        	specificSetUpMap.put("CheckrefundAmountUSD", applyAPInUSD);
        	specificSetUpMap.put("CheckrefundAmountPHP", applyAPInPHP);
        	
        }else {
        	applyAPInThird = productAmount;
        	applyAPInUSD = productAmount.multiply(thirdToUSD).setScale(2, BigDecimal.ROUND_UP);
        	applyAPInPHP = applyAPInUSD.multiply(urr).setScale(2, BigDecimal.ROUND_UP);
        	
        	specificSetUpMap.put("APRESOTHERSapplyAmountThird", applyAPInThird);
        	specificSetUpMap.put("APRESOTHERSapplyAmountPHP", applyAPInPHP);
        	specificSetUpMap.put("CheckrefundAmountThird", applyAPInThird);
        	specificSetUpMap.put("CheckrefundAmountPHP", applyAPInPHP);
        }
        

        AccountingEventTransactionId accountingEventTransactionId = new AccountingEventTransactionId("ACCOUNTS-PAYABLE-APPLY-REFUND");
        
        genAccountingEntryRefund_charges(tradeService, specificSetUpMap, productRef, currency, bcLcCurrency,currency,bcLcCurrency, accountingEventTransactionId, gltsNumber, tradeServiceStatus);
    
    }

    /***accounting entries for AR settlement
     * 
     * @param tradeService
     * @param details
     * @param paymentProduct
     * @param paymentService
     * @param productRef
     * @param gltsNumber
     * @param tradeServiceStatus
     */
    @Transactional
    private void genAccountingEntries_AR_SETTLE(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_AR_SETTLE");
        
        String arCurrency = details.get("currency").toString();
        BookCurrency bcLcCurrency = determineBookCurrency(arCurrency);
        
        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());
        BigDecimal arAmount = getBigDecimalOrZero(details.get("amount"));
        BigDecimal urr = getBigDecimalOrZero(details.get("urr"));
        BigDecimal thirdToUsd = BigDecimal.ONE;
        
        if(bcLcCurrency.equals(BookCurrency.THIRD)) {
        	thirdToUsd = getBigDecimalOrZero(details.get(arCurrency + "-USD"));
        }
        
        BigDecimal settleArInPHP;
        BigDecimal settleArInUSD;
        BigDecimal settleArInThird;
        String paymentKey = "";
        
        Map<String, Object> specificArSetMap = new HashMap<String, Object>();       
        Set<PaymentDetail> temp = paymentProduct.getDetails();
   
        for (PaymentDetail paymentDetail : temp) {
            System.out.println("---------------------------");
            printPaymentDetails(paymentDetail);

            String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
            System.out.println("paymentName:" + paymentName);

            paymentKey = paymentName + bcLcCurrency.toString();
            BigDecimal paymentValue = new BigDecimal(paymentDetail.getAmount().toString());
            
            specificArSetMap.put(paymentKey, paymentValue);
            
            // for baseamount value
            if(arCurrency.equalsIgnoreCase("PHP")) {
            	paymentKey = paymentName + "PHP";
            	specificArSetMap.put(paymentKey, paymentValue);
            	
            } else if(arCurrency.equalsIgnoreCase("USD"))  {
            	paymentKey = paymentName + "PHP";
            	paymentValue = paymentValue.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
            	specificArSetMap.put(paymentKey, paymentValue);            	
            }else {
            	paymentKey = paymentName + "PHP";
            	BigDecimal paymentValueInUSD = paymentValue.multiply(thirdToUsd).setScale(2,BigDecimal.ROUND_UP);
            	BigDecimal paymentValueInPHP = paymentValueInUSD.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
            	
            	specificArSetMap.put(paymentKey, paymentValueInPHP);
            	
            }
            	
            System.out.println("paymentKey: "+ paymentKey);
            System.out.println("paymentValue: "+paymentValue);
            
            System.out.println("---------------------------");
        }
         
        //for ar entries
        if(arCurrency.equalsIgnoreCase("PHP")) {
        	
        	settleArInPHP = arAmount;
        	specificArSetMap.put("ARproductPaymentTotalPHP", settleArInPHP);


        } else if(arCurrency.equalsIgnoreCase("USD"))  {
        	settleArInUSD = arAmount;
        	settleArInPHP = arAmount.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
        	
        	specificArSetMap.put("ARproductPaymentTotalUSD", settleArInUSD);
        	specificArSetMap.put("ARproductPaymentTotalPHP", settleArInPHP);

        	
        }else {
        	settleArInThird = arAmount;
        	settleArInUSD = arAmount.multiply(thirdToUsd).setScale(2, BigDecimal.ROUND_UP);
        	settleArInPHP = settleArInUSD.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
        	
        	specificArSetMap.put("ARproductPaymentTotalTHIRD", settleArInThird);
        	specificArSetMap.put("ARproductPaymentTotalPHP", settleArInPHP);
        	
        }
        
        genAccountingEntrySettlement(tradeService, specificArSetMap, productRef, arCurrency, bcLcCurrency, arCurrency, bcLcCurrency, new AccountingEventTransactionId("ACCOUNTS-RECEIVABLE-SETTLEMENT"), gltsNumber, tradeServiceStatus);
    }
    
    /**Accounting Entries for Setup AR
     * 
     * 
     * @param tradeService
     * @param details
     * @param paymentProduct
     * @param paymentService
     * @param productRef
     * @param gltsNumber
     * @param tradeServiceStatus
     */
    @Transactional
    private void genAccountingEntries_AR_SETUP(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    
    	//TODO - ALex
    	
    	System.out.println("Booking of AR will be done in SIBS-Loans - FSD");
    }

    @Transactional
    private void genAccountingEntries_AP_REFUND(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_AP_REFUND");
        
        String currency = details.get("currency").toString();
        BookCurrency bcLcCurrency = determineBookCurrency(currency);
        
        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());
        BigDecimal productAmount = getBigDecimalOrZero(details.get("amount"));
        BigDecimal urr = getBigDecimalOrZero(details.get("urr"));
        BigDecimal thirdToUSD = getBigDecimalOrZero(details.get(currency + "-USD"));
        
        BigDecimal refundAPInPHP;
        BigDecimal refundAPInUSD;
        BigDecimal refundAPInThird;
        
        Map<String, Object> specificSetUpMap = new HashMap<String, Object>();
        
        if(currency.equalsIgnoreCase("PHP")) {
        	
        		refundAPInPHP = productAmount;
	        	specificSetUpMap.put("APRESOTHERSapplyAmountPHP", refundAPInPHP);
	        	specificSetUpMap.put("CheckrefundAmountPHP", refundAPInPHP);
	        	

	        } else if(currency.equalsIgnoreCase("USD"))  {
	        	refundAPInUSD = productAmount;
	        	refundAPInPHP = productAmount.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
	        	
	        	specificSetUpMap.put("APRESOTHERSapplyAmountUSD", refundAPInUSD);
	        	specificSetUpMap.put("APRESOTHERSapplyAmountPHP", refundAPInPHP);
	        	specificSetUpMap.put("CheckrefundAmountUSD", refundAPInUSD);
	        	specificSetUpMap.put("CheckrefundAmountPHP", refundAPInPHP);
	        	
	        } else {
	        	
	        	refundAPInThird = productAmount;
	        	refundAPInUSD = productAmount.multiply(thirdToUSD).setScale(2, BigDecimal.ROUND_UP);
	        	refundAPInPHP = refundAPInUSD.multiply(urr).setScale(2, BigDecimal.ROUND_UP);
	        	
	        	specificSetUpMap.put("APRESOTHERSapplyAmountThird", refundAPInThird);
	        	specificSetUpMap.put("APRESOTHERSapplyAmountPHP", refundAPInPHP);
	        	specificSetUpMap.put("CheckrefundAmountThird", refundAPInThird);
	        	specificSetUpMap.put("CheckrefundAmountPHP", refundAPInPHP);
	        }
	        

        AccountingEventTransactionId accountingEventTransactionId = new AccountingEventTransactionId("ACCOUNTS-PAYABLE-APPLY-REFUND");   
        genAccountingEntryRefund_charges(tradeService, specificSetUpMap, productRef, currency, bcLcCurrency,currency,bcLcCurrency, accountingEventTransactionId, gltsNumber, tradeServiceStatus);
    }
    
    /**Accounting entries for IMPORT LC/NON LC REFUND of CHARGES
     * 
     * 
     * @param tradeService
     * @param details
     * @param paymentProduct
     * @param paymentService
     * @param paymentSettlement
     * @param productRef
     * @param gltsNumber
     * @param tradeServiceStatus
     */
    @Transactional
    private void genAccountingEntries_Refund_CASHLC_CHARGES(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    	System.out.println("genAccountingEntries_Refund_CASHLC_CHARGES");
    	
    	String currencyForRefund = details.get("currency").toString();
        BookCurrency bcLcCurrency = determineBookCurrency(currencyForRefund);
              
        BigDecimal UsdToPhpRates = getBigDecimalOrZero(details.get("USD-PHP_text_pass_on_rate"));
        BigDecimal thirdToUsdRates = getBigDecimalOrZero(details.get(currencyForRefund + "-USD_text_pass_on_rate"));
        BigDecimal productRefundAmount = getBigDecimalOrZero(details.get("refundableProductAmount"));
        BigDecimal chargesRefundAmount = getBigDecimalOrZero(details.get("refundableServiceChargeAmount"));
        BigDecimal totalRefundAmount = getBigDecimalOrZero(details.get("refundableAmount"));
        BigDecimal origProductRefundAmount = divideOrReturnZero(productRefundAmount,UsdToPhpRates).setScale(2, BigDecimal.ROUND_UP);
        

        String settlementCurrencyCharges = "PHP";
        String paymentName = "";
        String paymentInstrumentType = "";
        Set<PaymentDetail> paymentSettlementMap = paymentSettlement.getDetails();
        for (PaymentDetail paymentDetail : paymentSettlementMap) {
            System.out.println("--------paymentSettlementeMap-----------");
            printPaymentDetails(paymentDetail);
            System.out.println("---------------------------");
            
            settlementCurrencyCharges = paymentDetail.getCurrency().toString();
            paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
            paymentInstrumentType = paymentDetail.getPaymentInstrumentType().toString();
            
        }
        
        BookCurrency bcSettleCurrency = determineBookCurrency(settlementCurrencyCharges);    
        
        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        
        //TODO: identify if documentType        
        Map<String, Object> cashLcRefundMap = new HashMap<String, Object>();
        
        if (bcLcCurrency.equals(BookCurrency.USD)) {
        	cashLcRefundMap.put("outstandingBalanceUSD", origProductRefundAmount);
            cashLcRefundMap.put("outstandingBalancePHP", productRefundAmount);
            cashLcRefundMap.put("productPaymentTotalUSD", origProductRefundAmount);
            cashLcRefundMap.put("productPaymentTotalPHP", productRefundAmount);
            
        }else if (bcLcCurrency.equals(BookCurrency.THIRD)){
        	
        	origProductRefundAmount = origProductRefundAmount.divide(thirdToUsdRates, BigDecimal.ROUND_UP);
        	BigDecimal thirdToUsd = origProductRefundAmount.multiply(thirdToUsdRates).setScale(2, BigDecimal.ROUND_UP);
        	BigDecimal thirdToPhp = thirdToUsd.multiply(UsdToPhpRates).setScale(2, BigDecimal.ROUND_UP);
        	
        	cashLcRefundMap.put("outstandingBalanceTHIRD", origProductRefundAmount);
            cashLcRefundMap.put("outstandingBalancePHP", productRefundAmount);
            //bridges
            cashLcRefundMap.put("productPaymentTotalTHIRD", origProductRefundAmount);
            cashLcRefundMap.put("productPaymentTotalUSD", thirdToUsd);
            cashLcRefundMap.put("productPaymentTotalPHP", thirdToPhp);
            
        }else{ //BookCurrency.PHP
        	cashLcRefundMap.put("outstandingBalancePHP", productRefundAmount);
        }
                
        genAccountingEntryLC(tradeService, cashLcRefundMap, productRef, currencyForRefund, bcLcCurrency, settlementCurrencyCharges, bcSettleCurrency, new AccountingEventTransactionId("LC-CASH-REVERSAL-FC"), gltsNumber, tradeServiceStatus);
        
//        System.out.println("=================DETAILS AS OF===================");
//        System.out.println("currency for refund: "+currencyForRefund);
//        System.out.println("BookCurrency: "+bcLcCurrency);
//        System.out.println("Product Amount for Refund in php: "+ productRefundAmount);
//        System.out.println("Charges Amount for refund in php: "+ chargesRefundAmount);
//        System.out.println("Total Amount for refund in php: "+totalRefundAmount);
//        System.out.println("USD TO PHP Rates: "+UsdToPhpRates);
//        System.out.println("Product Amount for Refund in original currency: "+ origProductRefundAmount);
//        System.out.println("Setllement Currency for all: "+settlementCurrencyCharges);
//        System.out.println("Payment Type: "+ paymentInstrumentType);
//        System.out.println("=================DETAILS AS OF===================");
             
//        Boolean cilexCharged = willWeChargeCilex(paymentProduct);
//        System.out.println("cilexCharged:"+cilexCharged);
//        Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

//       Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);
//
//       System.out.println("======================Charges Map================");
//	       for(Map.Entry<String, Object> refundMaps : chargeMap.entrySet()) {  	
//	       	System.out.println("Key = " + refundMaps.getKey() + ", Value = " + refundMaps.getValue());     	
//	       }
	       
	       
	   //handling the charges
	   String chargeId = "";
	   BigDecimal chargeAmount = BigDecimal.TEN;
	   Map<String, Object> refundedCharges = new HashMap<String,Object>();
	   for (ServiceCharge charge : tradeService.getServiceCharge()){
	            chargeId = charge.getChargeId().toString();
	            chargeAmount = charge.getAmount();
	            System.out.println("Charges to be refund: " +chargeId +" amounted to "+ chargeAmount);
	            
	            //map that carries all charges to be refund
	            refundedCharges.put(chargeId, chargeAmount);
	   }
	       
	  Map<String, Object> refundedChargesMap = getAllChargesMap(refundedCharges, "PHP");
	  
      genAccountingEntryRefund_charges(tradeService, refundedChargesMap, productRef, currencyForRefund, bcLcCurrency,settlementCurrencyCharges,bcSettleCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-REFUND"), gltsNumber, tradeServiceStatus);
     
	  
	   //all in one payment  
	   Map<String, Object> settlementRefundMap = new HashMap<String, Object>();
	   
	   //as of now all settlement refund is in PHP
	   if(paymentInstrumentType.equalsIgnoreCase("REMITTANCE")) {
		   settlementRefundMap.put("APRemittancesettlementTotalPHP", totalRefundAmount);	   
	   }else if(paymentInstrumentType.equalsIgnoreCase("CASA")){
		   settlementRefundMap.put("CASAsettlementTotalPHP", totalRefundAmount);	 
	   }else {  // issuance to MC
		   settlementRefundMap.put("MCsettlementTotalPHP", totalRefundAmount);		   
	   }
	   
	   genAccountingEntryLC(tradeService, settlementRefundMap, productRef, settlementCurrencyCharges, bcSettleCurrency, settlementCurrencyCharges, bcSettleCurrency, new AccountingEventTransactionId("LC-CASH-CHARGES-REFUND-SETTLEMENT"), gltsNumber, tradeServiceStatus);

    }
    
    /**Accounting entries for EXPORT CHARGES Refund
     * 
     * @param tradeService
     * @param details
     * @param paymentProduct
     * @param paymentService
     * @param paymentSettlement
     * @param productRef
     * @param gltsNumber
     * @param tradeServiceStatus
     */
    @Transactional
    private void genAccountingEntries_Refund_EXPORT_CHARGES(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    
    	
    	String exportCurrency = details.get("currency").toString();
        BookCurrency bcLcCurrency = determineBookCurrency(exportCurrency);
        
        BigDecimal UsdToPhpRates = getBigDecimalOrZero(details.get("USD-PHP_text_pass_on_rate"));
        BigDecimal thirdToUsdRates = getBigDecimalOrZero(details.get(exportCurrency + "-USD_text_pass_on_rate"));
        BigDecimal chargesRefundAmount = getBigDecimalOrZero(details.get("refundableServiceChargeAmount"));


        String settlementCurrencyCharges = "PHP";
        String paymentInstrumentType = "";
        Set<PaymentDetail> paymentSettlementMap = paymentSettlement.getDetails();
        for (PaymentDetail paymentDetail : paymentSettlementMap) {
            System.out.println("--------paymentSettlementeMap-----------");
            printPaymentDetails(paymentDetail);
            System.out.println("---------------------------");
            
            settlementCurrencyCharges = paymentDetail.getCurrency().toString();
            paymentInstrumentType = paymentDetail.getPaymentInstrumentType().toString();
            
        }
        
        BookCurrency bcSettleCurrency = determineBookCurrency(settlementCurrencyCharges);    
        
        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

        
      System.out.println("=================DETAILS AS OF===================");
      System.out.println("BookCurrency: "+bcLcCurrency);
      System.out.println("Charges Amount for refund in php: "+ chargesRefundAmount);
      System.out.println("USD TO PHP Rates: "+UsdToPhpRates);
      System.out.println("Setllement Currency for all: "+settlementCurrencyCharges);
      System.out.println("Payment Type: "+ paymentInstrumentType);
      System.out.println("=================DETAILS AS OF===================");
        
        
      String chargeId = "";
	   BigDecimal chargeAmount = BigDecimal.TEN;
	   Map<String, Object> refundedCharges = new HashMap<String,Object>();
	   for (ServiceCharge charge : tradeService.getServiceCharge()){
	            chargeId = charge.getChargeId().toString();
	            chargeAmount = charge.getAmount();
	            System.out.println("Charges to be refund: " +chargeId +" amounted to "+ chargeAmount);
	            
	            //map that carries all charges to be refund
	            refundedCharges.put(chargeId, chargeAmount);
	   }
	   
	   
	   Map<String, Object> refundedChargesMap = getAllChargesMap(refundedCharges, "PHP");
	   genAccountingEntryRefund_charges(tradeService, refundedChargesMap, productRef, exportCurrency, bcLcCurrency,settlementCurrencyCharges,bcSettleCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-REFUND"), gltsNumber, tradeServiceStatus);
	     
	   
	   
	   //all in one payment  
	   Map<String, Object> settlementRefundMap = new HashMap<String, Object>();
	   
	   //as of now all settlement refund is in PHP
	   if(paymentInstrumentType.equalsIgnoreCase("REMITTANCE")) {
		   settlementRefundMap.put("APRemittancesettlementTotalPHP", chargesRefundAmount);	   
	   }else if(paymentInstrumentType.equalsIgnoreCase("CASA")){
		   settlementRefundMap.put("CASAsettlementTotalPHP", chargesRefundAmount);	 
	   }else {  // issuance to MC
		   settlementRefundMap.put("MCsettlementTotalPHP", chargesRefundAmount);		   
	   }
	   
	   genAccountingEntryLC(tradeService, settlementRefundMap, productRef, settlementCurrencyCharges, bcSettleCurrency, settlementCurrencyCharges, bcSettleCurrency, new AccountingEventTransactionId("LC-CASH-CHARGES-REFUND-SETTLEMENT"), gltsNumber, tradeServiceStatus);
	   
    }
    
    
    /**Accounting Entries for MD Application
     * 
     * @param tradeService
     * @param details
     * @param paymentProduct
     * @param paymentService
     * @param productRef
     * @param gltsNumber
     * @param tradeServiceStatus
     */
    @Transactional
    private void genAccountingEntries_MD_APPLICATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {

    	System.out.println("genAccountingEntries_MD_APPLICATION");
        //TWO Events
        // MD-APPLICATION-APPLY
        // MD-APPLICATION-REFUND


        String level="";
        String settlementType="";
        if(tradeService.getDetails().containsKey("modeOfRefund")){
            System.out.println("ETS");
            level="ETS";
            settlementType = tradeService.getDetails().get("modeOfRefund").toString();
            System.out.println("modeOfRefund:"+tradeService.getDetails().get("modeOfRefund").toString());

        } else if(tradeService.getDetails().containsKey("modeOfApplication")){
            System.out.println("TSD");
            level="TSD";
            settlementType = tradeService.getDetails().get("modeOfApplication").toString();
            System.out.println("modeOfApplication:"+tradeService.getDetails().get("modeOfApplication").toString());
        }

        System.out.println("tradeService.getTradeServiceId():"+tradeService.getTradeServiceId());

        System.out.println("mdCurrency:"+tradeService.getDetails().get("mdCurrency").toString());
        String mdCurrency = tradeService.getDetails().get("mdCurrency").toString();
        BookCurrency bcMdCurrency = determineBookCurrency(mdCurrency);

        System.out.println("amountOfMdToApply:"+tradeService.getDetails().get("amountOfMdToApply").toString());
//        BigDecimal amountOfMdToApply = new BigDecimal(tradeService.getDetails().get("amountOfMdToApply").toString());
//        
//        if(amountOfMdToApply == null || amountOfMdToApply == new BigDecimal((String) "")){
//        	amountOfMdToApply = BigDecimal.ZERO;
//        }
       
        BigDecimal amountOfMdToApply = tradeService.getDetails().get("amountOfMdToApply") != null &&
        tradeService.getDetails().get("amountOfMdToApply").toString().isEmpty() ?
			new BigDecimal(tradeService.getDetails().get("amountOfMdToApply").toString()) : BigDecimal.ZERO;
        
        
        System.out.println("amountOfMdToApply :"+amountOfMdToApply );
//        System.out.println("USD-PHP:"+tradeService.getDetails().get("USD-PHP").toString());
//        BigDecimal usd_php = new BigDecimal(tradeService.getDetails().get("USD-PHP").toString());

        System.out.println("urr:"+tradeService.getDetails().get("urr").toString());
        BigDecimal urr =  new BigDecimal(tradeService.getDetails().get("urr").toString());





        Map<String, Object> lcMap = new HashMap<String, Object>();
        Map<String, Object> paymentMap = new HashMap<String, Object>();

        System.out.println("settlementType ====>>"+settlementType);
        if(mdCurrency.equalsIgnoreCase("PHP")){
            //PHP
            lcMap.put("refundAmountPHP",amountOfMdToApply);
            System.out.println("refundAmountPHP:"+amountOfMdToApply);
            if(settlementType.equalsIgnoreCase("CASA")){
                paymentMap.put("CASArefundAmountPHP",amountOfMdToApply);
            } else if(settlementType.equalsIgnoreCase("REFUND_TO_CLIENT_ISSUE_MC")) {
                paymentMap.put("MCrefundAmountPHP",amountOfMdToApply);
            }
        } else if(mdCurrency.equalsIgnoreCase("USD")){
            //USD
            lcMap.put("refundAmountUSD", amountOfMdToApply);
            lcMap.put("refundAmountPHP", amountOfMdToApply.multiply(urr));
            System.out.println("refundAmountUSD:" + amountOfMdToApply);
            System.out.println("refundAmountPHP:" + amountOfMdToApply.multiply(urr));
            if(settlementType.equalsIgnoreCase("CASA")){
                paymentMap.put("CASArefundAmountUSD",amountOfMdToApply);
                paymentMap.put("CASArefundAmountPHP",amountOfMdToApply.multiply(urr));
            } else if(settlementType.equalsIgnoreCase("REFUND_TO_CLIENT_ISSUE_MC")) {
                paymentMap.put("MCrefundAmountUSD",amountOfMdToApply);
                paymentMap.put("MCrefundAmountPHP",amountOfMdToApply.multiply(urr));
            } else {
                paymentMap.put("CASArefundAmountUSD",amountOfMdToApply);
                paymentMap.put("CASArefundAmountPHP",amountOfMdToApply.multiply(urr));
            }
        } else {
            //THIRD
            BigDecimal third_usd = new BigDecimal(tradeService.getDetails().get(mdCurrency+"-PHP").toString());
            lcMap.put("refundAmountTHIRD",amountOfMdToApply);
            lcMap.put("refundAmountUSD",amountOfMdToApply.divide(third_usd,2,BigDecimal.ROUND_UP));
            lcMap.put("refundAmountPHP",amountOfMdToApply.divide(third_usd,2,BigDecimal.ROUND_UP).multiply(urr));

            System.out.println("refundAmountTHIRD:"+amountOfMdToApply);
            System.out.println("refundAmountUSD:"+amountOfMdToApply.divide(third_usd,2,BigDecimal.ROUND_UP));
            System.out.println("refundAmountUSD:"+amountOfMdToApply.divide(third_usd,2,BigDecimal.ROUND_UP).multiply(urr));
            if(settlementType.equalsIgnoreCase("CASA")){
                paymentMap.put("CASArefundAmountTHIRD",amountOfMdToApply);
                paymentMap.put("CASArefundAmountUSD",amountOfMdToApply.divide(third_usd,2,BigDecimal.ROUND_UP));
                paymentMap.put("CASArefundAmountPHP",amountOfMdToApply.divide(third_usd,2,BigDecimal.ROUND_UP).multiply(urr));
            } else if(settlementType.equalsIgnoreCase("REFUND_TO_CLIENT_ISSUE_MC")) {
                paymentMap.put("MCrefundAmountTHIRD",amountOfMdToApply);
                paymentMap.put("MCrefundAmountUSD",amountOfMdToApply.divide(third_usd,2,BigDecimal.ROUND_UP));
                paymentMap.put("MCrefundAmountPHP",amountOfMdToApply.divide(third_usd,2,BigDecimal.ROUND_UP).multiply(urr));
            }
        }
        

        genAccountingEntryLC_charges(tradeService, lcMap, productRef, mdCurrency, bcMdCurrency, mdCurrency, bcMdCurrency, new AccountingEventTransactionId("MD-APPLICATION-REFUND"), gltsNumber, tradeServiceStatus);
        genAccountingEntryRefund_charges(tradeService, paymentMap, productRef, mdCurrency, bcMdCurrency, mdCurrency, bcMdCurrency, new AccountingEventTransactionId("MD-APPLICATION-REFUND"), gltsNumber, tradeServiceStatus);

    }
    
    @Transactional
    private void genAccountingEntries_MD_COLLECTION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        //ONLY ONE EVENT
        //MD-COLLECTION-PAYMENT
    	
    	String currency = "PHP";
        
    	//MD Collection always look at the Settlement currency
        Set<PaymentDetail> laman = paymentProduct.getDetails();
        for (PaymentDetail paymentDetails : laman) {
        	currency = paymentDetails.getCurrency().toString();
        }
        
        BookCurrency bcLcCurrency = determineBookCurrency(currency);
        //NOTE: THIS IS A WIERD MODULE MUST DO OWN PAYMENT THING

        System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());
        BigDecimal productAmount = getBigDecimalOrZero(details.get("amount"));
        BigDecimal urr = getBigDecimalOrZero(details.get("urr"));
        BigDecimal thirdToUsd = BigDecimal.ZERO;

        String conversionStringToUSD = currency + "-USD";
        String conversionStringToUSD00 = currency + "-USD_pass_on_rate_charges";
        String conversionStringToUSD01 = currency + "-USD_pass_on_rate_cash";

        BigDecimal THIRD_USD_conversion = BigDecimal.ZERO;

        //Check if conversion of third to php exists
        if (details.containsKey(conversionStringToUSD00)) {
            THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD00));
        } else if (details.containsKey(conversionStringToUSD01)) {
            THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD01));
        } else if (details.containsKey(conversionStringToUSD)) {
            THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD));
        } else{
            System.out.println("THIRD to USD Conversion Missing");
        }

        thirdToUsd = THIRD_USD_conversion;



        Map<String, Object> chargeMap = new HashMap<String, Object>();
        String valueName = "mdPaymentAmount";
        insertValueNameToValueMapUrr(details, productAmount, currency, chargeMap, urr, valueName);

        BigDecimal paymentInPHPTotal = BigDecimal.ZERO;
        BigDecimal paymentInUSDTotal = BigDecimal.ZERO;
        BigDecimal paymentInTHIRDTotal = BigDecimal.ZERO;


        //Booking is done from payment currency to lc currency
        System.out.println("Payment of Service Charges Start");
//        Boolean PaymentChargeOnce = Boolean.FALSE;
        Set<PaymentDetail> temp = paymentProduct.getDetails();
        
        //payment 
       // String paymentCurrencyString = "PHP"; wala nato
        for (PaymentDetail paymentDetail : temp) {
            System.out.println("---------------------------");
            printPaymentDetails(paymentDetail);

            String curr = paymentDetail.getCurrency().toString();
            BigDecimal amnt = paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP);

            String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
            System.out.println("paymentName:" + paymentName);
            BigDecimal paymentInPHP;
            BigDecimal paymentInUSD;
            BigDecimal paymentInTHIRD;

            if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {
                paymentInPHP = paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP);


                if (thirdToUsd != null && thirdToUsd.compareTo(BigDecimal.ZERO) == 1
                        && urr != null && urr.compareTo(BigDecimal.ZERO) == 1
                        ) {
                    paymentInTHIRD = paymentDetail.getAmount().divide(thirdToUsd.multiply(urr), 2, BigDecimal.ROUND_UP);
                } else {
                    paymentInTHIRD = BigDecimal.ZERO;
                }

                if (urr != null && urr.compareTo(BigDecimal.ZERO) == 1) {
                    paymentInUSD = paymentDetail.getAmount().divide(urr, 2, BigDecimal.ROUND_UP);
                } else {
                    paymentInUSD = BigDecimal.ZERO;
                }


            } else if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")) {

                paymentInUSD = paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP);
                if (paymentDetail.getUrr() != null) {
                    paymentInPHP = paymentInUSD.multiply(urr).setScale(2, BigDecimal.ROUND_UP);
                } else {
                    paymentInPHP = BigDecimal.ZERO;
                }

                if (thirdToUsd != null && thirdToUsd.compareTo(BigDecimal.ZERO) == 1) {
                    paymentInTHIRD = paymentDetail.getAmount().divide(thirdToUsd, 2, BigDecimal.ROUND_UP);
                } else {
                    paymentInTHIRD = BigDecimal.ZERO;
                }

            } else {
                //currency is THIRD
                paymentInTHIRD = paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP);

                if (thirdToUsd != null && thirdToUsd.compareTo(BigDecimal.ZERO) == 1
                        && urr != null && urr.compareTo(BigDecimal.ZERO) == 1
                        ) {
                    paymentInPHP = paymentDetail.getAmount().multiply(thirdToUsd).multiply(urr).setScale(2, BigDecimal.ROUND_UP);
                } else {
                    paymentInPHP = BigDecimal.ZERO;
                }


                if (paymentDetail.getSpecialRateThirdToUsd() != null) {
                    paymentInUSD = paymentInTHIRD.divide(thirdToUsd, 2, BigDecimal.ROUND_UP);
                } else {
                    paymentInUSD = BigDecimal.ZERO;
                }


            }

            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            specificPaymentMap.put(paymentName + "PHP", paymentInPHP);
            specificPaymentMap.put("productPaymentTotalPHP", paymentInPHP);
            specificPaymentMap.put(paymentName + "USD", paymentInUSD);
            specificPaymentMap.put("productPaymentTotalUSD", paymentInUSD);
            specificPaymentMap.put(paymentName + "THIRD", paymentInTHIRD);
            specificPaymentMap.put("productPaymentTotalTHIRD", paymentInTHIRD);

            paymentInPHPTotal = paymentInPHPTotal.add(paymentInPHP);
            paymentInUSDTotal = paymentInUSDTotal.add(paymentInUSD);
            paymentInTHIRDTotal = paymentInTHIRDTotal.add(paymentInTHIRD);


            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
            System.out.println("specificPaymentMap:" + specificPaymentMap);
            System.out.println("---------------------------");

            String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
            BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
            
            
            //MD PAYMENT MAP for genAccountingEntryCharge
            Map<String, Object> mdPaymentMap = new HashMap<String, Object>();

            

            genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef,paymentSettlementCurrency, payBookCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("MD-COLLECTION-PAYMENT"), gltsNumber, tradeServiceStatus);

            String mdPaymentCurrency = paymentDetail.getCurrency().getCurrencyCode();
            BookCurrency bcMdCurrency = determineBookCurrency(mdPaymentCurrency);
        	
            //special case for third- For PCCF yun MD Third
            if(bcMdCurrency.toString().equalsIgnoreCase("THIRD")) {
			
				String conversionRate = details.get(paymentSettlementCurrency + "-USD").toString();
				BigDecimal thirdToUSDConversion = new BigDecimal(conversionRate);
				paymentInUSD = paymentInTHIRD.multiply(thirdToUSDConversion).setScale( 2, RoundingMode.HALF_UP);

				mdPaymentMap.put("mdPaymentAmountPHP", paymentInPHP);
				mdPaymentMap.put("productPaymentTotalPHP", paymentInPHP);
				mdPaymentMap.put("mdPaymentAmountUSD", paymentInUSD);
				mdPaymentMap.put("productPaymentTotalUSD", paymentInUSD);
				mdPaymentMap.put("mdPaymentAmountTHIRD", paymentInTHIRD);
				mdPaymentMap.put("productPaymentTotalTHIRD", paymentInTHIRD);

                genAccountingEntryCharge_charges(tradeService, mdPaymentMap, productRef,paymentSettlementCurrency, payBookCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("MD-COLLECTION-PAYMENT-THIRD"), gltsNumber, tradeServiceStatus);
                
            }else {
                mdPaymentMap.put("mdPaymentAmountPHP", paymentInPHP);
                mdPaymentMap.put("productPaymentTotalPHP", paymentInPHP);
                mdPaymentMap.put("mdPaymentAmountUSD", paymentInUSD);
                mdPaymentMap.put("productPaymentTotalUSD", paymentInUSD);
                mdPaymentMap.put("mdPaymentAmountTHIRD", paymentInTHIRD);
                mdPaymentMap.put("productPaymentTotalTHIRD", paymentInTHIRD);
                genAccountingEntryCharge_charges(tradeService, mdPaymentMap, productRef,paymentSettlementCurrency, payBookCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("MD-COLLECTION-PAYMENT"), gltsNumber, tradeServiceStatus);
                
            }

            System.out.println("specificPaymentMap PaymentCharge:" + specificPaymentMap);
            System.out.println("chargeMap PaymentCharge:" + chargeMap);
            System.out.println("---------------------------");

        }



//        System.out.println("PHP:" + paymentInPHPTotal);
//        System.out.println("USD:" + paymentInUSDTotal);
//        System.out.println("THIRD:" + paymentInTHIRDTotal);
//        BigDecimal newAmount;
//        if (currency.equalsIgnoreCase("PHP")) {
//            newAmount = paymentInPHPTotal;
//        } else if (currency.equalsIgnoreCase("USD")) {
//            newAmount = paymentInUSDTotal;
//        } else {
//            newAmount = paymentInTHIRDTotal;
//        }
//
//        System.out.println("newAmount:" + newAmount);
//
//
//        chargeMap = new HashMap<String, Object>();
//        insertValueNameToValueMapUrr(details, newAmount, currency, chargeMap, urr, valueName);
//
//        System.out.println("chargeMap Charge:" + chargeMap);
//
//        //Generate Accounting Entry Related to Charges since all LC opening has charges
//        genAccountingEntryCharge_charges(tradeService, chargeMap, productRef, currency, bcLcCurrency, currency, bcLcCurrency, new AccountingEventTransactionId("MD-COLLECTION-PAYMENT"), gltsNumber, tradeServiceStatus);




    }

    @Transactional
    private void genAccountingEntries_NON_LC_NEGOTIATION_ACCEPTANCE(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_NON_LC_NEGOTIATION_ACCEPTANCE");
        //SET-UP-CONTINGENT-ENTRY

        BigDecimal negotiationAmount = getBigDecimalOrZero(details.get("amount"));

        String negoCurrency = getStringOrReturnEmptyString(details, "currency");
        if (negoCurrency == null) {
            negoCurrency = "PHP";
            System.out.println("No nego currency found defaulting to PHP");
        }

        Map<String, Object> negoMap = new HashMap<String, Object>();
        BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));

        String valueName = "negoAmount";
        insertValueNameToValueMapUrr(details, negotiationAmount, negoCurrency, negoMap, urr, valueName);

        System.out.println("negoCurrency:" + negoCurrency);
        BookCurrency bcNegoCurrency = determineBookCurrency(negoCurrency);

        //SET-UP-CONTINGENT-ENTRY
        genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);


    }

    @Transactional
    private void genAccountingEntries_NON_LC_SETTLEMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    	System.out.println("genAccountingEntries_NON_LC_SETTLEMENT");
       
        try {
            //Create Product/Charges Summary here
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString(); //Get LC Currency
            } else {

            }
            if (lcCurrency.equalsIgnoreCase("")) {
                if (tradeService.getDetails().containsKey("currency")) {
                    lcCurrency = tradeService.getDetails().get("currency").toString();
                }
            }
            System.out.println("lcCurrency: " + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);
            
            String settlementCurrencyCharges = "";
            if(tradeService.getServiceChargeCurrency() !=null){
                tradeService.getServiceChargeCurrency().toString(); //Get Settlement Currency
            }else if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else {
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges: " + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());
            
            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();
           
            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "settlementAmount");

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, paymentSettlement);
           
            BigDecimal productAmount = getBigDecimalOrZero(details.get("productAmount"));
            BigDecimal outstandingAmount = getBigDecimalOrZero(details.get("outstandingAmount"));
            System.out.println("productAmount:" + productAmount);
            System.out.println("outstandingAmount:" + outstandingAmount);

            String negoCurrency = getStringOrReturnEmptyString(details, "currency");
            if (negoCurrency == null) {
                negoCurrency = "PHP";
                System.out.println("No nego currency found defaulting to PHP");
            }
            BookCurrency bcNegoCurrency = determineBookCurrency(negoCurrency);

            Map<String, Object> negoMapOutstandingBalance = new HashMap<String, Object>();
            Map<String, Object> negoMapProductAmount = new HashMap<String, Object>();
            Map<String, Object> negoMap;// = new HashMap<String, Object>();

//            BigDecimal USD_PHP_conversion = BigDecimal.ZERO;
            BigDecimal urr = BigDecimal.ZERO;

            if (details.containsKey("USD-PHP_urr")) {
                urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));
            } else if (details.containsKey("urr")) {
                urr = getBigDecimalOrZero(details.get("urr"));
            }
            System.out.println("urr:" + urr);
            String valueName = "settlementAmount";
            insertValueNameToValueMapUrr(tradeService, productAmount, negoCurrency, negoMapProductAmount, urr, valueName);
            System.out.println("negoMapProductAmount:" + negoMapProductAmount);//This is the nego map based on the product amount

            insertValueNameToValueMapUrr(tradeService, outstandingAmount, negoCurrency, negoMapOutstandingBalance, urr, valueName);
            System.out.println("negoMapOutstandingBalance:" + negoMapOutstandingBalance);//This is the nego map based on the outstanding amount

            String accountingEventTypeIdString; // = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR";
            if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC)) {

                if (tradeService.getDocumentClass().equals(DocumentClass.DP)) {
                      //REVERSAL-CONTINGENT-ENTRY
                    if (productAmount.compareTo(outstandingAmount) == 1) {//used to determine if what should be reverse in the contingent entry is the outstanding amount or the product amount
                        negoMap = negoMapOutstandingBalance;
                    } else {
                        negoMap = negoMapProductAmount;
                    }
                    genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
                }
                //SETTLEMENT-NEGO-AMOUNT
                accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT";
                System.out.println("===DM NON LC SETTLEMENT-NEGO-AMOUNT====");
                if (paymentProduct != null) {
                    generateAccountingEntryForDomesticPayment(tradeService, paymentProduct, productRef, lcCurrency, bcLcCurrency, gltsNumber, tradeServiceStatus, paymentSettlement);
                }

                if (paymentSettlement != null) {
                    generateAccountingEntryForDomesticSettlement(tradeService, paymentSettlement, productRef, lcCurrency, bcLcCurrency, gltsNumber, tradeServiceStatus, paymentProduct);
                }
                
                System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);
                //ONLY ONCE FOR EVERYTHING
                genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
             } else if (tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {

                if (tradeService.getDocumentClass().equals(DocumentClass.DP) || tradeService.getDocumentClass().equals(DocumentClass.DA)) {
                    //SET-UP-CONTINGENT-ENTRY
                    if (productAmount.compareTo(outstandingAmount) == 1) {//used to determine if what should be reverse in the contingent entry is the outstanding amount or the product amount
                        negoMap = negoMapOutstandingBalance;
                    } else {
                        negoMap = negoMapProductAmount;
                    }
                    genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
                }
                negoMap = negoMapProductAmount; //Reseting for payment and DUE FROM TO FOREIGN BANK
                generateAccountingEntryForForeignPayment(tradeService, paymentProduct, productRef, lcCurrency, bcLcCurrency, negoMap, gltsNumber, tradeServiceStatus);
            } else {
                 System.out.println("Document Type is neither Domestic or Foreign");
            }
            if (paymentService != null) {
              generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }
            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
             e.printStackTrace();
        }
    }

    @Transactional
    private void genAccountingEntries_INDEMNITY_FOREIGN_ISSUANCE(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_INDEMNITY_FOREIGN_ISSUANCE");
        try {
            //Create Product/Charges Summary here
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString(); //Get LC Currency
            }

            if("".equalsIgnoreCase(lcCurrency)){
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }

            String indemnityCurrency="PHP"; //Special case because it can only be settled through PHP
            BookCurrency bcIndemnityCurrency = determineBookCurrency(indemnityCurrency);

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

//            String settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString(); //Get Settlement Currency
            String settlementCurrencyCharges = "PHP"; //Get Settlement Currency
            System.out.println("settlementCurrencyCharges:" + settlementCurrencyCharges);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "shipmentAmount");
            System.out.println("lcMap:"+lcMap);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);
            System.out.println("chargeMap:"+chargeMap);


            if(tradeService.isForReinstatement()){
                //
                String shipmentCurrency = tradeService.getDetails().get("shipmentCurrency").toString();
                BookCurrency bcShipmentCurrency = determineBookCurrency(shipmentCurrency);
                String outstandingBalanceString = tradeService.getDetails().get("outstandingBalance").toString();
                BigDecimal outstandingBalance = new BigDecimal(outstandingBalanceString);
                BigDecimal urr = ratesService.getUrrConversionRateToday();
                Map<String, Object> negoMapOutstandingBalance = new HashMap<String, Object>();
                insertValueNameToValueMapUrr(tradeService, outstandingBalance, shipmentCurrency, negoMapOutstandingBalance, urr, "negoAmount");

                System.out.println(" handling for performance and financial for REINSTATEMENT");
                System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
                if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                    if (details.containsKey("standbyTagging")) {
                        if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                            //REVERSAL-CONTINGENT-ENTRY-PERFORMANCE
                            System.out.println("SETUP-CONTINGENT-ENTRY-PERFORMANCE");
                            genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, shipmentCurrency, bcShipmentCurrency, shipmentCurrency, bcShipmentCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                        } else {
                            //REVERSAL-CONTINGENT-ENTRY-FINANCIAL
                            System.out.println("SETUP-CONTINGENT-ENTRY-FINANCIAL");
                            genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, shipmentCurrency, bcShipmentCurrency, shipmentCurrency, bcShipmentCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, shipmentCurrency, bcShipmentCurrency, shipmentCurrency, bcShipmentCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                    }
                } else {
                    if(DocumentSubType1.CASH.equals(tradeService.getDocumentSubType1())){
                        genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, shipmentCurrency, bcShipmentCurrency, shipmentCurrency, bcShipmentCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-CASH"), gltsNumber, tradeServiceStatus);
                    } else if(DocumentSubType1.REGULAR.equals(tradeService.getDocumentSubType1())){
                        if(DocumentSubType2.SIGHT.equals(tradeService.getDocumentSubType2())){
                            //SIGHT
                            genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, shipmentCurrency, bcShipmentCurrency, shipmentCurrency, bcShipmentCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-REGULAR-SIGHT"), gltsNumber, tradeServiceStatus);
                        } else {
                            //USANCE
                            genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, shipmentCurrency, bcShipmentCurrency, shipmentCurrency, bcShipmentCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-REGULAR-USANCE"), gltsNumber, tradeServiceStatus);
                        }
                    }

                }
            }

            //Payment of Cancellation Fee is the same as Payment of Charges
            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, indemnityCurrency, bcIndemnityCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            String indemnityType = getStringOrReturnEmptyString(details, "indemnityType");

            //Note: No contingent entry setting for BE
            if (!indemnityType.equalsIgnoreCase("BE")) {
                //SET-UP_CONTINGENT-ENTRY
                genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SETUP-OF-CONTINGENT-ENTRY-BGBE"), gltsNumber, tradeServiceStatus);
            }


            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void genAccountingEntries_INDEMNITY_FOREIGN_CANCELLATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {

        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            }

            System.out.println("lcCurrency:" + lcCurrency);
            if(lcCurrency.equalsIgnoreCase("") && tradeService.getDetails().containsKey("shipmentCurrency")){
                lcCurrency = tradeService.getDetails().get("shipmentCurrency").toString().trim();
            } else {
                System.out.println("NNNNNNNNNNNNNOOOOOOOOOOOOOOOOOOOOOOOOOO       SHIPMENT CURRENCY");
            }
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = new HashMap<String, Object>();

            BigDecimal shipmentAmount = new BigDecimal(tradeService.getDetails().get("shipmentAmount").toString().replace(",",""));
            String conversionStringToUSD;
            BigDecimal THIRD_PHP_conversion = BigDecimal.ZERO;
            BigDecimal THIRD_USD_conversion = BigDecimal.ZERO;
            BigDecimal urr = getBigDecimalOrZero(details.get("creationExchangeRateUsdToPHPUrr"));


            if (lcCurrency.equalsIgnoreCase("PHP")) {
                lcMap.put("shipmentAmountPHP", shipmentAmount);

            } else if (lcCurrency.equalsIgnoreCase("USD")) {
                lcMap.put("shipmentAmountUSD", shipmentAmount);
                lcMap.put("shipmentAmountPHP", shipmentAmount.multiply(urr));

            } else {
                conversionStringToUSD = lcCurrency + "-USD";

                if (details.containsKey(conversionStringToUSD)) {
                    THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD));
                    THIRD_PHP_conversion = THIRD_USD_conversion.multiply(urr);
                }

                lcMap.put("shipmentAmountPHP", shipmentAmount.multiply(THIRD_PHP_conversion));
                lcMap.put("shipmentAmountUSD", shipmentAmount.multiply(THIRD_USD_conversion));
                lcMap.put("shipmentAmountTHIRD", shipmentAmount);
            }

            System.out.println("lcMap:"+lcMap);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);
            System.out.println("chargeMap:"+chargeMap);
            //Reversal of Contingent Entry
            genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-CANCELLATION-BG"), gltsNumber, tradeServiceStatus);

            String clientInitiatedFlag = "N";
            if(tradeService.getDetails().containsKey("clientInitiatedFlag")){
                if(tradeService.getDetails().get("clientInitiatedFlag")!=null && !tradeService.getDetails().get("clientInitiatedFlag").toString().equalsIgnoreCase("")){
                    tradeService.getDetails().get("clientInitiatedFlag").toString();
                }
            }

            //No charge if client initiated
            System.out.println("clientInitiatedFlag:"+clientInitiatedFlag);

            if(!clientInitiatedFlag.equalsIgnoreCase("Y") || !clientInitiatedFlag.equalsIgnoreCase("YES")){
                //Payment of Cancellation Fee is the same as Payment of Charges
                if (paymentService != null) {
                    generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
                }
            }


            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_UA_LOAN_SETTLEMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_UA_LOAN_SETTLEMENT");
        try {

            String nameOfAmount = "settlementAmount";

            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            } else {
                if (tradeService.getDetails().get("currency") != null) {
                    lcCurrency = (String) tradeService.getDetails().get("currency");
                }

            }
            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            String settlementCurrencyCharges = "";
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }


            System.out.println("settlementCurrencyCharges:" + settlementCurrencyCharges);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, nameOfAmount);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, paymentSettlement);

            BigDecimal productAmount = getBigDecimalOrZero(details.get("amount"));

            String negoCurrency = getStringOrReturnEmptyString(details, "currency");
            if (negoCurrency == null) {
                negoCurrency = "PHP";
                System.out.println("No currency found defaulting to PHP");
            }
            BookCurrency bcNegoCurrency = determineBookCurrency(negoCurrency);

            Map<String, Object> negoMap = new HashMap<String, Object>();

//            String conversionStringToUSD = "";
//            BigDecimal THIRD_USD_conversion = BigDecimal.ZERO;
            BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));

            String valueName = "settlementAmount";
            insertValueNameToValueMapUrr(tradeService, productAmount, negoCurrency, negoMap, urr, valueName);

            if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC)) {
                if (paymentProduct != null) {
                    generateAccountingEntryForDomesticPayment(tradeService, paymentProduct, productRef, negoCurrency, bcNegoCurrency, gltsNumber, tradeServiceStatus, paymentSettlement);
                }

                if (paymentSettlement != null) {
                    generateAccountingEntryForDomesticSettlement(tradeService, paymentSettlement, productRef, negoCurrency, bcNegoCurrency, gltsNumber, tradeServiceStatus, paymentProduct);
                }

            } else if (tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
                System.out.println("In Document Type Foreign");

                generateAccountingEntryForForeignPayment(tradeService, paymentProduct, productRef, negoCurrency, bcNegoCurrency, negoMap, gltsNumber, tradeServiceStatus);

            } else {
                System.out.println("Document Type is neither Domestic or Foreign");
            }

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, negoCurrency, bcNegoCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus,null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void genAccountingEntries_UA_LOAN_MATURITY_ADJUSTMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_UA_LOAN_MATURITY_ADJUSTMENT");
        try {

            String nameOfAmount = "loanAmount";

            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }

            System.out.println("settlementCurrencyCharges:" + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, nameOfAmount);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method used to generate NON LC Negotiation Accounting Entries
     *
     * @param tradeService       TradeService Object
     * @param details            Map containing values to be used in generating accounting entry
     * @param productRef         ProductId of Product
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntries_NON_LC_NEGOTIATION(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        //SET-UP-CONTINGENT-ENTRY

        BigDecimal negotiationAmount = getBigDecimalOrZero(details.get("amount"));

        String negoCurrency = getStringOrReturnEmptyString(details, "currency");
        if (negoCurrency == null) {
            negoCurrency = "PHP";
            System.out.println("No nego currency found defaulting to PHP");
        }

        Map<String, Object> negoMap = new HashMap<String, Object>();

        BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));
        String valueName = "negoAmount";

        insertValueNameToValueMapUrr(details, negotiationAmount, negoCurrency, negoMap, urr, valueName);

        System.out.println("negoCurrency:" + negoCurrency);
        BookCurrency bcNegoCurrency = determineBookCurrency(negoCurrency);

        //SET-UP-CONTINGENT-ENTRY
        genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);

    }

    /**
     * Method used to generate accounting entries for NON LC Cancellation transactions
     *
     * @param tradeService       TradeService object
     * @param details            details Map of TradeService
     * @param productRef         ProductId of the Product
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntries_NON_LC_CANCELLATION(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_NON_LC_CANCELLATION");
        //REVERSAL-CONTINGENT-ENTRY

        BigDecimal cancellationAmount = getBigDecimalOrZero(details.get("outstandingAmount"));
        System.out.println("outstandingAmount:" + cancellationAmount);

        String cancellationCurrency = getStringOrReturnEmptyString(details, "currency");
        if (cancellationCurrency == null) {
            cancellationCurrency = "PHP";
            System.out.println("No cancellation currency found defaulting to PHP");
        }

        Map<String, Object> cancellationMap = new HashMap<String, Object>();

        BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));
        String valueName = "cancellationAmount";

        insertValueNameToValueMapUrr(details, cancellationAmount, cancellationCurrency, cancellationMap, urr, valueName);

        System.out.println("cancellationCurrency:" + cancellationCurrency);
        BookCurrency bcCancellationCurrency = determineBookCurrency(cancellationCurrency);

        //REVERSAL-CONTINGENT-ENTRY
        genAccountingEntryLC(tradeService, cancellationMap, productRef, cancellationCurrency, bcCancellationCurrency, cancellationCurrency, bcCancellationCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);

    }

    /**
     * Method used to generate entries for LC Cancellation transactions
     *
     * @param tradeService       TradeService object
     * @param details            details Map of TradeService
     * @param productRef         ProductId of the Product
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntries_LC_CANCELLATION(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_LC_CANCELLATION");
        //ONLY ONE EVENT
        //Reversal of Contingent Entry

        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency;
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            } else if (tradeService.getDetails().containsKey("currency") && tradeService.getDetails().get("currency") != null) {
                lcCurrency = (String) tradeService.getDetails().get("currency");
            } else if (tradeService.getDetails().containsKey("hiddenCurrency") && tradeService.getDetails().get("hiddenCurrency") != null) {
                System.out.println("hiddenCurrency:" + tradeService.getDetails().get("hiddenCurrency"));
                lcCurrency = (String) tradeService.getDetails().get("hiddenCurrency");
            } else {
                //assume PHP
                lcCurrency = "PHP";
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = lcCurrency;
            System.out.println("settlementCurrencyCharges:" + settlementCurrencyCharges);
//            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());


            Map<String, Object> lcMap = new HashMap<String, Object>();

            //Added because outstandingAmount is the remaining contingent entry in the books that need to be reversed
            BigDecimal refundAmount = BigDecimal.ZERO;
            if (details.containsKey("outstandingBalance")) {
                System.out.println("details.get(\"outstandingBalance\"):" + tradeService.getDetails().get("outstandingBalance"));
                refundAmount = getBigDecimalOrZero(tradeService.getDetails().get("outstandingBalance"));
            } else if (details.containsKey("outstandingAmount")) {
                System.out.println("details.get(\"outstandingAmount\"):" + tradeService.getDetails().get("outstandingAmount"));
                refundAmount = getBigDecimalOrZero(tradeService.getDetails().get("outstandingAmount"));
            }
            System.out.println("refundAmount:" + refundAmount);

            BigDecimal urr = getBigDecimalOrZero(tradeService.getDetails().get("USD-PHP_urr"));
            if (urr.compareTo(BigDecimal.ZERO) == 0) {
                //try urr
                urr = getBigDecimalOrZero(tradeService.getDetails().get("urr"));
            }
            if (urr.compareTo(BigDecimal.ZERO) == 0) {
                //try urr
                urr = getBigDecimalOrZero(tradeService.getDetails().get("creationExchangeRateUsdToPHPUrr"));
            }

            System.out.println("urr:" + urr);

            String valueName = "refundAmount";
            insertValueNameToValueMapUrr(details, refundAmount, lcCurrency, lcMap, urr, valueName);

            System.out.println("lcMap:" + lcMap);


            System.out.println("WITHOUT IC NEGO");
            System.out.println("add handling for performance and financial for STANDBY LC");
            System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
            if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                if (details.containsKey("standbyTagging")) {
                    if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                        //REVERSAL-CONTINGENT-ENTRY-PERFORMANCE
                        System.out.println("REVERSAL-CONTINGENT-ENTRY-PERFORMANCE");
                        genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                    } else {
                        //REVERSAL-CONTINGENT-ENTRY-FINANCIAL
                        System.out.println("REVERSAL-CONTINGENT-ENTRY-FINANCIAL");
                        genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                    }
                } else {
                    genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                }
            } else {
                genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param tradeService       TradeService object
     * @param details            details Map of TradeService
     * @param paymentProduct     Payment object for Product Charges
     * @param paymentService     Payment object for Service Charges
     * @param productRef         ProductId of the Product
     * @param gltsNumber         current gltsnumber from sequence generator 
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntries_LC_FOREIGN_NEGOTIATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_LC_FOREIGN_NEGOTIATION");
        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";

            if( tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);

            String icNumber = (String) details.get("icNumber");

            BigDecimal negotiationAmount = getBigDecimalOrZero(details.get("negotiationAmount"));
            BigDecimal negotiationAmountForIC = getBigDecimalOrZero(details.get("negotiationAmount"));
            BigDecimal outstandingBalance = getBigDecimalOrZero(details.get("outstandingBalance"));
            BigDecimal outstandingCashAmount = BigDecimal.ZERO;
            BigDecimal cashAmount = BigDecimal.ZERO;
//            BigDecimal originalAmount = getBigDecimalOrZero(details.get("originalAmount"));
            
            if (productRef.getProductId().toString().contains("REGULAR")) {
				cashAmount = getBigDecimalOrZero(details.get("cashAmount"));
				outstandingCashAmount = getBigDecimalOrZero(cashAmount.subtract(negotiationAmount));
				System.out.println("negotiationAmount:" + negotiationAmount);
				System.out.println("outstandingBalance:" + outstandingBalance);
				System.out.println("cashAmount:" + cashAmount);
				System.out.println("outstandingCashAmount:" + outstandingCashAmount);
				
				if(outstandingCashAmount.compareTo(BigDecimal.ZERO) == -1){
					negotiationAmount = getBigDecimalOrZero(outstandingCashAmount.abs());
				}
				outstandingBalance = getBigDecimalOrZero(outstandingBalance.subtract(cashAmount));
			}
            
            String negoCurrency = getStringOrReturnEmptyString(details, "negotiationCurrency");
            if (negoCurrency == null) {
                negoCurrency = "PHP";
                System.out.println("No nego currency found defaulting to PHP");
            }
            BookCurrency bcNegoCurrency = determineBookCurrency(negoCurrency);

            Map<String, Object> negoMapOutstandingBalance = new HashMap<String, Object>();
            Map<String, Object> negoMapProductAmount = new HashMap<String, Object>();
            Map<String, Object> negoMap = new HashMap<String, Object>();

            BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));
            if(urr == null){
                urr = ratesService.getUrrConversionRateToday();
            } else if(BigDecimal.ZERO.compareTo(urr)==0){
                urr = ratesService.getUrrConversionRateToday();
            }


            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMap, urr, "negoAmount");
            System.out.println("foreign negoMap:" + negoMap);

            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMapProductAmount, urr, "negoAmount");
            insertValueNameToValueMapUrr(tradeService, negotiationAmountForIC, negoCurrency, negoMapProductAmount, urr, "negotiationAmountForIC");
            System.out.println("negoMapProductAmount:" + negoMapProductAmount);//This is the nego map based on the product amount

            insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, negoMapOutstandingBalance, urr, "negoAmount");
            System.out.println("negoMapOutstandingBalance:" + negoMapOutstandingBalance);//This is the nego map based on the outstanding amount


            //Settlement of Nego Amount
            if (productRef.getProductId().toString().contains("CASH")) {

                if (details.containsKey("icNumber") && icNumber != null && !icNumber.isEmpty()) {
                    System.out.println("WITH IC NEGO");//REVERSAL-CONTINGENT-ENTRY-WITH-IC-NEGO

//                    if (negotiationAmount.compareTo(outstandingBalance) == 1) {
//                        //used to determine if what should be reverse in the contingent entry is the outstanding amount or the product amount
//                        negoMap = negoMapOutstandingBalance;
//                    } else {
//                        negoMap = negoMapProductAmount;
//                    }

                    genAccountingEntryLC(tradeService, negoMapProductAmount, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-IC-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
                } else {

                }

                //Handling of OVER NEGOTIATION
                //Generate entries of Over Negotiation Payment
                //Goes to Trade Suspense
                if(paymentProduct!=null){
                    generateNegotiationCashOverPayment(tradeService, paymentProduct, productRef, lcCurrency, bcLcCurrency, gltsNumber, tradeServiceStatus);
                }

                //Checking if FC OR RG
                //Generate Accounting Entry Related to payment of cash fxlc
                String accountType = "FCDU";
                if (tradeService.getDetails().containsKey("accountType")) {
                    accountType = tradeService.getDetails().get("accountType").toString();
                } else {
                    //no accountType this means we have to base this on the lc currency
                    if (!lcCurrency.equalsIgnoreCase("PHP")) {
                        accountType = "FCDU";
                    }
                }


                Map<String, Object> negotiationCashMap = new HashMap<String, Object>();
                //outstandingBalanceUSD -> What will be reversed in AP CASH LC Domestic
                //negoAmountTHIRD -> What will be remitted

                if (negotiationAmount.compareTo(outstandingBalance) == -1 || negotiationAmount.compareTo(outstandingBalance) == 0) {
                    //if negotiationAmount is less than outstandingBalance then both sides should be equal
                    insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negotiationCashMap, urr, "negoAmount");
                    insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negotiationCashMap, urr, "outstandingBalance");

                } else {
                    insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negotiationCashMap, urr, "negoAmount");
                    insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, negotiationCashMap, urr, "outstandingBalance");

                }
                System.out.println("negotiationCashMap :" + negotiationCashMap);
                if (accountType.equalsIgnoreCase("FCDU")) {
                    genAccountingEntryLC(tradeService, negotiationCashMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-A/P-CASH-LC-FC"), gltsNumber, tradeServiceStatus);
                } else {
                    genAccountingEntryLC(tradeService, negotiationCashMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-A/P-CASH-LC-RG"), gltsNumber, tradeServiceStatus);
                }

            } else {
                if (details.containsKey("icNumber") && icNumber != null && !icNumber.isEmpty()) {
                    System.out.println("WITH IC NEGO");
                    //REVERSAL-CONTINGENT-ENTRY-WITH-IC-NEGO
//                    if (negotiationAmount.compareTo(outstandingBalance) == 1) {
//                        //used to determine if what should be reverse in the contingent entry is the outstanding amount or the product amount
//                        negoMap = negoMapOutstandingBalance;
//                    } else {
//                        negoMap = negoMapProductAmount;
//                    }
                    genAccountingEntryLC(tradeService, negoMapProductAmount, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-IC-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
                    if(tradeService.isForReinstatement()){
                    	genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
                    }
                } else {
                    //REVERSAL-CONTINGENT-ENTRY
                    System.out.println("WITHOUT IC NEGO");
                    System.out.println("add handling for performance and financial for STANDBY LC");
                    System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));

                    if (negotiationAmount.compareTo(outstandingBalance) == 1) {//used to determine if what should be reverse in the contingent entry is the outstanding amount or the product amount
                        negoMap = negoMapOutstandingBalance;
                    } else {
                        negoMap = negoMapProductAmount;
                    }



                    if(tradeService.isForReinstatement()){
                        System.out.println(" handling for performance and financial for REINSTATEMENT");
                        System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
                        if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                            if (details.containsKey("standbyTagging")) {
                                if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                    //REVERSAL-CONTINGENT-ENTRY-PERFORMANCE
                                    System.out.println("SETUP-CONTINGENT-ENTRY-PERFORMANCE");
                                    genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                                } else {
                                    //REVERSAL-CONTINGENT-ENTRY-FINANCIAL
                                    System.out.println("SETUP-CONTINGENT-ENTRY-FINANCIAL");
                                    genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                                }
                            } else {
                                genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
                        }

                    }

                    if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                        if (details.containsKey("standbyTagging")) {
                            if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                //REVERSAL-CONTINGENT-ENTRY-PERFORMANCE
                                System.out.println("REVERSAL-CONTINGENT-ENTRY-PERFORMANCE");
                                genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                            } else {
                                //REVERSAL-CONTINGENT-ENTRY-FINANCIAL
                                System.out.println("REVERSAL-CONTINGENT-ENTRY-FINANCIAL");
                                genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else if (outstandingCashAmount.compareTo(BigDecimal.ZERO) == -1){
                        genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
                    }

                }

                System.out.println("tradeService.getDocumentSubType1():"+tradeService.getDocumentSubType1());
                System.out.println("tradeService.getDocumentSubType1():"+tradeService.getDocumentSubType2());
                System.out.println("STARTING PAYMENT PRODUCT");
//                if (!DocumentSubType1.REGULAR.equals(tradeService.getDocumentSubType1()) &&  !DocumentSubType2.USANCE.equals(tradeService.getDocumentSubType2()) ) {
                if (!"USANCE".equalsIgnoreCase(tradeService.getDocumentSubType2().toString())) {
                    System.out.println("NO BOOKING OF LOANS FOR REGULAR USANCE");
                    //NO BOOKING OF LOANS FOR REGULAR USANCE
                    System.out.println();

                    String accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR-FC-BOOK";
                    if(productRef.getProductId().toString().equalsIgnoreCase("FX-LC-REGULAR-SIGHT") && cashAmount.compareTo(BigDecimal.ZERO) == 1){
                        System.out.println("cashFlag:"+tradeService.getDetails().get("cashFlag").toString());
                        System.out.println("cashAmount:"+tradeService.getDetails().get("cashAmount"));
                        System.out.println("negotiationAmount:"+tradeService.getDetails().get("negotiationAmount"));
                        System.out.println("overdrawnAmount:"+tradeService.getDetails().get("overdrawnAmount"));

						cashAmount = BigDecimal.ZERO;
						if(tradeService.getDetails().containsKey("cashAmount") && tradeService.getDetails().get("cashAmount") !=null){
							try {
								cashAmount = new BigDecimal(tradeService.getDetails().get("cashAmount").toString().replace(",",""));
							} catch (Exception e){
								cashAmount = BigDecimal.ZERO;
							}
						}
						
                        BigDecimal amountToBeUsed = BigDecimal.ZERO;
						if(tradeService.getDetails().containsKey("negotiationAmount") && tradeService.getDetails().containsKey("cashAmount") &&
								cashAmount.compareTo(BigDecimal.ZERO)==1){
							try{
								BigDecimal tempCashAmount = new  BigDecimal(tradeService.getDetails().get("cashAmount").toString());
								BigDecimal tempNegotiationAmount = new BigDecimal(tradeService.getDetails().get("negotiationAmount").toString());
								if(tempCashAmount.compareTo(tempNegotiationAmount) == 1){
									//Use negotiationAmount
									amountToBeUsed = tempNegotiationAmount;
								} else {
									//Use cashAmount
									amountToBeUsed = tempCashAmount;
								}
							} catch (Exception e){
								e.printStackTrace();
							}

							Map<String, Object> regSightAdjustedWithCashNegoMap = new HashMap<String, Object>();
							insertValueNameToValueMapUrr(tradeService, amountToBeUsed, negoCurrency, regSightAdjustedWithCashNegoMap, urr, "outstandingBalance");
							insertValueNameToValueMapUrr(tradeService, amountToBeUsed, negoCurrency, regSightAdjustedWithCashNegoMap, urr, "settlementTotal");

							System.out.println( "regSightAdjustedWithCashNegoMap:"+regSightAdjustedWithCashNegoMap);
								
							accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR";
							String accountType = "FCDU";
								//USE SMART DEFAULTS
								if(lcCurrency.equalsIgnoreCase("PHP")){
									accountType = "RBU";
								} else {
									accountType = "FCDU";
								}
								if (tradeService.getDetails().containsKey("accountType")) {
									accountType = tradeService.getDetails().get("accountType").toString();
								}
							accountingEventTypeIdString = getBookCodeStringPostFix(accountType, accountingEventTypeIdString);
							System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);
							ProductReference tempProductRef = new ProductReference("FX-LC-CASH",DocumentClass.LC,DocumentType.FOREIGN,DocumentSubType1.CASH,DocumentSubType2.SIGHT);
								if(accountType.equalsIgnoreCase("FCDU")){
									genAccountingEntryLC(tradeService, regSightAdjustedWithCashNegoMap, tempProductRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-A/P-CASH-LC-FC"), gltsNumber, tradeServiceStatus);
								} else {
									genAccountingEntryLC(tradeService, regSightAdjustedWithCashNegoMap, tempProductRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-A/P-CASH-LC-RG"), gltsNumber, tradeServiceStatus);
								}
						}
                    }


                    if (paymentProduct != null) {
                        //Booking is done from payment currency to lc currency
                        System.out.println("Payment of Product Charges Start NEGOTATION");
                        Set<PaymentDetail> temp = paymentProduct.getDetails();
                        for (PaymentDetail paymentDetail : temp) {
                            System.out.println("---------------------------");
                            printPaymentDetails(paymentDetail);

                            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                            placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                            placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency);
                            System.out.println("specificPaymentMap:" + specificPaymentMap);
                            System.out.println("---------------------------");

                            String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                            BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                            accountingEventTypeIdString = getAccountingEventIdStringPaymentOrLoan_FX(paymentDetail);
                            String accountType = "RBU";
                            //USE SMART DEFAULTS
                            if(lcCurrency.equalsIgnoreCase("PHP")){
                                accountType = "RBU";
                            } else {
                                accountType = "FCDU";
                            }
                            if (tradeService.getDetails().containsKey("accountType")) {
                                accountType = tradeService.getDetails().get("accountType").toString();
                            }
                            accountingEventTypeIdString = getBookCodeStringPostFix(accountType, accountingEventTypeIdString);
                            System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);

                            genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                            genAccountingEntryPaymentCharge_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                        }
                    }


                    Map<String, Object> negotiationCashMap = new HashMap<String, Object>();
                    //outstandingBalanceUSD -> What will be reversed in AP CASH LC Domestic
                    //negoAmountTHIRD -> What will be remitted
                    negotiationAmount = getBigDecimalOrZero(details.get("negotiationAmount"));
                    
                    insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negotiationCashMap, urr, "DUEFromFBsettlementTotal");
                    System.out.println("negotiationCashMap:" + negotiationCashMap);
                    //Generate Accounting Entry Related to payment of cash fxlc

                    //ONLY ONCE FOR EVERYTHING
                    genAccountingEntrySettlement_settlement(tradeService, negotiationCashMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                }

            }

            if (paymentService != null) {
                System.out.println("STARTING PAYMENT SERVICE");
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }


            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void genAccountingEntries_LC_DOMESTIC_NEGOTIATION(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, Payment paymentSettlement, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_LC_DOMESTIC_NEGOTIATION");
        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            } else {
                lcCurrency = tradeService.getDetails().get("negotiationCurrency").toString();
            }
            if("".equalsIgnoreCase(lcCurrency)){
                lcCurrency = tradeService.getDetails().get("negotiationCurrency").toString();
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges="PHP";

            System.out.println("settlementCurrencyCharges:" + settlementCurrencyCharges);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "negotiationAmount");
            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, paymentSettlement);
            System.out.println("lcMap:" + lcMap);
            System.out.println("chargeMap:" + chargeMap);

            BigDecimal negotiationAmount = getBigDecimalOrZero(details.get("negotiationAmount"));
            BigDecimal negotiationAmountForIC = getBigDecimalOrZero(details.get("negotiationAmount"));
            BigDecimal outstandingBalance = getBigDecimalOrZero(details.get("outstandingBalance"));
            BigDecimal outstandingCashAmount = BigDecimal.ZERO;
            BigDecimal cashAmount = BigDecimal.ZERO;
            
            if (productRef.getProductId().toString().contains("REGULAR")) {
            	cashAmount = getBigDecimalOrZero(details.get("cashAmount"));
                outstandingCashAmount = getBigDecimalOrZero(cashAmount.subtract(negotiationAmount));
                System.out.println("negotiationAmount:" + negotiationAmount);
                System.out.println("outstandingBalance:" + outstandingBalance);
                System.out.println("cashAmount:" + cashAmount);
                System.out.println("outstandingCashAmount:" + outstandingCashAmount);
                
                if(outstandingCashAmount.compareTo(BigDecimal.ZERO) == -1){
                	negotiationAmount = getBigDecimalOrZero(outstandingCashAmount.abs());
                }
                outstandingBalance = getBigDecimalOrZero(outstandingBalance.subtract(cashAmount));
            }


            String negoCurrency = getStringOrReturnEmptyString(details, "negotiationCurrency");
            if (negoCurrency == null) {
                negoCurrency = "PHP";
                System.out.println("No nego currency found defaulting to PHP");
            }
            BookCurrency bcNegoCurrency = determineBookCurrency(negoCurrency);

            Map<String, Object> negoMapOutstandingBalance = new HashMap<String, Object>();
            Map<String, Object> negoMapProductAmount = new HashMap<String, Object>();
            Map<String, Object> negoMap = new HashMap<String, Object>();

            BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));
            if (urr.compareTo(BigDecimal.ZERO) == 0) {
                urr = getBigDecimalOrZero(details.get("URR"));
            }
            if (urr.compareTo(BigDecimal.ZERO) == 0) {
                urr = getBigDecimalOrZero(details.get("urr"));
            }

            String settlementName = "";
            //Booking is done from payment currency to lc currency
            System.out.println("Settlement to Beneficiary of Product Charges Start NEGOTIATION");
            String settlementBaseName="";
            if (paymentSettlement != null) {
                Set<PaymentDetail> temp = paymentSettlement.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    //NOTE: from FSD only one settlement to beneficiary
                    System.out.println("---------------------------");
                    settlementName = getSettlementNameEBP(paymentDetail.getPaymentInstrumentType().toString());
                    settlementBaseName = paymentDetail.getPaymentInstrumentType().toString();
                    System.out.println("---------------------------");
                }
            }


            insertValueNameToValueMapUrr(details, negotiationAmount, negoCurrency, negoMap, urr, settlementName);
            insertValueNameToValueMapUrr(details, negotiationAmount, negoCurrency, negoMap, urr, "negoAmount");
            insertValueNameToValueMapUrr(tradeService, negotiationAmountForIC, negoCurrency, negoMap, urr, "negotiationAmountForIC");
            System.out.println("negoMap:" + negoMap);


            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMapProductAmount, urr, "negoAmount");
            insertValueNameToValueMapUrr(tradeService, negotiationAmountForIC, negoCurrency, negoMapProductAmount, urr, "negotiationAmountForIC");
            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMapProductAmount, urr, "settlementTotal");
            insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, negoMapProductAmount, urr, settlementName);
            System.out.println("negoMapProductAmount a:" + negoMapProductAmount);//This is the nego map based on the product amount


            insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, negoMapOutstandingBalance, urr, "negoAmount");
            insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, negoMapOutstandingBalance, urr, "settlementTotal");
            insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, negoMapOutstandingBalance, urr, settlementName);
            System.out.println("negoMapOutstandingBalance:" + negoMapOutstandingBalance);//This is the nego map based on the outstanding amount


            if (productRef.getProductId().toString().contains("CASH")) {

                Map<String, Object> cashNegoMap = new HashMap<String, Object>();

                if (negotiationAmount.compareTo(outstandingBalance) == -1 || negotiationAmount.compareTo(outstandingBalance) == 0) {
                    //if negotiationAmount is less than outstandingBalance then both sides should be equal
                    insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, cashNegoMap, urr, settlementName);
                    insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, cashNegoMap, urr, "settlementTotal");
                    insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, cashNegoMap, urr, "negoAmount");

                } else {
                    insertValueNameToValueMapUrr(tradeService, negotiationAmount, negoCurrency, cashNegoMap, urr, settlementName);
                    insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, cashNegoMap, urr, "settlementTotal");
                    insertValueNameToValueMapUrr(tradeService, outstandingBalance, negoCurrency, cashNegoMap, urr, "negoAmount");

                }
                System.out.println("negotiationCashMap :" + cashNegoMap);


                //TODO Handle OVER NEGOTIATION
                //Reversal of A/P-Cash-LC --CASH
                if(negoCurrency.equalsIgnoreCase("USD")){
                    System.out.println("angulo angulo USD:"+ settlementBaseName);
                     if(settlementBaseName.equalsIgnoreCase("CASA")){
                         genAccountingEntryLC(tradeService, cashNegoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-AP-CASH-LC-FC-BOOK"), gltsNumber, tradeServiceStatus);
                     } else {
                         genAccountingEntryLC(tradeService, cashNegoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-AP-CASH-LC-RG-BOOK"), gltsNumber, tradeServiceStatus);
                     }
                } else {
                    genAccountingEntryLC(tradeService, cashNegoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-AP-CASH-LC"), gltsNumber, tradeServiceStatus);
                }

                //Handle payment for over nego here.
                if(paymentProduct!=null){
                    generateOpeningCashOverPayment(tradeService, paymentProduct, productRef, lcCurrency, bcLcCurrency, gltsNumber, tradeServiceStatus);
                }
                
                // IC COntingent reversesal
                if (tradeService.getDetails().containsKey("icNumber") && ((tradeService.getDetails().get("icNumber") != null && !tradeService.getDetails().get("icNumber").toString().equals("")))) {
                	genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcLcCurrency, negoCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-IC-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
          	
                }
                
            } else {

                if(tradeService.isForReinstatement()){
                    System.out.println(" handling for performance and financial for REINSTATEMENT");
                    System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
                    if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                        if (details.containsKey("standbyTagging")) {
                            if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                //REVERSAL-CONTINGENT-ENTRY-PERFORMANCE
                                System.out.println("SETUP-CONTINGENT-ENTRY-PERFORMANCE");
                                genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                            } else {
                                //REVERSAL-CONTINGENT-ENTRY-FINANCIAL
                                System.out.println("SETUP-CONTINGENT-ENTRY-FINANCIAL");
                                genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
                    }

                }

                try {
                	
	                System.out.println(" handling for performance and financial for STANDBY LC");
	                System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
	                
	                double osBalance = Double.parseDouble(details.get("outstandingBalance").toString());
	            	double negoAmount = Double.parseDouble(details.get("negotiationAmount").toString());
	            	String revEntry;
	            	System.out.println("***outstandingBalance >>>>> " + details.get("outstandingBalance"));
	            	System.out.println("***negotiationAmount >>>>> " + details.get("negotiationAmount"));
	            	
	                // IC COntingent reversesal
	                if (tradeService.getDetails().containsKey("icNumber") && ((tradeService.getDetails().get("icNumber") != null && !tradeService.getDetails().get("icNumber").toString().equals("")))) {
	                	genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcLcCurrency, negoCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-IC-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
              	
	                } else if(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
	                    if (details.containsKey("standbyTagging")) {
	                        if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
	                            //REVERSAL-CONTINGENT-ENTRY-PERFORMANCE
	                            revEntry = "REVERSAL-CONTINGENT-ENTRY-PERFORMANCE";
	                        	System.out.println(revEntry);
	                        	//OLD without any condition
	                            //genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-PERFORMANCE"), gltsNumber, tradeServiceStatus);
	                        } else {
	                            //REVERSAL-CONTINGENT-ENTRY-FINANCIAL
	                        	revEntry = "REVERSAL-CONTINGENT-ENTRY-FINANCIAL";
	                            System.out.println(revEntry);
	                            //OLD without any condition
	                            //genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
	                        }
	                        
	                        //Condition for Over Nego
	                        if(osBalance >= negoAmount) {
	                    		System.out.println("os >= na ---> " + revEntry);
	                    		genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId(revEntry), gltsNumber, tradeServiceStatus);
	                       	} else if(osBalance < negoAmount) {
	                       		System.out.println("os < na ---> " + revEntry);
	                    		genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId(revEntry), gltsNumber, tradeServiceStatus);
	                        }
	
	                    } else {
	                    	genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-FINANCIAL"), gltsNumber, tradeServiceStatus);
	                    }
	                } else {
	                	if (outstandingCashAmount.compareTo(BigDecimal.ZERO) == -1){
	                    	if(osBalance >= negoAmount) {
	                    		System.out.println("os >= na");
	                    		genAccountingEntryLC(tradeService, negoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
	                       	} else if(osBalance < negoAmount) {
	                       		System.out.println("os < na");
	                    		genAccountingEntryLC(tradeService, negoMapOutstandingBalance, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
	                        }
	                	}
	                }
                	
                } catch (Exception e) {
                	e.printStackTrace();
            	}

                if (!productRef.getProductId().toString().contains("USANCE")) {//Settlement of Nego Amount is NOT applicable to USANCE
                    if (paymentProduct != null) {
                        generateAccountingEntryForDomesticPayment(tradeService, paymentProduct, productRef, lcCurrency, bcLcCurrency, gltsNumber, tradeServiceStatus, paymentSettlement );
                    }

                    System.out.println("productId:"+productRef.getProductId().toString());
                    //This section handles the code for Regular Sight LC that has been partially settled/adjusted which means they have a cashAmount
                    //cashAmount is the amount that will be used to partially pay the negotiation payment
                    if(productRef.getProductId().toString().equalsIgnoreCase("DM-LC-REGULAR-SIGHT") && cashAmount.compareTo(BigDecimal.ZERO) == 1){
                        System.out.println("cashFlag:"+tradeService.getDetails().get("cashFlag").toString());
                        System.out.println("cashAmount:"+tradeService.getDetails().get("cashAmount"));
                        System.out.println("negotiationAmount:"+tradeService.getDetails().get("negotiationAmount"));
                        System.out.println("overdrawnAmount:"+tradeService.getDetails().get("overdrawnAmount"));

                        cashAmount = BigDecimal.ZERO;
                        if(tradeService.getDetails().containsKey("cashAmount") && tradeService.getDetails().get("cashAmount") !=null){
                            try {
                                cashAmount = new BigDecimal(tradeService.getDetails().get("cashAmount").toString().replace(",",""));
                            } catch (Exception e){
                                cashAmount = BigDecimal.ZERO;
                            }
                        }

                        BigDecimal amountToBeUsed = BigDecimal.ZERO;
                        BigDecimal originalNegotiationAmount = BigDecimal.ZERO;
                        if(tradeService.getDetails().containsKey("negotiationAmount") && tradeService.getDetails().containsKey("cashAmount") &&
                                cashAmount.compareTo(BigDecimal.ZERO)==1){
                            try{
                                BigDecimal tempCashAmount = new  BigDecimal(tradeService.getDetails().get("cashAmount").toString());
                                BigDecimal tempNegotiationAmount = new BigDecimal(tradeService.getDetails().get("negotiationAmount").toString());
                                if(tempCashAmount.compareTo(tempNegotiationAmount) == 1){
                                    //Use negotiationAmount
                                    amountToBeUsed = tempNegotiationAmount;
                                } else {
                                    //Use cashAmount
                                    amountToBeUsed = tempCashAmount;
                                }
                                originalNegotiationAmount = tempNegotiationAmount;
                            } catch (Exception e){
                                e.printStackTrace();
                            }


                            Map<String, Object> regSightAdjustedWithCashNegoMap = new HashMap<String, Object>();
                            insertValueNameToValueMapUrr(tradeService, originalNegotiationAmount, negoCurrency, regSightAdjustedWithCashNegoMap, urr, settlementName);
                            insertValueNameToValueMapUrr(tradeService, amountToBeUsed, negoCurrency, regSightAdjustedWithCashNegoMap, urr, "negoAmount");
                            insertValueNameToValueMapUrr(tradeService, originalNegotiationAmount, negoCurrency, regSightAdjustedWithCashNegoMap, urr, "settlementTotal");

                            System.out.println( "regSightAdjustedWithCashNegoMap:"+regSightAdjustedWithCashNegoMap);
                            //TODO Handle have to handle thirds/usd for this case
                            //Reversal of A/P-Cash-LC --CASH
//                            genAccountingEntryLC_charges(tradeService, regSightAdjustedWithCashNegoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-AP-CASH-LC"), gltsNumber, tradeServiceStatus);
                            if(negoCurrency.equalsIgnoreCase("USD")){
                                System.out.println("angulo angulo USD:"+ settlementBaseName);
                                if(settlementBaseName.equalsIgnoreCase("CASA")){
                                    genAccountingEntryLC(tradeService, regSightAdjustedWithCashNegoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-AP-CASH-LC-FC-BOOK"), gltsNumber, tradeServiceStatus);
                                } else {
                                    genAccountingEntryLC(tradeService, regSightAdjustedWithCashNegoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-AP-CASH-LC-RG-BOOK"), gltsNumber, tradeServiceStatus);
                                }
                            } else {
                                genAccountingEntryLC(tradeService, regSightAdjustedWithCashNegoMap, productRef, negoCurrency, bcNegoCurrency, negoCurrency, bcNegoCurrency, new AccountingEventTransactionId("REVERSAL-AP-CASH-LC"), gltsNumber, tradeServiceStatus);
                            }
                        }
                    } else if (paymentSettlement != null && paymentProduct != null) {
                    	System.out.println("generateAccountingEntryForDomesticSettlement:"+productRef.getProductId().toString());
                        generateAccountingEntryForDomesticSettlement(tradeService, paymentSettlement, productRef, lcCurrency, bcLcCurrency, gltsNumber, tradeServiceStatus, paymentProduct);
                    }

                } else { //Regular Usance Amount

                }
                
            }
     
            
            //NOTE::FOR DOMESTIC NEGOTIATION THERE WILL ONLY BE CHARGES IF SETTLEMENT TO BENE IS SWIFT OR PDDTS
            String withCharge ="";


            System.out.println("paymentSettlement!=null:"+paymentSettlement!=null);
            if(paymentSettlement!=null){

                Set<PaymentDetail> temp = paymentSettlement.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);

                    String paymentSettlementName = getSettlementName(paymentDetail.getPaymentInstrumentType().toString());
                    if(paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.PDDTS) || paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.SWIFT) ){
                        System.out.println("SETTLEMENT EITHER SWIFT or PDDTS with charges");
                        withCharge="Y";
                    } else if(paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE) && productRef.getProductId().toString().equalsIgnoreCase("DM-LC-REGULAR-SIGHT")
                    		&& tradeService.getDetails().containsKey("cashFlag")){
                    	System.out.println("SETTLEMENT REMITTANCE/IBT without charges");
//                    	generateAccountingEntryForDomesticSettlement(tradeService, paymentSettlement, productRef, lcCurrency, bcLcCurrency, gltsNumber, tradeServiceStatus, paymentProduct);
                    }
                    System.out.println("paymentSettlementName:" + paymentSettlementName);

                    System.out.println("---------------------------");


                }
            }

            System.out.println("paymentProduct!=null:"+paymentProduct!=null);
            if(paymentProduct!=null){

                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);

                    String paymentSettlementName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(),paymentDetail.getCurrency());
                    if(paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.TR_LOAN) ||
                            paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.IB_LOAN) ||
                            paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.UA_LOAN)
                            ){
                        System.out.println("PRODUCT PAYMENT LOAN with charges");
                        withCharge="Y";
                    }
                    System.out.println("paymentSettlementName:" + paymentSettlementName);

                    System.out.println("---------------------------");


                }
            }

            System.out.println("withCharge:"+withCharge);
            System.out.println("paymentService!=null:"+paymentService!=null);
            if (paymentService != null && "Y".equalsIgnoreCase(withCharge)) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_LC_ADJUSTMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_LC_ADJUSTMENT");
        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            } else {
                lcCurrency = tradeService.getDetails().get("currency").toString();
            }

            if(lcCurrency!=null && lcCurrency.isEmpty()){
                lcCurrency = "PHP";
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges;
            BookCurrency bcSettlementCurrencyCharges;
            Map<String, Object> chargeMap;

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "adjustmentAmount");
            System.out.println("lcMap:" + lcMap);

              if(tradeService.getDetails().get("settlementCurrency")!=null){
                  settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
              } else{
                  settlementCurrencyCharges = "PHP";
              }

            if(!settlementCurrencyCharges.equalsIgnoreCase("")){
                bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);
                chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);


                if (paymentService != null) {
                    generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
                }
            } else {
                System.out.println("NO SETTLEMENT CURRENCY DEFAULTING ");
            }


            if(tradeService.isForReinstatement()){
                //outstandingBalance

                //System.out.println("generate_IMPORTS_ProductAmount_ValuesMap");
                Map<String, Object> lcReinstatementMap = new HashMap<String, Object>();
                try {

                    BigDecimal specialRateUsdToPhpServiceCharge = BigDecimal.ZERO;

                    if (details.containsKey("USD-PHP_special_rate_charges")) {
                        specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges"));
                    } else if (details.containsKey("USD-PHP_special_rate_cash")) {
                        specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_cash"));
                    } else if (details.containsKey("USD-PHP_text_special_rate")) {
                        specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_text_special_rate"));
                    } else if (details.containsKey("USD-PHP")) {
                        specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP"));
                    }

                    BigDecimal passOnRateUsdToPhpServiceCharge = BigDecimal.ZERO;

                    if (details.containsKey("USD-PHP_pass_on_rate_charges")) {
                        passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges"));
                    } else if (details.containsKey("USD-PHP_pass_on_rate_cash")) {
                        passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_cash"));
                    } else if (details.containsKey("USD-PHP_text_pass_on_rate")) {
                        passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_text_pass_on_rate"));
                    } else if (details.containsKey("USD-PHP")) {
                        passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP"));
                    }

                    BigDecimal specialRateThirdToPhpServiceCharge;
                    BigDecimal passOnRateThirdToPhpServiceCharge;

                    BigDecimal specialRateThirdToUsdServiceCharge = BigDecimal.ZERO;
                    BigDecimal passOnRateThirdToUsdServiceCharge = BigDecimal.ZERO;

                    BigDecimal passOnUrrServiceCharge = BigDecimal.ZERO;
                    if (details.containsKey("USD-PHP_urr")) {
                        passOnUrrServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_urr"));
                    } else if (details.containsKey("urr")) {
                        passOnUrrServiceCharge = getBigDecimalOrZero(details.get("urr"));
                    }

                    if (!(lcCurrency.equalsIgnoreCase("PHP") && !lcCurrency.equalsIgnoreCase("USD"))) {
                        System.out.println("because these values are null extract details map..");

                        passOnRateThirdToPhpServiceCharge = BigDecimal.ZERO;
                        specialRateThirdToPhpServiceCharge = BigDecimal.ZERO;

                        if (details.containsKey(lcCurrency + "-USD_pass_on_rate_charges")) {
                            passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_pass_on_rate_charges"));
                        } else if (details.containsKey(lcCurrency + "-USD_pass_on_rates_cash")) {
                            passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_text_pass_on_rates_cash"));
                        } else if (details.containsKey(lcCurrency + "-USD_text_pass_on_rates")) {
                            passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_text_pass_on_rates"));
                        } else if (details.containsKey(lcCurrency + "-USD")) {
                            passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD"));
                        }

                        if (details.containsKey(lcCurrency + "-USD_special_rate_charges")) {
                            specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_special_rate_charges"));
                        } else if (details.containsKey(lcCurrency + "-USD_special_rate_cash")) {
                            specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_special_rate_cash"));
                        } else if (details.containsKey(lcCurrency + "-USD_text_special_rates")) {
                            passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_text_special_rates"));
                        } else if (details.containsKey(lcCurrency + "-USD")) {
                            specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD"));
                        } else {
                            specialRateThirdToUsdServiceCharge = ratesService.getAngolConversionRate(lcCurrency,"USD",2);
                            passOnRateThirdToUsdServiceCharge = ratesService.getAngolConversionRate(lcCurrency,"USD",2);
                        }


//                        if(specialRateThirdToUsdServiceCharge==null || specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO)==0){
//                            if(!lcCurrency.equalsIgnoreCase("PHP") && !lcCurrency.equalsIgnoreCase("USD")){
//                                specialRateThirdToUsdServiceCharge = ratesService.getAngolConversionRate(lcCurrency,"USD",2);
//                            }
//                        }
//
//                        if(passOnRateThirdToUsdServiceCharge==null ||passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO)==0){
//                            if(!lcCurrency.equalsIgnoreCase("PHP") && !lcCurrency.equalsIgnoreCase("USD")){
//                                passOnRateThirdToUsdServiceCharge = ratesService.getAngolConversionRate(lcCurrency,"USD",2);
//                            }
//                        }

                    } else {

                        passOnRateThirdToPhpServiceCharge = BigDecimal.ZERO;
                        specialRateThirdToPhpServiceCharge = BigDecimal.ZERO;

                        passOnRateThirdToUsdServiceCharge = BigDecimal.ZERO;
                        specialRateThirdToUsdServiceCharge = BigDecimal.ZERO;

                    }

                    if(specialRateThirdToUsdServiceCharge==null || specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO)==0){
                        if(!lcCurrency.equalsIgnoreCase("PHP") && !lcCurrency.equalsIgnoreCase("USD")){
                            specialRateThirdToUsdServiceCharge = ratesService.getAngolConversionRate(lcCurrency,"USD",2);
                        }
                    }

                    if(passOnRateThirdToUsdServiceCharge==null ||passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO)==0){
                        if(!lcCurrency.equalsIgnoreCase("PHP") && !lcCurrency.equalsIgnoreCase("USD")){
                            passOnRateThirdToUsdServiceCharge = ratesService.getAngolConversionRate(lcCurrency,"USD",2);
                        }
                    }


                    System.out.println("passOnRateThirdToPhpServiceCharge:" + passOnRateThirdToPhpServiceCharge);
                    System.out.println("specialRateThirdToPhpServiceCharge:" + specialRateThirdToPhpServiceCharge);
                    System.out.println("passOnRateThirdToUsdServiceCharge:" + passOnRateThirdToUsdServiceCharge);
                    System.out.println("specialRateThirdToUsdServiceCharge:" + specialRateThirdToUsdServiceCharge);
                    System.out.println("passOnRateUsdToPhpServiceCharge:" + passOnRateUsdToPhpServiceCharge);
                    System.out.println("specialRateUsdToPhpServiceCharge:" + specialRateUsdToPhpServiceCharge);
                    System.out.println("passOnUrrServiceCharge:" + passOnUrrServiceCharge);


                //Map<String, Object> lcReinstatementMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "adjustmentAmount");

                    String outstandingBalanceString = details.get("outstandingBalance").toString().replace(",","");
                    BigDecimal outstandingBalanceBeforeReinstate = new BigDecimal(outstandingBalanceString);

                    //Convert to produce the three lcAmount
                    if (lcCurrency.equalsIgnoreCase("PHP")) {
                        lcReinstatementMap.put("adjustmentAmount" + "PHP", outstandingBalanceBeforeReinstate);
                        if (specialRateUsdToPhpServiceCharge != null && specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "USD", outstandingBalanceBeforeReinstate.divide(specialRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                        } else if (passOnRateUsdToPhpServiceCharge != null && passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "USD", outstandingBalanceBeforeReinstate.divide(passOnRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                        }

                        if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0 &&
                                passOnUrrServiceCharge != null && passOnUrrServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "THIRD", outstandingBalanceBeforeReinstate.divide(specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge), 2, BigDecimal.ROUND_HALF_UP));
                        } else if (passOnRateThirdToUsdServiceCharge != null && passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0 &&
                                passOnUrrServiceCharge != null && passOnUrrServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "THIRD", outstandingBalanceBeforeReinstate.divide(passOnRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    } else if (lcCurrency.equalsIgnoreCase("USD")) {
                        lcReinstatementMap.put("adjustmentAmount" + "USD", outstandingBalanceBeforeReinstate);

                        lcReinstatementMap.put("adjustmentAmount" + "PHP", outstandingBalanceBeforeReinstate.multiply(passOnUrrServiceCharge).setScale(2, BigDecimal.ROUND_HALF_UP));

                        if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0 &&
                                passOnUrrServiceCharge != null && passOnUrrServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "THIRD", outstandingBalanceBeforeReinstate.divide(specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge), 2, BigDecimal.ROUND_HALF_UP));
                        } else if (passOnRateThirdToUsdServiceCharge != null && passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0 &&
                                passOnUrrServiceCharge != null && passOnUrrServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "THIRD", outstandingBalanceBeforeReinstate.divide(passOnRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    } else {
                        lcReinstatementMap.put("adjustmentAmount" + "THIRD", outstandingBalanceBeforeReinstate);

                        if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "USD", outstandingBalanceBeforeReinstate.multiply(specialRateThirdToUsdServiceCharge).setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (passOnRateThirdToUsdServiceCharge != null && passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "USD", outstandingBalanceBeforeReinstate.multiply(passOnRateThirdToUsdServiceCharge).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }


                        if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "PHP", outstandingBalanceBeforeReinstate.multiply(specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge)).setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (passOnRateThirdToUsdServiceCharge != null && passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                            lcReinstatementMap.put("adjustmentAmount" + "PHP", outstandingBalanceBeforeReinstate.multiply(passOnRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge)).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }

                    }

                    System.out.println("lcReinstatementMap:"+lcReinstatementMap);

                        //ADJUSTMENT-CL-SETUP
                if( tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR)&& tradeService.getDocumentSubType2().equals(DocumentSubType2.USANCE)){
                    genAccountingEntryLC(tradeService, lcReinstatementMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("ADJUSTMENT-CL-SETUP"), gltsNumber, tradeServiceStatus);
                } else if(tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR)&& tradeService.getDocumentSubType2().equals(DocumentSubType2.SIGHT)){
                    genAccountingEntryLC(tradeService, lcReinstatementMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("ADJUSTMENT-CL-SETUP"), gltsNumber, tradeServiceStatus);
                } else if(tradeService.getDocumentSubType1().equals(DocumentSubType1.STANDBY)&& tradeService.getDocumentSubType2().equals(DocumentSubType2.SIGHT)){
                    genAccountingEntryLC(tradeService, lcReinstatementMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("ADJUSTMENT-CL-SETUP"), gltsNumber, tradeServiceStatus);
                }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
            
//            adjustment Standby Tagging
            	Boolean isThisStandby = productRef.getProductId().toString().contains("STANDBY");
            // Start ER# 20160523-117 - Modify the checking to create AE for thirds

            if (isThisStandby) {
				String tagAdjusted = tradeService.getDetails().get("standbyTaggingAdjustment").toString();
    			String standbyTagging = tradeService.getDetails().get("standbyTagging").toString();
    			String originalTagging = tradeService.getDetails().get("standbyTaggingOriginalValue").toString();
    			String outstandingBalanceStandby = details.get("outstandingBalance").toString().replace(",", "");
    			String outstandingBalanceStandbyPeso = details.get("outstandingBalance").toString().replace(",", "");
    			Map<String, Object> outStandingBalanceMap = new HashMap<String, Object>();
    			String cur = tradeService.getDetails().get("currency").toString().toUpperCase();
    			System.out.println("\n\n/n/n" +cur+"/n/n\n\n");
        			
    			if (!tradeService.getDetails().get("currency").toString().equalsIgnoreCase("PHP") && !tradeService.getDetails().get("currency").toString().equalsIgnoreCase("USD")) {
    				cur="THIRD";
    			}
        			
				if(tagAdjusted.equalsIgnoreCase("YES")) {
					
    				try {
    					System.out.println("\n\n/n/n" +cur+"/n/n\n\n");

    					if (!cur.equalsIgnoreCase("PHP")) {
    						System.out.println(">>>" +outstandingBalanceStandbyPeso+"<<<");
    						outStandingBalanceMap.put("adjustmentAmount" + cur ,outstandingBalanceStandby);
    						outStandingBalanceMap.put("urrRate" ,tradeService.getDetails().get("urr").toString().replace(",", ""));
    						BigDecimal urr = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get("urr").toString().replace(",", "")));
    						BigDecimal tempPeso = urr.multiply(BigDecimal.valueOf(Double.parseDouble(outstandingBalanceStandby)));
    						
    						if (cur.equalsIgnoreCase("THIRD")) {
    							System.out.println(">>>This is Third<<<");
    							if (details.containsKey(lcCurrency + "-USD")) {
    								System.out.println(">>>specialRateThirdToUsdServiceCharge<<<");
    								//getBigDecimalOrZero(details.get(lcCurrency + "-USD_special_rate_charges"));
    								BigDecimal toUsdRate = getBigDecimalOrZero(details.get(lcCurrency + "-USD"));
    								tempPeso = tempPeso.multiply(toUsdRate);
    								System.out.println(">>>" +tempPeso+"<<<");
    							}
    						}       		
    						//double urr = Double.parseDouble(tradeService.getDetails().get("urr").toString().replace(",", ""));
    						//double tempPeso = Math.round(urr * Double.parseDouble(outstandingBalanceStandby));
    						outstandingBalanceStandbyPeso = tempPeso.toString();
    						System.out.println("\n\n/n/n" +outstandingBalanceStandbyPeso+"/n/n\n\n");
    						System.out.println("\n\n/n/n" +urr+"/n/n\n\n");
    						System.out.println("\n\n/n/n" +outstandingBalanceStandby+"/n/n\n\n");
    						outStandingBalanceMap.put("adjustmentAmountPHP", outstandingBalanceStandbyPeso);        						
    						
    					} else {
       						outStandingBalanceMap.put("adjustmentAmount" + "PHP" ,outstandingBalanceStandby);
       					}

    					// adjust from PERFORMANCE to FINANCIAL
    					if (standbyTagging.equalsIgnoreCase("FINANCIAL")) {
    						genAccountingEntryLC(tradeService,outStandingBalanceMap, productRef, lcCurrency,bcLcCurrency, lcCurrency, bcLcCurrency,new AccountingEventTransactionId("ADJUSTMENT-CL-REVERSAL-PER"), gltsNumber,tradeServiceStatus);
    						genAccountingEntryLC(tradeService,outStandingBalanceMap, productRef, lcCurrency,bcLcCurrency, lcCurrency, bcLcCurrency,new AccountingEventTransactionId("ADJUSTMENT-CL-SETUP-FIN"), gltsNumber,tradeServiceStatus);


    						// adjust from Financial to Performance
    					} else {
    						genAccountingEntryLC(tradeService,outStandingBalanceMap, productRef, lcCurrency,bcLcCurrency, lcCurrency, bcLcCurrency,new AccountingEventTransactionId("ADJUSTMENT-CL-REVERSAL-FIN"), gltsNumber,tradeServiceStatus);
    						genAccountingEntryLC(tradeService,outStandingBalanceMap, productRef, lcCurrency,bcLcCurrency, lcCurrency, bcLcCurrency,new AccountingEventTransactionId("ADJUSTMENT-CL-SETUP-PER"), gltsNumber,tradeServiceStatus);	
    					}

    				} catch (Exception e) {
    					e.printStackTrace();
    				}
				}
    				
			}

			// End ER# 20160523-117.
						
			//Regular to CASH
            String accountingEventTypeIdString = "ADJUSTMENT-PAYMENT-CASH";
            //int counter = 0;

            if (paymentProduct != null) {

                //ADJUSTMENT-CL-REVERSAL
                if( tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR)&& tradeService.getDocumentSubType2().equals(DocumentSubType2.USANCE)){
                    genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("ADJUSTMENT-CL-REVERSAL"), gltsNumber, tradeServiceStatus);
                } else if(tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR)&& tradeService.getDocumentSubType2().equals(DocumentSubType2.SIGHT)){
                    genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("ADJUSTMENT-CL-REVERSAL"), gltsNumber, tradeServiceStatus);
                }


                //Booking is done from payment currency to lc currency
                System.out.println("Payment of Product Charges Start");
                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);

                    Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                    placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                    placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                    placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency);
                    System.out.println("specificPaymentMap:" + specificPaymentMap);
                    System.out.println("---------------------------");

                    String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    System.out.println("paymentSettlementCurrency:" + paymentSettlementCurrency);
                    BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                    System.out.println("payBookCurrency:" + payBookCurrency);

                    genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);

//                    if (counter == 0) {
//                        counter++;
//                        //ONLY ONCE FOR EVERYTHING
//
//                    }
                }
                genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency,  lcCurrency, bcLcCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
            }

            //Generate Accounting Entry Related to payment of cash fxlc
            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void genAccountingEntries_LC_FOREIGN_AMENDMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_LC_FOREIGN_AMENDMENT");
        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = tradeService.getDetails().get("currency").toString();
            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getServiceChargeCurrency()!=null){
                settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
            }
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "amendmentAmount");
            System.out.println("lcMap:" + lcMap);

            Map<String, Object> amendMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "amendmentAmount");
            System.out.println("amendMap:" + amendMap);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);
            System.out.println("chargeMap:" + chargeMap);


            if (productRef.getProductId().toString().contains("CASH")) {

                String amountSwitch = getStringOrReturnEmptyString(tradeService.getDetails(), "amountSwitch"); //on off
                String lcAmountFlag = getStringOrReturnEmptyString(tradeService.getDetails(), "lcAmountFlag"); // INC and DEC
                String currency = getStringOrReturnEmptyString(tradeService.getDetails(), "currency"); // INC and DEC
                String creationExchangeRateUsdToPHPUrrStr = getStringOrReturnEmptyString(tradeService.getDetails(), "creationExchangeRateUsdToPHPUrr");//TODO: REDO THIS

                BigDecimal amendmentAmount;
                BigDecimal urr = parseOrReturnZero(creationExchangeRateUsdToPHPUrrStr);

                //Check if Increase
                if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("INC")) {
                    String paymentSettlementCurrency = "";
                    BookCurrency payBookCurrency = null;

                    if (paymentProduct != null) {
                        //Booking is done from payment currency to lc currency
                        System.out.println("Payment of Product Charges Start");
                        Set<PaymentDetail> temp = paymentProduct.getDetails();
                        for (PaymentDetail paymentDetail : temp) {
                            System.out.println("---------------------------");
                            printPaymentDetails(paymentDetail);

                            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                            placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                            placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency);
                            System.out.println("specificPaymentMap:" + specificPaymentMap);
                            System.out.println("---------------------------");

                            paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                            payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

                            genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("AMENDMENT-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);
                        }
                    }

                    //Generate Accounting Entry Related to payment of cash fxlc
                    genAccountingEntryCharge(tradeService, chargeMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("AMENDMENT-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);

                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom");
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                    amendmentAmount = amountTo.subtract(amountFrom);
                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "lcAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);
                    System.out.println("amendMap:" + amendMap);

                    //ONLY ONCE FOR EVERYTHING
                    genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("AMENDMENT-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);
                } else if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("DEC")) {
                    //Check if Decrease
                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom");
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                    amendmentAmount = amountFrom.subtract(amountTo);
                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "lcAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);
                    System.out.println("amendMap:" + amendMap);
                    //ONLY ONCE FOR EVERYTHING
                    genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-PAYMENT-CASH-DECREASE-FX"), gltsNumber, tradeServiceStatus);
                }
            } else {


                //Generate Accounting Entry Related to setup of contingent liability

                //String expiryDateSwitch = getStringOrReturnEmptyString(tradeService.getDetails(),"expiryDateSwitch"); //on off
                String amountSwitch = getStringOrReturnEmptyString(tradeService.getDetails(), "amountSwitch"); //on off
                String tenorSwitch = getStringOrReturnEmptyString(tradeService.getDetails(), "tenorSwitch"); //on off
                String lcAmountFlag = getStringOrReturnEmptyString(tradeService.getDetails(), "lcAmountFlag"); // INC and DEC
                String currency = getStringOrReturnEmptyString(tradeService.getDetails(), "currency"); // INC and DEC
                String creationExchangeRateUsdToPHPUrrStr = getStringOrReturnEmptyString(tradeService.getDetails(), "creationExchangeRateUsdToPHPUrr");//TODO: REDO THIS
                //String expiryDateFlag = getStringOrReturnEmptyString(tradeService.getDetails(),"expiryDateFlag"); // EXT and RED

                BigDecimal amendmentAmount;
                BigDecimal urr = parseOrReturnZero(creationExchangeRateUsdToPHPUrrStr);


                System.out.println("Before reinstatement:");
                if(tradeService.isForReinstatement() || tradeService.isForReinstatement(getStringOrReturnEmptyString(tradeService.getDetails(), "expiryDate"))){
                    System.out.println("For reinstatement:");
                    String valueNameReinstate = "amendmentAmount";
                    Map<String, Object> amendReinstateMap = new HashMap<String, Object>();
                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amount");
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);
                    BigDecimal amendmentAmountReinstate = amountFrom;

                    insertValueNameToValueMapUrr(details, amendmentAmountReinstate, currency, amendReinstateMap, urr, valueNameReinstate);


                    if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                        if (details.containsKey("standbyTagging")) {
                            if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                //OPENING-CL-SETUP-PERFORMANCE
                                System.out.println("AMENDMENT-CL-SETUP-PERFORMANCE");
                                genAccountingEntryLC(tradeService, amendReinstateMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                            } else {
                                //OPENING-CL-SETUP-FINANCIAL
                                System.out.println("AMENDMENT-CL-SETUP-FINANCIAL");
                                genAccountingEntryLC(tradeService, amendReinstateMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, amendReinstateMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, amendReinstateMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP"), gltsNumber, tradeServiceStatus);
                    }

                }



                //Check if Increase
                if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("INC")) {
                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom"); // Revised 9/26/2016 
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                    amendmentAmount = amountTo.subtract(amountFrom);

                    System.out.println("amendmentAmount:" + amendmentAmount);	
                    System.out.println("currency:" + currency);
                    String valueName = "amendmentAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);

                    System.out.println("added handling for performance and financial for STANDBY LC");//"AMENDMENT-CL-SETUP"
                    System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
                    if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                        if (details.containsKey("standbyTagging")) {
                            if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                //OPENING-CL-SETUP-PERFORMANCE
                                System.out.println("AMENDMENT-CL-SETUP-PERFORMANCE");
                                genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                            } else {
                                //OPENING-CL-SETUP-FINANCIAL
                                System.out.println("AMENDMENT-CL-SETUP-FINANCIAL");
                                genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP"), gltsNumber, tradeServiceStatus);
                    }
                }

                //Check if Decrease
                if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("DEC")) {
                    System.out.println("amountSwitch.equalsIgnoreCase(\"on\") && lcAmountFlag.equalsIgnoreCase(\"DEC\")");
                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom"); // Revised 9/26/2016 
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);
                    System.out.println("amountFrom:" + amountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                    amendmentAmount = amountFrom.subtract(amountTo);
                    System.out.println("amountTo:" + amountTo);

                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "amendmentAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);

                    System.out.println("added handling for performance and financial for STANDBY LC");//"AMENDMENT-CL-REVERSAL";
                    System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
                    if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                        if (details.containsKey("standbyTagging")) {
                            if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                System.out.println("AMENDMENT-CL-REVERSAL-PERFORMANCE");//OPENING-CL-SETUP-PERFORMANCE
                                genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-REVERSAL-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                            } else {
                                System.out.println("AMENDMENT-CL-REVERSAL-FINANCIAL");//OPENING-CL-SETUP-FINANCIAL
                                genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-REVERSAL-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-REVERSAL-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-REVERSAL"), gltsNumber, tradeServiceStatus);
                    }
                }

                //Check if Change in Tenor from sight to usance
                if (tenorSwitch.equalsIgnoreCase("on")) {
                    //reverse of partial payment amount

                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amount");
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);

                    if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("INC")) {
                        amendmentAmount = amountTo.subtract(amountFrom);
                    } else if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("DEC")) {
                        amendmentAmount = amountFrom.subtract(amountTo);
                    } else {
                        amendmentAmount = amountFrom;
                    }

                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "amendmentAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);
                    System.out.println("amendMap:" + amendMap);

                    genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-REVERSAL-SIGHT-USANCE"), gltsNumber, tradeServiceStatus);
                    genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-CL-SETUP-SIGHT-USANCE"), gltsNumber, tradeServiceStatus);
                }
            }

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_LC_DOMESTIC_AMENDMENT(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_LC_DOMESTIC_AMENDMENT");
        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = tradeService.getDetails().get("currency").toString();
            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Settlement Currency
            String settlementCurrencyCharges = "PHP";
            System.out.println("settlementCurrencyCharges:" + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "amendmentAmount");
            System.out.println("lcMap:" + lcMap);

            Map<String, Object> amendMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "amendmentAmount");
            System.out.println("amendMap:" + amendMap);

            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);
            System.out.println("chargeMap:" + chargeMap);
            
            if (productRef.getProductId().toString().contains("CASH")) {

                String amountSwitch = getStringOrReturnEmptyString(tradeService.getDetails(), "amountSwitch"); //on off
                String lcAmountFlag = getStringOrReturnEmptyString(tradeService.getDetails(), "lcAmountFlag"); // INC and DEC
                String currency = getStringOrReturnEmptyString(tradeService.getDetails(), "currency"); // INC and DEC
                String creationExchangeRateUsdToPHPUrrStr = getStringOrReturnEmptyString(tradeService.getDetails(), "creationExchangeRateUsdToPHPUrr");//TODO: REDO THIS

                BigDecimal amendmentAmount;
                BigDecimal urr = parseOrReturnZero(creationExchangeRateUsdToPHPUrrStr);

                //Check if Increase
                if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("INC")) {

                    if (paymentProduct != null) {
                        //Booking is done from payment currency to lc currency
                        System.out.println("Payment of Product Charges Start");
                        Set<PaymentDetail> temp = paymentProduct.getDetails();
                        for (PaymentDetail paymentDetail : temp) {
                            System.out.println("---------------------------");
                            printPaymentDetails(paymentDetail);

                            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                            placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                            placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency);
                            System.out.println("specificPaymentMap:" + specificPaymentMap);
                            System.out.println("---------------------------");

                            String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                            BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

                            genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("AMENDMENT-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);
                        }
                    }


                    //Generate Accounting Entry Related to payment of cash fxlc
                    genAccountingEntryCharge(tradeService, chargeMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId("AMENDMENT-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);

                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom");
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                    amendmentAmount = amountTo.subtract(amountFrom);
                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "lcAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);

                    System.out.println("amendMap:" + amendMap);
                    //ONLY ONCE FOR EVERYTHING
                    genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId("AMENDMENT-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);
                } else if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("DEC")) {
                    //Check if Decrease

                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom");
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                    amendmentAmount = amountFrom.subtract(amountTo);
                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "lcAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);
                    System.out.println("amendMap:" + amendMap);
                    //ONLY ONCE FOR EVERYTHING
                    genAccountingEntryLC_charges(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("AMENDMENT-PAYMENT-CASH-DECREASE-DM"), gltsNumber, tradeServiceStatus);
                }

            } else {
                //Generate Accounting Entry Related to setup of contingent liability

                //String expiryDateSwitch = getStringOrReturnEmptyString(tradeService.getDetails(),"expiryDateSwitch"); //on off
                String amountSwitch = getStringOrReturnEmptyString(tradeService.getDetails(), "amountSwitch"); //on off
                String tenorSwitch = getStringOrReturnEmptyString(tradeService.getDetails(), "tenorSwitch"); //on off
                String lcAmountFlag = getStringOrReturnEmptyString(tradeService.getDetails(), "lcAmountFlag"); // INC and DEC
                String currency = getStringOrReturnEmptyString(tradeService.getDetails(), "currency"); // INC and DEC
                String creationExchangeRateUsdToPHPUrrStr = getStringOrReturnEmptyString(tradeService.getDetails(), "creationExchangeRateUsdToPHPUrr");//TODO: REDO THIS
                //String expiryDateFlag = getStringOrReturnEmptyString(tradeService.getDetails(),"expiryDateFlag"); // EXT and RED


                BigDecimal amendmentAmount;
                BigDecimal urr = parseOrReturnZero(creationExchangeRateUsdToPHPUrrStr);


                System.out.println("before reinstatement:");
                if(tradeService.isForReinstatement() || tradeService.isForReinstatement(getStringOrReturnEmptyString(tradeService.getDetails(), "expiryDate"))){
                	System.out.println("For Reinstatement");
                	String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amount");
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);
                    Map<String, Object> amendReinstatementMap = new HashMap<String, Object>();

                    BigDecimal amendmentAmountReinstatement = amountFrom;
                    System.out.println("amendmentAmount:" + amendmentAmountReinstatement);
                    System.out.println("currency:" + currency);
                    String valueName = "amendmentAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmountReinstatement, currency, amendReinstatementMap, urr, valueName);



                    if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                        if (details.containsKey("standbyTagging")) {
                            if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                //OPENING-CL-SETUP-PERFORMANCE
                                System.out.println("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-PERFORMANCE");
                                genAccountingEntryLC(tradeService, amendReinstatementMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                            } else {
                                //OPENING-CL-SETUP-FINANCIAL
                                System.out.println("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-FINANCIAL");
                                genAccountingEntryLC(tradeService, amendReinstatementMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, amendReinstatementMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, amendReinstatementMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT"), gltsNumber, tradeServiceStatus);
                    }

                } 


                //Check if Increase
                if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("INC")) {
                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom"); // Revised 9/26/2016 
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                    amendmentAmount = amountTo.subtract(amountFrom);
                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "amendmentAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);
                    System.out.println("amendMap:" + amendMap);

                    //String accEventId = "SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT";
                    System.out.println("added handling for performance and financial for STANDBY LC");
                    System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
                    if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                        if (details.containsKey("standbyTagging")) {
                            if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                //OPENING-CL-SETUP-PERFORMANCE
                                System.out.println("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-PERFORMANCE");
                                genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                            } else {
                                //OPENING-CL-SETUP-FINANCIAL
                                System.out.println("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-FINANCIAL");
                                genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-INCREASE-DMLC-AMOUNT"), gltsNumber, tradeServiceStatus);
                    }
                }
                //Check if Decrease
                if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("DEC")) {
                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amountFrom"); // Revised 9/26/2016 
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                    amendmentAmount = amountFrom.subtract(amountTo);
                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "amendmentAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);
                    System.out.println("amendMap:" + amendMap);

                    //String accEventId = "REVERSAL-CONTINGENT-ENTRY-DECREASE-DMLC-AMOUNT";
                    System.out.println("added handling for performance and financial for STANDBY LC");
                    System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
                    if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                        if (details.containsKey("standbyTagging")) {
                            if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                                //OPENING-CL-SETUP-PERFORMANCE
                                System.out.println("REVERSAL-CONTINGENT-ENTRY-DECREASE-DMLC-AMOUNT-PERFORMANCE");
                                genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-DECREASE-DMLC-AMOUNT-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                            } else {
                                //OPENING-CL-SETUP-FINANCIAL
                                System.out.println("REVERSAL-CONTINGENT-ENTRY-DECREASE-DMLC-AMOUNT-FINANCIAL");
                                genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-DECREASE-DMLC-AMOUNT-FINANCIAL"), gltsNumber, tradeServiceStatus);
                            }
                        } else {
                            genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-DECREASE-DMLC-AMOUNT-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-DECREASE-DMLC-AMOUNT"), gltsNumber, tradeServiceStatus);
                    }
                }

                //Check if Change in Tenor from sight to usance
                if (tenorSwitch.equalsIgnoreCase("on")) {

                    String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amount");
                    BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                    String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                    BigDecimal amountTo = parseOrReturnZero(strAmountTo);

                    if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("INC")) {
                        amendmentAmount = amountTo.subtract(amountFrom);
                    } else if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("DEC")) {
                        amendmentAmount = amountFrom.subtract(amountTo);
                    } else {
                        amendmentAmount = amountFrom;
                    }

                    System.out.println("amendmentAmount:" + amendmentAmount);
                    System.out.println("currency:" + currency);
                    String valueName = "amendmentAmount";
                    insertValueNameToValueMapUrr(details, amendmentAmount, currency, amendMap, urr, valueName);
                    System.out.println("amendMap:" + amendMap);


                    //reverse of partial payment amount
                    genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-CHANGE-TENOR-SIGHT-USANCE"), gltsNumber, tradeServiceStatus);
                    genAccountingEntryLC(tradeService, amendMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-CONTINGENT-ENTRY-CHANGE-TENOR-SIGHT-USANCE"), gltsNumber, tradeServiceStatus);
                }
            }

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    private void genAccountingEntries_LC_NEGOTIATION_DISCREPANCY(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
    	try {
	        BigDecimal negotiationAmount = getBigDecimalOrZero(details.get("negotiationAmount"));
	        BigDecimal negotiationRegularAmount = getBigDecimalOrZero(details.get("negotiationAmount"));
	        BigDecimal outstandingBalance = getBigDecimalOrZero(details.get("outstandingBalance"));
	        
	        DocumentNumber lcNumber = tradeService.getDocumentNumber();
	        LetterOfCredit lc = (LetterOfCredit) tradeProductRepository.load(lcNumber);
	    	BigDecimal totalCashAmount = BigDecimal.ZERO;
	    	
	        try {
		        totalCashAmount = getBigDecimalOrZero(details.get("totalCashAmount"));
	        } catch (Exception e) {
	        	System.out.println("Error on getting totalRegularAmount and totalCashAmount");
	        	e.printStackTrace();	        	
	        }
	        
	        if (!lc.getType().equals(LCType.CASH) && (lc.getCashFlag()!=null && lc.getCashFlag()==true)) {
	        	if (lc.getCashAmount().compareTo(lc.getTotalNegotiatedCashAmount()) == 1) {
	        		BigDecimal outstandingCash = getBigDecimalOrZero(lc.getCashAmount().subtract(lc.getTotalNegotiatedCashAmount()));
	        		outstandingBalance = getBigDecimalOrZero(outstandingBalance.subtract(outstandingCash));
		        	
	        		outstandingCash = getBigDecimalOrZero(outstandingCash.subtract(totalCashAmount));	        		
	        		negotiationRegularAmount = getBigDecimalOrZero(negotiationRegularAmount.subtract(outstandingCash));
	        	}
	        }            
	//        BigDecimal originalAmount = getBigDecimalOrZero(details.get("originalAmount"));
	
	        String negoCurrency = getStringOrReturnEmptyString(details, "negotiationCurrency");
	        if (negoCurrency == null) {
	            negoCurrency = "PHP";
	            System.out.println("No nego currency found defaulting to PHP");
	        }
	
	        Map<String, Object> lcMap = new HashMap<String, Object>();
	        Map<String, Object> osMap = new HashMap<String, Object>();
	        Map<String, Object> regularMap = new HashMap<String, Object>();
	
	//        String conversionString = "";
	        String conversionStringToUSD;
	        BigDecimal THIRD_PHP_conversion = BigDecimal.ZERO;
	        BigDecimal THIRD_USD_conversion;
	        BigDecimal urr = getBigDecimalOrZero(details.get("USD-PHP_urr"));
	
	
	        if (negoCurrency.equalsIgnoreCase("PHP")) {
	            lcMap.put("negoAmountPHP", negotiationAmount);
	            osMap.put("negoAmountPHP", outstandingBalance);
	            regularMap.put("negoAmountPHP", negotiationRegularAmount);
	
	        } else if (negoCurrency.equalsIgnoreCase("USD")) {
	            lcMap.put("negoAmountUSD", negotiationAmount);
	            lcMap.put("negoAmountPHP", negotiationAmount.multiply(urr));
	
	            osMap.put("negoAmountUSD", outstandingBalance);
	            osMap.put("negoAmountPHP", outstandingBalance.multiply(urr));            
	
	            regularMap.put("negoAmountUSD", negotiationRegularAmount);
	            regularMap.put("negoAmountPHP", negotiationRegularAmount.multiply(urr));
	        } else {
	            conversionStringToUSD = negoCurrency + "-USD";
	
	            if (details.containsKey(conversionStringToUSD)) {
	                THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD));
	                THIRD_PHP_conversion = THIRD_USD_conversion.multiply(urr);
	            }
	
	            lcMap.put("negoAmountPHP", negotiationAmount.multiply(THIRD_PHP_conversion));
	            lcMap.put("negoAmountTHIRD", negotiationAmount);
	
	            osMap.put("negoAmountPHP", outstandingBalance.multiply(THIRD_PHP_conversion));
	            osMap.put("negoAmountTHIRD", outstandingBalance);
	            
	            regularMap.put("negoAmountPHP", negotiationRegularAmount.multiply(THIRD_PHP_conversion));
	            regularMap.put("negoAmountTHIRD", negotiationRegularAmount);
	        }
	
	//        System.out.println("lcMap:" + lcMap);
	//        System.out.println("negoCurrency:" + negoCurrency);
	        BookCurrency bcLcCurrency = determineBookCurrency(negoCurrency);
	
	//        System.out.println("NEGOTIATION_DISCREPANCY DOES NOTHING");
	
	        //REVERSAL OF CONTINGENT ENTRY
	        //REVERSAL-CONTINGENT-ENTRY-IC
	        if (negotiationRegularAmount.compareTo(outstandingBalance) < 1) {
	        	genAccountingEntryLC(tradeService, regularMap, productRef, negoCurrency, bcLcCurrency, negoCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-IC"), gltsNumber, tradeServiceStatus);
	        } else {
	        	genAccountingEntryLC(tradeService, osMap, productRef, negoCurrency, bcLcCurrency, negoCurrency, bcLcCurrency, new AccountingEventTransactionId("REVERSAL-CONTINGENT-ENTRY-IC"), gltsNumber, tradeServiceStatus);
	        }
	        
	        //REVERSAL OF CONTINGENT ENTRY
	        genAccountingEntryLC(tradeService, lcMap, productRef, negoCurrency, bcLcCurrency, negoCurrency, bcLcCurrency, new AccountingEventTransactionId("SET-UP-IC-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);

	        if (tradeService.isForReinstatement(getStringOrReturnEmptyString(tradeService.getDetails(), "expiryDate"))) {
	        	if (!getStringOrReturnEmptyString(tradeService.getDetails(), "expiredStatus").equalsIgnoreCase("REINSTATED")) {
	        		genAccountingEntryLC(tradeService, osMap, productRef, negoCurrency, bcLcCurrency, negoCurrency, bcLcCurrency, new AccountingEventTransactionId("SETUP-CONTINGENT-ENTRY"), gltsNumber, tradeServiceStatus);
	        	}	        	
			}
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Transactional
    private void genAccountingEntries_LC_OPENING(TradeService tradeService, Map<String, Object> details, Payment paymentProduct, Payment paymentService, ProductReference productRef, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntries_LC_OPENING");
        try {
            //Create Product/Charges Summary here
            //Get LC Currency
            String lcCurrency = "";
            if (tradeService.getProductChargeCurrency() != null) {
                lcCurrency = tradeService.getProductChargeCurrency().toString();
            }

            System.out.println("lcCurrency:" + lcCurrency);
            BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

            //Get Charges Settlement Currency
            String settlementCurrencyCharges = "";
            if(tradeService.getServiceChargeCurrency()!=null){
                settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
            }
            if(tradeService.getDetails().get("settlementCurrency")!=null){
                settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
            } else{
                settlementCurrencyCharges = "PHP";
            }
            System.out.println("settlementCurrencyCharges:" + lcCurrency);
            BookCurrency bcSettlementCurrencyCharges = determineBookCurrency(settlementCurrencyCharges);

            System.out.println("looking for accounting entries for: " + productRef.getProductId().toString() + " | " + tradeService.getServiceType().toString());

            Boolean cilexCharged = willWeChargeCilex(paymentProduct);
            System.out.println("cilexCharged:"+cilexCharged);
            Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();

            Map<String, Object> lcMap = generate_IMPORTS_ProductAmount_ValuesMap(details, chargesSummaryMap, tradeService, lcCurrency, "lcAmount");
            Map<String, Object> chargeMap = generate_IMPORTS_CHARGES_ValuesMap(details, cilexCharged, chargesSummaryMap, tradeService, settlementCurrencyCharges, null);


            //CASH Payment or Contingent Liability Setup/Reversal Accounting Entries
            if (productRef.getProductId().toString().contains("CASH")) {
                generateOpeningCashPayment(tradeService, paymentProduct, productRef, lcCurrency, bcLcCurrency, lcMap, gltsNumber, tradeServiceStatus);

            } else {
                //Generate Accounting Entry Related to setup of contingent liability
                //"OPENING-CL-SETUP"
//                String accEventId;
                System.out.println("added handling for performance and financial for STANDBY LC");
                System.out.println(DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()));
                if (DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1())) {
                    if (details.containsKey("standbyTagging")) {
                        if (details.get("standbyTagging").toString().equalsIgnoreCase("P") || details.get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                            //OPENING-CL-SETUP-PERFORMANCE
                            System.out.println("OPENING-CL-SETUP-PERFORMANCE");
                            genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("OPENING-CL-SETUP-PERFORMANCE"), gltsNumber, tradeServiceStatus);
                        } else {
                            //OPENING-CL-SETUP-FINANCIAL
                            System.out.println("OPENING-CL-SETUP-FINANCIAL");
                            genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("OPENING-CL-SETUP-FINANCIAL"), gltsNumber, tradeServiceStatus);
                        }
                    } else {
                        genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("OPENING-CL-SETUP-FINANCIAL"), gltsNumber, tradeServiceStatus);
                    }
                } else {
                    genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("OPENING-CL-SETUP"), gltsNumber, tradeServiceStatus);
                }
            }

            if (paymentService != null) {
                generateChargesAndChargesPaymentAccountingEntries(tradeService, paymentService, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, chargeMap, lcMap, gltsNumber, tradeServiceStatus, paymentProduct);
            }

            generateAP_ExcessPaymentsBoth(tradeService, productRef, lcCurrency, bcLcCurrency, paymentProduct, paymentService, gltsNumber, tradeServiceStatus, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Generated accounting entry related to foreign payment
     *
     * @param tradeService       TradeService object
     * @param paymentProduct     Payment object for Product Charges
     * @param productRef         ProductId
     * @param lcCurrency         Currency of the LC as String
     * @param bcLcCurrency       BookCurrency of the LC
     * @param negoMap            map Containing Payment Related values
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    private void generateAccountingEntryForForeignPayment(TradeService tradeService, Payment paymentProduct, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, Map<String, Object> negoMap, String gltsNumber, String tradeServiceStatus) {
        System.out.println("generateAccountingEntryForForeignPayment");
        String accountingEventTypeIdString;
        int counter = 0;

        //accountingEventTypeIdString; = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR";
        if (paymentProduct != null) {
            //Booking is done from payment currency to lc currency
            System.out.println("Payment of Product Charges Start");
            Set<PaymentDetail> temp = paymentProduct.getDetails();

            if (!temp.isEmpty()) {
                counter = temp.size();
            }

//            System.out.println("counter sixe:" + counter);
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);

                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                placeDueFromForeignBankInPaymentMap((BigDecimal) negoMap.get("settlementAmountPHP"), (BigDecimal) negoMap.get("settlementAmountUSD"), (BigDecimal) negoMap.get("settlementAmountTHIRD"), specificPaymentMap);
                placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency);
                System.out.println("specificPaymentMap:" + specificPaymentMap);
                System.out.println("---------------------------");

                String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                accountingEventTypeIdString = getAccountingEventIdStringPaymentOrLoan_FX(paymentDetail);
                String accountType = "RBU";
                if (tradeService.getDetails().containsKey("accountType")) {
                    accountType = tradeService.getDetails().get("accountType").toString();
                } else {//no accountType this means we have to base this on the lc currency
                    if (!lcCurrency.equalsIgnoreCase("PHP")) {
                        accountType = "FCDU";
                    }

                }
                accountingEventTypeIdString = getBookCodeStringPostFix(accountType, accountingEventTypeIdString);
                System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);

                genAccountingEntryPayment_settlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);

                if (counter == 1) {
                    System.out.println("in counter");
                    String settlementPrefix = "settlementAmount";
                    Map<String, Object> specificSettlementMap = new HashMap<String, Object>();
                    BigDecimal dueInPHP = (BigDecimal) negoMap.get(settlementPrefix + "PHP");
                    BigDecimal dueInUSD = (BigDecimal) negoMap.get(settlementPrefix + "USD");
                    BigDecimal dueInTHIRD = (BigDecimal) negoMap.get(settlementPrefix + "THIRD");

                    placeDueFromForeignBankInPaymentMap(dueInPHP, dueInUSD, dueInTHIRD, specificSettlementMap);
                    genAccountingEntrySettlement_settlement(tradeService, specificSettlementMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                }

                --counter;
            }
        }
    }

    /**
     * Generates Excess Payments for Both Service and Product Charges
     *
     * @param tradeService       TradeService object
     * @param productRef         ProductId of the Product
     * @param lcCurrency         BookCurrency of the LC
     * @param bcLcCurrency       BookCurrency of the LC
     * @param paymentProduct     Payment object for Product Charges
     * @param paymentService     Payment object for Service Charges
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    private void generateAP_ExcessPaymentsBoth(TradeService tradeService, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, Payment paymentProduct, Payment paymentService, String gltsNumber, String tradeServiceStatus, Payment paymentSettlement) {
        System.out.println("generateAP_ExcessPayments");
        try {
            System.out.println("Problem with Excess");

            Map<String, Object> specificExcessMapProduct = getExcessPaymentsProductCharge(tradeService, paymentProduct, paymentSettlement);
            System.out.println("paymentProduct:" + paymentProduct);
            System.out.println("specificExcessMapProduct:" + specificExcessMapProduct);

            PaymentDetail paymentDetailProductExcess = null;
            PaymentDetail paymentDetailCheck = null;
            PaymentDetail paymentDetailRemittance = null;

            if (paymentProduct != null) {
                //Booking is done from payment currency to lc currency
                System.out.println("FIND Product Payment with EXCESS Start");
                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    //printPaymentDetails(paymentDetail);
                    if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CHECK)) {
                        paymentDetailCheck = paymentDetail;
                    }

                    if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE)) {
                        paymentDetailRemittance = paymentDetail;
                    }
                    System.out.println("---------------------------");
                }
                if (paymentDetailCheck != null && paymentDetailRemittance != null) {
                    if (paymentDetailCheck.getPaidDate().compareTo(paymentDetailRemittance.getPaidDate()) == 0) {
                        //choose any
                        paymentDetailProductExcess = paymentDetailCheck;
                    } else if (paymentDetailCheck.getPaidDate().compareTo(paymentDetailRemittance.getPaidDate()) == 1) {
                        paymentDetailProductExcess = paymentDetailCheck;
                    } else if (paymentDetailCheck.getPaidDate().compareTo(paymentDetailRemittance.getPaidDate()) == -1) {
                        paymentDetailProductExcess = paymentDetailRemittance;
                    }
                } else {
                    if (paymentDetailCheck != null) {
                        paymentDetailProductExcess = paymentDetailCheck;
                    }
                    if (paymentDetailRemittance != null) {
                        paymentDetailProductExcess = paymentDetailRemittance;
                    }
                }

                if (paymentDetailProductExcess != null) {
                    String paymentSettlementCurrencyProduct = paymentDetailProductExcess.getCurrency().getCurrencyCode();
                    BookCurrency payBookCurrencyProduct = determineBookCurrency(paymentSettlementCurrencyProduct);

                    //RG BOOK: SETTLEMENT-NEGO-AMOUNT-EXCESS-RG-BOOK
                    //FC BOOK: SETTLEMENT-NEGO-AMOUNT-EXCESS-FC-BOOK

                    AccountingEventTransactionId accountingEventTransactionId = new AccountingEventTransactionId("SETTLEMENT-NEGO-AMOUNT-EXCESS-RG-BOOK");
                    String accountType;
                    if (tradeService.getDetails().containsKey("accountType")) {
                        accountType = tradeService.getDetails().get("accountType").toString();
                        if ("RBU".equalsIgnoreCase(accountType)) {
                            accountingEventTransactionId = new AccountingEventTransactionId("SETTLEMENT-NEGO-AMOUNT-EXCESS-RG-BOOK");
                        } else {
                            accountingEventTransactionId = new AccountingEventTransactionId("SETTLEMENT-NEGO-AMOUNT-EXCESS-FC-BOOK");
                        }
                    } else {//no accountType this means we have to base this on the lc currency
                        if (!lcCurrency.equalsIgnoreCase("PHP")) {
                            //accountType = "FCDU";
                            accountingEventTransactionId = new AccountingEventTransactionId("SETTLEMENT-NEGO-AMOUNT-EXCESS-FC-BOOK");
                        }
                    }
                    System.out.println("Product Charge Excess accountingEventTransactionId:" + accountingEventTransactionId);
                    genAccountingEntryRefund_charges(tradeService, specificExcessMapProduct, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrencyProduct, payBookCurrencyProduct, accountingEventTransactionId, gltsNumber, tradeServiceStatus);
                }

            }


            Map<String, Object> specificExcessMapCharge = getExcessPaymentsServiceCharge(tradeService, paymentService, paymentProduct, paymentSettlement);
            System.out.println("paymentService:" + paymentService);
            System.out.println("specificExcessMapCharge:" + specificExcessMapCharge);

            PaymentDetail paymentDetailServiceExcess = null;

            if (paymentService != null) {
                //Booking is done from payment currency to lc currency
                System.out.println("FIND Service Payment with EXCESS Start");
                Set<PaymentDetail> temp = paymentService.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);
                    if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CHECK)) {
                        paymentDetailCheck = paymentDetail;
                        System.out.println("paymentDetailCheck:" + paymentDetailCheck.getPaidDate());
                    }

                    if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE)) {
                        paymentDetailRemittance = paymentDetail;
                        System.out.println("paymentDetailRemittance:" + paymentDetailRemittance.getPaidDate());
                    }
                    System.out.println("---------------------------");
                }
                if (paymentDetailCheck != null && paymentDetailRemittance != null) {
                    if (paymentDetailCheck.getPaidDate().compareTo(paymentDetailRemittance.getPaidDate()) == 0) {
                        //choose any
                        paymentDetailServiceExcess = paymentDetailCheck;
                    } else if (paymentDetailCheck.getPaidDate().compareTo(paymentDetailRemittance.getPaidDate()) == 1) {
                        paymentDetailServiceExcess = paymentDetailCheck;
                    } else if (paymentDetailCheck.getPaidDate().compareTo(paymentDetailRemittance.getPaidDate()) == -1) {
                        paymentDetailServiceExcess = paymentDetailRemittance;
                    }
                } else {
                    if (paymentDetailCheck != null) {
                        paymentDetailServiceExcess = paymentDetailCheck;
                    }
                    if (paymentDetailRemittance != null) {
                        paymentDetailServiceExcess = paymentDetailRemittance;
                    }
                }

                if (paymentDetailServiceExcess != null) {
                    System.out.println("paymentDetailServiceExcess:" + paymentDetailServiceExcess);
                    System.out.println("specificExcessMapCharge:" + specificExcessMapCharge);
                    String paymentSettlementCurrencyService = paymentDetailServiceExcess.getCurrency().getCurrencyCode();
                    BookCurrency payBookCurrencyService = determineBookCurrency(paymentSettlementCurrencyService);
                    if(DocumentClass.BC.equals(tradeService.getDocumentClass()) || DocumentClass.BP.equals(tradeService.getDocumentClass())){
                        genAccountingEntryRefund_charges(tradeService, specificExcessMapCharge, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrencyService, payBookCurrencyService, new AccountingEventTransactionId("PAYMENT-CHARGES-EXPORT"), gltsNumber, tradeServiceStatus);
                    } else {
                        genAccountingEntryRefund_charges(tradeService, specificExcessMapCharge, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrencyService, payBookCurrencyService, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Generated accounting entry related to domestic settlement
     *
     * @param tradeService       TradeService Object
     * @param paymentSettlement  Payment object for Settlement
     * @param productRef         ProductId of Product
     * @param lcCurrency         Currency of the Product as String
     * @param bcLcCurrency       BookCurrency of Product
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     * @param paymentProduct     Payment object for Product Charges
     */
    private void generateAccountingEntryForDomesticSettlement(TradeService tradeService, Payment paymentSettlement, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String gltsNumber, String tradeServiceStatus, Payment paymentProduct) {
        System.out.println("generateAccountingEntryForDomesticSettlement");
        String accountingEventTypeIdString;
//        int counter = 0;

//        BigDecimal fromProductPaymentRatesUsdToPhp = BigDecimal.ZERO;
        BigDecimal fromProductPaymentRatesThirdToUsd = BigDecimal.ZERO;
        if(paymentProduct != null){
	        Set<PaymentDetail> forRatesPaymentDetails = paymentProduct.getDetails();
	        for (PaymentDetail paymentDetail : forRatesPaymentDetails) {
	            if (paymentDetail.getSpecialRateThirdToUsd() != null && paymentDetail.getSpecialRateThirdToUsd().compareTo(BigDecimal.ZERO) > 0) {
	                fromProductPaymentRatesThirdToUsd = paymentDetail.getSpecialRateThirdToUsd();
	            }
	        }
        }
        
        //Booking is done from payment currency to lc currency
        System.out.println("Settlement to Beneficiary of Product Charges Start");
        Set<PaymentDetail> temp = paymentSettlement.getDetails();
        for (PaymentDetail paymentDetail : temp) {
            System.out.println("---------------------------");
            printPaymentDetails(paymentDetail);
            String settlementNameBase = paymentDetail.getPaymentInstrumentType().toString();
            String settlementName = getSettlementName(paymentDetail.getPaymentInstrumentType().toString());
            System.out.println("settlementName:" + settlementName);
            BigDecimal settlementInPHP;
            BigDecimal settlementInUSD;
            BigDecimal settlementInTHIRD;


            BigDecimal urr = BigDecimal.ZERO;
            if (tradeService.getDetails().containsKey("USD-PHP_urr")) {
                String urrString = (String) tradeService.getDetails().get("USD-PHP_urr");
                urr = new BigDecimal(urrString);
            } else if (tradeService.getDetails().containsKey("urr")) {
                String urrString = (String) tradeService.getDetails().get("urr");
                urr = new BigDecimal(urrString);
            } else if (tradeService.getDetails().containsKey("URR")) {
                String urrString = (String) tradeService.getDetails().get("URR");
                urr = new BigDecimal(urrString);
            } else if (tradeService.getDetails().containsKey("creationExchangeRateUsdToPHPUrr")) {
                String urrString = (String) tradeService.getDetails().get("creationExchangeRateUsdToPHPUrr");
                urr = new BigDecimal(urrString);
            }


            if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {
                settlementInPHP = paymentDetail.getAmount();
                settlementInUSD = BigDecimal.ZERO;
                settlementInTHIRD = BigDecimal.ZERO;
            } else if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")) {
                settlementInUSD = paymentDetail.getAmount();
                settlementInPHP = settlementInUSD.multiply(urr).setScale(2, BigDecimal.ROUND_UP);
                settlementInTHIRD = BigDecimal.ZERO;
            } else {
                settlementInTHIRD = paymentDetail.getAmount();
                BigDecimal thirdToUsd = tradeService.getSpecialRateThirdToUsd();

                if (fromProductPaymentRatesThirdToUsd != null) {
                    thirdToUsd = fromProductPaymentRatesThirdToUsd;
                }
                settlementInUSD = settlementInTHIRD.multiply(thirdToUsd).setScale(2, BigDecimal.ROUND_UP);
                settlementInPHP = settlementInTHIRD.multiply(thirdToUsd.multiply(urr)).setScale(2, BigDecimal.ROUND_UP);
            }


            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            placeSettlementsInPaymentMap(settlementName, settlementInPHP, settlementInUSD, settlementInTHIRD, specificPaymentMap);
            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
            System.out.println("specificPaymentMap:" + specificPaymentMap);
            System.out.println("---------------------------");

            String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
            BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
            accountingEventTypeIdString = getAccountingEventIdStringPaymentOrLoan_DM(lcCurrency, paymentDetail.getPaymentInstrumentType().toString());
            System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);

            String accountType = "RBU";
            if (tradeService.getDetails().containsKey("accountType")) {
                accountType = tradeService.getDetails().get("accountType").toString();
            } else {//no accountType this means we have to base this on the lc currency
                if (!lcCurrency.equalsIgnoreCase("PHP")) {
                    accountType = "FCDU";
                }

            }
            if (accountingEventTypeIdString.equalsIgnoreCase("SETTLEMENT-NEGO-AMOUNT-VIA-TR-LOAN")) {
                if (accountType.equalsIgnoreCase("RBU")) {
                    accountingEventTypeIdString += "-RG-BOOK";
                } else if(accountType.equalsIgnoreCase("FCDU")) {
                    accountingEventTypeIdString += "-FC-BOOK";
                } else {
                    //base it on what the settlement is
                    accountingEventTypeIdString += getBookCodeFromSettlementName(settlementNameBase,lcCurrency);
                }
            } else if(accountingEventTypeIdString.equalsIgnoreCase("SETTLEMENT-NEGO-AMOUNT-VIA-MD-OTHERS")){
                if (accountType.equalsIgnoreCase("RBU")) {
                    accountingEventTypeIdString += "-RG-BOOK";
                } else if(accountType.equalsIgnoreCase("FCDU")) {
                    accountingEventTypeIdString += "-FC-BOOK";
                } else {
                    //base it on what the settlement is
                    accountingEventTypeIdString += getBookCodeFromSettlementName(settlementNameBase,lcCurrency);
                }
            }else {
                if (accountType.equalsIgnoreCase("RBU")) {
                    accountingEventTypeIdString += "-RG-BOOK";
                } else if(accountType.equalsIgnoreCase("FCDU")) {
                    accountingEventTypeIdString += "-FC-BOOK";
                } else {
                    //base it on what the settlement is
                    accountingEventTypeIdString += getBookCodeFromSettlementName(settlementNameBase,lcCurrency);
                }
            }



//            for (String key:specificPaymentMap.keySet()) {
//                System.out.println("----------------------------------------------------------------------------------------------");
//                System.out.println("key:" + key);
//                Object temptemp = specificPaymentMap.get(key);
//                System.out.println("temptemp:"+temptemp);
//
//                if(temptemp instanceof BigDecimal) {
//                    if(((BigDecimal) temptemp).compareTo(BigDecimal.ZERO)==1){
//                        System.out.println("INSTANCE OF INSTANCE OF");
//                        AccountingEntryActualVariables accountingEntryActualVariables = new AccountingEntryActualVariables(
//                                "",
//                                "",
//                                "",//accountingEntry.getEntryType().toString(),
//                                (BigDecimal)temptemp,
//                                tradeService.getTradeServiceId(),
//                                productRef.getProductId(),
//                                tradeService.getServiceType(),
//                                new AccountingEventTransactionId(accountingEventTypeIdString),
//                                key,
//                                paymentDetail.getId().toString()
//                        );
//
//                        accountingEntryActualVariablesRepository.save(accountingEntryActualVariables);
//                    }
//                }
//            }

            genAccountingEntrySettlement_settlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
        }
    }

    /**
     * Generated accounting entry related to domestic payment
     *
     * @param tradeService       TradeService object
     * @param paymentProduct     Payment object for Product Charges
     * @param productRef         ProductId of the Product
     * @param lcCurrency         Currency of the LC as String
     * @param bcLcCurrency       BookCurrency of the LC
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     * @param paymentSettlement
     */
    private void generateAccountingEntryForDomesticPayment(TradeService tradeService, Payment paymentProduct, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String gltsNumber, String tradeServiceStatus, Payment paymentSettlement) {
        System.out.println("generateAccountingEntryForDomesticPayment");
//        int counter = 1;
        System.out.println();
        String accountingEventTypeIdString; //Booking is done from payment currency to lc currency
        System.out.println("Payment of Product Charges Start");
        Set<PaymentDetail> temp = paymentProduct.getDetails();
        for (PaymentDetail paymentDetail : temp) {
            System.out.println("---------------------------");
            printPaymentDetails(paymentDetail);
            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
            placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency); //orig profit or loss based on difference between urr and buy or sell rate

            System.out.println("specificPaymentMap:" + specificPaymentMap);
            System.out.println("---------------------------");

            String paymentNameBase = paymentDetail.getPaymentInstrumentType().toString();
            String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
            BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
            accountingEventTypeIdString = getAccountingEventIdStringPaymentOrLoan_DM(lcCurrency, paymentDetail.getPaymentInstrumentType().toString());
            System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);

            String accountType = "RBU";
            if (tradeService.getDetails().containsKey("accountType")) {
                accountType = tradeService.getDetails().get("accountType").toString();
            } else {//no accountType this means we have to base this on the lc currency
                if (!lcCurrency.equalsIgnoreCase("PHP")) {
                    accountType = "FCDU";
                }

            }
            if (accountingEventTypeIdString.equalsIgnoreCase("SETTLEMENT-NEGO-AMOUNT-VIA-TR-LOAN")) {
                if (accountType.equalsIgnoreCase("RBU")) {
                    accountingEventTypeIdString += "-RG-BOOK";
                } else if(accountType.equalsIgnoreCase("FCDU")) {
                    accountingEventTypeIdString += "-FC-BOOK";
                } else {
                    //base it on what the settlement is
                    accountingEventTypeIdString += getBookCodeFromSettlementName(paymentNameBase,lcCurrency);
                }
            } else if(accountingEventTypeIdString.equalsIgnoreCase("SETTLEMENT-NEGO-AMOUNT-VIA-MD-OTHERS")){
                if (accountType.equalsIgnoreCase("RBU")) {
                    accountingEventTypeIdString += "-RG-BOOK";
                } else if(accountType.equalsIgnoreCase("FCDU")) {
                    accountingEventTypeIdString += "-FC-BOOK";
                } else {
                    //base it on what the settlement is
                    accountingEventTypeIdString += getBookCodeFromSettlementName(paymentNameBase,lcCurrency);
                }
            }else {
                if (accountType.equalsIgnoreCase("RBU")) {
                    accountingEventTypeIdString += "-RG-BOOK";
                } else if(accountType.equalsIgnoreCase("FCDU")) {
                    accountingEventTypeIdString += "-FC-BOOK";
                } else {
                    //base it on what the settlement is
                    accountingEventTypeIdString += getBookCodeFromSettlementName(paymentNameBase,lcCurrency);
                }
            }



//            for (String key:specificPaymentMap.keySet()) {
//                System.out.println("----------------------------------------------------------------------------------------------");
//                System.out.println("key:" + key);
//                Object temptemp = specificPaymentMap.get(key);
//                System.out.println("temptemp:"+temptemp);
//
//                if(temptemp instanceof BigDecimal) {
//                    if(((BigDecimal) temptemp).compareTo(BigDecimal.ZERO)==1){
//                        System.out.println("INSTANCE OF INSTANCE OF");
//                        AccountingEntryActualVariables accountingEntryActualVariables = new AccountingEntryActualVariables(
//                                "",
//                                "",
//                                "",//accountingEntry.getEntryType().toString(),
//                                (BigDecimal)temptemp,
//                                tradeService.getTradeServiceId(),
//                                productRef.getProductId(),
//                                tradeService.getServiceType(),
//                                new AccountingEventTransactionId(accountingEventTypeIdString),
//                                key,
//                                paymentDetail.getId().toString()
//                        );
//
//                        accountingEntryActualVariablesRepository.save(accountingEntryActualVariables);
//                    }
//                }
//            }

            System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);
            genAccountingEntryPayment_settlement(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
        }
    }

    /**
     * Method used to generate charges related accounting entries for Other Import Charges
     * SETTLEMENT-CHARGES
     * Payment of Issuance Charges is the same as Payment of Charges
     * Payment of Charges Accounting Entries
     *
     * @param tradeService                TradeService object
     * @param paymentService              Payment object for Service Charges
     * @param productRef                  ProductId of the Product
     * @param lcCurrency                  Currency of the LC as String
     * @param bcLcCurrency                BookCurrency of the LC
     * @param settlementCurrencyCharges   Currency of the Settlement as String
     * @param bcSettlementCurrencyCharges BookCurrency of the Settlement as String
     * @param chargeMap                   Map containing charges summary
     * @param lcMap                       Map containing LC relevant values
     * @param gltsNumber                  current gltsnumber from sequence generator
     * @param tradeServiceStatus          TradeServiceStatus of the TradeService
     */
    @Transactional
    private void generateChargesAndChargesPaymentOtherImportChargesAccountingEntries(TradeService tradeService, Payment paymentService, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrencyCharges, BookCurrency bcSettlementCurrencyCharges, Map<String, Object> chargeMap, Map<String, Object> lcMap, String gltsNumber, String tradeServiceStatus, Payment paymentProduct) {

        //Booking is done from payment currency to lc currency
        System.out.println("Payment of Service Charges Start");

        String paymentSettlementCurrency ="";
        BookCurrency payBookCurrency = null;
        Boolean PaymentChargeOnce = Boolean.FALSE;
        Set<PaymentDetail> temp = paymentService.getDetails();
        for (PaymentDetail paymentDetail : temp) {
            System.out.println("---------------------------");
            printPaymentDetails(paymentDetail);

            String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
            System.out.println("paymentName:" + paymentName);


            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
            System.out.println("specificPaymentMap:" + specificPaymentMap);
            System.out.println("---------------------------");

            paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
            payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

            genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);

            System.out.println("specificPaymentMap PaymentCharge:" + chargeMap);
            System.out.println("---------------------------");

//            for (String key:specificPaymentMap.keySet()) {
//                System.out.println("----------------------------------------------------------------------------------------------");
//                System.out.println("key:" + key);
//                Object temptemp = specificPaymentMap.get(key);
//                System.out.println("temptemp:"+temptemp);
//
//                if(temptemp instanceof BigDecimal) {
//                    if(((BigDecimal) temptemp).compareTo(BigDecimal.ZERO)==1){
//                        System.out.println("INSTANCE OF INSTANCE OF");
//                        AccountingEntryActualVariables accountingEntryActualVariables = new AccountingEntryActualVariables(
//                                "",
//                                "",
//                                "",//accountingEntry.getEntryType().toString(),
//                                (BigDecimal)temptemp,
//                                tradeService.getTradeServiceId(),
//                                productRef.getProductId(),
//                                tradeService.getServiceType(),
//                                new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"),
//                                key,
//                                paymentDetail.getId().toString()
//                        );
//
//                        accountingEntryActualVariablesRepository.save(accountingEntryActualVariables);
//                    }
//                }
//            }


//            if (!PaymentChargeOnce) {
//                Map<String, Object> tScExcess = getExcessPaymentsServiceCharge(tradeService, paymentService, paymentProduct);
//                placeExcessInPaymentMap(tScExcess, specificPaymentMap);
//                System.out.println(" ChargeMap:" + chargeMap);
//                //Compute PAYMENTCHARGE related
//                System.out.println("specificPaymentMap :" + specificPaymentMap);
//                specificPaymentMap.putAll(chargeMap);
//                genAccountingEntryPaymentCharge_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);
////                genAccountingEntryRefund_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);
//                PaymentChargeOnce = Boolean.TRUE;
//            }

        }

        Map<String, Object> specificChargeMap = new HashMap<String, Object>();

        Map<String, Object> tScExcess = getExcessPaymentsServiceCharge(tradeService, paymentService, paymentProduct, null);
        placeExcessInPaymentMap(tScExcess, specificChargeMap);
        System.out.println(" ChargeMap:" + chargeMap);
        //Compute PAYMENTCHARGE related
        System.out.println("specificChargeMap :" + specificChargeMap);
        specificChargeMap.putAll(chargeMap);
        genAccountingEntryPaymentCharge_charges(tradeService, specificChargeMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);




        genAccountingEntryLC_charges(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);

        //Generate Accounting Entry Related to Charges since all LC opening has charges
        genAccountingEntryCharge_charges(tradeService, chargeMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);
    }

    /**
     * Method used to generate charges related accounting entries for Imports
     * SETTLEMENT-CHARGES
     * Payment of Issuance Charges is the same as Payment of Charges
     * Payment of Charges Accounting Entries
     *
     * @param tradeService                TradeService object
     * @param paymentService              Payment object for Service Charges
     * @param productRef                  ProductId of the Product
     * @param lcCurrency                  Currency of the LC as String
     * @param bcLcCurrency                BookCurrency of the LC
     * @param settlementCurrencyCharges   Currency of the Settlement as String
     * @param bcSettlementCurrencyCharges BookCurrency of the Settlement as String
     * @param chargeMap                   Map containing charges summary
     * @param lcMap                       Map containing LC relevant values
     * @param gltsNumber                  current gltsnumber from sequence generator
     * @param tradeServiceStatus          TradeServiceStatus of the TradeService
     */
    @Transactional
    private void generateChargesAndChargesPaymentAccountingEntries(TradeService tradeService, Payment paymentService, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrencyCharges, BookCurrency bcSettlementCurrencyCharges, Map<String, Object> chargeMap, Map<String, Object> lcMap, String gltsNumber, String tradeServiceStatus, Payment paymentProduct) {

        //Booking is done from payment currency to lc currency
        System.out.println("Payment of Service Charges Start");

        String paymentSettlementCurrency ="";
        BookCurrency payBookCurrency = null;
        Boolean PaymentChargeOnce = Boolean.FALSE;
        Set<PaymentDetail> temp = paymentService.getDetails();
        for (PaymentDetail paymentDetail : temp) {
            System.out.println("---------------------------");
            printPaymentDetails(paymentDetail);

            String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
            System.out.println("paymentName:" + paymentName);


            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
            System.out.println("specificPaymentMap:" + specificPaymentMap);
            System.out.println("---------------------------");

            paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
            payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

            genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);

            System.out.println("specificPaymentMap PaymentCharge:" + chargeMap);
            System.out.println("---------------------------");


        }

        Map<String, Object> specificChargeMap = new HashMap<String, Object>();

        Map<String, Object> tScExcess = getExcessPaymentsServiceCharge(tradeService, paymentService, paymentProduct, null);
        placeExcessInPaymentMap(tScExcess, specificChargeMap);
        System.out.println(" ChargeMap:" + chargeMap);
        //Compute PAYMENTCHARGE related
        System.out.println("specificChargeMap :" + specificChargeMap);
        specificChargeMap.putAll(chargeMap);
        
        //For Double 2% CWT Bug#3020
        //TODO: Find Other modules that has the same problem ... >> EXPORT Advising Opening >> import advance
        //TODO: Find the real problem for DM DP Settlement
       try {
    	   
    	   if(!(tradeService.getDocumentType().equals(DocumentType.DOMESTIC) 
                  	&& tradeService.getDocumentClass().equals(DocumentClass.DP) 
                  	&& tradeService.getServiceType().equals(ServiceType.SETTLEMENT))){
              	
                  genAccountingEntryPaymentCharge_charges(tradeService, specificChargeMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);
                
              }

//    	//not all modules has Document Type (i.e. Export advising)   
       }catch(Exception e) {
    	   genAccountingEntryPaymentCharge_charges(tradeService, specificChargeMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);   
    	   System.out.println("~~~~~~~~~~For NullPointerException~~~~~~~~~~~~~~~~~~~~~~");
    	   System.out.println("~~~~~~~~~~PLease check for the following if present in the module:");
    	   System.out.println("~~~~~~~~~~DOCUMENT TYPE || DOCUMENT CLASS || SERVICE TYPE~~~~~~~~~~~~~~~~~~~~~~");
    	   e.printStackTrace();
       }
        
        //Generate Accounting Entry Related to Charges since all LC opening has charges
        genAccountingEntryCharge_charges(tradeService, chargeMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"), gltsNumber, tradeServiceStatus);
    }

    /**
     * Method used to generate charges related accounting entries for EXPORT
     * SETTLEMENT-CHARGES
     * Payment of Issuance Charges is the same as Payment of Charges
     * Payment of Charges Accounting Entries for Export
     *
     * @param tradeService                TradeService object
     * @param paymentService              Payment object for Service Charges
     * @param productRef                  ProductId of the Product
     * @param lcCurrency                  Currency of the LC as String
     * @param bcLcCurrency                BookCurrency of the LC
     * @param settlementCurrencyCharges   Currency of the Settlement as String
     * @param bcSettlementCurrencyCharges BookCurrency of the Settlement as String
     * @param chargeMap                   Map containing charges summary
     * @param lcMap                       Map containing LC relevant values
     * @param gltsNumber                  current gltsnumber from sequence generator
     * @param tradeServiceStatus          TradeServiceStatus of the TradeService
     */
    @Transactional
    private void generateChargesAndChargesPaymentAccountingEntriesExport(TradeService tradeService, Payment paymentService, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrencyCharges, BookCurrency bcSettlementCurrencyCharges, Map<String, Object> chargeMap, Map<String, Object> lcMap, String gltsNumber, String tradeServiceStatus, Payment paymentProduct, Payment paymentSettlement) {

        //Booking is done from payment currency to lc currency
        System.out.println("Payment of Service Charges Start EXPORT");

        Boolean PaymentChargeOnce = Boolean.FALSE;
        Set<PaymentDetail> temp = paymentService.getDetails();
        for (PaymentDetail paymentDetail : temp) {
            System.out.println("---------------------------");
            printPaymentDetails(paymentDetail);

            String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
            System.out.println("paymentName:" + paymentName);


            Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
            placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
            placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
            System.out.println("specificPaymentMap:" + specificPaymentMap);
            System.out.println("---------------------------");

            String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
            BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

            genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-EXPORT"), gltsNumber, tradeServiceStatus);

//            for (String key:specificPaymentMap.keySet()) {
//                System.out.println("----------------------------------------------------------------------------------------------");
//                System.out.println("key:" + key);
//                Object temptemp = specificPaymentMap.get(key);
//
//                if(temptemp instanceof BigDecimal) {
//                    if(((BigDecimal) temptemp).compareTo(BigDecimal.ZERO)==1){
//                        System.out.println("INSTANCE OF INSTANCE OF");
//                        AccountingEntryActualVariables accountingEntryActualVariables = new AccountingEntryActualVariables(
//                                "",
//                                "",
//                                "",//accountingEntry.getEntryType().toString(),
//                                (BigDecimal)temptemp,
//                                tradeService.getTradeServiceId(),
//                                productRef.getProductId(),
//                                tradeService.getServiceType(),
//                                new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"),
//                                key,
//                                paymentDetail.getId().toString()
//                        );
//
//                        accountingEntryActualVariablesRepository.save(accountingEntryActualVariables);
//                    }
//                }
//            }

//            System.out.println("specificPaymentMap PaymentCharge:" + chargeMap);
            System.out.println("---------------------------");

            if (!PaymentChargeOnce) {
                Map<String, Object> tScExcess = getExcessPaymentsServiceCharge(tradeService, paymentService, paymentProduct, paymentSettlement);
                placeExcessInPaymentMap(tScExcess, specificPaymentMap);
                System.out.println(" ChargeMap:" + chargeMap);
                if(paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
                    //as is override nothing
                } else if(paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")) {
                    BigDecimal origAmount = new BigDecimal(chargeMap.get("chargesAmountOrig").toString());
                    specificPaymentMap.put("productPaymentTotalUSD",origAmount);
                    specificPaymentMap.put("productPaymentTotalPHP",origAmount.multiply(paymentDetail.getUrr()));
                } else {
                    BigDecimal origAmount = new BigDecimal(chargeMap.get("chargesAmountOrig").toString());
                    specificPaymentMap.put("productPaymentTotalTHIRD",origAmount);
                    BigDecimal thirdsPassOnRate = BigDecimal.ZERO;
                    if(tradeService.getDetails().containsKey(paymentDetail.getCurrency().getCurrencyCode() + "-USD_text_pass_on_rate") ||
                       tradeService.getDetails().containsKey(paymentDetail.getCurrency().getCurrencyCode() + "-USD_special_rate_charges_buying")){
                    	 thirdsPassOnRate = getBigDecimalOrZero(tradeService.getDetails().get(paymentDetail.getCurrency().getCurrencyCode() + "-USD_text_pass_on_rate"));
                    }
                    //henry
                    BigDecimal thirdToUsd = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get(lcCurrency+"-USD_special_rate_charges_buying").toString()));
                    specificPaymentMap.put("productPaymentTotalUSD",origAmount.multiply(thirdToUsd).setScale(2,BigDecimal.ROUND_HALF_UP));
                    specificPaymentMap.put("productPaymentTotalPHP",origAmount.multiply(thirdToUsd).multiply(paymentDetail.getUrr()).setScale(2,BigDecimal.ROUND_HALF_UP)); //USE URR
//                    specificPaymentMap.put("productPaymentTotalUSD",origAmount.divide(paymentDetail.getPassOnRateThirdToUsd(),2,BigDecimal.ROUND_UP));
//                    specificPaymentMap.put("productPaymentTotalPHP",origAmount.divide(paymentDetail.getPassOnRateThirdToUsd(),2,BigDecimal.ROUND_UP).multiply(paymentDetail.getUrr())); //USE URR


                }
                //Compute PAYMENTCHARGE related
                System.out.println("specificPaymentMap :" + specificPaymentMap);
                specificPaymentMap.putAll(chargeMap);
                genAccountingEntryPaymentCharge_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("PAYMENT-CHARGES-EXPORT"), gltsNumber, tradeServiceStatus);
                PaymentChargeOnce = Boolean.TRUE;
            }

        }



        //Generate Accounting Entry Related to Charges since all LC opening has charges
        genAccountingEntryCharge_charges(tradeService, chargeMap, productRef, lcCurrency, bcLcCurrency, settlementCurrencyCharges, bcSettlementCurrencyCharges, new AccountingEventTransactionId("PAYMENT-CHARGES-EXPORT"), gltsNumber, tradeServiceStatus);
    }

    /**
     * Method used to generate accounting entries for Opening Cash Payment
     *
     * @param tradeService       TradeService object
     * @param paymentProduct     Payment objectc for product payment
     * @param productRef         ProductId of the Product
     * @param lcCurrency         Currency of the LC as String
     * @param bcLcCurrency       BookCurrency of the LC
     * @param lcMap              Map containing LC data summary
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    @Transactional
    private void generateOpeningCashPayment(TradeService tradeService, Payment paymentProduct, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, Map<String, Object> lcMap, String gltsNumber, String tradeServiceStatus) {
        System.out.println("generateOpeningCashPayment");
        if (paymentProduct != null) {
            //Booking is done from payment currency to lc currency
            System.out.println("Payment of Product Charges Start");
            Set<PaymentDetail> temp = paymentProduct.getDetails();
            int onlyOnceCounter = 0;
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);

                String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
                System.out.println("paymentName:" + paymentName);

                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency);
                System.out.println("specificPaymentMap:" + specificPaymentMap);
                System.out.println("---------------------------");

                String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

                genAccountingEntryPayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("OPENING-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);
                genAccountingEntryPaymentCharge(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId("OPENING-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);


//                for (String key:specificPaymentMap.keySet()) {
//                    System.out.println("----------------------------------------------------------------------------------------------");
//                    System.out.println("key:" + key);
//                    Object temptemp = specificPaymentMap.get(key);
//                    System.out.println("temptemp:"+temptemp);
//
//                    if(temptemp instanceof BigDecimal) {
//                        if(((BigDecimal) temptemp).compareTo(BigDecimal.ZERO)==1){
//                            System.out.println("INSTANCE OF INSTANCE OF");
//                            AccountingEntryActualVariables accountingEntryActualVariables = new AccountingEntryActualVariables(
//                                    "",
//                                    "",
//                                    "",//accountingEntry.getEntryType().toString(),
//                                    (BigDecimal)temptemp,
//                                    tradeService.getTradeServiceId(),
//                                    productRef.getProductId(),
//                                    tradeService.getServiceType(),
//                                    new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"),
//                                    key,
//                                    paymentDetail.getId().toString()
//                            );
//
//                            accountingEntryActualVariablesRepository.save(accountingEntryActualVariables);
//                        }
//                    }
//                }

                if (onlyOnceCounter < 1) {
                    //ONLY ONCE FOR EVERYTHING
                    System.out.println("lcMap:" + lcMap);
                    if (lcMap.containsKey("lcAmountPHP")) {
                        lcMap.put("APCASHLCproductPaymentTotalPHP", lcMap.get("lcAmountPHP"));
                    }
                    if (lcMap.containsKey("lcAmountUSD")) {
                        lcMap.put("APCASHLCproductPaymentTotalUSD", lcMap.get("lcAmountUSD"));
                    }
                    if (lcMap.containsKey("lcAmountTHIRD")) {
                        lcMap.put("APCASHLCproductPaymentTotalTHIRD", lcMap.get("lcAmountTHIRD"));
                    }

                    genAccountingEntryLC(tradeService, lcMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId("OPENING-PAYMENT-CASH"), gltsNumber, tradeServiceStatus);
                    onlyOnceCounter++;


//                    for (String key:lcMap.keySet()) {
//                        System.out.println("----------------------------------------------------------------------------------------------");
//                        System.out.println("key:" + key);
//                        Object temptemp = lcMap.get(key);
//                        System.out.println("temptemp:"+temptemp);
//
//                        if(temptemp instanceof BigDecimal) {
//                            if(((BigDecimal) temptemp).compareTo(BigDecimal.ZERO)==1){
//                                System.out.println("INSTANCE OF INSTANCE OF");
//                                AccountingEntryActualVariables accountingEntryActualVariables = new AccountingEntryActualVariables(
//                                        "",
//                                        "",
//                                        "",//accountingEntry.getEntryType().toString(),
//                                        (BigDecimal)temptemp,
//                                        tradeService.getTradeServiceId(),
//                                        productRef.getProductId(),
//                                        tradeService.getServiceType(),
//                                        new AccountingEventTransactionId("PAYMENT-CHARGES-IMPORT"),
//                                        key,
//                                        paymentDetail.getId().toString()
//                                );
//
//                                accountingEntryActualVariablesRepository.save(accountingEntryActualVariables);
//                            }
//                        }
//                    }
                }
            }
            //TODO::Determine correct paymentSettlementCurrency and payBookCurrency and just call  generateAP_ExcessPayments
        }

        //Generate Accounting Entry Related to payment of cash fxlc

    }

    /**
     * Method used to generate accounting entries for Over payment mainly in LC CASH Foreign Negotiation
     *
     * @param tradeService       TradeService object
     * @param paymentProduct     Payment object for product payment
     * @param productRef         ProductId of the Product
     * @param lcCurrency         Currency of the LC as String
     * @param bcLcCurrency       BookCurrency of the LC
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    @Transactional
    private void generateNegotiationCashOverPayment(TradeService tradeService, Payment paymentProduct, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String gltsNumber, String tradeServiceStatus) {
        System.out.println("generateNegotiationCashOverPayment");
        if (paymentProduct != null) {
//            //Booking is done from payment currency to lc currency
//            System.out.println("Payment of Product Charges Start");
//            Set<PaymentDetail> temp = paymentProduct.getDetails();
////            int onlyOnceCounter = 0;
//            for (PaymentDetail paymentDetail : temp) {
//                System.out.println("---------------------------");
//                printPaymentDetails(paymentDetail);
//
//                String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
//                System.out.println("paymentName:" + paymentName);
//
//                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
//                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
//                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
//                placeFxProfitOrLossInPaymentMap(paymentDetail, specificPaymentMap, lcCurrency);
//                System.out.println("specificPaymentMap:" + specificPaymentMap);
//                System.out.println("---------------------------");
//
//                String accEvTranid = "";
//                if(paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.MD)){
//                    accEvTranid = "SETTLEMENT-NEGO-AMOUNT-VIA-MD-OTHERS";
//                    String acctype = "";
//                    if(tradeService.getDetails().containsKey("accountType")){
//                        acctype =  (String)tradeService.getDetails().get("accountType");
//                        if(acctype!=null){
//                            if("RBU".equalsIgnoreCase(acctype)){
//                                accEvTranid =accEvTranid + "-RG-BOOK";
//                            } else {
//                                accEvTranid =accEvTranid + "-FC-BOOK";
//                            }
//                        } else {
//                            accEvTranid =accEvTranid + "-FC-BOOK";
//                        }
//                    } else {
//                        accEvTranid =accEvTranid + "-FC-BOOK";
//                    }
//
//                    String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
//                    BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
//
//                    genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accEvTranid), gltsNumber, tradeServiceStatus);
//
//                } else {
//
//                }

            System.out.println("NO BOOKING OF LOANS FOR REGULAR USANCE");
            //NO BOOKING OF LOANS FOR REGULAR USANCE
            System.out.println();

            String accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR-FC-BOOK";
            if (paymentProduct != null) {
                //Booking is done from payment currency to lc currency
                System.out.println("Payment of Product Charges Start NEGOTATION");
                Set<PaymentDetail> temp = paymentProduct.getDetails();
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);

                    Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                    placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                    placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                    placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency); //TESTING FXPL
                    System.out.println("specificPaymentMap:" + specificPaymentMap);
                    System.out.println("---------------------------");

                    String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);
                    accountingEventTypeIdString = getAccountingEventIdStringPaymentOrLoan_FX(paymentDetail);
                    String accountType = "RBU";
                    //USE SMART DEFAULTS
                    if(lcCurrency.equalsIgnoreCase("PHP")){
                        accountType = "RBU";
                    } else {
                        accountType = "FCDU";
                    }
                    if (tradeService.getDetails().containsKey("accountType")) {
                        accountType = tradeService.getDetails().get("accountType").toString();
                    }
                    accountingEventTypeIdString = getBookCodeStringPostFix(accountType, accountingEventTypeIdString);
                    System.out.println("accountingEventTypeIdString:" + accountingEventTypeIdString);

                    genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                    genAccountingEntryPaymentCharge_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accountingEventTypeIdString), gltsNumber, tradeServiceStatus);
                }


            }
            //TODO::Determine correct paymentSettlementCurrency and payBookCurrency and just call  generateAP_ExcessPayments
        }

        //Generate Accounting Entry Related to payment of cash fxlc

    }

    /**
     * Method used to generate accounting entries for Over payment mainly in LC CASH Domestic Negotiation
     *
     * @param tradeService       TradeService object
     * @param paymentProduct     Payment object for product payment
     * @param productRef         ProductId of the Product
     * @param lcCurrency         Currency of the LC as String
     * @param bcLcCurrency       BookCurrency of the LC
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     */
    @Transactional
    private void generateOpeningCashOverPayment(TradeService tradeService, Payment paymentProduct, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String gltsNumber, String tradeServiceStatus) {
        System.out.println("generateOpeningCashPayment");
        if (paymentProduct != null) {
            //Booking is done from payment currency to lc currency
            System.out.println("Payment of Product Charges Start");
            Set<PaymentDetail> temp = paymentProduct.getDetails();
//            int onlyOnceCounter = 0;
            for (PaymentDetail paymentDetail : temp) {
                System.out.println("---------------------------");
                printPaymentDetails(paymentDetail);

                String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
                System.out.println("paymentName:" + paymentName);

                Map<String, Object> specificPaymentMap = new HashMap<String, Object>();
                placePaymentsInPaymentMap(tradeService, paymentDetail, specificPaymentMap);
                placeReferenceNumberInPaymentMap(paymentDetail, specificPaymentMap);
                placeFxProfitOrLossInPaymentMap(tradeService, paymentDetail, specificPaymentMap, lcCurrency);
                System.out.println("specificPaymentMap:" + specificPaymentMap);
                System.out.println("---------------------------");

                String accEvTranid = "";
                if(paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.MD)){
                    accEvTranid = "SETTLEMENT-NEGO-AMOUNT-VIA-MD-OTHERS";
                    String acctype = "";
                    if(tradeService.getDetails().containsKey("accountType")){
                        acctype =  (String)tradeService.getDetails().get("accountType");
                        if(acctype!=null){
                            if("RBU".equalsIgnoreCase(acctype)){
                                accEvTranid =accEvTranid + "-RG-BOOK";
                            } else {
                                accEvTranid =accEvTranid + "-FC-BOOK";
                            }
                        } else {
                            accEvTranid =accEvTranid + "-FC-BOOK";
                        }
                    } else {
                        accEvTranid =accEvTranid + "-FC-BOOK";
                    }

                    String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

                    genAccountingEntryPayment_charges(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accEvTranid), gltsNumber, tradeServiceStatus);

                } else {
                    accEvTranid = "OPENING-PAYMENT-CASH";
                    String paymentSettlementCurrency = paymentDetail.getCurrency().getCurrencyCode();
                    BookCurrency payBookCurrency = determineBookCurrency(paymentSettlementCurrency);

                    genAccountingEntryPayment_overpayment(tradeService, specificPaymentMap, productRef, lcCurrency, bcLcCurrency, paymentSettlementCurrency, payBookCurrency, new AccountingEventTransactionId(accEvTranid), gltsNumber, tradeServiceStatus);
                }


            }
            //TODO::Determine correct paymentSettlementCurrency and payBookCurrency and just call  generateAP_ExcessPayments
        }

        //Generate Accounting Entry Related to payment of cash fxlc

    }


    /**
     * Retrieves accounting entry with type AccountingEntryType.CHARGE and the combination of ProductId and ServiceType
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     *
     * @param tradeService                 TradeService object
     * @param details                      details Map of TradeService
     * @param productRef                   ProductId of the Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of the LC
     * @param settlementCurrency           Currency of the Settlement for Charges as String
     * @param bcSettlementCurrency         BookCurrency of the Settlement for Charges as String
     * @param accountingEventTransactionId the AccountingEventTransactionId to match
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryCharge(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(productRef.getProductId(), tradeService.getServiceType(), accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.CHARGE);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
//                    StackTraceElement[] trace = e.getStackTrace();
//                    errorExceptionMessage = "Internal Exception in Accounting Entry"+
//           				 "<br/> "+
//           				 "Please check the GL Entry properly"+
//           				 "<br/> "+
//           				 "TradeServiceID: "+tradeService.getTradeServiceId().toString()+
//           				 "<br/>";
           				//  "<br/> "+
           				 // trace[0].toString();
                    //errorAlert();
                    throw new RuntimeException("Error in genAccountingEntryCharge",e);
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Retrieves accounting entry with type AccountingEntryType.PAYMENT and the combination of ProductId and ServiceType
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     *
     * @param tradeService                 TradeService object
     * @param details                      details Map of TradeService
     * @param productRef                   ProductId of the Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of the LC
     * @param settlementCurrency           Currency of the Settlement for Charges as String
     * @param bcSettlementCurrency         BookCurrency of the Settlement for Charges as String
     * @param accountingEventTransactionId the AccountingEventTransactionId to match
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryPayment(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("private void genAccountingEntryPayment(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId)");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(productRef.getProductId(), tradeService.getServiceType(), accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.PAYMENT);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
        	//TODO: provide popup for end-users
            System.out.println("no accounting entries found");
        }
    }


    /**
     * Retrieves accounting entry with type AccountingEntryType.SETTLEMENT and the combination of ProductId and ServiceType
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     *
     * @param tradeService                 TradeService object
     * @param details                      details Map of TradeService
     * @param productRef                   ProductId of the Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of the LC
     * @param settlementCurrency           Currency of the Settlement for Charges as String
     * @param bcSettlementCurrency         BookCurrency of the Settlement for Charges as String
     * @param accountingEventTransactionId the AccountingEventTransactionId to match
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntrySettlement(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("private void genAccountingEntrySettlement(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId)");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(productRef.getProductId(), tradeService.getServiceType(), accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.SETTLEMENT);
        
        
        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Retrieves accounting entry with type AccountingEntryType.PAYMENT and the combination of ProductId and ServiceType is overriden as opening
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     * <p/>
     * This is done to allow reuse of opening payment config for cash negotiation
     *
     * @param tradeService                 TradeService object
     * @param details                      details Map of TradeService
     * @param productRef                   ProductId of the Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of the LC
     * @param settlementCurrency           Currency of the Settlement for Charges as String
     * @param bcSettlementCurrency         BookCurrency of the Settlement for Charges as String
     * @param accountingEventTransactionId the AccountingEventTransactionId to match
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryPayment_overpayment(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("private void genAccountingEntryPayment(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId)");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(productRef.getProductId(), ServiceType.OPENING, accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.PAYMENT);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

//
//    /**
//     * Retrieves accounting entry with type AccountingEntryType.SETTLEMENT and the combination of ProductId and ServiceType
//     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
//     *
//     * @param tradeService                 TradeService object
//     * @param details                      details Map of TradeService
//     * @param productRef                   ProductId of the Product
//     * @param lcCurrency                   Currency of the LC as String
//     * @param bcLcCurrency                 BookCurrency of the LC
//     * @param settlementCurrency           Currency of the Settlement as String
//     * @param bcSettlementCurrency         BookCurrency of the Settlement
//     * @param accountingEventTransactionId the AccountingEventTransactionId to match
//     * @param gltsNumber                   current gltsnumber from sequence generator
//     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
//     */
//    @Transactional
//    private void genAccountingEntrySettlement(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
//        System.out.println("private void genAccountingEntrySettlement(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId)");
//        List<AccountingEntry> accountingEntries;
//        accountingEntries = accountingEntryRepository.getEntries(productRef.getProductId(), tradeService.getServiceType(), accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.SETTLEMENT);
//
//        if (accountingEntries != null && !accountingEntries.isEmpty()) {
//            for (AccountingEntry accountingEntry : accountingEntries) {
//
//                System.out.println("----------------------------------------------------------------------------------------------");
//                System.out.println("accountingEntry:" + accountingEntry);
//
//                try {
//                    System.out.println("======================> Generating Actual ACCOUNTING entry");
//                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
//                    System.out.println("is entry saved:" + status);
//                } catch (Exception e) {
//                    System.out.println("===================> ACCOUNTING EXCEPTION");
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            System.out.println("no accounting entries found");
//        }
//    }

    /**
     * Retrieves accounting entry with type AccountingEntryType.PAYMENTCHARGE and the combination of ProductId and ServiceType
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     *
     * @param tradeService                 TradeService object
     * @param details                      details Map of TradeService
     * @param productRef                   ProductId of the Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of the LC
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of the Settlement as String
     * @param accountingEventTransactionId the AccountingEventTransactionId to match
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryPaymentCharge(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(productRef.getProductId(), tradeService.getServiceType(), accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.PAYMENTCHARGE);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Retrieves accounting entry with type AccountingEntryType.LC and the combination of ProductId and ServiceType
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     *
     * @param tradeService                 TradeService object
     * @param details                      details Map of TradeService
     * @param productRef                   ProductId of the Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of the LC
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of the Settlement
     * @param accountingEventTransactionId the AccountingEventTransactionId to match
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryLC(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntryLC");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(productRef.getProductId(), tradeService.getServiceType(), accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.LC);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {
                System.out.println("---------------------------------------------xoxo-------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Method similar to @method genAccountingEntryCharge but does not match using ProductId and ServiceType
     * Retrieves accounting entry with type AccountingEntryType.CHARGE and the combination of
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     * USED BY SERVICE CHARGE COMMON
     *
     * @param tradeService                 TradeService object
     * @param details                      details Map of TradeService
     * @param productRef                   ProductId of the Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of the LC
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of the Settlement
     * @param accountingEventTransactionId the AccountingEventTransactionId to match
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryCharge_charges(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.CHARGE);
        System.out.println("accountingEntries "+accountingEntries);
        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Method similar to @method genAccountingEntryPayment but does not match using ProductId and ServiceType
     * Retrieves accounting entry with type AccountingEntryType.PAYMENT and the combination of
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     * USED BY PRODUCT CHARGE COMMON
     *
     * @param tradeService                 TradeService object
     * @param details                      details Map of TradeService
     * @param productRef                   ProductId of the Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of the LC
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of the Settlement
     * @param accountingEventTransactionId the AccountingEventTransactionId to match
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryPayment_settlement(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("private void genAccountingEntryPayment_settlement(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId)");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.PAYMENT);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Method similar to @method genAccountingEntrySettlement but does not match using ProductId and ServiceType
     * Retrieves accounting entry with type AccountingEntryType.SETTLEMENT and the combination of
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     * USED BY PRODUCT CHARGE COMMON
     *
     * @param tradeService                 TradeService Object
     * @param details                      Map containing values to be used in generating accounting entry
     * @param productRef                   ProductId of Product
     * @param lcCurrency                   Currency of the Product as String
     * @param bcLcCurrency                 BookCurrency of Product
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of Settlement
     * @param accountingEventTransactionId AccountingEventTransactionId to be matched
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntrySettlement_settlement(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("private void genAccountingEntrySettlement_settlement(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId)");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.SETTLEMENT);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }


    /**
     * Method similar to @method genAccountingEntryPayment but does not match using ProductId and ServiceType
     * Retrieves accounting entry with type AccountingEntryType.PAYMENT and the combination of
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     * USED BY SERVICE CHARGE COMMON
     *
     * @param tradeService                 TradeService Object
     * @param details                      Map containing values to be used in generating accounting entry
     * @param productRef                   ProductId of Product
     * @param lcCurrency                   Currency of the Product as String
     * @param bcLcCurrency                 BookCurrency of Product
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of Settlement
     * @param accountingEventTransactionId AccountingEventTransactionId to be matched
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryPayment_charges(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("private void genAccountingEntryPayment_charges(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId)");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.PAYMENT);
        System.out.println("accountingEntries "+accountingEntries);
        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Method similar to @method genAccountingEntryPaymentCharge but does not match using ProductId and ServiceType
     * Retrieves accounting entry with type AccountingEntryType.PAYMENTCHARGE and the combination of
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     * USED BY SERVICE CHARGE COMMON
     *
     * @param tradeService                 TradeService Object
     * @param details                      Map containing values to be used in generating accounting entry
     * @param productRef                   ProductId of Product
     * @param lcCurrency                   Currency of the Product as String
     * @param bcLcCurrency                 BookCurrency of Product
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of Settlement
     * @param accountingEventTransactionId AccountingEventTransactionId to be matched
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryPaymentCharge_charges(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntryPaymentCharge_charges");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.PAYMENTCHARGE);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }


        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Method similar to @method genAccountingEntryRefund but does not match using ProductId and ServiceType
     * Retrieves accounting entry with type AccountingEntryType.REFUND and the combination of
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     * USED BY SERVICE CHARGE COMMON
     *
     * @param tradeService                 TradeService Object
     * @param details                      Map containing values to be used in generating accounting entry
     * @param productRef                   ProductId of Product
     * @param lcCurrency                   Currency of the Product as String
     * @param bcLcCurrency                 BookCurrency of Product
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of Settlement
     * @param accountingEventTransactionId AccountingEventTransactionId to be matched
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryRefund_charges(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntryRefund_charges");
        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.REFUND);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {

                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }


        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * Method similar to @method genAccountingEntryLC but does not match using ProductId and ServiceType
     * Retrieves accounting entry with type AccountingEntryType.LC and the combination of
     * accountingEventTransactionId , bcLcCurrency and bcSettlementCurrency
     * USED BY SERVICE CHARGE COMMON
     *
     * @param tradeService                 TradeService Object
     * @param details                      Map containing values to be used in generating accounting entry
     * @param productRef                   ProductId of Product
     * @param lcCurrency                   Currency of the LC as String
     * @param bcLcCurrency                 BookCurrency of Product
     * @param settlementCurrency           Currency of the Settlement as String
     * @param bcSettlementCurrency         BookCurrency of Settlement
     * @param accountingEventTransactionId AccountingEventTransactionId to be matched
     * @param gltsNumber                   current gltsnumber from sequence generator
     * @param tradeServiceStatus           TradeServiceStatus of the TradeService
     */
    @Transactional
    private void genAccountingEntryLC_charges(TradeService tradeService, Map<String, Object> details, ProductReference productRef, String lcCurrency, BookCurrency bcLcCurrency, String settlementCurrency, BookCurrency bcSettlementCurrency, AccountingEventTransactionId accountingEventTransactionId, String gltsNumber, String tradeServiceStatus) {
        System.out.println("genAccountingEntryLC_charges");

        List<AccountingEntry> accountingEntries;
        accountingEntries = accountingEntryRepository.getEntries(accountingEventTransactionId, bcLcCurrency, bcSettlementCurrency, AccountingEntryType.LC);

        if (accountingEntries != null && !accountingEntries.isEmpty()) {
            for (AccountingEntry accountingEntry : accountingEntries) {
                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.println("accountingEntry:" + accountingEntry);

                try {
                    System.out.println("======================> Generating Actual ACCOUNTING entry");
                    boolean status = generateAccountingEntryActual(tradeService, details, accountingEntry, productRef, lcCurrency, settlementCurrency, gltsNumber, tradeServiceStatus);
                    System.out.println("is entry saved:" + status);
                    System.out.println("==================================");
                } catch (Exception e) {
                    System.out.println("===================> ACCOUNTING EXCEPTION");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("no accounting entries found");
        }
    }

    /**
     * returns the equivalent BookCurrency for a given string representation of a currency
     *
     * @param currency the String representation of a currency
     * @return BookCurrency of equivalent of Currency field
     */
    private static BookCurrency determineBookCurrency(String currency) {
        BookCurrency bcSettlementCurrency;
        if (currency.equalsIgnoreCase("PHP")) {
            bcSettlementCurrency = BookCurrency.PHP;
        } else if (currency.equalsIgnoreCase("USD")) {
            bcSettlementCurrency = BookCurrency.USD;
        } else {
            bcSettlementCurrency = BookCurrency.THIRD;
        }
        return bcSettlementCurrency;
    }

    /**
     * Insert values in defaults map retrieved into details map and overwrites any existing values
     *
     * @param details  TradeService details Map
     * @param defaults System default values to be inserted in details map before computation begins
     */
    private static void insertDefaultsToDetailsMap(Map<String, Object> details, Map<String, Object> defaults) {
        for (String keyed : defaults.keySet()) {
            Object ob = defaults.get(keyed);
            //Check if it does not contain the key, thus overwrite with the default value
            if (!details.containsKey(keyed)) {
                if (ob instanceof BigDecimal) {
                    details.put(keyed, ((BigDecimal) ob).toPlainString());                   
                } else {
                    details.put(keyed, ob);
                }
            } else
                //Check if what is contained is null or empty and overwrite
                if (details.containsKey(keyed) && (details.get(keyed) == null || details.get(keyed).toString().equalsIgnoreCase(""))) {
                    if (ob instanceof BigDecimal) {
                        details.put(keyed, ((BigDecimal) ob).toPlainString());
                    } else {
                        details.put(keyed, ob);
                    }
                }
        }
    }

    /**
     * Returns BigDecimal value of @param num
     *
     * @param num the Object to be converted to BigDecimal equivalent or default to Zero
     * @return BigDecimal equivalent of num
     */
    private static BigDecimal getBigDecimalOrZero(Object num) {
        BigDecimal value = BigDecimal.ZERO;
        if (num != null) {
            try {
                value = new BigDecimal(num.toString().replace(",",""));
            } catch (Exception e) {
                System.out.println("object being converted produced an invalid BigDecimal");
                value = BigDecimal.ZERO;
            }
        }
        return value;
    }

    /**
     * Checks if key is found in details map and returns if it exists or empty string otherwise
     *
     * @param details map which would be checked for key
     * @param key     to be checked in map details
     * @return an empty string or the value of the key if found
     */
    private static String getStringOrReturnEmptyString(Map<String, Object> details, String key) {
        String retVal = "";
        if (details != null && details.containsKey(key)) {
            Object obj = details.get(key);
            if (obj != null) {
                retVal = obj.toString();
            }
        }
        return retVal;
    }

    /**
     * Divides numerator with denominator or returns a zero for an error or exception
     *
     * @param numerator   numerator in division operation
     * @param denominator denominator in division operation
     * @return the result of the division operation
     */
    private static BigDecimal divideOrReturnZero(BigDecimal numerator, BigDecimal denominator) {
        BigDecimal answer = BigDecimal.ZERO;

        if ((numerator != null && denominator != null)) {
            if (numerator.compareTo(BigDecimal.ZERO) == 1 && denominator.compareTo(BigDecimal.ZERO) == 1) {
                answer = numerator.divide(denominator, 9, BigDecimal.ROUND_HALF_UP);
            }
        }
        return answer;
    }

    /**
     * Returns BigDecimal value of @param strNum
     *
     * @param strNum string to be parsed
     * @return BigDecimal value of strNum
     */
    private static BigDecimal parseOrReturnZero(String strNum) {
        BigDecimal answer = BigDecimal.ZERO;
        if (strNum != null) {
            try {
                answer = new BigDecimal(strNum);
            } catch (Exception e) {
                System.out.println("()()()()()()Problem parsing num:" + strNum);
            }
        } else {
            System.out.println("strNum is null");
        }

        return answer;
    }

    /**
     * Places reference number in specificPaymentMap from paymentDetail
     *
     * @param paymentDetail      PaymentDetail whose reference number will be inserted in specificPaymentMap
     * @param specificPaymentMap Map which will be modified by insertion of PaymentDetail reference number
     */
    private static void placeReferenceNumberInPaymentMap(PaymentDetail paymentDetail, Map<String, Object> specificPaymentMap) {
        if (paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("CASA")) {
            specificPaymentMap.put("CASAAccountNo", paymentDetail.getReferenceNumber());
        }

        if (paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("CASH")) {
            specificPaymentMap.put("TradeSuspenseAccountNo", paymentDetail.getReferenceNumber());
        }

        if (paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("REMITTANCE")) {
            specificPaymentMap.put("RemittanceAccountNo", paymentDetail.getReferenceNumber());
        }

        if (paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("CHECK")) {
            specificPaymentMap.put("TradeSuspenseAccountNo", paymentDetail.getReferenceNumber());
        }
    }

    /**
     * places Settlement values in specificPaymentMap
     *
     * @param settlementName     String containing the settlement name
     * @param settlementInPHP    BigDecimal of PHP settlement
     * @param settlementInUSD    BigDecimal of USD settlement
     * @param settlementInTHIRD  BigDecimal of THIRD settlement
     * @param specificPaymentMap Settlement Map to be used in generating settlement entries
     */
    private static void placeSettlementsInPaymentMap(String settlementName, BigDecimal settlementInPHP, BigDecimal settlementInUSD, BigDecimal settlementInTHIRD, Map<String, Object> specificPaymentMap) {
        if (settlementInPHP != null) {
            specificPaymentMap.put(settlementName + "PHP", settlementInPHP);
            specificPaymentMap.put("settlementTotal" + "PHP", settlementInPHP);
        } else {
            specificPaymentMap.put(settlementName + "PHP", BigDecimal.ZERO);
            specificPaymentMap.put("settlementTotal" + "PHP", BigDecimal.ZERO);
        }
        if (settlementInUSD != null) {
            specificPaymentMap.put(settlementName + "USD", settlementInUSD);
            specificPaymentMap.put("settlementTotal" + "USD", settlementInUSD);
        } else {
            specificPaymentMap.put(settlementName + "USD", BigDecimal.ZERO);
            specificPaymentMap.put("settlementTotal" + "USD", BigDecimal.ZERO);
        }
        if (settlementInTHIRD != null) {
            specificPaymentMap.put(settlementName + "THIRD", settlementInTHIRD);
            specificPaymentMap.put("settlementTotal" + "THIRD", settlementInTHIRD);
        } else {
            specificPaymentMap.put(settlementName + "THIRD", BigDecimal.ZERO);
            specificPaymentMap.put("settlementTotal" + "THIRD", BigDecimal.ZERO);
        }
    }

    /**
     * places Due From Foreign Bank values in specificPaymentMap
     *
     * @param dueInPHP           BigDecimal of PHP settlement
     * @param dueInUSD           BigDecimal of USD settlement
     * @param dueInTHIRD         BigDecimal of THIRD settlement
     * @param specificPaymentMap Payment Map to be used in generating Due From Foreign Bank entries
     */
    private static void placeDueFromForeignBankInPaymentMap(BigDecimal dueInPHP, BigDecimal dueInUSD, BigDecimal dueInTHIRD, Map<String, Object> specificPaymentMap) {
        if (dueInPHP != null) {
            specificPaymentMap.put("DUEFromFBsettlementTotal" + "PHP", dueInPHP);
        } else {
            specificPaymentMap.put("DUEFromFBsettlementTotal" + "PHP", BigDecimal.ZERO);
        }

        if (dueInUSD != null) {
            specificPaymentMap.put("DUEFromFBsettlementTotal" + "USD", dueInUSD);
        } else {
            specificPaymentMap.put("DUEFromFBsettlementTotal" + "USD", BigDecimal.ZERO);
        }

        if (dueInTHIRD != null) {
            specificPaymentMap.put("DUEFromFBsettlementTotal" + "THIRD", dueInTHIRD);
        } else {
            specificPaymentMap.put("DUEFromFBsettlementTotal" + "THIRD", BigDecimal.ZERO);
        }
    }

    /**
     * places payments in specificPaymentMap
     *
     * @param tradeService       Tradeservice object
     * @param paymentDetail      PaymentDetail object to containing payment to be placed
     * @param specificPaymentMap Payment Map to be used in generating Product Payment Entries
     */
    private static void placePaymentsInPaymentMap(TradeService tradeService, PaymentDetail paymentDetail, Map<String, Object> specificPaymentMap) {

        String lcCurrency = "";
        if (tradeService.getDetails().containsKey("currency") && tradeService.getDetails().get("currency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("currency");
        } else if (tradeService.getDetails().containsKey("negotiationCurrency") && tradeService.getDetails().get("negotiationCurrency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("negotiationCurrency");
        } else if (tradeService.getDetails().containsKey("settlementCurrency") && tradeService.getDetails().get("settlementCurrency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("settlementCurrency");
        } else if (tradeService.getDetails().containsKey("draftCurrency") && tradeService.getDetails().get("draftCurrency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("draftCurrency");
        } else if (tradeService.getDetails().containsKey("productCurrency") && tradeService.getDetails().get("productCurrency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("productCurrency");
        }

        if(tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)){
            lcCurrency="PHP"; //Added fail safe
        }

        String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
        System.out.println("paymentName:" + paymentName);
        BigDecimal paymentInPHP = BigDecimal.ZERO;
        BigDecimal paymentInUSD = BigDecimal.ZERO;
        BigDecimal paymentInTHIRD = BigDecimal.ZERO;

        if(paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
            paymentInPHP = getPayment(paymentDetail, "PHP");
        } else if(paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")){
            paymentInUSD = getPayment(paymentDetail, "USD");
        } else if(!paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD") && !paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
            paymentInTHIRD = getPayment(paymentDetail, "THIRD");
        }



        System.out.println("lcCurrency:" + lcCurrency);
        System.out.println("paymentDetail.getAmount():" + paymentDetail.getAmount());
        System.out.println("paymentDetail.getAmountInLcCurrency():" + paymentDetail.getAmountInLcCurrency());

        //This section is used to determine the urr of this payment
        BigDecimal urrTemp = BigDecimal.ONE;
        if (paymentDetail.getUrr() != null) {
            urrTemp = paymentDetail.getUrr();
        }

        if(urrTemp.compareTo(BigDecimal.ZERO)==0){
            if(tradeService.getDetails().containsKey("urr")){
                urrTemp = getBigDecimalOrZero(tradeService.getDetails().get("urr"));
                System.out.println("urr from details:"+urrTemp);
            }
        }


        if(urrTemp.compareTo(BigDecimal.ZERO)==0){
            if(tradeService.getDetails().containsKey("USD-PHP_urr")){
                urrTemp = getBigDecimalOrZero(tradeService.getDetails().get("USD-PHP_urr"));
                System.out.println("USD-PHP_urr from details:"+urrTemp);
            }
        }

        if(urrTemp.compareTo(BigDecimal.ZERO)==0){
            if(tradeService.getPassOnUrrServiceCharge()!=null){
                urrTemp = tradeService.getPassOnUrrServiceCharge();
                System.out.println("urr from getPassOnUrrServiceCharge:"+urrTemp);
            }
        }
        if(urrTemp.compareTo(BigDecimal.ZERO)==0){
            System.out.println("Missing URR Defaulting to BigDecimal.ONE");
            urrTemp = BigDecimal.ONE;
        }


        if(paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP") && !lcCurrency.equalsIgnoreCase("PHP")){
            paymentInPHP = paymentDetail.getAmount();
            //Determine actual usd and third currency
            if(lcCurrency.equalsIgnoreCase("USD")){
                if(paymentDetail.getAmountInLcCurrency()!=null){
                    paymentInUSD = paymentDetail.getAmountInLcCurrency();
                }
            } else if (!lcCurrency.equalsIgnoreCase("USD") && !lcCurrency.equalsIgnoreCase("PHP")){
                if(paymentDetail.getAmountInLcCurrency()!=null){
                    paymentInTHIRD = paymentDetail.getAmountInLcCurrency();
                }
            }

        } else if(paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP") && lcCurrency.equalsIgnoreCase("PHP")){
            paymentInPHP = paymentDetail.getAmount();

        } else if(paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")){
            //USD i correct determine PHP and THIRDS
            paymentInUSD = paymentDetail.getAmount();
            paymentInPHP = paymentDetail.getAmount().multiply(urrTemp);
            if(!lcCurrency.equalsIgnoreCase("PHP") && !lcCurrency.equalsIgnoreCase("USD")){
                if(paymentDetail.getPassOnRateThirdToUsd()!=null){
                    paymentInTHIRD = paymentDetail.getAmount().multiply(paymentDetail.getSpecialRateThirdToUsd());
                }
            }

        } else if(!paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD") && !paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {
            //THIRDS
            paymentInTHIRD = paymentDetail.getAmount();

            if(paymentDetail.getSpecialRateThirdToUsd()!=null){
                paymentInUSD = paymentDetail.getAmount().multiply(paymentDetail.getSpecialRateThirdToUsd()).setScale(2,BigDecimal.ROUND_UP);
            }
            paymentInPHP = paymentInUSD.multiply(urrTemp);
        }

        if (paymentInPHP != null) {
            specificPaymentMap.put(paymentName + "PHP", paymentInPHP);
//            specificPaymentMap.put("productPaymentTotal" + "PHP", paymentInPHP);
        } else {
            specificPaymentMap.put(paymentName + "PHP", BigDecimal.ZERO);
//            specificPaymentMap.put("productPaymentTotal" + "PHP", paymentInPHP);
        }
        if (paymentInUSD != null) {
            specificPaymentMap.put(paymentName + "USD", paymentInUSD);
//            specificPaymentMap.put("productPaymentTotal" + "USD", paymentInUSD);
        } else {
            specificPaymentMap.put(paymentName + "USD", BigDecimal.ZERO);
//            specificPaymentMap.put("productPaymentTotal" + "USD", BigDecimal.ZERO);
        }
        if (paymentInTHIRD != null) {
            specificPaymentMap.put(paymentName + "THIRD", paymentInTHIRD);
//            specificPaymentMap.put("productPaymentTotal" + "THIRD", paymentInTHIRD);
        } else {
            specificPaymentMap.put(paymentName + "THIRD", BigDecimal.ZERO);
//            specificPaymentMap.put("productPaymentTotal" + "THIRD", BigDecimal.ZERO);
        }


        //Set productPaymentTotal
        if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {
            if (lcCurrency.equalsIgnoreCase("PHP")) {
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentInPHP);
            } else if (lcCurrency.equalsIgnoreCase("USD")) {
                System.out.println("ANGOL ANGOL ANGOL USD USD");
                //TODO::THIS LOOKS WRONG
                BigDecimal amountOrig;
                if (paymentDetail.getSpecialRateUsdToPhp() != null && paymentDetail.getSpecialRateUsdToPhp().compareTo(BigDecimal.ZERO) == 1) {
//                    amountOrig = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateUsdToPhp(), 2, BigDecimal.ROUND_UP);
                    amountOrig = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateUsdToPhp(), 2, BigDecimal.ROUND_HALF_EVEN);
                } else {//use urr there always should be a urr
//                    amountOrig = paymentDetail.getAmount().divide(urrTemp, 2, BigDecimal.ROUND_UP);
                    amountOrig = paymentDetail.getAmount().divide(urrTemp, 2, BigDecimal.ROUND_HALF_EVEN);
                }

                System.out.println("amountOrig:" + amountOrig);
//                System.out.println("amountOrig.multiply(paymentDetail.getUrr()):" + amountOrig.multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
                System.out.println("amountOrig.multiply(paymentDetail.getUrr()):" + amountOrig.multiply(urrTemp).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//                specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.multiply(urrTemp).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            } else {
                System.out.println("ANGOL ANGOL ANGOL THIRD THIRD");
                System.out.println("paymentDetail.getSpecialRateUsdToPhp():" + paymentDetail.getSpecialRateUsdToPhp());
                System.out.println("paymentDetail.getSpecialRateThirdToUsd():" + paymentDetail.getSpecialRateThirdToUsd());
                System.out.println("paymentDetail.getUrr():" + paymentDetail.getUrr());

                //TODO::THIS LOOKS WRONG
                BigDecimal amountOrig;
                if (paymentDetail.getSpecialRateUsdToPhp() != null && paymentDetail.getSpecialRateUsdToPhp().compareTo(BigDecimal.ZERO) == 1 &&
                        paymentDetail.getSpecialRateThirdToUsd() != null && paymentDetail.getSpecialRateThirdToUsd().compareTo(BigDecimal.ZERO) == 1) {
//                    amountOrig = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateUsdToPhp().multiply(paymentDetail.getSpecialRateThirdToUsd()), 2, BigDecimal.ROUND_UP);
                    amountOrig = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateUsdToPhp().multiply(paymentDetail.getSpecialRateThirdToUsd()), 2, BigDecimal.ROUND_HALF_EVEN);
                    System.out.println("amountOrig:" + amountOrig);
//                    specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd().multiply(urrTemp)).setScale(2, BigDecimal.ROUND_UP));
                    specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd().multiply(urrTemp)).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                } else {
                    amountOrig = paymentDetail.getAmount();
//                    specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.setScale(2, BigDecimal.ROUND_UP));
                    specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                    System.out.println("amountOrig:" + amountOrig);
                }
            }
            specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmount());
            specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount());

        } else if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")) {
            specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmount());
            specificPaymentMap.put(paymentName + "USD", paymentDetail.getAmount());
//            specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount().multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
            specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount().multiply(urrTemp).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//            specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmount().multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
            specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmount().multiply(urrTemp).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        } else {
//            specificPaymentMap.put("productPaymentTotal" + "THIRD", paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP));
            specificPaymentMap.put("productPaymentTotal" + "THIRD", paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN));
            if (paymentDetail.getSpecialRateThirdToUsd() != null) {
                specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmount().multiply(paymentDetail.getSpecialRateThirdToUsd().multiply(urrTemp)).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount().multiply(paymentDetail.getSpecialRateThirdToUsd().multiply(urrTemp)).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put(paymentName + "USD", paymentDetail.getAmount().multiply(paymentDetail.getSpecialRateThirdToUsd()).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmount().multiply(paymentDetail.getSpecialRateThirdToUsd()).setScale(2, BigDecimal.ROUND_UP));
            } else {
            	
            	//henry Feb 13 2016
            	BigDecimal thirdToUsd = BigDecimal.valueOf(Double.parseDouble(tradeService.getDetails().get(lcCurrency+"-USD_special_rate_charges_buying").toString()));
            	specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmount().multiply(thirdToUsd).multiply(thirdToUsd.multiply(urrTemp)).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount().multiply(thirdToUsd).multiply(thirdToUsd.multiply(urrTemp)).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put(paymentName + "USD", paymentDetail.getAmount().multiply(thirdToUsd).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmount().multiply(thirdToUsd).setScale(2, BigDecimal.ROUND_UP));
        
            	System.out.println("HENRYMAP: "+specificPaymentMap);
                System.out.println("MISSING paymentDetail.getSpecialRateThirdToUsd(). RATES INCOMPLETE!!!!");
//                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP));
            }

        }
        //added this section for override of currency amounts allowed because of amount in lc currency
        System.out.println("new section new section new section!");
        if (paymentDetail.getAmountInLcCurrency() != null) {
            if (lcCurrency.equalsIgnoreCase("PHP")) {
                System.out.println("new section new section new section! PHP");
                System.out.println("new section new section new section! " + paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmountInLcCurrency());
            } else if (lcCurrency.equalsIgnoreCase("USD")) {
                System.out.println("new section new section new section! USD");
                System.out.println("new section new section new section! " + paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put(paymentName + "USD", paymentDetail.getAmountInLcCurrency());
                if(!paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
//                    specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmountInLcCurrency().multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
                    specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmountInLcCurrency().multiply(urrTemp).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                }
//                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmountInLcCurrency().multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmountInLcCurrency().multiply(urrTemp).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            } else { //thirds
                System.out.println("new section new section new section! THIRD");
                System.out.println("new section new section new section! " + paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put("productPaymentTotal" + "THIRD", paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put(paymentName + "THIRD", paymentDetail.getAmountInLcCurrency());
                if(paymentDetail.getSpecialRateThirdToUsd() != null && paymentDetail.getSpecialRateThirdToUsd().compareTo(BigDecimal.ZERO) == 1){
                    System.out.println("WITH THIRD-USD in paymentdetail");
                    if(!paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")){
//                        specificPaymentMap.put(paymentName + "USD", paymentDetail.getAmountInLcCurrency().multiply(paymentDetail.getSpecialRateThirdToUsd()).setScale(2, BigDecimal.ROUND_UP));
                        specificPaymentMap.put(paymentName + "USD", paymentDetail.getAmountInLcCurrency().multiply(paymentDetail.getSpecialRateThirdToUsd()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                    }
//                    specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmountInLcCurrency().multiply(paymentDetail.getSpecialRateThirdToUsd()).setScale(2, BigDecimal.ROUND_UP));
                    specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmountInLcCurrency().multiply(paymentDetail.getSpecialRateThirdToUsd()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                    if(!paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
//                        specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmountInLcCurrency().multiply(paymentDetail.getSpecialRateThirdToUsd()).multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
                        specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmountInLcCurrency().multiply(paymentDetail.getSpecialRateThirdToUsd()).multiply(urrTemp).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                    }
//                    specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmountInLcCurrency().multiply(paymentDetail.getSpecialRateThirdToUsd()).multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
                    specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmountInLcCurrency().multiply(paymentDetail.getSpecialRateThirdToUsd()).multiply(urrTemp).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                } else if(tradeService.getSpecialRateThirdToUsdServiceCharge()!=null && tradeService.getSpecialRateThirdToUsdServiceCharge().compareTo(BigDecimal.ZERO) ==1) {
                    System.out.println("NO THIRD-USD in paymentdetail");
//                    specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmountInLcCurrency().multiply(tradeService.getSpecialRateThirdToUsdServiceCharge()).setScale(2, BigDecimal.ROUND_UP));
                    specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmountInLcCurrency().multiply(tradeService.getSpecialRateThirdToUsdServiceCharge()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                }
            }
            System.out.println("specificPaymentMap:"+specificPaymentMap);
        }
    }

    /**
     * places payments in specificPaymentMap
     *
     * @param tradeService       Tradeservice object
     * @param paymentDetail      PaymentDetail object to containing payment to be placed
     * @param specificPaymentMap Payment Map to be used in generating Product Payment Entries
     */
    private static void placePaymentsInPaymentMapCorres(TradeService tradeService, PaymentDetail paymentDetail, Map<String, Object> specificPaymentMap) {

        String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
        System.out.println("paymentName:" + paymentName);
        BigDecimal paymentInPHP = getPayment(paymentDetail, "PHP");
        BigDecimal paymentInUSD = getPayment(paymentDetail, "USD");
        BigDecimal paymentInTHIRD = getPayment(paymentDetail, "THIRD");
        
        String corresSettlementCurrency = tradeService.getDetails().get("currency").toString();
        String corresAccountType = "RBU";
        if (tradeService.getDetails().containsKey("accountType")) {
        	corresAccountType = tradeService.getDetails().get("accountType").toString();
        } else {
            if (!corresSettlementCurrency.equalsIgnoreCase("PHP")) {
            	corresAccountType = "FCDU";
            }
        }

        if (paymentInPHP != null) {
            specificPaymentMap.put(paymentName + "PHP", paymentInPHP);
        } else {
            specificPaymentMap.put(paymentName + "PHP", BigDecimal.ZERO);
        }
        if (paymentInUSD != null) {
//            specificPaymentMap.put(paymentName + "USD", paymentInUSD);
//            specificPaymentMap.put("productPaymentTotal" + "USD", paymentInUSD); 
        } else {
            specificPaymentMap.put(paymentName + "USD", BigDecimal.ZERO);
            specificPaymentMap.put("productPaymentTotal" + "USD", BigDecimal.ZERO);
        }
        if (paymentInTHIRD != null) {
            specificPaymentMap.put(paymentName + "THIRD", paymentInTHIRD);
            specificPaymentMap.put("productPaymentTotal" + "THIRD", paymentInTHIRD);
        } else {
            specificPaymentMap.put(paymentName + "THIRD", BigDecimal.ZERO);
            specificPaymentMap.put("productPaymentTotal" + "THIRD", BigDecimal.ZERO);
        }

        String lcCurrency = "";
        if (tradeService.getDetails().containsKey("currency") && tradeService.getDetails().get("currency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("currency");
        } else if (tradeService.getDetails().containsKey("negotiationCurrency") && tradeService.getDetails().get("negotiationCurrency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("negotiationCurrency");
        } else if (tradeService.getDetails().containsKey("settlementCurrency") && tradeService.getDetails().get("settlementCurrency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("settlementCurrency");
        } else if (tradeService.getDetails().containsKey("draftCurrency") && tradeService.getDetails().get("draftCurrency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("draftCurrency");
        } else if (tradeService.getDetails().containsKey("productCurrency") && tradeService.getDetails().get("productCurrency") != null) {
            lcCurrency = (String) tradeService.getDetails().get("productCurrency");
        }

        if(tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)){
            lcCurrency="PHP"; //Added fail safe
        }

        System.out.println("lcCurrency:" + lcCurrency);
        System.out.println("paymentDetail.getAmount():" + paymentDetail.getAmount());
        System.out.println("paymentDetail.getAmountInLcCurrency():" + paymentDetail.getAmountInLcCurrency());
        BigDecimal urrTemp = BigDecimal.ONE;
        if (paymentDetail.getUrr() != null) {
            urrTemp = paymentDetail.getUrr();
        }

        if(urrTemp.compareTo(BigDecimal.ZERO)==0){
            if(tradeService.getDetails().containsKey("urr")){
                urrTemp = getBigDecimalOrZero(tradeService.getDetails().get("urr"));
                System.out.println("urr from details:"+urrTemp);
            }
        }

        if(urrTemp.compareTo(BigDecimal.ZERO)==0){
            if(tradeService.getDetails().containsKey("USD-PHP_urr")){
                urrTemp = getBigDecimalOrZero(tradeService.getDetails().get("USD-PHP_urr"));
                System.out.println("USD-PHP_urr from details:"+urrTemp);
            }
        }
        
        if(urrTemp.compareTo(BigDecimal.ZERO)==0){
            if(tradeService.getPassOnUrrServiceCharge()!=null){
                urrTemp = tradeService.getPassOnUrrServiceCharge();
                System.out.println("urr from getPassOnUrrServiceCharge:"+urrTemp);
            }
        }
        if(urrTemp.compareTo(BigDecimal.ZERO)==0){
            System.out.println("Missing URR Defaulting to BigDecimal.ONE");
            urrTemp = BigDecimal.ONE;
        }

        //Set productPaymentTotal
        if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {
        	
            if (lcCurrency.equalsIgnoreCase("PHP")) {
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentInPHP);
            } else if (lcCurrency.equalsIgnoreCase("USD")) {
                BigDecimal amountOrig;
                if (paymentDetail.getSpecialRateUsdToPhp() != null && paymentDetail.getSpecialRateUsdToPhp().compareTo(BigDecimal.ZERO) == 1) {
                    amountOrig = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateUsdToPhp(), 2, BigDecimal.ROUND_UP);
                } else {//use urr there always should be a urr
                    amountOrig = paymentDetail.getAmount().divide(urrTemp, 2, BigDecimal.ROUND_UP);
                }

                System.out.println("amountOrig:" + amountOrig);
                System.out.println("amountOrig.multiply(paymentDetail.getUrr()):" + amountOrig.multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
                specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
            } else {
                System.out.println("paymentDetail.getSpecialRateUsdToPhp():" + paymentDetail.getSpecialRateUsdToPhp());
                System.out.println("paymentDetail.getSpecialRateThirdToUsd():" + paymentDetail.getSpecialRateThirdToUsd());
                System.out.println("paymentDetail.getUrr():" + paymentDetail.getUrr());

                BigDecimal amountOrig;
                if (paymentDetail.getSpecialRateUsdToPhp() != null && paymentDetail.getSpecialRateUsdToPhp().compareTo(BigDecimal.ZERO) == 1 &&
                        paymentDetail.getSpecialRateThirdToUsd() != null && paymentDetail.getSpecialRateThirdToUsd().compareTo(BigDecimal.ZERO) == 1) {
                    amountOrig = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateUsdToPhp().multiply(paymentDetail.getSpecialRateThirdToUsd()), 2, BigDecimal.ROUND_UP);
                    System.out.println("amountOrig:" + amountOrig);
                    specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd().multiply(urrTemp)).setScale(2, BigDecimal.ROUND_UP));
                    specificPaymentMap.put("productPaymentTotal" + "USD", amountOrig.divide(paymentDetail.getSpecialRateThirdToUsd(),2,BigDecimal.ROUND_UP));
                } else {
                    amountOrig = paymentDetail.getAmount();
                    specificPaymentMap.put("productPaymentTotal" + "PHP", amountOrig.setScale(2, BigDecimal.ROUND_UP));
                    specificPaymentMap.put("productPaymentTotal" + "USD", amountOrig.setScale(2, BigDecimal.ROUND_UP));
                    System.out.println("amountOrig:" + amountOrig);
                }
            }
        } else if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")) {
            specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount().multiply(urrTemp).setScale(2, BigDecimal.ROUND_UP));
        } else {
            if (paymentDetail.getSpecialRateThirdToUsd() != null) {
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount().multiply(paymentDetail.getSpecialRateThirdToUsd().multiply(urrTemp)).setScale(2, BigDecimal.ROUND_UP));
            } else {
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP));
            }

        }
        //added this section for override of currency amounts allowed because of amount in lc currency
        System.out.println("new section new section new section!");
        if (paymentDetail.getAmountInLcCurrency() != null) {
            if (lcCurrency.equalsIgnoreCase("PHP")) {
                System.out.println("new section new section new section! PHP");
                System.out.println("new section new section new section! " + paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put("productPaymentTotal" + "PHP", paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put(paymentName + "PHP", paymentDetail.getAmountInLcCurrency());
            } else if (lcCurrency.equalsIgnoreCase("USD")) {
                System.out.println("new section new section new section! USD");
                System.out.println("new section new section new section! " + paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put("productPaymentTotal" + "USD", paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put(paymentName + "USD", paymentDetail.getAmountInLcCurrency());
            } else { //thirds
                System.out.println("new section new section new section! THIRD");
                System.out.println("new section new section new section! " + paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put("productPaymentTotal" + "THIRD", paymentDetail.getAmountInLcCurrency());
                specificPaymentMap.put(paymentName + "THIRD", paymentDetail.getAmountInLcCurrency());
            }
        }
    }

    /**
     * places excess payments in specificPaymentMap
     *
     * @param tScExcess          Map containing Excess Payments to be placed in specificPaymentMap
     * @param specificPaymentMap Map to be used in Generating Excess Entries
     */
    private void placeExcessInPaymentMap(Map<String, Object> tScExcess, Map<String, Object> specificPaymentMap) {
        BigDecimal excessPHP = BigDecimal.ZERO;
        BigDecimal excessUSD = BigDecimal.ZERO;
        BigDecimal excessTHIRD = BigDecimal.ZERO;
        if (tScExcess.containsKey("excessPaymentPHP") && tScExcess.get("excessPaymentPHP") != null) {
            excessPHP = (BigDecimal) tScExcess.get("excessPaymentPHP");
        }

        if (tScExcess.containsKey("excessPaymentUSD") && tScExcess.get("excessPaymentUSD") != null) {
            excessUSD = (BigDecimal) tScExcess.get("excessPaymentUSD");
        }

        if (tScExcess.containsKey("excessPaymentTHIRD") && tScExcess.get("excessPaymentTHIRD") != null) {
            excessTHIRD = (BigDecimal) tScExcess.get("excessPaymentTHIRD");
        }


        if (excessPHP != null) {
            specificPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPayment" + "PHP", excessPHP);
        } else {
            specificPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPayment" + "PHP", BigDecimal.ZERO);
        }
        if (excessUSD != null) {
            specificPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPayment" + "USD", excessUSD);
        } else {
            specificPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPayment" + "USD", BigDecimal.ZERO);
        }

        if (excessTHIRD != null) {
            specificPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPayment" + "THIRD", excessTHIRD);
        } else {
            specificPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPayment" + "THIRD", BigDecimal.ZERO);
        }
        System.out.println("specificPaymentMap:::::::::::::::::::::::::" + specificPaymentMap);
    }

    /**
     * places FX Profit Or Loss Amounts in specificPaymentMap where lcCurency determines the exchange rate used
     *
     * @param paymentDetail      PaymentDetail object which will be charged for excess
     * @param specificPaymentMap Map where profit or loss k-v pair will be inserted
     * @param lcCurrency         Currency of the Product in String
     */
    private void placeFxProfitOrLossInPaymentMap(TradeService tradeService, PaymentDetail paymentDetail, Map<String, Object> specificPaymentMap, String lcCurrency) {
        System.out.println("placeFxProfitOrLossInPaymentMap(PaymentDetail paymentDetail, Map<String, Object> specificPaymentMap, String lcCurrency)");
        String paymentName = getPaymentName(paymentDetail.getPaymentInstrumentType().toString(), paymentDetail.getCurrency());
        System.out.println("paymentName:" + paymentName);
        String profitString;
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal amountOrig = BigDecimal.ZERO;
        BigDecimal amountUrrToPassOn = BigDecimal.ZERO;
        BigDecimal amountPassOnToSpecial = BigDecimal.ZERO;
        BigDecimal amountOneCent = BigDecimal.ZERO;
        BigDecimal amountCentOther = BigDecimal.ZERO;
        try {
            if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {

                if (paymentDetail.getUrr() != null && paymentDetail.getSpecialRateUsdToPhp() != null) {
                    BigDecimal differenceMultiplier = paymentDetail.getSpecialRateUsdToPhp().subtract(paymentDetail.getUrr());
                    BigDecimal diffSpecialToPassOnUsdToPhp = paymentDetail.getSpecialRateUsdToPhp().subtract(paymentDetail.getPassOnRateUsdToPhp());
                    BigDecimal diffPassOnToUrr = differenceMultiplier.subtract(diffSpecialToPassOnUsdToPhp);
                    BigDecimal diffOneCent = new BigDecimal("0.01"); //Parameterize this
                    BigDecimal diffOther = new BigDecimal("0.01");
                    if(paymentDetail.getSpecialRateUsdToPhp().compareTo(paymentDetail.getPassOnRateUsdToPhp())==0){
                        //pass on is equal to special rate apply one cent for RM/BR one the remaining to Treasury
                        diffSpecialToPassOnUsdToPhp = BigDecimal.ZERO;
                        diffOther = differenceMultiplier.subtract(diffOneCent);
                    }
                    System.out.println("differenceMultiplier:" + differenceMultiplier);
                    if (differenceMultiplier.compareTo(BigDecimal.ZERO) == 1) {
                        profitString = "fxProfitPHP";
                        System.out.println(differenceMultiplier);
                        System.out.println("profitString:" + profitString);
                    } else {
                        profitString = "fxLossPHP";
                        differenceMultiplier = paymentDetail.getUrr().subtract(paymentDetail.getSpecialRateUsdToPhp());
                        diffSpecialToPassOnUsdToPhp =  paymentDetail.getPassOnRateUsdToPhp().subtract(paymentDetail.getSpecialRateUsdToPhp());
                        diffPassOnToUrr = differenceMultiplier.subtract(diffSpecialToPassOnUsdToPhp);

                        System.out.println(differenceMultiplier);
                        System.out.println("profitString:" + profitString);
                    }

                    if (lcCurrency.equalsIgnoreCase("PHP")) {
                        System.out.println("PHP in FX profit loss: NONE");
                        amount = BigDecimal.ZERO;
                    } else if (lcCurrency.equalsIgnoreCase("USD")) {
                        System.out.println("USD in FX profit loss");
                        amountOrig = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateUsdToPhp(), 8, BigDecimal.ROUND_HALF_UP);
                        System.out.println("paymentDetail.getAmount() in php:" + paymentDetail.getAmount());
                        System.out.println("amountOrig :" + amountOrig);

                        amount = differenceMultiplier.multiply(amountOrig).setScale(2, BigDecimal.ROUND_HALF_UP);
                        amountPassOnToSpecial = diffSpecialToPassOnUsdToPhp.multiply(amountOrig);
                        amountUrrToPassOn = amount.subtract(amountPassOnToSpecial);
                        amountOneCent = diffOneCent.multiply(amountOrig).setScale(2, BigDecimal.ROUND_HALF_UP);
                        amountCentOther = amount.subtract(amountOneCent);
                        System.out.println("fx profit loss amount :" + amount);

                    } else {//if (!lcCurrency.equalsIgnoreCase("USD") && !lcCurrency.equalsIgnoreCase("PHP")) {
                        //THIRD
                        System.out.println("THIRD in FX profit loss");
                        System.out.println("paymentDetail.getAmount() in php:" + paymentDetail.getAmount());
                        System.out.println("paymentDetail.getSpecialRateThirdToUsd():" + paymentDetail.getSpecialRateThirdToUsd());

                         amountOrig = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateUsdToPhp().multiply(paymentDetail.getSpecialRateThirdToUsd()), 2, BigDecimal.ROUND_HALF_UP
                        );
                        System.out.println("paymentDetail.getAmount() in php:" + paymentDetail.getAmount());
                        System.out.println("amountOrig :" + amountOrig);

                        amount = differenceMultiplier.multiply(amountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd()));
                        amountPassOnToSpecial = diffSpecialToPassOnUsdToPhp.multiply(amountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd()));
                        amountUrrToPassOn = amount.subtract(amountPassOnToSpecial.multiply(paymentDetail.getSpecialRateThirdToUsd()));

                        amountOneCent = diffOneCent.multiply(amountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd())).setScale(2, BigDecimal.ROUND_HALF_UP);
                        amountCentOther = amount.subtract(amountOneCent);
                        System.out.println("fx profit loss amount :" + amount);
                    }

                    ProfitLossHolder profitLossHolder = new ProfitLossHolder(
                            tradeService.getTradeServiceId().toString(),
                            paymentDetail.getId().toString(),
                            amountOrig,
                            paymentDetail.getAmount() ,
                            amount, amountUrrToPassOn, amountPassOnToSpecial, amountOneCent,amountCentOther);

                    profitLossHolderRepository.delete(profitLossHolder.getTradeServiceId(),profitLossHolder.getPaymentDetailId());
                    profitLossHolderRepository.save(profitLossHolder);

                    System.out.println("profitString:" + profitString);
                    System.out.println("amount:" + amount);
                    if (amount != null) {
                        specificPaymentMap.put(profitString, amount);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * prints out the details of the paymentDetail
     *
     * @param paymentDetail the PaymentDetail whose details will be printed
     */
    private static void printPaymentDetails(PaymentDetail paymentDetail) {
        System.out.println("paymentDetail.getAmount():" + paymentDetail.getAmount());
        System.out.println("paymentDetail.getPaymentInstrumentType().toString():" + paymentDetail.getPaymentInstrumentType().toString());
        System.out.println("paymentDetail.getCurrency():" + paymentDetail.getCurrency());
        System.out.println("paymentDetail.getReferenceNumber():" + paymentDetail.getReferenceNumber()); //TradeSuspenseAccountNo CASA AccountNo
        System.out.println("paymentDetail.getReferenceId():" + paymentDetail.getReferenceId()); //AP
        System.out.println("paymentDetail.getStatus():" + paymentDetail.getStatus());
        System.out.println("paymentDetail.getPassOnRateThirdToPhp():" + paymentDetail.getPassOnRateThirdToPhp());
        System.out.println("paymentDetail.getPassOnRateThirdToUsd():" + paymentDetail.getPassOnRateThirdToUsd());
        System.out.println("paymentDetail.getPassOnRateUsdToPhp():" + paymentDetail.getPassOnRateUsdToPhp());
        System.out.println("paymentDetail.getSpecialRateThirdToPhp():" + paymentDetail.getSpecialRateThirdToPhp());
        System.out.println("paymentDetail.getSpecialRateThirdToUsd():" + paymentDetail.getSpecialRateThirdToUsd());
        System.out.println("paymentDetail.getSpecialRateUsdToPhp():" + paymentDetail.getSpecialRateUsdToPhp());
        System.out.println("paymentDetail.getUrr():" + paymentDetail.getUrr());
    }

    /**
     * extracts the payment amount from paymentDetail and converts to the currencyCode
     *
     * @param paymentDetail the payment detail of the payment
     * @param currencyCode  the currency code
     * @return Payment Amount
     *         TODO re evaluate this method
     */
    private static BigDecimal getPayment(PaymentDetail paymentDetail, String currencyCode) {
        BigDecimal amountPHP;
        BigDecimal amountUSD;
        BigDecimal amountTHIRD;

        if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {
            amountPHP = paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP);

            if (paymentDetail.getUrr() != null && paymentDetail.getUrr().compareTo(BigDecimal.ZERO) == 1) {
                amountUSD = amountPHP.divide(paymentDetail.getUrr(), 2, BigDecimal.ROUND_UP);
            } else {
                amountUSD = BigDecimal.ZERO;
            }

            if (paymentDetail.getSpecialRateThirdToUsd() != null && paymentDetail.getSpecialRateThirdToUsd().compareTo(BigDecimal.ZERO) == 1
                    && paymentDetail.getUrr() != null && paymentDetail.getUrr().compareTo(BigDecimal.ZERO) == 1
                    ) {
                amountTHIRD = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateThirdToUsd(), 12, BigDecimal.ROUND_UP);
                amountTHIRD = amountTHIRD.multiply(paymentDetail.getUrr());
            } else {
                amountTHIRD = BigDecimal.ZERO;
            }


        } else if (paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")) {

            amountUSD = paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP);
            if (paymentDetail.getUrr() != null) {
                amountPHP = amountUSD.multiply(paymentDetail.getUrr()).setScale(2, BigDecimal.ROUND_UP);
            } else {
                amountPHP = BigDecimal.ZERO;
            }

            if (paymentDetail.getSpecialRateThirdToUsd() != null && paymentDetail.getSpecialRateThirdToUsd().compareTo(BigDecimal.ZERO) == 1) {
                amountTHIRD = paymentDetail.getAmount().divide(paymentDetail.getSpecialRateThirdToUsd(), 2, BigDecimal.ROUND_UP);
            } else {
                amountTHIRD = BigDecimal.ZERO;
            }

        } else {
            //currency is THIRD
            amountTHIRD = paymentDetail.getAmount().setScale(2, BigDecimal.ROUND_UP);

            if (paymentDetail.getSpecialRateThirdToUsd() != null && paymentDetail.getSpecialRateThirdToUsd().compareTo(BigDecimal.ZERO) == 1
                    && paymentDetail.getUrr() != null && paymentDetail.getUrr().compareTo(BigDecimal.ZERO) == 1
                    ) {
                amountPHP = paymentDetail.getAmount().multiply(paymentDetail.getSpecialRateThirdToUsd());
                amountPHP = amountPHP.multiply(paymentDetail.getUrr()).setScale(2, BigDecimal.ROUND_UP);
            } else {
                amountPHP = BigDecimal.ZERO;
            }


            if (paymentDetail.getSpecialRateThirdToUsd() != null) {
                amountUSD = amountTHIRD.divide(paymentDetail.getSpecialRateThirdToUsd(), 2, BigDecimal.ROUND_UP);
            } else {
                amountUSD = BigDecimal.ZERO;
            }
        }

        if (currencyCode.equalsIgnoreCase("PHP")) {
            return amountPHP;
        } else if (currencyCode.equals("USD")) {
            return amountUSD;
        } else {
            return amountTHIRD;
        }

    }

    /**
     * returns the correct string payment name to be used given the payment instrument type in string format and the currency
     *
     * @param s        Payment Instrument Type
     * @param currency Currency of Payment
     * @return Payment Name
     *         TODO: May need to be refactored to a configuration
     */
    private static String getPaymentName(String s, Currency currency) {
        System.out.println("Payment Instrument Type:" + s);

        String rootWord = "";
        //TODO: Cross check with what is stored in database and Excel File


        if (currency.getCurrencyCode().equalsIgnoreCase("PHP")) {
            if (s.equalsIgnoreCase("CASA")) {
                rootWord = "CASAproductPaymentTotal";
            } else if (s.equalsIgnoreCase("CASH")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("DBP")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("CHECK")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("REMITTANCE")) {
                rootWord = "APRemmittanceproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TFS_SETUP_AR")) {
                rootWord = "ARproductPaymentTotal";
            } else if (s.equalsIgnoreCase("AR")) {
                rootWord = "ARproductPaymentTotal";
            } else if (s.equalsIgnoreCase("IBT_BRANCH")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TR_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("DTR_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("IB_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("UA_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("APPLY_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TFS_SETUP_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("OUTSIDE_SETUP_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("MD")) {
                rootWord = "MDproductPaymentTotal";
            } else if (s.equalsIgnoreCase("EBP")) {
                rootWord = "EBPproductPaymentTotal";
            }
        } else if (currency.getCurrencyCode().equalsIgnoreCase("USD")) {
            if (s.equalsIgnoreCase("CASA")) {
                rootWord = "CASAproductPaymentTotal";
            } else if (s.equalsIgnoreCase("CASH")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("CHECK")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("REMITTANCE")) {
                rootWord = "APRemmittanceproductPaymentTotal";
            } else if (s.equalsIgnoreCase("DBP")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TFS_SETUP_AR")) {
                rootWord = "ARproductPaymentTotal";
            } else if (s.equalsIgnoreCase("AR")) {
                rootWord = "ARproductPaymentTotal";
            } else if (s.equalsIgnoreCase("IBT_BRANCH")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TR_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("DTR_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("IB_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("UA_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("APPLY_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TFS_SETUP_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("OUTSIDE_SETUP_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("MD")) {
                rootWord = "MDproductPaymentTotal";
            } else if (s.equalsIgnoreCase("EBP")) {
                rootWord = "EBPproductPaymentTotal";
            }
        } else {
            if (s.equalsIgnoreCase("CASA")) {
                rootWord = "CASAproductPaymentTotal";
            } else if (s.equalsIgnoreCase("CASH")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("CHECK")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("REMITTANCE")) {
                rootWord = "APRemmittanceproductPaymentTotal";
            } else if (s.equalsIgnoreCase("DBP")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TFS_SETUP_AR")) {
                rootWord = "ARproductPaymentTotal";
            } else if (s.equalsIgnoreCase("AR")) {
                rootWord = "ARproductPaymentTotal";
            } else if (s.equalsIgnoreCase("IBT_BRANCH")) {
                rootWord = "CheckproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TR_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("DTR_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("IB_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("UA_LOAN")) {
                rootWord = "DBPDTRLoanproductPaymentTotal";
            } else if (s.equalsIgnoreCase("APPLY_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("TFS_SETUP_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("OUTSIDE_SETUP_AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("AP")) {
                rootWord = "APRESOTHERSproductPaymentTotal";
            } else if (s.equalsIgnoreCase("MD")) {
                rootWord = "MDproductPaymentTotal";
            } else if (s.equalsIgnoreCase("EBP")) {
                rootWord = "EBPproductPaymentTotal";
            }
        }

        return rootWord;

    }

    /**
     * returns the correct string settlement name to be used given the payment instrument type in string format and the currency
     *
     * @param s Payment Instrument Type
     * @return Settlement Name
     *         TODO: May need to be refactored to a configuration
     */
    private static String getSettlementName(String s) {
        System.out.println("Settlement Type:" + s);

        String rootWord = "DUEFromFBsettlementTotal";
        //TODO: Cross check with what is stored in database and Excel File

        if (s.equalsIgnoreCase("CASA")) {
            rootWord = "CASAsettlementTotal";
        } else if (s.equalsIgnoreCase("MC_ISSUANCE")) {
            rootWord = "MCsettlementTotal";
        } else if (s.equalsIgnoreCase("PDDTS")) {
            rootWord = "PDDTSsettlementTotal";
        } else if (s.equalsIgnoreCase("SWIFT")) {
            rootWord = "DUEFromCBsettlementTotal";
        } else if (s.equalsIgnoreCase("DEMAND_DEPOSIT")) {
            rootWord = "DUEFromCBsettlementTotal";
        } else if (s.equalsIgnoreCase("IBT_BRANCH")) {
        	//henry
        	rootWord = "IBTsettlementTotal";
           // rootWord = "CASAsettlementTotal";
//            rootWord = "APRemmittancesettlementTotal";
        } else if (s.equalsIgnoreCase("REMITTANCE")) {     //TODO Add support for IBT BRANCH
            rootWord = "APRemmittancesettlementTotal";
        } else if (s.equalsIgnoreCase("DUE_FROM_FOREIGN_BANK")) {     //TODO Add support for IBT BRANCH
            rootWord = "DUEFromFBsettlementTotal";
        }

        return rootWord;

    }

    /**
     * returns the correct string settlement name to be used given the payment instrument type in string format and the currency
     *
     * @param s Payment Instrument Type
     * @return Settlement Name
     *         TODO: May need to be refactored to a configuration
     */
    private static String getSettlementNameEBP(String s) {
        System.out.println("getSettlementNameEBP Settlement Type:" + s);

        String rootWord = "DUEFromFBsettlementTotal";
        //TODO: Cross check with what is stored in database and Excel File

        if (s.equalsIgnoreCase("CASA")) {
            rootWord = "CASAsettlementTotal";
        } else if (s.equalsIgnoreCase("MC_ISSUANCE")) {
            rootWord = "MCsettlementTotal";
        } else if (s.equalsIgnoreCase("PDDTS")) {
            rootWord = "PDDTSsettlementTotal";
        } else if (s.equalsIgnoreCase("SWIFT")) {
            rootWord = "DUEFromCBsettlementTotal";
        } else if (s.equalsIgnoreCase("DEMAND_DEPOSIT")) {
            rootWord = "DUEFromCBsettlementTotal";
        } else if (s.equalsIgnoreCase("IBT_BRANCH")) {
            System.out.println("IN IBT Branch");
            rootWord = "IBTsettlementTotal";
        } else if (s.equalsIgnoreCase("REMITTANCE")) {     //TODO Add support for IBT BRANCH
            rootWord = "APRemmittancesettlementTotal";
        } else if (s.equalsIgnoreCase("DUE_FROM_FOREIGN_BANK")) {     //TODO Add support for IBT BRANCH
            rootWord = "DUEFromFBsettlementTotal";
        }

        return rootWord;

    }

    /**
     * returns the correct string settlement name to be used given the payment instrument type in string format and the currency
     *
     * @param s Payment Instrument Type
     * @return Settlement Name
     *         TODO: May need to be refactored to a configuration
     */
    private static String getBookCodeFromSettlementName(String s, String currency) {
        System.out.println("Settlement Type:" + s);

        String rootWord = "-FC-BOOK";
        //TODO: Cross check with what is stored in database and Excel File

        if (s.equalsIgnoreCase("CASA")) {
            if(currency.equalsIgnoreCase("PHP")){
                rootWord = "-RG-BOOK";
            } else {
                rootWord = "-FC-BOOK";
            }
            rootWord = "-FC-BOOK";
        } else if (s.equalsIgnoreCase("MC_ISSUANCE")) {
            rootWord = "-RG-BOOK";
        } else if (s.equalsIgnoreCase("PDDTS")) {
            rootWord = "-RG-BOOK";
        } else if (s.equalsIgnoreCase("SWIFT")) {
            rootWord = "-FC-BOOK";
        } else if (s.equalsIgnoreCase("DEMAND_DEPOSIT")) {
            rootWord = "-FC-BOOK";
        } else if (s.equalsIgnoreCase("DUE_FROM_FOREIGN_BANK")) {
            rootWord = "-FC-BOOK";
        }

        return rootWord;

    }

    /**
     * Accounting entries for Settlement for Nego Amount Imports can be grouped either debit-casa-ar or IB_LOAN/TR_LOAN
     * This returns the equivalent base AccountingEventId for the payment type of the payment detail used
     * This is used for DM there is an equivalent for FX
     *
     * @param lcCurrency  currency of the Product
     * @param paymentName the paymentName that needs to be matched to an accountingEventTypeIdString
     * @return accountingEventTypeIdString
     *         TODO: May need to be refactored to a configuration
     */
    private static String getAccountingEventIdStringPaymentOrLoan_DM(String lcCurrency, String paymentName) {

        System.out.println("getAccountingEventIdStringPaymentOrLoan_DM:");
        System.out.println("payment:" + paymentName);
        System.out.println("lcCurrency:" + lcCurrency);
        String accountingEventTypeIdString;
        accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT";
        if (paymentName.equalsIgnoreCase("TR_LOAN")) {
            accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT-VIA-TR-LOAN";
        } else if (paymentName.equalsIgnoreCase("MD")) {
            System.out.println("angulo angulo angulo MD");
            accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT-VIA-MD-OTHERS";
        }

        return accountingEventTypeIdString;
    }

    /**
     * Accounting entries for Settlement for Nego Amount Imports can be grouped either debit-casa-ar or IB_LOAN/TR_LOAN
     * This returns the equivalent base AccountingEventId for the payment type of the payment detail used
     * This is used for FX there is an equivalent for DM
     *
     * @param paymentDetail PaymentDetails object used to determine correct accountingEventTypeIdString
     * @return accountingEventTypeIdString
     *         TODO: May need to be refactored to a configuration table
     */
    private static String getAccountingEventIdStringPaymentOrLoan_FX(PaymentDetail paymentDetail) {
        String accountingEventTypeIdString;
        accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA DEBIT CASA-AP-REMITTANCE-AR";
        if (
                paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("IB_LOAN")
                        || paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("TR_LOAN")
                        || paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("UA_LOAN")) {
            accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA IB TR LOAN";    //SETTLEMENT-NEGO-AMOUNT-VIA-IB-TR-LOAN
        } else if (paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("MD")) {
            accountingEventTypeIdString = "SETTLEMENT-NEGO-AMOUNT VIA MD OTHERS";
        }
        return accountingEventTypeIdString;
    }

    /**
     * returns accountingEventTypeIdString appended with correct book code
     *
     * @param accountType                 String code that determines whether it is FCDU or RBU
     * @param accountingEventTypeIdString String to append the correct book postfix (-FC-BOOK/-RG-BOOK)
     * @return accountingEventTypeIdString appended with correct book code
     */
    private static String getBookCodeStringPostFix(String accountType, String accountingEventTypeIdString) {
        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEE>" + accountType);
        if (accountType.equalsIgnoreCase("RBU")) {
            accountingEventTypeIdString += "-RG-BOOK";
        } else {
            accountingEventTypeIdString += "-FC-BOOK";
        }
        return accountingEventTypeIdString;
    }

    /**
     * Uses product charge to determine the necessary Product Amount to be used
     *
     * @param details           contains the Tradeservice details map
     * @param amountsSummaryMap amounts returned
     * @param tradeService      tradeService whose product amount map is to be extracted from
     * @param lcCurrency        currency of the lc/product
     * @param amountName        the name of the amount used
     * @return Map containing the amounts used
     */
    private static Map<String, Object> generate_IMPORTS_ProductAmount_ValuesMap(Map<String, Object> details, Map<String, List<Object>> amountsSummaryMap, TradeService tradeService, String lcCurrency, String amountName) {
        System.out.println("generate_IMPORTS_ProductAmount_ValuesMap");
        Map<String, Object> lcMap = new HashMap<String, Object>();
        try {

            BigDecimal specialRateUsdToPhpServiceCharge = BigDecimal.ZERO;

            if (details.containsKey("USD-PHP_special_rate_charges")) {
                specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges"));
            } else if (details.containsKey("USD-PHP_special_rate_cash")) {
                specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_cash"));
            } else if (details.containsKey("USD-PHP_text_special_rate")) {
                specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_text_special_rate"));
            } else if (details.containsKey("USD-PHP")) {
                specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP"));
            }

            BigDecimal passOnRateUsdToPhpServiceCharge = BigDecimal.ZERO;

            if (details.containsKey("USD-PHP_pass_on_rate_charges")) {
                passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges"));
            } else if (details.containsKey("USD-PHP_pass_on_rate_cash")) {
                passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_cash"));
            } else if (details.containsKey("USD-PHP_text_pass_on_rate")) {
                passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_text_pass_on_rate"));
            } else if (details.containsKey("USD-PHP")) {
                passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP"));
            }

            BigDecimal specialRateThirdToPhpServiceCharge;
            BigDecimal passOnRateThirdToPhpServiceCharge;

            BigDecimal specialRateThirdToUsdServiceCharge = BigDecimal.ZERO;
            BigDecimal passOnRateThirdToUsdServiceCharge = BigDecimal.ZERO;

            BigDecimal passOnUrrServiceCharge = BigDecimal.ZERO;
            if (details.containsKey("USD-PHP_urr")) {
                passOnUrrServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_urr"));
            } else if (details.containsKey("urr")) {
                passOnUrrServiceCharge = getBigDecimalOrZero(details.get("urr"));
            }
            if (!(lcCurrency.equalsIgnoreCase("PHP") || lcCurrency.equalsIgnoreCase("USD"))) {
                System.out.println("because these values are null extract details map..");

                passOnRateThirdToPhpServiceCharge = BigDecimal.ZERO;
                specialRateThirdToPhpServiceCharge = BigDecimal.ZERO;

                if (details.containsKey(lcCurrency + "-USD_pass_on_rate_charges")) {
                    passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_pass_on_rate_charges"));
                } else if (details.containsKey(lcCurrency + "-USD_pass_on_rates_cash")) {
                    passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_text_pass_on_rates_cash"));
                } else if (details.containsKey(lcCurrency + "-USD_text_pass_on_rates")) {
                    passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_text_pass_on_rates"));
                } else if (details.containsKey(lcCurrency + "-USD")) {
                    passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD"));
                }

                if (details.containsKey(lcCurrency + "-USD_special_rate_charges")) {
                    specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_special_rate_charges"));
                } else if (details.containsKey(lcCurrency + "-USD_special_rate_cash")) {
                    specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_special_rate_cash"));
                } else if (details.containsKey(lcCurrency + "-USD_text_special_rates")) {
                    passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD_text_special_rates"));
                } else if (details.containsKey(lcCurrency + "-USD")) {
                    specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(lcCurrency + "-USD"));
                }

            } else {

                passOnRateThirdToPhpServiceCharge = BigDecimal.ZERO;
                specialRateThirdToPhpServiceCharge = BigDecimal.ZERO;

                passOnRateThirdToUsdServiceCharge = BigDecimal.ZERO;
                specialRateThirdToUsdServiceCharge = BigDecimal.ZERO;

            }

            System.out.println("passOnRateThirdToPhpServiceCharge:" + passOnRateThirdToPhpServiceCharge);
            System.out.println("specialRateThirdToPhpServiceCharge:" + specialRateThirdToPhpServiceCharge);
            System.out.println("passOnRateThirdToUsdServiceCharge:" + passOnRateThirdToUsdServiceCharge);
            System.out.println("specialRateThirdToUsdServiceCharge:" + specialRateThirdToUsdServiceCharge);
            System.out.println("passOnRateUsdToPhpServiceCharge:" + passOnRateUsdToPhpServiceCharge);
            System.out.println("specialRateUsdToPhpServiceCharge:" + specialRateUsdToPhpServiceCharge);
            System.out.println("passOnUrrServiceCharge:" + passOnUrrServiceCharge);

            //TODO check computations
            for (String s : amountsSummaryMap.keySet()) {
                System.out.println("Key:" + s + ":Value:" + amountsSummaryMap.get(s));
                List<Object> tempList = amountsSummaryMap.get(s);
                if (tempList != null) {
                    for (Object o : tempList) {
                        if (o != null) {

                            if (s.equalsIgnoreCase("PC")) {
                                ProductCharge tempPC = (ProductCharge) o;
                                System.out.println("|Product Charge: " + "-Currency:" + tempPC.getCurrency() + "-Amount:" + tempPC.getAmount());

                                //Convert to produce the three lcAmount
                                if (tempPC.getCurrency().toString().equalsIgnoreCase("PHP")) {
                                    lcMap.put(amountName + "PHP", tempPC.getAmount());
                                    if (specialRateUsdToPhpServiceCharge != null && specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "USD", tempPC.getAmount().divide(specialRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                                    } else if (passOnRateUsdToPhpServiceCharge != null && passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "USD", tempPC.getAmount().divide(passOnRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                                    }

                                    if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0 &&
                                            passOnUrrServiceCharge != null && passOnUrrServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "THIRD", tempPC.getAmount().divide(specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge), 2, BigDecimal.ROUND_HALF_UP));
                                    } else if (passOnRateThirdToUsdServiceCharge != null && passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0 &&
                                            passOnUrrServiceCharge != null && passOnUrrServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "THIRD", tempPC.getAmount().divide(passOnRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge), 2, BigDecimal.ROUND_HALF_UP));
                                    }
                                } else if (tempPC.getCurrency().toString().equalsIgnoreCase("USD")) {
                                    lcMap.put(amountName + "USD", tempPC.getAmount());

                                    lcMap.put(amountName + "PHP", tempPC.getAmount().multiply(passOnUrrServiceCharge).setScale(2, BigDecimal.ROUND_HALF_UP));

                                    if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0 &&
                                            passOnUrrServiceCharge != null && passOnUrrServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "THIRD", tempPC.getAmount().divide(specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge), 2, BigDecimal.ROUND_HALF_UP));
                                    } else if (passOnRateThirdToUsdServiceCharge != null && passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0 &&
                                            passOnUrrServiceCharge != null && passOnUrrServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "THIRD", tempPC.getAmount().divide(passOnRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge), 2, BigDecimal.ROUND_HALF_UP));
                                    }
                                } else {
                                    lcMap.put(amountName + "THIRD", tempPC.getAmount());

                                    if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "USD", tempPC.getAmount().multiply(specialRateThirdToUsdServiceCharge).setScale(2, BigDecimal.ROUND_HALF_UP));
                                    } else if (passOnRateThirdToUsdServiceCharge != null && passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "USD", tempPC.getAmount().multiply(passOnRateThirdToUsdServiceCharge).setScale(2, BigDecimal.ROUND_HALF_UP));
                                    }


                                    if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "PHP", tempPC.getAmount().multiply(specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge)).setScale(2, BigDecimal.ROUND_HALF_UP));
                                    } else if (passOnRateThirdToUsdServiceCharge != null && passOnRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) != 0) {
                                        lcMap.put(amountName + "PHP", tempPC.getAmount().multiply(passOnRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge)).setScale(2, BigDecimal.ROUND_HALF_UP));
                                    }

                                }

                                System.out.println(amountName + "PHP:" + lcMap.get(amountName + "PHP"));
                                System.out.println(amountName + "USD:" + lcMap.get(amountName + "USD"));
                                System.out.println(amountName + "THIRD:" + lcMap.get(amountName + "THIRD"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return lcMap;
    }

    /**
     * Returns ChargeMap Map<String, Object> that contains all charges and equivalent conversion if it exists for EXPORTS.
     *
     * @param details           the details map of the TradeService where the import charges will be extracted and added to the returned Map
     * @param cilexCharged      flag whether cilex will be charged or not
     * @param chargesSummaryMap the map that will be modified then returned
     * @param tradeService      The TradeService where the import charges will be extracted and added to the returned Map
     * @param chargesCurrency   currency of the charge
     * @return Map containing the import charges
     */
    private static Map<String, Object> generate_EXPORTS_CHARGES_ValuesMap(Map<String, Object> details, Boolean cilexCharged, Map<String, List<Object>> chargesSummaryMap, TradeService tradeService, String chargesCurrency, Payment paymentSettlement) {

        int chargesRoundingMode = BigDecimal.ROUND_UP;

        Map<String, Object> chargeMap = new HashMap<String, Object>();
        BigDecimal chargesAmount = BigDecimal.ZERO;
        BigDecimal chargesAmountCWT = BigDecimal.ZERO;
        BigDecimal chargesAmountNet = BigDecimal.ZERO;
        BigDecimal chargesAmountOrig = BigDecimal.ZERO;
        String cwtFlag = details.get("cwtFlag") != null ? details.get("cwtFlag").toString() : "N";
        System.out.println("cwtFlag:" + cwtFlag);

        BigDecimal specialRateUsdToPhpServiceCharge = tradeService.getSpecialRateUsdToPhpServiceCharge();
        BigDecimal passOnRateUsdToPhpServiceCharge = tradeService.getPassOnRateUsdToPhpServiceCharge();

        System.out.println("specialRateUsdToPhpServiceCharge:" + specialRateUsdToPhpServiceCharge);
        System.out.println("passOnRateUsdToPhpServiceCharge:" + passOnRateUsdToPhpServiceCharge);

        BigDecimal specialRateThirdToUsdServiceCharge = tradeService.getSpecialRateThirdToUsdServiceCharge();
        BigDecimal passOnRateThirdToUsdServiceCharge = tradeService.getPassOnRateThirdToUsdServiceCharge();

        System.out.println("specialRateThirdToUsdServiceCharge:" + specialRateThirdToUsdServiceCharge);
        System.out.println("passOnRateThirdToUsdServiceCharge:" + passOnRateThirdToUsdServiceCharge);

        BigDecimal specialRateThirdToPhpServiceCharge = tradeService.getSpecialRateThirdToPhpServiceCharge();
        BigDecimal passOnRateThirdToPhpServiceCharge = tradeService.getPassOnRateThirdToPhpServiceCharge();

        System.out.println("specialRateThirdToPhpServiceCharge:" + specialRateThirdToPhpServiceCharge);
        System.out.println("passOnRateThirdToPhpServiceCharge:" + passOnRateThirdToPhpServiceCharge);

        if (!(chargesCurrency.equalsIgnoreCase("PHP") || chargesCurrency.equalsIgnoreCase("USD"))) {
            System.out.println("because these values are null extract details map.");
            System.out.println("chargesCurrency:" + chargesCurrency);
            //because these values are null extract from rates repository.

            passOnRateThirdToPhpServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-PHP_pass_on_rate_charges"));
            specialRateThirdToPhpServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-PHP_special_rate_charges"));

            passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-USD_pass_on_rate_charges"));
            specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-USD_special_rate_charges"));

            passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges"));
            specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges"));

        } else {

            //passOnRateThirdToPhpServiceCharge = BigDecimal.ZERO;
            //specialRateThirdToPhpServiceCharge = BigDecimal.ZERO;

            //passOnRateThirdToUsdServiceCharge = BigDecimal.ZERO;
            //specialRateThirdToUsdServiceCharge = BigDecimal.ZERO;

            passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges"));
            specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges"));
            //henry
			if((tradeService.getDocumentClass().equals(DocumentClass.BC) || tradeService.getDocumentClass().equals(DocumentClass.BP)) && tradeService.getDocumentType().equals(DocumentType.FOREIGN)
					&& (tradeService.getSpecialRateUsdToPhpServiceCharge() != null && tradeService.getPassOnRateUsdToPhpServiceCharge() != null)
					&& (passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 0 || specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 0)){
				specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges_buying"));
		        passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges_buying"));
			}
        }
        BigDecimal specialRateThirdToUsdTimesUsdToPhpConversionRate;
        if (specialRateThirdToUsdServiceCharge != null) {
            specialRateThirdToUsdTimesUsdToPhpConversionRate = specialRateThirdToUsdServiceCharge.multiply(specialRateUsdToPhpServiceCharge);
        } else {
            specialRateThirdToUsdTimesUsdToPhpConversionRate = BigDecimal.ONE;
        }

        BigDecimal passOnUrrServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_urr"));

        BigDecimal specialRateThirdToUsdTimesUrrConversionRate;
        if (specialRateThirdToUsdServiceCharge != null) {
            specialRateThirdToUsdTimesUrrConversionRate = specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge);
        } else {
            specialRateThirdToUsdTimesUrrConversionRate = BigDecimal.ONE;
        }


        //BigDecimal specialRateThirdToUsdTimesUrrConversionRate = specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge);
        System.out.println("specialRateThirdToUsdTimesUrrConversionRate:" + specialRateThirdToUsdTimesUrrConversionRate);
        System.out.println("specialRateThirdToUsdTimesUsdToPhpConversionRate:" + specialRateThirdToUsdTimesUsdToPhpConversionRate);
        System.out.println("passOnRateThirdToPhpServiceCharge:" + passOnRateThirdToPhpServiceCharge);
        System.out.println("specialRateThirdToPhpServiceCharge:" + specialRateThirdToPhpServiceCharge);
        System.out.println("passOnRateThirdToUsdServiceCharge:" + passOnRateThirdToUsdServiceCharge);
        System.out.println("specialRateThirdToUsdServiceCharge:" + specialRateThirdToUsdServiceCharge);
        System.out.println("passOnRateUsdToPhpServiceCharge:" + passOnRateUsdToPhpServiceCharge);
        System.out.println("specialRateUsdToPhpServiceCharge:" + specialRateUsdToPhpServiceCharge);
        System.out.println("USD-PHP_urr:" + passOnUrrServiceCharge);


        //NOTE: CWT is 2% of BankCommission, CILEX and Commitment Fee BOOKING
        //TODO:: Note can refactor this by ading the charge Accounting Code in the Charge Table still thinking if worth the effort
        for (String s : chargesSummaryMap.keySet()) {
            List<Object> tempList = chargesSummaryMap.get(s);
            List<String> arrList = new ArrayList<String>();
            for (Object o : tempList) {
                if (o != null) {

                    if (s.equalsIgnoreCase("SC")) {
                        ServiceCharge tempSC = (ServiceCharge) o;
                        System.out.println(arrList + "ARRAY" + tempSC.getChargeId().toString());
                        if (!arrList.contains(tempSC.getChargeId().toString())){
                        	arrList.add(tempSC.getChargeId().toString());
                        System.out.println("|ChargeId:" + tempSC.getChargeId() + "-Currency:" + tempSC.getCurrency() + "-Amount:" + tempSC.getAmount() + "-getOriginalAmount:" + tempSC.getOriginalAmount());
                        //TODO::Fuck and pangit Refactor
                        System.out.println(">>tempSc: "+ tempSC);
                        chargesAmountOrig =  chargesAmountOrig.add(tempSC.getOriginalAmount());

                        if (tempSC.getChargeId().toString().equalsIgnoreCase("BC")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed
                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {

                                    BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("bankCommissionGrossPHP", originalAmount);

                                } else {

                                    BigDecimal originalAmount = tempSC.getDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getDefaultAmount());
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount());
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("bankCommissionGrossPHP", originalAmount);
                                }

                            } else {
                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                    chargesAmount = chargesAmount.add(tempSC.getAmount());
                                    chargeMap.put("bankCommissionGrossPHP", tempSC.getAmount());
                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount());
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount());
                                    chargeMap.put("bankCommissionGrossPHP", tempSC.getDefaultAmount());
                                }
                            }
//                            chargeMap.put("bankCommissionPHP", tempSC.getAmount());
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("SUP")) {

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("suppliesFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("suppliesFeePHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CILEX")) {
                            if (cilexCharged) {
                                if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed

                                    if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                        BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("cilexFeeGrossPHP", originalAmount);
                                    } else {
                                        BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("cilexFeeGrossPHP", originalAmount);
                                    }


                                } else {

                                    if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                        chargesAmount = chargesAmount.add(tempSC.getAmount());
                                        chargeMap.put("cilexFeeGrossPHP", tempSC.getAmount());
                                    } else {
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount());
                                        chargesAmount = chargesAmount.add(tempSC.getDefaultAmount());
                                        chargeMap.put("cilexFeeGrossPHP", tempSC.getDefaultAmount());
                                    }

                                }
//                                chargeMap.put("cilexFeePHP", tempSC.getAmount());
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("NOTARIAL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("notarialFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("notarialFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));                             
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("notarialFeePHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-ADVISING")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
//                            chargeMap.put("advisingFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("advisingFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getOriginalAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getOriginalAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("advisingFeePHP", tempSC.getOriginalAmount().setScale(2, chargesRoundingMode));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-CONFIRMING")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("confirmingFeePHP", tempSC.getAmount());

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("confirmingFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getOriginalAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getOriginalAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("confirmingFeePHP", tempSC.getOriginalAmount().setScale(2, chargesRoundingMode));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CF")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed
//                                BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
//                                BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(originalAmount);
//                                chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
//                                chargeMap.put("commitmentFeeGrossPHP", originalAmount);


                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                    BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("commitmentFeeGrossPHP", originalAmount);

                                } else {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));

                                    BigDecimal originalAmount = tempSC.getDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getDefaultAmount());
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount());
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("commitmentFeeGrossPHP", originalAmount);

                                }

                            } else {
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(tempSC.getAmount());
//                                chargeMap.put("commitmentFeeGrossPHP", tempSC.getAmount());


                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }

                            }
//                            chargeMap.put("commitmentFeePHP", tempSC.getAmount());
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CABLE")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("cableFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("cableFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("cableFeePHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("DOCSTAMPS")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("docStampsFeePHP", tempSC.getAmount());
                        	
                        	BigDecimal docStampFee = BigDecimal.ONE;
                        	BigDecimal rateUSDtoPHPUrr = getBigDecimalOrZero(details.get("USD-PHP_urr"));
                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                               
                                if(chargesCurrency.equalsIgnoreCase("USD")) {
                                	  //for export; DocStamp is always use special rate except for EBC
                                    BigDecimal docStampTemp =  tempSC.getAmount();
                                    if((tradeService.getDocumentClass().equals(DocumentClass.BC) || tradeService.getDocumentClass().equals(DocumentClass.BP)) &&
                                    		tradeService.getDocumentType().equals(DocumentType.FOREIGN)){
                                    	docStampFee = tempSC.getDefaultAmount();
                                    } else {
                                    	docStampFee = docStampTemp.divide(rateUSDtoPHPUrr, 2, RoundingMode.HALF_UP).multiply(specialRateUsdToPhpServiceCharge);   
                                    }
                                    chargesAmountNet = chargesAmountNet.add(docStampFee.setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(docStampFee.setScale(2, chargesRoundingMode));
                                    chargeMap.put("docStampsFeePHP",docStampFee.setScale(2, chargesRoundingMode));                             	
                                }else {
                                	if((tradeService.getDocumentClass().equals(DocumentClass.BC) || tradeService.getDocumentClass().equals(DocumentClass.BP)) &&
                                    		tradeService.getDocumentType().equals(DocumentType.FOREIGN)){
                                		chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                   	 	chargeMap.put("docStampsFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    } else {
                                    	chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                   	 	chargeMap.put("docStampsFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    }
                                }
                              
                               
                            } else {
                                
                                if(chargesCurrency.equalsIgnoreCase("USD")) {
                                	  //for export; DocStamp is always use special rate except for EBC
                                    BigDecimal docStampTemp =  tempSC.getDefaultAmount();
                                    if((tradeService.getDocumentClass().equals(DocumentClass.BC) || tradeService.getDocumentClass().equals(DocumentClass.BP)) &&
                                    		tradeService.getDocumentType().equals(DocumentType.FOREIGN)){
                                    	docStampFee = docStampTemp;
                                    } else {
                                    	docStampFee = docStampTemp.divide(rateUSDtoPHPUrr, 2, RoundingMode.HALF_UP).multiply(specialRateUsdToPhpServiceCharge);   
                                    	System.out.println("HENRYrate "+rateUSDtoPHPUrr);
                                    	System.out.println("HENRYspecialrate "+specialRateUsdToPhpServiceCharge);
                                    }
                                    chargesAmountNet = chargesAmountNet.add(docStampFee.setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(docStampFee.setScale(2, chargesRoundingMode));
                                    chargeMap.put("docStampsFeePHP",  docStampFee.setScale(2, chargesRoundingMode));                              	
                                }else {
                                	chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                	chargeMap.put("docStampsFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                              
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("BOOKING")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                    BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("bookingCommissionFeeGrossPHP", originalAmount);

                                } else {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));

                                    BigDecimal originalAmount = tempSC.getDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("bookingCommissionFeeGrossPHP", originalAmount);
                                }

                            } else {
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(tempSC.getAmount());
//                                chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }


                            }
//                            chargeMap.put("bookingCommissionFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CANCEL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("cancellationFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("cancellationFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("cancellationFeePHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("POSTAGE")) {
                        	BigDecimal rateUSDtoPHPUrr = getBigDecimalOrZero(details.get("USD-PHP_urr"));
//                          chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                          chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                          chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                        	//henry
                          if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                              chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                              chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                              chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                              System.out.println("HENRY POSTAGE IF1:" + tempSC.getOriginalAmount());
                      
                          } else {
                        	  if(tradeService.getDocumentClass().equals(DocumentClass.BP) && tradeService.getDocumentType().equals(DocumentType.FOREIGN)){
                        		  chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                                  chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                                  chargeMap.put("postageFeePHP", tempSC.getDefaultAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                            	  System.out.println("IF");
                        	  } else if(tradeService.getDocumentClass().equals(DocumentClass.BC) && tradeService.getDocumentType().equals(DocumentType.FOREIGN)){
                        		  chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                                  chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                                  chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                        	  } else {
	                              chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
	                              chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
	                              chargeMap.put("postageFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                        	  }
                              //tempSC.getOriginalAmount().multiply(thirdToUsd).multiply(rateUSDtoPHPUrr).setScale(2, BigDecimal.ROUND_HALF_UP));
                              
                              System.out.println("HENRY POSTAGE 1:" + tempSC.getOriginalAmount());
                          }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("REMITTANCE")) {
                            System.out.println("Remittance Fee Stuff");
                            System.out.println("Remittance Fee Stuff:" + tempSC.getAmount());
                            System.out.println("Remittance Fee Stuff:" + new BigDecimal("18"));
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("remittanceFeePHP", tempSC.getAmount());


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("remittanceFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("remittanceFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }


                            String swiftFlag = "N";
                            if (paymentSettlement != null) {
                                Set<PaymentDetail> temp = paymentSettlement.getDetails();
                                for (PaymentDetail paymentDetail : temp) {
                                    System.out.println("---------------------------");
                                    printPaymentDetails(paymentDetail);
                                    if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.PDDTS)) {
                                        System.out.println("PAYMENT IS PDDTS Book 18 USD");
                                        swiftFlag = "Y";
                                    }

                                }
                            }


                            if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && tradeService.getServiceType().equals(ServiceType.NEGOTIATION) && "Y".equalsIgnoreCase(swiftFlag)) {
                                chargeMap.put("remittanceFeePHP", BigDecimal.ZERO);
                                chargeMap.put("remittanceFeeSpecialPHP", tempSC.getAmount());
                                chargeMap.put("remittanceFeeSpecialUSD", new BigDecimal("18").setScale(2, BigDecimal.ROUND_UNNECESSARY));

                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("INTEREST")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("interestPHP", tempSC.getAmount());


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("interestPHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("interestPHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("BSP")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("bspCommissionGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("bspCommissionPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("bspCommissionGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("bspCommissionGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("ADVISING-EXPORT")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("advisingExportFeePHP", tempSC.getAmount());

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("advisingExportFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("advisingExportFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-ADDITIONAL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("additionalCorresFeePHP", tempSC.getAmount());

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("additionalCorresFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("additionalCorresFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("POSTAGE")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("postageFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("OTHER-EXPORT")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("otherExportFeePHP", tempSC.getAmount());

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("otherExportFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("otherExportFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-EXPORT")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("otherExportFeePHP", tempSC.getAmount());

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("corresExportFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("corresExportFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }
                        }
                    }

                    if (s.equalsIgnoreCase("PC")) {
                        ProductCharge tempPC = (ProductCharge) o;
                        System.out.println("|Product Charge: " + "-Currency:" + tempPC.getCurrency() + "-Amount:" + tempPC.getAmount());

                        //Convert to produce the three lcAmount
                        if (tempPC.getCurrency().toString().equalsIgnoreCase("PHP")) {
                            chargeMap.put("lcAmountPHP", tempPC.getAmount());
                            if (specialRateUsdToPhpServiceCharge != null && specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().divide(specialRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_EVEN));
                            } else if (passOnRateUsdToPhpServiceCharge != null && passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().divide(passOnRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_EVEN));
                            }

                            if (specialRateThirdToPhpServiceCharge != null && specialRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdTimesUsdToPhpConversionRate, 2, BigDecimal.ROUND_HALF_UP));
                            } else if (passOnRateThirdToPhpServiceCharge != null && passOnRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdTimesUsdToPhpConversionRate, 2, BigDecimal.ROUND_HALF_UP));
                            }
                        } else if (tempPC.getCurrency().toString().equalsIgnoreCase("USD")) {
                            chargeMap.put("lcAmountUSD", tempPC.getAmount());

                            if (specialRateUsdToPhpServiceCharge != null && specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(specialRateUsdToPhpServiceCharge));
                            } else if (passOnRateUsdToPhpServiceCharge != null && passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(passOnRateUsdToPhpServiceCharge));
                            }

                            if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                            } else if (passOnRateThirdToPhpServiceCharge != null && passOnRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(passOnRateThirdToUsdServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                            }
                        } else {
                            chargeMap.put("lcAmountTHIRD", tempPC.getAmount());

                            if (specialRateThirdToUsdServiceCharge != null) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().multiply(specialRateThirdToUsdServiceCharge));
                            } else if (passOnRateThirdToUsdServiceCharge != null) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().multiply(passOnRateThirdToUsdServiceCharge));
                            }

                            //TODO:FIX THIS
                            if (specialRateThirdToUsdTimesUsdToPhpConversionRate != null) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(specialRateThirdToUsdTimesUsdToPhpConversionRate));
                            } else if (passOnRateThirdToPhpServiceCharge != null) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(passOnRateThirdToPhpServiceCharge));
                            }

                        }

                        System.out.println("henrylcAmountPHP:" + chargeMap.get("lcAmountPHP"));
                        System.out.println("henrylcAmountUSD:" + chargeMap.get("lcAmountUSD"));
                        System.out.println("henrylcAmountTHIRD:" + chargeMap.get("lcAmountTHIRD"));
                    }
                }
            }
            }
        }
        chargeMap.put("chargesAmountNetPHP", chargesAmountNet);
        chargeMap.put("chargesAmountPHP", chargesAmount);
        chargeMap.put("chargesAmountCWTPHP", chargesAmountCWT);
        chargeMap.put("chargesAmountOrig", chargesAmountOrig);

        System.out.println("chargesAmountNet:" + chargesAmountNet);
        System.out.println("chargesAmount:" + chargesAmount);
        System.out.println("chargesAmountCWT:" + chargesAmountCWT);

        // Charges Amount
        chargeMap.put("chargesAmountNetUSD", divideOrReturnZero(chargesAmountNet, passOnUrrServiceCharge));
        chargeMap.put("chargesAmountUSD", divideOrReturnZero(chargesAmount, passOnUrrServiceCharge));
        chargeMap.put("chargesAmountCWTUSD", divideOrReturnZero(chargesAmountCWT, passOnUrrServiceCharge));

        if("USD".equalsIgnoreCase(chargesCurrency)){
            chargeMap.put("chargesAmountNetUSD", chargesAmountOrig);
        }
        if (specialRateThirdToUsdTimesUrrConversionRate != null) {
            chargeMap.put("chargesAmountNetTHIRD", divideOrReturnZero(chargesAmountNet, specialRateThirdToUsdTimesUrrConversionRate));
            chargeMap.put("chargesAmountTHIRD", divideOrReturnZero(chargesAmount, specialRateThirdToUsdTimesUrrConversionRate));
            chargeMap.put("chargesAmountCWTTHIRD", divideOrReturnZero(chargesAmountCWT, specialRateThirdToUsdTimesUrrConversionRate));
        }

        System.out.println("-----------------------------------------------------------------------------");

        return chargeMap;
    }

    /**
     * Returns ChargeMap Map<String, Object> that contains all charges and equivalent conversion if it exists.
     *
     * @param details           the details map of the TradeService where the import charges will be extracted and added to the returned Map
     * @param cilexCharged      flag whether cilex will be charged or not
     * @param chargesSummaryMap the map that will be modified then returned
     * @param tradeService      The TradeService where the import charges will be extracted and added to the returned Map
     * @param chargesCurrency   currency of the charge
     * @return Map containing the import charges
     */
    private static Map<String, Object> generate_IMPORTS_CHARGES_ValuesMap(Map<String, Object> details, Boolean cilexCharged, Map<String, List<Object>> chargesSummaryMap, TradeService tradeService, String chargesCurrency, Payment paymentSettlement) {

        int chargesRoundingMode = BigDecimal.ROUND_UP;

        Map<String, Object> chargeMap = new HashMap<String, Object>();
        BigDecimal chargesAmount = BigDecimal.ZERO;
        BigDecimal chargesAmountCWT = BigDecimal.ZERO;
        BigDecimal chargesAmountNet = BigDecimal.ZERO;
        String cwtFlag = details.get("cwtFlag") != null ? details.get("cwtFlag").toString() : "N";
        System.out.println("cwtFlag:" + cwtFlag);

        BigDecimal specialRateUsdToPhpServiceCharge = tradeService.getSpecialRateUsdToPhpServiceCharge();
        BigDecimal passOnRateUsdToPhpServiceCharge = tradeService.getPassOnRateUsdToPhpServiceCharge();

        System.out.println("specialRateUsdToPhpServiceCharge:" + specialRateUsdToPhpServiceCharge);
        System.out.println("passOnRateUsdToPhpServiceCharge:" + passOnRateUsdToPhpServiceCharge);

        BigDecimal specialRateThirdToUsdServiceCharge = tradeService.getSpecialRateThirdToUsdServiceCharge();
        BigDecimal passOnRateThirdToUsdServiceCharge = tradeService.getPassOnRateThirdToUsdServiceCharge();

        System.out.println("specialRateThirdToUsdServiceCharge:" + specialRateThirdToUsdServiceCharge);
        System.out.println("passOnRateThirdToUsdServiceCharge:" + passOnRateThirdToUsdServiceCharge);

        BigDecimal specialRateThirdToPhpServiceCharge = tradeService.getSpecialRateThirdToPhpServiceCharge();
        BigDecimal passOnRateThirdToPhpServiceCharge = tradeService.getPassOnRateThirdToPhpServiceCharge();

        System.out.println("specialRateThirdToPhpServiceCharge:" + specialRateThirdToPhpServiceCharge);
        System.out.println("passOnRateThirdToPhpServiceCharge:" + passOnRateThirdToPhpServiceCharge);

        if (!(chargesCurrency.equalsIgnoreCase("PHP") || chargesCurrency.equalsIgnoreCase("USD"))) {
            System.out.println("because these values are null extract details map.");
            System.out.println("chargesCurrency:" + chargesCurrency);
            //because these values are null extract from rates repository.

            passOnRateThirdToPhpServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-PHP_pass_on_rate_charges"));
            specialRateThirdToPhpServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-PHP_special_rate_charges"));

            passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-USD_pass_on_rate_charges"));
            specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-USD_special_rate_charges"));

            passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges"));
            specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges"));

        } else {

            //passOnRateThirdToPhpServiceCharge = BigDecimal.ZERO;
            //specialRateThirdToPhpServiceCharge = BigDecimal.ZERO;

            //passOnRateThirdToUsdServiceCharge = BigDecimal.ZERO;
            //specialRateThirdToUsdServiceCharge = BigDecimal.ZERO;

            passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges"));
            specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges"));

        }
        BigDecimal specialRateThirdToUsdTimesUsdToPhpConversionRate;
        if (specialRateThirdToUsdServiceCharge != null) {
            specialRateThirdToUsdTimesUsdToPhpConversionRate = specialRateThirdToUsdServiceCharge.multiply(specialRateUsdToPhpServiceCharge);
        } else {
            specialRateThirdToUsdTimesUsdToPhpConversionRate = BigDecimal.ONE;
        }

        BigDecimal passOnUrrServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_urr"));

        BigDecimal specialRateThirdToUsdTimesUrrConversionRate;
        if (specialRateThirdToUsdServiceCharge != null) {
            specialRateThirdToUsdTimesUrrConversionRate = specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge);
        } else {
            specialRateThirdToUsdTimesUrrConversionRate = BigDecimal.ONE;
        }


        //BigDecimal specialRateThirdToUsdTimesUrrConversionRate = specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge);
        System.out.println("specialRateThirdToUsdTimesUrrConversionRate:" + specialRateThirdToUsdTimesUrrConversionRate);
        System.out.println("specialRateThirdToUsdTimesUsdToPhpConversionRate:" + specialRateThirdToUsdTimesUsdToPhpConversionRate);
        System.out.println("passOnRateThirdToPhpServiceCharge:" + passOnRateThirdToPhpServiceCharge);
        System.out.println("specialRateThirdToPhpServiceCharge:" + specialRateThirdToPhpServiceCharge);
        System.out.println("passOnRateThirdToUsdServiceCharge:" + passOnRateThirdToUsdServiceCharge);
        System.out.println("specialRateThirdToUsdServiceCharge:" + specialRateThirdToUsdServiceCharge);
        System.out.println("passOnRateUsdToPhpServiceCharge:" + passOnRateUsdToPhpServiceCharge);
        System.out.println("specialRateUsdToPhpServiceCharge:" + specialRateUsdToPhpServiceCharge);
        System.out.println("USD-PHP_urr:" + passOnUrrServiceCharge);


        //NOTE: CWT is 2% of BankCommission, CILEX and Commitment Fee BOOKING
        //TODO:: Note can refactor this by adding the charge Accounting Code in the Charge Table still thinking if worth the effort
        for (String s : chargesSummaryMap.keySet()) {
            List<Object> tempList = chargesSummaryMap.get(s);

            for (Object o : tempList) {
                if (o != null) {

                    if (s.equalsIgnoreCase("SC")) {
                        ServiceCharge tempSC = (ServiceCharge) o;
                        System.out.println("|ChargeId:" + tempSC.getChargeId() + "-Currency:" + tempSC.getCurrency() + "-Amount:" + tempSC.getAmount() + "-getOriginalAmount:" + tempSC.getOriginalAmount());
                        //TODO::aayusin ito
                        if (tempSC.getChargeId().toString().equalsIgnoreCase("BC")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed
                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {

                                    BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("bankCommissionGrossPHP", originalAmount);

                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        BigDecimal originalAmount = tempSC.getDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getDefaultAmount());
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount());
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("bankCommissionGrossPHP", originalAmount);
                                    }
                                }

                            } else {
                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                    chargesAmount = chargesAmount.add(tempSC.getAmount());
                                    chargeMap.put("bankCommissionGrossPHP", tempSC.getAmount());
                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount());
                                        chargesAmount = chargesAmount.add(tempSC.getDefaultAmount());
                                        chargeMap.put("bankCommissionGrossPHP", tempSC.getDefaultAmount());
                                    }
                                }
                            }
//                            chargeMap.put("bankCommissionPHP", tempSC.getAmount());
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("SUP")) {

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("suppliesFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("suppliesFeePHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                }
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CILEX")) {
                            System.out.println("in chargesMap function cilexCharged:"+cilexCharged);
                            if (cilexCharged) {
                                System.out.println("in chargesMap function cilexCharged:"+cilexCharged);
                                if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed

                                    String amountBasedFlag = "N";
                                    amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                    System.out.println("tempSC.getOverridenFlag():"+tempSC.getOverridenFlag());
                                    System.out.println("amountBasedFlag:"+amountBasedFlag);

                                    if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                        System.out.println("cilex cilex 01");
                                        BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("cilexFeeGrossPHP", originalAmount);
                                    } else {
                                        if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){
                                            System.out.println("cilex cilex 02");

                                        } else {
                                            System.out.println("cilex cilex 03");


                                            BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                            BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
                                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                            chargesAmount = chargesAmount.add(originalAmount);
                                            chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                            chargeMap.put("cilexFeeGrossPHP", originalAmount);
                                        }
                                    }

                                } else {
                                    String amountBasedFlag = "N";
                                    amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                                    if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                        System.out.println("cilex cilex 04");
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                        chargesAmount = chargesAmount.add(tempSC.getAmount());
                                        chargeMap.put("cilexFeeGrossPHP", tempSC.getAmount());
                                    } else {

                                        if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){
                                            System.out.println("cilex cilex 05");
                                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                            chargesAmount = chargesAmount.add(tempSC.getAmount());
                                            chargeMap.put("cilexFeeGrossPHP", tempSC.getAmount());
                                        } else {
                                            System.out.println("cilex cilex 06");
                                            chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount());
                                            chargesAmount = chargesAmount.add(tempSC.getDefaultAmount());
                                            chargeMap.put("cilexFeeGrossPHP", tempSC.getDefaultAmount());
                                        }
                                    }

                                }
//                                chargeMap.put("cilexFeePHP", tempSC.getAmount());
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("NOTARIAL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("notarialFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("notarialFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("notarialFeePHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                }
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-ADVISING")) {
                            //TODO this may not be in PHP
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
//                            chargeMap.put("advisingFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("advisingFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("advisingFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-CONFIRMING")) {
                            //TODO this may not be in PHP
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("confirmingFeePHP", tempSC.getAmount());

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("confirmingFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("confirmingFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CF")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed
//                                BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
//                                BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(originalAmount);
//                                chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
//                                chargeMap.put("commitmentFeeGrossPHP", originalAmount);

                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                    BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("commitmentFeeGrossPHP", originalAmount);

                                } else {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));

                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        BigDecimal originalAmount = tempSC.getDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getDefaultAmount());
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount());
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("commitmentFeeGrossPHP", originalAmount);
                                    }
                                }

                            } else {
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(tempSC.getAmount());
//                                chargeMap.put("commitmentFeeGrossPHP", tempSC.getAmount());

                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargeMap.put("commitmentFeeGrossPHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    }
                                }

                            }
//                            chargeMap.put("commitmentFeePHP", tempSC.getAmount());
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CABLE")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("cableFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));


                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("cableFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("cableFeePHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                }
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("DOCSTAMPS")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("docStampsFeePHP", tempSC.getAmount());

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("docStampsFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("docStampsFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("BOOKING")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed


                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                    BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("bookingCommissionFeeGrossPHP", originalAmount);

                                } else {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));

                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        BigDecimal originalAmount = tempSC.getDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("bookingCommissionFeeGrossPHP", originalAmount);
                                    }
                                }
                            } else {
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(tempSC.getAmount());
//                                chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    }
                                }


                            }
//                            chargeMap.put("bookingCommissionFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CANCEL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("cancellationFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("cancellationFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("cancellationFeePHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                }

                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("POSTAGE")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("cancellationFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("postageFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("REMITTANCE")) {
                            System.out.println("Remittance Fee Stuff");
                            System.out.println("Remittance Fee Stuff:" + tempSC.getAmount());
                            System.out.println("Remittance Fee Stuff:" + new BigDecimal("18"));
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("remittanceFeePHP", tempSC.getAmount());


                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("remittanceFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("remittanceFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }


                            String swiftFlag = "N";
                            if (paymentSettlement != null) {
                                Set<PaymentDetail> temp = paymentSettlement.getDetails();
                                for (PaymentDetail paymentDetail : temp) {
                                    System.out.println("---------------------------");
                                    printPaymentDetails(paymentDetail);
                                    if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.PDDTS)) {
                                        System.out.println("PAYMENT IS PDDTS Book 18 USD");
                                        swiftFlag = "Y";
                                    }

                                }
                            }


                            if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && tradeService.getServiceType().equals(ServiceType.NEGOTIATION) && "Y".equalsIgnoreCase(swiftFlag)) {
                                chargeMap.put("remittanceFeePHP", BigDecimal.ZERO);
                                chargeMap.put("remittanceFeeSpecialPHP", tempSC.getAmount());
                                chargeMap.put("remittanceFeeSpecialUSD", new BigDecimal("18").setScale(2, BigDecimal.ROUND_UNNECESSARY));
                            }

                            if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && tradeService.getServiceType().equals(ServiceType.SETTLEMENT) &&
                                    (tradeService.getDocumentClass().equals(DocumentClass.DA)||
                                            tradeService.getDocumentClass().equals(DocumentClass.DP)||
                                            tradeService.getDocumentClass().equals(DocumentClass.DR)||
                                            tradeService.getDocumentClass().equals(DocumentClass.OA)
                                    )
                            && "Y".equalsIgnoreCase(swiftFlag)) {
                                chargeMap.put("remittanceFeePHP", BigDecimal.ZERO);
                                chargeMap.put("remittanceFeeSpecialPHP", tempSC.getAmount());
                                chargeMap.put("remittanceFeeSpecialUSD", new BigDecimal("18").setScale(2, BigDecimal.ROUND_UNNECESSARY));
                            }

                            //Added for UA Loan
                            if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && tradeService.getServiceType().equals(ServiceType.UA_LOAN_SETTLEMENT) && "Y".equalsIgnoreCase(swiftFlag)) {
                                chargeMap.put("remittanceFeePHP", BigDecimal.ZERO);
                                chargeMap.put("remittanceFeeSpecialPHP", tempSC.getAmount());
                                chargeMap.put("remittanceFeeSpecialUSD", new BigDecimal("18").setScale(2, BigDecimal.ROUND_UNNECESSARY));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("INTEREST")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("interestPHP", tempSC.getAmount());


                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("interestPHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("interestPHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("BSP")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("bspCommissionGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("bspCommissionPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("bspCommissionGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("bspCommissionGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
                                }
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("ADVISING-EXPORT")) {
                            System.out.println("angol angol angol ADVISING-EXPORT cwtFlag:"+cwtFlag);
                            System.out.println("angol angol angol cwtFlag:"+cwtFlag);
                            System.out.println("angol angol angol !cwtFlag.equalsIgnoreCase(\"0\"):"+!cwtFlag.equalsIgnoreCase("0"));
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed
                                System.out.println("angol angol angol cwtFlag:"+cwtFlag);

                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                                    System.out.println("angol angol angol 00001");

                                    BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("advisingExportFeePHP", originalAmount);

                                } else {
                                    System.out.println("angol angol angol 00002");
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));

                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        System.out.println("angol angol angol 00003");
                                        BigDecimal originalAmount = tempSC.getDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("advisingExportFeePHP", originalAmount);
                                    }
                                }
                            } else {
                                System.out.println("angol angol angol 00004");
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(tempSC.getAmount());
//                                chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                System.out.println("angol angol angol cwtFlag no cwt:"+cwtFlag);
                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                    System.out.println("angol angol angol 00005");
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("advisingExportFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        System.out.println("angol angol angol 00006");
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargeMap.put("advisingExportFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    }
                                }


                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-ADDITIONAL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("additionalCorresFeePHP", tempSC.getAmount());
                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("additionalCorresFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("additionalCorresFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("POSTAGE")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("postageFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("OTHER-EXPORT")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("otherExportFeePHP", tempSC.getAmount());

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("otherExportFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("otherExportFeePHP", tempSC.getDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }
                        }
                    }

                    if (s.equalsIgnoreCase("PC")) {
                        ProductCharge tempPC = (ProductCharge) o;
                        System.out.println("|Product Charge: " + "-Currency:" + tempPC.getCurrency() + "-Amount:" + tempPC.getAmount());

                        //Convert to produce the three lcAmount
                        if (tempPC.getCurrency().toString().equalsIgnoreCase("PHP")) {
                            chargeMap.put("lcAmountPHP", tempPC.getAmount());
                            if (specialRateUsdToPhpServiceCharge != null && specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().divide(specialRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_EVEN));
                            } else if (passOnRateUsdToPhpServiceCharge != null && passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().divide(passOnRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_EVEN));
                            }

                            if (specialRateThirdToPhpServiceCharge != null && specialRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdTimesUsdToPhpConversionRate, 2, BigDecimal.ROUND_HALF_UP));
                            } else if (passOnRateThirdToPhpServiceCharge != null && passOnRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdTimesUsdToPhpConversionRate, 2, BigDecimal.ROUND_HALF_UP));
                            }
                        } else if (tempPC.getCurrency().toString().equalsIgnoreCase("USD")) {
                            chargeMap.put("lcAmountUSD", tempPC.getAmount());

                            if (specialRateUsdToPhpServiceCharge != null && specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(specialRateUsdToPhpServiceCharge));
                            } else if (passOnRateUsdToPhpServiceCharge != null && passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(passOnRateUsdToPhpServiceCharge));
                            }

                            if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                            } else if (passOnRateThirdToPhpServiceCharge != null && passOnRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(passOnRateThirdToUsdServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                            }
                        } else {
                            chargeMap.put("lcAmountTHIRD", tempPC.getAmount());

                            if (specialRateThirdToUsdServiceCharge != null) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().multiply(specialRateThirdToUsdServiceCharge));
                            } else if (passOnRateThirdToUsdServiceCharge != null) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().multiply(passOnRateThirdToUsdServiceCharge));
                            }

                            //TODO:FIX THIS
                            if (specialRateThirdToUsdTimesUsdToPhpConversionRate != null) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(specialRateThirdToUsdTimesUsdToPhpConversionRate));
                            } else if (passOnRateThirdToPhpServiceCharge != null) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(passOnRateThirdToPhpServiceCharge));
                            }

                        }

                        System.out.println("lcAmountPHP:" + chargeMap.get("lcAmountPHP"));
                        System.out.println("lcAmountUSD:" + chargeMap.get("lcAmountUSD"));
                        System.out.println("lcAmountTHIRD:" + chargeMap.get("lcAmountTHIRD"));
                    }
                }
            }
        }

        chargeMap.put("chargesAmountNetPHP", chargesAmountNet);
        chargeMap.put("chargesAmountPHP", chargesAmount);
        chargeMap.put("chargesAmountCWTPHP", chargesAmountCWT);

        System.out.println("chargesAmountNet:" + chargesAmountNet);
        System.out.println("chargesAmount:" + chargesAmount);
        System.out.println("chargesAmountCWT:" + chargesAmountCWT);

        // Charges Amount
        chargeMap.put("chargesAmountNetUSD", divideOrReturnZero(chargesAmountNet, passOnUrrServiceCharge));
        chargeMap.put("chargesAmountUSD", divideOrReturnZero(chargesAmount, passOnUrrServiceCharge));
        chargeMap.put("chargesAmountCWTUSD", divideOrReturnZero(chargesAmountCWT, passOnUrrServiceCharge));

        if (specialRateThirdToUsdTimesUrrConversionRate != null) {
            chargeMap.put("chargesAmountNetTHIRD", divideOrReturnZero(chargesAmountNet, specialRateThirdToUsdTimesUrrConversionRate));
            chargeMap.put("chargesAmountTHIRD", divideOrReturnZero(chargesAmount, specialRateThirdToUsdTimesUrrConversionRate));
            chargeMap.put("chargesAmountCWTTHIRD", divideOrReturnZero(chargesAmountCWT, specialRateThirdToUsdTimesUrrConversionRate));
        }

        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("chargeMap:"+chargeMap);
        return chargeMap;
    }

    /**
     * Returns ChargeMap Map<String, Object> that contains all charges and equivalent conversion if it exists.
     *
     * @param details           the details map of the TradeService where the import charges will be extracted and added to the returned Map
     * @param cilexCharged      flag whether cilex will be charged or not
     * @param chargesSummaryMap the map that will be modified then returned
     * @param tradeService      The TradeService where the import charges will be extracted and added to the returned Map
     * @param chargesCurrency   currency of the charge
     * @return Map containing the import charges
     */
    private static Map<String, Object> generate_OTHER_IMPORTS_CHARGES_ValuesMap(Map<String, Object> details, Boolean cilexCharged, Map<String, List<Object>> chargesSummaryMap, TradeService tradeService, String chargesCurrency, Payment paymentSettlement) {

        int chargesRoundingMode = BigDecimal.ROUND_UP;

        Map<String, Object> chargeMap = new HashMap<String, Object>();
        BigDecimal chargesAmount = BigDecimal.ZERO;
        BigDecimal chargesAmountCWT = BigDecimal.ZERO;
        BigDecimal chargesAmountNet = BigDecimal.ZERO;
        String cwtFlag = details.get("cwtFlag") != null ? details.get("cwtFlag").toString() : "N";
        System.out.println("cwtFlag:" + cwtFlag);

        BigDecimal specialRateUsdToPhpServiceCharge = tradeService.getSpecialRateUsdToPhpServiceCharge();
        BigDecimal passOnRateUsdToPhpServiceCharge = tradeService.getPassOnRateUsdToPhpServiceCharge();

        System.out.println("specialRateUsdToPhpServiceCharge:" + specialRateUsdToPhpServiceCharge);
        System.out.println("passOnRateUsdToPhpServiceCharge:" + passOnRateUsdToPhpServiceCharge);

        BigDecimal specialRateThirdToUsdServiceCharge = tradeService.getSpecialRateThirdToUsdServiceCharge();
        BigDecimal passOnRateThirdToUsdServiceCharge = tradeService.getPassOnRateThirdToUsdServiceCharge();

        System.out.println("specialRateThirdToUsdServiceCharge:" + specialRateThirdToUsdServiceCharge);
        System.out.println("passOnRateThirdToUsdServiceCharge:" + passOnRateThirdToUsdServiceCharge);

        BigDecimal specialRateThirdToPhpServiceCharge = tradeService.getSpecialRateThirdToPhpServiceCharge();
        BigDecimal passOnRateThirdToPhpServiceCharge = tradeService.getPassOnRateThirdToPhpServiceCharge();

        System.out.println("specialRateThirdToPhpServiceCharge:" + specialRateThirdToPhpServiceCharge);
        System.out.println("passOnRateThirdToPhpServiceCharge:" + passOnRateThirdToPhpServiceCharge);

        if (!(chargesCurrency.equalsIgnoreCase("PHP") || chargesCurrency.equalsIgnoreCase("USD"))) {
            System.out.println("because these values are null extract details map.");
            System.out.println("chargesCurrency:" + chargesCurrency);
            //because these values are null extract from rates repository.

            passOnRateThirdToPhpServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-PHP_pass_on_rate_charges"));
            specialRateThirdToPhpServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-PHP_special_rate_charges"));

            passOnRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-USD_pass_on_rate_charges"));
            specialRateThirdToUsdServiceCharge = getBigDecimalOrZero(details.get(chargesCurrency + "-USD_special_rate_charges"));

            passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges"));
            specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges"));

        } else {

            //passOnRateThirdToPhpServiceCharge = BigDecimal.ZERO;
            //specialRateThirdToPhpServiceCharge = BigDecimal.ZERO;

            //passOnRateThirdToUsdServiceCharge = BigDecimal.ZERO;
            //specialRateThirdToUsdServiceCharge = BigDecimal.ZERO;

            passOnRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_pass_on_rate_charges"));
            specialRateUsdToPhpServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_special_rate_charges"));

        }
        BigDecimal specialRateThirdToUsdTimesUsdToPhpConversionRate;
        if (specialRateThirdToUsdServiceCharge != null) {
            specialRateThirdToUsdTimesUsdToPhpConversionRate = specialRateThirdToUsdServiceCharge.multiply(specialRateUsdToPhpServiceCharge);
        } else {
            specialRateThirdToUsdTimesUsdToPhpConversionRate = BigDecimal.ONE;
        }

        BigDecimal passOnUrrServiceCharge = getBigDecimalOrZero(details.get("USD-PHP_urr"));

        BigDecimal specialRateThirdToUsdTimesUrrConversionRate;
        if (specialRateThirdToUsdServiceCharge != null) {
            specialRateThirdToUsdTimesUrrConversionRate = specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge);
        } else {
            specialRateThirdToUsdTimesUrrConversionRate = BigDecimal.ONE;
        }


        //BigDecimal specialRateThirdToUsdTimesUrrConversionRate = specialRateThirdToUsdServiceCharge.multiply(passOnUrrServiceCharge);
        System.out.println("specialRateThirdToUsdTimesUrrConversionRate:" + specialRateThirdToUsdTimesUrrConversionRate);
        System.out.println("specialRateThirdToUsdTimesUsdToPhpConversionRate:" + specialRateThirdToUsdTimesUsdToPhpConversionRate);
        System.out.println("passOnRateThirdToPhpServiceCharge:" + passOnRateThirdToPhpServiceCharge);
        System.out.println("specialRateThirdToPhpServiceCharge:" + specialRateThirdToPhpServiceCharge);
        System.out.println("passOnRateThirdToUsdServiceCharge:" + passOnRateThirdToUsdServiceCharge);
        System.out.println("specialRateThirdToUsdServiceCharge:" + specialRateThirdToUsdServiceCharge);
        System.out.println("passOnRateUsdToPhpServiceCharge:" + passOnRateUsdToPhpServiceCharge);
        System.out.println("specialRateUsdToPhpServiceCharge:" + specialRateUsdToPhpServiceCharge);
        System.out.println("USD-PHP_urr:" + passOnUrrServiceCharge);


        //NOTE: CWT is 2% of BankCommission, CILEX and Commitment Fee BOOKING
        //TODO:: Note can refactor this by adding the charge Accounting Code in the Charge Table still thinking if worth the effort
        for (String s : chargesSummaryMap.keySet()) {
            List<Object> tempList = chargesSummaryMap.get(s);

            for (Object o : tempList) {
                if (o != null) {

                    if (s.equalsIgnoreCase("SC")) {
                        ServiceCharge tempSC = (ServiceCharge) o;
                        System.out.println("|ChargeId:" + tempSC.getChargeId() + "-Currency:" + tempSC.getCurrency() + "-Amount:" + tempSC.getAmount() + "-getOriginalAmount:" + tempSC.getOriginalAmount());
                        //TODO::Fuck and pangit Refactor
                        if (tempSC.getChargeId().toString().equalsIgnoreCase("BC")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed
                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {

                                    BigDecimal originalAmount = tempSC.getCollectibleAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleAmount());
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount());
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("bankCommissionGrossPHP", originalAmount);

                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        BigDecimal originalAmount = tempSC.getCollectibleDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleDefaultAmount());
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount());
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("bankCommissionGrossPHP", originalAmount);
                                    }
                                }

                            } else {
                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount());
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount());
                                    chargeMap.put("bankCommissionGrossPHP", tempSC.getCollectibleAmount());
                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount());
                                        chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount());
                                        chargeMap.put("bankCommissionGrossPHP", tempSC.getCollectibleDefaultAmount());
                                    }
                                }
                            }
//                            chargeMap.put("bankCommissionPHP", tempSC.getAmount());
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("SUP")) {

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("suppliesFeePHP", tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("suppliesFeePHP", tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                }
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CILEX")) {
                            System.out.println("in chargesMap function cilexCharged:"+cilexCharged);
                            if (cilexCharged) {
                                System.out.println("in chargesMap function cilexCharged:"+cilexCharged);
                                if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed

                                    String amountBasedFlag = "N";
                                    amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                    System.out.println("tempSC.getOverridenFlag():"+tempSC.getOverridenFlag());
                                    System.out.println("amountBasedFlag:"+amountBasedFlag);

                                    if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                        System.out.println("cilex cilex 01");
                                        BigDecimal originalAmount = tempSC.getCollectibleAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleAmount());
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount());
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("cilexFeeGrossPHP", originalAmount);
                                    } else {
                                        if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){
                                            System.out.println("cilex cilex 02");

                                        } else {
                                            System.out.println("cilex cilex 03");


                                            BigDecimal originalAmount = tempSC.getCollectibleAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                            BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleAmount());
                                            chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount());
                                            chargesAmount = chargesAmount.add(originalAmount);
                                            chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                            chargeMap.put("cilexFeeGrossPHP", originalAmount);
                                        }
                                    }

                                } else {
                                    String amountBasedFlag = "N";
                                    amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                                    if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                        System.out.println("cilex cilex 04");
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount());
                                        chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount());
                                        chargeMap.put("cilexFeeGrossPHP", tempSC.getCollectibleAmount());
                                    } else {

                                        if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){
                                            System.out.println("cilex cilex 05");
                                            chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount());
                                            chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount());
                                            chargeMap.put("cilexFeeGrossPHP", tempSC.getCollectibleAmount());
                                        } else {
                                            System.out.println("cilex cilex 06");
                                            chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount());
                                            chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount());
                                            chargeMap.put("cilexFeeGrossPHP", tempSC.getCollectibleDefaultAmount());
                                        }
                                    }

                                }
//                                chargeMap.put("cilexFeePHP", tempSC.getAmount());
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("NOTARIAL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("notarialFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("notarialFeePHP", tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("notarialFeePHP", tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                }
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-ADVISING")) {
                            //TODO this may not be in PHP
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(2, chargesRoundingMode));
//                            chargeMap.put("advisingFeePHP", tempSC.getAmount().setScale(2, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("advisingFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("advisingFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-CONFIRMING")) {
                            //TODO this may not be in PHP
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("confirmingFeePHP", tempSC.getAmount());

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("confirmingFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("confirmingFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CF")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed
//                                BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
//                                BigDecimal cwtAmount = originalAmount.subtract(tempSC.getAmount());
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(originalAmount);
//                                chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
//                                chargeMap.put("commitmentFeeGrossPHP", originalAmount);

                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                    BigDecimal originalAmount = tempSC.getAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleAmount());
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount());
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("commitmentFeeGrossPHP", originalAmount);

                                } else {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));

                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        BigDecimal originalAmount = tempSC.getCollectibleDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleDefaultAmount());
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount());
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("commitmentFeeGrossPHP", originalAmount);
                                    }
                                }

                            } else {
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(tempSC.getAmount());
//                                chargeMap.put("commitmentFeeGrossPHP", tempSC.getAmount());

                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("commitmentFeeGrossPHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargeMap.put("commitmentFeeGrossPHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    }
                                }

                            }
//                            chargeMap.put("commitmentFeePHP", tempSC.getAmount());
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CABLE")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("cableFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));


                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("cableFeePHP", tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("cableFeePHP", tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                }
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("DOCSTAMPS")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("docStampsFeePHP", tempSC.getAmount());

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("docStampsFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("docStampsFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("BOOKING")) {
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed


                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                    BigDecimal originalAmount = tempSC.getCollectibleAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("bookingCommissionFeeGrossPHP", originalAmount);

                                } else {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));

                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        BigDecimal originalAmount = tempSC.getCollectibleDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("bookingCommissionFeeGrossPHP", originalAmount);
                                    }
                                }
                            } else {
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(tempSC.getAmount());
//                                chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    }
                                }


                            }
//                            chargeMap.put("bookingCommissionFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CANCEL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("cancellationFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("cancellationFeePHP", tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("cancellationFeePHP", tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                }

                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("POSTAGE")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("cancellationFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("postageFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("postageFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("REMITTANCE")) {
                            System.out.println("Remittance Fee Stuff");
                            System.out.println("Remittance Fee Stuff:" + tempSC.getAmount());
                            System.out.println("Remittance Fee Stuff:" + new BigDecimal("18"));
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("remittanceFeePHP", tempSC.getAmount());


                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("remittanceFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("remittanceFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                            }


                            String swiftFlag = "N";
                            if (paymentSettlement != null) {
                                Set<PaymentDetail> temp = paymentSettlement.getDetails();
                                for (PaymentDetail paymentDetail : temp) {
                                    System.out.println("---------------------------");
                                    printPaymentDetails(paymentDetail);
                                    if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.PDDTS)) {
                                        System.out.println("PAYMENT IS PDDTS Book 18 USD");
                                        swiftFlag = "Y";
                                    }

                                }
                            }

     
                            if (tradeService.getDocumentClass().equals(DocumentClass.IMPORT_CHARGES) && tradeService.getServiceType().equals(ServiceType.PAYMENT)) {
//                                chargeMap.put("remittanceFeePHP", BigDecimal.ZERO);
//                                chargeMap.put("remittanceFeeSpecialPHP", tempSC.getAmount());
//                                chargeMap.put("remittanceFeeSpecialUSD", new BigDecimal("18").setScale(2, BigDecimal.ROUND_UNNECESSARY));
                            }else if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && tradeService.getServiceType().equals(ServiceType.NEGOTIATION) && "Y".equalsIgnoreCase(swiftFlag)) {
                                chargeMap.put("remittanceFeePHP", BigDecimal.ZERO);
                                chargeMap.put("remittanceFeeSpecialPHP", tempSC.getAmount());
                                chargeMap.put("remittanceFeeSpecialUSD", new BigDecimal("18").setScale(2, BigDecimal.ROUND_UNNECESSARY));
                            }else if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && tradeService.getServiceType().equals(ServiceType.SETTLEMENT) &&
                                    (tradeService.getDocumentClass().equals(DocumentClass.DA)||
                                            tradeService.getDocumentClass().equals(DocumentClass.DP)||
                                            tradeService.getDocumentClass().equals(DocumentClass.DR)||
                                            tradeService.getDocumentClass().equals(DocumentClass.OA)
                                    )
                                    && "Y".equalsIgnoreCase(swiftFlag)) {
                                chargeMap.put("remittanceFeePHP", BigDecimal.ZERO);
                                chargeMap.put("remittanceFeeSpecialPHP", tempSC.getCollectibleAmount());
                                chargeMap.put("remittanceFeeSpecialUSD", new BigDecimal("18").setScale(2, BigDecimal.ROUND_UNNECESSARY));
                            }

                            //Added for UA Loan
                            else  if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && tradeService.getServiceType().equals(ServiceType.UA_LOAN_SETTLEMENT) && "Y".equalsIgnoreCase(swiftFlag)) {
                                chargeMap.put("remittanceFeePHP", BigDecimal.ZERO);
                                chargeMap.put("remittanceFeeSpecialPHP", tempSC.getCollectibleAmount());
                                chargeMap.put("remittanceFeeSpecialUSD", new BigDecimal("18").setScale(2, BigDecimal.ROUND_UNNECESSARY));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("INTEREST")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("interestPHP", tempSC.getAmount());


                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("interestPHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("interestPHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("BSP")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("bspCommissionGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("bspCommissionPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                                chargeMap.put("bspCommissionGrossPHP", tempSC.getCollectibleAmount().setScale(0, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                    chargeMap.put("bspCommissionGrossPHP", tempSC.getCollectibleDefaultAmount().setScale(0, chargesRoundingMode));
                                }
                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("ADVISING-EXPORT")) {
                            System.out.println("angol angol angol ADVISING-EXPORT cwtFlag:"+cwtFlag);
                            System.out.println("angol angol angol cwtFlag:"+cwtFlag);
                            System.out.println("angol angol angol !cwtFlag.equalsIgnoreCase(\"0\"):"+!cwtFlag.equalsIgnoreCase("0"));
                            if (!cwtFlag.equalsIgnoreCase("N")&&!cwtFlag.equalsIgnoreCase("0")) { // no cwt was removed
                                System.out.println("angol angol angol cwtFlag:"+cwtFlag);

                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));
                                    System.out.println("angol angol angol 00001");

                                    BigDecimal originalAmount = tempSC.getCollectibleAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                    BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(originalAmount);
                                    chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                    chargeMap.put("advisingExportFeePHP", originalAmount);

                                } else {
                                    System.out.println("angol angol angol 00002");
//                                    chargesAmountNet = chargesAmountNet.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargesAmount = chargesAmount.add(tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));
//                                    chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getDefaultAmount().setScale(0, chargesRoundingMode));

                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        System.out.println("angol angol angol 00003");
                                        BigDecimal originalAmount = tempSC.getCollectibleDefaultAmount().divide(new BigDecimal("0.98"), 6, chargesRoundingMode);
                                        BigDecimal cwtAmount = originalAmount.subtract(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(originalAmount);
                                        chargesAmountCWT = chargesAmountCWT.add(cwtAmount);
                                        chargeMap.put("advisingExportFeePHP", originalAmount);
                                    }
                                }
                            } else {
                                System.out.println("angol angol angol 00004");
//                                chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                                chargesAmount = chargesAmount.add(tempSC.getAmount());
//                                chargeMap.put("bookingCommissionFeeGrossPHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                                System.out.println("angol angol angol cwtFlag no cwt:"+cwtFlag);
                                String amountBasedFlag = "N";
                                amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);

                                if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                    System.out.println("angol angol angol 00005");
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("advisingExportFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                } else {
                                    if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                    } else {
                                        System.out.println("angol angol angol 00006");
                                        chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                        chargeMap.put("advisingExportFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    }
                                }


                            }


                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("CORRES-ADDITIONAL")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("additionalCorresFeePHP", tempSC.getAmount());
                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("additionalCorresFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("additionalCorresFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }

                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("POSTAGE")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargesAmount = chargesAmount.add(tempSC.getAmount().setScale(0, chargesRoundingMode));
//                            chargeMap.put("postageFeePHP", tempSC.getAmount().setScale(0, chargesRoundingMode));

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);


                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("postageFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("postageFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }
                        } else if (tempSC.getChargeId().toString().equalsIgnoreCase("OTHER-EXPORT")) {
//                            chargesAmountNet = chargesAmountNet.add(tempSC.getAmount());
//                            chargesAmount = chargesAmount.add(tempSC.getAmount());
//                            chargeMap.put("otherExportFeePHP", tempSC.getAmount());

                            String amountBasedFlag = "N";
                            amountBasedFlag = getStringDefaultOrAmount(tempSC, amountBasedFlag);
                            
                            if ("Y".equalsIgnoreCase(tempSC.getOverridenFlag())||"Y".equalsIgnoreCase(amountBasedFlag)) {
                                chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargesAmount = chargesAmount.add(tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                                chargeMap.put("otherExportFeePHP", tempSC.getCollectibleAmount().setScale(2, chargesRoundingMode));
                            } else {
                                if(tempSC.getAmount().compareTo(BigDecimal.ZERO)==0){

                                } else {
                                    chargesAmountNet = chargesAmountNet.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargesAmount = chargesAmount.add(tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                    chargeMap.put("otherExportFeePHP", tempSC.getCollectibleDefaultAmount().setScale(2, chargesRoundingMode));
                                }
                            }
                        }
                    }

                    if (s.equalsIgnoreCase("PC")) {
                        ProductCharge tempPC = (ProductCharge) o;
                        System.out.println("|Product Charge: " + "-Currency:" + tempPC.getCurrency() + "-Amount:" + tempPC.getAmount());

                        //Convert to produce the three lcAmount
                        if (tempPC.getCurrency().toString().equalsIgnoreCase("PHP")) {
                            chargeMap.put("lcAmountPHP", tempPC.getAmount());
                            if (specialRateUsdToPhpServiceCharge != null && specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().divide(specialRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_EVEN));
                            } else if (passOnRateUsdToPhpServiceCharge != null && passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().divide(passOnRateUsdToPhpServiceCharge, 2, BigDecimal.ROUND_HALF_EVEN));
                            }

                            if (specialRateThirdToPhpServiceCharge != null && specialRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdTimesUsdToPhpConversionRate, 2, BigDecimal.ROUND_HALF_UP));
                            } else if (passOnRateThirdToPhpServiceCharge != null && passOnRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdTimesUsdToPhpConversionRate, 2, BigDecimal.ROUND_HALF_UP));
                            }
                        } else if (tempPC.getCurrency().toString().equalsIgnoreCase("USD")) {
                            chargeMap.put("lcAmountUSD", tempPC.getAmount());

                            if (specialRateUsdToPhpServiceCharge != null && specialRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(specialRateUsdToPhpServiceCharge));
                            } else if (passOnRateUsdToPhpServiceCharge != null && passOnRateUsdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(passOnRateUsdToPhpServiceCharge));
                            }

                            if (specialRateThirdToUsdServiceCharge != null && specialRateThirdToUsdServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(specialRateThirdToUsdServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                            } else if (passOnRateThirdToPhpServiceCharge != null && passOnRateThirdToPhpServiceCharge.compareTo(BigDecimal.ZERO) == 1) {
                                chargeMap.put("lcAmountTHIRD", tempPC.getAmount().divide(passOnRateThirdToUsdServiceCharge, 2, BigDecimal.ROUND_HALF_UP));
                            }
                        } else {
                            chargeMap.put("lcAmountTHIRD", tempPC.getAmount());

                            if (specialRateThirdToUsdServiceCharge != null) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().multiply(specialRateThirdToUsdServiceCharge));
                            } else if (passOnRateThirdToUsdServiceCharge != null) {
                                chargeMap.put("lcAmountUSD", tempPC.getAmount().multiply(passOnRateThirdToUsdServiceCharge));
                            }

                            //TODO:FIX THIS
                            if (specialRateThirdToUsdTimesUsdToPhpConversionRate != null) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(specialRateThirdToUsdTimesUsdToPhpConversionRate));
                            } else if (passOnRateThirdToPhpServiceCharge != null) {
                                chargeMap.put("lcAmountPHP", tempPC.getAmount().multiply(passOnRateThirdToPhpServiceCharge));
                            }

                        }

                        System.out.println("lcAmountPHP:" + chargeMap.get("lcAmountPHP"));
                        System.out.println("lcAmountUSD:" + chargeMap.get("lcAmountUSD"));
                        System.out.println("lcAmountTHIRD:" + chargeMap.get("lcAmountTHIRD"));
                    }
                }
            }
        }

        chargeMap.put("chargesAmountNetPHP", chargesAmountNet);
        chargeMap.put("chargesAmountPHP", chargesAmount);
        chargeMap.put("chargesAmountCWTPHP", chargesAmountCWT);

        System.out.println("chargesAmountNet:" + chargesAmountNet);
        System.out.println("chargesAmount:" + chargesAmount);
        System.out.println("chargesAmountCWT:" + chargesAmountCWT);

        // Charges Amount
        chargeMap.put("chargesAmountNetUSD", divideOrReturnZero(chargesAmountNet, passOnUrrServiceCharge));
        chargeMap.put("chargesAmountUSD", divideOrReturnZero(chargesAmount, passOnUrrServiceCharge));
        chargeMap.put("chargesAmountCWTUSD", divideOrReturnZero(chargesAmountCWT, passOnUrrServiceCharge));

        if (specialRateThirdToUsdTimesUrrConversionRate != null) {
            chargeMap.put("chargesAmountNetTHIRD", divideOrReturnZero(chargesAmountNet, specialRateThirdToUsdTimesUrrConversionRate));
            chargeMap.put("chargesAmountTHIRD", divideOrReturnZero(chargesAmount, specialRateThirdToUsdTimesUrrConversionRate));
            chargeMap.put("chargesAmountCWTTHIRD", divideOrReturnZero(chargesAmountCWT, specialRateThirdToUsdTimesUrrConversionRate));
        }

        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("chargeMap:"+chargeMap);
        return chargeMap;
    }

    /**
     * Method used to determine whether Amount or Default Amount field is the one used in the generation of accounting
     * @param tempSC
     * @param amountBasedFlag
     * @return Y if ServiceCharge field to use in accounting is Amount rather than DefaultAmount
     */
    private static String getStringDefaultOrAmount(ServiceCharge tempSC, String amountBasedFlag) {
        if(tempSC.getAmount().compareTo(tempSC.getDefaultAmount())==1){
            BigDecimal baseAmount = tempSC.getAmount().subtract(tempSC.getDefaultAmount());
            if(baseAmount.compareTo(BigDecimal.ONE)==1){
                amountBasedFlag = "Y";
            }

        } else {
            BigDecimal baseAmount = tempSC.getDefaultAmount().subtract(tempSC.getAmount());
            if(baseAmount.compareTo(BigDecimal.ONE)==1){
                amountBasedFlag = "Y";
            }
        }
        return amountBasedFlag;
    }

    /**
     * This loops through paymentProduct to determine if a non PHP currency was used to pay.
     * Will return true if a non PHP currency payment was used.
     */
    private static Boolean willWeChargeCilex(Payment paymentProduct) {
        Boolean cilexCharged = false;
        //LOOP TO Determine if cilex is charged
        Set<PaymentDetail> temp;
        if (paymentProduct != null) {
            //Booking is done from payment currency to lc currency
            temp = paymentProduct.getDetails();
            for (PaymentDetail paymentDetail : temp) {
                if (!paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")) {
                    cilexCharged = true;
                }
            }
        }

        System.out.println("willWeChargeCilex:" +cilexCharged);
        return cilexCharged;
    }

    /**
     * Method used to check existence of configured Accounting Entries against the GL Mast
     */
    @Transactional
    public void getInvalidAccountingCode() {
        HashMap<String, String> accountingEntryRepositoryEntriesAll = accountingEntryRepository.getEntriesAll();
        List<Map<String, ?>> glMastRepositoryAllEntries = glMastRepository.getAllEntries();

        HashMap<String, String> glMastList = new HashMap<String, String>();
        for (Map<String, ?> glmastentry : glMastRepositoryAllEntries) {
            try {
                glMastList.put(glmastentry.get("ACCT_NO").toString(), glmastentry.get("TITLE").toString());
                
                System.out.println("-------------------------yahoo angol--------------------------------------");
                System.out.println(glmastentry.get("ACCT_NO"));
                System.out.println(glmastentry.get("TITLE"));
                System.out.println("------------------------------------------------------------------------------------");
            } catch (Exception e) {
                System.out.println("------------------------------------------------------------------------------------");
                System.out.println(glmastentry.get("ACCT_NO"));
                System.out.println(glmastentry.get("TITLE"));
                System.out.println("------------------------------------------------------------------------------------");
            }
        }

        for (String t : accountingEntryRepositoryEntriesAll.keySet()) {
            String particular = accountingEntryRepositoryEntriesAll.get(t);
            if (glMastList.containsKey(t)) {
            	
            	System.out.println("yahoo nothing todo");

            } else {
            	System.out.println("save valueHolderRepository");
                valueHolderRepository.save(new ValueHolder(t, particular));
                System.out.println("see  valueHolderRepository saved " + t + "and particular " + particular);
                
            }
        }
    }

    /**
     * Calls @method getExcessPayments type CHARGE
     *
     * @param tradeService   TradeService where excess service charge payment will be computed
     * @param paymentService Payment object for Service Charge Payment
     * @return map that contains excess payment
     */
    @Transactional
    Map<String, Object> getExcessPaymentsServiceCharge(TradeService tradeService, Payment paymentService, Payment paymentProduct, Payment paymentSettlement) {
        return getExcessPayments(tradeService, paymentProduct, paymentService, "SERVICE", paymentSettlement);
    }

    /**
     * Calls @method getExcessPayments type PRODUCT
     *
     * @param tradeService   TradeService where excess product charge payment will be computed
     * @param paymentProduct Payment object for Product Payment
     * @return map that contains excess payment
     */
    @Transactional
    Map<String, Object> getExcessPaymentsProductCharge(TradeService tradeService, Payment paymentProduct, Payment paymentSettlement) {
        return getExcessPayments(tradeService, paymentProduct, null, "PRODUCT", paymentSettlement);
    }

    /**
     * Returns excess payment Charge/Product depending on @param type
     *
     * @param tradeService   TradeService where excess product charge payment will be computed
     * @param paymentProduct Payment object for Product Payment
     * @param paymentService Payment object for Service Charge Payment
     * @param type           CHARGE/SERVICE type
     * @return map that contains excess payment
     *         TODO: CHECK Logic
     */
    @Transactional
    Map<String, Object> getExcessPayments(TradeService tradeService, Payment paymentProduct, Payment paymentService, String type, Payment paymentSettlement) {

        Map<String, Object> excessPaymentMap = new HashMap<String, Object>();
        BigDecimal amount = BigDecimal.ZERO;
        try {
            //This assumes that unused charges has already been removed from the tradeservice list for charges


            String lcCurrency = "";
            if (tradeService.getDetails().containsKey("currency") && tradeService.getDetails().get("currency") != null) {
                lcCurrency = (String) tradeService.getDetails().get("currency");
            } else if (tradeService.getDetails().containsKey("negotiationCurrency") && tradeService.getDetails().get("negotiationCurrency") != null) {
                lcCurrency = (String) tradeService.getDetails().get("negotiationCurrency");
            } else if (tradeService.getDetails().containsKey("settlementCurrency") && tradeService.getDetails().get("settlementCurrency") != null) {
                lcCurrency = (String) tradeService.getDetails().get("settlementCurrency");
            } else if (tradeService.getDetails().containsKey("draftCurrency") && tradeService.getDetails().get("draftCurrency") != null) {
                lcCurrency = (String) tradeService.getDetails().get("draftCurrency");
            } else if (tradeService.getDetails().containsKey("productCurrency") && tradeService.getDetails().get("productCurrency") != null) {
                lcCurrency = (String) tradeService.getDetails().get("productCurrency");
            } else if (tradeService.getDetails().containsKey("hiddenCurrency") && tradeService.getDetails().get("hiddenCurrency") != null) {
                lcCurrency = (String) tradeService.getDetails().get("hiddenCurrency");
            } else if (tradeService.getDetails().containsKey("savedCurrency") && tradeService.getDetails().get("savedCurrency") != null) {
                lcCurrency = (String) tradeService.getDetails().get("savedCurrency");
            }


            if (type.equalsIgnoreCase("PRODUCT") && paymentProduct != null) {
                System.out.println("PRODUCT EXCESS CHARGE");
                BigDecimal chargeInOrig = BigDecimal.ZERO;
                Map<String, Object> details = tradeService.getDetails();
                if (tradeService.getDocumentClass().equals(DocumentClass.LC) || tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)) {
                    //LC
                    //CASH OPENING
                    //SB RS NEGOTIATION
                    //UA LOAN SETTLEMENT

                    if (ServiceType.NEGOTIATION.equals(tradeService.getServiceType())) {
                        System.out.println("negotiationAmount:" + details.get("negotiationAmount"));
                        amount = new BigDecimal(details.get("negotiationAmount").toString());
                    } else if (ServiceType.OPENING.equals(tradeService.getServiceType())) {
                        System.out.println("opening amount:" + details.get("amount"));
                        amount = new BigDecimal(details.get("amount").toString());
                    } else if (ServiceType.ADJUSTMENT.equals(tradeService.getServiceType())) {
                        System.out.println("adjustmentAmount:" + details.get("amount"));
                        amount = new BigDecimal(details.get("amount").toString());
                    } else if (ServiceType.CANCELLATION.equals(tradeService.getServiceType())) {
                        System.out.println("cancellation outstandingBalance:" + details.get("outstandingBalance"));
                        amount = new BigDecimal(details.get("outstandingBalance").toString());
                    } else if (ServiceType.UA_LOAN_SETTLEMENT.equals(tradeService.getServiceType())) {
                        System.out.println("UA_LOAN_SETTLEMENT amount:" + details.get("amount"));
                        amount = new BigDecimal(details.get("amount").toString());
                    } else if (ServiceType.UA_LOAN_MATURITY_ADJUSTMENT.equals(tradeService.getServiceType())) {
                        System.out.println("UA_LOAN_MATURITY_ADJUSTMENT amount:" + details.get("amount"));
                        amount = new BigDecimal(details.get("amount").toString());
                    } else if (ServiceType.AMENDMENT.equals(tradeService.getServiceType())) {
                        System.out.println("AMENDMENT amount:" + details.get("amount"));


                        //String expiryDateSwitch = getStringOrReturnEmptyString(tradeService.getDetails(),"expiryDateSwitch"); //on off
                        String amountSwitch = getStringOrReturnEmptyString(tradeService.getDetails(), "amountSwitch"); //on off
                        String tenorSwitch = getStringOrReturnEmptyString(tradeService.getDetails(), "tenorSwitch"); //on off
                        String lcAmountFlag = getStringOrReturnEmptyString(tradeService.getDetails(), "lcAmountFlag"); // INC and DEC
                        String currency = getStringOrReturnEmptyString(tradeService.getDetails(), "currency"); // INC and DEC
                        String creationExchangeRateUsdToPHPUrrStr = getStringOrReturnEmptyString(tradeService.getDetails(), "creationExchangeRateUsdToPHPUrr");//TODO: REDO THIS
                        //String expiryDateFlag = getStringOrReturnEmptyString(tradeService.getDetails(),"expiryDateFlag"); // EXT and RED


                        BigDecimal amendmentAmount;
                        BigDecimal urr = parseOrReturnZero(creationExchangeRateUsdToPHPUrrStr);

                        //Check if Increase
                        if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("INC")) {
                            String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amount");
                            BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                            String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                            BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                            amount = amountTo.subtract(amountFrom);

                        }
                        //Check if Decrease
                        if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("DEC")) {
                            String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amount");
                            BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                            String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                            BigDecimal amountTo = parseOrReturnZero(strAmountTo);
                            amount = amountFrom.subtract(amountTo);
                        }

                        //Check if Change in Tenor from sight to usance
                        if (tenorSwitch.equalsIgnoreCase("on")) {

                            String strAmountFrom = getStringOrReturnEmptyString(tradeService.getDetails(), "amount");
                            BigDecimal amountFrom = parseOrReturnZero(strAmountFrom);

                            String strAmountTo = getStringOrReturnEmptyString(tradeService.getDetails(), "amountTo");
                            BigDecimal amountTo = parseOrReturnZero(strAmountTo);

                            if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("INC")) {
                                amount = amountTo.subtract(amountFrom);
                            } else if (amountSwitch.equalsIgnoreCase("on") && lcAmountFlag.equalsIgnoreCase("DEC")) {
                                amount = amountFrom.subtract(amountTo);
                            } else {
                                amount = amountFrom;
                            }

                        }
//                        amount = new BigDecimal(details.get("amount").toString());
                    } else {
                        System.out.println("productAmount:" + details.get("productAmount"));
                        amount = new BigDecimal(details.get("productAmount").toString());
                    }

                } else if (tradeService.getDocumentClass().equals(DocumentClass.DA) ||
                        tradeService.getDocumentClass().equals(DocumentClass.DP) ||
                        tradeService.getDocumentClass().equals(DocumentClass.DR) ||
                        tradeService.getDocumentClass().equals(DocumentClass.OA)) { //NON-LC
                    //ONLY SETTLEMENT
                    if (ServiceType.SETTLEMENT.equals(tradeService.getServiceType())) {
                        System.out.println("settlement productAmount:" + details.get("productAmount"));
                        amount = new BigDecimal(details.get("productAmount").toString());
                    } else if (ServiceType.CANCELLATION.equals(tradeService.getServiceType())) {
                        System.out.println("cancellation outstandingAmount:" + details.get("outstandingAmount"));
                        amount = new BigDecimal(details.get("outstandingAmount").toString());
                    }

                } else if (tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING)) {
                    if (ServiceType.OPENING_ADVISING.equals(tradeService.getServiceType())) {

                    } else if (ServiceType.CANCELLATION_ADVISING.equals(tradeService.getServiceType())) {

                    } else if (ServiceType.AMENDMENT_ADVISING.equals(tradeService.getServiceType())) {

                    } else if (ServiceType.CANCELLATION_ADVISING.equals(tradeService.getServiceType())) {

                    }
                } else if(tradeService.getDocumentClass().equals(DocumentClass.CORRES_CHARGE)){
                    //TODO
                    //AMOUNT IN BILLING CURRENCY
                    lcCurrency = (String) tradeService.getDetails().get("billingCurrency");

                    BigDecimal outstandingCorresCharge = BigDecimal.ZERO;
                    if(tradeService.getDetails().containsKey("outstandingCorresCharge")){
                        outstandingCorresCharge = new BigDecimal(details.get("outstandingCorresCharge").toString());
                    }

                    BigDecimal totalBillingAmountInPhp = BigDecimal.ZERO;
                    if(tradeService.getDetails().containsKey("totalBillingAmountInPhp")){
                        totalBillingAmountInPhp = new BigDecimal(details.get("totalBillingAmountInPhp").toString());
                    }
                    System.out.println("totalBillingAmountInPhp:"+totalBillingAmountInPhp);
                    System.out.println("outstandingCorresCharge:"+outstandingCorresCharge);

                    if(totalBillingAmountInPhp.compareTo(outstandingCorresCharge)==1){
                        amount = totalBillingAmountInPhp.subtract(outstandingCorresCharge);
                    }
                    System.out.println("corres PHP AMOUNT:"+amount);
                    BigDecimal usd_php_rate = new BigDecimal(tradeService.getDetails().get("USD-PHP_special_rate_cash").toString());
                    if(lcCurrency.equalsIgnoreCase("USD")){
                        amount = amount.divide(usd_php_rate,2,BigDecimal.ROUND_UP);
                    } else if(!lcCurrency.equalsIgnoreCase("USD") && !lcCurrency.equalsIgnoreCase("PHP")){
                        String third_usd_rate_str = lcCurrency+"-USD_special_rate_cash";
                        BigDecimal third_usd_rate = new BigDecimal(tradeService.getDetails().get(third_usd_rate_str).toString());
                        System.out.println("third_usd_rate:"+third_usd_rate);
                        amount = amount.divide(third_usd_rate.multiply(usd_php_rate),2,BigDecimal.ROUND_UP);
                    }
                    System.out.println("corres Billing Currency AMOUNT:"+amount);

                } else if(tradeService.getDocumentClass().equals(DocumentClass.BC) && tradeService.getServiceCharge().equals(ServiceType.SETTLEMENT)){
                    System.out.println("settlement totalAmountDueLc:" + details.get("totalAmountDueLc"));
                    amount = new BigDecimal(details.get("totalAmountDueLc").toString().replace(",",""));
                } else if(tradeService.getDocumentClass().equals(DocumentClass.BP) && tradeService.getServiceCharge().equals(ServiceType.SETTLEMENT)){
                    System.out.println("settlement amountDue:" + details.get("amountDue"));
                    amount = new BigDecimal(details.get("amountDue").toString().replace(",",""));
                }


                System.out.println(amount);

                //Extract product amount
                //Extract product currency
                System.out.println("chargeInOrig:" + chargeInOrig);
                Currency currencyInOrig = tradeService.getProductChargeCurrency();
                System.out.println("currencyInOrig:" + currencyInOrig); //Check here causing error
                if(currencyInOrig == null){
                    currencyInOrig = Currency.getInstance(lcCurrency);
                    System.out.println("currencyInOrig = Currency.getInstance(lcCurrency); ::"+currencyInOrig);
                }
                BigDecimal paidInOrig = paymentProduct.getTotalPaid(currencyInOrig);
                System.out.println("paidInOrig:" + paidInOrig);
                //amount = paidInOrig.subtract(chargeInOrig);
                excessPaymentMap.put("productExcess", amount);
                excessPaymentMap.put("productCurrency", currencyInOrig);


                System.out.println("lcCurrency:" + lcCurrency);
                BigDecimal totalPaidInLcCurrency = BigDecimal.ZERO;
                Set<PaymentDetail> temp = paymentProduct.getDetails();
//                int onlyOnceCounter = 0;
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    printPaymentDetails(paymentDetail);

                    System.out.println("paymentDetail.getAmount():" + paymentDetail.getAmount());
                    System.out.println("paymentDetail.getAmountInLcCurrency():" + paymentDetail.getAmountInLcCurrency());

                    if (paymentDetail.getAmountInLcCurrency() != null && paymentDetail.getAmountInLcCurrency().compareTo(BigDecimal.ZERO) == 1) {
                        totalPaidInLcCurrency = totalPaidInLcCurrency.add(paymentDetail.getAmountInLcCurrency());
                    } else {

                        totalPaidInLcCurrency = totalPaidInLcCurrency.add(paymentDetail.getAmount());
                    }

                }

                System.out.println("lcCurrency:" + lcCurrency);
                System.out.println("totalPaidInLcCurrency :" + totalPaidInLcCurrency);
                System.out.println("amount to be paid in lc currency:" + amount);
                System.out.println("excess in lc currency:" + totalPaidInLcCurrency.subtract(amount));
                BigDecimal amountExcessInLcCurrency = totalPaidInLcCurrency.subtract(amount);
                System.out.println("excess in lc currency:" + amountExcessInLcCurrency);
//                //Determine which payment detail can be booked as with excess
//                PaymentDetail chosenPaymentDetail;
//                BigDecimal remainingAmount = amount;
//                Set<PaymentDetail> filtered = new HashSet<PaymentDetail>();
//                Set<PaymentDetail> checkOrRemittance = filterCheckOrRemittanceEnough(paymentProduct, amount, currencyInOrig);
//                Set<PaymentDetail> t = paymentProduct.getDetails();

                //Assumption: Only one rate for a given currency
                //TODO: FIX THIS
                if ("PHP".equalsIgnoreCase(lcCurrency)) {
                    System.out.println("PHP EXCESS");
                    excessPaymentMap.put("excessPaymentPHP", amountExcessInLcCurrency);
                    excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentPHP", amountExcessInLcCurrency);

                    BigDecimal otherAmount = amountExcessInLcCurrency;
                    PaymentDetail paymentDetail = getPaymentDetail(paymentProduct, "USD");
                    if (paymentDetail != null && paymentDetail.getUrr() != null) {
                        otherAmount = otherAmount.divide(paymentDetail.getUrr(), 2, BigDecimal.ROUND_UP);
                        excessPaymentMap.put("excessPaymentUSD", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                        excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentUSD", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                    } else {
                        if (details.get("urr") != null) {
                            String tempUrr = (String) details.get("urr");
                            BigDecimal urr = new BigDecimal(tempUrr);
                            otherAmount = otherAmount.divide(urr, 2, BigDecimal.ROUND_UP);
                            excessPaymentMap.put("excessPaymentUSD", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                            excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentUSD", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                        }
                    }


                } else if ("USD".equalsIgnoreCase(lcCurrency)) {
                    System.out.println("USD EXCESS");
                    excessPaymentMap.put("excessPaymentUSD", amountExcessInLcCurrency);
                    excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentUSD", amountExcessInLcCurrency);

                    BigDecimal otherAmount = amountExcessInLcCurrency;
                    PaymentDetail paymentDetail = getPaymentDetail(paymentProduct, "USD");
                    if (paymentDetail == null) {
                        paymentDetail = getPaymentDetail(paymentProduct, "PHP");
                    }

                    if (paymentDetail != null && paymentDetail.getUrr() != null) {
                        otherAmount = otherAmount.multiply(paymentDetail.getUrr());
                    } else {
                        if (details.get("urr") != null) {
                            String tempUrr = (String) details.get("urr");
                            BigDecimal urr = new BigDecimal(tempUrr);
                            otherAmount = otherAmount.multiply(urr);
                        }
                    }
                    excessPaymentMap.put("excessPaymentPHP", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                    excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentPHP", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                } else if (!"PHP".equalsIgnoreCase(lcCurrency) && !"USD".equalsIgnoreCase(lcCurrency)) {
                    System.out.println("THIRD EXCESS");
                    excessPaymentMap.put("excessPaymentTHIRD", amountExcessInLcCurrency);
                    excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentTHIRD", amountExcessInLcCurrency);
                    BigDecimal otherAmount = amountExcessInLcCurrency;
                    PaymentDetail paymentDetail = getPaymentDetail(paymentProduct, "THIRD");
                    if (paymentDetail == null) {
                        paymentDetail = getPaymentDetail(paymentProduct, "USD");
                    }
                    if (paymentDetail == null) {
                        paymentDetail = getPaymentDetail(paymentProduct, "PHP");
                    }

                    System.out.println("paymentDetail:" + paymentDetail);
                    if (paymentDetail != null) {
                        if (paymentDetail.getUrr() != null && paymentDetail.getPassOnRateThirdToUsd() != null) {
                            System.out.println("paymentDetail.getSpecialRateThirdToUsd():" + paymentDetail.getSpecialRateThirdToUsd());
                            System.out.println("paymentDetail.paymentDetail.getUrr():" + paymentDetail.getUrr());

                            BigDecimal otherAmountOrig = otherAmount;
                            otherAmount = otherAmount.multiply(paymentDetail.getUrr().multiply(paymentDetail.getSpecialRateThirdToUsd()));
                            System.out.println("otherAmount 00:" + otherAmount);
                            excessPaymentMap.put("excessPaymentPHP", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                            excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentPHP", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                            otherAmount = otherAmountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd());
                            excessPaymentMap.put("excessPaymentUSD", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                            excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentUSD", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));

                        } else {
                            if (details.get("urr") != null && paymentDetail.getPassOnRateThirdToUsd() != null) {
                                String tempUrr = (String) details.get("urr");
                                System.out.println("tempUrr:" + tempUrr);
                                System.out.println("paymentDetail.getSpecialRateThirdToUsd():" + paymentDetail.getSpecialRateThirdToUsd());
                                BigDecimal urr = new BigDecimal(tempUrr);
                                BigDecimal otherAmountOrig = otherAmount;
                                otherAmount = otherAmount.multiply(urr.multiply(paymentDetail.getSpecialRateThirdToUsd()));
                                excessPaymentMap.put("excessPaymentPHP", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                                excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentPHP", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                                otherAmount = otherAmountOrig.multiply(paymentDetail.getSpecialRateThirdToUsd());
                                excessPaymentMap.put("excessPaymentUSD", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                                excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentUSD", otherAmount.setScale(2,BigDecimal.ROUND_FLOOR));
                            }
                        }
                    }
                }


            } else if (type.equalsIgnoreCase("SERVICE") && paymentService != null) {
                System.out.println("SERVICE EXCESS CHARGE");
                //REDO THIS



                Map<String, List<Object>> chargesSummaryMap = tradeService.getChargesSummary();
                System.out.println("chargesSummaryMap:"+chargesSummaryMap);
                Boolean cilexCharged = willWeChargeCilex(paymentProduct);
                if(DocumentClass.BC.equals(tradeService.getDocumentClass()) || DocumentClass.BP.equals(tradeService.getDocumentClass())){
                    if(!cilexCharged){
                        cilexCharged = willWeChargeCilex(paymentSettlement);
                    }
                }


                Map<String, Object> chargeMap = generate_EXPORTS_CHARGES_ValuesMap(tradeService.getDetails(), cilexCharged, chargesSummaryMap, tradeService, "PHP", null);

                System.out.println("chargesAmountNetPHP:"+chargeMap.get("chargesAmountNetPHP"));
                System.out.println("chargesAmountPHP:"+chargeMap.get("chargesAmountPHP"));
                System.out.println("chargesAmountCWTPHP:"+chargeMap.get("chargesAmountCWTPHP"));
                System.out.println("chargesAmountOrig:"+chargeMap.get("chargesAmountOrig"));

//                BigDecimal chargeInOrig = (BigDecimal)chargeMap.get("chargesAmountNetPHP");
                BigDecimal chargeInOrig = (BigDecimal)chargeMap.get("chargesAmountOrig");
                System.out.println("chargeInOrig:" + chargeInOrig);
                String settlementCurrencyCharges = "";
                if(tradeService.getServiceChargeCurrency()!=null){
                    settlementCurrencyCharges = tradeService.getServiceChargeCurrency().toString();
                }
                if(tradeService.getDetails().get("settlementCurrency")!=null){
                    settlementCurrencyCharges = tradeService.getDetails().get("settlementCurrency").toString();
                } else{
                    settlementCurrencyCharges = "PHP";
                }


                System.out.println("settlementCurrencyCharges:"+settlementCurrencyCharges);
                Currency currencyInOrig = Currency.getInstance(settlementCurrencyCharges);
                System.out.println("currencyInOrig:" + currencyInOrig);
                BigDecimal paidInOrig = paymentService.getTotalPaid(currencyInOrig);
                System.out.println("paidInOrig:" + paidInOrig);
                amount = paidInOrig.subtract(chargeInOrig);
                excessPaymentMap.put("serviceExcess", amount);
                excessPaymentMap.put("serviceCurrency", currencyInOrig);

                Set<PaymentDetail> temp = paymentService.getDetails();
                PaymentDetail paymentDetailTemp = null;
                for (PaymentDetail paymentDetail : temp) {
                    System.out.println("---------------------------");
                    //printPaymentDetails(paymentDetail);
                    if (paymentDetailTemp == null) {
                        paymentDetailTemp = paymentDetail;
                    } else if (paymentDetail.getCurrency().equals(currencyInOrig)) {
                        paymentDetailTemp = paymentDetail;
                    }

                    System.out.println("---------------------------");
                }


                if (currencyInOrig.getCurrencyCode().equalsIgnoreCase("PHP")) {
                    excessPaymentMap.put("excessPaymentPHP", amount);
                    excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentPHP", amount);
                } else if (currencyInOrig.getCurrencyCode().equalsIgnoreCase("USD")) {
                    excessPaymentMap.put("excessPaymentUSD", amount);
                    excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentUSD", amount);
                    BigDecimal otherAmount = amount;
//                    if (paymentDetailTemp != null && paymentDetailTemp.getSpecialRateUsdToPhp() != null) {
//                        otherAmount = otherAmount.multiply(paymentDetailTemp.getSpecialRateUsdToPhp());
//                    } else if (tradeService.getSpecialRateUsdToPhpServiceCharge() != null) {
//                        otherAmount = otherAmount.multiply(tradeService.getSpecialRateUsdToPhpServiceCharge());
//                    } else if (tradeService.getPassOnRateUsdToPhpServiceCharge() != null) {
//                        otherAmount = otherAmount.multiply(tradeService.getPassOnRateUsdToPhpServiceCharge());
//                    }
                    otherAmount = otherAmount.multiply(paymentDetailTemp.getUrr());
                    excessPaymentMap.put("excessPaymentPHP", otherAmount);
                    excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentPHP", otherAmount);
                } else {
                    excessPaymentMap.put("excessPaymentTHIRD", amount);
                    excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentTHIRD", amount);
                    BigDecimal otherAmount = amount;

                    if (paymentDetailTemp != null) {
                        if (paymentDetailTemp.getSpecialRateThirdToUsd() != null) {
                            otherAmount = otherAmount.multiply(paymentDetailTemp.getSpecialRateThirdToUsd());
                        } else if (tradeService.getSpecialRateThirdToUsdServiceCharge() != null) {
                            otherAmount = otherAmount.multiply(tradeService.getSpecialRateThirdToUsdServiceCharge());
                        } else if (tradeService.getPassOnRateThirdToUsdServiceCharge() != null) {
                            otherAmount = otherAmount.multiply(tradeService.getPassOnRateThirdToUsdServiceCharge());
                        }
                        excessPaymentMap.put("excessPaymentUSD", otherAmount);
                        excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentUSD", otherAmount);

//                        if (paymentDetailTemp.getSpecialRateUsdToPhp() != null) {
//                            otherAmount = otherAmount.multiply(paymentDetailTemp.getSpecialRateUsdToPhp());
//                        } else if (tradeService.getSpecialRateUsdToPhpServiceCharge() != null) {
//                            otherAmount = otherAmount.multiply(tradeService.getSpecialRateUsdToPhpServiceCharge());
//                        } else if (tradeService.getPassOnRateUsdToPhpServiceCharge() != null) {
//                            otherAmount = otherAmount.multiply(tradeService.getPassOnRateUsdToPhpServiceCharge());
//                        }

                        otherAmount = otherAmount.multiply(paymentDetailTemp.getUrr());
                        excessPaymentMap.put("excessPaymentPHP", otherAmount);
                        excessPaymentMap.put("APRESOTHERSproductPaymentTotalExcessPaymentPHP", otherAmount);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return excessPaymentMap;
    }

    /**
     * Because rates are stored within the payment detail to get a valid rate first get the payment details matching the needed currency
     * This uses the assumption that only one rate exists for a given currency and thus the rates of a payment detail with the same currency
     * may be used as the conversion rate.
     *
     * @param paymentProduct Payment object for Product Charges
     * @param currency       Currency of the Payment Details to be returned
     * @return PaymentDetails to be returned
     */
    private PaymentDetail getPaymentDetail(Payment paymentProduct, String currency) {

        Set<PaymentDetail> paymentDetails = paymentProduct.getDetails();
        Boolean forThird = false;
        if (currency.equalsIgnoreCase("THIRD")) {
            forThird = true; //for third test if the currency is not USD or PHP
        }

        for (PaymentDetail paymentDetail : paymentDetails) {
            System.out.println("payment currency:" + paymentDetail.getCurrency());
            System.out.println("payment amount:" + paymentDetail.getAmount());


            if (!forThird && paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase(currency)) {
                //if currency needed is not for THIRD amd the currency of paymentDetail is equal to the one needed, return paymentDetail
                return paymentDetail;
            } else if (forThird
                    && !paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")
                    && !paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")
                    ) {
                //If looking for a THIRD currency and currency of paymentDetails is not in PHP or USD
                //This uses a rule in the system that a payment can only be made in the Product Currency,PHP and USD
                //This means that if currency of the paymentDetails is not equal to PHP or USD then is has to be
                // in the third currency
                return paymentDetail;
            }

        }
        return null;
    }

    /**
     * Method used to replace accounting code defined in Ref Accentry with the various types of accounting code settings
     * it may contain such as
     * var_DFFB_code : Due from foreign bank
     * var_ClientsCASA : Casa account payment or settlement
     * AccountingCode : Charge
     *
     *
     *
     * @param tradeService       TradeService object
     * @param usedAccountingCode accounting code to be checked if it matches accounting code that needs to be overriden with actual value
     * @param particulars        particular of the accounting entry that may be changed depending on the matched accounting code
     * @param productRef
     * @param details
     * @return returns Map containing accountingCode and particulars with the both used as key
     */
    private Map<String, String> setAccountingCodeAndParticulars(TradeService tradeService, String usedAccountingCode, String particulars, ProductReference productRef, Map<String, Object> details) {
        System.out.println("setAccountingCodeAndParticulars");
        Map<String, String> k = new HashMap<String, String>();

        System.out.println("usedAccountingCode:" + usedAccountingCode);
        System.out.println(usedAccountingCode.equalsIgnoreCase("var_DFFB_code"));
        System.out.println("depositoryAccountNumber:"+tradeService.getDetails().get("depositoryAccountNumber"));
        System.out.println("reimbursingBankName:"+tradeService.getDetails().get("reimbursingBankName"));
        System.out.println("reimbursingBankAccountNumber:"+tradeService.getDetails().get("reimbursingBankAccountNumber"));
        System.out.println("accountType:"+tradeService.getDetails().get("accountType"));
        System.out.println("reimbursingBankIdentifierCode:"+tradeService.getDetails().get("reimbursingBankIdentifierCode"));
        System.out.println("corresBankCode:"+tradeService.getDetails().get("corresBankCode"));

        if (usedAccountingCode.equalsIgnoreCase("var_ClientsCASA") && tradeService.getDetails().containsKey("CASAAccountNo") && tradeService.getDetails().get("CASAAccountNo") != null) {
            System.out.println("var_ClientsCASA|CASAAccountNo|CASAAccountNo");
            usedAccountingCode = tradeService.getDetails().get("CASAAccountNo").toString();
            k.put("accountingCode", usedAccountingCode);
            k.put("particulars", particulars);
        } else if (usedAccountingCode.equalsIgnoreCase("var_DFFB_code") && tradeService.getDetails().containsKey("depositoryAccountNumber")) {
            System.out.println("var_DFFB_code|depositoryAccountNumber");
            String depositoryAccountNumber = tradeService.getDetails().get("depositoryAccountNumber").toString();
            String accountType = tradeService.getDetails().get("accountType").toString();
            usedAccountingCode = refBankRepository.getGlCode(accountType, depositoryAccountNumber);
            if(tradeService.getDetails().get("reimbursingBankName")!=null){
                particulars = "DUE FROM TO " + tradeService.getDetails().get("reimbursingBankName").toString();
            } else {
                particulars = "DUE FROM TO FOREIGN BANK" ;
            }

            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println(usedAccountingCode);
            System.out.println(particulars);
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            k.put("accountingCode", usedAccountingCode);
            k.put("particulars", particulars);
        } else if (usedAccountingCode.equalsIgnoreCase("var_DFFB_code") && tradeService.getDetails().containsKey("glCode")) {
            System.out.println("var_DFFB_code|glCode");
            usedAccountingCode = tradeService.getDetails().get("glCode").toString();
            if(tradeService.getDetails().get("reimbursingBankName")!=null){
                particulars = "DUE FROM TO " + tradeService.getDetails().get("reimbursingBankName").toString();
            } else {
                particulars = "DUE FROM TO FOREIGN BANK" ;
            }
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println(usedAccountingCode);
            System.out.println(particulars);
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            k.put("accountingCode", usedAccountingCode);
            k.put("particulars", particulars);
        } else if (usedAccountingCode.equalsIgnoreCase("var_DFFB_code") && tradeService.getDetails().containsKey("reimbursingBankAccountNumber")) {
            System.out.println("var_DFFB_code|reimbursingBankAccountNumber");
            String depositoryAccountNumber = tradeService.getDetails().get("reimbursingBankAccountNumber").toString();
            String accountType = tradeService.getDetails().get("accountType").toString();
            usedAccountingCode = refBankRepository.getGlCode(accountType, depositoryAccountNumber);
            if(tradeService.getDetails().get("reimbursingBankName")!=null){
                particulars = "DUE FROM TO " + tradeService.getDetails().get("reimbursingBankName").toString();
            } else {
                particulars = "DUE FROM TO FOREIGN BANK" ;
            }
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println(usedAccountingCode);
            System.out.println(particulars);
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            k.put("accountingCode", usedAccountingCode);
            k.put("particulars", particulars);
        } else if (usedAccountingCode.equalsIgnoreCase("var_DFFB_code") && tradeService.getDetails().containsKey("reimbursingBankMt202")) {
            String depositoryAccountNumber = tradeService.getDetails().get("reimbursingBankMt202").toString();
            usedAccountingCode = refBankRepository.getGlCode("FCDU", depositoryAccountNumber);
            if(tradeService.getDetails().get("reimbursingBankName")!=null){
                particulars = "DUE FROM TO " + tradeService.getDetails().get("reimbursingBankName").toString();
            } else {
                particulars = "DUE FROM TO FOREIGN BANK" ;
            }
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println(usedAccountingCode);
            System.out.println(particulars);
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            k.put("accountingCode", usedAccountingCode);
            k.put("particulars", particulars);
        } else if (usedAccountingCode.contains("AccountingCode")) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            String chargeCodes = usedAccountingCode.substring(0, usedAccountingCode.indexOf("AccountingCode"));
            System.out.println("usedAccountingCode:" + usedAccountingCode);
            System.out.println("chargeCodes:" + chargeCodes);
            String originalAccountingCode = usedAccountingCode;


            DocumentType documentType = null;
            DocumentSubType1 documentSubType1 = null;
            DocumentSubType2 documentSubType2 = null;
            if (!tradeService.getDocumentClass().equals(DocumentClass.MD)) {
                documentType = tradeService.getDocumentType();
                documentSubType1 = tradeService.getDocumentSubType1();
                documentSubType2 = tradeService.getDocumentSubType2();
            }

            if (tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)) {
                documentType = tradeService.getDocumentType();
                documentSubType1 = null;
                documentSubType2 = null;
            }

            //This section was added to accomodate IMPORT_CHARGES which uses the details of the original tradeservice instead
            TradeService originalTradeService = null;
            if(tradeService.getDocumentClass().equals(DocumentClass.IMPORT_CHARGES)){
                String originalTradeServiceId = (String)tradeService.getDetails().get("originalTradeServiceId");
                System.out.println("originalTradeServiceId:"+originalTradeServiceId);
                if(originalTradeServiceId!=null && !originalTradeServiceId.equalsIgnoreCase("")){
                    originalTradeService = tradeServiceRepository.load(new TradeServiceId(originalTradeServiceId));
                    if(originalTradeService !=null){
                        System.out.println("originalTradeServiceId:"+originalTradeService );
                    }
                }
            }

            System.out.println("Product ID:" + productRef.getProductId());



            ChargeId chargeId = getChargeId(chargeCodes);
            ChargeAccountingCode chargeAccountingCode = null;

            if(originalTradeService!=null){
                DocumentType documentTypeOriginal = null;
                DocumentSubType1 documentSubType1Original = null;
                DocumentSubType2 documentSubType2Original = null;
                if (!tradeService.getDocumentClass().equals(DocumentClass.MD)) {
                    documentTypeOriginal = originalTradeService.getDocumentType();
                    documentSubType1Original = originalTradeService.getDocumentSubType1();
                    documentSubType2Original = originalTradeService.getDocumentSubType2();
                }

                if (tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)) {
                    documentTypeOriginal = originalTradeService.getDocumentType();
                    documentSubType1Original = null;
                    documentSubType2Original = null;
                }

                ProductReference originalProductReference = productReferenceRepository.find(originalTradeService.getDocumentClass(), documentTypeOriginal, documentSubType1Original, documentSubType2Original);
                System.out.println("Original Product Ref:"+originalProductReference.getProductId());
                System.out.println("chargeId:"+chargeId);
                System.out.println("originalProductReference.getProductId():"+chargeId);
                System.out.println("originalTradeService.getServiceType():"+originalTradeService.getServiceType());
                chargeAccountingCode = chargeAccountingCodeRepository.getChargeAccountingCode(originalProductReference.getProductId(), originalTradeService.getServiceType(), chargeId);
            } else {
                chargeAccountingCode = chargeAccountingCodeRepository.getChargeAccountingCode(productRef.getProductId(), tradeService.getServiceType(), chargeId);
            }


            if (chargeAccountingCode != null) {
                System.out.println("chargeAccountingCode.getAccountingCode():" + chargeAccountingCode.getAccountingCode());
                usedAccountingCode = chargeAccountingCode.getAccountingCode().trim();
            }


            if(tradeService.getDocumentClass().equals(DocumentClass.IMPORT_CHARGES) && tradeService.getServiceType().equals(ServiceType.PAYMENT_OTHER)){
                System.out.println("IN HERE:: IMPORT CHARGES PAYMENT OTHER"  );
                System.out.println("");
                System.out.println(originalAccountingCode);
                System.out.println(details);
                if(details.containsKey(originalAccountingCode) && details.get(originalAccountingCode)!=null){
                    usedAccountingCode= details.get(originalAccountingCode).toString();
                }
            }
            System.out.println("usedAccountingCode:"+usedAccountingCode);
            System.out.println("particulars:"+particulars);
            k.put("accountingCode", usedAccountingCode);
            k.put("particulars", particulars);
        } else if(usedAccountingCode.equalsIgnoreCase("var_DFFB_code")  && tradeService.getDetails().containsKey("reimbursingBankIdentifierCode")){
            String bic = "";
            String branchCode="";
            String swiftCode = tradeService.getDetails().get("reimbursingBankIdentifierCode").toString();

            if (swiftCode != null && !swiftCode.trim().isEmpty()) {
                if (swiftCode.length() > 8) {
                    bic = swiftCode.substring(0, 8);
                    branchCode = swiftCode.substring(8);
                } else if (swiftCode.length() <= 8) {
                    bic = swiftCode;
                }
            }

            RefBank refBank = refBankRepository.getBank(bic, branchCode);
            if(refBank != null) {

                String accountType = "FCDU";
                if(tradeService.getDetails().containsKey("accountType") && !"".equalsIgnoreCase(tradeService.getDetails().get("accountType").toString())){
                    accountType = tradeService.getDetails().get("accountType").toString();
                }

                usedAccountingCode = refBank.getGlCode(accountType);
                particulars = "DUE FROM TO " + refBank.getInstitutionName();
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                System.out.println(usedAccountingCode);
                System.out.println(particulars);
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            }


            k.put("accountingCode", usedAccountingCode);
            k.put("particulars", particulars);

        } else if(usedAccountingCode.equalsIgnoreCase("var_AP_BOC")){
            k.put("accountingCode", "268610189006");
            k.put("particulars", particulars);
        } else if(usedAccountingCode.equalsIgnoreCase("var_BC_IPF")){
            k.put("accountingCode", "561201020000");
            k.put("particulars", particulars);
        } else if(usedAccountingCode.equalsIgnoreCase("var_PTTC")){
            k.put("accountingCode", "661518010000");
            k.put("particulars", particulars);
        } else if(usedAccountingCode.equalsIgnoreCase("var_CASA_MOB_BOC")){
            k.put("accountingCode", "179230405002");
            k.put("particulars", particulars);
        }
        
        
        for(Map.Entry<String, String> accountingCodesNaMakulit : k.entrySet()) {  	
        	System.out.println("Key K = " + accountingCodesNaMakulit.getKey() + ", Value K= " + accountingCodesNaMakulit.getValue());     	
        }
        
        return k;
    }

    /**
     * Used to convert the string representation of charge id to a chargeId object
     *
     * @param code string representation of charge id
     * @return chargeId
     */
    ChargeId getChargeId(String code) { //Rationalized standardize codes to allow easier conversion.
        return new ChargeId(code);
    }

//    /**
//     * Method originally used to determine if Check or Remittance is enough to cover excess payment
//     *
//     * @param paymentProduct Payment object for Product Charges
//     * @param amount Amount of the payment to be filtered
//     * @param currencyInOrig Currency of the Product being paid
//     * @return Set containing the payment details that is either a Check or a Remittance
//     */
//    private Set<PaymentDetail> filterCheckOrRemittanceEnough(Payment paymentProduct, BigDecimal amount, Currency currencyInOrig) {
//
//        Set<PaymentDetail> paymentDetails = paymentProduct.getDetails();
//        Set<PaymentDetail> checkOrRemittance = new HashSet<PaymentDetail>();
//        for (PaymentDetail paymentDetail : paymentDetails) {
//            System.out.println("payment currency:" + paymentDetail.getCurrency());
//            System.out.println("payment amount:" + paymentDetail.getAmount());
//
//            //if the same currency then can be compared without conversion
//            if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CHECK)
//                    || paymentDetail.equals(PaymentInstrumentType.REMITTANCE)) {
//                checkOrRemittance.add(paymentDetail);
//            }
//        }
//
//
//        return checkOrRemittance;
//    }

    /**
     * This function inserts amount to the valueMap with key valueName concatenated with the different currency conversion using the urr and rates included in details map
     *
     * @param details   TradeService details map
     * @param amount    the original amount to be inserted into valueMap
     * @param currency  the currency of the amount
     * @param valueMap  the map where amount is to be inserted
     * @param urr       rate to determined to be the urr field. computed outside of the system because this is dependent on what the Product Service did the saving.
     * @param valueName the equivalent in the formula of accounting entry setup without currency
     */
    private void insertValueNameToValueMapUrr(Map<String, Object> details, BigDecimal amount, String currency, Map<String, Object> valueMap, BigDecimal urr, String valueName) {
        System.out.println("insertValueNameToValueMapUrr");
        if (valueName == null || valueName.equalsIgnoreCase("")) {
            //Ideally should be throwing exception here
            System.out.println("Missing value name.");
        }

        System.out.println("currency:"+currency);
        if (currency.equalsIgnoreCase("PHP")) {
            valueMap.put(valueName + "PHP", amount);

        } else if (currency.equalsIgnoreCase("USD")) {
            valueMap.put(valueName + "USD", amount);
            valueMap.put(valueName + "PHP", amount.multiply(urr).setScale(2,BigDecimal.ROUND_UP));

        } else {
            System.out.println("angols");
            String conversionStringToUSD = currency + "-USD";
            String conversionStringToUSD00 = currency + "-USD_pass_on_rate_charges";
            String conversionStringToUSD01 = currency + "-USD_pass_on_rate_cash";

            BigDecimal THIRD_PHP_conversion = BigDecimal.ZERO;
            BigDecimal THIRD_USD_conversion = BigDecimal.ZERO;

            System.out.println("details.containsKey(conversionStringToUSD):"+details.containsKey(conversionStringToUSD));
            System.out.println("details.containsKey(conversionStringToUSD00):"+details.containsKey(conversionStringToUSD00));
            System.out.println("details.containsKey(conversionStringToUSD01):"+details.containsKey(conversionStringToUSD01));
            //Check if conversion of third to php exists
            if (details.containsKey(conversionStringToUSD00)) {
                System.out.println("a1");
                THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD00));
                THIRD_PHP_conversion = THIRD_USD_conversion.multiply(urr);
            } else if (details.containsKey(conversionStringToUSD01)) {
                System.out.println("a2");
                THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD01));
                THIRD_PHP_conversion = THIRD_USD_conversion.multiply(urr);
            } else if (details.containsKey(conversionStringToUSD)) {
                System.out.println("a3");
                THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD));
                THIRD_PHP_conversion = THIRD_USD_conversion.multiply(urr);
                System.out.println("THIRD_PHP_conversion:"+THIRD_PHP_conversion);
                System.out.println("THIRD_USD_conversion:"+THIRD_USD_conversion);
            }

            valueMap.put(valueName + "THIRD", amount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
            valueMap.put(valueName + "USD", amount.multiply(THIRD_USD_conversion).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            valueMap.put(valueName + "PHP", amount.multiply(THIRD_PHP_conversion).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        }
    }

    /**
     * This function inserts amount to the valueMap with key valueName concatenated with the different currency conversion using the urr and rates included in details map
     *
     * @param tradeService TradeService object
     * @param amount       the original amount to be inserted into valueMap
     * @param currency     the currency of the amount
     * @param valueMap     the map where amount is to be inserted
     * @param urr          rate to determined to be the urr field. computed outside of the system because this is dependent on what the Product Service did the saving.
     * @param valueName    the equivalent in the formula of accounting entry setup without currency
     */
    private void insertValueNameToValueMapUrr(TradeService tradeService, BigDecimal amount, String currency, Map<String, Object> valueMap, BigDecimal urr, String valueName) {

        Map<String, Object> details = tradeService.getDetails();


        if (currency.equalsIgnoreCase("PHP")) {
            valueMap.put(valueName + "PHP", amount);
//            valueMap.put(valueName + "USD", amount.divide(urr,2,BigDecimal.ROUND_UP));

        } else if (currency.equalsIgnoreCase("USD")) {
            valueMap.put(valueName + "USD", amount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
            valueMap.put(valueName + "PHP", amount.multiply(urr).setScale(2, BigDecimal.ROUND_HALF_EVEN));

        } else {

            BigDecimal THIRD_USD_conversion = BigDecimal.ZERO;
            String conversionStringToUSD = currency + "-USD";
            String conversionStringToUSD_cash = currency + "-USD_special_rate_cash";
            String conversionStringToUSD_charges = currency + "-USD_special_rate_charges";

            if (details.containsKey(conversionStringToUSD_cash)) {
                if (details.get(conversionStringToUSD_cash) != null) {
                    THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD_cash));
                }
            } else if (details.containsKey(conversionStringToUSD_charges)) {
                if (details.get(conversionStringToUSD_charges) != null) {
                    THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD_charges));
                }
            } else if (details.containsKey(currency + "-USD_special_rate_charges_buying")) {
                if (details.get(currency + "-USD_special_rate_charges_buying") != null) {
                    THIRD_USD_conversion = getBigDecimalOrZero(details.get(currency + "-USD_special_rate_charges_buying"));
                }
            } else if (details.containsKey(conversionStringToUSD) && tradeService.getDocumentType().equals(DocumentType.FOREIGN) &&
            		(tradeService.getDocumentClass().equals(DocumentClass.BC) || tradeService.getDocumentClass().equals(DocumentClass.BP)) && 
            		tradeService.getServiceType().equals(ServiceType.SETTLEMENT)) {
            	THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD));
            } else if (tradeService.getSpecialRateThirdToUsd() != null) {
                THIRD_USD_conversion = tradeService.getSpecialRateThirdToUsd();
            } else if (tradeService.getSpecialRateThirdToUsdServiceCharge() != null) {
                THIRD_USD_conversion = tradeService.getSpecialRateThirdToUsdServiceCharge();
            } else if (details.containsKey(conversionStringToUSD)) {
                THIRD_USD_conversion = getBigDecimalOrZero(details.get(conversionStringToUSD));
            }

            System.out.println("THIRD_USD_conversion:" + THIRD_USD_conversion);
            System.out.println("urr:" + urr);

            valueMap.put(valueName + "THIRD", amount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
            valueMap.put(valueName + "USD", amount.multiply(THIRD_USD_conversion).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            valueMap.put(valueName + "PHP", amount.multiply(THIRD_USD_conversion.multiply(urr)).setScale(2, BigDecimal.ROUND_HALF_EVEN));

        }
    }

    /**
     * Method to delete the actual accounting entries of the tradeserviceId
     *
     * @param tradeServiceId the tradeServiceId whose actual entries are to be deleted
     */
    @Transactional
    public void deleteActualEntries(TradeServiceId tradeServiceId) {

        System.out.println("in deleteActualEntries(TradeServiceId tradeServiceId)");
        accountingEntryActualRepository.delete(tradeServiceId);

    }


    public void getGlNotFound(){

        List<String> accList = accountingEntryRepository.getEntriesAllForChecking();
        List<String> removalList = new ArrayList<String>();

        for (String key : accList){
//            System.out.println(key);
            if(key.contains("ode")){
                removalList.add(key);
//                System.out.print("TO BE REMOVED");
            }
        }

        for (String key : removalList){
            accList.remove(key);
        }



        List<String> chrgList =  chargeAccountingCodeRepository.getChargeAccountingCodeList();
        removalList = new ArrayList<String>();

        for (String key : chrgList){
//            System.out.println(key);
            if(key.contains("ode")){
                removalList.add(key);
//                System.out.print("TO BE REMOVED");
            }
        }

        for (String key : removalList){
            accList.remove(key);
        }

        List<String> combinedList = new ArrayList<String>();
        combinedList.addAll(accList);
        combinedList.addAll(chrgList);
        System.out.println("combined list size:"+combinedList.size());

        System.out.println("COMBINED LIST");
        for (String key : combinedList){
            List<Map<String, ?>> glMastRepositoryAllEntries = glMastRepository.getEntriesChecking(key);
            if(glMastRepositoryAllEntries!=null){
                for (Map<String, ?> glmastentry : glMastRepositoryAllEntries) {
                    try {
//                        System.out.println("------------------------------------------------------------------------------------");
//                        System.out.println(glmastentry.get("ACCT_NO")+"|"+"|"+glmastentry.get("SHORT_T")+"|"+"|"+glmastentry.get("BRANCH")+"|"+"|"+glmastentry.get("BOOK_CODE"));
//                        System.out.println("------------------------------------------------------------------------------------");
                    } catch (Exception e) {
//                        System.out.println("------------------------------------------------------------------------------------");
//                        System.out.println(glmastentry.get("ACCT_NO")+"|"+"|"+glmastentry.get("SHORT_T")+"|"+"|"+glmastentry.get("BRANCH")+"|"+"|"+glmastentry.get("BOOK_CODE"));
//                        System.out.println("------------------------------------------------------------------------------------");
                    }
                }
            } else {
                System.out.println(key);
                valueHolderRepository.save(new ValueHolder(key, "NOT FOUND"));
            }

        }

    }

    private List<Map<String, ?>> checkAccountingCodeForCurrency(AccountingEntry accountingEntry, List<Map<String, ?>> t, String currency) {
        try {
            t = glMastRepository.getEntriesByCurrencyBookCodeAccountingCode(currency, accountingEntry.getBookCode().toString(), accountingEntry.getAccountingCode());
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }

        if (t.isEmpty()) {
            int maxlength = 40;
            System.out.println("Not Found in glmast");
            valueHolderRepository.save(new ValueHolder(accountingEntry.getAccountingCode(),  currency+accountingEntry.getBookCode().toString()+accountingEntry.getParticulars().substring(0, maxlength)));
        }
        return t;
    }

    /**
     * Given the method parameters save an entry to AccountingEntryActual Table;
     *
     * @param tradeService       TradeService object
     * @param details            details Map of TradeService
     * @param accountingEntry    AccountingEntry object that is the base where an actual entry will generated from
     * @param productRef         ProductId of the Product
     * @param lcCurrency         Currency of the LC as String
     * @param settlementCurrency Currency of the Settlement as String
     * @param gltsNumber         current gltsnumber from sequence generator
     * @param tradeServiceStatus TradeServiceStatus of the TradeService
     * @return boolean value representing if the ActualEntry was saved or not
     * @throws Exception
     */
    @Transactional
    private boolean generateAccountingEntryActual(TradeService tradeService, Map<String, Object> details, AccountingEntry accountingEntry, ProductReference productRef, String lcCurrency, String settlementCurrency, String gltsNumber, String tradeServiceStatus) throws Exception {
        System.out.println("generating the AccountingEntryActual");
        System.out.println("tradeService:" + tradeService.getTradeServiceId());
        System.out.println("accountingEntry:" + accountingEntry);
        System.out.println("productRef:" + productRef);
        System.out.println("lcCurrency:" + lcCurrency);
        System.out.println("settlementCurrency:" + settlementCurrency);


        Map<String, Object> defaults = accountingLookup.getDefaultValuesForServiceMap();
        //Insert default values from using serviceType
        //Insert default values from serviceType
        System.out.println("==============defaults here====================");
        insertDefaultsToDetailsMap(details, defaults);

        //NOTE Reference number is the Accounting Code for Cash and Check
        String cifNumber = tradeService.getCifNumber();
        String ccbdBranchUnitCode = tradeService.getCcbdBranchUnitCode();
        
        System.out.println("==============insertDefaultsToDetailsMap finised here====================");
        //TODO: Please ensure that a branch unit code is present
        String unitCode = accountingEntry.getUnitCode();
        if (ccbdBranchUnitCode != null && !ccbdBranchUnitCode.isEmpty() && unitCode.equalsIgnoreCase("LendUnitCode")) {
            unitCode = ccbdBranchUnitCode;
        } else if (cifNumber != null && !cifNumber.isEmpty() && unitCode.equalsIgnoreCase("MnBrUnitCode")) {
            System.out.println("cifNumber:" + cifNumber);
            System.out.println("cifNumber.substring(3,6):" + cifNumber.substring(3, 6));
            unitCode = cifNumber.substring(3, 6);
        } else if (unitCode.equalsIgnoreCase("CDTUnitCode")) {

            if(tradeService.getDetails().get("CDTUnitCodeForSendToMobBoc")!=null){
                String CDTUnitCode = tradeService.getDetails().get("CDTUnitCodeForSendToMobBoc").toString();
                unitCode = CDTUnitCode;

            } else {
                String iedieirdNumber = tradeService.getTradeServiceReferenceNumber().toString();
                CDTPaymentRequest cdtPaymentRequest =  cdtPaymentRequestRepository.load(iedieirdNumber);
                if(cdtPaymentRequest!=null){
                    System.out.println("cdtPaymentRequest.getAmount()"+cdtPaymentRequest.getAmount());
                    System.out.println("cdtPaymentRequest.getBankCharge()"+cdtPaymentRequest.getBankCharge());
                    System.out.println("cdtPaymentRequest.getAgentBankCode()"+cdtPaymentRequest.getAgentBankCode());
                    System.out.println("cdtPaymentRequest.getBranchUnitCode()"+cdtPaymentRequest.getBranchUnitCode());
                    
                    unitCode = cdtPaymentRequest.getBranchUnitCode();

//                    RefPas5Client refPas5Client = refPas5ClientRepository.load(cdtPaymentRequest.getAgentBankCode());
//
//                    System.out.println("refPas5Client.getCcbdBranchUnitCode()"+refPas5Client.getCcbdBranchUnitCode());
//                    unitCode = refPas5Client.getCcbdBranchUnitCode();

                    if(unitCode == null || unitCode == "000" || unitCode == "815"){
                        unitCode = "001"; //Head Office Branch
                    }
                } else {
                    unitCode = "001"; //Head Office Branch
                }
            }


        } else if (unitCode.equalsIgnoreCase("CDTProcUnitCode")) {

            if(tradeService.getDetails().get("CDTUnitCodeForSendToMobBoc")!=null){
                String CDTUnitCode = tradeService.getDetails().get("CDTUnitCodeForSendToMobBoc").toString();
                unitCode = CDTUnitCode;

            } else {
                String iedieirdNumber = tradeService.getTradeServiceReferenceNumber().toString();
                CDTPaymentRequest cdtPaymentRequest =  cdtPaymentRequestRepository.load(iedieirdNumber);
                System.out.println("cdtPaymentRequest.getAmount()"+cdtPaymentRequest.getAmount());
                System.out.println("cdtPaymentRequest.getBankCharge()"+cdtPaymentRequest.getBankCharge());
                System.out.println("cdtPaymentRequest.getAgentBankCode()"+cdtPaymentRequest.getAgentBankCode());
                System.out.println("cdtPaymentRequest.getUnitCode()"+cdtPaymentRequest.getUnitCode());
                
                unitCode = cdtPaymentRequest.getUnitCode();
                
                if(unitCode == null){
                    unitCode = "909"; //Head Office Branch
                } else if(unitCode == null || unitCode == "000" || unitCode == "815"){
                	unitCode = "001"; //Head Office Branch
                }
                
//                RefPas5Client refPas5Client = refPas5ClientRepository.load(cdtPaymentRequest.getAgentBankCode());
//                if (refPas5Client != null) {
//                    System.out.println("refPas5Client.getUnitCode()"+refPas5Client.getUnitCode());
//                    unitCode = refPas5Client.getUnitCode();
//                    
//                    if(unitCode == null || unitCode == "000" || unitCode == "815"){
//                        unitCode = "001"; //Head Office Branch
//                    }
//                }
//
//
//                if(unitCode == null ){
//                    unitCode = "909"; //Head Office Branch
//                }

            }


        }

        String respondingUnitCode = accountingEntry.getRespondingUnitCode();
        if (ccbdBranchUnitCode != null && !ccbdBranchUnitCode.isEmpty() && respondingUnitCode.equalsIgnoreCase("LendUnitCode")) {
            respondingUnitCode = ccbdBranchUnitCode;
        } else if (cifNumber != null && !cifNumber.isEmpty() && respondingUnitCode.equalsIgnoreCase("MnBrUnitCode")) {
            System.out.println("cifNumber:" + cifNumber);
            System.out.println("cifNumber.substring(3,6):" + cifNumber.substring(3, 6));
            respondingUnitCode = cifNumber.substring(3, 6);
        } else if (tradeService.getDetails().get("unitcode")!=null && unitCode.equalsIgnoreCase("CDTUnitCode")) {
            System.out.println("unitcode:" + (String)tradeService.getDetails().get("unitcode"));
            unitCode = (String)tradeService.getDetails().get("unitcode");
        } else if (tradeService.getDetails().get("unitcode")==null && unitCode.equalsIgnoreCase("CDTUnitCode")) {
            unitCode = "909";
        }

        String cifName = tradeService.getCifName();
        String particulars = accountingEntry.getParticulars() + "|" + cifName;
        System.out.println("particulars:" + particulars);

        String usedAccountingCode = accountingEntry.getAccountingCode();
//        System.out.println("details for charges:"+details);
        System.out.println("=====docs docs docs=====");
        System.out.println("usedAccountingCode "+usedAccountingCode);
        System.out.println("particulars "+particulars);
        System.out.println("=====docs docs docs=====");
        
        Map<String, String> k = setAccountingCodeAndParticulars(tradeService, usedAccountingCode, particulars, productRef, details);
        
        //System.out.println("=====WAG KA MAKULIT=====");
        for(Map.Entry<String, String> accountingCodesNaMakulit : k.entrySet()) {  	
        	System.out.println("Key = " + accountingCodesNaMakulit.getKey() + ", Value = " + accountingCodesNaMakulit.getValue());     	
        }
        //System.out.println("=====WAG KA MAKULIT====="); 
        if (k.get("accountingCode") != null) {
        	
        	System.out.println("====show me my accounting code========");
            usedAccountingCode = k.get("accountingCode");
            System.out.println("====show me my accounting code========");
        }
        if (k.get("particulars") != null) {
        	System.out.println("====show me exact particulars========");
            particulars = k.get("particulars");
            System.out.println("====show me exact particulars========");
        }


        AccountingEntryActual accountingEntryActual;

        String bookCurrencyActual = accountingEntry.getBookCurrency().toString();
        if (bookCurrencyActual.equalsIgnoreCase("THIRD")) {
            if (!lcCurrency.equalsIgnoreCase("USD") && !lcCurrency.equalsIgnoreCase("PHP")) {
                bookCurrencyActual = lcCurrency;
            }
        }

        System.out.println("bookCurrencyActual:" + bookCurrencyActual);
        System.out.println("usedAccountingCode:" + usedAccountingCode);
        System.out.println("particulars:" + particulars);

        String actype = "";
        String contingentFlag = "";
        String particularsNew = particulars;

        List<Map<String, ?>> t = new ArrayList<Map<String, ?>>();
        try {
            //TODO add unit code to the selection criteria
//            t = glMastRepository.getEntriesByCurrencyBookCodeAccountingCode(bookCurrencyActual, accountingEntry.getBookCode().toString(), usedAccountingCode.trim());
            t = glMastRepository.getEntriesByCurrencyBookCodeAccountingCodeUnitCode(bookCurrencyActual, accountingEntry.getBookCode().toString(), usedAccountingCode.trim(), unitCode);
            //System.out.println(t);
        } catch (Exception e) {
            //e.printStackTrace();
        	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        	System.out.println("it is working... glMastRepository.getEntriesByCurrencyBookCodeAccountingCode  ");
        	System.out.println("it is working... glMastRepository.getEntriesByCurrencyBookCodeAccountingCodeUnitCode  ");
        	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }

        String withError="";
        if (t.isEmpty()) {
            int maxlength = 49;
            if (particulars != null) {
                if (particulars.length() < 49) {
                    maxlength = particulars.length();
                }
            }
            System.out.println("Not Found in glmast");
            valueHolderRepository.save(new ValueHolder(usedAccountingCode, particulars.substring(0, maxlength)));

            withError="NOT_FOUND";
            particularsNew= "NOT FOUND in GLMAST:" +usedAccountingCode+"";
        } else {
            Map<String, ?> tt = t.get(0);
            System.out.println("Found in glmast");

            particularsNew = tt.get("SHORT_T").toString() + "|" + cifName;
//            int maxlength = 49;
//            if (particulars.length() < 49) {
//                maxlength = particularsNew.length();
//            }
            actype = tt.get("AC_TYPE").toString();
            contingentFlag = tt.get("VALPST").toString();
        }

        System.out.println("usedAccountingCode:" + usedAccountingCode);
        if ("179230402009".equalsIgnoreCase(usedAccountingCode.trim())) {
            System.out.println("Trade Suspense");
            particularsNew = "TRADE SUSPENSE" + "|" + cifName;
        }


        String tmpUcpbProductId = getUcpbProductIdString(tradeService, details, productRef);
//        particularsNew = tradeService.getTradeProductNumber().toString() +""+productRef.getShortName()+""+tradeService.getCifName().substring(0,10);
        String tradeProductNumber="";
        
       
        if(tradeService.getTradeProductNumber()!=null){
            tradeProductNumber=tradeService.getTradeProductNumber().toString();
        }
        if(productRef.getProductId().toString().equalsIgnoreCase("REBATE")){
            tradeProductNumber=tradeService.getTradeServiceId().toString();
        }
        if(productRef.getDocumentClass().toString().equalsIgnoreCase("INDEMNITY")) {
        	tradeProductNumber=tradeService.getDocumentNumber().toString();
        }
        
        try {
        	

            accountingEntryActual = new AccountingEntryActual(
                    unitCode,
                    respondingUnitCode,
                    accountingEntry.getBookCode().toString(),
                    bookCurrencyActual,
                    bookCurrencyActual,
                    accountingEntry.getEntryType().toString(),
                    usedAccountingCode,
                    particularsNew.toUpperCase(), //particulars
                    accountingEntry.computePesoValue(details),
                    accountingEntry.computeValue(details),
                    tradeService.getTradeServiceId(),
                    productRef.getProductId(),
                    tradeService.getServiceType(),
                    accountingEntry.getAccountingEventTransactionId(),
                    new Date(),
                    gltsNumber,
                    actype,
                    contingentFlag,
                    tmpUcpbProductId,
                    tradeServiceStatus,
                    withError,
                    tradeProductNumber
            );
        } catch (Exception e) {
            e.printStackTrace();
//            StackTraceElement[] trace = e.getStackTrace();
//            errorExceptionMessage = "Exception in Accounting Entry"+
//   				 "<br/><br/> "+
//   				 "<P align=\"left\">"+
//   				 "Exception: "+e.toString()+
//   				 "<P align=\"right\">"+
//   				 trace[0].toString()+
//   				 "<br/></P>"+
//   				 "TradeServiceID: "+tradeService.getTradeServiceId().toString()+
//   				 "<br/></P>";
//   				//  "<br/> "+
//   				 // trace[0].toString();
//            //errorAlert();
            throw new RuntimeException("Exception in generateAccountingEntryActual",e);
        }
        System.out.println("Accounting Entry generated will test");
//        System.out.println("accountingEntryActual:" + accountingEntryActual);     
        System.out.print(accountingEntryActual.getAccountingCode()+ "  ");
        System.out.print(accountingEntryActual.getParticulars()+ "  ");
        System.out.print(accountingEntryActual.getOriginalAmount()+ "  ");
        System.out.println(accountingEntryActual.getPesoAmount());
        if (accountingEntryActual != null && accountingEntryActual.getOriginalAmount().doubleValue() > 0 && accountingEntryActual.getPesoAmount().doubleValue() > 0) {
            System.out.println("Accounting Entry passed");
            accountingEntryActualRepository.save(accountingEntryActual);
            return true;
        } else {
            System.out.println("accountingEntryActual failed:" + accountingEntryActual);
            System.out.println("original amount:" + accountingEntryActual.getOriginalAmount());
            System.out.println("peso amount:" + accountingEntryActual.getPesoAmount());
        }
        return false;
    }

    private String getUcpbProductIdString(TradeService tradeService, Map<String, Object> details, ProductReference productRef) {
        String tmpUcpbProductId="-";
        try {
            tmpUcpbProductId = productReferenceRepository.find(productRef.getProductId()).getUcpbProductId();
            
            System.out.println("UCPB PRODUCT ID === "+productRef.getProductId());
            System.out.println("DOCUMENT CLASS === "+ productRef.getDocumentClass());
            
            //I know this is ugly but the model was flawed and i have to make do
            //TODO: add for standby financial and standby
            if (productRef.getProductId().toString().equalsIgnoreCase("FX-INDEMNITY") || productRef.getProductId().toString().equalsIgnoreCase("FX-INDEMNITY-STANDBY-SIGHT") || productRef.getProductId().toString().equalsIgnoreCase("INDEMNITY")) {
                if (details.get("indemnityType") != null && "BG".equalsIgnoreCase((String) details.get("indemnityType"))) {
                    tmpUcpbProductId = "TF114";
                } else {
                    tmpUcpbProductId = "TF119";
                }
            } else if(productRef.getDocumentClass().equals(DocumentClass.LC) &&
                      DocumentSubType1.STANDBY.equals(productRef.getDocumentSubType1())
                    ){
                System.out.println("standbyTagging override");
                if(DocumentType.FOREIGN.equals(productRef.getDocumentType())){
                    if (tradeService.getDetails().containsKey("standbyTagging")) {
                        if (tradeService.getDetails().get("standbyTagging").toString().equalsIgnoreCase("P") || tradeService.getDetails().get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                            System.out.println("FX-PERFORMANCE");
                            tmpUcpbProductId = "TF116";
                        } else {
                            System.out.println("FX-FINANCIAL");
                            tmpUcpbProductId = "TF115";
                        }
                    }
                } else if(DocumentType.DOMESTIC.equals(productRef.getDocumentType())) {
                    if (tradeService.getDetails().containsKey("standbyTagging")) {
                        if (tradeService.getDetails().get("standbyTagging").toString().equalsIgnoreCase("P") || tradeService.getDetails().get("standbyTagging").toString().equalsIgnoreCase("PERFORMANCE")) {
                            System.out.println("DM-PERFORMANCE");
                            tmpUcpbProductId = "TF214";
                        } else {
                            System.out.println("DM-FINANCIAL");
                            tmpUcpbProductId = "TF213";
                        }
                    }
                }
                
            }else if(productRef.getProductId().toString().equalsIgnoreCase("IMPORT_CHARGES")) {
            	
            	tmpUcpbProductId = actualImportChargesProductID;
            	System.out.println(tmpUcpbProductId);
            }

        } catch (Exception e) {
            System.out.println();
        }
        
        return tmpUcpbProductId;
    }

    /** This will determine the particular product ID for Import Charge base 
     *  on its given transaction Type
     * 
     * 
     * @param productId		Product ID of the selected Transaction Type
     * @return productId
     */
    private String getImportChargesProductId(String productId) {
    	
    	String ucpbProductId = productReferenceRepository.getUCPBProdID(productId);
    	
    	return ucpbProductId;
    }
    
    /**This will set the particular product ID for Import Charge base 
     * on its given transaction Type
     * 
     * @param ucpbProductId
     * @return ucpbProductId
     */
    private void setImportChargesProductId(String ucpbProductId) {
    	
    	this.actualImportChargesProductID = ucpbProductId;
    }
    
    


    /**
     * Used to generate auto cancellation accounting entries for expired lc batch process
     *
     * @param details       map with values necessary for generation of accounting entries
     * @param gltsNumber         the current gltsNumber based on query in sequence generator
     * @param documentClass
     * @param documentType
     * @param documentSubType1
     * @param documentSubType2
     * @param serviceType
     */
    @Transactional
    public void generateActualEntriesForTradeProductCancellation(Map<String, Object> details, String gltsNumber, DocumentClass documentClass, DocumentType documentType, DocumentSubType1 documentSubType1, DocumentSubType2 documentSubType2, ServiceType serviceType) {
        System.out.println("generateActualEntriesForTradeProductCancellation");
        String tradeServiceStatus = "EXPIRED";

        String documentNumber = "";

       BigDecimal amount;
       BigDecimal totalNegotiatedCashAmount = (BigDecimal)details.get("totalNegotiatedCashAmount");
       BigDecimal cashAmount = (BigDecimal)details.get("cashAmount");
       BigDecimal outstandingBalance = (BigDecimal)details.get("outstandingLCAmount");
        
      String cashFlag = details.get("cashFlag").toString();
	  if(cashFlag.equalsIgnoreCase("1")){    
        	try{
        	cashAmount = cashAmount.subtract(totalNegotiatedCashAmount); 
        	amount = outstandingBalance.subtract(cashAmount);
        	if(amount.compareTo(BigDecimal.ZERO) < 1){
        		amount = BigDecimal.ZERO;        			
        	}
        	if(cashAmount.compareTo(BigDecimal.ZERO) < 1){
        		cashAmount = BigDecimal.ZERO;	
        	}  
        	}catch(Exception e){
        		System.out.println("cashAmount: " + cashAmount);
        		System.out.println("totalNegotiatedCashAmount: " + totalNegotiatedCashAmount);
        		System.out.println("outstandingBalance: " + outstandingBalance);
        		amount = cashAmount;
        	}
        } else{
        	amount = outstandingBalance;
        }                

	   
        String currency = (String)details.get("lcCurrency");
        String documentNumberStr = (String)details.get("documentNumberStr");
		String effectiveDate =  (String)details.get("effectiveDate");
		System.out.println("EFFECTIVE DATE: "+effectiveDate);
        System.out.println("amount:"+amount);
        System.out.println("currency:"+currency);

        try {
            // find the UCPB product reference for this combination
            ProductReference productRef = productReferenceRepository.find(documentClass, documentType, documentSubType1, documentSubType2);
            String ucpbProductId = productRef.getUcpbProductId();

            if (productRef != null) {
                System.out.println("Product ID:" + productRef.getProductId());
                System.out.println("product found:" + productRef.getProductId());

                try {
                    AccountingEntryActual accountingEntryActualCredit;
                    AccountingEntryActual accountingEntryActualDebit;


                    //TODO Convert these to correct peso amounts
                    BigDecimal creditPeso = amount; //get from details map
                    System.out.println("creditPeso:" + creditPeso);
                    BigDecimal debitPeso = amount;  //get from details map
                    System.out.println("debitPeso:" + debitPeso);
                    if(currency.equalsIgnoreCase("PHP")){
                        //DO NOTHING Peso Amount SAME AS Original Amount
                    } else if(currency.equalsIgnoreCase("USD")) {
                        BigDecimal urr = (BigDecimal) details.get("urr");
                        creditPeso = amount.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
                        debitPeso = amount.multiply(urr).setScale(2,BigDecimal.ROUND_UP);
                    } else {
                        BigDecimal urr = (BigDecimal) details.get("urr");
                        BigDecimal thirdToUSD = (BigDecimal) details.get("thirdToUSD");
                        creditPeso = amount.multiply(thirdToUSD.multiply(urr)).setScale(2,BigDecimal.ROUND_UP);
                        debitPeso = amount.multiply(thirdToUSD.multiply(urr)).setScale(2,BigDecimal.ROUND_UP);
                    }



                    BigDecimal creditOriginal = amount;
                    System.out.println("creditOriginal:" + creditOriginal);
                    BigDecimal debitOriginal = amount;
                    System.out.println("debitOriginal:" + debitOriginal);

                    String debitAccountingCode = productRef.getContraContingentAccountingCode();
                    String creditAccountingCode = productRef.getContingentAccountingCode();

                    if(details.get("withDiscrepancy").toString().equalsIgnoreCase("Y")){
                        debitAccountingCode = productRef.getDiscrepancyContraContingentAccountingCode();
                        creditAccountingCode = productRef.getDiscrepancyContingentAccountingCode();
                    }
                    
                    //handling Standby Tagging
                    if(productRef.getProductId().toString().equalsIgnoreCase("FX-LC-STANDBY")) {
                    	String tagging = (String)details.get("standbyTagging");
                    	if (tagging.equalsIgnoreCase("FINANCIAL")) {
                    		debitAccountingCode = "833110102000";
                            creditAccountingCode = "823110102000";
                    	}else {
                    		debitAccountingCode = "834110102000";
                            creditAccountingCode = "824110102000";
                    	}
                    }
                    
                    if(productRef.getProductId().toString().equalsIgnoreCase("DM-LC-STANDBY")) {
                    	String tagging = (String)details.get("standbyTagging");
                    	if (tagging.equalsIgnoreCase("FINANCIAL")) {
                    		debitAccountingCode = "833110101000";
                            creditAccountingCode = "823110101000";
                    	}else {
                    		debitAccountingCode = "834110101000";
                            creditAccountingCode = "824110101000";
                    	}
                    }
                    
                    
                    System.out.println("debitAccountingCode:"+debitAccountingCode);
                    System.out.println("creditAccountingCode:"+creditAccountingCode);

                    String bookCode="RG";
                    String bookCurrency=currency;
                    String debitActype="";
                    String debitContingentFlag="";
                    String debitParticulars="";

                    List<Map<String, ?>> t = new ArrayList<Map<String, ?>>();
                    try {
                        //TODO add unit code to the selection criteria
//                        t = glMastRepository.getEntriesByCurrencyBookCodeAccountingCode(currency, BookCode.RG.toString(), debitAccountingCode.trim());
                        t = glMastRepository.getEntriesByCurrencyBookCodeAccountingCodeUnitCode(currency, BookCode.RG.toString(), debitAccountingCode.trim(), "909");
                        //System.out.println(t);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }

                    if (t.isEmpty()) {
                        int maxlength = 49;
                        if (debitParticulars != null) {
                            if (debitParticulars.length() < 49) {
                                maxlength = debitParticulars.length();
                            }
                        }
                        System.out.println("Not Found in glmast");
                        valueHolderRepository.save(new ValueHolder(debitAccountingCode, debitParticulars.substring(0, maxlength)));

                        debitParticulars= "NOT FOUND in GLMAST:" +debitAccountingCode+"";
                    } else {
                        Map<String, ?> tt = t.get(0);
                        System.out.println("Found in glmast");

                        debitParticulars= tt.get("SHORT_T").toString();

                        debitActype = tt.get("AC_TYPE").toString();
                        debitContingentFlag = tt.get("VALPST").toString();
                    }
                    

//                    /*Set the affective date plus one since Accounting Entries for Expired LCs are posted Next Banking Day.
//                     * 
//                     * 
//                     */
//                    Date effectiveDate = new Date();
//                    Calendar dateToPost = Calendar.getInstance();
//                    dateToPost.setTime(effectiveDate);
//                    dateToPost.add(Calendar.DATE, 1);
//                    effectiveDate = dateToPost.getTime();
					
					SimpleDateFormat defaultFormat = new SimpleDateFormat("MM-dd-yyyy");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    
                    String reformattedStr = sdf.format(defaultFormat.parse(effectiveDate));
                    System.out.println(reformattedStr);
                    
                    Date dateEffect = sdf.parse(reformattedStr);
                    
                     Calendar dateToPost = Calendar.getInstance();
                     dateToPost.setTime(dateEffect);
                     dateToPost.add(Calendar.DATE, 1);
                     dateEffect = dateToPost.getTime();

                     System.out.println("SIMPLE DATE FORMAT: " + sdf.format(dateEffect));

                        accountingEntryActualDebit = new AccountingEntryActual(
                                "909",
                                "909",
                                bookCode,
                                bookCurrency,//bookCurrency
                                bookCurrency,//originalCurrency
                                "Debit",//accountingEntry.getEntryType().toString(),
                                debitAccountingCode,
                                debitParticulars, //particulars
                                debitPeso, //accountingEntry.computePesoValue(details),
                                debitOriginal,
                                new TradeServiceId(documentNumber),
                                productRef.getProductId(),
                                serviceType,
                                new AccountingEventTransactionId("CANCELLATION-EXPIRED-LC"),
                                dateEffect,
                                gltsNumber,
                                debitActype,
                                debitContingentFlag,
                                ucpbProductId, //UCPB PRODUCT ID
                                tradeServiceStatus,
                                "",
                                documentNumberStr
                        );

                    String creditActype="";
                    String creditContingentFlag="";
                    String creditParticulars="";

                    try {
                        //TODO add unit code to the selection criteria
//                      t = glMastRepository.getEntriesByCurrencyBookCodeAccountingCode(currency, BookCode.RG.toString(), debitAccountingCode.trim());
                      t = glMastRepository.getEntriesByCurrencyBookCodeAccountingCodeUnitCode(currency, BookCode.RG.toString(), debitAccountingCode.trim(), "909");
                        //System.out.println(t);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }

                    if (t.isEmpty()) {
                        int maxlength = 49;
                        if (creditParticulars != null) {
                            if (creditParticulars.length() < 49) {
                                maxlength = creditParticulars.length();
                            }
                        }
                        System.out.println("Not Found in glmast");
                        valueHolderRepository.save(new ValueHolder(creditAccountingCode, creditParticulars.substring(0, maxlength)));

                        creditParticulars= "NOT FOUND in GLMAST:" +creditAccountingCode+"";
                    } else {
                        Map<String, ?> tt = t.get(0);
                        System.out.println("Found in glmast");

                        creditParticulars= tt.get("SHORT_T").toString();

                        creditActype = tt.get("AC_TYPE").toString();
                        creditContingentFlag = tt.get("VALPST").toString();
                    }
                    
                    
                    accountingEntryActualCredit = new AccountingEntryActual(
                            "909",
                            "909",
                            bookCode, //bookCode
                            bookCurrency,//bookCurrency
                            bookCurrency,//originalCurrency
                            "Credit",//accountingEntry.getEntryType().toString(),
                            creditAccountingCode,
                            creditParticulars, //particulars
                            creditPeso, //accountingEntry.computePesoValue(details),
                            creditOriginal,
                            new TradeServiceId(documentNumber),
                            productRef.getProductId(),
                            serviceType,
                            new AccountingEventTransactionId("CANCELLATION-EXPIRED-LC"),
                            dateEffect,
                            gltsNumber,
                            creditActype,
                            creditContingentFlag,
                            ucpbProductId, //UCPB PRODUCT ID
                            tradeServiceStatus,
                            "",
                            documentNumberStr
                    );
                        accountingEntryActualRepository.save(accountingEntryActualCredit);
                        accountingEntryActualRepository.save(accountingEntryActualDebit);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to generate accounting entries for send to MOB BOC account.
     * @param tradeService tradeService for this transaction
     * @param gltsNumber gltsNumber used by SIBS accounting
     * @param tradeServiceStatus status of the tradeService
     * @param bocAmount the amount to be sent to MOB BOC account
     */
    @Transactional
    public void generateActualEntriesSendMobToBOC(TradeService tradeService, String gltsNumber, String tradeServiceStatus, BigDecimal bocAmount) {
        System.out.println("generateActualEntriesSendMobToBOC");

        //TODO Support BOOK CODE
        DocumentClass documentClass = DocumentClass.CDT;
        DocumentType documentType = null;
        DocumentSubType1 documentSubType1 = null;
        DocumentSubType2 documentSubType2 = null;
        String lcCurrency = "PHP";
        BookCurrency bcLcCurrency = determineBookCurrency(lcCurrency);

        try {
            // find the UCPB product reference for this combination
            ProductReference productRef = productReferenceRepository.find(documentClass, documentType, documentSubType1, documentSubType2);
            if (productRef != null) {
                System.out.println("Product ID:" + productRef.getProductId());
                System.out.println("product found:" + productRef.getProductId());


                Map<String, Object> specificChargeAndPaymentMap = new HashMap<String, Object>();
                specificChargeAndPaymentMap.put("APBOCamountTotalPHP",bocAmount);
                System.out.println("specificChargeAndPaymentMap :" + specificChargeAndPaymentMap);

                String accEventTransactionId = "CDT-COLLECTION-TRANSFER-OF-TOTAL-CDT-COLLECTIONS-TO-MOB-BOC-ACCOUNT";
                genAccountingEntryPayment(tradeService, specificChargeAndPaymentMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId(accEventTransactionId), gltsNumber, tradeServiceStatus);
                genAccountingEntrySettlement(tradeService, specificChargeAndPaymentMap, productRef, lcCurrency, bcLcCurrency, lcCurrency, bcLcCurrency, new AccountingEventTransactionId(accEventTransactionId), gltsNumber, tradeServiceStatus);
            
                //Update isPosted column to 0
                isPosted(tradeService.getTradeServiceId().toString().trim(), new Boolean(false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**Returns a Map that will use to determine charges accounting entries come from PAYMENT-CHARGES-EXPORT
     * 
     * 
     * @param chargeMap		map to be return
     * @param chargeId		charges id present in the transaction
     * @param chargeAmount	amount of charges present in the transaction
     * @return
     */
    private static Map<String, Object> generateExportPaymentChargesMap(Map<String, Object> chargeMap, String chargeCurrency){
    	
    	String cur = "";
    	
    	if(chargeCurrency.equalsIgnoreCase("PHP")) {
    		cur = "PHP";
    	}else if (chargeCurrency.equalsIgnoreCase("USD")) {
    		cur = "USD";
    	}else {
    		cur="THIRD";
    	}
    	
    	
    	Map<String, Object> paymentChargeMap = new HashMap<String, Object>();
	
    	for(Map.Entry<String, Object> listChargesMap : chargeMap.entrySet()) {
	
    		if(listChargesMap.getKey().toString().equalsIgnoreCase("BC")) {
    			paymentChargeMap.put("bankCommissionGross" + cur, listChargesMap.getValue());
    			
    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CILEX")) {
    			paymentChargeMap.put("cilexFeeGross" + cur, listChargesMap.getValue());
    			
    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
    			paymentChargeMap.put("docStampsFee" + cur, listChargesMap.getValue());
    			
    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("ADVISING-EXPORT")) {
    			paymentChargeMap.put("advisingExportFee" + cur, listChargesMap.getValue());
    			
    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("BOOKING")) {
    			paymentChargeMap.put("bookingCommissionFeeGross" + cur, listChargesMap.getValue());
    			
    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("BSP")) {
    			paymentChargeMap.put("bspCommissionGross" + cur, listChargesMap.getValue());
    			
    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CABLE")) {
    			paymentChargeMap.put("cableFee" + cur, listChargesMap.getValue());
    			
    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CANCEL")) {
    			paymentChargeMap.put("cancellationFee" + cur, listChargesMap.getValue());
    			
    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CF")) {
    			paymentChargeMap.put("commitmentFeeGross" + cur, listChargesMap.getValue());
    			
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CORRES-ADDITIONAL")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
//    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
//    			paymentChargeMap.put("docStampsFeePHP", listChargesMap.getValue());
    		}
    		
    	}
    	
    	System.out.println("List of Charges");
    	for(Map.Entry<String, Object> paymentlistChargesMap : paymentChargeMap.entrySet()) {
    		
    		System.out.println("Key: "+ paymentlistChargesMap.getKey() + " value: "+ paymentlistChargesMap.getValue());
    	}
    	
    	
    	return paymentChargeMap;
    }
    
    /**returns charges amount together with corresponding formula values
     * 
     * @param chargeMap			Map Contains charges
     * @param chargeCurrency	Charges Currency
     * @return paymentChargeMap map that contains accounting code fbase from the given charges
     */
	private static Map<String, Object> getAllChargesMap(Map<String, Object> chargeMap, String chargeCurrency){
	    	
	    	String cur = "";
	    	
	    	if(chargeCurrency.equalsIgnoreCase("PHP")) {
	    		cur = "PHP";
	    	}else if (chargeCurrency.equalsIgnoreCase("USD")) {
	    		cur = "USD";
	    	}else {
	    		cur="THIRD";
	    	}
	    	
	    	
	    	Map<String, Object> paymentChargeMap = new HashMap<String, Object>();
		
	    	for(Map.Entry<String, Object> listChargesMap : chargeMap.entrySet()) {
		
	    		if(listChargesMap.getKey().toString().equalsIgnoreCase("BC")) {
	    			paymentChargeMap.put("bankCommissionGross" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("BOOKING")) {
	    			paymentChargeMap.put("bookingCommissionFeeGross" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("BSP")) {
	    			paymentChargeMap.put("bspCommissionGross" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CABLE")) {
	    			paymentChargeMap.put("cableFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CANCEL")) {
	    			paymentChargeMap.put("cancellationFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CF")) {
	    			paymentChargeMap.put("commitmentFeeGross" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CILEX")) {
	    			paymentChargeMap.put("cilexFeeGross" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
	    			paymentChargeMap.put("docStampsFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("ADVISING-EXPORT")) {
	    			paymentChargeMap.put("advisingExportFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CORRES-ADVISING")) {
	    			paymentChargeMap.put("advisingFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("CORRES-CONFIRMING")) {
	    			paymentChargeMap.put("confirmingFee" + cur, listChargesMap.getValue());
	    					
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("NOTARIAL")) {
	    			paymentChargeMap.put("notarialFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("DOCSTAMPS")) {
	    			paymentChargeMap.put("docStampsFeePHP" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("OTHER-EXPORT")) {
	    			paymentChargeMap.put("otherExportFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("POSTAGE")) {
	    			paymentChargeMap.put("postageFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("REMITTANCE")) {
	    			paymentChargeMap.put("remittanceFee" + cur, listChargesMap.getValue());
	    			
	    		}else if(listChargesMap.getKey().toString().equalsIgnoreCase("SUP")) {
	    			paymentChargeMap.put("suppliesFee" + cur, listChargesMap.getValue());
	    		}
	    		
	    	}
	    	
	    	System.out.println("List of Charges");
	    	for(Map.Entry<String, Object> paymentlistChargesMap : paymentChargeMap.entrySet()) {	
	    		System.out.println("Key: "+ paymentlistChargesMap.getKey() + " value: "+ paymentlistChargesMap.getValue());
	    	}
	    	
	    	
	    	return paymentChargeMap;
	    }
    
    /**This validates accounting enting entries against unit code, bookcode, accountingcode and currency in sibs glmast
     * 
     * 
     * @param unitCode  branch unit code
     * @param bookCode	booking code
     * @param glCode	accounting code
     * @param currency	settlement currency
     * @return true/false  true if found in glmast
     */
    public boolean validateAccountingEntries(String unitCode, String bookCode, String glCode, String currency) {
    	
  
    	
    	List<Map<String, ?>> t;
    	
    	t=  glMastRepository.getEntriesByCurrencyBookCodeLbpAccountingCodeUnitCode(currency, bookCode, glCode, unitCode);
        if(t == null || t.toString().equalsIgnoreCase("[]")) {
        	t=  glMastRepository.getEntriesByCurrencyBookCodeAccountingCodeUnitCode(currency, bookCode, glCode, unitCode);
        }
    	
    	boolean isFound = true;
    	
    	if(t == null) {
    		
    		isFound = false;
    		System.out.println("Not found in GLmast: null value returned");
    		
    	}else if (t.toString().equalsIgnoreCase("[]")) {
    		
    		isFound = false;
    		System.out.println("Not found in GLmast please check the following entry:");
    		
    	}else {
    		isFound = true;
    		//System.out.println("Not found in GLmast: t is with []" + isFound);
    	}
    	
    	System.out.println("Unit Code: "+ unitCode);
    	System.out.println("Book Code: " + bookCode);
    	System.out.println("Currency: " + currency);
    	System.out.println("GL Code: " + glCode);
    	
    	return isFound;
    	
    }
    
//    public String //errorAlert() {
//    	System.out.println(" ");
//    	System.out.println("Yehey Error! :P");
//    	String returnMessage = errorExceptionMessage;
//    	errorExceptionMessage = "NONE";	
//    	return returnMessage;  	
//    }
    
    public void isPosted(String tradeServiceId, Boolean isPosted) {

         accountingEntryActualRepository.updateIsPosted(tradeServiceId, isPosted);
    }
    
    
    public void updateIsPosted(Boolean isPostedValue) {
    	
    	accountingEntryActualRepository.updateIsPostedTrue(isPostedValue);
    }

       /**This will tag Specific Accounting Entries as "NOT_BALANCE"
     * if total Debit and Credit is not Balance
     * 
     * @param tradeServiceId
     */
    public void tagWithError(TradeServiceId tradeServiceId) { 	
    	
    	Boolean isBalance =  specificBalanceChecking(tradeServiceId);    	
    	
    	if(!isBalance) {
    		accountingEntryActualRepository.updateWithError(tradeServiceId.toString());	
    	}
    	
    }

    /**This will check the balance of Accounting Entries during GL Movement Batch run.
     * 
     * @return result
     */
    public Boolean balanceChecking() {
    	
    	System.out.println("Checking Balances...");
    	
    	Boolean result = true;
    	
    	Date dateToday = new Date();
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	String postingDate = dateFormat.format(dateToday);
    	
    	System.out.println("Date: "+postingDate);
    	
//    	BigDecimal totalAllOrigDebit = accountingEntryActualRepository.getAllTotalOrigDebit(postingDate);
//    	BigDecimal totalAllOrigCredit = accountingEntryActualRepository.getAllTotalOrigCredit(postingDate);
//    	BigDecimal totalAllPesoDebit = accountingEntryActualRepository.getAllTotalPesoDebit(postingDate);
//    	BigDecimal totalAllPesoCredit = accountingEntryActualRepository.getAllTotalPesoCredit(postingDate);
    	
    	BigDecimal totalAllOrigDebit= accountingEntryActualRepository.getAllTotalOrigDebit(postingDate) != null ?
    			//&&
    			//accountingEntryActualRepository.getAllTotalOrigDebit(postingDate).toString().isEmpty() ?
    			accountingEntryActualRepository.getAllTotalOrigDebit(postingDate) : BigDecimal.ZERO;
    	
    	BigDecimal totalAllOrigCredit= accountingEntryActualRepository.getAllTotalOrigCredit(postingDate) != null ?
    			//&&
    	    	//accountingEntryActualRepository.getAllTotalOrigCredit(postingDate).toString().isEmpty() ?
    	    	accountingEntryActualRepository.getAllTotalOrigCredit(postingDate) : BigDecimal.ZERO;
    	    			 
    	BigDecimal totalAllPesoDebit= accountingEntryActualRepository.getAllTotalPesoDebit(postingDate) != null ?
    			//&&
    	    	//accountingEntryActualRepository.getAllTotalPesoDebit(postingDate).toString().isEmpty() 
    	    	accountingEntryActualRepository.getAllTotalPesoDebit(postingDate) : BigDecimal.ZERO;
    	    	    			 
    	BigDecimal totalAllPesoCredit= accountingEntryActualRepository.getAllTotalPesoCredit(postingDate) != null ?
    			//&&
    	    	//accountingEntryActualRepository.getAllTotalPesoCredit(postingDate).toString().isEmpty() ?
    	    	accountingEntryActualRepository.getAllTotalPesoCredit(postingDate) : BigDecimal.ZERO;
    	
    	
    	System.out.println("totalAllOrigDebit: " + totalAllOrigDebit);
    	System.out.println("totalAllOrigCredit: " + totalAllOrigCredit);
    	System.out.println("totalAllPesoDebit: " + totalAllPesoDebit);
    	System.out.println("totalAllPesoCredit: " + totalAllPesoCredit);
    	   	
    	
    	if(totalAllOrigDebit.equals(totalAllOrigCredit) && totalAllPesoDebit.equals(totalAllPesoCredit)) {
    		
    		result = true;
    	}else {
    		
    		result = false;
    	}
    	
		return result;
    }
    
    
    /**This will check the Accounting Entries Balance per Transaction.
     * 
     * @param tradeServiceId
     * @return result
     */
    public Boolean specificBalanceChecking(TradeServiceId tradeServiceId) {
    	
    	Boolean result = true;
    	
    	System.out.println("Checking Balances...");
    	
    	BigDecimal totalOrigDebit = accountingEntryActualRepository.getTotalOriginalDebit(tradeServiceId);
    	BigDecimal totalOrigCredit = accountingEntryActualRepository.getTotalOriginalCredit(tradeServiceId);
    	BigDecimal totalPesoDebit = accountingEntryActualRepository.getTotalPesoDebit(tradeServiceId);
    	BigDecimal totalPesoCredit = accountingEntryActualRepository.getTotalPesoCredit(tradeServiceId);
    	
    	
    	System.out.println("totalOrigDebit: " + totalOrigDebit);
    	System.out.println("totalOrigCredit: " + totalOrigCredit);
    	System.out.println("totalPesoDebit: " + totalPesoDebit);
    	System.out.println("totalPesoCredit: " + totalPesoCredit);
    	
    	
    	if(totalOrigDebit.equals(totalOrigCredit) && totalPesoDebit.equals(totalPesoCredit)) { 		
    		result = true;
    	}else {  		
    		result = false;
    	}
    	
    	return result;
    }

}