package com.study.utils.redis;

import com.study.Constants.redis.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void set(String key,Object value){
        redisTemplate.opsForValue().set(key,value);
    }
    public void set(String key,Integer value){
        redisTemplate.opsForValue().set(key,value);
    }
    public void set(String key,Object value,Long time,TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value,time,timeUnit);
    }
    public void set(String key,Integer value,Long time,TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value,time,timeUnit);
    }
    public void set(String key1,String key2,Object value){
        redisTemplate.opsForHash().put(key1,key2,value);
    }
    public void set(String key1,String key2,Integer value){
        redisTemplate.opsForHash().put(key1,key2,value);
    }


    public Long setAdd(String key1, String... key2){
        return redisTemplate.opsForSet().add(key1, key2);
    }



    public Boolean setnx(String key,Object value){
        return redisTemplate.opsForValue().setIfAbsent(key,value);
    }
    public Boolean setnx(String key,Integer value){
        return redisTemplate.opsForValue().setIfAbsent(key,value);
    }
    public Boolean setnx(String key,Object value,Long time,TimeUnit timeUnit){
        return redisTemplate.opsForValue().setIfAbsent(key,value,time,timeUnit);
    }
    public Boolean setnx(String key,Integer value,Long time,TimeUnit timeUnit){
        return redisTemplate.opsForValue().setIfAbsent(key,value,time,timeUnit);
    }
    public Boolean setnx(String key1,String key2,Integer value){
        return redisTemplate.opsForHash().putIfAbsent(key1,key2,value);
    }
    public Boolean setnx(String key1,String key2,Object value){
        return redisTemplate.opsForHash().putIfAbsent(key1,key2,value);
    }
    public Boolean exist(String key){
        return redisTemplate.hasKey(key);
    }




    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Object get(String key1,String key2){
        return redisTemplate.opsForHash().get(key1,key2);
    }

    public Set<Object> getSet(String key){
        return redisTemplate.opsForSet().members(key);
    }

    public Object[] getHashKeys(String key){
        Set<Object> keys = redisTemplate.opsForHash().keys(key);
        return keys.toArray();
    }

    public Boolean containsKey(String key){
        return redisTemplate.hasKey(key);
    }

    public Boolean setExpire(String key,Long time, TimeUnit timeUnit){
        return redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 移除key的有效期
     * @param key 要移除的key
     */
    public Boolean removeExpire(String key){
        return redisTemplate.persist(key);
    }

    /**
     * 获取键的剩余过期时间
     * @param key 键
     * @return 返回键的剩余过期时间 Long类型
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public Long inOrDeValue(String key,Integer value){
        if(value.equals(1))
            return redisTemplate.opsForValue().increment(key);
        else
            return redisTemplate.opsForValue().decrement(key);
    }
    public Long hashIncr(String key,Object field,Integer value){
        return redisTemplate.opsForHash().increment(key, field, 1);
    }

    public Boolean delete(String key){
        return redisTemplate.delete(key);
    }
    public void delete(String key1,String key2){
        redisTemplate.opsForHash().delete(key1, key2);
    }
    public void setDelete(String key1,Object... key2){
        redisTemplate.opsForSet().remove(key1,  key2);
    }

    public Long execute(String script, List<String> KEYS, Object... args){
        return redisTemplate.execute(new DefaultRedisScript<>(script,Long.class),KEYS, args);
    }


    public Boolean zSet(String key,Object value,Long score){
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public void PipeZSet(String key,Map<Long,String> map){
//        Set<ZSetOperations.TypedTuple<Object>> tuples=new HashSet<>();
//        for(Long k:map.keySet()){
//            tuples.add(new ZSetOperations.TypedTuple<>(k,map.get(k)) {
//            })
//        }
        redisTemplate.executePipelined((RedisCallback<Void>)session->{
            for (Map.Entry<Long, String> entry : map.entrySet()) {
                Long score = entry.getKey();
                String member = entry.getValue();
                session.zSetCommands().zAdd(key.getBytes(), score, member.getBytes()
                );}
            return null;
        });
    }

    public Boolean zExist(String key,Object value){
        Double score = redisTemplate.opsForZSet().score(key, value);
        return Objects.isNull(score);
    }
    public Integer zSetSize(String key){
        Long size = redisTemplate.opsForZSet().size(key);
        if (Objects.isNull(size))throw new RuntimeException("系统异常！！！");
        return Integer.parseInt(size.toString());
    }

    public static final long BEGIN_TIMESTAMP=1740126519810L;
    //    序列号的位数
    public static final int BIT_LENGTH=31;
    /**
     * redis的分布式id格式：
     *     1位符号位+31位时间戳+32位序列号
     * @param keyPrefix 哪个业务需要生成分布式id，不同的业务得到的序列号是不一样的
     * @return 返回分布式id
     */
    public long generateId(String keyPrefix){
//        1.生成时间戳
        long now = System.currentTimeMillis();
        long timestamp=now-BEGIN_TIMESTAMP;

//        生成序列号
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");
//        获取当前日期，精确到天
        String date = Instant.ofEpochMilli(now).atZone(ZoneOffset.UTC).toLocalDate().format(dateTimeFormatter);
        Long count = redisTemplate.opsForValue().increment("REDIS_ID:" + keyPrefix + ":" + date);
        assert count!=null;
        return (timestamp << BIT_LENGTH) | count;
    }

    /**
     * 基于zset的时间窗口限流算法实现非法ip拦截
     * @param key zset的key
     * @param timeWindow 时间窗口大小
     * @param threshold 阈值
     * @param currentTime 当前时间
     * @return 返回成功与否
     */
    public boolean filterIp(String key, int timeWindow, int threshold, Long currentTime) {
        String lua= """
                    -- 这个ip的key值
                    local key = KEYS[1]
                    -- 时间窗口
                    local window = tonumber(ARGV[1])
                    -- 阈值
                    local limit = tonumber(ARGV[2])
                    -- 现在的时间
                    local now = tonumber(ARGV[3])
                    -- 移除窗口外的数据
                    redis.call('ZREMRANGEBYSCORE', key, 0, now - window)
                    -- 获取当前窗口内该ip的访问量
                    local count = redis.call('ZCARD', key)
                    -- 小于阈值，那么添加访问量
                    if count < limit then
                        redis.call('ZADD', key, now, now)
                        redis.call('PEXPIRE', key, window)
                        return 1
                    else
                        return 0
                    end""";
        Long execute = execute(lua, Collections.singletonList(key), timeWindow, threshold, currentTime);
        if(Objects.isNull(execute)||execute==0){
            throw new RuntimeException("访问过于频繁，请稍后再试！！！");
        }
        return execute==1;
    }

    /**
     * 基于固定时间窗口的hot key 监控算法
     * 时间边界问题	存在窗口切换时的计数遗漏（如流量跨窗口）
     * 内存消耗	    低（仅需存储计数和时间戳）
     * 计算复杂度	    O(1)（哈希表直接读写）
     * 适用场景	    中低频场景，允许少量误差
     *
     * 判断该key：field是否为热key
     * @param key hash中的大key，zset中的key
     * @param field hash中的小key，zset中的member
     * @param threshold 阈值，大于此阈值的hash的value都会被认定为 hot key
     * @param window 时间窗口，用于判定key:field是否过期 在zset中表示为score
     * @param current 当前时间戳
     * @return 返回是否为 hot key
     */
    public boolean countHotKey(String key,String field,int threshold,int window,long current){
        String countKey = "hot:count:" + key;
        String timeKey = "hot:time:" + key;
        String lua= """
                local countKey=KEYS[1]
                local timeKey=KEYS[2]
                local field=ARGV[1]
                local threshold=tonumber(ARGV[2])
                local window=tonumber(ARGV[3])
                local current=redis.call('time')[1]
                current=tonumber(current)
                -- 查看该key：field是否过期
                local score=redis.call('ZSCORE', timeKey,field)
                --如果 不存在该key：field 或者 过期了
                if not score or (current-tonumber(score))>window then
                    --更新hash和zset中该key：field的值
                    redis.call('hset', countKey, field, 1)
                    redis.call('ZADD', timeKey, current, field)
                    -- 设置键的过期时间，防止内存泄漏
                    redis.call('EXPIRE', countKey, window * 2)
                    redis.call('EXPIRE', timeKey, window * 2)
                    return 0
                else
                    -- 如果没过期，则判断其访问量是否大于阈值
                    local count=redis.call('hincrby',countKey,field,1)
                    if count>=threshold then
                        -- 大于阈值，返回0 表示判定为热key
                        return 1
                    else
                        return 0
                    end
                end
                """;

        Long execute = execute(lua, List.of(countKey,timeKey), field,threshold,window,current);
        return execute==1;
    }

    /**
     * 能够有效处理秒杀、突发流量等瞬时流量剧增场景 令牌桶算法
     * @param key 限流接口名
     * @param currentTime 当前时间戳
     * @param rate 令牌生成速率
     * @param threshold 令牌桶容量
     * @return 返回接口是否访问成功
     */

    public boolean connectorLimiterByTokenBucket(String key,long currentTime,int rate,int threshold){
        String lua=
                """
                        -- KEYS[1] = 当前令牌数键名
                        -- KEYS[2] = 最后一次获取令牌的时间戳键名
                        -- ARGV[1] = 当前时间戳（秒级）
                        -- ARGV[2] = 令牌生成速率（每秒生成数）
                        -- ARGV[3] = 令牌桶最大容量
                        local tokens_key = KEYS[1]
                        local last_refill_key = KEYS[2]
                        -- 当前令牌数量 令牌数量初始化为令牌桶最大容量
                        local current_tokens = tonumber(redis.call('GET', tokens_key) or ARGV[3])
                        -- 最后更新时间
                        local last_refill = tonumber(redis.call('GET', last_refill_key) or ARGV[1])
                                
                        -- 计算时间差同时计算新增令牌
                        local time_diff = ARGV[1] - last_refill
                        local new_tokens = time_diff * tonumber(ARGV[2])
                                  
                        -- 更新令牌数（不超过最大容量）
                        current_tokens = math.min(current_tokens + new_tokens, tonumber(ARGV[3]))
                                
                        -- 检查令牌是否足够
                        if current_tokens >= 1 then
                            -- 消耗令牌
                            current_tokens = current_tokens - 1
                            -- 更新Redis数据 合并两次set操作
                            redis.call("HMSET", KEYS[1], "tokens_key", current_tokens, "last_refill_key", ARGV[1])
                            -- redis.call('SET', tokens_key, current_tokens)
                            -- redis.call('SET', last_refill_key, ARGV[1])
                            return 1  -- 允许通过
                        else
                            -- 保留当前状态（不更新时间戳）
                            return 0  -- 拒绝请求
                        end
                """;
        Long execute = execute(lua,
                List.of(RedisConstants.CONNECTOR_LIMITER_TOKEN_BUCKET_CURRENT_TOKEN_NUMBER_PRE_KEY + key,
                        RedisConstants.CONNECTOR_LIMITER_TOKEN_BUCKET_LAST_TOKEN_UPDATE_TIME_PRE_KEY + key),
                currentTime,rate, threshold
        );
        return execute.equals(1L);
    }


    public Long countRemarkHotScore(String k,String flag_key,int score){
        String luaScript= """
                    local key = KEYS[1]
                    local flag_key = KEYS[2]
                    local time_key = key .. ':time'
                    local increment = tonumber(ARGV[1])
                    local current_time = tonumber(redis.call("time")[1])
                    local score_str = redis.call('get', key)
                    local score = 0
                    
                    if score_str then score = tonumber(score_str) end
                    
                    local pre_time_str = redis.call('get', time_key)
                    local pre_time = current_time
                    if pre_time_str then pre_time = tonumber(pre_time_str) end
                    
                    local hours_passed = math.floor((current_time - pre_time) / 3600)
                    local decay = hours_passed * 300
                    if decay < score then
                        score = score - decay
                    else
                        score = 0
                    end
                    
                    local new_value = score + increment
                    redis.call("set", key, new_value)
                    redis.call("set", time_key, current_time)
                    
                    local hot_flag = redis.call('exists', flag_key) == 1  -- 直接判断键是否存在
                    
                    -- 状态管理逻辑
                    if new_value > 1000 then
                        if not hot_flag then
                            redis.call('set', flag_key, new_value)
                            return  1  -- 返回新值+激活状态
                        end
                    elseif new_value < 800 then
                        if hot_flag then
                            redis.call('del', flag_key)
                            return  0  -- 返回新值+清除状态
                        end
                    end
                    return hot_flag and 1 or 0  -- 返回新值+当前状态
                """;

        return execute(luaScript, List.of(k,flag_key), score);
    }

    public Set<Object> getUnion(String userIdFromToken, String id) {
        return redisTemplate.opsForSet().intersect(RedisConstants.CON_FOLLOW_USER+userIdFromToken, RedisConstants.CON_FOLLOW_USER+id);
    }

    /**
     * 优惠券秒杀lua脚本 ，并实现一人一单
     * @param couponId 优惠券id
     * @param userId 用户id
     * @param diff 距优惠券过期的时间
     * @return 0 库存不足 1 已下单 2 成功
     */
    public Long secKillCoupon(Long couponId, Long userId, Long diff) {
        String lua= """
                -- 改进后的脚本：实现库存扣减+一人一单
                -- KEYS[1]：库存key（如 stock:goods:{goodsId}）
                -- KEYS[2]：一人一单key（如 order:user:{userId}:goods:{goodsId}）
                -- ARGV[1]：过期时间
                
                -- 1. 检查库存
                local stockKey = KEYS[1]
                local orderKey = KEYS[2]
                local deductNum = 1  -- 默认为扣减1个库存
                local pass_time = ARGV[1]
                
                -- 2. 获取库存并转换为数字
                local stock = tonumber(redis.call('get', stockKey))
                if not stock or stock < deductNum then
                    return 0  -- 库存不足或不存在
                end
                
                -- 3. 检查是否已下单（一人一单）
                local hasOrdered = redis.call('exists', orderKey)
                if hasOrdered == 1 then
                    return 1  -- 已下单，拒绝重复下单
                end
                
                -- 4. 扣减库存
                redis.call('decrby', stockKey, deductNum)
                
                -- 5. 标记已下单（设置过期时间，如24小时）
                redis.call('set', orderKey, 1, 'EX', pass_time)
                
                return 2  -- 成功：库存扣减+标记下单
                """;
        String couponStock = RedisConstants.SEC_KILL_COUPON_STOCK_PREHEAT_KEY + couponId;
        String couponUserRecord = RedisConstants.SEC_KILL_COUPON_USER_RECORD_KEY + couponId +":"+ userId;
        return execute(lua, List.of(couponStock, couponUserRecord), diff);
    }
}
