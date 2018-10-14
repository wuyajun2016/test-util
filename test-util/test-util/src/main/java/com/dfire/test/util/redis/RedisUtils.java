package com.dfire.test.util.redis;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.dfire.test.util.CommonConstants;

public class RedisUtils {
	
	 private static final Logger logger = Logger.getLogger(RedisUtils.class);
	 private static final String redisHost = CommonConstants.redisHost;
	 private static final String dbIndex = CommonConstants.redisDB;
	 
	 public static Jedis createJedis() {
		 Jedis jedis = null;
		 try{
			 String host = redisHost.split(":")[0];
			 int port = 7091;
			 int selectedDB = Integer.parseInt(dbIndex);
			 jedis = new Jedis(host, port);
			 jedis.select(selectedDB);		 
		 }catch(Exception e){
			 logger.info("fail to create connection to redis");
			 logger.error(e.toString());
		 }
		 return jedis;
	 }

	 public static Jedis createJedis(String host, int port, String passwrod) {
		 Jedis jedis = new Jedis(host, port);
		 if (!StringUtils.isNotBlank(passwrod))
			 jedis.auth(passwrod);
		 return jedis;
	 }
	 
	 public static Jedis createJedis(String host, int port, int database) {
		 Jedis jedis = new Jedis(host, port);
		 jedis.select(database);
		 return jedis;
	 }
	 
	 
	 public static boolean cleanCache(Jedis jedis, String key){
		 if(null != key){
			 jedis.del(key);
			 return true;
		 }
			
		 return false;
	 }
	 
	 public static boolean setCache(Jedis jedis, String key, String value){
		 if(null != key){
			 jedis.set(key, value);
			 return true;
		 }
			
		 return false;
	 }

	 public static String getCache(Jedis jedis, String key){
		 
		 String value = null;
		 if(null != key){
			 value = jedis.get(key);
		 }
			
		 return value;
	 }
	 
	 public static Long deleteCache(Jedis jedis, String key){
		 
		 long result = jedis.del(key);
			
		 return result;
	 }
	 
	 
}
