package dev.sample.ssd.das.rest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import dev.sample.ssd.das.utils.DBUtils;
import dev.sample.ssd.das.utils.OperationType;
import dev.sample.ssd.das.vos.SavingsAccountVO;


@Path("savings-account")
public class SavingsAccount {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pending-savings-accounts")
    public Response getPendingSavingsAccounts() {
    	System.out.println("In getPendingSavingsAccounts");
		JSONArray usersSAArray = new JSONArray();
    	try {
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.getPendingSavingsAccounts();
				ResultSet rs = stmt.executeQuery(query);

				while (rs.next()) {
					SavingsAccountVO saVO = new SavingsAccountVO();
					saVO.setValues(rs);
					usersSAArray.add(saVO.toJSON());
				}
				System.out.println("Returning pending savings account");
				return Response.ok(usersSAArray, MediaType.APPLICATION_JSON).build();
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
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{userid}")
    public Response getSADetails(@PathParam("userid") String userid) {
    	System.out.println("In get savings account details");
    	try {
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.getSAQuery(userid);
				ResultSet rs = stmt.executeQuery(query);
				SavingsAccountVO saVO = new SavingsAccountVO();
				boolean approvedSAAvailable = false;

				while (rs.next()) {
					saVO.setValues(rs);
					approvedSAAvailable = true;
				}
				if(approvedSAAvailable) {
					query = DBUtils.getLAQuery(userid);
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						saVO.setHasLoan("yes");
					}
					if( !"yes".equals(saVO.getHasLoan()) ) { // if it is not yes, set it to no
						saVO.setHasLoan("no");
					}
					System.out.println("Returning savings account details");
				}
				return Response.ok(saVO.toJSON(), MediaType.APPLICATION_JSON).build();
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
    @Path("{userid}")
    public Response createSavingsAccount(@PathParam("userid") String userid, HashMap<String, String> payload) {
    	System.out.println("In create savings account");
		HashMap<String, String> response = new HashMap<String, String>();
    	try {
    		int savingsAccountNo = getLastSavingsAccountNumber() + 1;
    		SavingsAccountVO saVO = new SavingsAccountVO(payload, OperationType.createSA);
    		saVO.setSavingsAccountNo(savingsAccountNo+"");
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.createSAQuery(userid, saVO);
				stmt.executeUpdate(query);
				response.put("Response", "Savings Account for userid '" + userid + "' created successfully");
				System.out.println("Savings account created successfully");
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
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{userid}")
    public Response updateSavingsAccount(@PathParam("userid") String userid, HashMap<String, String> payload) {
    	System.out.println("In update savings account");
    	HashMap<String, String> response = new HashMap<String, String>();
    	try {
        	SavingsAccountVO saVO = new SavingsAccountVO(payload, OperationType.updateSA);
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.getUpdateSAStatusQuery(userid, saVO);
				stmt.executeUpdate(query);
				response.put("Response", "Service Account for userid '" + userid + "' updated successfully");
				System.out.println("Savings account updated successfully");
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

    
    private int getLastSavingsAccountNumber() throws Exception{
		Connection conn = DBUtils.getConnection();
		Statement stmt = conn.createStatement();
		
		String query = DBUtils.getLastSavingsAccountNumberQuery();
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next() ) {
			int acNum = rs.getInt("savings_account_no");
			if( acNum >= 2000000) {
				return acNum;
			}
		}
		
		return 2000000;
		
    }
    
    
}