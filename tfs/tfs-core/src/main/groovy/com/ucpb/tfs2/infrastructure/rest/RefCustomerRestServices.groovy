package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.sysparams.RefCustomerRepository
import com.ucpb.tfs2.application.service.RefCustomerService
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
@Path("/refCustomer")  // root url/refCustomer
@Component
public class RefCustomerRestServices {

    @Autowired
    RefCustomerRepository refCustomerRepository

    @Autowired
    RefCustomerService refCustomerService

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchCustomer")
    public Response searchCustomer(@Context UriInfo allUri) {

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

            String centralBankCode        = jsonParams.get("centralBankCode")
            String clientTaxAccountNumber = jsonParams.get("clientTaxAccountNumber")
            String cifLongName            = jsonParams.get("cifLongName")
            String cifLongNameB           = jsonParams.get("cifLongNameB")

            List results = refCustomerRepository.getRequestsMatching(centralBankCode, clientTaxAccountNumber, cifLongName, cifLongNameB);

            if (results.size() == 0) {
                returnMap.put("status", "ok");
                returnMap.put("details", "not found");
            } else {
                returnMap.put("status", "ok");
                returnMap.put("details", results);
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
    @Path("/customerDetails/{customerId}")
    public Response getRefCustomerDetails(@PathParam("customerId") String customerId) {

        Gson gson = new Gson();
        Map returnMap = new HashMap();

        String result="";

        try {

            Map refCustomer = refCustomerRepository.getCustomerById(new Long(customerId));

            if (refCustomer != null) {

                DecimalFormat df = new DecimalFormat("###")

                if (refCustomer.get("id") != null && (BigInteger)refCustomer.get("id") > 0) {
                    refCustomer.put("id", df.format((BigInteger)refCustomer.get("id")));
                }

                if (refCustomer.get("accountType") != null && (BigInteger)refCustomer.get("accountType") > 0) {
                    refCustomer.put("accountType", df.format((BigInteger)refCustomer.get("accountType")));
                }

                if (refCustomer.get("accountOfficerCode") != null && (BigInteger)refCustomer.get("accountOfficerCode") > 0) {
                    refCustomer.put("accountOfficerCode", df.format((BigInteger)refCustomer.get("accountOfficerCode")));
                }

                if (refCustomer.get("clientType") != null && (BigInteger)refCustomer.get("clientType") > 0) {
                    refCustomer.put("clientType", df.format((BigInteger)refCustomer.get("clientType")));
                }

                if (refCustomer.get("clientNumber") != null && (BigInteger)refCustomer.get("clientNumber") > 0) {
                    refCustomer.put("clientNumber", df.format((BigInteger)refCustomer.get("clientNumber")));
                }

                if (refCustomer.get("clientBirthday") != null && !((String)refCustomer.get("clientBirthday")).isEmpty()) {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy hh:mm:ss a");
                    Date clientBirthday = dateFormat.parse(refCustomer.get("clientBirthday"));

                    dateFormat.applyPattern("MM/dd/yyyy");
                    refCustomer.put("clientBirthday", dateFormat.format(clientBirthday));
                }
            }

            returnMap.put("status", "ok");
            returnMap.put("details", refCustomer);

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
    @Path("/customerDetails/save")
    public Response saveCustomerDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);
            Long refCustomerId = refCustomerService.saveRefCustomerDetail(formDetails);
            returnMap.put("status", "ok")
            returnMap.put("details", refCustomerId)

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/customerDetails/delete")
    public Response deleteCustomerDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);
            println(formDetails);
            //refCustomerService.deleteRefCustomerDetail();

            returnMap.put("status", "ok")
            refCustomerService.deleteRefCustomerDetail(formDetails);

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
