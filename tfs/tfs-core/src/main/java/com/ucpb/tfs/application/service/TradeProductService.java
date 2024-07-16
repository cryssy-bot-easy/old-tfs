package com.ucpb.tfs.application.service;

import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.product.enums.LCTenor;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 8/17/12
 */
 
 /**
	(revision)
	SCR/ER Number: SCR# IBD-15-1125-01
	SCR/ER Description: LC Amendment - Inclusion of Buyer and Seller's Name and Address
	[Revised by:] Jonh Henry Santos Alabin
	[Date revised:] 1/12/2017
	Program [Revision] Details: Added conditional Statements for Buyer Info
	Member Type: JAVA
	Project: CORE
	Project Name: TradeProductService.java
*/

/**
(revision)
    Reference Number: ITDJCH-2018-03-001
    Reference Description: Add new fields on screen of different modules to comply with the requirements of ITRS.
    [Revised by:] Jaivee Hipolito
    [Date revised:] 03/16/2018
    Program [Revision:] add field commodity code.
    PROJECT: JAVA
    MEMBER TYPE  : CORE
    Project Name:  TradeProductService.gsp
*/

/**
(revision)
    [Revised by:] Cedrick C. Nungay
    [Date revised:] 08/16/2018
    Program [Revision:] Modified amendment of confirmation instruction flag, purpose of message, 
        other place of expiry, drawee, mixed payment details, deferred payment details, general description,
        special payment conditions and charges narrative for MT707
    PROJECT: JAVA
    MEMBER TYPE  : CORE
    Project Name:  TradeProductService.gsp
 */

/**
(revision)
    [Revised by:] Cedrick C. Nungay
    [Date revised:] 09/13/2018
    Program [Revision:] Added amendment of narrative for MT747
    PROJECT: JAVA
    MEMBER TYPE  : CORE
    Project Name:  TradeProductService.gsp
 */

public class TradeProductService {

    public static LetterOfCredit createLetterOfCredit(DocumentNumber documentNumber, Map<String, Object> details) {

        LetterOfCredit lc = new LetterOfCredit(documentNumber, details);

        DocumentSubType1 documentSubType1 = DocumentSubType1.valueOf((String) details.get("documentSubType1"));

        // If CASH
        if (documentSubType1.equals(DocumentSubType1.CASH)) {
            lc.setInitiallyAsCash();
        }

        return lc;
    }

    public static LetterOfCredit updateLetterOfCredit(LetterOfCredit lc, Map<String, Object> details) {

        lc.updateDetails(details);
        return lc;
    }

    private static String getAmendedWithCodes(String fromValue, String toValue) {
        int code = 0;

        boolean hasNoCode = false;
        if (fromValue == null) {
            fromValue = "";
        }

        for (String line : toValue.split("\r\n")) {
            int lineLength = line.length();
            hasNoCode = false;
            if (lineLength >= 5 && line.substring(0, 5).equalsIgnoreCase("/add/")) {
                code = 1;
                line = line.substring(5, lineLength);
            } else if (lineLength >= 8 && line.substring(0, 8).equalsIgnoreCase("/delete/")) {
                code = 2;
                line = line.substring(8, lineLength);
            } else if (lineLength >= 8 && line.substring(0, 8).equalsIgnoreCase("/repall/")) {
                code = 3;
                line = line.substring(8, lineLength);
            } else {
                hasNoCode = true;
            }

            if (!line.equalsIgnoreCase("")) {
                switch(code) {
                case 1: // add
                    fromValue = fromValue + (fromValue.equalsIgnoreCase("") ? "\r\n" : "") + line;
                    break;
                case 2: // delete
                	fromValue = fromValue.trim();
                    fromValue = fromValue.replace(line.trim(), "").trim();
                    break;
                case 3: // repall
                    if (hasNoCode) {
                        fromValue = fromValue + "\r\n" + line;
                    } else {
                        fromValue = line;
                    }
                    break;
                }
            }
        }
        return fromValue.replaceAll("\r\n\r\n", "\r\n");
    }

    public static LetterOfCredit amendLc(LetterOfCredit lc, Map<String, Object> detailsAmend) throws Exception {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        lc.setReceiversReference((String) detailsAmend.get("receiversReference"));

        // Basic Details tab
        if (detailsAmend.get("amountSwitch") != null && (!((String)detailsAmend.get("amountSwitch")).equals("")) && (!((String)detailsAmend.get("amountSwitch")).toLowerCase().equals("off"))) {
            if (detailsAmend.get("amountTo") != null && (!((String)detailsAmend.get("amountTo")).equals(""))) {

                System.out.println("1 =================================== amountTo");

                // This is always the same as the original LC currency, so no need to check
                BigDecimal amount = new BigDecimal((String)detailsAmend.get("amountTo"));
                lc.amendLcAmount(amount);
            }
        }
        if (detailsAmend.get("expiryCountryCodeSwitch") != null && (!((String)detailsAmend.get("expiryCountryCodeSwitch")).equals("")) && (!((String)detailsAmend.get("expiryCountryCodeSwitch")).equals("off"))) {
                System.out.println("2 =================================== expiryCountryCodeTo");
                lc.amendExpiryCountryCode((String)detailsAmend.get("expiryCountryCodeTo"));
        }
        if (detailsAmend.get("destinationBankSwitch") != null && (!((String)detailsAmend.get("destinationBankSwitch")).equals("")) && (!((String)detailsAmend.get("destinationBankSwitch")).equals("off"))) {
                System.out.println("3 =================================== destinationBankTo");
                lc.amendDestinationBank((String)detailsAmend.get("destinationBankTo"));
        }
        if (detailsAmend.get("tenorSwitch") != null && (!((String)detailsAmend.get("tenorSwitch")).equals("")) && (!((String)detailsAmend.get("tenorSwitch")).equals("off"))) {
            if (detailsAmend.get("tenorTo") != null && (!((String)detailsAmend.get("tenorTo")).equals(""))) {
                System.out.println("4 =================================== tenorTo");
                LCTenor tenor = LCTenor.valueOf((String)detailsAmend.get("tenorTo"));
                Long usancePeriod = 0L;
                if (detailsAmend.get("usancePeriodTo") != null && !((String)detailsAmend.get("usancePeriodTo")).equals("")) {
                    // usancePeriod = Long.getLong((String)detailsAmend.get("usancePeriodTo"));
                    usancePeriod = new Long((String)detailsAmend.get("usancePeriodTo"));
                }
                System.out.println("4a ================================== usancePeriod = " + usancePeriod);
                lc.amendTenor(tenor, usancePeriod);
                lc.amendTenorOfDraftNarrative((String) detailsAmend.get("tenorOfDraftNarrativeTo"));
            }
        }
        if (detailsAmend.get("applicableRulesSwitch") != null && (!((String)detailsAmend.get("applicableRulesSwitch")).equals("")) && (!((String)detailsAmend.get("applicableRulesSwitch")).equals("off"))) {
                System.out.println("5 =================================== applicableRulesTo");
                lc.amendApplicableRules((String)detailsAmend.get("applicableRulesTo"));
        }
        if (detailsAmend.get("requestedConfirmationPartySwitch") != null && (!((String)detailsAmend.get("requestedConfirmationPartySwitch")).equals("")) && (!((String)detailsAmend.get("requestedConfirmationPartySwitch")).equals("off"))) {
        	System.out.println("5 =================================== requestedConfirmationPartyTo");
        	lc.setRequestedConfirmationParty((String)detailsAmend.get("requestedConfirmationPartyTo"));
        }
        if (detailsAmend.get("confirmationInstructionsFlagSwitch") != null && (!((String)detailsAmend.get("confirmationInstructionsFlagSwitch")).equals("")) && (!((String)detailsAmend.get("confirmationInstructionsFlagSwitch")).equals("off"))) {
                System.out.println("6 =================================== confirmationInstructionsFlagTo");
                String confirmationInstructionsFlagTo = (String)detailsAmend.get("confirmationInstructionsFlagTo");
                if (confirmationInstructionsFlagTo.equalsIgnoreCase("YES")) {
                    confirmationInstructionsFlagTo = "Y";
                } else if (confirmationInstructionsFlagTo.equalsIgnoreCase("NO")) {
                    confirmationInstructionsFlagTo = "N";
                } else if (confirmationInstructionsFlagTo.equalsIgnoreCase("MAY ADD")) {
                    confirmationInstructionsFlagTo = "M";
                }
                lc.amendConfirmationInstructionsFlag(confirmationInstructionsFlagTo);
        }
        if (detailsAmend.get("expiryDateSwitch") != null && (!((String)detailsAmend.get("expiryDateSwitch")).equals("")) && (!((String)detailsAmend.get("expiryDateSwitch")).equals("off"))) {
            if (detailsAmend.get("expiryDateTo") != null && (!((String)detailsAmend.get("expiryDateTo")).equals(""))) {
                System.out.println("7 =================================== expiryDateTo");
                lc.amendExpiryDate(df.parse((String)detailsAmend.get("expiryDateTo")));
            }
        }
        if (detailsAmend.get("formOfDocumentaryCreditSwitch") != null && (!((String)detailsAmend.get("formOfDocumentaryCreditSwitch")).equals("")) && (!((String)detailsAmend.get("formOfDocumentaryCreditSwitch")).equals("off"))) {
                System.out.println("8 =================================== formOfDocumentaryCreditTo");
                lc.amendFormOfDocumentaryCredit((String)detailsAmend.get("formOfDocumentaryCreditTo"));
        }
        if (detailsAmend.get("purposeOfMessageSwitch") != null && (!((String)detailsAmend.get("purposeOfMessageSwitch")).equals("")) && (!((String)detailsAmend.get("purposeOfMessageSwitch")).equals("off"))) {
                System.out.println("8 =================================== purposeOfMessageTo");
                lc.amendPurposeOfMessage((String)detailsAmend.get("purposeOfMessageTo"));
        }
        if (detailsAmend.get("otherPlaceOfExpirySwitch") != null && (!((String)detailsAmend.get("otherPlaceOfExpirySwitch")).equals("")) && (!((String)detailsAmend.get("otherPlaceOfExpirySwitch")).equals("off"))) {
        	System.out.println("8 =================================== otherPlaceOfExpiryTo");
        	lc.amendOtherPlaceOfExpiry((String)detailsAmend.get("otherPlaceOfExpiryTo"));
        }


        // Importer/Exporter Tab
        if (detailsAmend.get("importerCbCodeSwitch") != null && (!((String)detailsAmend.get("importerCbCodeSwitch")).equals("")) && (!((String)detailsAmend.get("importerCbCodeSwitch")).equals("off"))) {
        	System.out.println("9 =================================== importerCbCodeTo");
        	lc.amendImporterCbCode((String)detailsAmend.get("importerCbCodeTo"));
        }

        if (detailsAmend.get("importerNameSwitch") != null && (!((String)detailsAmend.get("importerNameSwitch")).equals("")) && (!((String)detailsAmend.get("importerNameSwitch")).equals("off"))) {
                System.out.println("9 =================================== importerNameTo");
                lc.amendImporterName((String)detailsAmend.get("importerNameTo"));
        }

        if (detailsAmend.get("importerAddressSwitch") != null && (!((String)detailsAmend.get("importerAddressSwitch")).equals("")) && (!((String)detailsAmend.get("importerAddressSwitch")).equals("off"))) {
                System.out.println("10 =================================== importerAddressTo");
                lc.amendImporterAddress((String)detailsAmend.get("importerAddressTo"));
        }

        if (detailsAmend.get("exporterCbCodeSwitch") != null && (!((String)detailsAmend.get("exporterCbCodeSwitch")).equals("")) && (!((String)detailsAmend.get("exporterCbCodeSwitch")).equals("off"))) {
                System.out.println("11 =================================== exporterCbCodeTo");
                lc.amendExporterCbCode((String)detailsAmend.get("exporterCbCodeTo"));
        }

        if (detailsAmend.get("exporterNameSwitch") != null && (!((String)detailsAmend.get("exporterNameSwitch")).equals("")) && (!((String)detailsAmend.get("exporterNameSwitch")).equals("off"))) {
                System.out.println("12 =================================== exporterNameTo");
                lc.amendExporterName((String)detailsAmend.get("exporterNameTo"));
        }

        if (detailsAmend.get("exporterAddressSwitch") != null && (!((String)detailsAmend.get("exporterAddressSwitch")).equals("")) && (!((String)detailsAmend.get("exporterAddressSwitch")).equals("off"))) {
                System.out.println("13 =================================== exporterAddressTo");
                lc.amendExporterAddress((String) detailsAmend.get("exporterAddressTo"));
        }

//        if (detailsAmend.get("positiveToleranceLimitSwitchDisplay") != null && (!((String)detailsAmend.get("positiveToleranceLimitSwitchDisplay")).equals(""))) {
        if (detailsAmend.get("positiveToleranceLimitSwitch") != null && (!((String)detailsAmend.get("positiveToleranceLimitSwitch")).equals("")) && (!((String)detailsAmend.get("positiveToleranceLimitSwitch")).equals("off"))) {
            BigDecimal pos = BigDecimal.ZERO;
            if (detailsAmend.get("positiveToleranceLimitTo") != null && (!((String)detailsAmend.get("positiveToleranceLimitTo")).equals(""))) {
                pos = new BigDecimal((String)detailsAmend.get("positiveToleranceLimitTo"));
            }
            System.out.println("14 =================================== positiveToleranceLimitTo");
            lc.amendPositiveToleranceLimit(pos);
        }

//        if (detailsAmend.get("negativeToleranceLimitSwitchDisplay") != null && (!((String)detailsAmend.get("negativeToleranceLimitSwitchDisplay")).equals(""))) {
        if (detailsAmend.get("negativeToleranceLimitSwitch") != null && (!((String)detailsAmend.get("negativeToleranceLimitSwitch")).equals("")) && (!((String)detailsAmend.get("negativeToleranceLimitSwitch")).equals("off"))) {
            BigDecimal neg = BigDecimal.ZERO;
            if (detailsAmend.get("negativeToleranceLimitTo") != null && (!((String)detailsAmend.get("negativeToleranceLimitTo")).equals(""))) {
                neg = new BigDecimal((String)detailsAmend.get("negativeToleranceLimitTo"));
            }
            System.out.println("15 =================================== negativeToleranceLimitTo");
            lc.amendNegativeToleranceLimit(neg);
        }

//        if (detailsAmend.get("maximumCreditAmountSwitchDisplay") != null && (!((String)detailsAmend.get("maximumCreditAmountSwitchDisplay")).equals(""))) {
        if (detailsAmend.get("maximumCreditAmountSwitch") != null && (!((String)detailsAmend.get("maximumCreditAmountSwitch")).equals("")) && (!((String)detailsAmend.get("maximumCreditAmountSwitch")).equals("off"))) {
                System.out.println("16 =================================== maximumCreditAmountTo");
                lc.amendMaxCreditAmount((String)detailsAmend.get("maximumCreditAmountTo"));
        }

//        if (detailsAmend.get("additionalAmountsCoveredSwitch") != null && (!((String)detailsAmend.get("additionalAmountsCoveredSwitch")).equals(""))) {
//        if (detailsAmend.get("additionalAmountsCovered") != null && (!((String)detailsAmend.get("additionalAmountsCovered")).equals("")) && (!((String)detailsAmend.get("additionalAmountsCovered")).equals("off"))) {
//                System.out.println("17 =================================== additionalAmountsCoveredTo");
//                lc.amendAdditionalAmountsCovered((String)detailsAmend.get("additionalAmountsCoveredTo"));
//        }

        if (detailsAmend.get("additionalAmountsCoveredSwitch") != null && (!((String)detailsAmend.get("additionalAmountsCoveredSwitch")).equals("")) && (!((String)detailsAmend.get("additionalAmountsCoveredSwitch")).equals("off"))) {
        	System.out.println("17 =================================== additionalAmountsCoveredTo");
        	lc.amendAdditionalAmountsCovered((String)detailsAmend.get("additionalAmountsCoveredTo"));
        }

        if (detailsAmend.get("availableWithSwitch") != null && (!((String)detailsAmend.get("availableWithSwitch")).equals("")) && (!((String)detailsAmend.get("availableWithSwitch")).equals("off"))) {
//            if ((detailsAmend.get("availableWithTo") != null && (!((String)detailsAmend.get("availableWithTo")).equals(""))) &&
//                (detailsAmend.get("availableWithFlagTo") != null && (!((String)detailsAmend.get("availableWithFlagTo")).equals("")))) {
//                System.out.println("18 =================================== availableWithTo");
//                lc.amendAvailableWith((String)detailsAmend.get("availableWithTo"), (String)detailsAmend.get("identifierCodeTo"), (String)detailsAmend.get("nameAndAddressTo"));
//            }
            
            if (detailsAmend.get("availableWithFlagTo") != null && !((String)detailsAmend.get("availableWithFlagTo")).equals("")){
            	if ((detailsAmend.get("availableWithTo") != null || detailsAmend.get("nameAndAddressTo") != null) && 
                    (!((String)detailsAmend.get("availableWithTo")).equals("") || !((String)detailsAmend.get("nameAndAddressTo")).equals(""))){
            		
            		System.out.println("18A =================================== availableWithTo");            		
            		lc.amendAvailableWithNew((String)detailsAmend.get("availableWithFlagTo"), (String)detailsAmend.get("availableWithTo"), (String)detailsAmend.get("nameAndAddressTo"));
            	}
            } else if (detailsAmend.get("availableWithFlagMt") != null && !((String)detailsAmend.get("availableWithFlagMt")).equals("")) {
            	if ((detailsAmend.get("availableWithTo") != null || detailsAmend.get("nameAndAddressTo") != null) && 
                    (!((String)detailsAmend.get("availableWithTo")).equals("") || !((String)detailsAmend.get("nameAndAddressTo")).equals(""))){    
            		
            		System.out.println("18B =================================== availableWithTo");  
                	lc.amendAvailableWithNew((String)detailsAmend.get("availableWithFlagMt"), (String)detailsAmend.get("availableWithTo"), (String)detailsAmend.get("nameAndAddressTo"));
                }
            }
            
        }
        
//        if (detailsAmend.get("availableWithSwitch") != null && (!((String)detailsAmend.get("availableWithSwitch")).equals("")) && (!((String)detailsAmend.get("availableWithSwitch")).equals("off"))) {
//            System.out.println("18 =================================== availableWithTo");
//            String availableWithTo = (String)detailsAmend.get("availableWithTo");
//            if (availableWithTo == null) {
//            	availableWithTo = (String)detailsAmend.get("availableWith");
//            }
//            lc.amendAvailableWith(availableWithTo, (String)detailsAmend.get("identifierCodeTo"), (String)detailsAmend.get("nameAndAddressTo"));
//        }

        if (detailsAmend.get("availableBySwitch") != null && (!((String)detailsAmend.get("availableBySwitch")).equals("")) && (!((String)detailsAmend.get("availableBySwitch")).equals("off"))) {
                System.out.println("19 =================================== availableByTo");
                lc.amendAvailableBy((String)detailsAmend.get("availableByTo"));
//                System.out.println("20 =================================== draweeTo");
//                lc.amendDrawee((String)detailsAmend.get("draweeTo"));
//                System.out.println("21 =================================== tenorOfDraftNarrativeTo");
//                lc.amendTenorOfDraftNarrative((String)detailsAmend.get("tenorOfDraftNarrativeTo"));
//                System.out.println("22 =================================== mixedPaymentDetailsTo");
//                lc.amendMixedPaymentDetails((String)detailsAmend.get("mixedPaymentDetailsTo"));
//                System.out.println("23 =================================== deferredPaymentDetailsTo");
//                lc.amendDeferredPaymentDetails((String)detailsAmend.get("deferredPaymentDetailsTo"));
        }

        if (detailsAmend.get("draweeSwitch") != null && (!((String)detailsAmend.get("draweeSwitch")).equals("")) && (!((String)detailsAmend.get("draweeSwitch")).equals("off"))) {
                System.out.println("20 =================================== draweeTo");
                lc.amendDrawee((String)detailsAmend.get("draweeTo"));
        }
        
        if (detailsAmend.get("mixedPaymentDetailsSwitch") != null && (!((String)detailsAmend.get("mixedPaymentDetailsSwitch")).equals("")) && (!((String)detailsAmend.get("mixedPaymentDetailsSwitch")).equals("off"))) {
                System.out.println("22 =================================== mixedPaymentDetailsTo");
                lc.amendMixedPaymentDetails((String)detailsAmend.get("mixedPaymentDetailsTo"));
        }
        
        if (detailsAmend.get("deferredPaymentDetailsSwitch") != null && (!((String)detailsAmend.get("deferredPaymentDetailsSwitch")).equals("")) && (!((String)detailsAmend.get("deferredPaymentDetailsSwitch")).equals("off"))) {
                System.out.println("23 =================================== deferredPaymentDetailsTo");
                lc.amendDeferredPaymentDetails((String)detailsAmend.get("deferredPaymentDetailsTo"));
        }

        if (detailsAmend.get("partialShipmentSwitch") != null && (!((String)detailsAmend.get("partialShipmentSwitch")).equals("")) && (!((String)detailsAmend.get("partialShipmentSwitch")).equals("off"))) {
                lc.amendPartialShipment((String)detailsAmend.get("partialShipmentTo"));
        }
        if (detailsAmend.get("transShipmentSwitch") != null && (!((String)detailsAmend.get("transShipmentSwitch")).equals("")) && (!((String)detailsAmend.get("transShipmentSwitch")).equals("off"))) {
                System.out.println("25 =================================== transShipmentTo");
                lc.amendTransShipment((String)detailsAmend.get("transShipmentTo"));
        }

        if (detailsAmend.get("placeOfTakingDispatchOrReceiptSwitch") != null && (!((String)detailsAmend.get("placeOfTakingDispatchOrReceiptSwitch")).equals("")) && (!((String)detailsAmend.get("placeOfTakingDispatchOrReceiptSwitch")).equals("off"))) {
                System.out.println("26 =================================== placeOfTakingDispatchOrReceiptTo");
                lc.amendPlaceDispatchReceipt((String)detailsAmend.get("placeOfTakingDispatchOrReceiptTo"));
        }

        if (detailsAmend.get("portOfLoadingOrDepartureSwitch") != null && (!((String)detailsAmend.get("portOfLoadingOrDepartureSwitch")).equals("")) && (!((String)detailsAmend.get("portOfLoadingOrDepartureSwitch")).equals("off"))) {
                System.out.println("27 =================================== portOfLoadingOrDepartureTo");
                lc.amendPortLoadingDeparture((String)detailsAmend.get("portOfLoadingOrDepartureTo"));
//                System.out.println("28 =================================== bspCountryCodeTo");
//                lc.amendBspCountryCode((String)detailsAmend.get("bspCountryCodeTo"));
        }
        
        if (detailsAmend.get("bspCountryCodeSwitch") != null && (!((String)detailsAmend.get("bspCountryCodeSwitch")).equals("")) && (!((String)detailsAmend.get("bspCountryCodeSwitch")).equals("off"))){
            System.out.println("28 =================================== bspCountryCodeTo");
            lc.amendBspCountryCode((String)detailsAmend.get("bspCountryCodeTo"));
        }

        if (detailsAmend.get("portOfDischargeOrDestinationSwitch") != null && (!((String)detailsAmend.get("portOfDischargeOrDestinationSwitch")).equals("")) && (!((String)detailsAmend.get("portOfDischargeOrDestinationSwitch")).equals("off"))) {
                System.out.println("29 =================================== portOfDischargeOrDestinationTo");
                lc.amendPortDischargeDestination((String)detailsAmend.get("portOfDischargeOrDestinationTo"));
        }

        if (detailsAmend.get("placeOfFinalDestinationSwitch") != null && (!((String)detailsAmend.get("placeOfFinalDestinationSwitch")).equals("")) && (!((String)detailsAmend.get("placeOfFinalDestinationSwitch")).equals("off"))) {
                System.out.println("30 =================================== placeOfFinalDestinationTo");
                lc.amendPlaceFinalDestination((String)detailsAmend.get("placeOfFinalDestinationTo"));
        }


        // Shipments/Goods Tab
        if (detailsAmend.get("latestShipmentDateSwitch") != null && (!((String)detailsAmend.get("latestShipmentDateSwitch")).equals("")) && (!((String)detailsAmend.get("latestShipmentDateSwitch")).equals("off"))) {
            if (detailsAmend.get("latestShipmentDateTo") != null && (!((String)detailsAmend.get("latestShipmentDateTo")).equals(""))) {
                System.out.println("31 =================================== latestShipmentDateTo");
                lc.amendLatestShipmentDate(df.parse((String)detailsAmend.get("latestShipmentDateTo")));
            }
        }
        if (detailsAmend.get("shipmentPeriodSwitch") != null && (!((String)detailsAmend.get("shipmentPeriodSwitch")).equals("")) && (!((String)detailsAmend.get("shipmentPeriodSwitch")).equals("off"))) {
            System.out.println("32 =================================== shipmentPeriodTo");
            lc.amendShipmentPeriod((String)detailsAmend.get("shipmentPeriodTo"));
        }
        if (detailsAmend.get("generalDescriptionOfGoodsSwitch") != null && (!((String)detailsAmend.get("generalDescriptionOfGoodsSwitch")).equals("")) && (!((String)detailsAmend.get("generalDescriptionOfGoodsSwitch")).equals("off"))) {
            System.out.println("33 =================================== generalDescriptionOfGoodsTo");
            if (((String)detailsAmend.get("documentType")).equals("FOREIGN") && !((String)detailsAmend.get("documentSubType1")).equals("STANDBY")) {
            	String fromValue = (String)detailsAmend.get("generalDescriptionOfGoodsFrom");
                String toValue = (String)detailsAmend.get("generalDescriptionOfGoodsTo");
                System.out.println("From: " + fromValue);
                System.out.println("To: " + toValue);
             	lc.amendGeneralDescriptionOfGoods(getAmendedWithCodes(fromValue, toValue));
            } else {
             	lc.amendGeneralDescriptionOfGoods((String)detailsAmend.get("generalDescriptionOfGoodsTo"));
            }
        }

        if (detailsAmend.get("specialPaymentConditionsForBeneficiarySwitch") != null && (!((String)detailsAmend.get("specialPaymentConditionsForBeneficiarySwitch")).equals("")) && (!((String)detailsAmend.get("specialPaymentConditionsForBeneficiarySwitch")).equals("off"))) {
            System.out.println("34 =================================== specialPaymentConditionsForBeneficiaryTo");
            if (((String)detailsAmend.get("documentType")).equals("FOREIGN") && !((String)detailsAmend.get("documentSubType1")).equals("STANDBY")) {
                String fromValue = (String)detailsAmend.get("specialPaymentConditionsForBeneficiaryFrom");
                String toValue = (String)detailsAmend.get("specialPaymentConditionsForBeneficiaryTo");
                lc.setSpecialPaymentConditionsForBeneficiary(getAmendedWithCodes(fromValue, toValue));
            } else {
                lc.setSpecialPaymentConditionsForBeneficiary((String)detailsAmend.get("specialPaymentConditionsForBeneficiaryTo"));
            }
        }
        
        if (detailsAmend.get("specialPaymentConditionsForReceivingBankSwitch") != null && (!((String)detailsAmend.get("specialPaymentConditionsForReceivingBankSwitch")).equals("")) && (!((String)detailsAmend.get("specialPaymentConditionsForReceivingBankSwitch")).equals("off"))) {
            System.out.println("34 =================================== specialPaymentConditionsForReceivingBankTo");
            if (((String)detailsAmend.get("documentType")).equals("FOREIGN") && !((String)detailsAmend.get("documentSubType1")).equals("STANDBY")) {
                String fromValue = (String)detailsAmend.get("specialPaymentConditionsForReceivingBankFrom");
                String toValue = (String)detailsAmend.get("specialPaymentConditionsForReceivingBankTo");
                lc.setSpecialPaymentConditionsForReceivingBank(getAmendedWithCodes(fromValue, toValue));
            } else {
                lc.setSpecialPaymentConditionsForReceivingBank((String)detailsAmend.get("specialPaymentConditionsForReceivingBankTo"));
            }
        }
        
        // Additional Conditions (2) Tab
        if (detailsAmend.get("periodForPresentation1Switch") != null && (!((String)detailsAmend.get("periodForPresentation1Switch")).equals("")) && (!((String)detailsAmend.get("periodForPresentation1Switch")).equals("off"))) {
            System.out.println("34 =================================== periodForPresentation1To");
            if (detailsAmend.get("periodForPresentation1NumberTo") != null) {
                lc.amendPeriodForPresentation((String)detailsAmend.get("periodForPresentation1To"), Integer.parseInt((String)detailsAmend.get("periodForPresentation1NumberTo")));
            } else {
            	lc.amendPeriodForPresentation((String)detailsAmend.get("periodForPresentation1To"));
            }
        }
        if (detailsAmend.get("reimbursingBankSwitch") != null && (!((String)detailsAmend.get("reimbursingBankSwitch")).equals(""))) {
            if (detailsAmend.get("reimbursingBankFlagTo") != null) {

                System.out.println("35 =================================== reimbursingBankFlagTo");

                lc.amendReimbursingBankDetails((String)detailsAmend.get("reimbursingBankFlagTo"), (String)detailsAmend.get("reimbursingBankIdentifierCodeTo"), (String)detailsAmend.get("reimbursingBankNameAndAddressTo"));

                if (detailsAmend.get("reimbursingAccountTypeTo") != null && (!((String)detailsAmend.get("reimbursingAccountTypeTo")).equals(""))) {
                    System.out.println("36 =================================== reimbursingBankFlagTo");
                    lc.amendReimbursingBankAccountType((String)detailsAmend.get("reimbursingAccountTypeTo"));
                }
                if (detailsAmend.get("reimbursingCurrencyTo") != null && (!((String)detailsAmend.get("reimbursingCurrencyTo")).equals(""))) {
                    System.out.println("37 =================================== reimbursingBankFlagTo");
                    lc.amendReimbursingCurrency(Currency.getInstance((String)detailsAmend.get("reimbursingCurrencyTo")));
                }
                if (detailsAmend.get("reimbursingBankAccountNumberTo") != null && (!((String)detailsAmend.get("reimbursingBankAccountNumberTo")).equals(""))) {
                    System.out.println("38 =================================== reimbursingBankFlagTo");
                    lc.amendReimbursingBankAccountNumber((String)detailsAmend.get("reimbursingBankAccountNumberTo"));
                }
            }
        }
        if (detailsAmend.get("periodForPresentationSwitch") != null && (!((String)detailsAmend.get("periodForPresentationSwitch")).equals("")) && (!((String)detailsAmend.get("periodForPresentationSwitch")).equals("off"))) {
                System.out.println("39 =================================== periodForPresentationTo");
                lc.amendPeriodForPresentationAdviseThroughBank((String) detailsAmend.get("periodForPresentationTo"));
        }
        if (detailsAmend.get("adviseThroughBankSwitch") != null && (!((String)detailsAmend.get("adviseThroughBankSwitch")).equals("")) && (!((String)detailsAmend.get("adviseThroughBankSwitch")).equals("off"))) {
                System.out.println("40 =================================== adviseThroughBankFlagTo");
                lc.amendAdviseThroughBankDetails((String)detailsAmend.get("adviseThroughBankFlagTo"), (String)detailsAmend.get("adviseThroughBankIdentifierCodeTo"), (String)detailsAmend.get("adviseThroughBankLocationTo"), (String)detailsAmend.get("adviseThroughBankNameAndAddressTo"));
        }
        if (detailsAmend.get("senderToReceiverSwitch") != null && (!((String)detailsAmend.get("senderToReceiverSwitch")).equals("")) && (!((String)detailsAmend.get("senderToReceiverSwitch")).equals("off"))) {
                System.out.println("41 =================================== senderToReceiverTo");
                lc.amendSenderToReceiverInformation((String)detailsAmend.get("senderToReceiverTo"), (String)detailsAmend.get("senderToReceiverInformationTo"));
        }

        // Details of Guarantee tab (DM)
        if (detailsAmend.get("detailsOfGuaranteeSwitch") != null && (!((String)detailsAmend.get("detailsOfGuaranteeSwitch")).equals("")) && (!((String)detailsAmend.get("detailsOfGuaranteeSwitch")).equals("off"))) {
                System.out.println("42 =================================== detailsOfGuaranteeTo");
                lc.amendDetailsOfGuarantee((String)detailsAmend.get("detailsOfGuaranteeTo"));
        }
        
        // Additional Details tab (DM)
        if (detailsAmend.get("partialDeliverySwitch") != null && (!((String)detailsAmend.get("partialDeliverySwitch")).equals("")) && (!((String)detailsAmend.get("partialDeliverySwitch")).equals("off"))) {
            System.out.println("43 =================================== partialDeliveryTo");
            lc.amendPartialDelivery((String)detailsAmend.get("partialDeliveryTo"));
        }
        if (detailsAmend.get("beneficiaryNameSwitch") != null && (!((String)detailsAmend.get("beneficiaryNameSwitch")).equals("")) && (!((String)detailsAmend.get("beneficiaryNameSwitch")).equals("off"))) {
        	System.out.println("44 =================================== beneficiaryNameTo");
        	lc.amendBeneficiaryName((String)detailsAmend.get("beneficiaryNameTo"));
        }
        if (detailsAmend.get("beneficiaryAddressSwitch") != null && (!((String)detailsAmend.get("beneficiaryAddressSwitch")).equals("")) && (!((String)detailsAmend.get("beneficiaryAddressSwitch")).equals("off"))) {
        	System.out.println("45 =================================== beneficiaryAddressTo");
        	lc.amendBeneficiaryAddress((String)detailsAmend.get("beneficiaryAddressTo"));
        }
        if (detailsAmend.get("narrative") != null && (!((String)detailsAmend.get("narrative")).equals(""))) {
        	System.out.println("46 =================================== narrative");
        	lc.updateNarrative((String)detailsAmend.get("narrative"));
        }
        if (detailsAmend.get("narrativeFor747") != null && (!((String)detailsAmend.get("narrativeFor747")).equals(""))) {
        	System.out.println("46 =================================== narrativeFor747");
        	lc.updateNarrativeFor747((String)detailsAmend.get("narrativeFor747"));
        }
        // added by henry alabin
        if (detailsAmend.get("applicantNameSwitch") != null && (!((String)detailsAmend.get("applicantNameSwitch")).equals("")) && (!((String)detailsAmend.get("beneficiaryNameSwitch")).equals("off"))) {
        	System.out.println("47 =================================== applicantNameTo");
        	lc.amendApplicantName((String)detailsAmend.get("applicantNameTo"));
        }
        if (detailsAmend.get("applicantAddressSwitch") != null && (!((String)detailsAmend.get("applicantAddressSwitch")).equals("")) && (!((String)detailsAmend.get("beneficiaryAddressSwitch")).equals("off"))) {
        	System.out.println("48 =================================== applicantAddressTo");
        	lc.amendApplicantAddress((String)detailsAmend.get("applicantAddressTo"));
        }

        // added by Jaivee Hipolito
        if (detailsAmend.get("commodityCodeSwitch") != null && (!((String)detailsAmend.get("commodityCodeSwitch")).equals("")) && (!((String)detailsAmend.get("commodityCodeSwitch")).equals("off"))) {
            System.out.println("24 =================================== partialShipmentTo");
            lc.setCommodityCode((String)detailsAmend.get("commodityCodeTo"));
        }

        lc.increaseAmendmentCount();

        return lc;
    }
}
