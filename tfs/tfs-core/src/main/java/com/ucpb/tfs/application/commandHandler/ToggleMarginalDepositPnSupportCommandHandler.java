package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ucpb.tfs.application.command.ToggleMarginalDepositPnSupportCommand;
import com.ucpb.tfs.domain.settlementaccount.MarginalDeposit;
import com.ucpb.tfs.domain.settlementaccount.MarginalDepositRepository;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.MdPnSupport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 9/13/12
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ToggleMarginalDepositPnSupportCommandHandler implements CommandHandler<ToggleMarginalDepositPnSupportCommand>  {

    @Inject
    MarginalDepositRepository marginalDepositRepository;

    @Override
    public void handle(ToggleMarginalDepositPnSupportCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        SettlementAccountNumber settlementAccountNumber = new SettlementAccountNumber((String)parameterMap.get("documentNumber"));
        MarginalDeposit md = marginalDepositRepository.load(settlementAccountNumber);

        MdPnSupport mdPnSupport = MdPnSupport.valueOf((String)parameterMap.get("pnSupportFlag"));
        md.upDatePnSupport(mdPnSupport);

        marginalDepositRepository.update(md);
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside toggle marginal deposit pn support command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
}
