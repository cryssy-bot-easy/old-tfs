package com.ucpb.tfs2.application.util

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:55 PM
 */
class ImportAdvanceChargesCalculator extends ChargesCalculator {

    public Map computeAdvancePayment(Map productDetails) {

        println "productDetails:" + productDetails

        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal") ?: 0.01

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

        BigDecimal centavos = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("centavos"), "BigDecimal") ?: 0.3

        println "productDetails:"+productDetails
        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "chargeSettlementCurrency:" + chargeSettlementCurrency
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
        if (daysEtsToExpiry < 30) {
            daysEtsToExpiry = 30
        }
        println "daysEtsToExpiry:" + daysEtsToExpiry


        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")
        BigDecimal basePHPUrr = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")

        if (getBaseVariable("productSettlementThirdTotals") == 0 && getBaseVariable("productSettlementPHPTotals") == 0) {
            //all usd
            basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
        } else if (getBaseVariable("productSettlementPHPTotals") > 0) {
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        }
        println "THIS IS THE BASE:::" + basePHP

        println "monthsEtsToExpiry:" + monthsEtsToExpiry

        // parameterized factors
        BigDecimal bankCommissionFactor = 0
        bankCommissionFactor.setScale(12)
        bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysEtsToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "bankCommissionFactor:" + bankCommissionFactor

        if (monthsEtsToExpiry > 0 && bankCommissionDays > 0) { //This is the  No of days field found in recompute popup
            if (bankCommissionDays < 30) {
                bankCommissionDays = 30
            }
            bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(bankCommissionDays.divide(30, 12, BigDecimal.ROUND_FLOOR)).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
            println "bankCommissionFactor: " + bankCommissionFactor
        }

        println "cilexNumerator: " + cilexNumerator
        println "cilexPercentage: " + cilexPercentage
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "cilexFactor:" + cilexFactor
        BigDecimal cableFeeDefault = 1000

        // charges
        BigDecimal bankCommission = calculators.firstSucceedingPercentageWithMinimum(basePHP, 50000, 125, bankCommissionFactor, 1000)
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
            temp = currencyConverter.convert("REG-SELL", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_UP)
            minimumCilex = currencyConverter.convert("REG-SELL", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_UP)
            println "temp:" + temp
            cilex = calculators.percentageOf(temp, cilexFactor) //Use REG-SELL
        } else if (productSettlementUSDTotals > 0 && productSettlementThirdTotals > 0) {
            //This means that there was a PHP settlement
            temp = currencyConverter.convert("REG-SELL", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_UP)
            minimumCilex = currencyConverter.convert("REG-SELL", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_UP)
            println "temp:" + temp
            cilex = calculators.percentageOf(temp, cilexFactor) //Use REG-SELL
        } else {
            temp = currencyConverter.convert("URR", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_UP)
            minimumCilex = currencyConverter.convert("URR", "USD", 20, "PHP").setScale(2, BigDecimal.ROUND_UP)
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

        BigDecimal docStamps = calculators.forEvery(basePHPUrr, 200, centavos ?: 0.30)

        if ("CASH".equalsIgnoreCase(documentSubType1)) {

        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
            cilex = 0
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            cilex = 0
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            cilex = 0
        }



        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommission = cwtPercentage.multiply(bankCommission)
            cilex = cwtPercentage.multiply(cilex)
        }


        println "CHARGES ORIGINAL VALUE"
        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)

        def bankCommissionOrig = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        def docStampsOrig = docStamps.setScale(2, BigDecimal.ROUND_UP)
        def cableFeeOrig = cableFee.setScale(2, BigDecimal.ROUND_UP)
        def cilexOrig = cilex.setScale(2, BigDecimal.ROUND_UP)


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

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "PHP", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
        } else {
            ratesBasis = "URR"

            bankCommission = currencyConverter.convertWithPrecision(ratesBasis, "PHP", bankCommission, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            docStamps = currencyConverter.convertWithPrecision(ratesBasis, "PHP", docStamps, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cableFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cableFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cilex = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cilex, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)

            println "AFTER URR VALUE"

            ratesBasis = "REG-SELL"
            bankCommission = currencyConverter.convert(ratesBasis, "USD", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            docStamps = currencyConverter.convert(ratesBasis, "USD", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "USD", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)

        }



        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)

        BigDecimal total = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        total = total + docStamps.setScale(2, BigDecimal.ROUND_UP)
        total = total + cableFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + cilex.setScale(2, BigDecimal.ROUND_UP)


        return [
                BC: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                CABLE: cableFee.setScale(2, BigDecimal.ROUND_UP),
                CILEX: cilex.setScale(2, BigDecimal.ROUND_UP),
                TOTAL: total.setScale(2, BigDecimal.ROUND_UP),
                'BCoriginal': bankCommissionOrig,
                'DOCSTAMPSoriginal': docStampsOrig,
                'CABLEoriginal': cableFeeOrig,
                'CILEXoriginal': cilexOrig,
        ]
    }

    public Map computeAdvanceRefund(Map productDetails) {

        // precompute for the base variables

        // TODO!
        precomputeBaseDMLC(productDetails);

        //parameters

        String documentType = (String)ChargesCalculator.getExtendedPropertiesVariable(productDetails,"documentType", "String")?:"FOREIGN"
        String documentSubType1 =(String)ChargesCalculator.getExtendedPropertiesVariable(productDetails,"documentSubType1", "String")
        String documentSubType2 =(String)ChargesCalculator.getExtendedPropertiesVariable(productDetails,"documentSubType2", "String")

        String remittanceFlag = (String)ChargesCalculator.getExtendedPropertiesVariable(productDetails,"remittanceFlag", "String")?:"N"
        String cableFeeFlag = (String)ChargesCalculator.getExtendedPropertiesVariable(productDetails,"cableFeeFlag", "String")?:"N"

        println "remittanceFlag:"+remittanceFlag
        println "cableFeeFlag:"+cableFeeFlag
        BigDecimal cableFeeDefault = 500
        BigDecimal remittanceFeeDefault = 18
        Calculators calculators = new Calculators()

        BigDecimal docStamps = BigDecimal.ZERO
        if(((BigDecimal)getBaseVariable("totalTrAmount")).compareTo(BigDecimal.ZERO) == 1) {
            docStamps = calculators.firstSucceedingFixed((BigDecimal)getBaseVariable("totalTrAmountInPHP"), 5000, 20, 5000, 10)
        }

        BigDecimal cableFee = "Y".equalsIgnoreCase(cableFeeFlag)?cableFeeDefault:0.00
        BigDecimal remittanceFee = "Y".equalsIgnoreCase(remittanceFlag)?currencyConverter.convert("URR", "USD", remittanceFeeDefault, "PHP").setScale(2, BigDecimal.ROUND_UP):0.00

        // charges
        println "Documentary Stamps: " + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "Remittance Fee : " + remittanceFee.setScale(2, BigDecimal.ROUND_UP)
        println "Cable Fee: " + cableFee.setScale(2, BigDecimal.ROUND_UP)

        return [
                DOCSTAMPS:docStamps.setScale(2, BigDecimal.ROUND_UP),
                REMITTANCE:remittanceFee.setScale(2, BigDecimal.ROUND_UP),
                CABLE:cableFee.setScale(2, BigDecimal.ROUND_UP)
        ]
    }
	
	public Map computeExportAdvanceRefund(Map productDetails){
		 println "productDetails:" + productDetails

        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal") ?: 0.01

        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String issueDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("issueDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        BigDecimal bankCommissionDays = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionMonths"), "BigDecimal") ?: 0

        BigDecimal centavos = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("centavos"), "BigDecimal") ?: 0.3

        println "productDetails:"+productDetails
        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "chargeSettlementCurrency:" + chargeSettlementCurrency
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
        if (daysEtsToExpiry < 30) {
            daysEtsToExpiry = 30
        }
        println "daysEtsToExpiry:" + daysEtsToExpiry


        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")
        BigDecimal basePHPUrr = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")

        if (getBaseVariable("productSettlementThirdTotals") == 0 && getBaseVariable("productSettlementPHPTotals") == 0) {
            //all usd
            basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
        } else if (getBaseVariable("productSettlementPHPTotals") > 0) {
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        }
        println "THIS IS THE BASE:::" + basePHP

        println "monthsEtsToExpiry:" + monthsEtsToExpiry

        // parameterized factors
        BigDecimal bankCommissionFactor = 0
        bankCommissionFactor.setScale(12)
        bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysEtsToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "bankCommissionFactor:" + bankCommissionFactor

        if (monthsEtsToExpiry > 0 && bankCommissionDays > 0) { //This is the  No of days field found in recompute popup
            if (bankCommissionDays < 30) {
                bankCommissionDays = 30
            }
            bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(bankCommissionDays.divide(30, 12, BigDecimal.ROUND_FLOOR)).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
            println "bankCommissionFactor: " + bankCommissionFactor
        }

         BigDecimal cableFeeDefault = 500

        // charges
        BigDecimal bankCommission = calculators.firstSucceedingPercentageWithMinimum(basePHP, 50000, 125, bankCommissionFactor, 1000)
        BigDecimal cableFee = cableFeeDefault
        BigDecimal settledInForeignInUSD = (BigDecimal) getBaseVariable("settledInForeignInUSD")
        println "settledInForeignInUSD:" + settledInForeignInUSD
        BigDecimal productSettlementPHPTotals = (BigDecimal) getBaseVariable("productSettlementPHPTotals")
        BigDecimal productSettlementUSDTotals = (BigDecimal) getBaseVariable("productSettlementUSDTotals")
        BigDecimal productSettlementThirdTotals = (BigDecimal) getBaseVariable("productSettlementThirdTotals")
        println "productSettlementPHPTotals:" + productSettlementPHPTotals
        BigDecimal temp = BigDecimal.ZERO
       if (productSettlementPHPTotals > 0) {
            //This means that there was a PHP settlement
            temp = currencyConverter.convert("REG-SELL", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_UP)
            println "temp:" + temp
       } else if (productSettlementUSDTotals > 0 && productSettlementThirdTotals > 0) {
            //This means that there was a PHP settlement
            temp = currencyConverter.convert("REG-SELL", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_UP)
            println "temp:" + temp
        } else {
            temp = currencyConverter.convert("URR", "USD", settledInForeignInUSD, "PHP").setScale(2, BigDecimal.ROUND_UP)
            println "temp:" + temp
        }

       
        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommission = cwtPercentage.multiply(bankCommission)
        }


        println "CHARGES ORIGINAL VALUE"
        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
		println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_UP)
      
        def bankCommissionOrig = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        def cableFeeOrig = cableFee.setScale(2, BigDecimal.ROUND_UP)
       

        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
        String ratesBasis
        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            ratesBasis = "URR"
            println "no conversion required except for cilex and advising"

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
          } else {
            ratesBasis = "URR"

            bankCommission = currencyConverter.convertWithPrecision(ratesBasis, "PHP", bankCommission, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cableFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cableFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
      
            println "AFTER URR VALUE"

            ratesBasis = "REG-SELL"
            bankCommission = currencyConverter.convert(ratesBasis, "USD", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "USD", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
     
        }



        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_UP)
      
        BigDecimal total = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        total = total + cableFee.setScale(2, BigDecimal.ROUND_UP)
     

        return [
                BC: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                CABLE: cableFee.setScale(2, BigDecimal.ROUND_UP),
                TOTAL: total.setScale(2, BigDecimal.ROUND_UP),
                'BCoriginal': bankCommissionOrig,
				'CABLEoriginal': cableFeeOrig,
        ]
	}
}
