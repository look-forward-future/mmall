package com.mmall.controller.portal;

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
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;

/**
 * 这是收货地址的controller类
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value="/shipping/")
public class ShippingController {
	
	@Autowired
	private IShippingService iShippingService;
	
	/**
	 * 添加收货地址
	 * @param session
	 * @param shipping
	 * @return
	 */
	@RequestMapping(value="add.do")
	@ResponseBody
	public ServerResponse add(HttpSession session,Shipping shipping){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//这里写添加地址的逻辑
		return iShippingService.add(user.getId(), shipping);
	}
	
	/**
	 *删除收货地址
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@RequestMapping(value="del.do")
	@ResponseBody
	public ServerResponse del(HttpSession session,Integer shippingId){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//这里写删除地址的逻辑
		return iShippingService.delShippingSite(user.getId(), shippingId);
	}
	
	
	/**
	 *更新收货地址
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@RequestMapping(value="update.do")
	@ResponseBody
	public ServerResponse update(HttpSession session,Shipping shipping){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//这里写更新地址的逻辑
		return iShippingService.updateShippingSite(user.getId(), shipping);
	}
	
	
	/**
	 *更新收货地址
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@RequestMapping(value="select.do")
	@ResponseBody
	public ServerResponse select(HttpSession session,Integer shippingId){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//这里写查询地址的逻辑
		return iShippingService.select(user.getId(), shippingId);
	}
	
	
	/**
	 *使用mybatis的PageHelper将收货地址进行分页
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@RequestMapping(value="list.do")
	@ResponseBody
	public ServerResponse<PageInfo> list(HttpSession session,
			                  @RequestParam(value="pageNum",defaultValue="1")Integer pageNum,
			                  @RequestParam(value="pageSize",defaultValue="10")Integer pageSize){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//这里写查询地址的逻辑
		return iShippingService.list(user.getId(), pageNum, pageSize);
	}
	

}
