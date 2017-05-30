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
    
    int checkUsername(String username);
    
    int checkEmail(String email);
    
    //如果我们的接口方法中有多个参数，就需要使用mybatis的@Param注解
    User selectLogin(@Param("username")String username,@Param("password")String password);
    //通过用户名来查询问题
    String selectQuestionByUsername(String username);
    //查询问题的答案
    int selectAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
    //更新密码
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);
    //检查密码
    int checkPassword(@Param(value="password")String password,@Param("userId")Integer userId);
    //通过用户名来校验email是否存在
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
    
    
}