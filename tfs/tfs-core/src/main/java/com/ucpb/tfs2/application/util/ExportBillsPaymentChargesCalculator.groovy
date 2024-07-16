package com.ucpb.tfs2.application.util

import java.math.RoundingMode

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:55 PM
 */

/*
(revision)
SCR/ER Number:
SCR/ER Description: Incorrect Charges Computation (Redmine# 3701, 3765)
[Revised by:] Robin C. Rafael
[Date deployed:] 6/16/2017
Program [Revision] Details: 
	-CilexUSD minimum 20
	-declared baseUrrPhp and baseSellRatePhp
	-docStamps php computation
	-computations on all charges if PHP, if USD, and if thirds
Member Type: Groovy 
Project: Core
Project Name: ExportBillsPaymentChargesCalculator.groovy
*/

/*
(revision)
SCR/ER Number:
SCR/ER Description: to correct the charges computation of EBP transactions
[Revised by:] Jesse James Joson
[Date deployed:] 6/16/2017
Program [Revision] Details: base the rates on the settlement to beneficiary currency instead of the Charges currency
Member Type: Groovy 
Project: Core
Project Name: ExportBillsPaymentChargesCalculator.groovy
*/
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Changes hard-coded parameters of computing
 *      document stamps into data retrieved from database.
 *  Date revised: 02/01/2018
*/
class ExportBillsPaymentChargesCalculator extends ChargesCalculator {

    public Map computeNegotiation(Map productDetails) {
		println ("JJ - productDetails" + productDetails)
        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
		
		// JJ get settlementToBene currency
		String settlementToBeneCurrency = ""
		if (extendedProperties.get("settlementToBeneCurrency") != "") {
			settlementToBeneCurrency = extendedProperties.get("settlementToBeneCurrency")
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

        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        BigDecimal centavos = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("centavos"), "BigDecimal") ?: 0.3

        println "documentSubType1:" + documentSubType1
        println "documentSubType2:" + documentSubType2

        String remittanceFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "remittanceFlag", "String") ?: "N"
        println "remittanceFlag: " + remittanceFlag

        Calculators calculators = new Calculators()


        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")
        BigDecimal baseUSD = (BigDecimal) getBaseVariable("chargesBaseUSD")
		//asd
		//BigDecimal thirdsCurrencyBase = (BigDecimal) getBaseVariable("chargesBaseThirds")
		BigDecimal baseUrrPhp = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
		BigDecimal baseSellRatePhp = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
		
		println "ZXC baseURRPHP: " + baseUrrPhp
		println "ASD baseSellRatePhp: " + baseSellRatePhp
		//asd end
        BigDecimal daysCommitmentFee

        basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
        BigDecimal basePHPBuying = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
		println "basephpbuying: " + basePHPBuying
        println "BASE USD" + baseUSD
        println "URR BASE PHP" + basePHP
        println "WITHDRAWAL BASE PHP" + basePHP
        println "basePHP:" + basePHP


        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency").toString()
        String productCurrency = productDetails.get("productCurrency").toString()
		println "ASD productCurrency: " + productCurrency
		println " ASD chargeSettlementCurrency : " + chargeSettlementCurrency

        if(productCurrency.equalsIgnoreCase("USD") && chargeSettlementCurrency.equalsIgnoreCase("USD")){
            basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")
        } else if(productCurrency.equalsIgnoreCase("USD") && chargeSettlementCurrency.equalsIgnoreCase("PHP")){
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        }



        // parameterized factors
        BigDecimal bankCommissionFactor = 0
        bankCommissionFactor.setScale(12)
		println "ASD Bankcom numerator: " + bankCommissionNumerator + " bankcomdenominator: " + bankCommissionDenominator
        bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "bankCommissionFactor:" + bankCommissionFactor

        BigDecimal cilexFactor = 0
        cilexFactor.setScale(12)
        cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "cilexFactor:" + cilexFactor
        BigDecimal postageDefault = 400
        BigDecimal remittanceDefault = 18

        // charges
//        BigDecimal bankCommission = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, bankCommissionFactor, 1000)
        BigDecimal bankCommission = calculators.percentageOf(baseUSD, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP)
        BigDecimal bankCommissionPHP = calculators.percentageOf(baseUrrPhp, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP)
		println "ASD before minimum condition: " + bankCommissionPHP
        if (bankCommissionPHP < 1000) {
            bankCommissionPHP = new BigDecimal("1000")
        }



        BigDecimal postage = postageDefault
        BigDecimal remittance = remittanceDefault
        BigDecimal settledInForeignInUSD = (BigDecimal) getBaseVariable("settledInForeignInUSD")
        println "settledInForeignInUSD:" + settledInForeignInUSD
        BigDecimal productSettlementPHPTotals = (BigDecimal) getBaseVariable("productSettlementPHPTotals")
        BigDecimal productSettlementUSDTotals = (BigDecimal) getBaseVariable("productSettlementUSDTotals")
        BigDecimal productSettlementThirdTotals = (BigDecimal) getBaseVariable("productSettlementThirdTotals")
        println "productSettlementPHPTotals:" + productSettlementPHPTotals
        //always settled in USD because it is an EBP Loan
        BigDecimal cilex = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, cilexFactor, 0)
        // BigDecimal cilexUSD = calculators.percentageOf(settledInForeignInUSD, cilexFactor) //Use REG-SELL
		println "baseUSD?? : " + baseUSD
		BigDecimal cilexUSD = calculators.percentageOf(baseUSD, cilexFactor).setScale(2, BigDecimal.ROUND_HALF_UP)//comment by robs

        //BigDecimal minimumCilex = currencyConverter.convert("URR", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)

        //println "minimumCilex:" + minimumCilex

		//comment by robs
		println "cilex before minimum " + cilex
		println "cilexUSD: " + cilexUSD		
        if (cilexUSD < 20) {
			println "ASD entered condition: if cilex < minimum cilex"
            cilexUSD = 20
        }
		//comment end


        println "cilex after minimum cilex:" + cilex

		// comment by robs
        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)

        BigDecimal docStamps = calculators.forEvery(baseUrrPhp, baseAmount, centavos ?: rateAmount).setScale(2, BigDecimal.ROUND_HALF_UP)


        BigDecimal corresExport = BigDecimal.ZERO
        if(baseUSD>100000){
            corresExport = 50
        } else {
            corresExport = 35
        }


//        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "CHARGES ORIGINAL VALUE"
        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "charges documentary stamps1:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
        println "charges postage:" + postage.setScale(2, BigDecimal.ROUND_UP)
        println "charges remittance:" + remittance.setScale(2, BigDecimal.ROUND_UP)

        BigDecimal corresExportOriginal = currencyConverter.convert("URR", "USD", corresExport, "PHP").setScale(2, BigDecimal.ROUND_UP)
        def bankCommissionOrig=bankCommission.setScale(2, BigDecimal.ROUND_UP)
        def cilexOrig=currencyConverter.convert("REG-SELL", "USD", cilexUSD, "PHP").setScale(2, BigDecimal.ROUND_UP)
        def docStampsOrig=docStamps.setScale(2, BigDecimal.ROUND_UP)
        def postageOrig=postage.setScale(2, BigDecimal.ROUND_UP)
        def remittanceOrig=remittance.setScale(2, BigDecimal.ROUND_UP)
		println "ASD remittanceOrig: " + remittanceOrig // comment by robs

        println "darwin bank commission before:"+ bankCommission
        println "cwtFlag:"+cwtFlag
        println "chargeSettlementCurrency:"+chargeSettlementCurrency
//        if (("Y".equalsIgnoreCase(cwtFlag)||"1".equalsIgnoreCase(cwtFlag)) && chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
//            bankCommission = cwtPercentage.multiply(bankCommission)
//            println "darwin bank commission cwtFlag:"+ bankCommission
//        }

        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
        String ratesBasis = "URR"
		
		if (settlementToBeneCurrency.equalsIgnoreCase("PHP")) {
			cilex = 0
			cilexUSD = 0
		}
		
		
		
		println "BEFORE:>>>>>>"
		println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
		println "charges documentary stamps3:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
		println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
		println "charges postage:" + postage.setScale(2, BigDecimal.ROUND_UP)
		println "charges remittance:" + remittance.setScale(2, BigDecimal.ROUND_UP)
		
		println "ORIGcharges bank commission:" + bankCommissionOrig.setScale(2, BigDecimal.ROUND_UP)
		println "ORIGcharges documentary stamps3:" + docStampsOrig.setScale(2, BigDecimal.ROUND_UP)
		println "ORIGcharges cilex:" + cilexOrig.setScale(2, BigDecimal.ROUND_UP)
		println "ORIGcharges postage:" + postageOrig.setScale(2, BigDecimal.ROUND_UP)
		println "ORIGcharges remittance:" + remittanceOrig.setScale(2, BigDecimal.ROUND_UP)
		
		
		if (settlementToBeneCurrency.equalsIgnoreCase("PHP")) {
		
			ratesBasis = "REG-SELL"
			
			docStamps = calculators.forEvery(baseSellRatePhp, baseAmount, centavos ?: rateAmount).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "PHP docstamps: " + docStamps		
			docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI docstamps" + docStamps						
			
			remittance= currencyConverter.convert(ratesBasis, "USD", remittance, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI remittance: " + remittance
			//remittanceOrig is already in USD.
			
			bankCommission = calculators.percentageOf(baseSellRatePhp, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "PHP bankCommission: " + bankCommission
			if (bankCommission < 1000) {
				bankCommission = new BigDecimal("1000")
			}
			println "After checking for minimum PHP bankCommission: " + bankCommission		
			bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI bankCommission" + bankCommission
			
			cilex = currencyConverter.convert(ratesBasis, "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI cilex" + cilex
			
			corresExport = currencyConverter.convert(ratesBasis, "USD", corresExport, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI corresExport" + corresExport
			
			postage = currencyConverter.convert(ratesBasis, "PHP", postage, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI postage" + postage
						
		} else if (settlementToBeneCurrency.equalsIgnoreCase("USD")) {
		
			ratesBasis = "URR"
			
			docStamps = calculators.forEvery(baseUrrPhp, baseAmount, centavos ?: rateAmount).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "PHP docstamps: " + docStamps
			docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI docstamps" + docStamps
			
			remittance= currencyConverter.convert(ratesBasis, "USD", remittance, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI remittance: " + remittance
			//remittanceOrig is already in USD.
			
			bankCommission = calculators.percentageOf(baseUrrPhp, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "PHP bankCommission: " + bankCommission
			if (bankCommission < 1000) {
				bankCommission = new BigDecimal("1000")
			}
			println "After checking for minimum PHP bankCommission: " + bankCommission
			bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI bankCommission" + bankCommission
			
			cilex = currencyConverter.convert(ratesBasis, "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			cilexOrig=cilex.setScale(2, BigDecimal.ROUND_UP)
			println "UI cilex" + cilex
			
			corresExport = currencyConverter.convert(ratesBasis, "USD", corresExport, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI corresExport" + corresExport	
			
			postage = currencyConverter.convert(ratesBasis, "PHP", postage, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
			println "UI postage" + postage
		
		}
//		
//		
//         if (settlementToBeneCurrency.equalsIgnoreCase("PHP")) {
//			println "ASD entered >> if chargeSettlementCurrency equalsIgnoreCase PHP, chargesettlementcurrency: " + chargeSettlementCurrency
//            ratesBasis = "REG-SELL"
//            println "no conversion required except for cilex and remittance"
//            println "before conversion advising" + remittance
//
//
//            if (productSettlementPHPTotals > 0) {
//                ratesBasis = "REG-SELL"
//            } else {
//                ratesBasis = "URR"
//            }
//
//			docStamps = calculators.forEvery(baseSellRatePhp, 200, centavos?:0.30).setScale(2, BigDecimal.ROUND_HALF_UP)
//			println "docstamps: " + docStamps
//			
//            remittance= currencyConverter.convert(ratesBasis, "USD", remittance, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//			println "asd remittance: " + remittance
//            cilex= currencyConverter.convert(ratesBasis, "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//			
//			//ASD 6/16/2016 BC fix			
//			println "bankCommissionFactor: " + bankCommissionFactor
//			//bankCommission = bankCommissionPHP comment by robs: bcPHP is computed using URR. if settled in php, use reg-sell
//			println "if settlement php(before computing) BC: " + bankCommission + " baseSellRatePHP: " + baseSellRatePhp
//			bankCommission = calculators.percentageOf(baseSellRatePhp, bankCommissionFactor).setScale(2, BigDecimal.ROUND_HALF_UP)
//			println "zxc BC after: " + bankCommission
//			println "before minimum condition: " + bankCommission
//			if (bankCommission < 1000) {
//				bankCommission = new BigDecimal("1000")
//			}
//
//			println "bc FINAL PHP: " + bankCommission //asd
//			//ASD BC fix  1/16 end
//
//            bankCommissionOrig=bankCommission.setScale(2, BigDecimal.ROUND_UP)
//            cilexOrig=cilex.setScale(2, BigDecimal.ROUND_UP)
//
//            corresExport = currencyConverter.convert(ratesBasis, "USD", corresExport, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//
//            println "after conversion remittance:" + remittance
//
//        } else if (settlementToBeneCurrency.equalsIgnoreCase("USD")) {
//			
//			println "ASD entered condition >> if chargeSettlementCurrency equalsIgnoreCaseUSD, chargesettlementcurrency: " + chargeSettlementCurrency
//            ratesBasis = "URR"
//			// 6/15 CILEX fix comment by robs		
//			cilex = cilexUSD
//			println "final cilex: " + cilex
//			// CILEX fix comment end
//			
//
//            docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
//
//            postage = currencyConverter.convert(ratesBasis, "PHP", postage, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
//
//			// BC update
//			bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommissionPHP, chargeSettlementCurrency)
//			println "final BC: " + bankCommission
//			// BC fix asd end
//			
//            bankCommissionOrig= currencyConverter.convert("URR", "USD", baseUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//            cilexOrig= currencyConverter.convert("URR", "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//
//        } else {
//		
//			println "ASD entered if settlementCurrency equals: " + chargeSettlementCurrency
//			ratesBasis = "URR"
//			
//			//6/15 thirds cilex comment by robs
//			cilex = currencyConverter.convert("REG-SELL", "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
//			// CILEX comment end
//			
//
//			// NEW BC FIX 6/24
//			bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommissionPHP, "USD").setScale(2, BigDecimal.ROUND_HALF_UP)
//			println "BC from php to USD: " + bankCommission
//			bankCommission = currencyConverter.convert("REG-SELL", "USD", bankCommission, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
//			println "BC converted back to " + chargeSettlementCurrency + ". BC is now: " + bankCommission
//			// 6/24 BC fix end
//			
//			
//			//6-24 docstamp comment by robs
//			//convert the docstamp from PHP to thirds
//			docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, "USD").setScale(2, BigDecimal.ROUND_HALF_UP)
//			println "ds php now usd: " + docStamps
//			docStamps = currencyConverter.convert("REG-SELL", "USD", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_HALF_UP)
//			println "final thirds ds: " + docStamps
//			//comment end
//			
//			println "ASD thirdspostage before convertion: " + postage
//			postage = currencyConverter.convert(ratesBasis, "PHP", postage, "USD").setScale(2, BigDecimal.ROUND_UP) // try convert to usd first
//			println "ASD converted to USD: " + postage
//			postage = currencyConverter.convert("REG-SELL", "USD", postage, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//			println "postage converted to third currency (" + chargeSettlementCurrency + "): " + postage
//			
//			println "ASD remittance: " + remittance
//			try{
//				remittance = currencyConverter.convert("REG-SELL", "USD", remittance, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//				println "remittance converted to third currency (" + chargeSettlementCurrency + "): " + remittance
//			}catch (Exception e){
//				e.printStackTrace()
//			}
//			
//			// cilexOrig= currencyConverter.convert("URR", "USD", cilexUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//			// bankCommissionOrig= currencyConverter.convert("URR", "USD", baseUSD, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//			corresExport = currencyConverter.convert("REG-SELL", "USD", corresExport, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//        }

		BigDecimal bankComGross = bankCommission
		BigDecimal bankComCwt = 0	
		BigDecimal cilexGross = cilex
		BigDecimal cilexCwt = 0
		
		if (("Y".equalsIgnoreCase(cwtFlag)||"1".equalsIgnoreCase(cwtFlag))) {
			bankCommission = cwtPercentage.multiply(bankCommission).setScale(2, BigDecimal.ROUND_HALF_UP)			
			bankCommissionOrig = cwtPercentage.multiply(bankCommissionOrig).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "bankCommission with cwtFlag: "+ bankCommission + "\t" + bankCommissionOrig
			bankComCwt = bankComGross.subtract(bankCommission.setScale(2, BigDecimal.ROUND_UP))		
			
			cilex = cwtPercentage.multiply(cilex).setScale(2, BigDecimal.ROUND_HALF_UP)
			cilexOrig = cwtPercentage.multiply(cilexOrig).setScale(2, BigDecimal.ROUND_HALF_UP)
			println "cilex with cwtFlag: "+ cilex + "\t" + cilexOrig
			cilexCwt = cilexGross.subtract(cilex.setScale(2, BigDecimal.ROUND_UP))
		}


        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "charges documentary stamps3:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
        println "charges postage:" + postage.setScale(2, BigDecimal.ROUND_UP)
        println "charges remittance:" + remittance.setScale(2, BigDecimal.ROUND_UP)

        if(!"Y".equalsIgnoreCase(remittanceFlag)){
            remittance = BigDecimal.ZERO
            remittanceOrig = BigDecimal.ZERO
        }


        BigDecimal total = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        total = total + docStamps.setScale(2, BigDecimal.ROUND_UP)
        total = total + cilex.setScale(2, BigDecimal.ROUND_UP)
        total = total + postage.setScale(2, BigDecimal.ROUND_UP)
        total = total + remittance.setScale(2, BigDecimal.ROUND_UP)
        total = total + corresExport.setScale(2, BigDecimal.ROUND_UP)


		//
		println "R TEST postage: " + postage.setScale(2, BigDecimal.ROUND_UP)
		println "R TEST POSTAGEORIGINAL:  " + postageOrig.setScale(2, BigDecimal.ROUND_UP)
		println "R TEST BCoriginal: " + bankCommissionOrig.setScale(2, BigDecimal.ROUND_UP)
		//
        return [
                BC: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                CILEX: cilex.setScale(2, BigDecimal.ROUND_UP),
                POSTAGE: postage.setScale(2, BigDecimal.ROUND_UP),
                REMITTANCE: remittance.setScale(2, BigDecimal.ROUND_UP),
                'CORRES-EXPORT': corresExport.setScale(2, BigDecimal.ROUND_HALF_UP),
                TOTAL: total,
                BCoriginal: bankCommissionOrig.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStampsOrig.setScale(2, BigDecimal.ROUND_UP),
                CILEXoriginal: cilexOrig.setScale(2, BigDecimal.ROUND_UP),
                POSTAGEoriginal: postageOrig.setScale(2, BigDecimal.ROUND_UP),
                REMITTANCEoriginal: remittance.setScale(2, BigDecimal.ROUND_UP),
                'CORRES-EXPORToriginal': corresExport.setScale(2, BigDecimal.ROUND_HALF_UP),
				bankComGross: bankComGross.setScale(2, BigDecimal.ROUND_UP),
				bankComCwt: bankComCwt.setScale(2, BigDecimal.ROUND_HALF_UP),
				cilexGross: cilexGross.setScale(2, BigDecimal.ROUND_UP),
				cilexCwt: cilexCwt.setScale(2, BigDecimal.ROUND_HALF_UP)
        ]
    }

    public Map computeSettlement(Map productDetails) {

        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal") ?: 4
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal") ?: 0.01

        BigDecimal commitmentFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal commitmentFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeDenominator"), "BigDecimal") ?: 4
        BigDecimal commitmentFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeePercentage"), "BigDecimal") ?: 0.01
        BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexNumerator"), "BigDecimal") ?: 1
        BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexDenominator"), "BigDecimal") ?: 4
        BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexPercentage"), "BigDecimal") ?: 0.01

        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String issueDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("issueDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        BigDecimal bankCommissionDays = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionMonths"), "BigDecimal") ?: 0
        BigDecimal commitmentFeeMonths = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeMonths"), "BigDecimal") ?: 0

        BigDecimal centavos = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("centavos"), "BigDecimal") ?: 0.3

        println "documentSubType1:" + documentSubType1
        println "documentSubType2:" + documentSubType2

        Calculators calculators = new Calculators()
        println "issueDate:" + issueDate
        println "etsDate:" + etsDate
        println "expiryDate:" + expiryDate
        BigDecimal monthsEtsToExpiry = 0
        monthsEtsToExpiry.setScale(12)
        monthsEtsToExpiry = calculators.getMonthsTill(issueDate, expiryDate) < 1 ? 1 : calculators.getMonthsTill(issueDate, expiryDate)
        BigDecimal daysEtsToExpiry = calculators.getDaysTillA(issueDate, expiryDate)
        println "monthsEtsToExpiry:" + monthsEtsToExpiry
        println "daysEtsToExpiry:" + daysEtsToExpiry
        if(daysEtsToExpiry<30){
            daysEtsToExpiry = 30
        }
        println "daysEtsToExpiry:" + daysEtsToExpiry

        BigDecimal monthsCommitmentFee = 0
        monthsCommitmentFee.setScale(12)

        println "commitmentFeeDenominator: " + commitmentFeeDenominator
        println "commitmentFeeNumerator: " + commitmentFeeNumerator
        println "commitmentFeePercentage: " + commitmentFeePercentage

        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")

        basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
        println "URR BASE PHP" + basePHP
        basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        println "WITHDRAWAL BASE PHP" + basePHP
        println "basePHP:" + basePHP

        BigDecimal daysCommitmentFee

        if ("CASH".equalsIgnoreCase(documentSubType1)) {
            println "CASH SIGHT"
            monthsCommitmentFee = 0
            daysCommitmentFee = 0
            //Check if

            if (getBaseVariable("productSettlementThirdTotals") == 0 && getBaseVariable("productSettlementPHPTotals") == 0) {
                //all usd
                basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
            } else if (getBaseVariable("productSettlementThirdTotals") == 0 && getBaseVariable("productSettlementUSDTotals") == 0) {
                basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
            }
            println "THIS IS THE BASE:::" + basePHP

        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
            monthsCommitmentFee = monthsEtsToExpiry
            daysCommitmentFee = daysEtsToExpiry
            println "STANDBY"
            println "monthsCommitmentFee:"+monthsCommitmentFee
            println "daysCommitmentFee:"+daysCommitmentFee
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            println "REGULAR SIGHT"
            monthsCommitmentFee = 0
            daysCommitmentFee = 0
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            println "REGULAR USANCE"
            println "extendedProperties.get(\"usancePeriod\")" + extendedProperties.get("usancePeriod").toString()
            BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
            println usancePeriod
            println calculators.getMonthsOf(usancePeriod)
            monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)

            daysCommitmentFee = usancePeriod < 30 ? 30 : usancePeriod
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        }
        println "basePHP:" + basePHP
        println "monthsCommitmentFee:" + monthsCommitmentFee
        println "monthsEtsToExpiry:" + monthsEtsToExpiry
        println "daysCommitmentFee:" + daysCommitmentFee

        // parameterized factors
        BigDecimal bankCommissionFactor = 0
        bankCommissionFactor.setScale(12)
        bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysEtsToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "bankCommissionFactor:" + bankCommissionFactor

        if (monthsEtsToExpiry > 0 && bankCommissionDays > 0) { //This is the  No of days field found in recompute popup
            if (bankCommissionDays < 30) {
                bankCommissionDays = 30
            }
            bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(bankCommissionDays.divide(30, 12, RoundingMode.FLOOR)).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
            println "bankCommissionFactor: " + bankCommissionFactor
        }
        if (daysCommitmentFee > 0 && commitmentFeeMonths > 0) {
            daysCommitmentFee = commitmentFeeMonths //This is the  No of days field found in recompute popup
        }
        println "daysCommitmentFee:" + daysCommitmentFee
        if (!daysCommitmentFee) {
            daysCommitmentFee = 0
        }
        println "daysCommitmentFee:" + daysCommitmentFee
        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(((daysCommitmentFee).divide(30, 12, RoundingMode.FLOOR))).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "commitmentFeeFactor:" + commitmentFeeFactor
        println "cilexDenominator: " + commitmentFeeDenominator
        println "cilexNumerator: " + cilexNumerator
        println "cilexPercentage: " + cilexPercentage
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "cilexFactor:" + cilexFactor
        BigDecimal suppliesFeeDefault = 50
        BigDecimal cableFeeDefault = 800


        // charges
        BigDecimal bankCommission = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, bankCommissionFactor, 1000)
        BigDecimal commitmentFee = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, commitmentFeeFactor, 500)
        if ("CASH".equalsIgnoreCase(documentSubType1) || ("SIGHT".equalsIgnoreCase(documentSubType2) && "REGULAR".equalsIgnoreCase(documentSubType2))) {
            commitmentFee = 0
        }
        BigDecimal suppliesFee = suppliesFeeDefault
        BigDecimal cableFee = cableFeeDefault
        BigDecimal settledInForeignInUSD = (BigDecimal) getBaseVariable("settledInForeignInUSD")
        println "settledInForeignInUSD:" + settledInForeignInUSD
        BigDecimal productSettlementPHPTotals = (BigDecimal) getBaseVariable("productSettlementPHPTotals")
        BigDecimal productSettlementUSDTotals = (BigDecimal) getBaseVariable("productSettlementUSDTotals")
        BigDecimal productSettlementThirdTotals = (BigDecimal) getBaseVariable("productSettlementThirdTotals")
        println "productSettlementPHPTotals:" + productSettlementPHPTotals
        BigDecimal cilex = BigDecimal.ZERO
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
            temp = currencyConverter.convert("URR", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
            minimumCilex = currencyConverter.convert("URR", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_FLOOR)
            println "temp:" + temp
            cilex = calculators.percentageOf(temp, cilexFactor)
        }

        println "minimumCilex:" + minimumCilex
        println "cilex:" + cilex

        if (cilex < minimumCilex) {
            cilex = minimumCilex
        }
        println "cilex:" + cilex

        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
        BigDecimal docStamps = calculators.forEvery(basePHP, baseAmount, centavos ?: rateAmount)

        BigDecimal bankCommissionOriginal = bankCommission
        BigDecimal commitmentFeeOriginal = commitmentFee
        BigDecimal cilexOriginal = cilex
        BigDecimal docStampsOriginal=docStamps.setScale(2, BigDecimal.ROUND_UP)
        BigDecimal postageOriginal=postage.setScale(2, BigDecimal.ROUND_UP)
        BigDecimal remittanceOriginal=remittance.setScale(2, BigDecimal.ROUND_UP)

        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")

        if (("Y".equalsIgnoreCase(cwtFlag)||"1".equalsIgnoreCase(cwtFlag)) && chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            bankCommission = cwtPercentage.multiply(bankCommission)
        }


        println "CHARGES ORIGINAL VALUE"
        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges commitment fee:" + commitmentFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges documentary stamps4:" + docStamps.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges supplies fee:" + suppliesFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_HALF_UP)


        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
        String ratesBasis = "URR"
        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            ratesBasis = "URR"
            println "no conversion required except for cilex and advising"

            if (productSettlementPHPTotals > 0) {
                ratesBasis = "REG-SELL"
            } else {
                ratesBasis = "URR"
            }

            ratesBasis = "URR"

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            commitmentFee = currencyConverter.convert(ratesBasis, "PHP", commitmentFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            suppliesFee = currencyConverter.convert(ratesBasis, "PHP", suppliesFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "PHP", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)

        } else {
            ratesBasis = "URR"

            bankCommission = currencyConverter.convertWithPrecision(ratesBasis, "PHP", bankCommission, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            commitmentFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", commitmentFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            docStamps = currencyConverter.convertWithPrecision(ratesBasis, "PHP", docStamps, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cableFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cableFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            suppliesFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", suppliesFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cilex = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cilex, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)


            println "AFTER URR VALUE"
            println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP)
            println "charges commitment fee:" + commitmentFee.setScale(2, BigDecimal.ROUND_HALF_UP)
            println "charges documentary stamps5:" + docStamps.setScale(2, BigDecimal.ROUND_HALF_UP)
            println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)
            println "charges supplies fee:" + suppliesFee.setScale(2, BigDecimal.ROUND_HALF_UP)
            println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_HALF_UP)

            ratesBasis = "REG-SELL"
            bankCommission = currencyConverter.convert(ratesBasis, "USD", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            commitmentFee = currencyConverter.convert(ratesBasis, "USD", commitmentFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            docStamps = currencyConverter.convert(ratesBasis, "USD", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "USD", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            suppliesFee = currencyConverter.convert(ratesBasis, "USD", suppliesFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)


        }



        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges commitment fee:" + commitmentFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges documentary stamps6:" + docStamps.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges supplies fee:" + suppliesFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_HALF_UP)
        println "charges confirming fee:" + corresExport.setScale(2, BigDecimal.ROUND_HALF_UP)

        BigDecimal total = bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP)
        total = total + commitmentFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        total = total + docStamps.setScale(2, BigDecimal.ROUND_HALF_UP)
        total = total + cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        total = total + suppliesFee.setScale(2, BigDecimal.ROUND_HALF_UP)
        total = total + cilex.setScale(2, BigDecimal.ROUND_HALF_UP)


        return [
                BC: bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP),
                CF: commitmentFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_HALF_UP),
                CABLE: cableFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                SUP: suppliesFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                CILEX: cilex.setScale(2, BigDecimal.ROUND_HALF_UP),
                TOTAL: total,
                BCoriginal: bankCommissionOriginal.setScale(2, BigDecimal.ROUND_HALF_UP),
                CForiginal: commitmentFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                DOCSTAMPSoriginal: docStamps.setScale(2, BigDecimal.ROUND_HALF_UP),
                CABLEoriginal: cableFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                SUPoriginal: suppliesFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                CILEXoriginal: cilexOriginal.setScale(2, BigDecimal.ROUND_HALF_UP)
        ]
    }

}
