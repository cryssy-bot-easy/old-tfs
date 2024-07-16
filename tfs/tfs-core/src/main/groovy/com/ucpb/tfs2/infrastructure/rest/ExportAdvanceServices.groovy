package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.product.ExportAdvancePayment

import com.ucpb.tfs.domain.product.ExportAdvanceRepository
import com.ucpb.tfs.domain.product.TradeProductRepository
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

@Path("/exportadvance")
@Component
class ExportAdvanceServices {

    @Autowired
    ExportAdvanceRepository exportAdvanceRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payment/search")
    public Response ExportAdvancePaymentSearch(@Context UriInfo allUri) {
		println "going here"
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

            List exportAdvancePaymentList = exportAdvanceRepository.getAllExportAdvancePayments(jsonParams.get("cifName"),
                    jsonParams.get("documentNumber") ? new DocumentNumber(jsonParams.get("documentNumber")) : null,
                    jsonParams.get("currency") ? Currency.getInstance(jsonParams.get("currency")) : null,
                    jsonParams.get("amountFrom") ? new BigDecimal(jsonParams.get("amountFrom").replaceAll(",","")) : null,
                    jsonParams.get("amountTo") ? new BigDecimal(jsonParams.get("amountTo").replaceAll(",","")) : null,
					jsonParams.get("unitCode") ?: null,
					jsonParams.get("unitcode") ?: null);

            returnMap.put("status", "ok");
            returnMap.put("details", exportAdvancePaymentList)

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
    @Path("/payment/details")
    public Response ExportAdvancePaymentDetails(@Context UriInfo allUri) {
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
            ExportAdvancePayment exportAdvancePayment = exportAdvanceRepository.load(new DocumentNumber(jsonParams.get("documentNumber")))

            returnMap.put("status", "ok");
            returnMap.put("details", exportAdvancePayment)

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
