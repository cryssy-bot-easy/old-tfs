package com.ucpb.tfs.charges

import com.ucpb.tfs2.application.util.CurrencyConverter
import com.ucpb.tfs2.application.util.FXLCChargesCalculator
import junit.framework.Assert
import org.junit.Test

import java.math.RoundingMode

/**
 * User: angulo
 * Date: 3/30/13
 * Time: 00:23 AM
 */
class FxLcChargesTest {

    //@Test
    public void testFXLCOpeningCash() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
//        currencyConverter.addRate("REG-SELL", "USD", "PHP",  42.5000000)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "CASH",
                        documentSubType2: "SIGHT",
                        expiryDate: "3/10/2013",
                        etsDate: "2/8/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        println temp
        Assert.assertEquals("Testing Opening Cash BC", 3410.17, temp.get("BC"))
        Assert.assertEquals("Testing Opening Cash CF", temp.get("CF"), 0.00)
        Assert.assertEquals("Testing Opening Cash DOCSTAMPS", 4092.30, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Testing Opening Cash CILEX", 6102.95, temp.get("CILEX"))
        Assert.assertEquals("Testing Opening Cash CABLE", 800.00, temp.get("CABLE"))
        Assert.assertEquals("Testing Opening Cash SUP", 50.00, temp.get("SUP"))
        Assert.assertEquals("Testing Opening Cash ADVISING", 2113.00, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Testing Opening Cash CONFIRMING", 3410.17, temp.get("CORRES-CONFIRMING"))

    }

    //@Test
    public void testFXLCOpeningCash2() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
//        currencyConverter.addRate("REG-SELL", "USD", "PHP",  42.5000000)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "USD",
                productAmount: 234567.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        //[mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "234567.00"],
                        //[mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "CASH",
                        documentSubType2: "SIGHT",
                        expiryDate: "04/30/2013",
                        etsDate: "10/01/2012",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        println temp
//        Assert.assertEquals("Testing Opening Cash BC", 3410.17, temp.get("BC"))
//        Assert.assertEquals("Testing Opening Cash CF", temp.get("CF"), 0.00)
//        Assert.assertEquals("Testing Opening Cash DOCSTAMPS", 4092.30, temp.get("DOCSTAMPS"))
//        Assert.assertEquals("Testing Opening Cash CILEX", 6102.95, temp.get("CILEX"))
//        Assert.assertEquals("Testing Opening Cash CABLE", 800.00, temp.get("CABLE"))
//        Assert.assertEquals("Testing Opening Cash SUP", 50.00, temp.get("SUP"))
//        Assert.assertEquals("Testing Opening Cash ADVISING", 2113.00, temp.get("CORRES-ADVISING"))
//        Assert.assertEquals("Testing Opening Cash CONFIRMING", 3410.17, temp.get("CORRES-CONFIRMING"))

    }

    //@Test
    public void testFXLCOpeningCash3() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
//        currencyConverter.addRate("REG-SELL", "USD", "PHP",  42.5000000)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "USD",
                productAmount: 555666.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        //[mode: "CASH", currency: "EUR", amount: "35000.00"],
//                        [mode: "CASA", currency: "USD", amount: "234567.00"],
//                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                        [mode: "CASA", currency: "PHP", amount: "23599135.02"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "CASH",
                        documentSubType2: "SIGHT",
                        expiryDate: "04/30/2013",
                        etsDate: "10/01/2012",
                        advisingFlag: "N",
                        confirmingFlag: "N"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        println temp
//        Assert.assertEquals("Testing Opening Cash BC", 3410.17, temp.get("BC"))
//        Assert.assertEquals("Testing Opening Cash CF", temp.get("CF"), 0.00)
//        Assert.assertEquals("Testing Opening Cash DOCSTAMPS", 4092.30, temp.get("DOCSTAMPS"))
//        Assert.assertEquals("Testing Opening Cash CILEX", 6102.95, temp.get("CILEX"))
//        Assert.assertEquals("Testing Opening Cash CABLE", 800.00, temp.get("CABLE"))
//        Assert.assertEquals("Testing Opening Cash SUP", 50.00, temp.get("SUP"))
//        Assert.assertEquals("Testing Opening Cash ADVISING", 2113.00, temp.get("CORRES-ADVISING"))
//        Assert.assertEquals("Testing Opening Cash CONFIRMING", 3410.17, temp.get("CORRES-CONFIRMING"))

    }

    //@Test
    public void testFXLCOpeningStandby() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.284735)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 44.980000)
        currencyConverter.addRate("URR", "USD", "PHP", 44.920000)



        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 90000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties: [
                        documentType: "DOMESTIC",
                        documentSubType1: "STANDBY",
                        documentSubType2: "SIGHT",
                        expiryDate: "3/31/2013",
                        etsDate: "3/26/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        println temp
        Assert.assertEquals("Testing Opening STANDBY BC", 6501.08, temp.get("BC")) //6501.8 one cent difference with excel calculator
        Assert.assertEquals("Testing Opening STANDBY CF", 13002.16, temp.get("CF"))
        Assert.assertEquals("Testing Opening STANDBY SUP", 50.00, temp.get("SUP"))

        productDetails = [
                productCurrency: "EUR",
                productAmount: 90000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties: [
                        documentType: "DOMESTIC",
                        documentSubType1: "STANDBY",
                        documentSubType2: "SIGHT",
                        expiryDate: "3/31/2013",
                        etsDate: "3/26/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        temp = calculator.computeOpening(productDetails)
        println temp
        Assert.assertEquals("Testing Opening STANDBY BC less than 1 month", 6501.08, temp.get("BC")) //6501.8 one cent difference with excel calculator
        Assert.assertEquals("Testing Opening STANDBY CF 1 month", 13002.16, temp.get("CF"))
        Assert.assertEquals("Testing Opening STANDBY SUP 1 month", 50.00, temp.get("SUP"))

        productDetails = [
                productCurrency: "EUR",
                productAmount: 90000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties: [
                        documentType: "DOMESTIC",
                        documentSubType1: "STANDBY",
                        documentSubType2: "SIGHT",
                        expiryDate: "3/31/2013",
                        etsDate: "3/26/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        temp = calculator.computeOpening(productDetails)
        println temp
        Assert.assertEquals("Testing Opening STANDBY BC less than 1 month", 6501.08, temp.get("BC")) //6501.8 one cent difference with excel calculator
        Assert.assertEquals("Testing Opening STANDBY CF 1 month", 13002.16, temp.get("CF"))
        Assert.assertEquals("Testing Opening STANDBY SUP 1 month", 50.00, temp.get("SUP"))


    }

    //@Test
    public void testFXLCOpeningRegularSight() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.284735)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.50)
        currencyConverter.addRate("URR", "USD", "PHP", 42.26)



        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties: [
                        documentType: "DOMESTIC",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        expiryDate: "3/10/2013",
                        etsDate: "2/8/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();
        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        println temp
        Assert.assertEquals("Testing Opening REGULAR SIGHT BC", temp.get("BC"), 3412.58)
        Assert.assertEquals("Testing Opening REGULAR SIGHT CABLE", temp.get("CABLE"), 800.00)
        Assert.assertEquals("Testing Opening REGULAR SIGHT CF", temp.get("CF"), 0.00)
        Assert.assertEquals("Testing Opening REGULAR SIGHT SUP", temp.get("SUP"), 50.00)
        Assert.assertEquals("Testing Opening REGULAR SIGHT ADVISING", temp.get("CORRES-ADVISING"), 2113.00)
        Assert.assertEquals("Testing Opening REGULAR SIGHT CONFIRMING", temp.get("CORRES-CONFIRMING"), 3412.58)
    }

    //@Test
    public void testFXLCOpeningRegularUsance() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 44.980000)
        currencyConverter.addRate("URR", "USD", "PHP", 44.920000)



        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASA", currency: "EUR", amount: "20000"],
                        [mode: "CASA", currency: "USD", amount: "40170.27"],
                        [mode: "TR", currency: "PHP", amount: "407500"]
                ],
                extendedProperties: [
                        documentType: "DOMESTIC",
                        documentSubType1: "REGULAR",
                        documentSubType2: "USANCE",
                        expiryDate: "3/10/2013",
                        etsDate: "2/8/2013",
                        usancePeriod: "45",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();
        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeOpening(productDetails)
        println temp
        Assert.assertEquals("Testing Opening REGULAR USANCE BC", 3611.71, temp.get("BC"))
        Assert.assertEquals("Testing Opening REGULAR USANCE CF", 10835.13, temp.get("CF"))//6854.05 one cent difference with excel calculator
        Assert.assertEquals("Testing Opening REGULAR USANCE SUP", 50.00, temp.get("SUP"))
    }

    //@Test
    public void testFXLCNegotiationCash() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 500000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "CASH",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/2/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeNegotiation(productDetails)
        println temp
        //Assert.assertEquals("Testing Negotiation Cash DOCSTAMPS",temp.get("DOCSTAMPS"), 40951.20)
        //Assert.assertEquals("Testing Negotiation Cash CILEX",temp.get("CILEX"), 0)
        Assert.assertEquals("Testing Negotiation Cash CABLE", temp.get("CABLE"), 500.00)
        Assert.assertEquals("Testing Negotiation Cash NOTARIAL", 50.00, temp.get("NOTARIAL"))

    }

    //@Test
    public void testFXLCNegotiationRegularUsance() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 500000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "USANCE",
                        expiryDate: "6/30/2013",
                        etsDate: "4/2/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeNegotiation(productDetails)
        println temp

        //Assert.assertEquals("Testing Negotiation Cash DOCSTAMPS",temp.get("DOCSTAMPS"), 40951.20)
        //Assert.assertEquals("Testing Negotiation Cash CILEX",temp.get("CILEX"), 0)
        Assert.assertEquals("Testing Negotiation Cash CABLE", temp.get("CABLE"), 500.00)
        Assert.assertEquals("Testing Negotiation Cash NOTARIAL", 50.00, temp.get("NOTARIAL"))

    }

    //@Test
    public void testFXLCNegotiationStandby() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 500000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "STANBY",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/2/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeNegotiation(productDetails)
        println temp

        Assert.assertEquals("Testing Negotiation Standby CILEX", 6102.95, temp.get("CILEX"))
        Assert.assertEquals("Testing Negotiation Standby CABLE", 500.00, temp.get("CABLE"))

    }

    //@Test
    public void testFXLCNegotiationRegularSight() {

        CurrencyConverter currencyConverter = new CurrencyConverter()

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/2/2013",
                        advisingFlag: "Y",
                        confirmingFlag: "Y"
                ]
        ]

        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeNegotiation(productDetails)
        println temp

        Assert.assertEquals("Testing Negotiation Cash DOCSTAMPS", 560.00, temp.get("DOCSTAMPS")) //from personal computation not from charges excel
        Assert.assertEquals("Testing Negotiation Cash CILEX", 6102.95, temp.get("CILEX"))
        Assert.assertEquals("Testing Negotiation Cash CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Testing Negotiation Cash NOTARIAL", 50.00, temp.get("NOTARIAL"))

    }

    //@Test
    public void roundUpTest() {
        BigDecimal temp = 1000.21
        println temp.setScale(0, RoundingMode.HALF_UP)
    }

    //@Test
    public void testFXLCAmendment() {
        CurrencyConverter currencyConverter = new CurrencyConverter()
        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("CASH-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        advanceCorresChargesFlag: "Y",
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        //expiryDate: "6/30/2013",
                        expiryDate: "2/28/2014",
                        //etsDate:"4/18/2013",
                        etsDate: "5/8/2013",
                        //dateTo: "8/2/2013",
                        //usancePeriod: "45",
                        amount: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        confirmingFlag: "N",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "N"


                ]
        ]

        currencyConverter = new CurrencyConverter()
        calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        def temp = calculator.computeAmendment(productDetails)
        println "Increase in fxlc amount regular sight"
        println "temp:" + temp

        Assert.assertEquals("Increase in fxlc amount regular sight amendment BC",   6734.15, temp.get("BC"))
        Assert.assertEquals("Increase in fxlc amount regular sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Increase in fxlc amount regular sight amendment CABLE",   819.30, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Increase in fxlc amount regular sight amendment CORRES-ADVISING", 2125.00, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Increase in fxlc amount regular sight amendment TOTAL", 10178.45, temp.get("TOTAL"))


        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amountFrom: "50000",
                        amountTo: "45000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        expiryDateFlag: "on",
                        expiryDateFlag: "DEC",
                        //cwtFlag:"Y",
                        cwtFlag: "N",
                        cwtPercentage: "0.98"

                ]
        ]
        temp = calculator.computeAmendment(productDetails)
        println "Expiry date Decrease"
        println "temp:" + temp

        Assert.assertEquals("Change in confirmation instruction amendment BC", 500.00, temp.get("BC"))
        Assert.assertEquals("Change in confirmation instruction amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in confirmation instruction amendment TOTAL", 1000.00, temp.get("TOTAL"))



        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amountFrom: "50000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        narrativesCheck: "on",
                        //cwtFlag:"Y",
                        cwtFlag: "N",
                        cwtPercentage: "0.98"

                ]
        ]
        temp = calculator.computeAmendment(productDetails)
        println "Narratives change"
        println "temp:" + temp

        Assert.assertEquals("Change in narratives amendment BC", 500.00, temp.get("BC"))
        Assert.assertEquals("Change in narratives amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in narratives amendment TOTAL", 1000.00, temp.get("TOTAL"))

        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amount: "50000",
                        amountFrom: "50000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        changeInConfirmationCheck: "on",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        advanceCorresChargesFlag: "Y",
                        confirmationInstructionsFlagTo:'YES',
                        originalConfirmationInstructionsFlag:'NO',
                        confirmationInstructionsFlag:'on'
                ]
        ]
        temp = calculator.computeAmendment(productDetails)
        println "Change in confirmation instruction amendment"
        println "temp:" + temp

        Assert.assertEquals("Change in confirmation instruction amendment BC", 500.00, temp.get("BC"))
        Assert.assertEquals("Change in confirmation instruction amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in confirmation instruction amendment CORRES-ADVISING", 2125.00, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Change in confirmation instruction amendment CORRES-CONFIRMING", 8303.94, temp.get("CORRES-CONFIRMING"))
        Assert.assertEquals("Change in confirmation instruction amendment TOTAL", 11428.94, temp.get("TOTAL"))


        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "CASH",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amountFrom: "50000",
                        amount: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "n",
                        advanceCorresChargesFlag: "Y",

                ]
        ]


        currencyConverter = new CurrencyConverter()
        calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)


        temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment cash sight"
        println "temp:" + temp

        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment BC", 1651.41, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment DOCSTAMPS", 814.50, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment CORRES-ADVISING", 2123.50, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment TOTAL", 5089.41, temp.get("TOTAL"))

        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "STANDBY",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amountFrom: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
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

        temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment standby sight"
        println "temp:" + temp


        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "USANCE",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amountFrom: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "n"

                ]
        ]

        temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment standby sight"
        println "temp:" + temp

    }

    //@Test
    public void testFXLCAmendmentWeirdExtension() {
        CurrencyConverter currencyConverter = new CurrencyConverter()
        FXLCChargesCalculator calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("CASH-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("CASH-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        Map productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        advanceCorresChargesFlag: "Y",
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        //expiryDate: "6/30/2013",
                        expiryDate: "2/28/2014",
                        amendmentDate:"5/8/2013",
                        etsDate: "5/8/2013",
                        //dateTo: "8/2/2013",
                        //usancePeriod: "45",
                        amount: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        confirmingFlag: "N",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "N"


                ]
        ]

        currencyConverter = new CurrencyConverter()
        calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.5000000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)

        def temp = calculator.computeAmendment(productDetails)
        println "Increase in fxlc amount regular sight"
        println "temp:" + temp

        Assert.assertEquals("Increase in fxlc amount regular sight amendment BC",   6734.15, temp.get("BC"))
        Assert.assertEquals("Increase in fxlc amount regular sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Increase in fxlc amount regular sight amendment DOCSTAMPS",   819.30, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Increase in fxlc amount regular sight amendment CORRES-ADVISING", 2125.00, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Increase in fxlc amount regular sight amendment TOTAL", 10178.45, temp.get("TOTAL"))


        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amountFrom: "50000",
                        amountTo: "45000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        expiryDateFlag: "on",
                        expiryDateFlag: "DEC",
                        //cwtFlag:"Y",
                        cwtFlag: "N",
                        cwtPercentage: "0.98"

                ]
        ]
        temp = calculator.computeAmendment(productDetails)
        println "Expiry date Decrease"
        println "temp:" + temp

        Assert.assertEquals("Change in expiry date decrease amendment regular sight BC", 500.00, temp.get("BC"))
        Assert.assertEquals("Change in expiry date decrease amendment regular sight CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in expiry date decrease amendment regular sight TOTAL", 1000.00, temp.get("TOTAL"))



        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amountFrom: "50000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        narrativesCheck: "on",
                        //cwtFlag:"Y",
                        cwtFlag: "N",
                        cwtPercentage: "0.98"

                ]
        ]
        temp = calculator.computeAmendment(productDetails)
        println "Narratives change"
        println "temp:" + temp

        Assert.assertEquals("Change in narratives amendment regular sight BC", 500.00, temp.get("BC"))
        Assert.assertEquals("Change in narratives amendment regular sight CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in narratives amendment regular sight TOTAL", 1000.00, temp.get("TOTAL"))




        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "SIGHT",
                        expiryDate: "11/30/2013",
                        amendmentDate: "2/18/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amount: "50000",
                        amountFrom: "50000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        changeInConfirmationCheck: "on",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        advanceCorresChargesFlag: "Y",
                        confirmationInstructionsFlagTo:'YES',
                        originalConfirmationInstructionsFlag:'NO',
                        confirmationInstructionsFlag:'on'
                ]
        ]
        temp = calculator.computeAmendment(productDetails)
        println "Change in confirmation instruction amendment"
        println "temp:" + temp

        Assert.assertEquals("Change in confirmation instruction amendment regular sight BC", 500.00, temp.get("BC"))
        Assert.assertEquals("Change in confirmation instruction amendment regular sight CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in confirmation instruction amendment regular sight CORRES-ADVISING", 2125.00, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Change in confirmation instruction amendment regular sight CORRES-CONFIRMING", 32419.48, temp.get("CORRES-CONFIRMING"))
        Assert.assertEquals("Change in confirmation instruction amendment regular sight TOTAL", 35544.48, temp.get("TOTAL"))


        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "60000.00"],
                        //[mode: "CASA", currency: "USD", amount: "12800.00"],
                        //[mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "CASH",
                        documentSubType2: "SIGHT",
                        expiryDate: "11/30/2013",
                        //etsDate: "4/18/2013",
                        amendmentDate: "5/30/2013",
                        //dateTo: "8/2/2013",
                        //usancePeriod: "45",
                        amountFrom: "50000",
                        amount: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "n",
                        advanceCorresChargesFlag: "Y",

                ]
        ]


        currencyConverter = new CurrencyConverter()
        calculator = new FXLCChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

        currencyConverter.addRate("REG-SELL", "EUR", "USD", 1.2847350)
        currencyConverter.addRate("REG-SELL", "USD", "PHP", 42.4700000)
        currencyConverter.addRate("URR", "USD", "PHP", 42.2600000)


        temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment cash sight"
        println "temp:" + temp

        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment BC", 4162.46, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment DOCSTAMPS", 814.50, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment CORRES-ADVISING", 2123.50, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Change in increase in fxlc amount cash sight amendment TOTAL", 7600.46, temp.get("TOTAL"))

        productDetails = [
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
                        //dateTo: "8/2/2013",
                        //usancePeriod: "45",
                        amountFrom: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
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

        temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment standby sight"
        println "temp:" + temp

        println "temp:" + temp

        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment BC", 4186.09, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment DOCSTAMPS", 819.30, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CF", 8372.19, temp.get("CF"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CORRES-ADVISING", 2125.00, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment TOTAL", 16002.58, temp.get("TOTAL"))


        productDetails = [
                productCurrency: "EUR",
                productAmount: 50000.00,
                chargeSettlementCurrency: "PHP",
                productSettlement: [
                        [mode: "CASH", currency: "EUR", amount: "35000.00"],
                        [mode: "CASA", currency: "USD", amount: "12800.00"],
                        [mode: "TR", currency: "PHP", amount: "272000.00"]
                ],
                extendedProperties: [
                        documentType: "FOREIGN",
                        documentSubType1: "REGULAR",
                        documentSubType2: "USANCE",
                        expiryDate: "6/30/2013",
                        etsDate: "4/18/2013",
                        dateTo: "8/2/2013",
                        usancePeriod: "45",
                        amountFrom: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
                        cwtFlag: "N",
                        cwtPercentage: "0.98",
                        changeInConfirmationCheck: "n"

                ]
        ]

        temp = calculator.computeAmendment(productDetails)
        println "increase in fxlc amount amendment standby sight"
        println "temp:" + temp

        println "temp:" + temp

        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment BC", 1651.41, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment DOCSTAMPS", 814.50, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CF", 814.50, temp.get("CF"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CORRES-ADVISING", 2123.50, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment TOTAL", 5089.41, temp.get("TOTAL"))

    }

    //@Test
    public void testFXLCAmendment01() {
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
                        //dateTo: "8/2/2013",
                        //usancePeriod: "45",
                        amount: "50000",
                        amountFrom: "50000",
                        amountTo: "60000",
                        advisingFlag: "Y",
                        advanceCorresChargesFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "N",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
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

        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment BC", 4186.09, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment DOCSTAMPS", 819.30, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CF", 8372.19, temp.get("CF"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment CORRES-ADVISING", 2125.00, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Change in increase in fxlc amount standby sight amendment TOTAL", 16002.58, temp.get("TOTAL"))



    }

    //@Test
    public void testFXLCAmendmentIncreaseAndExtend() {
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
                        amountTo: "60000",
                        advisingFlag: "Y",
                        expiryDateFlag: "EXT",
                        expiryDateCheck: "on",
                        advanceCorresChargesFlag: "Y",
                        confirmingFlag: "Y",
                        tenorCheck: "Y",
                        amountSwitch: "on",
                        lcAmountFlag: "INC",
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

        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment BC", 4186.09, temp.get("BC"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CABLE", 500.00, temp.get("CABLE"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment DOCSTAMPS", 819.30, temp.get("DOCSTAMPS"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CF", 8372.19, temp.get("CF"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment CORRES-ADVISING", 2125.00, temp.get("CORRES-ADVISING"))
        Assert.assertEquals("Change in increase in fxlc amount extend expiry standby sight amendment TOTAL", 16002.58, temp.get("TOTAL"))



    }

    @Test
    public void testFXLCAmendmentExtend() {
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
