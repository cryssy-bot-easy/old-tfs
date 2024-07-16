package com.ucpb.tfs2.infrastructure.rest

import java.util.Map;

import com.google.gson.Gson
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.product.ExportBillsRepository
import com.ucpb.tfs.domain.product.TradeProductRepository
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number:
	SCR/ER Description: Redmine #4118 - If with outstanding EBC is tagged as Yes, the drop down lists of EBC document numbers
	are not complete. Example: Document number 909-11-307-17-00004-2 is not included in the list but it should be part of the
	drop down list since this is an approved EBC Nego and it is still outstanding.
	[Revised by:] John Patrick C. Bautista
	[Date Deployed:] 06/16/2017
	Program [Revision] Details: Added new method to query from Export Bills without the BP Currency restriction.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: ExportBillsRestService
 */

@Path("/exportbills")
@Component
class ExportBillsRestServices {

    @Autowired
    ExportBillsRepository exportBillsRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

	@Autowired
	TradeServiceRepository tradeServiceRepository;
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search/details")
    public Response searchExportBills(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }
	
		println "EXPORT PARAMS "+jsonParams
		
        try {
            List exportBillsList = exportBillsRepository.getAllExportBills(
                    jsonParams.get("documentNumber") ? new DocumentNumber(jsonParams.get("documentNumber")) : null,
                    jsonParams.get("cifName") ?: null,
                    jsonParams.get("corresBankCode") ?: null,
                    jsonParams.get("transaction") ?: null,
                    jsonParams.get("transactionType") ?: null,
                    jsonParams.get("status") ?: null,
               		jsonParams.get("amountFrom") ? new BigDecimal(jsonParams.get("amountFrom").replaceAll(",","")) : null,
       				jsonParams.get("amountTo") ? new BigDecimal(jsonParams.get("amountTo").replaceAll(",","")) : null,
					jsonParams.get("currency") ?: null,
					jsonParams.get("unitCode") ?: null,
					jsonParams.get("unitcode") ?: null
            )

            def returnList = []
	
            exportBillsList.each {
                returnList << tradeProductRepository.loadToMapExportBills(it.getAt("documentNumber"))
            }

            returnMap.put("status", "ok");
            returnMap.put("details", returnList)

        } catch (Exception e) {
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search/byCifNumber")
    public Response searchExportBillsByCifNumber(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            List exportBillsList = exportBillsRepository.getAllExportBillsByCifNumber((String) jsonParams.get("cifNumber"), (String) jsonParams.get("exportBillType"))

            def returnList = []

            exportBillsList.each {
                returnList << tradeProductRepository.loadToMapExportBills(it.getAt("documentNumber"))
            }

            returnMap.put("status", "ok");
            returnMap.put("details", returnList)

        } catch (Exception e) {
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/retrieveAllExportBills")
	public Response retrieveAllExportBills(@Context UriInfo allUri) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {

			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		try {
//			List exportBillsList = exportBillsRepository.getAllExportBillsByCifNumber((String) jsonParams.get("cifNumber"), (String) jsonParams.get("exportBillType"))
			// 01242017 - Redmine 4118: Changed method being called to remove restriction on BP Currency
			List exportBillsList = exportBillsRepository.getAllExportBillsByCifNumberNoRestrictionOnBpCurrency((String) jsonParams.get("cifNumber"), (String) jsonParams.get("exportBillType"))

			def returnList = []
			
			//	12092016 EBP Extraction - Case 2 - checking of oustanding amount

			exportBillsList.each {
				def outstandingAmt = it.getAt("outstandingAmount")
				if ( outstandingAmt > 0 ){
					returnList << tradeProductRepository.loadToMapExportBills(it.getAt("documentNumber"))
				} else {
					println "OUTSTANDING AMOUNT (less than zero)>>>>>>>>>> " + outstandingAmt
				}
			}

			returnMap.put("status", "ok");
			returnMap.put("details", returnList)

		} catch (Exception e) {
			Map errorDetails = new HashMap();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		result = gson.toJson(returnMap);

		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/retrieveAllExportBillsNoBPCurrencyRestriction")
	public Response retrieveAllExportBillsNoBPCurrencyRestriction(@Context UriInfo allUri) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {

			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		try {
			// 01242017 - Redmine 4118: Changed method being called to remove restriction on BP Currency
			List exportBillsList = exportBillsRepository.getAllExportBillsByCifNumberNoRestrictionOnBpCurrency((String) jsonParams.get("cifNumber"), (String) jsonParams.get("exportBillType"))

			def returnList = []
			
			//	12092016 EBP Extraction - Case 2 - checking of oustanding amount

			exportBillsList.each {
				def outstandingAmt = it.getAt("outstandingAmount")
				if ( outstandingAmt > 0 ){
					returnList << tradeProductRepository.loadToMapExportBills(it.getAt("documentNumber"))
				} else {
					println "OUTSTANDING AMOUNT (less than zero)>>>>>>>>>> " + outstandingAmt
				}
			}

			returnMap.put("status", "ok");
			returnMap.put("details", returnList)

		} catch (Exception e) {
			Map errorDetails = new HashMap();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		result = gson.toJson(returnMap);

		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getEbcDetailsByNegotiationNumber")
	public Response getEbcDetailsByNegotiationNumber(@Context UriInfo allUri){
		
		Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();
		
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
		
		for(String key : mpAllQueParams.keySet()) {
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		String tradeProductNumber = jsonParams.get("negotiationNumber")
		
		try {
			Map ebcTradeServiceDetails = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(tradeProductNumber), ServiceType.NEGOTIATION, DocumentType.FOREIGN, DocumentClass.BC);
			
			Map ebcDetails = ebcTradeServiceDetails;
			
			returnMap.put("status", "ok");
			returnMap.put("details", ebcDetails)

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
