package com.mmall.controller.backend;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;

/**
 * 这是产品管理的controller层
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
	
	@Autowired
	private IFileService iFileService;
	
	/**
	 *保存以及更新产品
	 * @param session
	 * @param product
	 * @return
	 */
	@RequestMapping("saveOrUpdate_product.do")
	@ResponseBody
	public ServerResponse saveProduct(HttpSession session,Product product){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		//接下来，判断是否是管理员
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员登录
			return iProductService.saveOrUpdateProduct(product);
		}
		else{
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
		}
	}
	
	/**
	 * 更改产品的上下架状态
	 * @param productId
	 * @param status
	 * @return
	 */
	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse<String> setSaleStatus(HttpSession session,Integer productId,Integer status){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		
		if(iUserService.checkAdminRole(user).isSuccess()){
			//是管理员登录
			return iProductService.setSaleStatues(productId, status);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 获取商品的详细信息
	 * @param session
	 * @param productId
	 * @return
	 */
	@RequestMapping("get_detail.do")
	@ResponseBody
	public ServerResponse getDetail(HttpSession session,Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员
			return iProductService.manageProductDetail(productId);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 后台查询商品的List
	 * @param session
	 * @param productId
	 * @return
	 */
	@RequestMapping("get_manage_list.do")
	@ResponseBody
	public ServerResponse getList(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")int pageNum,@RequestParam(value="pageSize",defaultValue="10")int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员
			return iProductService.getProductList(pageNum, pageSize);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 后台商品搜索功能的开发
	 * @param session
	 * @param productName
	 * @param productId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("product_search.do")
	@ResponseBody
	public ServerResponse ProductSearch(HttpSession session,String productName,Integer productId,@RequestParam(value="pageNum",defaultValue="1")int pageNum,@RequestParam(value="pageSize",defaultValue="10")int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员
			return iProductService.productSearch(productName, productId, pageNum, pageSize);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 后台商品的图片的springMVC上传
	 * 步骤：
	 * 1.首先，我们将图片上传到服务器上的目录上面(需要在服务器上创建一个临时的目录)
	 * 2.接着，在将图片上传到FTP服务器上，共所用用户共享(上传和下载)
	 * @param file
	 * @param request
	 * @return
	 */@RequestMapping("upload.do")
	   @ResponseBody
	//我们这个springMVC上传可以在index.jsp中实现，可以在index.jsp中创建form表单，表单中的action的值对应的就是这个controller的路径，
	//input标签中的name 属性对应的就是：MultipartFile的file
	//下面的方法中，为什么要添加上参数：HttpServletRequest，是因为：通过servlet的上下文去动态的创建一个相对路径
	public ServerResponse upload(HttpSession session,@RequestParam(value="upload_file",required=false)MultipartFile file,HttpServletRequest request){
		 
		 User user = (User) session.getAttribute(Const.CURRENT_USER);
			if(user == null){
				return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录！");
			}
			if(iUserService.checkAdminRole(user).isSuccess()){
				//说明是管理员
				//下面我们使用request获取session,获取servlet的上下文，调用getRealPath()方法，括号中我们可以设置一个虚拟路径，它会将在webapp下创建一个文件夹，名为：upload
				String path = request.getSession().getServletContext().getRealPath("upload");
				String targetFileName = iFileService.upload(file, path);
				String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
				
				//接下来，我们用map来组装返回值
				Map fileMap = Maps.newHashMap();
				fileMap.put("uri", targetFileName);
				fileMap.put("url", url);
				
				return ServerResponse.createBySuccess(fileMap);
			}
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");	
	}
	 
	 /**
	  * 这是富文本的图片上传的功能(这个也可以在：index.jsp中来写一个form来验证)
	  * 注意：
	  * 1.富文本中对于返回值是有要求的，我们使用的simditor，所以使用simditor的要求进行返回
	  *  simditor的返回的形式如下：
	  *  
  	  *	{
  	  *		"success": true/false,
  	  *		"msg": "error message", # optional
  	  *		"file_path": "[real file path]"
	  * }
	  * 2.富文本上传的时候，还需要修改一下	响应(response)的header,这是simditor要求的，就需要在方法中，在添加一个参数：HttpServletResponse
	  * 这是：前端的插件与后端的约定，要学习修改Response 的Header
	  * @param session
	  * @param file
	  * @param path
	  * @return
	  */
	 @RequestMapping("richtext_img_upload.do")
	 @ResponseBody
	 public Map richtextImgUpload(HttpSession session,@RequestParam(value="upload_file",required=false)MultipartFile file,HttpServletRequest request,HttpServletResponse response){
		 //我们可以使用一个集合Map来表示simditor的返回形式，首先，初始化这个Map集合
		 Map resultMap = Maps.newHashMap();
		 //判断用户是否登录
		 User user = (User) session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 resultMap.put("success", false);
			 resultMap.put("msg", "请登录管理员");
			 return resultMap;
		 }
		 //接下来，判断用户是否是管理员登录
		 if(iUserService.checkAdminRole(user).isSuccess()){
			 //是管理员
			 //处理我们富文本的上传的逻辑
			 String path = request.getSession().getServletContext().getRealPath("upload");
			 String targetFileName = iFileService.upload(file, path);
			 
			 
			 if(StringUtils.isBlank(targetFileName)){
				 resultMap.put("success", false);
				 resultMap.put("msg", "上传失败");
				 return resultMap;
			 }
			 String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
			 resultMap.put("success", true);
			 resultMap.put("msg", "上传成功");
			 resultMap.put("file_path", url);
			 //我们不需要处理所有的，只需要处理响应成功的就行啦
			 response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
			 return resultMap;
		 }
		 else{
			//若不是管理员的话，就会执行下面的代码
			 resultMap.put("success", false);
			 resultMap.put("msg", "无权限操作");
			 return resultMap;
		 } 
	 }

}
