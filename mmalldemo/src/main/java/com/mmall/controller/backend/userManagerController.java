package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

/**
 * 后台管理员登录的controller
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/manage/user")
public class userManagerController {
	//将IUserService注入进来
	@Autowired
	private IUserService iUserService;
	
	@RequestMapping(value="login.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username,String password,HttpSession session){
		ServerResponse<User> response = iUserService.login(username, password);
		if(response.isSuccess()){
			//获取登录成功的user
			User user = response.getData();
			//登录的是管理员
			if(user.getRole() == Const.Role.ROLE_ADMIN){
				session.setAttribute(Const.CURRENT_USER, user);
				return response;
			}
			else{
				return ServerResponse.createByMessage("不是管理员，无法登录");
			}
		}
		return response;
	}
	
}
