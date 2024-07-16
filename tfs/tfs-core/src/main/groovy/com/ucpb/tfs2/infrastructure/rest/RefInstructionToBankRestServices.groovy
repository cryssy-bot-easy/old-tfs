package com.ucpb.tfs2.infrastructure.rest

import javax.ws.rs.*
import javax.ws.rs.core.*

import com.ucpb.tfs.domain.sysparams.RefInstructionToBank
import com.ucpb.tfs.domain.sysparams.RefInstructionToBankRepository

import com.google.gson.Gson
import com.ucpb.tfs2.application.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Path("/refInstructionToBank")
@Component
class RefInstructionToBankRestServices {

	@Autowired
	RefInstructionToBankRepository refInstructionToBankRepository
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAllInstructionToBank")
	public Response getRefInstructionToBank(@Context UriInfo allUri) {
		
		Gson gson = new Gson();
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap();

		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		String result="";
		
		try {

			List<Map<String, Object>> allInstructionToBank = refInstructionToBankRepository.getAllRefInstructionToBank()
			
			returnMap.put("status", "ok");
			returnMap.put("details", allInstructionToBank);

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
	public Response saveRefInstructionToBank(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);

			String instructionToBankCode = formDetails.get("instructionToBankCode")
			String instruction = formDetails.get("instruction")

			RefInstructionToBank refInstructionToBank = refInstructionToBankRepository.getRefInstructionToBank(instructionToBankCode)
			
			if (refInstructionToBank == null){
				refInstructionToBank = new RefInstructionToBank()
			}
			
			refInstructionToBank.setInstructionToBankCode(instructionToBankCode)
			refInstructionToBank.setInstruction(instruction)
			
			refInstructionToBankRepository.saveOrUpdate(refInstructionToBank)
			
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
	public Response deleteRefInstructionToBank(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
						
			refInstructionToBankRepository.delete(Long.valueOf(formDetails.get("id")))

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
