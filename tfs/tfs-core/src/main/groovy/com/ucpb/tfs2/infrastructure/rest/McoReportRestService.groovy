package com.ucpb.tfs2.infrastructure.rest

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.ucpb.tfs.core.batch.process.McoReportPersistenceService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Component
@Path("/mco")
class McoReportRestService {

	private static final Gson GSON = new Gson();
	
	@Autowired
	McoReportPersistenceService mcoReportPersistenceService
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/processMonthEnd")
	public Response processMonthEnd(@Context UriInfo allUri, String postRequestBody) {
		Map formDetails = GSON.fromJson(postRequestBody, Map.class);
		Assert.notNull(formDetails["query"], "Report Query must not be null!");
		Assert.notNull(formDetails["type"], "Report Type must not be null!");
		Assert.notNull(formDetails["reportDate"], "Report Date must not be null!");
		def returnMap = [:]
		
		try{
			boolean success = mcoReportPersistenceService.persist(formDetails["query"].toString(),
				formDetails["type"].toString(),formDetails["reportDate"].toString());
			
			returnMap.put("success", success);
		}catch(Exception e){
			e.printStackTrace();
			returnMap.put("success", false);
		}

		return Response.status(200).entity(GSON.toJson(returnMap)).build();
	}

		
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/deleteAllData")
	public Response deleteAllData(@Context UriInfo allUri, String postRequestBody) {
		def returnMap = [:]
				
		try{
			boolean success = mcoReportPersistenceService.deleteAllData();
			returnMap.put("success", success);
		}catch(Exception e){
			e.printStackTrace();
			returnMap.put("success", false);
		}
		
		return Response.status(200).entity(GSON.toJson(returnMap)).build();
	}
}
