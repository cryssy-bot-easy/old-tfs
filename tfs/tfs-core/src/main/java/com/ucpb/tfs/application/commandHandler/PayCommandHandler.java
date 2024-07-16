package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.application.command.PayCommand;
import com.ucpb.tfs.domain.cdt.event.PaymentRequestPaidEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaidEvent;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Marvin Volante
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class PayCommandHandler implements CommandHandler<PayCommand> {

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Override
    public void handle(PayCommand command) {

        System.out.println("PayCommand");
        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        try {

            TradeService tradeService = tradeServiceRepository.load(new TradeServiceId((String) parameterMap.get("tradeServiceId")));

            String settlementAccountNumber = tradeService.getDocumentNumber().toString();

            ChargeType chargeType = ChargeType.valueOf(((String) parameterMap.get("chargeType")).toUpperCase());
            Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), chargeType);

            String modeOfPayment = (String) parameterMap.get("modeOfPayment");
            System.out.println("modeOfPayment >> " + modeOfPayment);
            PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(modeOfPayment);
            String referenceNumber = null;

            // For loans
            Currency bookingCurrency = null;
            Integer interestRate = null;
            String interestTerm = null;
            String repricingTerm = null;
            String repricingTermCode = null;
            String loanTerm = null;
            String loanTermCode = null;
            Date loanMaturityDate = null;

            String referenceId = null;

            BigDecimal amount = new BigDecimal(((String) parameterMap.get("amount")).trim());
            Currency settlementCurrency = Currency.getInstance((String) parameterMap.get("settlementCurrency"));
            PaymentDetail detail = null;

            PaymentItemPaidEvent itemPaidEvent = null;
            switch (paymentInstrumentType) {

                case CASA:
                    referenceNumber = (String) parameterMap.get("accountNumber");

                    payment.payItem(paymentInstrumentType, settlementAccountNumber, referenceNumber);

                    paymentRepository.saveOrUpdate(payment);

                    itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),settlementAccountNumber,detail);
                    break;
                case MD:
                    referenceNumber = (String) parameterMap.get("accountNumber");

                    payment.payItem(paymentInstrumentType, settlementAccountNumber, referenceNumber);

                    paymentRepository.saveOrUpdate(payment);
                    System.out.println("tradeproductnumber > " + tradeService.getTradeProductNumber());
                    itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),tradeService.getTradeProductNumber().toString(),detail);
                    break;
                case AP:
                    referenceNumber = (String) parameterMap.get("accountNumber");
                    referenceId = (String) parameterMap.get("referenceId");

                    payment.payItem(paymentInstrumentType, settlementAccountNumber, referenceNumber, referenceId);

                    paymentRepository.saveOrUpdate(payment);
                    detail = payment.getPaymentDetail(paymentInstrumentType);

                    itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),settlementAccountNumber,detail,referenceId);
                    break;
                case AR:
                    referenceNumber = tradeService.getDocumentNumber().toString();

                    payment.payItem(paymentInstrumentType, settlementAccountNumber, referenceNumber);

                    paymentRepository.saveOrUpdate(payment);

                    itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),settlementAccountNumber,detail,referenceId);


                    break;

                case CHECK:
                case CASH:
                case REMITTANCE:
                case IBT_BRANCH:
                    referenceNumber = (String) parameterMap.get("tradeSuspenseAccount");

                    payment.payItem(paymentInstrumentType, settlementAccountNumber, referenceNumber);

                    paymentRepository.saveOrUpdate(payment);

                    itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),settlementAccountNumber,detail);
                    break;

                case IB_LOAN:
                case TR_LOAN:

                    Map<String, Object> setStringMap1 = (Map<String, Object>) parameterMap.get("setupString");

                    // Use DocumentNumber as the referenceNumber for loans
                    referenceNumber = tradeService.getDocumentNumber().toString();

                    bookingCurrency = Currency.getInstance(((String) setStringMap1.get("bookingCurrency")).trim());
                    interestRate = new Integer(((String) setStringMap1.get("interestRate")).trim());
                    interestTerm = (String) setStringMap1.get("interestTerm");
                    repricingTerm = (String) setStringMap1.get("repricingTerm");
                    repricingTermCode = (String) setStringMap1.get("repricingTermCode");
                    loanTerm = (String) setStringMap1.get("loanTerm");
                    loanTermCode = (String) setStringMap1.get("loanTermCode");

                    DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
                    loanMaturityDate = df1.parse((String) setStringMap1.get("loanMaturityDate"));

                    payment.payItem(paymentInstrumentType, settlementAccountNumber, referenceNumber);

                    paymentRepository.saveOrUpdate(payment);

                    itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),settlementAccountNumber,detail);
                    break;

                case UA_LOAN:

                    Map<String, Object> setStringMap2 = (Map<String, Object>) parameterMap.get("setupString");

                    // Use DocumentNumber as the referenceNumber for loans
                    referenceNumber = tradeService.getDocumentNumber().toString();

                    bookingCurrency = Currency.getInstance(((String) setStringMap2.get("bookingCurrency")).trim());
                    DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                    loanMaturityDate = df2.parse((String) setStringMap2.get("loanMaturityDate"));

                    payment.payItem(paymentInstrumentType, settlementAccountNumber, referenceNumber);

                    paymentRepository.saveOrUpdate(payment);

                    itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),settlementAccountNumber,detail);
                    break;

                case MC_ISSUANCE:
                    referenceNumber = (String) parameterMap.get("tradeSuspenseAccount");
                    System.out.println("payment >> " + payment);
                    System.out.println("paymentInstrumentType >> " + paymentInstrumentType);
                    System.out.println("settlementAccountNumber >> " + settlementAccountNumber);
                    System.out.println("referenceNumber >> " + referenceNumber);
                    payment.payItem(paymentInstrumentType, settlementAccountNumber, referenceNumber);

                    paymentRepository.saveOrUpdate(payment);

                    itemPaidEvent = new PaymentItemPaidEvent(tradeService.getTradeServiceId(),settlementAccountNumber,detail);
                    break;
            }
//
//
//
//            System.out.println("\n");
//            System.out.println("paymentInstrumentType = " + paymentInstrumentType);
//            System.out.println("settlementAccountNumber = " + settlementAccountNumber);
//            System.out.println("referenceNumber = " + referenceNumber);
//            System.out.println("amount = " + amount.toString());
//            System.out.println("settlementCurrency = " + settlementCurrency.toString());
//            System.out.println("\n");

            // Pay item


            // Persist payment


            // Fire event

            eventPublisher.publish(itemPaidEvent);

            // TODO: this should not be here but until we fix this payment event sequence thing, it needs to be here for now
            // this is specific to CDT only
            if ((tradeService.getDocumentClass() == DocumentClass.CDT) &&
                    (tradeService.getServiceType() == ServiceType.PAYMENT)) {

                String iedieirdNumber = (String) tradeService.getTradeServiceReferenceNumber().toString();
                String processingUnitCode = (String) tradeService.getDetails().get("processingUnitCode").toString();

                if (tradeService.getPaymentStatus() == PaymentStatus.PAID) {
                    PaymentRequestPaidEvent paymentRequestPaidEvent = new PaymentRequestPaidEvent(iedieirdNumber, processingUnitCode);
                    eventPublisher.publish(paymentRequestPaidEvent);
                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside pay command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
