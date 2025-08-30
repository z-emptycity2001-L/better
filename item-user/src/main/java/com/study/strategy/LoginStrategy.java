package com.study.strategy;

import com.study.entity.dto.ResponseDto;
import com.study.entity.dto.user.LoginDTO;
import com.study.entity.dto.user.RegisterDTO;

public interface LoginStrategy {
    // 登录方法
    ResponseDto login(LoginDTO loginDTO);
    // 注册方法
    ResponseDto register(RegisterDTO registerDTO);
}
