package com.study.entity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    private String type; // 注册类型
    private String username;
    private String password;
    private String validPassword;
    private String email;
    private String phone;
    // 其他字段：手机号、验证码、用户名、密码等
}
