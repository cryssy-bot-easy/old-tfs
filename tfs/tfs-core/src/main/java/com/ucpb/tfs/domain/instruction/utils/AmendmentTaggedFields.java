
package com.ucpb.tfs.domain.instruction.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 10/4/12
 */
public class AmendmentTaggedFields {

    private static Map<String, String> lcTaggedFields = new HashMap<String, String>();
    
    static {

        lcTaggedFields.put("amountSwitch", "amountSwitch");
        lcTaggedFields.put("expiryCountryCodeSwitch", "expiryCountryCodeSwitch");
        lcTaggedFields.put("destinationBankSwitch", "destinationBankSwitch");
        lcTaggedFields.put("tenorSwitch", "tenorSwitch");
        lcTaggedFields.put("applicableRulesSwitch", "applicableRulesSwitch");
        lcTaggedFields.put("confirmationInstructionsFlagSwitch", "confirmationInstructionsFlagSwitch");
        lcTaggedFields.put("expiryDateSwitch", "expiryDateSwitch");
        lcTaggedFields.put("formOfDocumentaryCreditSwitch", "formOfDocumentaryCreditSwitch");

        lcTaggedFields.put("importerNameSwitch", "importerNameSwitch");
        lcTaggedFields.put("importerAddressSwitch", "importerAddressSwitch");
        lcTaggedFields.put("exporterCbCodeSwitch", "exporterCbCodeSwitch");
        lcTaggedFields.put("exporterNameSwitch", "exporterNameSwitch");
        lcTaggedFields.put("exporterAddressSwitch", "exporterAddressSwitch");
        lcTaggedFields.put("positiveToleranceLimitSwitchDisplay", "positiveToleranceLimitSwitchDisplay");
        lcTaggedFields.put("negativeToleranceLimitSwitchDisplay", "negativeToleranceLimitSwitchDisplay");
        lcTaggedFields.put("maximumCreditAmountSwitchDisplay", "maximumCreditAmountSwitchDisplay");
        lcTaggedFields.put("additionalAmountsCoveredSwitch", "additionalAmountsCoveredSwitch");
        lcTaggedFields.put("availableWithSwitch", "availableWithSwitch");
        lcTaggedFields.put("availableBySwitch", "availableBySwitch");
        lcTaggedFields.put("partialShipmentSwitch", "partialShipmentSwitch");
        lcTaggedFields.put("transShipmentSwitch", "transShipmentSwitch");
        lcTaggedFields.put("placeOfTakingDispatchOrReceiptSwitch", "placeOfTakingDispatchOrReceiptSwitch");
        lcTaggedFields.put("portOfLoadingOrDepartureSwitch", "portOfLoadingOrDepartureSwitch");
        lcTaggedFields.put("portOfDischargeOrDestinationSwitch", "portOfDischargeOrDestinationSwitch");
        lcTaggedFields.put("placeOfFinalDestinationSwitch", "placeOfFinalDestinationSwitch");

        lcTaggedFields.put("latestShipmentDateSwitch", "latestShipmentDateSwitch");
        lcTaggedFields.put("shipmentPeriodSwitch", "shipmentPeriodSwitch");
        lcTaggedFields.put("generalDescriptionOfGoodsSwitch", "generalDescriptionOfGoodsSwitch");

        lcTaggedFields.put("periodForPresentation1Switch", "periodForPresentation1Switch");
        lcTaggedFields.put("periodForPresentationSwitch", "periodForPresentationSwitch");
        lcTaggedFields.put("reimbursingBankSwitch", "reimbursingBankSwitch");
        lcTaggedFields.put("adviseThroughBankSwitch", "adviseThroughBankSwitch");
        lcTaggedFields.put("senderToReceiverSwitch", "senderToReceiverSwitch");

        lcTaggedFields.put("detailsOfGuaranteeSwitch", "detailsOfGuaranteeSwitch");
    }
    
    public static Map<String, Object> cleanse(Map<String, Object> parameterMap, Map<String, Object> details) {

        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            if (!lcTaggedFields.containsKey(pairs.getKey())) {
                details.remove(pairs.getKey());
            }
        }

        return details;
    }
}
