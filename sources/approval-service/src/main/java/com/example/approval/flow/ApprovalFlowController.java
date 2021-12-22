package com.example.approval.flow;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.example.approval.utils.OperationType;
import com.example.approval.vos.LoanAccountVO;
import com.example.approval.vos.SavingsAccountVO;
import com.example.verify.operations.Config;
import com.example.verify.operations.UsersSvc;

@Path("account")
public class ApprovalFlowController {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/savings/pendinglist")
	public Response getPendingSavingsAccounts() {
		System.out.println("In get pending savings list");
		CloseableHttpClient httpClient = null;
		try {
			String url = Config.getPendingSAPath();
			System.out.println("URL = " + url);

			HttpGet get = new HttpGet(url);

			get.setHeader("Accept", "application/json");
			get.setHeader("Content-type", "application/json");
			get.setHeader("Access-Control-Allow-Origin", "*");

			httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(get);
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			// System.out.println("response = " + responseString);
			System.out.println("Savings pending list returned");
			return Response.ok(responseString, MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.header("Access-Control-Allow-Origin", "*").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/loan/pendinglist")
	public Response getPendingLoanAccounts() {
		System.out.println("In get pending loan list");
		CloseableHttpClient httpClient = null;
		try {
			String url = Config.getPendingLAPath();

			HttpGet get = new HttpGet(url);

			get.setHeader("Accept", "application/json");
			get.setHeader("Content-type", "application/json");

			httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(get);
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println("returned pending loan list");

			return Response.ok(responseString, MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.header("Access-Control-Allow-Origin", "*").build();
		}

	}

	// @PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Path("/savings/status")
	// public Response updateSavingsAccountStatus(HashMap<String, String> payload) {
	// System.out.println("In update savings account status");
	// String responseMessage = "";
	// try {
	// SavingsAccountVO saVO = new SavingsAccountVO(payload,
	// OperationType.updateSA);
	// // create user in Security Verify
	// int resp = UsersSvc.createUser(saVO.getUserid(), saVO.getLastName(),
	// saVO.getFirstName(), saVO.getEmailId(),
	// saVO.getMobileNo());
	// if (resp != 200 && resp != 201)
	// throw new Exception("User was not created in security verify");
	// responseMessage = responseMessage + "User created successfully in Security
	// Verify. ";
	// // update account status
	// updateSAStatus(saVO);
	// responseMessage = responseMessage + "User savings account status successfully
	// in database. ";

	// System.out.println("Savings account status updated");

	// return Response.ok(responseMessage, MediaType.APPLICATION_JSON)
	// .header("Access-Control-Allow-Origin", "*")
	// .header("Access-Control-Allow-Headers", "origin, content-type, accept,
	// authorization")
	// .header("Access-Control-Allow-Credentials", "true")
	// .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS,
	// HEAD").build();

	// } catch (Exception e) {
	// e.printStackTrace();
	// return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
	// .header("Access-Control-Allow-Origin", "*").build();
	// }
	// }

	// @PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Path("/loan/status")
	// public Response updateLoanAccountStatus(HashMap<String, String> payload) {
	// System.out.println("In loan account status update");
	// CloseableHttpClient httpClient = null;
	// try {
	// System.out.println("payload = " + payload.toString());
	// LoanAccountVO laVO = new LoanAccountVO(payload, OperationType.updateLA);

	// String url = Config.getLAURL() + "/" + laVO.getUserid();
	// HttpPut put = new HttpPut(url);
	// put.setEntity(new StringEntity(laVO.toJSON().toString()));

	// put.setHeader("Accept", "application/json");
	// put.setHeader("Content-type", "application/json");

	// httpClient = HttpClients.createDefault();
	// CloseableHttpResponse response = httpClient.execute(put);
	// String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
	// System.out.println("Loan account status updated");

	// return Response.ok(responseString, MediaType.APPLICATION_JSON)
	// .header("Access-Control-Allow-Origin", "*")
	// .header("Access-Control-Allow-Headers", "origin, content-type, accept,
	// authorization")
	// .header("Access-Control-Allow-Credentials", "true")
	// .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS,
	// HEAD").build();

	// } catch (Exception e) {
	// e.printStackTrace();
	// return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
	// .header("Access-Control-Allow-Origin", "*").build();
	// }
	// }

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/savings/status")
	public Response updateSavingsAccountStatusGET(@QueryParam("user_id") String userId,
			@QueryParam("last_name") String lastName,
			@QueryParam("first_name") String firstName, @QueryParam("email_id") String emailId,
			@QueryParam("mobile_no") String mobileNo,
			@QueryParam("status") String status) {
		System.out.println("In update savings account status");
		System.out.println("userId = " + userId);
		System.out.println("lastName = " + lastName);
		System.out.println("firstName = " + firstName);
		System.out.println("emailId = " + emailId);
		System.out.println("mobileNo = " + mobileNo);
		System.out.println("status = " + status);
		String responseMessage = "";
		try {
			// SavingsAccountVO saVO = new SavingsAccountVO(payload,
			// OperationType.updateSA);
			SavingsAccountVO saVO = new SavingsAccountVO();
			saVO.setUserid(userId);
			saVO.setLastName(lastName);
			saVO.setFirstName(firstName);
			saVO.setMobileNo(mobileNo);
			saVO.setEmailId(emailId);
			saVO.setStatus(status);
			saVO.setOpType(OperationType.updateSA);

			// create user in Security Verify
			if (status.toLowerCase().equals("approved") || status.toLowerCase().equals("approve")) {
				int resp = UsersSvc.createUser(saVO.getUserid(), saVO.getLastName(), saVO.getFirstName(),
						saVO.getEmailId(), saVO.getMobileNo());
				if (resp != 200 && resp != 201) {
					System.out.println("User was not created in security verify");
					throw new Exception("User was not created in security verify");
				}
				responseMessage = responseMessage + "User created successfully in Security Verify. ";
			}
			// update account status
			updateSAStatus(saVO);
			responseMessage = responseMessage + "User savings account status successfully in database. ";

			System.out.println("Savings account status updated");

			return Response.ok(responseMessage, MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.header("Access-Control-Allow-Origin", "*").build();
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/loan/status")
	public Response updateLoanAccountStatusGET(@QueryParam("user_id") String userId,
			@QueryParam("status") String status,
			@QueryParam("approver_id") String approverId, @QueryParam("reject_reason") String rejectReason) {

		System.out.println("In loan account status update");
		System.out.println("userId = " + userId);
		System.out.println("status = " + status);
		System.out.println("approverId = " + approverId);
		System.out.println("rejectReason = " + rejectReason);

		CloseableHttpClient httpClient = null;
		try {
			// LoanAccountVO laVO = new LoanAccountVO(payload, OperationType.updateLA);
			LoanAccountVO laVO = new LoanAccountVO();
			if (userId == null || status == null) {
				throw new Exception("Either userid or status is not provided");
			}
			laVO.setUserid(userId);
			laVO.setStatus(status);
			if (approverId != null) {
				laVO.setApproverId(approverId);
			}
			if (rejectReason != null) {
				laVO.setRejectReason(rejectReason);
			}

			String url = Config.getLAURL() + "/" + laVO.getUserid();
			HttpPut put = new HttpPut(url);
			put.setEntity(new StringEntity(laVO.toJSON().toString()));

			put.setHeader("Accept", "application/json");
			put.setHeader("Content-type", "application/json");

			httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(put);
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println("Loan account status updated successfully");

			return Response.ok(responseString, MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.header("Access-Control-Allow-Origin", "*").build();
		}
	}

	private void updateSAStatus(SavingsAccountVO saVO) throws Exception {
		CloseableHttpClient httpClient = null;

		String url = Config.getSAURL() + "/" + saVO.getUserid();
		HttpPut put = new HttpPut(url);
		put.setEntity(new StringEntity(saVO.toJSON().toString()));

		put.setHeader("Accept", "application/json");
		put.setHeader("Content-type", "application/json");

		httpClient = HttpClients.createDefault();
		httpClient.execute(put);
	}

}
