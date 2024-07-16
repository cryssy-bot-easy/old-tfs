package com.ucpb.tfs2.application.util

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:55 PM
 */
class ExportsAdvisingChargesCalculator extends ChargesCalculator {

	/*	PROLOGUE:
		(revision)
		SCR/ER Number: 20151104-018
		SCR/ER Description: No cable charge generated in Export Advising  when the MT730 radio button was not checked.
		[Revised by:] MJ Lonzame
		[Date revised:] 06/22/2015
		Program [Revision] Details: The generated cableFee charge will have a value of 500 regardless whether the MT730 radio button
									is checked or not.
		Date deployment:
		Member Type: GROOVY
		Project: CORE
		Project Name: ExportsAdvisingChargesCalculator.groovy
	 */
	
    public Map getFirstAdvisingCharge(Map productDetails) {
		
		String serviceType = productDetails.extendedProperties?.get("serviceType");

        // In Php
        BigDecimal exportsAdvisingFee = new BigDecimal(1000)
		
		//@ carlo march 3 2015>>> 
		if(serviceType!=null){
			if(serviceType.equalsIgnoreCase("AMENDMENT_ADVISING")){
				exportsAdvisingFee	= new BigDecimal(500)
			}
		}
		//@ carlo march 3 2015<<<<<

        BigDecimal cableFee = BigDecimal.ZERO

        String cwtFlag = (String)ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cwtFlag", "String")
        BigDecimal cwtPercentage = (BigDecimal)ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cwtPercentage", "BigDecimal") ?: 0.98

        String sendMt730Flag = (String)ChargesCalculator.getExtendedPropertiesVariable(productDetails, "sendMt730Flag", "String")
        String sendMt799Flag = (String)ChargesCalculator.getExtendedPropertiesVariable(productDetails, "sendMt799Flag", "String")
//
//        if ((sendMt730Flag != null && sendMt730Flag.equals("1")) ||  // commented by mj lonzame
//            (sendMt799Flag != null && sendMt799Flag.equals("1"))) {
            cableFee = new BigDecimal(500)
//        }

        BigDecimal exportsAdvisingFeeoriginal = exportsAdvisingFee
        BigDecimal exportsAdvisingFeenocwtAmount = exportsAdvisingFee
        if ("1".equalsIgnoreCase(cwtFlag)||"Y".equalsIgnoreCase(cwtFlag)) {
            exportsAdvisingFee = cwtPercentage.multiply(exportsAdvisingFee)
        }

        return [
            'ADVISING-EXPORT' : exportsAdvisingFee.setScale(2, BigDecimal.ROUND_UP),
            'ADVISING-EXPORToriginal' : exportsAdvisingFeeoriginal.setScale(2, BigDecimal.ROUND_UP),
            'ADVISING-EXPORTnocwtAmount' : exportsAdvisingFeenocwtAmount.setScale(2, BigDecimal.ROUND_UP),
            CABLE : cableFee.setScale(2, BigDecimal.ROUND_UP),
            CABLEoriginal : cableFee.setScale(2, BigDecimal.ROUND_UP)
        ]
    }

    public Map getSecondAdvisingCharge(Map productDetails) {
		
		String serviceType = productDetails.extendedProperties?.get("serviceType");

        // In Php
        BigDecimal exportsAdvisingFee = new BigDecimal(1000)

        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
		
		//@ carlo march 2 2015>>>>>>
		if(serviceType!=null){
			if(serviceType.equalsIgnoreCase("AMENDMENT_ADVISING")){
				exportsAdvisingFee	= new BigDecimal(500)
			}
		}
		//<<<<<<<<<<<<<<<

        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        BigDecimal otherAdvisingFee = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("totalBankCharges"), "BigDecimal") ?: BigDecimal.ZERO

        BigDecimal exportsAdvisingFeeoriginal = exportsAdvisingFee
        BigDecimal exportsAdvisingFeenocwtAmount = exportsAdvisingFee

        if ("1".equalsIgnoreCase(cwtFlag)||"Y".equalsIgnoreCase(cwtFlag)) {
            exportsAdvisingFee = cwtPercentage.multiply(exportsAdvisingFee)
        }

        return [
            'ADVISING-EXPORT' : exportsAdvisingFee.setScale(2, BigDecimal.ROUND_UP),
            'ADVISING-EXPORToriginal' : exportsAdvisingFeeoriginal.setScale(2, BigDecimal.ROUND_UP),
            'ADVISING-EXPORTnocwtAmount' : exportsAdvisingFeenocwtAmount.setScale(2, BigDecimal.ROUND_UP),
            'OTHER-EXPORT' : otherAdvisingFee.setScale(2, BigDecimal.ROUND_UP),
            'OTHER-EXPORToriginal' : otherAdvisingFee.setScale(2, BigDecimal.ROUND_UP)
        ]
    }
}
