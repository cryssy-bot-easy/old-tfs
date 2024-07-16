package com.ucpb.tfs2.application.util

import org.jfree.date.SerialDate
import org.jfree.date.SerialDateUtilities
import org.joda.time.DateTime
import org.joda.time.Days

import java.text.DateFormat
import java.text.SimpleDateFormat

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
 *  Details: Added documentStampsOrig for computations and added parameters used on computing docstamps.
 *  Date revised: 04/13/2018
 */
class FXLCChargesCalculator extends ChargesCalculator {
	
	/*	 PROLOGUE:
		 (revision)
		 SCR/ER Number: 20151104-021
		 SCR/ER Description: Wrong computation of charges on the FXLC Charges.
		 [Revised by:] Gerard De Las Armas
		 [Date revised:] 07/22/2015
		 Program [Revision] Details: Changed the value of variable computedBoth from amountFrom to outstandingBalance in function getBankCommissionAmendmentExtensionWierd().
		 Date deployment:
		 Member Type: GROOVY
		 Project: CORE
		 Project Name: FXLCChargesCalculator.groovy
	 */

    public Map computeOpening(Map productDetails) {

        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal") ?: 0.01

        BigDecimal commitmentFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal commitmentFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeDenominator"), "BigDecimal") ?: 4
        BigDecimal commitmentFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeePercentage"), "BigDecimal") ?: 0.01

        BigDecimal confirmingFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmingFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal confirmingFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmingFeeDenominator"), "BigDecimal") ?: 8
        BigDecimal confirmingFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmingFeePercentage"), "BigDecimal") ?: 0.01

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

        //String confirmingFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "confirmingFlag", "String") ?: "N"
        //String advisingFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "advisingFlag", "String") ?: "N"
        String advanceCorresChargesFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("advanceCorresChargesFlag"), "String")
        println "advanceCorresChargesFlag advanceCorresChargesFlag advanceCorresChargesFlag :" + advanceCorresChargesFlag
        String confirmationInstructionsFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmationInstructionsFlag"), "String")
        println "confirmationInstructionsFlag confirmationInstructionsFlag confirmationInstructionsFlag :" + confirmationInstructionsFlag

        BigDecimal bankCommissionDays = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionMonths"), "BigDecimal") ?: 0
        BigDecimal commitmentFeeMonths = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeMonths"), "BigDecimal") ?: 0
        BigDecimal confirmingFeeMonths = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmingFeeMonths"), "BigDecimal") ?: 0

        BigDecimal centavos = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("centavos"), "BigDecimal") ?: 0.3

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

        BigDecimal monthsCommitmentFee = 0
        monthsCommitmentFee.setScale(12)

        println "commitmentFeeDenominator: " + commitmentFeeDenominator
        println "commitmentFeeNumerator: " + commitmentFeeNumerator
        println "commitmentFeePercentage: " + commitmentFeePercentage

        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")
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
            println "monthsCommitmentFee:" + monthsCommitmentFee
            println "daysCommitmentFee:" + daysCommitmentFee
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
            if (!chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
                basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
            }
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            println "REGULAR SIGHT"
            monthsCommitmentFee = 0
            daysCommitmentFee = 0
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
            if (!chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
                basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
            }
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            println "REGULAR USANCE"
            println "extendedProperties.get(\"usancePeriod\")" + extendedProperties.get("usancePeriod").toString()
            BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
            println usancePeriod
            println calculators.getMonthsOf(usancePeriod)
            monthsCommitmentFee = calculators.getMonthsOf(usancePeriod) < 1 ? 1 : calculators.getMonthsOf(usancePeriod)

            daysCommitmentFee = usancePeriod < 30 ? 30 : usancePeriod
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
            if (!chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
                basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
            }
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
            bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).multiply(bankCommissionDays.divide(30, 12, BigDecimal.ROUND_FLOOR)).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
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
        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(((daysCommitmentFee).divide(30, 12, BigDecimal.ROUND_FLOOR))).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "commitmentFeeFactor:" + commitmentFeeFactor
        println "cilexDenominator: " + commitmentFeeDenominator
        println "cilexNumerator: " + cilexNumerator
        println "cilexPercentage: " + cilexPercentage
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "cilexFactor:" + cilexFactor
        BigDecimal suppliesFeeDefault = 50
        BigDecimal cableFeeDefault = 800
        BigDecimal advisingDefault = 50
        BigDecimal confirmingDefault = 50

        BigDecimal daysConfirmingFee = daysEtsToExpiry
        println "daysConfirmingFee before:" + daysConfirmingFee
        if (confirmingFeeMonths.compareTo(BigDecimal.ZERO) == 1) {
            if (confirmingFeeMonths.compareTo(new BigDecimal("30")) == 1) {
                daysConfirmingFee = confirmingFeeMonths
            } else {
                daysConfirmingFee = 30
            }
        }
        println "daysConfirmingFee after:" + daysConfirmingFee

        BigDecimal confirmingFactor = confirmingFeePercentage.multiply(confirmingFeeNumerator).multiply(daysConfirmingFee.divide(30, 12, BigDecimal.ROUND_FLOOR)).divide(confirmingFeeDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "confirmingFactor:" + confirmingFactor

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

		BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
		BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
        BigDecimal docStamps = calculators.forEvery(basePHP, baseAmount, centavos ?: rateAmount)

        if ("CASH".equalsIgnoreCase(documentSubType1)) {
            commitmentFee = 0
        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
            cilex = 0
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            cilex = 0
            commitmentFee = 0
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            cilex = 0
        }


        BigDecimal advising = BigDecimal.ZERO
        if ("on".equalsIgnoreCase(advanceCorresChargesFlag) || "Y".equalsIgnoreCase(advanceCorresChargesFlag)) {
            advising = advisingDefault
            println "advising:" + advising
        }

        String useConfirmingMinimum = "N";
        BigDecimal confirming = BigDecimal.ZERO
        if (
                ("on".equalsIgnoreCase(advanceCorresChargesFlag) || "Y".equalsIgnoreCase(advanceCorresChargesFlag)) &&
                        ("on".equalsIgnoreCase(confirmationInstructionsFlag) || "Y".equalsIgnoreCase(confirmationInstructionsFlag))
        ) {


              String ratesBasisInternal = "URR"

            if ("CASH".equalsIgnoreCase(documentSubType1)) {
                println "CASH SIGHT"
                if (productSettlementPHPTotals > 0) {
                    ratesBasisInternal = "REG-SELL"
                } else {
                    ratesBasisInternal = "URR"
                }

            } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
                println "STANDBY"
                ratesBasisInternal = "REG-SELL"
            } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
                println "REGULAR SIGHT"
                ratesBasisInternal = "REG-SELL"
            } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
                println "REGULAR USANCE"
                ratesBasisInternal = "REG-SELL"
            }

            println "ratesBasisInternal:"+ratesBasisInternal
            BigDecimal minConfirming = currencyConverter.convert(ratesBasisInternal, "USD", confirmingDefault, "PHP").setScale(2, BigDecimal.ROUND_UP)
            println "minConfirming"+minConfirming
            confirming = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, confirmingFactor, minConfirming)
            println "confirming:" + confirming
            if(confirming.compareTo(minConfirming)<=0){
                useConfirmingMinimum = "Y";
            }
        }


        BigDecimal bankCommissionnocwtAmount = bankCommission
        BigDecimal commitmentFeenocwtAmount = commitmentFee
        BigDecimal cilexnocwtAmount = cilex




        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommission = cwtPercentage.multiply(bankCommission)
            commitmentFee = cwtPercentage.multiply(commitmentFee)
            cilex = cwtPercentage.multiply(cilex)
        }


        println "CHARGES ORIGINAL VALUE"
        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "charges commitment fee:" + commitmentFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges supplies fee:" + suppliesFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
        println "charges advising fee:" + advising.setScale(2, BigDecimal.ROUND_UP)
        println "charges confirming fee:" + confirming.setScale(2, BigDecimal.ROUND_UP)

        def bankCommissionOrig = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        def commitmentFeeOrig = commitmentFee.setScale(2, BigDecimal.ROUND_UP)
        def docStampsOrig = docStamps.setScale(2, BigDecimal.ROUND_UP)
        def cableFeeOrig = cableFee.setScale(2, BigDecimal.ROUND_UP)
        def suppliesFeeOrig = suppliesFee.setScale(2, BigDecimal.ROUND_UP)
        def cilexOrig = cilex.setScale(2, BigDecimal.ROUND_UP)
        def advisingOrig = advising.setScale(2, BigDecimal.ROUND_UP)
        def confirmingOrig = confirming.setScale(2, BigDecimal.ROUND_UP)


        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
        String ratesBasis
        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            ratesBasis = "URR"
            println "no conversion required except for cilex and advising"
            println "before conversion advising" + advising


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


            advising = currencyConverter.convert(ratesBasis, "USD", advising, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            println "after conversion advising:" + advising

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            commitmentFee = currencyConverter.convert(ratesBasis, "PHP", commitmentFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            suppliesFee = currencyConverter.convert(ratesBasis, "PHP", suppliesFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "PHP", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            confirming = currencyConverter.convert(ratesBasis, "PHP", confirming, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
//            if (advising < 50) {
//                advising = 50
//            }
            if (useConfirmingMinimum.equalsIgnoreCase("Y")) {
                confirming = 50
            }

        } else {
            ratesBasis = "URR"

            bankCommission = currencyConverter.convertWithPrecision(ratesBasis, "PHP", bankCommission, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            commitmentFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", commitmentFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            docStamps = currencyConverter.convertWithPrecision(ratesBasis, "PHP", docStamps, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cableFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cableFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            suppliesFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", suppliesFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cilex = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cilex, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            confirming = currencyConverter.convertWithPrecision(ratesBasis, "PHP", confirming, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)

//            if (advising < 50) {
//                advising = 50
//            }
            if (useConfirmingMinimum.equalsIgnoreCase("Y")) {
                confirming = 50
            }


            println "AFTER URR VALUE"

            ratesBasis = "REG-SELL"
            bankCommission = currencyConverter.convert(ratesBasis, "USD", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            commitmentFee = currencyConverter.convert(ratesBasis, "USD", commitmentFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            docStamps = currencyConverter.convert(ratesBasis, "USD", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "USD", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            suppliesFee = currencyConverter.convert(ratesBasis, "USD", suppliesFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            advising = currencyConverter.convert(ratesBasis, "USD", advising, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            confirming = currencyConverter.convert(ratesBasis, "USD", confirming, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)

        }



        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        println "charges commitment fee:" + commitmentFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges supplies fee:" + suppliesFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
        println "charges advising fee:" + advising.setScale(2, BigDecimal.ROUND_UP)
        println "charges confirming fee:" + confirming.setScale(2, BigDecimal.ROUND_UP)

        BigDecimal total = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        total = total + commitmentFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + docStamps.setScale(2, BigDecimal.ROUND_UP)
        total = total + cableFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + suppliesFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + cilex.setScale(2, BigDecimal.ROUND_UP)
        total = total + advising.setScale(2, BigDecimal.ROUND_UP)
        total = total + confirming.setScale(2, BigDecimal.ROUND_UP)


        return [
                'BC': bankCommission.setScale(2, BigDecimal.ROUND_UP),
                'CF': commitmentFee.setScale(2, BigDecimal.ROUND_UP),
                'DOCSTAMPS': docStamps.setScale(2, BigDecimal.ROUND_UP),
                'DOCSTAMPSoriginal': docStampsOrig,
                'CABLE': cableFee.setScale(2, BigDecimal.ROUND_UP),
                'SUP': suppliesFee.setScale(2, BigDecimal.ROUND_UP),
                'CILEX': cilex.setScale(2, BigDecimal.ROUND_UP),
                'CORRES-ADVISING': advising.setScale(2, BigDecimal.ROUND_UP),
                'CORRES-CONFIRMING': confirming.setScale(2, BigDecimal.ROUND_UP),
                'TOTAL': total.setScale(2, BigDecimal.ROUND_UP),
                'BCoriginal': bankCommissionOrig,
                'CForiginal': commitmentFeeOrig,
                'DOCSTAMPSoriginal': docStampsOrig,
                'CABLEoriginal': cableFeeOrig,
                'SUPoriginal': suppliesFeeOrig,
                'CILEXoriginal': cilexOrig,
                'CORRES-ADVISINGoriginal': advisingOrig,
                'CORRES-CONFIRMINGoriginal': confirmingOrig,
                'BCnocwtAmount':bankCommissionnocwtAmount,
                'CFnocwtAmount':commitmentFeenocwtAmount,
                'CILEXnocwtAmount':cilexnocwtAmount
        ]

    }

    public Map computeNegotiation(Map productDetails) {

        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexNumerator"), "BigDecimal") ?: 1
        BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexDenominator"), "BigDecimal") ?: 4
        BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexPercentage"), "BigDecimal") ?: 0.01

        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98
        BigDecimal originalDocstampsAmount = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("originalDocstampsAmount"), "BigDecimal") ?: 0

        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        BigDecimal overdrawnAmount = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("overdrawnAmount"), "BigDecimal") ?: 0

        println "documentSubType1:" + documentSubType1
        println "documentSubType2:" + documentSubType2


        Calculators calculators = new Calculators()
        println "etsDate:" + etsDate
        println "expiryDate:" + expiryDate
        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")


        if ("CASH".equalsIgnoreCase(documentSubType1)) {
            basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        }

        println "basePHP:" + basePHP

        // parameterized factors
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "cilexFactor:" + cilexFactor
        BigDecimal cableFeeDefault = 500
        BigDecimal notarialDefault = 50

        // charges
        BigDecimal notarial = notarialDefault
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

        println "minimumCilex :" + minimumCilex
        println "cilex:" + cilex

        if (settledInForeignInUSD > 0) {//minimum cilex must only be checked if there is a settlement in foreign currency otherwise use zero
            if (cilex < minimumCilex) {
                cilex = minimumCilex
            }
        }
        println "cilex:" + cilex


        BigDecimal totalTrAmountInPHP = (BigDecimal) getBaseVariable("totalTrAmountInPHP")
        println "totalTrAmountInPHP:" + totalTrAmountInPHP

        BigDecimal forFirst = productDetails.extendedProperties.forFirst == null || productDetails.extendedProperties.forFirst == ''  ? null : new BigDecimal(productDetails.extendedProperties.forFirst)
        BigDecimal forNext = productDetails.extendedProperties.forNext == null ||  productDetails.extendedProperties.forNext == '' ? null : new BigDecimal(productDetails.extendedProperties.forNext)
        BigDecimal forFirstAmount = productDetails.extendedProperties.forFirstAmount == null ||  productDetails.extendedProperties.forFirstAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forFirstAmount)
        BigDecimal forNextAmount = productDetails.extendedProperties.forNextAmount == null ||  productDetails.extendedProperties.forNextAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forNextAmount)
        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
        BigDecimal succeedingBaseAmount = productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT)
        BigDecimal succeedingRateAmount = productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT)
        BigDecimal docStamps = calculators.firstSucceedingFixed(totalTrAmountInPHP, forFirst ?: baseAmount, forFirstAmount ?: rateAmount, forNext ?: succeedingBaseAmount, forNextAmount ?: succeedingRateAmount)
        docStamps = docStamps.subtract(originalDocstampsAmount)

        BigDecimal cilexnocwtAmount = cilex
        if ("Y".equalsIgnoreCase(cwtFlag)) {
            cilex = cwtPercentage.multiply(cilex)
        }
        if ("CASH".equalsIgnoreCase(documentSubType1)) {
            //cilex = 0
            docStamps = 0
        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
            notarial = 0
            //docStamps = 0
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            println "regular sight has all the charges!"
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            cilex = 0
            docStamps = 0
        }



        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "CHARGES PESO VALUE"
        println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_UP)
        println "charges notarial fee:" + notarial.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)

        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency

        def docStampsoriginal = docStamps.setScale(2, BigDecimal.ROUND_UP)
        def cableFeeoriginal = cableFee.setScale(2, BigDecimal.ROUND_UP)
        def notarialoriginal = notarial.setScale(2, BigDecimal.ROUND_UP)
        def cilexoriginal = cilex.setScale(2, BigDecimal.ROUND_UP)

        String ratesBasis = "URR"
        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            ratesBasis = "URR"
            println "no conversion required except for cilex"

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            notarial = currencyConverter.convert(ratesBasis, "PHP", notarial, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "PHP", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_FLOOR)

        } else {
            ratesBasis = "URR"

            docStamps = currencyConverter.convertWithPrecision(ratesBasis, "PHP", docStamps, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cableFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cableFee, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            notarial = currencyConverter.convertWithPrecision(ratesBasis, "PHP", notarial, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)
            cilex = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cilex, "USD", 12)//.setScale(2, BigDecimal.ROUND_FLOOR)

            ratesBasis = "REG-SELL"
            docStamps = currencyConverter.convert(ratesBasis, "USD", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "USD", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            notarial = currencyConverter.convert(ratesBasis, "USD", notarial, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)

        }

        println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_FLOOR)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_FLOOR)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_FLOOR)
        println "charges notarial:" + notarial.setScale(2, BigDecimal.ROUND_FLOOR)

        BigDecimal total = cilex.setScale(2, BigDecimal.ROUND_UP)
        total = total + docStamps.setScale(2, BigDecimal.ROUND_UP)
        total = total + cableFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + notarial.setScale(2, BigDecimal.ROUND_UP)


        return [
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStampsoriginal,
                CABLE: cableFee.setScale(2, BigDecimal.ROUND_UP),
                CILEX: cilex.setScale(2, BigDecimal.ROUND_UP),
                NOTARIAL: notarial.setScale(2, BigDecimal.ROUND_UP),
                TOTAL: total.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStampsoriginal.setScale(2, BigDecimal.ROUND_UP),
                CABLEoriginal: cableFeeoriginal.setScale(2, BigDecimal.ROUND_UP),
                CILEXoriginal: cilexoriginal.setScale(2, BigDecimal.ROUND_UP),
                NOTARIALoriginal: notarialoriginal.setScale(2, BigDecimal.ROUND_UP),
                CILEXnocwtAmount:cilexnocwtAmount
        ]

    }

    public Map computeUaLoanMaturityAdjustment(Map productDetails) {

        // precompute for the base variables
        precomputeBaseDMLC(productDetails);

        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())

        BigDecimal commitmentFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal commitmentFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeDenominator"), "BigDecimal") ?: 4
        BigDecimal commitmentFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeePercentage"), "BigDecimal") ?: 0.01

        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        String loanMaturityDateFrom = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("loanMaturityDateFrom"), "String")
        String loanMaturityDateTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("loanMaturityDateTo"), "String")

        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():" + getBaseVariable("chargeSettlementCurrency")?.toString()

        Calculators calculators = new Calculators()

        println "etsDate: " + etsDate
        println "expiryDate: " + expiryDate
        println "loanMaturityDateFrom: " + loanMaturityDateFrom
        println "loanMaturityDateTo: " + loanMaturityDateTo

        println "commitmentFeeDenominator: " + commitmentFeeDenominator
        println "commitmentFeeNumerator: " + commitmentFeeNumerator
        println "commitmentFeePercentage: " + commitmentFeePercentage

        // Assumes that this will only be called during UA Loan Maturity Adjustment

        // BigDecimal monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
        Integer daysEtsToExpiry = (calculators.getDaysTill(loanMaturityDateFrom, loanMaturityDateTo) < 30) ? 30 : calculators.getDaysTill(loanMaturityDateFrom, loanMaturityDateTo)
        println "calculators.getDaysTill(loanMaturityDateFrom, loanMaturityDateTo):"+calculators.getDaysTill(loanMaturityDateFrom, loanMaturityDateTo)
        println "daysEtsToExpiry:"+daysEtsToExpiry
        BigDecimal monthsCommitmentFee = (daysEtsToExpiry.compareTo(30) != 1) ? 1 : (daysEtsToExpiry / 30)

        // BigDecimal basePHP = (BigDecimal)getBaseVariable("chargesBasePHP")
        // BigDecimal basePHP = (BigDecimal)getBaseVariable("chargesBaseUrrPHP")
        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")  // As per Ma'am Letty, 4/12/2013

        println "monthsCommitmentFee: " + monthsCommitmentFee
        println "monthsEtsToExpiry: " + (daysEtsToExpiry / 30)

        println "basePHP: " + basePHP

        // parameterized factors
        BigDecimal commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).multiply(monthsCommitmentFee).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP)
        println "commitmentFeeFactor: " + commitmentFeeFactor

        // charges
        BigDecimal commitmentFee = calculators.firstSucceedingPercentageWithMinimum(basePHP, 0, 0, commitmentFeeFactor, 500)
        BigDecimal bankCommission = BigDecimal.ZERO
        BigDecimal cableFee = new BigDecimal("500")


        BigDecimal commitmentFeenocwtAmount = commitmentFee
        BigDecimal bankCommissionnocwtAmount = bankCommission

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            commitmentFee = cwtPercentage.multiply(commitmentFee)
            bankCommission = cwtPercentage.multiply(bankCommission)
        }

        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "CHARGES ORIGINAL VALUE"
//        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_HALF_UP)
//        println "charges commitment fee:" + commitmentFee.setScale(2, BigDecimal.ROUND_HALF_UP)
//        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_HALF_UP)

        BigDecimal commitmentFeeoriginal = commitmentFee.setScale(2, BigDecimal.ROUND_UP)
        BigDecimal bankCommissionoriginal = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        BigDecimal cableFeeoriginal = cableFee.setScale(2, BigDecimal.ROUND_UP)

        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
        String ratesBasis = "URR"
        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            ratesBasis = "URR"
            println "PHP: no conversion required "

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_UP)
            commitmentFee = currencyConverter.convert(ratesBasis, "PHP", commitmentFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)

        } else {
            ratesBasis = "URR"

            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, "USD").setScale(2, BigDecimal.ROUND_FLOOR)
            commitmentFee = currencyConverter.convert(ratesBasis, "PHP", commitmentFee, "USD").setScale(2, BigDecimal.ROUND_FLOOR)
            cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, "USD").setScale(2, BigDecimal.ROUND_FLOOR)


            ratesBasis = "REG-SELL"
            bankCommission = currencyConverter.convert(ratesBasis, "USD", bankCommission, chargeSettlementCurrency.trim().toUpperCase()).setScale(2, BigDecimal.ROUND_FLOOR)
            commitmentFee = currencyConverter.convert(ratesBasis, "USD", commitmentFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_FLOOR)
            cableFee = currencyConverter.convert(ratesBasis, "USD", cableFee, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_FLOOR)

        }



        println "charges bank commission:" + bankCommission.setScale(2, BigDecimal.ROUND_FLOOR)
        println "charges commitment fee:" + commitmentFee.setScale(2, BigDecimal.ROUND_FLOOR)
        println "charges cable fee:" + cableFee.setScale(2, BigDecimal.ROUND_FLOOR)

        BigDecimal total = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        total = total + commitmentFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + cableFee.setScale(2, BigDecimal.ROUND_UP)



        return [
                BC: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                CF: commitmentFee.setScale(2, BigDecimal.ROUND_UP),
                CABLE: cableFee.setScale(2, BigDecimal.ROUND_UP),
                TOTAL: total.setScale(2, BigDecimal.ROUND_UP),
                BCoriginal: bankCommissionoriginal.setScale(2, BigDecimal.ROUND_UP),
                CForiginal: commitmentFeeoriginal.setScale(2, BigDecimal.ROUND_UP),
                CABLEoriginal: cableFeeoriginal.setScale(2, BigDecimal.ROUND_UP),
                BCnocwtAmount: bankCommissionnocwtAmount,
                CFnocwtAmount: commitmentFeenocwtAmount
        ]
    }

    public Map computeUaLoanSettlement(Map productDetails) {

        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexNumerator"), "BigDecimal") ?: 1
        BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexDenominator"), "BigDecimal") ?: 4
        BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexPercentage"), "BigDecimal") ?: 0.01

        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        String confirmingFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "confirmingFlag", "String") ?: "N"
        String advisingFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "advisingFlag", "String") ?: "N"

        BigDecimal bankCommissionMonths = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionMonths"), "BigDecimal") ?: 0
        BigDecimal commitmentFeeMonths = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeMonths"), "BigDecimal") ?: 0

        println "documentSubType1:" + documentSubType1
        println "documentSubType2:" + documentSubType2
        println "confirmingFlag:" + confirmingFlag
        println "advisingFlag:" + advisingFlag

        Calculators calculators = new Calculators()
        println "etsDate:" + etsDate
        println "expiryDate:" + expiryDate
        BigDecimal monthsEtsToExpiry = 0
        monthsEtsToExpiry.setScale(12)
        monthsEtsToExpiry = calculators.getMonthsTill(etsDate, expiryDate) < 1 ? 1 : calculators.getMonthsTill(etsDate, expiryDate)
        BigDecimal daysEtsToExpiry = calculators.getDaysTillA(etsDate, expiryDate)
        println "daysEtsToExpiry:" + daysEtsToExpiry
        daysEtsToExpiry = daysEtsToExpiry < 30 ? 30 : daysEtsToExpiry


        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")


        if ("CASH".equalsIgnoreCase(documentSubType1)) {
            if (getBaseVariable("productSettlementThirdTotals") == 0 && getBaseVariable("productSettlementPHPTotals") == 0) {
                //all usd
                basePHP = (BigDecimal) getBaseVariable("chargesBaseUrrPHP")
            } else if (getBaseVariable("productSettlementThirdTotals") == 0 && getBaseVariable("productSettlementUSDTotals") == 0) {
                basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
            }
            println "THIS IS THE BASE:::" + basePHP

        } else if ("STANDBY".equalsIgnoreCase(documentSubType1)) {
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            println "extendedProperties.get(\"usancePeriod\")" + extendedProperties.get("usancePeriod").toString()
            BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriod"), "BigDecimal") ?: 30
            println usancePeriod
            println calculators.getMonthsOf(usancePeriod)
            basePHP = (BigDecimal) getBaseVariable("chargesBaseSellRatePHP")
        }
        println "basePHP:" + basePHP
        println "monthsEtsToExpiry:" + monthsEtsToExpiry

        // parameterized factors
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "cilexFactor:" + cilexFactor

        // charges
        BigDecimal settledInForeignInUSD = (BigDecimal) getBaseVariable("settledInForeignInUSD")
        println "settledInForeignInUSD:" + settledInForeignInUSD
        BigDecimal productSettlementPHPTotals = (BigDecimal) getBaseVariable("productSettlementPHPTotals")
        BigDecimal productSettlementUSDTotals = (BigDecimal) getBaseVariable("productSettlementUSDTotals")
        BigDecimal productSettlementThirdTotals = (BigDecimal) getBaseVariable("productSettlementThirdTotals")
        println "productSettlementPHPTotals:" + productSettlementPHPTotals
        BigDecimal cilex
        BigDecimal temp
        BigDecimal minimumCilex
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

        println "minimumCilex :" + minimumCilex
        println "cilex:" + cilex

        if (settledInForeignInUSD > 0) {
            //minimum cilex must only be checked if there is a settlement in foreign currency otherwise use zero
            if (cilex < minimumCilex) {
                cilex = minimumCilex
            }
        }
        println "cilex:" + cilex

        BigDecimal totalTrAmountInPHP = (BigDecimal) getBaseVariable("totalTrAmountInPHP")
        println "totalTrAmountInPHP:" + totalTrAmountInPHP

        BigDecimal forFirst = productDetails.extendedProperties.forFirst == null || productDetails.extendedProperties.forFirst == ''  ? null : new BigDecimal(productDetails.extendedProperties.forFirst)
        BigDecimal forNext = productDetails.extendedProperties.forNext == null ||  productDetails.extendedProperties.forNext == '' ? null : new BigDecimal(productDetails.extendedProperties.forNext)
        BigDecimal forFirstAmount = productDetails.extendedProperties.forFirstAmount == null ||  productDetails.extendedProperties.forFirstAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forFirstAmount)
        BigDecimal forNextAmount = productDetails.extendedProperties.forNextAmount == null ||  productDetails.extendedProperties.forNextAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forNextAmount)
        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
        BigDecimal succeedingBaseAmount = productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT)
        BigDecimal succeedingRateAmount = productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT)

        BigDecimal docStamps = calculators.firstSucceedingFixed(totalTrAmountInPHP, forFirst ?: baseAmount, forFirstAmount ?: rateAmount, forNext ?: succeedingBaseAmount, forNextAmount ?: succeedingRateAmount)



        BigDecimal cilexnocwtAmount = cilex

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            cilex = cwtPercentage.multiply(cilex)
        }


        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "CHARGES ORIGINAL VALUE"
        println "charges documentary stamps:" + docStamps
        println "charges cilex:" + cilex
        def docStampsoriginal = docStamps.setScale(2, BigDecimal.ROUND_UP)
        def cilexoriginal = cilex.setScale(2, BigDecimal.ROUND_UP)


        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
        String ratesBasis = "URR"
        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            println "no conversion required except for cilex "

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency)
            cilex = currencyConverter.convert(ratesBasis, "PHP", cilex, chargeSettlementCurrency)

        } else {
            ratesBasis = "URR"

            docStamps = currencyConverter.convertWithPrecision(ratesBasis, "PHP", docStamps, "USD", 12)
            cilex = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cilex, "USD", 12)


            ratesBasis = "REG-SELL"
            docStamps = currencyConverter.convert(ratesBasis, "USD", docStamps, chargeSettlementCurrency)
            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency)

        }



        println "charges documentary stamps:" + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)

        BigDecimal total = docStamps.setScale(2, BigDecimal.ROUND_UP)
        total = total + cilex.setScale(2, BigDecimal.ROUND_UP)


        return [
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                CILEX: cilex.setScale(2, BigDecimal.ROUND_UP),
                TOTAL: total.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStampsoriginal.setScale(2, BigDecimal.ROUND_UP),
                CILEXoriginal: cilexoriginal.setScale(2, BigDecimal.ROUND_UP),
                CILEXnocwtAmount: cilexnocwtAmount
        ]

    }

    public Map computeAmendment(Map productDetails) {
        println "FXLCChargesCalculator computeAmendment"
        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionNumerator"), "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionDenominator"), "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("bankCommissionPercentage"), "BigDecimal") ?: 0.01

        BigDecimal confirmingFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmingFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal confirmingFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmingFeeDenominator"), "BigDecimal") ?: 8
        BigDecimal confirmingFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmingFeePercentage"), "BigDecimal") ?: 0.01

        BigDecimal commitmentFeeNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeNumerator"), "BigDecimal") ?: 1
        BigDecimal commitmentFeeDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeeDenominator"), "BigDecimal") ?: 4
        BigDecimal commitmentFeePercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("commitmentFeePercentage"), "BigDecimal") ?: 0.01

        BigDecimal usancePeriod = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("usancePeriodTo"), "BigDecimal") ?: 0
        println "angol angol:"+usancePeriod

        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String amendmentDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("amendmentDate"), "String")
        String expiryDateTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateTo"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        String tenorCheck = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("tenorSwitch"), "String")
        String amountSwitch = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("amountSwitch"), "String")
        String lcAmountFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("lcAmountFlag"), "String")
        String expiryDateCheck = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateCheck"), "String")
        String expiryDateFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateFlag"), "String")
        String confirmationInstructionsFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmationInstructionsFlag"), "String")
        String originalConfirmationInstructionsFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("originalConfirmationInstructionsFlag"), "String")
        String confirmationInstructionsFlagTo = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("confirmationInstructionsFlagTo"), "String")
        String narrativesCheck = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("narrativesCheck"), "String")
        String issueDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("issueDate"), "String")
        String advanceCorresChargesFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("advanceCorresChargesFlag"), "String")
        println "advanceCorresChargesFlag advanceCorresChargesFlag advanceCorresChargesFlag :" + advanceCorresChargesFlag

        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98
        BigDecimal originalDocstampsAmount = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("originalDocstampsAmount"), "BigDecimal") ?: 0

        //BigDecimal amountFrom = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("amountFrom"), "BigDecimal") ?: 0.98
        BigDecimal amountFrom = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("amount"), "BigDecimal") ?: 0
        BigDecimal outstandingBalance = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("outstandingBalance"), "BigDecimal") ?: 0
        BigDecimal amountTo = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("amountTo"), "BigDecimal") ?: 0
        BigDecimal expiryDateModifiedDays = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDateModifiedDays"), "BigDecimal") ?: 0

        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")

        BigDecimal amountDifference = amountTo - amountFrom
        if (amountFrom > amountTo) {
            amountDifference = amountFrom - amountTo
        }


        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        if (chargeSettlementCurrency == null || chargeSettlementCurrency.equals("")) {
            chargeSettlementCurrency = "PHP" //Assume PHP
        }


        println "amount from:" + amountFrom
        println "amount to:" + amountTo
        println "amountDifference ORIG:" + amountDifference
        String currency = (String) productDetails.productCurrency

        if ("CASH".equalsIgnoreCase(documentSubType1)) {
            BigDecimal amountFromUsdBase = amountFrom
            if (!currency.equalsIgnoreCase("USD") && !currency.equalsIgnoreCase("PHP")) {
                amountFromUsdBase = currencyConverter.convertWithPrecision("REG-SELL", (String) productDetails.productCurrency ?: "", amountFrom, "USD", 12)
            }
            BigDecimal amountFromPhpBase = currencyConverter.convert("URR", "USD", amountFromUsdBase, "PHP")
            amountFrom = amountFromPhpBase

            println "amountFrom CASH:" + amountFrom
            BigDecimal amountToUsdBase = amountTo
            if (!currency.equalsIgnoreCase("USD") && !currency.equalsIgnoreCase("PHP")) {
                amountToUsdBase = currencyConverter.convertWithPrecision("REG-SELL", (String) productDetails.productCurrency ?: "", amountTo, "USD", 12)
            }
            BigDecimal amountToPhpBase = currencyConverter.convert("URR", "USD", amountToUsdBase, "PHP")
            amountTo = amountToPhpBase
            println "amountTo:" + amountTo


            BigDecimal amountDifferenceUsdBase = amountDifference
            if (!currency.equalsIgnoreCase("USD") && !currency.equalsIgnoreCase("PHP")) {
                amountDifferenceUsdBase = currencyConverter.convertWithPrecision("REG-SELL", (String) productDetails.productCurrency ?: "", amountDifference, "USD", 12)
            }
            BigDecimal amountDifferencePhpBase = currencyConverter.convert("URR", "USD", amountDifferenceUsdBase, "PHP")
            amountDifference = amountDifferencePhpBase
            println "amountDifference CASH:" + amountDifference

            println "outstandingBalance CASH:" + outstandingBalance
            BigDecimal outstandingBalanceUsdBase = outstandingBalance
            if (!currency.equalsIgnoreCase("USD") && !currency.equalsIgnoreCase("PHP")) {
                outstandingBalanceUsdBase = currencyConverter.convertWithPrecision("REG-SELL", (String) productDetails.productCurrency ?: "", outstandingBalance, "USD", 12)
            }
            BigDecimal outstandingBalancePhpBase = currencyConverter.convert("URR", "USD", outstandingBalanceUsdBase, "PHP")
            outstandingBalance = outstandingBalancePhpBase
            println "outstandingBalance:" + outstandingBalance

        } else {
            BigDecimal amountFromUsdBase = amountFrom
            if (!currency.equalsIgnoreCase("USD") && !currency.equalsIgnoreCase("PHP")) {
                amountFromUsdBase = currencyConverter.convertWithPrecision("REG-SELL", (String) productDetails.productCurrency ?: "", amountFrom, "USD", 12)
            }

            println "amountFromUsdBase CASH:" + amountFromUsdBase
            BigDecimal amountFromPhpBase = currencyConverter.convert("REG-SELL", "USD", amountFromUsdBase, "PHP")
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && !chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
                println "amountTo on CASH:" + amountTo
                println "amountFrom CASH:" + amountFrom
                amountFromPhpBase = currencyConverter.convert("URR", "USD", amountFromUsdBase, "PHP")
            }
            println "amountFromPhpBase CASH:" + amountFromPhpBase
            amountFrom = amountFromPhpBase

            println "amountFrom CASH:" + amountFrom

            BigDecimal amountToUsdBase = amountTo
            if (!currency.equalsIgnoreCase("USD") && !currency.equalsIgnoreCase("PHP")) {
                amountToUsdBase = currencyConverter.convertWithPrecision("REG-SELL", (String) productDetails.productCurrency ?: "", amountTo, "USD", 12)
            }
            println "amountToUsdBase CASH:" + amountToUsdBase
            BigDecimal amountToPhpBase = currencyConverter.convert("REG-SELL", "USD", amountToUsdBase, "PHP")
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && !chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
                println "amountTo on CASH:" + amountTo
                println "amountFrom CASH:" + amountFrom
                amountToPhpBase = currencyConverter.convert("URR", "USD", amountToUsdBase, "PHP")
            }
            println "amountToPhpBase CASH:" + amountToPhpBase
            amountTo = amountToPhpBase
            println "amountTo ELSE:" + amountTo

            println "amountDifference ELSE:" + amountDifference
            BigDecimal amountDifferenceUsdBase = amountDifference
            if (!currency.equalsIgnoreCase("USD") && !currency.equalsIgnoreCase("PHP")) {
                amountDifferenceUsdBase = currencyConverter.convertWithPrecision("REG-SELL", (String) productDetails.productCurrency ?: "", amountDifference, "USD", 12)
            }
            BigDecimal amountDifferencePhpBase = currencyConverter.convert("REG-SELL", "USD", amountDifferenceUsdBase, "PHP")
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && !chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
                println "amountTo on ELSE:" + amountTo
                println "amountFrom ELSE:" + amountFrom
                amountDifferencePhpBase = currencyConverter.convert("URR", "USD", amountDifferenceUsdBase, "PHP")
            }
            amountDifference = amountDifferencePhpBase
            println "amountDifference ELSE:" + amountDifference


            BigDecimal outstandingBalanceUsdBase = outstandingBalance
            if (!currency.equalsIgnoreCase("USD") && !currency.equalsIgnoreCase("PHP")) {
                outstandingBalanceUsdBase = currencyConverter.convertWithPrecision("REG-SELL", (String) productDetails.productCurrency ?: "", outstandingBalance, "USD", 12)
            }

            println "outstandingBalanceUsdBase CASH:" + outstandingBalanceUsdBase
            BigDecimal outstandingBalancePhpBase = currencyConverter.convert("REG-SELL", "USD", outstandingBalanceUsdBase, "PHP")
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && !chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
                println "outstandingBalance CASH:" + outstandingBalance
                outstandingBalancePhpBase = currencyConverter.convert("URR", "USD", outstandingBalanceUsdBase, "PHP")
            }
            println "outstandingBalancePhpBase CASH:" + outstandingBalancePhpBase
            outstandingBalance = outstandingBalancePhpBase

            println "outstandingBalance CASH:" + outstandingBalance

        }
        println "amountDifference AFTER:" + amountDifference

        println "amendmentDate:" + amendmentDate
        println "etsDate:" + etsDate
        println "expiryDate:" + expiryDate
        println "expiryDateTo:" + expiryDateTo
        println "expiryDateModifiedDays:" + expiryDateModifiedDays
        println "issueDate:" + issueDate

        println "amountFrom:" + amountFrom
        println "amountTo:" + amountTo
        println "amountDifference:" + amountDifference

        println "documentSubType1:" + documentSubType1
        println "documentSubType2:" + documentSubType2

        println "tenorCheck:" + tenorCheck
        println "amountSwitch:" + amountSwitch
        println "lcAmountFlag:" + lcAmountFlag
        println "expiryDateCheck:" + expiryDateCheck
        println "expiryDateFlag:" + expiryDateFlag
        println "confirmationInstructionsFlag:" + confirmationInstructionsFlag
        println "confirmationInstructionsFlagTo:" + confirmationInstructionsFlagTo
        println "originalConfirmationInstructionsFlag:" + originalConfirmationInstructionsFlag

        println "narrativesCheck:" + narrativesCheck

        Calculators calculators = new Calculators()
        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")

        // parameterized factors
        BigDecimal cableFeeDefault = 500
        BigDecimal advisingFeeDefault = 50
        BigDecimal confirmingFeeDefault = 50

        // charges
        BigDecimal bankCommission = getBankCommissionAmendmentExtensionWierd(tenorCheck, amountSwitch, lcAmountFlag,
                expiryDateCheck, expiryDateFlag, confirmationInstructionsFlag, narrativesCheck,
                bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amountFrom, amountTo,
                expiryDateModifiedDays, expiryDate, expiryDateTo, amendmentDate, cwtFlag, cwtPercentage, amountDifference, amendmentDate, outstandingBalance)

        BigDecimal commitmentFee = BigDecimal.ZERO
        if (("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) ||
                "STANDBY".equalsIgnoreCase(documentSubType1)
        ) {
            commitmentFee = getCommitmentFeeAmendmentExtensionWeird(tenorCheck, amountSwitch, lcAmountFlag,
                    expiryDateCheck, expiryDateFlag, confirmationInstructionsFlag, narrativesCheck,
                    commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage, amountFrom, amountTo,
                    expiryDateModifiedDays, expiryDate, expiryDateTo, amendmentDate, cwtFlag, cwtPercentage, amountDifference, amendmentDate, documentSubType1, documentSubType2, usancePeriod, outstandingBalance)
        } else if("on".equalsIgnoreCase(tenorCheck) &&("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2))){
            commitmentFee = getCommitmentFeeAmendmentExtensionWeird(tenorCheck, amountSwitch, lcAmountFlag,
                    expiryDateCheck, expiryDateFlag, confirmationInstructionsFlag, narrativesCheck,
                    commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage, amountFrom, amountTo,
                    expiryDateModifiedDays, expiryDate, expiryDateTo, amendmentDate, cwtFlag, cwtPercentage, amountDifference, amendmentDate, documentSubType1, documentSubType2, usancePeriod, outstandingBalance)
        }


        BigDecimal cableFee = cableFeeDefault // As long as there is an amendment there is going to be a cable fee for FX

        BigDecimal docStamps
        if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
            println "amountTo on:" + amountTo
            println "amountFrom:" + amountFrom

            BigDecimal centavos = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("centavos"), "BigDecimal")
            BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
            BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)

            docStamps = calculators.forEvery(amountTo.subtract(amountFrom), baseAmount, centavos ?: rateAmount)
        } else {
            println "amountTo off:" + amountTo
            println "amountFrom:" + amountFrom
            docStamps = 0
        }

        if (!"INC".equalsIgnoreCase(lcAmountFlag)) {
            docStamps = 0
        }



        BigDecimal advising = BigDecimal.ZERO
        println "advanceCorresChargesFlag:" + advanceCorresChargesFlag
        if ("on".equalsIgnoreCase(advanceCorresChargesFlag) || "Y".equalsIgnoreCase(advanceCorresChargesFlag)) {

            advising = advisingFeeDefault
            println "advising:" + advising

            if ("EXT".equalsIgnoreCase(expiryDateFlag) && "on".equalsIgnoreCase(expiryDateCheck) && !"INC".equalsIgnoreCase(lcAmountFlag) && !"on".equalsIgnoreCase(amountSwitch)) {
                advising = 0
            }

        }

        BigDecimal confirming = getConfirmingFeeAmendment(tenorCheck, amountSwitch, lcAmountFlag,
                expiryDateCheck, expiryDateFlag, confirmationInstructionsFlag, narrativesCheck,
                confirmingFeeNumerator, confirmingFeeDenominator, confirmingFeePercentage, amountFrom, amountTo,
                expiryDateModifiedDays, expiryDate, expiryDateTo, amendmentDate, cwtFlag, cwtPercentage, confirmingFeeDefault, amountDifference, issueDate,
                originalConfirmationInstructionsFlag, confirmationInstructionsFlagTo, amendmentDate
        )
        BigDecimal bankCommissionnocwtAmount = bankCommission
        BigDecimal commitmentFeenocwtAmount = commitmentFee

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommission = cwtPercentage.multiply(bankCommission)
            commitmentFee = cwtPercentage.multiply(commitmentFee)
        }


        println "CHARGES PESO VALUE"
        println "charges documentary stamps:" + docStamps
        println "charges cable fee:" + cableFee
        println "charges bank commission:" + bankCommission
        println "charges commitment fee:" + commitmentFee
        println "charges advising fee:" + advising
        println "charges confirming fee:" + confirming

        def docStampsoriginal = docStamps.setScale(2, BigDecimal.ROUND_UP)
        def cableFeeoriginal = cableFee.setScale(2, BigDecimal.ROUND_UP)
        def bankCommissionoriginal = bankCommission.setScale(2, BigDecimal.ROUND_UP)
        def commitmentFeeoriginal = commitmentFee.setScale(2, BigDecimal.ROUND_UP)
        def advisingoriginal = advising.setScale(2, BigDecimal.ROUND_UP)
        def confirmingoriginal = confirming.setScale(2, BigDecimal.ROUND_UP)

        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency
        String ratesBasis = "URR"
        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            ratesBasis = "REG-SELL"

            println "no conversion required except for advising:" + advising
            advising = currencyConverter.convert(ratesBasis, "USD", advising, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            println "after conversion advising:" + advising

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
            docStamps = currencyConverter.convert(ratesBasis, "PHP", docStamps, chargeSettlementCurrency)
            cableFee = currencyConverter.convert(ratesBasis, "PHP", cableFee, chargeSettlementCurrency)
            commitmentFee = currencyConverter.convert(ratesBasis, "PHP", commitmentFee, chargeSettlementCurrency)
            bankCommission = currencyConverter.convert(ratesBasis, "PHP", bankCommission, chargeSettlementCurrency)
            confirming = currencyConverter.convert(ratesBasis, "PHP", confirming, chargeSettlementCurrency)

        } else {
            ratesBasis = "URR"

            docStamps = currencyConverter.convertWithPrecision(ratesBasis, "PHP", docStamps, "USD", 12)
            cableFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", cableFee, "USD", 12)
            bankCommission = currencyConverter.convertWithPrecision(ratesBasis, "PHP", bankCommission, "USD", 12)
            commitmentFee = currencyConverter.convertWithPrecision(ratesBasis, "PHP", commitmentFee, "USD", 12)
            confirming = currencyConverter.convertWithPrecision(ratesBasis, "PHP", confirming, "USD", 12)

            ratesBasis = "REG-SELL"
            docStamps = currencyConverter.convert(ratesBasis, "USD", docStamps, chargeSettlementCurrency)
            cableFee = currencyConverter.convert(ratesBasis, "USD", cableFee, chargeSettlementCurrency)
            bankCommission = currencyConverter.convert(ratesBasis, "USD", bankCommission, chargeSettlementCurrency)
            commitmentFee = currencyConverter.convert(ratesBasis, "USD", commitmentFee, chargeSettlementCurrency)
            advising = currencyConverter.convert(ratesBasis, "USD", advising, chargeSettlementCurrency)
            confirming = currencyConverter.convert(ratesBasis, "USD", confirming, chargeSettlementCurrency)

        }

        println "charges documentary stamps:" + docStamps
        println "charges cable fee:" + cableFee
        println "charges bank commission:" + bankCommission
        println "charges commitment fee:" + commitmentFee
        println "charges advising fee:" + advising
        println "charges confirming fee:" + confirming

        BigDecimal total = 0
        total = total + docStamps.setScale(2, BigDecimal.ROUND_UP)
        total = total + cableFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + bankCommission.setScale(2, BigDecimal.ROUND_UP)
        total = total + commitmentFee.setScale(2, BigDecimal.ROUND_UP)
        total = total + advising.setScale(2, BigDecimal.ROUND_UP)
        total = total + confirming.setScale(2, BigDecimal.ROUND_UP)

        println "total:" + total

        return [
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStampsoriginal,
                CABLE: cableFee.setScale(2, BigDecimal.ROUND_UP),
                BC: bankCommission.setScale(2, BigDecimal.ROUND_UP),
                CF: commitmentFee.setScale(2, BigDecimal.ROUND_UP),
                'CORRES-ADVISING': advising.setScale(2, BigDecimal.ROUND_UP),
                'CORRES-CONFIRMING': confirming.setScale(2, BigDecimal.ROUND_UP),
                TOTAL: total.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStampsoriginal.setScale(2, BigDecimal.ROUND_UP),
                CABLEoriginal: cableFeeoriginal.setScale(2, BigDecimal.ROUND_UP),
                BCoriginal: bankCommissionoriginal.setScale(2, BigDecimal.ROUND_UP),
                CForiginal: commitmentFeeoriginal.setScale(2, BigDecimal.ROUND_UP),
                'CORRES-ADVISINGoriginal': advisingoriginal.setScale(2, BigDecimal.ROUND_UP),
                'CORRES-CONFIRMINGoriginal': confirmingoriginal.setScale(2, BigDecimal.ROUND_UP),
                BCnocwtAmount: bankCommissionnocwtAmount,
                CFnocwtAmount: commitmentFeenocwtAmount
        ]

    }

    public Map computeAdjustment(Map productDetails) {

        // precompute for the base variables
        precomputeBaseFXLC(productDetails);
        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
        //parameters
        BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexNumerator"), "BigDecimal") ?: 1
        BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexDenominator"), "BigDecimal") ?: 4
        BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cilexPercentage"), "BigDecimal") ?: 0.01

        String expiryDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("expiryDate"), "String")
        String etsDate = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("etsDate"), "String")
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98

        String documentSubType1 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType1"), "String")
        String documentSubType2 = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("documentSubType2"), "String")


        println "documentSubType1:" + documentSubType1
        println "documentSubType2:" + documentSubType2

        Calculators calculators = new Calculators()
        println "etsDate:" + etsDate
        println "expiryDate:" + expiryDate
        BigDecimal monthsEtsToExpiry = 0
        monthsEtsToExpiry.setScale(12)
        monthsEtsToExpiry = calculators.getMonthsTill(etsDate, expiryDate) < 1 ? 1 : calculators.getMonthsTill(etsDate, expiryDate)
        //monthsEtsToExpiry = calculators.getMonthsTill(etsDate,expiryDate)<1?1:calculators.getMonthsTill(etsDate,expiryDate)
        BigDecimal daysEtsToExpiry = calculators.getDaysTillA(etsDate, expiryDate)
        println "daysEtsToExpiry:" + daysEtsToExpiry
        daysEtsToExpiry = daysEtsToExpiry < 30 ? 30 : daysEtsToExpiry

        BigDecimal basePHP = (BigDecimal) getBaseVariable("chargesBasePHP")
        println "basePHP:" + basePHP
        println "monthsEtsToExpiry:" + monthsEtsToExpiry

        // parameterized factors
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "cilexFactor:" + cilexFactor

        // charges
        BigDecimal settledInForeignInUSD = (BigDecimal) getBaseVariable("settledInForeignInUSD")
        println "settledInForeignInUSD:" + settledInForeignInUSD
        BigDecimal cilex = calculators.percentageOf((BigDecimal) getBaseVariable("settledInForeignInUSD"), cilexFactor)
        println "cilex:" + cilex
        BigDecimal minimumCilex = 20
        println "minimumCilex :" + minimumCilex
        if (cilex < minimumCilex && cilex > 0) {
            cilex = minimumCilex
        }
        String ratesBasis = "URR"

        BigDecimal cilexnocwtAmount = currencyConverter.convert(ratesBasis, "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP)

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            cilex = cwtPercentage.multiply(cilex)
        }

        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "CHARGES ORIGINAL VALUE"
        println "charges cilex:" + cilex.setScale(2, BigDecimal.ROUND_UP)
        def cilexoriginal = cilex.setScale(2, BigDecimal.ROUND_UP)

        println "CHARGES OF THIS VALUE:" + chargeSettlementCurrency

        if (chargeSettlementCurrency.equalsIgnoreCase("PHP")) {
            ratesBasis = "URR"
            println "no conversion required except for cilex and advising"
            println "before conversion cilex" + cilex
            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP)
            ratesBasis = "REG-SELL"
            println "after conversion cilex:" + cilex

        } else if (chargeSettlementCurrency.equalsIgnoreCase("USD")) {
            ratesBasis = "URR"
        } else {
            ratesBasis = "REG-SELL"
            cilex = currencyConverter.convert(ratesBasis, "USD", cilex, chargeSettlementCurrency)
        }

        println "charges cilex:" + cilex

        return [
                CILEX: cilex.setScale(2, BigDecimal.ROUND_UP),
                TOTAL: cilex.setScale(2, BigDecimal.ROUND_UP),
                CILEXoriginal: cilexoriginal.setScale(2, BigDecimal.ROUND_UP),
                CILEXnocwtAmount: cilexnocwtAmount
        ]
    }

    public Map computeIndemnityIssuance(Map productDetails) {

        Map extendedProperties = extractExtendedProperties(productDetails.get("extendedProperties").toString())
//        //parameters
        String cwtFlag = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtFlag"), "String")
        String indemnityType = (String) ChargesCalculator.convertToProperClass(extendedProperties.get("indemnityType"), "String")
        BigDecimal cwtPercentage = (BigDecimal) ChargesCalculator.convertToProperClass(extendedProperties.get("cwtPercentage"), "BigDecimal") ?: 0.98
        BigDecimal bankCommission = 500

        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)

        BigDecimal docStamps = baseAmount

        BigDecimal bankCommissionnocwtAmount = bankCommission

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommission = cwtPercentage.multiply(bankCommission)
        }

        if ("BE".equalsIgnoreCase(indemnityType)) {
            docStamps = 0.0
        }

        String chargeSettlementCurrency = productDetails.get("chargeSettlementCurrency")
        println "CHARGES ORIGINAL VALUE"
        println "charges bank commission:" + bankCommission
        println "charges documentary stamps:" + docStamps


        BigDecimal total = bankCommission
        total = total + docStamps


        return [
                BC: bankCommission,
                DOCSTAMPS: docStamps,
                TOTAL: total,
                BCoriginal: bankCommission,
                DOCSTAMPSoriginal: docStamps,
                BCnocwtAmount:bankCommissionnocwtAmount
        ]

    }

    public Map computeIndemnityCancellation(Map productDetails) {


        BigDecimal cancellationFee = 300.00

        println "CHARGES ORIGINAL VALUE"
        println "charges cancellation fee:" + cancellationFee

        return [
                CANCEL: cancellationFee,
                TOTAL: cancellationFee,
                CANCELoriginal: cancellationFee
        ]

    }

//    private getBankCommissionAmendment(String tenorCheck, String amountSwitch, String lcAmountFlag,
//                                       String expiryDateCheck, String expiryDateFlag,
//                                       String changeInConfirmationCheck, String narrativesCheck,
//                                       BigDecimal bankCommissionNumerator,
//                                       BigDecimal bankCommissionDenominator,
//                                       BigDecimal bankCommissionPercentage,
//                                       BigDecimal amountFrom, BigDecimal amountTo,
//                                       BigDecimal expiryDateModifiedDays,
//                                       String expiryDate, String expiryDateTo, String etsDate,
//                                       String cwtFlag,
//                                       BigDecimal cwtPercentage, BigDecimal amountDifference) {
//
//        BigDecimal months = BigDecimal.ZERO
//        if (expiryDate != null && etsDate != null) {
//            months = getMonthsTill(etsDate, expiryDate);
//        }
//
//        BigDecimal newMonths = BigDecimal.ZERO;
//        BigDecimal oldMonths = BigDecimal.ZERO;
//        BigDecimal bothMonths = BigDecimal.ZERO;
//        BigDecimal result = BigDecimal.ZERO;
//        BigDecimal fixed = BigDecimal.ZERO;
//        BigDecimal computedLcAmount = BigDecimal.ZERO;
//        BigDecimal computedLcExpiry = BigDecimal.ZERO;
//        BigDecimal computedBoth = BigDecimal.ZERO;
//
//        //FIXED part
//        if (
//                ("on".equalsIgnoreCase(amountSwitch) && "DEC".equalsIgnoreCase(lcAmountFlag)) ||
//                        ("on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)) ||
//                        ("on".equalsIgnoreCase(narrativesCheck)) ||
//                        ("on".equalsIgnoreCase(changeInConfirmationCheck)) ||
//                        ("on".equalsIgnoreCase(tenorCheck))) {
//            fixed = new BigDecimal("500");
//        }
//
//        BigDecimal bankCommissionFactor = 0
//        bankCommissionFactor.setScale(12)
//        bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_FLOOR)
//        println "bankCommissionFactor:" + bankCommissionFactor
//
//        if ("on".equalsIgnoreCase(amountSwitch)
//                && "INC".equalsIgnoreCase(lcAmountFlag)
//                && "on".equalsIgnoreCase(expiryDateCheck)
//                && "EXT".equalsIgnoreCase(expiryDateFlag)
//        ) {//Increase Amount and Extend Expiry
//            println "aa 01"
//            //COMPUTED part
//            //Bank Commission due to increase in FXLC Amount
//            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
//                oldMonths = months;
//                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
//                System.out.println("oldMonths:" + oldMonths);
//                System.out.println("amount change:" + amountDifference);
//                computedLcAmount = amountDifference;
//                computedLcAmount = computedLcAmount.multiply(bankCommissionFactor.multiply(oldMonths));
//                System.out.println("computed: lcAmount increase:" + computedLcAmount);
//            }
//
//            //Bank Commission due to extension of Expiry Date
//            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
//                newMonths = getMonthsTill(expiryDate, expiryDateTo);
//                System.out.println("newMonths before compareTo:" + newMonths);
//                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
//                System.out.println("newMonths before compareTo:" + newMonths);
//                computedLcExpiry = amountFrom.multiply(bankCommissionFactor).multiply(newMonths)
//                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
//            }
//
//            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && "on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
//                System.out.println("newMonths:" + newMonths);
//                computedBoth = (amountTo.subtract(amountFrom)).multiply(bankCommissionPercentage).multiply(newMonths).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
//                System.out.println("computed: lcAmount both:" + computedBoth);
//
//            }
//
//        } else if ("on".equalsIgnoreCase(amountSwitch)
//                && "INC".equalsIgnoreCase(lcAmountFlag)
//                && "on".equalsIgnoreCase(expiryDateCheck)
//                && "RED".equalsIgnoreCase(expiryDateFlag)
//        ) {//Increase amount and Decrease Expiry
//            //COMPUTED part
//            //Bank Commission due to increase in FXLC Amount
//            println "aa 02"
//            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
//                oldMonths = months;
//                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
//                System.out.println("oldMonths:" + oldMonths);
//                System.out.println("amount change:" + amountDifference);
//                computedLcAmount = amountDifference;
//                computedLcAmount = computedLcAmount.multiply(bankCommissionFactor.multiply(oldMonths));
//                System.out.println("computed: lcAmount increase:" + computedLcAmount);
//            }
//
//        } else if ("on".equalsIgnoreCase(amountSwitch)
//                && "DEC".equalsIgnoreCase(lcAmountFlag)
//                && "on".equalsIgnoreCase(expiryDateCheck)
//                && "EXT".equalsIgnoreCase(expiryDateFlag)
//        ) {//Increase amount and Extend Expiry
//            //Bank Commission due to extension of Expiry Date
//            println "aa 03"
//            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
//                newMonths = getMonthsTill(expiryDate, expiryDateTo);
//                System.out.println("newMonths before compareTo:" + newMonths);
//                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
//                System.out.println("newMonths before compareTo:" + newMonths);
//                computedLcExpiry = amountTo.multiply(bankCommissionFactor.multiply(newMonths))
//                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
//                fixed = new BigDecimal("500");
//            }
//
//        } else if ("on".equalsIgnoreCase(amountSwitch)
//                && "DEC".equalsIgnoreCase(lcAmountFlag)
//                && "on".equalsIgnoreCase(expiryDateCheck)
//                && "RED".equalsIgnoreCase(expiryDateFlag)
//        ) {//Decrease amount and Decrease Expiry
//            println "aa 04"
//            fixed = new BigDecimal("500");
//        } else if ("on".equalsIgnoreCase(amountSwitch)
//                && "INC".equalsIgnoreCase(lcAmountFlag)) {
//            //Bank Commission due to increase in FXLC Amount
//            println "aa 05"
//            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
//                oldMonths = months;
//                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
//                System.out.println("oldMonths:" + oldMonths);
//                System.out.println("amount change:" + amountDifference);
//                computedLcAmount = amountDifference;
//                computedLcAmount = computedLcAmount.multiply(bankCommissionFactor.multiply(oldMonths));
//                System.out.println("computed: lcAmount increase:" + computedLcAmount);
//            }
//        } else if ("on".equalsIgnoreCase(expiryDateCheck)
//                && "EXT".equalsIgnoreCase(expiryDateFlag)
//        ) {
//            //Bank Commission due to extension of Expiry Date
//            println "aa 06"
//            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
//                newMonths = getMonthsTill(expiryDate, expiryDateTo);
//                System.out.println("newMonths before compareTo:" + newMonths);
//                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
//                System.out.println("newMonths before compareTo:" + newMonths);
//                computedLcExpiry = amountFrom.multiply(bankCommissionFactor).multiply(newMonths)
//                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
//            }
//        } else {
//            println "aa 07"
//        }
//
//        result = addToResultIfNotNegativeOrZero(result, computedLcAmount);
//        result = addToResultIfNotNegativeOrZero(result, computedLcExpiry);
//        result = addToResultIfNotNegativeOrZero(result, computedBoth);
//
//        if (result.compareTo(fixed) != 1) {
//            println "result is equal to fixed"
//            result = fixed
//        }
//
//        //Minimum Checking here
//        BigDecimal minimumPhp = new BigDecimal("500");
//        BigDecimal resultAfterCwt = new BigDecimal("0");
//        if (result.compareTo(minimumPhp) != 1) {
//            resultAfterCwt = minimumPhp.setScale(2, BigDecimal.ROUND_UP);
//            println "result after minimum:" + resultAfterCwt
//        } else {
//            resultAfterCwt = result.setScale(2, BigDecimal.ROUND_UP);
//            println "result after minimum:" + resultAfterCwt
//        }
//
//        return resultAfterCwt
//    }


    private getBankCommissionAmendmentExtensionWierd(String tenorCheck, String amountSwitch, String lcAmountFlag,
                                                     String expiryDateCheck, String expiryDateFlag,
                                                     String changeInConfirmationCheck, String narrativesCheck,
                                                     BigDecimal bankCommissionNumerator,
                                                     BigDecimal bankCommissionDenominator,
                                                     BigDecimal bankCommissionPercentage,
                                                     BigDecimal amountFrom, BigDecimal amountTo,
                                                     BigDecimal expiryDateModifiedDays,
                                                     String expiryDate, String expiryDateTo, String etsDate,
                                                     String cwtFlag, BigDecimal cwtPercentage,
                                                     BigDecimal amountDifference, String amendmentDate,
                                                     BigDecimal outstandingBalance) {

        BigDecimal months = BigDecimal.ZERO
        if (expiryDate != null && etsDate != null) {
            months = getMonthsTill(etsDate, expiryDate);
        }

        BigDecimal newMonths = BigDecimal.ZERO;
        BigDecimal oldMonths = BigDecimal.ZERO;
        BigDecimal bothMonths = BigDecimal.ZERO;
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal fixed = BigDecimal.ZERO;
        BigDecimal computedLcAmount = BigDecimal.ZERO;
        BigDecimal computedLcExpiry = BigDecimal.ZERO;
        BigDecimal computedBoth = BigDecimal.ZERO;

        //FIXED part
        if (
                ("on".equalsIgnoreCase(amountSwitch) && "DEC".equalsIgnoreCase(lcAmountFlag)) ||
                        ("on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)) ||
                        ("on".equalsIgnoreCase(narrativesCheck)) ||
                        ("on".equalsIgnoreCase(changeInConfirmationCheck)) ||
                        ("on".equalsIgnoreCase(tenorCheck))) {
            fixed = new BigDecimal("500");
        }

        BigDecimal bankCommissionFactor = 0
        bankCommissionFactor.setScale(12)
        bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "bankCommissionFactor:" + bankCommissionFactor

        if ("on".equalsIgnoreCase(amountSwitch)
                && "INC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "EXT".equalsIgnoreCase(expiryDateFlag)
        ) {//Increase Amount and Extend Expiry
            println "aa 01"
            println "Increase Amount and Extend Expiry"

            //COMPUTED part
            //Bank Commission due to increase in FXLC Amount
            newMonths = getMonthsTill(amendmentDate, expiryDateTo);
            System.out.println("newMonths:" + newMonths);
            computedBoth = (amountTo.subtract(amountFrom)).multiply(bankCommissionPercentage).multiply(newMonths).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
            newMonths = getMonthsTill(expiryDate, expiryDateTo);
            computedBoth = computedBoth.add((outstandingBalance).multiply(bankCommissionPercentage).multiply(newMonths).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP));
            System.out.println("computed: lcAmount both:" + computedBoth);


        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "INC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "RED".equalsIgnoreCase(expiryDateFlag)
        ) {//Increase amount and Decrease Expiry
            //COMPUTED part
            //Bank Commission due to increase in FXLC Amount
            println "aa 02"
            println "Increase amount and Decrease Expiry    "
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
                oldMonths = months;
                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
                System.out.println("oldMonths:" + oldMonths);
                System.out.println("amount change:" + amountDifference);
                computedLcAmount = amountDifference;
                computedLcAmount = computedLcAmount.multiply(bankCommissionFactor.multiply(oldMonths));
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
            }

        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "DEC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "EXT".equalsIgnoreCase(expiryDateFlag)
        ) {//Increase amount and Extend Expiry
            //Bank Commission due to extension of Expiry Date
            println "aa 03"
            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                newMonths = getMonthsTill(expiryDate, expiryDateTo);
                System.out.println("newMonths before compareTo:" + newMonths);
                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
                System.out.println("newMonths before compareTo:" + newMonths);
                computedLcExpiry = amountTo.multiply(bankCommissionFactor.multiply(newMonths))
                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
                fixed = new BigDecimal("500");
            }

        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "DEC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "RED".equalsIgnoreCase(expiryDateFlag)
        ) {//Decrease amount and Decrease Expiry
            println "aa 04"
            fixed = new BigDecimal("500");
        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "INC".equalsIgnoreCase(lcAmountFlag)) {
            //Bank Commission due to increase in FXLC Amount
            println "aa 05"
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
                oldMonths = months
                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
                System.out.println("oldMonths:" + oldMonths);
                System.out.println("amount change:" + amountDifference);
                computedLcAmount = amountDifference;
                computedLcAmount = computedLcAmount.multiply(bankCommissionFactor.multiply(oldMonths));
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
            }
        } else if ("on".equalsIgnoreCase(expiryDateCheck)
                && "EXT".equalsIgnoreCase(expiryDateFlag)
        ) {
            //Bank Commission due to extension of Expiry Date
            println "aa 06"
            println "expiryDate:" + expiryDate
            println "expiryDateTo:" + expiryDateTo
            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                newMonths = getMonthsTill(expiryDate, expiryDateTo);
                System.out.println("newMonths before compareTo:" + newMonths);
                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
                System.out.println("newMonths before compareTo:" + newMonths);
                //computedLcExpiry = amountFrom.multiply(bankCommissionFactor).multiply(newMonths)
                computedLcExpiry = outstandingBalance.multiply(bankCommissionFactor).multiply(newMonths) //Changed due to bug report
                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
            }
        } else {
            println "aa 07"
        }

        result = addToResultIfNotNegativeOrZero(result, computedLcAmount);
        result = addToResultIfNotNegativeOrZero(result, computedLcExpiry);
        result = addToResultIfNotNegativeOrZero(result, computedBoth);

        if (result.compareTo(fixed) != 1) {
            println "result is equal to fixed"
            result = fixed
        }

        //Minimum Checking here
        BigDecimal minimumPhp = new BigDecimal("500");
        BigDecimal resultAfterCwt = new BigDecimal("0");
        if (result.compareTo(minimumPhp) != 1) {
            resultAfterCwt = minimumPhp.setScale(2, BigDecimal.ROUND_FLOOR);
            println "result after minimum:" + resultAfterCwt
        } else {
            resultAfterCwt = result.setScale(2, BigDecimal.ROUND_UP);
            println "result after non minimum:" + resultAfterCwt
        }

        return resultAfterCwt
    }


    private getConfirmingFeeAmendment(String tenorCheck, String amountSwitch, String lcAmountFlag,
                                      String expiryDateCheck, String expiryDateFlag,
                                      String changeInConfirmationCheck, String narrativesCheck,
                                      BigDecimal confirmingFeeNumerator,
                                      BigDecimal confirmingFeeDenominator,
                                      BigDecimal confirmingFeePercentage,
                                      BigDecimal amountFrom, BigDecimal amountTo,
                                      BigDecimal expiryDateModifiedDays,
                                      String expiryDate, String expiryDateTo, String etsDate,
                                      String cwtFlag,
                                      BigDecimal cwtPercentage,
                                      BigDecimal confirmingFeeMinimum, BigDecimal amountDifference, String issueDate,
                                      String originalConfirmationInstructionsFlag, String confirmationInstructionsFlagTo, String amendmentDate) {

        BigDecimal months = BigDecimal.ZERO
        if (expiryDate != null && amendmentDate != null) {
            months = getMonthsTill(amendmentDate, expiryDate)
        }

        BigDecimal newMonths = BigDecimal.ZERO
        BigDecimal oldMonths = BigDecimal.ZERO
        BigDecimal bothMonths = BigDecimal.ZERO
        BigDecimal result = BigDecimal.ZERO
        BigDecimal resultIncreaseInAmount = BigDecimal.ZERO
        BigDecimal resultExtensionExpiryDate = BigDecimal.ZERO
        BigDecimal resultIncreaseAndExtension = BigDecimal.ZERO
        BigDecimal resultChangeInConfirmation = BigDecimal.ZERO


        BigDecimal confirmingFeeFactor = 0
        confirmingFeeFactor.setScale(12)
        confirmingFeeFactor = confirmingFeePercentage.multiply(confirmingFeeNumerator).divide(confirmingFeeDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "confirmingFeeFactor:" + confirmingFeeFactor
        println "changeInConfirmationCheck:" + changeInConfirmationCheck
        if (("on".equalsIgnoreCase(changeInConfirmationCheck) || "Y".equalsIgnoreCase(changeInConfirmationCheck)) &&
                ("YES".equalsIgnoreCase(confirmationInstructionsFlagTo) && "NO".equalsIgnoreCase(originalConfirmationInstructionsFlag))
        ) {
            months = getMonthsTill(amendmentDate, expiryDate)
            println "confirming date:" + months
            println "amountFrom:" + amountFrom
            resultChangeInConfirmation = amountFrom.multiply(confirmingFeeFactor).multiply(months)
        }
        result = addToResultIfNotNegativeOrZero(result, resultChangeInConfirmation)

        //Minimum Checking here
        BigDecimal minimumPhp = new BigDecimal("50"); //Convert 50 USD to PHP
        minimumPhp = currencyConverter.convert("REG-SELL", "USD", minimumPhp, "PHP").setScale(2, BigDecimal.ROUND_UP)
        if (result.compareTo(minimumPhp) != 1 && result.compareTo(BigDecimal.ZERO) != 0) {
            return minimumPhp.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return result.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }

//
//    private getCommitmentFeeAmendment(String tenorCheck, String amountSwitch, String lcAmountFlag,
//                                      String expiryDateCheck, String expiryDateFlag,
//                                      String changeInConfirmationCheck, String narrativesCheck,
//                                      BigDecimal commitmentFeeNumerator,
//                                      BigDecimal commitmentFeeDenominator,
//                                      BigDecimal commitmentFeePercentage,
//                                      BigDecimal amountFrom, BigDecimal amountTo,
//                                      BigDecimal expiryDateModifiedDays,
//                                      String expiryDate, String expiryDateTo, String etsDate,
//                                      String cwtFlag,
//                                      BigDecimal cwtPercentage, BigDecimal amountDifference) {
//
//        BigDecimal months = BigDecimal.ZERO
//        if (expiryDate != null && etsDate != null) {
//            months = getMonthsTill(etsDate, expiryDate);
//        }
//
//        BigDecimal newMonths = BigDecimal.ZERO;
//        BigDecimal oldMonths = BigDecimal.ZERO;
//        BigDecimal bothMonths = BigDecimal.ZERO;
//        BigDecimal result = BigDecimal.ZERO;
//        BigDecimal fixed = BigDecimal.ZERO;
//        BigDecimal computedLcAmount = BigDecimal.ZERO;
//        BigDecimal computedLcExpiry = BigDecimal.ZERO;
//        BigDecimal computedBoth = BigDecimal.ZERO;
//
//        //FIXED part
//        if (
//                ("on".equalsIgnoreCase(amountSwitch) && "DEC".equalsIgnoreCase(lcAmountFlag)) ||
//                        ("on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)) ||
//                        ("on".equalsIgnoreCase(narrativesCheck)) ||
//                        ("on".equalsIgnoreCase(changeInConfirmationCheck)) ||
//                        ("on".equalsIgnoreCase(tenorCheck))) {
//            fixed = 0;
//        }
//
//        println "commitmentFeeNumerator:" + commitmentFeeNumerator
//        println "commitmentFeeDenominator:" + commitmentFeeDenominator
//        println "commitmentFeePercentage:" + commitmentFeePercentage
//        BigDecimal commitmentFeeFactor = 0
//        commitmentFeeFactor.setScale(12)
//        commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_FLOOR)
//        println "commitmentFeeFactor:" + commitmentFeeFactor
//
//        if ("on".equalsIgnoreCase(amountSwitch)
//                && "INC".equalsIgnoreCase(lcAmountFlag)
//                && "on".equalsIgnoreCase(expiryDateCheck)
//                && "EXT".equalsIgnoreCase(expiryDateFlag)
//        ) {//Increase Amount and Extend Expiry
//            println "aa 01"
//            //COMPUTED part
//            //Bank Commission due to increase in FXLC Amount
//            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
//                oldMonths = months;
//                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
//                System.out.println("oldMonths:" + oldMonths);
//                System.out.println("amount change:" + amountDifference);
//                //System.out.println("amount change:" + amountTo.subtract(amountFrom));
//                //computedLcAmount = amountTo.subtract(amountFrom);
//                computedLcAmount = amountDifference;
//                computedLcAmount = computedLcAmount.multiply(commitmentFeeFactor.multiply(oldMonths));
//                System.out.println("computed: lcAmount increase:" + computedLcAmount);
//            }
//
//            //Bank Commission due to extension of Expiry Date
//            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
//                newMonths = getMonthsTill(expiryDate, expiryDateTo);
//                System.out.println("newMonths before compareTo:" + newMonths);
//                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
//                System.out.println("newMonths before compareTo:" + newMonths);
//                computedLcExpiry = amountFrom.multiply(commitmentFeeFactor).multiply(newMonths)
//                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
//            }
//
//            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && "on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
//                System.out.println("bothMonths:" + bothMonths);
//                computedBoth = (amountTo.subtract(amountFrom)).multiply(commitmentFeePercentage).multiply(newMonths).multiply(commitmentFeeNumerator).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
//                System.out.println("computed: lcAmount both:" + computedBoth);
//
//            }
//
//        } else if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)
//                && "on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)) {//Increase amount and Decrease Expiry
//            //COMPUTED part
//            //Bank Commission due to increase in FXLC Amount
//            println "aa 02"
//            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
//                oldMonths = months;
//                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
//                System.out.println("oldMonths:" + oldMonths);
//                System.out.println("amount change:" + amountDifference);
//                //System.out.println("amount change:" + amountTo.subtract(amountFrom));
//                //computedLcAmount = amountTo.subtract(amountFrom);
//                computedLcAmount = amountDifference;
//                computedLcAmount = computedLcAmount.multiply(commitmentFeeFactor.multiply(oldMonths));
//                System.out.println("computed: lcAmount increase:" + computedLcAmount);
//            }
//
//        } else if ("on".equalsIgnoreCase(amountSwitch)
//                && "DEC".equalsIgnoreCase(lcAmountFlag)
//                && "on".equalsIgnoreCase(expiryDateCheck)
//                && "EXT".equalsIgnoreCase(expiryDateFlag)
//        ) {//Increase amount and Extend Expiry
//            //Bank Commission due to extension of Expiry Date
//            println "aa 03"
//            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
//                newMonths = getMonthsTill(expiryDate, expiryDateTo);
//                System.out.println("newMonths before compareTo:" + newMonths);
//                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
//                System.out.println("newMonths before compareTo:" + newMonths);
//                computedLcExpiry = amountTo.multiply(commitmentFeeFactor.multiply(newMonths))
//                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
//                fixed = new BigDecimal("500");
//            }
//
//        } else if ("on".equalsIgnoreCase(amountSwitch) && "DEC".equalsIgnoreCase(lcAmountFlag)
//                && "on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)) {//Decrease amount and Decrease Expiry
//            println "aa 04"
//            fixed = new BigDecimal("500");
//        } else if ("on".equalsIgnoreCase(amountSwitch)
//                && "INC".equalsIgnoreCase(lcAmountFlag)) {
//            //Bank Commission due to increase in FXLC Amount
//            println "aa 05"
//            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
//                oldMonths = months;
//                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
//                System.out.println("oldMonths:" + oldMonths);
//                System.out.println("amount change:" + amountDifference);
//                //System.out.println("amount change:" + amountTo.subtract(amountFrom));
//                //computedLcAmount = amountTo.subtract(amountFrom);
//                computedLcAmount = amountDifference;
//                computedLcAmount = computedLcAmount.multiply(commitmentFeeFactor.multiply(oldMonths));
//                System.out.println("computed: lcAmount increase:" + computedLcAmount);
//            }
//        } else if ("on".equalsIgnoreCase(expiryDateCheck)
//                && "EXT".equalsIgnoreCase(expiryDateFlag)
//        ) {
//            //Bank Commission due to extension of Expiry Date
//            println "aa 06"
//            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
//                newMonths = getMonthsTill(expiryDate, expiryDateTo);
//                System.out.println("newMonths before compareTo:" + newMonths);
//                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
//                System.out.println("newMonths before compareTo:" + newMonths);
//                computedLcExpiry = amountFrom.multiply(commitmentFeeFactor).multiply(newMonths)
//                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
//            }
//        } else {
//            println "aa 07"
//        }
//
//        result = addToResultIfNotNegativeOrZero(result, computedLcAmount);
//        result = addToResultIfNotNegativeOrZero(result, computedLcExpiry);
//        result = addToResultIfNotNegativeOrZero(result, computedBoth);
//
//        if (result.compareTo(fixed) != 1) {
//            println "result is equal to fixed"
//            result = fixed
//        }
//
//        //Minimum Checking here
//        BigDecimal minimumPhp = new BigDecimal("500");
//        BigDecimal resultAfterMin = new BigDecimal("0");
//        if (result > 0) {
//            if (result.compareTo(minimumPhp) != 1) {
//
//
//                resultAfterMin = minimumPhp.setScale(2, BigDecimal.ROUND_HALF_UP);
//                println "result after minimum:" + resultAfterMin
//            } else {
//                resultAfterMin = result.setScale(2, BigDecimal.ROUND_HALF_UP);
//                println "result after minimum:" + resultAfterMin
//            }
//        } else {
//
//        }
//
//
//
//
//        return resultAfterMin
//    }


    private getCommitmentFeeAmendmentExtensionWeird(String tenorCheck, String amountSwitch, String lcAmountFlag, String expiryDateCheck, String expiryDateFlag, String changeInConfirmationCheck, String narrativesCheck, BigDecimal commitmentFeeNumerator, BigDecimal commitmentFeeDenominator, BigDecimal commitmentFeePercentage, BigDecimal amountFrom, BigDecimal amountTo, BigDecimal expiryDateModifiedDays, String expiryDate, String expiryDateTo, String etsDate, String cwtFlag, BigDecimal cwtPercentage, BigDecimal amountDifference, String amendmentDate, String documentSubType1, String documentSubType2, BigDecimal usancePeriod, BigDecimal outstandingBalance) {

        BigDecimal months = BigDecimal.ZERO
        println "usancePeriod usancePeriod usancePeriod:"+usancePeriod
        if(documentSubType1.equalsIgnoreCase("REGULAR") && documentSubType2.equalsIgnoreCase("USANCE")){
            months = usancePeriod.divide(30,12,BigDecimal.ROUND_UP)
        } else {
            if (expiryDate != null && etsDate != null) {
                months = getMonthsTill(etsDate, expiryDate);
            }
        }


        BigDecimal newMonths = BigDecimal.ZERO;
        BigDecimal oldMonths = BigDecimal.ZERO;
        BigDecimal bothMonths = BigDecimal.ZERO;
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal fixed = BigDecimal.ZERO;
        BigDecimal computedLcAmount = BigDecimal.ZERO;
        BigDecimal computedLcExpiry = BigDecimal.ZERO;
        BigDecimal computedBoth = BigDecimal.ZERO;

        //FIXED part
        if (
                ("on".equalsIgnoreCase(amountSwitch) && "DEC".equalsIgnoreCase(lcAmountFlag)) ||
                        ("on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)) ||
                        ("on".equalsIgnoreCase(narrativesCheck)) ||
                        ("on".equalsIgnoreCase(changeInConfirmationCheck)) ||
                        ("on".equalsIgnoreCase(tenorCheck))) {
            fixed = 0;
        }

        println "commitmentFeeNumerator:" + commitmentFeeNumerator
        println "commitmentFeeDenominator:" + commitmentFeeDenominator
        println "commitmentFeePercentage:" + commitmentFeePercentage
        BigDecimal commitmentFeeFactor = 0
        commitmentFeeFactor.setScale(12)
        commitmentFeeFactor = commitmentFeePercentage.multiply(commitmentFeeNumerator).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_FLOOR)
        println "commitmentFeeFactor:" + commitmentFeeFactor

        if ("on".equalsIgnoreCase(amountSwitch)
                && "INC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "EXT".equalsIgnoreCase(expiryDateFlag)
           && !("REGULAR".equalsIgnoreCase(documentSubType1)&&"SIGHT".equalsIgnoreCase(documentSubType2))
        ) {//Increase Amount and Extend Expiry
            println "aa 01"
            println "Increase Amount and Extend Expiry"

            if(documentSubType1.equalsIgnoreCase("REGULAR") && documentSubType2.equalsIgnoreCase("USANCE")){
                newMonths = usancePeriod.divide(30,12,BigDecimal.ROUND_UP)
            } else {
                if (amendmentDate != null && expiryDateTo != null) {
                    newMonths = getMonthsTill(amendmentDate, expiryDateTo);
                }
            }

            System.out.println("newMonths:" + newMonths);
            computedBoth = (amountTo.subtract(amountFrom)).multiply(commitmentFeePercentage).multiply(newMonths).multiply(commitmentFeeNumerator).divide(commitmentFeeDenominator, 12, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
            System.out.println("computed: lcAmount both: Increase Amount and Extend Expiry:" + computedBoth);


        } else if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)
                && !("REGULAR".equalsIgnoreCase(documentSubType1)&&"SIGHT".equalsIgnoreCase(documentSubType2))
        ) {//Increase amount and Decrease Expiry
            //COMPUTED part
            //Bank Commission due to increase in FXLC Amount
            println "aa 02"
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
                if(documentSubType1.equalsIgnoreCase("REGULAR") && documentSubType2.equalsIgnoreCase("USANCE")){
                    oldMonths = usancePeriod.divide(30,12,BigDecimal.ROUND_UP)
                } else {
                    if (amendmentDate != null && expiryDateTo != null) {
                        oldMonths = getMonthsTill(amendmentDate, expiryDateTo);
                    }
                }
//                oldMonths = months;
                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
                System.out.println("oldMonths:" + oldMonths);
                System.out.println("amount change:" + amountDifference);
                //System.out.println("amount change:" + amountTo.subtract(amountFrom));
                //computedLcAmount = amountTo.subtract(amountFrom);
                computedLcAmount = amountDifference;
                computedLcAmount = computedLcAmount.multiply(commitmentFeeFactor.multiply(oldMonths));
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
            }

        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "DEC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "EXT".equalsIgnoreCase(expiryDateFlag)
        ) {//Decrease amount and Extend Expiry
            //Bank Commission due to extension of Expiry Date
            println "aa 03"
            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {

                System.out.println("newMonths before compareTo:" + newMonths);
                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
                System.out.println("newMonths before compareTo:" + newMonths);
                computedLcExpiry = amountTo.multiply(commitmentFeeFactor.multiply(newMonths))
                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
                fixed = new BigDecimal("500");
            }

        } else if ("on".equalsIgnoreCase(amountSwitch) && "DEC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)) {//Decrease amount and Decrease Expiry
            println "aa 04"
            fixed = new BigDecimal("500");
        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "INC".equalsIgnoreCase(lcAmountFlag)
                && !("REGULAR".equalsIgnoreCase(documentSubType1)&&"SIGHT".equalsIgnoreCase(documentSubType2))
        ) {
            //Bank Commission due to increase in FXLC Amount
            println "aa 05"
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
                if(documentSubType1.equalsIgnoreCase("REGULAR") && documentSubType2.equalsIgnoreCase("USANCE")){
                    println "usancePeriod usancePeriod usancePeriod usancePeriod:"+usancePeriod
                    oldMonths = usancePeriod.divide(30,12,BigDecimal.ROUND_UP)
                    println "oldMonths:"+oldMonths
                } else {
                    println "in else in else"
                    if (amendmentDate != null && expiryDateTo != null) {
                        oldMonths = getMonthsTill(amendmentDate, expiryDateTo);
                    }
                }
//                oldMonths = months;
                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
                System.out.println("oldMonths.compareTo(new BigDecimal(\"1\")):" + oldMonths.compareTo(new BigDecimal("1")));
                System.out.println("oldMonths:" + oldMonths);
                System.out.println("amount change:" + amountDifference);
                //System.out.println("amount change:" + amountTo.subtract(amountFrom));
                //computedLcAmount = amountTo.subtract(amountFrom);
                computedLcAmount = amountDifference;
                computedLcAmount = computedLcAmount.multiply(commitmentFeeFactor.multiply(oldMonths));
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
            }
        } else if ("on".equalsIgnoreCase(expiryDateCheck)
                && "EXT".equalsIgnoreCase(expiryDateFlag)
                && ("STANDBY".equalsIgnoreCase(documentSubType1)&&"SIGHT".equalsIgnoreCase(documentSubType2))
        ) {
            //Bank Commission due to extension of Expiry Date
            println "aa 06"
            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {

                newMonths = getMonthsTill(expiryDate, expiryDateTo);
                System.out.println("newMonths before compareTo:" + newMonths);
                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
                System.out.println("newMonths before compareTo:" + newMonths);
                //computedLcExpiry = amountFrom.multiply(commitmentFeeFactor).multiply(newMonths)
                computedLcExpiry = outstandingBalance.multiply(commitmentFeeFactor).multiply(newMonths) //Changed due to bug report
                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
            }
        } else if ("on".equalsIgnoreCase(tenorCheck)){
            println "aa 07"
//            newMonths = months;
            newMonths = usancePeriod.divide(30,12,BigDecimal.ROUND_UP);
            System.out.println("newMonths before compareTo:" + newMonths);
            newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
            System.out.println("newMonths before compareTo:" + newMonths);
            computedLcExpiry = amountFrom.multiply(commitmentFeeFactor).multiply(newMonths);
            System.out.println("computed: expiryDate extension:" + computedLcExpiry);
        } else {
            println "aa 08"

            computedLcAmount = amountFrom.multiply(commitmentFeeFactor.multiply(oldMonths));

        }

        result = addToResultIfNotNegativeOrZero(result, computedLcAmount);
        result = addToResultIfNotNegativeOrZero(result, computedLcExpiry);
        result = addToResultIfNotNegativeOrZero(result, computedBoth);

        if (result.compareTo(fixed) != 1) {
            println "result is equal to fixed"
            result = fixed
        }

        //Minimum Checking here
        BigDecimal minimumPhp = new BigDecimal("500");
        BigDecimal resultAfterMin = new BigDecimal("0");
        if (result > 0) {
            if (result.compareTo(minimumPhp) != 1) {
                resultAfterMin = minimumPhp.setScale(2, BigDecimal.ROUND_HALF_UP);
                println "result after minimum:" + resultAfterMin
            } else {
                resultAfterMin = result.setScale(2, BigDecimal.ROUND_HALF_UP);
                println "result after minimum:" + resultAfterMin
            }
        } else {

        }
//
//        if ("on".equalsIgnoreCase(tenorCheck)) {
//            println "commitmentFee:" + resultAfterMin
//        } else {
//            resultAfterMin = 0
//            println "commitmentFee:" + resultAfterMin
//        }


        return resultAfterMin
    }


    private static BigDecimal addToResultIfNotNegativeOrZero(BigDecimal result, BigDecimal toBeAdded) {
        if (toBeAdded.compareTo(BigDecimal.ZERO) == 1) {
            return result.add(toBeAdded);
        }
        return result;
    }

    public static BigDecimal getMonthsTill(String dateFrom, String dateTo) {
        System.out.println("getMonthsTill");
        System.out.println("Date From:" + dateFrom);
        System.out.println("Date To:" + dateTo);

        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");

        try {
            Date startDate = formatter.parse(dateFrom);
            Date endDate = formatter.parse(dateTo);
            System.out.println("dateFrom:" + dateFrom);
            System.out.println("dateTo:" + dateTo);

            //Uses Days 30/360 Convention
            Integer days = SerialDateUtilities.dayCount30ISDA(
                    SerialDate.createInstance(startDate),
                    SerialDate.createInstance(endDate)
            );
            System.out.println("days 360:" + days);

            DateTime dateTimeFrom = new DateTime(startDate);
            DateTime dateTimeTo = new DateTime(endDate);
            int daysint = Days.daysBetween(dateTimeFrom, dateTimeTo).getDays();
            Integer daysInt = new Integer(daysint);

            System.out.println("days 365:" + daysint);

            // get a rounded up version of this using a math hack
            // rounding up: (numerator + denominator-1) / denominator
            // rounding down: (numerator + (denominator)/2) / denominator
//            System.out.println("months:" + ((days + 29) / 30));
            System.out.println("days:" + days);
            System.out.println("months 360:" + new BigDecimal(days).divide(new BigDecimal("30"), 12, BigDecimal.ROUND_UP));
            System.out.println("months 365:" + new BigDecimal(daysInt).divide(new BigDecimal("30"), 12, BigDecimal.ROUND_UP));
            return new BigDecimal(daysInt).divide(new BigDecimal("30"), 12, BigDecimal.ROUND_UP);

        } catch (Exception e) {
            // todo: handle invalid dates here
            return new BigDecimal(0);
        }
    }

}
