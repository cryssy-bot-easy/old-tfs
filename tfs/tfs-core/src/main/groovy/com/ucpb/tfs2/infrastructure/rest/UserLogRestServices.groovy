package com.ucpb.tfs2.infrastructure.rest

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.google.gson.Gson
import com.ucpb.tfs.domain.security.UserRepository

@Path("/userlog")
@Component
class UserLogRestServices {
	
	@Autowired
	UserRepository userRepository;
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/search/logout")
//	public Response searchEmployeeToLogout(@Context UriInfo allUri) {
//		Gson gson = new Gson();
//		Map returnMap = new HashMap();
//
//		String result="";
//		try {
//			
//			List<Map<String, Object>> usersToLogout = userRepository.getUserToLogout()
//
//			returnMap.put("status", "ok");
//			returnMap.put("details", usersToLogout);
//
//		} catch(Exception e) {
//			e.printStackTrace()
//			return Response.status(500).entity(gson.toJson(['errorString':e.toString()])).build();
//		}
//		
//		result = gson.toJson(returnMap);
//
//		return Response.status(200).entity(result).build();
//	}
	
}
