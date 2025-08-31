package com.study.filter;

import com.study.Constants.RedisConstants;
import com.study.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;
@Slf4j
@Component
/**
 * 使用redis的zSet实现时间窗口算法，拦截非法请求
 */
public class IllegalIpFilterConfig {

    @Autowired
    private RedisUtil redisUtil;

//    滑动时间窗口方案
    private static final int TIME_WINDOW_ONE=60*1000;//1min的时间窗口
    private static final int THRESHOLD_ONE=50;//限流阈值，1min内若访问服务器资源的次数超过这个阈值，那么就需要限制其访问
    private static final int TIME_WINDOW_THREE=60*1000*3;//1min的时间窗口
    private static final int THRESHOLD_THREE=200;//限流阈值，1min内若访问服务器资源的次数超过这个阈值，那么就需要限制其访问

    private static final String[] TIME_WINDOW_WRITE_LIST={"1"};
    private static final String[] TOKEN_BUCKET_WRITE_LIST={"/disease/get/id"};


//    令牌桶限流方案
    private static final int RATE=10000;

    private static final int THRESHOLD=20000;

    @Bean
    public GlobalFilter flow(){
        return new IllegalIpFilterConfig.IllegalIpFilter();
    }

    public class IllegalIpFilter implements GlobalFilter, Ordered {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            String clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress()+path;//访问服务器资源的ip地址
            if(findIpInWriteList(path,TIME_WINDOW_WRITE_LIST)){
                if (!timeWindowFilter(clientIp)){
                    throw new RuntimeException("系统繁忙，请稍后再试！！！");
                }
            } else if (findIpInWriteList(path,TOKEN_BUCKET_WRITE_LIST)) {
                if (!bucketTokenFilter(clientIp)){
                    throw new RuntimeException("系统繁忙，请稍后再试！！！");
                }
            }

            return chain.filter(exchange);
        }



        @Override
        public int getOrder() {
            return 2;
        }

        public boolean findIpInWriteList(String ip,String[] list){
            for (String item:list
                 ) {
                if(item.equals(ip))return true;
            }
            return false;
        }
    }
    private boolean timeWindowFilter(String clientIp) {
        log.info("get into flow controller filter！！！");
        String key1=RedisConstants.ZSET_ONE_TIME_WINDOW_LIMIT_PRE_KEY+ clientIp;
        String key2=RedisConstants.ZSET_Three_TIME_WINDOW_LIMIT_PRE_KEY+ clientIp;
        long currentTime = System.currentTimeMillis();
        boolean success1 = redisUtil.filterIp(key1, TIME_WINDOW_ONE, THRESHOLD_ONE, currentTime);//统计1min的瞬时流量时间窗口
        boolean success2 = redisUtil.filterIp(key2,TIME_WINDOW_THREE,THRESHOLD_THREE, currentTime);//统计三分钟的平滑流量时间窗口
        if(success1 && success2) {
            log.info("时间窗口统计成功 " + clientIp);
            return true;
        }
        log.error("当前系统繁忙！！！");
        return false;
    }

    private boolean bucketTokenFilter(String clientIp) {
        log.info("get into flow controller filter！！！");
        boolean result = redisUtil.connectorLimiterByTokenBucket(RedisConstants.CONNECTOR_LIMITER_TOKEN_BUCKET_PRE_KEY + clientIp, System.currentTimeMillis(), RATE, THRESHOLD);
        if(result) {
            log.info("令牌获取成功 " + clientIp);
            return true;
        }
        System.out.println("令牌获取失败");
        return false;
    }

}
