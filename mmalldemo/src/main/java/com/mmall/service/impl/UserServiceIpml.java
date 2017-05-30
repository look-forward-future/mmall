package com.mmall.service.impl;

import java.util.UUID;



import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;

/**
 * 
 * @author Administrator
 *
 */
//这个注解的意思是：具有服务层的作用，controller层可以调用Service来实现功能
@Service("iUserService")
public class UserServiceIpml implements IUserService{
	
	public static final String TOKEN_PREFIX="token_";
	//自动注入UserMapper
	@Autowired
	private UserMapper userMapper;
	

	@Override
	public ServerResponse<User> login(String username, String password) {
		
		int resultCount = userMapper.checkUsername(username);
		if(resultCount == 0){
			return ServerResponse.createByMessage("用户名不存在");
		}
		
		//MD5加密 
		//下面我们通过MD5Util的MD5EncodeUtf8()方法对password进行加密
		String md5Password = MD5Util.MD5EncodeUtf8(password);	
		//下面我们要用加密的password
		User user= userMapper.selectLogin(username, md5Password);
		if(user == null){
			return ServerResponse.createByMessage("密码错误");
		}
		//将密码置为空，是为了避免将密码传到前端
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess("用户登陆成功",user);
		
	}
	
	public ServerResponse<String> resigter(User user){
		/*//校验用户名是否存在
		ServerResponse<String> vaildResponse = this.checkVaild(user.getUsername(), Const.USERNAME);
	
		if(!vaildResponse.isSuccess()){
			return vaildResponse;
		}
		//校验email是否存在
		vaildResponse = this.checkVaild(user.getEmail(), Const.EMAIL); 
		if(!vaildResponse.isSuccess()){
			return vaildResponse;
		}
		*/
		
		//校验用户名和email是否存在
		int resultCount = userMapper.checkUsername(user.getUsername());
		if(resultCount>0){
			return ServerResponse.createByMessage("用户名已存在");
		}
		resultCount = userMapper.checkEmail(user.getEmail());
		if(resultCount>0){
			return ServerResponse.createByMessage("email已存在");
		}
		
		user.setRole(Const.Role.ROLE_CUSTOMER);
		//MD5加密
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		//将加密后的用户插入到数据库中
		 resultCount = userMapper.insert(user);
		//如果插入的行数为0
		if(resultCount == 0){
			return ServerResponse.createByMessage("注册失败");
		}
		return ServerResponse.createBySuccessMessage("注册成功");
	}	
	/*这里我们是来校验用户名和email是否存在，虽然在注册中， 我们已经校验过了，但是，避免一些恶意用户通过接口访问注册接口，从而获取用户信息
	 */
	public ServerResponse<String> checkVaild(String str,String type){
		if(StringUtils.isNotBlank(type)){
			
			//校验用户名是否存在
			if(Const.USERNAME.equals(type)){
				int resultCount = userMapper.checkUsername(str);
				if(resultCount>0){
					return ServerResponse.createByMessage("用户名已存在");
				}
			}
			//校验email是否存在
			if(Const.EMAIL.equals(type)){
				int resultCount = userMapper.checkEmail(str);
				if(resultCount>0){
					return ServerResponse.createByMessage("email已存在");
				}
			}
			
		}
		else{
			return ServerResponse.createByMessage("参数错误");
		}
		return ServerResponse.createBySuccessMessage("校验成功");
	}
	
	//密码提示问题
	public ServerResponse<String>  selectQuestion(String username){
		//首先，要校验一下，这个用户名是否存在
		ServerResponse<String> vaildResponse = this.checkVaild(username, Const.USERNAME);
		if(vaildResponse.isSuccess()){
			return ServerResponse.createByMessage("用户名不存在");
		}
		
		String question = userMapper.selectQuestionByUsername(username);
		if(StringUtils.isNotBlank(question)){
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByMessage("找回密码的问题是空的");
	}
	
	//校验密码提示问题答案
	public ServerResponse<String> checkAnswer(String username,String question,String answer){
		int resultCount = userMapper.selectAnswer(username, question, answer);
		if(resultCount>0){
			//说明问题和答案是这个用户的，并且是正确的
			//使用UUID来生成一个forgetToken字符串，并且把它放到本地cache(内存)中
			String forgetToken = UUID.randomUUID().toString();
			//接下来，就要处理一下，这个forgetTken,我们在com.mmall.common中在创建一个TokenCache.java
			//下面就调用setKey()方法将这个forgetToken放入到：本地缓存中去
			TokenCache.setKey(TOKEN_PREFIX+username, forgetToken);
			return ServerResponse.createBySuccess(forgetToken);
		}
		return ServerResponse.createByMessage("问题的答案错误");
	}
	
	//忘记密码中的重置密码实现
	public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
		//校验一下forgetToken是否为空
		if(StringUtils.isBlank(forgetToken)){
			return ServerResponse.createByMessage("参数错误，token需要传递");
		}
		//接下来，我们需要校验一下username是否存在，如果：username不存在的话，那么，forgetToken的key为：一个定值，任何人都可以拿到，这是不行的
		ServerResponse<String> vaildResponse = this.checkVaild(username, Const.USERNAME);
	
		if(vaildResponse.isSuccess()){
			return ServerResponse.createByMessage("用户名不存在");
		}
		//下面这里就是：用户名存在的情况，从本地缓存中获取token
		String token = TokenCache.getKey(TOKEN_PREFIX+username);
		//使用StringUtils的isBland()方法来对cache(缓存)token是否为空
		if(StringUtils.isBlank(token)){
			return ServerResponse.createByMessage("token无效或者过期");
		}
		/*
		 *为什么要用"org.apache.commons.lang3.StringUtils"而不用equals(cs1,cs2)来比较，forgetToken和cache中的token
		 *是因为："java.lang.String"中的equal(a,b)如果a 为:null 的话，a.equals(b)会报空指针异常，而StringBuild()方法避免这个问题
		 */
		if(StringUtils.equals(forgetToken, token)){
			//接下来就可以修改密码了，首先将password使用MD5Password进行加密
			String MD5Password = MD5Util.MD5EncodeUtf8(passwordNew);
			//更新密码
			int resultCount =userMapper.updatePasswordByUsername(username, MD5Password);
			//如果，更新的行数大于0，表示：更新密码成功，否则：更新密码失败
			if(resultCount>0){
				return ServerResponse.createBySuccessMessage("更新密码成功");
			}
			return ServerResponse.createByMessage("更新密码失败");
		}
		else{
			return ServerResponse.createByMessage("token错误，请重新获取重置密码的token");
		}
		
	}
	
	
	//登录状态下的重置密码
	public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
		//为了防止横向越权，我们需要验证一下旧的密码，一定要指定是这个用户的，如果不指定这个用户id,那么count(1)>0
 		int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
		if(resultCount == 0){
			return ServerResponse.createByMessage("旧密码错误");
		}
		//设置新密码
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		//调用userMapper中的方法自动更新
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if(updateCount>0){
			return ServerResponse.createBySuccessMessage("密码更新成功");
		}
		return ServerResponse.createByMessage("密码更新失败");
	}
	
	//更新用户的个人信息
	public ServerResponse<User> updateInformation(User user){
		/*username是不能更新的
		*email也需要校验的，校验email是否存在，并且存在的email如果相同的的话，不能是我们当前这个用户的，即：在email相同的时候，id !=userId(当前用户的id),
		*如果此时，从数据库中查询出来的email存在的话，说明：这个email已经被其他用户占用啦，是无法更新的
		*/
		int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
		if(resultCount>0){
			return ServerResponse.createByMessage("email已存在，请添加一个新的email");
		}
		
		//接着，我们new 一个user,即可更新个人信息
		User updateUser = new User();
		
		updateUser.setId(user.getId());
		updateUser.setUsername(user.getUsername());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());
		//然后，调用userMapper中的updatePrimaryKeySelective()方法，将new的updateUser对象放入，通过更新的行数，来判断更新个人信息是否成功
		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if(updateCount>0){
			return ServerResponse.createBySuccess("用户的个人信息更新成功", updateUser);
		}
		return ServerResponse.createByMessage("用户的个人信息更新失败");
	}
	
	//获取用户的详细信息
	public ServerResponse<User> getInformation(Integer userId){
		User user = userMapper.selectByPrimaryKey(userId);
		if(user == null){
			return ServerResponse.createByMessage("找不到当前用户");
		}
		//若用户不为空，我们就将密码置为空，是因为：将密码置为空以后，前台可以拿到用户的信息,若不将密码置为空的话，会将密码传到前端，这样的话，我们的密码就暴露了
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);
	}
	
	//backend

/**
 * 校验是否是管理员
 * @param user
 * @return
 */
	public ServerResponse checkAdminisRole(User user){
		//当user不为空并且user的角色为:admin 的时候
		if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}
}
