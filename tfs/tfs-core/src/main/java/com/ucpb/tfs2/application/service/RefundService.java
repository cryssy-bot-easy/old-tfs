package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * User: IPCVal
 * Date: 7/29/13
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class RefundService {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,##0.00");

    public void saveProductRefundDetails(TradeServiceId tradeServiceId, List<Map<String, Object>> newProductRefundListMap) throws Exception {

        // 1) Load TradeService
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        BigDecimal refundableProductAmountNotRounded = BigDecimal.ZERO;

        // 2) Assemble the ProductRefundDetails
        Set<ProductRefundDetail> productRefundDetails = new HashSet<ProductRefundDetail>();

        for(Map<String, Object> newProductRefundMap : newProductRefundListMap) {

            ChargeId chargeId = new ChargeId((String) newProductRefundMap.get("chargeId"));

            BigDecimal amount = new BigDecimal(((String) newProductRefundMap.get("amount")).replaceAll(",", ""));
            Currency currency = Currency.getInstance((String) newProductRefundMap.get("currency"));

            BigDecimal originalAmount = new BigDecimal(((String) newProductRefundMap.get("originalAmount")).replaceAll(",",""));
            Currency originalCurrency = Currency.getInstance((String) newProductRefundMap.get("originalCurrency"));

            BigDecimal defaultAmount = new BigDecimal(((String) newProductRefundMap.get("defaultAmount")).replaceAll(",",""));

            String overridenFlag = (String)newProductRefundMap.get("overridenFlag");
            String transactionType = (String) newProductRefundMap.get("transactionType");
            BigDecimal newSpecialRateThirdToUsd = (newProductRefundMap.get("newSpecialRateThirdToUsd") != null) ? new BigDecimal((String) newProductRefundMap.get("newSpecialRateThirdToUsd")) : null;
            BigDecimal newSpecialRateThirdToPhp = (newProductRefundMap.get("newSpecialRateThirdToPhp") != null) ? new BigDecimal((String) newProductRefundMap.get("newSpecialRateThirdToPhp")) : null;
            BigDecimal newSpecialRateUsdToPhp = (newProductRefundMap.get("newSpecialRateUsdToPhp") != null) ? new BigDecimal((String) newProductRefundMap.get("newSpecialRateUsdToPhp")) : null;
            BigDecimal newUrr = (newProductRefundMap.get("newUrr") != null) ? new BigDecimal((String) newProductRefundMap.get("newUrr")) : null;
            BigDecimal refundAmountInDefaultCurrency = new BigDecimal(newProductRefundMap.get("refundAmountInDefaultCurrency").toString().replaceAll(",",""));
            BigDecimal refundAmountInOriginalCurrency = new BigDecimal(newProductRefundMap.get("refundAmountInOriginalCurrency").toString().replaceAll(",",""));
            BigDecimal newRefundAmountInOriginalCurrency = new BigDecimal(newProductRefundMap.get("newRefundAmountInOriginalCurrency").toString().replaceAll(",",""));

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IN saveProductRefundDetails()");
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
            System.out.println("******************************* refundAmountInDefaultCurrency = " + refundAmountInDefaultCurrency);
            System.out.println("******************************* refundAmountInOriginalCurrency = " + refundAmountInOriginalCurrency);
            System.out.println("******************************* newRefundAmountInOriginalCurrency = " + newRefundAmountInOriginalCurrency);

            ProductRefundDetail productRefundDetail = new ProductRefundDetail(
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
                    refundAmountInDefaultCurrency,
                    refundAmountInOriginalCurrency,
                    newRefundAmountInOriginalCurrency);

            productRefundDetails.add(productRefundDetail);

            refundableProductAmountNotRounded = refundableProductAmountNotRounded.add(refundAmountInDefaultCurrency);
        }

        // 3) Set ProductRefundDetails to TradeService
        tradeService.updateProductRefundDetails(productRefundDetails);

        BigDecimal refundableServiceChargeAmount = BigDecimal.ZERO;

        if (tradeService.getDetails().get("refundableServiceChargeAmount") != null) {
            refundableServiceChargeAmount = new BigDecimal((String) tradeService.getDetails().get("refundableServiceChargeAmount"));
        }

        Map<String, Object> tradeServiceDetails = tradeService.getDetails();


        BigDecimal refundableProductAmount = new BigDecimal(DECIMAL_FORMAT.format(refundableProductAmountNotRounded).replaceAll(",", ""));


        tradeServiceDetails.put("refundableAmount", (refundableProductAmount.add(refundableServiceChargeAmount)));
        tradeServiceDetails.put("refundableProductAmount", refundableProductAmount);

        ServiceInstruction serviceInstruction = serviceInstructionRepository.load(tradeService.getServiceInstructionId());

        Map<String, Object> serviceInstructionDetails = serviceInstruction.getDetails();

        serviceInstructionDetails.put("refundableAmount", (refundableProductAmount.add(refundableServiceChargeAmount)));
        serviceInstructionDetails.put("refundableServiceChargeAmount", refundableServiceChargeAmount);

        serviceInstruction.setDetails(serviceInstructionDetails);

        serviceInstructionRepository.merge(serviceInstruction);

        tradeService.setDetails(tradeServiceDetails);


        // 4) Save TradeService
        tradeServiceRepository.saveOrUpdate(tradeService);
    }

    public void deleteProductRefundDetails(TradeServiceId tradeServiceId) throws Exception {

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        tradeService.deleteProductRefundDetails();

        BigDecimal refundableServiceChargeAmount = BigDecimal.ZERO;

        if (tradeService.getDetails().get("refundableServiceChargeAmount") != null) {
            refundableServiceChargeAmount = new BigDecimal((String) tradeService.getDetails().get("refundableServiceChargeAmount"));
        }

        Map<String, Object> tradeServiceDetails = tradeService.getDetails();
        tradeServiceDetails.put("refundableAmount", refundableServiceChargeAmount);
        tradeServiceDetails.put("refundableProductAmount", BigDecimal.ZERO);

        ServiceInstruction serviceInstruction = serviceInstructionRepository.load(tradeService.getServiceInstructionId());

        Map<String, Object> serviceInstructionDetails = serviceInstruction.getDetails();

        serviceInstructionDetails.put("refundableAmount", refundableServiceChargeAmount);
        serviceInstructionDetails.put("refundableProductAmount", BigDecimal.ZERO);

        serviceInstruction.setDetails(serviceInstructionDetails);

        serviceInstructionRepository.merge(serviceInstruction);

        tradeServiceRepository.saveOrUpdate(tradeService);
    }

    public void saveChargesRefundDetails(TradeServiceId tradeServiceId, List<Map<String, Object>> newChargesRefundListMap) throws Exception {

        // 1) Load TradeService, including the ServiceCharges
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);


        BigDecimal refundableServiceChargeAmountNotRounded = BigDecimal.ZERO;

        // 2) Assemble the new ServiceCharges
        Set<ServiceCharge> serviceChargeSet = new HashSet<ServiceCharge>();

        for(Map<String, Object> newChargesMap : newChargesRefundListMap) {

            ChargeId chargeId = new ChargeId((String)newChargesMap.get("chargeId"));

            BigDecimal amount = new BigDecimal(((String)newChargesMap.get("amount")).replaceAll(",", ""));
            Currency currency = Currency.getInstance((String)newChargesMap.get("currency"));

            BigDecimal originalAmount = new BigDecimal(((String)newChargesMap.get("originalAmount")).replaceAll(",",""));
            Currency originalCurrency = Currency.getInstance((String)newChargesMap.get("originalCurrency"));

            BigDecimal defaultAmount = new BigDecimal(((String)newChargesMap.get("defaultAmount")).replaceAll(",",""));
            BigDecimal nocwtAmount = newChargesMap.get("nocwtAmount") != null ? new BigDecimal(((String)newChargesMap.get("nocwtAmount")).replaceAll(",","")) : null;

            String overridenFlag = (String)newChargesMap.get("overridenFlag");
            String transactionType = (String)newChargesMap.get("transactionType");
            BigDecimal newSpecialRateThirdToUsd = (newChargesMap.get("newSpecialRateThirdToUsd") != null) ? new BigDecimal((String)newChargesMap.get("newSpecialRateThirdToUsd")) : null;
            BigDecimal newSpecialRateThirdToPhp = (newChargesMap.get("newSpecialRateThirdToPhp") != null) ? new BigDecimal((String)newChargesMap.get("newSpecialRateThirdToPhp")) : null;
            BigDecimal newSpecialRateUsdToPhp = (newChargesMap.get("newSpecialRateUsdToPhp") != null) ? new BigDecimal((String)newChargesMap.get("newSpecialRateUsdToPhp")) : null;
            BigDecimal newUrr = (newChargesMap.get("newUrr") != null) ? new BigDecimal((String)newChargesMap.get("newUrr")) : null;
            BigDecimal refundAmountInDefaultCurrency = new BigDecimal(newChargesMap.get("refundAmountInDefaultCurrency").toString().replaceAll(",",""));
            BigDecimal refundAmountInOriginalCurrency = new BigDecimal(newChargesMap.get("refundAmountInOriginalCurrency").toString().replaceAll(",",""));
            BigDecimal newRefundAmountInOriginalCurrency = new BigDecimal(((String) newChargesMap.get("newRefundAmountInOriginalCurrency")).replaceAll(",", ""));

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IN saveChargesRefundDetails()");
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
            System.out.println("******************************* refundAmountInDefaultCurrency = " + refundAmountInDefaultCurrency);
            System.out.println("******************************* refundAmountInOriginalCurrency = " + refundAmountInOriginalCurrency);
            System.out.println("******************************* newRefundAmountInOriginalCurrency = " + newRefundAmountInOriginalCurrency);

            ServiceCharge serviceCharge = new ServiceCharge(tradeServiceId, chargeId, amount, currency, originalAmount, originalCurrency, defaultAmount, overridenFlag, nocwtAmount);

            serviceCharge.setServiceChargeRefundDetail(transactionType, newSpecialRateThirdToUsd, newSpecialRateThirdToPhp, newSpecialRateUsdToPhp, newUrr, refundAmountInDefaultCurrency, refundAmountInOriginalCurrency, newRefundAmountInOriginalCurrency);

            serviceChargeSet.add(serviceCharge);

            refundableServiceChargeAmountNotRounded = refundableServiceChargeAmountNotRounded.add(refundAmountInDefaultCurrency);
        }

        // 3) Set new ServiceCharges to TradeService
        tradeService.updateServiceChargesForRefund(serviceChargeSet);

        BigDecimal refundableProductAmount = BigDecimal.ZERO;


        if (tradeService.getDetails().get("refundableProductAmount") != null) {
            refundableProductAmount = new BigDecimal((String) tradeService.getDetails().get("refundableProductAmount"));
        }

        Map<String, Object> tradeServiceDetails = tradeService.getDetails();

        BigDecimal refundableServiceChargeAmount = new BigDecimal(DECIMAL_FORMAT.format(refundableServiceChargeAmountNotRounded).replaceAll(",", ""));

        tradeServiceDetails.put("refundableAmount", (refundableProductAmount.add(refundableServiceChargeAmount)));
        tradeServiceDetails.put("refundableServiceChargeAmount", refundableServiceChargeAmount);

        ServiceInstruction serviceInstruction = serviceInstructionRepository.load(tradeService.getServiceInstructionId());

        Map<String, Object> serviceInstructionDetails = serviceInstruction.getDetails();

        serviceInstructionDetails.put("refundableAmount", (refundableProductAmount.add(refundableServiceChargeAmount)));
        serviceInstructionDetails.put("refundableServiceChargeAmount", refundableServiceChargeAmount);

        serviceInstruction.setDetails(serviceInstructionDetails);

        serviceInstructionRepository.merge(serviceInstruction);

        tradeService.setDetails(tradeServiceDetails);

        // 4) Save TradeService
        tradeServiceRepository.saveOrUpdate(tradeService);
    }

    public void deleteServiceCharges(TradeServiceId tradeServiceId) throws Exception {

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        tradeService.removeServiceCharges();

        BigDecimal refundableProductAmount = BigDecimal.ZERO;

        if (tradeService.getDetails().get("refundableProductAmount") != null) {
            refundableProductAmount = new BigDecimal((String) tradeService.getDetails().get("refundableProductAmount"));
        }

        Map<String, Object> tradeServiceDetails = tradeService.getDetails();
        tradeServiceDetails.put("refundableAmount", refundableProductAmount);
        tradeServiceDetails.put("refundableServiceChargeAmount", BigDecimal.ZERO);

        ServiceInstruction serviceInstruction = serviceInstructionRepository.load(tradeService.getServiceInstructionId());

        Map<String, Object> serviceInstructionDetails = serviceInstruction.getDetails();

        serviceInstructionDetails.put("refundableAmount", refundableProductAmount);
        serviceInstructionDetails.put("refundableServiceChargeAmount", BigDecimal.ZERO);

        serviceInstruction.setDetails(serviceInstructionDetails);

        serviceInstructionRepository.merge(serviceInstruction);

        tradeServiceRepository.saveOrUpdate(tradeService);
    }
}
