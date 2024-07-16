package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.ucpb.tfs.domain.mt.OutgoingMTRepository
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs2.application.service.OutgoingMTService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

@Path("/outgoingMT")
@Component
class OutgoingMTServices {

    @Autowired
    OutgoingMTRepository outgoingMTRepository

    @Autowired
    OutgoingMTService outgoingMTService


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response listOutgoingMT(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {

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

            //List allRequests = cdtPaymentRequestRepository.getAllRequests();

            List outgoingMTs = outgoingMTRepository.getAllOutgoingMT()
            returnMap.put("status", "ok");
            returnMap.put("details", outgoingMTs)

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
    public Response saveServiceInstruction(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {
            // call the service to create a new SI
            //Map newSI = serviceInstructionService.createServiceInstruction(jsonParams)

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            // extract the known fields
            String username = formDetails["username"];
            String messageType = formDetails["messageType"];
            String destinationBank = formDetails["destinationBank"];

            // remove the known fields from the details map
            // formDetails.remove("username")
            // formDetails.remove("messageType")
            // formDetails.remove("destinationBank")

            // call the service to save the outgoing MT
            TradeService outgoingMTts = outgoingMTService.saveOutgoingMT(username, formDetails)

            // we indirectly convert the object to a map
            String savedMT = gson.toJson(outgoingMTts)
            Map savedMTMap = gson.fromJson(savedMT, Map.class);

            returnMap.put("details", savedMTMap )
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
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getExistingTransaction")
	public Response getExistingTransaction(@Context UriInfo allUri, String postRequestBody){
		Gson gson = new Gson();
		Map formDetails = gson.fromJson(postRequestBody, Map.class);
		String documentNumber = formDetails["documentNumber"];
		String messageType = formDetails["messageType"];
		String result = "";
		def returnMap = [:];
		
		try{
			Map<String,Object> tradeService = new HashMap<String,Object>()
			switch(messageType){
				case "103":
					tradeService = outgoingMTService.getTradeServiceForMt103(documentNumber)
					println "TRADESERVICE MAP:" + tradeService
					break;
				default:
					tradeService = null;
			}
			
			returnMap.put("status", "ok")
			returnMap.put("details", tradeService)
		}catch(Exception e){
			Map errorDetails = new HashMap();
	
			e.printStackTrace();
	
			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());
	
			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}
		
		result = gson.toJson(returnMap);
		
		return Response.status(200).entity(result).build();
	}
}