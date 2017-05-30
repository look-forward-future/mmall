package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {
	//这是添加商品到购物车的接口
	ServerResponse<CartVo> addToCart(Integer productId,Integer count,Integer userId);
	//这是我们更新购物车的接口
	ServerResponse<CartVo> updateCart(Integer userId,Integer count,Integer productId);
	//这是从购物车中删除产品的接口
	ServerResponse<CartVo> deleteProductFromCart(Integer userId,String productIds);
	//查询购物车中的产品
	ServerResponse<CartVo> list(Integer userId);
	//这是购物车中全选、全反选、单选以及单反选的接口
	ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked);
	//获取购物车的产品的总数量
	ServerResponse<Integer> getCartProductCount(Integer userId);
}
