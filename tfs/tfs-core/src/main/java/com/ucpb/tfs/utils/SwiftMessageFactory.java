package com.ucpb.tfs.utils;

import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.ucpb.tfs.domain.service.enumTypes.DocumentClass.*;
import static com.ucpb.tfs.domain.service.enumTypes.DocumentClass.DA;
import static com.ucpb.tfs.domain.service.enumTypes.DocumentClass.DP;
import static com.ucpb.tfs.domain.service.enumTypes.DocumentType.DOMESTIC;
import static com.ucpb.tfs.domain.service.enumTypes.DocumentType.FOREIGN;
import static com.ucpb.tfs.domain.service.enumTypes.ServiceType.*;
import static com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus.APPROVED;
import static com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus.POST_APPROVED;

/**
 */
@Component
public class SwiftMessageFactory {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SwiftMessageBuilder builder;


    public List<RawSwiftMessage> generateSwiftMessages(TradeService tradeService) {

        List<RawSwiftMessage> messagesToSend = new ArrayList<RawSwiftMessage>();

        if (DP.equals(tradeService.getDocumentClass()) && SETTLEMENT.equals(tradeService.getServiceType())) {

            if(DOMESTIC.equals(tradeService.getDocumentType())){
                Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
                if (payment != null && payment.hasSwift()) {
                    messagesToSend.addAll(builder.build("103", tradeService));
                }
            }else if(FOREIGN.equals(tradeService.getDocumentType())){
                messagesToSend.addAll(builder.build("202", tradeService));
                messagesToSend.addAll(builder.build("400", tradeService));
            }

        } else if (FOREIGN.equals(tradeService.getDocumentType()) && (DR.equals(tradeService.getDocumentClass()) || OA.equals(tradeService.getDocumentClass())) && SETTLEMENT.equals(tradeService.getServiceType())) {
            messagesToSend.addAll(builder.build("103", tradeService));
        } else if (DA.equals(tradeService.getDocumentClass()) && SETTLEMENT.equals(tradeService.getServiceType())) {
            messagesToSend.addAll(builder.build("202", tradeService));
            messagesToSend.addAll(builder.build("400", tradeService));
        } else if (DA.equals(tradeService.getDocumentClass()) && NEGOTIATION_ACCEPTANCE.equals(tradeService.getServiceType())) {
            messagesToSend.addAll(builder.build("412", tradeService));
        } else if (DP.equals(tradeService.getDocumentClass()) && NEGOTIATION.equals(tradeService.getServiceType())){
            messagesToSend.addAll(builder.build("410", tradeService));
        } else if(DA.equals(tradeService.getDocumentClass()) && NEGOTIATION_ACKNOWLEDGEMENT.equals(tradeService.getServiceType())){
            messagesToSend.addAll(builder.build("410", tradeService));
        } else if(DA.equals(tradeService.getDocumentClass()) && NEGOTIATION_ACCEPTANCE.equals(tradeService.getServiceType())){
            messagesToSend.addAll(builder.build("412", tradeService));

        }

        return messagesToSend;

    }

}
