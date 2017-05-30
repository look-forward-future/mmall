package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;

/**
 * 这是后台的订单Controller类
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value="/manage/order")
public class OrderManageController{
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IOrderService iOrderService;
	
	
	/**
	 * 这是我们后台获取订单列表的方法
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="list.do")
	@ResponseBody
	public ServerResponse<PageInfo> orderList(HttpSession session,Long orderNo,
								@RequestParam(value="pageNum",defaultValue="1")int pageNum,
								@RequestParam(value="pageSize",defaultValue="10")int pageSize){
		User user =(User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//判断是否是管理员登录
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员登录
			return iOrderService.manageOrderList(pageNum, pageSize);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 这是后台获取订单的详情的方法
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="detail.do")
	@ResponseBody
	public ServerResponse<OrderVo> detail(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//接着，判断是否是管理员登录
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员登录
			//下面这里写获取订单详情的逻辑
			return iOrderService.manageOrderDetail(orderNo);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 通过订单号来查询订单
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="search.do")
	@ResponseBody
	public ServerResponse<PageInfo> searchOrder(HttpSession session,Long orderNo,
								@RequestParam(value="pageNum",defaultValue="1")int pageNum,
								@RequestParam(value="pageSize",defaultValue="10")int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			return iOrderService.managesearch(orderNo, pageNum, pageSize);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	
	/**
	 * 这是我们发货的方法
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="send.do")
	@ResponseBody
	public ServerResponse sendOrderGoods(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		if(iUserService.checkAdminRole(user).isSuccess()){
			//说明是管理员登录
			return iOrderService.manageSendGoods(orderNo);
		}
		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
	

}
