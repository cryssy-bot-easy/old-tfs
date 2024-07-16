package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.ucpb.tfs.domain.service.ChargesParameter
import com.ucpb.tfs.domain.service.ChargesParameterRepository
import com.ucpb.tfs2.application.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/**
 * PROLOGUE
 * Description: Rest services for Charges Parameter
 * Revised by: Cedrick C. Nungay
 * Date deployment: 03/20/2018
 * Member Type: groovy
 * Project: CORE
 * Project Name: ChargesParameterRestServices.groovy 
*/

@Path("/chargesParameter")
@Component
class ChargesParameterRestServices {

    @Autowired
    ChargesParameterRepository chargesParameterRepository

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getChargesParameter")
    public Response getChargesParameter(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
			println 'jsonParams'
			println jsonParams
	        returnMap.put("result", chargesParameterRepository.getParameters(jsonParams));
	        returnMap.put("status", "ok");
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

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
}
