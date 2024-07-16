package com.ucpb.tfs2.application.util

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:54 PM
 */

/*
(revision)
SCR/ER Number:
SCR/ER Description: Incorrect Docstamp computation (Redmine# 3765)
[Revised by:] Robin C. Rafael
[Date deployed:]
Program [Revision] Details: Change the returned value rounding to ROUND_HALF_UP instead of ROUND_UP
Member Type: Groovy
Project: CORE
Project Name: CurrencyConverter.groovy
*/
class CurrencyConverter {

    Node rates;

    public CurrencyConverter() {
        rates = new Node(null, "rates");
    }

    public addRate(String rateType, String sourceCurrency, String targetCurrency, BigDecimal rate) {

        if(rates.get(sourceCurrency).isEmpty() ) {
            rates.appendNode(sourceCurrency, null)
        }

        Node baseNode = (Node) ((NodeList) rates.get(sourceCurrency)).get(0)

        NodeList targetNodeList = (NodeList) baseNode.get(targetCurrency)

        if (targetNodeList.isEmpty()) {
            baseNode.appendNode(targetCurrency, null)
        }

        Node targetNode = (Node) ((NodeList) baseNode.get(targetCurrency)).get(0)

        NodeList rateTypeList = (NodeList) targetNode.get(rateType)

        if (rateTypeList.isEmpty()) {
            targetNode.appendNode(rateType, null, rate)
        }

    }

    public getRate(String rateType, String sourceCurrency, String targetCurrency) {

        NodeList targetList = (NodeList) rates.get(sourceCurrency)

        if (targetList.isEmpty()) {
            return null
        }
        else {

            NodeList sourceList = (NodeList) targetList.get(0).get(targetCurrency)
            NodeList ratesList = (NodeList) sourceList.get(0).get(rateType)

            if (ratesList.isEmpty()) {
                return null
            }
            else {
                return ratesList.get(0).value()
            }

        }
    }

    public String getXml() {

        def sw = new StringWriter()
        new XmlNodePrinter(new PrintWriter(sw)).print(rates)
        return sw.toString()

    }
    /****
     *
     * @param rateType what rate is to be used to convert
     * @param sourceCurrency currency of amount to be converted
     * @param baseAmount amount to be converted
     * @param targetCurrency currency amount is to be converted to
     * @return converted amount
     */

    public convert(String rateType, String sourceCurrency, BigDecimal baseAmount, String targetCurrency) {

        BigDecimal newValue;

        if (sourceCurrency.equalsIgnoreCase(targetCurrency)) {
            return baseAmount
        }

        BigDecimal rate = getRate(rateType, sourceCurrency, targetCurrency)

        if (rate == null) {

            // if no rate is available, try the reverse
            rate = getRate(rateType, targetCurrency, sourceCurrency)
            println("rate:"+rate)
            println("baseAmount:"+baseAmount)
            newValue = baseAmount.divide(rate,12,BigDecimal.ROUND_FLOOR)

        } else {
            println("rate:"+rate)
            println("baseAmount:"+baseAmount)
            newValue = baseAmount.multiply(rate)
        }

        return newValue.setScale(2, BigDecimal.ROUND_HALF_UP)
    }


    /****
     *
     * @param rateType
     * @param sourceCurrency
     * @param baseAmount
     * @param targetCurrency
     * @return converted amount
     */

    public convertWithPrecision(String rateType, String sourceCurrency, BigDecimal baseAmount, String targetCurrency, BigDecimal precision) {

        BigDecimal newValue;

        if (sourceCurrency.equalsIgnoreCase(targetCurrency)) {
            return baseAmount
        }

        BigDecimal rate = getRate(rateType, sourceCurrency, targetCurrency)

        if (rate == null) {

            // if no rate is available, try the reverse
            rate = getRate(rateType, targetCurrency, sourceCurrency)
            println("rate:"+rate)
            println("baseAmount:"+baseAmount)
            newValue = baseAmount.divide(rate,12,BigDecimal.ROUND_FLOOR)

        } else {
            newValue = baseAmount.multiply(rate)
        }

        return newValue.setScale(precision.intValue(), BigDecimal.ROUND_UP)
    }
}
