package dev.sample.ssd.das.rest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import dev.sample.ssd.das.utils.DBUtils;
import dev.sample.ssd.das.utils.UserType;
import dev.sample.ssd.das.vos.UserVO;


@Path("employee")
public class BankEmployee {
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{employeeid}")
    public Response getSADetails(@PathParam("employeeid") String employeeid) {
    	System.out.println("In get employee details");
    	try {
			Connection conn = DBUtils.getConnection();
			try {
				String query = DBUtils.getEmployeeQuery(employeeid);
				UserVO employee = new UserVO(UserType.employeeType);

				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					employee.setValues(rs);
				}
				System.out.println("Returning employee details");
				return Response.ok(employee.toJSON(), MediaType.APPLICATION_JSON).build();
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
			}finally {
				conn.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
		}
    }

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(HashMap<String, String> payload) {
    	System.out.println("In create employee");
		HashMap<String, String> response = new HashMap<String, String>();
    	try {
    		UserVO employee = new UserVO(payload, UserType.employeeType);
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.createEmployeeQuery(employee);
				stmt.executeUpdate(query);
				response.put("Response", "Bank employee userid '" + (String)payload.get("user_id") + "' created successfully");
				System.out.println("Employee created");
				return Response.ok(response, MediaType.APPLICATION_JSON).build();
				
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
			}finally {
				conn.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
		}
    }
    
}