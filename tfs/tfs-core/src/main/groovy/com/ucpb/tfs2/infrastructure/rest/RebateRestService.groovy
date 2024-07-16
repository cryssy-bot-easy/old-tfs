package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson

import com.ucpb.tfs.domain.product.RebateRepository
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

@Path("/rebate")
@Component
class RebateRestService {

    @Autowired
    RebateRepository rebateRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

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

        try {
            List rebateList = rebateRepository.getAllRebateBy(jsonParams.get("corresBankCode") ?: null, jsonParams.get("unitCode") ?: null)

            returnMap.put("status", "ok");
            returnMap.put("details", rebateList)

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
