package com.ucpb.tfs.charges

import com.ucpb.tfs2.application.util.CurrencyConverter
import com.ucpb.tfs2.application.util.DMLCChargesCalculator
import junit.framework.Assert
import org.junit.Test

/**
 * User: angulo
 * Date: 3/26/13
 * Time: 8:45 PM
 */
class DmLcChargesTest {

    @Test
    public void testDMLCOpeningCash() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.339009)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 40.950000)
        currencyConverter.addRate("URR", "USD", "PHP", 40.750000)

        Map productDetails = [
                productCurrency : "EUR",
                productAmount : 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement : [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties:[
                        documentType:"FOREIGN",
                        documentSubType1:"CASH",
                        documentSubType2:"SIGHT",
                        expiryDate:"3/10/2013",
                        etsDate:"2/8/2013",
                ]
        ]

        DMLCChargesCalculator calculator = new DMLCChargesCalculator ();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        Assert.assertEquals("Testing Opening Cash BC",temp.get("BC"),3410.29)
        Assert.assertEquals("Testing Opening Cash CF",temp.get("CF"),0.00)
        Assert.assertEquals("Testing Opening Cash SUP",temp.get("SUP"),50.00)

    }

    @Test
    public void testDMLCOpeningStandby() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.284735)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 44.980000)
        currencyConverter.addRate("URR", "USD", "PHP", 44.920000)



        Map productDetails = [
                productCurrency : "EUR",
                productAmount : 90000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement : [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties:[
                        documentType:"DOMESTIC",
                        documentSubType1:"STANDBY",
                        documentSubType2:"SIGHT",
                        expiryDate:"3/31/2013",
                        etsDate:"3/26/2013",
                ]
        ]

        DMLCChargesCalculator calculator = new DMLCChargesCalculator ();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        Assert.assertEquals("Testing Opening STANDBY BC",6501.08,temp.get("BC")) //6501.8 one cent difference with excel calculator
        Assert.assertEquals("Testing Opening STANDBY CF",13002.16,temp.get("CF"))
        Assert.assertEquals("Testing Opening STANDBY SUP",50.00,temp.get("SUP"))

        productDetails = [
                productCurrency : "EUR",
                productAmount : 90000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement : [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties:[
                        documentType:"DOMESTIC",
                        documentSubType1:"STANDBY",
                        documentSubType2:"SIGHT",
                        expiryDate:"3/31/2013",
                        etsDate:"3/26/2013",
                ]
        ]

        temp = calculator.computeOpening(productDetails)
        Assert.assertEquals("Testing Opening STANDBY BC less than 1 month",6501.08,temp.get("BC")) //6501.8 one cent difference with excel calculator
        Assert.assertEquals("Testing Opening STANDBY CF 1 month",13002.16,temp.get("CF"))
        Assert.assertEquals("Testing Opening STANDBY SUP 1 month",50.00,temp.get("SUP"))

        productDetails = [
                productCurrency : "EUR",
                productAmount : 90000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement : [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties:[
                        documentType:"DOMESTIC",
                        documentSubType1:"STANDBY",
                        documentSubType2:"SIGHT",
                        expiryDate:"3/31/2013",
                        etsDate:"3/26/2013",
                ]
        ]

        temp = calculator.computeOpening(productDetails)
        Assert.assertEquals("Testing Opening STANDBY BC less than 1 month",6501.08,temp.get("BC")) //6501.8 one cent difference with excel calculator
        Assert.assertEquals("Testing Opening STANDBY CF 1 month",13002.16,temp.get("CF"))
        Assert.assertEquals("Testing Opening STANDBY SUP 1 month",50.00,temp.get("SUP"))


    }

    @Test
    public void testDMLCOpeningRegularSight() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.339009)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 40.950000)
        currencyConverter.addRate("URR", "USD", "PHP", 40.750000)



        Map productDetails = [
                productCurrency : "EUR",
                productAmount : 50000.00,
                chargeSettlementCurrency: "PHP",
//                productSettlement : [
//                        [mode: "CASA", currency: "EUR", amount: "20000"],
//                        [mode: "CASA", currency: "USD", amount: "40170.27"],
//                        [mode: "TR", currency: "PHP", amount: "407500"]
//                ],
                extendedProperties:[
                        documentType:"DOMESTIC",
                        documentSubType1:"REGULAR",
                        documentSubType2:"SIGHT",
                        expiryDate:"3/10/2013",
                        etsDate:"2/8/2013",
                ]
        ]

        DMLCChargesCalculator calculator = new DMLCChargesCalculator ();


        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        Assert.assertEquals("Testing Opening REGULAR SIGHT BC",temp.get("BC"),3427.03)
        Assert.assertEquals("Testing Opening REGULAR SIGHT CF",temp.get("CF"),6854.05)
        Assert.assertEquals("Testing Opening REGULAR SIGHT SUP",temp.get("SUP"),50.00)
    }

    @Test
    public void testDMLCOpeningRegularUsance() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 44.980000)
        currencyConverter.addRate("URR", "USD", "PHP", 44.920000)



        Map productDetails = [
                productCurrency : "EUR",
                productAmount : 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement : [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties:[
                        documentType:"DOMESTIC",
                        documentSubType1:"REGULAR",
                        documentSubType2:"USANCE",
                        expiryDate:"3/10/2013",
                        etsDate:"2/8/2013",
                        usancePeriod:"45",
                ]
        ]

        DMLCChargesCalculator calculator = new DMLCChargesCalculator ();


        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        Assert.assertEquals("Testing Opening REGULAR USANCE BC",3611.71,temp.get("BC"))
        Assert.assertEquals("Testing Opening REGULAR USANCE CF",10835.13,temp.get("CF") )//6854.05 one cent difference with excel calculator
        Assert.assertEquals("Testing Opening REGULAR USANCE SUP",50.00,temp.get("SUP"))
    }

}
