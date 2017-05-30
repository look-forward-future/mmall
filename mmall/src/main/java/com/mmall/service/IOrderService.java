package com.mmall.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

/**
 * 这是订单的service的接口类
 * @author Administrator
 *
 */

public interface IOrderService {
	//这是我们支付宝支付的接口
	ServerResponse pay(Integer userId,Long orderNo,String path);
	//这是回调方法，验证支付状态
	ServerResponse aliCallback(Map<String,String> params);
	//这是获取订单状态的方法
	ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo);
	//创建订单的接口的方法
	ServerResponse createOrderByUesId(Integer userId,Integer shippingId);
	//取消订单的接口方法
	ServerResponse<String> cancelOrder(Integer userId,Long orderNo);
	//这是获取购物车中商品的详情的接口的方法
	ServerResponse  getCartProduct(Integer userId);
	//这是查看订单详情的接口的方法
	ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo);
	//获取订单列表的接口方法
	ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize);
	
	//backend
	
	//这是后台获取订单的列表的接口的方法
	ServerResponse<PageInfo> manageOrderList(int pageNum,int pageSize);
	//这是后台查看订单的详情的接口方法
	ServerResponse<OrderVo> manageOrderDetail(Long orderNo);
	//这是后台通过订单号来查找订单的接口方法
	ServerResponse<PageInfo> managesearch(Long orderNo,int pageNum,int pageSize);
	//这是实现发货功能的接口方法
	ServerResponse<String> manageSendGoods(Long orderNo);

}
