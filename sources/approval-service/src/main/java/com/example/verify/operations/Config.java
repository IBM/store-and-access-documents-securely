package com.example.verify.operations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	private static Properties props = new Properties();

	static {
		try {
			ClassLoader classLoader = Config.class.getClassLoader();
			InputStream input = classLoader.getResourceAsStream("config/config.properties");
			props.load(input);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private static String baseURL = Config.props.getProperty("DAS_BASE_URL");
	private static String pendingSAPath = Config.props.getProperty("PENDING_SA_PATH");
	private static String pendingLAPath = Config.props.getProperty("PENDING_LA_PATH");
	private static String saPath = Config.props.getProperty("SA_PATH");
	private static String laPath = Config.props.getProperty("LA_PATH");
	private static String userPath = Config.props.getProperty("USER_PATH");
	private static String clientId = Config.props.getProperty("CLIENT_ID");
	private static String clientSecret = Config.props.getProperty("CLIENT_SECRET");
	private static String userURL = Config.props.getProperty("USER_URL");
	private static String tokenURL = Config.props.getProperty("TOKEN_URL");
			
			
	public static String getUserURL() {
		return userURL;
	}

	public static String getTokenURL() {
		return tokenURL;
	}

	public static String getUserPath() {
		return userPath;
	}

	public static String getClientSecret() {
		return clientSecret;
	}

	public static String getClientId() {
		return clientId;
	}

	public static Properties getProps() {
		return props;
	}
	
	public static String getPendingSAPath() {
		return baseURL + pendingSAPath;
	}
	
	public static String getPendingLAPath() {
		return baseURL + pendingLAPath;
	}

	public static String getAFSUserURL() {
		return baseURL + userPath;
	}

	public static String getSAURL() {
		return baseURL + saPath;
	}

	public static String getLAURL() {
		return baseURL + laPath;
	}

}
