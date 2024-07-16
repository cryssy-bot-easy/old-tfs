package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.application.command.DeleteSettlementCommand;
import com.ucpb.tfs.domain.cdt.event.CDTUnpaidEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentDeletedEvent;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 9/7/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class DeleteSettlementCommandHandler implements CommandHandler<DeleteSettlementCommand> {

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    private PaymentRepository paymentRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Override
    public void handle(DeleteSettlementCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        TradeService tradeService = tradeServiceRepository.load(new TradeServiceId((String)parameterMap.get("tradeServiceId")));

        ChargeType chargeType = ChargeType.valueOf(((String)parameterMap.get("chargeType")).toUpperCase());
        Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), chargeType);
        if (payment == null) {
            payment = new Payment(tradeService.getTradeServiceId(), chargeType);
        }

        String paymentMode = (String) parameterMap.get("paymentMode");

        PaymentInstrumentType paymentInstrumentType = PaymentInstrumentType.valueOf(paymentMode);
        String referenceNumber = null;

        String referenceId = null;

        // executes changing of status before deleting item
        if (payment.getChargeType().equals(ChargeType.SETTLEMENT)) {
            if (payment.containsPddtsOrSwift()) {
                System.out.println("containsPddtsOrSwift");

//                // set paymentStatus to UNPAID only if the current paymentStatus is NO_PAYMENT_REQUIRED
//                if (tradeService.getPaymentStatus().equals(PaymentStatus.NO_PAYMENT_REQUIRED)) {
                    tradeService.setPaymentStatus(PaymentStatus.UNPAID);
//                }
                //additional conditions for REFUNDS since they function similarly to SETTLEMENT but are required to be paid.
            }else if(DocumentType.REFUND.equals(tradeService.getDocumentType())){
            	if(payment.containsCasaOrIbt() || PaymentInstrumentType.MC_ISSUANCE.equals(paymentInstrumentType))
            		tradeService.unPay();
            }else if(ServiceType.REFUND.equals(tradeService.getServiceType())){
            	if(payment.containsCasaOrIbt() || PaymentInstrumentType.MC_ISSUANCE.equals(paymentInstrumentType))
            		tradeService.unPay();
            } else {
                //List<ChargeType> chargeTypeList = paymentRepository.getAllPaymentChargeTypesPerTradeService(payment.getTradeServiceId());

                // change TradeService.paymentStatus to NO_PAYMENT_REQUIRED only if there is no payment added for PRODUCT and SERVICE
                System.out.println("here i am : " + parameterMap.get("containsProductPayment"));
                if (!parameterMap.get("containsProductPayment").equals("true")) {
                    System.out.println("setting no payment required #6");
                    tradeService.setAsNoPaymentRequired();
                } else {
                    if (tradeService.getPaymentStatus().equals(PaymentStatus.PAID) || tradeService.getPaymentStatus().equals(PaymentStatus.NO_PAYMENT_REQUIRED)) {
                        tradeService.setPaymentStatus(PaymentStatus.UNPAID);
                    }
                }


                if (tradeService.getDocumentClass().equals(DocumentClass.LC) && tradeService.getServiceType().equals(ServiceType.REFUND)) {
                    tradeService.setPaymentStatus(PaymentStatus.UNPAID);
                }
            }

            // set payment status to unpaid
            payment.unPay();
        }

        switch (paymentInstrumentType) {
            case CASA:
                referenceNumber = (String) parameterMap.get("accountNumber");

                try {
                    payment.deleteItem(paymentInstrumentType, referenceNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case MD:
            case AP:
                referenceNumber = (String) parameterMap.get("accountNumber");
                referenceId = (String) parameterMap.get("referenceId");

                try {
                    payment.deleteItem(paymentInstrumentType, referenceNumber, referenceId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case AR:
                referenceNumber = tradeService.getDocumentNumber().toString(); //tradeService.getServiceInstructionId().toString(); //(String) parameterMap.get("accountNumber");

                try {
                    payment.deleteItem(paymentInstrumentType, referenceNumber);
                } catch (Exception e) {
                    e.printStackTrace();
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

                try {
                    payment.deleteItem(paymentInstrumentType, referenceNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case IB_LOAN:
            case TR_LOAN:
            case UA_LOAN:
            case DBP:
            case EBP:
                referenceNumber = tradeService.getDocumentNumber().toString();

                try {
                    payment.deleteItem(paymentInstrumentType, referenceNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }

        System.out.println("\n");
        System.out.println("paymentInstrumentType = " + paymentInstrumentType);
        System.out.println("referenceNumber = " + referenceNumber);
        System.out.println("\n");

//        try {
//
//            // Delete item
//            payment.deleteItem(paymentInstrumentType, referenceNumber);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Persist payment
        paymentRepository.saveOrUpdate(payment);

        PaymentDeletedEvent paymentDeletedEvent = new PaymentDeletedEvent(tradeService.getTradeServiceId(), payment);
        eventPublisher.publish(paymentDeletedEvent);

        if (tradeService.getDocumentClass().equals(DocumentClass.CDT) && tradeService.getServiceType().equals(ServiceType.PAYMENT)) {
            System.out.println("cdt payment event is called by MC_ISSUANCE");
            String iedieirdNumber = tradeService.getTradeServiceReferenceNumber().toString();
            String processingUnitCode = null;

            if (tradeService.getDetails().get("processingUnitCode") != null) {
                processingUnitCode = tradeService.getDetails().get("processingUnitCode").toString();
            } else {
                processingUnitCode = tradeService.getDetails().get("unitCode").toString();
            }

            CDTUnpaidEvent cdtUnpaidEvent = new CDTUnpaidEvent(iedieirdNumber, tradeService.getTradeServiceId());
            eventPublisher.publish(cdtUnpaidEvent);
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside delete settlement command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
