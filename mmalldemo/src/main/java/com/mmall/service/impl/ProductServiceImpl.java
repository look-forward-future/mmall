package com.mmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;

/**
 * 这是商品的服务类的实现类
 * @author Administrator
 *
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{
	
	@Autowired
	private ProductMapper productMapper;
	
	//下面是保存或者更新产品，对于前端是两个操作，但是，对于后台的话，我们可以使用一个接口来实现，只是我们判断一下对商品是一个新增、保存还是更新
	public ServerResponse saveOrUpdateProduct(Product product){
		if(product != null){
			//接着，我们判断一下，商品的子图是否为空
			if(StringUtils.isNoneBlank(product.getSubImages())){
				//那么，我们就取第一个子图赋给我们的主图
				String[] subImageArray = product.getSubImages().split(",");
				if(subImageArray.length > 0){
					product.setMainImage(subImageArray[0]);
				}
			}
			
			//如果我们是更新商品的话，商品的ID一定是不为空的
			if(product.getId() != null){
				//因为前端传过来的商品应该是：一个product
				int rowCount = productMapper.updateByPrimaryKey(product);
				if(rowCount > 0){
					return ServerResponse.createBySuccessMessage("更新商品成功");
				}
				return ServerResponse.createByMessage("更新商品失败");
			}
			else{
				//如果product.getId()等于null的话，我们就要新增商品
				int productCount = productMapper.insert(product);
				if(productCount > 0){
					return ServerResponse.createBySuccessMessage("新增商品成功");
				}
				return ServerResponse.createByMessage("新增商品失败");
			}
			
		}
		return ServerResponse.createByMessage("新增或者更新产品参数不正确");
	}
	
	//下面的方法是：更新商品上下架的状态
	public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
		if(productId == null || status == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		//然后，我们去创建这个product对象
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		
		//接着，我们就需要更新这个产品
		int rowCount = productMapper.updateByPrimaryKeySelective(product);
		if(rowCount > 0){
			return ServerResponse.createBySuccessMessage("修改产品的销售状态成功");
		}
		return ServerResponse.createByMessage("修改产品的销售状态失败");
	}
	
	/**
	 * 这个方法是获取商品详情的方法,由于这个后台的实现，故我们后台的service实现都是使用manage开头，以便以后的维护及管理
	 */
	/*public ServerResponse<Object> manageProductDetail(Integer productId){
		if(productId == null){
			//若productID为空，则：返回参数错误
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		//接着，通过productId来获取product对象
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			//若获取的product为空的话，则：表示商品已经删除或者下架
			return ServerResponse.createByMessage("商品已下架或者删除");
		}
		//此时，我们就要返回一个vo对象(即：value object)，因为：我们这个项目比较简单，我们就使用vo对象来处理，即：pojo-->vo
		//若是比较复杂的项目，我们就需要将vo对象拆分成：bo和vo，其中，bo：是(business object)业务对象，此时的vo是：(view object 视图层的对象),即：pojo-->bo(business)-->vo(view object)
		//我们还是使用vo来组装我们的业务对象，首先，在pojo的同级别创建vo包，(即：com.mmall.vo)
		ProductDetailVo  productDetailVo =
				
	}
	
	//在下面我们创建一个类,组装产品的vo
	public assembleProductDetailVo(Product product){
		ProductDetailVo productDetailVo = new ProductDetailVo();
		//下面我们就set这些属性
		productDetailVo.setId(product.getId());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setName(product.getName());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setStack(product.getStock());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setSubImages(product.getSubImages());
		
		//imageHost ,它需要从配置文件中获取，将配置和代码分离，我们就在com.mmall.util 包中创建一个类，名称为：PropertiesUtil,用于配置图片
		//parentCategoryId
		//createTime
		//updateTime
	}*/
}
