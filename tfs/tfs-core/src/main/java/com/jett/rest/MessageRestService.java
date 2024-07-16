package com.jett.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/message")
public class MessageRestService {

    @GET
    @Path("/{param}")
    public Response printMessage(@PathParam("param") String msg) {

        String result = "Restful example : " + msg;

        return Response.status(200).entity(result).build();

    }

    @GET
    @Path("/save/{id}")
    public Response save(@PathParam("id") String id) {
        String result = "Saving : " + id;

        return Response.status(200).entity(result).build();
    }

}