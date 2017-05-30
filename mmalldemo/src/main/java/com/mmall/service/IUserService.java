package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * 
 * @author Administrator
 *
 */
public interface IUserService {
	//这是用户登录接口
	ServerResponse<User> login(String username,String password);
	//这是用户注册接口
	ServerResponse<String> resigter(User user);
	//这是校验用户名和email是否存在的接口
	ServerResponse<String> checkVaild(String str,String type);
	//这是查询问问题
	ServerResponse<String>  selectQuestion(String username);
	//这是检查问题的答案
	ServerResponse<String> checkAnswer(String username,String question,String answer);
	//忘记密码的重置密码
	ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
	//登录状态下的重置密码
	ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);
	//更新用户信息
	ServerResponse<User> updateInformation(User user);
	//获取用户的详细信息
	ServerResponse<User> getInformation(Integer userId);
	//下面是校验是否是管理员的接口
	ServerResponse checkAdminisRole(User user);
}
