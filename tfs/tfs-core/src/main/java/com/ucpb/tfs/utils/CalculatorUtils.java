package com.ucpb.tfs.utils;

import org.jfree.date.SerialDate;
import org.jfree.date.SerialDateUtilities;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * User: Jett
 * Date: 7/25/12
 */

public class CalculatorUtils {

    /**
     * @param date
     * @return
     */
    public static BigDecimal getMonthsTill(String date) {

        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");

        try {
            Date endDate = formatter.parse(date);
            System.out.println("endDate:" + endDate.toString());
            System.out.println("startDate:" + new Date().toString());

            Integer days = SerialDateUtilities.dayCount30ISDA(
                    SerialDate.createInstance(new Date()),
                    SerialDate.createInstance(endDate)
            );

            // get a rounded up version of this using a math hack
            // rounding up: (numerator + denominator-1) / denominator
            // rounding down: (numerator + (denominator)/2) / denominator
            //System.out.println("months:" + ((days + 29) / 30));
            //return (days + 29) / 30;
            System.out.println("days:" + days);
            System.out.println("months:" + new BigDecimal(days.toString()).divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP));
            return new BigDecimal(days).divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP);

        } catch (Exception e) {
            // todo: handle invalid dates here
            return new BigDecimal(0);
        }
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
            System.out.println("months 360:" + new BigDecimal(days).divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP));
            System.out.println("months 365:" + new BigDecimal(daysInt).divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP));
            return new BigDecimal(daysInt).divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP);

        } catch (Exception e) {
            // todo: handle invalid dates here
            return new BigDecimal(0);
        }
    }

    public static BigDecimal divideUp(BigDecimal numerator, BigDecimal denominator) {
        //Added default scale because dividing small numbers were returning zero 1/8 or 1/4
        return numerator.divide(denominator, 20, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal divideUp(BigDecimal numerator, BigDecimal denominator, int scale) {

        return numerator.divide(denominator, scale, BigDecimal.ROUND_HALF_UP);

    }

    // converts an amount to peso using the rate provided
    public static BigDecimal Pesoize(BigDecimal amount, BigDecimal toPesoRate) {
        return new BigDecimal(0.0);
    }

    //returns number of months rounded up
    public static BigDecimal getMonthsOf(BigDecimal daysPeriod) {
        if (daysPeriod == null) {
            daysPeriod = new BigDecimal("0");
        }
        return daysPeriod.divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP);
    }

    //TODO :Used by nego incomplete must pass docstamp amount for fxlc opening
    public static BigDecimal getDocStampsAmountBasic(BigDecimal docAmount, BigDecimal oldDocAmount) {
        System.out.println("docAmount:" + docAmount);
        System.out.println("oldDocAmount:" + oldDocAmount);
        BigDecimal step = new BigDecimal("5000");
        BigDecimal payStep01 = new BigDecimal("20");
        BigDecimal payStep02 = new BigDecimal("10");
        if (docAmount.subtract(oldDocAmount).compareTo(step) < 1) {
            return payStep01; //less than or equal to 5000
        } else {
            BigDecimal holder = payStep01.add(docAmount.subtract(step).divide(step, BigDecimal.ROUND_HALF_UP).multiply(payStep02)).subtract(oldDocAmount);
            if (holder.compareTo(new BigDecimal(0)) < 1) {
                return new BigDecimal(0);
            } else {
                return holder;
            }
        }

    }

    public static BigDecimal getNotarialAmount(BigDecimal notarialAmount) {
        return notarialAmount;
    }

    public static BigDecimal getCableFee(BigDecimal cableFee) {
        return cableFee;
    }

    public static BigDecimal getSuppliesFee(BigDecimal suppliesFee) {
        return suppliesFee;
    }

    public static BigDecimal getBankCommission(
            String expiryDate,
            BigDecimal bankCommissionNumerator,
            BigDecimal bankCommissionDenominator,
            BigDecimal bankCommissionPercentage,
            BigDecimal productAmount,
            BigDecimal cwtPercentage,
            String cwtFlag
    ) {
        BigDecimal months = getMonthsTill(expiryDate);
        System.out.println(months);
        if (months.compareTo(BigDecimal.ONE) < 1) {
            months = BigDecimal.ONE;
        }

        BigDecimal result = multiplyNumeratorDenominatorPercentageAmountMonths(bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, productAmount, months);
        System.out.println("result" + result);
        if (result.compareTo(new BigDecimal("1000")) < 1) {
            result = new BigDecimal("1000.00");
        }

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            result = result.multiply(cwtPercentage);
        }

        return result;
    }

    public static BigDecimal getCommitmentFee_LC_Opening(
            BigDecimal productAmount,
            BigDecimal usancePeriod,
            String expiryDate,
            String documentSubType1,
            String documentSubType2,
            BigDecimal commitmentFeeNumerator,
            BigDecimal commitmentFeeDenominator,
            BigDecimal commitmentFeePercentage,
            String cwtFlag,
            BigDecimal cwtPercentage
    ) {
        if (commitmentFeeNumerator == null) {
            commitmentFeeNumerator = new BigDecimal("1");
        }
        if (commitmentFeeDenominator == null) {
            commitmentFeeDenominator = new BigDecimal("4");
        }
        if (commitmentFeePercentage == null) {
            commitmentFeePercentage = new BigDecimal("0.01");
        } else if (commitmentFeePercentage.compareTo(BigDecimal.ONE) == 1) {
            commitmentFeePercentage = commitmentFeePercentage.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        }

        if (cwtPercentage == null) {
            cwtPercentage = new BigDecimal("0.98");
        }

        BigDecimal result = BigDecimal.ZERO;

        if ("CASH".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            return BigDecimal.ZERO;
        } else if ("STANDBY".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            BigDecimal months = getMonthsTill(expiryDate);
            if (months.compareTo(BigDecimal.ONE) != 1) {
                months = BigDecimal.ONE;
            }
            result = productAmount.multiply(divideUp(commitmentFeeNumerator, commitmentFeeDenominator)).multiply(commitmentFeePercentage).multiply(months);
            System.out.println("months" + months);
            System.out.println("result:" + result);
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "SIGHT".equalsIgnoreCase(documentSubType2)) {
            result = productAmount.multiply(divideUp(commitmentFeeNumerator, commitmentFeeDenominator)).multiply(commitmentFeePercentage);
            System.out.println("result:" + result);
        } else if ("REGULAR".equalsIgnoreCase(documentSubType1) && "USANCE".equalsIgnoreCase(documentSubType2)) {
            BigDecimal months = getMonthsOf(usancePeriod);

            if (months.compareTo(BigDecimal.ONE) != 1) {
                months = BigDecimal.ONE;
            }

            result = productAmount.multiply(divideUp(commitmentFeeNumerator, commitmentFeeDenominator)).multiply(commitmentFeePercentage).multiply(months);
            System.out.println("month" + months);
            System.out.println("result:" + result);
        }

        //Minimum Checking here
        BigDecimal minimumPhp = new BigDecimal("500");
        BigDecimal resultAfterCwt = new BigDecimal("0");
        if (result.compareTo(minimumPhp) != 1) {
            resultAfterCwt = minimumPhp.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            resultAfterCwt = result.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        if ("Y".equalsIgnoreCase(cwtFlag.trim())) {
            resultAfterCwt = resultAfterCwt.multiply(cwtPercentage);
        }

        return resultAfterCwt.setScale(2, BigDecimal.ROUND_HALF_UP);

    }


    public static BigDecimal getCilex(
            String expiryDate,
            BigDecimal productChargeAmountNetOfPesoAmountPaid,
            BigDecimal cilexNumerator,
            BigDecimal cilexDenominator,
            BigDecimal cilexPercentage,
            BigDecimal usdToPHPSpecialRate,
            String cwtFlag,
            BigDecimal cwtPercentage,
            BigDecimal cilexDollarMinimum
    ) {
        System.out.println("expiryDate:" + expiryDate);
        System.out.println("productChargeAmountNetOfPesoAmountPaid:" + productChargeAmountNetOfPesoAmountPaid);
        System.out.println("cilexNumerator:" + cilexNumerator);
        System.out.println("cilexDenominator:" + cilexDenominator);
        System.out.println("cilexPercentage:" + cilexPercentage);
        System.out.println("usdToPHPSpecialRate:" + usdToPHPSpecialRate);
        BigDecimal months = getMonthsTill(expiryDate);
        System.out.println("months:" + months);
        if (months.compareTo(BigDecimal.ONE) != 1) {
            months = BigDecimal.ONE;
        }
        BigDecimal result = multiplyNumeratorDenominatorPercentageAmount(cilexNumerator, cilexDenominator, cilexPercentage, productChargeAmountNetOfPesoAmountPaid);
        System.out.println("cilex result:" + result);
        BigDecimal dollarMinimum = multiplyTwoValues(usdToPHPSpecialRate, cilexDollarMinimum);
        System.out.println("DollarMinimum:" + dollarMinimum);
        if (result.compareTo(dollarMinimum) != 1) {
            result = dollarMinimum;
        }

        System.out.println("cilex after twenty dollar minimum:" + result);
        if ("Y".equalsIgnoreCase(cwtFlag)) {
            result = result.multiply(cwtPercentage);
        }
        System.out.println("cilex after cwt:" + result);

        return result;
    }


    public static BigDecimal getAdvisingFee_FXLC_Opening(
            BigDecimal usdToPHPSpecialRate,
            BigDecimal advisingFee,
            String advanceCorresChargesFlag,
            BigDecimal default_advisingFeeMinimum
    ) {
        System.out.println("usdToPHPSpecialRate:" + usdToPHPSpecialRate);
        System.out.println("advisingFee:" + advisingFee);
        System.out.println("advanceCorresChargesFlag:" + advanceCorresChargesFlag);


        if (!"Y".equalsIgnoreCase(advanceCorresChargesFlag)) {
            return BigDecimal.ZERO;
        }

        BigDecimal fiftyDollarMinimum = CalculatorUtils.multiplyTwoValues(usdToPHPSpecialRate, default_advisingFeeMinimum);
        System.out.println("fiftyDollarMinimum:" + fiftyDollarMinimum);
        BigDecimal result = advisingFee;
        if (advisingFee.compareTo(fiftyDollarMinimum) != 1) {
            result = fiftyDollarMinimum;
        }
        System.out.println("result after fifty dollar minimum" + result);
        return result;
    }

    public static BigDecimal getConfirmingFee_FXLC_Opening(
            String expiryDate,
            BigDecimal confirmingFeeNumerator,
            BigDecimal confirmingFeeDenominator,
            BigDecimal confirmingFeePercentage,
            BigDecimal productAmount,
            String advanceCorresChargesFlag,
            String confirmationInstructionsFlag,
            BigDecimal usdToPHPSpecialRate,
            BigDecimal default_confirmingFeeMinimum) {
        System.out.println("expiryDate:" + expiryDate);
        System.out.println("confirmingFeeNumerator:" + confirmingFeeNumerator);
        System.out.println("confirmingFeeDenominator:" + confirmingFeeDenominator);
        System.out.println("confirmingFeePercentage:" + confirmingFeePercentage);
        System.out.println("productAmount:" + productAmount);
        System.out.println("advanceCorresChargesFlag:" + advanceCorresChargesFlag);
        System.out.println("confirmationInstructionsFlag:" + confirmationInstructionsFlag);

        if (!("Y".equalsIgnoreCase(advanceCorresChargesFlag)
                || "Y".equalsIgnoreCase(confirmationInstructionsFlag))) {
            return BigDecimal.ZERO;
        }

        BigDecimal months = CalculatorUtils.getMonthsTill(expiryDate);
        System.out.println("months:" + months);
        if (months.compareTo(BigDecimal.ONE) != 1) {
            months = BigDecimal.ONE;
        }
        BigDecimal tmp = CalculatorUtils.multiplyNumeratorDenominatorPercentageAmountMonths(confirmingFeeNumerator, confirmingFeeDenominator, confirmingFeePercentage, productAmount, months);
        BigDecimal fiftyDollarMinimum = CalculatorUtils.multiplyTwoValues(usdToPHPSpecialRate, default_confirmingFeeMinimum);
        System.out.println("fiftyDollarMinimum" + fiftyDollarMinimum);
        if (tmp.compareTo(fiftyDollarMinimum) != 1) {
            tmp = fiftyDollarMinimum;
        }

        System.out.println("after dollar minimum" + tmp);
        return tmp;
    }


    /**
     * LC OPENING
     */

    public static BigDecimal getDocStamps_FXLC_Opening(
            String expiryDate,
            BigDecimal productAmount,
            BigDecimal docStampsAmountPer,
            BigDecimal docStampsRoundToThisNumber
    ) {
        System.out.println("expiryDate:" + expiryDate);
        System.out.println("productAmount:" + productAmount);
        BigDecimal months = getMonthsTill(expiryDate);
        System.out.println(months);

        if (months.compareTo(new BigDecimal(1)) <= 0) {
            months = new BigDecimal(1);
        }
        System.out.println(months);
        System.out.println("docstamps productAmount:" + new BigDecimal("0.30").multiply(CalculatorUtils.divideUp(CalculatorUtils.getRoundedNearest200(productAmount), new BigDecimal("200"))));

//        BigDecimal result = new BigDecimal("0.30").multiply(divideUp(getRoundedNearest200(productAmount), new BigDecimal("200")));
        BigDecimal result = docStampsAmountPer.multiply(divideUp(getRoundedNearestValue(productAmount, docStampsRoundToThisNumber), docStampsRoundToThisNumber));
        System.out.println("result" + result);

        return result;
    }

    public static BigDecimal getCommitmentFee_FXLC_Opening(
            BigDecimal productAmount,
            BigDecimal usancePeriod,
            String expiryDate,
            String documentSubType1,
            String documentSubType2,
            BigDecimal commitmentFeeNumerator,
            BigDecimal commitmentFeeDenominator,
            BigDecimal commitmentFeePercentage,
            String cwtFlag,
            BigDecimal cwtPercentage
    ) {
        return getCommitmentFee_LC_Opening(
                productAmount,
                usancePeriod,
                expiryDate,
                documentSubType1,
                documentSubType2,
                commitmentFeeNumerator,
                commitmentFeeDenominator,
                commitmentFeePercentage,
                cwtFlag,
                cwtPercentage);
    }

    public static BigDecimal getCommitmentFee_DMLC_Opening(
            BigDecimal productAmount,
            BigDecimal usancePeriod,
            String expiryDate,
            String documentSubType1,
            String documentSubType2,
            BigDecimal commitmentFeeNumerator,
            BigDecimal commitmentFeeDenominator,
            BigDecimal commitmentFeePercentage,
            String cwtFlag,
            BigDecimal cwtPercentage
    ) {
        return getCommitmentFee_LC_Opening(
                productAmount,
                usancePeriod,
                expiryDate,
                documentSubType1,
                documentSubType2,
                commitmentFeeNumerator,
                commitmentFeeDenominator,
                commitmentFeePercentage,
                cwtFlag,
                cwtPercentage);
    }


    /**
     * LC AMENDMENT
     */

    public static BigDecimal getBankCommission_FXLC_Amendment(
            String tenorCheck,
            String amountSwitch,
            String lcAmountFlag,
            String expiryDateCheck,
            String expiryDateFlag,
            String changeInConfirmationCheck,
            String narrativesCheck,
            BigDecimal bankCommissionNumerator,
            BigDecimal bankCommissionDenominator,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            BigDecimal expiryDateModifiedDays,
            String expiryDate,
            String expiryDateTo,
            BigDecimal bankCommissionPercentage,
            String cwtFlag,
            BigDecimal cwtPercentage
    ) {
        System.out.println("tenorCheck:" + tenorCheck);
        System.out.println("amountSwitch:" + amountSwitch);
        System.out.println("lcAmountFlag:" + lcAmountFlag);
        System.out.println("expiryDateCheck:" + expiryDateCheck);
        System.out.println("expiryDateFlag:" + expiryDateFlag);
        System.out.println("changeInConfirmationCheck:" + changeInConfirmationCheck);
        System.out.println("narrativesCheck:" + narrativesCheck);
        System.out.println("bankCommissionNumerator:" + bankCommissionNumerator);
        System.out.println("bankCommissionDenominator:" + bankCommissionDenominator);
        System.out.println("amountFrom:" + amountFrom);
        System.out.println("amountTo:" + amountTo);
        System.out.println("expiryDateModifiedDays:" + expiryDateModifiedDays);
        System.out.println("expiryDate:" + expiryDate);
        System.out.println("expiryDateTo:" + expiryDateTo);
        System.out.println("bankCommissionPercentage:" + bankCommissionPercentage);
        System.out.println("cwtFlag:" + cwtFlag);
        System.out.println("cwtPercentage:" + cwtPercentage);


        BigDecimal months = new BigDecimal("0");


        if (expiryDate != null) {
            months = getMonthsTill(expiryDate);
        }

        if (expiryDateModifiedDays == null) {
            expiryDateModifiedDays = new BigDecimal("0");
        }
        if (bankCommissionNumerator == null) {
            bankCommissionNumerator = new BigDecimal("1");
        }

        if (bankCommissionDenominator == null) {
            bankCommissionDenominator = new BigDecimal("8");
        }

        if (bankCommissionPercentage == null) {
            bankCommissionPercentage = new BigDecimal("0.01");
        } else if (!(bankCommissionPercentage.compareTo(BigDecimal.ONE) < 1)) {
            bankCommissionPercentage = bankCommissionPercentage.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        }

        if (cwtPercentage == null) {
            cwtPercentage = new BigDecimal("0.98");
        }

//        System.out.println("months");

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


        if ("on".equalsIgnoreCase(amountSwitch)
                && "INC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "EXT".equalsIgnoreCase(expiryDateFlag)
                ) {//Increase Amount and Extend Expiry

            //COMPUTED part
            //Bank Commission due to increase in FXLC Amount
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
                oldMonths = months;
                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
                System.out.println("oldMonths:" + oldMonths);
                System.out.println("bankCommissionPercentage:" + bankCommissionPercentage);
                System.out.println("bankCommissionNumerator:" + bankCommissionNumerator);
                System.out.println("bankCommissionDenominator:" + bankCommissionDenominator);
                System.out.println("amount change:" + amountTo.subtract(amountFrom));
                computedLcAmount = amountTo.subtract(amountFrom);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
                computedLcAmount = computedLcAmount.multiply(bankCommissionPercentage);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
                computedLcAmount = computedLcAmount.multiply(oldMonths);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
                computedLcAmount = computedLcAmount.multiply(bankCommissionNumerator);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
                computedLcAmount = computedLcAmount.divide(bankCommissionDenominator, 20, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
            }

            //Bank Commission due to extension of Expiry Date
            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                newMonths = getMonthsTill(expiryDate, expiryDateTo);
                System.out.println("newMonths before compareTo:" + newMonths);
                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
                System.out.println("newMonths before compareTo:" + newMonths);
                computedLcExpiry = fixed.add(amountFrom).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 20, BigDecimal.ROUND_HALF_UP).multiply(bankCommissionPercentage).multiply(newMonths).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
            }

            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && "on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                System.out.println("bothMonths:" + bothMonths);
                computedBoth = (amountTo.subtract(amountFrom)).multiply(bankCommissionPercentage).multiply(newMonths).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 20, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computed: lcAmount both:" + computedBoth);

            }

        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "INC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "RED".equalsIgnoreCase(expiryDateFlag)
                ) {//Increase amount and Decrease Expiry
            //COMPUTED part
            //Bank Commission due to increase in FXLC Amount
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
                oldMonths = months;
                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
                System.out.println("oldMonths:" + oldMonths);
                System.out.println("bankCommissionPercentage:" + bankCommissionPercentage);
                System.out.println("bankCommissionNumerator:" + bankCommissionNumerator);
                System.out.println("bankCommissionDenominator:" + bankCommissionDenominator);
                System.out.println("amount change:" + amountTo.subtract(amountFrom));
                computedLcAmount = amountTo.subtract(amountFrom);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
                computedLcAmount = computedLcAmount.multiply(bankCommissionPercentage);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
                computedLcAmount = computedLcAmount.multiply(oldMonths);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
                computedLcAmount = computedLcAmount.multiply(bankCommissionNumerator);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
                computedLcAmount = computedLcAmount.divide(bankCommissionDenominator, 20, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computed: lcAmount increase:" + computedLcAmount);
            }

        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "DEC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "EXT".equalsIgnoreCase(expiryDateFlag)
                ) {//Increase amount and Decrease Expiry
            //Bank Commission due to extension of Expiry Date
            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                newMonths = getMonthsTill(expiryDate, expiryDateTo);
                System.out.println("newMonths before compareTo:" + newMonths);
                newMonths = newMonths.compareTo(new BigDecimal("1")) != 1 ? new BigDecimal(1) : newMonths;
                System.out.println("newMonths before compareTo:" + newMonths);
                computedLcExpiry = fixed.add(amountTo).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 20, BigDecimal.ROUND_HALF_UP).multiply(bankCommissionPercentage).multiply(newMonths).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computed: expiryDate extension:" + computedLcExpiry);
            }

        } else if ("on".equalsIgnoreCase(amountSwitch)
                && "DEC".equalsIgnoreCase(lcAmountFlag)
                && "on".equalsIgnoreCase(expiryDateCheck)
                && "RED".equalsIgnoreCase(expiryDateFlag)
                ) {//Increase amount and Decrease Expiry
            fixed = new BigDecimal("500");
        }

        result = addToResultIfNotNegativeOrZero(result, computedLcAmount);
        result = addToResultIfNotNegativeOrZero(result, computedLcExpiry);
        result = addToResultIfNotNegativeOrZero(result, computedBoth);

        //Minimum Checking here
        BigDecimal minimumPhp = new BigDecimal("500");
        BigDecimal resultAfterCwt = new BigDecimal("0");
        if (result.compareTo(minimumPhp) != 1) {
            resultAfterCwt = minimumPhp.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            resultAfterCwt = result.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        if ("Y".equalsIgnoreCase(cwtFlag.trim())) {
            resultAfterCwt = resultAfterCwt.multiply(cwtPercentage);
        }

        return resultAfterCwt.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    //TODO add as parameter cableFee
    public static BigDecimal getCableFeeFxlcAmendment(
            String tenorCheck,
            String amountSwitch,
            String expiryDateCheck,
            String changeInConfirmationCheck,
            String narrativesCheck
    ) {

        System.out.println("tenorCheck:" + tenorCheck);
        System.out.println("amountSwitch:" + amountSwitch);
        System.out.println("expiryDateCheck:" + expiryDateCheck);
        System.out.println("changeInConfirmationCheck:" + changeInConfirmationCheck);
        System.out.println("narrativesCheck:" + narrativesCheck);

        BigDecimal fixed = new BigDecimal(0);

        if ((tenorCheck.equalsIgnoreCase("on")) || (amountSwitch.equalsIgnoreCase("on")) || (expiryDateCheck.equalsIgnoreCase("on")) || (changeInConfirmationCheck.equalsIgnoreCase("on")) || (narrativesCheck.equalsIgnoreCase("on"))) {
            fixed = new BigDecimal(500);
        }

        return fixed;
    }

    //TODO add as parameter confirmingFeePercentage
    public static BigDecimal getConfirmingFeeFxlcAmendment(
            String amountSwitch,
            String lcAmountFlag,
            String expiryDateCheck,
            String expiryDateFlag,
            String changeInConfirmationCheck,
            String advanceCorresChargesFlag,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            BigDecimal expiryDateModifiedDays,
            BigDecimal confirmingFeeNumerator,
            BigDecimal confirmingFeeDenominator,
            String expiryDate,
            String expiryDateTo,
            String processingDate,
            BigDecimal confirmingFeePercentage,
            BigDecimal usdToPHPSpecialRate,
            BigDecimal confirmingFeeMinimum
    ) {

        System.out.println("amountSwitch:" + amountSwitch);
        System.out.println("lcAmountFlag:" + lcAmountFlag);
        System.out.println("expiryDateCheck:" + expiryDateCheck);
        System.out.println("expiryDateFlag:" + expiryDateFlag);
        System.out.println("changeInConfirmationCheck:" + changeInConfirmationCheck);
        System.out.println("advanceCorresChargesFlag:" + advanceCorresChargesFlag);
        System.out.println("amountFrom:" + amountFrom);
        System.out.println("amountTo:" + amountTo);
        System.out.println("expiryDateModifiedDays:" + expiryDateModifiedDays);
        System.out.println("confirmingFeeNumerator:" + confirmingFeeNumerator);
        System.out.println("confirmingFeeDenominator:" + confirmingFeeDenominator);
        System.out.println("expiryDate:" + expiryDate);
        System.out.println("expiryDateTo:" + expiryDateTo);
        System.out.println("processingDate:" + processingDate);
        System.out.println("confirmingFeePercentage:" + confirmingFeePercentage);
        System.out.println("usdToPHPSpecialRate:" + usdToPHPSpecialRate);
        System.out.println("confirmingFeeMinimum:" + confirmingFeeMinimum);


        BigDecimal months = new BigDecimal("0");
        if (expiryDate != null) {
            months = getMonthsTill(expiryDate);
        }

        if (expiryDateModifiedDays == null) {
            expiryDateModifiedDays = new BigDecimal("0");
        }

        if (confirmingFeeNumerator == null) {
            confirmingFeeNumerator = new BigDecimal("1");
        }
        if (confirmingFeeDenominator == null) {
            confirmingFeeDenominator = new BigDecimal("8");
        }
        if (confirmingFeePercentage == null) {
            confirmingFeePercentage = new BigDecimal("0.01");
        } else if (confirmingFeePercentage.compareTo(BigDecimal.ONE) == 1) {
            System.out.println("within");
            confirmingFeePercentage = confirmingFeePercentage.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            System.out.println("confirmingFeePercentage:" + confirmingFeePercentage);
        }

        System.out.println("confirmingFeePercentage:" + confirmingFeePercentage);
        BigDecimal cfLcAmountIncrease = BigDecimal.ZERO;
        BigDecimal cfExpiryExtension = BigDecimal.ZERO;
        BigDecimal cfChangeInConfirmationInstruction = BigDecimal.ZERO;
        BigDecimal computedBoth = BigDecimal.ZERO;


        BigDecimal newMonths = BigDecimal.ZERO;
        BigDecimal oldMonths = BigDecimal.ZERO;
        BigDecimal result = BigDecimal.ZERO;


        if ("Y".equalsIgnoreCase(advanceCorresChargesFlag)) {


            if ("on".equalsIgnoreCase(amountSwitch)
                    && "INC".equalsIgnoreCase(lcAmountFlag)
                    && "on".equalsIgnoreCase(expiryDateCheck)
                    && "EXT".equalsIgnoreCase(expiryDateFlag)
                    ) {//Increase Amount and Extend Expiry

            } else if ("on".equalsIgnoreCase(amountSwitch)
                    && "INC".equalsIgnoreCase(lcAmountFlag)
                    && "on".equalsIgnoreCase(expiryDateCheck)
                    && "RED".equalsIgnoreCase(expiryDateFlag)
                    ) {//Increase amount and Decrease Expiry

            } else if ("on".equalsIgnoreCase(amountSwitch)
                    && "DEC".equalsIgnoreCase(lcAmountFlag)
                    && "on".equalsIgnoreCase(expiryDateCheck)
                    && "EXT".equalsIgnoreCase(expiryDateFlag)
                    ) {//Increase amount and Decrease Expiry

            } else if ("on".equalsIgnoreCase(amountSwitch)
                    && "DEC".equalsIgnoreCase(lcAmountFlag)
                    && "on".equalsIgnoreCase(expiryDateCheck)
                    && "RED".equalsIgnoreCase(expiryDateFlag)
                    ) {//Increase amount and Decrease Expiry

            }


            //COMPUTED part
            //Confirming Fee due to increase in FXLC Amount
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
                oldMonths = months;
                oldMonths = oldMonths.compareTo(new BigDecimal(1)) < 1 ? new BigDecimal(1) : oldMonths;
                cfLcAmountIncrease = (amountTo.subtract(amountFrom)).multiply(confirmingFeePercentage).multiply(oldMonths).multiply(confirmingFeeNumerator).divide(confirmingFeeDenominator, 20, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("cfLcAmountIncrease:" + cfLcAmountIncrease);
            }

            //Confirming Fee due to extension of Expiry Date
            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                newMonths = getMonthsTill(expiryDateTo);
                newMonths = newMonths.compareTo(new BigDecimal(1)) < 1 ? new BigDecimal(1) : newMonths;
                cfExpiryExtension = amountFrom.multiply(confirmingFeeNumerator).divide(confirmingFeeDenominator, 20, BigDecimal.ROUND_HALF_UP).multiply(confirmingFeePercentage).multiply(newMonths).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("cfExpiryExtension:" + cfExpiryExtension);
            }

            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && "on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                computedBoth = (amountTo.subtract(amountFrom)).multiply(confirmingFeePercentage).multiply(newMonths).multiply(confirmingFeeNumerator).divide(confirmingFeeDenominator, 20, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computedBoth:" + computedBoth);
            }

            if ("on".equalsIgnoreCase(changeInConfirmationCheck)) {
//                BigDecimal monthsBetween = new BigDecimal(getMonthsTill(processingDate, expiryDate));
                BigDecimal monthsBetween = getMonthsTill(processingDate, expiryDate);
                BigDecimal amnt = amountTo;
                if (amnt.compareTo(BigDecimal.ZERO) < 1) {
                    amnt = amountFrom;
                }
                System.out.println("amnt:" + amnt);
                System.out.println("monthsBetween:" + monthsBetween);
                System.out.println("confirmingFeeNumerator:" + confirmingFeeNumerator);
                System.out.println("confirmingFeeDenominator:" + confirmingFeeDenominator);
                System.out.println("confirmingFeePercentage:" + confirmingFeePercentage);

                cfChangeInConfirmationInstruction = amnt.multiply(confirmingFeeNumerator).divide(confirmingFeeDenominator, 20, BigDecimal.ROUND_HALF_UP).multiply(confirmingFeePercentage).multiply(monthsBetween).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("cfChangeInConfirmationInstruction:" + cfChangeInConfirmationInstruction);
            }

            // 50 USD Minimum

            result = addToResultIfNotNegativeOrZero(result, cfLcAmountIncrease);
            result = addToResultIfNotNegativeOrZero(result, cfExpiryExtension);
            result = addToResultIfNotNegativeOrZero(result, computedBoth);
            result = addToResultIfNotNegativeOrZero(result, cfChangeInConfirmationInstruction);


            //TODO: Implement minimum amount if(result)


            System.out.println("result:" + result);
        }
        return result;
    }

    //TODO find out how to determine if Advising Fee is billable to the client
    public static BigDecimal getAdvisingFeeFxlcAmendment(
            String tenorCheck,
            String amountSwitch,
            String lcAmountFlag,
            String changeInConfirmationCheck,
            BigDecimal usdToPHPSpecialRate
    ) {
        System.out.println("tenorCheck:" + tenorCheck);
        System.out.println("amountSwitch:" + amountSwitch);
        System.out.println("lcAmountFlag:" + lcAmountFlag);
        System.out.println("changeInConfirmationCheck:" + changeInConfirmationCheck);
        System.out.println("usdToPHPSpecialRate:" + usdToPHPSpecialRate);

        if ("on".equalsIgnoreCase(tenorCheck) ||
                ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) ||
                        ("on".equalsIgnoreCase(changeInConfirmationCheck)))) {
            return new BigDecimal("50").multiply(usdToPHPSpecialRate);
        } else {
            return new BigDecimal("0");
        }
    }

    public static BigDecimal getDocStampsFxlcAmendment(
            String amountSwitch,
            String lcAmountFlag,
            BigDecimal amountFrom,
            BigDecimal amountTo
    ) {
        BigDecimal stepAmountSize = new BigDecimal("200");
        BigDecimal stepAmountPerCent = new BigDecimal("0.30");
        System.out.println("amountSwitch:" + amountSwitch);
        System.out.println("lcAmountFlag:" + lcAmountFlag);
        System.out.println("amountFrom:" + amountFrom);
        System.out.println("amountTo:" + amountTo);

        if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
            System.out.println(amountTo.subtract(amountFrom));
            System.out.println(amountTo.subtract(amountFrom).divide(stepAmountSize, 20, BigDecimal.ROUND_HALF_UP));
            System.out.println(amountTo.subtract(amountFrom).divide(stepAmountSize, 20, BigDecimal.ROUND_HALF_UP).multiply(stepAmountPerCent));
            return (amountTo.subtract(amountFrom).divide(stepAmountSize, 20, BigDecimal.ROUND_HALF_UP).multiply(stepAmountPerCent)).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return new BigDecimal("0");
        }
    }

    //TODO: Fix usancePeriodFrom and usancePeriodTo support
    public static BigDecimal getCommitmentFeeFxlcAmendment(
            String tenorCheck,
            String amountSwitch,
            String lcAmountFlag,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            BigDecimal usancePeriod,
            String expiryDate,
            String documentSubType1,
            String documentSubType2,
            BigDecimal commitmentFeeNumerator,
            BigDecimal commitmentFeeDenominator,
            BigDecimal commitmentFeePercentage
    ) {

        System.out.println("tenorCheck:" + tenorCheck);
        System.out.println("amountSwitch:" + amountSwitch);
        System.out.println("lcAmountFlag:" + lcAmountFlag);
        System.out.println("amountFrom:" + amountFrom);
        System.out.println("amountTo:" + amountTo);
        System.out.println("usancePeriod:" + usancePeriod);
        System.out.println("expiryDate:" + expiryDate);
        System.out.println("documentSubType1:" + documentSubType1);
        System.out.println("documentSubType2:" + documentSubType2);
        System.out.println("commitmentFeeNumerator:" + commitmentFeeNumerator);
        System.out.println("commitmentFeeDenominator:" + commitmentFeeDenominator);
        System.out.println("commitmentFeePercentage:" + commitmentFeePercentage);

        if (commitmentFeeNumerator == null) {
            commitmentFeeNumerator = new BigDecimal("1");
        }
        if (commitmentFeeDenominator == null) {
            commitmentFeeDenominator = new BigDecimal("4");
        }
        if (commitmentFeePercentage == null) {
            commitmentFeePercentage = new BigDecimal("0.01");
        } else if (commitmentFeePercentage.compareTo(BigDecimal.ONE) == 1) {
            commitmentFeePercentage = commitmentFeePercentage.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        }

        System.out.println("commitmentFeePercentage:" + commitmentFeePercentage);

        BigDecimal result = new BigDecimal("0");
        BigDecimal resultLcAmount = new BigDecimal("0");
        BigDecimal resultTenor = new BigDecimal("0");
        Boolean cfExists = new Boolean(false);
        if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
            BigDecimal months = getMonthsTill(expiryDate); //TODO: Verify not stated in docs assumed by angol
            if (months.compareTo(new BigDecimal("1")) < 1) {
                months = new BigDecimal("1");
            }
            resultLcAmount = divideUp(commitmentFeeNumerator, commitmentFeeDenominator).multiply(commitmentFeePercentage).multiply(amountFrom).multiply(months);
            cfExists = new Boolean(true);
            System.out.println("resultLcAmount:" + resultLcAmount);
        }

        if ("on".equalsIgnoreCase(tenorCheck)) {
            BigDecimal months = getMonthsOf(usancePeriod);  //TODO: Verify not stated in docs assumed by angol
            if (months.compareTo(new BigDecimal("1")) < 1) {
                months = new BigDecimal("1");
            }

            System.out.println("months:" + months);
            System.out.println("commitmentFeeNumerator:" + commitmentFeeNumerator);
            System.out.println("commitmentFeeDenominator:" + commitmentFeeDenominator);
            System.out.println("commitmentFeePercentage:" + commitmentFeePercentage);
            System.out.println("amountFrom:" + amountFrom);

            resultTenor = divideUp(commitmentFeeNumerator, commitmentFeeDenominator);
            System.out.println("resultTenor:" + resultTenor);
            resultTenor = resultTenor.multiply(commitmentFeePercentage);
            System.out.println("resultTenor:" + resultTenor);
            resultTenor = resultTenor.multiply(amountFrom);
            System.out.println("resultTenor:" + resultTenor);
            resultTenor = resultTenor.multiply(months);
            System.out.println("resultTenor:" + resultTenor);
            cfExists = new Boolean(true);
        }

        if (cfExists) {
            result = result.add(resultLcAmount);
            result = result.add(resultTenor);
            System.out.println("result:" + result);
            if ((result.compareTo(new BigDecimal("500"))) < 1) {
                return new BigDecimal("500");
            } else {
                return result;
            }
        } else {
            return new BigDecimal("0");
        }

    }


    //TODO: Apply CWT
    public static BigDecimal getBankCommissionDmlcAmendment(
            String tenorCheck,
            String amountSwitch,
            String lcAmountFlag,
            String expiryDateCheck,
            String expiryDateFlag,
            String changeInConfirmationCheck,
            String narrativesCheck,
            BigDecimal bankCommissionNumerator,
            BigDecimal bankCommissionDenominator,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            BigDecimal expiryDateModifiedDays,
            String expiryDate,
            String expiryDateTo,
            BigDecimal bankCommissionPercentage
    ) {

        try {
            System.out.println("tenorCheck:" + tenorCheck);
            System.out.println("amountSwitch:" + amountSwitch);
            System.out.println("lcAmountFlag:" + lcAmountFlag);
            System.out.println("expiryDateCheck:" + expiryDateCheck);
            System.out.println("expiryDateFlag:" + expiryDateFlag);
            System.out.println("changeInConfirmationCheck:" + changeInConfirmationCheck);
            System.out.println("narrativesCheck:" + narrativesCheck);
            System.out.println("bankCommissionNumerator:" + bankCommissionNumerator);
            System.out.println("bankCommissionDenominator:" + bankCommissionDenominator);
            System.out.println("amountFrom:" + amountFrom);
            System.out.println("amountTo:" + amountTo);
            System.out.println("expiryDateModifiedDays:" + expiryDateModifiedDays);
            System.out.println("expiryDate:" + expiryDate);
            System.out.println("expiryDateTo:" + expiryDateTo);
            System.out.println("bankCommissionPercentage:" + bankCommissionPercentage);

            BigDecimal months = getMonthsTill(expiryDate);
            System.out.println("months:" + months);


            if (bankCommissionNumerator == null) {
                bankCommissionNumerator = new BigDecimal("1");
            }
            if (bankCommissionDenominator == null) {
                bankCommissionDenominator = new BigDecimal("8");
            }
            if (bankCommissionPercentage == null) {
                bankCommissionPercentage = new BigDecimal("0.01");
            } else if (!(bankCommissionPercentage.compareTo(BigDecimal.ONE) < 1)) {
                bankCommissionPercentage = bankCommissionPercentage.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            }

            if (!"on".equalsIgnoreCase(amountSwitch)) {
                amountTo = BigDecimal.ZERO;
            }

            if (amountFrom == null) {
                amountFrom = BigDecimal.ZERO;
            }

            if (amountTo == null) {
                amountTo = BigDecimal.ZERO;
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
            if (("on".equalsIgnoreCase(amountSwitch) && "DEC".equalsIgnoreCase(lcAmountFlag)) || ("on".equalsIgnoreCase(expiryDateCheck) && "RED".equalsIgnoreCase(expiryDateFlag)) || ("on".equalsIgnoreCase(narrativesCheck)) || ("on".equalsIgnoreCase(changeInConfirmationCheck)) || ("on".equalsIgnoreCase(tenorCheck))) {
                fixed = new BigDecimal("500");
            } else {
                fixed = fixed;
            }


            //COMPUTED part
            //Bank Commission due to increase in FXLC Amount
            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
                oldMonths = months;
                oldMonths = oldMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : oldMonths;
                System.out.println("oldMonths:" + oldMonths);
                computedLcAmount = (amountTo.subtract(amountFrom)).multiply(bankCommissionPercentage).multiply(months).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 20, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computed bank commission: lcAmount increase:" + computedLcAmount);
            }

            //Bank Commission due to extension of Expiry Date
            if ("on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                //newMonths = expiryDateModifiedDays.divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP);
                newMonths = getMonthsTill(expiryDateTo);
                newMonths = newMonths.compareTo(new BigDecimal("1")) < 1 ? new BigDecimal("1") : newMonths;
                System.out.println("newMonths:" + newMonths);
                computedLcExpiry = fixed.add(amountFrom).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 20, BigDecimal.ROUND_HALF_UP).multiply(bankCommissionPercentage).multiply(newMonths).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computed bank commission: expiryDate extension:" + computedLcExpiry);
            }

            if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag) && "on".equalsIgnoreCase(expiryDateCheck) && "EXT".equalsIgnoreCase(expiryDateFlag)) {
                computedBoth = (amountTo.subtract(amountFrom)).multiply(bankCommissionPercentage).multiply(newMonths).multiply(bankCommissionNumerator).divide(bankCommissionDenominator, 20, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("computed bank commission: lcAmount both:" + computedBoth);
            }

            if (computedBoth.compareTo(new BigDecimal("0")) < 1 && computedLcAmount.compareTo(new BigDecimal("0")) < 1 && computedLcExpiry.compareTo(new BigDecimal("0")) < 1) {
                result = fixed;
            } else {
                result = addToResultIfNotNegativeOrZero(result, fixed);
                System.out.println(result);
                result = addToResultIfNotNegativeOrZero(result, computedLcAmount);
                System.out.println(result);
                result = addToResultIfNotNegativeOrZero(result, computedLcExpiry);
                System.out.println(result);
                result = addToResultIfNotNegativeOrZero(result, computedBoth);
                System.out.println(result);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }

    }


    public static BigDecimal getCommitmentFeeAmendmentDmlc(
            String tenorCheck,
            String amountSwitch,
            String lcAmountFlag,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            BigDecimal usancePeriod,
            String expiryDate,
            String documentSubType1,
            String documentSubType2,
            BigDecimal commitmentFeeNumerator,
            BigDecimal commitmentFeeDenominator,
            BigDecimal commitmentFeePercentage
    ) {

        System.out.println("tenorCheck:" + tenorCheck);
        System.out.println("amountSwitch:" + amountSwitch);
        System.out.println("lcAmountFlag:" + lcAmountFlag);
        System.out.println("amountFrom:" + amountFrom);
        System.out.println("amountTo:" + amountTo);
        System.out.println("usancePeriod:" + usancePeriod);
        System.out.println("expiryDate:" + expiryDate);
        System.out.println("documentSubType1:" + documentSubType1);
        System.out.println("documentSubType2:" + documentSubType2);
        System.out.println("commitmentFeeNumerator:" + commitmentFeeNumerator);
        System.out.println("commitmentFeeDenominator:" + commitmentFeeDenominator);
        System.out.println("commitmentFeePercentage:" + commitmentFeePercentage);

        if (commitmentFeeNumerator == null) {
            commitmentFeeNumerator = new BigDecimal("1");
        }
        if (commitmentFeeDenominator == null) {
            commitmentFeeDenominator = new BigDecimal("4");
        }
        if (commitmentFeePercentage == null) {
            commitmentFeePercentage = new BigDecimal("0.01");
        } else if (!(commitmentFeePercentage.compareTo(BigDecimal.ONE) < 1)) {
            commitmentFeePercentage = commitmentFeePercentage.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        }

        if (!"on".equalsIgnoreCase(amountSwitch)) {
            amountTo = BigDecimal.ZERO;
        }

        if (amountFrom == null) {
            amountFrom = BigDecimal.ZERO;
        }

        if (amountTo == null) {
            amountTo = BigDecimal.ZERO;
        }


        BigDecimal result = new BigDecimal(0);
        BigDecimal resultLcAmount = new BigDecimal(0);
        BigDecimal resultTenor = new BigDecimal(0);
        Boolean cfExists = Boolean.FALSE;
        if ("on".equalsIgnoreCase(amountSwitch) && "INC".equalsIgnoreCase(lcAmountFlag)) {
            //BigDecimal months = new BigDecimal(getMonthsTill(expiryDate)); //TODO: Verify not stated in docs assumed by angol
            BigDecimal months = getMonthsTill(expiryDate); //TODO: Verify not stated in docs assumed by angol
            resultLcAmount = divideUp(commitmentFeeNumerator, commitmentFeeDenominator).multiply(commitmentFeePercentage).multiply(amountFrom).multiply(months);
            cfExists = Boolean.TRUE;
        }

        if ("on".equalsIgnoreCase(tenorCheck)) {
            BigDecimal months = getMonthsOf(usancePeriod);  //TODO: Verify not stated in docs assumed by angol
            resultTenor = divideUp(commitmentFeeNumerator, commitmentFeeDenominator).multiply(commitmentFeePercentage).multiply(amountFrom).multiply(months);
            cfExists = Boolean.TRUE;
        }

        if (cfExists) {
            result = addToResultIfNotNegativeOrZero(result, resultLcAmount);
            result = addToResultIfNotNegativeOrZero(result, resultTenor);
            if ((result.compareTo(new BigDecimal("500"))) < 1) {
                return new BigDecimal("500");
            } else {
                return result;
            }
        } else {
            return BigDecimal.ZERO;
        }

    }


    public static BigDecimal getSuppliesFeeDmlcAmendment(
            String tenorCheck,
            String amountSwitch,
            String expiryDateCheck,
            String changeInConfirmationCheck,
            String narrativesCheck,
            BigDecimal suppliesFee
    ) {
        BigDecimal fixed = BigDecimal.ZERO;

        if ((tenorCheck.equalsIgnoreCase("on")) || (amountSwitch.equalsIgnoreCase("on")) || (expiryDateCheck.equalsIgnoreCase("on")) || (changeInConfirmationCheck.equalsIgnoreCase("on")) || (narrativesCheck.equalsIgnoreCase("on"))) {
            fixed = suppliesFee;
        }

        return fixed;
    }


    /**
     * LC NEGOTIATION
     */

    public static BigDecimal getDocStampsAmount_FXLC_Nego(
            BigDecimal docAmount,
            BigDecimal oldDocAmount
    ) {
        return getDocStampsAmountBasic(docAmount, oldDocAmount);
    }

    public static BigDecimal getRemittanceFee_DMLC_Nego(
            BigDecimal remittanceFee,
            BigDecimal urr
    ) {
        System.out.println("remittanceFee:" + remittanceFee);
        System.out.println("urr:" + urr);
        BigDecimal eighteenDollarMinimum = new BigDecimal("18");
        BigDecimal minimumRemittanceFee = eighteenDollarMinimum.multiply(urr);
        if (remittanceFee.compareTo(minimumRemittanceFee) < 1) {
            return minimumRemittanceFee;
        } else {
            return remittanceFee;
        }
    }

    public static BigDecimal getDocStampsAmount_DMLC_Nego(
            BigDecimal productAmount,
            BigDecimal urr
    ) {//TODO:Confirm
        return getDocStampsAmountBasic(productAmount, new BigDecimal("0"));
    }


    public static BigDecimal getDocStamps_UALOAN_Settlement(
            BigDecimal docAmount,
            BigDecimal oldDocAmount
    ) {
        System.out.println("docAmount:" + docAmount);
        System.out.println("oldDocAmount:" + oldDocAmount);
        if (oldDocAmount.compareTo(BigDecimal.ZERO) < 1) {
            System.out.println("oldDocAmount is zero");
            BigDecimal temp = new BigDecimal("0.30").multiply(divideUp(getRoundedNearest200(docAmount), new BigDecimal("200")));
            System.out.println("doc productAmount from opening" + temp);
            oldDocAmount = temp;
        }

        BigDecimal step = new BigDecimal("5000");
        BigDecimal payStep01 = new BigDecimal("20");
        BigDecimal payStep02 = new BigDecimal("10");
        if (docAmount.subtract(oldDocAmount).compareTo(step) < 1) {
            return payStep01.setScale(2, BigDecimal.ROUND_HALF_UP); //less than or equal to 5000
        } else {
            BigDecimal holder = payStep01.add(docAmount.subtract(step).divide(step, BigDecimal.ROUND_HALF_UP).multiply(payStep02)).subtract(oldDocAmount);
            if (holder.compareTo(BigDecimal.ZERO) < 1) {
                return BigDecimal.ZERO;
            } else {
                return holder.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }
    }


    public static BigDecimal getRemitanceFee_UALOAN_Settlement(
            BigDecimal usdToPHPSpecialRate,
            BigDecimal remittanceFeeinUsd
    ) {
        return usdToPHPSpecialRate.multiply(remittanceFeeinUsd);
    }


    public static BigDecimal getDocStampsAmount_DMLC_UALOAN_Settlement(
            BigDecimal productAmount,
            BigDecimal urr
    ) {//TODO:Confirm
        return getDocStampsAmountBasic(productAmount, new BigDecimal(0));
    }

    public static BigDecimal getCommitmentFee_FXLC_UALOAN_MaturityAdjustment(
            String loanMaturityDateFrom,
            String loanMaturityDateTo,
            BigDecimal productAmount,
            BigDecimal commitmentFeeNumerator,
            BigDecimal commitmentFeeDenominator,
            BigDecimal commitmentFeePercentage,
            BigDecimal default_commitmentFeeMinimum,
            String cwtFlag,
            BigDecimal cwtPercentage

    ) {
        System.out.println("loanMaturityDateFrom:" + loanMaturityDateFrom);
        System.out.println("loanMaturityDateTo:" + loanMaturityDateTo);
        System.out.println("productAmount:" + productAmount);
        System.out.println("commitmentFeeNumerator:" + commitmentFeeNumerator);
        System.out.println("commitmentFeeDenominator:" + commitmentFeeDenominator);
        System.out.println("commitmentFeePercentage:" + commitmentFeePercentage);
        BigDecimal months = CalculatorUtils.getMonthsTill(loanMaturityDateFrom, loanMaturityDateTo);
        System.out.println("months:" + months);
        if (months.compareTo(BigDecimal.ONE) != 1) {
            months = BigDecimal.ONE;
        }
        System.out.println("months:" + months);
        BigDecimal result = CalculatorUtils.multiplyNumeratorDenominatorPercentageAmountMonths(commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage, productAmount, months);
        System.out.println("result:" + result);
        if (result.compareTo(default_commitmentFeeMinimum) != 1) {
            result = default_commitmentFeeMinimum;
        }
        if ("Y".equalsIgnoreCase(cwtFlag)) {
            if (cwtPercentage == null) {
                cwtPercentage = new BigDecimal("0.98");
            }
            result = result.multiply(cwtPercentage);
        }
        return result;
    }


    //TODO: use externalize productAmount
    public static BigDecimal getCommitmentFee_DMLC_UALOAN_MaturityAdjustment(
            BigDecimal amountFrom,
            BigDecimal amountTo,
            BigDecimal usancePeriod,
            BigDecimal commitmentFeeNumerator,
            BigDecimal commitmentFeeDenominator,
            BigDecimal commitmentFeePercentage,
            String cwtFlag,
            BigDecimal cwtPercentage,
            BigDecimal default_commitmentFeeMinimum

    ) {

        System.out.println("amountFrom:" + amountFrom);
        System.out.println("amountTo:" + amountTo);
        System.out.println("usancePeriod:" + usancePeriod);
        System.out.println("commitmentFeeNumerator:" + commitmentFeeNumerator);
        System.out.println("commitmentFeeDenominator:" + commitmentFeeDenominator);
        System.out.println("commitmentFeePercentage:" + commitmentFeePercentage);

        if (commitmentFeeNumerator == null) {
            commitmentFeeNumerator = new BigDecimal("1");
        }
        if (commitmentFeeDenominator == null) {
            commitmentFeeDenominator = new BigDecimal("4");
        }
        if (commitmentFeePercentage == null) {
            commitmentFeePercentage = new BigDecimal("0.01");
        } else if (!(commitmentFeePercentage.compareTo(BigDecimal.ONE) < 1)) {
            commitmentFeePercentage = commitmentFeePercentage.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        }


        BigDecimal months = getMonthsOf(usancePeriod);  //TODO: Verify not stated in docs assumed by angol
        BigDecimal result = divideUp(commitmentFeeNumerator, commitmentFeeDenominator).multiply(commitmentFeePercentage).multiply(amountTo.subtract(amountFrom)).multiply(months);

        if ((result.compareTo(default_commitmentFeeMinimum)) != 1) {
            result = default_commitmentFeeMinimum;
        }

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            result = result.multiply(cwtPercentage);

        }

        return result;
    }


    public static BigDecimal getCancelFee_Indemnity_Issuance(BigDecimal default_INDEMNITY_Cancel_Fee) {
        return default_INDEMNITY_Cancel_Fee;
    }

    public static BigDecimal getBankCommission_Indemnity_Issuance(BigDecimal default_INDEMNITY_BC) {
        return default_INDEMNITY_BC;
    }

    public static BigDecimal getDocStamps_Indemnity_Issuance(BigDecimal default_INDEMNITY_DOCSTAMPS, String indemnityType) {
        System.out.println("default_INDEMNITY_DOCSTAMPS:" + default_INDEMNITY_DOCSTAMPS);
        BigDecimal result = default_INDEMNITY_DOCSTAMPS;

        if ("BE".equalsIgnoreCase(indemnityType)) {
            result = BigDecimal.ZERO;
        }

        System.out.println("result:" + result);
        return result;
    }

    /**
     * NON LC
     */

    public static BigDecimal getBankCommission_FX_NON_LC_SETTLEMENT(
            BigDecimal productAmount,
            BigDecimal bankCommissionNumerator,
            BigDecimal bankCommissionDenominator,
            BigDecimal bankCommissionPercentage,
            String cwtFlag,
            BigDecimal cwtPercentage
    ) {
        BigDecimal tranch01 = new BigDecimal("50000");
        BigDecimal bankCommissionAmountInitial = new BigDecimal("125");
        BigDecimal bankCommissionAmount = getBankCommissionNON_LC_Common(
                cwtFlag,
                cwtPercentage,
                productAmount,
                bankCommissionNumerator,
                bankCommissionDenominator,
                bankCommissionPercentage,
                bankCommissionAmountInitial,
                tranch01);

        if (bankCommissionAmount.compareTo(new BigDecimal("1000")) != 1) {
            bankCommissionAmount = new BigDecimal("1000");
        }

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommissionAmount = bankCommissionAmount.multiply(cwtPercentage);
        }

        return bankCommissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private static BigDecimal getBankCommissionNON_LC_Common(
            String cwtFlag,
            BigDecimal cwtPercentage,
            BigDecimal productAmount,
            BigDecimal bankCommissionNumerator,
            BigDecimal bankCommissionDenominator,
            BigDecimal bankCommissionPercentage,
            BigDecimal bankCommissionAmountInitial,
            BigDecimal tranch01
    ) {
        System.out.println("cwtFlag:" + cwtFlag);
        System.out.println("cwtPercentage:" + cwtPercentage);
        System.out.println("productAmount:" + productAmount);
        System.out.println("bankCommissionNumerator:" + bankCommissionNumerator);
        System.out.println("bankCommissionDenominator:" + bankCommissionDenominator);
        System.out.println("bankCommissionPercentage:" + bankCommissionPercentage);
        System.out.println("bankCommissionAmountInitial:" + bankCommissionAmountInitial);
        System.out.println("tranch01:" + tranch01);
        BigDecimal remainingProductAmount = productAmount.subtract(tranch01);
        BigDecimal bankCommissionNext = BigDecimal.ZERO;
        System.out.println("remainingProductAmount:" + remainingProductAmount);
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

        return bankCommissionNext;

    }

    public static BigDecimal getDocStampsAmount_FX_NON_LC_SETTLEMENT(
            BigDecimal productAmount,
            String TR_LOAN_FLAG,
            BigDecimal trloanAmount
    ) {
        BigDecimal temp;
        BigDecimal stepAmount = new BigDecimal("200"); //normally settled
        BigDecimal normallySettled = BigDecimal.ZERO;
        if (trloanAmount != null) {
            normallySettled = productAmount.subtract(trloanAmount);
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
            holderTRLoan = payStep01.add(getRoundedNearest5000(trloanAmount).subtract(step).divide(step, BigDecimal.ROUND_HALF_UP).multiply(payStep02));
        } else if (trloanAmount.compareTo(BigDecimal.ZERO) > 0) {
            // if trloan amount is less than 500 but greater than zero
            holderTRLoan = payStep01;
        }

        BigDecimal holderNormallySettled = BigDecimal.ZERO;
        if (normallySettled.compareTo(BigDecimal.ZERO) > 0) {
            holderNormallySettled = new BigDecimal("0.30").multiply(divideUp(getRoundedNearest5000(normallySettled), stepAmount));
        }


        BigDecimal holder = holderNormallySettled.add(holderTRLoan);

        if (holder.compareTo(BigDecimal.ZERO) == 1) {
            return holder.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal getDocStampsAmount_DM_NON_LC_SETTLEMENT(
            BigDecimal productAmount,
            String TR_LOAN_FLAG,
            BigDecimal trloanAmount
    ) {
        BigDecimal roundedTrLoanAmount = getRoundedNearest5000(trloanAmount);
        System.out.println("roundedTrLoanAmount:" + roundedTrLoanAmount);


        BigDecimal step = new BigDecimal("5000");
        BigDecimal payStep01 = new BigDecimal("20");
        BigDecimal payStep02 = new BigDecimal("10");
        BigDecimal holderTRLoan = BigDecimal.ZERO;
        if (roundedTrLoanAmount.compareTo(step) > 0) {
            // if trloan amount is greater than step(5000) and tr
            holderTRLoan = payStep01.add(roundedTrLoanAmount.subtract(step).divide(step, BigDecimal.ROUND_HALF_UP).multiply(payStep02));
        } else if (roundedTrLoanAmount.compareTo(BigDecimal.ZERO) > 0) {
            // if trloan amount is less than 500 but greater than zero
            holderTRLoan = payStep01;
        }
        System.out.println("holderTRLoan:" + holderTRLoan);


        if (holderTRLoan.compareTo(BigDecimal.ZERO) == 1) {
            return holderTRLoan.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }


    }


    public static BigDecimal getCableFee_FX_NON_LC_SETTLEMENT(
            BigDecimal default_FX_NON_LC_SETTLEMENT_CABLE
    ) {
        return default_FX_NON_LC_SETTLEMENT_CABLE;
    }


    public static BigDecimal getCableFee_DM_NON_LC_SETTLEMENT(
            BigDecimal default_FX_NON_LC_SETTLEMENT_CABLE, String cableFeeFlag
    ) {
        if ("Y".equalsIgnoreCase(cableFeeFlag)) {
            return default_FX_NON_LC_SETTLEMENT_CABLE;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal getCilex_FX_NON_LC_SETTLEMENT(
            BigDecimal productAmount,
            BigDecimal cilexNumerator,
            BigDecimal cilexDenominator,
            BigDecimal cilexPercentage,
            String cwtFlag,
            BigDecimal cwtPercentage,
            BigDecimal usdToPHPSpecialRate
    ) {
        BigDecimal cilex = productAmount.multiply(cilexNumerator).multiply(cilexPercentage).divide(cilexDenominator, 9, BigDecimal.ROUND_HALF_UP);

        BigDecimal cilexMinimumInPhp = new BigDecimal("20");
        cilexMinimumInPhp = cilexMinimumInPhp.multiply(usdToPHPSpecialRate);

        System.out.println("cilexMinimumInPhp:" + cilexMinimumInPhp);
        System.out.println("cilex:" + cilex);
        if (cilex.compareTo(cilexMinimumInPhp) != 1) {
            cilex = cilexMinimumInPhp;
        }

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            cilex = cilex.multiply(cwtPercentage);
        }

        return cilex;
    }

    public static BigDecimal getBookingCommission_FX_NON_LC_SETTLEMENT(
            BigDecimal default_FX_NON_LC_SETTLEMENT_BookingCommission
    ) {
        return default_FX_NON_LC_SETTLEMENT_BookingCommission;
    }

    public static BigDecimal getNotarialFeeCommission_FX_NON_LC_SETTLEMENT(
            BigDecimal default_FX_NON_LC_SETTLEMENT_NOTARIAL
    ) {
        return default_FX_NON_LC_SETTLEMENT_NOTARIAL;
    }

    public static BigDecimal getBspCommission_FX_NON_LC_SETTLEMENT(
            BigDecimal default_FX_NON_LC_SETTLEMENT_BSP
    ) {
        return default_FX_NON_LC_SETTLEMENT_BSP;
    }

    public static BigDecimal getBankCommission_DM_NON_LC_SETTLEMENT(
            BigDecimal productAmount,
            BigDecimal bankCommissionNumerator,
            BigDecimal bankCommissionDenominator,
            BigDecimal bankCommissionPercentage,
            String cwtFlag,
            BigDecimal cwtPercentage
    ) {
        BigDecimal tranch01 = new BigDecimal("50000");
        BigDecimal bankCommissionAmountInitial = new BigDecimal("125");
        BigDecimal bankCommissionAmount = getBankCommissionNON_LC_Common(
                cwtFlag,
                cwtPercentage,
                productAmount,
                bankCommissionNumerator,
                bankCommissionDenominator,
                bankCommissionPercentage,
                bankCommissionAmountInitial,
                tranch01);

        if (bankCommissionAmount.compareTo(new BigDecimal("500")) != 1) {
            bankCommissionAmount = new BigDecimal("500");
        }

        if ("Y".equalsIgnoreCase(cwtFlag)) {
            bankCommissionAmount = bankCommissionAmount.multiply(cwtPercentage);
        }

        return bankCommissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal getDocStampsAmount_DM_NON_LC_SETTLEMENT(
            BigDecimal productAmount, String TR_LOAN_FLAG
    ) {
        BigDecimal temp;
        BigDecimal stepAmount = new BigDecimal("200");
        if (TR_LOAN_FLAG.equalsIgnoreCase("Y")) {
            BigDecimal step = new BigDecimal("5000");
            BigDecimal payStep01 = new BigDecimal("20");
            BigDecimal payStep02 = new BigDecimal("10");

            BigDecimal holder = payStep01.add(productAmount.subtract(step).divide(step, BigDecimal.ROUND_HALF_UP).multiply(payStep02));
            if (holder.compareTo(BigDecimal.ZERO) < 1) {
                return BigDecimal.ZERO;
            } else {
                return holder;
            }

        } else {
            temp = new BigDecimal("0.30").multiply(divideUp(getRoundedNearest200(productAmount), stepAmount));
        }
        return temp;
    }

    public static BigDecimal getCableFee_DM_NON_LC_SETTTLEMENT(
            BigDecimal default_DM_NON_LC_SETTTLEMENT_CABLE
    ) {
        return default_DM_NON_LC_SETTTLEMENT_CABLE;
    }

    public static BigDecimal getRemittanceFee_DM_NON_LC_SETTLEMENT(
            BigDecimal usdToPHPSpecialRate,
            BigDecimal remittanceFeeInUsd,
            String remittanceFlag
    ) {
        if ("Y".equalsIgnoreCase(remittanceFlag)) {
            return usdToPHPSpecialRate.multiply(remittanceFeeInUsd);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * EXPORTS
     */

    //TODO:Implement
    public static BigDecimal getBankCommissionAmount_EBC_Settlement(BigDecimal productAmount) {
        return productAmount;
    }


    //TODO:Implement
    public static BigDecimal getDocStampsAmount_EBC_Settlement(BigDecimal productAmount) {
        return productAmount;
    }


    //TODO:Implement
    public static BigDecimal getCilexAmount_EBC_Settlement(BigDecimal productAmount) {
        return productAmount;
    }

    //TODO:Test
    public static BigDecimal getPostageAmount_EBC_Settlement(BigDecimal EBC_SETTLEMENT_PostageFee) {
        return EBC_SETTLEMENT_PostageFee;
    }

    //TODO:Test
    public static BigDecimal getAdvisingFeeExportAdvisingOpening(
            BigDecimal EXPORT_ADVISING_FXLC_OPENING_AdvisingFee
    ) {
        return EXPORT_ADVISING_FXLC_OPENING_AdvisingFee;
    }

    //TODO:Test
    public static BigDecimal getRemittanceFeeEbcSettlement(
            BigDecimal usdToPHPSpecialRate,
            BigDecimal EBC_SETTLEMENT_RemittanceFeeinUsd
    ) {
        return usdToPHPSpecialRate.multiply(EBC_SETTLEMENT_RemittanceFeeinUsd);
    }


    private static BigDecimal addToResultIfNotNegativeOrZero(
            BigDecimal result,
            BigDecimal fixed
    ) {
        if (fixed.compareTo(BigDecimal.ZERO) == 1) {
            return result.add(fixed);
        }
        return result;
    }

    /**
     * @param productAmount
     * @param value
     * @return Rounded Value
     */
    private static BigDecimal getRoundedNearestValue(
            BigDecimal productAmount,
            BigDecimal value
    ) {
        BigDecimal remainder = productAmount.remainder(value);
        System.out.println("remainder:" + remainder);
        if (remainder.compareTo(new BigDecimal("0")) == 0) {
            return productAmount;
        } else {
            productAmount = value.subtract(remainder).add(productAmount);
            System.out.println("newAmount:" + productAmount);
            return productAmount;
        }
    }

    /**
     * @param productAmount
     * @return
     */
    private static BigDecimal getRoundedNearest5000(
            BigDecimal productAmount
    ) {
        BigDecimal remainder = productAmount.remainder(new BigDecimal("5000"));
        System.out.println("remainder:" + remainder);
        if (remainder.compareTo(new BigDecimal("0")) == 0) {
            return productAmount;
        } else {
            BigDecimal newAmount = new BigDecimal("5000").subtract(remainder).add(productAmount);
            System.out.println("newAmount:" + newAmount);
            return newAmount;
        }
    }


    public static BigDecimal getRoundedNearest200(
            BigDecimal productAmount
    ) {
        //NOTE: remainder can be negative
        //TODO: Handle negative
        BigDecimal remainder = productAmount.remainder(new BigDecimal("200"));
        System.out.println("remainder:" + remainder);
        if (remainder.compareTo(new BigDecimal("0")) == 0) {
            return productAmount;
        } else {
            BigDecimal newAmount = new BigDecimal("200").subtract(remainder).add(productAmount);
            System.out.println("newAmount:" + newAmount);
            return newAmount;
        }
    }


    public static BigDecimal multiplyNumeratorDenominatorPercentageAmountMonths(
            BigDecimal numerator,
            BigDecimal denominator,
            BigDecimal percentage,
            BigDecimal productAmount,
            BigDecimal months
    ) {
        System.out.println("numerator:" + numerator);
        System.out.println("denominator:" + denominator);
        System.out.println("percentage:" + percentage);
        System.out.println("productAmount:" + productAmount);
        System.out.println("months:" + months);
        System.out.println("multiplied value:" + divideUp(numerator, denominator).multiply(percentage).multiply(productAmount).multiply(months));
        return divideUp(numerator, denominator).multiply(percentage).multiply(productAmount).multiply(months);

    }


    public static BigDecimal multiplyNumeratorDenominatorPercentageAmount(
            BigDecimal numerator,
            BigDecimal denominator,
            BigDecimal percentage,
            BigDecimal productAmount
    ) {
        System.out.println("numerator:" + numerator);
        System.out.println("denominator:" + denominator);
        System.out.println("percentage:" + percentage);
        System.out.println("productAmount:" + productAmount);
        System.out.println("multiplied value:" + divideUp(numerator, denominator).multiply(percentage).multiply(productAmount));
        return divideUp(numerator, denominator).multiply(percentage).multiply(productAmount);

    }


    public static BigDecimal multiplyTwoValues(
            BigDecimal value01,
            BigDecimal value02
    ) {
        return value01.multiply(value02);
    }


    public static BigDecimal multiply(String... numbers) {
        BigDecimal product = BigDecimal.ONE;
        for (String number : numbers) {
            product = product.multiply(new BigDecimal(number != null ? number : "1"));
        }
        return product;
    }


    //@Test
//    public void testCalculatorUtils() {
//        System.out.println("angol");
//        BigDecimal temp;
//        temp = getCableFeeFxlcAmendment("N", "N", "N", "N", "N");
//        System.out.println(temp);
//        //Assert.assertTrue(temp.compareTo(new BigDecimal(0)) == 0);
//
//        temp = getCableFeeFxlcAmendment("on", "N", "N", "N", "N");
//        System.out.println(temp);
//        //Assert.assertTrue(temp.compareTo(new BigDecimal(500)) == 0);
//
//
//        String tenorCheck = "on";
//        String amountSwitch = "on";
//        String lcAmountFlag = "INC";
//        String expiryDateCheck = "on";
//        String expiryDateFlag = "EXT";
//        String changeInConfirmationCheck = "on";
//        String narrativesCheck = "on";
//        BigDecimal bankCommissionNumerator = new BigDecimal(1);
//        BigDecimal bankCommissionDenominator = new BigDecimal(8);
//        BigDecimal bankCommissionPercentage = new BigDecimal("0.01");
//        BigDecimal amountFrom = new BigDecimal(1000000);
//        BigDecimal amountTo = new BigDecimal(2000000);
//        BigDecimal expiryDateModifiedDays = new BigDecimal(20);
//        String expiryDate = "12/31/2012";
//        String processingDate = "10/31/2012";
//        BigDecimal usdToPHPSpecialRate = new BigDecimal(40.5);
//        BigDecimal confirmingFeeNumerator = new BigDecimal(1);
//        BigDecimal confirmingFeeDenominator = new BigDecimal(8);
//        BigDecimal confirmingFeePercentage = new BigDecimal(0.01);
//        String advanceCorresChargesFlag = "on";
//        BigDecimal commitmentFeeNumerator = new BigDecimal(1);
//        BigDecimal commitmentFeeDenominator = new BigDecimal(4);
//        BigDecimal commitmentFeePercentage = new BigDecimal("0.01");
//
//        temp = getCableFeeFxlcAmendment(tenorCheck, amountSwitch, expiryDateCheck, changeInConfirmationCheck, narrativesCheck);
//        System.out.println("cable fee:" + temp);
//
//        temp = getBankCommission_FXLC_Amendment(tenorCheck, amountSwitch, lcAmountFlag,
//                expiryDateCheck, expiryDateFlag, changeInConfirmationCheck, narrativesCheck,
//                bankCommissionNumerator, bankCommissionDenominator,
//                amountFrom, amountTo,
//                expiryDateModifiedDays,
//                expiryDate,bankCommissionPercentage);
//        System.out.println("bank commission:" + temp);
//
//
//        temp = getAdvisingFeeAmendment(tenorCheck, amountSwitch, lcAmountFlag, changeInConfirmationCheck, usdToPHPSpecialRate);
//        System.out.println("advising fee:" + temp);
//
//
//        temp = getConfirmingFeeFxlcAmendment(amountSwitch, lcAmountFlag,
//                expiryDateCheck, expiryDateFlag,
//                changeInConfirmationCheck,
//                advanceCorresChargesFlag,
//                amountFrom, amountTo,
//                expiryDateModifiedDays,
//                confirmingFeeNumerator, confirmingFeeDenominator, expiryDate, processingDate,
//                confirmingFeePercentage
//                );
//
//
//        System.out.println("confirming fee:" + temp);
//
//        temp = getDocStampsFxlcAmendment(amountSwitch, lcAmountFlag, amountFrom, amountTo);
//        System.out.println("doc stamps fee:" + temp);
//        lcAmountFlag = "DEC";
//        temp = getDocStampsFxlcAmendment(amountSwitch, lcAmountFlag, amountFrom, amountTo);
//        System.out.println("doc stamps fee:" + temp);
//
//        BigDecimal usancePeriod = new BigDecimal(31);
//        String documentSubType1 = "SIGHT";
//        String documentSubType2 = "STANDBY";
//
//        temp = getCommitmentFeeAmendment(tenorCheck,
//                amountSwitch, lcAmountFlag,
//                amountFrom, amountTo,
//                usancePeriod, expiryDate,
//                documentSubType1, documentSubType2, commitmentFeeNumerator, commitmentFeeDenominator,commitmentFeePercentage);
//        System.out.println("commitment fee:" + temp);
//
////        temp = getCommitmentFeeFxlcAmendment(tenorCheck, amountSwitch, lcAmountFlag, amountFrom, amountTo, usancePeriod, expiryDate, documentSubType1, documentSubType2);
////        System.out.println("commitment fee:" + temp);
//
//        temp = getCommitmentFeeAmendmentDmlc(tenorCheck, amountSwitch, lcAmountFlag, amountFrom, amountTo, usancePeriod, expiryDate, documentSubType1, documentSubType2, commitmentFeeNumerator, commitmentFeeDenominator,commitmentFeeDenominator);
//        System.out.println("commitment fee:" + temp);
//
//        BigDecimal productAmount = new BigDecimal(10000000);
//        String settlementCurrency = "PHP";
//        //BigDecimal usdToPHPSpecialRate = new BigDecimal(40);
//
//        temp = getCilex_UALOAN_Settlement(productAmount, settlementCurrency, usdToPHPSpecialRate);
//        System.out.println("CILEX UA Loan Settlement:" + temp);
//
//
//        documentSubType1 = "REGULAR";
//        documentSubType2 = "USANCE";
//        temp = getCommitmentFee_DMLC_Opening(productAmount, usancePeriod, expiryDate, documentSubType1, documentSubType2, commitmentFeeNumerator, commitmentFeeDenominator);
//        System.out.println("DMLC Commitment Fee Opening:" + temp);
//
//        BigDecimal remittanceFee = new BigDecimal(1000);
//
//        temp = getRemittanceFeeDmlcNego(remittanceFee, usdToPHPSpecialRate);
//        System.out.println("getRemittanceFeeDmlcNego:" + temp);
//
//        temp = getBankCommissionDmlcAmendment(tenorCheck, amountSwitch, lcAmountFlag,
//                expiryDateCheck, expiryDateFlag, changeInConfirmationCheck, narrativesCheck,
//                bankCommissionNumerator, bankCommissionDenominator,
//                amountFrom, amountTo,
//                expiryDateModifiedDays,
//                expiryDate,bankCommissionPercentage);
//        System.out.println("bank commission:" + temp);
//    }


//    @Test()
//    public void testCalculatorUtils() {
//        String endDate = "12/31/2012";
//        BigDecimal monthsTill = getMonthsTill(endDate);
//    }
}
