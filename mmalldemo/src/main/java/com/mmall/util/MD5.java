package com.mmall.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 下面这是MD5加密
 * @author Administrator
 *
 */

public class MD5 {

	//这是MD5加密的算法
	private static String byteToArrayHexString(byte[] bt){
		//首先，创建定义定义一个StringBuffer字符串，它是一个内容可变的字符串
		StringBuffer resultSb = new StringBuffer();
		//遍历一下，这个byte[]字节数组
		for(int i = 0;i<bt.length;i++){
			resultSb.append(byteToHexString(bt[i]));
		}
		return resultSb.toString();
	}
	
	//在定义一个方法，将单个字符转换成HexString的方法
	private static String byteToHexString(byte b){
		int n = b;
		if(n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return HexDigits[d1]+HexDigits[d2];
	}
	
	//在定义一个全局变量，是由16进制的16个数组成的一个字符数组
	private static final String[] HexDigits = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
	
	
	/**
	 * 下面写一个对字符串进行MD5加密的方法
	 * @param origin  这是：要加密的字符串
	 * @param charsetName 这是：加密的字符集
	 * @return

	 */
	private static String MD5Encode(String origin,String charsetName) {
		//首先，定义一个字符串
		String resultString = null;
		//初始化一下，要加密的字符串
		resultString = new String(origin);
		
		try {
			//使用：MessageDigest(信息摘要)对象，定义使用的解码方法
			MessageDigest md = MessageDigest.getInstance("MD5");
			//判断一下，加密字符集是否为空
			if(charsetName == null || "".equals(charsetName)){
				resultString = byteToArrayHexString(md.digest(resultString.getBytes()));
			}
			else
				resultString = byteToArrayHexString(md.digest(resultString.getBytes(charsetName)));
			
		} catch (Exception e) {
			
		}
		return resultString.toUpperCase();
		
	}
}
