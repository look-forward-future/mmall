package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.OrderItem;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
    
    List<OrderItem> getOrderItem(@Param(value="userId")Integer userId,@Param(value="orderNo")Long orderNo);
    //下面写一个使用mybatis来批量插入的方法
    void batchInsert(@Param(value="orderItemList")List<OrderItem> orderItemList);
    
    List<OrderItem> getOrderItemByOrderNo(Long orderNo);
    
}