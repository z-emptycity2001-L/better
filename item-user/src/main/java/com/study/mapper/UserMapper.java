package com.study.mapper;


import com.study.entity.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    /*========================***增***========================*/
    Integer insertUser(User user);
    /*========================***查***========================*/
    User selectUserByUsername(String username);
    User selectUserByMailbox(String mailbox);

    User selectUserById(Long id);

    /*========================***改***========================*/
    Integer updateUserById(User user);

    Integer updateUser(User user);

    Integer updateUserPasswordByMailbox(String password);//根据邮箱找回密码

    Integer deleteUserByUsername(String username);

}
