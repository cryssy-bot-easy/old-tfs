package com.ucpb.tfs2.infrastructure.rest

import javax.ws.rs.*
import javax.ws.rs.core.*

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ucpb.tfs.domain.sysparams.BranchUnit
import com.ucpb.tfs.domain.sysparams.BranchUnitRepository;

import com.google.gson.Gson
import com.ucpb.tfs2.application.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Path("/branchUnit")
@Component
class BranchUnitRestServices {
	
	@Autowired
	BranchUnitRepository branchUnitRepository;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search")
	public Response searchBranchUnit(@Context UriInfo allUri) {

		println "Inside Rest Services"
		
		Gson gson = new Gson();
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap();

		String unitCode = "";
		String unitName = "";

		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		String result="";
		
		try {

			if (jsonParams.get("unitCode") != null) {
				unitCode = jsonParams.get("unitCode")
			}

			if (jsonParams.get("unitName") != null) {
				unitName = jsonParams.get("unitName")
			}
			
			println "Unit Code: " + unitCode
			println "Unit Name: " + unitName
			
			List<Map<String, Object>> branchUnit = branchUnitRepository.getBranchUnit(unitCode, unitName)

			println "BranchUnitList: " + branchUnit
			
			returnMap.put("status", "ok");
			returnMap.put("details", branchUnit);

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
	@Path("/save")
	public Response saveBranchUnit(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
			
			println "Form Details" + formDetails
			
			String unitCode = formDetails.get("unitCode")+""
			String branchName = formDetails.get("unitName")+""
			String branchAddress = formDetails.get("unitAddress")+""
			String branchType = formDetails.get("branchType")+""
			String swiftStatus = formDetails.get("swiftStatus")+""
			
			BranchUnit branchUnit = branchUnitRepository.getBranchUnit(unitCode)
			
			if (branchUnit == null){
				branchUnit = new BranchUnit()
			}

			branchUnit.setUnitCode(unitCode);
			branchUnit.setBranchName(branchName)
			branchUnit.setBranchAddress(branchAddress)
			branchUnit.setBranchType(branchType)
			branchUnit.setSwiftStatus(swiftStatus)
			
			branchUnitRepository.saveOrUpdate(branchUnit)
			
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
