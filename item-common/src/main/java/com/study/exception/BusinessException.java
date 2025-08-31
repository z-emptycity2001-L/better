package com.study.exception;

import com.study.Eume.ExceptEnum;
import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter // 提供getter方法，便于全局异常处理器获取信息
public class BusinessException extends RuntimeException {
    /** 错误码（非200） */
    private final int code;

    /**
     * 构造方法：传入错误码和消息
     */
    public BusinessException(int code, String message) {
        super(message); // 父类 RuntimeException 的消息
        this.code = code;
    }

    public BusinessException(ExceptEnum exceptEnum){
        super(exceptEnum.getMessage());
        this.code= exceptEnum.getCode();
    }

    /**
     * 快速创建异常的静态方法（可选）
     */
    public static BusinessException of(int code, String message) {
        return new BusinessException(code, message);
    }

    public static BusinessException of(ExceptEnum exceptEnum) {
        return new BusinessException(exceptEnum);
    }
}

