package com.mmall.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;

/**
 * 这是收货地址的逻辑类
 * @author Administrator
 *
 */

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{
	
	@Autowired
	private ShippingMapper shippingMapper;

	/**
	 * 这是添加收货地址
	 * @param userId
	 * @param shipping
	 * @return
	 */
	public ServerResponse add(Integer userId,Shipping shipping){
		//由于我们没有让前端传一个userId,故：在后端赋值一下
		shipping.setUserId(userId);
		/*下面我们调用：shippingMapper中的insert()方法
		 * 我们与前端有一个约定，我们在插入shipping后，把这个id传给前端，以便：前端通过将这个id给后台，获取这个收货地址
		 * 我们如何来实现啦，需要在shippingMapper中的insert的sql中去添加两个属性，userGeneratekeys()和keyProperty()
		 * 这个id就可以填充到shipping的getId()上
		 */
		int rowCount = shippingMapper.insert(shipping);
		if(rowCount > 0){
			Map result = Maps.newHashMap();
			result.put("shippingId", shipping.getId());
			return ServerResponse.createBySuccess("新建地址成功！", result);
		}
		return ServerResponse.createByErrorMessage("新建地址失败！");
	}
	
	/**
	 * 删除收货地址
	 * @param userId
	 * @param shippingId
	 * @return
	 */
	public ServerResponse delShippingSite(Integer userId,Integer shippingId){
		/*这里我们注意：不能使用我们：deleteByPrimaryKey(Integer id);这个方法来删除收货地址
		 *是因为：这样会造成横向越权，因为：我们在controller中，判断了：是登录状态下，是普通用户，即：任何用户只要登录后，传入的不是：自己的shippingId，也可以删除别人的收货地址
		 *由于：这个shippingId没有与userId关联，我们只要将shipping与userId关联起来就解决啦
		 */
		//去shippingMapper中，重新写一个删除收货地址的sql
		int rowCount = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
		if(rowCount > 0){
			return ServerResponse.createBySuccessMessage("删除地址成功！");
		}
		return ServerResponse.createByErrorMessage("删除收货地址失败！");
	}
	
	/**
	 * 更新收货地址
	 * @param userId
	 * @param shippingId
	 * @return
	 */
	public ServerResponse updateShippingSite(Integer userId,Shipping shipping){
		/*这里我们注意：横向越权的情况，因为：我们的shippingMapper中的原方法都没有与userId相关联
		 */
		//去shippingMapper中，重新写一个更新收货地址的sql
		int rowCount = shippingMapper.updateByShipping(shipping);
		if(rowCount > 0){
			return ServerResponse.createBySuccessMessage("更新地址成功！");
		}
		return ServerResponse.createByErrorMessage("更新收货地址失败！");
	}
	
	
	/**
	 * 查询收货地址
	 * @param userId
	 * @param shippingId
	 * @return
	 */
	public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
		/*这里我们注意：横向越权的情况，因为：我们的shippingMapper中的原方法都没有与userId相关联
		 */
		//去shippingMapper中，重新写一个查询收货地址的sql
		Shipping shipping = shippingMapper.selectByUserIdAndshippingId(userId, shippingId);
		if(shipping == null){
			return ServerResponse.createBySuccessMessage("无法查询到收货地址！");
		}
		return ServerResponse.createBySuccess("查询收货地址成功！", shipping);
	}
	
	
	/**
	 * 使用mybatis的PageHelper对收货地址进行分页处理
	 * @param userId
	 * @param shippingId
	 * @return
	 */
	public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize){
		//首先，开始分页
		PageHelper.startPage(pageNum, pageSize);
		//接着，调用shipping中的Shipping的List
		List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
		//然后，进行分页
		PageInfo pageInfo = new PageInfo(shippingList);
		return ServerResponse.createBySuccess("收货地址分页成功！", pageInfo);
	}
}
