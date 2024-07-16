package com.ucpb.tfs.domain.cdt.event;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository;
import com.ucpb.tfs.domain.cdt.enums.CDTStatus;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.event.CDTRemittanceCreatedEvent;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs2.application.service.DocumentNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 10/29/13
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
public class CDTPaymentRequestEventListener {

    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository;

    @Autowired
    DocumentNumberService documentNumberService;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @EventListener // manual tag as paid
    public void taggedAsPaidEventListener(CDTTagAsPaidEvent cdtTagAsPaidEvent) {
        CDTPaymentRequest cdtPaymentRequest = cdtPaymentRequestRepository.load(cdtTagAsPaidEvent.getIedieirdNumber());

        DocumentNumber paymentReferenceNumber = documentNumberService.generateDocumentNumber(cdtTagAsPaidEvent.getProcessingUnitCode(),
                null, DocumentClass.CDT, null, null, null, ServiceType.PAYMENT);

        cdtPaymentRequest.setPaymentReferenceNumber(paymentReferenceNumber.toString());

        cdtPaymentRequestRepository.merge(cdtPaymentRequest);
    }

    @EventListener
    public void payCDT(CDTPaidEvent cdtPaidEvent) {
        System.out.println("cdt paid event is dispatched");
        CDTPaymentRequest cdtPaymentRequest = cdtPaymentRequestRepository.load(cdtPaidEvent.getIedieirdNumber());

        BigDecimal totalAmountPaid = BigDecimal.ZERO;

        for (PaymentDetail paymentDetail : cdtPaidEvent.getPayment().getDetails()) {
            if (PaymentStatus.PAID.equals(paymentDetail.getStatus())) {
                totalAmountPaid = totalAmountPaid.add(paymentDetail.getAmount());
            }
        }
        System.out.println("totalAmountPaid: " + totalAmountPaid);
        System.out.println("cdtPaymentRequest.getAmount()): " + cdtPaymentRequest.getAmount());
        System.out.println();

        if (totalAmountPaid.compareTo(cdtPaymentRequest.getAmount()) >= 0) {
            System.out.println("generating document number");

            cdtPaymentRequest.setStatus(CDTStatus.PAID);
            cdtPaymentRequest.setDatePaid(new Date());

            DocumentNumber paymentReferenceNumber = documentNumberService.generateDocumentNumber(cdtPaidEvent.getProcessingUnitCode(),
                    null, DocumentClass.CDT, null, null, null, ServiceType.PAYMENT);

            cdtPaymentRequest.setPaymentReferenceNumber(paymentReferenceNumber.toString());

            cdtPaymentRequestRepository.merge(cdtPaymentRequest);
            String gltsNumber = gltsSequenceRepository.getGltsSequence();

//            TradeService tradeService = cdtPaidEvent.getTradeService();
//            tradeService.tagStatus(TradeServiceStatus.APPROVED);
//
//            tradeServiceRepository.merge(tradeService);

            //This is not included in AMLA format 1.0 
            //as per dicussion with maam juliet 02/20/2015
//            CDTPaymentRequestPaidEvent cdtPaymentRequestPaidEvent = new CDTPaymentRequestPaidEvent(cdtPaymentRequest, totalAmountPaid, gltsNumber, cdtPaidEvent.getTradeService());
//            eventPublisher.publish(cdtPaymentRequestPaidEvent);
        }
    }

    @EventListener
    public void unpayCDT(CDTUnpaidEvent cdtUnpaidEvent) {
        System.out.println("cdt unpaid event is dispatched");
        CDTPaymentRequest cdtPaymentRequest = cdtPaymentRequestRepository.load(cdtUnpaidEvent.getIedieirdNumber());

//        cdtPaymentRequest.setStatus(CDTStatus.PENDING);
//        if (isUploadedDateToday(cdtPaymentRequest)) {
            cdtPaymentRequest.tagAsNew();
//        }

        cdtPaymentRequest.clearPaymentReferenceNumber();

        cdtPaymentRequestRepository.merge(cdtPaymentRequest);

        CDTPaymentRequestUnpaidEvent cdtPaymentRequestUnpaidEvent = new CDTPaymentRequestUnpaidEvent(cdtUnpaidEvent.getTradeServiceId());
        eventPublisher.publish(cdtPaymentRequestUnpaidEvent);
    }

    private Boolean isUploadedDateToday(CDTPaymentRequest cdtPaymentRequest) {
        Calendar calToday = Calendar.getInstance();
        calToday.set(Calendar.HOUR, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MILLISECOND, 0);

        Calendar calUploadedDate = Calendar.getInstance();
        calUploadedDate.setTime(cdtPaymentRequest.getDateUploaded());
        calUploadedDate.set(Calendar.HOUR, 0);
        calUploadedDate.set(Calendar.MINUTE, 0);
        calUploadedDate.set(Calendar.SECOND, 0);
        calUploadedDate.set(Calendar.MILLISECOND, 0);

        if (calToday.getTime().equals(calUploadedDate.getTime())) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @EventListener
    public void setCDTAdditionalDetails(PaymentHistoryUploadedEvent paymentHistoryUploadedEvent) {
        System.out.println("paymentHistoryUploadedEvent");
        CDTPaymentRequest cdtPaymentRequest = cdtPaymentRequestRepository.load(paymentHistoryUploadedEvent.getIedieirdNumber());

        cdtPaymentRequest.setAdditionalDetails(paymentHistoryUploadedEvent.getCollectionLine(),
                paymentHistoryUploadedEvent.getCollectionAgencyCode(),
                paymentHistoryUploadedEvent.getCollectionChannel());

        // always cash
        cdtPaymentRequest.setTransactionTypeCode(Boolean.FALSE);

        cdtPaymentRequestRepository.merge(cdtPaymentRequest);
    }

    @EventListener
    public void tagAsRemitted(CDTRemittanceCreatedEvent cdtRemittanceCreatedEvent) {
        System.out.println("tagAsRemitted event");


    }
}
