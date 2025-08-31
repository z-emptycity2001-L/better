package com.study.entity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private String type; // 登录类型：phone/password/email
    private String phone;
    private String email;
//    private String code;
    private String username;
    private String password;

    private Long id;
}
