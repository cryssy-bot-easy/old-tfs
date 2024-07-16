package com.ucpb.tfs2.application.service;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.mt.OutgoingMTRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.TradeServiceSavedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class OutgoingMTService {

    @Autowired
    OutgoingMTRepository outgoingMTRepository;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    public TradeService saveOutgoingMT(String userid, Map details) {

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(userid);

        DateFormat simpleDF = new SimpleDateFormat("ddHHmmss");
        String mtTradeReference = "MT" + simpleDF.format(new Date());

        TradeService tradeService;
        tradeService = tradeServiceRepository.load(new TradeServiceReferenceNumber(mtTradeReference));

        tradeService = new TradeService(new DocumentNumber(""), new TradeProductNumber(""), DocumentClass.MT, null, null, null, ServiceType.CREATE, userActiveDirectoryId, mtTradeReference);
        tradeService.setDetails(details);
        tradeService.tagStatus(TradeServiceStatus.PENDING);

        tradeServiceRepository.persist(tradeService);

        // dispatch a tradeservice saved event so that routing info is created
        TradeServiceSavedEvent tradeServiceSavedEvent = new TradeServiceSavedEvent(tradeService.getTradeServiceId(), null, TradeServiceStatus.PENDING, userActiveDirectoryId);
        eventPublisher.publish(tradeServiceSavedEvent);

        // OLD CODE: create a new one
        // OutgoingMT outgoingMT = new OutgoingMT(userid, messageType, destinationBankCode, details);
        // outgoingMTRepository.persist(outgoingMT);

        return tradeService;

    }
       
    @SuppressWarnings("unchecked")
	public Map<String,Object> getTradeServiceForMt103(String documentNumber){
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	result = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(documentNumber),
    			ServiceType.NEGOTIATION, DocumentType.DOMESTIC, DocumentClass.LC);
    	if(result == null || result.isEmpty()){
    		result = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(documentNumber),
        			ServiceType.SETTLEMENT, DocumentType.DOMESTIC, DocumentClass.DP);
    	}else if(result == null || result.isEmpty()){
    		result = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(documentNumber),
    				ServiceType.SETTLEMENT, DocumentType.DOMESTIC, DocumentClass.UA);
    	}else if(result == null || result.isEmpty()){
    		result = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(documentNumber),
    				ServiceType.SETTLEMENT, DocumentType.FOREIGN, DocumentClass.DR);
    	}else if(result == null || result.isEmpty()){
    		result = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(documentNumber),
    				ServiceType.SETTLEMENT, DocumentType.FOREIGN, DocumentClass.OA);
    	}else if(result == null || result.isEmpty()){
    		result = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(documentNumber),
    				ServiceType.PAYMENT, DocumentType.FOREIGN, DocumentClass.IMPORT_ADVANCE);
    	}else if(result == null || result.isEmpty()){
    		//null if empty
    		result = null;
    	}
    	return result;
    }
}