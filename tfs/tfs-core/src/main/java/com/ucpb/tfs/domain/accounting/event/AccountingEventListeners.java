package com.ucpb.tfs.domain.accounting.event;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.domain.accounting.AccountingEntryRepository;
import com.ucpb.tfs.domain.accounting.AccountingEventId;
import com.ucpb.tfs.domain.accounting.AccountingEventRepository;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.LCNegotiationRepository;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayableRepository;
import com.ucpb.tfs.domain.settlementaccount.AccountsReceivableRepository;
import com.ucpb.tfs.domain.settlementaccount.MarginalDepositRepository;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountRepository;
import com.ucpb.tfs.domain.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * User: giancarlo
 * Date: 10/5/12
 * Time: 6:50 PM
 */
@Component
public class AccountingEventListeners {

    @Autowired
    ChargesService chargesService;

    @Autowired
    AccountingEventRepository accountingEventRepository;

    @Autowired
    AccountingEntryRepository accountingEntryRepository;

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    TradeProductRepository tradeProductRepository;

    @Autowired
    DomainEventPublisher eventPublisher;




    @EventListener
    public void saveAccountingEvent(SaveAccountingEvent saveAccountingEvent){}

    @EventListener
    public void reverseAccountingEvent(ReverseAccountingEvent reverseAccountingEvent){}


}
