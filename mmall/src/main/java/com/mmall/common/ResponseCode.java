package com.mmall.common;

/**
 * 下面是一个枚举类
 * @author Administrator
 *
 */
public enum ResponseCode {
	
	SUCCESS(0,"SUCCESS"),
	ERROR(1,"ERROR"),
	NEED_LOGIN(10,"NEED_LOGIN"),
	ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");
	
	//下面我们定义属性
	private int code;
	private String desc;
	
	//下面是构造器
	private ResponseCode(int code,String desc){
		this.code=code;
		this.desc=desc;
	}
	
	//在对属性进行开放
	public int getCode(){
		return code;
	}

	public String getDesc() {
		return desc;
	}

	

}
