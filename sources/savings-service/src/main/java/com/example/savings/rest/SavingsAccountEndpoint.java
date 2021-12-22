package com.example.savings.rest;

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

import com.example.savings.util.SavingsServiceUtil;
import com.example.savings.util.UploadFileThread;



@Path("savings")
@RequestScoped
public class SavingsAccountEndpoint {
	
	private static Logger logger = Logger.getLogger(SavingsAccountEndpoint.class.getName());

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createSavingsAccount(@javax.ws.rs.core.Context javax.servlet.http.HttpServletRequest request) {
		boolean successFlag = true;
		try {
			String userid = request.getParameter("userid");
			logger.log(Level.INFO,"Savings account creation endpoint - start");
			logger.log(Level.INFO,"User ID:"+userid);
			logger.log(Level.INFO, "Number of parts in the request:" + request.getParts().size());
			javax.servlet.http.Part idprooffile = request.getPart("taxidfile");
			javax.servlet.http.Part addressprooffile = request.getPart("nationalidfile");
	
			InputStream idInitialStream = idprooffile.getInputStream();
			String extension = FilenameUtils.getExtension(idprooffile.getSubmittedFileName());
			File idtargetFile = new File(userid+"-"+"taxid"+"."+extension);
		    OutputStream idOutStream = new FileOutputStream(idtargetFile);
			logger.log(Level.INFO,"Storing tax id file on Server first");
			  
		    byte[] buffer = new byte[8 * 1024];
		    int bytesRead;
		    while ((bytesRead = idInitialStream.read(buffer)) != -1) {
		        idOutStream.write(buffer, 0, bytesRead);
		    }
		    idInitialStream.close();
		    idOutStream.flush();
		    idOutStream.close();
		    
		    InputStream addressInitialStream = addressprooffile.getInputStream();
		    extension = FilenameUtils.getExtension(addressprooffile.getSubmittedFileName());
			File addresstargetFile = new File(userid+"-"+"nationalid"+"."+extension);
		    OutputStream addressOutStream = new FileOutputStream(addresstargetFile);
			logger.log(Level.INFO,"Storing nationalid file on Server first");
			
		    while ((bytesRead = addressInitialStream.read(buffer)) != -1) {
		        addressOutStream.write(buffer, 0, bytesRead);
		    }
		    addressInitialStream.close();
		    addressOutStream.flush();
		    addressOutStream.close();
			
			// Create a record with account pending status on RDBMS
			successFlag = SavingsServiceUtil.createUser(request.getParameter("userid"), request.getParameter("firstname"),request.getParameter("lastname"),
					request.getParameter("mobilenumber"), request.getParameter("address"), request.getParameter("emailid"));
	
			logger.log(Level.INFO,"Uploading taxid file to COS. Calling COS service");
			
		    UploadFileThread uploadTaxId = new UploadFileThread(idtargetFile);
		    Thread uploadTaxIdThread = new Thread(uploadTaxId);
			uploadTaxIdThread.start();
			
			logger.log(Level.INFO,"Uploading nationalid file to COS. Calling COS service");
			
			UploadFileThread uploadNationalId = new UploadFileThread(addresstargetFile);
		    Thread uploadNationalIdThread = new Thread(uploadNationalId);
		    uploadNationalIdThread.start();
			
	
		} catch (Exception e) {
			successFlag = false;
			e.printStackTrace();
			logger.log(Level.SEVERE,"Error creating user!");
		}
		
		if (!successFlag)
			return Response.status(500).entity("Error creating user. Please check server logs.").build();
		return Response.ok("{\"message\":\"Savings account created\"}").build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerDetails(@javax.ws.rs.core.Context javax.servlet.http.HttpServletRequest request) {
		logger.log(Level.INFO,"Got request : "+request.getParameter("userid")+ " "+request.getRemoteAddr() + " " +request.getRequestURL());
		String userDetails = SavingsServiceUtil.getCustomerDetails(request.getParameter("userid"));
		
		if (userDetails == null)
			return Response.status(500).entity("Error getting user. Please check server logs.").build();
		return Response.ok(userDetails).build();
	}

}
