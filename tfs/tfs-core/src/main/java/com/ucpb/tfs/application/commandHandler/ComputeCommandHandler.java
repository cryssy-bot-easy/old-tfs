package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.ComputeCommand;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.reference.ValueHolder;
import com.ucpb.tfs.domain.reference.ValueHolderRepository;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

/**
 * User: giancarlo
 * Date: 10/10/12
 * Time: 1:41 PM
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ComputeCommandHandler implements CommandHandler<ComputeCommand> {

    @Inject
    ValueHolderRepository valueHolderRepository;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    TokenProvider tokenProvider;

    @Inject
    ChargesService chargesService;

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;


    @Override
    public void handle(ComputeCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        String chargeIdToCompute = (String) parameterMap.get("chargeToCompute");
        ChargeId chargeId = new ChargeId(chargeIdToCompute);

        // Load from repository using ETS number
        ServiceInstructionId etsNumber = new ServiceInstructionId((String) parameterMap.get("etsNumber"));
        TradeService tradeService = tradeServiceRepository.load(etsNumber);

        String username = command.getUserActiveDirectoryId() ;
        if(command.getUserActiveDirectoryId() ==  null){
            username = (String) parameterMap.get("username");
        }
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(username);
        ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);
        ets.updateDetails(parameterMap, userActiveDirectoryId);

        // Persist update
        serviceInstructionRepository.merge(ets);


        // Compute Charge Value
        BigDecimal result = chargesService.computeSpecificCharge(tradeService, parameterMap, chargeId);



        System.out.println("Charge Computed:" + result);
        System.out.println("Command Token:" + command.getToken());

        // Persist to ValueHolder table
        ValueHolder valueHolder = new ValueHolder(command.getToken(), result.toString());
        valueHolderRepository.save(valueHolder);

    }


    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside compute command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
