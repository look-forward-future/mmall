package com.mmall.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;

/**
 * 这是购物车的业务逻辑类
 * @author Administrator
 *
 */
@Service(value="iCartService")
public class CartServiceImpl implements ICartService {
	
	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private  ProductMapper productMapper;
	
	/**
	 * 这是添加商品到购物车的方法
	 * @param productId
	 * @param count
	 * @param userId
	 * @return
	 */
	public ServerResponse<CartVo> addToCart(Integer productId,Integer count,Integer userId){
		if(productId == null || count == null){
			return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		//首先，查找通过productId和userId获取商品
		Cart cart = cartMapper.selectByUserIdProductId(productId, userId); 
		if(cart == null){
			//说明：该购物车中没有商品,就需要向这个购物车中添加商品的记录
			Cart currentCart = new Cart();
			
			currentCart.setUserId(userId);
			currentCart.setProductId(productId);
			currentCart.setQuantity(count);
			//由于Cart购物车中的checked（选中状态），在Const常量类中，创建一个分类
			currentCart.setChecked(Const.cart.CHECKED);
			
			//然后，将其插入到：CartMapper中去
			cartMapper.insert(currentCart);		
		}
		else{
			//说明：购物车中有这个产品的记录
			//将数量相加
			count = cart.getQuantity()+count;
			//修改一下这个cart的数量
			cart.setQuantity(count);
			//将购物车对象更新
			cartMapper.updateByPrimaryKeySelective(cart);
		}
		//这里，我们还要想到当客服在添加购买商品的数量的时候，超过了商品的库存的时候的情况，故：需要一个限制条件
		//在者，购物车也要有商品的图片，我们需要使用value Object来进行组装
		return this.list(userId);
	}
	
	/**
	 * 这是我们更新购物车的方法
	 * @param userId
	 * @param count
	 * @param productId
	 * @return
	 */
	public ServerResponse<CartVo> updateCart(Integer userId,Integer count,Integer productId){
		//首先，判断一下，参数是否为空
		if(userId == null || count == null || productId == null){
			return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		//接着，我们调用方法来获取Cart对象
		Cart cart = cartMapper.selectByUserIdProductId(productId, userId);
		if(cart != null){
			//说明：我们的购物车中有这个商品的记录，我们更新购物车，其实是：更改购物车中，商品的数量
			cart.setQuantity(count);
		}
		//接着，我们还要更新购物车中的产品的数量
		cartMapper.updateByPrimaryKeySelective(cart);
		return this.list(userId);
	}
	
	/**
	 * 下面我们删除购物车中的产品（既可以删除单个产品，又可以删除多个的产品）
	 * @param userId
	 * @param productIds
	 * @return
	 */
	public ServerResponse<CartVo> deleteProductFromCart(Integer userId,String productIds){
		if(userId == null || StringUtils.isEmpty(productIds)){
			return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		//下面我们使用splitter来队productIds实现分割
		List<String> productList = Splitter.on(",").splitToList(productIds);
		if(CollectionUtils.isEmpty(productList)){
			return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "删除购物车中的参数productId的集合为空！");
		}
		//接着，在cartMapper.java类中去写一个删除商品的方法
		cartMapper.deleteProductByUserIdProductIds(userId, productList);
		return this.list(userId);
	}
	
	
	/**
	 * 查询购物车产品的方法
	 * @param userId
	 * @return
	 */
	public ServerResponse<CartVo> list(Integer userId){
		CartVo cartVo = this.getCartVoLimit(userId);
		return ServerResponse.createBySuccess(cartVo);
	}
	
	
	/**
	 * 这是一个购物车中全选、全反选、单选以及单反选的 方法
	 * @param userId
	 * @param checked
	 * @return
	 */
	public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
		//在cartMapper中去写一个全选的方法
		cartMapper.checkedOrUnCheck(userId,productId,checked);
		return this.list(userId);
	}
	
	
	/**
	 * 购物车中产品的总数量
	 * @param userId
	 * @return
	 */
	public ServerResponse<Integer> getCartProductCount(Integer userId){
		//首先，我们对userId进行非空判断
		if(userId == null){
			return ServerResponse.createBySuccess(0);
		}
		//在cartMapper中去写一个sql方法
		return ServerResponse.createBySuccess(cartMapper.getProductCount(userId));
	}
	
	
	
	//下面我们来组装一下CartVo类
	private CartVo getCartVoLimit(Integer userId){
		//创建一个CartVo对象的实例
		CartVo cartVo = new CartVo();
		//接着，我们通过userId从数据库中获取这个用户的购物车的商品的记录的集合
		List<Cart> cartList = cartMapper.selectCartByUserId(userId);
		//初始化一下，List<CartProductVo>这个集合
		List<CartProductVo> cartProductVoList =Lists.newArrayList();
		//接着，初始化一下，购物车的总价格，这里我们选用：String构造器来初始化它，记住：一定要用：BigDecimal(String str)的string 构造器，因为：它可以解决计算中丢失精度的问题
		//为了我们在使用BigDecimal 来进行计算时，将我们的price的类型转换为：stringL类型，就在util类中，创建一个BigDecimal的工具类
		BigDecimal cartTotalPrice = new BigDecimal("0");
		
		if(CollectionUtils.isNotEmpty(cartList)){
			//遍历这个cartList
			for(Cart cartItem:cartList){
				//创建CartProductVO实例，将CartProductVo类中，关于Cart购物车类中的属性进行赋值
				CartProductVo cartProductVo = new CartProductVo();
				cartProductVo.setId(cartItem.getId());
				cartProductVo.setProductId(cartItem.getProductId());
				
				
				//通过产品的ID，productId来获取产品
				Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
				if(product != null){
					cartProductVo.setProductName(product.getName());
					cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductMainImage(product.getMainImage());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStatus(product.getStatus());
					cartProductVo.setProductStock(product.getStock());
					//这里我们要对库存进行一个判断处理，当 产品的库存小于我们购物车中我们需要购买的商品的数量时，我们就将商品的库存赋值给它
					//首先，定义一个变量
					int byLimitCount = 0;
					//判断一下产品的库存是否大于我们购买产品的数量
					if(product.getStock() >= cartItem.getQuantity()){
						//库存充足
						byLimitCount = cartItem.getQuantity();
						//下面我们为购物车对象赋值一个库存数量，可以在：常量类Const中来定义一下，这是我们与前台的一个约定
						cartProductVo.setLimitQuantity(Const.cart.LIMIT_NUM_SUCCESS);
					}
					else{
						byLimitCount = product.getStock();
						cartProductVo.setLimitQuantity(Const.cart.LIMIT_NUM_FAIL);
						//接下来，在购物车中更新有效库存
						Cart cartForCount = new Cart();
						cartForCount.setId(userId);
						cartForCount.setQuantity(byLimitCount);
						//调用cartMapper中的方法来更新库存
						cartMapper.updateByPrimaryKeySelective(cartForCount);
					}
					cartProductVo.setQuantity(byLimitCount);
					//计算每一个商品的总价
					cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity() ));
					//商品是否被勾选
					cartProductVo.setProductChecked(cartItem.getChecked());
				}
				//下面是：判断购物车中的产品是否被勾选，若被勾选的话，就将其加入到总价之中
				if(cartItem.getChecked() == Const.cart.CHECKED){
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
				}
				cartProductVoList.add(cartProductVo);
			}
				
		}
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVoList(cartProductVoList);
		//我们还要为：AllChecked这个字段在下面创建一个方法
		cartVo.setAllChecked(this.selectAllChecked(userId));
		cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		
		return cartVo;	
		
	}
	
	//下面这个方法是我们在cart类中判断购物车对象是否全部选中
	public boolean selectAllChecked(Integer userId){
		if(userId == null){
			return false;
		}
		return cartMapper.selectCarProductCheckedStatusByUserId(userId) == 0;
	}
}
