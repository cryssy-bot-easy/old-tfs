package com.ucpb.tfs.application.service;

import java.math.BigDecimal;
import java.util.Map;

import com.ucpb.tfs.domain.reference.ChargeId;

public class ServiceChargeService {
	public static BigDecimal getTotalServiceCharges(Map<String, Object> parameterMap){
		BigDecimal totalServiceCharge = BigDecimal.ZERO;
		for (String key : parameterMap.keySet()) {
            Object keyValue = parameterMap.get(key);
            if (keyValue != null && !keyValue.equals("")) {

                ChargeId chargeId = null;
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
                	System.out.println("chargeId: " + chargeId.toString());
                	System.out.println("chargeId value: " + keyValue.toString());
                	totalServiceCharge = totalServiceCharge.add(new BigDecimal((String) keyValue));
                }
            }
		}
		System.out.println("totalServiceCharge: " + totalServiceCharge.toString());
		return totalServiceCharge;
	}
}
