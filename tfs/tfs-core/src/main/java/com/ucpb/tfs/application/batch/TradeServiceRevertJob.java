package com.ucpb.tfs.application.batch;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.batch.job.SpringJob;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaymentReversedEvent;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.ucpb.tfs.domain.payment.PaymentInstrumentType.*;

import java.util.List;

/**
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TradeServiceRevertJob implements SpringJob {

    private PaymentRepository paymentRepository;

    private TradeServiceRepository tradeServiceRepository;

    private TaskRepository taskRepository;

    private DomainEventPublisher eventPublisher;

    @Override
    public void execute() {
        List<TradeService> unapprovedTradeServices = tradeServiceRepository.getUnapprovedTradeServices();

        for(TradeService tradeService : unapprovedTradeServices){
            reversePayment(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
            reversePayment(tradeService.getTradeServiceId(), ChargeType.SERVICE);

            // Unpay TradeService if payments have been reversed.
            if (!tradeService.getPaymentStatus().equals(PaymentStatus.NO_PAYMENT_REQUIRED)) {
                tradeService.unPay();
            }
            tradeService.resetApprovers();
            tradeService.updateStatus(TradeServiceStatus.PENDING,tradeService.getCreatedBy());

            tradeServiceRepository.update(tradeService);

            Task tsdTask = taskRepository.load(new TaskReferenceNumber(tradeService.getTradeServiceId().toString()));
            tsdTask.setUserActiveDirectoryId(new UserActiveDirectoryId(tradeService.getCreatedBy().toString()));
            tsdTask.updateStatus(TaskStatus.PENDING,new UserActiveDirectoryId(tradeService.getCreatedBy().toString()));

            taskRepository.persist(tsdTask);
        }

    }


    @Override
    public void execute(String reportDate) {
        //TODO
        List<TradeService> unapprovedTradeServices = tradeServiceRepository.getUnapprovedTradeServices();

        for(TradeService tradeService : unapprovedTradeServices){
            reversePayment(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
            reversePayment(tradeService.getTradeServiceId(), ChargeType.SERVICE);

            // Unpay TradeService if payments have been reversed.
            if (!tradeService.getPaymentStatus().equals(PaymentStatus.NO_PAYMENT_REQUIRED)) {
                tradeService.unPay();
            }
            tradeService.resetApprovers();
            tradeService.updateStatus(TradeServiceStatus.PENDING,tradeService.getCreatedBy());

            tradeServiceRepository.update(tradeService);

            Task tsdTask = taskRepository.load(new TaskReferenceNumber(tradeService.getTradeServiceId().toString()));
            tsdTask.setUserActiveDirectoryId(new UserActiveDirectoryId(tradeService.getCreatedBy().toString()));
            tsdTask.updateStatus(TaskStatus.PENDING,new UserActiveDirectoryId(tradeService.getCreatedBy().toString()));

            taskRepository.persist(tsdTask);
        }

    }

    public void setPaymentRepository(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void setTradeServiceRepository(TradeServiceRepository tradeServiceRepository) {
        this.tradeServiceRepository = tradeServiceRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private void reversePayment(TradeServiceId id,ChargeType chargeType){
        Payment payment = paymentRepository.get(id, chargeType);
        if(payment != null){
            for(PaymentDetail payItem : payment.getDetails()){
                if(payItem.isPaid() && !CASA.equals(payItem.getPaymentInstrumentType()) && !payItem.getPaymentInstrumentType().isLoan()){
                    payment.reverseItemPayment(payItem.getId());
                    eventPublisher.publish(new PaymentItemPaymentReversedEvent(id,payItem,null));
                }
            }
            payment.unPay();
            paymentRepository.saveOrUpdate(payment);
        }
    }

    public void setEventPublisher(DomainEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
