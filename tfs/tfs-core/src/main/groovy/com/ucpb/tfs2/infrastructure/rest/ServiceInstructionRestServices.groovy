package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.instruction.ServiceInstructionId
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository
import com.ucpb.tfs.domain.task.Task
import com.ucpb.tfs.domain.task.TaskReferenceNumber
import com.ucpb.tfs.domain.task.TaskRepository
import com.ucpb.tfs2.application.service.ServiceInstructionService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PropertiesLoaderUtils

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

@Path("/ets")
@Component
class ServiceInstructionRestServices {

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    ServiceInstructionService serviceInstructionService;

    @Autowired
    TaskRepository taskRepository;
	
	private String defaultAddress;
	private String enableNotification;
	
	public ServiceInstructionRestServices() {
		Resource resource = new ClassPathResource("/tfs.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		this.defaultAddress = props.get("mail.smtp.defaultAddress");
		this.enableNotification = props.get("mail.smtp.enableNotification");
	}
	
	public void setDefaultAddress(String defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	public void setEnableNotification(String enableNotification) {
		this.enableNotification = enableNotification;
	}

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/details")
    public Response getServiceInstructionDetails(@Context UriInfo allUri) {

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

            // if ETS number was specified ...
            if (jsonParams.get("etsNumber") != null) {
                Map serviceInstructionRecord = serviceInstructionRepository.getServiceInstructionBy(new ServiceInstructionId(jsonParams.get("etsNumber").toString()));
                returnMap.put("details", serviceInstructionRecord)
            }

            // if secret all was specified was specified ...
            if (jsonParams.get("all") != null) {
                // pass null and no criteria is added

                List allServiceInstructions;
                allServiceInstructions = serviceInstructionRepository.getAllServiceInstruction()
                returnMap.put("details", allServiceInstructions)
            }

            returnMap.put("status", "ok");


        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
			returnMap.put("da", this.defaultAddress());
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
        println "saveServiceInstruction"
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        Map jsonParams = gson.fromJson(postRequestBody, Map.class);

        try {

            String etsNumber = jsonParams.get("etsNumber")

            Map newSI
            // creates new serviceInstruction
            if (!etsNumber || etsNumber.equals("")) {
                println "add mode"
                newSI = serviceInstructionService.createServiceInstruction(jsonParams)
            }else {
                println "edit mode"
                newSI = serviceInstructionService.updateServiceInstruction2(jsonParams)
            }
			
			println "newSI:" + newSI.toMapString()
            returnMap.put("details", newSI)
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
    @Path("/reverse")
    public Response reverseEts(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        Map jsonParams = gson.fromJson(postRequestBody, Map.class);

        try {

            String etsNumber = jsonParams.get("etsNumber")

            Map newSI

            // creates new serviceInstruction
            if (etsNumber != null || !etsNumber.equals("")) {
                newSI = serviceInstructionService.reverseEts(jsonParams)
            }

            returnMap.put("details", newSI)
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
    @Path("/getReversal")
    public Response getReversal(@Context UriInfo allUri) {
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

            // if ETS number was specified ...
            if (jsonParams.get("etsNumber") != null) {
                def reversalCount = serviceInstructionRepository.getReversal((String) jsonParams.get("etsNumber"), (String) jsonParams.get("serviceType"));

                Boolean hasReversal = Boolean.FALSE

                if (reversalCount > 0) {
                    println "meron"
                    hasReversal = Boolean.TRUE
                } else {
                    println "wala"
                    hasReversal = Boolean.FALSE
                }

                returnMap.put("hasReversal", hasReversal)
            }

            returnMap.put("status", "ok");


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

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getNextBranchApprovers")
    public Response getNextBranchApprovers(@Context UriInfo allUri) {
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

            def nextBranchApprovers = serviceInstructionRepository.getNextBranchApprovers((String) jsonParams.get("roleId"),
                (String) jsonParams.get("unitCode"),
                (String) jsonParams.get("lastUser"),
                (String) jsonParams.get("currentOwner"));

            Task task = taskRepository.load(new TaskReferenceNumber((String) jsonParams.get("etsNumber")))


            def responseMap = [nextBranchApprovers: nextBranchApprovers,
                               routedTo: task.getUserActiveDirectoryId().toString()]

            returnMap.put("response", responseMap);
            returnMap.put("status", "ok");


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

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/rerouteServiceInstruction")
    public Response rerouteServiceInstruction(@Context UriInfo allUri, String postRequestBody) {
		
		
	        Gson gson = new Gson();
	
	        String result="";
	        Map returnMap = new HashMap();
	
	        Map jsonParams = gson.fromJson(postRequestBody, Map.class);
	
	        try {
				/**
				 * 	05/26/2017 Redmine #4222 - E-mail Notification
				 * 	Edit by Pat - Added parameter routedTo to be passed to reroute
				 */
	            serviceInstructionService.rerouteServiceInstruction((String) jsonParams.get("etsNumber"), (String) jsonParams.get("routedTo"), (String) jsonParams.get("rerouteTo"),(String) jsonParams.get("loggedInUsername"))
	
	            returnMap.put("response", "success")
	            returnMap.put("status", "ok")
	
	        } catch(Exception e) {
	
	            Map errorDetails = new HashMap();
	
	            e.printStackTrace();
				
				errorDetails.put("code", e.getMessage());
	            errorDetails.put("description", e.toString());
	
	            returnMap.put("status", "error");
	            returnMap.put("error", errorDetails);
				if (this.enableNotification.equalsIgnoreCase("true") ) {
				returnMap.put("email", this.defaultAddress);
				returnMap.put("errMessage", "EXCEPTION: " + e.getMessage());
				}else{
				returnMap.put("email", "");
				returnMap.put("errMessage", "");
				}
	        }
	
	        // format return data as json
	        result = gson.toJson(returnMap);
			
			Response resp = Response.status(200).entity(result).build();
	
	        return resp;
		
		
    }

}