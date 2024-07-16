package com.ucpb.tfs.domain.service.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.Charge;
import com.ucpb.tfs.domain.reference.ChargeRepository;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 7/29/13
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */

@Transactional
public class HibernateServiceChargeRepository implements ServiceChargeRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Autowired
    private TradeServiceRepository tradeServiceRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    @Override
    public List<Map<String, Object>> getServiceChargeBaseAmount(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        List<Map<String, Object>> serviceChargeBaseAmounts = new ArrayList<Map<String, Object>>();

        for (ServiceCharge serviceCharge: tradeService.getServiceCharges()) {
            Map<String, Object> serviceChargeMap = new HashMap<String, Object>();

            if ("N".equals(serviceCharge.getOverridenFlag())) {
                serviceChargeMap.put("baseAmount", serviceCharge.getDefaultAmount());
                serviceChargeMap.put("baseCurrency", "PHP");
            } else {
                serviceChargeMap.put("baseAmount", serviceCharge.getOriginalAmount());
                serviceChargeMap.put("baseCurrency", serviceCharge.getOriginalCurrency());
            }


            Charge charge = chargeRepository.load(serviceCharge.getChargeId());
            serviceChargeMap.put("chargeId", serviceCharge.getChargeId().toString());
            serviceChargeMap.put("chargeDescription", charge.getDescription());

            serviceChargeMap.put("settlementCurrency", serviceCharge.getOriginalCurrency().toString());

            serviceChargeBaseAmounts.add(serviceChargeMap);
        }

        return serviceChargeBaseAmounts;
    }

    @Override
    public List<Map<String, Object>> getSavedNewServiceCharges(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        Date transactionDate = tradeService.getModifiedDate();
        ServiceType transactionType = tradeService.getServiceType();

        List<Map<String, Object>> newServiceCharges = new ArrayList<Map<String, Object>>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        for (ServiceCharge serviceCharge: tradeService.getServiceCharges()) {
            Map<String, Object> serviceChargeMap = new HashMap<String, Object>();

            serviceChargeMap.put("id", serviceCharge.getId());
            serviceChargeMap.put("transactionDate", simpleDateFormat.format(transactionDate));
            serviceChargeMap.put("transactionType", serviceCharge.getTransactionType());

            Charge charge = chargeRepository.load(serviceCharge.getChargeId());
            serviceChargeMap.put("chargeType", charge.getDescription());

            serviceChargeMap.put("settlementCurrency", serviceCharge.getOriginalCurrency());
            serviceChargeMap.put("oldAmount", serviceCharge.getOriginalAmount());
            serviceChargeMap.put("newAmount", serviceCharge.getNewRefundAmount());
            serviceChargeMap.put("refundAmount", serviceCharge.getRefundAmount());


            newServiceCharges.add(serviceChargeMap);
        }

        return newServiceCharges;
    }

    @Override
    public Map<String, Object> getSavedNewCollectibleCharges(TradeServiceId tradeServiceId) {
        System.out.println(tradeServiceId);
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        System.out.println(tradeService);
        Date transactionDate = tradeService.getModifiedDate();
        ServiceType transactionType = tradeService.getServiceType();

        List<Map<String, Object>> newServiceCharges = new ArrayList<Map<String, Object>>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        Currency currency = null;

        for (ServiceCharge serviceCharge: tradeService.getServiceCharges()) {
            Map<String, Object> serviceChargeMap = new HashMap<String, Object>();

            serviceChargeMap.put("id", serviceCharge.getId());
            serviceChargeMap.put("transactionDate", simpleDateFormat.format(transactionDate));
            serviceChargeMap.put("transactionType", serviceCharge.getTransactionType());

            Charge charge = chargeRepository.load(serviceCharge.getChargeId());
            serviceChargeMap.put("chargeType", charge.getDescription());

            serviceChargeMap.put("settlementCurrency", serviceCharge.getOriginalCurrency());
            serviceChargeMap.put("oldAmount", serviceCharge.getOriginalAmount());
            serviceChargeMap.put("newAmount", serviceCharge.getNewCollectibleAmount());
            serviceChargeMap.put("collectibleAmount", serviceCharge.getCollectibleAmount());

            currency = serviceCharge.getOriginalCurrency();
            newServiceCharges.add(serviceChargeMap);
        }

        Map<String, Object> returnMap = new HashMap<String, Object>();

        returnMap.put("charges", newServiceCharges);
        returnMap.put("currency", currency);

        return returnMap;
    }
}
