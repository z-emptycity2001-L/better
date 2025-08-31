package com.study.client;

import com.study.client.fallback.UserServiceFallback;
import com.study.entity.dto.ResponseDto;
import com.study.entity.dto.user.LoginDTO;
import com.study.entity.dto.user.RegisterDTO;
import com.study.entity.po.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "user-service", fallback = UserServiceFallback.class)
@RequestMapping("/user")
public interface UserClient {
    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginDTO loginDTO);

    @PostMapping("/register")
    public ResponseDto register(@RequestBody RegisterDTO registerDTO);

    @PostMapping("/logout")
    public ResponseDto logout(@RequestBody LoginDTO loginDTO);

    public ResponseDto changeUserInfo(@RequestBody User user);
}
