package com.study.util;

import com.study.entity.po.User;
import com.study.utils.redis.RedisUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserRedisUtil extends RedisUtil {

    private static final UserRedisUtil userRedisUtil=new UserRedisUtil();
    /**
     * 用户注销登录
     * @param delKey 删除redis中记录的登录信息
     * @param setKey 将 setVal 放置在黑名单中
     * @param setVal  token值
     * @param time 黑名单放置的时间
     * @return 返回成功结果
     */
    private Long logout(String delKey, String setKey, String setVal, Long time){
        String Lua= """
                local delKey=KEYS[1]
                local setKey=KEYS[2]
                local setVal=ARGV[1]
                local time=ARGV[2]
                -- 删除redis中记录的登录信息
                redis.call('del',delKey)
                将 token记录 放置在黑名单中
                redis.call('set',setKey,setVal)
                -- 设置过期时间
                redis.call('expire',setKey,time)
                end
                -- 返回操作结果
                return 1
                """;
        return super.execute(Lua, List.of(delKey,setKey),setVal,time);
    }

    public static Long getLogout(String delKey, String setKey, String setVal, Long time){
        return userRedisUtil.logout(delKey, setKey, setVal, time);
    }
}
