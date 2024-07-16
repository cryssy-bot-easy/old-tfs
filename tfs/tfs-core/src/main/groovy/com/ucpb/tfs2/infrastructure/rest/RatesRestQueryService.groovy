package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.incuventure.cqrs.infrastructure.StandardAPICallDispatcher
import com.ucpb.tfs.domain.payment.PaymentRepository
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.domain.service.TradeServiceId
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.interfaces.services.RatesService
import com.ucpb.tfs2.application.util.CurrencyConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import java.text.ParseException
import java.text.SimpleDateFormat
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/**
 * Created by IntelliJ IDEA.
 * User: Marv
 * Date: 12/19/12
 * Time: 3:08 PM
 */

@Path("/rates")
@Component
class RatesRestQueryService {

    @Autowired
    StandardAPICallDispatcher standardAPICallDispatcher;

    @Autowired
    @Qualifier("RatesService")
    RatesService ratesService;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/details2")
    public Response executeQuery(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web                                           +
            def tempList = []
            def insert = true

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (((rate.RATE_NUMBER == 1 &&
                        rate.BASE_CURRENCY?.toString()?.trim().equals("USD")) ||
                        (!rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
                                rate.RATE_NUMBER == 2 &&
                                rate.BASE_CURRENCY?.toString()?.trim().equals("PHP"))) &&
                        insert) {
                    if (!tempList.contains(rate)) {
                        tempList << rate
                        insert = false
                    }
                } else if (!insert) {
                    insert = true
                }
            }

            insert = true
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
                        rate.RATE_NUMBER == 2 &&
                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
                        insert) {
                    if (!tempList.contains(rate)) {
                        tempList << rate
                        insert = false
                    }
                }
            }
            insert = true
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
                        rate.RATE_NUMBER == 3 &&
                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE") &&
                        insert) {
                    if (!tempList.contains(rate)) {
                        tempList << rate
                        insert = false
                    }
                }
            }

            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim().equals(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim().equals("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/details")
    public Response executeQuery2(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web                                           +
            def tempList = []

            for (Map<String, Object> rate : ratesService.getDailyRates()) {

                // URR : USD to PHP
                if ((rate.RATE_NUMBER == 3) &&
                        (rate.CURRENCY_CODE.toString()?.trim().equals("USD")) &&
                        (rate.BASE_CURRENCY?.toString()?.trim().equals("PHP"))) {

                    tempList << rate

                }

                // URR : USD to PHP
                if ((rate.RATE_NUMBER == 2) &&
                        (rate.CURRENCY_CODE.toString()?.trim().equals("USD")) &&
                        (rate.BASE_CURRENCY?.toString()?.trim().equals("PHP"))) {

                    tempList << rate
                }

                if (!jsonParams.currency?.toUpperCase().equals("USD") && !jsonParams.currency?.toUpperCase().equals("PHP")) {

                    // URR : USD to PHP
                    if ((rate.RATE_NUMBER == 2) &&
                            (rate.CURRENCY_CODE.toString()?.trim().equals(jsonParams.currency?.toUpperCase())) &&
                            (rate.BASE_CURRENCY?.toString()?.trim().equals("USD"))) {

                        tempList << rate
                    }
                }

                // LC CASH SELL: USD to PHP
//                if ((rate.RATE_NUMBER == 17) &&
//                        (rate.CURRENCY_CODE.toString()?.trim().equals("USD")) &&
//                        (rate.BASE_CURRENCY?.toString()?.trim().equals("PHP"))) {
//
//                    tempList << rate
//                }

            }


            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim().equals(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim().equals("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/detailsEB")
    public Response executeQueryEB(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web                                           +
            def tempList = []

            for (Map<String, Object> rate : ratesService.getDailyRates()) {

                // URR : USD to PHP
                if ((rate.RATE_NUMBER == 3) &&
                        (rate.CURRENCY_CODE.toString()?.trim().equals("USD")) &&
                        (rate.BASE_CURRENCY?.toString()?.trim().equals("PHP"))) {

                    tempList << rate

                }

                // FCDU WITHDRAWAL : USD to PHP
                if ((rate.RATE_NUMBER == 13) &&
                        (rate.CURRENCY_CODE.toString()?.trim().equals("USD")) &&
                        (rate.BASE_CURRENCY?.toString()?.trim().equals("PHP"))) {

                    tempList << rate
                }

                if (!jsonParams.currency?.toUpperCase().equals("USD") && !jsonParams.currency?.toUpperCase().equals("PHP")) {

                    // BN BUY : currency to USD
                    if ((rate.RATE_NUMBER == 1) &&
                            (rate.CURRENCY_CODE.toString()?.trim().equals(jsonParams.currency?.toUpperCase())) &&
                            (rate.BASE_CURRENCY?.toString()?.trim().equals("USD"))) {

                        tempList << rate
                    }
                }

            }


            def ratesList = []

            // sorts the list of retrieve rates in order for the display
            tempList = tempList.sort { it.RATE_DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim().equals(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim().equals("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/savedRates")
    public Response getSavedRates(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {

            TradeService tradeService = null;

            if (jsonParams.get("tradeServiceId")) {
                tradeService = tradeServiceRepository.load(new TradeServiceId(jsonParams.get("tradeServiceId")));
            }

            def ratesList = [tradeService?.getSavedRates() ?: [:]]

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/regularSellRates")
    public Response getRegularSellRates(@Context UriInfo allUri) {
        println "regularSellRates"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web
            def tempList = []
            def insert = true

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (
                        rate.RATE_NUMBER == 2 &&
                                (!rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("USD"))
//                                ((!rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("USD"))
//                                ||
//                                ( !rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("PHP")))

                                //&&
                                //insert
                ) {
                    if (!tempList.contains(rate)) {
                        //println "rate for sell rate:"+rate
                        tempList << rate
                        insert = false
                    }
                } else {
                    //println "rate for sell rate in else:"+rate
                }
//                else if (!insert) {
//                    insert = true
//                }
            }

            insert = true
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
                        rate.RATE_NUMBER == 14 &&
                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
                        insert) {
                    if (!tempList.contains(rate)) {
                        //println "from usd to php sell rate:"+rate
                        tempList << rate
                        insert = false
                    }
                }
            }
            insert = true
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
                        rate.RATE_NUMBER == 3 &&
                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE") &&
                        insert) {
                    if (!tempList.contains(rate)) {
                        //println "from urr:"+ rate
                        tempList << rate
                        insert = false
                    }
                }
            }

            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim().equalsIgnoreCase(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim().equals("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/cashSellRates")
    public Response getCashSellRates(@Context UriInfo allUri) {
        println "cashSellRates"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web
            def tempList = []

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if ((rate.RATE_NUMBER == 2 && //angol
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        !rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") )
                ) {
                    if (!tempList.contains(rate)) {
                        tempList << rate
                    }
                }
            }

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if ((rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        rate.RATE_NUMBER == 17 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("PHP"))
                        || (rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase(jsonParams.currency) &&
                        rate.RATE_NUMBER == 17 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("USD") && !rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD"))) {
                    if (!tempList.contains(rate)) {
                        //println "from USD to PHP sell rate:"+ rate
                        tempList << rate
                    }
                }
            }
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        rate.RATE_NUMBER == 3 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("PHP") &&
                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE")) {
                    if (!tempList.contains(rate)) {
                        //println "from urr:"+ rate
                        tempList << rate
                    }
                }
            }

            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/regularSellRatesDM")
    public Response getRegularSellRatesDM(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web
            def tempList = []
            def insert = true

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (
                        rate.RATE_NUMBER == 2 &&
                                (!rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("USD"))
//                                ((!rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("USD"))
//                                ||
//                                ( !rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("PHP")))

                //&&
                //insert
                ) {
                    if (!tempList.contains(rate)) {
                        //println "rate for sell rate:"+rate
                        tempList << rate
                        insert = false
                    }
                } else {
                    //println "rate for sell rate in else:"+rate
                }
//                else if (!insert) {
//                    insert = true
//                }
            }

//            insert = true
//            for (Map<String, Object> rate : ratesService.getDailyRates()) {
//                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
//                        rate.RATE_NUMBER == 14 &&
//                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
//                        insert) {
//                    if (!tempList.contains(rate)) {
//                        //println "from usd to php sell rate:"+rate
//                        tempList << rate
//                        insert = false
//                    }
//                }
//            }
            insert = true
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
                        rate.RATE_NUMBER == 3 &&
                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE") &&
                        insert) {
                    if (!tempList.contains(rate)) {
                        //println "from urr:"+ rate
                        tempList << rate
                        insert = false
                    }
                }
            }

            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim().equalsIgnoreCase(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim().equals("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }
            println "ratesList:"+ratesList
            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/cashSellRatesDM")
    public Response getCashSellRatesDM(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web
            def tempList = []

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
//                if ((rate.RATE_NUMBER == 17 && //angol
                if ((rate.RATE_NUMBER == 2 && // marv: for DM Cash, rate should be Bank Note Sell always if EUR-USD
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        !rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") )
                ) {
                    if (!tempList.contains(rate)) {
                        tempList << rate
                    }
                }
            }

//            for (Map<String, Object> rate : ratesService.getDailyRates()) {
//                if ((rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") &&
//                        rate.RATE_NUMBER == 2 &&
//                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("PHP"))
//                        || (rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase(jsonParams.currency) &&
//                        rate.RATE_NUMBER == 2 &&
//                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("USD"))) {
//                    if (!tempList.contains(rate)) {
//                        //println "from USD to PHP sell rate:"+ rate
//                        tempList << rate
//                    }
//                }
//            }
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        rate.RATE_NUMBER == 3 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("PHP") &&
                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE")) {
                    if (!tempList.contains(rate)) {
                        //println "from urr:"+ rate
                        tempList << rate
                    }
                }
            }

            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }
            println "ratesList:"+ratesList
            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/getCashBuyRates")
    public Response getCashBuyRates(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web
            def tempList = []

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if ((rate.RATE_NUMBER == 1 && //angol
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        !rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") )
                ) {
                    if (!tempList.contains(rate)) {
                        tempList << rate
                    }
                }
            }

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        rate.RATE_NUMBER == 1 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("PHP")) {
                    if (!tempList.contains(rate)) {
                        //println "from USD to PHP sell rate:"+ rate
                        tempList << rate
                    }
                }
            }
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        rate.RATE_NUMBER == 3 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("PHP") &&
                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE")) {
                    if (!tempList.contains(rate)) {
                        //println "from urr:"+ rate
                        tempList << rate
                    }
                }
            }

            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/regularBuyRates")
    public Response getRegularBuyRates(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web
            def tempList = []
            def insert = true

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (
                        rate.RATE_NUMBER == 13 &&
                                (!rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("USD"))
//                                ((!rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("USD"))
//                                ||
//                                ( !rate.CURRENCY_CODE?.toString()?.trim().equals("USD") && rate.BASE_CURRENCY?.toString()?.trim().equals("PHP")))

                //&&
                //insert
                ) {
                    if (!tempList.contains(rate)) {
                        //println "rate for sell rate:"+rate
                        tempList << rate
                        insert = false
                    }
                } else {
                    //println "rate for sell rate in else:"+rate
                }
//                else if (!insert) {
//                    insert = true
//                }
            }

            insert = true
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
                        rate.RATE_NUMBER == 13 &&
                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
                        insert) {
                    if (!tempList.contains(rate)) {
                        //println "from usd to php sell rate:"+rate
                        tempList << rate
                        insert = false
                    }
                }
            }
            insert = true
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
                        rate.RATE_NUMBER == 3 &&
                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE") &&
                        insert) {
                    if (!tempList.contains(rate)) {
                        //println "from urr:"+ rate
                        tempList << rate
                        insert = false
                    }
                }
            }

            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim().equalsIgnoreCase(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim().equals("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/getAllUrr")
    public Response getAllUrr(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            def ratesList = ratesService.getAllUrr();

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/sellRates")
//    public Response getSellRates(@Context UriInfo allUri) {
//
//        Gson gson = new Gson();
//
//        String result="";
//        Map returnMap = new HashMap();
//
//        Map jsonParams = new HashMap();
//
//
//        // get all rest parameters
//        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
//
//        for(String key : mpAllQueParams.keySet()) {
//
//            // if there are multiple instances of the same param, we only use the first one
//            jsonParams.put(key, mpAllQueParams.getFirst(key));
//
//        }
//
//
//
//        try {
//            // copied from tfs-web
//            def tempList = []
//            def insert = true
//
//            for (Map<String, Object> rate : ratesService.getDailyRates()) {
//                if (((rate.RATE_NUMBER == 1 &&
//                        rate.BASE_CURRENCY?.toString()?.trim().equals("USD")) ||
//                        (!rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
//                                rate.RATE_NUMBER == 2 &&
//                                rate.BASE_CURRENCY?.toString()?.trim().equals("PHP"))) &&
//                        insert) {
//                    if (!tempList.contains(rate)) {
//                        tempList << rate
//                        insert = false
//                    }
//                } else if (!insert) {
//                    insert = true
//                }
//            }
//
//            insert = true
//            for (Map<String, Object> rate : ratesService.getDailyRates()) {
//                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
//                        rate.RATE_NUMBER == Integer.parseInt(jsonParams.get("rateNumber")) &&
//                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
//                        insert) {
//                    if (!tempList.contains(rate)) {
//                        tempList << rate
//                        insert = false
//                    }
//                }
//            }
//            insert = true
//            for (Map<String, Object> rate : ratesService.getDailyRates()) {
//                if (rate.CURRENCY_CODE?.toString()?.trim().equals("USD") &&
//                        rate.RATE_NUMBER == 3 &&
//                        rate.BASE_CURRENCY?.toString()?.trim().equals("PHP") &&
//                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE") &&
//                        insert) {
//                    if (!tempList.contains(rate)) {
//                        tempList << rate
//                        insert = false
//                    }
//                }
//            }
//
//            def ratesList = []
//
//            // sorts the list of retrieve rates in order for the display
////            tempList = tempList.sort{it.RATE_NUMBER}
//            tempList = tempList.sort{it.DEFINITION}
//
//            tempList.each {
//                // get all currencies of equal with the currency passed as parameter and USD
//                if (it.CURRENCY_CODE?.toString()?.trim().equals(jsonParams.currency?.toUpperCase()) ||
//                        it.CURRENCY_CODE?.toString()?.trim().equals("USD")) {
//
//                    ratesList << [
//                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
//                            description: it.RATE_DEFINITION?.toString()?.trim(),
//                            conversionRate: it.CONVERSION_RATE
//                    ]
//                }
//            }
//
//            def responseMap = [details: ratesList]
//
//            returnMap.put("status", "ok");
//            returnMap.put("response", responseMap);
//
//        }
//        catch(Exception e) {
//
//            Map errorDetails = new HashMap();
//
//            errorDetails.put("code", e.getMessage());
//            errorDetails.put("description", e.toString());
//
//            returnMap.put("status", "error");
//            returnMap.put("error", errorDetails);
//        }
//
//        // format return data as json
//        result = gson.toJson(returnMap);
//
//        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
//        return Response.status(200).entity(result).build();
//    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/convert")
//    public Response convert(@Context UriInfo allUri) {
//        Gson gson = new Gson();
//
//        String result="";
//        Map returnMap = new HashMap();
//
//        Map jsonParams = new HashMap();
//
//
//        // get all rest parameters
//        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
//
//
//        for(String key : mpAllQueParams.keySet()) {
//
//            // if there are multiple instances of the same param, we only use the first one
//            jsonParams.put(key, mpAllQueParams.getFirst(key));
//
//        }
//
//        CurrencyConverter currencyConverter = new CurrencyConverter();
//        def responseMap = [details: currencyConverter.convert("URR", "USD", new BigDecimal("1000"), "PHP")]
//
//        returnMap.put("status", "ok");
//        returnMap.put("response", responseMap);
//
//        // format return data as json
//        result = gson.toJson(returnMap);
//
//        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
//        return Response.status(200).entity(result).build();
//    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCorresChargesRates")
    public Response getCorresChargesRates(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            def tempList = []

            String billingCurrency = (String) jsonParams.get("billingCurrency")
            String settlementCurrency = (String) jsonParams.get("settlementCurrency")

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                switch (settlementCurrency) {
                    case "USD":
                        if (billingCurrency.equals("USD")) {
                            if (rate.RATE_NUMBER == 3 &&
                                    rate.CURRENCY_CODE.toString()?.trim()?.equals("USD") &&
                                    rate.BASE_CURRENCY.toString()?.trim()?.equals("PHP")) {

                                if (!(tempList.contains(rate))) {
                                    tempList << rate
                                }
                            }
                        } else {
                            if (!(billingCurrency.equals("PHP"))) {
                                if (rate.RATE_NUMBER == 2 &&
                                        rate.CURRENCY_CODE.toString()?.trim()?.equals(billingCurrency) &&
                                        rate.BASE_CURRENCY.toString()?.trim()?.equals("USD")) {

                                    if (!(tempList.contains(rate))) {
                                        tempList << rate
                                    }
                                }

                                if (rate.RATE_NUMBER == 3 &&
                                        rate.CURRENCY_CODE.toString()?.trim()?.equals("USD") &&
                                        rate.BASE_CURRENCY.toString()?.trim()?.equals("PHP")) {

                                    if (!(tempList.contains(rate))) {
                                        tempList << rate
                                    }
                                }
                            }
                        }


                        break

                    case "PHP":
                        if (billingCurrency.equals("USD")) {
                            if (rate.RATE_NUMBER == 2 &&
                                    rate.CURRENCY_CODE.toString()?.trim()?.equals(billingCurrency) &&
                                    rate.BASE_CURRENCY.toString()?.trim()?.equals("PHP")) {

                                if (!(tempList.contains(rate))) {
                                    tempList << rate
                                }
                            }

                            if (rate.RATE_NUMBER == 3 &&
                                    rate.CURRENCY_CODE.toString()?.trim()?.equals("USD") &&
                                    rate.BASE_CURRENCY.toString()?.trim()?.equals("PHP")) {

                                if (!(tempList.contains(rate))) {
                                    tempList << rate
                                }
                            }
                        } else if (!(billingCurrency.equals("PHP")) && !(billingCurrency.equals("USD"))) {
                            if (rate.RATE_NUMBER == 2 &&
                                    rate.CURRENCY_CODE.toString()?.trim()?.equals(billingCurrency) &&
                                    rate.BASE_CURRENCY.toString()?.trim()?.equals("USD")) {

                                if (!(tempList.contains(rate))) {
                                    tempList << rate
                                }
                            }

                            // 01/18/2014
//                            if (rate.RATE_NUMBER == 2 &&
//                                    rate.CURRENCY_CODE.toString()?.trim()?.equals("USD") &&
//                                    rate.BASE_CURRENCY.toString()?.trim()?.equals("PHP")) {
//
//                                if (!(tempList.contains(rate))) {
//                                    tempList << rate
//                                }
//                            }


                            if (rate.RATE_NUMBER == 3 &&
                                    rate.CURRENCY_CODE.toString()?.trim()?.equals("USD") &&
                                    rate.BASE_CURRENCY.toString()?.trim()?.equals("PHP")) {

                                if (!(tempList.contains(rate))) {
                                    tempList << rate
                                }
                            }
									
									
							if (rate.RATE_NUMBER == 14 &&
									rate.CURRENCY_CODE.toString()?.trim()?.equals("USD") &&
									rate.BASE_CURRENCY.toString()?.trim()?.equals("PHP")) {
								
								if (!(tempList.contains(rate))) {
									tempList << rate
								}
							}
									
                        }

                        break

                    default:
                        if (!(billingCurrency in ["USD", "PHP"])) {
                            if (rate.RATE_NUMBER == 2 &&
                                    rate.CURRENCY_CODE.toString()?.trim()?.equals(billingCurrency) &&
                                    rate.BASE_CURRENCY.toString()?.trim()?.equals("USD")) {

                                if (!(tempList.contains(rate))) {
                                    tempList << rate
                                }
                            }

                            if (rate.RATE_NUMBER == 3 &&
                                    rate.CURRENCY_CODE.toString()?.trim()?.equals("USD") &&
                                    rate.BASE_CURRENCY.toString()?.trim()?.equals("PHP")) {

                                if (!(tempList.contains(rate))) {
                                    tempList << rate
                                }

                            }
                        }

                        break
                }

            }

            tempList = tempList.sort { it.RATE_DEFINITION }

            def ratesList = []

            tempList.each {
                println it.RATE_NUMBER + " " + it.RATE_DEFINITION?.toString()?.trim() + " (" + it.RATE_DEFINITION?.toString()?.trim() + ")"
                ratesList << [
                    rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                    description: it.RATE_DEFINITION?.toString()?.trim(),
                    descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                    conversionRate: it.CONVERSION_RATE,
                    rateNumber: it.RATE_NUMBER
                ]
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
    @Path("/getServiceChargeRates")
    public Response getServiceChargeRates(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
			TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(jsonParams.get("tradeServiceId")))
            def rates
			if(!DocumentClass.INDEMNITY.equals(tradeService.getDocumentClass()))
			rates = paymentRepository.getServiceChargeRates(new TradeServiceId(jsonParams.get("tradeServiceId")))

            returnMap.put("status", "ok");
            returnMap.put("response", rates);

        } catch (Exception e) {

            Map errorDetails = new HashMap();

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/testPost")
    public Response testPost(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = gson.fromJson(postRequestBody, Map.class);

        try {
            returnMap.put("response", jsonParams.get("newCharges"))
            returnMap.put("status", "ok")
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

        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bankNoteSellRates")
    public Response getBankNoteSellRates(@Context UriInfo allUri) {
        println "bankNoteSell"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            // copied from tfs-web
            def tempList = []

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if ((rate.RATE_NUMBER == 2 && //angol
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        !rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") )
                ) {
                    if (!tempList.contains(rate)) {
                        tempList << rate
                    }
                }
            }

            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if ((rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        rate.RATE_NUMBER == 2 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("PHP"))
                        || (rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase(jsonParams.currency) &&
                        rate.RATE_NUMBER == 2 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("USD") && !rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD"))) {
                    if (!tempList.contains(rate)) {
                        //println "from USD to PHP sell rate:"+ rate
                        tempList << rate
                    }
                }
            }
            for (Map<String, Object> rate : ratesService.getDailyRates()) {
                if (rate.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD") &&
                        rate.RATE_NUMBER == 3 &&
                        rate.BASE_CURRENCY?.toString()?.trim()?.equalsIgnoreCase("PHP") &&
                        rate.RATE_DEFINITION?.toString()?.contains("BOOKING RATE")) {
                    if (!tempList.contains(rate)) {
                        //println "from urr:"+ rate
                        tempList << rate
                    }
                }
            }

            def ratesList = []

            // sorts the list of retrieve rates in order for the display
//            tempList = tempList.sort{it.RATE_NUMBER}
            tempList = tempList.sort { it.DEFINITION }

            tempList.each {
                // get all currencies of equal with the currency passed as parameter and USD
                if (it.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase(jsonParams.currency?.toUpperCase()) ||
                        it.CURRENCY_CODE?.toString()?.trim()?.equalsIgnoreCase("USD")) {

                    ratesList << [
                            rates: it.CURRENCY_CODE?.toString()?.trim() + "-" + it.BASE_CURRENCY?.toString()?.trim(),
                            description: it.RATE_DEFINITION?.toString()?.trim(),
                            descriptionLbp: it.RATE_DEFINITION_LBP?.toString()?.trim(),
                            conversionRate: it.CONVERSION_RATE
                    ]
                }
            }

            def responseMap = [details: ratesList]

            returnMap.put("status", "ok");
            returnMap.put("response", responseMap);

        }
        catch (Exception e) {

            Map errorDetails = new HashMap();

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
	@Path("/getAllConversionRateByRateNumber")
	public Response getConversionRateByType(@Context UriInfo allUri) {
		Gson gson = new Gson();
		String result = "";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap();
		
		// get all rest parameters
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
		for (String key : mpAllQueParams.keySet()) {
			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key));
		}
	
		List<Map<String,Object>> conversionRatesList = new ArrayList<Map<String,Object>>()
		
		try {
			conversionRatesList = ratesService.getAllConversionRateByRateNumber(
				new SimpleDateFormat("MM/dd/yyyy").parse(jsonParams.get("onlineReportDate").toString()),
				Integer.parseInt(jsonParams.get("rateType")))

			returnMap.put("status", "ok");
			returnMap.put("response", conversionRatesList);
		} catch(ParseException pex){
			println "=======getAllConversionRateByRateNumber========="
			pex.printStackTrace()
			println "PARSE EXCEPTION: "+pex.toString()
			println "================================================"
			returnMap.put("status", "error");
			returnMap.put("response", conversionRatesList);
		}catch (Exception e) {
			println "=======getAllConversionRateByRateNumber========="
			e.printStackTrace()
			println "EXCEPTION: "+e.toString()
			println "================================================"
			returnMap.put("status", "error");
			returnMap.put("response", conversionRatesList);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
	}

		@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAllConversionRateByRateNumberHistorical")
	public Response getConversionRateByTypeHistorical(@Context UriInfo allUri) {
		Gson gson = new Gson();
		String result = "";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap();
		
		// get all rest parameters
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
		for (String key : mpAllQueParams.keySet()) {
			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key));
		}
		
		List<Map<String,Object>> conversionRatesList = new ArrayList<Map<String,Object>>()
			
		try {
			conversionRatesList = ratesService.getAllConversionRateByRateNumberHistorical(
				new SimpleDateFormat("MM/dd/yyyy").parse(jsonParams.get("onlineReportDate").toString()),
				Integer.parseInt(jsonParams.get("rateType")))
			
			returnMap.put("status", "ok");
			returnMap.put("response", conversionRatesList);
		} catch(ParseException pex){
			println "=======getAllConversionRateByRateNumberHistorical========="
			pex.printStackTrace()
			println "PARSE EXCEPTION: "+pex.toString()
			println "=========================================================="
			returnMap.put("status", "error");
			returnMap.put("response", conversionRatesList);
		}catch (Exception e) {
			println "=======getAllConversionRateByRateNumberHistorical========="
			e.printStackTrace()
			println "EXCEPTION: "+e.toString()
			println "=========================================================="
			returnMap.put("status", "error");
			returnMap.put("response", conversionRatesList);
		}
		
		// format return data as json
		result = gson.toJson(returnMap);
		
		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getUrrToday")
    public Response getUrrToday(@Context UriInfo allUri) {
        Gson gson = new Gson();
        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));
        }

        try {
//            List<Map<String,Object>> conversionRatesList = ratesService.getUrrConversionRateToday()
            BigDecimal urr  = ratesService.getUrrConversionRateToday()

            returnMap.put("status", "ok");
            returnMap.put("response", ['urr':urr]);
        } catch (Exception e) {
            Map errorDetails = new HashMap();

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
	@Path("/getAllCurrency")
	public Response getAllCurrency(@Context UriInfo allUri) {
		Gson gson = new Gson();
		String result = "";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap();

		try {
			List<Map<String,Object>> currencyList  = ratesService.getAllCurrency();

			returnMap.put("status", "ok");
			returnMap.put("currencyList", currencyList);
		} catch (Exception e) {
			e.printStackTrace();
			Map errorDetails = new HashMap();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		result = gson.toJson(returnMap);

		return Response.status(200).entity(result).build();
	}
}
