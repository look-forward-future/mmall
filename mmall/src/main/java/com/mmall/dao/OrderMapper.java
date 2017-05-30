package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Order;
import com.mmall.pojo.PayInfo;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
    
    Order selectOrderByUserId(@Param(value="userId")Integer userId,@Param(value="orderNo")Long orderNo);
    
    Order selectByOrderNo(Long orderNo);
    
    List<Order> selectOrderListByuserId(Integer userId);
    
    List<Order> selectAllOrder();
    
    Order selectOrderByOrderNo(Long orderNo);
}