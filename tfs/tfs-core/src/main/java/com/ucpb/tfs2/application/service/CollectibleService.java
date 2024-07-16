package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: IPCVal
 * Date: 8/1/13
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class CollectibleService {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    public void saveProductCollectibleDetails(TradeServiceId tradeServiceId, List<Map<String, Object>> newProductCollectibleListMap) throws Exception {

        // 1) Load TradeService
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        // 2) Assemble the ProductCollectibleDetails
        Set<ProductCollectibleDetail> productCollectibleDetails = new HashSet<ProductCollectibleDetail>();

        for(Map<String, Object> newProductCollectibleMap : newProductCollectibleListMap) {

            ChargeId chargeId = new ChargeId((String) newProductCollectibleMap.get("chargeId"));

            BigDecimal amount = new BigDecimal(((String) newProductCollectibleMap.get("amount")).replaceAll(",", ""));
            Currency currency = Currency.getInstance((String) newProductCollectibleMap.get("currency"));

            BigDecimal originalAmount = new BigDecimal(((String) newProductCollectibleMap.get("originalAmount")).replaceAll(",",""));
            Currency originalCurrency = Currency.getInstance((String) newProductCollectibleMap.get("originalCurrency"));

            BigDecimal defaultAmount = new BigDecimal(((String) newProductCollectibleMap.get("defaultAmount")).replaceAll(",",""));

            String overridenFlag = (String) newProductCollectibleMap.get("overridenFlag");
            String transactionType = (String) newProductCollectibleMap.get("transactionType");
            BigDecimal newSpecialRateThirdToUsd = (newProductCollectibleMap.get("newSpecialRateThirdToUsd") != null) ? new BigDecimal((String) newProductCollectibleMap.get("newSpecialRateThirdToUsd")) : null;
            BigDecimal newSpecialRateThirdToPhp = (newProductCollectibleMap.get("newSpecialRateThirdToPhp") != null) ? new BigDecimal((String) newProductCollectibleMap.get("newSpecialRateThirdToPhp")) : null;
            BigDecimal newSpecialRateUsdToPhp = (newProductCollectibleMap.get("newSpecialRateUsdToPhp") != null) ? new BigDecimal((String) newProductCollectibleMap.get("newSpecialRateUsdToPhp")) : null;
            BigDecimal newUrr = (newProductCollectibleMap.get("newUrr") != null) ? new BigDecimal((String) newProductCollectibleMap.get("newUrr")) : null;
            BigDecimal collectibleAmountInDefaultCurrency = new BigDecimal(((String) newProductCollectibleMap.get("collectibleAmountInDefaultCurrency")).replaceAll(",",""));
            BigDecimal collectibleAmountInOriginalCurrency = new BigDecimal(((String) newProductCollectibleMap.get("collectibleAmountInOriginalCurrency")).replaceAll(",",""));
            BigDecimal newCollectibleAmountInOriginalCurrency = new BigDecimal(((String) newProductCollectibleMap.get("newCollectibleAmountInOriginalCurrency")).replaceAll(",",""));

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IN saveProductCollectibleDetails()");
            System.out.println("******************************* chargeId = " + chargeId);
            System.out.println("******************************* amount = " + amount);
            System.out.println("******************************* currency = " + currency);
            System.out.println("******************************* originalAmount = " + originalAmount);
            System.out.println("******************************* originalCurrency = " + originalCurrency);
            System.out.println("******************************* defaultAmount = " + defaultAmount);
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            System.out.println("******************************* overridenFlag = " + overridenFlag);
            System.out.println("******************************* transactionType = " + transactionType);
            System.out.println("******************************* newSpecialRateThirdToUsd = " + newSpecialRateThirdToUsd);
            System.out.println("******************************* newSpecialRateThirdToPhp = " + newSpecialRateThirdToPhp);
            System.out.println("******************************* newSpecialRateUsdToPhp = " + newSpecialRateUsdToPhp);
            System.out.println("******************************* newUrr = " + newUrr);
            System.out.println("******************************* collectibleAmountInDefaultCurrency = " + collectibleAmountInDefaultCurrency);
            System.out.println("******************************* collectibleAmountInOriginalCurrency = " + collectibleAmountInOriginalCurrency);
            System.out.println("******************************* newCollectibleAmountInOriginalCurrency = " + newCollectibleAmountInOriginalCurrency);

            ProductCollectibleDetail productCollectibleDetail = new ProductCollectibleDetail(
                    tradeServiceId,
                    transactionType,
                    chargeId,
                    amount,
                    originalAmount,
                    defaultAmount,
                    currency,
                    originalCurrency,
                    newSpecialRateThirdToUsd,
                    newSpecialRateThirdToPhp,
                    newSpecialRateUsdToPhp,
                    newUrr,
                    collectibleAmountInDefaultCurrency,
                    collectibleAmountInOriginalCurrency,
                    newCollectibleAmountInOriginalCurrency);

            productCollectibleDetails.add(productCollectibleDetail);
        }

        // 3) Set ProductCollectibleDetails to TradeService
        tradeService.updateProductCollectibleDetails(productCollectibleDetails);

        // 4) Save TradeService
        tradeServiceRepository.saveOrUpdate(tradeService);
    }

    public void saveChargesCollectibleDetails(TradeServiceId tradeServiceId, List<Map<String, Object>> newChargesCollectibleListMap, String originalTradeServiceId) throws Exception {

        // 1) Load TradeService, including the ServiceCharges
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        // 2) Assemble the new ServiceCharges
        Set<ServiceCharge> serviceChargeSet = new HashSet<ServiceCharge>();

        for(Map<String, Object> newChargesMap : newChargesCollectibleListMap) {

            ChargeId chargeId = new ChargeId((String)newChargesMap.get("chargeId"));

            BigDecimal amount = new BigDecimal(((String)newChargesMap.get("amount")).replaceAll(",", ""));
            Currency currency = Currency.getInstance((String)newChargesMap.get("currency"));

            BigDecimal originalAmount = new BigDecimal(((String)newChargesMap.get("originalAmount")).replaceAll(",",""));
            Currency originalCurrency = Currency.getInstance((String)newChargesMap.get("originalCurrency"));

            BigDecimal defaultAmount = (newChargesMap.get("defaultAmount") != null) ? new BigDecimal(((String)newChargesMap.get("defaultAmount")).replaceAll(",","")) : null;
            BigDecimal nocwtAmount = (newChargesMap.get("nocwtAmount") != null) ? new BigDecimal(((String)newChargesMap.get("nocwtAmount")).replaceAll(",","")) : null;

            String overridenFlag = (String)newChargesMap.get("overridenFlag");
            String transactionType = (String)newChargesMap.get("transactionType");
            BigDecimal newSpecialRateThirdToUsd = (newChargesMap.get("newSpecialRateThirdToUsd") != null) ? new BigDecimal((String)newChargesMap.get("newSpecialRateThirdToUsd")) : null;
            BigDecimal newSpecialRateThirdToPhp = (newChargesMap.get("newSpecialRateThirdToPhp") != null) ? new BigDecimal((String)newChargesMap.get("newSpecialRateThirdToPhp")) : null;
            BigDecimal newSpecialRateUsdToPhp = (newChargesMap.get("newSpecialRateUsdToPhp") != null) ? new BigDecimal((String)newChargesMap.get("newSpecialRateUsdToPhp")) : null;
            BigDecimal newUrr = (newChargesMap.get("newUrr") != null) ? new BigDecimal((String)newChargesMap.get("newUrr")) : null;
            BigDecimal collectibleAmountInDefaultCurrency = new BigDecimal(newChargesMap.get("collectibleAmountInDefaultCurrency").toString().replaceAll(",",""));
            BigDecimal collectibleAmountInOriginalCurrency = new BigDecimal(newChargesMap.get("collectibleAmountInOriginalCurrency").toString().replaceAll(",",""));
            BigDecimal newCollectibleAmountInOriginalCurrency = new BigDecimal(newChargesMap.get("newCollectibleAmountInOriginalCurrency").toString().replaceAll(",",""));

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IN saveChargesCollectibleDetails()");
            System.out.println("******************************* chargeId = " + chargeId);
            System.out.println("******************************* amount = " + amount);
            System.out.println("******************************* currency = " + currency);
            System.out.println("******************************* originalAmount = " + originalAmount);
            System.out.println("******************************* originalCurrency = " + originalCurrency);
            System.out.println("******************************* defaultAmount = " + defaultAmount);
            System.out.println("******************************* nocwtAmount = " + nocwtAmount);
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            System.out.println("******************************* overridenFlag = " + overridenFlag);
            System.out.println("******************************* transactionType = " + transactionType);
            System.out.println("******************************* newSpecialRateThirdToUsd = " + newSpecialRateThirdToUsd);
            System.out.println("******************************* newSpecialRateThirdToPhp = " + newSpecialRateThirdToPhp);
            System.out.println("******************************* newSpecialRateUsdToPhp = " + newSpecialRateUsdToPhp);
            System.out.println("******************************* newUrr = " + newUrr);
            System.out.println("******************************* collectibleAmountInDefaultCurrency = " + collectibleAmountInDefaultCurrency);
            System.out.println("******************************* collectibleAmountInOriginalCurrency = " + collectibleAmountInOriginalCurrency);
            System.out.println("******************************* newCollectibleAmountInOriginalCurrency = " + newCollectibleAmountInOriginalCurrency);

            ServiceCharge serviceCharge = new ServiceCharge(tradeServiceId, chargeId, amount, currency, originalAmount, originalCurrency, defaultAmount, overridenFlag, nocwtAmount);

            serviceCharge.setServiceChargeCollectibleDetail(transactionType, newSpecialRateThirdToUsd, newSpecialRateThirdToPhp, newSpecialRateUsdToPhp, newUrr, collectibleAmountInDefaultCurrency, collectibleAmountInOriginalCurrency, newCollectibleAmountInOriginalCurrency);

            serviceChargeSet.add(serviceCharge);
        }

        tradeService.getDetails().put("originalTradeServiceId",originalTradeServiceId);
        // 3) Set new ServiceCharges to TradeService
        tradeService.updateServiceChargesForCollectible(serviceChargeSet);

        // 4) Save TradeService
        tradeServiceRepository.saveOrUpdate(tradeService);
    }

    public void deleteServiceCharges(TradeServiceId tradeServiceId) throws Exception {

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        tradeService.removeServiceCharges();
        tradeServiceRepository.saveOrUpdate(tradeService);
    }

    public void updatePaymentStatus(TradeServiceId tradeServiceId, BigDecimal totalCollectible) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        if (totalCollectible.compareTo(BigDecimal.ZERO) <= 0) {
            tradeService.setPaymentStatus(PaymentStatus.NO_PAYMENT_REQUIRED);
        } else {
            tradeService.setPaymentStatus(PaymentStatus.UNPAID);
        }
    }
}
