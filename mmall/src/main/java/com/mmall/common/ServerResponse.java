package com.mmall.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 下面是服务器响应类
 * @author Administrator
 *
 */

//这个注解是：将这个类进行json序列化的时候，若响应的属性值为：null，则：不会被序列化成json，传递到前端
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
//下面的类实现了序列化接口
public class ServerResponse<T> implements Serializable{
	
	//下面是用户响应的一些属性
	private int status;
	private String msg;
	private T data;
	
	//下面我们来写属性的构造器
	public ServerResponse(int status){
		this.status=status;
	}
	
	public ServerResponse(int status,String msg){
		this.status=status;
		this.msg=msg;
	}
	
	public ServerResponse(int status,T data){
		this.status=status;
		this.data=data;
	}
	
	public ServerResponse(int status,String msg,T data){
		this.status=status;
		this.msg=msg;
		this.data=data;
	}
	
	//下面的注解的意思是：当进行json序列化的时候，isSuccess()方法将会被忽略，不会序列化
	@JsonIgnore
	//接下来我们来写一个响应成功的方法
	public boolean isSuccess(){
		return this.status == ResponseCode.SUCCESS.getCode();
	}
	
	//接着，我们来开发属性
	public int getStatus(){
		return status;
	}
	
	public String getMsg(){
		return msg;
	}
	
	public T getData(){
		return data;
	}
	
	//然后，开发静态方法
	//下面的方法是创建成功，返回响应代码
	public static <T> ServerResponse<T> createBySuccess(){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}
	//创建成功，并返回信息
	public static <T> ServerResponse<T> createBySuccessMessage(String msg){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
		
	}
	//创建成功，返回数据
	public static <T> ServerResponse<T> createBySuccess(T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}
	//创建成功，返回数据和信息
	public static <T> ServerResponse<T> createBySuccess(String msg,T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
	}
	//创建失败，返回失败的状态码
	public static <T> ServerResponse<T> createByError(){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode());
	}
	//创建失败，返回错误信息
	public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
	}
	
	//创建失败，返回错误代码和信息
	public static <T> ServerResponse<T> createByError(int errorCode,String errorMessage){
		return new ServerResponse<T>(errorCode,errorMessage);
	}
}
