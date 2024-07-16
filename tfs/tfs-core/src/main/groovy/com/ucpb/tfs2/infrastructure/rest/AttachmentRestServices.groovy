package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.attach.AttachmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/**
 * User: IPCVal
 */
@Path("/attachment")
@Component
public class AttachmentRestServices {

    @Autowired
    AttachmentRepository attachmentRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFileDetails")
    public Response getFileDetails(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String result = "";
        Map returnMap = new HashMap();

        try {

            String idStr = jsonParams.get("id")
            println "AttachmentRestServices.getFileDetails(): file idStr = ${idStr}"

            if (idStr != null) {

                Long id = Long.valueOf(idStr)
                Map<String, Object> detailsMap = attachmentRepository.getAttachmentDetailsMap(id)

                println "AttachmentRestServices.getFileDetails(): Retrieved file name = ${(String)detailsMap.get("filename")}"

                returnMap.put("details", detailsMap)
                returnMap.put("status", "ok")

            } else {

                Map errorDetails = new HashMap();

                errorDetails.put("code", "file id is null");
                errorDetails.put("description", "file id is null");

                returnMap.put("status", "error");
                returnMap.put("error", errorDetails);
            }

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/delete")
    public Response delete(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        try {

            // POST, so get from postRequestBody
            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            String idStr = formDetails.get("id")
            println "AttachmentRestServices.delete(): file idStr = ${idStr}"

            if (idStr != null) {

                Long id = Long.valueOf(idStr)

                Map<String, Object> detailsMap = attachmentRepository.getAttachmentDetailsMap(id)
                println "AttachmentRestServices.delete(): Retrieved file name = ${(String)detailsMap.get("filename")}"

                int deletedAttachment = attachmentRepository.delete(id)

                Map details = new HashMap();
                details.put("filename", (String)detailsMap.get("filename"));

                returnMap.put("details", details)
                returnMap.put("status", "ok")

            } else {

                Map errorDetails = new HashMap();
                errorDetails.put("code", "file id is null");
                errorDetails.put("description", "file id is null");

                returnMap.put("status", "error");
                returnMap.put("error", errorDetails);
            }

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }
}
