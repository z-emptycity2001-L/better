package com.study.filter;

import com.study.Constants.jwt.JwtConstants;
import com.study.Constants.user.ThreadLocalConstants;
import com.study.exception.BusinessException;
import com.study.Eume.ExceptEnum;
import com.study.utils.JwtTokenUtil;
import com.study.utils.ThreadLocalUtil;
import com.study.utils.redis.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FilterConfig {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ThreadLocalUtil threadLocalUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Bean
    public GlobalFilter my(){
        return new MyGlobalFilter();
    }

    public class MyGlobalFilter implements GlobalFilter, Ordered {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            log.info("global filter: "+"the mapper "+path+" get into");
            Mono<Void> filter;
            /*--------------如果请求路径在白名单内，放行--------------*/
            if (matchPathUser(path)||path.startsWith("/test")) {
                log.info("mapper has existed in the page");
                filter=chain.filter(exchange);
            }else{//否则，需要检验请求头的token表示
                HttpHeaders headers = request.getHeaders();
                List<String> tokens = headers.get("token");
                if(!Objects.isNull(tokens)){
                    String token = tokens.get(0);
                    //过滤黑名单token
                    if(!Objects.isNull(redisUtil.get(JwtConstants.BLACK_LIST_TOKEN_PRE_KEY+token))){
                        throw BusinessException.of(ExceptEnum.BLACK_USER_LOGIN);
                    }
                    Claims claims = jwtTokenUtil.getClaimsFromToken(token);
                    if(Objects.isNull(claims)){
                        throw BusinessException.of(ExceptEnum.USER_LOGIN_ERROR);
                    }
                    //从redis中获取token，若获取到token，则为有效token,否则，无效token,放置在黑名单内
                    String userId = (String) redisUtil.get(JwtConstants.TOKEN_KEY + jwtTokenUtil.getUserIdFromToken(token));
                    if(!Objects.isNull(userId)){
                        redisUtil.set(JwtConstants.BLACK_LIST_TOKEN_PRE_KEY+userId,token,JwtConstants.BLACK_LIST_TOKEN_EXPIRE, TimeUnit.MILLISECONDS);
                        throw BusinessException.of(ExceptEnum.USER_LOGIN_TOKEN_TIMEOUT);
                    }
                    log.info("global filter: "+"the mapper "+path+" 放行");
//                    通过的请求在threadLocal中设置token值
                    threadLocalUtil.setThreadLocal(ThreadLocalConstants.USERINFO,token);
                    filter=chain.filter(exchange);
                }else{
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return filter;
        }

        @Override
        public int getOrder() {
            return 1;
        }

        public boolean matchPathUser(String path){
            final String[] paths={"/user/register","/user/login","user/logout","/user/get","/user/sendMail"};
            for(String s:paths){
                if(s.equals(path))return true;
            }
            return false;
        }

    }


}
