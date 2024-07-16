package com.ucpb.tfs2.application.util

import java.math.RoundingMode

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:55 PM
 */

/**
 PROLOGUE:
 (revision)
 SCR/ER Number:
 SCR/ER Description: Wrong computation and no accounting entry was generated for Doc Stamp fee.
 [Revised by:] Lymuel Arrome Saul
 [Date revised:] 2/5/2016
 Program [Revision] Details: Corrected the computation of documentary stamps
 Date deployment: 2/9/2016
 Member Type: GROOVY
 Project: CORE
 Project Name: ExportBillsCollectionChargesCalculator.groovy
 */

 /**
 PROLOGUE:
 (revision)
 SCR/ER Number:
 SCR/ER Description: TO correct computation of charges when EBC have EBP
 [Revised by:] Jesse James Joson
 Program [Revision] Details: Set the base amount to the proceeds amount for BankCom and Cilex, EBCamount less EBPamount for the base of Doc Stamp.
 Date deployment: 6/16/2017
 Member Type: GROOVY
 Project: CORE
 Project Name: ExportBillsCollectionChargesCalculator.groovy
 */
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Changes hard-coded parameters of computing
 *      document stamps into data retrieved from database.
 *  Date revised: 02/01/2018
*/
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Commented additional computation of docStamps on computeSettlement if the settlement currency is PHP.
 *  Date revised: 04/13/2018
 */
 
 
class ExportBillsCollectionChargesCalculator extends ChargesCalculator {

	public Map computeSettlement(Map productDetails) {

		// precompute for the base variables
		precomputeBaseFXLC(productDetails);
		Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
		
		// JJ get settlementToBene currency
		String settlementToBeneCurrency = ""
		if (productDetails.containsKey("newProceedsCurrency") && productDetails.get("newProceedsCurrency") != "") {
			settlementToBeneCurrency = extendedProperties.get("newProceedsCurrency")
			println "settlementToBeneCurrency: " + settlementToBeneCurrency
		}
		//end
		
		//parameters
		BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal") ?: 1
		BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal") ?: 4
		BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal") ?: 0.01

		BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexNumerator"), "BigDecimal") ?: 1
		BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexDenominator"), "BigDecimal") ?: 4
		BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexPercentage"), "BigDecimal") ?: 0.01

		//        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
		//        String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
		//        String issueDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("issueDate"), "String")
		String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
		BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

		String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
		String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

		String advanceCorresChargesFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("advanceCorresChargesFlag"), "String")
		println "advanceCorresChargesFlag advanceCorresChargesFlag advanceCorresChargesFlag :" + advanceCorresChargesFlag
		String confirmationInstructionsFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmationInstructionsFlag"), "String")
		println "confirmationInstructionsFlag confirmationInstructionsFlag confirmationInstructionsFlag :" + confirmationInstructionsFlag

		BigDecimal centavos = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("centavos"), "BigDecimal") ?: 0.3

		println "documentSubType1:" + documentSubType1
		println "documentSubType2:" + documentSubType2

		String remittanceFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("remittanceFlag"), "String")


		Calculators calculators = new Calculators()


		BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")
		BigDecimal baseUSD = (BigDecimal) getBaseVariable("chargesBaseUSD")
		BigDecimal basePHPBuying = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
		BigDecimal docstampBasePHP = (BigDecimal) getBaseVariable("ebcDocstampBasePHP")
		BigDecimal UrrPhp = (BigDecimal) getBaseVariable("ebcDocstampBaseUrrPHP") // edited by kyle and pauline 
		BigDecimal EBCNegoRegSellPhp = (BigDecimal) getBaseVariable("ebcDocstampBaseSellRatePHP")
		println "hi regsell: " + EBCNegoRegSellPhp
		println "hi URR php: " + UrrPhp

		println "BASE USD" + baseUSD
		println "URR BASE PHP" + basePHP
		println "WITHDRAWAL BASE PHP" + basePHPBuying
		String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency").toString()
		String productCurrency = productDetails.get("productCurrency").toString()

		if(productCurrency.equalsIgnoreCase("USD") && chargeSettlementCurrency.equalsIgnoreCase("USD")){
			basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")
			docstampBasePHP = (BigDecimal) getBaseVariable("ebcDocstampBaseUrrPHP")
		} else if(productCurrency.equalsIgnoreCase("USD") && chargeSettlementCurrency.equalsIgnoreCase("PHP")){
			basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
			docstampBasePHP = (BigDecimal) getBaseVariable("ebcDocstampBaseUrrPHP")//(BigDecimal) getBaseVariable("ebcDocstampBaseSellRatePHP")
		} else if (!productCurrency.equalsIgnoreCase("USD") && !productCurrency.equalsIgnoreCase("PHP")){
			if(chargeSettlementCurrency.equalsIgnoreCase("USD")){
				docstampBasePHP = (BigDecimal) getBaseVariable("ebcDocstampBaseUrrPHP")
			} else if (chargeSettlementCurrency.equalsIgnoreCase("PHP")){
				docstampBasePHP = (BigDecimal) getBaseVariable("ebcDocstampBaseSellRatePHP")
			}
		}
		BigDecimal daysCommitmentFee

		// parameterized factors
		BigDecimal bankCommissionFactor = 0
		bankCommissionFactor.setScale(12)
		bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_FLOOR)
		println "bankCommissionFactor:" + bankCommissionFactor

		BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
		println "cilexFactor:" + cilexFactor
		BigDecimal postageDefault = 400
		BigDecimal remittanceDefault = 18


		BigDecimal postage = postageDefault
		BigDecimal remittance = remittanceDefault
		BigDecimal settledInForeignInUSD = (BigDecimal) getBaseVariable("settledInForeignInUSD")
		println "settledInForeignInUSD:" + settledInForeignInUSD
		BigDecimal totalNotSettledByTRinPHP = (BigDecimal) getBaseVariable("totalNotSettledByTRinPHP")
		println "zxc totalNotSettledByTRinPHP: " + totalNotSettledByTRinPHP
		BigDecimal productSettlementPHPTotals = (BigDecimal) getBaseVariable("productSettlementPHPTotals")
		BigDecimal productSettlementUSDTotals = (BigDecimal) getBaseVariable("productSettlementUSDTotals")
		BigDecimal productSettlementThirdTotals = (BigDecimal) getBaseVariable("productSettlementThirdTotals")
		println "productSettlementPHPTotals:" + productSettlementPHPTotals

		// charges
		println "zxc baseUSD: " + baseUSD
		println "zxc docstampBasePHP: " + docstampBasePHP
		println "zxc settledInForeignInUSD: " + settledInForeignInUSD
		BigDecimal bankCommission = calculators.percentageOf(settledInForeignInUSD, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP)
		BigDecimal settledInForeignInUSDConvertedToURR
		//BigDecimal bankCommissionPHP = currencyConverter.convert("URR", "USD", bankCommission, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
		BigDecimal bankCommissionPHP = calculators.percentageOf(totalNotSettledByTRinPHP, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP)
		println "zxc bankCommissionPHP before minimum conversion: " + bankCommissionPHP
		if (bankCommissionPHP < 1000) {
			bankCommissionPHP = new BigDecimal("1000")
		}
		println "zxc bankCommissionPHP after minimum conversion: " + bankCommissionPHP
		BigDecimal cilex = BigDecimal.ZERO
		BigDecimal cilexUSD = calculators.percentageOf(settledInForeignInUSD, cilexFactor) //Use REG-SELL
		BigDecimal temp = BigDecimal.ZERO
		BigDecimal minimumCilex = 20
		if (productSettlementPHPTotals > 0) {
			//This means that there was a PHP settlement
			temp = currencyConverter.convert("REG-SELL", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
			minimumCilex = currencyConverter.convert("REG-SELL", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
			println "temp:" + temp
			cilex = calculators.percentageOf(temp, cilexFactor) //Use REG-SELL

		} else if (productSettlementUSDTotals > 0 && productSettlementThirdTotals > 0) {
			//This means that there was a PHP settlement
			temp = currencyConverter.convert("REG-SELL", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
			minimumCilex = currencyConverter.convert("REG-SELL", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
			println "temp:" + temp
			cilex = calculators.percentageOf(temp, cilexFactor) //Use REG-SELL
		} else {
			temp = currencyConverter.convert("REG-SELL", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
			//            temp = currencyConverter.convert("URR", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
			//            minimumCilex = currencyConverter.convert("URR", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
			minimumCilex = currencyConverter.convert("REG-SELL", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
			println "temp:" + temp
			cilex = calculators.percentageOf(temp, cilexFactor)
		}

		println "minimumCilex:" + minimumCilex
		println "cilex:" + cilex

		if (settledInForeignInUSD > 0) {//minimum cilex must only be checked if there is a settlement in foreign currency otherwise use zero
			if (cilex < minimumCilex) {
				//                cilex = minimumCilex
				cilexUSD = 20 
			}
		}



		println "cilex:" + cilex
		println "cilexUSD:" + cilexUSD

        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
		//edit by kyle and pauline for EBC Docstamps
		//BigDecimal docStamps = docstampBasePHP.divide(200).multiply(0.30)
		//BigDecimal docStamps = calculators.forEvery(UrrPhp, 200, centavos?:0.30).setScale(2, BigDecimal.ROUND_HALF_UP)
		BigDecimal docStamps = calculators.forEvery(UrrPhp, baseAmount, centavos ?: rateAmount).setScale(2, BigDecimal.ROUND_HALF_UP)
		//edit end
		println "zxc docStamps: " + docStamps



		println "CHARGES ORIGINAL VALUE"
		println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
		println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
		println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
		println "charges postage:" + postage.setScale(2, BigDecimal.ROUND_UP)
		println "charges remittance:" + remittance.setScale(2, BigDecimal.ROUND_UP)

		def bankCommissionOrig=bankCommission.setScale(2, BigDecimal.ROUND_UP)
		def cilexOrig=currencyConverter.convert("REG-SELL", "USD", cilexUSD, "PHP").setScale(2, BigDecimal.ROUND_UP)
		def docStampsOrig=docStamps.setScale(2, BigDecimal.ROUND_HALF_UP)
		def postageOrig=postage.setScale(2, BigDecimal.ROUND_HALF_UP)
		def remittanceOrig=remittance.setScale(2, BigDecimal.ROUND_UP)

		
		BigDecimal bankComGross = bankCommission
		BigDecimal bankComCwt = 0
		BigDecimal cilexGross = cilexUSD
		BigDecimal cilexCwt = 0

		if (("Y".equalsIgnoreCase(cwtFlag)||"1".equalsIgnoreCase(cwtFlag))  && chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
			bankCommission = cwtPercentage.multiply(bankCommission)
			cilexUSD = cwtPercentage.multiply(cilexUSD)
		}

		
		if (settlementToBeneCurrency.equalsIgnoreCase("PHP")) {
			cilex = 0
			cilexUSD = 0
		}

		println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
		String ratesBasis = "URR"
		if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
			ratesBasis = "URR"
			println "no conversion required except for cilex and remittance"
			println "before conversion advising" + remittance


			if (productSettlementPHPTotals > 0) {
				ratesBasis = "REG-SELL"
			} else {
				ratesBasis = "URR"
			}
			//docStamps = calculators.forEvery(EBCNegoRegSellPhp, 200, centavos?:0.30).setScale(2, BigDecimal.ROUND_HALF_UP)
			//docStamps = calculators.forEvery(EBCNegoRegSellPhp, baseAmount, centavos ?: rateAmount).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "hi docStamps: " + docStamps
			remittance= currencyConverter.convert("REG-SELL", "USD", remittance, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
			cilex= currencyConverter.convert("REG-SELL", "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)

			//comment by robs 8/16/2016. fixed BC for EBC Settlement
			println "if settlement php(before computing) BC: " + bankCommission + " baseSellRatePHP: " + basePHPBuying
			bankCommission = calculators.percentageOf(basePHPBuying, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "zxc BC after: " + bankCommission
			println "before minimum condition: " + bankCommission
			if (bankCommission < 1000) {
				bankCommission = new BigDecimal("1000")
			}
			bankComGross = bankCommission
			cilexGross = currencyConverter.convert("REG-SELL", "USD", cilexGross, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)

			if (("Y".equalsIgnoreCase(cwtFlag)||"1".equalsIgnoreCase(cwtFlag))) {
				bankCommission = cwtPercentage.multiply(bankCommission)
				println "darwin bank commission cwtFlag:"+ bankCommission
			}
			println "bc FINAL PHP: " + bankCommission
			//commend end

			bankCommissionOrig=bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP)
			cilexOrig=cilex.setScale(2, BigDecimal.ROUND_HALF_UP)
			ratesBasis = "URR"
			println "after conversion remittance:" + remittance

		} else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
			ratesBasis = "URR"
			docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
			//            docStamps = currencyConverter.convert("REG-SELL", "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
			postage = currencyConverter.convert(ratesBasis, "PHP", postage, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)//this must be fixed at 400.00
			//            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
			//            cilex = currencyConverter.convert(ratesBasis, "PHP", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			cilex = cilexUSD

			bankCommissionPHP = calculators.percentageOf(basePHP, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP);		
			if (bankCommissionPHP < 1000) {
				bankCommissionPHP = new BigDecimal("1000")
			}
			bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommissionPHP, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)//edited by max 7/1/2016
			println "zxc bankCommission: " + bankCommission
			bankComGross = bankCommission
			cilexGross = cilex
			bankCommissionOrig= currencyConverter.convert("URR", "USD", baseUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			cilexOrig= currencyConverter.convert("URR", "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			
			if ("Y".equalsIgnoreCase(cwtFlag)||"1".equalsIgnoreCase(cwtFlag)) {
				bankCommission = cwtPercentage.multiply(bankCommission)
				cilex = cwtPercentage.multiply(cilex)
			}
			
		} else {
			ratesBasis = "URR"

			postage = currencyConverter.convertWithPrecision(ratesBasis, "PHP", postage, "USD", 12).setScale(2, BigDecimal.ROUND_HALF_UP) //added by max
			docStamps = currencyConverter.convertWithPrecision(ratesBasis, "PHP", docStamps, "USD", 12).setScale(2, BigDecimal.ROUND_FLOOR) //added by max

			//            bankCommission = currencyConverter.convertWithPrecision(ratesBasis, "PHP", bankCommission, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
			//            cilex = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cilex, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
			//       cilexOrig= currencyConverter.convert("URR", "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			//        bankCommissionOrig= currencyConverter.convert("URR", "USD", baseUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)



			println "AFTER URR VALUE"
			println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP)
			println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_HALF_UP)
			println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_HALF_UP)
			println "charges confirming fee:" + postage.setScale(2, BigDecimal.ROUND_HALF_UP)

			ratesBasis = "REG-SELL"

			//            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			//          bankCommission = currencyConverter.convert(ratesBasis, "USD", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
			//bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommissionPHP, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP) // revise by max
			println "BCPHP: " + bankCommissionPHP
			bankCommission = currencyConverter.convert("URR", "PHP", bankCommissionPHP, "USD").setScale(2, BigDecimal.ROUND_HALF_UP)
			println "BC from php to USD: " + bankCommission
			bankCommission = currencyConverter.convert("REG-SELL", "USD", bankCommission, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "BC converted back to " + chargeSettlementCurrency + ". BC is now: " + bankCommission // comment by robs

			cilex = currencyConverter.convert(ratesBasis, "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			docStamps = currencyConverter.convert(ratesBasis, "USD", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
			postage = currencyConverter.convert(ratesBasis, "USD", postage, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			remittance = currencyConverter.convert(ratesBasis, "USD", remittance, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			bankComGross = bankCommission
			cilexGross = cilex
		}

		if(!"Y".equalsIgnoreCase(remittanceFlag)){
			remittance = BigDecimal.ZERO
			remittanceOrig = BigDecimal.ZERO
		}
		
		if ("Y".equalsIgnoreCase(cwtFlag)||"1".equalsIgnoreCase(cwtFlag)) {
			bankComCwt = bankComGross.subtract(bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP))
			cilexCwt = cilexGross.subtract(cilex.setScale(2, BigDecimal.ROUND_UP))
		}


		println "charges bank commission:1" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
		println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
		println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
		println "charges postage:" + postage.setScale(2, BigDecimal.ROUND_UP)
		println "charges remittance:" + remittance.setScale(2, BigDecimal.ROUND_UP)

		BigDecimal total = bankCommission.setScale(2, BigDecimal.ROUND_UP)
		total = total + docStamps.setScale(2, BigDecimal.ROUND_UP)
		total = total + cilex.setScale(2, BigDecimal.ROUND_UP)
		total = total + postage.setScale(2, BigDecimal.ROUND_UP)
		total = total + remittance.setScale(2, BigDecimal.ROUND_UP)


		return [
			BC: bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP),
			DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
			CILEX: cilex.setScale(2, BigDecimal.ROUND_UP),
			POSTAGE: postage.setScale(2, BigDecimal.ROUND_UP),
			REMITTANCE: remittance.setScale(2, BigDecimal.ROUND_UP),
			TOTAL: total,
			BCoriginal: bankCommissionOrig.setScale(2, BigDecimal.ROUND_UP),
			DOCSTAMPSoriginal: docStampsOrig.setScale(2, BigDecimal.ROUND_HALF_UP),
			CILEXoriginal: cilexOrig.setScale(2, BigDecimal.ROUND_UP),
			POSTAGEoriginal: postageOrig.setScale(2, BigDecimal.ROUND_UP),
			REMITTANCEoriginal: remittanceOrig.setScale(2, BigDecimal.ROUND_UP),
			bankComGross: bankComGross.setScale(2, BigDecimal.ROUND_HALF_UP),
			bankComCwt: bankComCwt.setScale(2, BigDecimal.ROUND_HALF_UP),
			cilexGross: cilexGross.setScale(2, BigDecimal.ROUND_UP),
			cilexCwt: cilexCwt.setScale(2, BigDecimal.ROUND_HALF_UP)
		]
	}

	public Map computeCancellation(Map productDetails) {

		// precompute for the base variables
		//precomputeBaseFXLC(productDetails);
		Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
		//parameters
		BigDecimal cableFeeDefault = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cableFeeDefault"), "BigDecimal") ?: 500
		BigDecimal courierFeeDefault = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("courierFeeDefault"), "BigDecimal") ?: 400

		//parameters
		def cableFee = cableFeeDefault
		def courierFee = courierFeeDefault


		String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
		println "CHARGES ORIGINAL VALUE"
		println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)
		println "charges courier fee:" + courierFee.setScale(2, BigDecimal.ROUND_HALF_UP)

		println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
		String ratesBasis = "URR"
		if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
			ratesBasis = "URR"


		} else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
			ratesBasis = "URR"
			cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			courierFee = currencyConverter.convert(ratesBasis, "PHP", courierFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
		} else {
			ratesBasis = "URR"

			cableFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cableFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
			courierFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", courierFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)

			println "AFTER URR VALUE"
			println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)
			println "charges cable fee:" + courierFee.setScale(2, BigDecimal.ROUND_HALF_UP)

			ratesBasis = "REG-SELL"
			cableFee = currencyConverter.convert(ratesBasis, "USD", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			courierFee = currencyConverter.convert(ratesBasis, "USD", courierFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)

		}



		println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)
		println "charges courier fee:" + courierFee.setScale(2, BigDecimal.ROUND_HALF_UP)

		BigDecimal total = cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)
		total = total + courierFee.setScale(2, BigDecimal.ROUND_HALF_UP)


		return [
			COURIER: courierFee.setScale(2, BigDecimal.ROUND_HALF_UP),
			CABLE: cableFee.setScale(2, BigDecimal.ROUND_HALF_UP),
			TOTAL: total
		]
	}
}
