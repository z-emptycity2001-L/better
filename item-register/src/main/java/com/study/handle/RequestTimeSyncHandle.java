package com.study.handle;

import com.study.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class RequestTimeSyncHandle implements HandlerInterceptor {
    @Autowired
    private ThreadLocalUtil threadLocalUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long currentTimeMillis = System.currentTimeMillis();
        threadLocalUtil.setThreadLocal("time",currentTimeMillis);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long time = threadLocalUtil.<Long>getThreadLocal("time");
        long now = System.currentTimeMillis();
        log.info("this path {} has passed {} ms",request.getPathInfo(),now-time);
        threadLocalUtil.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
