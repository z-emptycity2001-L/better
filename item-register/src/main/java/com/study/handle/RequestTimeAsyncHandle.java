package com.study.handle;

import com.study.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

public class RequestTimeAsyncHandle implements AsyncHandlerInterceptor {
    @Autowired
    private ThreadLocalUtil threadLocalUtil;
    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        threadLocalUtil.clear();
        AsyncHandlerInterceptor.super.afterConcurrentHandlingStarted(request, response, handler);
    }
}
