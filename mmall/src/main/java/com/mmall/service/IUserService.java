package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * 下面是一个接口类
 * @author Administrator
 *
 */


public interface IUserService {
	//下面是我们用户登录的接口
	ServerResponse<User> login(String username,String password);
	//下面是用户注册的接口
	ServerResponse<String> register(User user);
	//这是我们校验用户名和邮箱的接口
	ServerResponse<String> vaildUsernameAndEmail(String str,String type);
	//这是密码提示问题的接口
	ServerResponse<String> forgetGetQuestion(String username);
	//这是密码提示问题的答案的接口
	ServerResponse<String> forgetGetAnswer(String username,String question,String answer);
	//忘记密码之重置密码的接口
	ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
	//登录状态下的重置密码的接口
	ServerResponse<String> loginResetPassword(User user,String passwordOld,String passwordNew);
	//这是更新用户个人信息的接口
	ServerResponse<User> updateUserInfo(User user);
	//这是验证当前用户是否是管理员的接口
	ServerResponse checkAdminRole(User user);
}
