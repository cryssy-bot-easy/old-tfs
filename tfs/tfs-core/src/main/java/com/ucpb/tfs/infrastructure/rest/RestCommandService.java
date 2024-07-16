package com.ucpb.tfs.infrastructure.rest;

import com.google.gson.Gson;
import com.incuventure.cqrs.infrastructure.StandardAPICallDispatcher;
import com.ucpb.tfs.application.query.instruction.IServiceInstructionFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.Map;

@Path("/command")
@Component
public class RestCommandService {

    @Autowired
    StandardAPICallDispatcher standardAPICallDispatcher;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{commandName}")
    public Response executeCommand(@PathParam("commandName") String command, String commandParam) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        System.out.println(commandParam);
        Map jsonParams = gson.fromJson(commandParam, Map.class);

//        System.out.println("body in map: " + jsonParams);

        try {
            Object returnVal = standardAPICallDispatcher.dispatch(command, jsonParams);

            returnMap.put("status", "ok");
            returnMap.put("response", returnVal);

        }
        catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.toString());
            errorDetails.put("description", e.getCause());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

}
