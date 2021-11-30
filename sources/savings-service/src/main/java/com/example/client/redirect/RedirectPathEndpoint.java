package com.example.client.redirect;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



@Path("/redirect")
public class RedirectPathEndpoint {
	
	@GET
	@Path("/dashboard")
	@RolesAllowed("users")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response redirect(@javax.ws.rs.core.Context javax.servlet.http.HttpServletRequest request) {
		System.out.println("Redirected successfully!!!");
		return Response.ok("test").build();
	}


}
