package com.study.entity.po;

import com.study.entity.dto.user.LoginDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String sex;
    private Integer age;
    private String mailbox;
    private String idNumber;
    private String avatar;
    private String createTime;
    private String updateTime;
    public User(LoginDTO userDto){
        this.username=userDto.getUsername();
        this.password=userDto.getPassword();
        this.mailbox=userDto.getEmail();
    }
}
