package com.mmall.common;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 这是一个长常量类
 * @author Administrator
 *
 */
public class Const {
	
	public static final String CURRENT_USER = "currentUser";
	
	public static final String USERNAME = "username";
	
	public static final String EMAIL = "email";
	
	public interface Role{
		
		int ROLE_CUSTOMER=0;//普通用户
		int ROLE_ADMIN=1;//管理员
	}
	
	//下面我们创建一个接口，来对checked分类
	public interface cart{
		int CHECKED = 1;//在购物车中选中的状态
		int UN_CHECKED = 0;//在购物车中未选中的状态 
		
		String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
		String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
	}
	
	//这是订单状态的枚举
	public enum orderStatusEnum{
		CANCELED(0,"已取消"),
		NO_PAY(10,"未支付"),
		PAID(20,"已付款"),
		SHIPPED(40,"已发货"),
		ORDER_SUCCESS(50,"订单完成"),
		ORDER_CLOSE(60,"订单关闭");
		
		
		//首先声明两个属性
		private int code;
		private String value;
		
		//创建一个构造器
		orderStatusEnum(int code,String value){
			this.code=code;
			this.value=value;
		}
		
		public int getCode(){
			return code;
		}
		
		public void setCode(int code){
			this.code=code;
		}
		
		public String getValue(){
			return value;
		}
		
		public void setValue(String value){
			this.value=value;
		}
		//下面是我们获取枚举的value的方法
		public static orderStatusEnum codeOf(int code){
			for(orderStatusEnum statusEnum:values()){
				if(statusEnum.getCode() == code){
					return statusEnum;
				}
			}
			throw new RuntimeException("没有找到对应的枚举");
			
		}
	}
	
	
	//下面我们创建一个枚举类，来对商品的上下架进行分类
	public enum ProductStatusEnum{
		ON_SALE(1,"在线");
		
		private int code;
		private String value;
		
		//创建一个构造器
		 ProductStatusEnum(int code,String value){
			this.code=code;
			this.value=value;
		}
		
		public int getCode(){
			return code;
		}
		
		public String getStatus(){
			return value;
		}
	}
	
	//下面这个接口类是用来表示：orderBy，动态排序的分类
	public interface ProductListOrderBy{
		//这里为什么要用set，而不用list,是因为：set的contain方法的时间复杂程度为O1,而list的contain方法的时间复杂度为：On,就为了提高效率就使用set
		Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");
	}
	
	//支付宝的状态
	public interface AlipayCallback{
		//这是等待买家付款
		String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
		//交易成功
		String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
		
		//下面是两个返回值，返回给前端
		String RESPONSE_SUCCESS = "success";
		String RESPONSE_FAILED = "failed";
		
	}
	
	//这是支付平台的枚举
	public enum PayPlatformEnum{
		
		ALIPAY(1,"支付宝");
	
		private int code;
		private String value;
		
		//创建一个构造器
		PayPlatformEnum(int code,String value){
			this.code=code;
			this.value=value;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
		
	}
	
	//这是在线支付方式的枚举
	public enum paymentTypeEnum{
		
		ON_LINE_PAY(1,"在线支付");
		
		private int code;
		private String value;
		
		private paymentTypeEnum(int code,String value){
			this.code = code;
			this.value = value;
		}
		
		public int getCode(){
			return code;
		}
		
		public String getValue(){
			return value;
		}
		
		public static paymentTypeEnum codeof(int code){
			//下面是我们遍历这个枚举类中的values
			for(paymentTypeEnum paymentEnum:values()){
				if(paymentEnum.getCode() == code){
					return paymentEnum;
				}
			}
			throw new RuntimeException("没有找到对应的枚举");
		}
	
	}
	
	
	
}
