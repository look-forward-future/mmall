package com.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;

/**
 * 这是前端的商品的Controller
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/product")
public class productController {
	
	@Autowired
	private IProductService iProductService;

	/**
	 * 前端的商品的详情
	 * @param productId
	 * @return
	 */
	@RequestMapping("get_product_detail.do")
	@ResponseBody
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
		return iProductService.getProductDetails(productId);
	}
	
	/**
	 * 这是前端搜索商品列表
	 * @param keyWord
	 * @param categoryId
	 * @param pageNum
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	@RequestMapping("get_list.do")
	@ResponseBody
	public ServerResponse<PageInfo> getList(@RequestParam(value="keyWord",required=false)String keyWord,
			@RequestParam(value="categoryId",required=false)Integer categoryId,
			@RequestParam(value="pageNum",defaultValue="1")int pageNum,
			@RequestParam(value="pageNum",defaultValue="10")int pageSize,
			@RequestParam(value="orderBy",defaultValue="")String orderBy){
		return iProductService.getListByCategoryIdAndKeyword(keyWord, categoryId, pageNum, pageSize, orderBy);
	}
}
