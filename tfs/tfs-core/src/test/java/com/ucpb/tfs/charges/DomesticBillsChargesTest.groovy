package com.ucpb.tfs.charges

import com.ucpb.tfs2.application.util.CurrencyConverter
import com.ucpb.tfs2.application.util.FXLCChargesCalculator
import junit.framework.Assert
import org.junit.Test

/**
 * User: angulo
 * Date: 3/30/13
 * Time: 00:23 AM
 */
class DomesticBillsChargesTest {

    @Test
    public void testDomesticBillsCollectionSettlement() {
        CurrencyConverter currencyConverter = new CurrencyConverter()
        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("CASH-SELL", "EUR", "USD", 1.2847350)
        //currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "50000.00"],
                        // [mode: "CASA", currency: "USD", amount: "12800.00"],
                        //[mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "STANDBY",
                        documentSubType2: "SIGHT",
                        expiryDate: "11/30/2013",
                        amendmentDate: "5/30/2013",
                        etsDate: "4/18/2013",
                        expiryDateTo: "11/30/2014",
                        //dateTo: "8/2/2013",
                        //usancePeriod: "45",
                        amount: "50000",
                        amountFrom: "50000",
                        //amountTo: "60000",
                        advisingFlag: "Y",
                        expiryDateFlag: "EXT",
                        expiryDateCheck: "on",
                        advanceCorresChargesFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "Y",
                        //amountSwitch: "on",
                        //lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "n"

                ]
        ]

        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("CASH-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        def temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment standby sight"
        println "temp:" + temp

        println "temp:" + temp

        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment BC", 41519.69, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CF", 83039.38, temp.get("CF"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment TOTAL", 125059.07, temp.get("TOTAL"))



    }

    //@Test
    public void testDomesticBillsPaymentNegotiation() {
        CurrencyConverter currencyConverter = new CurrencyConverter()
        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("CASH-SELL", "EUR", "USD", 1.2847350)
        //currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "50000.00"],
                        // [mode: "CASA", currency: "USD", amount: "12800.00"],
                        //[mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "STANDBY",
                        documentSubType2: "SIGHT",
                        expiryDate: "11/30/2013",
                        amendmentDate: "5/30/2013",
                        etsDate: "4/18/2013",
                        expiryDateTo: "11/30/2014",
                        //dateTo: "8/2/2013",
                        //usancePeriod: "45",
                        amount: "50000",
                        amountFrom: "50000",
                        //amountTo: "60000",
                        advisingFlag: "Y",
                        expiryDateFlag: "EXT",
                        expiryDateCheck: "on",
                        advanceCorresChargesFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "Y",
                        //amountSwitch: "on",
                        //lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "n"

                ]
        ]

        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("CASH-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        def temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment standby sight"
        println "temp:" + temp

        println "temp:" + temp

        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment BC", 41519.69, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CF", 83039.38, temp.get("CF"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment TOTAL", 125059.07, temp.get("TOTAL"))



    }

    //@Test
    public void testDomesticBillsPaymentSettlement() {
        CurrencyConverter currencyConverter = new CurrencyConverter()
        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("CASH-SELL", "EUR", "USD", 1.2847350)
        //currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "50000.00"],
                        // [mode: "CASA", currency: "USD", amount: "12800.00"],
                        //[mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "STANDBY",
                        documentSubType2: "SIGHT",
                        expiryDate: "11/30/2013",
                        amendmentDate: "5/30/2013",
                        etsDate: "4/18/2013",
                        expiryDateTo: "11/30/2014",
                        //dateTo: "8/2/2013",
                        //usancePeriod: "45",
                        amount: "50000",
                        amountFrom: "50000",
                        //amountTo: "60000",
                        advisingFlag: "Y",
                        expiryDateFlag: "EXT",
                        expiryDateCheck: "on",
                        advanceCorresChargesFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "Y",
                        //amountSwitch: "on",
                        //lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "n"

                ]
        ]

        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("CASH-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        def temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment standby sight"
        println "temp:" + temp

        println "temp:" + temp

        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment BC", 41519.69, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CF", 83039.38, temp.get("CF"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment TOTAL", 125059.07, temp.get("TOTAL"))



    }
}

