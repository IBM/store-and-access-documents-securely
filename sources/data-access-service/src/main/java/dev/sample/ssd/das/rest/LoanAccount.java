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

import dev.sample.ssd.das.utils.DBUtils;
import dev.sample.ssd.das.utils.OperationType;
import dev.sample.ssd.das.vos.LoanAccountVO;


@Path("loan-account")
public class LoanAccount {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pending-loan-accounts")
    public Response getPendingLoanAccounts() {
    	System.out.println("In get pneding loan list");
    	JSONArray usersArray = new JSONArray();
    	try {
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.getPendingLAQuery();
				ResultSet rs = stmt.executeQuery(query);

				while (rs.next()) {
					LoanAccountVO laVO = new LoanAccountVO();
					laVO.setValues(rs);
					usersArray.add(laVO.toJSON());
				}
				System.out.println("Returning pending loan list");
				return Response.ok(usersArray, MediaType.APPLICATION_JSON).build();
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
    public Response getLADetails(@PathParam("userid") String userid) {
    	System.out.println("In get loan account details");
    	try {
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.getLAQuery(userid);
				ResultSet rs = stmt.executeQuery(query);
				LoanAccountVO laVO = new LoanAccountVO();

				while (rs.next()) {
					laVO.setValues(rs);
				}
				System.out.println("returning loan account details");
				return Response.ok(laVO.toJSON(), MediaType.APPLICATION_JSON).build();
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
    public Response createLoanAccount(@PathParam("userid") String userid, HashMap<String, String> payload) {
    	System.out.println("In create loan account");
		HashMap<String, String> response = new HashMap<String, String>();
    	try {
    		int loanAccountNo = getLastLoanAccountNumber() + 1;
			Connection conn = DBUtils.getConnection();
			conn.setAutoCommit(false); // here we have multiple updates for a single transaction
			LoanAccountVO laVO = new LoanAccountVO(payload, OperationType.createLA);
			laVO.setLoanAccountNo(loanAccountNo+"");
			try {
				Statement stmt = conn.createStatement();
				
				String query = DBUtils.createLAQuery(userid, laVO);
				stmt.executeUpdate(query);
				
				query = DBUtils.updateUserForLoanQuery(userid, laVO);
				stmt.executeUpdate(query);
				
				response.put("Response", "Loan Account for userid '" + userid + "' created successfully");
				
				conn.commit();
				System.out.println("Loan account created");
				return Response.ok(response, MediaType.APPLICATION_JSON).build();
				
			} catch (Exception e) {
				conn.rollback();
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
    
    private int getLastLoanAccountNumber() throws Exception{
		Connection conn = DBUtils.getConnection();
		Statement stmt = conn.createStatement();
		
		String query = DBUtils.getLastLoanAccountNumberQuery();
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next() ) {
			int acNum = rs.getInt("loan_account_no");
			if( acNum >= 1000000) {
				return acNum;
			}
		}
		return 1000000;
    }
    
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{userid}")
    public Response updateLoanAccount(@PathParam("userid") String userid, HashMap<String, String> payload) {
    	System.out.println("In update loan account");
		HashMap<String, String> response = new HashMap<String, String>();
    	try {
        	LoanAccountVO laVO = new LoanAccountVO(payload, OperationType.updateLA);
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.updateLAQuery(userid, laVO);
				stmt.executeUpdate(query);
				response.put("Response", "Loan Account for userid '" + userid + "' updated successfully");
				System.out.println("Loan account updated");
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