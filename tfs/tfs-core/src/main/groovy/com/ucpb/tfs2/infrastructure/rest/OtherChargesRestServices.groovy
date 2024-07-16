package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.ucpb.tfs.domain.service.TradeServiceId
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.domain.product.TradeProduct
import com.ucpb.tfs.domain.product.TradeProductRepository
import com.ucpb.tfs2.application.service.OtherChargesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * Created with IntelliJ IDEA.
 * User: IPCVal
 * Date: 8/2/13
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/otherCharges")
@Component
public class OtherChargesRestServices {

	/**
		PROLOGUE:
		(revision)
		SCR/ER Number: ER# 20160620-066 
		SCR/ER Description: Discrepancy under gl code 561201680000 which is not included in the TF Alloc and TFS exception report of May 2016 but reflected in GL RM4105
		[Revised by:] Lymuel Arrome Saul
		[Date revised:] 06/16/2016
		Program [Revision] Details: Removed the condition in the select query which replaces the dash with blanks and exact match only when searching for a document number.
		Date deployment: 6/17/2016
		Member Type: GROOVY
		Project: CORE
		Project Name: OtherChargesRestServices.groovy
	*/
	
    @Autowired
    OtherChargesService otherChargesService;

    @Autowired
    TradeServiceRepository tradeServiceRepository;
    
    @Autowired
    TradeProductRepository tradeProductRepository;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/saveCharge")
    public Response saveCharge(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();
        String result = '';
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            formDetails.each {
                println it
            }
            println "formDetails:"+formDetails
            TradeServiceId tradeServiceId = new TradeServiceId((String)formDetails.get("tradeServiceId"));
            String transactionType = (String)formDetails.get("transactionType");
            String chargeType = (String)formDetails.get("chargeType");
            BigDecimal amount = new BigDecimal(((String)formDetails.get("amount")).replaceAll(",",''));
            Currency currency = Currency.getInstance((String)formDetails.get("currency"));
            String cwtFlag = (String)formDetails.get("cwtFlag");

            otherChargesService.saveDetail(tradeServiceId, transactionType, chargeType, amount, currency, cwtFlag);

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
    @Path("/deleteCharge")
    public Response deleteCharge(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();
        String result = '';
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            TradeServiceId tradeServiceId = new TradeServiceId((String)formDetails.get("tradeServiceId"));
            String id = (String)formDetails.get("id");

            otherChargesService.deleteDetail(tradeServiceId, id);

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
    @Path("/getOtherChargeDetails")
    public Response getOtherChargeDetails(@Context UriInfo allUri) {

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

            Set otherChargesDetails = null
            if (tradeServiceMap != null) {
                otherChargesDetails = tradeServiceMap['otherChargesDetails'];
            }

            if (otherChargesDetails != null) {
                returnMap.put("status", "ok");
                returnMap.put("details", otherChargesDetails);
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
	@Path("/getCifDetailsFromDocumentNumber")
	public Response getCifDetailsFromDocumentNumber(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();
		String result = '';
		Map returnMap = new HashMap();

		try {

			Map formDetails = gson.fromJson(postRequestBody, Map.class);
			println "formDetails: "+formDetails.toString();
//			String documentNumber = ((String)formDetails.get("documentNumber"))?.replaceAll("-", "")
			String documentNumber = ((String)formDetails.get("documentNumber"))

			Map <String, Object> resultMap = tradeProductRepository.load(documentNumber)
			
			returnMap.put("status", "ok");
			returnMap.put("resultMap", resultMap);

			if(resultMap.isEmpty()){
				resultMap.put("status", "error");
			} else {
				resultMap.put("status", "ok");
			}
			
		} catch(Exception e) {
		
			Map errorDetails = new HashMap();

			e.printStackTrace();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("resultMap", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		return Response.status(200).entity(result).build();
	}
}
