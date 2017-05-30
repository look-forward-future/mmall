package com.mmall.util;

import java.math.BigDecimal;

/**
 * 这里工具类是我们为了：方便以后，每次使用：BigDecimal来计算时，将我们price的类型转换为：String
 * @author Administrator
 *
 */

public class BigDecimalUtil {
	//为了使用我们在本类中进行转换，下面我们写一个私有的构造器
	private BigDecimalUtil(){
		
	}
	
	//下面我们来写BigDecimal的方法
	//这是：加法运算的方法
	public static BigDecimal add(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2);
	}
	//这是减法运算的方法
	public static BigDecimal sub(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2);
	}
	//这是乘法运算的方法
	public static BigDecimal mul(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2);
	} 
	//这是除法运算的方法
	public static BigDecimal div(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);//四舍五入，保留两位小数
	}

}
