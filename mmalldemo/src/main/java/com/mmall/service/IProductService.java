package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

/**
 * 这是有个商品的服务类接口
 * @author Administrator
 *
 */

public interface IProductService {
	//这是新增、更新以及保存商品的接口
	ServerResponse saveOrUpdateProduct(Product product);
	//这是修改产品的销售状态的接口
	ServerResponse<String> setSaleStatus(Integer productId,Integer status);
}
