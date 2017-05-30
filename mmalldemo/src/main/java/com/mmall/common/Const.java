package com.mmall.common;
/**
 * 这个类存储的都是常量
 * @author Administrator
 *
 */

public class Const {

	public static final String CURRENT_USER = "currentUser";
	
	public static final String EMAIL = "email";
	
	public static final String USERNAME = "username";
	
	//下面是role接口
	public interface Role{
		int ROLE_CUSTOMER = 0;//普通用户
		int ROLE_ADMIN = 1;//管理员
	}
}
