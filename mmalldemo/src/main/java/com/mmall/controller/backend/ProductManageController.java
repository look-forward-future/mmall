package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;

/**
 * 这是商品管理的controller类
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IProductService iProductService;
	
	
	//首先我们创建一个保存商品方法
	@RequestMapping("save.do")
	@ResponseBody
	public ServerResponse productSave(HttpSession session,Product product){
		//首先，判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
		}
		//然后，判断是否是管理员登录,由于：我们在前面已经在IUserService类中已经写了判断是否是管理员登录的方法，在该类中来注入之后在调用
		if(iUserService.checkAdminisRole(user).isSuccess()){
			//是管理员
			//增加产品的业务逻辑
			return iProductService.saveOrUpdateProduct(product);
		}
		else{
			return ServerResponse.createByMessage("无权限操作，请使用管理员权限");
		}
	
	}
	
	//下面的方法是更新商品的上下架(即：商品的状态)
	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status){
		//首先，判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
		}
		//然后，判断是否是管理员登录,由于：我们在前面已经在IUserService类中已经写了判断是否是管理员登录的方法，在该类中来注入之后在调用
		if(iUserService.checkAdminisRole(user).isSuccess()){
			//是管理员
			//
			return iProductService.setSaleStatus(productId, status);
		}
		else{
			return ServerResponse.createByMessage("无权限操作，请使用管理员权限");
		}
	
	}
	
	//下面是后台获取商品详情的功能的方法
	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse getDetail(HttpSession session,Integer productId){
		//首先，判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
		}
		//然后，判断是否是管理员登录,由于：我们在前面已经在IUserService类中已经写了判断是否是管理员登录的方法，在该类中来注入之后在调用
		if(iUserService.checkAdminisRole(user).isSuccess()){
			//是管理员
			//填充业务
			return null;
		}
		else{
			return ServerResponse.createByMessage("无权限操作，请使用管理员权限");
		}
	
	}
}
