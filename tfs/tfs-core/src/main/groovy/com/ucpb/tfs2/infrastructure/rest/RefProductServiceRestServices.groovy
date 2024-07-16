package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.reference.ProductId
import com.ucpb.tfs.domain.reference.ProductServiceReferenceRepository
import com.ucpb.tfs.domain.service.enumTypes.ServiceType
import com.ucpb.tfs2.application.service.RefProductServiceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.*
import javax.ws.rs.core.*
/**
 * Created with IntelliJ IDEA.
 * User: IPC JON
 * Date: 1/25/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/refProductService")
@Component
public class RefProductServiceRestServices {

    @Autowired
    ProductServiceReferenceRepository refProductServiceRepository

    @Autowired
    RefProductServiceService refProductServiceService

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchProductService")
    public Response searchProductService(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            String productId = jsonParams.get("productId");
            String serviceType  = jsonParams.get("serviceType");

            List results = refProductServiceRepository.getRequestsMatching(productId, serviceType);

            if (results.size() == 0) {
                returnMap.put("status", "ok");
                returnMap.put("details", "not found");
            } else {
                returnMap.put("status", "ok");
                returnMap.put("details", results);
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


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/productServiceDetails/{productServiceId}")
    public Response getRefProductServiceDetails(@PathParam("productServiceId") String productServiceId) {

        Gson gson = new Gson();
        Map returnMap = new HashMap();

        String result="";

        try {

            Map refProductService = refProductServiceRepository.getProductServiceById(new Long (productServiceId));

            returnMap.put("status", "ok");
            returnMap.put("details", refProductService);

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
    @Path("/productServiceDetails/save")
    public Response saveProductServiceDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            refProductServiceService.saveRefProductServiceDetail(formDetails);

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