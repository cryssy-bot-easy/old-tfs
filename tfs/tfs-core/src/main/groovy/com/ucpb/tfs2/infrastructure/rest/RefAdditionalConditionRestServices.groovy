package com.ucpb.tfs2.infrastructure.rest

import javax.ws.rs.*
import javax.ws.rs.core.*

import com.ucpb.tfs.domain.sysparams.RefAdditionalCondition
import com.ucpb.tfs.domain.sysparams.RefAdditionalConditionRepository

import com.google.gson.Gson
import com.ucpb.tfs2.application.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Path("/refAdditionalCondition")
@Component
class RefAdditionalConditionRestServices {
	
	@Autowired
	RefAdditionalConditionRepository refAdditionalConditionRepository
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAllAdditionalCondition")
	public Response getRefAdditionalCondition(@Context UriInfo allUri) {
		
		Gson gson = new Gson();
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap();

		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		String result="";
		
		try {

			List<Map<String, Object>> allAdditionalCondition = refAdditionalConditionRepository.getAllRefAdditionalCondition()
			
			returnMap.put("status", "ok");
			returnMap.put("details", allAdditionalCondition);

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
		
		return Response.status(200).entity(result).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/save")
	public Response saveRefAdditionalCondition(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);

			String conditionType = formDetails.get("conditionType")
			String conditionCode = formDetails.get("conditionCode")
			String condition = formDetails.get("condition")
			
			RefAdditionalCondition refAdditionalCondition = refAdditionalConditionRepository.getRefAdditionalCondition(conditionCode)
			
			if (refAdditionalCondition == null){
				refAdditionalCondition = new RefAdditionalCondition()
			}
			
			refAdditionalCondition.setConditionType(conditionType)
			refAdditionalCondition.setConditionCode(conditionCode)
			refAdditionalCondition.setCondition(condition)
			
			refAdditionalConditionRepository.saveOrUpdate(refAdditionalCondition)
			
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
	public Response deleteRefAdditionalCondition(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
						
			refAdditionalConditionRepository.delete(Long.valueOf(formDetails.get("id")))

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
