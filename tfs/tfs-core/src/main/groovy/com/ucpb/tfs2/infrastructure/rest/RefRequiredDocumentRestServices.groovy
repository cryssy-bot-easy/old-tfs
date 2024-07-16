package com.ucpb.tfs2.infrastructure.rest

import javax.ws.rs.*
import javax.ws.rs.core.*

import com.ucpb.tfs.domain.sysparams.RefRequiredDocuments
import com.ucpb.tfs.domain.sysparams.RefRequiredDocumentsRepository

import com.google.gson.Gson
import com.ucpb.tfs2.application.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Path("/refRequiredDocument")
@Component
class RefRequiredDocumentRestServices {
	
	@Autowired
	RefRequiredDocumentsRepository refRequiredDocumentsRepository
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAllRequiredDocument")
	public Response getRequiredDocuments(@Context UriInfo allUri) {
		
		Gson gson = new Gson();
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap();

		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		String result="";
		
		try {

			List<Map<String, Object>> allRefRequiredDocument = refRequiredDocumentsRepository.getAllRefRequiredDocuments()
			
			returnMap.put("status", "ok");
			returnMap.put("details", allRefRequiredDocument);

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
	public Response saveRefRequiredDocument(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);

			String documentCode = formDetails.get("documentCode")
			String documentType = formDetails.get("documentType")
			String description = formDetails.get("description")
			
			RefRequiredDocuments refRequiredDocuments = refRequiredDocumentsRepository.getRefRequiredDocument(documentCode)
			
			if (refRequiredDocuments == null){
				refRequiredDocuments = new RefRequiredDocuments()
			}
			
			refRequiredDocuments.setDocumentCode(documentCode)
			refRequiredDocuments.setDocumentType(documentType)
			refRequiredDocuments.setDescription(description)

			refRequiredDocumentsRepository.saveOrUpdate(refRequiredDocuments)
			
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
	public Response deleteRefRequiredDocument(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
						
			refRequiredDocumentsRepository.delete(Long.valueOf(formDetails.get("id")))

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
