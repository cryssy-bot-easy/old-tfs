package com.ucpb.tfs.domain.service.event.charge;

import com.google.gson.Gson;

import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.service.ServiceCharge;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: giancarlo
 * Date: 12/20/12
 * Time: 2:36 PM
 */
@Component
public class ServiceChargeEventListener {

    @Autowired
    ChargesService chargesService;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;


    @EventListener
    public void updateCharges(FxNonLcSettlementEvent fxNonLcSettlementEvent) {

        System.out.println("\n INSIDE updateCharges(FxNonLcSettlementEvent fxNonLcSettlementEvent)\n");

        try {

//            ServiceInstruction ets = fxNonLcSettlementEvent.getServiceInstruction();
//            UserActiveDirectoryId userActiveDirectoryId = fxNonLcSettlementEvent.getUserActiveDirectoryId();
//
            TradeService tradeService = fxNonLcSettlementEvent.getTradeService();
//            //TradeService tradeService = tradeServiceRepository.load(ets.getServiceInstructionId());
//
            // a trade service item's lc currency or amount has been modified, we clear the previous charges
            tradeService.removeServiceCharges();
//
//            //remove saved fee values in map
//            ets.clearChargesSavedInDetails(fxNonLcSettlementEvent.getUserActiveDirectoryId());
//
            // a trade service item's lc currency or amount has been modified, we call the service to add charges to it
            chargesService.applyChargesNewStyle(tradeService, fxNonLcSettlementEvent.getDetails());

            tradeServiceRepository.saveOrUpdate(tradeService);
            System.out.println(fxNonLcSettlementEvent.getDetails());

            System.out.println("Persisted TradeService!");



        } catch (Exception e){
            e.printStackTrace();

        }

    }

//
//    @EventListener
//    public void updateCharges(DmNonLcSettlementEvent dmNonLcSettlementEvent) {
//
//        System.out.println("\n INSIDE updateCharges(DmNonLcSettlementEvent dmNonLcSettlementEvent)\n");
//
//        try {
//
//            ServiceInstruction ets = dmNonLcSettlementEvent.getServiceInstruction();
//            UserActiveDirectoryId userActiveDirectoryId = dmNonLcSettlementEvent.getUserActiveDirectoryId();
//
//            TradeService tradeService = dmNonLcSettlementEvent.getTradeService();
//            //TradeService tradeService = tradeServiceRepository.load(ets.getServiceInstructionId());
//
//            // a trade service item's lc currency or amount has been modified, we clear the previous charges
//            tradeService.removeServiceCharges();
//
//            //remove saved fee values in map
//            ets.clearChargesSavedInDetails(dmNonLcSettlementEvent.getUserActiveDirectoryId());
//
//            // a trade service item's lc currency or amount has been modified, we call the service to add charges to it
//            chargesService.applyChargesNewStyle(tradeService, dmNonLcSettlementEvent.getDetails());
//
//            tradeServiceRepository.saveOrUpdate(tradeService);
//            System.out.println("Persisted TradeService!");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//    }

//    @EventListener
//    public void includeIncludeChargesInServiceInstruction(ChargesTabSavedEvent chargesTabSavedEvent) {
//        System.out.println("###########################################");
//        System.out.println("INCLUDING CHARGES IN SERVICE INSTRUCTION...");
//        ServiceInstructionId serviceInstructionId = chargesTabSavedEvent.getServiceInstructionId();
//        TradeServiceId tradeServiceId = chargesTabSavedEvent.getTradeServiceId();
//
//        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
//
//        List<String> chargesMapList = new ArrayList<String>();
//
//        for (ServiceCharge serviceCharge : tradeService.getServiceCharge()) {
//            Map<String, String> serviceChargeMap = new HashMap<String, String>();
//
//            serviceChargeMap.put("chargeId", serviceCharge.getChargeId().toString());
//            serviceChargeMap.put("currency", serviceCharge.getCurrency().toString());
//            serviceChargeMap.put("amount", serviceCharge.getAmount().toString());
//
//            String serviceChargeMapString = serviceChargeMap.toString();
//
//            if (!chargesMapList.contains(serviceChargeMapString)) {
//                chargesMapList.add(serviceChargeMapString);
//            }
//        }
//
//        ServiceInstruction serviceInstruction = serviceInstructionRepository.load(serviceInstructionId);
//        serviceInstruction.includeChargesInDetails(chargesMapList);
//        System.out.println("###########################################");
//    }
}
