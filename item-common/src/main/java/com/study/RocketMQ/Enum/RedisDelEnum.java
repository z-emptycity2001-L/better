package com.study.RocketMQ.Enum;

import lombok.Data;

@Data
public class RedisDelEnum<T> {


    private String topic;
    private T body;


}
