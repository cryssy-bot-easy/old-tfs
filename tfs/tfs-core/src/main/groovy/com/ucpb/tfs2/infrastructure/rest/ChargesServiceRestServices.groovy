package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.ucpb.tfs.application.bootstrap.ChargesLookup
import com.ucpb.tfs.application.service.ChargesService
import com.ucpb.tfs.domain.payment.PaymentRepository
import com.ucpb.tfs.domain.reference.GltsSequenceRepository
import com.ucpb.tfs.domain.service.ChargesParameter
import com.ucpb.tfs.domain.service.ChargesParameterRepository
import com.ucpb.tfs.domain.service.ServiceChargeRepository
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.domain.service.TradeServiceId
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1
import com.ucpb.tfs2.application.service.TradeServiceService
import com.ucpb.tfs2.application.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/**
 * PROLOGUE
 * SCR/ER Description: Revision on charges computation of Export transactions
 *	[Revised by:] Jesse James Joson
 *	Program [Revision] Details: To set the correct amount where to base the charges computation.
 *	Date deployment: 6/16/2016 
 *	Member Type: groovy
 *	Project: CORE
 *	Project Name: ChargesServiceRestServices.groovy 
*/
/**
 *  SCR/ER Description:
 *  Revised by: Cedrick C. Nungay
 *  Revision Details: Added retrieval of charges parameters as parameter
 *        on the computation that uses document stamps.
 *  Date revised: 02/01/2018
 *  Member Type: groovy
*/
/**
 *  SCR/ER Description:
 *  Revised by: Cedrick C. Nungay
 *  Revision Details: Remove unnecessary productAmount parameter on getDMOpeningCharge method.
 *  Date revised: 03/20/2018
 *  Member Type: groovy
 */
/**
 *  SCR/ER Description:
 *  Revised by: Cedrick C. Nungay
 *  Revision Details: Returned productAmount parameter on getDMOpeningCharge method.
 *  Date revised: 04/13/2018
 *  Member Type: groovy
 */

@Path("/charges")
@Component
class ChargesServiceRestServices {
	
	/*	 PROLOGUE:
		 (revision)
		 SCR/ER Number: 20151104-021
		 SCR/ER Description: Wrong computation of charges on the FXLC Charges.
		 [Revised by:] Gerard De Las Armas
		 [Date revised:] 07/22/2015
		 Program [Revision] Details: Changed the value of variable amount from outstandingBalance to amountFrom in function(s) getFXAmendmentCharge()
		 							 and getDMAmendmentCharge().
		 Date deployment:
		 Member Type: GROOVY
		 Project: CORE
		 Project Name: ChargesServiceRestServices.groovy
	 */
	
	/**
		 (revision)
		 SCR/ER Number:
		 SCR/ER Description: Wrong computation and no accounting entry was generated for Doc Stamp fee.
		 [Revised by:] Lymuel Arrome Saul
		 [Date revised:] 2/5/2016
		 Program [Revision] Details: Added EBC Negotiation Amount in the parameters to be passed on ExportBillsCollectionChargesCalculator.groovy
		 							 in the function getExportBillsCollectionSettlementCharge()
		 Date deployment: 2/9/2016
		 Member Type: GROOVY
		 Project: CORE
		 Project Name: ChargesServiceRestServices.groovy

	*/

    @Autowired
    TradeServiceService tradeServiceService

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ChargesService chargesService;

    @Autowired
    ChargesLookup chargesLookup;

    @Autowired
    ServiceChargeRepository serviceChargeRepository

    @Autowired
    GltsSequenceRepository gltsSequenceRepository

    @Autowired
    ChargesParameterRepository chargesParameterRepository

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDMNONLCSettlementCharge")
    public Response getDMNONLCSettlementCharge(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDMNONLCSettlementCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            if (tradeService != null) {

                HashMap<String, Object> details = tradeService.getDetails();

                String documentClass = tradeService.getDocumentClass()
                String documentType = tradeService.getDocumentType()
                String documentSubType1 = tradeService.getDocumentSubType1()
                String documentSubType2 = tradeService.getDocumentSubType2()
                String serviceType = tradeService.getServiceType()
                String amount = (String) details.get("amount")
                String issueDate = (String) details.get("issueDate")  // As of 5/31/2013
                String expiryDate = (String) details.get("expiryDate")
                String usancePeriod = (String) details.get("usancePeriod")

                Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
                extendedPropertiesMap.put("documentClass", documentClass)
                extendedPropertiesMap.put("documentType", documentType)
                extendedPropertiesMap.put("documentSubType1", documentSubType1)
                extendedPropertiesMap.put("documentSubType2", documentSubType2)
                extendedPropertiesMap.put("serviceType", serviceType)
                // extendedPropertiesMap.put("etsDate", etsDate)
                extendedPropertiesMap.put("issueDate", issueDate)
                extendedPropertiesMap.put("expiryDate", expiryDate)
                extendedPropertiesMap.put("usancePeriod", usancePeriod)

                println "extendedPropertiesMap" + extendedPropertiesMap
                println "productAmount:" + amount
                println "productCurrency:" + jsonParams.get("productCurrency").toString()

                Map productDetails = [
                        productCurrency: jsonParams.get("productCurrency"),
                        productAmount: new BigDecimal(amount),
                        chargeSettlementCurrency: "PHP",//jsonParams.get("settlementCurrency"),
                        productSettlement: buildPaymentModeMap(tradeServiceId),
                        extendedProperties: extendedPropertiesMap,
                        chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
                ]

                NonLCChargesCalculator calculator = new NonLCChargesCalculator();

                calculator.setCurrencyConverter(currencyConverter)
                calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

                def temp = calculator.computeDM(productDetails)
                println temp

                returnMap.put("result", temp);
                returnMap.put("status", "ok");

            } else {

                throw new Exception("(Graceful exit) TradeService is null. Probably TradeService is not yet created.");
            }

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXNONLCSettlementCharge")
    public Response getFXNONLCSettlementCharge(@Context UriInfo allUri) {
        println "getFXNONLCSettlementCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String issueDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'issueDate')
            String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
            String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
            String advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'advanceCorresChargesFlag')
            String confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'confirmationInstructionsFlag')
            String amount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'amount')



            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("issueDate", issueDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)
            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + amount
            println "productCurrency:" + jsonParams.get("productCurrency").toString()
            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: jsonParams.get("productCurrency"),
                    productAmount: new BigDecimal(amount),
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
					chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            NonLCChargesCalculator calculator = new NonLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeFX(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDMOpeningCharge")
    public Response getDMOpeningCharge(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDMOpeningCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            if (tradeService != null) {

                HashMap<String, Object> details = tradeService.getDetails();

                String documentClass = tradeService.getDocumentClass()
                String documentType = tradeService.getDocumentType()
                String documentSubType1 = tradeService.getDocumentSubType1()
                String documentSubType2 = tradeService.getDocumentSubType2()

                // String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
                // String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
                // String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
                // String etsDate = (String)details.get("etsDate")
                // String amount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'amount')
                String amount = (String) details.get("amount")
                String issueDate = (String) details.get("issueDate")  // As of 5/31/2013
                String expiryDate = (String) details.get("expiryDate")
                String usancePeriod = (String) details.get("usancePeriod")

                Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
                extendedPropertiesMap.put("documentClass", documentClass)
                extendedPropertiesMap.put("documentType", documentType)
                extendedPropertiesMap.put("documentSubType1", documentSubType1)
                extendedPropertiesMap.put("documentSubType2", documentSubType2)
                // extendedPropertiesMap.put("etsDate", etsDate)
                extendedPropertiesMap.put("issueDate", issueDate)
                extendedPropertiesMap.put("expiryDate", expiryDate)
                extendedPropertiesMap.put("usancePeriod", usancePeriod)

                println "extendedPropertiesMap" + extendedPropertiesMap
                println "productAmount:" + amount
                println "productCurrency:" + jsonParams.get("productCurrency").toString()

                Map productDetails = [
                        productCurrency: jsonParams.get("productCurrency"),
                        productAmount: new BigDecimal(amount),
                        chargeSettlementCurrency: "PHP",//jsonParams.get("settlementCurrency"),
                        productSettlement: buildPaymentModeMap(tradeServiceId),
                        extendedProperties: extendedPropertiesMap
                ]

                DMLCChargesCalculator calculator = new DMLCChargesCalculator();

                calculator.setCurrencyConverter(currencyConverter)
                calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

                def temp = calculator.computeOpening(productDetails)
                println temp

                returnMap.put("result", temp);
                returnMap.put("status", "ok");

            } else {

                throw new Exception("(Graceful exit) TradeService is null. Probably TradeService is not yet created.");
            }

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDMNegotiationCharge")
    public Response getDMNegotiationCharge(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDMNegotiationCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)
            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)

            String cableFeeFlag = tradeServiceService.getCableFlagFromSettlement(tradeServiceId)
            String remittanceFlag = tradeServiceService.getRemittanceFlagFromSettlement(tradeServiceId)
            String negotiationAmount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'negotiationAmount')

            extendedPropertiesMap.put("cableFeeFlag", cableFeeFlag)
            extendedPropertiesMap.put("remittanceFlag", remittanceFlag)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)

            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + negotiationAmount
            println "productCurrency:" + jsonParams.get("productCurrency").toString()

            Map productDetails = [
                    productCurrency: jsonParams.get("productCurrency"),
                    productAmount: new BigDecimal(negotiationAmount),
                    chargeSettlementCurrency: "PHP",
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            DMLCChargesCalculator calculator = new DMLCChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeNegotiation(productDetails)
            println temp
            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDMAmendmentCharge")
    public Response getDMAmendmentCharge(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDMAmendmentCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            if (tradeService != null) {

                Map<String, Object> details = tradeService.getDetails()

                String documentClass = tradeService.getDocumentClass()
                String documentType = tradeService.getDocumentType()
                String documentSubType1 = tradeService.getDocumentSubType1()
                String documentSubType2 = tradeService.getDocumentSubType2()

                // String etsDate = (String) details.get("etsDate")
                String amendmentDate = (String) details.get("amendmentDate")  // As of 5/31/2013
                String expiryDate = (String) details.get("expiryDate")
                String usancePeriod = (String) details.get("usancePeriod")

                String amountSwitch = (String) details.get("amountSwitch")
                String lcAmountFlagDisplay = (String) details.get("lcAmountFlagDisplay")
                String lcAmountFlag = (String) details.get("lcAmountFlag")
                String amount = (String) details.get("amountFrom") //-->will use outstandingBalance instead of amount
                String amountTo = (String) details.get("amountTo")
                String expiryDateSwitch = (String) details.get("expiryDateSwitch")
                String expiryDateFlagDisplay = (String) details.get("expiryDateFlagDisplay")
                String originalExpiryDate = (String) details.get("originalExpiryDate")
                String expiryDateTo = (String) details.get("expiryDateTo")
                String tenorSwitch = (String) details.get("tenorSwitch")
                String originalTenor = (String) details.get("originalTenor")
                String tenorTo = (String) details.get("tenorTo")
                String usancePeriodTo = (String) details.get("usancePeriodTo")
                String narrativeSwitch = (String) details.get("narrativeSwitch")
                String narrative = (String) details.get("narrative")

                Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)

                extendedPropertiesMap.put("documentClass", documentClass)
                extendedPropertiesMap.put("documentType", documentType)
                extendedPropertiesMap.put("documentSubType1", documentSubType1)
                extendedPropertiesMap.put("documentSubType2", documentSubType2)

                // extendedPropertiesMap.put("etsDate", etsDate)
                extendedPropertiesMap.put("amendmentDate", amendmentDate)
                extendedPropertiesMap.put("expiryDate", expiryDate)
                extendedPropertiesMap.put("usancePeriod", usancePeriod)

                extendedPropertiesMap.put("amountSwitch", amountSwitch)
                extendedPropertiesMap.put("lcAmountFlagDisplay", lcAmountFlagDisplay)
                extendedPropertiesMap.put("lcAmountFlag", lcAmountFlag)

                println "amountSwitch:" + amountSwitch
                println "lcAmountFlagDisplay:" + lcAmountFlagDisplay
                println "lcAmountFlag:" + lcAmountFlag

                extendedPropertiesMap.put("amount", amount)
                extendedPropertiesMap.put("amountTo", amountTo)
                extendedPropertiesMap.put("expiryDateSwitch", expiryDateSwitch)
                extendedPropertiesMap.put("expiryDateFlagDisplay", expiryDateFlagDisplay)
                extendedPropertiesMap.put("originalExpiryDate", originalExpiryDate)
                extendedPropertiesMap.put("expiryDateTo", expiryDateTo)
                extendedPropertiesMap.put("tenorSwitch", tenorSwitch)
                extendedPropertiesMap.put("originalTenor", originalTenor)
                extendedPropertiesMap.put("tenorTo", tenorTo)
                extendedPropertiesMap.put("usancePeriodTo", usancePeriodTo)
                extendedPropertiesMap.put("narrativeSwitch", narrativeSwitch)
                extendedPropertiesMap.put("narrative", narrative.replace(':',''))

                println "extendedPropertiesMap" + extendedPropertiesMap
                println "productAmount:" + amount //jsonParams.get("productAmount").toString()
                println "productCurrency:" + jsonParams.get("productCurrency").toString()

                Map productDetails = [
                        productCurrency: jsonParams.get("productCurrency"),
                        productAmount: new BigDecimal(amount),//new BigDecimal(jsonParams.get("productAmount").toString()),
                        chargeSettlementCurrency: "PHP", //THIS IS DM ONLY PHP
                        productSettlement: buildPaymentModeMap(tradeServiceId),
                        extendedProperties: extendedPropertiesMap
                ]

                DMLCChargesCalculator calculator = new DMLCChargesCalculator();

                calculator.setCurrencyConverter(currencyConverter)
                calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

                def temp = calculator.computeAmendment(productDetails)

                println "###### temp = ${temp}"

                returnMap.put("result", temp);
                returnMap.put("status", "ok");

            } else {

                throw new Exception("(Graceful exit) TradeService is null. Probably TradeService is not yet created.");
            }

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDUALoanMaturityAdjustmentCharge")
    public Response getDUALoanMaturityAdjustmentCharge(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDUALoanMaturityAdjustmentCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            if (tradeService != null) {

                Map<String, Object> details = tradeService.getDetails()

                String documentClass = tradeService.getDocumentClass()
                String documentType = tradeService.getDocumentType()
                String documentSubType1 = tradeService.getDocumentSubType1()
                String documentSubType2 = tradeService.getDocumentSubType2()
                // String etsDate = (String) details.get("etsDate")
                String negotiationValueDate = (String) details.get("negotiationValueDate")  // As of 5/31/2013
                String expiryDate = (String) details.get("expiryDate")
                String usancePeriod = (String) details.get("usancePeriod")

                String loanMaturityDateFrom = (String) details.get("loanMaturityDateFrom")
                String loanMaturityDateTo = (String) details.get("loanMaturityDateTo")

                String productAmount = (String) details.get("amount")
                String productCurrency = (String) details.get("currency")

                Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
                extendedPropertiesMap.put("documentClass", documentClass)
                extendedPropertiesMap.put("documentType", documentType)
                extendedPropertiesMap.put("documentSubType1", documentSubType1)
                extendedPropertiesMap.put("documentSubType2", documentSubType2)
                // extendedPropertiesMap.put("etsDate", etsDate)
                extendedPropertiesMap.put("negotiationValueDate", negotiationValueDate)
                extendedPropertiesMap.put("expiryDate", expiryDate)
                extendedPropertiesMap.put("usancePeriod", usancePeriod)

                extendedPropertiesMap.put("loanMaturityDateFrom", loanMaturityDateFrom)
                extendedPropertiesMap.put("loanMaturityDateTo", loanMaturityDateTo)

                println "extendedPropertiesMap " + extendedPropertiesMap
                println "productAmount: " + productAmount
                println "productCurrency: " + productCurrency

                Map productDetails = [
                        productCurrency: productCurrency,
                        productAmount: new BigDecimal(productAmount.toString()),
                        chargeSettlementCurrency: "PHP",
                        productSettlement: buildPaymentModeMap(tradeServiceId),
                        extendedProperties: extendedPropertiesMap
                ]

                DMLCChargesCalculator calculator = new DMLCChargesCalculator();

                calculator.setCurrencyConverter(currencyConverter)
                calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

                def temp = calculator.computeUaLoanMaturityAdjustment(productDetails)

                println temp

                returnMap.put("result", temp);
                returnMap.put("status", "ok");

            } else {

                throw new Exception("(Graceful exit) TradeService is null. Probably TradeService is not yet created.");
            }

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDUALoanSettlementCharge")
    public Response getDUALoanSettlementCharge(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDUALoanSettlementCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()
            String cableFeeFlag = tradeServiceService.getCableFlagFromSettlement(tradeServiceId)
            String remittanceFlag = tradeServiceService.getRemittanceFlagFromSettlement(tradeServiceId)
            String productAmount = (String) details.get("amount")
            String productCurrency = (String) details.get("currency")

            extendedPropertiesMap.put("cableFeeFlag", cableFeeFlag)
            extendedPropertiesMap.put("remittanceFlag", remittanceFlag)
            println "extendedPropertiesMap " + extendedPropertiesMap
            println "productAmount: " + productAmount
            println "productCurrency: " + productCurrency

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)

            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: new BigDecimal(productAmount.toString()),
                    chargeSettlementCurrency: "PHP",
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            DMLCChargesCalculator calculator = new DMLCChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            def temp = calculator.computeUaLoanSettlement(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXOpeningCharge")
    public Response getFXOpeningCharge(@Context UriInfo allUri) {
        println "getFXOpeningCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String issueDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'issueDate')
            String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
            String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
            String advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'advanceCorresChargesFlag')
            String confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'confirmationInstructionsFlag')
            String amount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'amount')


            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("issueDate", issueDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)


            def temp01 = jsonParams.get("recompute")
            String recompute = ""
            if(temp01!=null){
                recompute=temp01.toString()
            }



            if(recompute.equalsIgnoreCase("Y")){

                jsonParams.get("cilexPercentage")?extendedPropertiesMap.put("cilexPercentage", jsonParams.get("cilexPercentage")?.toString()):""
                jsonParams.get("cilexDenominator")?extendedPropertiesMap.put("cilexDenominator", jsonParams.get("cilexDenominator")?.toString()):""
                jsonParams.get("cilexNumerator")?extendedPropertiesMap.put("cilexNumerator", jsonParams.get("cilexNumerator")?.toString()):""

                jsonParams.get("confirmingFeePercentage")?extendedPropertiesMap.put("confirmingFeePercentage", jsonParams.get("confirmingFeePercentage")?.toString()):""
                jsonParams.get("confirmingFeeDenominator")?extendedPropertiesMap.put("confirmingFeeDenominator", jsonParams.get("confirmingFeeDenominator")?.toString()):""
                jsonParams.get("confirmingFeeNumerator")?extendedPropertiesMap.put("confirmingFeeNumerator", jsonParams.get("confirmingFeeNumerator")?.toString()):""

                jsonParams.get("commitmentFeePercentage")?extendedPropertiesMap.put("commitmentFeePercentage", jsonParams.get("commitmentFeePercentage")?.toString()):""
                jsonParams.get("commitmentFeeDenominator")?extendedPropertiesMap.put("commitmentFeeDenominator", jsonParams.get("commitmentFeeDenominator")?.toString()):""
                jsonParams.get("commitmentFeeNumerator")?extendedPropertiesMap.put("commitmentFeeNumerator", jsonParams.get("commitmentFeeNumerator")?.toString()):""

                jsonParams.get("bankCommissionPercentage")?extendedPropertiesMap.put("bankCommissionPercentage", jsonParams.get("bankCommissionPercentage")?.toString()):""
                jsonParams.get("bankCommissionDenominator")?extendedPropertiesMap.put("bankCommissionDenominator", jsonParams.get("bankCommissionDenominator")?.toString()):""
                jsonParams.get("bankCommissionNumerator")?extendedPropertiesMap.put("bankCommissionNumerator", jsonParams.get("bankCommissionNumerator")?.toString()):""

            }


            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + amount
            println "productCurrency:" + jsonParams.get("productCurrency").toString()
            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: jsonParams.get("productCurrency"),
                    productAmount: new BigDecimal(amount),
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeOpening(productDetails)
            println  'dayyyyyyyyyyyyyyyyyyyyy' + calculator
            println  'jussssssssskooooooooooooooooo' + temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXNegotiationCharge")
    public Response getFXNegotiationCharge(@Context UriInfo allUri) {
        println "getFXNegotiationCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            String etsDate = (String) details.get('etsDate')
            //String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String expiryDate = (String) details.get('expiryDate')
            String usancePeriod = (String) details.get('usancePeriod')
            String cwtFlag = (String) details.get('cwtFlag') ?: "N"
            String cwtPercentage = (String) details.get('cwtPercentage') ?: "0.98"
            String negotiationAmount = (String) details.get('negotiationAmount')
            String currency = (String) details.get('negotiationCurrency')
            String overdrawnAmount = (String) details.get('overdrawnAmount')
            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("centavos", jsonParams.get("documentaryStampsCentavos").toString())
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("cwtFlag", cwtFlag)
            extendedPropertiesMap.put("cwtPercentage", cwtPercentage)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("negotiationAmount", negotiationAmount)
            extendedPropertiesMap.put("overdrawnAmount", overdrawnAmount)
            println "extendedPropertiesMap" + extendedPropertiesMap
            //println "productAmount:"+jsonParams.get("productAmount").toString()
            println "productCurrency:" + jsonParams.get("productCurrency").toString()
            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            println "cwtFlag:" + jsonParams.get("cwtFlag").toString()
            println "cwtPercentage:" + jsonParams.get("cwtPercentage").toString()
            Map productDetails = [
                    productCurrency: currency,
                    productAmount: new BigDecimal(negotiationAmount),
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeNegotiation(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXAmendmentCharge")
    public Response getFXAmendmentCharge(@Context UriInfo allUri) {
        println "getFXAmendmentCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()
            String issueDate = (String) details.get("issueDate")
            String etsDate = (String) details.get("etsDate")
            String amendmentDate = (String) details.get("amendmentDate")
            String expiryDate = (String) details.get("expiryDate")
            String usancePeriod = (String) details.get("usancePeriod")

            String amountSwitch = (String) details.get("amountSwitch")
            String lcAmountFlagDisplay = (String) details.get("lcAmountFlagDisplay")
            String outstandingBalance = (String) details.get("outstandingBalance") //-->will use outstandingBalance
            String amount = (String) details.get("amountFrom") //-->will use outstandingBalance
            String amountTo = (String) details.get("amountTo") ?: '0'
            String expiryDateSwitch = (String) details.get("expiryDateSwitch")
            String expiryDateFlagDisplay = (String) details.get("expiryDateFlagDisplay")
            String originalExpiryDate = (String) details.get("originalExpiryDate")
            String expiryDateTo = (String) details.get("expiryDateTo")
            String tenorSwitch = (String) details.get("tenorSwitch")
            String originalTenor = (String) details.get("originalTenor")
            String tenorTo = (String) details.get("tenorTo")
            String usancePeriodTo = (String) details.get("usancePeriodTo")
            if(!usancePeriodTo){
                usancePeriodTo = "0"
            }
            println "angol angol usancePeriodTo:"+usancePeriodTo
            String narrativeSwitch = (String) details.get("narrativeSwitch")
            String narrative = (String) details.get("narrative")
            String confirmationInstructionsFlag = (String) details.get("confirmationInstructionsFlagSwitch")
            String originalConfirmationInstructionsFlag = (String) details.get("originalConfirmationInstructionsFlag")
            String confirmationInstructionsFlagTo = (String) details.get("confirmationInstructionsFlagTo")
//            String confirmationInstructionsFlag = (String)details.get("confirmationInstructionsFlag")
            String advanceCorresChargesFlag = (String) details.get("advanceCorresChargesFlag")

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("amendmentDate", amendmentDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)

            extendedPropertiesMap.put("amountSwitch", amountSwitch)
            extendedPropertiesMap.put("lcAmountFlagDisplay", lcAmountFlagDisplay)
            extendedPropertiesMap.put("lcAmountFlag", lcAmountFlagDisplay)
            extendedPropertiesMap.put("outstandingBalance", outstandingBalance)
            extendedPropertiesMap.put("amount", amount)
            extendedPropertiesMap.put("amountTo", amountTo)
            extendedPropertiesMap.put("expiryDateSwitch", expiryDateSwitch)
            extendedPropertiesMap.put("expiryDateCheck", expiryDateSwitch)
            extendedPropertiesMap.put("expiryDateFlagDisplay", expiryDateFlagDisplay)
            extendedPropertiesMap.put("expiryDateFlag", expiryDateFlagDisplay)
            extendedPropertiesMap.put("originalExpiryDate", originalExpiryDate)
            extendedPropertiesMap.put("expiryDateTo", expiryDateTo)
            extendedPropertiesMap.put("tenorSwitch", tenorSwitch)
            extendedPropertiesMap.put("originalTenor", originalTenor)
            extendedPropertiesMap.put("tenorTo", tenorTo)
            extendedPropertiesMap.put("usancePeriodTo", usancePeriodTo)
            extendedPropertiesMap.put("narrativeSwitch", narrativeSwitch)
            extendedPropertiesMap.put("narrative", narrative)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlagTo", confirmationInstructionsFlagTo)
            extendedPropertiesMap.put("originalConfirmationInstructionsFlag", originalConfirmationInstructionsFlag)
            extendedPropertiesMap.put("issueDate", issueDate)
            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + jsonParams.get("productAmount").toString()
            println "productCurrency:" + jsonParams.get("productCurrency").toString()
            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: jsonParams.get("productCurrency"),
                    productAmount: new BigDecimal(amount),
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeAmendment(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXAdjustmentCharge")
    public Response getFXAdjustmentCharge(@Context UriInfo allUri) {
        println "getFXAdjustmentCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
            String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
            String advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'advanceCorresChargesFlag')
            String confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'confirmationInstructionsFlag')
            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)
            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + jsonParams.get("productAmount").toString()
            println "productCurrency:" + jsonParams.get("productCurrency").toString()
            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: jsonParams.get("productCurrency"),
                    productAmount: new BigDecimal(jsonParams.get("productAmount").toString()),
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeAdjustment(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXUALoanMaturityAdjustmentCharge")
    public Response getFXUALoanMaturityAdjustmentCharge(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getFXUALoanMaturityAdjustmentCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)
            println "jsonParams:" + jsonParams
            String chargesSettlementCurrencyParam = jsonParams.get("")

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String etsDate = (String) details.get("etsDate")
            String expiryDate = (String) details.get("expiryDate")
            String usancePeriod = (String) details.get("usancePeriod")

            String loanMaturityDateFrom = (String) details.get("loanMaturityDateFrom")
            String loanMaturityDateTo = (String) details.get("loanMaturityDateTo")

            String productAmount = (String) details.get("amount")
            String productCurrency = (String) details.get("currency")

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)

            extendedPropertiesMap.put("loanMaturityDateFrom", loanMaturityDateFrom)
            extendedPropertiesMap.put("loanMaturityDateTo", loanMaturityDateTo)

            println "extendedPropertiesMap " + extendedPropertiesMap
            println "productAmount: " + productAmount
            println "productCurrency: " + productCurrency
            println "jsonParams.get(\"settlementCurrency\"): " + jsonParams.get("settlementCurrency")

            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: new BigDecimal(productAmount.toString()),
                    chargeSettlementCurrency: jsonParams.get("settlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            def temp = calculator.computeUaLoanMaturityAdjustment(productDetails)

            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXUALoanSettlementCharge")
    public Response getFXUALoanSettlementCharge(@Context UriInfo allUri) {
        println "getFXUALoanSettlementCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
            String cwtFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'cwtFlag')
            String cwtPercentage = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'cwtPercentage')
            String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
            String advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'advanceCorresChargesFlag')
            String confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'confirmationInstructionsFlag')
            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)
            extendedPropertiesMap.put("cwtFlag", cwtFlag)
            extendedPropertiesMap.put("cwtPercentage", cwtPercentage)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)

            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + jsonParams.get("productAmount").toString()
            println "productCurrency:" + jsonParams.get("productCurrency").toString()
            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: jsonParams.get("productCurrency"),
                    productAmount: new BigDecimal(jsonParams.get("productAmount").toString()),
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeUaLoanSettlement(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXIndemnityIssuanceCharge")
    public Response getFXIndemnityIssuanceCharge(@Context UriInfo allUri) {
        println "getFXIndemnityIssuanceCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)
            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()
            String cwtFlag = (String) details.get('cwtFlag') ?: "N"
            String cwtPercentage = (String) details.get('cwtPercentage') ?: "0.98"
            String indemnityType = (String) details.get('indemnityType') ?: "BE"

			String documentClass = tradeService.getDocumentClass()
			String documentType = tradeService.getDocumentType()
			String documentSubType1 = tradeService.getDocumentSubType1()
			String documentSubType2 = tradeService.getDocumentSubType2()
			String serviceType = tradeService.getServiceType()

			Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
			extendedPropertiesMap.put("documentClass", documentClass)
			extendedPropertiesMap.put("documentType", documentType)
			extendedPropertiesMap.put("documentSubType1", documentSubType1)
			extendedPropertiesMap.put("documentSubType2", documentSubType2)
			extendedPropertiesMap.put("serviceType", serviceType)

            extendedPropertiesMap.put("indemnityType", indemnityType)
            extendedPropertiesMap.put("cwtPercentage", cwtPercentage)
            extendedPropertiesMap.put("cwtFlag", cwtFlag)
            println "extendedPropertiesMap" + extendedPropertiesMap

            println "productAmount:" + jsonParams.get("productAmount").toString()
            println "productCurrency:" + jsonParams.get("productCurrency").toString()
            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: "PHP",
                    productAmount: BigDecimal.ZERO,
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeIndemnityIssuance(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXIndemnityCancellationCharge")
    public Response getFXIndemnityCancellationCharge(@Context UriInfo allUri) {
        println "getFXIndemnityCancellationCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)


            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + jsonParams.get("productAmount").toString()
            println "productCurrency:" + jsonParams.get("productCurrency").toString()
            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: "PHP",
                    productAmount: BigDecimal.ZERO,
                    chargeSettlementCurrency: "PHP",
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeIndemnityCancellation(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getImportAdvancePaymentCharge")
    public Response getImportAdvancePaymentCharge(@Context UriInfo allUri) {

        println "getImportAdvancePaymentCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()
            String cwtFlag = (String) details.get('cwtFlag') ?: "N"
            String cwtPercentage = (String) details.get('cwtPercentage') ?: "0.98"
            String productCurrency = (String) details.get('currency')
            String productAmountString = (String) details.get('amount')
            BigDecimal productAmount = new BigDecimal(productAmountString.replace(',',''))

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("cwtFlag",cwtFlag)
            extendedPropertiesMap.put("cwtPercentage",cwtPercentage)


            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: productAmount,
                    chargeSettlementCurrency: jsonParams.get("settlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap]

            ImportAdvanceChargesCalculator calculator = new ImportAdvanceChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Payment!
            def temp = calculator.computeAdvancePayment(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getImportAdvanceRefundCharge")
    public Response getImportAdvanceRefundCharge(@Context UriInfo allUri) {

        println "getImportAdvanceRefundCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            Map productDetails = [:]

            ImportAdvanceChargesCalculator calculator = new ImportAdvanceChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Refund!
            def temp = calculator.computeAdvanceRefund(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getExportAdvanceRefundCharge")
	public Response getExportAdvanceRefundCharge(@Context UriInfo allUri) {

		println "getExportAdvanceRefundCharge"
		Gson gson = new Gson();

		
        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()
            String cwtFlag = (String) details.get('cwtFlag') ?: "N"
            String cwtPercentage = (String) details.get('cwtPercentage') ?: "0.98"
            String productCurrency = (String) details.get('currency')
            String productAmountString = (String) details.get('amount')
            BigDecimal productAmount = new BigDecimal(productAmountString.replace(',',''))

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("cwtFlag",cwtFlag)
            extendedPropertiesMap.put("cwtPercentage",cwtPercentage)


            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: productAmount,
                    chargeSettlementCurrency: jsonParams.get("settlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap]

            ImportAdvanceChargesCalculator calculator = new ImportAdvanceChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

			// Compute for Refund!
			def temp = calculator.computeExportAdvanceRefund(productDetails)
			println temp

			returnMap.put("result", temp);
			returnMap.put("status", "ok");

		} catch (Exception e) {

			Map errorDetails = new HashMap();

			e.printStackTrace();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getExportsAdvisingCharge")
    public Response getExportsAdvisingCharge(@Context UriInfo allUri) {

        println "***************************** getExportsAdvisingCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            String documentClass = tradeService.getDocumentClass()
            String documentSubType1 = tradeService.getDocumentSubType1()
			String serviceType = tradeService?.getServiceType();
            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
			extendedPropertiesMap.put("serviceType",serviceType)
            println "***************************** documentClass = ${documentClass}"
            println "***************************** documentSubType1 = ${documentSubType1}"

            Map<String, Object> details = tradeService.getDetails()
            if (details.get("cwtFlag") != null) {
                println "cwtFlag:"+details.get("cwtFlag")
                extendedPropertiesMap.put("cwtFlag", details.get("cwtFlag"))
            }
            if (details.get("cwtPercentage") != null) {
                extendedPropertiesMap.put("cwtPercentage", details.get("cwtPercentage"))
            }

            if (documentSubType1 != null && documentSubType1.equals(DocumentSubType1.FIRST_ADVISING.toString())) {

                extendedPropertiesMap.put("sendMt730Flag", details.get("sendMt730Flag"))
                extendedPropertiesMap.put("sendMt799Flag", details.get("sendMt799Flag"))

            } else if (documentSubType1 != null && documentSubType1.equals(DocumentSubType1.SECOND_ADVISING.toString())) {

                extendedPropertiesMap.put("totalBankCharges", details.get("totalBankCharges"))
            }

            println "extendedPropertiesMap = " + extendedPropertiesMap

            Map productDetails = [
                    extendedProperties: extendedPropertiesMap
            ]

            ExportsAdvisingChargesCalculator calculator = new ExportsAdvisingChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            def temp
            if (documentSubType1 != null && documentSubType1.equals(DocumentSubType1.FIRST_ADVISING.toString())) {
                temp = calculator.getFirstAdvisingCharge(productDetails)
            } else if (documentSubType1 != null && documentSubType1.equals(DocumentSubType1.SECOND_ADVISING.toString())) {
                temp = calculator.getSecondAdvisingCharge(productDetails)
            }
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getExportBillsCollectionSettlementCharge")
    public Response getExportBillsCollectionSettlementCharge(@Context UriInfo allUri) {

        println "getExportBillsCollectionSettlementCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }
			println "***************************** documentClass = ${jsonParams}"
            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()
            String remittanceFlag = tradeServiceService.getRemittanceFlagFromSettlement(tradeServiceId)

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("remittanceFlag", remittanceFlag)
            println "***************************** documentClass = ${documentClass}"
            println "***************************** documentSubType1 = ${documentSubType1}"

            Map<String, Object> details = tradeService.getDetails()
            if (details.get("cwtFlag") != null) {
                extendedPropertiesMap.put("cwtFlag", details.get("cwtFlag"))
            }
            if (details.get("cwtPercentage") != null) {
                extendedPropertiesMap.put("cwtPercentage", details.get("cwtPercentage"))
            }


            String productCurrency = details.get("currency")

            BigDecimal productAmount = BigDecimal.ZERO
            if (details.containsKey("amountForCredit") && ! "".equalsIgnoreCase(details.get("amountForCredit").toString())) {
                productAmount = new BigDecimal(details.get("amountForCredit").toString())
            }
			
			BigDecimal ebcNegotiationAmount = BigDecimal.ZERO
			if (details.containsKey("amount") && ! "".equalsIgnoreCase(details.get("amount").toString())) {
				ebcNegotiationAmount = new BigDecimal(details.get("amount").toString())
			}
			BigDecimal bpAmount = BigDecimal.ZERO
			if (details.containsKey("bpAmount") && !"".equalsIgnoreCase(details.get("bpAmount").toString())) {
				bpAmount = new BigDecimal(details.get("bpAmount").toString())
			}

            List paymentModeMapList = buildPaymentModeMap(tradeServiceId)
            if(paymentModeMapList.isEmpty()){
                paymentModeMapList = buildPaymentModeMapSettlement(tradeServiceId)
            }

            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: productAmount,
					ebcNegotiationAmount: ebcNegotiationAmount,
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: paymentModeMapList,
                    extendedProperties: extendedPropertiesMap,
                    bpAmount: bpAmount,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]
			
			println "productDetails: " + productDetails

            ExportBillsCollectionChargesCalculator calculator = new ExportBillsCollectionChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Negotiation!
            def temp = calculator.computeSettlement(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getExportBillsPaymentNegotiationCharge")
    public Response getExportBillsPaymentNegotiationCharge(@Context UriInfo allUri) {

        println "getExportBillsPaymentNegotiationCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()
            String remittanceFlag = tradeServiceService.getRemittanceFlagFromSettlement(tradeServiceId)

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)
            println "***************************** documentClass = ${documentClass}"
            println "***************************** documentSubType1 = ${documentSubType1}"
            extendedPropertiesMap.put("remittanceFlag", remittanceFlag)

            Map<String, Object> details = tradeService.getDetails()
            if (details.get("cwtFlag") != null) {
                extendedPropertiesMap.put("cwtFlag", details.get("cwtFlag"))
            }
            if (details.get("cwtPercentage") != null) {
                extendedPropertiesMap.put("cwtPercentage", details.get("cwtPercentage"))
            }
			if (details.get("newProceedsCurrency") != null){
				extendedPropertiesMap.put("settlementToBeneCurrency", details.get("newProceedsCurrency"))
			}
            String productCurrency = details.get("currency")

            BigDecimal productAmount = BigDecimal.ZERO
            BigDecimal advanceInterest = BigDecimal.ZERO
            if (details.containsKey("amount") && ! "".equalsIgnoreCase(details.get("amount").toString())) {
                productAmount = new BigDecimal(details.get("amount").toString().replace(",",""))
            }

//            if (details.containsKey("advanceInterest") && ! "".equalsIgnoreCase(details.get("advanceInterest").toString())) {
//                advanceInterest = new BigDecimal(details.get("advanceInterest").toString().replace(",",""))
//                productAmount = productAmount.add(advanceInterest)
//            }

            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: productAmount,
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            ExportBillsPaymentChargesCalculator calculator = new ExportBillsPaymentChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Negotiation!
            def temp = calculator.computeNegotiation(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getExportBillsPaymentSettlementCharge")
    public Response getExportBillsPaymentSettlementCharge(@Context UriInfo allUri) {

        println "getExportBillsPaymentSettlementCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            String documentClass = tradeService.getDocumentClass()
            String documentSubType1 = tradeService.getDocumentSubType1()

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            println "***************************** documentClass = ${documentClass}"
            println "***************************** documentSubType1 = ${documentSubType1}"

            Map<String, Object> details = tradeService.getDetails()
            if (details.get("cwtFlag") != null) {
                extendedPropertiesMap.put("cwtFlag", details.get("cwtFlag"))
            }
            if (details.get("cwtPercentage") != null) {
                extendedPropertiesMap.put("cwtPercentage", details.get("cwtPercentage"))
            }

            Map productDetails = [
                    extendedProperties: extendedPropertiesMap
            ]

            ExportBillsPaymentChargesCalculator calculator = new ExportBillsPaymentChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Settlement!
            def temp = calculator.computeSettlement(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getExportBillsCollectionCancellationCharge")
    public Response getExportBillsCollectionCancellationCharge(@Context UriInfo allUri) {

        println "getExportBillsCollectionCancellationCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            String documentClass = tradeService.getDocumentClass()
            String documentSubType1 = tradeService.getDocumentSubType1()

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            println "***************************** documentClass = ${documentClass}"
            println "***************************** documentSubType1 = ${documentSubType1}"

            Map<String, Object> details = tradeService.getDetails()
            if (details.get("cwtFlag") != null) {
                extendedPropertiesMap.put("cwtFlag", details.get("cwtFlag"))
            }
            if (details.get("cwtPercentage") != null) {
                extendedPropertiesMap.put("cwtPercentage", details.get("cwtPercentage"))
            }
            String productCurrency = details.get("currency")

            if (details.containsKey("proceedsAmount") && ! "".equalsIgnoreCase(details.get("proceedsAmount").toString()))
            BigDecimal productAmount = new BigDecimal(details.get("proceedsAmount").toString())

            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: productAmount,
                    chargeSettlementCurrency: jsonParams.get("settlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap
            ]

            ExportBillsCollectionChargesCalculator calculator = new ExportBillsCollectionChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Cancellation!
            def temp = calculator.computeCancellation(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDomesticBillsCollectionSettlementCharge")//TODO
    public Response getDomesticBillsCollectionSettlementCharge(@Context UriInfo allUri) {

        println "getDomesticBillsCollectionSettlementCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            String documentClass = tradeService.getDocumentClass()
            String documentSubType1 = tradeService.getDocumentSubType1()

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            println "***************************** documentClass = ${documentClass}"
            println "***************************** documentSubType1 = ${documentSubType1}"

            Map<String, Object> details = tradeService.getDetails()
            if (details.get("cwtFlag") != null) {
                extendedPropertiesMap.put("cwtFlag", details.get("cwtFlag"))
            }
            if (details.get("cwtPercentage") != null) {
                extendedPropertiesMap.put("cwtPercentage", details.get("cwtPercentage"))
            }

            String productCurrency = details.get("currency")
            println "angolproductCurrencyproductCurrency:"+productCurrency

            println "angolsettlementCurrencysettlementCurrency:"+jsonParams.get("chargeSettlementCurrency")

            BigDecimal productAmount = BigDecimal.ZERO
            if (details.containsKey("proceedsAmount") && ! "".equalsIgnoreCase(details.get("proceedsAmount").toString())) {
                productAmount = new BigDecimal(details.get("proceedsAmount").toString())
            }

            BigDecimal dbcBillableAmount = BigDecimal.ZERO
            if (details.containsKey("amount") && ! "".equalsIgnoreCase(details.get("amount").toString())) {
                dbcBillableAmount = new BigDecimal(details.get("amount").toString())

                if(dbcBillableAmount){
                    extendedPropertiesMap.put("dbcBillableAmount", dbcBillableAmount)
                }
            }

            if (details.get("newProceedsCurrency") != null) {
                extendedPropertiesMap.put("newProceedsCurrency", details.get("newProceedsCurrency"))
            }


            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: productAmount,
                    chargeSettlementCurrency: jsonParams.get("chargeSettlementCurrency"),
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap
            ]

            DBCChargesCalculator calculator = new DBCChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Settlement!
            def temp = calculator.computeSettlement(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDomesticBillsPaymentNegotiationCharge")//TODO
    public Response getDomesticBillsPaymentNegotiationCharge(@Context UriInfo allUri) {

        println "getDomesticBillsPaymentNegotiationCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
            println "key:"+key+"|"+mpAllQueParams.getFirst(key).toString()
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String productCurrency = ""
            String chargeSettlementCurrency = jsonParams.get("chargeSettlementCurrency")
            println "chargeSettlementCurrency:"+chargeSettlementCurrency
            BigDecimal productAmount = BigDecimal.ZERO

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            println "***************************** documentClass = ${documentClass}"
            println "***************************** documentSubType1 = ${documentSubType1}"

            Map<String, Object> details = tradeService.getDetails()
            if (details.get("currency") != null) {
                println "currency currency angol 0000100:" + details.get("currency")
                productCurrency = details.get("currency")
            }
            println "amount amount angol 0000100:" + details.get("amount")
            if (details.get("amount") != null) {
                println "amount amount angol 0000100:" + details.get("amount")
                productAmount = new BigDecimal(details.get("amount"))
            } else {
                productAmount = BigDecimal.ZERO
            }
            if (details.get("cwtFlag") != null) {
                extendedPropertiesMap.put("cwtFlag", details.get("cwtFlag"))
            }
            if (details.get("cwtPercentage") != null) {
                extendedPropertiesMap.put("cwtPercentage", details.get("cwtPercentage"))
            }

            if (details.get("exportViaPddtsFlag") != null) {
                if (details.get("exportViaPddtsFlag").toString().equalsIgnoreCase("1")) {
                    extendedPropertiesMap.put("remittanceFlag", "Y")
                } else {
                    extendedPropertiesMap.put("remittanceFlag", "N")
                }

            } else {
                extendedPropertiesMap.put("remittanceFlag", "N")
            }
            println "productAmount:"+productAmount
            println "productCurrency:"+productCurrency

            Map productDetails = [
                    productAmount: productAmount,
                    productCurrency: productCurrency,
                    chargeSettlementCurrency: chargeSettlementCurrency,
                    extendedProperties: extendedPropertiesMap
            ]

            DBPChargesCalculator calculator = new DBPChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Negotiation!
            def temp = calculator.computeNegotiation(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDomesticBillsPaymentSettlementCharge")
    public Response getDomesticBillsPaymentSettlementCharge(@Context UriInfo allUri) {

        println "getDomesticBillsPaymentSettlementCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParams(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            String documentClass = tradeService.getDocumentClass()
            String documentSubType1 = tradeService.getDocumentSubType1()

            Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            println "***************************** documentClass = ${documentClass}"
            println "***************************** documentSubType1 = ${documentSubType1}"

            Map<String, Object> details = tradeService.getDetails()
            if (details.get("cwtFlag") != null) {
                extendedPropertiesMap.put("cwtFlag", details.get("cwtFlag"))
            }
            if (details.get("cwtPercentage") != null) {
                extendedPropertiesMap.put("cwtPercentage", details.get("cwtPercentage"))
            }

            Map productDetails = [
                    extendedProperties: extendedPropertiesMap
            ]

            DBPChargesCalculator calculator = new DBPChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            // Compute for Settlement!
            def temp = calculator.computeSettlement(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDefaultValues")
    public Response getDefaultValues(@Context UriInfo allUri) {
        println "getDefaultValues"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        try {
            def temp = chargesLookup.getDefaultValuesForServiceMap();

            for (String key : temp.keySet()) {
                println "key: " + key + ", value: " + temp.get(key)
            }

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    private static void insertRatesFromParams(HashMap<String, String> jsonParams, CurrencyConverter currencyConverter) {
        println "jsonParams:" + jsonParams
        println "jsonParams.get(\"productCurrency\"):" + jsonParams.get("productCurrency")
        println "jsonParams.get(\"thirdToUsdSpecialConversionRateCurrency\"):" + jsonParams.get("thirdToUsdSpecialConversionRateCurrency")
        if ((jsonParams.get("thirdToUsdSpecialConversionRateCurrency") != null && (new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()).compareTo(BigDecimal.ZERO) > 0))
                && jsonParams.get("productCurrency") != null) {

            println "angol angol angol"
            String productCurrency = (String) jsonParams.get("productCurrency")
            println "thirdToUsdSpecialConversionRateCurrency:" + new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString())
            currencyConverter.addRate("REG-SELL", productCurrency.trim().toUpperCase(), "USD",
                    new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString())
            )
            currencyConverter.addRate("REG-SELL", "USD", productCurrency.trim().toUpperCase(),
                    (BigDecimal.ONE).divide(new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()), 12, BigDecimal.ROUND_UP)
            )
        }

        if (jsonParams.get("usdToPhpSpecialConversionRate") != null) {
            println "usdToPhpSpecialConversionRate:" + new BigDecimal(jsonParams.get("usdToPhpSpecialConversionRate").toString())
            currencyConverter.addRate("REG-SELL", "USD", "PHP", new BigDecimal(jsonParams.get("usdToPhpSpecialConversionRate").toString()))
        }

        if (jsonParams.get("urr") != null) {
            println "urr:" + new BigDecimal(jsonParams.get("urr").toString())
            currencyConverter.addRate("URR", "USD", "PHP", new BigDecimal(jsonParams.get("urr").toString()))
        }

        if (jsonParams.get("urr") != null &&
                (jsonParams.get("thirdToUsdSpecialConversionRateCurrency") != null && (new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()).compareTo(BigDecimal.ZERO) > 0)) &&
                jsonParams.get("productCurrency") != null) {

            String productCurrency = (String) jsonParams.get("productCurrency")
            println "thirdToUsdSpecialConversionRateCurrency:" + new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString())
            BigDecimal thirdToPhp = new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()) * new BigDecimal(jsonParams.get("urr").toString())
            currencyConverter.addRate("URR", productCurrency.trim().toUpperCase(), "PHP", thirdToPhp)
            //Comment out if causing error
            //currencyConverter.addRate("URR", "PHP", productCurrency.trim().toUpperCase(), BigDecimal.ONE.divide(thirdToPhp,12,BigDecimal.ROUND_UP))
        }

        if (jsonParams.get("usdToPhpSpecialConversionRate") != null &&
                (jsonParams.get("thirdToUsdSpecialConversionRateCurrency") != null && (new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()).compareTo(BigDecimal.ZERO) > 0)) &&
                jsonParams.get("productCurrency") != null) {

            String productCurrency = (String) jsonParams.get("productCurrency")
            println "thirdToUsdSpecialConversionRateCurrency:" + new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString())
            BigDecimal thirdToPhp = new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()) * new BigDecimal(jsonParams.get("usdToPhpSpecialConversionRate").toString())
            currencyConverter.addRate("REG-SELL", productCurrency.trim().toUpperCase(), "PHP", thirdToPhp)

            //Comment out if causing error
            //currencyConverter.addRate("REG-SELL", "PHP",productCurrency.trim().toUpperCase(),  BigDecimal.ONE.divide(thirdToPhp,12,BigDecimal.ROUND_UP))
        }
    }

    private List buildPaymentModeMap(TradeServiceId tradeServiceId) {
        return tradeServiceService.buildPaymentModeMap(tradeServiceId)
    }

    private List buildPaymentModeMapSettlement(TradeServiceId tradeServiceId) {
        return tradeServiceService.buildPaymentModeMapSettlement(tradeServiceId)
    }

    private Map buildExtendedPropertiesMap(Map paramz) {
        Map extended = [:]
        extended.putAll(paramz)
        println("extended:" + extended)
        return extended
    }

    //START OF PAYMENT OF OTHER IMPORT/EXPORT CHARGES RELATED FUNCTIONS
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getServiceChargeBaseAmounts")
    public Response getServiceChargeBaseAmounts(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            def serviceChargeBaseAmounts = serviceChargeRepository.getServiceChargeBaseAmount(new TradeServiceId((String) jsonParams.get("tradeServiceId")))

            returnMap.put("response", serviceChargeBaseAmounts);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getSavedNewServiceCharges")
    public Response getSavedNewServiceCharges(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            def savedNewServiceCharges = serviceChargeRepository.getSavedNewServiceCharges(new TradeServiceId((String) jsonParams.get("tradeServiceId")))

            returnMap.put("response", savedNewServiceCharges);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getSavedNewCollectibleCharges")
    public Response getSavedNewCollectibleCharges(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            def savedNewServiceCharges = serviceChargeRepository.getSavedNewCollectibleCharges(new TradeServiceId((String) jsonParams.get("tradeServiceId")))

            returnMap.put("response", savedNewServiceCharges);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCifList")
    public Response getCifList(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            def savedNewServiceCharges = gltsSequenceRepository.


            returnMap.put("response", savedNewServiceCharges);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }





    //TODO:: TRANSFORM THIS TO FUNCTIONS NOT WEBSERVICES?
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDMNONLCSettlementChargeOthers")
    public Response getDMNONLCSettlementChargeOthers(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDMNONLCSettlementCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter) //TODO Fix this

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            if (tradeService != null) {

                HashMap<String, Object> details = tradeService.getDetails();

                String documentClass = tradeService.getDocumentClass()
                String documentType = tradeService.getDocumentType()
                String documentSubType1 = tradeService.getDocumentSubType1()
                String documentSubType2 = tradeService.getDocumentSubType2()
                String serviceType = tradeService.getServiceType()
                String amount = (String) details.get("amount")
                String issueDate = (String) details.get("issueDate")  // As of 5/31/2013
                String expiryDate = (String) details.get("expiryDate")
                String usancePeriod = (String) details.get("usancePeriod")
                String currency = (String) details.get("currency")

//                Map extendedPropertiesMap = buildExtendedPropertiesMap(jsonParams)
                Map extendedPropertiesMap = [:]
                extendedPropertiesMap.put("documentClass", documentClass)
                extendedPropertiesMap.put("documentType", documentType)
                extendedPropertiesMap.put("documentSubType1", documentSubType1)
                extendedPropertiesMap.put("documentSubType2", documentSubType2)
                extendedPropertiesMap.put("serviceType", serviceType)
                // extendedPropertiesMap.put("etsDate", etsDate)
                extendedPropertiesMap.put("issueDate", issueDate)
                extendedPropertiesMap.put("expiryDate", expiryDate)
                extendedPropertiesMap.put("usancePeriod", usancePeriod)

                println "extendedPropertiesMap" + extendedPropertiesMap
                println "productAmount:" + amount
                println "productCurrency:" + currency
//                println "productCurrency:" + jsonParams.get("productCurrency").toString() //TODO Replace this

                Map productDetails = [
                        productCurrency: currency,
                        productAmount: new BigDecimal(amount),
                        chargeSettlementCurrency: "PHP",//jsonParams.get("settlementCurrency"),
                        productSettlement: buildPaymentModeMap(tradeServiceId),
                        extendedProperties: extendedPropertiesMap,
                        chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
                ]

                NonLCChargesCalculator calculator = new NonLCChargesCalculator();

                calculator.setCurrencyConverter(currencyConverter)
                calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

                def temp = calculator.computeDM(productDetails)
                println temp

                returnMap.put("result", temp);
                returnMap.put("status", "ok");

            } else {

                throw new Exception("(Graceful exit) TradeService is null. Probably TradeService is not yet created.");
            }

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXNONLCSettlementChargeOthers")
    public Response getFXNONLCSettlementChargeOthers(@Context UriInfo allUri) {
        println "getFXNONLCSettlementCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)//TODO

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String issueDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'issueDate')
            String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
            String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
            String advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'advanceCorresChargesFlag')
            String confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'confirmationInstructionsFlag')
            String amount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'amount')
            String currency = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'currency')
            String settlementCurrency = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'settlementCurrency')


            Map extendedPropertiesMap = [:]
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("issueDate", issueDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)
            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + amount
            println "productCurrency:" + currency
//            println "productCurrency:" + jsonParams.get("productCurrency").toString()
//            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: currency,
                    productAmount: new BigDecimal(amount),
                    chargeSettlementCurrency: settlementCurrency,
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            NonLCChargesCalculator calculator = new NonLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeFX(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDMOpeningChargeOthers")
    public Response getDMOpeningChargeOthers(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDMOpeningCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            if (tradeService != null) {

                HashMap<String, Object> details = tradeService.getDetails();

                String documentClass = tradeService.getDocumentClass()
                String documentType = tradeService.getDocumentType()
                String documentSubType1 = tradeService.getDocumentSubType1()
                String documentSubType2 = tradeService.getDocumentSubType2()

                // String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
                // String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
                // String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
                // String etsDate = (String)details.get("etsDate")
                // String amount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'amount')
                String amount = (String) details.get("amount")
                String issueDate = (String) details.get("issueDate")  // As of 5/31/2013
                String expiryDate = (String) details.get("expiryDate")
                String usancePeriod = (String) details.get("usancePeriod")

                Map extendedPropertiesMap = [:]
                extendedPropertiesMap.put("documentClass", documentClass)
                extendedPropertiesMap.put("documentType", documentType)
                extendedPropertiesMap.put("documentSubType1", documentSubType1)
                extendedPropertiesMap.put("documentSubType2", documentSubType2)
                // extendedPropertiesMap.put("etsDate", etsDate)
                extendedPropertiesMap.put("issueDate", issueDate)
                extendedPropertiesMap.put("expiryDate", expiryDate)
                extendedPropertiesMap.put("usancePeriod", usancePeriod)

                println "extendedPropertiesMap" + extendedPropertiesMap
                println "productAmount:" + amount
                println "productCurrency:" + jsonParams.get("productCurrency").toString()

                Map productDetails = [
                        productCurrency: jsonParams.get("productCurrency"),
                        productAmount: new BigDecimal(amount),
                        chargeSettlementCurrency: "PHP",//jsonParams.get("settlementCurrency"),
                        productSettlement: buildPaymentModeMap(tradeServiceId),
                        extendedProperties: extendedPropertiesMap
                ]

                DMLCChargesCalculator calculator = new DMLCChargesCalculator();

                calculator.setCurrencyConverter(currencyConverter)
                calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

                def temp = calculator.computeOpening(productDetails)
                println temp

                returnMap.put("result", temp);
                returnMap.put("status", "ok");

            } else {

                throw new Exception("(Graceful exit) TradeService is null. Probably TradeService is not yet created.");
            }

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDMNegotiationChargeOthers")
    public Response getDMNegotiationChargeOthers(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDMNegotiationCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)
            Map extendedPropertiesMap = [:]

            String cableFeeFlag = tradeServiceService.getCableFlagFromSettlement(tradeServiceId)
            String remittanceFlag = tradeServiceService.getRemittanceFlagFromSettlement(tradeServiceId)
            String negotiationAmount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'negotiationAmount')

            extendedPropertiesMap.put("cableFeeFlag", cableFeeFlag)
            extendedPropertiesMap.put("remittanceFlag", remittanceFlag)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)

            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + negotiationAmount
            println "productCurrency:" + jsonParams.get("productCurrency").toString()

            Map productDetails = [
                    productCurrency: jsonParams.get("productCurrency"),
                    productAmount: new BigDecimal(negotiationAmount),
                    chargeSettlementCurrency: "PHP",
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            DMLCChargesCalculator calculator = new DMLCChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeNegotiation(productDetails)
            println temp
            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDMAmendmentChargeOthers")
    public Response getDMAmendmentChargeOthers(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDMAmendmentCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            if (tradeService != null) {

                Map<String, Object> details = tradeService.getDetails()

                String documentClass = tradeService.getDocumentClass()
                String documentType = tradeService.getDocumentType()
                String documentSubType1 = tradeService.getDocumentSubType1()
                String documentSubType2 = tradeService.getDocumentSubType2()

                // String etsDate = (String) details.get("etsDate")
                String amendmentDate = (String) details.get("amendmentDate")  // As of 5/31/2013
                String expiryDate = (String) details.get("expiryDate")
                String usancePeriod = (String) details.get("usancePeriod")

                String amountSwitch = (String) details.get("amountSwitch")
                String lcAmountFlagDisplay = (String) details.get("lcAmountFlagDisplay")
                String lcAmountFlag = (String) details.get("lcAmountFlag")
                String amount = (String) details.get("outstandingBalance") //-->will use outstandingBalance instead of amount
                String amountTo = (String) details.get("amountTo")
                String expiryDateSwitch = (String) details.get("expiryDateSwitch")
                String expiryDateFlagDisplay = (String) details.get("expiryDateFlagDisplay")
                String originalExpiryDate = (String) details.get("originalExpiryDate")
                String expiryDateTo = (String) details.get("expiryDateTo")
                String tenorSwitch = (String) details.get("tenorSwitch")
                String originalTenor = (String) details.get("originalTenor")
                String tenorTo = (String) details.get("tenorTo")
                String usancePeriodTo = (String) details.get("usancePeriodTo")
                String narrativeSwitch = (String) details.get("narrativeSwitch")
                String narrative = (String) details.get("narrative")

                Map extendedPropertiesMap = [:]

                extendedPropertiesMap.put("documentClass", documentClass)
                extendedPropertiesMap.put("documentType", documentType)
                extendedPropertiesMap.put("documentSubType1", documentSubType1)
                extendedPropertiesMap.put("documentSubType2", documentSubType2)

                // extendedPropertiesMap.put("etsDate", etsDate)
                extendedPropertiesMap.put("amendmentDate", amendmentDate)
                extendedPropertiesMap.put("expiryDate", expiryDate)
                extendedPropertiesMap.put("usancePeriod", usancePeriod)

                extendedPropertiesMap.put("amountSwitch", amountSwitch)
                extendedPropertiesMap.put("lcAmountFlagDisplay", lcAmountFlagDisplay)
                extendedPropertiesMap.put("lcAmountFlag", lcAmountFlag)

                println "amountSwitch:" + amountSwitch
                println "lcAmountFlagDisplay:" + lcAmountFlagDisplay
                println "lcAmountFlag:" + lcAmountFlag

                extendedPropertiesMap.put("amount", amount)
                extendedPropertiesMap.put("amountTo", amountTo)
                extendedPropertiesMap.put("expiryDateSwitch", expiryDateSwitch)
                extendedPropertiesMap.put("expiryDateFlagDisplay", expiryDateFlagDisplay)
                extendedPropertiesMap.put("originalExpiryDate", originalExpiryDate)
                extendedPropertiesMap.put("expiryDateTo", expiryDateTo)
                extendedPropertiesMap.put("tenorSwitch", tenorSwitch)
                extendedPropertiesMap.put("originalTenor", originalTenor)
                extendedPropertiesMap.put("tenorTo", tenorTo)
                extendedPropertiesMap.put("usancePeriodTo", usancePeriodTo)
                extendedPropertiesMap.put("narrativeSwitch", narrativeSwitch)
                extendedPropertiesMap.put("narrative", narrative.replace(':',''))

                println "extendedPropertiesMap" + extendedPropertiesMap
                println "productAmount:" + amount //jsonParams.get("productAmount").toString()
                println "productCurrency:" + jsonParams.get("productCurrency").toString()

                Map productDetails = [
                        productCurrency: jsonParams.get("productCurrency"),
                        productAmount: new BigDecimal(amount),//new BigDecimal(jsonParams.get("productAmount").toString()),
                        chargeSettlementCurrency: "PHP", //THIS IS DM ONLY PHP
                        productSettlement: buildPaymentModeMap(tradeServiceId),
                        extendedProperties: extendedPropertiesMap
                ]

                DMLCChargesCalculator calculator = new DMLCChargesCalculator();

                calculator.setCurrencyConverter(currencyConverter)
                calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

                def temp = calculator.computeAmendment(productDetails)

                println "###### temp = ${temp}"

                returnMap.put("result", temp);
                returnMap.put("status", "ok");

            } else {

                throw new Exception("(Graceful exit) TradeService is null. Probably TradeService is not yet created.");
            }

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDUALoanMaturityAdjustmentChargeOthers")
    public Response getDUALoanMaturityAdjustmentChargeOthers(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDUALoanMaturityAdjustmentCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)

            if (tradeService != null) {

                Map<String, Object> details = tradeService.getDetails()

                String documentClass = tradeService.getDocumentClass()
                String documentType = tradeService.getDocumentType()
                String documentSubType1 = tradeService.getDocumentSubType1()
                String documentSubType2 = tradeService.getDocumentSubType2()
                // String etsDate = (String) details.get("etsDate")
                String negotiationValueDate = (String) details.get("negotiationValueDate")  // As of 5/31/2013
                String expiryDate = (String) details.get("expiryDate")
                String usancePeriod = (String) details.get("usancePeriod")

                String loanMaturityDateFrom = (String) details.get("loanMaturityDateFrom")
                String loanMaturityDateTo = (String) details.get("loanMaturityDateTo")

                String productAmount = (String) details.get("amount")
                String productCurrency = (String) details.get("currency")

                Map extendedPropertiesMap = [:]
                extendedPropertiesMap.put("documentClass", documentClass)
                extendedPropertiesMap.put("documentType", documentType)
                extendedPropertiesMap.put("documentSubType1", documentSubType1)
                extendedPropertiesMap.put("documentSubType2", documentSubType2)
                // extendedPropertiesMap.put("etsDate", etsDate)
                extendedPropertiesMap.put("negotiationValueDate", negotiationValueDate)
                extendedPropertiesMap.put("expiryDate", expiryDate)
                extendedPropertiesMap.put("usancePeriod", usancePeriod)

                extendedPropertiesMap.put("loanMaturityDateFrom", loanMaturityDateFrom)
                extendedPropertiesMap.put("loanMaturityDateTo", loanMaturityDateTo)

                println "extendedPropertiesMap " + extendedPropertiesMap
                println "productAmount: " + productAmount
                println "productCurrency: " + productCurrency

                Map productDetails = [
                        productCurrency: productCurrency,
                        productAmount: new BigDecimal(productAmount.toString()),
                        chargeSettlementCurrency: "PHP",
                        productSettlement: buildPaymentModeMap(tradeServiceId),
                        extendedProperties: extendedPropertiesMap
                ]

                DMLCChargesCalculator calculator = new DMLCChargesCalculator();

                calculator.setCurrencyConverter(currencyConverter)
                calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

                def temp = calculator.computeUaLoanMaturityAdjustment(productDetails)

                println temp

                returnMap.put("result", temp);
                returnMap.put("status", "ok");

            } else {

                throw new Exception("(Graceful exit) TradeService is null. Probably TradeService is not yet created.");
            }

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getDUALoanSettlementChargeOthers")
    public Response getDUALoanSettlementChargeOthers(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getDUALoanSettlementCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            Map extendedPropertiesMap = [:]

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()
            String cableFeeFlag = tradeServiceService.getCableFlagFromSettlement(tradeServiceId)
            String remittanceFlag = tradeServiceService.getRemittanceFlagFromSettlement(tradeServiceId)
            String productAmount = (String) details.get("amount")
            String productCurrency = (String) details.get("currency")

            extendedPropertiesMap.put("cableFeeFlag", cableFeeFlag)
            extendedPropertiesMap.put("remittanceFlag", remittanceFlag)

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)

            println "extendedPropertiesMap " + extendedPropertiesMap
            println "productAmount: " + productAmount
            println "productCurrency: " + productCurrency

            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: new BigDecimal(productAmount.toString()),
                    chargeSettlementCurrency: "PHP",
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            DMLCChargesCalculator calculator = new DMLCChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            def temp = calculator.computeUaLoanSettlement(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXOpeningChargeOthers")
    public Response getFXOpeningChargeOthers(@Context UriInfo allUri) {
        println "getFXOpeningCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()

            String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String issueDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'issueDate')
            String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
            String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
            String advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'advanceCorresChargesFlag')
            String confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'confirmationInstructionsFlag')
            String amount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'amount')
            String currency = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'currency')
            String settlementCurrency = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'settlementCurrency')

            Map extendedPropertiesMap = [:]
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("issueDate", issueDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)


            def temp01 = jsonParams.get("recompute")
            String recompute = ""
            if(temp01!=null){
                recompute=temp01.toString()
            }



            if(recompute.equalsIgnoreCase("Y")){

                jsonParams.get("cilexPercentage")?extendedPropertiesMap.put("cilexPercentage", jsonParams.get("cilexPercentage")?.toString()):""
                jsonParams.get("cilexDenominator")?extendedPropertiesMap.put("cilexDenominator", jsonParams.get("cilexDenominator")?.toString()):""
                jsonParams.get("cilexNumerator")?extendedPropertiesMap.put("cilexNumerator", jsonParams.get("cilexNumerator")?.toString()):""

                jsonParams.get("confirmingFeePercentage")?extendedPropertiesMap.put("confirmingFeePercentage", jsonParams.get("confirmingFeePercentage")?.toString()):""
                jsonParams.get("confirmingFeeDenominator")?extendedPropertiesMap.put("confirmingFeeDenominator", jsonParams.get("confirmingFeeDenominator")?.toString()):""
                jsonParams.get("confirmingFeeNumerator")?extendedPropertiesMap.put("confirmingFeeNumerator", jsonParams.get("confirmingFeeNumerator")?.toString()):""

                jsonParams.get("commitmentFeePercentage")?extendedPropertiesMap.put("commitmentFeePercentage", jsonParams.get("commitmentFeePercentage")?.toString()):""
                jsonParams.get("commitmentFeeDenominator")?extendedPropertiesMap.put("commitmentFeeDenominator", jsonParams.get("commitmentFeeDenominator")?.toString()):""
                jsonParams.get("commitmentFeeNumerator")?extendedPropertiesMap.put("commitmentFeeNumerator", jsonParams.get("commitmentFeeNumerator")?.toString()):""

                jsonParams.get("bankCommissionPercentage")?extendedPropertiesMap.put("bankCommissionPercentage", jsonParams.get("bankCommissionPercentage")?.toString()):""
                jsonParams.get("bankCommissionDenominator")?extendedPropertiesMap.put("bankCommissionDenominator", jsonParams.get("bankCommissionDenominator")?.toString()):""
                jsonParams.get("bankCommissionNumerator")?extendedPropertiesMap.put("bankCommissionNumerator", jsonParams.get("bankCommissionNumerator")?.toString()):""

            }


            println "extendedPropertiesMap" + extendedPropertiesMap
            println "productAmount:" + amount
//            println "productCurrency:" + jsonParams.get("productCurrency").toString()
//            println "chargeSettlementCurrency:" + jsonParams.get("chargeSettlementCurrency").toString()
            Map productDetails = [
                    productCurrency: currency,
                    productAmount: new BigDecimal(amount),
                    chargeSettlementCurrency: settlementCurrency,
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
					chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeOpening(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXNegotiationChargeOthers")
    public Response getFXNegotiationChargeOthers(@Context UriInfo allUri) {
        println "getFXNegotiationCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            String etsDate = (String) details.get('etsDate')
            //String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String expiryDate = (String) details.get('expiryDate')
            String usancePeriod = (String) details.get('usancePeriod')
            String cwtFlag = (String) details.get('cwtFlag') ?: "N"
            String cwtPercentage = (String) details.get('cwtPercentage') ?: "0.98"
            String negotiationAmount = (String) details.get('negotiationAmount')
            String currency = (String) details.get('negotiationCurrency')
            String overdrawnAmount = (String) details.get('overdrawnAmount')
            String settlementCurrency = (String) details.get('settlementCurrency')

            Map extendedPropertiesMap = [:]
            extendedPropertiesMap.put("centavos", jsonParams.get("documentaryStampsCentavos").toString())
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("cwtFlag", cwtFlag)
            extendedPropertiesMap.put("cwtPercentage", cwtPercentage)
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("negotiationAmount", negotiationAmount)
            extendedPropertiesMap.put("overdrawnAmount", overdrawnAmount)
            println "extendedPropertiesMap" + extendedPropertiesMap

            Map productDetails = [
                    productCurrency: currency,
                    productAmount: new BigDecimal(negotiationAmount),
                    chargeSettlementCurrency: settlementCurrency,
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeNegotiation(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXAmendmentChargeOthers")
    public Response getFXAmendmentChargeOthers(@Context UriInfo allUri) {
        println "getFXAmendmentCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()
            String issueDate = (String) details.get("issueDate")
            String etsDate = (String) details.get("etsDate")
            String amendmentDate = (String) details.get("amendmentDate")
            String expiryDate = (String) details.get("expiryDate")
            String usancePeriod = (String) details.get("usancePeriod")

            String amountSwitch = (String) details.get("amountSwitch")
            String lcAmountFlagDisplay = (String) details.get("lcAmountFlagDisplay")
            String outstandingBalance = (String) details.get("outstandingBalance") //-->will use outstandingBalance
            String amount = (String) details.get("outstandingBalance") //-->will use outstandingBalance
            String amountTo = (String) details.get("amountTo") ?: '0'
            String expiryDateSwitch = (String) details.get("expiryDateSwitch")
            String expiryDateFlagDisplay = (String) details.get("expiryDateFlagDisplay")
            String originalExpiryDate = (String) details.get("originalExpiryDate")
            String expiryDateTo = (String) details.get("expiryDateTo")
            String tenorSwitch = (String) details.get("tenorSwitch")
            String originalTenor = (String) details.get("originalTenor")
            String tenorTo = (String) details.get("tenorTo")
            String usancePeriodTo = (String) details.get("usancePeriodTo")
            if(!usancePeriodTo){
                usancePeriodTo = "0"
            }
            println "angol angol usancePeriodTo:"+usancePeriodTo
            String narrativeSwitch = (String) details.get("narrativeSwitch")
            String narrative = (String) details.get("narrative")
            String confirmationInstructionsFlag = (String) details.get("confirmationInstructionsFlagSwitch")
            String originalConfirmationInstructionsFlag = (String) details.get("originalConfirmationInstructionsFlag")
            String confirmationInstructionsFlagTo = (String) details.get("confirmationInstructionsFlagTo")
//            String confirmationInstructionsFlag = (String)details.get("confirmationInstructionsFlag")
            String advanceCorresChargesFlag = (String) details.get("advanceCorresChargesFlag")
            String settlementCurrency = (String) details.get("settlementCurrency")
            String currency = (String) details.get("currency")

            Map extendedPropertiesMap = [:]
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("amendmentDate", amendmentDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)

            extendedPropertiesMap.put("amountSwitch", amountSwitch)
            extendedPropertiesMap.put("lcAmountFlagDisplay", lcAmountFlagDisplay)
            extendedPropertiesMap.put("lcAmountFlag", lcAmountFlagDisplay)
            extendedPropertiesMap.put("outstandingBalance", outstandingBalance)
            extendedPropertiesMap.put("amount", amount)
            extendedPropertiesMap.put("amountTo", amountTo)
            extendedPropertiesMap.put("expiryDateSwitch", expiryDateSwitch)
            extendedPropertiesMap.put("expiryDateCheck", expiryDateSwitch)
            extendedPropertiesMap.put("expiryDateFlagDisplay", expiryDateFlagDisplay)
            extendedPropertiesMap.put("expiryDateFlag", expiryDateFlagDisplay)
            extendedPropertiesMap.put("originalExpiryDate", originalExpiryDate)
            extendedPropertiesMap.put("expiryDateTo", expiryDateTo)
            extendedPropertiesMap.put("tenorSwitch", tenorSwitch)
            extendedPropertiesMap.put("originalTenor", originalTenor)
            extendedPropertiesMap.put("tenorTo", tenorTo)
            extendedPropertiesMap.put("usancePeriodTo", usancePeriodTo)
            extendedPropertiesMap.put("narrativeSwitch", narrativeSwitch)
            extendedPropertiesMap.put("narrative", narrative)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlagTo", confirmationInstructionsFlagTo)
            extendedPropertiesMap.put("originalConfirmationInstructionsFlag", originalConfirmationInstructionsFlag)
            extendedPropertiesMap.put("issueDate", issueDate)
            println "extendedPropertiesMap" + extendedPropertiesMap

            Map productDetails = [
                    productCurrency: currency,
                    productAmount: new BigDecimal(amount),
                    chargeSettlementCurrency: settlementCurrency,
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeAmendment(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXAdjustmentChargeOthers")
    public Response getFXAdjustmentChargeOthers(@Context UriInfo allUri) {
        println "getFXAdjustmentCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
            String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
            String advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'advanceCorresChargesFlag')
            String confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'confirmationInstructionsFlag')
            String settlementCurrency = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'settlementCurrency')
            String lcCurrency = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'lcCurrency')
            String amount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'amount')
            Map extendedPropertiesMap = [:]
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)
            println "extendedPropertiesMap" + extendedPropertiesMap

            Map productDetails = [
                    productCurrency: lcCurrency,
                    productAmount: new BigDecimal(amount.toString()),
                    chargeSettlementCurrency: settlementCurrency,
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeAdjustment(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXUALoanMaturityAdjustmentChargeOthers")
    public Response getFXUALoanMaturityAdjustmentChargeOthers(@Context UriInfo allUri) {

        println "\n++++++++++++++++++++++++++++++++ getFXUALoanMaturityAdjustmentCharge"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)
            println "jsonParams:" + jsonParams
            String chargesSettlementCurrencyParam = jsonParams.get("")

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String etsDate = (String) details.get("etsDate")
            String expiryDate = (String) details.get("expiryDate")
            String usancePeriod = (String) details.get("usancePeriod")

            String loanMaturityDateFrom = (String) details.get("loanMaturityDateFrom")
            String loanMaturityDateTo = (String) details.get("loanMaturityDateTo")

            String productAmount = (String) details.get("amount")
            String productCurrency = (String) details.get("currency")
            String settlementCurrency = (String) details.get("settlementCurrency")

            Map extendedPropertiesMap = [:]
            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)

            extendedPropertiesMap.put("loanMaturityDateFrom", loanMaturityDateFrom)
            extendedPropertiesMap.put("loanMaturityDateTo", loanMaturityDateTo)

            println "extendedPropertiesMap " + extendedPropertiesMap
            println "productAmount: " + productAmount
            println "productCurrency: " + productCurrency
//            println "jsonParams.get(\"settlementCurrency\"): " + jsonParams.get("settlementCurrency")

            Map productDetails = [
                    productCurrency: productCurrency,
                    productAmount: new BigDecimal(productAmount.toString()),
                    chargeSettlementCurrency: settlementCurrency,
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();

            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")

            def temp = calculator.computeUaLoanMaturityAdjustment(productDetails)

            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXUALoanSettlementChargeOthers")
    public Response getFXUALoanSettlementChargeOthers(@Context UriInfo allUri) {
        println "getFXUALoanSettlementCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)

            String etsDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'etsDate')
            String expiryDate = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'expiryDate')
            String cwtFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'cwtFlag')
            String cwtPercentage = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'cwtPercentage')
            String usancePeriod = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'usancePeriod')
            String advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'advanceCorresChargesFlag')
            String confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'confirmationInstructionsFlag')
            String currency = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'lcCurrency')
            String amount = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'amount')
            String settlementCurrency = tradeServiceService.getTradeServiceProperty(tradeServiceId, 'settlementCurrency')

            Map extendedPropertiesMap = [:]
            extendedPropertiesMap.put("etsDate", etsDate)
            extendedPropertiesMap.put("expiryDate", expiryDate)
            extendedPropertiesMap.put("usancePeriod", usancePeriod)
            extendedPropertiesMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag)
            extendedPropertiesMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag)
            extendedPropertiesMap.put("cwtFlag", cwtFlag)
            extendedPropertiesMap.put("cwtPercentage", cwtPercentage)

            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)
            println "extendedPropertiesMap" + extendedPropertiesMap

            Map productDetails = [
                    productCurrency: currency,
                    productAmount: amount,
                    chargeSettlementCurrency: settlementCurrency,
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeUaLoanSettlement(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXIndemnityIssuanceChargeOthers")
    public Response getFXIndemnityIssuanceChargeOthers(@Context UriInfo allUri) {
        println "getFXIndemnityIssuanceCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)
            TradeService tradeService = tradeServiceService.getTradeService(tradeServiceId)
            Map<String, Object> details = tradeService.getDetails()
            String cwtFlag = (String) details.get('cwtFlag') ?: "N"
            String cwtPercentage = (String) details.get('cwtPercentage') ?: "0.98"
            String indemnityType = (String) details.get('indemnityType') ?: "BE"
            String settlementCurrency = (String) details.get('settlementCurrency') ?: "PHP"

            String documentClass = tradeService.getDocumentClass()
            String documentType = tradeService.getDocumentType()
            String documentSubType1 = tradeService.getDocumentSubType1()
            String documentSubType2 = tradeService.getDocumentSubType2()
            String serviceType = tradeService.getServiceType()

            extendedPropertiesMap.put("documentClass", documentClass)
            extendedPropertiesMap.put("documentType", documentType)
            extendedPropertiesMap.put("documentSubType1", documentSubType1)
            extendedPropertiesMap.put("documentSubType2", documentSubType2)
            extendedPropertiesMap.put("serviceType", serviceType)

            Map extendedPropertiesMap = [:]
            extendedPropertiesMap.put("indemnityType", indemnityType)
            extendedPropertiesMap.put("cwtPercentage", cwtPercentage)
            extendedPropertiesMap.put("cwtFlag", cwtFlag)
            println "extendedPropertiesMap" + extendedPropertiesMap

            Map productDetails = [
                    productCurrency: "PHP",
                    productAmount: BigDecimal.ZERO,
                    chargeSettlementCurrency: settlementCurrency,
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap,
                    chargesParameter: chargesParameterRepository.getParameters(extendedPropertiesMap)
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeIndemnityIssuance(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFXIndemnityCancellationChargeOthers")
    public Response getFXIndemnityCancellationChargeOthers(@Context UriInfo allUri) {
        println "getFXIndemnityCancellationCharge"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
            }

            CurrencyConverter currencyConverter = new CurrencyConverter()
            insertRatesFromParamsOthers(jsonParams, currencyConverter)


            Map extendedPropertiesMap = [:]
            println "extendedPropertiesMap" + extendedPropertiesMap
            Map productDetails = [
                    productCurrency: "PHP",
                    productAmount: BigDecimal.ZERO,
                    chargeSettlementCurrency: "PHP",
                    productSettlement: buildPaymentModeMap(tradeServiceId),
                    extendedProperties: extendedPropertiesMap
            ]

            FXLCChargesCalculator calculator = new FXLCChargesCalculator();
            calculator.setCurrencyConverter(currencyConverter)
            calculator.configRatesBasis("REG-SELL", "URR", "REG-SELL", "REG-SELL")
            def temp = calculator.computeIndemnityCancellation(productDetails)
            println temp

            returnMap.put("result", temp);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();

    }

    //TODO
    private static void insertRatesFromParamsOthers(HashMap<String, String> jsonParams, CurrencyConverter currencyConverter) {
        println "jsonParams:" + jsonParams
        println "jsonParams.get(\"productCurrency\"):" + jsonParams.get("productCurrency")
        println "jsonParams.get(\"thirdToUsdSpecialConversionRateCurrency\"):" + jsonParams.get("thirdToUsdSpecialConversionRateCurrency")
        if ((jsonParams.get("thirdToUsdSpecialConversionRateCurrency") != null && (new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()).compareTo(BigDecimal.ZERO) > 0))
                && jsonParams.get("productCurrency") != null) {

            println "angol angol angol"
            String productCurrency = (String) jsonParams.get("productCurrency")
            println "thirdToUsdSpecialConversionRateCurrency:" + new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString())
            currencyConverter.addRate("REG-SELL", productCurrency.trim().toUpperCase(), "USD",
                    new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString())
            )
            currencyConverter.addRate("REG-SELL", "USD", productCurrency.trim().toUpperCase(),
                    (BigDecimal.ONE).divide(new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()), 12, BigDecimal.ROUND_UP)
            )
        }

        if (jsonParams.get("usdToPhpSpecialConversionRate") != null) {
            println "usdToPhpSpecialConversionRate:" + new BigDecimal(jsonParams.get("usdToPhpSpecialConversionRate").toString())
            currencyConverter.addRate("REG-SELL", "USD", "PHP", new BigDecimal(jsonParams.get("usdToPhpSpecialConversionRate").toString()))
        }

        if (jsonParams.get("urr") != null) {
            println "urr:" + new BigDecimal(jsonParams.get("urr").toString())
            currencyConverter.addRate("URR", "USD", "PHP", new BigDecimal(jsonParams.get("urr").toString()))
        }

        if (jsonParams.get("urr") != null &&
                (jsonParams.get("thirdToUsdSpecialConversionRateCurrency") != null && (new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()).compareTo(BigDecimal.ZERO) > 0)) &&
                jsonParams.get("productCurrency") != null) {

            String productCurrency = (String) jsonParams.get("productCurrency")
            println "thirdToUsdSpecialConversionRateCurrency:" + new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString())
            BigDecimal thirdToPhp = new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()) * new BigDecimal(jsonParams.get("urr").toString())
            currencyConverter.addRate("URR", productCurrency.trim().toUpperCase(), "PHP", thirdToPhp)
            //Comment out if causing error
            //currencyConverter.addRate("URR", "PHP", productCurrency.trim().toUpperCase(), BigDecimal.ONE.divide(thirdToPhp,12,BigDecimal.ROUND_UP))
        }

        if (jsonParams.get("usdToPhpSpecialConversionRate") != null &&
                (jsonParams.get("thirdToUsdSpecialConversionRateCurrency") != null && (new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()).compareTo(BigDecimal.ZERO) > 0)) &&
                jsonParams.get("productCurrency") != null) {

            String productCurrency = (String) jsonParams.get("productCurrency")
            println "thirdToUsdSpecialConversionRateCurrency:" + new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString())
            BigDecimal thirdToPhp = new BigDecimal(jsonParams.get("thirdToUsdSpecialConversionRateCurrency").toString()) * new BigDecimal(jsonParams.get("usdToPhpSpecialConversionRate").toString())
            currencyConverter.addRate("REG-SELL", productCurrency.trim().toUpperCase(), "PHP", thirdToPhp)

            //Comment out if causing error
            //currencyConverter.addRate("REG-SELL", "PHP",productCurrency.trim().toUpperCase(),  BigDecimal.ONE.divide(thirdToPhp,12,BigDecimal.ROUND_UP))
        }
    }

}