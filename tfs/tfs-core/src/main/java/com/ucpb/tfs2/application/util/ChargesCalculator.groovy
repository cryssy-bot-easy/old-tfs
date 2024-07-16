package com.ucpb.tfs2.application.util

/**
 * User: angulo
 * Date: 3/25/13
 * Time: 4:55 PM
 */

/**
 	 PROLOGUE:
	 (revision)
	 SCR/ER Number:
	 SCR/ER Description: Wrong computation and no accounting entry was generated for Doc Stamp fee.
	 [Revised by:] Lymuel Arrome Saul
	 [Date revised:] 2/5/2016
	 Program [Revision] Details: Corrected the computation of Base Amount of the Doc Stamp fee for EBC Settlement in the function precomputeBaseFXLC()
	 Date deployment: 2/9/2016
	 Member Type: GROOVY
	 Project: CORE
	 Project Name: ChargesCalculator.groovy

*/

/**
 	 PROLOGUE:
	 (revision)
	 SCR/ER Description: To correct the base amount to be used in Doc stamp of EBC with EBP
	 [Revised by:] Jesse James Joson
	 Program [Revision] Details: Corrected the computation of Base Amount of the Doc Stamp fee for EBC Settlement.
	 Date deployment: 6/16/2016 
	 Member Type: GROOVY
	 Project: CORE
	 Project Name: ChargesCalculator.groovy

*/

class ChargesCalculator {

    CurrencyConverter currencyConverter
    Map <String, String> ratesConfig

    String thirdToUsdRateType
    String usdToPhpSingleRateType
    String usdToPhpMixedRateType
    String settlementToDraftUSDRateType

    Boolean mixedPayment

    Map results = new HashMap<String, Object>()


    public void setCurrencyConverter(CurrencyConverter currencyConverter) {
        this.currencyConverter = currencyConverter
    }

    public void     configRatesBasis(String thirdToUsd, String usdToPhpSingle, String usdToPhpMixed, String settlementToDraftUSD) {

        // rate type to use to convert thirds to USD
        this.thirdToUsdRateType = thirdToUsd

        // rate type to use to convert USD to PHP if a single currency is used for payment
        this.usdToPhpSingleRateType = usdToPhpSingle

        // rate type to use to convert USD to PHP if mixed currency is used for payment
        this.usdToPhpMixedRateType = usdToPhpMixed

        // rate type to use to convert settlement currency to the draft currency (currency of amount being paid)
        this.settlementToDraftUSDRateType = settlementToDraftUSD

    }

    protected Object getBaseVariable(String variableName) {

        return results.get(variableName)

    }

    protected void precomputeBase(Map productDetails) {

        BigDecimal usdBase = BigDecimal.ZERO
        BigDecimal phpBase = BigDecimal.ZERO
        BigDecimal phpDMBase = BigDecimal.ZERO

        String productCurrency = productDetails.productCurrency
        BigDecimal productAmount = productDetails.productAmount

        HashSet<String> productSettlementCurrencies = new HashSet<String>()

        String chargesSettlementCurrency = productDetails.chargesSettlementCurrency
        String productSettlementCurrency = ""

        Map settlementPerCurrency = new HashMap<String, BigDecimal>()
        Map settlementPerModeCurrency = new HashMap<String, Object>()

        // total foreign currency settled (in USD)
        BigDecimal fcSettlementTotalInUsd = BigDecimal.ZERO

        // any third currency used to settle product payment
        String productSettlementThirdCurrency = ""

        mixedPayment = false

        // get the totals per currency per settlement mode
        if (productDetails.containsKey("productSettlement")) {

            productDetails.productSettlement.each() { settlement ->

                if (settlement.currency && settlement.amount && settlement.mode) {

                    BigDecimal amount = new BigDecimal(settlement.amount)
                    String currency = settlement.currency
                    String mode = settlement.mode

                    productSettlementCurrencies.add(currency)

                    // total settlement per currency
                    if (!settlementPerCurrency.containsKey(currency)) {
                        settlementPerCurrency.put(currency, BigDecimal.ZERO)
                    }

                    BigDecimal newTotal = amount.add((BigDecimal)settlementPerCurrency.get(currency))
                    settlementPerCurrency.put(currency, newTotal)

                    // total settlement per currency per mode
                    if (!settlementPerModeCurrency[(mode)]) {
                        settlementPerModeCurrency.put(mode, [:])
                    }

                    if (!settlementPerModeCurrency[(mode)][(currency)]) {
                        settlementPerModeCurrency[(mode)][(currency)] = BigDecimal.ZERO
                    }

                    newTotal = amount.add((BigDecimal)settlementPerModeCurrency[(mode)][(currency)])
                    settlementPerModeCurrency[(mode)][(currency)] = newTotal

                    // determine thirds currency
                    if (!settlement.currency.equalsIgnoreCase("USD") && !settlement.currency.equalsIgnoreCase("PHP")) {
                        productSettlementThirdCurrency = currency
                    }
                }
            }

            if (productSettlementCurrencies.size() > 1 && productSettlementCurrencies.contains("PHP")) {
                mixedPayment = true
            }

            if (settlementPerCurrency.containsKey("USD")){
                fcSettlementTotalInUsd = fcSettlementTotalInUsd.add(settlementPerCurrency.get("USD"));
            }

            if (settlementPerCurrency.containsKey(productSettlementThirdCurrency)){
                fcSettlementTotalInUsd = fcSettlementTotalInUsd.add(currencyConverter.convert("REG-SELL", productSettlementThirdCurrency, settlementPerCurrency.get(productSettlementThirdCurrency), "USD"));
            }
        }

        // compute for the base
        if (!productCurrency.equalsIgnoreCase("USD") && !productCurrency.equalsIgnoreCase("PHP")) {

            // convert
            usdBase = currencyConverter.convert(thirdToUsdRateType, productCurrency, productAmount, "USD")
            phpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpDMBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
            println "productCurrency: "+productCurrency
            println "productAmount: "+productAmount
            println "usdBase: "+usdBase
            println "phpBase: "+phpBase
            println "phpDMBase: "+phpDMBase

        } else if(productCurrency.equalsIgnoreCase("USD")) {

            usdBase = productAmount
            phpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpDMBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
            println "phpDMBase:"+phpDMBase

        } else if (productCurrency.equalsIgnoreCase("PHP")) {

            phpBase = productAmount
            phpDMBase = productAmount

        }

        // get payment NOT in TR loan
        BigDecimal totalNotSettledByTRinPHP = BigDecimal.ZERO

        BigDecimal totalNotInTrUSD = BigDecimal.ZERO
        BigDecimal totalNotInTrPHP = BigDecimal.ZERO

        String trCurrency
        BigDecimal totalTrAmount = BigDecimal.ZERO
        BigDecimal totalTrAmountInPHP = BigDecimal.ZERO

        settlementPerModeCurrency.each() { mode, settlements ->

            if(!((String)mode).equalsIgnoreCase("TR")&&!((String)mode).equalsIgnoreCase("TR_LOAN")) {

                settlements.each() { String currency, BigDecimal amount ->

                    if (currency.equalsIgnoreCase("USD")) {
                        totalNotInTrUSD = totalNotInTrUSD.add(amount)
                    } else if (currency.equalsIgnoreCase("PHP")) {
                        totalNotInTrPHP = totalNotInTrPHP.add(amount)
                    } else {
                        totalNotInTrUSD = totalNotInTrUSD.add(currencyConverter.convert(thirdToUsdRateType, currency, amount, "USD"))
                    }
                }

            } else {

                // settlement has TR

                settlements.each() { String currency, BigDecimal amount ->

                    // assumed that this
                    if (currency.equalsIgnoreCase("USD")) {
                        trCurrency = currency
                        totalTrAmount = totalTrAmount.add(amount)
                    } else if (currency.equalsIgnoreCase("PHP")) {
                        trCurrency = currency
                        totalTrAmount = totalTrAmount.add(amount)
                    }
                    // No TR for thirds

                }

            }
        }

        if ("USD".equalsIgnoreCase(trCurrency)) {
            totalTrAmountInPHP = currencyConverter.convert(usdToPhpSingleRateType, trCurrency, totalTrAmount, "PHP")
        } else {
            totalTrAmountInPHP =  totalTrAmount
        }

        totalNotSettledByTRinPHP = totalNotSettledByTRinPHP.add(totalNotInTrPHP)
        totalNotSettledByTRinPHP = totalNotSettledByTRinPHP.add(currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", totalNotInTrUSD, "PHP"))

        results.put("productCurrency", productCurrency)
        results.put("productAmount", productAmount)

        results.put("chargesBaseDMPHP", phpDMBase)

        results.put("chargesBaseUSD", usdBase)
        results.put("chargesBasePHP", phpBase)

        results.put("productSettlementThirdTotals", settlementPerCurrency.get(productSettlementThirdCurrency) ? settlementPerCurrency.get(productSettlementThirdCurrency) : BigDecimal.ZERO )
        results.put("productSettlementUSDTotals", settlementPerCurrency.get("USD") ? settlementPerCurrency.get("USD") : BigDecimal.ZERO)
        results.put("productSettlementPHPTotals", settlementPerCurrency.get("PHP") ? settlementPerCurrency.get("PHP") : BigDecimal.ZERO)

        results.put("settledInForeignInUSD", fcSettlementTotalInUsd)

        results.put("totalNotSettledByTRinPHP", totalNotSettledByTRinPHP)

        results.put("trCurrency", trCurrency)
        results.put("totalTrAmount", totalTrAmount)
        results.put("totalTrAmountInPHP", totalTrAmountInPHP)


        results.each() { key, value ->

            println String.format("%s \t %s", key, value)

        }

        println "--------------------"

        // =========================== calculation specific



    }

    protected void precomputeBaseDMLC(Map productDetails) {

        BigDecimal usdBase = BigDecimal.ZERO
        BigDecimal phpBase = BigDecimal.ZERO
        BigDecimal phpUrrBase = BigDecimal.ZERO
        BigDecimal phpSellRateBase = BigDecimal.ZERO

        String productCurrency = productDetails.productCurrency
        BigDecimal productAmount = productDetails.productAmount

        HashSet<String> productSettlementCurrencies = new HashSet<String>()

        String chargesSettlementCurrency = "PHP"
        String productSettlementCurrency = ""

        Map settlementPerCurrency = new HashMap<String, BigDecimal>()
        Map settlementPerModeCurrency = new HashMap<String, Object>()

        // total foreign currency settled (in USD)
        BigDecimal fcSettlementTotalInUsd = BigDecimal.ZERO

        // any third currency used to settle product payment
        String productSettlementThirdCurrency = ""

        mixedPayment = false

        // get the totals per currency per settlement mode
        if (productDetails.containsKey("productSettlement")) {

            productDetails.productSettlement.each() { settlement ->

                if (settlement.currency && settlement.amount && settlement.mode) {

                    BigDecimal amount = new BigDecimal(settlement.amount)
                    String currency = settlement.currency
                    String mode = settlement.mode

                    productSettlementCurrencies.add(currency)

                    // total settlement per currency
                    if (!settlementPerCurrency.containsKey(currency)) {
                        settlementPerCurrency.put(currency, BigDecimal.ZERO)
                    }

                    BigDecimal newTotal = amount.add((BigDecimal)settlementPerCurrency.get(currency))
                    settlementPerCurrency.put(currency, newTotal)

                    // total settlement per currency per mode
                    if (!settlementPerModeCurrency[(mode)]) {
                        settlementPerModeCurrency.put(mode, [:])
                    }

                    if (!settlementPerModeCurrency[(mode)][(currency)]) {
                        settlementPerModeCurrency[(mode)][(currency)] = BigDecimal.ZERO
                    }

                    newTotal = amount.add((BigDecimal)settlementPerModeCurrency[(mode)][(currency)])
                    settlementPerModeCurrency[(mode)][(currency)] = newTotal

                    // determine thirds currency
                    if (!settlement.currency.equalsIgnoreCase("USD") && !settlement.currency.equalsIgnoreCase("PHP")) {
                        productSettlementThirdCurrency = currency
                    }
                }
            }

            if (productSettlementCurrencies.size() > 1 && productSettlementCurrencies.contains("PHP")) {
                mixedPayment = true
            }

            if (settlementPerCurrency.containsKey("USD")){
                fcSettlementTotalInUsd = fcSettlementTotalInUsd.add(settlementPerCurrency.get("USD"));
            }

            if (settlementPerCurrency.containsKey(productSettlementThirdCurrency)){
                fcSettlementTotalInUsd = fcSettlementTotalInUsd.add(currencyConverter.convert("REG-SELL", productSettlementThirdCurrency, settlementPerCurrency.get(productSettlementThirdCurrency), "USD"));
            }
        }

        // compute for the base
            if (!productCurrency.equalsIgnoreCase("USD") && !productCurrency.equalsIgnoreCase("PHP")) {

            // convert
            usdBase = currencyConverter.convert(thirdToUsdRateType, productCurrency, productAmount, "USD")
            phpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", usdBase, "PHP")


        } else if(productCurrency.equalsIgnoreCase("USD")) {

            usdBase = productAmount
            phpBase = currencyConverter.convert(usdToPhpMixedRateType , "USD", usdBase, "PHP")
            phpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", usdBase, "PHP")

        } else if (productCurrency.equalsIgnoreCase("PHP")) {

            phpBase = productAmount
            phpUrrBase = productAmount
            phpSellRateBase = productAmount
        }

        // get payment NOT in TR loan
        BigDecimal totalNotSettledByTRinPHP = BigDecimal.ZERO

        BigDecimal totalNotInTrUSD = BigDecimal.ZERO
        BigDecimal totalNotInTrPHP = BigDecimal.ZERO

        String trCurrency
        BigDecimal totalTrAmount = BigDecimal.ZERO
        BigDecimal totalTrAmountInPHP = BigDecimal.ZERO

        settlementPerModeCurrency.each() { mode, settlements ->

            if(!((String)mode).equalsIgnoreCase("TR_LOAN")) {

                settlements.each() { String currency, BigDecimal amount ->

                    if (currency.equalsIgnoreCase("USD")) {
                        totalNotInTrUSD = totalNotInTrUSD.add(amount)
                    } else if (currency.equalsIgnoreCase("PHP")) {
                        totalNotInTrPHP = totalNotInTrPHP.add(amount)
                    } else {
                        totalNotInTrUSD = totalNotInTrUSD.add(currencyConverter.convert(thirdToUsdRateType, currency, amount, "USD"))
                    }
                }

            } else {

                // settlement has TR

                settlements.each() { String currency, BigDecimal amount ->

                    // assumed that this
                    if (currency.equalsIgnoreCase("USD")) {
                        trCurrency = currency
                        totalTrAmount = totalTrAmount.add(amount)
                    } else if (currency.equalsIgnoreCase("PHP")) {
                        trCurrency = currency
                        totalTrAmount = totalTrAmount.add(amount)
                    }
                    // No TR for thirds

                }

            }
        }

        if ("USD".equalsIgnoreCase(trCurrency)) {
            totalTrAmountInPHP = currencyConverter.convert(usdToPhpSingleRateType, trCurrency, totalTrAmount, "PHP")
        } else {
            totalTrAmountInPHP =  totalTrAmount
        }

        totalNotSettledByTRinPHP = totalNotSettledByTRinPHP.add(totalNotInTrPHP)
        totalNotSettledByTRinPHP = totalNotSettledByTRinPHP.add(currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", totalNotInTrUSD, "PHP"))

        results.put("productCurrency", productCurrency)
        results.put("productAmount", productAmount)

        results.put("chargesBaseUrrPHP", phpUrrBase)
        results.put("chargesBaseSellRatePHP", phpSellRateBase)

        results.put("chargesBaseUSD", usdBase)
        results.put("chargesBasePHP", phpBase)

        results.put("productSettlementThirdTotals", settlementPerCurrency.get(productSettlementThirdCurrency) ? settlementPerCurrency.get(productSettlementThirdCurrency) : BigDecimal.ZERO )
        results.put("productSettlementUSDTotals", settlementPerCurrency.get("USD") ? settlementPerCurrency.get("USD") : BigDecimal.ZERO)
        results.put("productSettlementPHPTotals", settlementPerCurrency.get("PHP") ? settlementPerCurrency.get("PHP") : BigDecimal.ZERO)

        results.put("settledInForeignInUSD", fcSettlementTotalInUsd)

        results.put("totalNotSettledByTRinPHP", totalNotSettledByTRinPHP)

        results.put("trCurrency", trCurrency)
        results.put("totalTrAmount", totalTrAmount)
        results.put("totalTrAmountInPHP", totalTrAmountInPHP)


        results.each() { key, value ->

            println String.format("%s \t %s", key, value)

        }

        println "--------------------"

        // =========================== calculation specific

    }

    protected void precomputeBaseFXLC(Map productDetails) {

        BigDecimal usdBase = BigDecimal.ZERO
        BigDecimal phpBase = BigDecimal.ZERO
        BigDecimal phpUrrBase = BigDecimal.ZERO
        BigDecimal phpSellRateBase = BigDecimal.ZERO

		//only for EBC Settlement DocStamp Charges
		BigDecimal ebcDocstampUsdBase = BigDecimal.ZERO
        BigDecimal ebcDocstampPhpBase = BigDecimal.ZERO
        BigDecimal ebcDocstampPhpUrrBase = BigDecimal.ZERO
        BigDecimal ebcDocstampPhpSellRateBase = BigDecimal.ZERO


        String productCurrency = productDetails.productCurrency
        BigDecimal productAmount = productDetails.productAmount

        HashSet<String> productSettlementCurrencies = new HashSet<String>()

        String chargesSettlementCurrency = productDetails.chargesSettlementCurrency
        String productSettlementCurrency = ""

        Map settlementPerCurrency = new HashMap<String, BigDecimal>()
        Map settlementPerModeCurrency = new HashMap<String, Object>()

        // total foreign currency settled (in USD)
        BigDecimal fcSettlementTotalInUsd = BigDecimal.ZERO

        // any third currency used to settle product payment
        String productSettlementThirdCurrency = ""

        mixedPayment = false

        // get the totals per currency per settlement mode
        if (productDetails.containsKey("productSettlement")) {

            productDetails.productSettlement.each() { settlement ->

                if (settlement.currency && settlement.amount && settlement.mode) {
                    println "settlement.amount:"+settlement.amount
                    BigDecimal amount = new BigDecimal(settlement.amount)
                    String currency = settlement.currency
                    String mode = settlement.mode

                    productSettlementCurrencies.add(currency)

                    // total settlement per currency
                    if (!settlementPerCurrency.containsKey(currency)) {
                        settlementPerCurrency.put(currency, BigDecimal.ZERO)
                    }

                    BigDecimal newTotal = amount.add((BigDecimal)settlementPerCurrency.get(currency))
                    settlementPerCurrency.put(currency, newTotal)

                    // total settlement per currency per mode
                    if (!settlementPerModeCurrency[(mode)]) {
                        settlementPerModeCurrency.put(mode, [:])
                    }

                    if (!settlementPerModeCurrency[(mode)][(currency)]) {
                        settlementPerModeCurrency[(mode)][(currency)] = BigDecimal.ZERO
                    }

                    newTotal = amount.add((BigDecimal)settlementPerModeCurrency[(mode)][(currency)])
                    settlementPerModeCurrency[(mode)][(currency)] = newTotal

                    // determine thirds currency
                    if (!settlement.currency.equalsIgnoreCase("USD") && !settlement.currency.equalsIgnoreCase("PHP")) {
                        productSettlementThirdCurrency = currency
                    }
                }
            }

            if (productSettlementCurrencies.size() > 1 && productSettlementCurrencies.contains("PHP")) {
                mixedPayment = true
            }

            if (settlementPerCurrency.containsKey("USD")){
                fcSettlementTotalInUsd = fcSettlementTotalInUsd.add(settlementPerCurrency.get("USD"));
            }

            println "productSettlementThirdCurrency:"+productSettlementThirdCurrency
            if (settlementPerCurrency.containsKey(productSettlementThirdCurrency)){
                println "currencyConverter.rates:"+currencyConverter.rates
                fcSettlementTotalInUsd = fcSettlementTotalInUsd.add(currencyConverter.convert("REG-SELL", productSettlementThirdCurrency, settlementPerCurrency.get(productSettlementThirdCurrency), "USD"));
                println "fcSettlementTotalInUsd:"+fcSettlementTotalInUsd
            }
        }
		println "Currency Converter productCurrency!!!!!!!!:"+ productCurrency
		println "Currency Converter productAmount!!!!!!!!!!:"+ productAmount

        // compute for the base
        if (!productCurrency.equalsIgnoreCase("USD") && !productCurrency.equalsIgnoreCase("PHP")) {

            println "Currency Converter productCurrency:"+ productCurrency
            println "Currency Converter productAmount:"+ productAmount
            // convert
            usdBase = currencyConverter.convert(thirdToUsdRateType, productCurrency, productAmount, "USD")
            phpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", usdBase, "PHP")
			
			//only for EBC Settlement DocStamp Charges
			if (productDetails.containsKey("ebcNegotiationAmount")  && productDetails.containsKey("bpAmount") &&
				!productDetails.bpAmount.toString().equalsIgnoreCase("")) {
				BigDecimal ebcNegotiationAmount = productDetails.ebcNegotiationAmount				
				BigDecimal bpAmount = productDetails.bpAmount
				BigDecimal ebcLessEbp = ebcNegotiationAmount.subtract(bpAmount)
				println "THIRD: With EBP - Docstamp should base on: " + ebcLessEbp
				ebcDocstampUsdBase = currencyConverter.convert(thirdToUsdRateType, productCurrency, ebcLessEbp, "USD")
				ebcDocstampPhpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", ebcDocstampUsdBase, "PHP")
				ebcDocstampPhpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", ebcDocstampUsdBase, "PHP")
				ebcDocstampPhpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", ebcDocstampUsdBase, "PHP")
			} else if (productDetails.containsKey("ebcNegotiationAmount")) {
				BigDecimal ebcNegotiationAmount = productDetails.ebcNegotiationAmount				
				println "THIRD: Withoout EBP - Docstamp should base on: " + ebcNegotiationAmount
				ebcDocstampUsdBase = currencyConverter.convert(thirdToUsdRateType, productCurrency, ebcNegotiationAmount, "USD")
				ebcDocstampPhpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", ebcDocstampUsdBase, "PHP")
				ebcDocstampPhpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", ebcDocstampUsdBase, "PHP")
				ebcDocstampPhpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", ebcDocstampUsdBase, "PHP")
			
			}


        } else if(productCurrency.equalsIgnoreCase("USD")) {

            usdBase = productAmount
            phpBase = currencyConverter.convert(usdToPhpMixedRateType , "USD", usdBase, "PHP")
            phpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", usdBase, "PHP")

			//only for EBC Settlement DocStamp Charges
			if (productDetails.containsKey("ebcNegotiationAmount") && productDetails.containsKey("bpAmount") &&
				!productDetails.bpAmount.toString().equalsIgnoreCase("")) {
				BigDecimal ebcNegotiationAmount = productDetails.ebcNegotiationAmount			
				BigDecimal bpAmount = productDetails.bpAmount
				BigDecimal ebcLessEbp = ebcNegotiationAmount.subtract(bpAmount)
				
				println "USD: With EBP - Docstamp should base on: " + ebcLessEbp
				ebcDocstampUsdBase = ebcLessEbp
	            ebcDocstampPhpBase = currencyConverter.convert(usdToPhpMixedRateType , "USD", ebcDocstampUsdBase, "PHP")
	            ebcDocstampPhpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", ebcDocstampUsdBase, "PHP")
	            ebcDocstampPhpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", ebcDocstampUsdBase, "PHP")
			} else if (productDetails.containsKey("ebcNegotiationAmount")) {
				BigDecimal ebcNegotiationAmount = productDetails.ebcNegotiationAmount
				println "USD: Without EBP - Docstamp should base on: " + ebcNegotiationAmount
				ebcDocstampUsdBase = currencyConverter.convert(thirdToUsdRateType, productCurrency, ebcNegotiationAmount, "USD")
				ebcDocstampPhpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", ebcDocstampUsdBase, "PHP")
				ebcDocstampPhpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", ebcDocstampUsdBase, "PHP")
				ebcDocstampPhpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", ebcDocstampUsdBase, "PHP")
			
			}

        } else if (productCurrency.equalsIgnoreCase("PHP")) {

            phpBase = productAmount
            phpUrrBase = productAmount
            phpSellRateBase = productAmount
			usdBase = currencyConverter.convert("URR", productCurrency, productAmount, "USD") //comment by robin

			//only for EBC Settlement DocStamp Charges
			if (productDetails.containsKey("ebcNegotiationAmount") && productDetails.containsKey("bpAmount") &&
				!productDetails.bpAmount.toString().equalsIgnoreCase("")) {
			
				BigDecimal ebcNegotiationAmount = productDetails.ebcNegotiationAmount
				BigDecimal bpAmount = productDetails.bpAmount
				BigDecimal ebcLessEbp = ebcNegotiationAmount.subtract(bpAmount)
				
				println "PHP: With EBP - Docstamp should base on: " + ebcLessEbp
				ebcDocstampPhpBase = ebcLessEbp
				ebcDocstampPhpUrrBase = ebcLessEbp
				ebcDocstampPhpSellRateBase = ebcLessEbp
			} else if (productDetails.containsKey("ebcNegotiationAmount")) {
				BigDecimal ebcNegotiationAmount = productDetails.ebcNegotiationAmount
				println "PHP: Without EBP - Docstamp should base on: " + ebcNegotiationAmount
				ebcDocstampPhpBase = ebcNegotiationAmount
				ebcDocstampPhpUrrBase =ebcNegotiationAmount
				ebcDocstampPhpSellRateBase = ebcNegotiationAmount
			}
        }
		println "phpSellRateBase" + phpSellRateBase
        // get payment NOT in TR loan
        BigDecimal totalNotSettledByTRinPHP = BigDecimal.ZERO

        BigDecimal totalNotInTrUSD = BigDecimal.ZERO
        BigDecimal totalNotInTrPHP = BigDecimal.ZERO

        String trCurrency
        BigDecimal totalTrAmount = BigDecimal.ZERO
        BigDecimal totalTrAmountInPHP = BigDecimal.ZERO

        settlementPerModeCurrency.each() { mode, settlements ->

            if(!((String)mode).equalsIgnoreCase("TR")&&!((String)mode).equalsIgnoreCase("TR_LOAN")&&!((String)mode).equalsIgnoreCase("DTR_LOAN")) {

                settlements.each() { String currency, BigDecimal amount ->

                    if (currency.equalsIgnoreCase("USD")) {
                        totalNotInTrUSD = totalNotInTrUSD.add(amount)
                    } else if (currency.equalsIgnoreCase("PHP")) {
                        totalNotInTrPHP = totalNotInTrPHP.add(amount)
                    } else {
                        totalNotInTrUSD = totalNotInTrUSD.add(currencyConverter.convert(thirdToUsdRateType, currency, amount, "USD"))
                    }
                }

            } else {

                // settlement has TR

                settlements.each() { String currency, BigDecimal amount ->

                    // assumed that this
                    if (currency.equalsIgnoreCase("USD")) {
                        trCurrency = currency
                        totalTrAmount = totalTrAmount.add(amount)
                    } else if (currency.equalsIgnoreCase("PHP")) {
                        trCurrency = currency
                        totalTrAmount = totalTrAmount.add(amount)
                    }
                    // No TR for thirds

                }

            }
        }

        if ("USD".equalsIgnoreCase(trCurrency)) {
            totalTrAmountInPHP = currencyConverter.convert(usdToPhpSingleRateType, trCurrency, totalTrAmount, "PHP")
        } else {
            totalTrAmountInPHP =  totalTrAmount
        }

        totalNotSettledByTRinPHP = totalNotSettledByTRinPHP.add(totalNotInTrPHP)
        totalNotSettledByTRinPHP = totalNotSettledByTRinPHP.add(currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", totalNotInTrUSD, "PHP"))

        results.put("productCurrency", productCurrency)
        results.put("productAmount", productAmount)
        results.put("chargesSettlementCurrency", chargesSettlementCurrency)

        results.put("chargesBaseUrrPHP", phpUrrBase)
        results.put("chargesBaseSellRatePHP", phpSellRateBase)


        results.put("chargesBaseUSD", usdBase)
        results.put("chargesBasePHP", phpBase)

        results.put("productSettlementThirdTotals", settlementPerCurrency.get(productSettlementThirdCurrency) ? settlementPerCurrency.get(productSettlementThirdCurrency) : BigDecimal.ZERO )
        results.put("productSettlementUSDTotals", settlementPerCurrency.get("USD") ? settlementPerCurrency.get("USD") : BigDecimal.ZERO)
        results.put("productSettlementPHPTotals", settlementPerCurrency.get("PHP") ? settlementPerCurrency.get("PHP") : BigDecimal.ZERO)

        results.put("settledInForeignInUSD", fcSettlementTotalInUsd)

        results.put("totalNotSettledByTRinPHP", totalNotSettledByTRinPHP)

        results.put("trCurrency", trCurrency)
        results.put("totalTrAmount", totalTrAmount)
        results.put("totalTrAmountInPHP", totalTrAmountInPHP)

		//only for EBC Settlement DocStamp Charges
		results.put("ebcDocstampBaseUrrPHP", ebcDocstampPhpUrrBase)
		results.put("ebcDocstampBaseSellRatePHP", ebcDocstampPhpSellRateBase)
		results.put("ebcDocstampBaseUSD", ebcDocstampUsdBase)
		results.put("ebcDocstampBasePHP", ebcDocstampPhpBase)



        results.each() { key, value ->

            println String.format("%s \t %s", key, value)

        }

        println "--------------------"

        // =========================== calculation specific

    }

    protected void precomputeBaseDMBPBC(Map productDetails) {
        println "productDetailsprecomputeBaseDMBPBC:"+productDetails

        BigDecimal usdBase = BigDecimal.ZERO
        BigDecimal phpBase = BigDecimal.ZERO
        BigDecimal phpUrrBase = BigDecimal.ZERO
        BigDecimal phpSellRateBase = BigDecimal.ZERO

        String productCurrency = productDetails.productCurrency
        BigDecimal productAmount = productDetails.productAmount

        HashSet<String> productSettlementCurrencies = new HashSet<String>()

        String chargesSettlementCurrency = "PHP"
        String productSettlementCurrency = ""

        Map settlementPerCurrency = new HashMap<String, BigDecimal>()
        Map settlementPerModeCurrency = new HashMap<String, Object>()

        // total foreign currency settled (in USD)
        BigDecimal fcSettlementTotalInUsd = BigDecimal.ZERO

        // any third currency used to settle product payment
        String productSettlementThirdCurrency = ""

        mixedPayment = false

        // get the totals per currency per settlement mode
        if (productDetails.containsKey("productSettlement")) {

            productDetails.productSettlement.each() { settlement ->

                if (settlement.currency && settlement.amount && settlement.mode) {

                    BigDecimal amount = new BigDecimal(settlement.amount)
                    String currency = settlement.currency
                    String mode = settlement.mode

                    productSettlementCurrencies.add(currency)

                    // total settlement per currency
                    if (!settlementPerCurrency.containsKey(currency)) {
                        settlementPerCurrency.put(currency, BigDecimal.ZERO)
                    }

                    BigDecimal newTotal = amount.add((BigDecimal)settlementPerCurrency.get(currency))
                    settlementPerCurrency.put(currency, newTotal)

                    // total settlement per currency per mode
                    if (!settlementPerModeCurrency[(mode)]) {
                        settlementPerModeCurrency.put(mode, [:])
                    }

                    if (!settlementPerModeCurrency[(mode)][(currency)]) {
                        settlementPerModeCurrency[(mode)][(currency)] = BigDecimal.ZERO
                    }

                    newTotal = amount.add((BigDecimal)settlementPerModeCurrency[(mode)][(currency)])
                    settlementPerModeCurrency[(mode)][(currency)] = newTotal

                    // determine thirds currency
                    if (!settlement.currency.equalsIgnoreCase("USD") && !settlement.currency.equalsIgnoreCase("PHP")) {
                        productSettlementThirdCurrency = currency
                    }
                }
            }

            if (productSettlementCurrencies.size() > 1 && productSettlementCurrencies.contains("PHP")) {
                mixedPayment = true
            }

            if (settlementPerCurrency.containsKey("USD")){
                fcSettlementTotalInUsd = fcSettlementTotalInUsd.add(settlementPerCurrency.get("USD"));
            }

            if (settlementPerCurrency.containsKey(productSettlementThirdCurrency)){
                fcSettlementTotalInUsd = fcSettlementTotalInUsd.add(currencyConverter.convert("REG-SELL", productSettlementThirdCurrency, settlementPerCurrency.get(productSettlementThirdCurrency), "USD"));
            }
        }

        // compute for the base
        if (!productCurrency.equalsIgnoreCase("USD") && !productCurrency.equalsIgnoreCase("PHP")) {

            // convert
            usdBase = currencyConverter.convert(thirdToUsdRateType, productCurrency, productAmount, "USD")
            phpBase = currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", usdBase, "PHP")


        } else if(productCurrency.equalsIgnoreCase("USD")) {

            usdBase = productAmount
            phpBase = currencyConverter.convert(usdToPhpMixedRateType , "USD", usdBase, "PHP")
            phpUrrBase = currencyConverter.convert(usdToPhpSingleRateType, "USD", usdBase, "PHP")
            phpSellRateBase = currencyConverter.convert(usdToPhpMixedRateType, "USD", usdBase, "PHP")

        } else if (productCurrency.equalsIgnoreCase("PHP")) {

            phpBase = productAmount
            phpUrrBase = productAmount
            phpSellRateBase = productAmount
        }

        // get payment NOT in TR loan
        BigDecimal totalNotSettledByTRinPHP = BigDecimal.ZERO

        BigDecimal totalNotInTrUSD = BigDecimal.ZERO
        BigDecimal totalNotInTrPHP = BigDecimal.ZERO

        String trCurrency
        BigDecimal totalTrAmount = BigDecimal.ZERO
        BigDecimal totalTrAmountInPHP = BigDecimal.ZERO

        settlementPerModeCurrency.each() { mode, settlements ->

            if(!((String)mode).equalsIgnoreCase("TR_LOAN")) {

                settlements.each() { String currency, BigDecimal amount ->

                    if (currency.equalsIgnoreCase("USD")) {
                        totalNotInTrUSD = totalNotInTrUSD.add(amount)
                    } else if (currency.equalsIgnoreCase("PHP")) {
                        totalNotInTrPHP = totalNotInTrPHP.add(amount)
                    } else {
                        totalNotInTrUSD = totalNotInTrUSD.add(currencyConverter.convert(thirdToUsdRateType, currency, amount, "USD"))
                    }
                }

            } else {

                // settlement has TR

                settlements.each() { String currency, BigDecimal amount ->

                    // assumed that this
                    if (currency.equalsIgnoreCase("USD")) {
                        trCurrency = currency
                        totalTrAmount = totalTrAmount.add(amount)
                    } else if (currency.equalsIgnoreCase("PHP")) {
                        trCurrency = currency
                        totalTrAmount = totalTrAmount.add(amount)
                    }
                    // No TR for thirds

                }

            }
        }

        if ("USD".equalsIgnoreCase(trCurrency)) {
            totalTrAmountInPHP = currencyConverter.convert(usdToPhpSingleRateType, trCurrency, totalTrAmount, "PHP")
        } else {
            totalTrAmountInPHP =  totalTrAmount
        }

        totalNotSettledByTRinPHP = totalNotSettledByTRinPHP.add(totalNotInTrPHP)
        totalNotSettledByTRinPHP = totalNotSettledByTRinPHP.add(currencyConverter.convert(mixedPayment ? usdToPhpMixedRateType : usdToPhpSingleRateType, "USD", totalNotInTrUSD, "PHP"))


        BigDecimal negotiationAmount = new BigDecimal(productDetails?.extendedProperties?.negotiationAmount?:"0")
        BigDecimal convertedDbpAmount = new BigDecimal(productDetails?.extendedProperties?.convertedDbpAmount?:"0")
        BigDecimal proceedsAmountInProductCurrency = new BigDecimal(productDetails?.extendedProperties?.proceedsAmount?:"0")
        BigDecimal proceedsAmountInSettlementCurrency = new BigDecimal(productDetails?.extendedProperties?.proceedsAmountInSettlementCurrency?:"0")
        BigDecimal actualCorresCharges = negotiationAmount - proceedsAmountInProductCurrency
        BigDecimal remainingDbcBillableAmount = negotiationAmount - convertedDbpAmount

        String proceedsAmountSettlementCurrency = productDetails?.extendedProperties?.proceedsAmountSettlementCurrency?:""


        results.put("negotiationAmount", negotiationAmount)
        results.put("convertedDbpAmount", convertedDbpAmount)
        results.put("proceedsAmount", proceedsAmountInProductCurrency)
        results.put("proceedsAmountInSettlementCurrency", proceedsAmountInSettlementCurrency)


        results.put("actualCorresCharges", actualCorresCharges)
        results.put("remainingDbcBillableAmount", remainingDbcBillableAmount)
        results.put("proceedsAmountSettlementCurrency", proceedsAmountSettlementCurrency)



        results.put("productCurrency", productCurrency)
        results.put("productAmount", productAmount)

        results.put("chargesBaseUrrPHP", phpUrrBase)
        results.put("chargesBaseSellRatePHP", phpSellRateBase)

        results.put("chargesBaseUSD", usdBase)
        results.put("chargesBasePHP", phpBase)

        results.put("productSettlementThirdTotals", settlementPerCurrency.get(productSettlementThirdCurrency) ? settlementPerCurrency.get(productSettlementThirdCurrency) : BigDecimal.ZERO )
        results.put("productSettlementUSDTotals", settlementPerCurrency.get("USD") ? settlementPerCurrency.get("USD") : BigDecimal.ZERO)
        results.put("productSettlementPHPTotals", settlementPerCurrency.get("PHP") ? settlementPerCurrency.get("PHP") : BigDecimal.ZERO)

        results.put("settledInForeignInUSD", fcSettlementTotalInUsd)

        results.put("totalNotSettledByTRinPHP", totalNotSettledByTRinPHP)

        results.put("trCurrency", trCurrency)
        results.put("totalTrAmount", totalTrAmount)
        results.put("totalTrAmountInPHP", totalTrAmountInPHP)


        results.each() { key, value ->

            println String.format("%s \t %s", key, value)

        }

        println "--------------------"

        // =========================== calculation specific

    }

    protected static Object getExtendedPropertiesVariable (Map productDetails, String variableName, String type){
        Map extendedProperties = (Map)productDetails.get("extendedProperties")
        Object retVal =  extendedProperties.get(variableName)

        if (retVal == null){
            return null
        } else if (retVal.getClass().toString().equalsIgnoreCase(type)){
            return retVal
        } else if ("BigDecimal".equalsIgnoreCase(type)) {
            return new BigDecimal(retVal)
        } else if ("String".equalsIgnoreCase(type)){
            return retVal.toString()
        } else {
            return retVal
        }
    }

    protected static Object getExtendedPropertiesVariableMapAlreadyExtracted (Map extendedProperties, String variableName, String type){
        Object retVal =  extendedProperties.get(variableName)

        if (retVal == null){
            return null
        } else if (retVal.getClass().toString().equalsIgnoreCase(type)){
            return retVal
        } else if ("BigDecimal".equalsIgnoreCase(type)) {
            return new BigDecimal(retVal)
        } else if ("String".equalsIgnoreCase(type)){
            return retVal.toString()
        } else {
            return retVal
        }
    }

    protected static Object convertToProperClass (Object obj, String type){
        if (obj == null){
            return null
        } else if (obj.getClass().toString().equalsIgnoreCase(type)){
            return obj
        } else if ("BigDecimal".equalsIgnoreCase(type)) {
            return new BigDecimal(obj)
        } else if ("String".equalsIgnoreCase(type)){
            return obj.toString()
        } else {
            println "retVal type is" +obj.getClass().toString()
            return obj
        }
    }

    Map extractExtendedProperties(String extendedPropertiesString) {

        String tobesplit = extendedPropertiesString.substring(1,extendedPropertiesString.size()-1)
        Map returnMap =[:]
        tobesplit.split(",").each { sbstr ->
            if (sbstr.length()>3){
            def nameAndValue = sbstr.split(":")
                try{
                if( nameAndValue.size()==2 && nameAndValue[1]!=null){
                    returnMap.put(nameAndValue[0].replace(" ",""),nameAndValue[1]?:"")
                }
                } catch (Exception e){

                }
            }
        }
        return returnMap


    }

}
