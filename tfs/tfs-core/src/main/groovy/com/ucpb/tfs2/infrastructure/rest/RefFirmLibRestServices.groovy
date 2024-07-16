package com.ucpb.tfs2.infrastructure.rest

import javax.ws.rs.*
import javax.ws.rs.core.*

import com.ucpb.tfs.domain.sysparams.RefFirmLib
import com.ucpb.tfs.domain.sysparams.RefFirmLibRepository

import com.google.gson.Gson
import com.ucpb.tfs2.application.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Path("/refFirmLib")
@Component
class RefFirmLibRestServices {

	@Autowired
	RefFirmLibRepository refFirmLibRepository
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchFirmLib")
	public Response searchFirmLib(@Context UriInfo allUri) {
	
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
			String firmCode = jsonParams.get("firmCode")
			String firmDescription = jsonParams.get("firmDescription")
			
			List results = refFirmLibRepository.getRequestsMatching(firmCode, firmDescription);
			
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
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/save")
	public Response saveRefFirmLib(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();
		
		String result="";
		Map returnMap = new HashMap();
		
		try {			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
			
			String firmCode = formDetails.get("firmCode")
			String firmDescription = formDetails.get("firmDescription")
			String addOrEditFirmLib = formDetails.get("addOrEditFirmLib")
			RefFirmLib refFirmLib = refFirmLibRepository.getRefFirmLib(firmCode)				
			if (refFirmLib == null){
				refFirmLib = new RefFirmLib()
			}
			
			//check if firm code is existing when adding new firm code
			if(firmCode?.trim() == refFirmLib?.getFirmCode()?.trim() && addOrEditFirmLib?.equalsIgnoreCase("add")) {
				returnMap.put("checkRecord", "existing")
			} else {
				returnMap.put("checkRecord", "notExisting")
				refFirmLib.setFirmCode(firmCode.toUpperCase())
				refFirmLib.setFirmDescription(firmDescription.toUpperCase())
				refFirmLibRepository.saveOrUpdate(refFirmLib)
			}
			
			returnMap.put("updated", "true")
			returnMap.put("status", "ok")		
		} catch(Exception e) {
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
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete")
	public Response deleteRefFirmLib(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();
		
		String result="";
		Map returnMap = new HashMap();
		
		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
					
			refFirmLibRepository.delete(formDetails.get("firmCode").toString())
			
			returnMap.put("updated", true)
			returnMap.put("status", "ok")
		
		} catch(Exception e) {
		
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
