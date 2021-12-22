package com.example.loan.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;

import com.example.loan.util.LoanServiceUtil;
import com.example.loan.util.UploadFileThread;



@Path("loan")
@RequestScoped
public class LoanAccountEndpoint {
	
	private static Logger logger = Logger.getLogger(LoanAccountEndpoint.class.getName());

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createLoanAccount(@javax.ws.rs.core.Context javax.servlet.http.HttpServletRequest request) {
		boolean successFlag = true;
		try {
			String userid = request.getParameter("userid");
			logger.log(Level.INFO, "User id:"+userid);
			logger.log(Level.INFO, "Number of parts:" + request.getParts().size());
			javax.servlet.http.Part incomeproof = request.getPart("incomeprooffile");
	
			InputStream idInitialStream = incomeproof.getInputStream();
			String extension = FilenameUtils.getExtension(incomeproof.getSubmittedFileName());
			File idtargetFile = new File(userid+"-"+"incomeproof"+"."+extension);
		    
		    OutputStream idOutStream = new FileOutputStream(idtargetFile);

		    byte[] buffer = new byte[8 * 1024];
		    int bytesRead;
		    while ((bytesRead = idInitialStream.read(buffer)) != -1) {
		        idOutStream.write(buffer, 0, bytesRead);
		    }
		    idInitialStream.close();
		    idOutStream.flush();
		    idOutStream.close();
		    
		   
		    UploadFileThread uploadTaxId = new UploadFileThread(idtargetFile);
		    Thread uploadTaxIdThread = new Thread(uploadTaxId);
			uploadTaxIdThread.start();
		  	

			// Create a record with account pending status on RDBMS
			successFlag = LoanServiceUtil.createLoanAccount(userid, request.getParameter("loan_type"), request.getParameter("loan_amount"), request.getParameter("tax_id"), request.getParameter("income"));
			
		} catch (Exception e) {
			successFlag = false;
			logger.log(Level.SEVERE, "Error creating loan account!");
			e.printStackTrace();
		}
		
		if (!successFlag)
			return Response.status(500).entity("Error creating loan account. Please check server logs.").build();
	
		return Response.ok("{\"message\":\"Loan account created\"}").build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerDetails(@javax.ws.rs.core.Context javax.servlet.http.HttpServletRequest request) {
		String userDetails = LoanServiceUtil.getCustomerDetails(request.getParameter("userid"));
		if (userDetails == null)
			return Response.status(500).entity("Error getting user. Please check server logs.").build();
	    return Response.ok(userDetails).build();
	}

}
