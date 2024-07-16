package com.ucpb.tfs.domain.service.event.charge;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.service.TradeService;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.service.TradeService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * User: giancarlo
 * Date: 12/21/12
 * Time: 5:00 PM
 */
public class DmNonLcSettlementEvent implements DomainEvent {

    private TradeService tradeService;
    private ServiceInstruction serviceInstruction;
    private UserActiveDirectoryId userActiveDirectoryId;

    private Map<String, Object> details;


    public DmNonLcSettlementEvent(){}


    public DmNonLcSettlementEvent(TradeService tradeService, Map<String, Object> details){
        System.out.println("ANGOL ANGOL ANGOL 1");
        this.tradeService = tradeService;
        this.details = details;
    }

    public DmNonLcSettlementEvent(TradeService tradeService, ServiceInstruction serviceInstruction, UserActiveDirectoryId userActiveDirectoryId, Map<String, Object> details) {
        System.out.println("ANGOL ANGOL ANGOL 1");
        this.tradeService = tradeService;
        this.serviceInstruction = serviceInstruction;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.details = details;
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public ServiceInstruction getServiceInstruction() {
        return serviceInstruction;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public static Boolean determineCompleteness(Map<String, Object> details){

        //Bank Commission
        if(!checkMapValueForBigDecimal(details, "productAmount")) {return Boolean.FALSE;}

        //Documentary Stamps
        if(!checkMapValueForString(details, "TR_LOAN_FLAG")) {return Boolean.FALSE;}

        //System.out.println("IT IS COMPLETE !!!!");
        return Boolean.TRUE;
    }

    private static Boolean checkMapValueForString(Map<String, Object> details, String key) {
        Object obj = details.get(key);
        if (obj != null && (obj.toString().equalsIgnoreCase("Y") || obj.toString().equalsIgnoreCase("N"))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private static Boolean checkMapValueForBigDecimal(Map<String, Object> details, String key) {
        Object obj = details.get(key);
        if (obj != null) {
            String amountStr = obj.toString();
            try {
                BigDecimal amount = new BigDecimal(amountStr);
            } catch (Exception e) {
                return Boolean.FALSE;
            }
        } else {
        }
        return Boolean.TRUE;
    }

}
