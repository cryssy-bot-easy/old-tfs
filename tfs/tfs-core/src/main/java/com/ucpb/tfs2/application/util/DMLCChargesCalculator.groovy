package com.ucpb.tfs2.application.util

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:55 PM
 */
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Changes hard-coded parameters of computing
 *      document stamps into data retrieved from database.
 *  Date revised: 02/01/2018
*/
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Added values for forFirst and forNext on computeNegotiation method.
 *  Date revised: 03/20/2018
 */
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Added values for forFirstAmount and forNextAmount on computeNegotiation method.
 *  Date revised: 03/21/2018
 */
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Added values for forFirst, forNext, forFirstAmount and forNextAmount on computeUaLoanSettlement method.
 *  Date revised: 03/21/2018
 */
class DMLCChargesCalculator extends ChargesCalculator {

    public Map computeOpening(Map productDetails) {

        // precompute for the base variables
        precomputeBaseDMLC(productDetails);

        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal") ?: 0.01

        BigDecimal commitmentFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal commitmentFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeDenominator"), "BigDecimal") ?: 4
        BigDecimal commitmentFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeePercentage"), "BigDecimal") ?: 0.01

        // String etsDate =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String issueDate =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("issueDate"), "String")
        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        //String documentType =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentType", "String")
        //String documentClass =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("documentClass", "String")
        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():" + getBaseVariable("chargeSettlementCurrency")?.toString()

        BigDecimal bankCommissionDays = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionMonths"), "BigDecimal") ?: 0
        BigDecimal commitmentFeeMonths = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeMonths"), "BigDecimal") ?: 0

        Calculators calculators = new Calculators()

        // println "etsDate: " + etsDate
        println "issueDate: " + issueDate
        println "expiryDate: " + expiryDate
        println "bankCommissionDays: " + bankCommissionDays
        println "commitmentFeeMonths: " + commitmentFeeMonths

        // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
        // Integer daysEtsToExpiry = (calculators.getDaysTill(etsDate,expiryDate) < 30) ? 30 : calculators.getDaysTill(etsDate,expiryDate)
        Integer daysIssueDateToExpiry = (calculators.getDaysTill(issueDate,expiryDate) < 30) ? 30 : calculators.getDaysTill(issueDate,expiryDate)
        println ">>>>>>>>>>>>>>>>>>>>>>>>> daysIssueDateToExpiry = ${daysIssueDateToExpiry}"
        BigDecimal monthsCommitmentFee = 0

        // BigDecimal basePHP = (BigDecimal)getBaseVariable("chargesBasePHP")
        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")  // As per Ma'am Letty, 4/12/2013

        if ("CASH".equalsIgnoreCase(documentSubType1)) {
            monthsCommitmentFee = 0
            // basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
            // monthsCommitmentFee = monthsEtsToExpiry.compareTo(BigDecimal.ONE)!=1?1:monthsEtsToExpiry
            // monthsCommitmentFee = (daysEtsToExpiry.compareTo(30) != 1) ? 1 : (daysEtsToExpiry/30)
            monthsCommitmentFee = (daysIssueDateToExpiry.compareTo(30) != 1) ? 1 : (daysIssueDateToExpiry/30)
            // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            monthsCommitmentFee = 1
            // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            println "extendedProperties.get(\"usancePeriod\") = " + extendedProperties.get("usancePeriod").toString()
            BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
            println usancePeriod
            println calculators.getMonthsOf(usancePeriod)
            if (usancePeriod == null) {
                usancePeriod = BigDecimal.ONE;
            }
            println ">>>>>>>>>>>>> (usancePeriod/30) = ${(usancePeriod / 30)}"
            // monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
            monthsCommitmentFee = (usancePeriod / 30) < 1 ? 1 : (usancePeriod / 30)
            // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
        }

        if ((commitmentFeeMonths > 0) && (monthsCommitmentFee > 0)) {
            monthsCommitmentFee = commitmentFeeMonths / 30
        }

        println "monthsCommitmentFee: " + monthsCommitmentFee
        // println "monthsEtsToExpiry: " + (daysEtsToExpiry/30)
        println "monthsIssueDateToExpiry: " + (daysIssueDateToExpiry/30)

        println "basePHP: " + basePHP

        // parameterized factors
        // BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(monthsEtsToExpiry).divide(bankCommissionDenominator,12,BigDecimal.ROUND_HALF_UP)
        // BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysEtsToExpiry/30).divide(bankCommissionDenominator,12,BigDecimal.ROUND_HALF_UP)
        BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysIssueDateToExpiry/30).divide(bankCommissionDenominator,12,BigDecimal.ROUND_HALF_UP)
        println "bankCommissionFactor: " + bankCommissionFactor

        println "bankCommissionDays: " + bankCommissionDays
        if ((bankCommissionDays > 0) && (daysIssueDateToExpiry > 0)) {
            if(bankCommissionDays < 30) {
                bankCommissionDays = 30
            }
            bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(bankCommissionDays / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
            println "bankCommissionFactor: " + bankCommissionFactor
        }

        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
        println "commitmentFeeFactor: " + commitmentFeeFactor

        BigDecimal suppliesFeeDefault = 50

        // charges
        BigDecimal bankCommission = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, bankCommissionFactor, 1000)
        BigDecimal commitmentFee = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, commitmentFeeFactor, 500)
        commitmentFee = "CASH".equalsIgnoreCase(documentSubType1) ? 0 : commitmentFee
        BigDecimal suppliesFee = suppliesFeeDefault

        BigDecimal bankCommissionnocwtAmount = bankCommission
        BigDecimal commitmentFeenocwtAmount = commitmentFee

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommission = cwtPercentage.multiply(bankCommission)
            commitmentFee = cwtPercentage.multiply(commitmentFee)
        }

        return [
                BC: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                CF: commitmentFee.setScale(2, BigDecimal.ROUND_UP),
                SUP: suppliesFee.setScale(2, BigDecimal.ROUND_UP),
                BCoriginal: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                CForiginal: commitmentFee.setScale(2, BigDecimal.ROUND_UP),
                SUPoriginal: suppliesFee.setScale(2, BigDecimal.ROUND_UP),
                BCnocwtAmount: bankCommissionnocwtAmount.setScale(2, BigDecimal.ROUND_UP),
                CFnocwtAmount: commitmentFeenocwtAmount.setScale(2, BigDecimal.ROUND_UP)
        ]
    }

    public Map computeNegotiation(Map productDetails) {

        // precompute for the base variables
        precomputeBaseDMLC(productDetails);

        //parameters

        String documentType = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentType", "String") ?: "FOREIGN"
        String documentSubType1 = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentSubType1", "String")
        String documentSubType2 = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentSubType2", "String")

        String remittanceFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "remittanceFlag", "String") ?: "N"
        String cableFeeFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cableFeeFlag", "String") ?: "N"
        BigDecimal forFirst = productDetails.extendedProperties.forFirst == null || productDetails.extendedProperties.forFirst == ''  ? null : new BigDecimal(productDetails.extendedProperties.forFirst)
        BigDecimal forNext = productDetails.extendedProperties.forNext == null ||  productDetails.extendedProperties.forNext == '' ? null : new BigDecimal(productDetails.extendedProperties.forNext)
        BigDecimal forFirstAmount = productDetails.extendedProperties.forFirstAmount == null ||  productDetails.extendedProperties.forFirstAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forFirstAmount)
        BigDecimal forNextAmount = productDetails.extendedProperties.forNextAmount == null ||  productDetails.extendedProperties.forNextAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forNextAmount)

        println "remittanceFlag: " + remittanceFlag
        println "cableFeeFlag: " + cableFeeFlag

        BigDecimal cableFeeDefault = 500
        BigDecimal remittanceFeeDefault = 18

        BigDecimal docStamps = BigDecimal.ZERO
        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
        BigDecimal succeedingBaseAmount = productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT)
        BigDecimal succeedingRateAmount = productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT)
        Calculators calculators = new Calculators()
        if (((BigDecimal) getBaseVariable("totalTrAmount")).compareTo(BigDecimal.ZERO) == 1) {
            docStamps = calculators.firstSucceedingFixed((BigDecimal) getBaseVariable("totalTrAmountInPHP"), forFirst ?: baseAmount, forFirstAmount ?: rateAmount, forNext ?: succeedingBaseAmount, forNextAmount ?: succeedingRateAmount)
        }

        BigDecimal cableFee = "Y".equalsIgnoreCase(cableFeeFlag) ? cableFeeDefault : 0.00
        BigDecimal remittanceFee = "Y".equalsIgnoreCase(remittanceFlag) ? currencyConverter.convert("URR", "USD", remittanceFeeDefault, "PHP") : 0.00

        if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            cableFee = 0
            docStamps = 0
            remittanceFee = 0
        }

        // charges
        println "Documentary Stamps: " + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "Remittance Fee : " + remittanceFee.setScale(2, BigDecimal.ROUND_UP)
        println "Cable Fee: " + cableFee.setScale(2, BigDecimal.ROUND_UP)

        return [
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                REMITTANCE: remittanceFee.setScale(2, BigDecimal.ROUND_UP),
                CABLE: cableFee.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStamps.setScale(2, BigDecimal.ROUND_UP),
                REMITTANCEoriginal: remittanceFee.setScale(2, BigDecimal.ROUND_UP),
                CABLEoriginal: cableFee.setScale(2, BigDecimal.ROUND_UP)
        ]
    }

    public Map computeAmendment(Map productDetails) {

        // precompute for the base variables
        precomputeBaseDMLC(productDetails);

        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal") ?: 0.01

        BigDecimal commitmentFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal commitmentFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeDenominator"), "BigDecimal") ?: 4
        BigDecimal commitmentFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeePercentage"), "BigDecimal") ?: 0.01

        // String etsDate =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String amendmentDate =(String)ChargesCalculator.convertToProperClass(extendedProperties.get("amendmentDate"), "String")
        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():" + getBaseVariable("chargeSettlementCurrency")?.toString()

        Calculators calculators = new Calculators()

        // println "etsDate: " + etsDate
        println "amendmentDate: " + amendmentDate
        println "expiryDate: " + expiryDate

        // BigDecimal basePHP = (BigDecimal)getBaseVariable("chargesBasePHP")
        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")  // As per Ma'am Letty, 4/12/2013
        println "basePHP: " + basePHP

        // parameterized factors

        // charges
        String amountSwitch = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("amountSwitch"), "String")
        String lcAmountFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("lcAmountFlag"), "String")
        BigDecimal amount = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("amount"), "BigDecimal")
        BigDecimal amountTo = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("amountTo"), "BigDecimal")

        String expiryDateSwitch = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateSwitch"), "String")
        String expiryDateFlagDisplay = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateFlagDisplay"), "String")

        String tenorSwitch = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("tenorSwitch"), "String")
        String originalTenor = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("originalTenor"), "String")
        String tenorTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("tenorTo"), "String")

        String narrativeSwitch = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("narrativeSwitch"), "String")
        String narrative = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("narrative"), "String")

        BigDecimal bankCommComputed = BigDecimal.ZERO
        BigDecimal commitFeeComputed = BigDecimal.ZERO
        BigDecimal supFeeComputed = BigDecimal.ZERO

        BigDecimal bankCommFixed = BigDecimal.ZERO
        BigDecimal commitFeeFixed = BigDecimal.ZERO
        BigDecimal supFeeFixed = BigDecimal.ZERO

        if (tenorSwitch != null && tenorSwitch.equalsIgnoreCase("on")) {

            if ((originalTenor != null && originalTenor.equalsIgnoreCase("SIGHT")) && (tenorTo != null && tenorTo.equalsIgnoreCase("USANCE"))) {

                println "\n============= Change SIGHT to USANCE"

                BigDecimal monthsCommitmentFee = 0

                if ("CASH".equalsIgnoreCase(documentSubType1)) {
                    println "============= CASH"
                    // monthsCommitmentFee = 0
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
                } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
                    println "============= STANDBY"
                    // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
                    // Integer daysEtsToExpiry = (calculators.getDaysTill(etsDate,expiryDate) < 30) ? 30 : calculators.getDaysTill(etsDate,expiryDate)
                    Integer daysAmendmentDateToExpiry = (calculators.getDaysTill(amendmentDate, expiryDate) < 30) ? 30 : calculators.getDaysTill(amendmentDate, expiryDate)
                    // monthsCommitmentFee = monthsEtsToExpiry.compareTo(BigDecimal.ONE)!=1?1:monthsEtsToExpiry
                    // monthsCommitmentFee = (daysEtsToExpiry.compareTo(30) != 1) ? 1 : (daysEtsToExpiry/30)
                    monthsCommitmentFee = (daysAmendmentDateToExpiry.compareTo(30) != 1) ? 1 : (daysAmendmentDateToExpiry / 30)
                    // println "monthsEtsToExpiry: " + (daysEtsToExpiry/30)
                    println "monthsAmendmentDateToExpiry: " + (daysAmendmentDateToExpiry / 30)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR SIGHT"
                    //monthsCommitmentFee = 1
                    println "extendedProperties.get(\"usancePeriodTo\") = " + extendedProperties.get("usancePeriodTo").toString()
                    BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriodTo"), "BigDecimal") ?: 30
                    println usancePeriod
                    println calculators.getMonthsOf(usancePeriod)
                    if (usancePeriod == null) {
                        usancePeriod = 30;
                    }
                    println ">>>>>>>>>>>>> (usancePeriod/30) = ${(usancePeriod / 30)}"
                    // monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
                    monthsCommitmentFee = (usancePeriod / 30) < 1 ? 1 : (usancePeriod / 30)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR USANCE"
                    println "extendedProperties.get(\"usancePeriodTo\") = " + extendedProperties.get("usancePeriodTo").toString()
                    BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriodTo"), "BigDecimal") ?: 30
                    println usancePeriod
                    println calculators.getMonthsOf(usancePeriod)
                    if (usancePeriod == null) {
                        usancePeriod = 30;
                    }
                    println ">>>>>>>>>>>>> (usancePeriod/30) = ${(usancePeriod / 30)}"
                    // monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
                    monthsCommitmentFee = (usancePeriod / 30) < 1 ? 1 : (usancePeriod / 30)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                }

                println "monthsCommitmentFee: " + monthsCommitmentFee

                // Computed
                BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
                commitFeeComputed = commitFeeComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, commitmentFeeFactor, 500))
                println "************ commitFeeComputed = ${commitFeeComputed}"

                // Fixed
                bankCommFixed = 500
                supFeeFixed = 50
            }
        }

        // As per Sir Jing, 6/21/2013
        Boolean isSpecialRule = Boolean.FALSE
        if ((amountSwitch != null && amountSwitch.equalsIgnoreCase("on")) &&
            (expiryDateSwitch != null && expiryDateSwitch.equalsIgnoreCase("on"))) {

            if( lcAmountFlag != null && lcAmountFlag.equalsIgnoreCase("INC") &&
                    expiryDateFlagDisplay != null && expiryDateFlagDisplay.equalsIgnoreCase("RED")){

                println "\n============= INCREASE Amount AND EARLY EXPIRY"

                // Need to do below to compute for the increase

                BigDecimal productAmount = (BigDecimal) productDetails.get("productAmount")
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> productAmount = ${productAmount}"
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amount = ${amount}"
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amountTo = ${amountTo}"
                BigDecimal diff = amountTo.subtract(amount)
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> diff = ${diff}"
                productDetails.put("productAmount", diff)

                precomputeBaseDMLC(productDetails);

                // BigDecimal basePhpTo = (BigDecimal)getBaseVariable("chargesBasePHP")
                BigDecimal basePhpTo = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")

                println "############ basePhpTo = ${basePhpTo}"


                String expiryDateTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateTo"), "String")
                println ">>>>>>>>>>>>>>>>>> expiryDateTo = ${expiryDateTo}"

                // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
                // Integer daysEtsToExpiry = (calculators.getDaysTill(etsDate,expiryDate) < 30) ? 30 : calculators.getDaysTill(etsDate,expiryDate)
                Integer daysAmendmentDateToExpiry = (calculators.getDaysTill(amendmentDate, expiryDateTo) < 30) ? 30 : calculators.getDaysTill(amendmentDate, expiryDateTo)
                // println "'calculators.getDaysTill(etsDate,expiryDate):" +calculators.getDaysTill(etsDate,expiryDate)
                println "'calculators.getDaysTill(amendmentDate,expiryDateTo): " + calculators.getDaysTill(amendmentDate, expiryDateTo)


                // Computed
                // BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysEtsToExpiry/30).divide(bankCommissionDenominator,12,BigDecimal.ROUND_HALF_UP)
                BigDecimal bankCommissionFactor // = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysAmendmentDateToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
                if(documentSubType1.equalsIgnoreCase("REGULAR") && documentSubType2.equalsIgnoreCase("USANCE")){
                    BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
                    bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(usancePeriod/30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
                } else {
                    bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysAmendmentDateToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
                }
                println "bankCommissionFactor: " + bankCommissionFactor
                bankCommComputed = bankCommComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, bankCommissionFactor, 500))
                println "************ bankCommComputed = ${bankCommComputed}"

                BigDecimal monthsCommitmentFee = 0

                if ("CASH".equalsIgnoreCase(documentSubType1)) {
                    println "============= CASH"
                    // monthsCommitmentFee = 0
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
                } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
                    println "============= STANDBY"
                    // monthsCommitmentFee = monthsEtsToExpiry.compareTo(BigDecimal.ONE)!=1?1:monthsEtsToExpiry
                    // monthsCommitmentFee = (daysEtsToExpiry.compareTo(30) != 1) ? 1 : (daysEtsToExpiry/30)
                    monthsCommitmentFee = (daysAmendmentDateToExpiry.compareTo(30) != 1) ? 1 : (daysAmendmentDateToExpiry / 30)
                    // println "monthsEtsToExpiry: " + (daysEtsToExpiry/30)
                    println "monthsAmendmentDateToExpiry: " + (daysAmendmentDateToExpiry / 30)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR SIGHT"
                    monthsCommitmentFee = 1
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR USANCE"
                    println "extendedProperties.get(\"usancePeriod\") = " + extendedProperties.get("usancePeriod").toString()
                    BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
                    println usancePeriod
                    println calculators.getMonthsOf(usancePeriod)
                    if (usancePeriod == null) {
                        usancePeriod = BigDecimal.ONE;
                    }
                    println ">>>>>>>>>>>>> (usancePeriod/30) = ${(usancePeriod / 30)}"
                    // monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
                    monthsCommitmentFee = (usancePeriod / 30) < 1 ? 1 : (usancePeriod / 30)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                }

                println "monthsCommitmentFee: " + monthsCommitmentFee

                // Computed
                if (monthsCommitmentFee > 0) {
                    BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
                    commitFeeComputed = commitFeeComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, commitmentFeeFactor, 500))
                    if ("CASH".equalsIgnoreCase(documentSubType1)) {
                        commitFeeComputed = BigDecimal.ZERO;
                    }
                }

                println "************ commitFeeComputed = ${commitFeeComputed}"

                // Fixed
                supFeeFixed = 50

                // Revert back to the original productAmount
                productDetails.put("productAmount", productAmount)
                precomputeBaseDMLC(productDetails);

            } else if ((lcAmountFlag != null && lcAmountFlag.equalsIgnoreCase("INC")) &&
                (expiryDateFlagDisplay != null && expiryDateFlagDisplay.equalsIgnoreCase("EXT"))) {

                isSpecialRule = Boolean.TRUE

                println "\n============= INCREASE Amount and EXTEND Expiry Date"

                // Need to do below to compute for the increase

                BigDecimal productAmount = (BigDecimal) productDetails.get("productAmount")
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> productAmount = ${productAmount}"
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amount = ${amount}"
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amountTo = ${amountTo}"
                BigDecimal diff = amountTo.subtract(amount);
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> diff = ${diff}"
                productDetails.put("productAmount", diff)

                precomputeBaseDMLC(productDetails);

                BigDecimal basePhpTo = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")

                println "############ basePhpTo = ${basePhpTo}"
                println "############ basePHP = ${basePHP}"

                String expiryDateTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateTo"), "String")
                println ">>>>>>>>>>>>>>>>>> expiryDateTo = ${expiryDateTo}"

                // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
                // Integer daysEtsToExpiry = (calculators.getDaysTill(etsDate,expiryDate) < 30) ? 30 : calculators.getDaysTill(etsDate,expiryDate)
                Integer daysAmendmentDateToNewExpiry = (calculators.getDaysTill(amendmentDate, expiryDateTo) < 30) ? 30 : calculators.getDaysTill(amendmentDate, expiryDateTo)
                // println "'calculators.getDaysTill(etsDate,expiryDate):" +calculators.getDaysTill(etsDate,expiryDate)
                println "calculators.getDaysTill(amendmentDate, expiryDateTo): " + calculators.getDaysTill(amendmentDate, expiryDateTo)

                Integer daysExpiryDateToNewExpiry = (calculators.getDaysTill(expiryDate, expiryDateTo) < 30) ? 30 : calculators.getDaysTill(expiryDate, expiryDateTo)
                println "calculators.getDaysTill(expiryDate, expiryDateTo): " + calculators.getDaysTill(expiryDate, expiryDateTo)


                // Computed
                // BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysEtsToExpiry/30).divide(bankCommissionDenominator,12,BigDecimal.ROUND_HALF_UP)
                BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysAmendmentDateToNewExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
                println "bankCommissionFactor: " + bankCommissionFactor
                bankCommComputed = bankCommComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, bankCommissionFactor, 500))
                println "************ bankCommComputed INCREASE = ${bankCommComputed}"

                bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysExpiryDateToNewExpiry/ 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
                println "bankCommissionFactor: " + bankCommissionFactor
                bankCommComputed = bankCommComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, bankCommissionFactor, 500))
                println "************ bankCommComputed EXTEND = ${bankCommComputed}"



                BigDecimal monthsCommitmentFee = 0
                BigDecimal monthsCommitmentFeeExpiryToNewExpiry = 0

                if ("CASH".equalsIgnoreCase(documentSubType1)) {
                    println "============= CASH"
                    monthsCommitmentFeeExpiryToNewExpiry = (daysExpiryDateToNewExpiry.compareTo(30) != 1) ? 1 : (daysExpiryDateToNewExpiry/30)
                    // monthsCommitmentFee = 0
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
                } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
                    println "============= STANDBY"
                    // monthsCommitmentFee = monthsEtsToExpiry.compareTo(BigDecimal.ONE)!=1?1:monthsEtsToExpiry
                    // monthsCommitmentFee = (daysEtsToExpiry.compareTo(30) != 1) ? 1 : (daysEtsToExpiry/30)
                    monthsCommitmentFee = (daysAmendmentDateToNewExpiry.compareTo(30) != 1) ? 1 : (daysAmendmentDateToNewExpiry/30)
                    monthsCommitmentFeeExpiryToNewExpiry = (daysExpiryDateToNewExpiry.compareTo(30) != 1) ? 1 : (daysExpiryDateToNewExpiry/30)
                    // println "monthsEtsToExpiry: " + (daysEtsToExpiry/30)
                    println "monthsAmendmentDateToNewExpiry: " + (daysAmendmentDateToNewExpiry/30)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR SIGHT"
                    monthsCommitmentFee = 1
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR USANCE"
                    println "extendedProperties.get(\"usancePeriod\") = " + extendedProperties.get("usancePeriod").toString()
                    BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
                    println ">>>>>>>>>>>>> usancePeriod = ${usancePeriod}"
                    println calculators.getMonthsOf(usancePeriod)
                    if (usancePeriod == null) {
                        usancePeriod = BigDecimal.ONE;
                    }
                    println ">>>>>>>>>>>>> (usancePeriod/30) = ${(usancePeriod / 30)}"
                    // monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
                    monthsCommitmentFee = (usancePeriod / 30) < 1 ? 1 : (usancePeriod / 30)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                }

                println "monthsCommitmentFee: " + monthsCommitmentFee

                // Computed
                if (monthsCommitmentFee > 0) {
                    BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
                    commitFeeComputed = commitFeeComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, commitmentFeeFactor, 500))
                    if ("CASH".equalsIgnoreCase(documentSubType1)) {
                        commitFeeComputed = BigDecimal.ZERO;
                    }
                    if(monthsCommitmentFeeExpiryToNewExpiry > 0){
                        BigDecimal commitmentFeeFactor2 = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFeeExpiryToNewExpiry).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
                        commitFeeComputed = commitFeeComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, commitmentFeeFactor2, 500))
                    }
                }

                println "************ commitFeeComputed = ${commitFeeComputed}"

                // Fixed
                supFeeFixed = 50

                // Revert back to the original productAmount
                productDetails.put("productAmount", productAmount)
                precomputeBaseDMLC(productDetails);
            }
        }

        if (!isSpecialRule) {
            println "SIR JINGS RULE IS NOT FOLLOWED"

            if (amountSwitch != null && amountSwitch.equalsIgnoreCase("on")) {
                println "SIRJINGS AMOUNTSWITCH"

                if( lcAmountFlag != null && lcAmountFlag.equalsIgnoreCase("INC") &&
                        expiryDateFlagDisplay != null && expiryDateFlagDisplay.equalsIgnoreCase("RED")){

//                    println "\n============= INCREASE Amount AND EARLY EXPIRY"
//
//                    // Need to do below to compute for the increase
//
//                    BigDecimal productAmount = (BigDecimal) productDetails.get("productAmount")
//                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> productAmount = ${productAmount}"
//                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amount = ${amount}"
//                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amountTo = ${amountTo}"
//                    BigDecimal diff = amountTo.subtract(amount)
//                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> diff = ${diff}"
//                    productDetails.put("productAmount", diff)
//
//                    precomputeBaseDMLC(productDetails);
//
//                    // BigDecimal basePhpTo = (BigDecimal)getBaseVariable("chargesBasePHP")
//                    BigDecimal basePhpTo = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
//
//                    println "############ basePhpTo = ${basePhpTo}"
//
//
//                    String expiryDateTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateTo"), "String")
//                    println ">>>>>>>>>>>>>>>>>> expiryDateTo = ${expiryDateTo}"
//
//                    // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
//                    // Integer daysEtsToExpiry = (calculators.getDaysTill(etsDate,expiryDate) < 30) ? 30 : calculators.getDaysTill(etsDate,expiryDate)
//                    Integer daysAmendmentDateToExpiry = (calculators.getDaysTill(amendmentDate, expiryDateTo) < 30) ? 30 : calculators.getDaysTill(amendmentDate, expiryDate)
//                    // println "'calculators.getDaysTill(etsDate,expiryDate):" +calculators.getDaysTill(etsDate,expiryDate)
//                    println "'calculators.getDaysTill(amendmentDate,expiryDate): " + calculators.getDaysTill(amendmentDate, expiryDate)
//
//                    // Computed
//                    // BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysEtsToExpiry/30).divide(bankCommissionDenominator,12,BigDecimal.ROUND_HALF_UP)
//                    BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysAmendmentDateToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
//                    println "bankCommissionFactor: " + bankCommissionFactor
//                    bankCommComputed = bankCommComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, bankCommissionFactor, 500))
//                    println "************ bankCommComputed = ${bankCommComputed}"
//
//                    BigDecimal monthsCommitmentFee = 0
//
//                    if ("CASH".equalsIgnoreCase(documentSubType1)) {
//                        println "============= CASH"
//                        // monthsCommitmentFee = 0
//                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
//                    } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
//                        println "============= STANDBY"
//                        // monthsCommitmentFee = monthsEtsToExpiry.compareTo(BigDecimal.ONE)!=1?1:monthsEtsToExpiry
//                        // monthsCommitmentFee = (daysEtsToExpiry.compareTo(30) != 1) ? 1 : (daysEtsToExpiry/30)
//                        monthsCommitmentFee = (daysAmendmentDateToExpiry.compareTo(30) != 1) ? 1 : (daysAmendmentDateToExpiry / 30)
//                        // println "monthsEtsToExpiry: " + (daysEtsToExpiry/30)
//                        println "monthsAmendmentDateToExpiry: " + (daysAmendmentDateToExpiry / 30)
//                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
//                    } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
//                        println "============= REGULAR SIGHT"
//                        monthsCommitmentFee = 1
//                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
//                    } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
//                        println "============= REGULAR USANCE"
//                        println "extendedProperties.get(\"usancePeriod\") = " + extendedProperties.get("usancePeriod").toString()
//                        BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
//                        println usancePeriod
//                        println calculators.getMonthsOf(usancePeriod)
//                        if (usancePeriod == null) {
//                            usancePeriod = BigDecimal.ONE;
//                        }
//                        println ">>>>>>>>>>>>> (usancePeriod/30) = ${(usancePeriod / 30)}"
//                        // monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
//                        monthsCommitmentFee = (usancePeriod / 30) < 1 ? 1 : (usancePeriod / 30)
//                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
//                    }
//
//                    println "monthsCommitmentFee: " + monthsCommitmentFee
//
//                    // Computed
//                    if (monthsCommitmentFee > 0) {
//                        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
//                        commitFeeComputed = commitFeeComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, commitmentFeeFactor, 500))
//                        if ("CASH".equalsIgnoreCase(documentSubType1)) {
//                            commitFeeComputed = BigDecimal.ZERO;
//                        }
//                    }
//
//                    println "************ commitFeeComputed = ${commitFeeComputed}"
//
//                    // Fixed
//                    supFeeFixed = 50

                    // Revert back to the original productAmount
//                    productDetails.put("productAmount", productAmount)
//                    precomputeBaseDMLC(productDetails);

                }else if (lcAmountFlag != null && lcAmountFlag.equalsIgnoreCase("INC")) {

                    println "\n============= INCREASE Amount"

                    // Need to do below to compute for the increase

                    BigDecimal productAmount = (BigDecimal) productDetails.get("productAmount")
                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> productAmount = ${productAmount}"
                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amount = ${amount}"
                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amountTo = ${amountTo}"
                    BigDecimal diff = amountTo.subtract(amount)
                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> diff = ${diff}"
                    productDetails.put("productAmount", diff)

                    precomputeBaseDMLC(productDetails);

                    // BigDecimal basePhpTo = (BigDecimal)getBaseVariable("chargesBasePHP")
                    BigDecimal basePhpTo = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")

                    println "############ basePhpTo = ${basePhpTo}"

                    // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
                    // Integer daysEtsToExpiry = (calculators.getDaysTill(etsDate,expiryDate) < 30) ? 30 : calculators.getDaysTill(etsDate,expiryDate)
                    Integer daysAmendmentDateToExpiry = (calculators.getDaysTill(amendmentDate, expiryDate) < 30) ? 30 : calculators.getDaysTill(amendmentDate, expiryDate)
                    // println "'calculators.getDaysTill(etsDate,expiryDate):" +calculators.getDaysTill(etsDate,expiryDate)
                    println "'calculators.getDaysTill(amendmentDate,expiryDate): " + calculators.getDaysTill(amendmentDate, expiryDate)

                    // Computed
                    // BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysEtsToExpiry/30).divide(bankCommissionDenominator,12,BigDecimal.ROUND_HALF_UP)
                    BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(daysAmendmentDateToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
                    println "bankCommissionFactor: " + bankCommissionFactor
                    bankCommComputed = bankCommComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, bankCommissionFactor, 500))
                    println "************ bankCommComputed = ${bankCommComputed}"

                    BigDecimal monthsCommitmentFee = 0

                    if ("CASH".equalsIgnoreCase(documentSubType1)) {
                        println "============= CASH"
                        // monthsCommitmentFee = 0
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
                    } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
                        println "============= STANDBY"
                        // monthsCommitmentFee = monthsEtsToExpiry.compareTo(BigDecimal.ONE)!=1?1:monthsEtsToExpiry
                        // monthsCommitmentFee = (daysEtsToExpiry.compareTo(30) != 1) ? 1 : (daysEtsToExpiry/30)
                        monthsCommitmentFee = (daysAmendmentDateToExpiry.compareTo(30) != 1) ? 1 : (daysAmendmentDateToExpiry / 30)
                        // println "monthsEtsToExpiry: " + (daysEtsToExpiry/30)
                        println "monthsAmendmentDateToExpiry: " + (daysAmendmentDateToExpiry / 30)
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                    } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
                        println "============= REGULAR SIGHT"
                        monthsCommitmentFee = 1
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                    } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
                        println "============= REGULAR USANCE"
                        println "extendedProperties.get(\"usancePeriod\") = " + extendedProperties.get("usancePeriod").toString()
                        BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
                        println usancePeriod
                        println calculators.getMonthsOf(usancePeriod)
                        if (usancePeriod == null) {
                            usancePeriod = BigDecimal.ONE;
                        }
                        println ">>>>>>>>>>>>> (usancePeriod/30) = ${(usancePeriod / 30)}"
                        // monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
                        monthsCommitmentFee = (usancePeriod / 30) < 1 ? 1 : (usancePeriod / 30)
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                    }

                    println "monthsCommitmentFee: " + monthsCommitmentFee

                    // Computed
                    if (monthsCommitmentFee > 0) {
                        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
                        commitFeeComputed = commitFeeComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, commitmentFeeFactor, 500))
                        if ("CASH".equalsIgnoreCase(documentSubType1)) {
                            commitFeeComputed = BigDecimal.ZERO;
                        }
                    }

                    println "************ commitFeeComputed = ${commitFeeComputed}"

                    // Fixed
                    supFeeFixed = 50

                    // Revert back to the original productAmount
                    productDetails.put("productAmount", productAmount)
                    precomputeBaseDMLC(productDetails);

                } else if (lcAmountFlag != null && lcAmountFlag.equalsIgnoreCase("DEC")) {

                    println "\n============= DECREASE Amount"

                    // Fixed
                    bankCommFixed = 500
                    supFeeFixed = 50
                }
            }

            if (expiryDateSwitch != null && expiryDateSwitch.equalsIgnoreCase("on")) {
                println "SIRJINGS EXPIRYSWITCH"

                if (expiryDateFlagDisplay != null && expiryDateFlagDisplay.equalsIgnoreCase("EXT")
                && lcAmountFlag != null && lcAmountFlag.equalsIgnoreCase("DEC")) {

                    println "\n============= DECREASE amount EXTEND Expiry Date"

                    // Need to do below to compute for the increase
                    BigDecimal productAmount = (BigDecimal) productDetails.get("productAmount")
                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> productAmount = ${productAmount}"
                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amount = ${amount}"
                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> amountTo = ${amountTo}"
                    BigDecimal diff = amount.subtract(amountTo)
//                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> diff = ${diff}"
                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> use amountTo = ${amountTo}"
                    productDetails.put("productAmount", amountTo)

                    precomputeBaseDMLC(productDetails);

                    BigDecimal basePhpTo = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")

                    println "############ basePhpTo = ${basePhpTo}"


                    String expiryDateTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateTo"), "String")
                    String originalExpiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("originalExpiryDate"), "String")
                    println ">>>>>>>>>>>>>>>>>> expiryDateTo = ${expiryDateTo}"
                    println ">>>>>>>>>>>>>>>>>> originalExpiryDate = ${originalExpiryDate}"

                    // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
                    Integer newDaysEtsToExpiry = (calculators.getDaysTill(originalExpiryDate, expiryDateTo) < 30) ? 30 : calculators.getDaysTill(originalExpiryDate, expiryDateTo)
                    BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(newDaysEtsToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
                    println "bankCommissionFactor: " + bankCommissionFactor

                    // Computed
                    bankCommComputed = bankCommComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, bankCommissionFactor, 500))
                    println "************ bankCommComputed = ${bankCommComputed}"

                    BigDecimal monthsCommitmentFee = 0

                    if ("CASH".equalsIgnoreCase(documentSubType1)) {
                        println "============= CASH"
                        // monthsCommitmentFee = 0
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
                    } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
                        println "============= STANDBY"
                        // monthsCommitmentFee = monthsEtsToExpiry.compareTo(BigDecimal.ONE)!=1?1:monthsEtsToExpiry
                        monthsCommitmentFee = (newDaysEtsToExpiry.compareTo(30) != 1) ? 1 : (newDaysEtsToExpiry / 30)
                        println "monthsEtsToExpiry: " + (newDaysEtsToExpiry / 30)
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                    } else if ("REGULAR".equalsIgnoreCase(documentSubType1)) {
                        println "============= REGULAR"
                        monthsCommitmentFee = 0
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
/*
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR SIGHT"
                    monthsCommitmentFee = 1
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR USANCE"
                    println "extendedProperties.get(\"usancePeriod\")"+extendedProperties.get("usancePeriod").toString()
                    BigDecimal usancePeriod = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal")?:30
                    println usancePeriod
                    println calculators.getMonthsOf(usancePeriod)
                    monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
*/
                    }

                    println "monthsCommitmentFee: " + monthsCommitmentFee

                    // Computed
                    if (monthsCommitmentFee > 0) {
                        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
                        commitFeeComputed = commitFeeComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePhpTo, 0, 0, commitmentFeeFactor, 500))
                        println "************ commitFeeComputed = ${commitFeeComputed}"
                    }
                    // Fixed
                    supFeeFixed = 50


                    // Revert back to the original productAmount
                    productDetails.put("productAmount", productAmount)
                    precomputeBaseDMLC(productDetails);

                } else if (expiryDateFlagDisplay != null && expiryDateFlagDisplay.equalsIgnoreCase("EXT") ) {

                    println "\n============= EXTEND Expiry Date"
                    println "\n============= EXTEND Expiry Date BASE AMOUNT:" + basePHP

                    String expiryDateTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateTo"), "String")
                    String originalExpiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("originalExpiryDate"), "String")
                    println ">>>>>>>>>>>>>>>>>> expiryDateTo = ${expiryDateTo}"
                    println ">>>>>>>>>>>>>>>>>> originalExpiryDate = ${originalExpiryDate}"

                    // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
                    Integer newDaysEtsToExpiry = (calculators.getDaysTill(originalExpiryDate, expiryDateTo) < 30) ? 30 : calculators.getDaysTill(originalExpiryDate, expiryDateTo)
                    BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(newDaysEtsToExpiry / 30).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
                    println "bankCommissionFactor: " + bankCommissionFactor


                    println "************ BEFORE EXPIRY bankCommComputed = ${bankCommComputed}"
                    // Computed
                    if(bankCommComputed.compareTo(new BigDecimal("500"))){
                        bankCommComputed = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, bankCommissionFactor, 500)
                    } else {
                        bankCommComputed = bankCommComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, bankCommissionFactor, 500))
                    }
                    println "************ AFTER EXPIRY bankCommComputed = ${bankCommComputed}"

                    BigDecimal monthsCommitmentFee = 0

                    if ("CASH".equalsIgnoreCase(documentSubType1)) {
                        println "============= CASH"
                        // monthsCommitmentFee = 0
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
                    } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
                        println "============= STANDBY"
                        // monthsCommitmentFee = monthsEtsToExpiry.compareTo(BigDecimal.ONE)!=1?1:monthsEtsToExpiry
                        monthsCommitmentFee = (newDaysEtsToExpiry.compareTo(30) != 1) ? 1 : (newDaysEtsToExpiry / 30)
                        println "monthsEtsToExpiry: " + (newDaysEtsToExpiry / 30)
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                    } else if ("REGULAR".equalsIgnoreCase(documentSubType1)) {
                        println "============= REGULAR"
                        monthsCommitmentFee = 0
                        // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
/*
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR SIGHT"
                    monthsCommitmentFee = 1
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
                } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
                    println "============= REGULAR USANCE"
                    println "extendedProperties.get(\"usancePeriod\")"+extendedProperties.get("usancePeriod").toString()
                    BigDecimal usancePeriod = (BigDecimal)ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal")?:30
                    println usancePeriod
                    println calculators.getMonthsOf(usancePeriod)
                    monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)
                    // basePHP = (BigDecimal)getBaseVariable("chargesBaseSellRatePHP")
*/
                    }

                    println "monthsCommitmentFee: " + monthsCommitmentFee

                    // Computed
                    if (monthsCommitmentFee > 0) {
                        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
                        commitFeeComputed = commitFeeComputed.add(calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, commitmentFeeFactor, 500))
                        println "************ commitFeeComputed = ${commitFeeComputed}"
                    }
                    // Fixed
                    supFeeFixed = 50

                } else if (expiryDateFlagDisplay != null && expiryDateFlagDisplay.equalsIgnoreCase("RED")) {

                    println "\n============= REDUCE Expiry Date"

                    // Fixed
                    bankCommFixed = 500
                    supFeeFixed = 50
                }
            }
        }

        if (narrativeSwitch != null && narrativeSwitch.equalsIgnoreCase("on")) {
            if (narrative != null && !narrative.isEmpty()) {
                bankCommFixed = 500
                supFeeFixed = 50
            }
        }

        BigDecimal bankCommission = bankCommComputed
        BigDecimal commitmentFee = commitFeeComputed
        BigDecimal suppliesFee = supFeeComputed
        if (bankCommFixed.compareTo(bankCommission) > 0) {
            bankCommission = bankCommFixed
        }
        if (commitFeeFixed.compareTo(commitmentFee) > 0) {
            commitmentFee = commitFeeFixed
        }
        if (supFeeFixed.compareTo(suppliesFee) > 0) {
            suppliesFee = supFeeFixed
        }

        BigDecimal bankCommissionnocwtAmount = bankCommission
        BigDecimal commitmentFeenocwtAmount = commitmentFee

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommission = cwtPercentage.multiply(bankCommission)
            commitmentFee = cwtPercentage.multiply(commitmentFee)
        }

        return [
                BC: bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP),
                CF: commitmentFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                SUP: suppliesFee.setScale(2, BigDecimal.ROUND_HALF_UP),
                BCnocwtAmount:bankCommissionnocwtAmount.setScale(2, BigDecimal.ROUND_HALF_UP),
                CFnocwtAmount:commitmentFeenocwtAmount.setScale(2, BigDecimal.ROUND_HALF_UP)
        ]
    }

    public Map computeUaLoanMaturityAdjustment(Map productDetails) {

        // precompute for the base variables
        precomputeBaseDMLC(productDetails);

        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())

        BigDecimal commitmentFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal commitmentFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeDenominator"), "BigDecimal") ?: 4
        BigDecimal commitmentFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeePercentage"), "BigDecimal") ?: 0.01
        println "commitmentFeeNumerator:"+commitmentFeeNumerator
        println "commitmentFeeDenominator:"+commitmentFeeDenominator
        println "commitmentFeePercentage:"+commitmentFeePercentage

        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        // String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String negotiationValueDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("negotiationValueDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        String loanMaturityDateFrom = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("loanMaturityDateFrom"), "String")
        String loanMaturityDateTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("loanMaturityDateTo"), "String")

        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():" + getBaseVariable("chargeSettlementCurrency")?.toString()

        Calculators calculators = new Calculators()

        // println "etsDate: " + etsDate
        println "negotiationValueDate: " + negotiationValueDate
        println "expiryDate: " + expiryDate
        println "loanMaturityDateFrom: " + loanMaturityDateFrom
        println "loanMaturityDateTo: " + loanMaturityDateTo

        // Assumes that this will only be called during UA Loan Maturity Adjustment

        // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
//        Integer daysEtsToExpiry = (calculators.getDaysTill(loanMaturityDateFrom, loanMaturityDateTo) < 30) ? 30 : calculators.getDaysTill(loanMaturityDateFrom, loanMaturityDateTo)
        Integer daysNegotiationValueDateToExpiry = (calculators.getDaysTill(loanMaturityDateFrom, loanMaturityDateTo) < 30) ? 30 : calculators.getDaysTill(loanMaturityDateFrom, loanMaturityDateTo)
//        Integer daysNegotiationValueDateToExpiry = (calculators.getDaysTill(negotiationValueDate, loanMaturityDateTo) < 30) ? 30 : calculators.getDaysTill(negotiationValueDate, loanMaturityDateTo)
        BigDecimal monthsCommitmentFee = (daysNegotiationValueDateToExpiry.compareTo(30) != 1) ? 1 : (daysNegotiationValueDateToExpiry / 30)

        // BigDecimal basePHP = (BigDecimal)getBaseVariable("chargesBasePHP")
        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")  // As per Ma'am Letty, 4/12/2013

        println "monthsCommitmentFee: " + monthsCommitmentFee
        println "monthsNegotiationValueDateToExpiry: " + (daysNegotiationValueDateToExpiry / 30)

        println "basePHP: " + basePHP

        // parameterized factors
        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
        println "commitmentFeeFactor: " + commitmentFeeFactor

        // charges
        BigDecimal commitmentFee = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, commitmentFeeFactor, 500)
        BigDecimal bankCommission = BigDecimal.ZERO
        BigDecimal suppliesFee = BigDecimal.ZERO
        BigDecimal commitmentFeenocwtAmount = commitmentFee
        BigDecimal bankCommissionnocwtAmount = bankCommission

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            commitmentFee = cwtPercentage.multiply(commitmentFee)
            bankCommission = cwtPercentage.multiply(bankCommission)
        }

        return [
                BC: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                CF: commitmentFee.setScale(2, BigDecimal.ROUND_UP),
                SUP: suppliesFee.setScale(2, BigDecimal.ROUND_UP),
                BCoriginal: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                CForiginal: commitmentFee.setScale(2, BigDecimal.ROUND_UP),
                SUPoriginal: suppliesFee.setScale(2, BigDecimal.ROUND_UP),
                BCnocwtAmount: bankCommissionnocwtAmount.setScale(2, BigDecimal.ROUND_UP),
                CFnocwtAmount: commitmentFeenocwtAmount.setScale(2, BigDecimal.ROUND_UP)
        ]
    }

    public Map computeUaLoanSettlement(Map productDetails) {

        // precompute for the base variables
        precomputeBaseDMLC(productDetails);

        //parameters

        String documentType = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentType", "String") ?: "FOREIGN"
        String documentSubType1 = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentSubType1", "String")
        String documentSubType2 = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentSubType2", "String")

        String remittanceFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "remittanceFlag", "String") ?: "N"
        String cableFeeFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cableFeeFlag", "String") ?: "N"

        println "remittanceFlag: " + remittanceFlag
        println "cableFeeFlag: " + cableFeeFlag

        BigDecimal cableFeeDefault = 500
        BigDecimal remittanceFeeDefault = 18

        BigDecimal docStamps = BigDecimal.ZERO

        Calculators calculators = new Calculators()
        if (((BigDecimal) getBaseVariable("totalTrAmountInPHP")).compareTo(BigDecimal.ZERO) == 1 && productDetails.chargesParameter != [:]) {
            BigDecimal forFirst = productDetails.extendedProperties.forFirst == null || productDetails.extendedProperties.forFirst == ''  ? null : new BigDecimal(productDetails.extendedProperties.forFirst)
            BigDecimal forNext = productDetails.extendedProperties.forNext == null ||  productDetails.extendedProperties.forNext == '' ? null : new BigDecimal(productDetails.extendedProperties.forNext)
            BigDecimal forFirstAmount = productDetails.extendedProperties.forFirstAmount == null ||  productDetails.extendedProperties.forFirstAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forFirstAmount)
            BigDecimal forNextAmount = productDetails.extendedProperties.forNextAmount == null ||  productDetails.extendedProperties.forNextAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forNextAmount)
            BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
            BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
            BigDecimal succeedingBaseAmount = productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT)
            BigDecimal succeedingRateAmount = productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT)
            //docStamps = calculators.firstSucceedingFixed((BigDecimal) getBaseVariable("totalNotSettledByTRinPHP"), 5000, 20, 5000, 10)
            docStamps = calculators.firstSucceedingFixed((BigDecimal) getBaseVariable("totalTrAmountInPHP"), forFirst ?: baseAmount, forFirstAmount ?: rateAmount, forNext ?: succeedingBaseAmount, forNextAmount ?: succeedingRateAmount)
        }




        BigDecimal cableFee = BigDecimal.ZERO
        BigDecimal remittanceFee = BigDecimal.ZERO
        if (cableFeeFlag != null && !cableFeeFlag.trim().isEmpty()) {
            cableFee = "Y".equalsIgnoreCase(cableFeeFlag) ? cableFeeDefault : 0.00
        }
        if (remittanceFlag != null && !remittanceFlag.trim().isEmpty()) {
            remittanceFee = "Y".equalsIgnoreCase(remittanceFlag) ? currencyConverter.convert("URR", "USD", remittanceFeeDefault, "PHP") : 0.00
        }

        // charges
        println "Documentary Stamps: " + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "Remittance Fee : " + remittanceFee.setScale(2, BigDecimal.ROUND_UP)
        println "Cable Fee: " + cableFee.setScale(2, BigDecimal.ROUND_UP)

        return [
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                REMITTANCE: remittanceFee.setScale(2, BigDecimal.ROUND_UP),
                CABLE: cableFee.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStamps.setScale(2, BigDecimal.ROUND_UP),
                REMITTANCEoriginal: remittanceFee.setScale(2, BigDecimal.ROUND_UP),
                CABLEoriginal: cableFee.setScale(2, BigDecimal.ROUND_UP)
        ]
    }
}
