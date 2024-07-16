package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.incuventure.cqrs.infrastructure.StandardCommandBus
import com.ucpb.tfs.application.command.instruction.TagAsApprovedCommand
import com.ucpb.tfs.domain.instruction.ServiceInstruction
import com.ucpb.tfs.domain.instruction.ServiceInstructionId
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

@Path("/debug")
@Component
class DebugRestServices {

    @Autowired
    StandardCommandBus commandBus;

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/ets/fasttrack")
    public Response savePayment(@Context UriInfo allUri, String postRequestBody) {

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

        String etsNumber = jsonParams.get("etsNumber").toString()

        try {

            Map serviceInstructionRecord = serviceInstructionRepository.getServiceInstructionBy(new ServiceInstructionId(jsonParams.get("etsNumber").toString()));

            ServiceInstruction serviceInstruction = serviceInstructionRepository.load(new ServiceInstructionId(etsNumber))

            if(serviceInstruction != null) {

                Map fakeDetailsMap = [referenceType: "ETS", username: "brancha", etsNumber:jsonParams["etsNumber"]]
                TagAsApprovedCommand approvedCommand = new TagAsApprovedCommand()

                approvedCommand.setParameterMap(fakeDetailsMap)

                // dispatch the command
                commandBus.dispatch(approvedCommand);

                returnMap.put("status", "ok");
                returnMap.put("details", "ETS: " + etsNumber + " fast tracked!")
            }
            else {
                returnMap.put("status", "error");
                returnMap.put("details", "ETS " + etsNumber + " does not exist!")
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

}
