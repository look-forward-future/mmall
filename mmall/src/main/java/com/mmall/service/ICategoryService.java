package com.mmall.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

/**
 * 这是品类service业务逻辑类的接口类
 * @author Administrator
 *
 */

public interface ICategoryService {
	//添加品类的接口
	ServerResponse addCategory(String categoryName,Integer parentId);
	//修改品类名称的接口
	ServerResponse setCategoryName(Integer categoryId,String categoryName);
	//获取品类子节点(平级)
	ServerResponse<List<Category>> getCategory(Integer categoryId);
	//递归查询本节点及其子节点的id
	ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
