package com.mmall.service.impl;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.StringUtil;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;

/**
 * 这是业务逻辑的实现类
 * @author Administrator
 *
 */

@Service("iUserService")
public class UserServiceImpl implements IUserService{
	
	//这里声明一个Token的前缀
	private static final String TOKEN_PREFIX = "token_";
	
	@Autowired
	private UserMapper userMapper;
	
	public ServerResponse<User> login(String username,String password) {
		//首先，我们去:com.mmall.dao中，定义一个判断用户名是否存在的接口，在接着到接口的实现类：com.mmall.service下面的mappers包下面的userMapper.xml中去写实现
		//最后，回到这个方法中来调用这个接口
		if(username == null){
			return ServerResponse.createByErrorMessage("用户名不能为空！");
		}
		
		int resultCount = userMapper.selectUsername(username);
		if(resultCount == 0){
			return ServerResponse.createBySuccessMessage("用户名不存在");
		}
		
		//接下来，在判断密码是否正确,这里要注意我们的密码需要使用MD5加密
		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectLogin(username, md5Password);
		if(user == null){
			return ServerResponse.createByErrorMessage("密码错误");
		}
		//此时，将密码置为空，是为了避免将我们密码传递到前端，造密码泄露
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess("用户登录成功!", user);
	}
	
	//下面我们来实现用户注册的功能
	public ServerResponse<String> register(User user){
		//首先，要判断用户名和邮箱在我们的数据库中是否存在
		int resultCount = userMapper.selectUsername(user.getUsername());
		if(resultCount > 0){
			return ServerResponse.createBySuccessMessage("用户名已存在");
		}
		//接着，我们要验证邮箱是否存在
		resultCount = userMapper.selectEmail(user.getEmail());
		if(resultCount > 0){
			return ServerResponse.createBySuccessMessage("邮箱已存在！");
		}
		//然后，我们来设置一下登录的role(角色)，这里是普通用户登录
		user.setRole(Const.Role.ROLE_CUSTOMER);
		//由于我们要将注册的用户插入到数据库之中去，然而数据库中的密码是使用MD5加密后的，故，在这里我们要将我们的注册密码进行MD5加密
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		//接着，我们就可以将用户对象插入到数据库中，通过判断插入的条数来判断用户注册是否成功
		resultCount = userMapper.insert(user);
		if(resultCount == 0){
			return ServerResponse.createByErrorMessage("用户注册失败！");
		}
		return ServerResponse.createBySuccessMessage("用户注册成功！");
	}
	
	//下面是验证用户名和邮箱是否存在
	public ServerResponse<String> vaildUsernameAndEmail(String str,String type){
		//首先，我们要对type这个属性进行空判断
		if(StringUtils.isNotBlank(type)){
			//这是当type为：username时，这个方法就会调用：username的接口方法
			if(Const.USERNAME.equals(type)){
				int resultCount =  userMapper.selectUsername(str);
				if(resultCount >0 ){
					return ServerResponse.createByErrorMessage("用户名已存在！");
				}
			}
			
			if(Const.EMAIL.equals(type)){
				int resultCount = userMapper.selectEmail(str);
				if(resultCount > 0){
					return ServerResponse.createByErrorMessage("邮箱已存在！");
				}
			}
		}
		else{
			return ServerResponse.createByErrorMessage("参数错误！");
		}
		return ServerResponse.createBySuccessMessage("校验成功！");
		
	}
	
	//这是密码提示问题
	public ServerResponse<String> forgetGetQuestion(String username){
		//首先，要判断一下，username是否存在
		ServerResponse<String> response = this.vaildUsernameAndEmail(username,Const.USERNAME);
		if(response.isSuccess()){
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//接着，我们就要判断这个用是否设置了密码提示问题
		 String question = userMapper.selectQuestionByUsername(username);
		 //当从数据库中查询出的问题不为空，就将这些question返回前端
		if(question != null){
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByErrorMessage("该用户未设置密码提示问题！");
	}
	
	//下面是验证密码提示问题的答案
	public ServerResponse<String> forgetGetAnswer(String username,String question,String answer){
		//首先，要判断一下，username是否存在
		ServerResponse<String> response = vaildUsernameAndEmail(username,Const.USERNAME);
		if(response.isSuccess()){
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//然后，我们在接口中去写一个验证密码的问题的答案的接口后，并在下面调用
		int resultCount = userMapper.selectAnswer(username, question, answer);
		if(resultCount == 0){
			return ServerResponse.createByErrorMessage("提示问题的答案不正确！");
		}
		//接着，我们生成一个forgetToken字符串，并将其放到本地cache（内存）中
		String forgetToken = UUID.randomUUID().toString();
		//然后，我们在com.mmall.common 中创建一个类名：TokenCache，在这个类中声明一个本地缓存、将生成的token放入本地缓存中的方法和从缓存中获取token的方法
		//声明完后，我们就回到这里，调用方法，将我们生成的TokenCahce字符串添加到本地缓存中
		TokenCache.setKey(TOKEN_PREFIX+username, forgetToken);
		return ServerResponse.createBySuccess("提示问题的答案正确！", forgetToken);
	}
	
	//这是我们忘记密码重置密码
	public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
		//首先，我们来验证一下forgetToken空判断
		if(StringUtils.isBlank(forgetToken)){
			return ServerResponse.createByErrorMessage("参数错误，forgetToken需要传递！");
		}
		//接着，要判断一下，username是否存在
		ServerResponse<String> response = this.vaildUsernameAndEmail(username,Const.USERNAME);
		if(response.isSuccess()){
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//然后，从本地缓存中获取token
		String token = TokenCache.getKey(TOKEN_PREFIX+username);
		if(StringUtils.isBlank(token)){
			return ServerResponse.createByErrorMessage("token无效或者过期！");
		}
		//接着，就可以将我们传递的forgetToken和从本地缓存获取的token进行比较
		if(StringUtils.equals(forgetToken, token)){
			//表示：用户回答提示问题正确，可以开始修改密码啦
			String MD5Password = MD5Util.MD5EncodeUtf8(passwordNew);
			//如何让数据库中的原始密码也更改过来啦，需要在userMapper中创建一个接口
			int count  = userMapper.updatePasswordByUsername(username, MD5Password);
			if(count > 0){
				return ServerResponse.createBySuccessMessage("更新密码成功！");
			}
			return ServerResponse.createByErrorMessage("更新密码失败！");
		}
		else{
			return ServerResponse.createByErrorMessage("token错误，需要重新获取token");
		}
	}
	
	//登录状态下的重置密码
	public ServerResponse<String> loginResetPassword(User user,String passwordOld,String passwordNew){
		//首先，要对passwordOld进行：MD5加密
		String MD5Password = MD5Util.MD5EncodeUtf8(passwordOld);
		
		int count = userMapper.selectPasswordByUsername(user.getUsername(), MD5Password);
		if(count == 0){
			return ServerResponse.createByErrorMessage("密码输入错误！");
		}
		//接着，就可以修改我们的密码
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if(updateCount == 0){
			return ServerResponse.createByErrorMessage("更新用户密码失败！");
		}
		return ServerResponse.createBySuccessMessage("更新用户密码成功！");
	}
	
	//更新用户的个人信息
	public ServerResponse<User> updateUserInfo(User user){
		//用户名是不会被更新的
		//接下来，我们要校验一下有效是否存在，在id!=userId(即：当前用户)的情况下，数据库中是否存在这样的email,若存在，说明：该email已经被其他的用户所占用，无法更新
		int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
		if(resultCount > 0){
			return ServerResponse.createByErrorMessage("该email已被占用！");
		}
		User updateUser = new User();
		
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());
		
		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if(updateCount == 0){
			return ServerResponse.createByErrorMessage("更新用户个人信息失败！");
		}
		return ServerResponse.createBySuccess("更新用户的个人信息成功！", updateUser);
	}
	
	//backend 验证是否是管理员登录
	public ServerResponse checkAdminRole(User user){
		if(user != null || user.getRole().intValue() == Const.Role.ROLE_ADMIN){
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}
	


}
