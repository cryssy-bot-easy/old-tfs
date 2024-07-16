package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.ucpb.tfs.application.service.ChargesService
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.product.ExportAdvising
import com.ucpb.tfs.domain.product.ExportAdvisingRepository
import com.ucpb.tfs.domain.product.TradeProductRepository
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.domain.service.TradeServiceId
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs2.application.service.TradeServiceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.*
import java.text.SimpleDateFormat

@Path("/exportAdvising")
@Component
class ExportAdvisingRestServices {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ExportAdvisingRepository exportAdvisingRepository;

    @Autowired
    TradeServiceService tradeServiceService

    @Autowired
    ChargesService chargesService;

    @Autowired
    TradeProductRepository tradeProductRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public Response exportAdvisingSearch(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            List exportAdvisingList = exportAdvisingRepository.getAllExportAdvising(jsonParams.get("documentNumber") != "" ? new DocumentNumber(jsonParams.get("documentNumber")) : null,
                    jsonParams.get("lcNumber") != "" ? new DocumentNumber(jsonParams.get("lcNumber")) : null,
                    jsonParams.get("exporterName") != "" ? jsonParams.get("exporterName") : null,
                    jsonParams.get("processDate") != "" ? new Date(jsonParams.get("processDate")) : null,
					jsonParams.get("unitCode") ?: null);

            returnMap.put("status", "ok");
            returnMap.put("details", exportAdvisingList)

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

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/paymentSearch")
//    public Response exportAdvisingPaymentSearch(@Context UriInfo allUri) {
//        Gson gson = new Gson();
//
//        String result = "";
//        Map returnMap = new HashMap();
//        Map jsonParams = new HashMap<String, String>();
//
//        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
//
//        for (String key : mpAllQueParams.keySet()) {
//
//            // if there are multiple instances of the same param, we only use the first one
//            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
//        }
//
//        try {
//
//            List exportAdvisingList = tradeServiceRepository.getAllExportAdvising(jsonParams.get("documentNumber") ? new DocumentNumber(jsonParams.get("documentNumber")) : null,
//                    jsonParams.get("lcNumber") ? new DocumentNumber(jsonParams.get("lcNumber")) : null,
//                    jsonParams.get("exporterName"));
//
//            returnMap.put("status", "ok");
//            returnMap.put("details", exportAdvisingList)
//
//        } catch (Exception e) {
//            Map errorDetails = new HashMap();
//
//            errorDetails.put("code", e.getMessage());
//            errorDetails.put("description", e.toString());
//
//            returnMap.put("status", "error");
//            returnMap.put("error", errorDetails);
//        }
//
//        result = gson.toJson(returnMap);
//
//        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
//        return Response.status(200).entity(result).build();
//    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payTransaction")
    public Response payExportAdvisingTransaction(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = gson.fromJson(postRequestBody, Map.class);

        try {

            TradeService tradeService = null

            String tradeServiceId = jsonParams.tradeServiceId

            String userName = jsonParams.username
            String unitCode = jsonParams.unitcode

            tradeService = tradeServiceRepository.load(new TradeServiceId(tradeServiceId))

            tradeService.getDetails().put("username", userName)
            tradeService.getDetails().put("unitcode", unitCode)


            TradeService newTradeService = null

            Map<String, Object> details = tradeService.getDetails();

            details.put("approvedOnce", true)

            newTradeService = tradeServiceService.duplicateTradeService(
                                                                   tradeService,
                                                                   newTradeService,
                                                                   tradeService.getDocumentClass(),
                                                                   tradeService.getDocumentType(),
                                                                   tradeService.getDocumentSubType1(),
                                                                   tradeService.getDocumentSubType2(),
                                                                   tradeService.getServiceType(),
                                                                   details)


            // temporarily commented out since it does not do anything up until charges for exports are implemented
            //chargesService.applyChargesNewStyle(newTradeService, newTradeService.getDetails())

            // get a full map of the trade service we just saved
            Map savedTradeService = tradeServiceRepository.getTradeServiceBy(newTradeService.getTradeServiceId())


            returnMap.put("details", savedTradeService)
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
    @Path("/autoComplete")
    public Response autoCompleteExportAdvising(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

//            List exportAdvisingList = exportAdvisingRepository.autoCompleteExportAdvising(jsonParams.get("documentNumber") ?
//                                                                                          "%" + jsonParams.get("documentNumber") + "%" :
//                                                                                          "");

            List exportAdvisingList = exportAdvisingRepository.autoCompleteExportAdvising(jsonParams.get("cifNumber"),
                    jsonParams.get("documentNumber") ?
                "%" + jsonParams.get("documentNumber") + "%" :
                "");

            returnMap.put("status", "ok");
            returnMap.put("details", exportAdvisingList)

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
    @Path("/getExportAdvising")
    public Response getExportAdvising(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
			
            ExportAdvising exportAdvising = (ExportAdvising) tradeProductRepository.load(new DocumentNumber((String) jsonParams.get("exlcAdviseNumber")));

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

            def details = [:]

            details.put("adviseNumber", exportAdvising.getDocumentNumber().toString());
            details.put("lcNumber", exportAdvising.getLcNumber().toString());
            details.put("lcIssueDate", sdf.format(exportAdvising.getLcIssueDate()));
            details.put("lcType", exportAdvising.getLcType());
            details.put("lcTenor", exportAdvising.getLcTenor().toString());
            details.put("usanceTerm", exportAdvising.getUsanceTerm().toString());
            details.put("lcCurrency", exportAdvising.getLcCurrency().toString());
            details.put("lcAmount", exportAdvising.getLcAmount().toString());
            details.put("lcExpiryDate", sdf.format(exportAdvising.getLcExpiryDate()));
            details.put("issuingBankCode", exportAdvising.getIssuingBank());
			details.put("issuingBankName" , exportAdvising.getIssuingBankName());
			details.put("issuingBankAddress", exportAdvising.getIssuingBankAddress());
            details.put("reimbursingBankCode", exportAdvising.getReimbursingBank());
            details.put("buyerName", exportAdvising.getImporterName());
            details.put("buyerAddress", exportAdvising.getImporterAddress());
            details.put("sellerName", exportAdvising.getExporterName());
            details.put("cbCode", exportAdvising.getExporterCbCode());
			
			println "inside rest" + details
			
			returnMap.put("status", "ok");
            returnMap.put("details", details)

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
}