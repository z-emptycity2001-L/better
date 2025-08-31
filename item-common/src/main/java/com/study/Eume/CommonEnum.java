package com.study.Eume;

import lombok.Getter;

@Getter
public class CommonEnum {
    /** 错误码 */
    private final int code;
    /** 错误描述 */
    private final String message;

    CommonEnum(int code, String message){
        this.code=code;
        this.message=message;
    }
}
