package com.study.factory;

import com.study.client.UserClient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserApiFactory {
    public static UserClient userClientApi;

    public UserClient getUserClientApi(){
        return userClientApi;
    }
}
