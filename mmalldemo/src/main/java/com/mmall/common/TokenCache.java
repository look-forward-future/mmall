package com.mmall.common;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


public class TokenCache {
	//定义logger日志
	private static Logger logger=LoggerFactory.getLogger(TokenCache.class);
	/*声明一个静态的内存块LoadingCache<String,String>，通过CacheBuilder.newBuilder()来构建chache,在使用(initialCapacity(1000))来设置初始化容量,
	 * 接着我们调用maximumSize(10000)方法时，即：当cache缓存的最大容量当超过10000时，guava的这个cache会使用LRU算法(最小缓存算法)来移除缓存项
	 * 然后，调用方法：expireAfterAccess(int a,TimeUnit.HOURS),来设置有效期,其中，参数a为：int型，第二个参数为：单位
	 *在去new build(),这个括号中我们可以使用一个匿名函数，这个函数的作用是：数据加载实现
	 */
	private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String,String>(){
		//下面的方法是一个默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，则调用下面的方法
		@Override 
		public String load(String s) throws Exception{
			//这里我们要将null改为：“null”  ,因为：在下面的get方法中，若key没有对应的值，则：会返回“null”,如果我们不将null改为："null"的话，在下面的get方法中，就无法对value进行空判断
			return "null";
		}
	});
	//下面的方法是：添加token到本地缓存的方法
	public static void setKey(String key,String value){
		localCache.put(key, value);
	}
	//从本地缓存中获取指定的token
	public static String getKey(String key){
		//首先，初始化一下value
		String value=null;
		try{
			value=localCache.get(key);
			if("null".equals(value)){
				return null;
			}
			return value;
			
		}catch(Exception e){
			//打印异常
			logger.error("localCache get error",e);
			
		}
		return null;
	}
}
