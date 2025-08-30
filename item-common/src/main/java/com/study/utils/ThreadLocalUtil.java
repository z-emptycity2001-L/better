package com.study.utils;

import com.study.exception.BusinessException;
import com.study.exception.Eume.ExceptEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
public class ThreadLocalUtil {
    private final static ThreadLocal<Map> threadLocal= new ThreadLocal<>();

    public void setThreadLocal(String key,Object value){
        if (Objects.isNull(threadLocal.get().get(key))){
            threadLocal.get().put(key,value);
        }else {
            throw new BusinessException(ExceptEnum.THREADHOLD.getCode(), ExceptEnum.THREADHOLD.getMessage());
        }
    }

    public <T>T getThreadLocal(String key){
        return (T) threadLocal.get().get(key);
    }

    public void clear(){
        threadLocal.remove();
    }

    public void remove(String key){
        threadLocal.get().remove(key);
    }
}
