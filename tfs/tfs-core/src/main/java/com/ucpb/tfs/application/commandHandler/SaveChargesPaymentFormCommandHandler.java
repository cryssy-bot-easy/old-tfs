package com.ucpb.tfs.application.commandHandler;

/**
 *
 * @author Marvin Volante <marvin.volante@incuventure.net>
 *
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveChargesPaymentFormCommand;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentSavedEvent;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayable;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayableRepository;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveChargesPaymentFormCommandHandler implements CommandHandler<SaveChargesPaymentFormCommand> {

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    private PaymentRepository paymentRepository;

    @Inject
    SettlementAccountRepository settlementAccountRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    AccountsPayableRepository accountsPayableRepository;

    @Override
    public void handle(SaveChargesPaymentFormCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        TradeService tradeService = null;

        String username;
        UserActiveDirectoryId userActiveDirectoryId;

        if (command.getUserActiveDirectoryId() == null) {
            username = parameterMap.get("username").toString();
            userActiveDirectoryId = new UserActiveDirectoryId(username);
        } else {
            userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
        }

        if (((String) parameterMap.get("referenceType")).equals("ETS")) {
            // Load from repository using ETS number
            ServiceInstructionId etsNumber = new ServiceInstructionId((String) parameterMap.get("etsNumber"));
            tradeService = tradeServiceRepository.load(etsNumber);
        } else {
            tradeService = tradeServiceRepository.load(new TradeServiceId((String) parameterMap.get("tradeServiceId")));
        }

        Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
        if (payment == null) {
            payment = new Payment(tradeService.getTradeServiceId(), ChargeType.SERVICE);
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

        // Create payment
        System.out.println("angulo angulo angulo:" + parameterMap.get("chargesPaymentSummary"));

        List<Map<String, Object>> chargesPaymentListMap = (List<Map<String, Object>>) parameterMap.get("chargesPaymentSummary");

        Set<PaymentDetail> tempDetails = new HashSet<PaymentDetail>();
        for (Map<String, Object> chargesPaymentMap : chargesPaymentListMap) {

            String paymentMode = (String) chargesPaymentMap.get("paymentMode");

            PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(paymentMode);
            String referenceNumber = null;
            String referenceId = null;
            String accountName = null;

            BigDecimal amount = new BigDecimal(((String) chargesPaymentMap.get("amount")).trim());
            Currency settlementCurrency = Currency.getInstance((String) chargesPaymentMap.get("settlementCurrency"));

            // Rates
            String ratesString = (String) chargesPaymentMap.get("rates");
            HashMap<String, BigDecimal> temp = extractRates(ratesString);


            BigDecimal passOnRateThirdToUsd = temp.get("passOnRateThirdToUsd");
            BigDecimal passOnRateThirdToPhp = null;
            BigDecimal passOnRateUsdToPhp = temp.get("passOnRateUsdToPhp");
            BigDecimal specialRateThirdToUsd = temp.get("specialRateThirdToUsd");
            BigDecimal specialRateThirdToPhp = null;
            BigDecimal specialRateUsdToPhp = temp.get("specialRateUsdToPhp");
            BigDecimal urr = temp.get("urr");

            // Use this as there are no loan mode of payment in Service Charges.
            PaymentDetail tempDetail = null;

            switch (paymentInstrumentType) {
                case CASA:
                    referenceNumber = (String) chargesPaymentMap.get("accountNumber");
                    accountName = (String) chargesPaymentMap.get("accountName");

                    tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                            passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);
                           
                    tempDetail.setAccountName(accountName);

                    break;
                case AR:
                    referenceNumber = tradeService.getDocumentNumber().toString(); //(String) productPaymentMap.get("referenceId");//tradeService.getServiceInstructionId().toString(); //(String)chargesPaymentMap.get("")
                    tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, referenceId, amount, settlementCurrency,
                            passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);

                    break;
                case AP:
                    //  For Service charges, only AP and AR are used; MD is not.
                    AccountsPayable accountsPayable = accountsPayableRepository.load((String) chargesPaymentMap.get("referenceId"));

                    referenceNumber = accountsPayable.getSettlementAccountNumber().toString();
                    referenceId = (String) chargesPaymentMap.get("referenceId");

                    tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, referenceId, amount, settlementCurrency,
                            passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);

                    break;
                case CHECK:
                case CASH:
                case REMITTANCE:
                case IBT_BRANCH:
                    referenceNumber = (String) chargesPaymentMap.get("tradeSuspenseAccount");

                    tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency,
                            passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                            specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);
                    break;
//                case AR:
//                    // If AR, the referenceNumber is the documentNumber from the TradeService.
//                    // If does not exist, the AR should be created by TFS when this item is PAID.
//                    referenceNumber = tradeService.getDocumentNumber().toString();
//
//                    tempDetail = new PaymentDetail(paymentInstrumentType, referenceNumber, amount, settlementCurrency);
//                    break;
            }

            System.out.println("\n");
            System.out.println("paymentInstrumentType = " + paymentInstrumentType);
            System.out.println("referenceNumber = " + referenceNumber);
            System.out.println("referenceId = " + referenceId);
            System.out.println("amount = " + amount.toEngineeringString());
            System.out.println("settlementCurrency = " + settlementCurrency.toString());

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
        }
        System.out.println("\n");

        if (PaymentStatus.NO_PAYMENT_REQUIRED.equals(tradeService.getPaymentStatus())) {

            Boolean hasOtherPayments = Boolean.FALSE;

            Payment productPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
            Payment settlementPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
            Payment servicePayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);

            if (productPayment != null) {
                if (productPayment.getDetails() != null && productPayment.getDetails().size() > 0) {
                    hasOtherPayments = Boolean.TRUE;
                }
            }

            if (settlementPayment != null) {
                if (settlementPayment.getDetails() != null && settlementPayment.getDetails().size() > 0) {
                    hasOtherPayments = Boolean.TRUE;
                }
            }

            if (servicePayment != null) {
                if (servicePayment.getDetails() != null && servicePayment.getDetails().size() > 0) {
                    hasOtherPayments = Boolean.TRUE;
                }
            }

            if (!hasOtherPayments) {
                tradeService.setAsNoPaymentRequired();
            } else {
                tradeService.setPaymentStatus(PaymentStatus.UNPAID);
            }
        }

        // Persist payment

        // 1) Remove all items first
        payment.deleteAllPaymentDetails();
        // 2) Add new PaymentDetails
        payment.addNewPaymentDetails(tempDetails);
        // 3) Use saveOrUpdate
        paymentRepository.saveOrUpdate(payment);

        PaymentSavedEvent paymentSavedEvent = new PaymentSavedEvent(tradeService.getTradeServiceId(), payment, userActiveDirectoryId);
        eventPublisher.publish(paymentSavedEvent);
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
        System.out.println("inside save charges payment form command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
