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
import com.mmall.service.impl.CategoryServiceImpl;

/**
 * 这是分类管理，是在后台里面
 * @author Administrator
 *
 */
@Controller
@RequestMapping("manage/category" )
public class CategoryManageController {
	//将IUserService注入到该类中来
	@Autowired
	private IUserService iUserService;
	@Autowired
	private ICategoryService iCategoryService; 
	
	
	@RequestMapping("add_category.do")
	@ResponseBody  //该注解的作用是：使我们返回值自动使用jocken的序列化
	//下面是添加分类
	public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value="parentId",defaultValue="0")int parentId){
		//首先判断一下用户是否登录
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
		}
		//校验一下，是否是管理员,可以将其写在UserServiceImpl中，在使用@Autowried注解将其注解过来
		if(iUserService.checkAdminisRole(user).isSuccess()){
			//是管理员
			//增加我们处理分类的逻辑,这个处理分类的逻辑就在IUserServiceImpl中创建一个Service类
			 return iCategoryService.addCategory(categoryName, parentId);
		}
		else{
			return ServerResponse.createByMessage("无权限操作，需要管理员权限");
		}
	
	}
	
	//接下来，我们来写一个更新categoryName的功能
	@RequestMapping("set_category_name.do")
	@ResponseBody 
	public ServerResponse setCategoryName(HttpSession session,String categoryName,Integer categoryId){
		//首先判断一下用户是否登录
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
		}
		//校验一下，是否是管理员
		if(iUserService.checkAdminisRole(user).isSuccess()){
			//是管理员登录，下面来处理更新品类的名称
			return iCategoryService.updateCategoryName(categoryName, categoryId);
		}
		else{
			return ServerResponse.createByMessage("无权限操作，需要管理员权限");
		}
		
	}
	
	
	//下面是我们查询子节点
	@RequestMapping("get_category.do")
	@ResponseBody 
	public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0")Integer categoryId){
		//首先判断一下用户是否登录
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
		}
		//校验一下，是否是管理员
		if(iUserService.checkAdminisRole(user).isSuccess()){
			//查询子节点的category信息，并且不递归，保持平级
			return iCategoryService.getChildrenParallelCategory(categoryId);
		}
		else{
			return ServerResponse.createByMessage("无权限操作，需要管理员权限");
		}
	}
	/**
	 * 下面的方法是：获取当前的Id,并且递归查询它的子节点的Id
	 * @return
	 */
	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0")Integer categoryId){
		//首先判断一下用户是否登录
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
		}
		//校验一下，是否是管理员
		if(iUserService.checkAdminisRole(user).isSuccess()){
			//查询节点的id和递归子节点的id
			return iCategoryService.selectCategoryAndChildrenById(categoryId);
		}
		else{
			return ServerResponse.createByMessage("无权限操作，需要管理员权限");
		}		

	}
}
