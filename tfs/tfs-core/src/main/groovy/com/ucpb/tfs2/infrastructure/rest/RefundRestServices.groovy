package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.service.TradeServiceId
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs2.application.service.RefundService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*
/**
 * Created with IntelliJ IDEA.
 * User: IPCVal
 * Date: 7/29/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/refund")
@Component
public class RefundRestServices {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    RefundService refundService

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getProductRefundDetails")
    public Response getProductRefundDetails(@Context UriInfo allUri) {

        Gson gson = new Gson();
        String result = '';
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId = new TradeServiceId((String)jsonParams.get("tradeServiceId"));

            Map tradeServiceMap = tradeServiceRepository.getTradeServiceBy(tradeServiceId);

            println "\n################################## tradeServiceMap = ${tradeServiceMap}\n"

            Set productRefundDetails = null
            if (tradeServiceMap != null) {
                productRefundDetails = tradeServiceMap['productRefundDetails'];
            }

            if (productRefundDetails != null) {
                returnMap.put("status", "ok");
                returnMap.put("details", productRefundDetails);
            } else {
                returnMap.put("status", "ok");
                returnMap.put("details", "not found");
            }

        } catch(Exception e) {

            e.printStackTrace();

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
    @Path("/saveProductRefundDetails")
    public Response saveProductRefundDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();
        String result = '';
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            TradeServiceId tradeServiceId = new TradeServiceId((String)formDetails.get("tradeServiceId"));
            List<Map<String, Object>> newProductRefundListMap = (List<Map<String, Object>>) formDetails.get("newProductRefund");

            refundService.saveProductRefundDetails(tradeServiceId, newProductRefundListMap);

            returnMap.put("details", "ok")
            returnMap.put("status", "ok")

        } catch(Exception e) {

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleteProductRefundDetails")
    public Response deleteProductRefundDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();
        String result = '';
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            TradeServiceId tradeServiceId = new TradeServiceId((String)formDetails.get("tradeServiceId"));

            refundService.deleteProductRefundDetails(tradeServiceId);

            returnMap.put("details", "ok")
            returnMap.put("status", "ok")

        } catch(Exception e) {

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
    @Path("/getChargesRefundDetails")
    public Response getChargesRefundDetails(@Context UriInfo allUri) {

        Gson gson = new Gson();
        String result = '';
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeServiceId tradeServiceId = new TradeServiceId((String)jsonParams.get("tradeServiceId"));

            Map tradeServiceMap = tradeServiceRepository.getTradeServiceBy(tradeServiceId);

            println "\n################################## tradeServiceMap = ${tradeServiceMap}\n"

            Set serviceCharges = null
            if (tradeServiceMap != null) {
                serviceCharges = tradeServiceMap['serviceCharges'];
            }

            if (serviceCharges != null) {
                returnMap.put("status", "ok");
                returnMap.put("details", serviceCharges);
            } else {
                returnMap.put("status", "ok");
                returnMap.put("details", "not found");
            }

        } catch(Exception e) {

            e.printStackTrace();

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
    @Path("/saveChargesRefundDetails")
    public Response saveChargesRefundDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();
        String result = '';
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            TradeServiceId tradeServiceId = new TradeServiceId((String)formDetails.get("tradeServiceId"));
            List<Map<String, Object>> newChargesRefundListMap = (List<Map<String, Object>>) formDetails.get("newCharges");

            refundService.saveChargesRefundDetails(tradeServiceId, newChargesRefundListMap);

            returnMap.put("details", "ok")
            returnMap.put("status", "ok")

        } catch(Exception e) {

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleteChargesRefundDetails")
    public Response deleteChargesRefundDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();
        String result = '';
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            TradeServiceId tradeServiceId = new TradeServiceId((String)formDetails.get("tradeServiceId"));

            refundService.deleteServiceCharges(tradeServiceId);

            returnMap.put("details", "ok")
            returnMap.put("status", "ok")

        } catch(Exception e) {

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
}
