package com.example.savings.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class SavingsServiceUtil {

	private static final String COMMA = ",";
	private static final String COLON = ":";
	private static final String DOUBLE_QUOTES = "\"";
	private static final String dataAccessSvcUserUrl;
	private static final String dataAccessSvcSavingUrl;

	static {
		ClassLoader classLoader = SavingsServiceUtil.class.getClassLoader();
		File configFile = new File(classLoader.getResource("config.properties").getFile());
		FileReader reader = null;
		try {
			reader = new FileReader(configFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Properties config = new Properties();
		try {
			config.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataAccessSvcUserUrl = config.getProperty("dataAccessSvcUserUrl");
		System.out.println(dataAccessSvcUserUrl);
		dataAccessSvcSavingUrl = config.getProperty("dataAccessSvcSavingUrl");
		System.out.println(dataAccessSvcSavingUrl);
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
	public static boolean createUser(String user_id, String first_name, String last_name, String mobile_no,
			String address, String email_id) {
		CloseableHttpClient httpClient = null;
		try {
			StringBuffer dataBuffer = new StringBuffer();
			dataBuffer.append("{");
			dataBuffer.append(DOUBLE_QUOTES + "user_id"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+user_id+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"first_name"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+first_name+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"last_name"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+last_name+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"mobile_no"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+mobile_no+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"address"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+address+DOUBLE_QUOTES+COMMA);
			dataBuffer.append(DOUBLE_QUOTES+"email_id"+DOUBLE_QUOTES+COLON+DOUBLE_QUOTES+email_id+DOUBLE_QUOTES);
			dataBuffer.append("}");
			
		    String postData = dataBuffer.toString();
			
			System.out.println(postData);
			String result = "";
			HttpPost post = new HttpPost(dataAccessSvcUserUrl);
			post.setEntity(new StringEntity(postData));
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");

			httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(post);

			result = EntityUtils.toString(response.getEntity());
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public static String getCustomerDetails(String userid) {
		StringBuffer textView = new StringBuffer();
		JSONObject savings = null;
		try {
			HttpClient client = HttpClients.createDefault();
			HttpGet request = new HttpGet(dataAccessSvcUserUrl + "/" + userid);
			HttpResponse response = client.execute(request);

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				textView.append(line);
			}
			System.out.println("Savings account details - "+ textView);
			savings = new JSONObject(textView.toString());
			
			client = HttpClients.createDefault();
			request = new HttpGet(dataAccessSvcSavingUrl + "/" + userid);
			response = client.execute(request);

			// Get the response
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			line = "";
			StringBuffer temp = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				temp.append(line);
			}
			System.out.println("Loan account there or not - "+ temp);
			JSONObject loan = new JSONObject(temp.toString());
			System.out.println(loan.toString());
			Iterator keysIterator = loan.keySet().iterator();
			while (keysIterator.hasNext()) {
			   String key = keysIterator.next().toString();
			   savings.put(key, loan.get(key));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(savings.toString());
		return savings.toString();
	}

	public static void main(String args[]) {
//		 createUser("cust12", "Hund", "Cust", "9876452648", "Hundredth street",
//		 "9752498843", "hundred@hundred.com");
		getCustomerDetails("cust12");
	}

}
