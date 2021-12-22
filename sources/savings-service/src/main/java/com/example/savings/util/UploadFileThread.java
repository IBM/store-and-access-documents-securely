package com.example.savings.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class UploadFileThread implements Runnable {

	private static final String uploadUrl;
	private static Logger logger =  Logger.getLogger(UploadFileThread.class.getName());

	static {
		// load uploadUrl from resources config
		ClassLoader classLoader = UploadFileThread.class.getClassLoader();
		// Getting resource(File) from class loader
		File configFile = new File(classLoader.getResource("config.properties").getFile());
		FileReader reader = null;
		try {
			reader = new FileReader(configFile);
		} catch (FileNotFoundException e1) {
			logger.log(Level.SEVERE,"config.properties file not found!");
			e1.printStackTrace();
		}
		Properties config = new Properties();
		try {
			config.load(reader);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"config.properties file not loaded!");
			e.printStackTrace();
		}
		uploadUrl = config.getProperty("documentSvcUrl");
	}

	private File uploadFile;

	public UploadFileThread(File file) throws IOException {
		this.uploadFile = file;
	}

	/**
	 * Upload file
	 * 
	 * @param file
	 * @return
	 */
	private boolean uploadFile() {
		int timeout = 100;
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
				.setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		try {
			logger.log(Level.INFO,"New Thread - POST request to COS service starts...");
			HttpPost post = new HttpPost(uploadUrl);
			FileBody fileBody = new FileBody(this.uploadFile, ContentType.DEFAULT_BINARY);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("File", fileBody);
			HttpEntity entity = builder.build();
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			logger.log(Level.INFO,"POST request to COS service ends..." + response.getStatusLine());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error uploading file to Object Storage");
			e.printStackTrace();
			return false;
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error closing connection!");
				e.printStackTrace();
			}
		}
		return true;
	}

	

	@Override
	public void run() {
		uploadFile();	
	}

}