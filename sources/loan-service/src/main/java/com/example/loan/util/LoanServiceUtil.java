package com.example.loan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class LoanServiceUtil {

	private static final String dataAccessSvcUrl;
	
	private static final String COMMA = ",";
	private static final String COLON = ":";
	private static final String DOUBLE_QUOTES = "\"";
	
	private static Logger logger = Logger.getLogger(LoanServiceUtil.class.getName());

	static {
		ClassLoader classLoader = LoanServiceUtil.class.getClassLoader();
		File configFile = new File(classLoader.getResource("config.properties").getFile());
		FileReader reader = null;
		try {
			reader = new FileReader(configFile);
		} catch (FileNotFoundException e1) {
			logger.log(Level.SEVERE, "The file config.properties was not found!");
			e1.printStackTrace();
		}
		Properties config = new Properties();
		try {
			config.load(reader);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error loading config.properties!");
			e.printStackTrace();
		}
		dataAccessSvcUrl = config.getProperty("dataAccessSvcUrl");
		logger.log(Level.INFO, "Data Access Service URL:" + dataAccessSvcUrl);
	}

	/**
	 * Create user
	 * 
	 * @param user_id
	 * @param first_name
	 * @param last_name
	 * @param mobile_no
	 * @param address
	 * @param national_id
	 * @param email_id
	 * @return
	 */
	public static boolean createLoanAccount(String user_id, String loan_type, String loan_amount, String tax_id, String income) {
		CloseableHttpClient httpClient = null;
		logger.log(Level.INFO, "Create loan account - start");
		try {
			
			Date date = new Date();  
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
		    String strDate= formatter.format(date); 
		    StringBuffer dataBuffer = new StringBuffer();
			dataBuffer.append("{");
			dataBuffer.append(DOUBLE_QUOTES + "status"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+"PendingApproval"+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"apply_date"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+strDate+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"loan_type"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+loan_type+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"tax_id"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+tax_id+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"income"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+income+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"loan_amount"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+loan_amount+DOUBLE_QUOTES);
			dataBuffer.append("}");
		
			String postData = dataBuffer.toString();
			System.out.println(postData);
			String result = "";
			HttpPost post = new HttpPost(dataAccessSvcUrl+"/"+user_id);
			post.setEntity(new StringEntity(postData));
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");

			httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(post);

			result = EntityUtils.toString(response.getEntity());
			logger.log(Level.INFO,result);
			logger.log(Level.INFO, "Create loan account - End");

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error creating loan account!");
			e.printStackTrace();
			return false;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error closing connection!");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public static String getCustomerDetails(String userid) {
		StringBuffer textView = new StringBuffer();
		logger.log(Level.INFO, "Get loan account details - start");
		
		try {
			HttpClient client = HttpClients.createDefault();
			HttpGet request = new HttpGet(dataAccessSvcUrl + "/" + userid);
			HttpResponse response = client.execute(request);

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				textView.append(line);

			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error getting loan details!");
			e.printStackTrace();
			return null;
		}
		logger.log(Level.INFO, textView.toString());
		return textView.toString();
	}

	public static void main(String args[]) {
		createLoanAccount("cust12", "Vehicle","10000","BDGPF7654G","5000");
		//System.out.println(getCustomerDetails("cust10"));
	}

}
