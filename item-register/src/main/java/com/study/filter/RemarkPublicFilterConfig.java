package com.study.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.Constants.RedisConstants;
import com.study.utils.redis.RedisUtil;
import com.study.vo.RemarkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
/**
 * 使用redis的zSet实现时间窗口算法，拦截非法请求
 */
public class RemarkPublicFilterConfig {

    @Autowired
    private RedisUtil redisUtil;

//    滑动时间窗口方案
    private static final int TIME_WINDOW_ONE=60*1000;//1min的时间窗口
    private static final int THRESHOLD_ONE=50;//限流阈值，1min内若访问服务器资源的次数超过这个阈值，那么就需要限制其访问
    private static final int TIME_WINDOW_THREE=60*1000*3;//1min的时间窗口
    private static final int THRESHOLD_THREE=200;//限流阈值，1min内若访问服务器资源的次数超过这个阈值，那么就需要限制其访问

    private static final String[] TIME_WINDOW_WRITE_LIST={"1"};
    private static final String[] COUNT_HOT_SCORE={"/remark/getRemark","remark/getHotRemark","remark/publish"};


//    令牌桶限流方案
    private static final int RATE=10000;

    private static final int THRESHOLD=20000;

    @Bean
    public GlobalFilter flow(){
        return new RemarkPublicFilter();
    }

    public class RemarkPublicFilter implements GlobalFilter, Ordered {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            String clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress()+path;//访问服务器资源的ip地址
            boolean flag=false;//是否是post方法
            if(countHotScore(path,COUNT_HOT_SCORE)){
                RemarkVo remarkVo;
                Long parentId;
                if (request.getMethod().matches(HttpMethod.POST.name())) {
                    flag=true;
                    String requestBody = exchange.getAttribute("body").toString();
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        remarkVo = mapper.readValue(requestBody, RemarkVo.class);
                    } catch (JsonProcessingException e) {
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                        return exchange.getResponse().writeWith(Flux.just(
                                exchange.getResponse().bufferFactory().wrap("参数格式错误".getBytes())
                        ));
                    }
                    parentId = remarkVo.getParentId();
                }else{
                    parentId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                }
                //TODO 异步统计帖子热度
                log.info("开始统计热帖，要统计的id为：{}",parentId);
                Long result;
                if (flag){
                    result= redisUtil.countRemarkHotScore(RedisConstants.COUNT_REMARK_HOT_SCORE_PRE_KEY + parentId,RedisConstants.HOT_REMARK_FLAG_PRE_KEY+parentId, 10);
                }else{
                    result= redisUtil.countRemarkHotScore(RedisConstants.COUNT_REMARK_HOT_SCORE_PRE_KEY+parentId,RedisConstants.HOT_REMARK_FLAG_PRE_KEY+parentId,1);
                }
                if(result>0){
                    log.info("检查到热贴，热帖id为：{},并且移除热贴key（如果存在）的有效期",parentId);
                    Boolean success = redisUtil.removeExpire(RedisConstants.HOT_REMARK_CACHE_PRE_KEY + parentId);
                    if(success){
                        log.info("已经失效的热帖，重新变为热帖，成功移除热帖key的有效期");

                    }else{
                        log.info("本来就不是热帖，移除热帖key的有效期失败");
                    }
                }else{
                    log.info("该帖子id为：{}不是热帖（可能以前是热帖）,设置热贴key（如果存在）的有效期",parentId);
                    Boolean success = redisUtil.setExpire(RedisConstants.HOT_REMARK_CACHE_PRE_KEY + parentId, 12L, TimeUnit.HOURS);
                    if(success){
                        log.info("成功设置key的有效期");
                    }
                }
//                publicMessageBuilder.countRemarkHotScoreMessage(parentId,"REMARK_HOT_SCORE_COUNT");
            }

            return chain.filter(exchange);
        }



        @Override
        public int getOrder() {
            return 3;
        }

        public boolean countHotScore(String ip,String[] list){
            for (String item:list
                 ) {
                if(ip.contains(item))return true;
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
