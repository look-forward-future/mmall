package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * 产品业务逻辑接口类
 * @author Administrator
 *
 */

public interface IProductService {
	//新增以及更新产品的接口
	ServerResponse<String> saveOrUpdateProduct(Product product);
	//更改产品的上下架的接口
	ServerResponse<String> setSaleStatues(Integer productId,Integer status); 
	//获取产品的详细信息的接口
	ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
	//后台查询产品的列表的接口
	ServerResponse<PageInfo> getProductList(int pageNum,int pageSize);
	//后台商品的搜索功能的接口
	ServerResponse<PageInfo> productSearch(String productName,Integer productId,int pageNum,int pageSize);
	
	//这是前台获取商品的详细信息的接口
	ServerResponse<ProductDetailVo> getProductDetails(Integer productId);
	//通过前台通过商品的名称和品类的ID来获取商品的接口
	ServerResponse<PageInfo> getListByCategoryIdAndKeyword(String keyWord,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
