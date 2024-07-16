package com.ucpb.tfs.application.commandHandler;

/**
 *
 * @author Marvin Volante <marvin.volante@incuventure.net>
 *
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveChargesFormCommand;
import com.ucpb.tfs.application.service.ServiceChargeService;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.event.TradeServiceUpdatedEvent;
import com.ucpb.tfs.domain.service.event.charge.ChargesTabSavedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveChargesFormCommandHandler implements CommandHandler<SaveChargesFormCommand> {

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;
    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Inject
    PaymentRepository paymentRepository;

    @Override
    public void handle(SaveChargesFormCommand command) {

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        System.out.println("referenceType:" + parameterMap.get("referenceType").toString() + "|");


        try {

            String username;
            UserActiveDirectoryId userActiveDirectoryId;

            if (command.getUserActiveDirectoryId() == null) {
                username = parameterMap.get("username").toString();
                userActiveDirectoryId = new UserActiveDirectoryId(username);
            } else {
                userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
            }

            /*
            * Currently this is only invoked from ETS, but check just the same :)
            */
            System.out.println("true or false:" + parameterMap.get("referenceType").toString().equalsIgnoreCase("ETS"));

            if (parameterMap.get("referenceType").toString().equalsIgnoreCase("ETS")) {

                System.out.println("within if");
                // Load from repository using ETS number
                ServiceInstructionId etsNumber = new ServiceInstructionId((String) parameterMap.get("etsNumber"));
                TradeService tradeService = tradeServiceRepository.load(etsNumber);
                tradeService = TradeServiceService.setSpecialRates(tradeService, tradeService.getDetails());

                // Save charges

                // 1) Delete charges first before saving a new set
                tradeServiceRepository.deleteServiceCharges(tradeService.getTradeServiceId());

                //1.1) get all fees into one map
                //1.2) compare with current service charges
                ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

//                parameterMap.remove("amount");
//                parameterMap.remove("amountFrom");
//                parameterMap.remove("productAmount");
                if(parameterMap.containsKey("outstandingBalance") && tradeService.getDocumentClass().equals(DocumentClass.LC) && tradeService.getServiceType().equals(ServiceType.AMENDMENT) ){
                    parameterMap.put("amount",parameterMap.get("outstandingBalance"));
                }
                ets.updateDetails(parameterMap, userActiveDirectoryId);

                // 2) Now add Service Charges
                tradeService = TradeServiceService.addServiceChargesForParamMap(tradeService, ets.getDetails(), parameterMap, userActiveDirectoryId);

                tradeService.getDetails().put("chargesOverridenFlag", "Y");

                //Save Rates in Trade Service
                tradeService.updateServiceChargeRates(parameterMap);
                //tradeService.printRates();
                
                tradeService = verifyPaymentStatus(tradeService, ServiceChargeService.getTotalServiceCharges(parameterMap));
                                
                // 3) Use saveOrUpdate to persist
                tradeServiceRepository.saveOrUpdate(tradeService);


                // Add to token registry
                tokenProvider.addTokenForId(command.getToken(), tradeService.getDocumentNumber().toString());

                // Fire event
                // This updates Service Charges so no status is passed.
                TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, userActiveDirectoryId);
                eventPublisher.publish(tradeServiceUpdatedEvent);

                System.out.println("DISPATCHING chargesTabSavedEvent");
                ChargesTabSavedEvent chargesTabSavedEvent = new ChargesTabSavedEvent(tradeService.getServiceInstructionId(), tradeService.getTradeServiceId());
                eventPublisher.publish(chargesTabSavedEvent);
            } else if (parameterMap.get("referenceType").toString().equalsIgnoreCase("PAYMENT")) {

                //TODO Fix this. SaveChargesFormCommandHandler must overwrite charges
                System.out.println("TODO Fix this. SaveChargesFormCommandHandler must overwrite charges");

                // Load from repository using ETS number
                TradeServiceId tradeServiceId = new TradeServiceId((String) parameterMap.get("tradeServiceId"));
                TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
                tradeService = TradeServiceService.setSpecialRates(tradeService, tradeService.getDetails());

                // 1) Delete charges first before saving a new set
                tradeServiceRepository.deleteServiceCharges(tradeService.getTradeServiceId());
                parameterMap.remove("mainCifNumber");
                parameterMap.remove("mainCifName");

                parameterMap.remove("amount");
                parameterMap.remove("amountFrom");
                parameterMap.remove("productAmount");
                
                // for some reason, the facility type is always becoming null when saved here. So, this is just an ugly fix.
                if(parameterMap.get("facilityType") != null)
                if(parameterMap.get("facilityType").toString().isEmpty() && !tradeService.getFacilityType().isEmpty()){
                	parameterMap.put("facilityType", tradeService.getFacilityType());
                }

                //1.1) get all fees into one map
                //1.2) compare with current service charges
                tradeService.updateDetails(parameterMap, userActiveDirectoryId);

                // 2) Now add Service Charges
                tradeService = TradeServiceService.addServiceChargesForParamMap(tradeService, tradeService.getDetails(), parameterMap, userActiveDirectoryId);

                tradeService.getDetails().put("chargesOverridenFlag", "Y"); //USED by UI/TFS-WEB to determine if charges displayed will be computed or retrieved from ServiceCharge Table

                //Save Rates in Trade Service
                tradeService.updateServiceChargeRates(parameterMap);
                //tradeService.printRates();
                
                tradeService = verifyPaymentStatus(tradeService, ServiceChargeService.getTotalServiceCharges(parameterMap));

                // 3) Use saveOrUpdate to persist
                tradeServiceRepository.saveOrUpdate(tradeService);


                // Add to token registry
                tokenProvider.addTokenForId(command.getToken(), tradeService.getDocumentNumber().toString());
                
                // Fire event
                // This updates Service Charges so no status is passed.
                TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, userActiveDirectoryId);
                eventPublisher.publish(tradeServiceUpdatedEvent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside save charges form command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
    
    private TradeService verifyPaymentStatus(TradeService tradeService, BigDecimal totalServiceCharges){
    	System.out.println("Setting up service Charges Payment...");
    	System.out.println("totalServiceCharges: " + totalServiceCharges.toString());
    	Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SERVICE);
    	
    	System.out.println("has payment? " + payment != null);
        if (payment == null) {
            payment = new Payment(tradeService.getTradeServiceId(), ChargeType.SERVICE);
        }
        if (totalServiceCharges.doubleValue() == 0.00)
        	payment.setStatus(PaymentStatus.NO_PAYMENT_REQUIRED);
        else payment.unPay();
        
        Boolean hasOtherPayments = Boolean.FALSE;
        
        Payment productPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
        Payment settlementPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.SETTLEMENT);
        
        if (productPayment != null) {
            if (productPayment.getDetails() != null && productPayment.getDetails().size() > 0) {
                hasOtherPayments = Boolean.TRUE;
            }
        }

        if (settlementPayment != null) {
            if (settlementPayment.getDetails() != null && settlementPayment.getDetails().size() > 0) {
                hasOtherPayments = Boolean.TRUE;
            }
        }
        
        if (!PaymentStatus.NO_PAYMENT_REQUIRED.equals(payment.getStatus())){
        	hasOtherPayments = Boolean.TRUE;
        }
        
        if (!hasOtherPayments) {
            tradeService.setAsNoPaymentRequired();
        } else {
        	int otherPayments = 0;
        	int otherPaidPayments = 0;
        	if (payment != null) {
        		otherPayments++;
        		if(PaymentStatus.PAID.equals(payment.getStatus()) || PaymentStatus.NO_PAYMENT_REQUIRED.equals(payment.getStatus())){
        			otherPaidPayments++;
        		}
        	}
        	if (productPayment != null) {
        		otherPayments++;
        		if(PaymentStatus.PAID.equals(productPayment.getStatus())){
        			otherPaidPayments++;
        		}
        	}
        	if (settlementPayment != null) {
        		otherPayments++;
        		if(PaymentStatus.PAID.equals(settlementPayment.getStatus())){
        			otherPaidPayments++;
        		}
        	}
        	if(otherPayments == otherPaidPayments){
        		tradeService.paid();
        	} else {
        		tradeService.unPay();
        	}
        }
        
        paymentRepository.saveOrUpdate(payment);
    	return tradeService;
    }
}
