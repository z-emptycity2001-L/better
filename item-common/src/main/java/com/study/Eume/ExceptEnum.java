package com.study.Eume;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public enum ExceptEnum {

    THREADLOCAL_EXISTED(500,"该线程变量已存在"),
    LOGIN_TYPE_ERROR(500,"不支持的登陆类型"),

    LOGIN_NO_USER_RECORD(500,"不存在该用户"),
    
    LOGIN_PASSWORD_ERROR(500, "密码错误"), 
    
    REGISTER_PASSWORD_TWICE_DIFFERENT(500, "两次密码输入不一致"),
    REGISTER_USER_EXISTED(500, "该用户已存在"),
    COMMON_ERROR_RESPONSE(9999, "系统异常"),
    ROCKETMQ_MSG_SEND_ERROR(500,"消息发送失败" ),
    BLACK_USER_LOGIN(501, "非法登录"),
    USER_LOGIN_ERROR(502, "登陆异常,请重新登录！"),
    USER_LOGIN_TOKEN_TIMEOUT(503, "登陆已过期")

    ;
    /** 错误码 */
    private final int code;
    /** 错误描述 */
    private final String message;

    ExceptEnum(int code, String message){
        this.code=code;
        this.message=message;
    }


}
