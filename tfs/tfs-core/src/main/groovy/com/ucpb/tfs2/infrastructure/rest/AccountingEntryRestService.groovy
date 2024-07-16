package com.ucpb.tfs2.infrastructure.rest



import com.google.gson.Gson
import com.ucpb.tfs.application.service.AccountingService
import com.ucpb.tfs.domain.accounting.AccountingEntryActualRepository;
import com.ucpb.tfs.interfaces.repositories.GlMastRepository
import javax.ws.rs.*
import javax.ws.rs.core.*

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.DecimalFormat
import java.text.SimpleDateFormat


@Path("/accounting")
@Component
class AccountingEntryRestService {

	@Autowired
	GlMastRepository glMastRepository

	@Autowired
	AccountingService accountingService
	
	@Autowired
	AccountingEntryActualRepository accountingEntryActualRepository
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/validate")
	public Response validateEntry(@Context UriInfo allUri){

		Gson gson = new Gson();
		
		String result=[:];
		Map returnMap = new HashMap<String, String>();
		
		
		try {
			MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
			
			String unitCode = parameters.unitCode.toString().replace("[", "").replace("]", "")
			String bookCode = parameters.bookCode.toString().replace("[", "").replace("]", "")
			String accountingCode = parameters.accountingCode.toString().replace("[", "").replace("]", "")
			String bookCurrency = parameters.bookCurrency.toString().replace("[", "").replace("]", "")
			
			def validateAccountingEntries = accountingService.validateAccountingEntries(unitCode, bookCode, accountingCode, bookCurrency)
			
			String validationResult = validateAccountingEntries.toString()
			returnMap.put("result", validationResult)
			
			println "GL Validation result >>>>>> " + returnMap
			result = gson.toJson(returnMap);
			
			return Response.status(200).entity(result).build();
		}catch(Exception e){
			e.printStackTrace();

			//TODO: if error must return error message
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/checkError")
	public Response checkError(@Context UriInfo allUri){

		Gson gson = new Gson();
		
		String result="Hello";
		Map returnMap = new HashMap<String, String>();
				
		try {		
			def errorInAccountingEntries = accountingService.errorAlert()
			
			String errorCheckResult = errorInAccountingEntries.toString()
			returnMap.put("result", errorCheckResult)
			
			result = gson.toJson(returnMap);
			
			return Response.status(200).entity(result).build();
		}catch(Exception e){
			e.printStackTrace();

		}
	}
}
