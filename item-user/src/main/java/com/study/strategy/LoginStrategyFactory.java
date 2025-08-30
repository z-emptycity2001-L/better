package com.study.strategy;

import com.study.exception.Eume.ExceptEnum;
import com.study.strategy.impl.EmailLoginStrategy;
import com.study.strategy.impl.PasswordLoginStrategy;
import com.study.strategy.impl.PhoneLoginStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginStrategyFactory {
    // 存储策略类型与实例的映射（避免重复创建）
    private static final Map<String, LoginStrategy> STRATEGY_MAP = new ConcurrentHashMap<>();

    // 静态初始化：注册所有策略
    static {
        STRATEGY_MAP.put("phone", new PhoneLoginStrategy());
        STRATEGY_MAP.put("password", new PasswordLoginStrategy());
        STRATEGY_MAP.put("wechat", new EmailLoginStrategy());
    }

    // 根据类型获取策略
    public static LoginStrategy getStrategy(String type) {
        LoginStrategy strategy = STRATEGY_MAP.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException(ExceptEnum.LOGIN_TYPE_ERROR.getMessage());
        }
        return strategy;
    }
}
