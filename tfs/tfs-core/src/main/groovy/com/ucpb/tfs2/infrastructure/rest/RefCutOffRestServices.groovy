package com.ucpb.tfs2.infrastructure.rest

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.*
import javax.ws.rs.core.*

import com.ucpb.tfs.domain.sysparams.CutOff
import com.ucpb.tfs.domain.sysparams.CutOffRepository

import com.google.gson.Gson
import com.ucpb.tfs2.application.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Path("/cutOff")
@Component
class RefCutOffRestServices {

	@Autowired
	CutOffRepository cutOffRepository
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCutOff")
	public Response getCutOff(@Context UriInfo allUri) {
		
		Gson gson = new Gson();
		Map returnMap = new HashMap();

		String result="";
		
		try {

			CutOff cutOff = cutOffRepository.getCutOffTime()
			
			returnMap.put("status", "ok");
			returnMap.put("details", cutOff);

		} catch(Exception e) {
			e.printStackTrace();
			
			Map errorDetails = new HashMap();

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
	@Path("/save")
	public Response saveCutOff(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();
		DecimalFormat df = new DecimalFormat("00");
		
		try {
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
			int hour = Integer.parseInt(formDetails.get("hour"))
			int minute = Integer.parseInt(formDetails.get("minute"))
			String period = formDetails.get("period")
			
			String cutOffTime = df.format(hour)+":"+df.format(minute)+" "+period
			
			CutOff cutOff = cutOffRepository.getCutOffTime()
			
			cutOff.setCutOffTime(cutOffTime)
			
			cutOffRepository.save(cutOff)
						
			returnMap.put("updated", "true")
			returnMap.put("status", "ok")
			
		} catch(Exception e) {
			e.printStackTrace();
			
			Map errorDetails = new HashMap();

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
	@Path("/validate")
	public Response validateCutOff(@Context UriInfo allUri, String postRequestBody) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();
		Boolean cutOffStatus = false;
		
		try {	
			CutOff cutOffObj = cutOffRepository.getCutOffTime()
			
			SimpleDateFormat _12HR_FORMAT = new SimpleDateFormat("hh:mm a");

			String cutOff = cutOffObj.getCutOffTime();

			if (_12HR_FORMAT.parse(_12HR_FORMAT.format(new Date())).after(_12HR_FORMAT.parse(cutOff))){
				System.out.println("Time Now: " + _12HR_FORMAT.parse(_12HR_FORMAT.format(new Date())) + " is After " + _12HR_FORMAT.parse(cutOff));
				cutOffStatus = true;
			}
			
			Map cutOffDetails = new HashMap();
			
			cutOffDetails.put("cutOffStatus", cutOffStatus)
			cutOffDetails.put("cutOffTime", cutOff)
			
			returnMap.put("details", cutOffDetails)
			returnMap.put("updated", "true")
			returnMap.put("status", "ok")
			
		} catch(Exception e) {
			e.printStackTrace();
			
			Map errorDetails = new HashMap();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		result = gson.toJson(returnMap);

		return Response.status(200).entity(result).build();
	}
	
}
