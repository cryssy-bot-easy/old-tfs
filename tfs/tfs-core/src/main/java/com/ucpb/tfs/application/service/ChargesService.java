package com.ucpb.tfs.application.service;

import com.ucpb.tfs.application.bootstrap.ChargesLookup;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.reference.*;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.utils.CalculatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

/**
 * User: Jett
 * Date: 8/28/12
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ChargesService {

    @Autowired
    ChargesLookup chargeLookup;

    @Autowired
    ProductReferenceRepository productReferenceRepository;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ChargeDefaultsReferenceRepository chargeDefaultsReferenceRepository;

    public void applyCharges(TradeService tradeService, ServiceInstruction serviceInstruction) {
        if (tradeService.getServiceType().toString().equalsIgnoreCase("AMENDMENT")
                && tradeService.getDocumentClass().toString().equalsIgnoreCase("LC")
                ) {
            System.out.println("APPLY CHARGES 3 For Amendment");
            applyCharges(tradeService, serviceInstruction, serviceInstruction.getDetails());
        } else {
            System.out.println("APPLY CHARGES 3 ETS");
            applyCharges(tradeService, serviceInstruction.getDetails());
        }

    }

    //for save nature of amendment command
    public void applyCharges(TradeService tradeService, ServiceInstruction serviceInstruction, Map<String, Object> parameterMap) {
        System.out.println("APPLY CHARGES save nature of command amendment");
        Map<String, Object> details = serviceInstruction.getDetails();

        BigDecimal bdamountFROM = BigDecimal.ZERO;
        BigDecimal bdamountTO = BigDecimal.ZERO;
        BigDecimal bdxrate = BigDecimal.ZERO;
        BigDecimal bdUSDxrate = BigDecimal.ZERO;
        BigDecimal bdUSD_PHPUrrxrate = BigDecimal.ZERO;


        Object amountFROM = parameterMap.get("amount");
        bdamountFROM = getBigDecimalOrZeroForAmounts(amountFROM); // new BigDecimal(amountFROM.toString());

        Object amountTO = parameterMap.get("amountTo");
        bdamountTO = getBigDecimalOrZeroForAmounts(amountTO);

        Object xrate = parameterMap.get("creationExchangeRate");//create a third to php field
        bdxrate = getBigDecimalOrZeroForRates(xrate).setScale(6, BigDecimal.ROUND_HALF_UP);

        Object xrateUSD = parameterMap.get("creationExchangeRateUsdToPHPSpecialRate");//create a usd to php field
        bdUSDxrate = getBigDecimalOrZeroForRates(xrateUSD).setScale(6, BigDecimal.ROUND_HALF_UP);

        Object xrateUSDUrr = parameterMap.get("creationExchangeRateUsdToPHPUrr");//create a usd to php field
        bdUSD_PHPUrrxrate = getBigDecimalOrZeroForRates(xrateUSDUrr).setScale(6, BigDecimal.ROUND_HALF_UP);

        //NOTE: Domestic LC transactions are to use urr
        if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC)) {
            bdUSDxrate = bdUSD_PHPUrrxrate;
        }

        if (!details.get("currency").toString().equalsIgnoreCase("PHP") && !tradeService.getDocumentType().equals(DocumentType.DOMESTIC)) {
            System.out.println("currency:" + details.get("currency").toString());
            System.out.println("exchange rate of specified currency:" + bdxrate.toString());
            details.put("amount", bdamountFROM.multiply(bdxrate));
            details.put("amountTo", bdamountTO.multiply(bdxrate));
        } else if (tradeService.getDocumentType().equals(DocumentType.DOMESTIC) && details.get("currency").toString().equalsIgnoreCase("USD")) {
            System.out.println("currency:" + details.get("currency").toString());
            System.out.println("exchange rate of specified currency:" + bdxrate.toString());
            details.put("amount", bdamountFROM.multiply(bdUSD_PHPUrrxrate));
            details.put("amountTo", bdamountTO.multiply(bdUSD_PHPUrrxrate));
        }

        details.put("usdToPHPSpecialRate", bdUSDxrate);
        details.put("urr", bdUSD_PHPUrrxrate);


        System.out.println("apply Charges:");
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());
        // find the UCPB product reference for this combination
        ProductReference productRef;

        productRef = productReferenceRepository.find(tradeService.getDocumentClass(), tradeService.getDocumentType(), tradeService.getDocumentSubType1(), tradeService.getDocumentSubType2());
        //TODO: Check if UA Loan Amendment
        //TODO: UA Loan Settlement
        //TODO: Adjustment
        //TODO Refactor Later


        if (productRef != null) {
            System.out.println("looking for charges for: " + productRef.getProductId().toString());
            List<TradeServiceChargeReference> charges = chargeLookup.getChargesForService(productRef.getProductId(), tradeService.getServiceType());

            // clear all prior charges
            tradeService.removeServiceCharges();

            for (String keyed : details.keySet()) {
                Object ob = details.get(keyed);
                System.out.println("key:" + keyed + " value:" + ob.toString());
            }

            Map<String, Object> defaultz = chargeLookup.getDefaultValuesForServiceMap();

            updateDetailsFromDefaultzIfNullOrEmpty(details, defaultz);

            // add the charges to the trade service
            if (charges != null) {

                for (TradeServiceChargeReference charge : charges) {

                    BigDecimal result;
                    System.out.println("----------------------------------------------------------------------------------------------");
                    System.out.println(charge);

                    if (testIfNotSpecial(tradeService, details, charge)) {

                        try {
                            System.out.println("CHHHHHHHHHHHHHAAAAAAARRRRRRRRRRRRRRRRGGGGGGGGGGGGGGEEEEEEEESSSSSSSSSSSSSSSSSSSSSSS");
//                        result = charge.compute((Map) details);
                            result = charge.compute(details);
//                            result = BigDecimal.TEN;
                            System.out.println("result:" + result);
                            System.out.println("result:" + result.setScale(2, BigDecimal.ROUND_HALF_UP));
                            // add charges, result is always in PHP
                            // todo: remove hardcode for charges in PHP
                            tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                        } catch (Exception e) {
                            // todo: add an exception handler here
                            System.out.println("!!!!!!!!!!!!! CHARGES EXCEPTION");
                            e.printStackTrace();
                        }
                    } else {

                        handleSpecial(tradeService, details, charge);

                    }
                }

            } else {
                System.out.println("no charges found");
            }
        } else {
            System.out.println("!!!!!!!!!!!!!! product reference not found");
        }


        details.put("amount", bdamountFROM);
        details.put("amountTo", bdamountTO);

    }

    public void applyNoCharges (TradeService tradeService) {
        System.out.println("applyNoCharges :");
        System.out.println("TradeServiceId:" + tradeService.getTradeServiceId());
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());

        // marv.06Nov2012.start - temporary fix for missing charges in indemnity
        DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
        DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();

        if (tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)) {
            if (tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR) || tradeService.getDocumentSubType1().equals(DocumentSubType1.CASH)) {
                documentSubType1 = null;
                documentSubType2 = null;
            }
        }

        if(tradeService.getDocumentClass().equals(DocumentClass.BC)||tradeService.getDocumentClass().equals(DocumentClass.BP)){
            System.out.println("within override documentSubType1 and documentSubType2");
            documentSubType1 = null;
            documentSubType2 = null;
        }


        // clear all prior charges
        tradeService.removeServiceCharges();

    }

    public void applyCharges(TradeService tradeService, Map<String, Object> details) {
        System.out.println("apply Charges 1:");
        System.out.println("TradeServiceId:" + tradeService.getTradeServiceId());
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());

        // marv.06Nov2012.start - temporary fix for missing charges in indemnity
        DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
        DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();

        if (tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)) {
            if (tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR) || tradeService.getDocumentSubType1().equals(DocumentSubType1.CASH)) {
                documentSubType1 = null;
                documentSubType2 = null;
            }
        }

        if(tradeService.getDocumentClass().equals(DocumentClass.BC)||tradeService.getDocumentClass().equals(DocumentClass.BP)){
            System.out.println("within override documentSubType1 and documentSubType2");
            documentSubType1 = null;
            documentSubType2 = null;
        }

        // find the UCPB product reference for this combination
        ProductReference productRef;
        productRef = productReferenceRepository.find(tradeService.getDocumentClass(), tradeService.getDocumentType(), documentSubType1, documentSubType2);

        BigDecimal bdamount = BigDecimal.ZERO;
        bdamount = replaceAmountWithConvertedAmount(tradeService, details, bdamount);


        if (productRef != null) {
            System.out.println("looking for charges for: " + productRef.getProductId().toString());
            List<TradeServiceChargeReference> charges = chargeLookup.getChargesForService(productRef.getProductId(), tradeService.getServiceType());

            // clear all prior charges
            tradeService.removeServiceCharges();

            //Get default values
//            Map<String, Object> defaultz = chargeLookup.getDefaultValuesForServiceMap();

            //update details map to include non existent default values.
//            updateDetailsFromDefaultzIfNullOrEmpty(details, defaultz);

            // add the charges to the trade service
            if (charges != null) {

                for (TradeServiceChargeReference charge : charges) {

                    BigDecimal result;
                    System.out.println("----------------------------------------------------------------------------------------------");
                    System.out.println(charge);

                    if (testIfNotSpecial(tradeService, details, charge)) {

                        try {
                            System.out.println("CHHHHHHHHHHHHHAAAAAAARRRRRRRRRRRRRRRRGGGGGGGGGGGGGGEEEEEEEESSSSSSSSSSSSSSSSSSSSSSS");
                            result = charge.compute(details);
//                            result = BigDecimal.TEN;
                            System.out.println("result:" + result);
                            System.out.println("result:" + result.setScale(2, BigDecimal.ROUND_HALF_UP));
                            // add charges, result is always in PHP
                            // todo: remove hardcode for charges in PHP

                            if(charge.getChargeId().equals(new ChargeId("REMITTANCE"))){
                                tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), new BigDecimal("18").setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("USD"));
                            } else if(charge.getChargeId().equals(new ChargeId("BSP"))){
                                tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), new BigDecimal("100").setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                            } else if(charge.getChargeId().equals(new ChargeId("NOTARIAL"))){
                                tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), new BigDecimal("50").setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                            } else if(charge.getChargeId().equals(new ChargeId("BOOKING"))){
                                tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), new BigDecimal("500").setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                            } else if(charge.getChargeId().equals(new ChargeId("SUP"))){
                                tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), new BigDecimal("50").setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                            } else if(charge.getChargeId().equals(new ChargeId("ADVISING"))){
                                tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), new BigDecimal("1000").setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                            } else if(charge.getChargeId().equals(new ChargeId("CABLE"))){
                                if(tradeService.getDocumentType().equals(DocumentType.FOREIGN)){
                                    tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), new BigDecimal("1000").setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                                } else {
                                    tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), new BigDecimal("500").setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                                }
                            } else {
                                tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                            }
                        } catch (Exception e) {
                            // todo: add an exception handler here
                            System.out.println("!!!!!!!!!!!!! CHARGES EXCEPTION");
                            e.printStackTrace();
                        }
                    } else {

                        handleSpecial(tradeService, details, charge);

                    }
                }

            } else {
                System.out.println("no charges found");
            }
        } else {
            System.out.println("!!!!!!!!!!!!!! product reference not found");
        }
        replaceConvertedAmountWithOriginalAmount(tradeService, details, bdamount);
    }

    private void createWorkingCopyMap(Map<String, Object> details, Map<String, Object> detailsOriginal) {
        for (String keyed : detailsOriginal.keySet()) {
            details.put(keyed, detailsOriginal.get(keyed));
        }
    }

    private void updateDetailsFromDefaultzIfNullOrEmpty(Map<String, Object> details, Map<String, Object> defaultz) {
        for (String keyed : defaultz.keySet()) {
            Object ob = defaultz.get(keyed);

            System.out.println("keyed:" + keyed + " ob.toString():" + ob.toString());

            if (!details.containsKey(keyed)) {

                //details.put(keyed, ob);

                if (ob instanceof BigDecimal) {
                    details.put(keyed, ((BigDecimal) ob).toPlainString());
                } else {
                    details.put(keyed, ob);
                }

            } else {
                Object obb = details.get(keyed);
                System.out.println("keyed:" + keyed + " obb.toString():" + obb.toString());

                if (obb == null || obb.toString().isEmpty()) {

                    //details.put(keyed, ob);

                    if (ob instanceof BigDecimal) {
                        details.put(keyed, ((BigDecimal) ob).toPlainString());
                    } else {
                        details.put(keyed, ob);
                    }
                }
            }

        }
    }

    private static void replaceConvertedAmountWithOriginalAmount(TradeService tradeService, Map<String, Object> details, BigDecimal bdamount) {

        if (tradeService.getServiceType() == ServiceType.OPENING) {
            if (bdamount != null) {
                details.put("amount", bdamount);
            } else {
                details.put("amount", BigDecimal.ZERO);
            }
        }

        if (tradeService.getServiceType() == ServiceType.NEGOTIATION) {
            if (bdamount != null) {
                details.put("negotiationAmount", bdamount);
            } else {
                details.put("negotiationAmount", BigDecimal.ZERO);
            }
        }

        if (tradeService.getServiceType() == ServiceType.ADJUSTMENT) {
            if (bdamount != null) {
                details.put("cashAmount", bdamount);
            } else {
                details.put("cashAmount", BigDecimal.ZERO);
            }
        }

        if (tradeService.getServiceType() == ServiceType.UA_LOAN_MATURITY_ADJUSTMENT) {
            if (bdamount != null) {
                details.put("amount", bdamount);
            } else {
                details.put("amount", BigDecimal.ZERO);
            }
        }

        if (tradeService.getServiceType() == ServiceType.SETTLEMENT &&
                (tradeService.getDocumentClass().equals(DocumentClass.DA)
                        || tradeService.getDocumentClass().equals(DocumentClass.DP)
                        || tradeService.getDocumentClass().equals(DocumentClass.DR)
                        || tradeService.getDocumentClass().equals(DocumentClass.OA))) {
            if (bdamount != null) {
                details.put("productAmount", bdamount);
            } else {
                details.put("productAmount", BigDecimal.ZERO);
            }
            System.out.println("productAmount: " + details.get("productAmount"));
        }
    }

    private static BigDecimal replaceAmountWithConvertedAmount(TradeService tradeService, Map<String, Object> details, BigDecimal bdamount) {
        BigDecimal bdxrate;
        BigDecimal bdUSDxrate;
        BigDecimal bdUSD_PHPUrrxrate;

        Object xrate = details.get("creationExchangeRate");//create a usd to php field
        bdxrate = getBigDecimalOrZeroForRates(xrate);

        Object xrateUSD = details.get("creationExchangeRateUsdToPHPSpecialRate");//create a usd to php field
        bdUSDxrate = getBigDecimalOrZeroForRates(xrateUSD);

        Object xrateUSDUrr = details.get("creationExchangeRateUsdToPHPUrr");//create a usd to php field
        bdUSD_PHPUrrxrate = getBigDecimalOrZeroForRates(xrateUSDUrr);

        //NOTE: Domestic LC transactions are to use urr
        if (tradeService.getDocumentType() != null && tradeService.getDocumentType().equals(DocumentType.DOMESTIC)) {
            bdUSDxrate = bdUSD_PHPUrrxrate;
        }


        if (bdUSD_PHPUrrxrate != null && bdUSD_PHPUrrxrate.compareTo(BigDecimal.ZERO) == 1) {
            details.put("urr", bdUSD_PHPUrrxrate);
        }

        if (tradeService.getServiceType() == ServiceType.OPENING) {

            Object amount = details.get("amount");
            bdamount = getBigDecimalOrZeroForAmounts(amount);
            details.put("amount", bdamount);

            if (!details.get("currency").toString().equalsIgnoreCase("PHP") && !details.get("currency").toString().equalsIgnoreCase("USD")) {
                System.out.println("currency:" + details.get("currency").toString());
                System.out.println("exchange rate of specified currency:" + bdxrate.toString());
                details.put("amount", bdamount.multiply(bdxrate));
            } else if (details.get("currency").toString().equalsIgnoreCase("USD")) {
                System.out.println("currency:" + details.get("currency").toString());
                System.out.println("exchange rate of specified currency:" + bdUSDxrate.toString());
                details.put("amount", bdamount.multiply(bdUSDxrate));
            }

            details.put("usdToPHPSpecialRate", bdUSDxrate);
        }

        if (tradeService.getServiceType() == ServiceType.NEGOTIATION &&
                tradeService.getDocumentClass() == DocumentClass.LC) {

            Object amount = details.get("negotiationAmount");
            bdamount = getBigDecimalOrZeroForAmounts(amount);

            if (!details.get("originalCurrency").toString().equalsIgnoreCase("PHP") && !details.get("negotiationCurrency").toString().equalsIgnoreCase("USD")) {
                details.put("negotiationAmount", bdamount.multiply(bdxrate));
            } else if ("USD".equalsIgnoreCase(details.get("negotiationCurrency").toString())) {
                System.out.println("negotiationCurrency:" + details.get("negotiationCurrency").toString());
                System.out.println("exchange rate of specified currency:" + bdUSDxrate.toString());
                details.put("negotiationAmount", bdamount.multiply(bdUSDxrate));
            }

            details.put("usdToPHPSpecialRate", bdUSDxrate);
        }

        if (tradeService.getServiceType() == ServiceType.ADJUSTMENT &&
                tradeService.getDocumentClass() == DocumentClass.LC) {

            Object amount = details.get("cashAmount");
            bdamount = getBigDecimalOrZeroForAmounts(amount);


            if (!details.get("currency").toString().equalsIgnoreCase("PHP") && !details.get("currency").toString().equalsIgnoreCase("USD")) {
                details.put("cashAmount", bdamount.multiply(bdxrate));
            } else if (details.get("currency").toString().equalsIgnoreCase("USD")) {
                System.out.println("currency:" + details.get("currency").toString());
                System.out.println("exchange rate of specified currency:" + bdUSDxrate.toString());
                details.put("cashAmount", bdamount.multiply(bdUSDxrate));
            }

            details.put("usdToPHPSpecialRate", bdUSDxrate);
        }

        if (tradeService.getServiceType() == ServiceType.SETTLEMENT &&
                (tradeService.getDocumentClass().equals(DocumentClass.DA) ||
                        tradeService.getDocumentClass().equals(DocumentClass.DP) ||
                        tradeService.getDocumentClass().equals(DocumentClass.DR) ||
                        tradeService.getDocumentClass().equals(DocumentClass.OA))) {

            Object amount = details.get("productAmount");
            bdamount = getBigDecimalOrZeroForAmounts(amount);


            if (!details.get("currency").toString().equalsIgnoreCase("PHP") && !details.get("currency").toString().equalsIgnoreCase("USD")) {
                details.put("productAmount", bdamount.multiply(bdxrate));
            } else if (details.get("currency").toString().equalsIgnoreCase("USD")) {
                System.out.println("currency:" + details.get("currency").toString());
                System.out.println("exchange rate of specified currency:" + bdUSDxrate.toString());
                details.put("productAmount", bdamount.multiply(bdUSDxrate));
            }

            System.out.println("productAmount after conversion:" + details.get("productAmount"));

            details.put("usdToPHPSpecialRate", bdUSDxrate);
        }

        if (tradeService.getServiceType() == ServiceType.UA_LOAN_MATURITY_ADJUSTMENT) {

            Object amount = details.get("amount");
            bdamount = getBigDecimalOrZeroForAmounts(amount);
            if (!details.get("currency").toString().equalsIgnoreCase("PHP") && !details.get("currency").toString().equalsIgnoreCase("USD")) {
                details.put("amount", bdamount.multiply(bdxrate));
            } else if (details.get("currency").toString().equalsIgnoreCase("USD")) {
                System.out.println("currency:" + details.get("currency").toString());
                System.out.println("exchange rate of specified currency:" + bdUSDxrate.toString());
                details.put("amount", bdamount.multiply(bdUSDxrate));
            }

            details.put("usdToPHPSpecialRate", bdUSDxrate);
        }

        return bdamount;
    }

    public BigDecimal computeSpecificCharge(TradeService tradeService, ServiceInstruction serviceInstruction, ChargeId chargeId) {
        return computeSpecificCharge(tradeService, serviceInstruction.getDetails(), chargeId);
    }

    public BigDecimal computeSpecificCharge(TradeService tradeService, Map<String, Object> details, ChargeId chargeId) {
        System.out.println("computeSpecificCharge:");
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());
        System.out.println("ChargeId:" + chargeId.toString());

        // find the UCPB product reference for this combination
        ProductReference productRef;

        productRef = productReferenceRepository.find(tradeService.getDocumentClass(), tradeService.getDocumentType(), tradeService.getDocumentSubType1(), tradeService.getDocumentSubType2());
        //TODO: Check if UA Loan Amendment or UA Loan Settlement


        if (productRef != null) {
            System.out.println("looking for charges for: " + productRef.getProductId().toString());
            List<TradeServiceChargeReference> charges = chargeLookup.getChargesForService(productRef.getProductId(), tradeService.getServiceType());

            for (String keyed : details.keySet()) {
                System.out.println("----------------------------------------------------------------------------------------------");
                Object ob = details.get(keyed);
                System.out.println("key:" + keyed + " value:" + ob.toString());
            }

            Map<String, Object> defaultz = chargeLookup.getDefaultValuesForServiceMap();

            for (String keyed : defaultz.keySet()) {
                Object ob = defaultz.get(keyed);
                if (!details.containsKey(keyed)) {

                    // details.put(keyed, ob);

                    if (ob instanceof BigDecimal) {
                        details.put(keyed, ((BigDecimal) ob).toPlainString());
                    } else {
                        details.put(keyed, ob);
                    }
                }
                if (details.containsKey(keyed) && (details.get(keyed) == null || details.get(keyed).toString() == "")) {

                    // details.put(keyed, ob);

                    if (ob instanceof BigDecimal) {
                        details.put(keyed, ((BigDecimal) ob).toPlainString());
                    } else {
                        details.put(keyed, ob);
                    }
                }
            }

            // add the charges to the trade service
            if (charges != null) {

                for (TradeServiceChargeReference charge : charges) {

                    BigDecimal result;
                    System.out.println("----------------------------------------------------------------------------------------------");
                    System.out.println(charge);


                    try {
                        System.out.println("CHHHHHHHHHHHHHAAAAAAARRRRRRRRRRRRRRRRGGGGGGGGGGGGGGEEEEEEEESSSSSSSSSSSSSSSSSSSSSSS");

                        result = charge.compute(details);
                        //result = BigDecimal.TEN;
                        System.out.println("result:" + result);
                        System.out.println("result:" + result.setScale(2, BigDecimal.ROUND_HALF_UP));

                        System.out.println("Charge Id:" + chargeId);
                        System.out.println("Charge Charge Id:" + charge.getChargeId());
                        if (charge.getChargeId().toString().equalsIgnoreCase(chargeId.toString())) {
                            System.out.println("Will Return:");
                            return result.setScale(2, BigDecimal.ROUND_HALF_UP);
                        }
                    } catch (Exception e) {
                        // todo: add an exception handler here
                        System.out.println("!!!!!!!!!!!!! CHARGES EXCEPTION");
                        e.printStackTrace();
                    }
                }

            } else {
                System.out.println("no charges found");
            }
        } else {
            System.out.println("!!!!!!!!!!!!!! product reference not found");
        }
        return new BigDecimal(0);

    }

    private Boolean testIfNotSpecial(TradeService tradeService, Map<String, Object> details, TradeServiceChargeReference chargeReference) {
        System.out.println("in testIfNotSpecial");
        //TODO put exceptional cases condition here

        if (tradeService.getDocumentClass() == DocumentClass.LC && tradeService.getDocumentType() == DocumentType.FOREIGN && tradeService.getDocumentSubType1() == DocumentSubType1.REGULAR && tradeService.getDocumentSubType2() == DocumentSubType2.SIGHT && tradeService.getServiceType() == ServiceType.ADJUSTMENT) {
            if (chargeReference.getChargeId() == new ChargeId("CILEX")) {
                System.out.println("CILEX");
                return Boolean.FALSE;
            }
        } else if (tradeService.getDocumentClass() == DocumentClass.LC && tradeService.getDocumentType() == DocumentType.FOREIGN && tradeService.getDocumentSubType1() == DocumentSubType1.CASH && tradeService.getDocumentSubType2() == DocumentSubType2.SIGHT && tradeService.getServiceType() == ServiceType.ADJUSTMENT) {
            System.out.println("CILEX");
            return Boolean.TRUE;
        } else {
            return Boolean.TRUE;
        }

        return Boolean.TRUE;

    }

    private void handleSpecial(TradeService tradeService, Map<String, Object> details, TradeServiceChargeReference chargeReference) {
        System.out.println("in handleSpecial ");
        //TODO put exceptional cases condition handling here
    }

    private static BigDecimal getBigDecimalOrZeroForAmounts(Object num) {
        BigDecimal value = BigDecimal.ZERO;
        if (num != null) {
            try {
                value = new BigDecimal(num.toString());
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("object being converted produced an invalid BigDecimal");
                value = BigDecimal.ZERO;
            }
        }
        return value.setScale(8, BigDecimal.ROUND_HALF_UP);
    }

    private static BigDecimal getBigDecimalOrZeroForRates(Object num) {
        BigDecimal value = BigDecimal.ZERO;
        if (num != null) {
            try {
                value = new BigDecimal(num.toString());
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("object being converted produced an invalid BigDecimal");
                value = BigDecimal.ZERO;
            }
        }
        return value.setScale(8, BigDecimal.ROUND_HALF_UP);
    }

    public void applyChargesNewStyle(TradeService tradeService, Map<String, Object> details) {

        System.out.println("apply Charges new style:");
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());

        DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
        DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();

        if (tradeService.getDocumentClass().equals(DocumentClass.INDEMNITY)) {
            if (tradeService.getDocumentSubType1().equals(DocumentSubType1.REGULAR) || tradeService.getDocumentSubType1().equals(DocumentSubType1.CASH)) {
                documentSubType1 = null;
                documentSubType2 = null;
            }
        }
        // find the UCPB product reference for this combination
        ProductReference productRef = productReferenceRepository.find(tradeService.getDocumentClass(), tradeService.getDocumentType(), documentSubType1, documentSubType2);


        BigDecimal bdamount = BigDecimal.ZERO;
        bdamount = replaceAmountWithConvertedAmount(tradeService, details, bdamount);


        if (productRef != null) {
            System.out.println("looking for charges for: " + productRef.getProductId().toString());
            List<TradeServiceChargeReference> charges = chargeLookup.getChargesForService(productRef.getProductId(), tradeService.getServiceType());

            // clear all prior charges
            tradeService.removeServiceCharges();

            for (String keyed : details.keySet()) {
                Object ob = details.get(keyed);
                System.out.println("key:" + keyed + " value:" + ob.toString());
            }

            Map<String, Object> defaultz = chargeLookup.getDefaultValuesForServiceMap();

            updateDetailsFromDefaultzIfNullOrEmpty(details, defaultz);

            // add the charges to the trade service
            if (charges != null) {

                for (TradeServiceChargeReference charge : charges) {

                    BigDecimal result;
                    System.out.println("----------------------------------------------------------------------------------------------");
                    System.out.println(charge);

                    if (testIfNotSpecial(tradeService, details, charge)) {

                        try {
                            System.out.println("CHHHHHHHHHHHHHAAAAAAARRRRRRRRRRRRRRRRGGGGGGGGGGGGGGEEEEEEEESSSSSSSSSSSSSSSSSSSSSSS");
                            result = charge.compute(details);
//                            result = BigDecimal.TEN;
                            System.out.println("result:" + result);
                            System.out.println("result:" + result.setScale(2, BigDecimal.ROUND_HALF_UP));
                            // add charges, result is always in PHP
                            // todo: remove hardcode for charges in PHP
                            tradeService.addCharge(charge.getChargeId(), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), result.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"));
                        } catch (Exception e) {
                            // todo: add an exception handler here
                            System.out.println("!!!!!!!!!!!!! CHARGES EXCEPTION");
                            e.printStackTrace();
                        }
                    } else {

                        handleSpecial(tradeService, details, charge);

                    }
                }

            } else {
                System.out.println("no charges found");
            }
        } else {
            System.out.println("!!!!!!!!!!!!!! product reference not found");
        }
        replaceConvertedAmountWithOriginalAmount(tradeService, details, bdamount);


    }

    /**
     * Method that is called to remove cilex and other charges that should are not going to be used in computation
     * @param tradeServiceId tradeService
     * @param paymentProduct paymentProduct
     */
    public void removeChargesNotUsed(TradeServiceId tradeServiceId, Payment paymentProduct) {

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        System.out.println("apply Charges new style:");
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());

        Boolean isProductPaymentCurrencyNonPhp = Boolean.FALSE;
        if (paymentProduct != null) {
            isProductPaymentCurrencyNonPhp = paymentProduct.checkIfPaymentCurrenciesIsNonPhp();
        }


        Boolean isProductPaymentDTRLOAN = Boolean.FALSE;
        if (paymentProduct != null &&
                (
                        (tradeService.getDocumentClass().equals(DocumentClass.DP) || tradeService.getDocumentClass().equals(DocumentClass.DA)
                                || tradeService.getDocumentClass().equals(DocumentClass.OA) || tradeService.getDocumentClass().equals(DocumentClass.DR))
                                && tradeService.getDocumentType() != null && tradeService.getDocumentType().equals(DocumentType.DOMESTIC)
                )
                ) {//This is for non lc where Docstamps will only be charged if isProductPaymentDTRLOAN is false. cilex is removed here too
            isProductPaymentDTRLOAN = paymentProduct.hasTrLoan();
            tradeService.removeUnusedCharges(isProductPaymentCurrencyNonPhp, isProductPaymentDTRLOAN);
        } else {//cilex is removed here. cilex is removed here too
            tradeService.removeUnusedCharges(isProductPaymentCurrencyNonPhp);
        }


    }

    public void removeCharges(TradeServiceId tradeServiceId) {

        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        System.out.println("apply Charges new style:");
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());

        tradeService.removeNoCharges();


    }

    /**
     * Method that is called to remove cilex and other charges that should are not going to be used in computation
     * @param tradeServiceId tradeService
     * @param paymentProduct paymentProduct
     */
    public void removeCilex(TradeServiceId tradeServiceId) {
        System.out.println("removing cilex");
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        System.out.println("apply Charges new style:");
        System.out.println("DocumentClass:" + tradeService.getDocumentClass());
        System.out.println("DocumentType:" + tradeService.getDocumentType());
        System.out.println("DocumentSubType1:" + tradeService.getDocumentSubType1());
        System.out.println("DocumentSubType2:" + tradeService.getDocumentSubType2());
        System.out.println("ServiceType:" + tradeService.getServiceType());
        tradeService.removeCilex();


    }

    public BigDecimal computeServiceCharge(Map<String,String> parameters){

        //ServiceType
        //ProductRef

        String strTemp = parameters.get("chargeType");
        ChargeId chargeId = new ChargeId(strTemp); //This is the charge to be computed


        strTemp =  parameters.get("tradeServiceId");
        if(strTemp != null && !strTemp.isEmpty()){
            TradeServiceId tradeServiceId = new TradeServiceId(strTemp);
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        }

        //CalculatorUtils.get



        return null; //TODO remove this after implementation
    }
}
