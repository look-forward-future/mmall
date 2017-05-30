package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

@Controller
@RequestMapping("/user/") //这个注解是将我们的请求地址打到/user/这个命名空间下
public class userController {
	//将IUserService注入进来
	@Autowired
	private IUserService iUserService;
	
	
/**
 * 用户登录
 * @param username
 * @param password
 * @param session
 * @return
 */
	@RequestMapping(value="login.do",method=RequestMethod.POST )
	@ResponseBody   //这个注解的意思是：当我们返回的时候自动通过springmvc的jocken插件将我们的返回值序列化成json
	public ServerResponse<User> login(String username,String password,HttpSession session){
		//servlet-->mybatis->dao
		
		ServerResponse<User> response = iUserService.login(username, password);
		if(response.isSuccess()){
			//如果用户登录响应成功的话，就将其保存在session中
			session.setAttribute(Const.CURRENT_USER, response.getData());
		}
		
		return response;
		
	}
	
	//下面是用户登出方法
	@RequestMapping(value="logout.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
	
		return ServerResponse.createBySuccess();
	}
	
	//这是用户注册的方法
	@RequestMapping(value="register.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user){
		return iUserService.resigter(user);
	}
	
	//下面是校验我们的用户名和email是否存在
	@RequestMapping(value="check_vaild.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkVaild(String str,String type){
		return iUserService.checkVaild(str, type);
	}
	//这是获取用户的个人信息
	@RequestMapping(value="get_user_info.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){
		//从session中获取User
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user != null){
			return ServerResponse.createBySuccess(user);
		}
		return ServerResponse.createByMessage("用户未登录，无法获取当前用户的信息");
	}
	
	//忘记密码中的提示问题
	@RequestMapping(value="forget_get_question.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username){
		return iUserService.selectQuestion(username);
	}
 	
	//忘记密码中的校验问题的答案
	@RequestMapping(value="forget_check_answer.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
		return iUserService.checkAnswer(username, question, answer);
	}
	
	//忘记中的重置密码
	@RequestMapping(value="forget_reset_password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
		return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
	}
	
	//登录状态下的重置密码
	@RequestMapping(value="reset_password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
		
				User user = (User) session.getAttribute(Const.CURRENT_USER);
				if(user == null){
					return ServerResponse.createByMessage("用户未登录");
				}
				return iUserService.resetPassword(passwordOld, passwordNew, user);
	}
	
	//这是更新用户信息
	@RequestMapping(value="update_information.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> updateInformation(HttpSession session,User user){
		//从session中获取用户
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		
		if(currentUser == null){
			return ServerResponse.createByMessage("用户未登录");
		}
		//设置用户的id
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		ServerResponse<User> response = iUserService.updateInformation(user);
		
		if(response.isSuccess()){
			return ServerResponse.createBySuccess("更新用户信息成功", response.getData());
		}
		return response;
	}
	
	//这是获取用户的详细信息，若是用户没有登录，将要强制登录
	@RequestMapping(value="get_information.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getInformation(HttpSession session){
		
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			//当我们传递一个10给前端的时候，前端就会强制登录
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录需要前置登录status=10");
		}
		return iUserService.getInformation(currentUser.getId());
	}
}
