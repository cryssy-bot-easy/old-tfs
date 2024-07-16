package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.incuventure.cqrs.infrastructure.StandardAPICallDispatcher
import com.ucpb.tfs.domain.corresCharges.CorresChargeAdvanceRepository
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.domain.service.TradeProductNumber
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass
import com.ucpb.tfs.domain.service.enumTypes.ServiceType
import com.ucpb.tfs.domain.service.enumTypes.DocumentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/**
 * Created by IntelliJ IDEA.
 * User: Marv
 * Date: 12/19/12
 * Time: 3:08 PM
 */

@Path("/corresCharge")
@Component
class CorresChargeRestService {

    @Autowired
    StandardAPICallDispatcher standardAPICallDispatcher;

    @Autowired
    CorresChargeAdvanceRepository corresChargeAdvanceRepository
	
	@Autowired
	TradeServiceRepository tradeServiceRepository

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/advance/search")
    public Response searchAdvanceCorresCharge(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            List corresChargeList = corresChargeAdvanceRepository.findAllByDocumentNumber((String) jsonParams.get("documentNumber"), jsonParams.get("unitCode")?.toString() ?: null, (String) jsonParams.get("unitcode"));

            returnMap.put("status", "ok");
            returnMap.put("details", corresChargeList)

        } catch (Exception e) {
			e.printStackTrace()
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getDetails")
	public Response getTradeServiceDetails(@Context UriInfo allUri){
		Gson gson = new Gson();
		
		String result = "";
		
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();
		
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for (String key: mpAllQueParams.keySet()) {
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}
		
		try {
			TradeProductNumber tradeProductNumber = new TradeProductNumber((String) jsonParams.get("documentNumber"))
			
			Map tradeServiceDetails = tradeServiceRepository.getTradeServiceBy(tradeProductNumber, ServiceType.OPENING, DocumentType.FOREIGN,DocumentClass.LC)
			println "ASSSSDDDFF" + tradeServiceDetails.get("details").get("countryCode")
			Map details = [countryCode: tradeServiceDetails.get("details").get("countryCode")]
			returnMap.put("status", "ok");
			returnMap.put("details", details)
		} catch (Exception e) {
			e.printStackTrace()
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

		result = gson.toJson(returnMap);
		
		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
	} 

}
