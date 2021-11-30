package dev.sample.ssd.das.rest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONObject;

import dev.sample.ssd.das.utils.DBUtils;
import dev.sample.ssd.das.utils.UserType;
import dev.sample.ssd.das.vos.UserVO;


@Path("user")
public class User {
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{userid}")
    public Response getSADetails(@PathParam("userid") String userid) {
    	System.out.println("In get user details");
    	try {
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.getUserQuery(userid);
				ResultSet rs = stmt.executeQuery(query);
				UserVO userVO = new UserVO();

				while (rs.next()) {
					userVO.setValues(rs);
				}
		    	System.out.println("Returning user details");
				
				return Response.ok(userVO.toJSON(), MediaType.APPLICATION_JSON).build();
			} catch (Exception e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
			}finally {
				conn.close();
			}
			
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
		}
    }
    

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(HashMap<String, String> payload) {
    	System.out.println("In Create user");
    	try {
	    	UserVO userVO = new UserVO(payload, UserType.customerType);
			HashMap<String, String> response = new HashMap<String, String>();
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.createUserQuery(userVO);
				System.out.println("createUser Query = " + query);
				stmt.executeUpdate(query);
				System.out.println("User created successfully");
				
				new SavingsAccount().createSavingsAccount(userVO.getUserid(), payload);
				System.out.println("Savings Account created successfully");
				
				response.put("Response", "User with userid '" + userVO.getUserid() + "' created successfully");
				return Response.ok(response, MediaType.APPLICATION_JSON).build();
				
			} catch (Exception e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
			}finally {
				conn.close();
			}
			
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
		}
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{userid}")
    public Response deleteUser(@PathParam("userid") String userid) {
    	System.out.println("In delete user");
    	try {
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.getDeleteUserFromLoanAccountQuery(userid);
				stmt.executeUpdate(query);
				query = DBUtils.getDeleteUserFromSavingsAccountQuery(userid);
				stmt.executeUpdate(query);
				query = DBUtils.getDeleteUserFromUserQuery(userid);
				stmt.executeUpdate(query);
				
				JSONObject response = new JSONObject();
				response.put("message", "User details of " + userid + " deleted successfully.");
				
				System.out.println("User deleted successfully");
				
				return Response.ok(response, MediaType.APPLICATION_JSON).build();
			} catch (Exception e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
			}finally {
				conn.close();
			}
			
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
		}
    }
    
}