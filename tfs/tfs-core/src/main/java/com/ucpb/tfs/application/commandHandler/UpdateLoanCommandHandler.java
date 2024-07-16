package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.application.command.UpdateLoanCommand;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaidEvent;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaymentReversedEvent;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.utils.MapUtil;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class UpdateLoanCommandHandler implements CommandHandler<UpdateLoanCommand> {

    private static final String PAID = "PAID";
    private static final String UNPAID = "UNPAID";
    private static final String PROCESSING = "PROCESSING";
    private static final String REJECTED = "REJECTED";
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TradeServiceRepository tradeServiceRepository;


    @Autowired
    DomainEventPublisher eventPublisher;

    @Override
    public void handle(UpdateLoanCommand updateLoanCommand) {
        System.out.println("updateLoanCommand");
        MapUtil mapUtil = new MapUtil(updateLoanCommand.getParameterMap());
        TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(mapUtil.getString("tradeServiceId")));
        Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
        PaymentDetail detail = payment.getPaymentDetail(PaymentInstrumentType.valueOf(mapUtil.getString("modeOfPayment")));

        if(detail != null){
            if(PAID.equalsIgnoreCase(mapUtil.getString("status"))){
                detail.paid();
                detail.setPnNumber(mapUtil.getAsLong("pnNumber"));
                if(payment.payItem(detail.getId())){
                    eventPublisher.publish(new PaymentItemPaidEvent(tradeService.getTradeServiceId(),tradeService.getDocumentNumber().toString(),detail));
                }
            }else if(UNPAID.equalsIgnoreCase(mapUtil.getString("status"))){
                payment.reverseItemPayment(detail.getId());
                TradeServiceId reversalTradeServiceId = null;
                if(!StringUtils.isEmpty(mapUtil.getString("reversalDENumber"))){
                    reversalTradeServiceId = new TradeServiceId(mapUtil.getString("reversalDENumber"));
                }
                eventPublisher.publish(new PaymentItemPaymentReversedEvent(tradeService.getTradeServiceId(),detail,reversalTradeServiceId));
            }else if(PROCESSING.equalsIgnoreCase(mapUtil.getString("status"))){
                detail.setForInquiry();
            }else if(REJECTED.equalsIgnoreCase(mapUtil.getString("status"))){
                detail.rejected();
            }
            paymentRepository.saveOrUpdate(detail);
        }

    }
}
