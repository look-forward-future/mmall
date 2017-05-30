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
 * 这个类是接口的实现类
 * @author Administrator
 *
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
	//定义logger
	private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	/*我们实现添加品类的功能的方法,注意：这里的parentId的类型写成：Integer(int的包装类，即：整型)，使其具备对象的特征
	 * 否则：下面我们队parentId为空的判断就会报错
	*/
	public ServerResponse addCategory(String categoryName,Integer parentId){
		if(parentId == null || StringUtils.isBlank(categoryName)){
			return ServerResponse.createByMessage("添加品类参数错误");
		}
		//接着，我们new一下Category类
		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		//设置这个分类是可用的
		category.setStatus(true);
		
		//接下来，我们来判断添加品类是否成功
		int rowCount = categoryMapper.insert(category);
		if(rowCount > 0){
			return ServerResponse.createBySuccessMessage("添加品类成功");
		}
		return ServerResponse.createByMessage("添加品类失败");
	}
	
	/**
	 * 这个方法是：我们更新品类的名称
	 * **********注意：这个方法中的categoryId表示的是数据表中的属性：id**********
	 */
	public ServerResponse updateCategoryName(String categoryName,Integer categoryId){
		if(categoryId == null || StringUtils.isBlank(categoryName)){
			return ServerResponse.createByMessage("更新品类名称错误");
		}
		
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);
		////上面为什么要setId()呢？？是因为：我们的update...方法也是通过主键来进行更新，并且是选择性的更新，这个只更新categoryName
		int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
		if(rowCount > 0){
			return ServerResponse.createBySuccessMessage("更新品类名字成功");
		}
		return ServerResponse.createByMessage("更新品类名字失败");
		
	}
	
	/*
	 * 查询子节点的category信息,并打印出category信息
	 * ********注意：下面的方法中的参数categoryId代表的是数据表中的属性：parentId**********
	 */
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		//判断这个集合是否为空，若为空，则：不需要向前端发送数据，只需要打印一条日志即可
		if(CollectionUtils.isEmpty(categoryList)){
			logger.info("未找到当前分类的子分类");
		}
		return ServerResponse.createBySuccess(categoryList);
	}
	
	/**
	 * 递归查询节点及子节点的id
	 * @param category
	 * @return
	 */
	public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
		//下面我们使用guava中提供的方法,使用来初始化
		//我们的参数中的categorySet的初始化就是在调用递归算法的时候进行初始化
		Set<Category> categorySet = Sets.newHashSet();
		//下面我们调用下面的递归方法
		findChildCategory(categorySet,categoryId);
		//由于我们返回的是字节即子节点的id,我们在初始话一个Inetger的List集合
		List<Integer> categoryIdList = Lists.newArrayList();
		if(categoryId != null){
			for(Category categoryItem:categorySet){
				//下面我们就将Category对象中的id添加到上面创建的List集合中去
				categoryIdList.add(categoryItem.getId());
			}
			
		}
		return ServerResponse.createBySuccess(categoryIdList);
	}
	
	/*下面我们来写一个递归函数，使用set<Category>可以直接排除重复,由于set集合中的Category不是基本的类型，如：int、String....，基本类型中都已经写了hashcode()和equals()方法，
	 *故：我们定义的category类型需要写hashcode()和equals()方法，来比较两个对象是否相同，由于我们需要排除重复
	 *注意：当我们使用set集合时，最好重写hashcode()和equals()方法，最好让这两个方法中的判断因子都相同，可以避免意想不到的问题
	*/
	/*递归算法，算出子节点
	 * ********下面的递归方法中的属性categoryId代表的是数据表中的parentId***********
	 * 下面的递归方法中我们有添加了一个参数为：Set<Category>，也就是：我们在拿这个方法本身的返回值当成这个方法的参数再调用这个方法
	 * 
	*/
	private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
		//当categoryId传入：0的时候，返回的对象就为空
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		//当category不为空的时候，即：
		if(category != null){
			//下面是将category添加到categorySet集合中
			categorySet.add(category);
		}
		
		//查找子节点，递归算法一定要有一个退出的条件
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		/*
		 *这里要注意：这是mybatis返回的一个集合，mybatis返回集合的处理是：如果没有查找到子节点的话，它不会返回一个null，即：categoryList不会是一个null,
		 *故：这里我们也就不需要对这个集合进行空判断，如果我们是调用一些不可预知的方法，我们就要进行空判断
		 */
		//接着，我们迭代一个这个集合
		for(Category categoryItem:categoryList){
			//下面我们调用自己本身的方法,当集合遍历完后，就会退出for循环，返回集合Set
			//下面是进入递归
			findChildCategory(categorySet,categoryItem.getId());
		}
		return categorySet;
	}

}
