package com.zerozone.vintage.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;


@RequiredArgsConstructor
public class MetricsInterceptor implements HandlerInterceptor {
    private final CustomMetrics customMetrics;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        customMetrics.incrementApiRequest();
        return true;
    }
}