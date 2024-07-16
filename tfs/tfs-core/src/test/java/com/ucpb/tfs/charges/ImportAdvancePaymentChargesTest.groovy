package com.ucpb.tfs.charges
import com.ucpb.tfs2.application.util.CurrencyConverter
import com.ucpb.tfs2.application.util.ImportAdvanceChargesCalculator
import junit.framework.Assert
import org.junit.Test
/**
 * User: angulo
 * Date: 3/26/13
 * Time: 8:45 PM
 */
class ImportAdvancePaymentChargesTest {

    @Test
    public void testImportAdvancePayment() {

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

        ImportAdvanceChargesCalculator calculator = new ImportAdvanceChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeAdvancePayment(productDetails)

        Assert.assertEquals("Testing Opening Cash BC",temp.get("BC"),3410.29)
        Assert.assertEquals("Testing Opening Cash CF",temp.get("CF"),0.00)
        Assert.assertEquals("Testing Opening Cash SUP",temp.get("SUP"),50.00)
    }

    @Test
    public void testImportAdvanceRefund() {

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

        ImportAdvanceChargesCalculator calculator = new ImportAdvanceChargesCalculator();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.computeAdvancePayment(productDetails)

        Assert.assertEquals("Testing Opening Cash BC",temp.get("BC"),3410.29)
        Assert.assertEquals("Testing Opening Cash CF",temp.get("CF"),0.00)
        Assert.assertEquals("Testing Opening Cash SUP",temp.get("SUP"),50.00)
    }
}
