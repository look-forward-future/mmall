package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;

/**
 * 这是购物车的controller类
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/cart/")
public class cartController {
	
	@Autowired
	private ICartService iCartService;
	
	/**
	 * 这是添加商品到购物车
	 * @param session
	 * @param productId
	 * @param count
	 * @return
	 */
	@RequestMapping(value="add_cart.do")
	@ResponseBody
	public ServerResponse<CartVo> add(HttpSession session,Integer productId,Integer count){
		
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "未登录，需要强制登录status=10");		
		}
		//下面写我们购物车的逻辑
		return iCartService.addToCart(productId, count, user.getId());
	}
	
	/**
	 * 下面是我们更新购物车的方法，其实就是：更新购物车中产品的数量
	 * @param session
	 * @param productId
	 * @param count
	 * @return
	 */
	@RequestMapping(value="update_cart.do")
	@ResponseBody
	public ServerResponse<CartVo> updateCartByUserIdAndCount(HttpSession session,Integer productId,Integer count){
		
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//下面写我们更新购物车的逻辑
		return iCartService.updateCart(user.getId(), count, productId);
	}
	
	/**
	 * 下面是我们从购物车中删除产品的方法
	 * @param session
	 * @param productIds
	 * @return
	 */
	@RequestMapping(value="delete_cart.do")
	@ResponseBody
	public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds){
		
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//然后，我们写从购物车中删除产品的逻辑
		return iCartService.deleteProductFromCart(user.getId(), productIds);
	}
	
	/**
	 * 这是查询购物车中的产品的方法
	 * @param session
	 * @return
	 */
	@RequestMapping(value="select_product.do")
	@ResponseBody
	public ServerResponse<CartVo> selectProduct(HttpSession session){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//书写查询购物车中的产品的逻辑
		return iCartService.list(user.getId());
	}
	
	/**
	 * 购物车中商品的全选
	 * @param session
	 * @param checked
	 * @return
	 */
	@RequestMapping(value="select_all.do")
	@ResponseBody
	public ServerResponse<CartVo> selectAll(HttpSession session){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//全选的实现逻辑,在这里我们在参数中来决定是全选还是全反选
		return iCartService.selectOrUnSelect(user.getId(), null, Const.cart.CHECKED);
	}
	
	/**
	 * 购物车中的全反选的方法
	 * @param session
	 * @return
	 */
	@RequestMapping(value="un_select_all.do")
	@ResponseBody
	public ServerResponse<CartVo> unSelectAll(HttpSession session){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//全选的实现逻辑,在这里我们在参数中来决定是全选还是全反选
		return iCartService.selectOrUnSelect(user.getId(), null, Const.cart.UN_CHECKED);
	}
	
	
	/**
	 * 这是购物车中单选的方法
	 * @param session
	 * @return
	 */
	@RequestMapping(value="single_select.do")
	@ResponseBody
	public ServerResponse<CartVo> singleSelect(HttpSession session,Integer productId){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//全选的实现逻辑,在这里我们在参数中来决定是全选还是全反选
		return iCartService.selectOrUnSelect(user.getId(),productId, Const.cart.CHECKED);
	}
	
	
	/**
	 * 这是购物车中单反选的方法
	 * @param session
	 * @return
	 */
	@RequestMapping(value="un_single_select.do")
	@ResponseBody
	public ServerResponse<CartVo> unSingleSelect(HttpSession session,Integer productId){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//全选的实现逻辑,在这里我们在参数中来决定是全选还是全反选
		return iCartService.selectOrUnSelect(user.getId(),productId, Const.cart.UN_CHECKED);
	}
	
	/**
	 * 获取购物车中产品的数量
	 * @param session
	 * @return
	 */
	@RequestMapping(value="get_count.do")
	@ResponseBody
	public ServerResponse getProductCount(HttpSession session){
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createBySuccess(0);
		}
		//然后，写获取购物车中的产品的数量
		return iCartService.getCartProductCount(user.getId());
	}
	

}
