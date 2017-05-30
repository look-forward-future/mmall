package com.mmall.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 这是订单明细的Vo
 * @author Administrator
 *
 */

public class OrderItemVo {

	private Long orderNo;
	private Integer productId;
	private String productName;
	private String productImage;
	private BigDecimal currentUnitPrice;
	private Integer quantity;
	private BigDecimal totalPrice;
	//为什么这里我们的创建时间不使用Date类型，而使用String类型，是因为：我们在前端的时间的显示的形式是以String类型
	private String createTime;
	
	
	public Long getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductImage() {
		return productImage;
	}
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}
	public BigDecimal getCurrentUnitPrice() {
		return currentUnitPrice;
	}
	public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
		this.currentUnitPrice = currentUnitPrice;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
	
	
}
