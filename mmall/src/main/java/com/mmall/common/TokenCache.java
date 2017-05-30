package com.mmall.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 这是将forgetToken保存在本地缓存中
 * @author Administrator
 *
 */

public class TokenCache {  
	//声明logger
	private static Logger logger =LoggerFactory.getLogger(TokenCache.class);
	//声明一个本地缓存
	private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12,TimeUnit.HOURS)
			.build(new CacheLoader<String,String>(){
				
			//下面是默认数据加载实现的方法，当我们调用get方法时，若key没有对应的值，就会调用下面的方法
			public String load(String s) throws Exception{
				return "null";
			}
	});
	
	//这是将token添加到本地缓存中的方法
	public static void setKey(String key,String value){
		localCache.put(key, value);
	}
	
	//这是从本地缓存中获取token
	public static String getKey(String key){
		//首先，将value值置为：null
		String value = null;
		try {
			 value = localCache.get(key);
			 if("null".equals(value)){
				 return null;
			 }
			 return value;
		} catch (ExecutionException e) {
			//打印异常堆栈
			logger.error("localCache get error", e);
			return null;
		}
		
	}
	

}
