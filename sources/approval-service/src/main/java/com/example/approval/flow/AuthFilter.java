package com.example.approval.flow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class AuthFilter implements Filter {

	private static Properties props = new Properties();
	private static Logger logger = Logger.getLogger(AuthFilter.class.getName());

	static {
		try {
			ClassLoader classLoader = AuthFilter.class.getClassLoader();
			InputStream input = classLoader.getResourceAsStream("verify.config");
			props.load(input);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error loading Security Verify configuration.");
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final String authorizationHeaderValue = ((HttpServletRequest) request).getHeader("Authorization");
		String token = null;
		if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("Bearer")) {
			token = authorizationHeaderValue.substring(7, authorizationHeaderValue.length());
		}

		HttpServletResponse resp = (HttpServletResponse) response;
		boolean isValidRequest = true;

		logger.log(Level.INFO, "Http Request method:" + ((HttpServletRequest) request).getMethod());
		if (token == null && ((HttpServletRequest) request).getMethod().equalsIgnoreCase("GET")) {
			logger.log(Level.SEVERE, "No token found!");
			isValidRequest = false;
		}

		if (token != null) {
			HttpPost post = new HttpPost(props.getProperty("introspectionUrl"));
			List<NameValuePair> urlParameters = new ArrayList();
			urlParameters.add(new BasicNameValuePair("client_id", props.getProperty("clientId")));
			urlParameters.add(new BasicNameValuePair("client_secret", props.getProperty("clientSecret")));
			urlParameters.add(new BasicNameValuePair("token", token));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			String result = "";
			try (CloseableHttpClient httpClient = HttpClients.createDefault();
					CloseableHttpResponse res = httpClient.execute(post)) {
				result = EntityUtils.toString(res.getEntity());
				logger.log(Level.INFO, "Token introspection results:" + result);
				logger.log(Level.INFO, "Http Request method:" + ((HttpServletRequest) request).getMethod());
				if (((HttpServletRequest) request).getMethod().equalsIgnoreCase("GET")) {
					logger.log(Level.INFO, "Inside GET condition");
					JSONObject tokenIntro = new JSONObject(result);
					if (tokenIntro.getBoolean("active") == false) {
						isValidRequest = false;
					}
				}
			}

		}
		if (isValidRequest)
			chain.doFilter(request, response);
		else {
			resp.setStatus(401);
			resp.getWriter().print("401 - Unauthorized request");
		}
	}

}
