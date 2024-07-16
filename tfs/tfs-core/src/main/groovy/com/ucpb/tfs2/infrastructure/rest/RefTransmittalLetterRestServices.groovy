package com.ucpb.tfs2.infrastructure.rest

import java.util.Map;

import javax.ws.rs.*
import javax.ws.rs.core.*

import com.ucpb.tfs.domain.sysparams.RefTransmittalLetter
import com.ucpb.tfs.domain.sysparams.RefTransmittalLetterRepository

import com.google.gson.Gson
import com.ucpb.tfs2.application.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Path("/refTransmittalLetter")
@Component
class RefTransmittalLetterRestServices {
	
	@Autowired
	RefTransmittalLetterRepository refTransmittalLetterRepository
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAllTransmittalLetter")
	public Response getTransmittalLetter(@Context UriInfo allUri) {
		
		Gson gson = new Gson();
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap();

		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		String transmittalLetterCode = jsonParams.get("transmittalLetterCode");
		String result = ""
		
		try {
	
			List<RefTransmittalLetter> allRefTransmittalLetter = refTransmittalLetterRepository.getAllRefTransmittalLetter();
			
			returnMap.put("status", "ok");
			returnMap.put("details", allRefTransmittalLetter);

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
	public Response saveRefTransmittalLetter(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);

			String transmittalLetterCode = formDetails.get("transmittalLetterCode")
			String letterDescription = formDetails.get("letterDescription")
			
			RefTransmittalLetter refTransmittalLetter = refTransmittalLetterRepository.getRefTransmittalLetter(transmittalLetterCode)
			
			if (refTransmittalLetter == null){
				refTransmittalLetter = new RefTransmittalLetter()
			}
			
			refTransmittalLetter.setTransmittalLetterCode(transmittalLetterCode)
			refTransmittalLetter.setLetterDescription(letterDescription)

			refTransmittalLetterRepository.saveOrUpdate(refTransmittalLetter)
			
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
	public Response deleteRefTransmittalLetter(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
						
			refTransmittalLetterRepository.delete(Long.valueOf(formDetails.get("id")))

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
