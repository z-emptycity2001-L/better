package com.study.utils;

import com.study.RocketMQ.Publish.CommonMsgBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;
@Data
public class CommonResponse<T> {
    /** 状态码：200表示成功，非200表示失败 */
    private int code;
    /** 响应消息 */
    private String message;
    /** 响应数据（成功时返回） */
    private T data;

    // 成功响应（带数据）
    public static <T> CommonResponse<T> success(String message,T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    // 成功响应（无数据）
    public static <T> CommonResponse<T> success(String message) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(200);
        response.setMessage(message);
        return response;
    }

    // 失败响应（自定义状态码和消息）
    public static <T> CommonResponse<T> fail(int code,String message) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    public static <T> CommonResponse<T> fail( String message) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    public static <T>CommonResponse<T> of(String message){
        return CommonResponse.fail(500,message);
    }
}
