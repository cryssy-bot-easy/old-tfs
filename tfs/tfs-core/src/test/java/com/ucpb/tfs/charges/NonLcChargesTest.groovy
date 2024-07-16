package com.ucpb.tfs.charges

import com.ucpb.tfs2.application.util.CurrencyConverter
import com.ucpb.tfs2.application.util.NonLCChargesCalculator
import org.junit.Test

/**
 * User: angulo
 * Date: 3/26/13
 * Time: 10:21 PM
 */

class NonLcChargesTest  {

    @Test
    public void testDPSettlement() {

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
                        bankCommissionDenominator:"4",
                        remittanceFlag:"Y",
                        cableFeeFlag:"Y",
                        documentType:"FOREIGN"
                ]
        ]

        NonLCChargesCalculator calculator = new NonLCChargesCalculator ();

        calculator.setCurrencyConverter(currencyConverter)
        calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
        def temp = calculator.compute(productDetails)
        println "temp:"+temp

    }

}
