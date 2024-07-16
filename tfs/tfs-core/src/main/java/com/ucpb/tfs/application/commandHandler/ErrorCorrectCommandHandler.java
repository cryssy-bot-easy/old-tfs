package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.application.command.ErrorCorrectCommand;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaymentReversedEvent;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Marvin Volante
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ErrorCorrectCommandHandler implements CommandHandler<ErrorCorrectCommand> {

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Override
    public void handle(ErrorCorrectCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        try {

            TradeService tradeService = tradeServiceRepository.load(new TradeServiceId((String)parameterMap.get("tradeServiceId")));

            ChargeType chargeType = ChargeType.valueOf(((String)parameterMap.get("chargeType")).toUpperCase());
            Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), chargeType);

            String modeOfPayment = (String) parameterMap.get("modeOfPayment");

            // Always CASA
            PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(modeOfPayment);
            String settlementAccountNumber = (String) parameterMap.get("accountNumber");

            BigDecimal amount = new BigDecimal(((String) parameterMap.get("amount")).trim());
            Currency settlementCurrency = Currency.getInstance((String) parameterMap.get("settlementCurrency"));

            System.out.println("\n");
            System.out.println("paymentInstrumentType = " + paymentInstrumentType);
            System.out.println("referenceNumber = " + tradeService.getDocumentNumber().toString());
            System.out.println("settlementAccountNumber = " + settlementAccountNumber);
            System.out.println("amount = " + amount.toString());
            System.out.println("settlementCurrency = " + settlementCurrency.toString());
            System.out.println("\n");

            // Reverse payment
            payment.reverseItemPayment(paymentInstrumentType, tradeService.getDocumentNumber().toString(), settlementAccountNumber);

            // Persist payment
            paymentRepository.saveOrUpdate(payment);

            // Fire event
            // CASA error correction does not need the "null" parameters (these are for loans only)
            PaymentItemPaymentReversedEvent paymentItemPaymentReversedEvent = new PaymentItemPaymentReversedEvent(
                                                                                tradeService.getTradeServiceId(),
                                                                                paymentInstrumentType,
                                                                                tradeService.getDocumentNumber().toString(),
                                                                                settlementAccountNumber,
                                                                                amount,
                                                                                settlementCurrency,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null);
            eventPublisher.publish(paymentItemPaymentReversedEvent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside error correct command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
