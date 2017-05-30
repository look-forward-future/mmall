package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 这是订单vo对象，组装这个对象，返回个前端
 * @author Administrator
 *
 */

public class OrderVo {
//我们从order对象中拿一些字段
	
	private Long orderNo;//这是订单号
	private BigDecimal payment;//这是付款金额
	private Integer paymentType;//这是付款方式
	private String paymentTypeDesc;//这是付款方式的描述
	private Integer postage;//这是运费
	private Integer status;//订单的状态
	private String statusDesc;//状态描述
	private String paymentTime;//付款时间
	private String sendTime;
	private String endTime;
	private String closeTime;
	private String createTime;
	
	//订单明细，需要在去创建一个OrderItemVo，将我们书写的OrderItemVo作为一个List放入到当前类中
	private List<OrderItemVo> orderItemList;
	
	//商品图片的host
	private String imageHost;
	//这是商品的收货地址的id
	private Integer shippingId;
	//收货人的姓名
	private String receiverName;
	//我们还将具体的收货地址传递给前端，我们就要去创建一个shippingVo，将我们的shippingVo放入
	private ShippingVo shippingVo;
	
	
	public Long getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}
	public BigDecimal getPayment() {
		return payment;
	}
	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}
	public Integer getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentTypeDesc() {
		return paymentTypeDesc;
	}
	public void setPaymentTypeDesc(String paymentTypeDesc) {
		this.paymentTypeDesc = paymentTypeDesc;
	}
	public Integer getPostage() {
		return postage;
	}
	public void setPostage(Integer postage) {
		this.postage = postage;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getPaymentTime() {
		return paymentTime;
	}
	public void setPaymentTime(String paymentTime) {
		this.paymentTime = paymentTime;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public List<OrderItemVo> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItemVo> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public String getImageHost() {
		return imageHost;
	}
	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}
	public Integer getShippingId() {
		return shippingId;
	}
	public void setShippingId(Integer shippingId) {
		this.shippingId = shippingId;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public ShippingVo getShippingVo() {
		return shippingVo;
	}
	public void setShippingVo(ShippingVo shippingVo) {
		this.shippingVo = shippingVo;
	}
	
	
}
