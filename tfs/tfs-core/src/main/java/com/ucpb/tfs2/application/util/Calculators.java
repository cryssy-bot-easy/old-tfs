package com.ucpb.tfs2.application.util;

import org.jfree.date.SerialDate;
import org.jfree.date.SerialDateUtilities;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:52 PM
 */
public class Calculators {

    /***
     * generic routine to handle computation for based on baseAmount, charge [firstCharge] for the first [firstAmount]
     * and [forEveryCharge] for the succeeding [forEveryAmount]
     * @param baseAmount
     * @param firstAmount
     * @param firstCharge
     * @param forEveryAmount
     * @param forEveryCharge
     * @return the charge computed using this function
     */
    public BigDecimal firstSucceedingFixed(BigDecimal baseAmount,
                                           BigDecimal firstAmount,
                                           BigDecimal firstCharge,
                                           BigDecimal forEveryAmount,
                                           BigDecimal forEveryCharge) {

        BigDecimal balance = baseAmount;
        BigDecimal totalCharge = BigDecimal.ZERO;

        if(balance.compareTo(BigDecimal.ZERO) > 0) {

            totalCharge = totalCharge.add(firstCharge);
            balance = balance.subtract(firstAmount);

        }

        while(balance.compareTo(BigDecimal.ZERO) > 0) {

            // todo: refactor to just divide
            balance = balance.subtract(forEveryAmount);
            totalCharge = totalCharge.add(forEveryCharge);
        }

        return totalCharge;

    }

    /***
     *
     * @param baseAmount this is the amount used in computing the charges assumed to be in PHP
     * @param firstAmount this is the part of the baseAmount which is charged the first charge
     * @param firstCharge the charge for the firstAmount part of the baseAmount
     * @param percentOfRemaining the charge factor used in computing the part of the baseAmount excluding the firstAmount
     * @return the charge computed using this function
     */
    public BigDecimal firstSucceedingPercentage(BigDecimal baseAmount,
                                                BigDecimal firstAmount,
                                                BigDecimal firstCharge,
                                                BigDecimal percentOfRemaining) {

        return firstSucceedingPercentageWithMinimum(baseAmount, firstAmount, firstCharge, percentOfRemaining, BigDecimal.ZERO);

    }

    /***
     *
     * @param baseAmount this is the amount used in computing the charges assumed to be in PHP
     * @param firstAmount this is the part of the baseAmount which is charged the first charge
     * @param firstCharge the charge for the firstAmount part of the baseAmount
     * @param percentOfRemaining the charge factor used in computing the part of the baseAmount excluding the firstAmount
     * @param minimum the minimum amount to be returned if the charge computed is less than this amount
     * @return the charge computed using this function
     */
    public BigDecimal firstSucceedingPercentageWithMinimum(BigDecimal baseAmount,
                                                           BigDecimal firstAmount,
                                                           BigDecimal firstCharge,
                                                           BigDecimal percentOfRemaining, BigDecimal minimum) {

        BigDecimal balance = baseAmount;
        BigDecimal totalCharge = BigDecimal.ZERO;

        if(balance.compareTo(BigDecimal.ZERO) > 0) {

            // todo: refactor to just divide
            totalCharge = totalCharge.add(firstCharge);
            System.out.println("totalCharge:"+totalCharge);
            balance = balance.subtract(firstAmount);
            System.out.println("balance:"+balance);
            totalCharge = totalCharge.add(balance.multiply(percentOfRemaining));
            System.out.println("totalCharge:"+totalCharge);
        }

        if(minimum.compareTo(BigDecimal.ZERO) > 0 && totalCharge.compareTo(minimum) <= 0) {
            return minimum; //.setScale(2, BigDecimal.ROUND_UP);
        }

        return totalCharge; // .setScale(2, BigDecimal.ROUND_UP);
    }

    /***
     *
     * @param baseAmount this is the amount used in computing the charges assumed to be in PHP
     * @param everyAmount this is the amount which is charged the
     * @param chargeForEvery the amount of charge for each everyAmount
     * @return the charge computed using this function
     */

    public BigDecimal forEvery(BigDecimal baseAmount,
                               BigDecimal everyAmount,
                               BigDecimal chargeForEvery) {

        BigDecimal balance = baseAmount;
        BigDecimal totalCharge = BigDecimal.ZERO;

        BigDecimal bg[] = baseAmount.divideAndRemainder(everyAmount);

        totalCharge = chargeForEvery.multiply(bg[0]);

        if(bg[1].compareTo(BigDecimal.ZERO) > 0) {
        //if(bg[0].compareTo(BigDecimal.ZERO) > 0) {
            totalCharge = totalCharge.add(chargeForEvery);
        }

        return totalCharge;

    }

    public BigDecimal percentageOf(BigDecimal amount, BigDecimal percentage ) {

        return amount.multiply(percentage); // .setScale(2, BigDecimal.ROUND_UP);

    }

    // date utilities
    public BigDecimal getMonthsTill(String dateFrom, String dateTo) {

            Integer daysInt = getDaysTill(dateFrom, dateTo);

//            System.out.println("days 365:" + daysint);

        // get a rounded up version of this using a math hack
        // rounding up: (numerator + denominator-1) / denominator
        // rounding down: (numerator + (denominator)/2) / denominator
//            System.out.println("months:" + ((days + 29) / 30));
//            System.out.println("days:" + days);
//            System.out.println("months 360:" + new BigDecimal(days).divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP));
//            System.out.println("months 365:" + new BigDecimal(daysInt).divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP));

            return new BigDecimal(daysInt).divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP);
    }

    // date utilities
    public Integer getDaysTill(String dateFrom, String dateTo) {
//        System.out.println("getMonthsTill");
//        System.out.println("Date From:" + dateFrom);
//        System.out.println("Date To:" + dateTo);

        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");

        try {

            Date startDate = formatter.parse(dateFrom);
            Date endDate = formatter.parse(dateTo);
//            System.out.println("dateFrom:" + dateFrom);
//            System.out.println("dateTo:" + dateTo);

            //Uses Days 30/360 Convention
            Integer days = SerialDateUtilities.dayCountActual(
                    SerialDate.createInstance(startDate),
                    SerialDate.createInstance(endDate)
            );

//            System.out.println("days 360:" + days);

            DateTime dateTimeFrom = new DateTime(startDate);
            DateTime dateTimeTo = new DateTime(endDate);
            int daysint = Days.daysBetween(dateTimeFrom, dateTimeTo).getDays();
            Integer daysInt = new Integer(daysint);

            System.out.println("\n>>>>>>>>>>> daysInt = " + daysint + "\n");

            return daysInt;

        } catch (Exception e) {
            // todo: handle invalid dates here
            return new Integer(0);
        }
    }

    // date utilities
    public BigDecimal getDaysTillA(String dateFrom, String dateTo) {
//        System.out.println("getMonthsTill");
//        System.out.println("Date From:" + dateFrom);
//        System.out.println("Date To:" + dateTo);

        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");

        try {
            Date startDate = formatter.parse(dateFrom);
            Date endDate = formatter.parse(dateTo);
//            System.out.println("dateFrom:" + dateFrom);
//            System.out.println("dateTo:" + dateTo);

            //Uses Days 30/360 Convention
            Integer days = SerialDateUtilities.dayCountActual(
                    SerialDate.createInstance(startDate),
                    SerialDate.createInstance(endDate)
            );

//            System.out.println("days 360:" + days);

            DateTime dateTimeFrom = new DateTime(startDate);
            DateTime dateTimeTo = new DateTime(endDate);
            int daysint = Days.daysBetween(dateTimeFrom, dateTimeTo).getDays();
            Integer daysInt = new Integer(daysint);

            return new BigDecimal(daysInt);

        } catch (Exception e) {
            // todo: handle invalid dates here
            return new BigDecimal(0);
        }
    }

    /***
     * returns number of months assuming 30 days per month rounded up
     * @param daysPeriod
     * @return number of months assuming 30 days per month rounded up
     */
    public BigDecimal getMonthsOf(BigDecimal daysPeriod) {
        if (daysPeriod == null) {
            daysPeriod = new BigDecimal("1");
        }
        return daysPeriod.divide(new BigDecimal("30"), 6, BigDecimal.ROUND_HALF_UP);
    }

}

