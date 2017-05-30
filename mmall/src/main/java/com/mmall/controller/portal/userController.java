package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;









import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

/**
 * 这是用户控制层类
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value="/user/")
public class userController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping( value="login.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(HttpSession session,String username,String password){
	
		 ServerResponse<User> response = iUserService.login(username, password);
		if(response.isSuccess()){
			session.setAttribute(Const.CURRENT_USER, response.getData());
			return response;
		}
		return response;
		
	}
	
	/**
	 * 退出登录
	 * @param session
	 * @return
	 */
	@RequestMapping(value="loginOut.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> loginOut(HttpSession session){
		//首先，我们要判断用户是否处于登录状态
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorMessage("用户未登录，无法进行退出操作！");
		}
		//从session中将其删除
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.createBySuccessMessage("退出登录成功");
	}
	
	/**
	 * 这是用户注册
	 * @param user
	 * @return
	 */
	@RequestMapping(value="register.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> userRegister(User user){
		return iUserService.register(user);
	}
	
	/*
	 * 验证用户名和邮箱是否存在
	 */
	@RequestMapping(value="vaild_parameter.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> vaildUsernameAndEmail(String str,String type){
		return iUserService.vaildUsernameAndEmail(str, type);
	}
	
	/*
	 * 获取用户的个人信息
	 */
	@RequestMapping(value="get_info.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){
		//首先，我们要判断当前用户是否处于登录状态
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		//这是当从session中获取的user不为空时，返回user对象
		if(user != null){
			return ServerResponse.createBySuccess(user);
		}
		return ServerResponse.createByErrorMessage("用户未登录，无法获取用户的个人信息！");
	}
	
	/*
	 * 忘记密码的提示问题
	 */
	@RequestMapping(value="forget_question.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username){
		return iUserService.forgetGetQuestion(username);
	}
	
	/**
	 * 验证密码提示答案
	 */
	@RequestMapping(value="forget_get_answer.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetAnswer(String username,String question,String answer){
		return iUserService.forgetGetAnswer(username, question, answer);
	}
	
	/**
	 * 忘记密码重置密码
	 */
	@RequestMapping(value="forget_reset_password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
		return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
	}
	
	/**
	 * 登录状态下的重置密码
	 */
	@RequestMapping(value="login_reset_password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> loginResetPassword(HttpSession session,String passwordOld,String passwordNew){
		//首先，判断用户是否登录
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorMessage("该用户未登录！");
		}
		return iUserService.loginResetPassword(user, passwordOld, passwordNew);
	}
	
	/**
	 * 更新用户的个人信息
	 */
	@RequestMapping(value="update_info.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> updateUserInfo(HttpSession session,User user){
		//首先，判断用户是否登录
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			return ServerResponse.createByErrorMessage("用户未登录！");
		}
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		
		return iUserService.updateUserInfo(user);
		
	}
	
	/**
	 * 获取用户的详细信息，若用户未登录，则：强制登录
	 */
	@RequestMapping(value="get_infomation.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getInformation(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录需要强制登录，status=10");
		}
		
		User currentUser =userMapper.selectByPrimaryKey(user.getId());
		if(currentUser == null){
			return ServerResponse.createByErrorMessage("获取用户信息失败！");
		}
		//在返回用户对象之前，需要将密码置为空
		currentUser.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess(currentUser);
	}
	
	
	
 }

