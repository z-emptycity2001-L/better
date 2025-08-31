package com.study.entity.dto;

import com.study.utils.CommonResponse;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 统一响应类
 * @param <T> 响应数据类型
 */
@Data
public class ResponseDto<T> extends CommonResponse<T> {
    /** 状态码：200表示成功，非200表示失败 */
    private int code;
    /** 响应消息 */
    private String message;
    /** 响应数据（成功时返回） */
    private T data;

    // 成功响应（带数据）
    public static <T> ResponseDto<T> success(T data) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    // 成功响应（无数据）
    public static <T> ResponseDto<T> success(@DefaultValue(value = "null") String message) {
        return success(message);
    }

    // 失败响应（自定义状态码和消息）
    public static <T> ResponseDto<T> fail(int code,@DefaultValue("null") String message) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}
