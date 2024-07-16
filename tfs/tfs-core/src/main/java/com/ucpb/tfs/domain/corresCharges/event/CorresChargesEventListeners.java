package com.ucpb.tfs.domain.corresCharges.event;

import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ucpb.tfs.domain.corresCharges.*;
import com.ucpb.tfs.domain.corresCharges.enumTypes.CorresChargeTradeProductType;
import com.ucpb.tfs.domain.corresCharges.enumTypes.CorresChargeType;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.event.*;
import com.ucpb.tfs.domain.service.ServiceCharge;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

/**
 * User: IPCVal
 * Date: 11/4/12
 */
@Component
public class CorresChargesEventListeners {

    @Autowired
    CorresChargeAdvanceRepository corresChargeAdvanceRepository;

    @Autowired
    CorresChargeActualRepository corresChargeActualRepository;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @EventListener
    public void lcOpened(LetterOfCreditCreatedEvent letterOfCreditCreatedEvent) {

        TradeService tradeService = letterOfCreditCreatedEvent.getTradeService();
        if (tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
            createCorresChargeAdvance(tradeService, CorresChargeTradeProductType.LC, ServiceType.OPENING);
        }
    }

    @EventListener
    public void lcAmended(LCAmendedEvent lcAmendedEvent) {

        TradeService tradeService = lcAmendedEvent.getTradeService();
        if (tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
            createCorresChargeAdvance(tradeService, CorresChargeTradeProductType.LC, ServiceType.AMENDMENT);
        }
    }

    @EventListener
    public void daCreated(DACreatedEvent daCreatedEvent) {

        TradeService tradeService = daCreatedEvent.getTradeService();
        if (tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
            createCorresChargeAdvance(tradeService, CorresChargeTradeProductType.NON_LC, ServiceType.NEGOTIATION_ACKNOWLEDGEMENT);
        }
    }

    @EventListener
    public void dpCreated(DPCreatedEvent dpCreatedEvent) {

        TradeService tradeService = dpCreatedEvent.getTradeService();
        if (tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
            createCorresChargeAdvance(tradeService, CorresChargeTradeProductType.NON_LC, ServiceType.NEGOTIATION);
        }
    }

    @EventListener
    public void oaCreated(OACreatedEvent oaCreatedEvent) {

        TradeService tradeService = oaCreatedEvent.getTradeService();
        if (tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
            createCorresChargeAdvance(tradeService, CorresChargeTradeProductType.NON_LC, ServiceType.NEGOTIATION);
        }
    }

    @EventListener
    public void drCreated(DRCreatedEvent drCreatedEvent) {

        TradeService tradeService = drCreatedEvent.getTradeService();
        if (tradeService.getDocumentType().equals(DocumentType.FOREIGN)) {
            createCorresChargeAdvance(tradeService, CorresChargeTradeProductType.NON_LC, ServiceType.NEGOTIATION);
        }
    }

    @EventListener
    public void corresChargeActualApproved(CorresChargeActualApprovedEvent corresChargeActualApprovedEvent) {

        CorresChargeActual corresChargeActual = corresChargeActualApprovedEvent.getCorresChargeActual();

//        if (corresChargeActualApprovedEvent.getHasReference() != null && corresChargeActualApprovedEvent.getHasReference() == Boolean.FALSE) {
        if (corresChargeActualApprovedEvent.getHasReference() != null && corresChargeActualApprovedEvent.getHasReference() == Boolean.TRUE) {

            System.out.println("WITH REFERENCE");

            // Compute outstandingBalance: Actual minus Advance minus Payment

            // Should all be in PHP
            BigDecimal totalAdvance = BigDecimal.ZERO;
            BigDecimal totalActual  = BigDecimal.ZERO;
            BigDecimal totalPayment = BigDecimal.ZERO;
            BigDecimal totalCovered = BigDecimal.ZERO;

            // 1. Load all Advance
            List<CorresChargeAdvance> listAdvance = corresChargeAdvanceRepository.getAllByDocumentNumber(corresChargeActual.getDocumentNumber());

            if (listAdvance != null && !listAdvance.isEmpty()) {

                for (CorresChargeAdvance corresChargeAdvance : listAdvance) {
                    totalAdvance = totalAdvance.add(corresChargeAdvance.getAmount());
                    totalCovered = totalCovered.add(corresChargeAdvance.getCoveredAmount());
                }
                // 2. Load all Actual
                //    Should already contain the latest that was approved
                List<CorresChargeActual> listActual = corresChargeActualRepository.getAllByDocumentNumber(corresChargeActual.getDocumentNumber());

                if (listActual != null && !listActual.isEmpty()) {

                    for (CorresChargeActual actual : listActual) {
                        totalActual = totalActual.add(actual.getAmount());

                        Payment payment = paymentRepository.load(actual.getTradeServiceId());

                        if (payment != null) {
                            totalPayment = totalPayment.add(payment.getTotalPaid(Currency.getInstance("PHP")));
                        }
                    }
                }
            }
            System.out.println("TOTAL ACTUAL: " + totalActual);
            System.out.println("TOTAL ADVANCE: " + totalAdvance);
            System.out.println("TOTAL PAYMENT: " + totalPayment);
            System.out.println("TOTAL COVERED: " + totalCovered);

            BigDecimal amountToAllocate = BigDecimal.ZERO;

            if (totalAdvance.compareTo(totalActual) < 0) { // totalAdvance < totalActual  : there will be an oustanding balance
                System.out.println("totalAdvance < totalActual");
                System.out.println(totalAdvance.toString() + "<" + totalActual);
                amountToAllocate = totalAdvance.subtract(totalCovered);
                allocateAmount(amountToAllocate, listAdvance);
            }

            if (totalAdvance.compareTo(totalActual) >= 0) { // totalAdvance >= totalActual : advance will be covered
                System.out.println("totalAdvance >= totalActual");
                System.out.println(totalAdvance.toString() + ">=" + totalActual);
                allocateAmount(totalActual, listAdvance);
            }
        }
    }

    public void allocateAmount(BigDecimal amountToAllocate, List<CorresChargeAdvance> listAdvance) {
            // Do allocation of totalOutstanding until 0
            // Supposed to be this list is already sorted
            allocation:
            for (CorresChargeAdvance corresChargeAdvance : listAdvance) {
                System.out.println(corresChargeAdvance.getAmount() + " " + corresChargeAdvance.getCoveredAmount());
                // Skip records which have amount = outstandingBalance (already allocated)
                if (corresChargeAdvance.getAmount().compareTo(corresChargeAdvance.getCoveredAmount()) > 0) {

                    BigDecimal diff = corresChargeAdvance.getAmount().subtract(corresChargeAdvance.getCoveredAmount());

                    if (amountToAllocate.compareTo(diff) > 0) {

                        corresChargeAdvance.setCoveredAmount(corresChargeAdvance.getCoveredAmount().add(diff));

                        // Subtract from totalOutstanding
                        amountToAllocate = amountToAllocate.subtract(diff);

                        corresChargeAdvanceRepository.merge(corresChargeAdvance);
                    }
                    else if (amountToAllocate.compareTo(diff) <= 0) {

                        // Add remaining from total outstanding to the outstanding of the Corres Charge Advance
                        corresChargeAdvance.setCoveredAmount(corresChargeAdvance.getCoveredAmount().add(amountToAllocate));

                        // Set to 0
                        amountToAllocate = BigDecimal.ZERO;

                        corresChargeAdvanceRepository.merge(corresChargeAdvance);

                        break allocation;  // break because there is no outstanding left
                    }
                }
            }
    }

    private void createCorresChargeAdvance(TradeService tradeService,
                                           CorresChargeTradeProductType corresChargeTradeProductType,
                                           ServiceType serviceType) {
        System.out.println("createCorresChargeAdvance");

        if (corresChargeTradeProductType.equals(CorresChargeTradeProductType.NON_LC)) {

            // Non-LC's have no advance corres charge, so log as zero and with no currency
            CorresChargeAdvance corresChargeAdvance = new CorresChargeAdvance(tradeService.getTradeServiceId(),
                                                         tradeService.getDocumentNumber(),
                                                         serviceType,
                                                         CorresChargeType.NEGO_FEE,
                                                         BigDecimal.ZERO,
                                                         Currency.getInstance("PHP"));

            corresChargeAdvanceRepository.save(corresChargeAdvance);

        } else {

            List<Object> serviceChargesList = (List<Object>)(tradeService.getChargesSummary().get("SC"));

            CorresChargeAdvance corresChargeAdvanceAdvising = null;
            CorresChargeAdvance corresChargeAdvanceConfirming = null;

            for (Object obj : serviceChargesList) {

                if (obj instanceof ServiceCharge) {

                    ServiceCharge serviceCharge = (ServiceCharge)obj;

                    BigDecimal amount = serviceCharge.getAmount();

                    if (serviceCharge.getChargeId().toString().equals("CORRES-ADVISING")) {

                        corresChargeAdvanceAdvising = new CorresChargeAdvance(tradeService.getTradeServiceId(),
                                                                tradeService.getDocumentNumber(),
                                                                serviceType,
                                                                CorresChargeType.ADVISING,
                                                                amount,
                                                                Currency.getInstance("PHP"));

                    } else if (serviceCharge.getChargeId().toString().equals("CORRES-CONFIRMING")) {

                        corresChargeAdvanceConfirming = new CorresChargeAdvance(tradeService.getTradeServiceId(),
                                                                  tradeService.getDocumentNumber(),
                                                                  serviceType,
                                                                  CorresChargeType.CONFIRMING,
                                                                  amount,
                                                                  Currency.getInstance("PHP"));
                    }
                }
            }

            if (corresChargeAdvanceAdvising == null) {
                corresChargeAdvanceAdvising = new CorresChargeAdvance(tradeService.getTradeServiceId(),
                                                        tradeService.getDocumentNumber(),
                                                        serviceType,
                                                        CorresChargeType.ADVISING,
                                                        BigDecimal.ZERO,
                                                        Currency.getInstance("PHP"));
            }

            if (corresChargeAdvanceConfirming == null) {
                corresChargeAdvanceConfirming = new CorresChargeAdvance(tradeService.getTradeServiceId(),
                                                          tradeService.getDocumentNumber(),
                                                          serviceType,
                                                          CorresChargeType.CONFIRMING,
                                                          BigDecimal.ZERO,
                                                          Currency.getInstance("PHP"));
            }

            corresChargeAdvanceRepository.save(corresChargeAdvanceAdvising);
            corresChargeAdvanceRepository.save(corresChargeAdvanceConfirming);
        }
    }

}
