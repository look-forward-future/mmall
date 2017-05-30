package com.mmall.common;
/**
 * 这是一个枚举类"enum"
 * @author Administrator
 *
 */
public enum ResponseCode {
	//下面就是枚举
	SUCCESS(0,"SUCCESS"), //表示登录成功
	ERROR(1,"ERROR"), //表示登录失败
	NEED_LOGIN(10,"NEED_LOGIN"), //表示强制登录
	ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT"); //表示参数错误
	
	//这是表示的是上面枚举中括号里面的属性定义
	private final int code;
	private final String desc;
	
	//下面是构造器
	ResponseCode(int code,String desc){
		this.code=code;
		this.desc=desc;
	}
	
	public int getCode(){
		return code;
	}
	
	public String getDesc(){
		return desc;
	}
}
