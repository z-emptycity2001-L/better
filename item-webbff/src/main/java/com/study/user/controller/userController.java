package com.study.user.controller;


import com.study.client.impl.UserServiceImpl;
import com.study.entity.dto.ResponseDto;
import com.study.entity.dto.user.LoginDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class userController {
    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginDTO loginDTO){
        return null;
    }
}
