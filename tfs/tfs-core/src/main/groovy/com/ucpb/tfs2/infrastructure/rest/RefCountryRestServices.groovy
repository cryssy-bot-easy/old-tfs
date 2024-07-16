package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.sysparams.RefCountryRepository
import com.ucpb.tfs2.application.service.RefCountryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.*
import javax.ws.rs.core.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
/**
 * Created with IntelliJ IDEA.
 * User: IPCVal
 * Date: 1/22/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/refCountry")  // root url/refCustomer
@Component
public class RefCountryRestServices {

    @Autowired
    RefCountryRepository refCountryRepository

    @Autowired
    RefCountryService refCountryService

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchCountry")
    public Response searchCountry(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        // Map formDetails = gson.fromJson(postRequestBody, Map.class);

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            String countryCode = jsonParams.get("countryCode")
            String countryName = jsonParams.get("countryName")
            String countryISO  = jsonParams.get("countryISO")

            println countryCode+countryName+countryISO
            List results = refCountryRepository.getRequestsMatching(countryCode, countryName, countryISO);

            if (results.size() == 0) {
                returnMap.put("status", "ok");
                returnMap.put("details", "not found");

                println(results.size());


            } else {
                returnMap.put("status", "ok");
                returnMap.put("details", results);
                println(results.size());
            }

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
    @Path("/countryDetails/{countryCode}")
    public Response getRefCountryDetails(@PathParam("countryCode") String countryCode) {

        Gson gson = new Gson();
        Map returnMap = new HashMap();

        String result="";

        try {

            Map refCountry = refCountryRepository.getCountryByCode(new String(countryCode));
            returnMap.put("status", "ok");
            returnMap.put("details", refCountry);

        } catch(Exception e) {

            e.printStackTrace()

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
    @Path("/countryDetails/save")
    public Response saveCountryDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);
            String refCountryCode = refCountryService.saveRefCountryDetail(formDetails);
            returnMap.put("status", "ok")
            returnMap.put("details", refCountryCode)

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
