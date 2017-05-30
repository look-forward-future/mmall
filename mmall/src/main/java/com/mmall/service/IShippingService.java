package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * 这是收货地址的逻辑接口
 * @author Administrator
 *
 */

public interface IShippingService {
	//添加收货地址的接口
	ServerResponse add(Integer userId,Shipping shipping);
	//删除收货地址的接口
	ServerResponse delShippingSite(Integer userId,Integer shippingId);
	//更新收货地址的接口
	ServerResponse updateShippingSite(Integer userId,Shipping shipping);
	//查询收货地址的接口
	ServerResponse<Shipping> select(Integer userId,Integer shippingId);
	//对收货地址进行分页的接口
	ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize);

}
