package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

import com.mmall.pojo.OrderItem;

/**
 * 这是订单中产品的vo
 * @author Administrator
 *
 */
public class OrderProductVo {
	
	private List<OrderItem> orderItemList;//这里定义了订单详情的列表
	private BigDecimal productTotalPrice;//这是商品的总价
	private String imageHost;//这是图片地址的前缀
	
	
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public BigDecimal getProductTotalPrice() {
		return productTotalPrice;
	}
	public void setProductTotalPrice(BigDecimal productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}
	public String getImageHost() {
		return imageHost;
	}
	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}
	
	
	

}
