package com.mmall.dao;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    int selectUsername(String username);
    
    User selectLogin(@Param("username")String username,@Param("password")String password);
    
    int selectEmail(String email);
    
    String selectQuestionByUsername(String username);
    
    int selectAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
    
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);
    	
    int selectPasswordByUsername(@Param("username")String username,@Param("password")String password);
    
    int checkEmailByUserId(@Param("userId")Integer userId,@Param("email")String email);
}

