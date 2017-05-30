package com.mmall.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;

/**
 * 这是业务逻辑类
 * @author Administrator
 *
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{
	
	//定义logger
	private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired
	private CategoryMapper categoryMapper;

	/**
	 * 添加品类
	 * @param categoryName  (表示的品类的名称)
	 * @param parentId  (这个参数：表示的是：品类的父节点)
	 * @return
	 */
	public ServerResponse addCategory(String categoryName,Integer parentId){
		//首先，对参数进行非空判断
		if(parentId == null || StringUtils.isBlank(categoryName)){
			return ServerResponse.createByErrorMessage("添加参数错误！");
		}
		//创建category类的实例
		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);//代表是可用的
		
		int resultCount = categoryMapper.insert(category);
		if(resultCount > 0){
			return ServerResponse.createBySuccessMessage("添加品类成功！");
		}
		return ServerResponse.createByErrorMessage("添加品类失败！");
	}
	
	//这是修改品类的名称
	public ServerResponse setCategoryName(Integer categoryId,String categoryName){
		if(categoryId == null || StringUtils.isBlank(categoryName)){
			return ServerResponse.createByErrorMessage("添加参数错误！");
		}
		
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);
		
		int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
		if(resultCount > 0){
			return ServerResponse.createBySuccessMessage("更新品类名称成功！");
		}
		return ServerResponse.createByErrorMessage("更新品类名称失败！");
	}
	
	/**
	 * 获取品类的子节点(平级)
	 */
	public ServerResponse<List<Category>> getCategory(Integer categoryId){
		
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		if(CollectionUtils.isEmpty(categoryList)){
			//判断当从数据库中获取的品类为空的话，打印一条logger
			logger.info("未找到当前分类的子分类！");
		}
		return ServerResponse.createBySuccess("获取品类的子节点成功！", categoryList);
	}
	
	/**
	 * 递归查询本节点id及其子节点的id
	 */
	public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){

		//对categorySet这个set集合进行初始化
		Set<Category> categorySet = Sets.newHashSet();
		//接着，调用递归算法
		findChildCategory(categorySet,categoryId);
		
		//由于我们要获取category的id,故我们要先创建一个有int类型组成的集合，并进行初始化
		List<Integer> categoryIdList = Lists.newArrayList();
		//然后，判断categoryId是否为空，若不为空，就遍历我们从递归方法中返回的集合，将集合中对象的id都添加到我们categoryIdList中去
		if(categoryId != null){
			for(Category categoryItem:categorySet){
				categoryIdList.add(categoryItem.getId());
			}
			return ServerResponse.createBySuccess(categoryIdList);
		}
		return ServerResponse.createByErrorMessage("未传入参数categoryId,请传入参数后，在操作！");
	}
	
	/*下面在写一个递归方法，获取节点、子节点以及孙子节点。。。。,这个方法返回值我们定义的是：set<Category>，是为了排除重复对象，
	 * 但是，这个set集合中的对象不是基本类型，如：String、int.....因为：基本类型都已经写好了equals()方法和hashCode()方法，
	 * 然而，此时的：Category类型没有，需要在：com.mmall.pojo下的：Category.java类中去写：equals()和hashCode()方法
	 *用于，对添加进set集合中的对象进行判断是否是相同的对象
	 */
	public Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		//当category对象不为空时，就将这个对象添加到set集合中，并将这个Set集合作为参数
		if(category != null){
			categorySet.add(category);
		}
		//下面为这个递归算法设置一个结束条件,就调用上面的方法获取子节点的方法,知道下面这个方法没有返回值为止
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		//遍历这个List
		for(Category categoryItem:categoryList){
			findChildCategory(categorySet,categoryItem.getId());
		}
		return categorySet;
	}
}
