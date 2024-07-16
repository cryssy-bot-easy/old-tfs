package com.ucpb.tfs.application.service;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.utils.AmendmentTaggedFields;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.enumTypes.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 8/17/12
 */
  
 /**
 * PROLOGUE
 * SCR/ER Description: To set the PHP conversion of the EBP charges
 *	[Revised by:] Jesse James Joson
 *	Program [Revision] Details: Set the rates based on the settlement to bene currency, instead of the charges currency.
 *	Date deployment: 6/16/2016 
 *	Member Type: Java
 *	Project: Core
 *	Project Name: TradeServiceService.java 
*/

public class TradeServiceService {

    public static TradeService createTradeService(ServiceInstruction ets, DocumentNumber documentNumber, TradeProductNumber tradeProductNumber, UserActiveDirectoryId userActiveDirectoryId) {

        Map<String, Object> details = ets.getDetails();

        DocumentClass documentClass = null;
        if (details.get("documentClass") != null && !((String) details.get("documentClass")).trim().equals("")) {
            documentClass = DocumentClass.valueOf((String) details.get("documentClass"));
        }

        DocumentType documentType = null;
        if (details.get("documentType") != null && !((String) details.get("documentType")).trim().equals("")) {
            documentType = DocumentType.valueOf((String) details.get("documentType"));
        }

        DocumentSubType1 documentSubType1 = null;
        if (details.get("documentSubType1") != null && !((String) details.get("documentSubType1")).trim().equals("")) {
            documentSubType1 = DocumentSubType1.valueOf((String) details.get("documentSubType1"));
        }

        DocumentSubType2 documentSubType2 = null;
        if (details.get("documentSubType2") != null && !((String) details.get("documentSubType2")).trim().equals("")) {
            documentSubType2 = DocumentSubType2.valueOf((String) details.get("documentSubType2"));
        }

        ServiceType serviceType = null;
        if (details.get("serviceType") != null && !((String) details.get("serviceType")).trim().equals("")) {
            serviceType = ServiceType.valueOf(((String) details.get("serviceType")).toUpperCase());
        }

        // Create and persist TradeService
        try{
            TradeService tradeService = new TradeService(ets.getServiceInstructionId(), documentNumber, tradeProductNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType, userActiveDirectoryId);
            tradeService = setSpecialRates(tradeService, details);
            tradeService.updateDetails(details, userActiveDirectoryId);

            // If ETS was saved as DRAFT, also save TradeService as DRAFT; else, save as MARV
            if (ets.getStatus().equals(ServiceInstructionStatus.DRAFT)) {
                tradeService.tagStatus(TradeServiceStatus.DRAFT);
            } else {
                tradeService.tagStatus(TradeServiceStatus.MARV);
            }


            return tradeService;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static TradeService createTradeService(Map<String, Object> details, DocumentNumber documentNumber, TradeProductNumber tradeProductNumber, UserActiveDirectoryId userActiveDirectoryId, String tradeServiceReferenceNumber) {

        DocumentClass documentClass = null;
        if (details.get("documentClass") != null && !((String) details.get("documentClass")).trim().equals("")) {
            documentClass = DocumentClass.valueOf((String) details.get("documentClass"));
        }

        DocumentType documentType = null;
        if (details.get("documentType") != null && !((String) details.get("documentType")).trim().equals("")) {
            documentType = DocumentType.valueOf((String) details.get("documentType"));
        }

        DocumentSubType1 documentSubType1 = null;
        if (details.get("documentSubType1") != null && !((String) details.get("documentSubType1")).trim().equals("")) {
            documentSubType1 = DocumentSubType1.valueOf((String) details.get("documentSubType1"));
        }

        DocumentSubType2 documentSubType2 = null;
        if (details.get("documentSubType2") != null && !((String) details.get("documentSubType2")).trim().equals("")) {
            documentSubType2 = DocumentSubType2.valueOf((String) details.get("documentSubType2"));
        }

        ServiceType serviceType = null;
        if (details.get("serviceType") != null && !((String) details.get("serviceType")).trim().equals("")) {
            serviceType = ServiceType.valueOf(((String) details.get("serviceType")).toUpperCase());
        }

        // Create and persist TradeService
        // ServiceInstructionId is null

        TradeService tradeService = new TradeService(documentNumber, tradeProductNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType, userActiveDirectoryId, tradeServiceReferenceNumber);
        tradeService = setSpecialRates(tradeService, details);
        tradeService.updateDetails(details, userActiveDirectoryId);
        

        // Tag as PENDING
        tradeService.tagStatus(TradeServiceStatus.PENDING);

        return tradeService;
    }

    public static TradeService updateTradeServiceDetails(TradeService tradeService, Map<String, Object> details, UserActiveDirectoryId userActiveDirectoryId, String type) {
        System.out.println("serviceType :" + details.get("serviceType"));
        
        if( details.get("generalDescriptionOfGoodsFrom") != null )
            details.put("generalDescriptionOfGoodsFrom", details.get("generalDescriptionOfGoodsFrom").toString().toUpperCase());
        if( details.get("generalDescriptionOfGoodsTo") != null )
            details.put("generalDescriptionOfGoodsTo", details.get("generalDescriptionOfGoodsTo").toString().toUpperCase());
        
        if( details.get("specialPaymentConditionsForBeneficiaryFrom") != null )
            details.put("specialPaymentConditionsForBeneficiaryFrom", details.get("specialPaymentConditionsForBeneficiaryFrom").toString().toUpperCase());
        if( details.get("specialPaymentConditionsForBeneficiaryTo") != null )
            details.put("specialPaymentConditionsForBeneficiaryTo", details.get("specialPaymentConditionsForBeneficiaryTo").toString().toUpperCase());
        
        if( details.get("specialPaymentConditionsForReceivingBankFrom") != null )
            details.put("specialPaymentConditionsForReceivingBankFrom", details.get("specialPaymentConditionsForReceivingBankFrom").toString().toUpperCase());
        if( details.get("specialPaymentConditionsForReceivingBankTo") != null )
            details.put("specialPaymentConditionsForReceivingBankTo", details.get("specialPaymentConditionsForReceivingBankTo").toString().toUpperCase());

        String serviceTypeString = ((String) details.get("serviceType")).toUpperCase();
        // For LC Amendment only
        ServiceType serviceType = ServiceType.valueOf(serviceTypeString);
        if (serviceTypeString.equalsIgnoreCase("UA LOAN SETTLEMENT")) {
            serviceType = ServiceType.UA_LOAN_SETTLEMENT;
        } else if (serviceTypeString.equalsIgnoreCase("UA LOAN SETTLEMENT")) {
            serviceType = ServiceType.UA_LOAN_MATURITY_ADJUSTMENT;
        }
        if (serviceType.equals(ServiceType.AMENDMENT)) {
            Map<String, Object> currentAmendedFields = AmendmentTaggedFields.cleanse(details, tradeService.getDetails());
            System.out.println("tradeService.getDetails().get(\"amount\"):"+tradeService.getDetails().get("amount"));
            System.out.println("tradeService.getDetails().get(\"outstandingBalance\"):"+tradeService.getDetails().get("outstandingBalance"));
            System.out.println("tradeService.getDetails().get(\"amountFrom\"):"+tradeService.getDetails().get("amountFrom"));

            if("Y".equalsIgnoreCase(type)){
                currentAmendedFields.put("amountFrom",tradeService.getDetails().get("outstandingBalance"));
                currentAmendedFields.put("amount",tradeService.getDetails().get("outstandingBalance"));
            }
            tradeService.setDetails(currentAmendedFields);
            printCurrentAmendedFields(currentAmendedFields);
        }
        
        tradeService = setSpecialRates(tradeService, details);
        tradeService.updateDetails(details, userActiveDirectoryId);
        return tradeService;
    }

    public static TradeService addServiceCharges(TradeService tradeService, Map<String, Object> serviceChargesDetails, UserActiveDirectoryId userActiveDirectoryId) {
        System.out.println("addServiceCharges   ");

        //TODO Add checking if special rate exists and use that

        String settlementCurrency = (String) serviceChargesDetails.get("settlementCurrency");// Change this to get currency to php
        Currency tmpCurrency = Currency.getInstance("PHP");
        BigDecimal conversionRate = new BigDecimal(1); //Default to php no conversion
        //TODO:: fix fx
        if (settlementCurrency != null && !settlementCurrency.equalsIgnoreCase("") && !settlementCurrency.equalsIgnoreCase("PHP")) {
            tmpCurrency = Currency.getInstance(settlementCurrency);
            String strConversionRate = (String) serviceChargesDetails.get(settlementCurrency);
            System.out.println("Using settlement Currency" + tmpCurrency.getCurrencyCode());
            System.out.println("strConversionRate:" + strConversionRate);
            conversionRate = new BigDecimal(strConversionRate);
        }

        for (String key : serviceChargesDetails.keySet()) {

            Object keyValue = serviceChargesDetails.get(key);
            System.out.println("key:" + key);
            System.out.println("keyValue:" + keyValue.toString());

            if (keyValue != null && !keyValue.equals("")) {

                ChargeId chargeId = null;

                if (key.toLowerCase().equals("bankcommission")) {
                    chargeId = new ChargeId("BC");
                } else if (key.toLowerCase().equals("confirmingfee")) {
                    chargeId = new ChargeId("CORRES-CONFIRMING");
                } else if (key.toLowerCase().equals("commitmentfee")) {
                    chargeId = new ChargeId("CF");
                } else if (key.toLowerCase().equals("suppliesfee")) {
                    chargeId = new ChargeId("SUP");
                } else if (key.toLowerCase().equals("cablefee")) {
                    chargeId = new ChargeId("CABLE");
                } else if (key.toLowerCase().equals("cilexfee")) {
                    chargeId = new ChargeId("CILEX");
                } else if (key.toLowerCase().equals("advisingfee")) {
                    chargeId = new ChargeId("CORRES-ADVISING");
                } else if (key.toLowerCase().equals("documentarystamp")) {
                    chargeId = new ChargeId("DOCSTAMPS");
                } else if (key.toLowerCase().equals("notarialFee")) {
                    chargeId = new ChargeId("NOTARIAL");
                } else if (key.toLowerCase().equals("cancellationFee")) {
                    chargeId = new ChargeId("CANCEL");
                } else if (key.toLowerCase().equals("bspFee")) {
                    chargeId = new ChargeId("BSP");
                } else if (key.toLowerCase().equals("bookingCommission")) {
                    chargeId = new ChargeId("BOOKING");
                } else if (key.toLowerCase().equals("interestFee")) {
                    chargeId = new ChargeId("INTEREST");
                } else if (key.toLowerCase().equals("remittanceFee")) {
                    chargeId = new ChargeId("REMITTANCE");
                }


                if (chargeId != null) {
                    BigDecimal pesoAmount = new BigDecimal((String) keyValue).multiply(conversionRate);
                    System.out.println("peso amount:" + pesoAmount);
                    System.out.println("orig amount:" + new BigDecimal((String) keyValue));
                    // For Service Charges, currency is always PHP
                    tradeService.addCharge(chargeId, pesoAmount.setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance("PHP"), userActiveDirectoryId, new BigDecimal((String) keyValue), tmpCurrency);
                }
            }
        }

        return tradeService;
    }

    public static TradeService addServiceChargesForParamMap(TradeService tradeService, Map<String, Object> serviceChargesDetails, Map<String, Object> parameterMap, UserActiveDirectoryId userActiveDirectoryId) {
        System.out.println("addServiceChargesForParamMap   ");

        //TODO Add checking if special rate exists and use that

        String settlementCurrency = (String) serviceChargesDetails.get("settlementCurrency");// Change this to get currency to php
        System.out.println("settlementCurrency used:" + settlementCurrency);
        Currency tmpCurrency = Currency.getInstance("PHP");
        BigDecimal conversionRate = new BigDecimal(1); //Default to php no conversion
        //TODO:: fix fx
        if (settlementCurrency != null && !settlementCurrency.equalsIgnoreCase("") && !settlementCurrency.equalsIgnoreCase("PHP")) {
            tmpCurrency = Currency.getInstance(settlementCurrency);

            if (settlementCurrency.equalsIgnoreCase("USD")) {

                //String conversionRateKey = "USD-PHP_text_special_rate";
                String conversionRateKey = "USD-PHP_urr";
//                String strConversionRate = (String) serviceChargesDetails.get(settlementCurrency);
                String strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_text_special_rate strConversionRate:" + strConversionRate);
                conversionRate = new BigDecimal(strConversionRate);

            }  else {
                //String conversionRateKey = "USD-PHP_text_special_rate";
                String conversionRateKey = "USD-PHP_urr";
                String strConversionRate = (String) parameterMap.get(conversionRateKey);
                System.out.println("USD-PHP_text_special_rate strConversionRate:" + strConversionRate);

                String ThirdToPHPconversionRateKey = "";
                String ThirdToUSDconversionRateKey = "";
                //added by max for validation
                if(ThirdToPHPconversionRateKey == "" || ThirdToPHPconversionRateKey == " " || ThirdToPHPconversionRateKey == null || ThirdToPHPconversionRateKey.isEmpty()){
                	ThirdToPHPconversionRateKey = settlementCurrency.trim().toUpperCase() + "-PHP_text_special_rate_buying";
                } else{
                	ThirdToPHPconversionRateKey = settlementCurrency.trim().toUpperCase() + "-PHP_text_special_rate";
                }
                if(ThirdToUSDconversionRateKey == "" || ThirdToUSDconversionRateKey == " " || ThirdToUSDconversionRateKey == null || ThirdToUSDconversionRateKey.isEmpty()){
                	ThirdToUSDconversionRateKey = settlementCurrency.trim().toUpperCase() + "-USD_text_special_rate_buying";
                }else{
                	 ThirdToUSDconversionRateKey = settlementCurrency.trim().toUpperCase() + "-USD_text_special_rate";
                }
                //end of max validation

                String ThirdToUSD_strConversionRate = (String) parameterMap.get(ThirdToUSDconversionRateKey);
                String ThirdToPHP_strConversionRate = (String) parameterMap.get(ThirdToPHPconversionRateKey);
                System.out.println("ThirdToUSD_strConversionRate :" + ThirdToUSD_strConversionRate);
                System.out.println("ThirdToPHP_strConversionRate :" + ThirdToPHP_strConversionRate);
//                if (ThirdToPHP_strConversionRate != null && ThirdToPHP_strConversionRate != "") {
//                    System.out.println("conversion 1 ");
//                    conversionRate = new BigDecimal(ThirdToPHP_strConversionRate);
//                } else

                if (ThirdToUSD_strConversionRate != null && !"".equalsIgnoreCase(ThirdToUSD_strConversionRate) && strConversionRate != null && !"".equalsIgnoreCase(strConversionRate ) ) {
                    System.out.println("conversion 2 ");
                    BigDecimal usd_php_conversionrate = new BigDecimal(strConversionRate);
                    BigDecimal third_usd_conversionrate = new BigDecimal(ThirdToUSD_strConversionRate);
                    conversionRate = usd_php_conversionrate.multiply(third_usd_conversionrate);
                } else {
                    System.out.println("conversion 3 ");
                    conversionRate = new BigDecimal(1);
                }
                System.out.println("conversionRate:" + conversionRate);

            }
        }        

        System.out.println("JJ " + parameterMap);
    	String strConversionRate = "";
        BigDecimal conversionRateEBP = BigDecimal.ONE;
        
        if (tradeService.getServiceType().equals(ServiceType.NEGOTIATION) && tradeService.getDocumentClass().equals(DocumentClass.BP) && 
        		settlementCurrency != null && !settlementCurrency.equalsIgnoreCase("") && !settlementCurrency.equalsIgnoreCase("PHP")) {
        	String settlementToBeneCurrency = (String) tradeService.getDetails().get("newProceedsCurrency");
        	String conversionRateKey = "USD-PHP_urr";
        	
        	if (settlementToBeneCurrency.equalsIgnoreCase("PHP")) {
        		conversionRateKey = "USD-PHP_text_special_rate";
        	}
        			
        	if (parameterMap.containsKey(conversionRateKey)) {
        		strConversionRate = (String) parameterMap.get(conversionRateKey);
        	} else if (tradeService.getDetails().containsKey(conversionRateKey)) {
        		strConversionRate = (String) tradeService.getDetails().get(conversionRateKey);
        	}        	
        	
            System.out.println("strConversionRate:" + strConversionRate);                        
            conversionRateEBP = new BigDecimal(strConversionRate);
            
        }

        for (String key : parameterMap.keySet()) {
            Object keyValue = parameterMap.get(key);
            Object keyValueOriginal = null;
            Object keyValueOverriddenFlag = null;
            Object keyValueNoCWT = null;
            if (keyValue != null && !keyValue.equals("")) {


                ChargeId chargeId = null;
                //TODO::Make this configurable
                //TODO::YOU CAN STILL IMPROVE THIS
                //TODO::GET these values from the database
                //TODO::Add the other charges
                if (key.equalsIgnoreCase("BC")) {
                    chargeId = new ChargeId("BC");
                } else if (key.equalsIgnoreCase("CORRES-CONFIRMING")) {
                    chargeId = new ChargeId("CORRES-CONFIRMING");
                } else if (key.equalsIgnoreCase("CF")) {
                    chargeId = new ChargeId("CF");
                } else if (key.equalsIgnoreCase("SUP")) {
                    chargeId = new ChargeId("SUP");
                } else if (key.equalsIgnoreCase("CABLE")) {
                    chargeId = new ChargeId("CABLE");
                } else if (key.equalsIgnoreCase("CILEX")) {
                    chargeId = new ChargeId("CILEX");
                } else if (key.equalsIgnoreCase("CORRES-ADVISING")) {
                    chargeId = new ChargeId("CORRES-ADVISING");
                } else if (key.equalsIgnoreCase("DOCSTAMPS")) {
                    chargeId = new ChargeId("DOCSTAMPS");
                } else if (key.equalsIgnoreCase("NOTARIAL")) {
                    chargeId = new ChargeId("NOTARIAL");
                } else if (key.equalsIgnoreCase("BSP")) {
                    chargeId = new ChargeId("BSP");
                } else if (key.equalsIgnoreCase("BOOKING")) {
                    chargeId = new ChargeId("BOOKING");
                } else if (key.equalsIgnoreCase("REMITTANCE")) {
                    chargeId = new ChargeId("REMITTANCE");
                } else if (key.equalsIgnoreCase("ADVISING-EXPORT")) {
                    chargeId = new ChargeId("ADVISING-EXPORT");
                } else if (key.equalsIgnoreCase("OTHER-EXPORT")) {
                    chargeId = new ChargeId("OTHER-EXPORT");
                } else if (key.equalsIgnoreCase("CANCEL")) {
                    chargeId = new ChargeId("CANCEL");
                } else if (key.equalsIgnoreCase("POSTAGE")) {
                    chargeId = new ChargeId("POSTAGE");
                } else if (key.equalsIgnoreCase("CORRES-EXPORT")) {
                    chargeId = new ChargeId("CORRES-EXPORT");
                }



                if (chargeId != null) {
                    System.out.println(key+"original");
                    if(parameterMap.containsKey(key+"original")){
                        keyValueOriginal = parameterMap.get(key+"original");
                    }
                    if(parameterMap.containsKey(key+"overridenFlag")){
                        keyValueOverriddenFlag = parameterMap.get(key+"overridenFlag");
                    }
                    if(parameterMap.containsKey(key+"nocwtAmount")){
                        keyValueNoCWT = parameterMap.get(key+"nocwtAmount");
                    }
                    System.out.println("+keyValueOriginal:::"+keyValueOriginal);
                    System.out.println("+keyValueOverridenFlag:::"+keyValueOverriddenFlag);
                    System.out.println("+keyValueNoCWT:::"+keyValueNoCWT);
                    BigDecimal pesoAmount = new BigDecimal((String) keyValue).multiply(conversionRate);
                    String overriddenFlag;
                    BigDecimal defaultAmount = pesoAmount;
                    
                    if (tradeService.getServiceType().equals(ServiceType.NEGOTIATION) && tradeService.getDocumentClass().equals(DocumentClass.BP) &&
                    		!strConversionRate.equalsIgnoreCase("")) {                    	
                    	pesoAmount = new BigDecimal((String) keyValue).multiply(conversionRateEBP).setScale(2, BigDecimal.ROUND_HALF_UP);
                        System.out.println("defaultAmount:" + pesoAmount); 
                        defaultAmount = pesoAmount;

                    }
                    
                    //JJ Export Charges
                    if (((tradeService.getServiceType().equals(ServiceType.NEGOTIATION) && tradeService.getDocumentClass().equals(DocumentClass.BP)) ||
                    		(tradeService.getServiceType().equals(ServiceType.SETTLEMENT) && tradeService.getDocumentClass().equals(DocumentClass.BC))) &&
                    		chargeId.toString().equalsIgnoreCase("BC") && pesoAmount.compareTo(BigDecimal.valueOf(1000)) == -1) {
                    	
                    	if (pesoAmount.setScale(0, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.valueOf(980))!=0 &&
                    			pesoAmount.setScale(0, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.valueOf(0))!=0) {
                    		System.out.println("NO CWT");
                        	pesoAmount = BigDecimal.valueOf(1000);
                        	defaultAmount = pesoAmount;
                    	} else if (pesoAmount.setScale(0, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.valueOf(0))==0) {
                        	pesoAmount = BigDecimal.valueOf(0);
                    	} 
                    }

                    //JJ Export Charges
                    if (((tradeService.getServiceType().equals(ServiceType.NEGOTIATION) && tradeService.getDocumentClass().equals(DocumentClass.BP)) ||
                    		(tradeService.getServiceType().equals(ServiceType.SETTLEMENT) && tradeService.getDocumentClass().equals(DocumentClass.BC))) &&
                    		chargeId.toString().equalsIgnoreCase("POSTAGE")) {
                    	
                    	if (pesoAmount.setScale(0, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.valueOf(400))==0) {
                        	pesoAmount = BigDecimal.valueOf(400);
                        	defaultAmount = pesoAmount;
                    	}
                    }
                    
                    if(keyValueOriginal!=null && !keyValueOriginal.toString().equalsIgnoreCase("")){
                        BigDecimal ttmptmp = new BigDecimal((String) keyValueOriginal);
                        if(ttmptmp.compareTo(BigDecimal.ZERO)==1){

                            if(key.equalsIgnoreCase("CORRES-ADVISING")){
                                defaultAmount = ttmptmp.multiply(conversionRate);
                            } else {
                                defaultAmount = ttmptmp;
                            }
//                            defaultAmount = ttmptmp.multiply(conversionRate);
                            System.out.println("TODO::ASK WHAT IS REQUIRED");
                            //TODO::ASK WHAT IS REQUIRED
                        }
                    } else {
                        System.out.println("defaultAmount = pesoAmount;");
                        defaultAmount = pesoAmount;
                    }

                    if(keyValueOverriddenFlag!=null && !keyValueOverriddenFlag.toString().equalsIgnoreCase("")){
                        overriddenFlag = keyValueOverriddenFlag.toString();
                    }else{
                        overriddenFlag = "N";
                    }

                    BigDecimal nocwtAmount = pesoAmount;
                    if(keyValueNoCWT!=null && !keyValueNoCWT.toString().equalsIgnoreCase("")){
                        BigDecimal ttmptmp = new BigDecimal((String) keyValueNoCWT);
                        if(ttmptmp.compareTo(BigDecimal.ZERO)==1){
                            System.out.println("TODO::ASK WHAT IS REQUIRED");
                            //TODO::ASK WHAT IS REQUIRED
                            nocwtAmount = ttmptmp;
                        }
                    } else {
                        System.out.println("nocwtAmount = pesoAmount;");
                        nocwtAmount = pesoAmount;
                    }

//                    System.out.println("default amount:" + defaultAmount);
//                    System.out.println("nocwtAmount amount:" + nocwtAmount);

                    System.out.println("chargeId:" + chargeId.toString()+ " orig amount:" + new BigDecimal((String) keyValue) +" peso amount:" + pesoAmount);
                    // For Service Charges, currency is always PHP
                    //tradeService.addCharge(chargeId, pesoAmount.setScale(2, BigDecimal.ROUND_UP), Currency.getInstance("PHP"), userActiveDirectoryId, new BigDecimal((String) keyValue).setScale(2, BigDecimal.ROUND_UP), tmpCurrency);
                    tradeService.addCharge(chargeId,
                            pesoAmount.setScale(2, BigDecimal.ROUND_UP), Currency.getInstance("PHP"), userActiveDirectoryId,
                            new BigDecimal((String) keyValue).setScale(2, BigDecimal.ROUND_UP), tmpCurrency,
                            defaultAmount, overriddenFlag, nocwtAmount);
                }
            }
        }

        return tradeService;
    }

    public static TradeService updateProductCharge(TradeService tradeService, Map<String, Object> details, UserActiveDirectoryId userActiveDirectoryId) {
        tradeService.updateProductCharge(details, userActiveDirectoryId);
        return tradeService;
    }

    private static void printCurrentAmendedFields(Map<String, Object> parameterMap) {
        System.out.println("\nSTART: ========== CURRENT AMENDED FIELDS");
        Iterator it = parameterMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
        System.out.println("END: ========== CURRENT AMENDED FIELDS\n");
    }
    
    public static TradeService setSpecialRates(TradeService tradeService, Map<String, Object> details){
    	String currency = null;
        if ((details.get("currency") != null && !((String) details.get("currency")).trim().equals("")) ||
    		(details.get("negotiationCurrency") != null && !((String) details.get("negotiationCurrency")).trim().equals(""))) {
			currency = (details.get("negotiationCurrency") != null && !((String) details
					.get("negotiationCurrency")).trim().equals("")) ? details
					.get("negotiationCurrency").toString().trim() : details
					.get("currency").toString().trim();
            System.out.println("setSpecialRates :"+ currency );
            Map<String, BigDecimal> rates = new HashMap<String, BigDecimal>();
        	if(!currency.equals("PHP") && !currency.equals("USD")){
        		if(details.get(currency+"-USD_pass_on_rate_cash") != null){
        			rates.put("specialRateThirdToUsd", new BigDecimal(details.get(currency+"-USD_pass_on_rate_cash").toString().trim()));
        		} else if(details.get(currency+"-USD_pass_on_rate_charges") != null){
        			rates.put("specialRateThirdToUsd", new BigDecimal(details.get(currency+"-USD_pass_on_rate_charges").toString().trim()));
        		} else if(details.get(currency+"-USD") != null && !details.get(currency+"-USD").toString().trim().equals(currency+"-USD")){
        			rates.put("specialRateThirdToUsd", new BigDecimal(details.get(currency+"-USD").toString().trim()));
        		}
        	}
        	
        	if(details.get("USD-PHP_pass_on_rate_cash") != null){
        		rates.put("specialRateUsdToPhp", new BigDecimal(details.get("USD-PHP_pass_on_rate_cash").toString().trim()));
        	} else if(details.get("USD-PHP_pass_on_rate_charges") != null){
        		rates.put("specialRateUsdToPhp", new BigDecimal(details.get("USD-PHP_pass_on_rate_charges").toString().trim()));
        	} else if(details.get("USD-PHP") != null && !details.get("USD-PHP").toString().trim().equals("USD-PHP")){
    			rates.put("specialRateUsdToPhp", new BigDecimal(details.get("USD-PHP").toString().trim()));
    		} else if(details.get("usdToPHPSpecialRate") != null){
    			rates.put("specialRateUsdToPhp", new BigDecimal(details.get("usdToPHPSpecialRate").toString().trim()));
    		} else if(details.get("USD-PHP_urr") != null){
    			rates.put("specialRateUsdToPhp", new BigDecimal(details.get("USD-PHP_urr").toString().trim()));
    		} else if(details.get("urr") != null){
    			rates.put("specialRateUsdToPhp", new BigDecimal(details.get("urr").toString().trim()));
    		}

            if(details.get("urr") != null){
                rates.put("urr", new BigDecimal(details.get("urr").toString().trim()));
            }
            System.out.println("rates:"+rates);

            tradeService.setSpecialRates(rates);
        }
        
        return tradeService;
    }
    
}
