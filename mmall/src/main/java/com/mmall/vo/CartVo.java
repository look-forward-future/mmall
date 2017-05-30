package com.mmall.vo;

/**
 * 这个类表示的是：我们可以把添加到购物车中所用的商品的记录看成是一个整体，即：一个List集合，故下面我们定义了一个List集合的属性来表示购物车中的所有的商品的记录
 * 此外，还有：商品的总价格，是否全部勾选
 */

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author Administrator
 *
 */

public class CartVo {
	//这是一个由CartProductVo类作为元素所组成的集合
	private List<CartProductVo> cartProductVoList;
	private BigDecimal cartTotalPrice;//商品的总价格
	private Boolean allChecked;//是否已经都勾选
	private String imageHost;//由于：我们的购物车中有商品的图片，我们可以通过点击图片可以查看这个商品
	
	public List<CartProductVo> getCartProductVoList() {
		return cartProductVoList;
	}
	public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
		this.cartProductVoList = cartProductVoList;
	}
	public BigDecimal getCartTotalPrice() {
		return cartTotalPrice;
	}
	public void setCartTotalPrice(BigDecimal cartTotalPrice) {
		this.cartTotalPrice = cartTotalPrice;
	}
	public Boolean getAllChecked() {
		return allChecked;
	}
	public void setAllChecked(Boolean allChecked) {
		this.allChecked = allChecked;
	}
	public String getImageHost() {
		return imageHost;
	}
	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}
	
	
}
