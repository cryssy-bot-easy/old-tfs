package com.ucpb.tfs.application.commandHandler;

/**
 *
 * @author Marvin Volante <marvin.volante@incuventure.net>
 *
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveLcPaymentFormCommand;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.casa.CasaAccount;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaidEvent;
import com.ucpb.tfs.domain.payment.event.PaymentSavedEvent;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayable;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayableRepository;
import com.ucpb.tfs.utils.MapUtil;
import com.ucpb.tfs2.application.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
	(revision)
	SCR/ER Number: ER# 20151202-007
	SCR/ER Description: No Client name in generated Credit Memo
	[Revised by:] Jesse James Joson
	[Date revised:] 12/03/2015
	Program [Revision] Details: Upon saving of Settlement Tab, it will always check the Client name for CASA settlement.
	PROJECT: CORE
	MEMBER TYPE  : JAVA

*/

/**
	(revision)
	SCR/ER Number: ER# 20151204-016
	SCR/ER Description: DM LC Error - when payment tab was saved, screen back to intial state where all fields are blank.
	[Revised by:] Jesse James Joson
	[Date revised:] 12/11/2015
	Program [Revision] Details: Upon saving of Settlement Tab, it will always check the Client name for CASA settlement if not CASA just proceed to normal saving.
	PROJECT: CORE
	MEMBER TYPE  : JAVA

*/

/**
 * (revision)
 *	SCR/ER Number:
 *	SCR/ER Description: Missing Save Button in EBP Nego and EBC Settlement Data Entry (Redmine# 4213)
 *	[Revised by:] Brian Harold A. Aquino
 *	[Date revised:] 05/23/2017 (tfs Rev# 7323)
 *	[Date deployed:] 06/16/2017
 *	Program [Revision] Details: Validation for saving in EBP Nego Data Entry
 *	Member Type: Java
 *	Project: WEB
 *	Project Name: SaveLcPaymentFormCommandHandler.java
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveLcPaymentFormCommandHandler implements CommandHandler<SaveLcPaymentFormCommand> {

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    private PaymentRepository paymentRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    @Inject
    AccountsPayableRepository accountsPayableRepository;

    CasaAccount casaAccount;
	    
	@Autowired
	private PaymentService paymentService;

    @Override
    public void handle(SaveLcPaymentFormCommand command) {

        try {

            Map<String, Object> parameterMap = command.getParameterMap();

            // temporary prints parameters
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            printParameters(parameterMap);

            String username;
            UserActiveDirectoryId userActiveDirectoryId;

            if (command.getUserActiveDirectoryId() == null) {
                username = parameterMap.get("username").toString();
                userActiveDirectoryId = new UserActiveDirectoryId(username);
            } else {
                userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
            }

            TradeService tradeService = null;

            if (((String) parameterMap.get("referenceType")).equals("ETS")) {
                // Load from repository using ETS number
                ServiceInstructionId etsNumber = new ServiceInstructionId((String) parameterMap.get("etsNumber"));
                tradeService = tradeServiceRepository.load(etsNumber);
            } else {
                tradeService = tradeServiceRepository.load(new TradeServiceId((String) parameterMap.get("tradeServiceId")));
            }
            
            tradeService = TradeServiceService.setSpecialRates(tradeService, tradeService.getDetails());

            Payment payment = null;

            List<Map<String, Object>> productPaymentListMap = new ArrayList<Map<String, Object>>();

            if(parameterMap.get("documentClass").equals("CORRES_CHARGE") && parameterMap.get("referenceType").equals("ETS") && parameterMap.get("form").equals("lcPayment")){
            	System.out.println("im in settlement corres charges"); 
            	tradeService.getDetails().put("allTabSaved", "Y");
            }
            
            if (parameterMap.get("chargeType") != null && parameterMap.get("chargeType").equals("SETTLEMENT")) {
                System.out.println("im in settlement");
                payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
                if (payment == null) {
                    System.out.println("new payment will be made..");
                    payment = new Payment(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
                }

                System.out.println("proceedsPaymentSummary:" + parameterMap.get("proceedsPaymentSummary"));
                productPaymentListMap = (List<Map<String, Object>>) parameterMap.get("proceedsPaymentSummary");
            } else {
                System.out.println("im in product");
                payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                System.out.println("payment: " + payment);
                if (payment == null) {
                    payment = new Payment(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                }

                System.out.println("payment charge type: " + payment.getChargeType());
                System.out.println("documentPaymentSummary:" + parameterMap.get("documentPaymentSummary"));
                productPaymentListMap = (List<Map<String, Object>>) parameterMap.get("documentPaymentSummary");
            }

            // Create payment
            //List<Map<String, Object>> productPaymentListMap = (List<Map<String, Object>>)parameterMap.get("documentPaymentSummary");

            //Form or clearing reset of remittanceFlag
            tradeService.getDetails().put("remittanceFlag", "N");
            tradeService.getDetails().put("cableFeeFlag", "N");

            String lcCurrency = "";
            if (tradeService.getDetails().containsKey("currency")) {
                lcCurrency = (String) tradeService.getDetails().get("currency");
            } else if (tradeService.getDetails().containsKey("hiddenCurrency")) {
                lcCurrency = (String) tradeService.getDetails().get("hiddenCurrency");
            } else if (tradeService.getDetails().containsKey("negoCurrency")) {
                lcCurrency = (String) tradeService.getDetails().get("negoCurrency");
            } else if (tradeService.getDetails().containsKey("negotiationCurrency")) {
                lcCurrency = (String) tradeService.getDetails().get("negotiationCurrency");
            } else if (tradeService.getDetails().containsKey("settlementCurrency")) {
                lcCurrency = (String) tradeService.getDetails().get("settlementCurrency");
            }

            // List<Map<String, String>> ratesNameDescListMap = new ArrayList<Map<String, String>>();
            String thirdToUsdRateName = "";
            String thirdToPhpRateName = "";
            String usdToPhpRateName = "";
            String urrRateName = "";
            String thirdToUsdRateDescription = "";
            String thirdToPhpRateDescription = "";
            String usdToPhpRateDescription = "";
            String urrRateDescription = "";

            Iterator it = parameterMap.entrySet().iterator();
            while (it.hasNext()) {

                Map.Entry pairs = (Map.Entry) it.next();

                // System.out.println(" >>>>>>>>>>>>>>>> pairs.getKey() = " + pairs.getKey());

                if (pairs.getKey().toString().contains("RATE_NAME_")) {

                    String cntr = pairs.getKey().toString().replace("RATE_NAME_", "");
                    String ratesName = (String) parameterMap.get("RATE_NAME_" + cntr.trim());
                    String ratesDesc = (String) parameterMap.get("RATE_DESC_" + cntr.trim());

                    System.out.println(" >>>>>>>>>>>>>>>> ratesName = " + ratesName);
                    System.out.println(" >>>>>>>>>>>>>>>> ratesDesc = " + ratesDesc);

                    String[] ratesNameArray = ratesName.split("-");

                    System.out.println(" >>>>>>>>>>>>>>>> ratesNameArray[0] = " + ratesNameArray[0]);
                    System.out.println(" >>>>>>>>>>>>>>>> ratesNameArray[1] = " + ratesNameArray[1]);

                    if (ratesNameArray[0].equalsIgnoreCase("USD") && ratesNameArray[1].equalsIgnoreCase("PHP")) {
                        if (ratesDesc.contains("BOOKING")) {
                            // URR
                            urrRateName = ratesName;
                            urrRateDescription = ratesDesc;
                        } else {
                            usdToPhpRateName = ratesName;
                            usdToPhpRateDescription = ratesDesc;
                        }
                    } else if (!ratesNameArray[0].equalsIgnoreCase("USD") && ratesNameArray[1].equalsIgnoreCase("PHP")) {
                        thirdToPhpRateName = ratesName;
                        thirdToPhpRateDescription = ratesDesc;
                    } else if (!ratesNameArray[0].equalsIgnoreCase("PHP") && ratesNameArray[1].equalsIgnoreCase("USD")) {
                        thirdToUsdRateName = ratesName;
                        thirdToUsdRateDescription = ratesDesc;
                    }
                }
            }

            System.out.println("THIS IS THE LCCURRENCY::" + lcCurrency);

            Set<PaymentDetail> tempDetails = new HashSet<PaymentDetail>();
            for (Map<String, Object> productPaymentMap : productPaymentListMap) {
                PaymentDetail tempDetail = null;

                String paymentMode = (String) productPaymentMap.get("paymentMode");
                if (paymentMode.equalsIgnoreCase("DTR_LOAN")) {
                    paymentMode = "TR_LOAN";
                }
                System.out.println("paymentMode :"+paymentMode);

                PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(paymentMode);
                System.out.println("paymentInstrumentType paymentInstrumentType paymentInstrumentType:"+paymentInstrumentType);
                String referenceNumber = null;
                String accountName = null;

                BigDecimal amount = (productPaymentMap.get("amountSettlement") != null) ? new BigDecimal(((String) productPaymentMap.get("amountSettlement")).trim()) : new BigDecimal(((String) productPaymentMap.get("amount")).trim());
                System.out.println("amount Paid:" + amount);
                BigDecimal amountInLcCurrency = null;
                if(payment.getChargeType().equals(ChargeType.PRODUCT) && productPaymentMap.containsKey("amount") && productPaymentMap.get("amount")!=null){
                    System.out.println("amount paid in settlement amount in");
                    amountInLcCurrency = new BigDecimal(((String) productPaymentMap.get("amount")).trim());
                } else {
                    List<DocumentClass> nonLcList = new ArrayList<DocumentClass>();
                    nonLcList.add(DocumentClass.OA);
                    nonLcList.add(DocumentClass.DP);
                    nonLcList.add(DocumentClass.DR);
                    nonLcList.add(DocumentClass.DA);

                    if (nonLcList.contains(tradeService.getDocumentClass())) {
                        if (payment.getChargeType().equals(ChargeType.PRODUCT)) {
                            amountInLcCurrency = new BigDecimal(((String) productPaymentMap.get("amount")).trim());
                        } else if (payment.getChargeType().equals(ChargeType.SETTLEMENT)) {
                            amountInLcCurrency = new BigDecimal(((String) productPaymentMap.get("amountSettlement")).trim());
                        }
                    } else {
                        amountInLcCurrency = BigDecimal.ZERO;
                    }
                }
                
                System.out.println("amount Paid In Settlement Currency:" + amountInLcCurrency);
                Currency settlementCurrency = (productPaymentMap.get("settlementCurrency") != null) ? Currency.getInstance(((String) productPaymentMap.get("settlementCurrency")).trim()) : Currency.getInstance(((String) productPaymentMap.get("currency")).trim());

                // Rates
                String ratesString = (String) productPaymentMap.get("rates");
                System.out.println("ratesString:"+ratesString);
                BigDecimal passOnRateThirdToUsd = null;
                BigDecimal passOnRateThirdToPhp = null;
                BigDecimal passOnRateUsdToPhp = null;
                BigDecimal specialRateThirdToUsd = null;
                BigDecimal specialRateThirdToPhp = null;
                BigDecimal specialRateUsdToPhp = null;
                BigDecimal urr = null;

                if (ratesString != null && !ratesString.equals("")) {  // applicable to those payments with rates

                    String[] ratesArray = ratesString.split(",");
                    System.out.println("ratesArray >> " + ratesString);

                    HashMap<String, BigDecimal> temp = extractRates(ratesString);


                    passOnRateThirdToUsd = temp.get("passOnRateThirdToUsd");
                    passOnRateThirdToPhp = null;
                    passOnRateUsdToPhp = temp.get("passOnRateUsdToPhp");
                    specialRateThirdToUsd = temp.get("specialRateThirdToUsd");
                    specialRateThirdToPhp = null;
                    specialRateUsdToPhp = temp.get("specialRateUsdToPhp");
                    urr = temp.get("urr");
                    System.out.println("passOnRateThirdToUsd:"+passOnRateThirdToUsd);
                    System.out.println("passOnRateUsdToPhp:"+passOnRateUsdToPhp);
                    System.out.println("specialRateThirdToUsd:"+specialRateThirdToUsd);
                    System.out.println("specialRateUsdToPhp:"+specialRateUsdToPhp);
                    System.out.println("urr:"+urr);

                }
                System.out.println("Print Rates");

                // For loans
                Currency bookingCurrency = null;
                BigDecimal interestRate = null;
                String interestTerm = null;
                String interestTermCode = null;
                String repricingTerm = null;
                String repricingTermCode = null;
                String loanTerm = null;
                String loanTermCode = null;
                Date loanMaturityDate = null;
                Integer paymentTerm = null;
                Integer facilityId = null;
                String facilityType = null;
                String facilityReferenceNumber = null;
                Integer loanPaymentCode = null;
                Boolean withCramApproval = null;
                //PaymentStatus status = null;

                String referenceId = null;
                if (paymentInstrumentType.equals(PaymentInstrumentType.PDDTS)) {
                    System.out.println("ANGOLS--ANGOLS--ANGOLS--ANGOLS");
                    tradeService.getDetails().put("remittanceFlag", "Y");
                }

                if (paymentInstrumentType.equals(PaymentInstrumentType.SWIFT)) {
                    System.out.println("ANGOLS--ANGOLS--ANGOLS--ANGOLS");
                    tradeService.getDetails().put("cableFeeFlag", "Y");
                }

                System.out.println("paymentInstrumentType: " + paymentInstrumentType);
                switch (paymentInstrumentType) {

                    case CASA:
                    	accountName = (String) productPaymentMap.get("accountName");
                    case MD:
                        referenceNumber = (String) productPaymentMap.get("accountNumber");

                        tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency, accountName);
                        break;
                    case AR:
                        referenceNumber = tradeService.getDocumentNumber().toString(); //(String) productPaymentMap.get("referenceId");//tradeService.getServiceInstructionId().toString(); //(String)chargesPaymentMap.get("")
                        tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, referenceId, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);
                        break;
                    case AP:

                        // For Product charge, only MD and AP are used to pay; AR is not.
                        AccountsPayable accountsPayable = accountsPayableRepository.load((String) productPaymentMap.get("accountNumber"));
                        try {
                        referenceNumber = accountsPayable.getSettlementAccountNumber().toString();
                        referenceId = (String) productPaymentMap.get("accountNumber");
                        } catch (Exception e) {
                        	e.printStackTrace();
                        }
                        tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, referenceId, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);

                        break;

                    case CHECK:
                    case CASH:
                    case REMITTANCE:
                    case IBT_BRANCH:
                    case MC_ISSUANCE:
                    case SWIFT:
                    case PDDTS:
                        referenceNumber = (String) productPaymentMap.get("tradeSuspenseAccount");
                        System.out.println("IN HERE HERE HERE 20131018");
                        tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);

                        break;

                    case IB_LOAN:
                    case TR_LOAN:
                    case DBP:
                    case EBP:

                        Map<String, Object> setStringMap1 = (Map<String, Object>) productPaymentMap.get("setupString");
                        MapUtil mapWrapper = new MapUtil((Map<String, Object>) productPaymentMap.get("setupString"));

                        // Use DocumentNumber as the referenceNumber for loans
                        referenceNumber = tradeService.getDocumentNumber().toString();

                        //booking currency is now equal to settlement currency
//                        bookingCurrency = mapWrapper.getAsCurrency("bookingCurrency");

                        //status = (PaymentStatus) parameterMap.get("status");
                        for (String s : setStringMap1.keySet()) {
                            System.out.println("s:"+s);
                            System.out.println("setStringMap1.get(s):"+setStringMap1.get(s));
                        }
                        System.out.println("mapWrapper:"+mapWrapper);
                        interestRate = mapWrapper.getAsBigDecimal("interestRate");
                        interestTerm = mapWrapper.getString("interestTerm").trim();
                        interestTermCode = mapWrapper.getString("interestTermCode") != null ? mapWrapper.getString("interestTermCode").trim() : mapWrapper.getString("interestTermCode");
                        repricingTerm = mapWrapper.getString("repricingTerm").trim();
                        repricingTermCode = mapWrapper.getString("repricingTermCode").trim();
                        loanTerm = mapWrapper.getString("loanTerm").trim();
                        loanTermCode = (String) setStringMap1.get("loanTermCode");
                        loanPaymentCode = new Integer(((String) setStringMap1.get("loanPaymentCode")).trim());
                        withCramApproval = new Boolean(((String) setStringMap1.get("withCramApproval")).trim());
                        //status = mapWrapper.getString("status") != null ? mapWrapper.getString("status").trim() : mapWrapper.getString("status");
                        System.out.println("interest Rate in Handler: " + interestRate);
                        loanMaturityDate = mapWrapper.getAsDate("loanMaturityDate");
//                        paymentTerm = mapWrapper.getAsInteger("paymentTerm");
//                        facilityId = mapWrapper.getAsInteger("facilityId");
//                        facilityType = mapWrapper.getString("facilityType");
//                        facilityReferenceNumber = mapWrapper.getString("facilityReferenceNumber");
                        if(null == setStringMap1.get("numberOfFreeFloatDays")){

	                        tempDetail = new PaymentDetail(paymentInstrumentType,
	                                referenceNumber,
	                                amount,
	                                settlementCurrency,
	                                settlementCurrency,
	                                interestRate,
	                                interestTerm,
	                                interestTermCode,
	                                repricingTerm,
	                                repricingTermCode,
	                                loanTerm,
	                                loanTermCode,
	                                loanMaturityDate,
	                                paymentTerm,
	                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
	                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
	                                facilityId, facilityType, facilityReferenceNumber, amountInLcCurrency, loanPaymentCode, withCramApproval);
                        } else {
                        	tempDetail = new PaymentDetail(paymentInstrumentType,
                                    referenceNumber,
                                    amount,
                                    settlementCurrency,
                                    settlementCurrency,
                                    interestRate,
                                    interestTerm,
                                    interestTermCode,
                                    repricingTerm,
                                    repricingTermCode,
                                    loanTerm,
                                    loanTermCode,
                                    loanMaturityDate,
                                    paymentTerm,
                                    passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                    specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                                    facilityId, facilityType, facilityReferenceNumber, amountInLcCurrency, loanPaymentCode, withCramApproval,
                                    new Long(((String) setStringMap1.get("numberOfFreeFloatDays")).trim()));
                        }
                        
                        System.out.println("dulong interest rate: " + interestRate);
                        System.out.println("tempdetail interest rate: " + tempDetail.getInterestRate());

                        // add loan details for loan
                        System.out.println("ADDING LOAN DETAILS IN TRADESERVICE");
                        tradeService.getDetails().put("bookingCurrency", settlementCurrency.toString());
                        tradeService.getDetails().put("interestRate", interestRate);
                        tradeService.getDetails().put("interestTerm", interestTerm);
                        tradeService.getDetails().put("interestTermCode", interestTermCode);
                        tradeService.getDetails().put("repricingTerm", repricingTerm);
                        tradeService.getDetails().put("repricingTermCode", repricingTermCode);
                        tradeService.getDetails().put("loanTerm", loanTerm);
                        tradeService.getDetails().put("loanTermCode", loanTermCode);
                        tradeService.getDetails().put("loanMaturityDate", loanMaturityDate);
                        tradeService.getDetails().put("paymentTerm", paymentTerm);
                        tradeService.getDetails().put("facilityId", facilityId);
                        tradeService.getDetails().put("facilityType", facilityType);
                        tradeService.getDetails().put("facilityReferenceNumber", facilityReferenceNumber);
                        tradeService.getDetails().put("loanPaymentCode", loanPaymentCode);
                        tradeService.getDetails().put("withCramApproval", withCramApproval);
                        if( setStringMap1.containsKey("numberOfFreeFloatDays") && !setStringMap1.get("numberOfFreeFloatDays").toString().equalsIgnoreCase("")){
                        	tradeService.getDetails().put("numberOfFreeFloatDays", new Long(((String) setStringMap1.get("numberOfFreeFloatDays")).trim()));
                        }
                        System.out.println("END ADDING LOAN DETAILS IN TRADESERVICE");
                        break;

                    case UA_LOAN:

                        Map<String, Object> setStringMap2 = (Map<String, Object>) productPaymentMap.get("setupString");

                        // Use DocumentNumber as the referenceNumber for loans
                        referenceNumber = tradeService.getDocumentNumber().toString();

                        bookingCurrency = Currency.getInstance(((String) setStringMap2.get("bookingCurrency")).trim());
                        DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                        loanMaturityDate = df2.parse((String) setStringMap2.get("loanMaturityDate"));
//                        paymentTerm = Integer.valueOf((String)setStringMap2.get("paymentTerm"));
//                        facilityId = Integer.valueOf((String)setStringMap2.get("facilityId"));
//                        facilityType = (String)setStringMap2.get("facilityType");
//                        facilityReferenceNumber = (String)setStringMap2.get("facilityReferenceNumber");


                        tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber,
                                amount, settlementCurrency,
                                bookingCurrency, interestRate, interestTerm, interestTermCode,
                                repricingTerm, repricingTermCode,
                                loanTerm, loanTermCode, loanMaturityDate, paymentTerm,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                                facilityId, facilityType, facilityReferenceNumber, amountInLcCurrency, loanPaymentCode, withCramApproval);

                        break;
                }

                System.out.println("\n");
                System.out.println("paymentInstrumentType = " + paymentInstrumentType);
                System.out.println("referenceNumber = " + referenceNumber);
                
                System.out.println("=========Testing Starts========");
                try {
                    System.out.println("accountName = " + accountName);
                    System.out.println("paymentInstrumentType===" + paymentInstrumentType);                
                    if (paymentInstrumentType.toString().equalsIgnoreCase("CASA") && (accountName == null || accountName.equals(""))) {
                        username = parameterMap.get("username").toString();
                        Currency currency =Currency.getInstance(parameterMap.get("proceedsCurrencyModified").toString());    
                        casaAccount = paymentService.getAccountDetails(referenceNumber,username,currency);
                        System.out.println("CASA Response accountName = " + casaAccount.getAccountName());
                        accountName = casaAccount.getAccountName();
                        tempDetail.setAccountName(accountName);
                	} 
                } catch (Exception e) {
                	e.printStackTrace();
                }
                System.out.println("=========Testing End========");            	
            	
                tempDetail.updateRatesNameDescription(
                        thirdToUsdRateName,
                        thirdToPhpRateName,
                        usdToPhpRateName,
                        urrRateName,
                        thirdToUsdRateDescription,
                        thirdToPhpRateDescription,
                        usdToPhpRateDescription,
                        urrRateDescription);

                tempDetails.add(tempDetail);
                System.out.println(""+tempDetail.getUrr());
                System.out.println(""+tempDetail.getSpecialRateThirdToUsd());
                System.out.println(""+tempDetail.getSpecialRateUsdToPhp());
                System.out.println(""+tempDetail.getPassOnRateThirdToUsd());
                System.out.println(""+tempDetail.getPassOnRateUsdToPhp());
                System.out.println("final print for interest rate: " + tempDetail.getInterestRate());
            }
            System.out.println("\n");


            // added pass on rate confirmed by parameter
            if(parameterMap.get("passOnRateConfirmedByCash") != null){
            	tradeService.getDetails().put("passOnRateConfirmedByCash", parameterMap.get("passOnRateConfirmedByCash"));
            }
            if(parameterMap.get("passOnRateConfirmedBySettlement") != null){
            	tradeService.getDetails().put("passOnRateConfirmedBySettlement", parameterMap.get("passOnRateConfirmedBySettlement"));
            }
            if(parameterMap.get("newProceedsCurrency") != null){
            	tradeService.getDetails().put("newProceedsCurrency", parameterMap.get("newProceedsCurrency"));
            }

            if (tradeService.getDocumentClass().equals(DocumentClass.CORRES_CHARGE)) {
                tradeService.getDetails().put("totalBillingAmountInPhp", parameterMap.get("totalBillingAmountInPhp"));
            }

            Iterator itTS = parameterMap.entrySet().iterator();

            while (itTS.hasNext()) {
                Map.Entry pairs = (Map.Entry) itTS.next();
                //System.out.println(pairs.getKey() + " = " + pairs.getValue());
                if (pairs.getKey().toString().contains("_text_pass_on_rate") ||
                        pairs.getKey().toString().contains("_pass_on_rate_cash") ||
                        pairs.getKey().toString().contains("_text_special_rate") ||
                        pairs.getKey().toString().contains("_special_rate_cash")) {

                    tradeService.getDetails().put((String) pairs.getKey(), pairs.getValue());
                }
            }

            //tradeServiceRepository.merge(tradeService);

            if (tradeService.getServiceInstructionId() != null) {
                ServiceInstruction serviceInstruction = serviceInstructionRepository.load(tradeService.getServiceInstructionId());
                if (serviceInstruction != null) {
                	if(parameterMap.get("passOnRateConfirmedByCash") != null){
                		serviceInstruction.getDetails().put("passOnRateConfirmedByCash", parameterMap.get("passOnRateConfirmedByCash"));
                	}
                    if(parameterMap.get("passOnRateConfirmedBySettlement") != null){
                    	serviceInstruction.getDetails().put("passOnRateConfirmedBySettlement", parameterMap.get("passOnRateConfirmedBySettlement"));
                    }
                    if(parameterMap.get("newProceedsCurrency") != null){
                    	serviceInstruction.getDetails().put("newProceedsCurrency", parameterMap.get("newProceedsCurrency"));
                    }
                    if(parameterMap.get("documentClass").equals("CORRES_CHARGE") && parameterMap.get("referenceType").equals("ETS") && parameterMap.get("form").equals("lcPayment")){ 
                    	serviceInstruction.getDetails().put("allTabSaved", "Y");
                    }
                
                    Iterator itSE = parameterMap.entrySet().iterator();

                    while (itSE.hasNext()) {
                        Map.Entry pairs = (Map.Entry) itSE.next();
                        //System.out.println(pairs.getKey() + " = " + pairs.getValue());
                        if (pairs.getKey().toString().contains("_text_pass_on_rate") ||
                                pairs.getKey().toString().contains("_pass_on_rate_cash") ||
                                pairs.getKey().toString().contains("_text_special_rate") ||
                                pairs.getKey().toString().contains("_special_rate_cash")) {

                            serviceInstruction.getDetails().put((String) pairs.getKey(), pairs.getValue());
                        }
                    }

                    serviceInstructionRepository.merge(serviceInstruction);
                }
            }


            // Persist payment

            // 1) Remove all items first
            payment.deleteAllPaymentDetails();
            // 2) Add new PaymentDetails
            payment.addNewPaymentDetails(tempDetails);
            System.out.println(payment.displayList());
            // 3) Use saveOrUpdate
            paymentRepository.saveOrUpdate(payment);
            //paymentRepository.merge(payment);

            PaymentSavedEvent paymentSavedEvent = new PaymentSavedEvent(tradeService.getTradeServiceId(), payment, userActiveDirectoryId);
            eventPublisher.publish(paymentSavedEvent);
            System.out.println("for saving of tradeservice passOnRateConfirmedBy: " + tradeService.getDetails().get("passOnRateConfirmedByCash"));
            tradeServiceRepository.merge(tradeService);


            for (PaymentDetail pd : payment.getDetails()) {
                if (pd.getPaymentInstrumentType().equals(PaymentInstrumentType.SWIFT) ||
                        pd.getPaymentInstrumentType().equals(PaymentInstrumentType.PDDTS) ||
                        pd.getPaymentInstrumentType().equals(PaymentInstrumentType.MC_ISSUANCE)) {

                    PaymentItemPaidEvent itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),
                            pd.getPaymentInstrumentType(),
                            tradeService.getDocumentNumber().toString(),
                            pd.getReferenceNumber(),
                            pd.getAmount(),
                            pd.getCurrency(),
                            pd.getBookingCurrency(),
                            pd.getInterestRate(),
                            pd.getInterestTerm(),
                            pd.getInterestTermCode(),
                            pd.getRepricingTerm(),
                            pd.getRepricingTermCode(),
                            pd.getLoanTerm(),
                            pd.getLoanTermCode(),
                            pd.getLoanMaturityDate());

                    payment.payItem(pd.getPaymentInstrumentType(), tradeService.getDocumentNumber().toString(), pd.getReferenceNumber());
                    //added this since if it is settlement it should not actually be treated as an itemPaidEvent imho--Angol
                    if (payment.getChargeType().compareTo(ChargeType.PRODUCT) == 0 || payment.getChargeType().compareTo(ChargeType.SERVICE) == 0) {
                        eventPublisher.publish(itemPaidEvent);
                    }
                }
            }

            if (payment.getChargeType().equals(ChargeType.SETTLEMENT)) {
                if (payment.containsPddtsOrSwift() || payment.containsCasaOrIbt()) {
                    System.out.println("containsPddtsOrSwiftOrCasaOrIbt");
                    
                    // set paymentStatus to UNPAID only if the current paymentStatus is NO_PAYMENT_REQUIRED
                    if (tradeService.getPaymentStatus().equals(PaymentStatus.NO_PAYMENT_REQUIRED)) {
                        tradeService.setPaymentStatus(PaymentStatus.UNPAID);
                    }
                    //additional conditions for REFUNDS since they function similarly to SETTLEMENT but are required to be paid.
                } else if(ServiceType.REFUND.equals(tradeService.getServiceType())){
                	if(payment.containsCasaOrIbt())
                		tradeService.unPay();
                	else
                		tradeService.setPaymentStatus(PaymentStatus.NO_PAYMENT_REQUIRED);
                } else {
                    //List<ChargeType> chargeTypeList = paymentRepository.getAllPaymentChargeTypesPerTradeService(payment.getTradeServiceId());

                    // change TradeService.paymentStatus to NO_PAYMENT_REQUIRED only if there is no payment added for PRODUCT and SERVICE
                    System.out.println("here i am : " + parameterMap.get("containsProductPayment"));
                    Boolean hasOtherPayments = Boolean.FALSE;

                    if (!parameterMap.get("containsProductPayment").equals("true")) {
                        System.out.println("setting no payment required #5");
                        Payment productPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
//                        Payment settlementPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
                        Payment servicePayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);

                        if (productPayment != null && productPayment.getDetails().size() > 0) {
                            hasOtherPayments = Boolean.TRUE;
                        }

//                        if (settlementPayment != null && settlementPayment.getDetails().size() > 0) {
//                            hasOtherPayments = Boolean.TRUE;
//                        }

                        if (servicePayment != null && servicePayment.getDetails().size() > 0) {
                            hasOtherPayments = Boolean.TRUE;
                        }

                        System.out.println("hasOtherPayments" + hasOtherPayments);

                        if (!hasOtherPayments) {
                            tradeService.setAsNoPaymentRequired();
                        } else {
                            tradeService.setPaymentStatus(PaymentStatus.UNPAID);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, BigDecimal> extractRates(String ratesString) {
        HashMap<String, BigDecimal> temp = new HashMap<String, BigDecimal>();

        HashMap<String, String> tempContainer = new HashMap<String, String>();

        String[] ratesArray = ratesString.split(",");
        for (String s : ratesArray) {
            String[] rate = s.split("=");
            if (rate.length == 2) {
                tempContainer.put(rate[0], rate[1]);
            }
        }
        System.out.println("tempContainer:" + tempContainer);


        String strPassOnRateUsdToPhp = null;
        String strPassOnRateThirdToUsd = null;
        String strPassOnRateThirdToPhp = null;
        String strSpecialRateUsdToPhp = null;
        String strSpecialRateThirdToUsd = null;
        String strSpecialRateThirdToPhp = null;
        String strUrr = null;

        if (tempContainer.containsKey("passOnRateUsdToPhp")) {
            strPassOnRateUsdToPhp = tempContainer.get("passOnRateUsdToPhp");
        }

        if (tempContainer.containsKey("passOnRateThirdToUsd")) {
            strPassOnRateThirdToUsd = tempContainer.get("passOnRateThirdToUsd");
        }

        if (tempContainer.containsKey("specialRateUsdToPhp")) {
            strSpecialRateUsdToPhp = tempContainer.get("specialRateUsdToPhp");
        }

        if (tempContainer.containsKey("specialRateThirdToUsd")) {
            strSpecialRateThirdToUsd = tempContainer.get("specialRateThirdToUsd");
        }

        if (tempContainer.containsKey("urr")) {
            strUrr = tempContainer.get("urr");
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
        if (strPassOnRateUsdToPhp != null && !strPassOnRateUsdToPhp.equals("")) {
            passOnRateUsdToPhp = new BigDecimal(strPassOnRateUsdToPhp.trim());
        }

        if (strSpecialRateThirdToUsd != null && !strSpecialRateThirdToUsd.equals("")) {
            specialRateThirdToUsd = new BigDecimal(strSpecialRateThirdToUsd.trim());
        }
        if (specialRateThirdToUsd == null) {
            specialRateThirdToUsd = passOnRateThirdToUsd;
            System.out.println(specialRateThirdToUsd);
        }


        if (strSpecialRateUsdToPhp != null && !strSpecialRateUsdToPhp.equals("")) {
            specialRateUsdToPhp = new BigDecimal(strSpecialRateUsdToPhp.trim());
        }
        if (specialRateUsdToPhp == null) {
            specialRateUsdToPhp = passOnRateUsdToPhp;
            System.out.println(specialRateUsdToPhp);
        }

        if (strUrr != null && !strUrr.equals("")) {
            urr = new BigDecimal(strUrr.trim());
        }

        if (specialRateUsdToPhp == null && passOnRateUsdToPhp == null) {
            specialRateUsdToPhp = urr;
            passOnRateUsdToPhp = urr;
        }

        temp.put("specialRateThirdToUsd", specialRateThirdToUsd);
        temp.put("passOnRateThirdToUsd", passOnRateThirdToUsd);
        temp.put("specialRateUsdToPhp", specialRateUsdToPhp);
        temp.put("passOnRateUsdToPhp", passOnRateUsdToPhp);
        temp.put("urr", urr);

        System.out.println("temp:" + temp);
        return temp;
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside save lc payment form command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
