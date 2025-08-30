package com.study.strategy.impl;

import com.study.entity.dto.ResponseDto;
import com.study.entity.dto.user.LoginDTO;
import com.study.entity.dto.user.RegisterDTO;
import com.study.strategy.LoginStrategy;

public class PhoneLoginStrategy implements LoginStrategy {
    public ResponseDto<LoginDTO> login(LoginDTO loginDTO) {
        return null;
    }

    public ResponseDto<RegisterDTO> register(RegisterDTO registerDTO) {
        return null;
    }
}
