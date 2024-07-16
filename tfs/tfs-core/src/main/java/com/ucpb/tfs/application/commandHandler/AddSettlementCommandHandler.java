package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.AddSettlementCommand;
import com.ucpb.tfs.domain.cdt.event.CDTPaidEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaidEvent;
import com.ucpb.tfs.domain.payment.event.PaymentSavedEvent;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayable;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayableRepository;
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
 * User: Marv
 * Date: 9/7/12
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class AddSettlementCommandHandler implements CommandHandler<AddSettlementCommand> {

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    private PaymentRepository paymentRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    AccountsPayableRepository accountsPayableRepository;

    @Override
    public void handle(AddSettlementCommand command) {
        System.out.println("AddSettlementCommandHandler");
        try {

            Map<String, Object> parameterMap = command.getParameterMap();

            // temporary prints parameters
            printParameters(parameterMap);
            
            String username;
            UserActiveDirectoryId userActiveDirectoryId;

            if(command.getUserActiveDirectoryId() == null){
                username = parameterMap.get("username").toString();
                userActiveDirectoryId = new UserActiveDirectoryId(username);
            } else {
                userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
            }

            TradeService tradeService = tradeServiceRepository.load(new TradeServiceId((String)parameterMap.get("tradeServiceId")));

            String lcCurrency = "";
            if(tradeService.getDetails().containsKey("currency")){
                lcCurrency = (String) tradeService.getDetails().get("currency");
            }
            else
            if(tradeService.getDetails().containsKey("hiddenCurrency")){
                lcCurrency = (String) tradeService.getDetails().get("hiddenCurrency");
            }
            else
            if(tradeService.getDetails().containsKey("negoCurrency")){
                lcCurrency = (String) tradeService.getDetails().get("negoCurrency");
            }
            else
            if(tradeService.getDetails().containsKey("negotiationCurrency")){
                lcCurrency = (String) tradeService.getDetails().get("negotiationCurrency");
            }
            else
            if(tradeService.getDetails().containsKey("settlementCurrency")){
                lcCurrency = (String) tradeService.getDetails().get("settlementCurrency");
            }


            ChargeType chargeType = ChargeType.valueOf(((String)parameterMap.get("chargeType")).toUpperCase());
            Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), chargeType);
            if (payment == null) {
                payment = new Payment(tradeService.getTradeServiceId(), chargeType);
            }

            String paymentMode = (String) parameterMap.get("paymentMode");

            PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(extractPaymentMode(paymentMode));

            String referenceNumber = null;

            BigDecimal amount = parameterMap.get("amountSettlement") != null ? new BigDecimal(((String) parameterMap.get("amountSettlement")).trim()) : new BigDecimal(((String) parameterMap.get("amount")).trim());
            BigDecimal amountInLcCurrency = null;
            if(parameterMap.containsKey("amount")){
                String strAmount = (String) parameterMap.get("amount");
                if(strAmount!=null && !strAmount.equalsIgnoreCase("")){
                    System.out.println("parameterMap.containsKey(\"amount\")::"+ strAmount);
                    try {
                        amountInLcCurrency = new BigDecimal(((String) parameterMap.get("amount")).trim());
                        System.out.println("AMOUNT IN LC CURRENCY " + amountInLcCurrency);
                    } catch (Exception e){
                        e.printStackTrace();
                        amountInLcCurrency = null;
                    }
                } else if(chargeType.equals(ChargeType.SETTLEMENT)){
                    amountInLcCurrency = amount;
                }
            }

            Currency settlementCurrency = parameterMap.get("settlementCurrency") != null ? Currency.getInstance(((String) parameterMap.get("settlementCurrency")).trim()) : Currency.getInstance(((String) parameterMap.get("currency")).trim());

            // Rates
            String ratesString = (String)parameterMap.get("rates");
            System.out.println("rates:"+ratesString);
            BigDecimal passOnRateThirdToUsd = null;
            BigDecimal passOnRateThirdToPhp = null;
            BigDecimal passOnRateUsdToPhp = null;
            BigDecimal specialRateThirdToUsd = null;
            BigDecimal specialRateThirdToPhp = null;
            BigDecimal specialRateUsdToPhp = null;
            BigDecimal urr = null;

            // will only apply rates if there is rates passed (this will not be executed in md collection)
            if(ratesString != null && ratesString.length() != 0) {
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

                String[] usdToPhpSpecial = ratesArray[3].split("=");
                if (usdToPhpSpecial.length > 1) {
                    strSpecialRateUsdToPhp = usdToPhpSpecial[1];
                }
                String[] thirdToUsdSpecial = ratesArray[4].split("=");
                if (thirdToUsdSpecial.length > 1) {
                    strSpecialRateThirdToUsd = thirdToUsdSpecial[1];
                }
                String[] thirdToPhpSpecial = ratesArray[5].split("=");
                if (thirdToPhpSpecial.length > 1) {
                    strSpecialRateThirdToPhp = thirdToPhpSpecial[1];
                }
                String[] urrString = ratesArray[6].split("=");
                if (urrString.length > 1) {
                    strUrr = urrString[1];
                }

                String strPassOnBuyRate="";
                if (ratesArray.length > 7 && ratesArray[7] != null) {
                    String[] buyRatePassOnString = ratesArray[7].split("=");
                    if (buyRatePassOnString.length > 1) {
                        strPassOnBuyRate = buyRatePassOnString[1];
                    }
                }

                String strSpecialBuyRate="";
                if (ratesArray.length > 8 && ratesArray[8] != null) {
                    String[] buyRateSpecialString = ratesArray[8].split("=");
                    if (buyRateSpecialString.length > 1) {
                        strSpecialBuyRate = buyRateSpecialString[1];
                    }
                }

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

                if ("PHP".equalsIgnoreCase(lcCurrency) &&  "USD".equalsIgnoreCase(settlementCurrency.getCurrencyCode()) ){
                    System.out.println("ANGULO ANGULO ANGULO");
                    if (strPassOnBuyRate != null && !strPassOnBuyRate.equals("")) {
                        System.out.println("ANGULO ANGULO ANGULO 00");
                        passOnRateUsdToPhp = new BigDecimal(strPassOnBuyRate.trim());
                    }

                    if (strSpecialBuyRate != null && !strSpecialBuyRate.equals("")) {
                        System.out.println("ANGULO ANGULO ANGULO 01");
                        specialRateUsdToPhp = new BigDecimal(strSpecialBuyRate.trim());
                    }

                }



            }else{
                System.out.println("no rates..");
            }

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
            String accountName = null;

            switch (paymentInstrumentType) {

                case CASA:

                    accountName = (String) parameterMap.get("accountName");

                case MD:
                    // For Product charge, only MD and AP are used to pay; AR is not.
                    referenceNumber = (String) parameterMap.get("accountNumber");

                    System.out.println("amountInLcCurrency:"+amountInLcCurrency);
                    //System.out.println("BigDecimal.ZERO.compareTo(amountInLcCurrency)==-1:"+(BigDecimal.ZERO.compareTo(amountInLcCurrency)==-1));

                    if(amountInLcCurrency!=null && BigDecimal.ZERO.compareTo(amountInLcCurrency)==-1){
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, accountName, amountInLcCurrency);
                    } else {
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,accountName);
                    }

                    break;
                case AR:
                    referenceNumber = tradeService.getDocumentNumber().toString(); //(String) productPaymentMap.get("referenceId");//tradeService.getServiceInstructionId().toString(); //(String)chargesPaymentMap.get("")


                    if(amountInLcCurrency!=null && BigDecimal.ZERO.compareTo(amountInLcCurrency)==-1){
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);
                    } else {

                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,"");
                    }


                    break;
                case AP:

                    // For Product charge, only MD and AP are used to pay; AR is not.
                    AccountsPayable accountsPayable = accountsPayableRepository.load((String) parameterMap.get("referenceId"));

                    referenceNumber = accountsPayable.getSettlementAccountNumber().toString();
                    String referenceIdAp = (String) parameterMap.get("referenceId");

                    if(amountInLcCurrency!=null && BigDecimal.ZERO.compareTo(amountInLcCurrency)==-1){
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber, amount, settlementCurrency, referenceIdAp,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);
                    } else {
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber, amount, settlementCurrency, referenceIdAp,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);
                    }



                    break;

                case CHECK:
                case CASH:
                case REMITTANCE:
                case IBT_BRANCH:
                case MC_ISSUANCE:
                case SWIFT:
                case PDDTS:

                    referenceNumber = (String) parameterMap.get("tradeSuspenseAccount");

                    if(amountInLcCurrency!=null && BigDecimal.ZERO.compareTo(amountInLcCurrency)==-1){
                        System.out.println("i am here " + amountInLcCurrency);
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,"",amountInLcCurrency);
                    } else {
                        System.out.println("i am there " + amountInLcCurrency);
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,"");
                    }


                    break;
                case IB_LOAN:
                case TR_LOAN:
                case DBP:
                case EBP:
                    Map<String, Object> setStringMap1 = (Map<String, Object>)parameterMap.get("setupString");
                    // Use DocumentNumber as the referenceNumber for loans
                    referenceNumber = tradeService.getDocumentNumber().toString();

                    bookingCurrency = Currency.getInstance((String)parameterMap.get("settlementCurrency"));
                    interestRate = new BigDecimal(((String) setStringMap1.get("interestRate")).trim());
                    interestTerm = (String) setStringMap1.get("interestTerm");
                    interestTermCode  = (String)setStringMap1.get("interestTermCode");
                    repricingTerm = (String) setStringMap1.get("repricingTerm");
                    repricingTermCode = (String) setStringMap1.get("repricingTermCode");
                    loanTerm = (String) setStringMap1.get("loanTerm");
                    loanTermCode = (String) setStringMap1.get("loanTermCode");

                    DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
                    loanMaturityDate = df1.parse((String) setStringMap1.get("loanMaturityDate"));




                    if(amountInLcCurrency!=null && BigDecimal.ZERO.compareTo(amountInLcCurrency)==-1){
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber,
                                amount, settlementCurrency,
                                bookingCurrency, interestRate, interestTerm,interestTermCode,
                                repricingTerm, repricingTermCode,
                                loanTerm, loanTermCode, loanMaturityDate,paymentTerm,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                                facilityId, facilityType, null,null,amountInLcCurrency);
                    } else {
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber,
                                amount, settlementCurrency,
                                bookingCurrency, interestRate, interestTerm,interestTermCode,
                                repricingTerm, repricingTermCode,
                                loanTerm, loanTermCode, loanMaturityDate,paymentTerm,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                                facilityId, facilityType, null,null);
                    }



                    break;
                case UA_LOAN:

                    Map<String, Object> uaMap = (Map<String, Object>)parameterMap.get("setupString");
                    // Use DocumentNumber as the referenceNumber for loans
                    referenceNumber = tradeService.getDocumentNumber().toString();

                    bookingCurrency = Currency.getInstance((String)parameterMap.get("settlementCurrency"));
                    interestRate = BigDecimal.ZERO;
                    interestTerm = "0";
                    interestTermCode  = "M";
                    repricingTerm = (String) uaMap.get("repricingTerm");
                    repricingTermCode = (String) uaMap.get("repricingTermCode");
                    loanTermCode = "M";

                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    loanMaturityDate = dateFormat.parse((String) uaMap.get("loanMaturityDate"));




                    if(amountInLcCurrency!=null && BigDecimal.ZERO.compareTo(amountInLcCurrency)==-1){
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber,
                                amount, settlementCurrency,
                                bookingCurrency, interestRate, interestTerm,interestTermCode,
                                repricingTerm, repricingTermCode,
                                loanTerm, loanTermCode, loanMaturityDate,paymentTerm,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                                facilityId, facilityType, null,null, amountInLcCurrency);
                    } else {
                        payment.addOrUpdateItem(paymentInstrumentType, referenceNumber,
                                amount, settlementCurrency,
                                bookingCurrency, interestRate, interestTerm,interestTermCode,
                                repricingTerm, repricingTermCode,
                                loanTerm, loanTermCode, loanMaturityDate,paymentTerm,
                                passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                                specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                                facilityId, facilityType, null,null);
                    }



                    break;

//                case UA_LOAN:
//
//                    Map<String, Object> setStringMap2 = (Map<String, Object>)parameterMap.get("setupString");
//
//                    // Use DocumentNumber as the referenceNumber for loans
//                    referenceNumber = tradeService.getDocumentNumber().toString();
//
//                    bookingCurrency = Currency.getInstance(((String) setStringMap2.get("bookingCurrency")).trim());
//                    DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
//                    loanMaturityDate = df2.parse((String) setStringMap2.get("loanMaturityDate"));
//                    paymentTerm = Integer.valueOf((String)setStringMap2.get("paymentTerm"));
//                    facilityId = Integer.valueOf((String)setStringMap2.get("facilityId"));
//                    facilityType = (String)setStringMap2.get("facilityType");
//                    facilityReferenceNumber = (String)setStringMap2.get("facilityReferenceNumber");
//
//                    payment.addOrUpdateItem(paymentInstrumentType, referenceNumber,
//                                            amount, settlementCurrency,
//                                            bookingCurrency, interestRate, interestTerm,
//                                            repricingTerm, repricingTermCode,
//                                            loanTerm, loanTermCode, loanMaturityDate,paymentTerm,
//                                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
//                                            facilityId, facilityType, facilityReferenceNumber);
//
//                    break;
            }

            System.out.println("\n");
            System.out.println("paymentInstrumentType = " + paymentInstrumentType);
            System.out.println("referenceNumber = " + referenceNumber);

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

            Set<PaymentDetail> newPaymentDetailSet = payment.getDetails();
            for (PaymentDetail pd : newPaymentDetailSet) {
                // This automatically updates the "backing" set in Payment
                pd.updateRatesNameDescription(
                        thirdToUsdRateName,
                        thirdToPhpRateName,
                        usdToPhpRateName,
                        urrRateName,
                        thirdToUsdRateDescription,
                        thirdToPhpRateDescription,
                        usdToPhpRateDescription,
                        urrRateDescription);
            }

/*
            Set<PaymentDetail> newPaymentDetailSet = payment.getDetails();
            for (PaymentDetail pd : newPaymentDetailSet) {
                pd.updateRatesNameDescription(
                        "Test 1",
                        "Test 2",
                        "Test 3",
                        "Test 4",
                        "Test 1 DESC",
                        "Test 2 DESC",
                        "Test 3 DESC",
                        "Test 4 DESC");
            }
*/

            // Persist payment
            paymentRepository.saveOrUpdate(payment);

            PaymentSavedEvent paymentSavedEvent = new PaymentSavedEvent(tradeService.getTradeServiceId(), payment, userActiveDirectoryId);
            eventPublisher.publish(paymentSavedEvent);

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

                    eventPublisher.publish(itemPaidEvent);

                    if (tradeService.getDocumentClass().equals(DocumentClass.CDT) && tradeService.getServiceType().equals(ServiceType.PAYMENT)) {
                        System.out.println("cdt payment event is called by MC_ISSUANCE");
                        String iedieirdNumber = tradeService.getTradeServiceReferenceNumber().toString();
                        String processingUnitCode = null;

                        if (tradeService.getDetails().get("processingUnitCode") != null) {
                            processingUnitCode = tradeService.getDetails().get("processingUnitCode").toString();
                        } else {
                            processingUnitCode = tradeService.getDetails().get("unitCode").toString();
                        }

                        CDTPaidEvent cdtPaidEvent = new CDTPaidEvent(iedieirdNumber, payment, processingUnitCode, tradeService);
                        eventPublisher.publish(cdtPaidEvent);
                    }
                }
            }

            if (payment.getChargeType().equals(ChargeType.SETTLEMENT)) {
                if (payment.containsPddtsOrSwift()) {
                    System.out.println("containsPddtsOrSwift");

                    // set paymentStatus to UNPAID only if the current paymentStatus is NO_PAYMENT_REQUIRED
                    if (tradeService.getPaymentStatus().equals(PaymentStatus.NO_PAYMENT_REQUIRED)) {
                        tradeService.setPaymentStatus(PaymentStatus.UNPAID);
                    }
                    //additional conditions for REFUNDS since they function similarly to SETTLEMENT but are required to be paid.
                } else if(ServiceType.REFUND.equals(tradeService.getServiceType())){
                	if(payment.containsCasaOrIbt())
                		tradeService.unPay();
                	else {
                		Payment servicePayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
                		if (servicePayment != null && servicePayment.getDetails().size() > 0) {
                			if (PaymentStatus.PAID.equals(servicePayment.getStatus()))
            					tradeService.paid();
                			else
                				tradeService.unPay();
                        } else
                		tradeService.setPaymentStatus(PaymentStatus.NO_PAYMENT_REQUIRED);
                	}
                } else {
                    //List<ChargeType> chargeTypeList = paymentRepository.getAllPaymentChargeTypesPerTradeService(payment.getTradeServiceId());

                    // change TradeService.paymentStatus to NO_PAYMENT_REQUIRED only if there is no payment added for PRODUCT and SERVICE
                    System.out.println("here i am : " + parameterMap.get("containsProductPayment"));
//                    if (!parameterMap.get("containsProductPayment").equals("true")) {
//                        System.out.println("setting no payment required #7");
//                        tradeService.setAsNoPaymentRequired();
//                    }

                    Boolean hasOtherPayments = Boolean.FALSE;

                    if (!parameterMap.get("containsProductPayment").equals("true")) {
                        System.out.println("setting no payment required #5");
                        Payment productPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                        Payment settlementPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
                        Payment servicePayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);

                        if (productPayment != null && productPayment.getDetails().size() > 0) {
                            hasOtherPayments = Boolean.TRUE;
                        }

                        if (settlementPayment != null && settlementPayment.getDetails().size() > 0) {
                            hasOtherPayments = Boolean.TRUE;
                        }

                        if (servicePayment != null && servicePayment.getDetails().size() > 0) {
                            hasOtherPayments = Boolean.TRUE;
                        }

                        if (!hasOtherPayments) {
                            tradeService.setAsNoPaymentRequired();
                        } else {
                            tradeService.setPaymentStatus(PaymentStatus.UNPAID);
                        }
                    }
                }

                if (tradeService.getDocumentClass().equals(DocumentClass.LC) && tradeService.getServiceType().equals(ServiceType.REFUND)) {
                    String settlementMode = (String) parameterMap.get("paymentMode");

                    PaymentInstrumentType settlementType = PaymentInstrumentType.valueOf(extractPaymentMode(settlementMode));

                    if (settlementType.equals(PaymentInstrumentType.MC_ISSUANCE)) {
                        tradeService.setPaymentStatus(PaymentStatus.PAID);
                    } else {
                        tradeService.setPaymentStatus(PaymentStatus.UNPAID);
                    }

                }
                //Added this section to set tradeService.paymentStatus to PAID whenever
                //a CASH LC DOMESTIC NEGOTATION  occurs
                if (tradeService.getDocumentClass().equals(DocumentClass.LC) && tradeService.getServiceType().equals(ServiceType.NEGOTIATION)
                        && tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && tradeService.getDocumentSubType1().equals(DocumentSubType1.CASH)) {
                    String settlementMode = (String) parameterMap.get("paymentMode");

                    PaymentInstrumentType settlementType = PaymentInstrumentType.valueOf(extractPaymentMode(settlementMode));

                    if (settlementType.equals(PaymentInstrumentType.MC_ISSUANCE)) {
                        tradeService.setPaymentStatus(PaymentStatus.PAID);
                    } else {
                        tradeService.setPaymentStatus(PaymentStatus.UNPAID);
                    }
                }
            }

            if (parameterMap.get("passOnRateConfirmedByCash") != null) {
                Map details = tradeService.getDetails();

                details.put("passOnRateConfirmedByCash", (String) parameterMap.get("passOnRateConfirmedByCash"));

                tradeService.updateDetails(details);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractPaymentMode(String paymentMode){
        if("DTR_LOAN".equalsIgnoreCase(paymentMode)){
            return "TR_LOAN";
        }else if("DUA_LOAN".equalsIgnoreCase(paymentMode)){
            return "UA_LOAN";
        }
        return paymentMode;
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside add settlement command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
