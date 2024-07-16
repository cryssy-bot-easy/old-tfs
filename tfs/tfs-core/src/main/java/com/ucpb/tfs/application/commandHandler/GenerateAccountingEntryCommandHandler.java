package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.GenerateAccountingEntryCommand;
import com.ucpb.tfs.application.service.AccountingService;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.domain.accounting.AccountingEntryActualRepository;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: giancarlo
 * Date: 11/7/12
 * Time: 7:16 PM
 */
public class GenerateAccountingEntryCommandHandler implements CommandHandler<GenerateAccountingEntryCommand> {

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ChargesService chargesService;

    @Autowired
    AccountingService accountingService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Autowired
    AccountingEntryActualRepository accountingEntryActualRepository;


    @Override
    public void handle(GenerateAccountingEntryCommand command) {

        /*
         * Command used to generate accounting entry actual into
         */


        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        // for approval of eTS, we do not route it to anyone specific so we dump it to a generic
        // TSD bucket
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId("TSD");

        TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        // Load from repository
        ServiceInstructionId etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
        ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);


        Payment paymentService = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
        Payment paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
        Payment paymentSettlement = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
        Payment paymentRefund = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.REFUND);
        accountingEntryActualRepository.delete(tradeServiceId);
        String gltsNumber = gltsSequenceRepository.getGltsSequence();
        accountingService.generateActualEntries(tradeService, ets, paymentProduct, paymentService, paymentSettlement, paymentRefund, gltsNumber,tradeService.getStatus().toString());
        gltsSequenceRepository.incrementGltsSequence();


    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside generate accounting entry command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }


}
