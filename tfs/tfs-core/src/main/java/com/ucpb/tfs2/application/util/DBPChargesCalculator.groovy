package com.ucpb.tfs2.application.util

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:55 PM
 */
class DBPChargesCalculator extends ChargesCalculator {

    public Map computeNegotiation(Map productDetails) {
        println "computeNegotiation"
        println "productDetails:"+productDetails
        // precompute for the base variables
        precomputeBaseDMBPBC(productDetails);

        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal postageFeeDefaultParam = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("postageFeeDefault"), "BigDecimal")?:400
        BigDecimal remittanceFeeDefaultParam = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("remittanceFeeDefault"), "BigDecimal")?:18

        BigDecimal bankCommissionNumerator = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal")?:1
        BigDecimal bankCommissionDenominator = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal")?:4
        BigDecimal bankCommissionPercentage = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal")?:0.01

        BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexNumerator"), "BigDecimal") ?: 1
        BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexDenominator"), "BigDecimal") ?: 4
        BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexPercentage"), "BigDecimal") ?: 0.01

        String cwtFlag =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage =(BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal")?:0.98
        String remittanceFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "remittanceFlag", "String") ?: "N"
        BigDecimal centavos = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("centavos"), "BigDecimal") ?: 0.3

        String documentType =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentType"), "String")
        String documentClass =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentClass"), "String")
        String documentSubType1 =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():"+getBaseVariable("chargeSettlementCurrency")?.toString()

        BigDecimal negotiationAmount = (BigDecimal)getBaseVariable("negotiationAmount")
        BigDecimal convertedDbpAmount = (BigDecimal)getBaseVariable("convertedDbpAmount")
        BigDecimal proceedsAmount = (BigDecimal)getBaseVariable("proceedsAmount")
        BigDecimal actualCorresCharges = (BigDecimal)getBaseVariable("actualCorresCharges")
        BigDecimal proceedsAmountInSettlementCurrency = (BigDecimal)getBaseVariable("proceedsAmountInSettlementCurrency")
        String proceedsAmountSettlementCurrency = (String)getBaseVariable("proceedsAmountSettlementCurrency")
        String currency = (String) productDetails.productCurrency

        //DocStamps -> Based on Remaining DBC Amount billable
        BigDecimal proceedsAmountInPHP = proceedsAmount
        if ("PHP".equalsIgnoreCase(currency)){
            proceedsAmountInPHP = proceedsAmount
        } else if ("USD".equalsIgnoreCase(currency)){
            if(proceedsAmountSettlementCurrency.equalsIgnoreCase("PHP")){
                proceedsAmountInPHP = currencyConverter.convert("URR", currency, proceedsAmount, "USD")
            } else { //No thirds so this is automatically USD use urr
                proceedsAmountInPHP = currencyConverter.convert("REG-SELL", currency, proceedsAmount, "USD")
            }
        } else {
            if(proceedsAmountSettlementCurrency.equalsIgnoreCase("PHP")){
                BigDecimal usdBase = currencyConverter.convert("REG-SELL", currency, proceedsAmount, "USD")
                proceedsAmountInPHP = currencyConverter.convert("URR", "USD", usdBase, "PHP")
            } else { //No thirds so this is automatically USD use urr
                proceedsAmountInPHP = currencyConverter.convert("REG-SELL", currency, proceedsAmount, "USD")
            }

//            usdBase = currencyConverter.convert(thirdToUsdRateType, currency, productAmount, "USD")
//            phpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", usdBase, "PHP")
//            phpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
//
//            proceedsAmountInPHP = proceedsAmountInProductCurrency
        }


        //Bank Commission -> Based Proceeds Settlement Amount
        //CILEX -> Based Proceeds Settlement Amount
        //Postage Fee -> Proceeds Settlement Amount is greater than Zero
        //Remittance Fee -> 18 USD
        //Additional Corres Charge -> Based on Actual Corres Charge

        Calculators calculators = new Calculators()
        BigDecimal basePHP = (BigDecimal)getBaseVariable("chargesBasePHP")

        if ("CASH".equalsIgnoreCase(documentSubType1)){
            basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)){
            basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)){
            basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            println "extendedProperties.get(\"usancePeriod\")"+extendedProperties.get("usancePeriod").toString()
            BigDecimal usancePeriod = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal")?:30
            println usancePeriod
            println calculators.getMonthsOf(usancePeriod)
            basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        }


        // parameterized factors
        BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).divide(bankCommissionDenominator,12,BigDecimal.ROUND_HALF_UP)
        println "bankCommissionFactor:"+bankCommissionFactor
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "cilexFactor:" + cilexFactor

        BigDecimal postageFeeDefault = postageFeeDefaultParam
        BigDecimal remittanceFeeDefault = remittanceFeeDefaultParam
        println "remittanceFeeDefault::::"+remittanceFeeDefault

        // charges
        BigDecimal bankCommission = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, bankCommissionFactor, 1000)
        BigDecimal postageFee = postageFeeDefault

        //DocStamps -> Based on Remaining DBC Amount billable
        BigDecimal remainingDbcBillableAmountOrig = (BigDecimal) getBaseVariable("remainingDbcBillableAmount")
        BigDecimal remainingDbcBillableAmount = (BigDecimal) getBaseVariable("remainingDbcBillableAmount")
        BigDecimal docStamps
        println "remainingDbcBillableAmount:"+remainingDbcBillableAmount
        if("PHP".equalsIgnoreCase(currency)){
            docStamps = calculators.forEvery(basePHP, 200, centavos)
        } else if ("USD".equalsIgnoreCase(currency)){
            docStamps = calculators.forEvery(basePHP, 200, centavos)
        } else{
            docStamps = calculators.forEvery(basePHP, 200, centavos)
//            docStamps = calculators.forEvery(remainingDbcBillableAmount, 200, centavos)
        }

        BigDecimal remittanceFee = BigDecimal.ZERO

        if("PHP".equalsIgnoreCase(productDetails.productCurrency)){
            remittanceFee = 100.00
        } else {
//            remittanceFee = "Y".equalsIgnoreCase(remittanceFlag) ? currencyConverter.convert("URR", "USD", remittanceFeeDefault, "PHP").setScale(2, BigDecimal.ROUND_UP) : 0.00
            remittanceFee = currencyConverter.convert("URR", "USD", remittanceFeeDefault, "PHP").setScale(2, BigDecimal.ROUND_UP)  //TODO tama yung taas mali yung baba
        }

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

        if (settledInForeignInUSD > 0) {//minimum cilex must only be checked if there is a settlement in foreign currency otherwise use zero
            if (cilex < minimumCilex) {
                cilex = minimumCilex
            }
        }
        println "cilex:" + cilex
        if ("Y".equalsIgnoreCase(cwtFlag)){
            bankCommission = cwtPercentage.multiply(bankCommission)
            cilex = cwtPercentage.multiply(cilex)
        }

        println "CHARGES ORIGINAL VALUE"
        println "Documentary Stamps: " + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "Bank Commission: " + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "Cilex Fee : " + cilex.setScale(2, BigDecimal.ROUND_UP)
        println "Postage Fee : " + postageFee.setScale(2, BigDecimal.ROUND_UP)
        println "Remittance Fee : " + remittanceFee.setScale(2, BigDecimal.ROUND_UP)


        def bankCommissionOrig = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        def docStampsOrig = docStamps.setScale(2, BigDecimal.ROUND_UP)
        def cilexOrig = cilex.setScale(2, BigDecimal.ROUND_UP)
        def postageFeeOrig = postageFee.setScale(2, BigDecimal.ROUND_UP)
        def remittanceFeeOrig = remittanceFee.setScale(2, BigDecimal.ROUND_UP)

        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
        String ratesBasis
        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            ratesBasis = "URR"
            println "no conversion required except for cilex and advising"


            if ("CASH".equalsIgnoreCase(documentSubType1)) {
                println "CASH SIGHT"
                if (productSettlementPHPTotals > 0) {
                    ratesBasis = "REG-SELL"
                } else {
                    ratesBasis = "URR"
                }

            } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
                println "STANDBY"
                ratesBasis = "REG-SELL"
            } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
                println "REGULAR SIGHT"
                ratesBasis = "REG-SELL"
            } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
                println "REGULAR USANCE"
                ratesBasis = "REG-SELL"
            }

            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP)

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)

            postageFee = currencyConverter.convert(ratesBasis, "PHP", postageFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            remittanceFee = currencyConverter.convert(ratesBasis, "PHP", remittanceFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)


        } else {
            ratesBasis = "URR"

            bankCommission = currencyConverter.convertWithPrecision(ratesBasis, "PHP", bankCommission, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            docStamps = currencyConverter.convertWithPrecision(ratesBasis, "PHP", docStamps, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
//            cilex = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cilex, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            postageFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", postageFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            remittanceFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", remittanceFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)



            println "AFTER URR VALUE"

            ratesBasis = "REG-SELL"
            bankCommission = currencyConverter.convert(ratesBasis, "USD", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            docStamps = currencyConverter.convert(ratesBasis, "USD", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            postageFee = currencyConverter.convert(ratesBasis, "USD", postageFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            remittanceFee = currencyConverter.convert(ratesBasis, "USD", remittanceFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)

        }



        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
        println "charges postage:" + postageFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges remittance:" + remittanceFee.setScale(2, BigDecimal.ROUND_UP)

        BigDecimal total = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        total = total + docStamps.setScale(2, BigDecimal.ROUND_UP)
        total = total + cilex.setScale(2, BigDecimal.ROUND_UP)
        total = total + postageFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + remittanceFee.setScale(2, BigDecimal.ROUND_UP)



        return [
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                BC:bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP),
                CILEX: cilex.setScale(2, BigDecimal.ROUND_HALF_UP),
                POSTAGE:postageFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                REMITTANCE: remittanceFee.setScale(2, BigDecimal.ROUND_UP),
                TOTAL:total,
                DOCSTAMPSoriginal: docStampsOrig.setScale(2, BigDecimal.ROUND_UP),
                BCoriginal:bankCommissionOrig.setScale(2, BigDecimal.ROUND_HALF_UP),
                CILEXoriginal: cilexOrig.setScale(2, BigDecimal.ROUND_HALF_UP),
                POSTAGEoriginal:postageFeeOrig.setScale(2, BigDecimal.ROUND_HALF_UP),
                REMITTANCEoriginal: remittanceFeeOrig.setScale(2, BigDecimal.ROUND_UP)
        ]
    }

    public Map computeSettlement(Map productDetails) {

        // precompute for the base variables

        // TODO!
        precomputeBaseDMBPBC(productDetails);

        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal negoAmountNet = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("negoAmountNet"), "BigDecimal")?:0
        BigDecimal negoAmountGross = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("negoAmountGross"), "BigDecimal")?:0


        String etsDate =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String cwtFlag =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage =(BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal")?:0.98

        //String documentType =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentType", "String")
        //String documentClass =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentClass", "String")
        String documentSubType1 =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():"+getBaseVariable("chargeSettlementCurrency")?.toString()

        Calculators calculators = new Calculators()
        println "etsDate:"+etsDate
        BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)

        BigDecimal basePHP = (BigDecimal)getBaseVariable("chargesBasePHP")

        if ("CASH".equalsIgnoreCase(documentSubType1)){
            basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)){
            basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)){
            basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        }



        // charges
        BigDecimal otherExport = negoAmountGross.subtract(negoAmountNet)



        return [
                "OTHER-EXPORT":otherExport.setScale(2, BigDecimal.ROUND_HALF_UP),
                TOTAL:otherExport.setScale(2, BigDecimal.ROUND_HALF_UP)
        ]
    }


}
