package com.mmall.controller.portal;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.vo.OrderVo;

/**
 * 这是我们订单的controller类
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value="/order/")
public class OrderController {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private IOrderService iOrderService;
	
	
	/**
	 * 这是我们前端创建订单的方法
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@RequestMapping(value="create.do")
	@ResponseBody
	public ServerResponse create(HttpSession session,Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//下面写创建订单的逻辑
		return iOrderService.createOrderByUesId(user.getId(), shippingId);
	}	
	
	
	/**
	 * 这是取消订单的方法
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="cancel.do")
	@ResponseBody
	public ServerResponse<String> cancel(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//这里写我们取消订单的逻辑
		return iOrderService.cancelOrder(user.getId(), orderNo);
	}
	
	/** 
	 * 这是获取购物车用户选中商品的信息
	 * @param session
	 * @return
	 */
	@RequestMapping(value="get_cart_product_info.do")
	@ResponseBody
	public ServerResponse getCartProductInfo(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//下面写用户选中商品的信息的逻辑
		return iOrderService.getCartProduct(user.getId());
	}
	
	/**
	 * 实现查看订单详情的功能
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
		//下面写订单详情的逻辑
		return iOrderService.getOrderDetail(user.getId(), orderNo);
	}
	
	/**
	 * 查看订单列表的方法
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="list.do")
	@ResponseBody
	public ServerResponse list(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")int pageNum,@RequestParam(value="pageSize",defaultValue="10")int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//写查看订单列表的逻辑
		return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
	}
	
	
	
	
	
	
	
	
	
	/**
	 *下面的方法是支付的方法
	 * @param session  用户的回话
	 * @param orderNo  订单号
	 * @param request	servlet的request
	 * @return
	 */
	@RequestMapping(value="pay.do")
	@ResponseBody
	public ServerResponse pay(HttpSession session,Long orderNo,HttpServletRequest request){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		String path = request.getSession().getServletContext().getRealPath("upload");
		
		
		//接下来，就去Service中写业务逻辑
		return iOrderService.pay(user.getId(), orderNo, path);
	}
	
	
	/**
	 * 这是支付宝的回调方法，由于：支付宝的回调是把数据放入到HttpServletRequest中，我们需要从request中去获取，故：方法中的参数只有：HttpServletRequest
	 * @param request
	 * @return
	 */
	@RequestMapping(value="alipay_callback.do")
	@ResponseBody
	public Object alipay_callback(HttpServletRequest request){
		//下面声明一个数组，来承载我们从request中获取的value
		Map<String,String> params = Maps.newHashMap();
		
		//首先，我们从request中把Map拿出来
		Map requestParams = request.getParameterMap();
		
		//由于：我们上面从request中获取的value是一个String[]，我们需要将其遍历出来
		//下面我们使用迭代器遍历一下Map中的key
		for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
			String name = (String)iter.next(); 
			String[] values = (String[])requestParams.get(name);
			//先声明一个String
			String valueStr = "";
			//接着，在遍历一下我们获取的value
			for(int i = 0;i<values.length;i++){
				//下面我们使用三样运算符来赋值
				valueStr = (i == values.length-1)?valueStr + values[i]:valueStr + values[i]+",";
			}
			
			//特别要注意的是：这里我们使用 put来添加时，不能讲"name"
			params.put(name, valueStr);
			
		}
		logger.info("支付宝回调，sign:{},trade_status:{},参数：{}",params.get("sign"),params.get("trade_status"),params.toString());
		
		//非常重要，验证回调的正确性，是不是支付宝发的，并且还要避免重复通知
		//由于在支付宝验证回调的说明中，说：在验证时，要出去："sign"和"sign_type"这两个属性，由于在源码中，已经出去了"sign",故：下面只需要出去"sign_type"即可
		params.remove("sign_type");
		try {
			boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
			if(!alipayRSACheckV2){
				return ServerResponse.createByErrorMessage("非法请求，验证不通过，再恶意请求我们就找报警找网警啦！");
			}
		} catch (AlipayApiException e) {
			logger.error("支付宝验证回调异常！",e);
		}
		//接着，我们还要在：OrderServiceImpl中去判断订单状态以及订单是否已经支付啦
		ServerResponse serverResponse = iOrderService.aliCallback(params); 
		//由于：我们需要向前端返回的是success或者failed，故：需要对返回进行判断
		if(serverResponse.isSuccess()){
			return Const.AlipayCallback.RESPONSE_SUCCESS;
		}
		return Const.AlipayCallback.RESPONSE_FAILED;
		
	}
	
	/**
	 * 这是查询订单的支付状态
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="query_order_pay_status.do")
	@ResponseBody
	public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//这里写查询订单状态的业务逻辑,由于我们与前端的约定，返回的是一个boolean类型，故：这里需要进行判断一下
		ServerResponse<Boolean> serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
		
		if(serverResponse.isSuccess()){
			return ServerResponse.createBySuccess(true);
		}
		return ServerResponse.createBySuccess(false);
	}

}
