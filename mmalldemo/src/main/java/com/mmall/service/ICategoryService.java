package com.mmall.service;

import java.util.List;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;



/**
 * 这是一个service接口
 * @author Administrator
 *
 */

public interface ICategoryService {
	//下面是我们添加品类实现的接口
	ServerResponse addCategory(String categoryName,Integer parentId);
	//这是更新品类名称的接口
	ServerResponse updateCategoryName(String categoryName,Integer categoryId);
	//这是查询子节点（平级的），并且返回category信息的接口
	ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
	//下面我们递归查询本节点以及子节点的ID
	ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
