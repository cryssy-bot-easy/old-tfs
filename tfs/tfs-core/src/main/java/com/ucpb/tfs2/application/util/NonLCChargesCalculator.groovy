package com.ucpb.tfs2.application.util

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:56 PM
 */
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Changes hard-coded parameters of computing
 *      document stamps into data retrieved from database.
 *  Date revised: 02/01/2018
*/
/**
 *  Revised by: Cedrick C. Nungay
 *  Details: Added documentStampsOrig for computations and added parameters used on computing docstamps.
 *  Date revised: 04/13/2018
 */
class NonLCChargesCalculator extends ChargesCalculator {

    public Map compute(Map productDetails) {
        // precompute for the base variables
        precomputeBase(productDetails);

        //parameters
        BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexNumerator", "BigDecimal") ?: 1
        BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexDenominator", "BigDecimal") ?: 4
        BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexPercentage", "BigDecimal") ?: 0.01

        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionNumerator", "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionDenominator", "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionPercentage", "BigDecimal") ?: 0.01

        String documentType = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentType", "String") ?: "FOREIGN"
        String remittanceFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "remittanceFlag", "String") ?: "N"
        String cableFeeFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cableFeeFlag", "String") ?: "N"

        Calculators calculators = new Calculators()

        // parameterized factors
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_HALF_UP)
        BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
        BigDecimal cableFeeDefault
        BigDecimal cableFeeDefaultDM = 500
        BigDecimal cableFeeDefaultFX = 1000
        BigDecimal remittanceFeeDefault = 18
        BigDecimal bookingComissionDefault = 500
        BigDecimal notarialFeeDefault = 50
        BigDecimal bspRegFeeDefault = 100

        // charges
        BigDecimal bankComission = calculators.firstSucceedingPercentageWithMinimum((BigDecimal) getBaseVariable("chargesBasePHP"), 50000, 125, bankCommissionFactor, 1000)

        cableFeeDefault = "DOMESTIC".equalsIgnoreCase(documentType) ? cableFeeDefaultDM : cableFeeDefaultFX
        cableFeeDefault = "Y".equalsIgnoreCase(cableFeeFlag) ? cableFeeDefault : 0
        BigDecimal cableFee = cableFeeDefault

        BigDecimal cilex = calculators.percentageOf((BigDecimal) getBaseVariable("settledInForeignInUSD"), cilexFactor)
        BigDecimal bookingCommission = bookingComissionDefault;
        // TODO: add condition if TR loan is used
        BigDecimal docStamps = BigDecimal.ZERO
        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
        BigDecimal succeedingBaseAmount = productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT)
        BigDecimal succeedingRateAmount = productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT)
        BigDecimal baseNegoAmount = productDetails.chargesParameter.BASENEGOAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASENEGOAMOUNT)
        BigDecimal rateNegoAmount = productDetails.chargesParameter.RATENEGOAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATENEGOAMOUNT)
        if (((BigDecimal) getBaseVariable("totalTrAmount")).compareTo(BigDecimal.ZERO) == 1) {
            docStamps = calculators.firstSucceedingFixed((BigDecimal) getBaseVariable("totalTrAmountInPHP"), baseAmount, rateAmount, succeedingBaseAmount, succeedingRateAmount)
        }

        if ((BigDecimal) getBaseVariable("totalNotSettledByTRinPHP")?.compareTo(BigDecimal.ZERO) == 1) {
            BigDecimal normalAmount = (BigDecimal) getBaseVariable("totalNotSettledByTRinPHP") - (BigDecimal) getBaseVariable("totalTrAmountInPHP")
            println "normalAmount:" + normalAmount
            docStamps = docStamps.add(calculators.forEvery(normalAmount, baseNegoAmount, rateNegoAmount))
        }

        BigDecimal notarialFee = notarialFeeDefault;
        BigDecimal bspRegFee = bspRegFeeDefault
        BigDecimal remittanceFee = currencyConverter.convert("URR", "USD", remittanceFeeDefault, "PHP").setScale(2, BigDecimal.ROUND_UP)
        remittanceFee = "Y".equalsIgnoreCase(remittanceFlag) ? remittanceFee : 0

        println "Bank Comission : " + bankComission.setScale(2, BigDecimal.ROUND_UP)
        println "Cable Fee : " + cableFee.setScale(2, BigDecimal.ROUND_UP)
        println "CILEX in PHP " + currencyConverter.convert("URR", "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP)
        println "Documentary Stamps: " + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "Booking Comission : " + bookingCommission.setScale(2, BigDecimal.ROUND_UP)
        println "Notarial Fee : " + notarialFee.setScale(2, BigDecimal.ROUND_UP)
        println "BSP Registration Fee : " + bspRegFee.setScale(2, BigDecimal.ROUND_UP)
        println "Remittance Fee : " + remittanceFee.setScale(2, BigDecimal.ROUND_UP)

        //Convert to Charges Settlement Currency
        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():" + getBaseVariable("chargeSettlementCurrency")?.toString()

        return [
                BC: bankComission,
                CABLE: cableFee,
                CILEX: currencyConverter.convert("URR", "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                BOOKING: bookingCommission.setScale(2, BigDecimal.ROUND_UP),
                NOTARIAL: notarialFee.setScale(2, BigDecimal.ROUND_UP),
                BSP: bspRegFee.setScale(2, BigDecimal.ROUND_UP)
        ]

    }

    public Map computeDM(Map productDetails) {

        // precompute for the base variables
        precomputeBaseDMLC(productDetails);

        //parameters
        BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexNumerator", "BigDecimal") ?: 1
        BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexDenominator", "BigDecimal") ?: 4
        BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexPercentage", "BigDecimal") ?: 0.01

        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionNumerator", "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionDenominator", "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionPercentage", "BigDecimal") ?: 0.01

        String documentType = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentType", "String") ?: "FOREIGN"
        String remittanceFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "remittanceFlag", "String") ?: "N"
        String cableFeeFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cableFeeFlag", "String") ?: "N"

        Calculators calculators = new Calculators()

        // parameterized factors
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_HALF_UP)
        BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
        BigDecimal cableFeeDefault
        BigDecimal cableFeeDefaultDM = 500
        BigDecimal cableFeeDefaultFX = 1000
        BigDecimal remittanceFeeDefault = 18
        BigDecimal bookingComissionDefault = 500
        BigDecimal notarialFeeDefault = 50
        BigDecimal bspRegFeeDefault = 100

        // charges
        BigDecimal bankComission = calculators.firstSucceedingPercentageWithMinimum((BigDecimal) getBaseVariable("chargesBasePHP"), 50000, 125, bankCommissionFactor, 1000)

        cableFeeDefault = "DOMESTIC".equalsIgnoreCase(documentType) ? cableFeeDefaultDM : cableFeeDefaultFX
        cableFeeDefault = "Y".equalsIgnoreCase(cableFeeFlag) ? cableFeeDefault : 0
        BigDecimal cableFee = cableFeeDefault

        BigDecimal cilex = calculators.percentageOf((BigDecimal) getBaseVariable("settledInForeignInUSD"), cilexFactor)
        BigDecimal bookingCommission = bookingComissionDefault;
        // TODO: add condition if TR loan is used
        BigDecimal docStamps = BigDecimal.ZERO
        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
        BigDecimal succeedingBaseAmount = productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT)
        BigDecimal succeedingRateAmount = productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT)
        BigDecimal baseNegoAmount = productDetails.chargesParameter.BASENEGOAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASENEGOAMOUNT)
        BigDecimal rateNegoAmount = productDetails.chargesParameter.RATENEGOAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATENEGOAMOUNT)
        BigDecimal centavos = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "centavos", "BigDecimal")
        BigDecimal forFirst = productDetails.extendedProperties.forFirst == null || productDetails.extendedProperties.forFirst == ''  ? null : new BigDecimal(productDetails.extendedProperties.forFirst)
        BigDecimal forNext = productDetails.extendedProperties.forNext == null ||  productDetails.extendedProperties.forNext == '' ? null : new BigDecimal(productDetails.extendedProperties.forNext)
        BigDecimal forFirstAmount = productDetails.extendedProperties.forFirstAmount == null ||  productDetails.extendedProperties.forFirstAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forFirstAmount)
        BigDecimal forNextAmount = productDetails.extendedProperties.forNextAmount == null ||  productDetails.extendedProperties.forNextAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forNextAmount)
        if (((BigDecimal) getBaseVariable("totalTrAmount")).compareTo(BigDecimal.ZERO) == 1) {
            docStamps = calculators.firstSucceedingFixed((BigDecimal) getBaseVariable("totalTrAmountInPHP"), forFirst ?: baseAmount, forFirstAmount ?: rateAmount, forNext ?: succeedingBaseAmount, forNextAmount ?: succeedingRateAmount)
        }

        if ((BigDecimal) getBaseVariable("totalNotSettledByTRinPHP")?.compareTo(BigDecimal.ZERO) == 1) {
            BigDecimal normalAmount = (BigDecimal) getBaseVariable("totalNotSettledByTRinPHP") - (BigDecimal) getBaseVariable("totalTrAmountInPHP")
            println "normalAmount:" + normalAmount
            docStamps = docStamps.add(calculators.forEvery(normalAmount, baseNegoAmount, centavos ?: rateNegoAmount))
        }

        BigDecimal notarialFee = notarialFeeDefault;
        BigDecimal bspRegFee = bspRegFeeDefault
        BigDecimal remittanceFee = currencyConverter.convert("URR", "USD", remittanceFeeDefault, "PHP").setScale(2, BigDecimal.ROUND_UP)
        remittanceFee = "Y".equalsIgnoreCase(remittanceFlag) ? remittanceFee : 0

        println "Bank Comission : " + bankComission.setScale(2, BigDecimal.ROUND_UP)
        println "Cable Fee : " + cableFee.setScale(2, BigDecimal.ROUND_UP)
        println "CILEX in PHP " + currencyConverter.convert("URR", "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP)
        println "Documentary Stamps: " + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "Booking Comission : " + bookingCommission.setScale(2, BigDecimal.ROUND_UP)
        println "Notarial Fee : " + notarialFee.setScale(2, BigDecimal.ROUND_UP)
        println "BSP Registration Fee : " + bspRegFee.setScale(2, BigDecimal.ROUND_UP)
        println "Remittance Fee : " + remittanceFee.setScale(2, BigDecimal.ROUND_UP)

        //Convert to Charges Settlement Currency
        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():" + getBaseVariable("chargeSettlementCurrency")?.toString()

        return [
                BC: bankComission,
                CABLE: cableFee,
                CILEX: currencyConverter.convert("URR", "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPS: docStamps.setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStamps.setScale(2, BigDecimal.ROUND_UP),
                BOOKING: bookingCommission.setScale(2, BigDecimal.ROUND_UP),
                NOTARIAL: notarialFee.setScale(2, BigDecimal.ROUND_UP),
                BSP: bspRegFee.setScale(2, BigDecimal.ROUND_UP)
        ]

    }

    public Map computeFX(Map productDetails) {

        // precompute for the base variables
        precomputeBaseFXLC(productDetails);

        //parameters
        BigDecimal cilexNumerator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexNumerator", "BigDecimal") ?: 1
        BigDecimal cilexDenominator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexDenominator", "BigDecimal") ?: 4
        BigDecimal cilexPercentage = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cilexPercentage", "BigDecimal") ?: 0.01

        BigDecimal bankCommissionNumerator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionNumerator", "BigDecimal") ?: 1
        BigDecimal bankCommissionDenominator = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionDenominator", "BigDecimal") ?: 8
        BigDecimal bankCommissionPercentage = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "bankCommissionPercentage", "BigDecimal") ?: 0.01

        String documentType = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "documentType", "String") ?: "FOREIGN"
        String remittanceFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "remittanceFlag", "String") ?: "N"
        String cableFeeFlag = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "cableFeeFlag", "String") ?: "N"

        Calculators calculators = new Calculators()

        // parameterized factors
        BigDecimal cilexFactor = cilexPercentage.multiply(cilexNumerator).divide(cilexDenominator, 12, BigDecimal.ROUND_HALF_UP)
        BigDecimal bankCommissionFactor = bankCommissionPercentage.multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 12, BigDecimal.ROUND_HALF_UP)
        BigDecimal cableFeeDefault
        BigDecimal cableFeeDefaultDM = 500
        BigDecimal cableFeeDefaultFX = 1000
        BigDecimal remittanceFeeDefault = 18
        BigDecimal bookingComissionDefault = 500
        BigDecimal notarialFeeDefault = 50
        BigDecimal bspRegFeeDefault = 100

        // charges
        BigDecimal bankComission = calculators.firstSucceedingPercentageWithMinimum((BigDecimal) getBaseVariable("chargesBasePHP"), 50000, 125, bankCommissionFactor, 1000)

        cableFeeDefault = "DOMESTIC".equalsIgnoreCase(documentType) ? cableFeeDefaultDM : cableFeeDefaultFX
        cableFeeDefault = "Y".equalsIgnoreCase(cableFeeFlag) ? cableFeeDefault : 0
        BigDecimal cableFee = cableFeeDefault

        BigDecimal cilex = calculators.percentageOf((BigDecimal) getBaseVariable("settledInForeignInUSD"), cilexFactor)
        BigDecimal bookingCommission = bookingComissionDefault;
        // TODO: add condition if TR loan is used
        BigDecimal docStamps = BigDecimal.ZERO
        BigDecimal baseAmount = productDetails.chargesParameter.BASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASEAMOUNT)
        BigDecimal rateAmount = productDetails.chargesParameter.RATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATEAMOUNT)
        BigDecimal succeedingBaseAmount = productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGBASEAMOUNT)
        BigDecimal succeedingRateAmount = productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.SUCCEEDINGRATEAMOUNT)
        BigDecimal baseNegoAmount = productDetails.chargesParameter.BASENEGOAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.BASENEGOAMOUNT)
        BigDecimal rateNegoAmount = productDetails.chargesParameter.RATENEGOAMOUNT == null ? null : new BigDecimal(productDetails.chargesParameter.RATENEGOAMOUNT)
        BigDecimal centavos = (BigDecimal) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "centavos", "BigDecimal")
        BigDecimal forFirst = productDetails.extendedProperties.forFirst == null || productDetails.extendedProperties.forFirst == ''  ? null : new BigDecimal(productDetails.extendedProperties.forFirst)
        BigDecimal forNext = productDetails.extendedProperties.forNext == null ||  productDetails.extendedProperties.forNext == '' ? null : new BigDecimal(productDetails.extendedProperties.forNext)
        BigDecimal forFirstAmount = productDetails.extendedProperties.forFirstAmount == null ||  productDetails.extendedProperties.forFirstAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forFirstAmount)
        BigDecimal forNextAmount = productDetails.extendedProperties.forNextAmount == null ||  productDetails.extendedProperties.forNextAmount == '' ? null : new BigDecimal(productDetails.extendedProperties.forNextAmount)
        if (((BigDecimal) getBaseVariable("totalTrAmount")).compareTo(BigDecimal.ZERO) == 1) {
            docStamps = calculators.firstSucceedingFixed((BigDecimal) getBaseVariable("totalTrAmountInPHP"), forFirst ?: baseAmount, forFirstAmount ?: rateAmount, forNext ?: succeedingBaseAmount, forNextAmount ?: succeedingRateAmount)
        }

        if ((BigDecimal) getBaseVariable("totalNotSettledByTRinPHP")?.compareTo(BigDecimal.ZERO) == 1) {
            BigDecimal normalAmount = (BigDecimal) getBaseVariable("totalNotSettledByTRinPHP") - (BigDecimal) getBaseVariable("totalTrAmountInPHP")
            println "normalAmount:" + normalAmount
            docStamps = docStamps.add(calculators.forEvery(normalAmount, baseNegoAmount, centavos ?: rateNegoAmount))
        }

        BigDecimal notarialFee = notarialFeeDefault;
        BigDecimal bspRegFee = bspRegFeeDefault
        BigDecimal remittanceFee = currencyConverter.convert("URR", "USD", remittanceFeeDefault, "PHP").setScale(2, BigDecimal.ROUND_UP)
        remittanceFee = "Y".equalsIgnoreCase(remittanceFlag) ? remittanceFee : 0

        println "Bank Comission : " + bankComission.setScale(2, BigDecimal.ROUND_UP)
        println "Cable Fee : " + cableFee.setScale(2, BigDecimal.ROUND_UP)
        println "CILEX in PHP " + currencyConverter.convert("URR", "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP)
        println "Documentary Stamps: " + docStamps.setScale(2, BigDecimal.ROUND_UP)
        println "Booking Comission : " + bookingCommission.setScale(2, BigDecimal.ROUND_UP)
        println "Notarial Fee : " + notarialFee.setScale(2, BigDecimal.ROUND_UP)
        println "BSP Registration Fee : " + bspRegFee.setScale(2, BigDecimal.ROUND_UP)
        println "Remittance Fee : " + remittanceFee.setScale(2, BigDecimal.ROUND_UP)
        cilex = currencyConverter.convert("URR", "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP)
        BigDecimal total = bankComission + cableFee + cilex + docStamps + bookingCommission + notarialFee + bspRegFee + remittanceFee

        //Convert to Charges Settlement Currency
        println "getBaseVariable(\"chargeSettlementCurrency\")?.toString():" + getBaseVariable("chargeSettlementCurrency")?.toString()

        String chargeSettlementCurrency = (String) ChargesCalculator.getExtendedPropertiesVariable(productDetails, "chargeSettlementCurrency", "String")
        return [
                BC: bankComission,
                CABLE: cableFee,
                CILEX: currencyConverter.convert("URR", "USD", cilex, "PHP").setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPS: currencyConverter.convert("URR", "PHP", docStamps, chargeSettlementCurrency).setScale(2, BigDecimal.ROUND_UP),
                DOCSTAMPSoriginal: docStamps.setScale(2, BigDecimal.ROUND_UP),
                BOOKING: bookingCommission.setScale(2, BigDecimal.ROUND_UP),
                NOTARIAL: notarialFee.setScale(2, BigDecimal.ROUND_UP),
                BSP: bspRegFee.setScale(2, BigDecimal.ROUND_UP),
                TOTAL: total
        ]

    }



    def recomputeNONLCBankCommission(
            BigDecimal productAmount,
            String lccurrency,
            String settlementcurrency,
            String cwtFlag,
            String documentType,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateCurrency,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency,
            BigDecimal usdToPhpSpecialConversionRate,
            String conversionStyle,
            BigDecimal bankCommissionNumerator,
            BigDecimal bankCommissionDenominator,
            BigDecimal bankCommissionPercentage,
            BigDecimal cwtPercentage
    ) {
        BigDecimal originalAmount = productAmount

        if ("domestic".equalsIgnoreCase(documentType)) {
            productAmount = computeCorrectProductAmountDM(
                    productAmount,
                    lccurrency,
                    thirdToUsdSpecialConversionRateCurrency,
                    usdToPhpSpecialConversionRate,
                    urr,
                    conversionStyle
            )
        } else {
            productAmount = computeCorrectProductAmount(
                    productAmount,
                    lccurrency,
                    thirdToUsdSpecialConversionRateCurrency,
                    usdToPhpSpecialConversionRate,
                    urr,
                    conversionStyle
            )
        }

        bankCommissionDenominator = bankCommissionDenominator ?: new BigDecimal("8");
        bankCommissionNumerator = bankCommissionNumerator ?: new BigDecimal("1");
        bankCommissionPercentage = bankCommissionPercentage ?: new BigDecimal("0.01");
        println "bankCommissionPercentage:" + bankCommissionPercentage
        println "bankCommissionDenominator:" + bankCommissionDenominator
        println "bankCommissionNumerator:" + bankCommissionNumerator

        if (bankCommissionPercentage.compareTo(BigDecimal.ONE) >= 0) {
            bankCommissionPercentage = bankCommissionPercentage / 100
        }
        cwtPercentage = cwtPercentage ?: new BigDecimal("0.98");


        BigDecimal bankCommissionAmountInitial = new BigDecimal("125");
        BigDecimal tranch01 = new BigDecimal("50000");

        BigDecimal remainingProductAmount = productAmount.subtract(tranch01);
        BigDecimal bankCommissionNext = BigDecimal.ZERO;
        if (remainingProductAmount.compareTo(BigDecimal.ZERO) == 1) {
            bankCommissionNext = remainingProductAmount.multiply(bankCommissionNumerator);
            System.out.println(bankCommissionNext);
            bankCommissionNext = bankCommissionNext.multiply(bankCommissionPercentage);
            System.out.println(bankCommissionNext);
            bankCommissionNext = bankCommissionNext.divide(bankCommissionDenominator, 9, BigDecimal.ROUND_HALF_UP);
            System.out.println(bankCommissionNext);
            bankCommissionNext = bankCommissionNext.add(bankCommissionAmountInitial);
            System.out.println(bankCommissionNext);
        }

        if ("FOREIGN".equalsIgnoreCase(documentType)) {
            if (bankCommissionNext.compareTo(new BigDecimal("1000")) != 1) {
                bankCommissionNext = new BigDecimal("1000");
            }
        } else {
            if (bankCommissionNext.compareTo(new BigDecimal("500")) != 1) {
                bankCommissionNext = new BigDecimal("500");
            }
        }

        println "cwtFlag:" + cwtFlag
        if ("Y".equalsIgnoreCase(cwtFlag) || "Yes".equalsIgnoreCase(cwtFlag)) {
            bankCommissionNext = bankCommissionNext.multiply(cwtPercentage);
        }

        BigDecimal settlementConversionRate = computeConversionRateSettlementCharges(
                lccurrency,
                settlementcurrency,
                urr,
                thirdToUsdSpecialConversionRateSettlementCurrency
        )
        println "settlementConversionRate:" + settlementConversionRate
        return bankCommissionNext.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)
    }

    def recompute_NONLC_DocumentaryStamps(
            BigDecimal productAmount,
            String lccurrency,
            String settlementcurrency,
            String TR_LOAN_FLAG,
            BigDecimal trloanAmount,
            String trLoanCurrency,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateCurrency,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency,
            BigDecimal usdToPhpSpecialConversionRate,
            String conversionStyle,
            String documentType,
            BigDecimal otherAmount,
            BigDecimal centavos
    ) {
        BigDecimal originalAmount = productAmount
        BigDecimal trLoanAmountPhp
        BigDecimal trLoanAmountNotPaidInPHP
        //convert tr loan amount to original amount
        if ("PHP".equalsIgnoreCase(trLoanCurrency)) {
            trLoanAmountPhp = trloanAmount
//            if ("PHP".equalsIgnoreCase(lccurrency)){
//                trLoanAmountNotPaidInPHP = originalAmount.subtract(trLoanAmountPhp)
//            }  else if ("USD".equalsIgnoreCase(lccurrency)){
//                BigDecimal trLoanAmountUSD = trloanAmount.divide(urr)
//                trLoanAmountNotPaidInPHP = originalAmount.subtract(trLoanAmountUSD)
//                trLoanAmountNotPaidInPHP = trLoanAmountNotPaidInPHP.multiply(usdToPhpSpecialConversionRate)
//            } else {
//                BigDecimal trLoanAmountTHIRD = trloanAmount.divide(thirdToUsdSpecialConversionRateCurrency.multiply(usdToPhpSpecialConversionRate),6,BigDecimal.ROUND_HALF_UP)
//                trLoanAmountNotPaidInPHP = originalAmount.subtract(trLoanAmountTHIRD)
//                trLoanAmountNotPaidInPHP = trLoanAmountNotPaidInPHP.multiply(thirdToUsdSpecialConversionRateCurrency.multiply(usdToPhpSpecialConversionRate))
//            }
        } else {
            //USD Default , TR
            println "trloanAmount trloanAmount trloanAmount trloanAmount:" + trloanAmount
            println "urr urr urr urr urr urr:" + urr
            println "trLoanAmountPhp trLoanAmountPhp trLoanAmountPhp trLoanAmountPhp trLoanAmountPhp trLoanAmountPhp:" + trloanAmount.multiply(urr)

            trLoanAmountPhp = trloanAmount.multiply(urr)
//            if ("USD".equalsIgnoreCase(lccurrency)){
//                trLoanAmountNotPaidInPHP = originalAmount.subtract(trloanAmount)
//                trLoanAmountNotPaidInPHP = trLoanAmountNotPaidInPHP.multiply(urr)
//            }  else if ("PHP".equalsIgnoreCase(lccurrency)){
//               trLoanAmountNotPaidInPHP = originalAmount.subtract(trloanAmount.multiply(usdToPhpSpecialConversionRate))
//            } else {
//                BigDecimal trLoanAmountTHIRD = trloanAmount.divide(thirdToUsdSpecialConversionRateCurrency,6,BigDecimal.ROUND_HALF_UP)
//                println "originalAmount"+originalAmount
//                println "trLoanAmountTHIRD"+trLoanAmountTHIRD
//                trLoanAmountNotPaidInPHP = originalAmount.subtract(trLoanAmountTHIRD)
//                println "trLoanAmountNotPaidInPHP"+trLoanAmountNotPaidInPHP
//                trLoanAmountNotPaidInPHP = trLoanAmountNotPaidInPHP.multiply(thirdToUsdSpecialConversionRateCurrency.multiply(usdToPhpSpecialConversionRate))
//                println "trLoanAmountNotPaidInPHP"+trLoanAmountNotPaidInPHP
//            }
        }


        println "trLoanAmountNotPaidInPHP:" + trLoanAmountNotPaidInPHP
        println "conversionStyle conversionStyle conversionStyle conversionStyle:" + conversionStyle


        if ("domestic".equalsIgnoreCase(documentType)) {
            productAmount = computeCorrectProductAmountDM(
                    productAmount,
                    lccurrency,
                    thirdToUsdSpecialConversionRateCurrency,
                    usdToPhpSpecialConversionRate,
                    urr,
                    conversionStyle
            )
        } else {
            productAmount = computeCorrectProductAmount(
                    productAmount,
                    lccurrency,
                    thirdToUsdSpecialConversionRateCurrency,
                    usdToPhpSpecialConversionRate,
                    urr,
                    conversionStyle
            )
        }


        trLoanAmountNotPaidInPHP = productAmount.subtract(trLoanAmountPhp)
        println "productAmount after conversion doc stamps:" + productAmount
        println "trLoanAmountPhp after conversion doc stamps:" + trLoanAmountPhp
        println "trLoanAmountNotPaidInPHP after conversion doc stamps:" + productAmount - trLoanAmountPhp
        println "trLoanAmountNotPaidInPHP after conversion doc stamps:" + trLoanAmountNotPaidInPHP

        BigDecimal temp;
        BigDecimal stepAmount = new BigDecimal("200")
        BigDecimal normallySettled = BigDecimal.ZERO;
        if (trloanAmount != null) {
            normallySettled = otherAmount
            trloanAmount = getRoundedNearest(trLoanAmountPhp, new BigDecimal("5000"))
        } else {
            normallySettled = productAmount;
            trloanAmount = BigDecimal.ZERO;
        }

        BigDecimal step = new BigDecimal("5000");
        BigDecimal payStep01 = new BigDecimal("20");
        BigDecimal payStep02 = new BigDecimal("10");
        BigDecimal holderTRLoan = BigDecimal.ZERO;
        if (trloanAmount.compareTo(step) > 0) {
            // if trloan amount is greater than step(5000) and tr
            holderTRLoan = payStep01.add(trloanAmount.subtract(step).divide(step, 2,BigDecimal.ROUND_UP).multiply(payStep02));
        } else if (trloanAmount.compareTo(BigDecimal.ZERO) > 0) {
            // if trloan amount is less than 500 but greater than zero
            holderTRLoan = payStep01;
        }

        BigDecimal holderNormallySettled = BigDecimal.ZERO;
        if (!"domestic".equalsIgnoreCase(documentType)) {
            if (normallySettled.compareTo(BigDecimal.ZERO) > 0) {
                println "getRoundedNearest200(normallySettled):" + getRoundedNearest200(normallySettled)
                holderNormallySettled = centavos.multiply(divideUp(getRoundedNearest200(normallySettled), stepAmount));
            }
        }




        BigDecimal holder = holderNormallySettled.add(holderTRLoan);

        BigDecimal settlementConversionRate = computeConversionRateSettlementCharges(
                lccurrency,
                settlementcurrency,
                urr,
                thirdToUsdSpecialConversionRateSettlementCurrency
        )
        println "settlementConversionRate:" + settlementConversionRate
        if (holder.compareTo(BigDecimal.ZERO) == 1) {
            return holder.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)
        } else {
            return BigDecimal.ZERO;
        }
    }

    def recompute_NONLC_RemittanceFee(
            BigDecimal remittanceMinimum,
            String remittanceFlag,
            String lccurrency,
            String settlementcurrency,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency
    ) {
        println "remittanceFlag:" + remittanceFlag
        BigDecimal settlementConversionRate = computeConversionRateSettlementCharges(
                lccurrency,
                settlementcurrency,
                urr,
                thirdToUsdSpecialConversionRateSettlementCurrency
        )
        println "settlementConversionRate:" + settlementConversionRate
        if ("Y".equalsIgnoreCase(remittanceFlag)) {
            return remittanceMinimum.multiply(urr).divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)
        } else {
            return BigDecimal.ZERO
        }
    }

    def recompute_NONLC_CableFee(
            BigDecimal cableFeeMinimum,
            String lccurrency,
            String settlementcurrency,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency,
            String cableFeeFlag
    ) {

        BigDecimal settlementConversionRate = computeConversionRateSettlementCharges(
                lccurrency,
                settlementcurrency,
                urr,
                thirdToUsdSpecialConversionRateSettlementCurrency
        )
        println "settlementConversionRate:" + settlementConversionRate
        if ("Y".equalsIgnoreCase(cableFeeFlag)) {
            return cableFeeMinimum.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)
        } else if ("null".equalsIgnoreCase(cableFeeFlag)) {
            return cableFeeMinimum.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)
        } else {
            return BigDecimal.ZERO
        }

    }


    def recompute_NONLC_NotarialFee(
            BigDecimal notarialFee,
            String lccurrency,
            String settlementcurrency,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency
    ) {

        BigDecimal settlementConversionRate = computeConversionRateSettlementCharges(
                lccurrency,
                settlementcurrency,
                urr,
                thirdToUsdSpecialConversionRateSettlementCurrency
        )

        return notarialFee.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)

    }


    def recompute_NONLC_BspCommission(
            BigDecimal bspCommissionMinimum,
            String lccurrency,
            String settlementcurrency,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency
    ) {

        BigDecimal settlementConversionRate = computeConversionRateSettlementCharges(
                lccurrency,
                settlementcurrency,
                urr,
                thirdToUsdSpecialConversionRateSettlementCurrency
        )
        println "settlementConversionRate:" + settlementConversionRate
        bspCommissionMinimum.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)
    }

    def recompute_NONLC_Booking(
            BigDecimal bookingFeeMinimum,
            String lccurrency,
            String settlementcurrency,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency,
            BigDecimal cwtPercentage,
            String cwtFlag
    ) {

        BigDecimal settlementConversionRate = computeConversionRateSettlementCharges(
                lccurrency,
                settlementcurrency,
                urr,
                thirdToUsdSpecialConversionRateSettlementCurrency
        )
        println "settlementConversionRate:" + settlementConversionRate
        println "cwtFlag:" + cwtFlag
        println "cwtPercentage:" + cwtPercentage
        if ("Y".equalsIgnoreCase(cwtFlag)) {
            return bookingFeeMinimum.multiply(cwtPercentage).divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)
        } else {
            return bookingFeeMinimum.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP)
        }

    }

    def recompute_NONLC_CILEX(
            BigDecimal productAmount,
            BigDecimal cilexNumerator,
            BigDecimal cilexDenominator,
            BigDecimal cilexPercentage,
            String cwtFlag,
            BigDecimal cwtPercentage,
            String lccurrency,
            String settlementcurrency,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateCurrency,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency,
            BigDecimal usdToPhpSpecialConversionRate
    ) {
        BigDecimal cilex = productAmount.multiply(cilexNumerator).multiply(cilexPercentage).divide(cilexDenominator, 9, BigDecimal.ROUND_HALF_UP);

        BigDecimal cilexMinimumInPhp = new BigDecimal("20");
        cilexMinimumInPhp = cilexMinimumInPhp.multiply(urr);

        System.out.println("cilexMinimumInPhp:" + cilexMinimumInPhp);
        System.out.println("cilex:" + cilex);
        if (cilex.compareTo(cilexMinimumInPhp) != 1) {
            cilex = cilexMinimumInPhp;
        }

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            cilex = cilex.multiply(cwtPercentage);
        }


        BigDecimal settlementConversionRate = computeConversionRateSettlementCharges(
                lccurrency,
                settlementcurrency,
                urr,
                thirdToUsdSpecialConversionRateSettlementCurrency
        )
        println "settlementConversionRate:" + settlementConversionRate
        println "cilex:" + cilex
        println "cilex divided by settlement conversion rate:" + cilex.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP);
        return cilex.divide(settlementConversionRate, 2, BigDecimal.ROUND_HALF_UP);

    }

    private static BigDecimal computeCorrectProductAmountDM(
            BigDecimal productAmount,
            String lccurrency,
            BigDecimal thirdToUsdSpecialConversionRateCurrency,
            BigDecimal usdToPhpSpecialConversionRate,
            BigDecimal urr,
            String conversionStyle
    ) {

        BigDecimal conversionRate = computeConversionRateProductAmountChargesDM(
                lccurrency,
                thirdToUsdSpecialConversionRateCurrency,
                usdToPhpSpecialConversionRate,
                urr,
                conversionStyle
        )
        return productAmount.multiply(conversionRate)

    }


    private static BigDecimal computeCorrectProductAmount(
            BigDecimal productAmount,
            String lccurrency,
            BigDecimal thirdToUsdSpecialConversionRateCurrency,
            BigDecimal usdToPhpSpecialConversionRate,
            BigDecimal urr,
            String conversionStyle
    ) {

        BigDecimal conversionRate = computeConversionRateProductAmountCharges(
                lccurrency,
                thirdToUsdSpecialConversionRateCurrency,
                usdToPhpSpecialConversionRate,
                urr,
                conversionStyle
        )
        return productAmount.multiply(conversionRate)

    }

    //TODO: Fix with sell rate
    private static BigDecimal computeConversionRateProductAmountCharges(
            String lccurrency,
            BigDecimal thirdToUsdSpecialConversionRateCurrency,
            BigDecimal usdToPhpSpecialConversionRate,
            BigDecimal urr,
            String conversionStyle
    ) {

        if (!lccurrency.equalsIgnoreCase("USD") && !lccurrency.equalsIgnoreCase("PHP")) {
            if (conversionStyle.equalsIgnoreCase("sell-sell")) {
                return usdToPhpSpecialConversionRate.multiply(thirdToUsdSpecialConversionRateCurrency)
            } else {
                return urr.multiply(thirdToUsdSpecialConversionRateCurrency)
            }

        } else if (lccurrency.equalsIgnoreCase("USD")) {
            if (conversionStyle.equalsIgnoreCase("sell-sell")) {
                return usdToPhpSpecialConversionRate
            } else {
                return urr
            }
        } else if (lccurrency.equalsIgnoreCase("PHP")) {
            return BigDecimal.ONE
        }

    }

    private static BigDecimal computeConversionRateProductAmountChargesDM(
            String lccurrency,
            BigDecimal thirdToUsdSpecialConversionRateCurrency,
            BigDecimal usdToPhpSpecialConversionRate,
            BigDecimal urr,
            String conversionStyle
    ) {

        if (!lccurrency.equalsIgnoreCase("USD") && !lccurrency.equalsIgnoreCase("PHP")) {
            if (conversionStyle.equalsIgnoreCase("sell-sell")) {
                return urr.multiply(thirdToUsdSpecialConversionRateCurrency)
            } else {
                return urr.multiply(thirdToUsdSpecialConversionRateCurrency)
            }

        } else if (lccurrency.equalsIgnoreCase("USD")) {
            if (conversionStyle.equalsIgnoreCase("sell-sell")) {
                return urr
            } else {
                return urr
            }
        } else if (lccurrency.equalsIgnoreCase("PHP")) {
            return BigDecimal.ONE
        }

    }

    private static BigDecimal computeConversionRateSettlementCharges(
            String lccurrency,
            String settlementcurrency,
            BigDecimal urr,
            BigDecimal thirdToUsdSpecialConversionRateSettlementCurrency
    ) {
        if (lccurrency.equalsIgnoreCase(settlementcurrency)) {
            // Use THIRD-USD Sell rate and usdToPhpSpecialConversionRate or usdToPhpSpecialConversionRate
            if (!settlementcurrency.equalsIgnoreCase("USD") && !settlementcurrency.equalsIgnoreCase("PHP")) {
                return urr.multiply(thirdToUsdSpecialConversionRateSettlementCurrency)
            } else if (lccurrency.equalsIgnoreCase("USD")) {
                return urr
            } else if (lccurrency.equalsIgnoreCase("PHP")) {
                return BigDecimal.ONE
            }

        } else {
            if (!settlementcurrency.equalsIgnoreCase("USD") && !settlementcurrency.equalsIgnoreCase("PHP")) {
                //paid in THIRD USD PESO
                return urr.multiply(thirdToUsdSpecialConversionRateSettlementCurrency)
            } else if (settlementcurrency.equalsIgnoreCase("USD")) {
                //paid in peso or usd
                return urr
            } else {
                //paid in peso or usd
                return BigDecimal.ONE
            }
        }
    }


}
