package com.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;

/**
 * 产品的业务逻辑类
 * @author Administrator
 *
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	@Autowired
	private ICategoryService iCategoryService;
	
	/**
	 * 保存、新增以及更新产品
	 * @param product
	 * @return
	 */
	public ServerResponse<String> saveOrUpdateProduct(Product product){
		if(product != null){
			//如果产品的子图不为空的话，先将子图分割开来，再将子图中的第一张图作为主图
			if(StringUtils.isBlank(product.getSubImages())){
				String[] subImageArray = product.getSubImages().split(",");
				if(subImageArray.length > 0){
					product.setMainImage(subImageArray[0]);
				}
			}
			
			//下面我们判断我们是否传了产品的id,若传入了产品id,我们就更新这个产品，否则，就新增这个产品
			if(product.getId() != null){
				int rowCount = productMapper.updateByPrimaryKey(product);
				if(rowCount > 0){
					return ServerResponse.createBySuccessMessage("更新产品成功！");
				}
				return ServerResponse.createByErrorMessage("更新产品失败！");
			}
			
			int resultCount = productMapper.insert(product); 
			if(resultCount > 0){
				return ServerResponse.createBySuccessMessage("新增产品成功！");
			}
			return ServerResponse.createByErrorMessage("新增产品失败！");
		}
		else{
			return ServerResponse.createByErrorMessage("新增或者更新产品参数错误！");
		}
		
	}
	
	/**
	 * 更改产品的上下架的状态
	 * @param productId
	 * @param status
	 * @return
	 */
	//这里我们为什么要传入一个参数productId,是因为：指定更改哪一个产品的上下架状态
	public ServerResponse<String> setSaleStatues(Integer productId,Integer status){
		if(productId == null || status == null){
			return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		Product product = new Product();
		
		product.setId(productId); 
		product.setStatus(status);
		
		int rowCount = productMapper.updateByPrimaryKeySelective(product);
		if(rowCount > 0){
			return ServerResponse.createBySuccessMessage("修改产品的销售状态成功！");
		}
		return ServerResponse.createByErrorMessage("修改产品的销售状态失败！");
	}
	
	/**
	 * 查询产品的详细信息
	 * 需要返回一个vo(Value Object)
	 * @param productId
	 * @return
	 */
	//由于它是后台的，为了便于管理，将其方法名以：manage来开头
	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
		if(productId == null){
			return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServerResponse.createByErrorMessage("产品已下架或者删除！");
		}
		//接着，我们就要返回一个VO对象(Value Object)，因为：我们要将数据返回给前端  如果是：比较复杂的业务逻辑的话，就需要：pojo-->bo(business Object)-->vo(Value Object)
		//即：使用vo来组装我们的业务对象,首先，在src/main/java下创建包名为：com.mmall.vo，
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		
		return ServerResponse.createBySuccess(productDetailVo);
	}
	
	//接着，在下面我们创建一个类，将productDetailVo组装上
	private ProductDetailVo assembleProductDetailVo(Product product){
		ProductDetailVo productDetailVo = new ProductDetailVo();
		
		productDetailVo.setId(product.getId());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setSubImages(product.getSubImages());
		productDetailVo.setName(product.getName());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setStock(product.getStock());
		
		//这里的imageHost我们从配置中来获取，不需要硬编码到我们的业务逻辑中
		productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
	
		//parentCategoryId
		//这里我们调用品类管理模块中的接口方法
		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId()); 
		//当category为：null时，说明：当前为：根节点(即：0)
		if(category == null){
			productDetailVo.setParentCategoryId(0);
		}
		productDetailVo.setParentCategoryId(category.getParentId());
		
		//由于：我们从数据库中获取的时间是以：秒的形式储存的，在前端不便于查看，下面我们要将createTime 和 updateTime 在前端以年月日时分秒的形式展示出来，需要对其进行格式的转换
		//createTime
		productDetailVo.setCreateTime(DateTimeUtil.dateOrStr(product.getCreateTime()));
		
		//updateTime
		productDetailVo.setUpdateTime(DateTimeUtil.dateOrStr(product.getUpdateTime()));
		
		return productDetailVo;
	}
	
	/**
	 * 后台查询商品的列表
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
		/*
		 * 我们在这里要使用分页插件来对商品进行分页处理，具体的步骤如下：
		 * 1.startPage-->start
		 * 2.填充自己的SQL查询逻辑
		 * 3.pageHelper-->收尾
		 */
		PageHelper.startPage(pageNum, pageSize);
		//下面我们就执行sql逻辑，就是：获取商品的列表，由于：productMapper.java类中没有这样的sql语句，故：需要进入此类中书写
		List<Product> productList = productMapper.selectList();
		//但是我们的list中不需要Product类中这么详细的属性，我们就在：com.mmall.vo包下创建一个类名：ProductListVo,在这个新建的类中去把我们需要的属性定义进去
		//这里我们就可以获取组装的方法
		//定义一个list集合，并进行初始化
		List<ProductListVo> prodcutListVoList = Lists.newArrayList();
		
		for(Product productItem:productList){
			ProductListVo productListVo = assembleProductListVo(productItem);
			prodcutListVoList.add(productListVo);
		}
		//接下来，进行的收尾工作
		PageInfo pageResult = new PageInfo(productList);
		//由于我们给前端展示的不是这个product，而是将product进行分页，此时，我们将其重置的就可以啦
		pageResult.setList(prodcutListVoList);
		return ServerResponse.createBySuccess(pageResult);
	}
	
	
	//在下面写一个productListVo的组装方法
	public ProductListVo assembleProductListVo(Product product){
		//首先，创建ProductListVo类的实例
		ProductListVo productList = new ProductListVo();
		//从product类中获取属性值填充到ProductListVo类中的属性中
		productList.setId(product.getId());
		productList.setCategoryId(product.getCategoryId());
		productList.setName(product.getName());
		productList.setSubtitle(product.getSubtitle());
		productList.setPrice(product.getPrice());
		productList.setMainImage(product.getMainImage());
		productList.setStatus(product.getStatus());
		
		productList.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
		return productList;
		
	}
	
	/**
	 * 后台商品搜索功能
	 * @param productName
	 * @param productId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public ServerResponse<PageInfo> productSearch(String productName,Integer productId,int pageNum,int pageSize){
		//首先，还是使用pageHelper分页插件
		PageHelper.startPage(pageNum, pageSize);
		//接下来，在判断一下，productName是否为空，若不为空的话，则：我们就用SQL中的模糊查询构建一下这个productName
		if(StringUtils.isNotBlank(productName)){
			productName = new StringBuilder().append("%").append(productName).append("%").toString();
		}
		List<Product> productList = productMapper.selectByNameAndproductId(productName, productId);
		
List<ProductListVo> prodcutListVoList = Lists.newArrayList();
		
		for(Product productItem:productList){
			ProductListVo productListVo = assembleProductListVo(productItem);
			prodcutListVoList.add(productListVo);
		}
		//接下来，进行的收尾工作
		PageInfo pageResult = new PageInfo(productList);
		//由于我们给前端展示的不是这个product，而是将product进行分页，此时，我们将其重置的就可以啦
		pageResult.setList(prodcutListVoList);
		return ServerResponse.createBySuccess(pageResult);
	}
	
	/**
	 * 前端获取商品的详情
	 * @param productId
	 * @return
	 */
	public ServerResponse<ProductDetailVo> getProductDetails(Integer productId){
		if(productId == null){
			return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		//可以通过productId来获取商品的信息
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServerResponse.createByErrorMessage("该商品已经删除或者下架！");
		}
		//由于是前端获取商品的详情，故：我们还要判断一下，商品是否已经下架
		if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
			return ServerResponse.createByErrorMessage("该商品已经删除或者下架！");
		}
		//下面我们使用：value Object来进行组装
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.createBySuccess(productDetailVo);
	}
	
	
	/**
	 * 前端查询商品的列表(通过商品的名称或者商品的品类id来获取商品)
	 * 这个方法既使用于：单独使用productName和categoryId来查询商品的列表，还可以：组合来查询商品的列表(即：当keyWord和categoryId都不为空的时候)
	 * @param keyWord
	 * @param categoryId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public ServerResponse<PageInfo> getListByCategoryIdAndKeyword(String keyWord,Integer categoryId,int pageNum,int pageSize,String orderBy){
		//首先对参数进行判断
		if(StringUtils.isBlank(keyWord) && categoryId == null){
			return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		//这里定义一个有：ID组成的List列表
		List<Integer> categoryIdList = new ArrayList<Integer>();
		//当categoryId不为空的时候
		if(categoryId != null){
			//可以通过categoryId来获取商品的品类
			Category category = categoryMapper.selectByPrimaryKey(categoryId);
			if(category == null && StringUtils.isBlank(keyWord)){
				//我们没有获取品类，是在品类id存在的情况下，我们返回一个空集合，不报错
				List<ProductListVo> productList = Lists.newArrayList();
				//开始分页
				PageHelper.startPage(pageNum, pageSize);
				//进行分页操作
				PageInfo resultList = new PageInfo(productList);
				return ServerResponse.createBySuccess(resultList);
			}
			//接下来，我们获取品类id及子节点的id集合，便于：我们后面再mysql中去in这个ID的集合来获取商品，需要调用我们前面的递归算法
			categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
		}
		//当keyWord不为空的时候
		if(StringUtils.isNotBlank(keyWord)){
			//进行组装一下keyWord
			keyWord = new StringBuilder().append("%").append(keyWord).append("%").toString();
		}
		//开始分页
		PageHelper.startPage(pageNum, pageSize);
		//排序处理，首先，有一个参数：orderBy需要声明
		//开始进行动态排序
		if(StringUtils.isNotBlank(orderBy)){
			//下面来判断一下，我们传入的字符串是否在我们刚刚创建的接口类中的set集合中的元素
			if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
				//然后，我们将传入的orderBy参数以："_"来进行分割
				String[] orderByArray = orderBy.split("_");
				//调用PageHelper的orderBy方法,方法中参数的格式为："price asc"
				PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
				
			}
		}
		//我们就需要获取productList,需要在ProductMapper中去写SQL
		//由于我们在前面categoryIdList由于new了一下，在前面已经赋值啦，如果传入categoryIdList为空集合的话，sql中的in不到元素，就会找不到商品，
		//因此，我们使用运算符来判断一下，如下：
		List<Product> productList = productMapper.selectByNameAndProductIds(StringUtils.isBlank(keyWord)?null:keyWord, categoryIdList.size() == 0?null:categoryIdList);
		//我们拿到List<Product>，就可以去组装：productListVo
		List<ProductListVo> productListVoList = Lists.newArrayList();
		//接下来，对这个productList遍历
		for(Product product:productList){
			ProductListVo productListVo = assembleProductListVo(product);
			productListVoList.add(productListVo);
		}
		PageInfo pageInfo = new PageInfo(productList);
		//然后，把集合置成：productListVoList
		pageInfo.setList(productListVoList);
		return ServerResponse.createBySuccess(pageInfo);
	}
	
	
}
