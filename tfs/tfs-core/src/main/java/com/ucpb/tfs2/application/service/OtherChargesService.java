package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.service.OtherChargesDetail;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

/**
 * User: IPCVal
 * Date: 8/2/13
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class OtherChargesService {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    public void saveDetail(
            TradeServiceId tradeServiceId,
            String transactionType,
            String chargeType,
            BigDecimal amount,
            Currency currency,
            String cwtFlag) throws Exception {

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        System.out.println("cwtFlag:"+cwtFlag);

        OtherChargesDetail otherChargesDetail = new OtherChargesDetail(transactionType, chargeType, amount, currency, cwtFlag);

        tradeService.addOtherChargeDetail(otherChargesDetail);

        tradeServiceRepository.saveOrUpdate(tradeService);
    }

    public void deleteDetail(TradeServiceId tradeServiceId, String id) throws Exception {

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        tradeService.deleteOtherChargeDetail(id);

        tradeServiceRepository.saveOrUpdate(tradeService);
    }

    public Set<OtherChargesDetail> getAllDetails(TradeServiceId tradeServiceId) throws Exception {

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        Set<OtherChargesDetail> otherChargesDetails = tradeService.getOtherChargesDetails();
        return otherChargesDetails;

    }
}
