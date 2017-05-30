package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Shipping;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);
    
    int deleteByUserIdAndShippingId(@Param(value="userId")Integer userId,@Param(value="shippingId")Integer shippingId);
    
    int updateByShipping(Shipping record);
    
    Shipping selectByUserIdAndshippingId(@Param(value="userId")Integer userId,@Param(value="shippingId")Integer shippingId);
    
    List<Shipping> selectByUserId(Integer userId);
}