package com.example.loan.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class AuthFilter implements Filter {

	private static Properties props = new Properties();

	static {
		try {
			ClassLoader classLoader = AuthFilter.class.getClassLoader();
			InputStream input = classLoader.getResourceAsStream("verify.config");
			props.load(input);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final String authorizationHeaderValue = ((HttpServletRequest)request).getHeader("Authorization");
		String token = "";
	    if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("Bearer")) {
	      token = authorizationHeaderValue.substring(7, authorizationHeaderValue.length());
	    }
		HttpPost post = new HttpPost(props.getProperty("introspectionUrl"));
		List<NameValuePair> urlParameters = new ArrayList();
		urlParameters.add(new BasicNameValuePair("client_id", props.getProperty("clientId")));
		urlParameters.add(new BasicNameValuePair("client_secret", props.getProperty("clientSecret")));
		urlParameters.add(new BasicNameValuePair("token", token));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        String result = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse res = httpClient.execute(post)){
            result = EntityUtils.toString(res.getEntity());
        }
       chain.doFilter(request, response);
	}

}
