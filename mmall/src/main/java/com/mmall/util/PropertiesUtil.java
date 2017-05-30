package com.mmall.util;



import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
	
	 //首先，我们定义一个日志
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	//在声明一个Properties
	private static Properties props;
	
	/*由于我们希望在Tomcat启动的时候就能读取到里面的配置，使用一个静态代码块
	 * 静态代码块的执行顺序是：最先执行的是静态代码块，且只会执行一次，然后，执行普通代码块，最后执行的是构造代码块
	 */
	static{
		//将我们的FTP的配置文件放进来
		String fileName = "mmall.properties";
		props = new Properties();
		
		try {
			props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
		 
		} catch (IOException e) {
			logger.error("配置文件读取异常", e);
		}
		
	}
	
	//接着，我们来写两个方法，通过mmall.properties配置文件中的key来获取value
	public static String getProperty(String key){
		String value = props.getProperty(key.trim());
		if(StringUtils.isBlank(value)){
			return null;
		}
		return value.trim();
	}
	
	public static String getProperty(String key,String defaultValue){
		String value = props.getProperty(key.trim());
		if(StringUtils.isBlank(value)){
			value = defaultValue;
		}
		return value.trim();
	}
	
}
