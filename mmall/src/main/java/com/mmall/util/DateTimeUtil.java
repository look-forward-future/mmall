package com.mmall.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 这是一个时间转换的工具类
 * @author Administrator
 *
 */

public class DateTimeUtil {

	//使用：joda-time来实现
	//str-->Date
	//Date-->str\
	//这里我们还可以将formatStr抽出来做成一个静态变量
	public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static Date strToDate(String dateTimeStr){
		//下面返回的是DateTimeFormatter格式
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
		DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
		return dateTime.toDate();
	}
	
	//Date-->str
	public static String dateOrStr(Date date){
		if(date == null){
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(STANDARD_FORMAT);
	}
	
	/*public static void main(String[] args){
		System.out.println(DateTimeUtil.dateOrStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
		System.out.println(DateTimeUtil.strToDate("2012-1-1 12:12:23","yyyy-MM-dd HH:mm:ss"));
	}*/

}
