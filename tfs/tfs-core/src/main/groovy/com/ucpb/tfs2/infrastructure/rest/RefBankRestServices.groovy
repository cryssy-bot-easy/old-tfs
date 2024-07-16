package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.sysparams.RefBankRepository
import com.ucpb.tfs2.application.service.RefBankService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.*
import javax.ws.rs.core.*
/**
 * Created with IntelliJ IDEA.
 * User: IPCVal
 * Date: 1/22/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/refBank")
@Component
public class RefBankRestServices {

    @Autowired
    RefBankRepository refBankRepository

    @Autowired
    RefBankService refBankService
		
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchBank")
    public Response searchBank(@Context UriInfo allUri) {

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

            String bic             = jsonParams.get("bic")
            String branchCode      = jsonParams.get("branchCode")
            String institutionName = jsonParams.get("institutionName")
            String depositoryFlag  = jsonParams.get("depositoryFlag")

            List results = refBankRepository.getRequestsMatching(bic, branchCode, institutionName, depositoryFlag);

            if (results.size() == 0) {
                returnMap.put("status", "ok");
                returnMap.put("details", "not found")
            } else {
                returnMap.put("status", "ok");
                returnMap.put("details", results)
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
    @Path("/bankDetails/{swiftCode}")
    public Response getRefBankDetails(@PathParam("swiftCode") String swiftCode) {

        Gson gson = new Gson();
        Map returnMap = new HashMap();

        String result="";

        try {

            String bic, branchCode;

            if (swiftCode != null && !swiftCode.trim().isEmpty()) {
                swiftCode = swiftCode.toUpperCase();
                if (swiftCode.length() > 8) {
                    bic = swiftCode.substring(0, 8);
                    branchCode = swiftCode.substring(8);
                } else if (swiftCode.length() <= 8) {
                    bic = swiftCode;
                }
            }

            Map refBank = refBankRepository.getBankByBicAndBranch(bic, branchCode);

            returnMap.put("status", "ok");
            returnMap.put("details", refBank);

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
    @Path("/bankDetails/save")
    public Response saveCustomerDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            refBankService.saveRefBankDetail(formDetails);

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
