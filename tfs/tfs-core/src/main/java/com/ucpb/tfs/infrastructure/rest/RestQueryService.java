package com.ucpb.tfs.infrastructure.rest;

import com.google.gson.Gson;
import com.incuventure.cqrs.infrastructure.StandardAPICallDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.Map;

@Path("/query")
@Component
public class RestQueryService {

    @Autowired
    StandardAPICallDispatcher standardAPICallDispatcher;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{queryName}")
    public Response executeQuery(@PathParam("queryName") String query, @Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            Map tMap = new HashMap();
            Object returnVal = standardAPICallDispatcher.dispatch(query, jsonParams);
            returnMap.put("status", "ok");
            returnMap.put("response", returnVal);

        }
        catch(Exception e) {

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
