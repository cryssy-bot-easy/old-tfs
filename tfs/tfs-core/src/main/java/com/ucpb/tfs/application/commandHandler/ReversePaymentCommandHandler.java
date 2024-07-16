package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.application.command.ReversePaymentCommand;
import com.ucpb.tfs.domain.cdt.event.PaymentRequestUnpaidEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaymentReversedEvent;
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
public class ReversePaymentCommandHandler implements CommandHandler<ReversePaymentCommand> {

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Override
    public void handle(ReversePaymentCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        try {

            TradeService tradeService = tradeServiceRepository.load(new TradeServiceId((String)parameterMap.get("tradeServiceId")));

            ChargeType chargeType = ChargeType.valueOf(((String)parameterMap.get("chargeType")).toUpperCase());
            Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), chargeType);

            String modeOfPayment = (String) parameterMap.get("modeOfPayment");

            PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(modeOfPayment);
            String referenceNumber = null;

            // For loans
            Currency bookingCurrency = null;
            BigDecimal interestRate = null;
            String interestTerm = null;
            String repricingTerm = null;
            String repricingTermCode = null;
            String loanTerm = null;
            String loanTermCode = null;
            Date loanMaturityDate = null;

            String referenceId = null;

            switch (paymentInstrumentType) {

                case CHECK:
                case CASH:
                case REMITTANCE:
                case IBT_BRANCH:
                case MC_ISSUANCE:
                    referenceNumber = (String) parameterMap.get("tradeSuspenseAccount");

                    payment.reverseItemPayment(paymentInstrumentType, tradeService.getDocumentNumber().toString(), referenceNumber);
                    break;

                case MD:
                case AP:
                    referenceNumber = (String) parameterMap.get("accountNumber");
                    referenceId = (String) parameterMap.get("referenceId");

                    payment.reverseItemPayment(paymentInstrumentType, tradeService.getDocumentNumber().toString(), referenceNumber, referenceId);
                    break;
                case AR:
                    referenceNumber = (String) parameterMap.get("accountNumber");

                    referenceId = payment.getReferenceId(paymentInstrumentType, tradeService.getDocumentNumber().toString());

                    payment.reverseItemPayment(paymentInstrumentType, tradeService.getDocumentNumber().toString(), referenceNumber);
                    break;

                case IB_LOAN:
                case TR_LOAN:

                    Map<String, Object> setStringMap1 = (Map<String, Object>)parameterMap.get("setupString");

                    // Use DocumentNumber as the referenceNumber for loans
                    referenceNumber = tradeService.getDocumentNumber().toString();

                    bookingCurrency = Currency.getInstance(((String) setStringMap1.get("bookingCurrency")).trim());
                    interestRate = new BigDecimal(((String) setStringMap1.get("interestRate")).trim());
                    interestTerm = (String) setStringMap1.get("interestTerm");
                    repricingTerm = (String) setStringMap1.get("repricingTerm");
                    repricingTermCode = (String) setStringMap1.get("repricingTermCode");
                    loanTerm = (String) setStringMap1.get("loanTerm");
                    loanTermCode = (String) setStringMap1.get("loanTermCode");

                    DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
                    loanMaturityDate = df1.parse((String) setStringMap1.get("loanMaturityDate"));

                    payment.reverseItemPayment(paymentInstrumentType, tradeService.getDocumentNumber().toString(), referenceNumber);

                    break;

                case UA_LOAN:

                    Map<String, Object> setStringMap2 = (Map<String, Object>)parameterMap.get("setupString");

                    // Use DocumentNumber as the referenceNumber for loans
                    referenceNumber = tradeService.getDocumentNumber().toString();

                    bookingCurrency = Currency.getInstance(((String) setStringMap2.get("bookingCurrency")).trim());
                    DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                    loanMaturityDate = df2.parse((String) setStringMap2.get("loanMaturityDate"));

                    payment.reverseItemPayment(paymentInstrumentType, tradeService.getDocumentNumber().toString(), referenceNumber);

                    break;
            }

            BigDecimal amount = new BigDecimal(((String) parameterMap.get("amount")).trim());
            Currency settlementCurrency = Currency.getInstance((String) parameterMap.get("settlementCurrency"));

            System.out.println("\n");
            System.out.println("paymentInstrumentType = " + paymentInstrumentType);
            System.out.println("referenceNumber = " + tradeService.getDocumentNumber().toString());
            System.out.println("settlementAccountNumber = " + referenceNumber);
            System.out.println("amount = " + amount.toString());
            System.out.println("settlementCurrency = " + settlementCurrency.toString());
            System.out.println("\n");

            // Reverse payment
//            payment.reverseItemPayment(paymentInstrumentType, tradeService.getDocumentNumber().toString(), referenceNumber);

            // Persist payment
            paymentRepository.saveOrUpdate(payment);

            // Fire event
            PaymentItemPaymentReversedEvent paymentItemPaymentReversedEvent = new PaymentItemPaymentReversedEvent(
                                                                                tradeService.getTradeServiceId(),
                                                                                paymentInstrumentType,
                                                                                tradeService.getDocumentNumber().toString(),
                                                                                referenceNumber,
                                                                                amount,
                                                                                settlementCurrency,
                                                                                bookingCurrency,
                                                                                interestRate,
                                                                                interestTerm,
                                                                                repricingTerm,
                                                                                repricingTermCode,
                                                                                loanTerm,
                                                                                loanTermCode,
                                                                                loanMaturityDate,
                                                                                referenceId);

            Boolean isReversal = false;
            String reversalTradeServiceId = "";

            if(parameterMap.get("reverseDE") != null) {
                String rDE = (String) parameterMap.get("reverseDE");
                if(rDE.equalsIgnoreCase("true")) {
                    isReversal = true;
                    reversalTradeServiceId = (String) parameterMap.get("reversalDENumber");
                }
            }

            if(isReversal) {
                paymentItemPaymentReversedEvent.setReversal(true);
                paymentItemPaymentReversedEvent.setReversalTradeServiceId(new TradeServiceId(reversalTradeServiceId));
            }

            eventPublisher.publish(paymentItemPaymentReversedEvent);




            // TODO: this should not be here but until we fix this payment event sequence thing, it needs to be here for now
            // this is specific to CDT only
            if((tradeService.getDocumentClass() == DocumentClass.CDT) &&
                    (tradeService.getServiceType() == ServiceType.PAYMENT)) {

                String iedieirdNumber = (String) tradeService.getTradeServiceReferenceNumber().toString();

                if(tradeService.getPaymentStatus() == PaymentStatus.UNPAID) {
                    PaymentRequestUnpaidEvent paymentRequestUnpaidEvent = new PaymentRequestUnpaidEvent(iedieirdNumber);
                    eventPublisher.publish(paymentRequestUnpaidEvent);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside reverse payment command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
