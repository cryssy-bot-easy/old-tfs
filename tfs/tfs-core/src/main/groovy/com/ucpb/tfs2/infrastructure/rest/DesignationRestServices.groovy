package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.security.DesignationRepository
import com.ucpb.tfs.domain.security.PositionCode
import com.ucpb.tfs.domain.security.PositionRepository
import com.ucpb.tfs2.application.service.DesignationService
import com.ucpb.tfs2.application.service.PositionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.*
import javax.ws.rs.core.*

@Path("/designation")
@Component
class DesignationRestServices {

    @Autowired
    DesignationRepository designationRepository

    @Autowired
    DesignationService designationService

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allDesignations")
    public Response getAllPositions(@Context UriInfo allUri) {

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

            List allDesignations = designationRepository.getAllDesignationMatching(null);

            returnMap.put("status", "ok");
            returnMap.put("result", allDesignations)

        } catch(Exception e) {

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
    @Path("/getDesignation")
    public Response getPosition(@Context UriInfo allUri) {

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
            println "jsonParams " + jsonParams
            def designation = designationRepository.load(new Long((String) jsonParams.get("id")));
            println designation
            returnMap.put("status", "ok");
            returnMap.put("result", designation)

        } catch(Exception e) {

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
    @Path("/save")
    public Response savePosition(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);
            println "formDetails :: " + formDetails
            designationService.saveDesignation(formDetails)

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
    @Path("/search")
    public Response searchPositions(@Context UriInfo allUri) {

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

            List positions = designationService.searchDesignation(jsonParams);

            returnMap.put("status", "ok");
            returnMap.put("result", positions)

        } catch(Exception e) {

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
}
