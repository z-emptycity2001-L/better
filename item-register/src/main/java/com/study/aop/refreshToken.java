package com.study.aop;

import com.study.Constants.jwt.JwtConstants;
import com.study.Constants.redis.RedisConstants;
import com.study.Constants.user.ThreadLocalConstants;
import com.study.utils.JwtTokenUtil;
import com.study.utils.ThreadLocalUtil;
import com.study.utils.redis.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class refreshToken {
    @Autowired
    private ThreadLocalUtil threadLocalUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisUtil redisUtil;
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) && within(com.study.*.controller..*)")
    public void checkUserInfoAspect() {

    }

    @Before("checkUserInfoAspect()")
    public void before(JoinPoint joinPoint) throws Throwable {
        String token = threadLocalUtil.getThreadLocal(ThreadLocalConstants.USERINFO);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        redisUtil.setExpire(JwtConstants.TOKEN_KEY + userId,JwtConstants.TOKEN_REDIS_EXPIRATION, TimeUnit.MILLISECONDS);
        log.info("token：{} 续期成功",token);

    }
}
