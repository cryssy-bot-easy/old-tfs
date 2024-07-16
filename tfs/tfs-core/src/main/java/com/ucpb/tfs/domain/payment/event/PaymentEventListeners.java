package com.ucpb.tfs.domain.payment.event;

import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.service.AccountingService;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.domain.settlementaccount.*;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: IPCVal
 * Date: 9/10/12
 */
@Component
public class PaymentEventListeners {

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    MarginalDepositRepository marginalDepositRepository;

    @Inject
    AccountsPayableRepository accountsPayableRepository;

    @Inject
    AccountsReceivableRepository accountsReceivableRepository;

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    TradeProductRepository tradeProductRepository;

    @Inject
    ChargesService chargesService;

    @Autowired
    AccountingService accountingService;

    @EventListener
    public void itemPaid(PaymentItemPaidEvent paymentItemPaidEvent) {

        System.out.println("\n INSIDE itemPaid(PaymentItemPaidEvent)\n");

        try {

            PaymentInstrumentType paymentInstrumentType = paymentItemPaidEvent.getPaymentInstrumentType();
            SettlementAccountNumber settlementAccountNumber = new SettlementAccountNumber(paymentItemPaidEvent.getSettlementAccountNumber());
            String referenceNumber = paymentItemPaidEvent.getReferenceNumber();
            BigDecimal amount = paymentItemPaidEvent.getAmount();
            Currency currency = paymentItemPaidEvent.getCurrency();

            System.out.println("paymentItemPaidEvent.getTradeServiceId():" + paymentItemPaidEvent.getTradeServiceId() + "||");
            TradeService tradeService = tradeServiceRepository.load(paymentItemPaidEvent.getTradeServiceId());


            switch (paymentInstrumentType) {

                case CASA:
                    Casa casa = new Casa(settlementAccountNumber);
                    casa.debit(amount, currency, ReferenceType.CASA, referenceNumber);
                    break;

                case CASH:
                    Cash cash = new Cash(settlementAccountNumber);
                    cash.debit(amount, currency, ReferenceType.CASH, referenceNumber);
                    break;

                case CHECK:
                    Check check = new Check(settlementAccountNumber);
                    check.debit(amount, currency, ReferenceType.CHECK, referenceNumber);
                    break;

                case REMITTANCE:
                    Remittance remittance = new Remittance(settlementAccountNumber);
                    remittance.debit(amount, currency, ReferenceType.REMITTANCE, referenceNumber);
                    break;

                case IBT_BRANCH:
                    IbtBranch ibtBranch = new IbtBranch(settlementAccountNumber);
                    ibtBranch.debit(amount, currency, ReferenceType.IBT_BRANCH, referenceNumber);
                    break;
                case MD:
                    // If MD is used as payment, use ReferenceType.APPLY_MD
                    System.out.println("settlementAccountNumber > " + settlementAccountNumber);
                    MarginalDeposit md = marginalDepositRepository.load(settlementAccountNumber);
                    md.debit(amount, currency, ReferenceType.APPLY_MD, referenceNumber);
                    marginalDepositRepository.persist(md);
                    break;

                case AP:
                    AccountsPayable ap = null;
                    if (paymentItemPaidEvent.getReferenceId() != null)
                    ap = accountsPayableRepository.load(new SettlementAccountNumber(paymentItemPaidEvent.getReferenceNumber()), paymentItemPaidEvent.getReferenceId());
                    else 
                	ap = accountsPayableRepository.load(new SettlementAccountNumber(paymentItemPaidEvent.getReferenceNumber()));
                    	

                    ap.debit(amount, currency, ReferenceType.APPLY_AP, new Date(), paymentItemPaidEvent.getReferenceNumber(), null);

                    if (ap.getApOutstandingBalance().compareTo(BigDecimal.ZERO) < 1) {
                        ap.refundAccountsPayable();
                    }

                    accountsPayableRepository.persist(ap);
                    break;

                case AR:
                    AccountsReceivable ar = null;

                    Date bookingDate = new Date();

                    settlementAccountNumber = new SettlementAccountNumber(tradeService.getTradeProductNumber().toString());
                    DocumentClass documentClass = tradeService.getDocumentClass();
                    ServiceType serviceType = tradeService.getServiceType();
                    DocumentType documentType = tradeService.getDocumentType();
                    DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
                    DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();
                    String natureOfTransaction = null;
                    
                    if (tradeService.getDetails().get("natureOfTransaction") != null && !tradeService.getDetails().get("natureOfTransaction").toString().isEmpty()){
                    	natureOfTransaction = (String) tradeService.getDetails().get("natureOfTransaction");
                    } else if(containsNonLCDocumentClasses(documentClass)) {
                        natureOfTransaction = buildLastNonLCTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                    } else if(containsLCDocumentClasses(documentClass)) {
                        natureOfTransaction = buildLastLcTransactionString(serviceType, documentClass, documentType, documentSubType1, documentSubType2);
                    }

                    currency = paymentItemPaidEvent.getCurrency();
                    amount = paymentItemPaidEvent.getAmount();

                    ar = new AccountsReceivable(settlementAccountNumber,
                            currency,
                            (String) tradeService.getDetails().get("cifNumber"),
                            (String) tradeService.getDetails().get("cifName"),
                            (String) tradeService.getDetails().get("accountOfficer"),
                            (String) tradeService.getDetails().get("ccbdBranchUnitCode"),
                            bookingDate,
                            natureOfTransaction,
                            amount,
                            tradeService.getTradeServiceId());

                    ar.credit(amount, currency, ReferenceType.TFS_SETUP_AR, bookingDate, paymentItemPaidEvent.getReferenceNumber());
                    accountsReceivableRepository.persist(ar);

                    PaymentDetail detail = paymentItemPaidEvent.getPaymentDetail();
                    detail.setReferenceId(ar.getId());

                    paymentRepository.saveOrUpdate(detail);

                    break;
            }


            updateTradeServicePaymentStatus(tradeService, paymentItemPaidEvent.getCurrency());

            //Delete Charges
            if(DocumentClass.LC.equals(tradeService.getDocumentClass())
                    && DocumentType.DOMESTIC.equals(tradeService.getDocumentType())
                    && ServiceType.NEGOTIATION.equals(tradeService.getServiceType()) ){
                //Check if paymentSettlement and PaymentProduct
                Payment paymentService = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
                Payment paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                Payment paymentSettlement = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
                Boolean service = false;
                Boolean product = true;
                Boolean settlement = true;
                if(paymentService ==null){
                    System.out.println("NO PAYMENTSERVICE");
                    service = true;
                } else {
                    System.out.println( "PAYMENT SERVICE:"+paymentService );
                }
                if(paymentSettlement !=null){
                    Set<PaymentDetail> details = paymentSettlement.getDetails();
                    for(PaymentDetail paymentDetail : details){
                        if(!PaymentInstrumentType.CASA.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.MC_ISSUANCE.equals(paymentDetail.getPaymentInstrumentType())){
                            System.out.println("WITH CHARGE DUE TO SETTLEMENT"+ paymentDetail.getPaymentInstrumentType());
                            settlement = false;
                        }
                    }
                }
                if(paymentProduct !=null){
                    Set<PaymentDetail> details = paymentProduct.getDetails();
                    for(PaymentDetail paymentDetail : details){
                        if(!PaymentInstrumentType.CASA.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.CASH.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.REMITTANCE.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.MD.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.AP.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.AR.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.CHECK.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.APPLY_AP.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.APPLY_AR.equals(paymentDetail.getPaymentInstrumentType())
                                && !PaymentInstrumentType.IBT_BRANCH.equals(paymentDetail.getPaymentInstrumentType())){
                            product = false;
                            System.out.println("NO CHARGE DUE TO PRODUCT:"+paymentDetail.getPaymentInstrumentType());
                        } else {
                            System.out.println("WITH CHARGE DUE TO PRODUCT:"+paymentDetail.getPaymentInstrumentType());
                        }
                    }
                }

                if(product && service && settlement){
                    System.out.println("NO CHARGE TRANSACTION");
                    chargesService.removeCharges(tradeService.getTradeServiceId());
                } else {
                    System.out.println("WITH CHARGE TRANSACTION");
                }

            }


            Payment paymentProduct0 = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
            String withCilex ="N";
            if(paymentProduct0!=null){
                Set<PaymentDetail> details = paymentProduct0.getDetails();
                for(PaymentDetail paymentDetail : details){
                    if(!paymentDetail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
                        withCilex="Y";
                        System.out.println("WITH NON PHP MEANS WITH CILEX");
                    }
                }
            if(!withCilex.equalsIgnoreCase("Y")){
                //remove cilex.
                System.out.println("WITH NON PHP MEANS WITH CILEX");
                chargesService.removeCilex(tradeService.getTradeServiceId());
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception when handling PaymentEventListener",e);
        }
    }

    @EventListener
    public void itemPaymentReversed(PaymentItemPaymentReversedEvent paymentItemPaymentReversedEvent) {

        System.out.println("\n INSIDE itemPaymentReversed(PaymentItemPaymentReversedEvent)\n");

        try {

            PaymentInstrumentType paymentInstrumentType = paymentItemPaymentReversedEvent.getPaymentInstrumentType();
            SettlementAccountNumber settlementAccountNumber = new SettlementAccountNumber(paymentItemPaymentReversedEvent.getSettlementAccountNumber());
            String referenceNumber = paymentItemPaymentReversedEvent.getReferenceNumber();
            BigDecimal amount = paymentItemPaymentReversedEvent.getAmount();
            Currency currency = paymentItemPaymentReversedEvent.getCurrency();

            // For loans
            Currency bookingCurrency = paymentItemPaymentReversedEvent.getBookingCurrency();
            BigDecimal interestRate = paymentItemPaymentReversedEvent.getInterestRate();
            String interestTerm = paymentItemPaymentReversedEvent.getInterestTerm();
            String repricingTerm = paymentItemPaymentReversedEvent.getRepricingTerm();
            String repricingTermCode = paymentItemPaymentReversedEvent.getRepricingTerm();
            String loanTerm = paymentItemPaymentReversedEvent.getLoanTerm();
            String loanTermCode = paymentItemPaymentReversedEvent.getLoanTermCode();
            Date loanMaturityDate = paymentItemPaymentReversedEvent.getLoanMaturityDate();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

            TradeService tradeService = tradeServiceRepository.load(paymentItemPaymentReversedEvent.getTradeServiceId());

            switch (paymentInstrumentType) {

                // TODO: Reverse payment (handles both reversal and error correct)

                case CASA:
                    break;

                case CASH:
                    break;

                case CHECK:
                    break;

                case REMITTANCE:
                    break;

                case IBT_BRANCH:
                    break;

                case TR_LOAN:
                    break;

                case IB_LOAN:
                    break;

                case UA_LOAN:
                    break;

                case MD:
                    MarginalDeposit md = marginalDepositRepository.load(settlementAccountNumber);
                    md.credit(amount, currency, ReferenceType.APPLY_MD, referenceNumber);
                    marginalDepositRepository.persist(md);
                    break;

                case AP:

                    AccountsPayable ap = accountsPayableRepository.load(paymentItemPaymentReversedEvent.getReferenceId());

                    ap.credit(amount, currency, ReferenceType.APPLY_AP, new Date(), paymentItemPaymentReversedEvent.getReferenceNumber());

                    if (ap.getApOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
                        ap.openAccountsPayable();
                    }

                    accountsPayableRepository.persist(ap);
                    break;

                case AR:
                    AccountsReceivable ar = accountsReceivableRepository.load(paymentItemPaymentReversedEvent.getReferenceId());
                    ar.closeAccountsReceivable();
                    accountsReceivableRepository.persist(ar);
                    break;
            }


            updateTradeServicePaymentStatus(tradeService, paymentItemPaymentReversedEvent.getCurrency());

            if(tradeService.getDocumentClass().equals(DocumentClass.CDT)
                    && tradeService.getServiceType().equals(ServiceType.PAYMENT)){
                //for reversal delete accounting entry localizing for CDT only
                System.out.println("CDT Payment Reversal tradeService.getTradeServiceId():"+tradeService.getTradeServiceId());
                accountingService.deleteActualEntries(tradeService.getTradeServiceId());
            } else {
                System.out.println("ALL Payment Reversal tradeService.getTradeServiceId():"+tradeService.getTradeServiceId());
//                accountingService.deleteActualEntries(tradeService.getTradeServiceId());
            }

            if (paymentItemPaymentReversedEvent.getReversal()) {
                System.out.println("this is a reversal");
                TradeService reversalTradeService = tradeServiceRepository.load(paymentItemPaymentReversedEvent.getReversalTradeServiceId());
                checkForReversals(tradeService, reversalTradeService);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception when handling PaymentEventListener reverse",e);
        }
    }

    private void checkForReversals(TradeService tradeService, TradeService reversalTradeService) {

        Boolean allUnpaid = true;

        List<Payment> payments = paymentRepository.loadAllPayment(tradeService.getTradeServiceId());

        for (Payment p : payments) {

            for (PaymentDetail paymentDetail : p.getDetails()) {
                if (paymentDetail.getStatus().equals(PaymentStatus.PAID)) {
                    allUnpaid = false;
                    break;
                }
            }

            if (allUnpaid) {
                break;
            }
        }

        if (allUnpaid) {
            System.out.println("ALL PAID " + tradeService.getTradeServiceId().toString());
            System.out.println("ALL PAID revesal: " + reversalTradeService.getTradeServiceId().toString());
            reversalTradeService.paid();
            tradeServiceRepository.update(reversalTradeService);
        }
    }


    @EventListener
    public void lcCurrencyChanged(PaymentLcCurrencyChangedEvent paymentLcCurrencyChangedEvent) {

        ServiceInstructionId serviceInstructionId = paymentLcCurrencyChangedEvent.getServiceInstructionId();

        TradeService tradeService = tradeServiceRepository.load(serviceInstructionId);

        Payment productPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
        Payment serviceChargesPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);

        productPayment.deleteAllPaymentDetails();
        serviceChargesPayment.deleteAllPaymentDetails();

        paymentRepository.saveOrUpdate(productPayment);
        paymentRepository.saveOrUpdate(serviceChargesPayment);
    }

    private void updateTradeServicePaymentStatus(TradeService tradeService, Currency paymentCurrency) {

//        Payment paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
//        Payment paymentServiceCharges = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
//
//        Boolean isProductPaymentCurrencyNonPhp = Boolean.FALSE;
//        if (paymentProduct != null) {
//            isProductPaymentCurrencyNonPhp = paymentProduct.checkIfPaymentCurrenciesIsNonPhp();
//        }
//
//        BigDecimal totalServiceChargesAmount = tradeService.getTotalServiceChargesAmount(isProductPaymentCurrencyNonPhp); //this is for all products where cilex is only charged if there is a non php payment
//
//        Boolean isProductPaymentDTRLOAN = Boolean.FALSE;
//        if (paymentProduct != null &&
//                (
//                        (tradeService.getDocumentClass().equals(DocumentClass.DP) || tradeService.getDocumentClass().equals(DocumentClass.DA)
//                                || tradeService.getDocumentClass().equals(DocumentClass.OA) || tradeService.getDocumentClass().equals(DocumentClass.DR))
//                                && tradeService.getDocumentType() != null && tradeService.getDocumentType().equals(DocumentType.DOMESTIC)
//                )
//                ) {//This is for non lc where Docstamps will only be charged if
//            isProductPaymentDTRLOAN = paymentProduct.hasTrLoan();
//            totalServiceChargesAmount = tradeService.getTotalServiceChargesAmount(isProductPaymentCurrencyNonPhp, isProductPaymentDTRLOAN);
//        }
//
//
//        BigDecimal productChargeAmount = tradeService.getProductChargeAmount();
//
//
//        if (productChargeAmount != null) {
//            System.out.println("\n>>>>>>>>>>>>>>>>>>>>> PRODUCT CHARGE = " + productChargeAmount.toPlainString());
//        } else {
//            System.out.println("\n>>>>>>>>>>>>>>>>>>>>> There was no PRODUCT CHARGE for this TradeService.");
//        }
//        if (totalServiceChargesAmount != null) {
//            System.out.println(">>>>>>>>>>>>>>>>>>>>> SERVICE CHARGES = " + totalServiceChargesAmount.toPlainString() + "\n");
//        } else {
//            System.out.println("\n>>>>>>>>>>>>>>>>>>>>> There were no SERVICE CHARGES for this TradeService.");
//        }
//
//        Boolean isProductPaid = Boolean.FALSE;
//        if (productChargeAmount != null && paymentProduct != null) {
//            if (paymentProduct.getTotalPaid(tradeService.getProductChargeCurrency()).compareTo(productChargeAmount) >= 0) {
//                isProductPaid = Boolean.TRUE;
//                System.out.println("\n########################## PRODUCT CHARGE WAS PAID!\n");
//            } else {
//                System.out.println("\n########################## PRODUCT CHARGE IS UNPAID!\n");
//                // throw Exception?
//            }
//        } else {
//            // No Product Charge
//            isProductPaid = Boolean.TRUE;
//        }
//
//        Boolean isServiceChargesPaid = Boolean.FALSE;
//        if (totalServiceChargesAmount != null && paymentServiceCharges != null) {
//            // Target currency for service charges is always PHP
//            // if (paymentServiceCharges.getTotalPaid(Currency.getInstance("PHP")).compareTo(totalServiceChargesAmount) >= 0) {
//            if (paymentServiceCharges.                                         (tradeService.getServiceChargesCurrency()).compareTo(totalServiceChargesAmount) >= 0) {
//                isServiceChargesPaid = Boolean.TRUE;
//                System.out.println("\n########################## TOTAL SERVICE CHARGES WERE FULLY PAID!\n");
//            } else {
//                System.out.println("\n########################## TOTAL SERVICE CHARGES ARE NOT FULLY PAID!\n");
//                // throw Exception?
//            }
//        } else {
//            // No Service Charges
//            isServiceChargesPaid = Boolean.TRUE;
//        }
//
//        ServiceType serviceType = tradeService.getServiceType();  // OPENING, NEGOTIATION, ETC.
//        DocumentClass documentClass = tradeService.getDocumentClass();  // LC, INDEMNITY, ETC.
//        DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1(); // REGULAR, SIGHT
//
//        switch (serviceType) {
//
//            case OPENING:
//
//                if (documentClass.equals(DocumentClass.LC)) {
//                    switch (documentSubType1) {
//                        case CASH:
//                            if (isProductPaid && isServiceChargesPaid) {
//                                tradeService.paid();
//                            } else {
//                                tradeService.unPay();
//                            }
//                            break;
//                        case REGULAR:
//                        case STANDBY:
//                            if (isServiceChargesPaid) {
//                                tradeService.paid();
//                            } else {
//                                tradeService.unPay();
//                            }
//                            break;
//                    }
//                }
//                break;
//
//            case NEGOTIATION:
//
//                if (documentClass.equals(DocumentClass.LC)) {
//
//                    // 1. If negotiationAmount < outstanding cashAmount: Only Service Charges must be paid.
//                    // 2. If negotiationAmount > outstanding cashAmount, Both Product (the excess) and Service Charges
//                    //    must be paid.
//
//                    DocumentNumber lcNumber = new DocumentNumber((String) tradeService.getDetails().get("lcNumber"));
//                    System.out.println("\nlcNumber = " + lcNumber.toString() + "\n");
//
//                    LetterOfCredit lc = (LetterOfCredit) tradeProductRepository.load(lcNumber);
//
//                    BigDecimal outstandingApCashAmount = lc.getOutstandingApCashAmount();
//
//                    System.out.println("\noutstandingApCashAmount = " + outstandingApCashAmount.toPlainString() + "\n");
//
//                    BigDecimal negotiatedAmount = new BigDecimal((String) tradeService.getDetails().get("negotiationAmount"));
//
//                    if (negotiatedAmount.compareTo(outstandingApCashAmount) <= 0) {
//                        if (isServiceChargesPaid) {
//                            tradeService.paid();
//                        } else {
//                            tradeService.unPay();
//                        }
//                    } else if (negotiatedAmount.compareTo(outstandingApCashAmount) > 0) {
//                        if (isProductPaid && isServiceChargesPaid) {
//                            tradeService.paid();
//                        } else {
//                            tradeService.unPay();
//                        }
//                    }
//                }
//                break;
//
//            case ADJUSTMENT:
//
//                if (documentClass.equals(DocumentClass.LC)) {
//
//                    // Only REGULAR-to-CASH adjustment has Service Charges (and Product Charge, of course).
//                    // The Product Charge is the cash component.
//                    String partialCashSettlementFlag = (String) tradeService.getDetails().get("partialCashSettlementFlag");
//
//                    if (partialCashSettlementFlag != null && partialCashSettlementFlag.equals("partialCashSettlementEnabled")) {
//                        if (isProductPaid && isServiceChargesPaid) {
//                            tradeService.paid();
//                        } else {
//                            tradeService.unPay();
//                        }
//                    }
//                }
//                break;
//
//            case AMENDMENT:
//
//                if (documentClass.equals(DocumentClass.LC)) {
//
//                    switch (documentSubType1) {
//
//                        case CASH:
//
//                            if (tradeService.getDetails().get("amountSwitch") != null && (!((String) tradeService.getDetails().get("amountSwitch")).equals("")) && (!((String) tradeService.getDetails().get("amountSwitch")).toLowerCase().equals("off"))) {
//
//                                BigDecimal outstandingBalance = new BigDecimal((String) tradeService.getDetails().get("outstandingBalance"));
//                                BigDecimal amountTo = new BigDecimal((String) tradeService.getDetails().get("amountTo"));
//
//                                if (amountTo.compareTo(outstandingBalance) > 0) {
//
//                                    // Check for Product and Service Charges here ONLY
//                                    if (isProductPaid && isServiceChargesPaid) {
//                                        tradeService.paid();
//                                    } else {
//                                        tradeService.unPay();
//                                    }
//
//                                } else {
//
//                                    if (isServiceChargesPaid) {
//                                        tradeService.paid();
//                                    } else {
//                                        tradeService.unPay();
//                                    }
//                                }
//
//                            } else {
//
//                                if (isServiceChargesPaid) {
//                                    tradeService.paid();
//                                } else {
//                                    tradeService.unPay();
//                                }
//                            }
//                            break;
//
//                        case REGULAR:
//                        case STANDBY:
//                            if (isServiceChargesPaid) {
//                                tradeService.paid();
//                            } else {
//                                tradeService.unPay();
//                            }
//                            break;
//                    }
//                }
//                break;
//
//            case CANCELLATION:
//                if (documentClass.equals(DocumentClass.INDEMNITY)) {
//                    // For TSD-initiated cancellation only:
//                    // ETS Number is null and TradeServiceReferenceNumber is not null
//                    if (tradeService.getServiceInstructionId() == null && tradeService.getTradeServiceReferenceNumber() != null) {
//                        if (isServiceChargesPaid) {
//                            tradeService.paid();
//                        } else {
//                            tradeService.unPay();
//                        }
//                    }
//                }
//                break;
//
//            case COLLECTION:
//                if (documentClass.equals(DocumentClass.MD)) {
//                    if (paymentProduct.getStatus().equals(PaymentStatus.PAID)) {
//                        tradeService.paid();
//                    } else {
//                        tradeService.unPay();
//                    }
//                }
//                break;
//
//            case ISSUANCE:
//                if (documentClass.equals(DocumentClass.INDEMNITY)) {
//                    if (isServiceChargesPaid) {
//                        tradeService.paid();
//                    } else {
//                        tradeService.unPay();
//                    }
//                }
//                break;
//            case PAYMENT:
//                if (documentClass.equals(DocumentClass.CDT)) {
//                    if (isProductPaid) {
//                        tradeService.paid();
//                    } else {
//                        tradeService.unPay();
//                    }
//                }
//                break;
//
//            //added by Arvin
//            case SETTLEMENT:
//                if (documentClass.equals(DocumentClass.DA) || documentClass.equals(DocumentClass.DP) || documentClass.equals(DocumentClass.OA) || documentClass.equals(DocumentClass.DR)) {
//                    System.out.println("<><><><><><><><><>SETTLEMENT<><><><><><><><>");
//                    if (isProductPaid && isServiceChargesPaid) {
//                        tradeService.paid();
//                        System.out.println("<><><><><><><><><>SETTLEMENT PAID<><><><><><><><>");
//                    } else {
//                        tradeService.unPay();
//                        System.out.println("<><><><><><><><><>SETTLEMENT UNPAID<><><><><><><><>");
//                    }
//                }
//                break;
//        }

        if (!PaymentStatus.NO_PAYMENT_REQUIRED.equals(tradeService.getPaymentStatus())) {
        	System.out.println("(isTradeServiceFullyPaid(tradeService.getTradeServiceId())): " + (isTradeServiceFullyPaid(tradeService.getTradeServiceId())));
            if (isTradeServiceFullyPaid(tradeService.getTradeServiceId())) {
                tradeService.paid();
            } else {
                tradeService.unPay();
            }
            tradeServiceRepository.merge(tradeService);
        }
    }

    /**
     * This was designed to do 3 things:
     * 1. Insert TR Loan Amount due to saving a negotiation/settlement tab
     * 2. Update productChargeAmountNetOfPesoAmountPaid used charges computation due to  due to saving a negotiation/settlement tab
     * 3. Update the creationExchangeRate thus modifying how the productAmount base is calculated for charges computation.
     *
     * @param paymentSavedEvent
     */
    @EventListener
    public void updateTradeServiceDetailsWithTRLoanAmountOrCilexAmount(PaymentSavedEvent paymentSavedEvent) {
        System.out.println("in updateTradeServiceDetailsWithTRLoanAmountOrCilexAmount");

        TradeService referenceTradeService = tradeServiceRepository.load(paymentSavedEvent.getTradeServiceId());

        // disable executing this listener in refund
        if (!referenceTradeService.getServiceType().equals(ServiceType.REFUND)) {
            Payment savedPayment = paymentRepository.get(paymentSavedEvent.getTradeServiceId(), paymentSavedEvent.getPayment().getChargeType());

            System.out.println("CHARGE TYPE: " + savedPayment.getChargeType());
            if (savedPayment.getChargeType().equals(ChargeType.PRODUCT)) {
                TradeServiceId tradeserviceId = paymentSavedEvent.getTradeServiceId();
                UserActiveDirectoryId userActiveDirectoryId = paymentSavedEvent.getUserActiveDirectoryId();

                TradeService tradeService = tradeServiceRepository.load(tradeserviceId);

                if (tradeService != null) {
                    Map<String, Object> details = tradeService.getDetails();
                    Payment paymentProduct;
                    if (paymentSavedEvent.getPayment().getChargeType().equals(ChargeType.PRODUCT)) {
                        paymentProduct = savedPayment;
                    } else {
                        paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
                    }


                    if (paymentProduct != null) {
                        //Determine how product was paid using product payment and use this to compute PHP Base Amount
                        // THIRD settled with THIRD will simply use THIRD-USD Sell Rate and USD-PHP urr
                        // USD settled with USD will used USD-PHP urr
                        // THIRD settled with USD will use THIRD to USD Sell Rate and USD-PHP urr
                        // Multiple currency settlement of THIRD will use THIRD to USD Sell Rate and USD to PHP Sell Rate
                        // Multiple currency settlement of USD will use USD to PHP Sell Rate

                        Set<PaymentDetail> paymentDetails = paymentProduct.getDetails();
                        Boolean multiplePaymentCurrency = Boolean.FALSE;
                        Currency tCurrency = null;
                        for (PaymentDetail paymentDetail : paymentDetails) {
                            Currency curr = paymentDetail.getCurrency();
                            if (tCurrency == null) {
                                tCurrency = curr;
                            } else if (!tCurrency.equals(curr)) {
                                multiplePaymentCurrency = Boolean.TRUE;
                            }
                        }
                        //Get Currency of Product
                        String productCurrency = "";
                        if (tradeService.getDetails().containsKey("currency")) {
                            productCurrency = (String) tradeService.getDetails().get("currency");
                        } else if (tradeService.getDetails().containsKey("negotiationCurrency")) {
                            productCurrency = (String) tradeService.getDetails().get("negotiationCurrency");
                        } else {
                            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++  no product currency");
                            if(tradeService.getDocumentClass().equals(DocumentClass.CDT) && tradeService.getServiceType().equals(ServiceType.PAYMENT)){
                            	productCurrency = "PHP";
                            }
                        }


                        //TODO: Check the difference between the two
                        if (tCurrency != null && productCurrency != null
                                && (tCurrency.getCurrencyCode().equalsIgnoreCase("USD")
                                || !(tCurrency.getCurrencyCode().equalsIgnoreCase("USD") && tCurrency.getCurrencyCode().equalsIgnoreCase("PHP")) //THIRD
                        )
                                && (productCurrency.equalsIgnoreCase("USD")
                                || !(productCurrency.equalsIgnoreCase("USD") && productCurrency.equalsIgnoreCase("PHP")) //THIRD
                        )
                                && multiplePaymentCurrency.equals(Boolean.FALSE)) {
                            //This means that the product currency is equal to product payment currency
                            // and that there is only one currency involved

                            //Set creation exchange rate here
                            //Get THIRD-USD Sell Rate
                            //Get USD-PHP Sell Rate
                            //Get USD-PHP URR

                            //Check if THIRD or USD
                            // if THIRD use THIRD to USD sell rate and USD to PHP sell rate
                            // if USD use USD to PHP sell rate
                            BigDecimal creationExchangeRate = BigDecimal.ZERO;
                            if (!productCurrency.equalsIgnoreCase("USD") && !productCurrency.equalsIgnoreCase("PHP")) {
                                //THIRD

                                if (tradeService.getDetails().containsKey(productCurrency + "-USD_special_rate_cash")
                                        && tradeService.getDetails().containsKey("urr")
                                        ) {
                                    String tmp00 = (String) tradeService.getDetails().get(productCurrency + "-USD_special_rate_cash");
                                    String tmp01 = (String) tradeService.getDetails().get("urr");
                                    System.out.println(productCurrency + "-USD_special_rate_cash:" + tmp00);
                                    System.out.println("USD-PHP_special_rate_cash:" + tmp01);
                                    creationExchangeRate = new BigDecimal(tmp01).multiply(new BigDecimal(tmp00));
                                } else if (tradeService.getDetails().containsKey(productCurrency + "-USD")
                                        && tradeService.getDetails().containsKey("urr")) {
                                    String tmp00 = (String) tradeService.getDetails().get(productCurrency + "-USD");
                                    String tmp01 = (String) tradeService.getDetails().get("urr");
                                    System.out.println(productCurrency + "-USD:" + tmp00);
                                    System.out.println("urr:" + tmp01);
                                    creationExchangeRate = new BigDecimal(tmp01).multiply(new BigDecimal(tmp00));
                                } else if (tradeService.getDetails().containsKey(productCurrency + "-USD_special_rate_charges")
                                        && tradeService.getDetails().containsKey("urr")) {
                                    String tmp00 = (String) tradeService.getDetails().get(productCurrency + "-USD_special_rate_charges");
                                    String tmp01 = (String) tradeService.getDetails().get("urr");
                                    System.out.println(productCurrency + "-PHP_special_rate_charges:" + tmp00);
                                    System.out.println("urr:" + tmp01);
                                    creationExchangeRate = new BigDecimal(tmp00).multiply(new BigDecimal(tmp01));
                                } else if (tradeService.getDetails().containsKey(productCurrency + "-USD_special_rate")
                                        && tradeService.getDetails().containsKey("urr")) {
                                    String tmp00 = (String) tradeService.getDetails().get(productCurrency + "-USD_special_rate");
                                    String tmp01 = (String) tradeService.getDetails().get("urr");
                                    System.out.println(productCurrency + "-USD_special_rate" + tmp00);
                                    System.out.println("urr:" + tmp01);
                                    creationExchangeRate = new BigDecimal(tmp01).multiply(new BigDecimal(tmp00));
                                }


                            } else if (productCurrency.equalsIgnoreCase("USD")) {
                                //USD
                                if (tradeService.getDetails().containsKey("USD-PHP") && !tradeService.getDetails().get("USD-PHP").toString().equalsIgnoreCase("USD-PHP")) {
                                    String tmp = (String) tradeService.getDetails().get("USD-PHP");
                                    System.out.println("USD-PHP:" + tmp);
                                    creationExchangeRate = new BigDecimal(tmp);
                                } else if (tradeService.getDetails().containsKey("USD-PHP_special_rate_cash")) {
                                    String tmp = (String) tradeService.getDetails().get("USD-PHP_special_rate_cash");
                                    System.out.println("USD-PHP_special_rate_cash:" + tmp);
                                    creationExchangeRate = new BigDecimal(tmp);
                                } else if (tradeService.getDetails().containsKey("USD-PHP_special_rate_charges")) {
                                    String tmp = (String) tradeService.getDetails().get("USD-PHP_special_rate_charges");
                                    System.out.println("USD-PHP_special_rate_charges:" + tmp);
                                    creationExchangeRate = new BigDecimal(tmp);
                                } else if (tradeService.getDetails().containsKey("USD-PHP_special_rate")) {
                                    String tmp = (String) tradeService.getDetails().get("USD-PHP_special_rate");
                                    System.out.println("USD-PHP_special_rate_charges:" + tmp);
                                    creationExchangeRate = new BigDecimal(tmp);
                                }

                            } else {
                                //PHP
                                creationExchangeRate = BigDecimal.ONE;
                            }

                            System.out.println("creationExchangeRateOLD::" + (String) tradeService.getDetails().get("creationExchangeRate"));
                            System.out.println("creationExchangeRateNEW::" + creationExchangeRate.toPlainString());
                            tradeService.getDetails().put("creationExchangeRate", creationExchangeRate.toPlainString());

                        } else {
                            //NEW::if either multi currency or in PHP
                            //Check if THIRD or USD
                            // if THIRD use THIRD to USD sell rate and USD to PHP sell rate
                            // if USD use USD to PHP sell rate
                            BigDecimal creationExchangeRate = BigDecimal.ZERO;
                            if (!productCurrency.equalsIgnoreCase("USD") && !productCurrency.equalsIgnoreCase("PHP")) {
                                if (tradeService.getDetails().containsKey(productCurrency + "-USD_special_rate")) {
                                    //THIRD
                                    String tmp00 = (String) tradeService.getDetails().get(productCurrency + "-USD_special_rate");
                                    String tmp01 = (String) tradeService.getDetails().get("USD-PHP_special_rate");
                                    creationExchangeRate = new BigDecimal(tmp00).multiply(new BigDecimal(tmp01));
                                } else if (tradeService.getDetails().containsKey(productCurrency + "-USD_special_rate_cash")) {
                                    //THIRD
                                    String tmp00 = (String) tradeService.getDetails().get(productCurrency + "-USD_special_rate_cash");
                                    String tmp01 = (String) tradeService.getDetails().get("USD-PHP_special_rate_cash");
                                    creationExchangeRate = new BigDecimal(tmp00).multiply(new BigDecimal(tmp01));
                                } else {
                                    //THIRD
                                    String tmp00 = (String) tradeService.getDetails().get(productCurrency + "-USD");
                                    String tmp01 = (String) tradeService.getDetails().get("USD-PHP");
                                    creationExchangeRate = new BigDecimal(tmp00).multiply(new BigDecimal(tmp01));
                                }

                            } else if (productCurrency.equalsIgnoreCase("USD")) {
                                if (tradeService.getDetails().containsKey("USD-PHP_special_rate")) {
                                    //USD
                                    String tmp01 = (String) tradeService.getDetails().get("USD-PHP_special_rate");
                                    creationExchangeRate = new BigDecimal(tmp01);
                                } else if (tradeService.getDetails().containsKey("USD-PHP_special_rate_cash")) {
                                    //USD
                                    String tmp01 = (String) tradeService.getDetails().get("USD-PHP_special_rate_cash");
                                    creationExchangeRate = new BigDecimal(tmp01);
                                } else {
                                    //USD
                                    String tmp00 = (String) tradeService.getDetails().get("USD-PHP");
                                    creationExchangeRate = new BigDecimal(tmp00);
                                }
//                            //USD
//                            String tmp = (String) tradeService.getDetails().get("USD-PHP");
//                            creationExchangeRate = new BigDecimal(tmp);
                                tradeService.getDetails().put("creationExchangeRateUsdToPHPSpecialRate", creationExchangeRate.toPlainString());
                            } else {
                                //PHP
                                creationExchangeRate = BigDecimal.ONE;
                            }

                            System.out.println("creationExchangeRateOLD::" + (String) tradeService.getDetails().get("creationExchangeRate"));
                            System.out.println("creationExchangeRateNEW::" + creationExchangeRate.toPlainString());
                            tradeService.getDetails().put("creationExchangeRate", creationExchangeRate.toPlainString());

                        }

                        if (tradeService.getDocumentClass().equals(DocumentClass.DA)
                                || tradeService.getDocumentClass().equals(DocumentClass.DP)
                                || tradeService.getDocumentClass().equals(DocumentClass.OA)
                                || tradeService.getDocumentClass().equals(DocumentClass.DR)) {
                            //Payment payment = paymentSavedEvent.getPayment();
                            PaymentDetail trPaymentDetail = savedPayment.getTRLoanPayment();


                            if (trPaymentDetail != null) {
                                BigDecimal amount = trPaymentDetail.getAmount();
                                BigDecimal phpAmount = BigDecimal.ZERO;
                                Currency currency = trPaymentDetail.getCurrency();

                                if (currency.toString().equalsIgnoreCase("PHP")) {
                                    phpAmount = amount;
                                    details.put("TR_LOAN_AMOUNT", phpAmount);
                                } else if (currency.toString().equalsIgnoreCase("USD")) {
                                    if (trPaymentDetail.getUrr() != null) {
                                        // Use urr for USD
                                        phpAmount = trPaymentDetail.getUrr().multiply(amount);
                                    }

                                    details.put("TR_LOAN_AMOUNT", phpAmount);
                                } else if (!currency.toString().equalsIgnoreCase("USD") && !currency.toString().equalsIgnoreCase("PHP")) {
                                    //Uses urr
                                    if (trPaymentDetail.getSpecialRateThirdToUsd() != null
                                            && trPaymentDetail.getUrr() != null) {
                                        phpAmount = trPaymentDetail.getSpecialRateThirdToUsd().multiply(trPaymentDetail.getUrr()).multiply(amount);
                                    } else {
                                        phpAmount = trPaymentDetail.getPassOnRateThirdToUsd().multiply(trPaymentDetail.getUrr()).multiply(amount);
                                    }

                                    details.put("TR_LOAN_AMOUNT", phpAmount);
                                }
                                System.out.println("TR_LOAN_AMOUNT" + phpAmount);
                            }

                            //BigDecimal pesoAmountPaid = paymentProduct.getTotalPaid(Currency.getInstance("PHP"));

                        } else {
                            //Place holder for LC related changes that need to be done
                            if (details.get("documentClass").equals("LC")) {

                            }
                        }

                        if (!tradeService.getDocumentClass().equals(DocumentClass.MD)) {
                            BigDecimal usdAmountPaid = paymentProduct.getTotalPrePaymentWithCurrency("USD");
                            if (usdAmountPaid == null || usdAmountPaid.compareTo(BigDecimal.ZERO) != 1) {
                                usdAmountPaid = BigDecimal.ZERO;
                            }
                            System.out.println("usdAmountPaid:" + usdAmountPaid);
                            BigDecimal thirdAmountPaidInUsd = BigDecimal.ZERO;
                            if (!"USD".equalsIgnoreCase(productCurrency) && !"PHP".equalsIgnoreCase(productCurrency)) {
                                BigDecimal thirdAmountPaid = paymentProduct.getTotalPrePaymentWithCurrency(productCurrency);
                                System.out.println("thirdAmountPaid:" + thirdAmountPaid);
                                BigDecimal conversionRateFromPrepaymentWithCurrency = paymentProduct.getTotalPrePaymentWithCurrencySellRate(productCurrency);
                                System.out.println("conversionRateFromPrepaymentWithCurrency:" + conversionRateFromPrepaymentWithCurrency);
                                if (conversionRateFromPrepaymentWithCurrency.compareTo(BigDecimal.ZERO) == 0) {
                                    conversionRateFromPrepaymentWithCurrency = BigDecimal.ONE;
                                }
                                thirdAmountPaidInUsd = thirdAmountPaid.multiply(conversionRateFromPrepaymentWithCurrency);
                                System.out.println("thirdAmountPaidInUsd:" + thirdAmountPaidInUsd);
                                if (thirdAmountPaidInUsd == null || thirdAmountPaidInUsd.compareTo(BigDecimal.ZERO) != 1) {
                                    thirdAmountPaidInUsd = BigDecimal.ZERO;
                                }
                            }

                            BigDecimal nonPhpPaidInUsd = thirdAmountPaidInUsd.add(usdAmountPaid);
                            System.out.println("nonPhpPaidInUsd:" + nonPhpPaidInUsd);
                            BigDecimal productChargeAmountNetOfPesoAmountPaid = BigDecimal.ZERO;
                            if (nonPhpPaidInUsd != null) {
                                System.out.println(productCurrency);
                                BigDecimal urr = paymentProduct.getPaymentUrr();
                                productChargeAmountNetOfPesoAmountPaid = nonPhpPaidInUsd.multiply(urr);
                                System.out.println("productChargeAmountNetOfPesoAmountPaid:" + productChargeAmountNetOfPesoAmountPaid);
                                details.put("productChargeAmountNetOfPesoAmountPaid", productChargeAmountNetOfPesoAmountPaid);
                            } else {
                                details.put("productChargeAmountNetOfPesoAmountPaid", BigDecimal.ZERO);
                            }
                            tradeService.updateDetails(details, userActiveDirectoryId);

                            // a trade service item was modified, we call the service to delete old charges
                            // commented this one out. causing error in saving cash lc payment in dm
                            //tradeServiceRepository.deleteServiceCharges(tradeService.getTradeServiceId());
                            tradeService.getDetails().put("chargesOverridenFlag", "N");

                            //FOR DM_LC_REGULAR_SIGHT NO CHARGES
                            String trLoanFlag = "N";
                            PaymentDetail trPaymentDetail2 = savedPayment.getTRLoanPayment();
                            if (trPaymentDetail2 != null) {
                                trLoanFlag = "Y";
                            }

                            if(tradeService.getDocumentClass().equals(DocumentClass.LC)
                                    && tradeService.getDocumentType().equals(DocumentType.DOMESTIC)
                                    && tradeService.getDocumentSubType1().equals(DocumentSubType1.STANDBY)
                                    && tradeService.getDocumentSubType2().equals(DocumentSubType2.SIGHT)
                                    && tradeService.getServiceType().equals(ServiceType.NEGOTIATION)
                                    && trLoanFlag.equalsIgnoreCase("N")){
                                // we add charges to it
                                chargesService.applyNoCharges(tradeService);
                            } else {
                                // we add charges to it
                                chargesService.applyCharges(tradeService, details);
                            }


                            tradeServiceRepository.merge(tradeService);
                            System.out.println("Persisted TradeService!");
                        }

                    }
                }
            } else if (savedPayment.getChargeType().equals(ChargeType.SETTLEMENT)) {
                System.out.println("clearing pddts and mt103 details from proceeds to beneficiary");
                //Payment savedPayment = paymentSavedEvent.getPayment();
                System.out.println("CHARGE TYPE: " + savedPayment.getChargeType());

                System.out.println();
                Boolean doNotResetChargesBecauseItIsMC_Issuance=false;
                //FLAG used tosignifyy if charges woill have to be reset.
                Set<PaymentDetail> paymentDetails = savedPayment.getDetails();
                for (PaymentDetail paymentDetail : paymentDetails){
                    if(paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.MC_ISSUANCE)){
                        doNotResetChargesBecauseItIsMC_Issuance = true;
                    }
                }

                TradeServiceId tradeserviceId = paymentSavedEvent.getTradeServiceId();

                TradeService tradeService = tradeServiceRepository.load(tradeserviceId);

                UserActiveDirectoryId userActiveDirectoryId = paymentSavedEvent.getUserActiveDirectoryId();

                if (tradeService != null) {
                    Map<String, Object> details = tradeService.getDetails();

                    // clears pddts details
                    details.remove("fundingReferenceNumber");
                    details.remove("swift");
                    details.remove("bank");
                    details.remove("beneficiary");
                    details.remove("pddtsAccountNumber");
                    details.remove("byOrder");

                    // clears mt103 details
                    details.remove("receivingBank");

                    details.remove("senderReference");
                    details.remove("bankOperationCode");

                    details.remove("orderingAccountNumber");
                    details.remove("orderingAddress");

                    details.remove("accountWithInstitution");
                    details.remove("nameAndAddress");

                    details.remove("beneficiaryName");
                    details.remove("beneficiaryAddress");
                    details.remove("beneficiaryAccountNumber");

                    details.remove("detailsOfCharges");
                    details.remove("senderToReceiverInformation");
                    details.remove("chargesOverridenFlag");
                    tradeService.updateDetails(details, userActiveDirectoryId);

                    if(!doNotResetChargesBecauseItIsMC_Issuance){
                        chargesService.applyCharges(tradeService, details); //TODO TEST
                    }
                    tradeServiceRepository.merge(tradeService);
                    System.out.println("Persisted TradeService!");
                }
            }
        }
    }

//    @EventListener
//    public void clearProceedsToBeneficiaryPaymentDetails(PaymentSavedEvent paymentSavedEvent) {
////        System.out.println("clearing pddts and mt103 details from proceeds to beneficiary");
////        Payment savedPayment = paymentSavedEvent.getPayment();
////        System.out.println("CHARGE TYPE: " + savedPayment.getChargeType());
////        if (savedPayment.getChargeType().equals(ChargeType.SETTLEMENT)) {
////            TradeServiceId tradeserviceId = paymentSavedEvent.getTradeServiceId();
////
////            TradeService tradeService = tradeServiceRepository.load(tradeserviceId);
////
////            UserActiveDirectoryId userActiveDirectoryId = paymentSavedEvent.getUserActiveDirectoryId();
////
////            if (tradeService != null) {
////                Map<String, Object> details = tradeService.getDetails();
////
////                // clears pddts details
////                details.remove("fundingReferenceNumber");
////                details.remove("swift");
////                details.remove("bank");
////                details.remove("beneficiary");
////                details.remove("pddtsAccountNumber");
////                details.remove("byOrder");
////
////                // clears mt103 details
////                details.remove("receivingBank");
////
////                details.remove("senderReference");
////                details.remove("bankOperationCode");
////
////                details.remove("orderingAccountNumber");
////                details.remove("orderingAddress");
////
////                details.remove("accountWithInstitution");
////                details.remove("nameAndAddress");
////
////                details.remove("beneficiaryName");
////                details.remove("beneficiaryAddress");
////                details.remove("beneficiaryAccountNumber");
////
////                details.remove("detailsOfCharges");
////                details.remove("senderToReceiverInformation");
////
////                tradeService.updateDetails(details, userActiveDirectoryId);
////
////                tradeServiceRepository.merge(tradeService);
////                System.out.println("Persisted TradeService!");
////            }
////        }
//    }

    @EventListener
    public void removeSettlementPayments(RemovedSettlementPaymentsEvent removedSettlementPaymentsEvent) {
        Payment payment = paymentRepository.get(removedSettlementPaymentsEvent.getTradeServiceId(), ChargeType.SETTLEMENT);

        paymentRepository.delete(payment);
        System.out.println("settlement payments deleted...");
    }

//    @EventListener
//    public void setupArIfExist(PaymentSavedEvent paymentSavedEvent) {
//        Payment payment = paymentSavedEvent.getPayment();
//        TradeService tradeService = tradeServiceRepository.load(paymentSavedEvent.getTradeServiceId());
//
//        for(PaymentDetail pd : payment.getDetails()) {
//            if (pd.getPaymentInstrumentType().equals(PaymentInstrumentType.AR)) {
//                SettlementAccountNumber settlementAccountNumber = new SettlementAccountNumber(tradeService.getServiceInstructionId().toString());
//                AccountsReceivable ar = accountsReceivableRepository.load(settlementAccountNumber);
//
//                if (ar == null ) {
//                    ar = new AccountsReceivable(settlementAccountNumber);
//                }
//                // Persist
//
//                System.out.println("TRADE SERVICE >>>>> " + tradeService.getDetails());
//                Currency currency = Currency.getInstance(((String) tradeService.getDetails().get("currency")).trim());
//
//                ar.setCif((String) tradeService.getDetails().get("cifNumber"), (String) tradeService.getDetails().get("cifName"));
//
//                String bookingDate = null; //(String) tradeService.getDetails().get("bookingDate");
//                String applicationReferenceNumber = null; //(String) tradeService.getDetails().get("documentNumber");
//                String natureOfTransaction = null; //(String) tradeService.getDetails().get("natureOfTransaction");
//
////                ReferenceType referenceType = null;
////
////                if (payment.getChargeType().equals(ChargeType.PRODUCT)) {
////                    referenceType = ReferenceType.APPLY_AR;
////                }
//
//                try {
//                    ar.credit(pd.getAmount(), currency, ReferenceType.APPLY_AR, tradeService.getServiceInstructionId().toString());
//                    accountsReceivableRepository.persist(ar);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    System.out.println("ERROR SAVING AR VIA MODE OF PAYMENT");
//                }
//            }
//        }
//    }

    private boolean isTradeServiceFullyPaid(TradeServiceId tradeServiceId) {
        for (Payment payment : paymentRepository.getAllPayments(tradeServiceId)) {
            for (PaymentDetail detail : payment.getDetails()) {
                if (!PaymentStatus.NO_PAYMENT_REQUIRED.equals(detail.getStatus()) && !detail.isPaid()) {
                    return false;
                }
            }
        }
        return true;
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
}
