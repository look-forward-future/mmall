package com.mmall.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *下面是服务器响应类
 * @author Administrator
 *
 */

//这个注解是：将这个类进行json序列化的时候，若响应的属性值为：null，则：不会被序列化成json，传递到前端
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
//下面这个类实现了序列化接口
public class ServerResponse<T> implements Serializable {

	//下面是用户响应的属性
	private int state;
	private String msg;
	private T data;  //这里我们为什么要将数据类型，定义为：T,因为：便于后面的管理
	
	//下面是构造器
	private ServerResponse(int state){
		this.state=state;
	}
	
	private ServerResponse(int state,T data){
		this.state=state;
		this.data=data;
	}
	
	private ServerResponse(int state,String msg,T data){
		this.state=state;
		this.msg=msg;
		this.data=data;
	}
	
	private ServerResponse(int state,String msg){
		this.state=state;
		this.msg=msg;
	}
	//下面的注解的意思是：当进行json序列化的时候，isSuccess()方法将会被忽略，不会序列化
	@JsonIgnore
	//下面的方法是表示响应成功
    public boolean isSuccess(){
    	return this.state == ResponseCode.SUCCESS.getCode();
    }
    //这是对外开放的方法，供外面的用户访问
    public int getState(){
    	return state;
    }
    
    public String getMsg(){
    	return msg;
    }
    
    public T getData(){
    	return data;
    }
    //这是开放的静态方法，当我们创建成功的话，就会返回
    public static <T> ServerResponse<T> createBySuccess(){
    	return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
  //当我们创建成功后，返回信息
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
    	return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
  //当我们创建成功后，返回数据
    public static <T> ServerResponse<T> createBySuccess(T data){
    	return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
    	return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    
    public static <T> ServerResponse<T> createByError(){
    	return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
  //创建失败以后，返回错误的信息
    public static <T> ServerResponse<T> createByMessage(String errorMessage){
    	return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    //创建一个错误的代码信息
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
    	return new ServerResponse<T>(errorCode,errorMessage);
    }
}

