package com.study.client.impl;

import com.study.client.UserClient;
import com.study.entity.dto.ResponseDto;
import com.study.entity.dto.user.LoginDTO;
import com.study.entity.dto.user.RegisterDTO;
import com.study.entity.po.User;
import com.study.mapper.UserMapper;
import com.study.strategy.LoginStrategyFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserClient {
    private LoginStrategyFactory loginStrategyFactory;
    @Autowired
    private UserMapper userMapper;
    public ResponseDto login(LoginDTO loginDTO) {
        return LoginStrategyFactory.getStrategy(loginDTO.getType()).login(loginDTO);
    }

    @Override
    public ResponseDto register(RegisterDTO registerDTO) {
        return LoginStrategyFactory.getStrategy(registerDTO.getType()).register(registerDTO);
    }

    @Override
    public ResponseDto logout(LoginDTO loginDTO) {
        return LoginStrategyFactory.getStrategy(loginDTO.getType()).logout(loginDTO);
    }

    @Override
    public ResponseDto changeUserInfo(User user) {
        userMapper.updateUser(user);
        return ResponseDto.success("success");
    }
}
