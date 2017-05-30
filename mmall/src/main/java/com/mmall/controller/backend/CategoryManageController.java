package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;

/**
 * 这是品类controller类
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private ICategoryService iCategoryService;
	
	/**
	 * 这是添加品类的方法
	 * @param session
	 * @param CategoryName
	 * @param parentId
	 * @return
	 */
	@RequestMapping("add_category.do")
	@ResponseBody
	public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value="parentId",defaultValue="0")Integer parentId){
		//首先，我们判断用户是否已经登录
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		//接着，我们来判断是否是管理员登录,可以将验证当前用户是否是管理员登录的方法放到:com.mmall.service.impl下面的：UserServiceImpl.java类中
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员
			//下面可以写添加品类的逻辑
			return iCategoryService.addCategory(categoryName, parentId);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 修改品类的名称
	 * @param session
	 * @param categoryId
	 * @param categoryName
	 * @return
	 */
	@RequestMapping("set_category_name.do")
	@ResponseBody
	public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		//接着，我们来判断是否是管理员登录,可以将验证当前用户是否是管理员登录的方法放到:com.mmall.service.impl下面的：UserServiceImpl.java类中
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员
			return iCategoryService.setCategoryName(categoryId, categoryName);
			
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 获取品类的子节点(平级)
	 * @param session
	 * @param categoryId
	 * @return
	 */
	@RequestMapping("get_category.do")
	@ResponseBody
	public ServerResponse getCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0")Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		//接着，我们来判断是否是管理员登录,可以将验证当前用户是否是管理员登录的方法放到:com.mmall.service.impl下面的：UserServiceImpl.java类中
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员
			return iCategoryService.getCategory(categoryId);
			
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 获取当前节点id及子节点的id
	 * @param session
	 * @param categoryId
	 * @return
	 */
	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServerResponse getCategoryDeepChildrenById(HttpSession session,Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		//接下来，判断是否是管理员登录
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员
			//获取当前节点ID及其子节点的ID
			return iCategoryService.selectCategoryAndChildrenById(categoryId);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
}


