package com.dfire.test.util;

import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Properties;

import org.apache.log4j.Logger;

public class CommonConstants {
	private static final Logger logger = Logger.getLogger(CommonConstants.class);
	
	/**测试代码中用到的常量定义*/
	public static final String resourcePath;
	public static final String DEFAULT_CONFIG = "config.properties";
	public static final String DEFAULT_HOST;

	public static final String ORDER_JDBCURL;
	public static final String MENU_JDBCURL;

	public static final boolean isWhitelist;
	public static final String basePath;
	
	public static final String HEADER_HOST;
	public static final boolean HTTPS_CLIENT;

	public static final boolean SUB_DOMAIN;
	public static final boolean WITHOUT_HOST;
	
	public static final String jettyPort;
	
	// redis configuration 
	public static final String redisHost;
	public static final String redisDB;
	
	static{
		Properties properties = new Properties();
		resourcePath = ClassLoader.getSystemResource("").getPath();
		logger.info("the resources path is: " + resourcePath);
		basePath = resourcePath + "testcase/";
		try {
			//获取该环境下的配置项
			InputStream is = new FileInputStream(resourcePath + DEFAULT_CONFIG );
			logger.info("parse config file as: " + (resourcePath + DEFAULT_CONFIG ));
			try {
				properties.load(is);
			} finally {
				is.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		DEFAULT_HOST = (String)properties.get("DEFAULT_HOST");

		ORDER_JDBCURL = (String)properties.get("order_jdbc_url");
		MENU_JDBCURL = (String)properties.get("menu_jdbc_url");
		
		jettyPort = (String)properties.get("jettyPort");
		
		redisHost = (String)properties.get("cache.redis.server");
		redisDB = (String)properties.get("cache.redis.database");
		
		String whitelist = (String)properties.get("WHITELIST");
		if (whitelist!=null) {
			if (whitelist.equalsIgnoreCase("true")) {
				isWhitelist = true;
			}else {
				isWhitelist = false;
			}
		}else {
			isWhitelist = false;
		}

	
		//是否用https形式进行请求
		String https = (String)properties.get("https");
		if (https!=null) {
			if (https.equalsIgnoreCase("true")) {
				HTTPS_CLIENT = true;
			}else {
				HTTPS_CLIENT = false;
			}
		}else {
			HTTPS_CLIENT = false;
		}
		//是否用子域名形式进行请求
		String sub_domain = (String)properties.get("SUB_DOMAIN");
		if (sub_domain!=null) {
			if (sub_domain.equalsIgnoreCase("true")) {
				SUB_DOMAIN = true;
			}else {
				SUB_DOMAIN = false;
			}
		}else {
			SUB_DOMAIN = false;
		}
		//请求中是否带host头
		String without_host = (String)properties.get("WITHOUT_HOST");
		if (without_host!=null) {
			if (without_host.equalsIgnoreCase("true")) {
				WITHOUT_HOST = true;
			}else {
				WITHOUT_HOST = false;
			}
		}else {
			WITHOUT_HOST = false;
		}
		HEADER_HOST = (String)properties.get("HEADER_HOST");
	
	}
	

}
